package com.meredithwanta.protein_viewer.client;

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
public class EmbeddingClient {

  private static final String MODEL_URL = "http://embeddings:8000/embed";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public EmbeddingClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public float[] getEmbedding(String sequence) {
    if (sequence == null || sequence.isBlank()) return null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");

      String body = objectMapper.writeValueAsString(
          objectMapper.createObjectNode().put("sequence", sequence)
      );

      HttpEntity<String> request = new HttpEntity<>(body, headers);
      JsonNode root = objectMapper.readTree(
          restTemplate.postForObject(MODEL_URL, request, String.class)
      );

      JsonNode embeddingNode = root.path("embedding");
      float[] embedding = new float[embeddingNode.size()];
      for (int i = 0; i < embeddingNode.size(); i++) {
        embedding[i] = (float) embeddingNode.path(i).asDouble();
      }
      return embedding;
    } catch (Exception e) {
      System.out.println("Embedding service failed: " + e.getMessage());
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
