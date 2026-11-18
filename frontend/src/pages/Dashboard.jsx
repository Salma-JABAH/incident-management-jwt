import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { getAssignedIncidents, getMyIncidents, getStatistics } from '../services/incidentService'
import { getErrorMessage } from '../services/api'
import IncidentTable from '../components/IncidentTable'

function Stat({ label, value, tone }) {
  return (
    <div className={`stat ${tone || ''}`}>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  )
}

function EmployeeDashboard() {
  const [incidents, setIncidents] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getMyIncidents().then(setIncidents).catch((e) => setError(getErrorMessage(e)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!incidents) return <p>Chargement...</p>

  const count = (...s) => incidents.filter((i) => s.includes(i.status)).length
  return (
    <>
      <div className="stats">
        <Stat label="Total de mes incidents" value={incidents.length} />
        <Stat label="OPEN" value={count('OPEN')} tone="warn" />
        <Stat label="IN_PROGRESS" value={count('IN_PROGRESS')} tone="info" />
        <Stat label="RESOLVED / CLOSED" value={count('RESOLVED', 'CLOSED')} tone="ok" />
      </div>
      <div className="card">
        <h3>Mes derniers incidents</h3>
        <IncidentTable incidents={incidents.slice(0, 5)} showAssignee />
      </div>
    </>
  )
}

function TechnicianDashboard() {
  const [incidents, setIncidents] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getAssignedIncidents().then(setIncidents).catch((e) => setError(getErrorMessage(e)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!incidents) return <p>Chargement...</p>

  const count = (...s) => incidents.filter((i) => s.includes(i.status)).length
  return (
    <>
      <div className="stats">
        <Stat label="Incidents affectes" value={incidents.length} />
        <Stat label="Incidents critiques" value={incidents.filter((i) => i.priority === 'CRITICAL').length} tone="danger" />
        <Stat label="En cours" value={count('IN_PROGRESS')} tone="info" />
        <Stat label="Resolus" value={count('RESOLVED', 'CLOSED')} tone="ok" />
      </div>
      <div className="card">
        <h3>Derniers incidents affectes</h3>
        <IncidentTable incidents={incidents.slice(0, 5)} showOwner />
      </div>
    </>
  )
}

function AdminDashboard() {
  const [s, setS] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getStatistics().then(setS).catch((e) => setError(getErrorMessage(e)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!s) return <p>Chargement...</p>

  return (
    <>
      <h3>Utilisateurs</h3>
      <div className="stats">
        <Stat label="Total utilisateurs" value={s.totalUsers} />
        <Stat label="Employes" value={s.employees} />
        <Stat label="Techniciens" value={s.technicians} />
      </div>
      <h3>Incidents</h3>
      <div className="stats">
        <Stat label="Total incidents" value={s.totalIncidents} />
        <Stat label="OPEN" value={s.open} tone="warn" />
        <Stat label="IN_PROGRESS" value={s.inProgress} tone="info" />
        <Stat label="CLOSED" value={s.closed} tone="ok" />
      </div>
    </>
  )
}

export default function Dashboard() {
  const { session } = useAuth()
  return (
    <div>
      <h2>Bonjour {session.fullName} 👋</h2>
      <p className="muted">Tableau de bord - role {session.role}</p>
      {session.role === 'EMPLOYEE' && <EmployeeDashboard />}
      {session.role === 'TECHNICIAN' && <TechnicianDashboard />}
      {session.role === 'ADMIN' && <AdminDashboard />}
    </div>
  )
}
