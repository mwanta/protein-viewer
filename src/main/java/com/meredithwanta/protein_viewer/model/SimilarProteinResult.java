package com.meredithwanta.protein_viewer.model;

/**
 * Contains information about a protein that is has been compared for similarity against another protein.
 *
 * @param pdbId : the pdbID of this protein.
 * @param title : the title of this protein.
 * @param organism : the organism this protein is from.
 * @param similarity : how similar this protein is to the comparison protein (1 = more similar to match 0 - 100% scale)
 */
public record SimilarProteinResult(
    String pdbId,
    String title,
    String organism,
    double similarity
) {}
