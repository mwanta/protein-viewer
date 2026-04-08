import { useEffect, useRef } from "react"
import * as $3Dmol from "3dmol"

function ProteinViewer({ pdbId }) {
  const viewerRef = useRef(null)

  useEffect(() => {
    if (!pdbId || !viewerRef.current) return

    const viewer = $3Dmol.createViewer(viewerRef.current, {
      backgroundColor: "black"
    })

    $3Dmol.download(`pdb:${pdbId}`, viewer, { multimodel: true }, () => {
      viewer.setStyle({}, { cartoon: { color: "spectrum" } })
      viewer.zoomTo()
      viewer.render()
    })

    // Clean up viewer when pdbId changes or component unmounts
    return () => viewer.clear()
  }, [pdbId])

  return (
    <div
      ref={viewerRef}
      style={{ width: "640px", height: "480px", position: "relative" }}
    />
  )
}

export default ProteinViewer