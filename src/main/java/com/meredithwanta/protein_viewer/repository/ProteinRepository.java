package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.Protein;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * To represent the operations that can be performed on the Protein table.
 * Extends JpaRepository for use of standard database operations.
 */
public interface ProteinRepository extends JpaRepository<Protein, String>{


  /**
   * Finds a protein by the given pdbID.
   *
   * @param pdbId : the identifier for this protein (i.e. 4HHB)
   *
   * @return the Protein associated with the given pdb ID.
   */
  Optional<Protein> findByPdbId(String pdbId);
}
