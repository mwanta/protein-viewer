import { useState } from "react"

function ProteinSearch({ onSearch, error }) {
  const [pdbId, setPdbId] = useState("")

  const handleSearch = () => {
    if (pdbId.trim()) onSearch(pdbId.trim().toUpperCase())
  }

  return (
    <div>
      <input
        value={pdbId}
        onChange={e => setPdbId(e.target.value)}
        onKeyDown={e => e.key === "Enter" && handleSearch()}
        placeholder="Enter PDB ID (e.g. 4HHB)"
      />
      <button onClick={handleSearch}>Search</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  )
}

export default ProteinSearch