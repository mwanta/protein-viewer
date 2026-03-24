package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class:
 * A user data table to represent a Protein that has been marked as a Favorite by the user.
 * Stores and fetches data from the PostgreSQL database.
 */
@Entity
@Table(name = "favorites")
@Data
@NoArgsConstructor
public class Favorite {

  @Id //each favorite has its own id (separate from ManyToOne relationship)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne //many favorites could reference one protein, but it is only favorited once
  @JoinColumn(name = "pdb_id")
  private Protein protein;

  @ManyToOne //many users can have the same Favorite
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "saved_at") //@Column maps field to the database column in both directions
  private LocalDateTime savedAt;

  /**
   * To construct a Favorite Protein
   *
   * @param protein: the RCSB protein that has been favorited.
   * @param user: the User who is favoriting this Protein.
   */
  public Favorite(Protein protein, User user) {
    this.protein = protein;
    this.user = user;
    this.savedAt = LocalDateTime.now();
  }
}
