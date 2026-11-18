import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../services/authService'
import { getErrorMessage } from '../services/api'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(form.fullName, form.email, form.password)
      navigate('/login', { state: { registered: true } })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card narrow">
      <h2>Inscription</h2>
      <p className="muted">Votre compte sera cree avec le role EMPLOYEE.</p>
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <label>Nom complet</label>
        <input name="fullName" value={form.fullName} onChange={change} required />
        <label>Email</label>
        <input name="email" type="email" value={form.email} onChange={change} required />
        <label>Mot de passe (6 caracteres minimum)</label>
        <input name="password" type="password" minLength={6} value={form.password} onChange={change} required />
        <button className="btn" disabled={loading}>
          {loading ? 'Creation...' : "S'inscrire"}
        </button>
      </form>
      <p className="muted">
        Deja inscrit ? <Link to="/login">Se connecter</Link>
      </p>
    </div>
  )
}
