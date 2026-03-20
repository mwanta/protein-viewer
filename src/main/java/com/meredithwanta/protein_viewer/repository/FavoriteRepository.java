package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * To represent the operations that can be performed on the Favorite table.
 * Extends JpaRepository for use of standard database operations.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  /**
   * Finds a Protein in the Favorites table by PDB ID and username. Utilizes Spring Data JPA to automatically
   * generated an implementation based on the method name.
   *
   * Format:
   *  findBy: Generate SELECT query
   *  Protein: Go to protein field on Favorite
   *  PdbId: access the pdbId field on Protein
   *  And
   *  User: Go to user field on Favorite
   *  Username: access the username field on User
   *
   * @param pdbId: the PBD ID to search for.
   * @param username: the user the Favorite is associated with.
   *
   * @return the Favorite associated with the given PDB ID and username if it exists.
   */
  Optional<Favorite> findByProteinPdbIdAndUserUsername(String pdbId, String username);

  /**
   * Find favorite proteins by User.
   *
   * @param username: the User that has favorited it.
   *
   * @return a list of favorite proteins associated with the given User.
   */
  List<Favorite> findByUserUsername(String username);
}
