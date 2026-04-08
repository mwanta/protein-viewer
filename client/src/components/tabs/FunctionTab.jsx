const getComment = (uniprotData, type) => {
  const comments = uniprotData?.comments
  if (!comments) return null
  const match = comments.find(c => c.commentType === type)
  return match?.texts?.[0]?.value ?? null
}

function FunctionTab({ annotation }) {
  if (!annotation) return <p>No annotation data available.</p>

  return (
    <div>
      <h3>Function</h3>
      <p>{getComment(annotation.uniprotData, "FUNCTION") ?? "No function data available."}</p>
      <h4>Subcellular location</h4>
      <p>{getComment(annotation.uniprotData, "SUBCELLULAR LOCATION") ?? "Not available."}</p>
    </div>
  )
}

export default FunctionTab