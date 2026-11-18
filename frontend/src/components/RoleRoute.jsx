import { Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Restreint une page a certains roles, ex : <RoleRoute roles={['ADMIN']} /> */
export default function RoleRoute({ roles }) {
  const { session } = useAuth()

  if (!session || !roles.includes(session.role)) {
    return (
      <div className="card center">
        <h2>403 - Acces refuse</h2>
        <p>Vous n'avez pas les droits necessaires pour acceder a cette page.</p>
      </div>
    )
  }
  return <Outlet />
}
