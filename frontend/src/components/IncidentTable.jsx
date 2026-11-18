import { Link } from 'react-router-dom'

export const fmt = (iso) => (iso ? new Date(iso).toLocaleString('fr-FR') : '-')

export function StatusBadge({ status }) {
  return <span className={`badge status-${status}`}>{status}</span>
}

export function PriorityBadge({ priority }) {
  return <span className={`badge prio-${priority}`}>{priority}</span>
}

/** Tableau reutilisable de liste d'incidents. */
export default function IncidentTable({ incidents, showOwner = false, showAssignee = false }) {
  if (!incidents.length) return <p className="muted">Aucun incident.</p>
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Titre</th>
            <th>Categorie</th>
            <th>Priorite</th>
            <th>Statut</th>
            {showOwner && <th>Declare par</th>}
            {showAssignee && <th>Technicien</th>}
            <th>Cree le</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {incidents.map((i) => (
            <tr key={i.id}>
              <td>{i.id}</td>
              <td>{i.title}</td>
              <td>{i.category}</td>
              <td>
                <PriorityBadge priority={i.priority} />
              </td>
              <td>
                <StatusBadge status={i.status} />
              </td>
              {showOwner && <td>{i.createdByName}</td>}
              {showAssignee && <td>{i.assignedToName || '-'}</td>}
              <td>{fmt(i.createdAt)}</td>
              <td>
                <Link to={`/incidents/${i.id}`}>Details</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
