import { useEffect, useState } from 'react'
import { getAllIncidents } from '../services/incidentService'
import { getErrorMessage } from '../services/api'
import IncidentTable from '../components/IncidentTable'

export default function AdminIncidents() {
  const [incidents, setIncidents] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getAllIncidents().then(setIncidents).catch((e) => setError(getErrorMessage(e)))
  }, [])

  return (
    <div className="card">
      <h2>Tous les incidents</h2>
      <p className="muted">Ouvrez un incident pour l'affecter a un technicien.</p>
      {error && <div className="alert error">{error}</div>}
      {!incidents && !error && <p>Chargement...</p>}
      {incidents && <IncidentTable incidents={incidents} showOwner showAssignee />}
    </div>
  )
}
