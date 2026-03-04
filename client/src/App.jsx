
import { useState, useEffect, useRef } from "react"
import * as $3Dmol from "3dmol"
import './App.css'

function App() {
  const [pdbId, setPdbId] = useState("")
  const [protein, setProtein] = useState(null)
  const [error, setError] = useState(null)

  const search = async () => {
    try {
      setError(null)
      const res = await fetch(`http://localhost:8080/api/protein/${pdbId}`)
      if (!res.ok) throw new Error("Protein not found")
      const data = await res.json()
      setProtein(data)
    } catch (err) {
      setError(err.message)
      setProtein(null)
    }
  }

  const viewerRef = useRef(null)

  useEffect(() => {
    if (!protein) return

    const viewer = $3Dmol.createViewer(viewerRef.current, {
      backgroundColor: "black"
    })

    $3Dmol.download(`pdb:${protein.entry?.id}`, viewer, { multimodel: true }, () => {
      viewer.setStyle({}, { cartoon: { color: "spectrum" } })
      viewer.zoomTo()
      viewer.render()
    })
  }, [protein])

  return (
    <div>
      <h1>Protein Structure Viewer</h1>
      <input
        value={pdbId}
        onChange={e => setPdbId(e.target.value)}
        placeholder="Enter PDB ID (e.g. 4HHB)"
      />
      <button onClick={search}>Search</button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {protein && (
        <div>
          <h2>{protein.struct?.title}</h2>
          <p>ID: {protein.entry?.id}</p>
          <p>Method: {protein.exptl?.[0]?.method}</p>
        </div>
      )}

      {protein && (
        <div
          ref={viewerRef}
          style={{ width: "640px", height: "480px", position: "relative" }}
        />
      )}
    </div>
  )
}

export default App
