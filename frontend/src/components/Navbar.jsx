import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { session, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="brand">🛡️ Incidents IT</div>
      {session ? (
        <>
          <div className="links">
            <NavLink to="/">Dashboard</NavLink>
            {session.role === 'EMPLOYEE' && (
              <>
                <NavLink to="/incidents/my">Mes incidents</NavLink>
                <NavLink to="/incidents/new">Nouvel incident</NavLink>
              </>
            )}
            {session.role === 'TECHNICIAN' && <NavLink to="/incidents/assigned">Incidents affectes</NavLink>}
            {session.role === 'ADMIN' && (
              <>
                <NavLink to="/admin/incidents">Tous les incidents</NavLink>
                <NavLink to="/admin/users">Utilisateurs</NavLink>
              </>
            )}
            <NavLink to="/profile">Profil</NavLink>
          </div>
          <div className="user">
            <span>
              {session.fullName} <span className={`badge role-${session.role}`}>{session.role}</span>
            </span>
            <button className="btn small secondary" onClick={handleLogout}>
              Deconnexion
            </button>
          </div>
        </>
      ) : (
        <div className="links">
          <NavLink to="/login">Connexion</NavLink>
          <NavLink to="/register">Inscription</NavLink>
        </div>
      )}
    </nav>
  )
}
