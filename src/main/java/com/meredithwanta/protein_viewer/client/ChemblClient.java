package com.meredithwanta.protein_viewer.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the ChEMBL REST API.
 * Fetches known drugs and compounds that interact with a given protein target.
 */
@Service
public class ChemblClient {

  private static final String BASE_URL = "https://www.ebi.ac.uk/chembl/api/data/";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * To construct this client object.
   *
   * @param restTemplate : to access built-in Spring Boot method to interact with the API.
   * @param objectMapper : to create a JsonNode object for the retrieved JSON String.
   */
  public ChemblClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public JsonNode fetch(String uniprotId) {
    if (uniprotId == null) {
      return null;
    }

    try {
      //resolve UniProt ID to ChEMBL target ID
      String targetUrl = BASE_URL + "target.json?target_components__accession=" + uniprotId + "&limit=1";
      String targetJson = restTemplate.getForObject(targetUrl, String.class);
      JsonNode targetRoot = objectMapper.readTree(targetJson);
      String chemblId = targetRoot.path("targets").path(0)
          .path("target_chembl_id").asText(null);
      if (chemblId == null) {
        return null;
      }

      //fetch bioactivity data
      String dataUrl = BASE_URL + "activity.json?target_chembl_id=" + chemblId
          + "&limit=20&order_by_pchembl_value";
      String dataJson = restTemplate.getForObject(dataUrl, String.class);
      JsonNode dataRoot = objectMapper.readTree(dataJson);
      return dataRoot;
    } catch (Exception e) {
      return null;
    }
  }

}
