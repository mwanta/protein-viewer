package com.meredithwanta.protein_viewer.model;

import java.util.List;

/**
 * Bundle protein information to send to the frontend.
 *
 * @param protein : the protein to send information for
 * @param annotation : the annotation for the protein
 * @param similarProteins : proteins similar to this protein
 */
public record ProteinDetailDto(
    Protein protein,
    ProteinAnnotation annotation,
    List<SimilarProteinResult> similarProteins
) {}
