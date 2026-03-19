package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class:
 * A cache table to represent a Protein that has been pulled from the RCSB Data Bank.
 * Stores and fetches data from the PostgreSQL database.
 * Prevents repetitive API calls.
 */
@Entity
@Table(name = "proteins")
public class Protein {

  @Id
  @Column(name = "pdb_id")
  private String pdbId;

  @Column(columnDefinition = "TEXT")
  private String metadata;

  @Column(name = "cached_at")
  private LocalDateTime cachedAt;

  /**
   * Basic constructor.
   */
  public Protein(){}

  /**
   * To construct a Protein.
   *
   * @param pdbID: the RCSB protein ID.
   * @param metadata: the protein's information.
   */
  public Protein(String pdbId, String metadata) {
    this.pdbId = pdbId;
    this.metadata = metadata;
    this.cachedAt = LocalDateTime.now();
  }

  /**
   * Returns the PBD ID of this Protein.
   *
   * @return the PDB ID of this Protein.
   */
  public String getPdbId() {
    return pdbId;
  }

  /**
   * Returns the metadata of this Protein.
   *
   * @return the metadata of this Protein.
   */
  public String getMetadata() {
    return metadata;
  }

  /**
   * Returns the date when this Protein was cached.
   *
   * @return the date/time when this Protein was cached.
   */
  public LocalDateTime getCachedAt() {
    return cachedAt;
  }
}
