-- Find the given protein within protein embeddings if it exists

SELECT COUNT(*)
FROM protein_embeddings
WHERE protein_id = :proteinId;