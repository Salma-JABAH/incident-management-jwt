import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Bloque l'acces aux pages si l'utilisateur n'est pas connecte. (Confort UX : la vraie securite est cote backend.) */
export default function ProtectedRoute() {
  const { session } = useAuth()
  const location = useLocation()

  if (!session) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }
  return <Outlet />
}
