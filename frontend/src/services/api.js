import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

// ---------- Interceptor de requete : ajoute automatiquement "Authorization: Bearer <JWT>" ----------
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ---------- Interceptor de reponse : gestion du 401 (token expire / invalide) ----------
let refreshPromise = null

function clearSession() {
  ;['token', 'refreshToken', 'role', 'fullName'].forEach((k) => localStorage.removeItem(k))
}

function redirectToLogin() {
  clearSession()
  sessionStorage.setItem('sessionExpired', '1') // message affiche par la page Login
  if (window.location.pathname !== '/login') {
    window.location.assign('/login')
  }
}

// Tente d'obtenir un nouvel access token avec le refresh token (une seule requete a la fois)
function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) return Promise.reject(new Error('no refresh token'))
  if (!refreshPromise) {
    refreshPromise = axios
      .post(`${API_URL}/auth/refresh`, { refreshToken })
      .then(({ data }) => {
        localStorage.setItem('token', data.token)
        localStorage.setItem('refreshToken', data.refreshToken)
        return data.token
      })
      .finally(() => {
        refreshPromise = null
      })
  }
  return refreshPromise
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    const status = error.response?.status
    const isAuthCall = original?.url?.startsWith('/auth/')

    // 401 sur une route protegee (pas sur /auth/login : un mauvais mot de passe n'est pas une session expiree)
    if (status === 401 && !isAuthCall) {
      if (!original._retry && localStorage.getItem('refreshToken')) {
        original._retry = true
        try {
          const newToken = await refreshAccessToken()
          original.headers.Authorization = `Bearer ${newToken}`
          return api(original) // rejoue la requete initiale avec le nouveau token
        } catch (e) {
          // refresh impossible -> session terminee
        }
      }
      redirectToLogin()
    }
    return Promise.reject(error)
  }
)

/** Extrait un message lisible d'une erreur Axios. */
export function getErrorMessage(error) {
  const data = error.response?.data
  if (data?.errors) {
    return Object.values(data.errors).join(' • ')
  }
  return data?.message || 'Erreur de communication avec le serveur'
}

export default api
