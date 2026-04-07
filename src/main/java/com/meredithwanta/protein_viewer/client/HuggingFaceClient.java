package com.meredithwanta.protein_viewer.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client for the HuggingFace Inference API.
 * Generate protein sequence embeddings using Meta's ESM-2 model.
 * Used for protein similarity searches.
 */
@Service
public class HuggingFaceClient {

  private static final String MODEL_URL =
      "https://api-inference.huggingface.co/models/facebook/esm2_t33_650M_UR50D";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${huggingface.api.key}")
  private String apiKey;

  public HuggingFaceClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public float[] getEmbedding(String sequence) {
    if (sequence == null || sequence.isBlank()) return null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + apiKey);
      headers.set("Content-Type", "application/json");

      String body = objectMapper.writeValueAsString(
          objectMapper.createObjectNode().put("inputs", sequence)
      );

      HttpEntity<String> request = new HttpEntity<>(body, headers);
      String response = restTemplate.postForObject(MODEL_URL, request, String.class);
      JsonNode root = objectMapper.readTree(response);

      //ESM-2 returns shape [1, sequence_length, 1280]
      return averagePool(root.path(0));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Average pools per-token embeddings into a single sequence-level embedding.
   * Each token has a 1280-dimensional vector (take the mean across all tokens)
   *
   * @param tokenEmbeddings : JsonNode of shape [sequence_length, 1280]
   * @return float array of length 1280
   */
  private float[] averagePool(JsonNode tokenEmbeddings) {
    int seqLen = tokenEmbeddings.size();
    int dims = tokenEmbeddings.path(0).size();
    float[] pooled = new float[dims];

    for (int seqInd = 0; seqInd < seqLen; seqInd++) {
      JsonNode token = tokenEmbeddings.path(seqInd);

      for (int dimInd = 0; dimInd < dims; dimInd++) {
        pooled[dimInd] += token.path(dimInd).asFloat();
      }
    }

    for (int index = 0; index < dims; index++) {
      pooled[index] /= seqLen;
    }

    return pooled;
  }

}
