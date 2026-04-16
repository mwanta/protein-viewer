from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModel
import torch

app = FastAPI()

# Load model once at startup — ESM-2 8M is small enough to run on CPU
tokenizer = AutoTokenizer.from_pretrained("facebook/esm2_t6_8M_UR50D")
model = AutoModel.from_pretrained("facebook/esm2_t6_8M_UR50D")
model.eval()

class EmbedRequest(BaseModel):
    sequence: str

@app.post("/embed")
def embed(body: EmbedRequest):
    if not body.sequence:
        raise HTTPException(status_code=400, detail="Sequence is required")

    # Truncate very long sequences to avoid memory issues on CPU
    sequence = body.sequence[:1024]

    inputs = tokenizer(sequence, return_tensors="pt", truncation=True, max_length=1024)

    with torch.no_grad():
        outputs = model(**inputs)

    # Average pool across sequence length to get a single [320] vector
    embedding = outputs.last_hidden_state.mean(dim=1).squeeze().tolist()
    return {"embedding": embedding}

@app.get("/health")
def health():
    return {"status": "ok"}