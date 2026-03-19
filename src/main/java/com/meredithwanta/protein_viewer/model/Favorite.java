package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class:
 * A user data table to represent a Protein that has been marked as a Favorite by the user.
 * Stores and fetches data from the PostgreSQL database.
 */
@Entity
@Table(name = "favorites")
public class Favorite {

  @Id //each favorite has its own id (separate from ManyToOne relationship)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne //many favorites could reference one protein, but it is only favorited once
  @JoinColumn(name = "pdb_id")
  private Protein protein;

  @Column(name = "saved_at") //@Column maps field to the database column in both directions
  private LocalDateTime savedAt;

  /**
   * Basic constructor.
   */
  public Favorite() {}

  /**
   * To construct a Favorite Protein
   *
   * @param protein: the RCSB protein that has been favorited.
   */
  public Favorite(Protein protein) {
    this.protein = protein;
    this.savedAt = LocalDateTime.now();
  }

  /**
   * Returns the database ID of this Favorite.
   *
   * @return the database ID of this Favorite.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the Favorite Protein.
   *
   * @return the Favorite Protein.
   */
  public Protein getProtein() {
    return protein;
  }

  /**
   * Returns the date when this Favorite Protein was saved.
   *
   * @return the date/time when this Favorite Protein was saved.
   */
  public LocalDateTime getSavedAt() {
    return savedAt;
  }
}
