import { useEffect, useState } from 'react'
import { disableUser, enableUser, getUsers } from '../services/incidentService'
import { getErrorMessage } from '../services/api'
import { fmt } from '../components/IncidentTable'

export default function AdminUsers() {
  const [users, setUsers] = useState(null)
  const [error, setError] = useState('')

  const load = () => getUsers().then(setUsers).catch((e) => setError(getErrorMessage(e)))
  useEffect(() => {
    load()
  }, [])

  const toggle = async (u) => {
    setError('')
    try {
      await (u.enabled ? disableUser(u.id) : enableUser(u.id))
      await load()
    } catch (e) {
      setError(getErrorMessage(e))
    }
  }

  return (
    <div className="card">
      <h2>Gestion des utilisateurs</h2>
      {error && <div className="alert error">{error}</div>}
      {!users && !error && <p>Chargement...</p>}
      {users && (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Nom</th>
                <th>Email</th>
                <th>Role</th>
                <th>Compte</th>
                <th>Cree le</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.fullName}</td>
                  <td>{u.email}</td>
                  <td>
                    <span className={`badge role-${u.role}`}>{u.role}</span>
                  </td>
                  <td>
                    <span className={`badge ${u.enabled ? 'status-RESOLVED' : 'prio-CRITICAL'}`}>
                      {u.enabled ? 'Actif' : 'Desactive'}
                    </span>
                  </td>
                  <td>{fmt(u.createdAt)}</td>
                  <td>
                    <button className={`btn small ${u.enabled ? 'danger' : ''}`} onClick={() => toggle(u)}>
                      {u.enabled ? 'Desactiver' : 'Activer'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
