import { useState, useEffect } from "react"
import FunctionTab from "./tabs/FunctionTab"
import DrugsTab from "./tabs/DrugsTab"
import DiseasesTab from "./tabs/DiseasesTab"
import SimilarTab from "./tabs/SimilarTab"

function ProteinDetail({ annotation, similarProteins, pdbId, authFetch, onLoadProtein }) {
  const [activeTab, setActiveTab] = useState("function")
  const [similar, setSimilar] = useState(similarProteins ?? [])
  const [loadingSimilar, setLoadingSimilar] = useState(false)

    useEffect(() => {
        if (activeTab !== "similar" || similar.length > 0) return

        setLoadingSimilar(true)
        let attempts = 0
        const maxAttempts = 10

        const poll = async () => {
            attempts++
            if (attempts >= maxAttempts) {
                setLoadingSimilar(false)
                clearInterval(interval)
                return
            }
            try {
                const res = await authFetch(`/api/protein/${pdbId}/similar`)
                const data = await res.json()
                if (data.length > 0) {
                    setSimilar(data)
                    setLoadingSimilar(false)
                    clearInterval(interval)
                }
            } catch (err) {
                console.error(err)
                setLoadingSimilar(false)
                clearInterval(interval)
            }
        }

        poll()
        const interval = setInterval(poll, 5000)
        return () => clearInterval(interval)
    }, [activeTab, pdbId])

  useEffect(() => {
    setSimilar(similarProteins ?? [])
  }, [pdbId])

  const tabs = ["function", "drugs", "diseases", "similar"]

  return (
    <div>
      <div>
        {tabs.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            style={{ fontWeight: activeTab === tab ? "bold" : "normal" }}
          >
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </div>

      {activeTab === "function"  && <FunctionTab annotation={annotation} />}
      {activeTab === "drugs"     && <DrugsTab annotation={annotation} />}
      {activeTab === "diseases"  && <DiseasesTab annotation={annotation} />}
      {activeTab === "similar"   && (
        <SimilarTab
          similar={similar}
          loading={loadingSimilar}
          onLoadProtein={onLoadProtein}
        />
      )}
    </div>
  )
}

export default ProteinDetail