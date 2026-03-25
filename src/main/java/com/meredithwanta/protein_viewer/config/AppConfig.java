package com.meredithwanta.protein_viewer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Register a Bean to be used throughout all client classes.
 */
@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
