package com.meredithwanta.protein_viewer.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the Open Targets GraphQL API.
 * Fetches disease associations and evidence scores for a given protein target.
 * Uses a GraphQL -> queries are sent as POST requests rather than GET.
 */
@Service
public class OpenTargetsClient {

  private static final String BASE_URL = "https://api.platform.opentargets.org/api/v4/graphql";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * To construct this client object.
   *
   * @param restTemplate : to access built-in Spring Boot method to interact with the API.
   * @param objectMapper : to create a JsonNode object for the retrieved JSON String.
   */
  public OpenTargetsClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  /**
   * Fetches disease associations for a protein identified by its UniProt ID (accepted by Open Targets).
   *
   * @param uniprotID: the UniProt accession ID (e.g. P69905)
   *
   * @return disease association data as a JsonNode, or null if none found.
   */
  public JsonNode fetch(String uniprotID) {
    if (uniprotID == null) {
      return null;
    }

    try {
      String query = """
      {
        "query": "{ target(id: \\"%s\\") { id approvedName associatedDiseases(page: {index: 0, size: 20}) { rows { disease { id name } score } } } }"
      }
      """.formatted(uniprotID);

      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");

      HttpEntity<String> entity = new HttpEntity<>(query, headers);

      String response = restTemplate.postForObject(BASE_URL, entity, String.class);
      return objectMapper.readTree(response);
    } catch (Exception e) {
      return null;
    }
  }
}
