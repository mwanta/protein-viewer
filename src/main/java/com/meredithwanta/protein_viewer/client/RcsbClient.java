package com.meredithwanta.protein_viewer.client;

import com.meredithwanta.protein_viewer.model.Protein;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the RCSB Protein Data Bank REST API.
 * Fetches protein structure metadata and resolves UniProt accession IDs.
 */
@Service
public class RcsbClient {

  private static final String BASE_URL = "https://data.rcsb.org/rest/v1/core/";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * To construct this client object.
   *
   * @param restTemplate : to access built-in Spring Boot method to interact with the API.
   * @param objectMapper : to create a JsonNode object for the retrieved JSON String.
   */
  public RcsbClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  /**
   * Fetches protein metadata from RCSB and maps it to a protein entity.
   * Also extracts the UniProt ID for compatibility with ChEMBL and Open Targets
   *
   * @param pdbId : the RCSB PDB ID
   * @return a Protein entity with metadata from RCSB.
   */
  public Protein fetch(String pdbId) {
    String json = restTemplate.getForObject(BASE_URL + "entry/" + pdbId, String.class);
    JsonNode root = objectMapper.readTree(json);

    String title = root.path("struct").path("title").asString(null);

    String organism = fetchPolymerData(pdbId).organism();

    JsonNode resolutionNode = root.path("rcsb_entry_info")
        .path("resolution_combined")
        .path(0);
    Double resolution = resolutionNode.isMissingNode() ? null : resolutionNode.asDouble();

    String uniprotId = fetchPolymerData(pdbId).uniprotId();

    Protein protein = new Protein();
    protein.setPdbId(pdbId);
    protein.setTitle(title);
    protein.setOrganism(organism);
    protein.setResolution(resolution);
    protein.setUniprotID(uniprotId);
    protein.setCachedAt(Instant.now());
    return protein;

  }

  /**
   * Extract the first polymer entity and extract the first UniProt accession + amino acid sequence from its
   * reference sequence annotations (is not in the main entry point).
   *
   *
   * @param pdbId : the RSCB PDB ID
   * @return the UniProt accession ID, or null if not found and the amino acid sequence
   */
  public PolymerData fetchPolymerData(String pdbId) {
    String url = BASE_URL + "polymer_entity/"  + pdbId + "/1";
    try {
      String json = restTemplate.getForObject(url, String.class);
      JsonNode root = objectMapper.readTree(json);
      String uniprotId =  root.path("rcsb_polymer_entity_container_identifiers")
          .path("uniprot_ids")
          .path(0)
          .asString(null);
      String sequence =  root.path("entity_poly")
          .path("pdbx_seq_one_letter_code_can")
          .asString(null);
      String organism = root.path("rcsb_entity_source_organism")
          .path(0)
          .path("scientific_name")
          .asString(null);
      return new PolymerData(uniprotId, sequence, organism);
    } catch (Exception e) {
      System.out.println("fetchPolymerData failed: " + e.getMessage());
      return new PolymerData(null, null, null);
    }
  }

  /**
   * Holds polymer data from the RCSB database.
   *
   * @param uniprotId : the UniProt ID
   * @param sequence : the amino acid sequence
   * @param organism : the source organism of the protein
   */
  public record PolymerData(String uniprotId, String sequence, String organism) {}
}
