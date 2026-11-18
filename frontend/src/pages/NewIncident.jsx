import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { CATEGORIES, PRIORITIES, createIncident } from '../services/incidentService'
import { getErrorMessage } from '../services/api'

export default function NewIncident() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', description: '', category: 'OTHER', priority: 'MEDIUM' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const created = await createIncident(form)
      navigate(`/incidents/${created.id}`)
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card narrow">
      <h2>Declarer un incident</h2>
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <label>Titre</label>
        <input name="title" value={form.title} onChange={change} maxLength={150} />
        <label>Description</label>
        <textarea name="description" rows={5} value={form.description} onChange={change} maxLength={2000} />
        <label>Categorie</label>
        <select name="category" value={form.category} onChange={change}>
          {CATEGORIES.map((c) => (
            <option key={c}>{c}</option>
          ))}
        </select>
        <label>Priorite</label>
        <select name="priority" value={form.priority} onChange={change}>
          {PRIORITIES.map((p) => (
            <option key={p}>{p}</option>
          ))}
        </select>
        <button className="btn" disabled={loading}>
          {loading ? 'Envoi...' : "Declarer l'incident"}
        </button>
      </form>
    </div>
  )
}
