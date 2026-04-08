function FavoritesList({ favorites, onLoad, onRemove }) {
  if (favorites.length === 0) return null

  return (
    <div>
      <h2>Favorites</h2>
      {favorites.map(fav => (
        <div key={fav.protein.pdbId}>
          <span>{fav.protein.pdbId}</span>
          {fav.protein.title && <span> — {fav.protein.title}</span>}
          <button onClick={() => onLoad(fav.protein.pdbId)}>Load</button>
          <button onClick={() => onRemove(fav.protein.pdbId)}>Remove</button>
        </div>
      ))}
    </div>
  )
}

export default FavoritesList