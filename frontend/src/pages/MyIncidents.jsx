import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyIncidents } from '../services/incidentService'
import { getErrorMessage } from '../services/api'
import IncidentTable from '../components/IncidentTable'

export default function MyIncidents() {
  const [incidents, setIncidents] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getMyIncidents().then(setIncidents).catch((e) => setError(getErrorMessage(e)))
  }, [])

  return (
    <div className="card">
      <div className="row between">
        <h2>Mes incidents</h2>
        <Link className="btn" to="/incidents/new">
          + Nouvel incident
        </Link>
      </div>
      {error && <div className="alert error">{error}</div>}
      {!incidents && !error && <p>Chargement...</p>}
      {incidents && <IncidentTable incidents={incidents} showAssignee />}
    </div>
  )
}
