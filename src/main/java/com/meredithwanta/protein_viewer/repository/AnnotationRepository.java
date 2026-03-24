package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.ProteinAnnotation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * A class to represent methods that can be used to interact with the ProteinAnnotation database.
 * Uses JdbcTemplate to allow for customizable SQL queries.
 */
@Repository
public class AnnotationRepository {

  private final NamedParameterJdbcTemplate jdbcDelegate;
  private final ObjectMapper objectMapper;
  private final String upsertSql;
  private final String findSql;

  /**
   * To construct this Annotation Repository.
   *
   * @param jdbcDelegate Spring's helper class for running SQL queries.
   *                     NamedParameter = uses named placements (:variable vs ?)
   * @param objectMapper Converts between Java objects and JSON.
   *                     JsonNode = tree representation of a JSON object.
   */
  public AnnotationRepository(NamedParameterJdbcTemplate jdbcDelegate, ObjectMapper objectMapper) {
    this.jdbcDelegate = jdbcDelegate;
    this.objectMapper = objectMapper;
    this.upsertSql = loadSql("sql/annotation-upset.sql");
    this.findSql = loadSql("sql/annotation-find-by-protein.sql");
  }

  /**
   * Maps each name from the API output to its value to be saved in the database.
   *
   * @param proteinID:   the ID for this protein.
   * @param uniprot:     the Json node representing UniProt output for this protein.
   * @param chembl:      the Json node representing ChEMBL output for this protein.
   * @param openTargets: the Json node representing Open Targets output for this protein.
   */
  public void upsert(Integer proteinID, JsonNode uniprot, JsonNode chembl, JsonNode openTargets) {
    jdbcDelegate.update(upsertSql, new MapSqlParameterSource()
        .addValue("proteinId", proteinID)
        .addValue("uniprotData", toJson(uniprot))
        .addValue("chemblData", toJson(chembl))
        .addValue("openTargetsData", toJson(openTargets)));
  }

  /**
   * Returns the ProteinAnnotation associated with the given proteinId if it exists.
   *
   * @param proteinId: the protein ID to search for.
   *
   * @return the ProteinAnnotation associated with the given ID if it exists, otherwise empty.
   */
  public Optional<ProteinAnnotation> findByProteinId(Integer proteinId) {
    return jdbcDelegate.query(findSql,
        new MapSqlParameterSource("proteinId", proteinId),
        result -> {
          if (!result.next()) {
            return Optional.empty();
          }
          return Optional.of(new ProteinAnnotation(
              result.getInt("protein_id"),
              toNode(result.getString("uniprot_data")),
              toNode(result.getString("chembl_data")),
              toNode(result.getString("open_targets_data")),
              result.getTimestamp("cachedAt").toInstant()
          ));
        });
  }

  /**
   * Converts the given JsonNode Java object into the JSON string representation.
   *
   * @param json: the JsonNode object to convert.
   * @return a JSON String object.
   */
  private String toJson(JsonNode json) {
    try {
      return objectMapper.writeValueAsString(json);
    } catch (JacksonException e) {
      throw new RuntimeException("Failed to serialize JSON", e);
    }
  }

  /**
   * Converts the given JSON String into a JsonNode Java object.
   *
   * @param json: the String to convert.
   * @return a JsonNode object representing the given String, or a null node if the given String is null.
   */
  private JsonNode toNode(String json) {
    try {
      return json != null ? objectMapper.readTree(json) : objectMapper.nullNode();
    } catch (JacksonException e) {
      throw new RuntimeException("Failed to serialize JSON", e);
    }
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
