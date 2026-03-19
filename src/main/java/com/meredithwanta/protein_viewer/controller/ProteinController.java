package com.meredithwanta.protein_viewer.controller;

import com.meredithwanta.protein_viewer.model.Favorite;
import com.meredithwanta.protein_viewer.model.Protein;
import com.meredithwanta.protein_viewer.repository.FavoriteRepository;
import com.meredithwanta.protein_viewer.repository.ProteinRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

// Marked as a REST API handler
@RestController
// Set class path
@RequestMapping("/api")
// Allow React calls
@CrossOrigin(origins = "http://localhost:5173")
public class ProteinController {

  private final RestTemplate restTemplate = new RestTemplate();
  private final ProteinRepository proteinRepository;
  private final FavoriteRepository favoriteRepository;

  /**
   * To construct this ProteinController.
   *
   * @param proteinRepository: to interact with the Protein cache table.
   * @param favoriteRepository: to interact with the Favorite user data table.
   */
  public ProteinController(ProteinRepository proteinRepository, FavoriteRepository favoriteRepository) {
    this.proteinRepository = proteinRepository;
    this.favoriteRepository = favoriteRepository;
  }

  /**
   * Get the metadata for this pdbId.
   * Steps:
   *  1. Check the cache table to see if this protein has already been retrieved. If so,
   *      return the saved metadata.
   *  2. If the protein is not in the table, retrieve it from the RCSB API. Save the protein
   *      to the table for future reference and return the metadata.
   *
   * @param pdbId: the protein database ID to search RCSB for (i.e. 4HHB)
   *
   * @return the protein's metadata.
   */
  @GetMapping("/protein/{pdbId}") // File path to dynamic pdbID
  public String getProtein(@PathVariable String pdbId) {
    String id = pdbId.toUpperCase();

    //check if this protein has already been cached
    Optional<Protein> protein = proteinRepository.findById(id);
    if (protein.isPresent()) {
      return protein.get().getMetadata();
    }

    //Otherwise, fetch from RCSB and cache
    String url = "https://data.rcsb.org/rest/v1/core/entry/" + id;

    // Calls the RCSB API and saves the response
    String metadata = restTemplate.getForObject(url, String.class);

    //save the protein to the database
    proteinRepository.save(new Protein(id, metadata));
    return metadata;
  }

  /**
   * Returns all Proteins in the Favorite user data table.
   *
   * @return a list of all stored Favorite proteins.
   */
  @GetMapping("/favorites")
  public List<Favorite> getFavorites() {
    return favoriteRepository.findAll();
  }

  /**
   * Saves a Protein from the cache table to Favorites.
   * The protein must be retrieved to be favorited, therefore it should always be in the database.
   *
   * @param pdbId: the PDB ID of the protein to favorite.
   *
   * @return the now Favorite protein.
   */
  @PostMapping("/favorites/{pdbId}")
  public Favorite addFavorite(@PathVariable String pdbId) {
    String id = pdbId.toUpperCase();
    Protein protein = proteinRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Protein not found in cache"));

    return favoriteRepository.save(new Favorite(protein));
  }

  /**
   * Deletes the given Protein from Favorites.
   *
   * @param pdbId: the Protein to remove from Favorites.
   */
  @DeleteMapping("/favorites/{pdbId}")
  public void deleteFavorite(@PathVariable String pdbId) {
    String id = pdbId.toUpperCase();
    favoriteRepository.findByProteinPdbId(id)
        .ifPresent(favoriteRepository::delete);
  }
}
