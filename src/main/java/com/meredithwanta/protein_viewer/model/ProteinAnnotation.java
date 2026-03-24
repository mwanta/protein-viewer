package com.meredithwanta.protein_viewer.model;

import tools.jackson.databind.JsonNode;
import java.time.Instant;

public record ProteinAnnotation(
    Integer proteinId,
    JsonNode uniprotData,
    JsonNode chemblData,
    JsonNode openTargetsData,
    Instant cachedAt
) {}