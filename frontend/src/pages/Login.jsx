import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage } from '../services/api'

export default function Login() {
  const { login, session } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    // Message affiche apres une redirection due a un 401 (token expire)
    if (sessionStorage.getItem('sessionExpired')) {
      setInfo('Votre session a expire. Veuillez vous reconnecter.')
      sessionStorage.removeItem('sessionExpired')
    }
    if (location.state?.registered) {
      setInfo('Compte cree avec succes. Vous pouvez maintenant vous connecter.')
    }
  }, [location.state])

  useEffect(() => {
    if (session) navigate('/', { replace: true })
  }, [session, navigate])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setInfo('')
    setLoading(true)
    try {
      await login(email, password)
      navigate(location.state?.from?.pathname || '/', { replace: true })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card narrow">
      <h2>Connexion</h2>
      {info && <div className="alert info">{info}</div>}
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <label>Email</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <label>Mot de passe</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button className="btn" disabled={loading}>
          {loading ? 'Connexion...' : 'Se connecter'}
        </button>
      </form>
      <p className="muted">
        Pas de compte ? <Link to="/register">Creer un compte</Link>
      </p>
    </div>
  )
}
