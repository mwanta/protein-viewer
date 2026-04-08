function SimilarTab({ similar, loading, onLoadProtein }) {
  if (loading) {
    return <p>Generating embeddings, this may take a moment...</p>
  }

  if (!similar?.length) {
    return <p>No similar proteins found yet — check back shortly.</p>
  }

  return (
    <div>
      <h3>Similar proteins</h3>
      <table>
        <thead>
          <tr>
            <th>PDB ID</th>
            <th>Title</th>
            <th>Organism</th>
            <th>Similarity</th>
          </tr>
        </thead>
        <tbody>
          {similar.map((s, i) => (
            <tr
              key={i}
              style={{ cursor: "pointer" }}
              onClick={() => onLoadProtein(s.pdbId)}
            >
              <td>{s.pdbId}</td>
              <td>{s.title}</td>
              <td>{s.organism}</td>
              <td>{(s.similarity * 100).toFixed(1)}%</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default SimilarTab