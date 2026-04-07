package com.meredithwanta.protein_viewer.controller;

import com.meredithwanta.protein_viewer.model.Favorite;
import com.meredithwanta.protein_viewer.model.Protein;
import com.meredithwanta.protein_viewer.model.ProteinDetailDto;
import com.meredithwanta.protein_viewer.model.SimilarProteinResult;
import com.meredithwanta.protein_viewer.model.User;
import com.meredithwanta.protein_viewer.repository.FavoriteRepository;
import com.meredithwanta.protein_viewer.repository.ProteinRepository;
import com.meredithwanta.protein_viewer.repository.UserRepository;
import com.meredithwanta.protein_viewer.service.ProteinService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * REST controller for protein and favorites endpoints.
 * All data retrieval delegates to service.ProteinService
 */
// Marked as a REST API handler
@RestController
// Set class path
@RequestMapping("/api")
// Allow React calls
@CrossOrigin(origins = "http://localhost:5173")
public class ProteinController {

  private final ProteinService proteinService;
  private final ProteinRepository proteinRepository;
  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;

  /**
   * To construct this ProteinController.
   *
   * @param proteinService: the service class to retrieve API data.
   * @param proteinRepository: to interact with the Protein cache table.
   * @param favoriteRepository: to interact with the Favorite user data table.
   * @param userRepository: to interact with the User data table.
   */
  public ProteinController(ProteinRepository proteinRepository, FavoriteRepository favoriteRepository,
                           UserRepository userRepository, ProteinService proteinService) {
    this.proteinService = proteinService;
    this.proteinRepository = proteinRepository;
    this.favoriteRepository = favoriteRepository;
    this.userRepository = userRepository;
  }

  /**
   * Returns fulls protein detail for the given PDB ID.
   * Includes: structure metadata, UnitProt/ChEMBL/Open Targets annotations.
   * Also includes similar proteins if embedding has been generated.
   *
   * @param pdbId: the protein database ID to search RCSB for (i.e. 4HHB)
   *
   * @return a ProteinDetailDto containing the protein's data.
   */
  @GetMapping("/protein/{pdbId}") // File path to dynamic pdbID
  public ProteinDetailDto getProtein(@PathVariable String pdbId) {
    return proteinService.getProteinDetail(pdbId.toUpperCase());
  }

  /**
   * Returns similar proteins to protein of the given PDB ID using embedding cosine similarity.
   * Called separately to prevent delaying the main response.
   *
   * @param pdbId : the RCSB PDB ID to find similar proteins for.
   * @return a list of similar proteins.
   */
  @GetMapping("/protein/{pdbId}/similar")
  public List<SimilarProteinResult> getSimilarProtein(@PathVariable String pdbId) {
    return proteinService.getSimilarProteins(pdbId.toUpperCase(), 5);
  }

  /**
   * Returns all Proteins in the Favorite user data table for the current User.
   *
   * @return a list of all stored Favorite proteins for the current User.
   */
  @GetMapping("/favorites")
  public List<Favorite> getFavorites() {
    User user = getCurrentUser();
    return favoriteRepository.findByUserUsername(user.getUsername());
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
    User user = getCurrentUser();
    Protein protein = proteinRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Protein not found in cache"));

    return favoriteRepository.save(new Favorite(protein, user));
  }

  /**
   * Deletes the given Protein from Favorites.
   *
   * @param pdbId: the Protein to remove from Favorites.
   */
  @DeleteMapping("/favorites/{pdbId}")
  public void deleteFavorite(@PathVariable String pdbId) {
    String id = pdbId.toUpperCase();
    User user = getCurrentUser();
    favoriteRepository.findByProteinPdbIdAndUserUsername(id, user.getUsername())
        .ifPresent(favoriteRepository::delete);
  }

  /**
   * To get the current User.
   *
   * @return the current logged-in User.
   */
  private User getCurrentUser() {
    String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
  }
}
