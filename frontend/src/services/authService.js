import api from './api'

export async function register(fullName, email, password) {
  const { data } = await api.post('/auth/register', { fullName, email, password })
  return data
}

export async function login(email, password) {
  const { data } = await api.post('/auth/login', { email, password })
  saveSession(data)
  return data
}

export async function logout() {
  const refreshToken = localStorage.getItem('refreshToken')
  try {
    if (refreshToken) await api.post('/auth/logout', { refreshToken })
  } catch (e) {
    // on deconnecte quand meme cote client
  }
  clearSession()
}

export function saveSession(data) {
  localStorage.setItem('token', data.token)
  if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken)
  localStorage.setItem('role', data.role)
  localStorage.setItem('fullName', data.fullName)
}

export function clearSession() {
  ;['token', 'refreshToken', 'role', 'fullName'].forEach((k) => localStorage.removeItem(k))
}

export function getSession() {
  const token = localStorage.getItem('token')
  if (!token) return null
  return {
    token,
    role: localStorage.getItem('role'),
    fullName: localStorage.getItem('fullName'),
  }
}

// ---------- Profil ----------
export async function getProfile() {
  const { data } = await api.get('/api/profile')
  return data
}

export async function updateProfile(fullName, email) {
  const { data } = await api.put('/api/profile', { fullName, email })
  saveSession(data) // le backend renvoie un nouveau token (l'email est le subject du JWT)
  return data
}

export async function changePassword(oldPassword, newPassword, confirmPassword) {
  const { data } = await api.put('/api/profile/password', { oldPassword, newPassword, confirmPassword })
  return data
}
