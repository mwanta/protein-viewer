
import { useState, useEffect, useRef } from "react"
import * as $3Dmol from "3dmol"
import './App.css'

function App() {
  const [pdbId, setPdbId] = useState("")
  const [protein, setProtein] = useState(null)
  const [error, setError] = useState(null)
  const [favorites, setFavorites] = useState([])
  const viewerRef = useRef(null)

  useEffect(() => {
      fetchFavorites()
  },   [])

  useEffect(() => {
    if (!protein || !viewerRef.current) return

    const viewer = $3Dmol.createViewer(viewerRef.current, {
      backgroundColor: "black"
    })

    $3Dmol.download(`pdb:${protein.entry?.id}`, viewer, { multimodel: true }, () => {
      viewer.setStyle({}, { cartoon: { color: "spectrum" } })
      viewer.zoomTo()
      viewer.render()
    })
  }, [protein])

  const fetchFavorites = async () => {
    try {
      const res = await fetch("/api/favorites")
      if (!res.ok) throw new Error("Failed to fetch favorites")
      const data = await res.json()
      setFavorites(data)
    } catch (err) {
      console.error(err)
    }
  }

  const search = async () => {
    try {
      setError(null)
      const res = await fetch(`/api/protein/${pdbId}`)
      if (!res.ok) throw new Error("Protein not found")
      const data = await res.json()
      setProtein(data)
    } catch (err) {
      setError(err.message)
      setProtein(null)
    }
  }

  const addFavorite = async () => {
    await fetch(`/api/favorites/${protein.entry?.id}`, { method: "POST" })
    fetchFavorites()
  }

  const removeFavorite = async (pdbId) => {
    await fetch(`/api/favorites/${pdbId}`, { method: "DELETE" })
    fetchFavorites()
  }

  const loadFavorite = (pdbId) => {
    setPdbId(pdbId)
    fetch(`/api/protein/${pdbId}`)
      .then(res => res.json())
      .then(data => setProtein(data))
      .catch(err => setError("Failed to load favorite"))
  }

  const isFavorite = protein && favorites.some(fav => fav.protein.pdbId === protein.entry?.id)

  return (
    <div>
      <h1>Protein Structure Viewer</h1>

      <input
        value={pdbId}
        onChange={e => setPdbId(e.target.value)}
        onKeyDown={e => e.key === "Enter" && search()}
        placeholder="Enter PDB ID (e.g. 4HHB)"
      />
      <button onClick={search}>Search</button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {protein && (
        <div>
          <h2>{protein.struct?.title}</h2>
          <p>ID: {protein.entry?.id}</p>
          <p>Method: {protein.exptl?.[0]?.method}</p>
          <button onClick={isFavorite? () => removeFavorite(protein.entry?.id) : addFavorite}>
            {isFavorite ? "Remove from Favorites" : "Add to Favorites"}
          </button>
        </div>
      )}

      {protein && (
        <div
          ref={viewerRef}
          style={{ width: "640px", height: "480px", position: "relative" }}
        />
      )}

      {favorites.length > 0 && (
        <div>
          <h2>Favorites</h2>
          {favorites.map(fav => (
            <div key={fav.protein.pdbId}>
              <span>{fav.protein.pdbId}</span>
              <button onClick={() => loadFavorite(fav.protein.pdbId)}>Load</button>
              <button onClick={() => removeFavorite(fav.protein.pdbId)}>Remove</button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default App
