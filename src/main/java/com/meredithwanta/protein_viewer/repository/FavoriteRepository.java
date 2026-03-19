package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.Favorite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * To represent the operations that can be performed on the Favorite table.
 * Extends JpaRepository for use of standard database operations.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  /**
   * Finds a Protein in the Favorites table by PDB ID. Utilizes Spring Data JPA to automatically
   * generated an implementation based on the method name.
   *
   * Format:
   *  findBy: Generate SELECT query
   *  Protein: Go to protein field on Favorite
   *  PdbId: acces the pdbId field on Protein
   *
   * @param pdbId: the PBD ID to search for.
   *
   * @return the Favorite associated with the given PDB ID if it exists.
   */
  Optional<Favorite> findByProteinPdbId(String pdbId);
}
