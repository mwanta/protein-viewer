package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.SimilarProteinResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * A class to represent methods that can be used to interact with the ProteinEmbedding database.
 * Uses JdbcTemplate to allow for customizable SQL queries.
 */
@Repository
public class EmbeddingRepository {

  private final NamedParameterJdbcTemplate jdbcDelegate;
  private final String saveSql;
  private final String existsSql;
  private final String nearestSql;

  /**
   * To construct this EmbeddingRepository.
   *
   * @param jdbcDelegate Spring's helper class for running SQL queries.
   *                     NamedParameter = uses named placements (:variable vs ?)
   */
  public EmbeddingRepository(NamedParameterJdbcTemplate jdbcDelegate) {
    this.jdbcDelegate = jdbcDelegate;
    this.saveSql = loadSql("sql/embedding_save.sql");
    this.existsSql = loadSql("sql/embedding_exists.sql");
    this.nearestSql = loadSql("sql/embedding_find_nearest.sql");

  }

  /**
   * Maps each name from the API output to its value to be saved in the database.
   *
   * @param proteinId : the ID for this protein.
   * @param embeddingVector : the vector representing the protein's amino acid sequence.
   *                          similar amino acid sequences = similar vectors
   * @param modelVersion : the version of this model.
   */
  public void save(Integer proteinId, float[] embeddingVector, String modelVersion) {
    jdbcDelegate.update(saveSql, new MapSqlParameterSource()
        .addValue("proteinId", proteinId)
        .addValue("embeddingVector", embeddingVector)
        .addValue("modelVersion", modelVersion));
  }

  /**
   * Returns if this protein's embedding exists within the database.
   *
   * @param proteinId : the ID of the protein to search for.
   *
   * @return a boolean representing if this protein exists in the embedding database.
   */
  public boolean existsByProteinId(Integer proteinId) {
    Integer count = jdbcDelegate.queryForObject(existsSql,
        new MapSqlParameterSource("proteinId", proteinId), Integer.class);
    return count != null && count > 0;
  }

  public List<SimilarProteinResult> findNearestNeighbors(Integer proteinId, int limit) {
    return jdbcDelegate.query(nearestSql,
        new MapSqlParameterSource() //map variables to their placement in the .sql script
            .addValue("proteinId", proteinId)
            .addValue("limit", limit),
        (result, rowNum) -> new SimilarProteinResult(
            result.getString("pdb_id"),
            result.getString("title"),
            result.getString("organism"),
            result.getDouble("similarity")
        ));
  }

  /**
   * Returns the String representation of the given vector in the form of [x,y,z,...].
   *
   * @param embeddingVector : the vector to be translated to String form.
   *
   * @return the String representation of the given vector.
   */
  private String toVectorString(float[] embeddingVector) {
    StringBuilder builder = new StringBuilder("[");
    for (int index = 0; index < embeddingVector.length; index++) {
      builder.append(embeddingVector[index]);
      if (index <  embeddingVector.length - 1) {
        builder.append(",");
      }
    }

    return builder.append("]").toString();
  }

  /**
   * Loads the given SQL file as a String from the resources folder.
   *
   * @param fileName: the path relative to src/main/resources to the SQL file to load.
   * @return a String representing the contents of the given SQL file.
   */
  private String loadSql(String fileName) {
    try {
      return new ClassPathResource(fileName).getContentAsString(StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load sql file: " + fileName + e);
    }
  }

}
