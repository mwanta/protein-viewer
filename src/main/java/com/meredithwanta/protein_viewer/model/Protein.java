package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class:
 * A cache table to represent a Protein that has been pulled from the RCSB Data Bank.
 * Stores and fetches data from the PostgreSQL database.
 * Prevents repetitive API calls.
 */
@Entity
@Table(name = "proteins")
@Data //Generates getters/setters for all fields + basics (toString, equals, etc)
@NoArgsConstructor
public class Protein {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable =false)
  private String pdbId;

  private String uniprotID;
  private String title;
  private String organism;
  private Double resolution;
  private Instant cachedAt;

}
