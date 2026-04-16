-- Create a new protein embedding row

INSERT INTO protein_embeddings (protein_id, embedding, model_version, generated_at)
VALUES (:proteinId, CAST(:embedding AS vector), :modelVersion, NOW())
ON CONFLICT (protein_id) DO NOTHING;