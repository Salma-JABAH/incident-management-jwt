import { useEffect, useState } from 'react'
import { changePassword, getProfile, updateProfile } from '../services/authService'
import { getErrorMessage } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { fmt } from '../components/IncidentTable'

export default function Profile() {
  const { refreshSession } = useAuth()
  const [profile, setProfile] = useState(null)
  const [form, setForm] = useState({ fullName: '', email: '' })
  const [pwd, setPwd] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' })
  const [profileMsg, setProfileMsg] = useState(null)
  const [pwdMsg, setPwdMsg] = useState(null)

  useEffect(() => {
    getProfile().then((p) => {
      setProfile(p)
      setForm({ fullName: p.fullName, email: p.email })
    })
  }, [])

  const saveProfile = async (e) => {
    e.preventDefault()
    setProfileMsg(null)
    try {
      await updateProfile(form.fullName, form.email)
      refreshSession()
      setProfile(await getProfile())
      setProfileMsg({ type: 'info', text: 'Profil mis a jour.' })
    } catch (err) {
      setProfileMsg({ type: 'error', text: getErrorMessage(err) })
    }
  }

  const savePassword = async (e) => {
    e.preventDefault()
    setPwdMsg(null)
    if (pwd.newPassword !== pwd.confirmPassword) {
      setPwdMsg({ type: 'error', text: 'La confirmation ne correspond pas au nouveau mot de passe.' })
      return
    }
    try {
      const res = await changePassword(pwd.oldPassword, pwd.newPassword, pwd.confirmPassword)
      setPwdMsg({ type: 'info', text: res.message })
      setPwd({ oldPassword: '', newPassword: '', confirmPassword: '' })
    } catch (err) {
      setPwdMsg({ type: 'error', text: getErrorMessage(err) })
    }
  }

  if (!profile) return <p>Chargement...</p>

  return (
    <div className="grid2">
      <div className="card">
        <h2>Mon profil</h2>
        <p className="muted">
          Role : <span className={`badge role-${profile.role}`}>{profile.role}</span> • Membre depuis le{' '}
          {fmt(profile.createdAt)}
        </p>
        {profileMsg && <div className={`alert ${profileMsg.type}`}>{profileMsg.text}</div>}
        <form onSubmit={saveProfile}>
          <label>Nom complet</label>
          <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
          <label>Email</label>
          <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <button className="btn">Enregistrer</button>
        </form>
      </div>

      <div className="card">
        <h2>Changer le mot de passe</h2>
        {pwdMsg && <div className={`alert ${pwdMsg.type}`}>{pwdMsg.text}</div>}
        <form onSubmit={savePassword}>
          <label>Ancien mot de passe</label>
          <input
            type="password"
            value={pwd.oldPassword}
            onChange={(e) => setPwd({ ...pwd, oldPassword: e.target.value })}
            required
          />
          <label>Nouveau mot de passe</label>
          <input
            type="password"
            minLength={6}
            value={pwd.newPassword}
            onChange={(e) => setPwd({ ...pwd, newPassword: e.target.value })}
            required
          />
          <label>Confirmation du nouveau mot de passe</label>
          <input
            type="password"
            value={pwd.confirmPassword}
            onChange={(e) => setPwd({ ...pwd, confirmPassword: e.target.value })}
            required
          />
          <button className="btn">Modifier le mot de passe</button>
        </form>
      </div>
    </div>
  )
}
