package com.meredithwanta.protein_viewer.client;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the UniProt REST API.
 * Fetches protein function, pathway, and subcellular location annotations.
 * Uses same accession ID as ChEMBL and Open Targets.
 */
@Service
public class UniProtClient {

  private static final String BASE_URL = "https://rest.uniprot.org/uniprotkb/";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * To construct this client object.
   *sca
   * @param restTemplate : to access built-in Spring Boot method to interact with the API.
   * @param objectMapper : to create a JsonNode object for the retrieved JSON String.
   */
  public UniProtClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  /**
   * Fetches the annotation data for a UniProt accession.
   *
   * @param uniprotId the UniProt accession ID to look up (e.g. P69905).
   *
   * @return a JsonNode representing the full UniProt entry, null is not found
   */
  public JsonNode fetch(String uniprotId) {
    if (uniprotId == null) {
      return null;
    }
    try {
      String url = BASE_URL + uniprotId + "?format=json";
      String json = restTemplate.getForObject(url, String.class);
      return objectMapper.readTree(json);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Fetch ensemblID to use for Open Targets API
   *
   * @param uniprotId : this protein's uniProt ID
   * @return the ensembl ID for thsi probelm
   */
  public String fetchEnsemblId(String uniprotId) {
    if (uniprotId == null) return null;
    try {
      String url = BASE_URL + uniprotId + "?format=json";
      String json = restTemplate.getForObject(url, String.class);
      JsonNode root = objectMapper.readTree(json);
      JsonNode crossRefs = root.path("uniProtKBCrossReferences");

      System.out.println("crossRefs size: " + crossRefs.size());

      for (JsonNode ref : crossRefs) {
        if ("Ensembl".equals(ref.path("database").asString(null))) {
          System.out.println("Found Ensembl ref: " + ref.toString());
          for (JsonNode prop : ref.path("properties")) {
            if ("GeneId".equals(prop.path("key").asString(null))) {
              String geneId = prop.path("value").asString(null);
              if (geneId != null && geneId.contains(".")) {
                geneId = geneId.substring(0, geneId.indexOf("."));
              }
              return geneId;
            }
          }
        }
      }
      return null;
    } catch (Exception e) {
      System.out.println("fetchEnsemblId failed: " + e.getMessage());
      return null;
    }
  }
}
