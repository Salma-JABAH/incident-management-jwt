import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { addComment, assignIncident, getIncident, getUsers, updateStatus } from '../services/incidentService'
import { getErrorMessage } from '../services/api'
import { PriorityBadge, StatusBadge, fmt } from '../components/IncidentTable'

export default function IncidentDetails() {
  const { id } = useParams()
  const { session } = useAuth()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [technicians, setTechnicians] = useState([])
  const [techId, setTechId] = useState('')
  const [newStatus, setNewStatus] = useState('IN_PROGRESS')
  const [note, setNote] = useState('')

  const load = useCallback(async () => {
    try {
      setData(await getIncident(id))
      setError('')
    } catch (e) {
      setData(null)
      setError(getErrorMessage(e))
    }
  }, [id])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (session.role === 'ADMIN') {
      getUsers()
        .then((users) => setTechnicians(users.filter((u) => u.role === 'TECHNICIAN' && u.enabled)))
        .catch(() => {})
    }
  }, [session.role])

  const run = async (action, okMessage) => {
    setNotice('')
    setError('')
    try {
      await action()
      setNotice(okMessage)
      await load()
    } catch (e) {
      setError(getErrorMessage(e))
    }
  }

  if (!data) {
    return (
      <div className="card">
        {error ? <div className="alert error">{error}</div> : <p>Chargement...</p>}
        <Link to="/">← Retour</Link>
      </div>
    )
  }

  const { incident, history, comments } = data
  const closed = incident.status === 'CLOSED'
  const isAdmin = session.role === 'ADMIN'
  const isTech = session.role === 'TECHNICIAN'
  // Le backend reste l'autorite : ces conditions servent seulement a n'afficher que les actions utiles
  const statusOptions = isAdmin ? ['IN_PROGRESS', 'RESOLVED', 'CLOSED'] : ['IN_PROGRESS', 'RESOLVED']

  return (
    <div>
      <Link to="/">← Retour</Link>
      {error && <div className="alert error">{error}</div>}
      {notice && <div className="alert info">{notice}</div>}

      <div className="card">
        <div className="row between">
          <h2>
            #{incident.id} - {incident.title}
          </h2>
          <div>
            <PriorityBadge priority={incident.priority} /> <StatusBadge status={incident.status} />
          </div>
        </div>
        <p>{incident.description}</p>
        <div className="meta">
          <span>Categorie : <b>{incident.category}</b></span>
          <span>Declare par : <b>{incident.createdByName}</b></span>
          <span>Technicien : <b>{incident.assignedToName || 'non affecte'}</b></span>
          <span>Cree le : <b>{fmt(incident.createdAt)}</b></span>
          <span>Mis a jour : <b>{fmt(incident.updatedAt)}</b></span>
        </div>
      </div>

      {(isAdmin || isTech) && !closed && (
        <div className="card">
          <h3>Actions</h3>
          {isAdmin && (
            <div className="row">
              <select value={techId} onChange={(e) => setTechId(e.target.value)}>
                <option value="">-- Choisir un technicien --</option>
                {technicians.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.fullName}
                  </option>
                ))}
              </select>
              <button
                className="btn"
                disabled={!techId}
                onClick={() => run(() => assignIncident(incident.id, techId), 'Incident affecte.')}
              >
                Affecter
              </button>
            </div>
          )}
          <div className="row">
            <select value={newStatus} onChange={(e) => setNewStatus(e.target.value)}>
              {statusOptions.map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
            <button className="btn" onClick={() => run(() => updateStatus(incident.id, newStatus), 'Statut mis a jour.')}>
              Changer le statut
            </button>
          </div>
        </div>
      )}
      {closed && (isAdmin || isTech) && <div className="alert info">Incident clos : plus aucune modification possible.</div>}

      <div className="grid2">
        <div className="card">
          <h3>Historique</h3>
          <ul className="timeline">
            {history.map((h) => (
              <li key={h.id}>
                <span className="time">{fmt(h.createdAt)}</span> {h.message}
              </li>
            ))}
          </ul>
        </div>

        <div className="card">
          <h3>Notes de traitement</h3>
          {comments.length === 0 && <p className="muted">Aucune note pour le moment.</p>}
          {comments.map((c) => (
            <div key={c.id} className="note">
              <div className="muted">
                {c.authorName} - {fmt(c.createdAt)}
              </div>
              {c.content}
            </div>
          ))}
          {isTech && !closed && (
            <form
              onSubmit={(e) => {
                e.preventDefault()
                run(async () => {
                  await addComment(incident.id, note)
                  setNote('')
                }, 'Note ajoutee.')
              }}
            >
              <label>Ajouter une note</label>
              <textarea rows={3} value={note} onChange={(e) => setNote(e.target.value)} maxLength={2000} />
              <button className="btn" disabled={!note.trim()}>
                Ajouter
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}
