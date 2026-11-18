import { createContext, useContext, useState, useCallback } from 'react'
import * as authService from '../services/authService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => authService.getSession())

  const login = useCallback(async (email, password) => {
    await authService.login(email, password)
    setSession(authService.getSession())
  }, [])

  const logout = useCallback(async () => {
    await authService.logout()
    setSession(null)
  }, [])

  // utilise apres modification du profil (nouveau token / nouveau nom)
  const refreshSession = useCallback(() => setSession(authService.getSession()), [])

  return (
    <AuthContext.Provider value={{ session, login, logout, refreshSession }}>{children}</AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
