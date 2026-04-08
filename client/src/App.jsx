
import { useState, useEffect } from "react"
import ProteinSearch from "./components/ProteinSearch"
import ProteinViewer from "./components/ProteinViewer"
import ProteinDetail from "./components/ProteinDetail"
import FavoritesList from "./components/FavoritesList"
import "./App.css"

function App() {
  const [token, setToken] = useState(null)
  const [authMode, setAuthMode] = useState("login")
  const [authUsername, setAuthUsername] = useState("")
  const [authPassword, setAuthPassword] = useState("")
  const [authError, setAuthError] = useState(null)

  const [protein, setProtein] = useState(null)
  const [error, setError] = useState(null)
  const [favorites, setFavorites] = useState([])

  useEffect(() => {
      if (token) fetchFavorites()
  }, [token])

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

  const search = async (pdbId) => {
    try {
      setError(null)
      setProtein(null)
      const res = await authFetch(`/api/protein/${pdbId}`)
      if (!res.ok) throw new Error("Protein not found")
      const data = await res.json()
      setProtein(data)
    } catch (err) {
      setError(err.message)
    }
  }

  const addFavorite = async () => {
    await authFetch(`/api/favorites/${protein.protein.pdbId}`, { method: "POST" })
    fetchFavorites()
  }

  const removeFavorite = async (pdbId) => {
    await authFetch(`/api/favorites/${pdbId}`, { method: "DELETE" })
    fetchFavorites()
  }

  const isFavorite = protein && favorites.some(fav => fav.protein.pdbId === protein.protein.pdbId)

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
      <button onClick={() => { setToken(null), setProtein(null), setFavorites([]) }}>
      Logout
      </button>

      <ProteinSearch onSearch={search} error={error} />

      {protein && (
        <>
          <h2>{protein.protein.title}</h2>
          <p>ID: {protein.protein.pdbId}</p>
          <p>Organism: {protein.protein.organism}</p>
          <p>Resolution: {protein.protein.resolution} Å</p>
          <button onClick={isFavorite ? () => removeFavorite(protein.protein.pdbId) : addFavorite}>
            {isFavorite ? "Remove from Favorites" : "Add to Favorites"}
          </button>
          <ProteinViewer pdbId={protein.protein.pdbId} />
          <ProteinDetail
            annotation={protein.annotation}
            similarProteins={protein.similarProteins}
            pdbId={protein.protein.pdbId}
            authFetch={authFetch}
            onLoadProtein={search}
          />
        </>
      )}

      <FavoritesList
        favorites={favorites}
        onLoad={search}
        onRemove={removeFavorite}
      />
    </div>

      
  )
}

export default App
