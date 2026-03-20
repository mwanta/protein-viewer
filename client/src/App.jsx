
import { useState, useEffect, useRef } from "react"
import * as $3Dmol from "3dmol"
import './App.css'

function App() {
  const [token, setToken] = useState(null)
  const [authMode, setAuthMode] = useState("login")
  const [authUsername, setAuthUsername] = useState("")
  const [authPassword, setAuthPassword] = useState("")
  const [authError, setAuthError] = useState(null)

  const [pdbId, setPdbId] = useState("")
  const [protein, setProtein] = useState(null)
  const [error, setError] = useState(null)
  const [favorites, setFavorites] = useState([])
  const viewerRef = useRef(null)

  useEffect(() => {
      if (token) fetchFavorites()
  }, [token])

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

  const authFetch = (url, options = {}) => {
    return fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
        ...options.headers
      }
    })
  }

  const handleAuth = async () => {
    try {
      setAuthError(null)
      const res = await fetch(`/api/auth/${authMode}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: authUsername, password: authPassword })
      })
      if (!res.ok) {
        const msg = await res.text()
        throw new Error(msg || "Authentication failed")
      }
      if (authMode === "login") {
        const data = await res.json()
        setToken(data.token)
      } else {
        setAuthMode("login")
        setAuthError("Registration successful, please log in")
      }
    } catch (err) {
      setAuthError(err.message)
    }
  }

  const fetchFavorites = async () => {
    try {
      const res = await authFetch("/api/favorites")
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
      const res = await authFetch(`/api/protein/${pdbId}`)
      if (!res.ok) throw new Error("Protein not found")
      const data = await res.json()
      setProtein(data)
    } catch (err) {
      setError(err.message)
      setProtein(null)
    }
  }

  const addFavorite = async () => {
    await authFetch(`/api/favorites/${protein.entry?.id}`, { method: "POST" })
    fetchFavorites()
  }

  const removeFavorite = async (pdbId) => {
    await authFetch(`/api/favorites/${pdbId}`, { method: "DELETE" })
    fetchFavorites()
  }

  const loadFavorite = (pdbId) => {
    setPdbId(pdbId)
    authFetch(`/api/protein/${pdbId}`)
      .then(res => res.json())
      .then(data => setProtein(data))
      .catch(err => setError("Failed to load favorite"))
  }

  const isFavorite = protein && favorites.some(fav => fav.protein.pdbId === protein.entry?.id)

  if (!token) {
    return (
      <div>
        <h1>Protein Structure Viewer</h1>
        <h2>{authMode === "login" ? "Login" : "Register"}</h2>
        <input
          value={authUsername}
          onChange={e => setAuthUsername(e.target.value)}
          onKeyDown={e => e.key === "Enter" && handleAuth()}
          placeholder="Username"
        />
        <input
          type="password"
          value={authPassword}
          onChange={e => setAuthPassword(e.target.value)}
          onKeyDown={e => e.key === "Enter" && handleAuth()}
          placeholder="Password"
        />
        <button onClick={handleAuth}>
          {authMode === "login" ? "Login" : "Register"}
        </button>
        <button onClick={() => setAuthMode(authMode === "login" ? "register" : "login")}>
          {authMode === "login" ? "Need an account? Register" : "Have an account? Login"}
        </button>
        {authError && <p style={{ color: "red" }}>{authError}</p>}
      </div>
    )
  }

  return (
    <div>
      <h1>Protein Structure Viewer</h1>
      <button onClick={() => { setToken(null), setProtein(null), setFavorites([]) }}>Logout</button>

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
