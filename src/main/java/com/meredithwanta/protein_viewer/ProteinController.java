package com.meredithwanta.protein_viewer;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

// Marked as a REST API handler
@RestController
// Set class path
@RequestMapping("/api/protein")
// Allow React calls
@CrossOrigin(origins = "http://localhost:5173")
public class ProteinController {

  private final RestTemplate restTemplate = new RestTemplate();

  // File path to dynamic pdbID
  @GetMapping("/{pdbId}")
  public String getProtein(@PathVariable String pdbId) {
    String url = "https://data.rcsb.org/rest/v1/core/entry/" + pdbId.toUpperCase();
    // Calls the RCSB API and returns the response
    return restTemplate.getForObject(url, String.class);
  }
}
