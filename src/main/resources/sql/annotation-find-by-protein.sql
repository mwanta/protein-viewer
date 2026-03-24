-- Find a protein by its given ID

SELECT protein_id, uniprot_data, chembl_data, open_targets_data, cached_at
FROM protein_annotations
WHERE protein_id = :proteinId;