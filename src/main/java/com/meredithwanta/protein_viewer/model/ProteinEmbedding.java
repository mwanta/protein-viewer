package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "protein_embeddings")
@Data
@NoArgsConstructor
public class ProteinEmbedding {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "protein_id", nullable = false)
  private Protein protein;

  @Column(columnDefinition = "vector(320)")
  @JdbcTypeCode(SqlTypes.VECTOR)
  @Array(length = 320)
  private float[] embeddingVector;

  private String modelVersion;
  private Instant generatedAt;
}