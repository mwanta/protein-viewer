function DiseasesTab({ annotation }) {
  const rows = annotation?.openTargetsData?.data?.target?.associatedDiseases?.rows

  if (!rows?.length) return <p>No disease association data available.</p>

  return (
    <div>
      <h3>Disease associations</h3>
      <table>
        <thead>
          <tr>
            <th>Disease</th>
            <th>Association score</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i}>
              <td>{row.disease.name}</td>
              <td>{row.score.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default DiseasesTab