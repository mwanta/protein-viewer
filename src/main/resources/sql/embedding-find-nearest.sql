-- Given a protein, finds the N most similar proteins in the database by cosine distance.
-- (Compares ESM-2 embeddings)
-- Excludes the query protein itself.
-- similarity = 1 - cosine distance, so 1.0 is identical, 0.0 is completely dissimilar.

SELECT
    p.pdb_id,
    p.title,
    p.organism,
    -- Visualization (similarity score)
    1 - (pe.embedding <=> query.embedding) AS similarity
FROM protein_embeddings pe
JOIN proteins p ON p.id = pe.protein_id

-- Compare the embedding vector of this protein across every row
CROSS JOIN (
    SELECT embedding
    FROM protein_embeddings
    WHERE protein_id = :proteinID
) query
WHERE pe.protein_id != :proteinID

-- cosine distance operator (lower number = more similar -> 0 = vectors are identical)
ORDER BY pe.embedding <=> query.embedding
LIMIT :limit;