package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.Protein;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * To represent the operations that can be performed on the Protein table.
 * Extends JpaRepository for use of standard database operations.
 */
public interface ProteinRepository extends JpaRepository<Protein, String>{
}
