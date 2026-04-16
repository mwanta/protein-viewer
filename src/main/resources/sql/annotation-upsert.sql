-- Upsert = insert a new row if the key doesn't exist or update it if it does

INSERT INTO protein_annotations (protein_id, uniprot_data, chembl_data, open_targets_data, cached_at)
VALUES (:proteinId, :uniprotData::jsonb, :chemblData::jsonb, :openTargetsData::jsonb, NOW())
ON CONFLICT (protein_id)
DO UPDATE SET
   uniprot_data = EXCLUDED.uniprot_data,
   chembl_data = EXCLUDED.chembl_data,
   open_targets_data = EXCLUDED.open_targets_data,
   cached_at = NOW();

