package com.meredithwanta.protein_viewer.service;

import com.meredithwanta.protein_viewer.client.ChemblClient;
import com.meredithwanta.protein_viewer.client.EmbeddingClient;
import com.meredithwanta.protein_viewer.client.OpenTargetsClient;
import com.meredithwanta.protein_viewer.client.UniProtClient;
import com.meredithwanta.protein_viewer.model.Protein;
import com.meredithwanta.protein_viewer.model.ProteinAnnotation;
import com.meredithwanta.protein_viewer.model.ProteinDetailDto;
import com.meredithwanta.protein_viewer.model.SimilarProteinResult;
import com.meredithwanta.protein_viewer.repository.AnnotationRepository;
import com.meredithwanta.protein_viewer.repository.EmbeddingRepository;
import com.meredithwanta.protein_viewer.repository.ProteinRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.meredithwanta.protein_viewer.client.RcsbClient;
import tools.jackson.databind.JsonNode;

/**
 * Service class for protein data retrieval and caching.
 * Handles the fan-out to RCSB, UniProt, ChEMBL, and Open Targets APIs.
 * Caches call results in PostgreSQL to avoid redundant API calls.
 * Embedding generation is kicked off asynchronously so main response is not blocked.
 */
@Service
@AllArgsConstructor
public class ProteinService {

  private final RcsbClient rcsbClient;
  private final ChemblClient chemblClient;
  private final OpenTargetsClient openTargetsClient;
  private final UniProtClient uniProtClient;
  private final EmbeddingClient embeddingClient;
  private final ProteinRepository proteinRepository;
  private final AnnotationRepository annotationRepository;
  private final EmbeddingRepository embeddingRepository;

  public ProteinDetailDto getProteinDetail(String pdbId){
    Optional<Protein> cached = proteinRepository.findByPdbId(pdbId);

    //return from the database if it exists
    if (cached.isPresent() && isFresh(cached.get().getCachedAt())) {
      Protein protein = cached.get();
      Optional<ProteinAnnotation> annotation = annotationRepository.findByProteinId(protein.getId());
      List<SimilarProteinResult> similar = embeddingRepository.existsByProteinId(protein.getId())
          ? embeddingRepository.findNearestNeighbors(protein.getId(), 5)
          : Collections.emptyList();
      return new ProteinDetailDto(protein, annotation.orElse(null), similar);
    }

    //Not cached, retrieve from API
    //Pull from RCSB first to get UniProt ID
    Protein protein = rcsbClient.fetch(pdbId);
    String uniprotId = protein.getUniprotID();
    String ensemblId = uniProtClient.fetchEnsemblId(uniprotId);

    //Fan out to UniProt, ChEMBL, and Open Targets in parallel
    CompletableFuture<JsonNode> uniprotFuture =
        CompletableFuture.supplyAsync(() -> uniProtClient.fetch(uniprotId));
    CompletableFuture<JsonNode> chemblFuture =
        CompletableFuture.supplyAsync(() -> chemblClient.fetch(uniprotId));
    CompletableFuture<JsonNode> openTargetsFuture =
        CompletableFuture.supplyAsync(() -> openTargetsClient.fetch(ensemblId));

    CompletableFuture.allOf(uniprotFuture, chemblFuture, openTargetsFuture).join();

    //cache protein metadata
    Optional<Protein> existing = proteinRepository.findByPdbId(pdbId);
    if (existing.isPresent()) {
        protein.setId(existing.get().getId());
    }

    protein = proteinRepository.save(protein);

    //save annotations
    annotationRepository.upsert(
        protein.getId(),
        uniprotFuture.join(),
        chemblFuture.join(),
        openTargetsFuture.join()
    );

    //kick of embedding generation asynchronously (don't block response)
    generateEmbeddingAsync(protein.getId(), pdbId);

    ProteinAnnotation annotation = new ProteinAnnotation(
        protein.getId(),
        uniprotFuture.join(),
        chemblFuture.join(),
        openTargetsFuture.join(),
        Instant.now()
    );

    //initiate similar proteins as empty list while they are generating
    return new ProteinDetailDto(protein, annotation, Collections.emptyList());

  }

  /**
   * Returns the x most similar proteins to the given protein by embedding cosine similarity.
   * Is only called when the user opens the similar proteins tab.
   *
   * @param pdbId : the pdb id of the protein to match
   * @param limit : the number of desired similar proteins
   * @return a list of similar proteins.
   */
  public List<SimilarProteinResult> getSimilarProteins(String pdbId, int limit){
    return proteinRepository.findByPdbId(pdbId)
        .filter(p -> embeddingRepository.existsByProteinId(p.getId()))
        .map(p -> embeddingRepository.findNearestNeighbors(p.getId() ,limit))
        .orElse(Collections.emptyList());
  }

  /**
   * Generates and stores an ESM-2 embedding for the given protein.
   * Is run asynchronously to prevent the main response from being delayed.
   *
   * @param proteinId : the database ID of the protein
   * @param pdbId : the pdb id of the protein
   */
  public void generateEmbeddingAsync(Integer proteinId, String pdbId) {
    System.out.println("generateEmbeddingAsync called for " + pdbId + " proteinId=" + proteinId);
    if (embeddingRepository.existsByProteinId(proteinId)) return;
    try {
      String sequence = rcsbClient.fetchPolymerData(pdbId).sequence();
      if (sequence == null) return;

      float[] embedding = embeddingClient.getEmbedding(sequence);
      if (embedding == null) return;
      embeddingRepository.save(proteinId, embedding, "esm2_t33_650M_UR50D");
    } catch (Exception e) {
      // embedding failure shouldn't fail main program
      System.out.println("embedding generation failed: " + e);
    }
  }

  /**
   * Returns true if the protein was cached within the last 7 days.
   *
   * @param cachedAt : the last date the protein was cached at.
   * @return true if the protein was cached within the last 7 days.
   */
  private boolean isFresh(Instant cachedAt) {
    return cachedAt != null && cachedAt.isAfter(Instant.now().minus(7, ChronoUnit.DAYS));
  }

}
