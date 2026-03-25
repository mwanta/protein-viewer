package com.meredithwanta.protein_viewer.client;

import com.meredithwanta.protein_viewer.model.Protein;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the RCSB Protein Data Bank REST API.
 * Fetches protein structure metadata and resolves UniProt accession IDs.
 */
@Service
public class RcsbClient {

  private static final String BASE_URL = "https://data.rcsb.org/rest/v1/core/";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * To construct this client object.
   *
   * @param restTemplate : to access built-in Spring Boot method to interact with the API.
   * @param objectMapper : to create a JsonNode object for the retrieved JSON String.
   */
  public RcsbClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public Protein fetch(String pdbId) {
    String json = restTemplate.getForObject(BASE_URL + "entry/" + pdbId, String.class);
    JsonNode root = objectMapper.readTree(json);

    String title = root.path("struct").path("title").asText(null);

    String organism = root.path("rcsb_entry_info")
        .path("tax_id_list")
        .path(0)
        .path("scientific_name").asText(null);

    Double resolution = root.path("rcsb_entry_info")
        .path("resolution_combined")
        .path(0).asDouble();

    String uniprotId = extractUniprotId(pdbId);

    Protein protein = new Protein();
    protein.setPdbId(pdbId);
    protein.setTitle(title);
    protein.setOrganism(organism);
    protein.setResolution(resolution);
    protein.setUniprotID(uniprotId);
    protein.setCachedAt(Instant.now());
    return protein;

  }

  /**
   * Extract the first polymer entity and extract the first UniProt accession from its
   * reference sequence annotations (is not in the main entry point).
   *
   * @param pdbId : the RSCB PDB ID
   * @return the UniProt accession ID, or null if not found.
   */
  private String extractUniprotId(String pdbId) {
    String url = BASE_URL + "polymer_entity/"  + pdbId + "/1";
    try {
      String json = restTemplate.getForObject(url, String.class);
      JsonNode root = objectMapper.readTree(json);
      return root.path("rcsb_polymer_entity_container_identifiers")
          .path("uniprot_ids")
          .path(0)
          .asText(null);
    } catch (Exception e) {
      return null;
    }
  }
}
