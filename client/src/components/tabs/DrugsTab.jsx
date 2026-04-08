function DrugsTab({ annotation }) {
  const activities = annotation?.chemblData?.activities

  if (!activities?.length) return <p>No drug data available.</p>

  return (
    <div>
      <h3>Known drugs and compounds</h3>
      <table>
        <thead>
          <tr>
            <th>Compound</th>
            <th>Activity type</th>
            <th>Value</th>
            <th>Units</th>
          </tr>
        </thead>
        <tbody>
          {activities.slice(0, 10).map((a, i) => (
            <tr key={i}>
              <td>{a.molecule_chembl_id}</td>
              <td>{a.standard_type}</td>
              <td>{a.standard_value}</td>
              <td>{a.standard_units}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default DrugsTab