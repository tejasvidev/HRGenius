import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api, setAccessToken } from '../../api/client'

export type Role = 'SUPER_ADMIN' | 'ADMIN' | 'HR_MANAGER' | 'HR_EXECUTIVE' | 'RECRUITER' | 'MANAGER' | 'EMPLOYEE'

export interface UserProfile {
  id: number
  email: string
  firstName: string
  lastName: string
  roles: Role[]
}

interface TokenResponse {
  accessToken: string
  tokenType: 'Bearer'
  expiresInSeconds: number
  user: UserProfile
}

interface AuthContextValue {
  user: UserProfile | null
  initializing: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null)
  useEffect(() => {
    const expireSession = () => setUser(null)
    window.addEventListener('hrgenius:session-expired', expireSession)
    return () => window.removeEventListener('hrgenius:session-expired', expireSession)
  }, [])
  const refresh = useQuery({
    queryKey: ['auth', 'refresh'],
    queryFn: async () => {
      const { data } = await api.post<TokenResponse>('/api/v1/auth/refresh')
      setAccessToken(data.accessToken)
      setUser(data.user)
      return data
    },
    retry: false,
    staleTime: Infinity,
  })

  const login = useCallback(async (email: string, password: string) => {
    const { data } = await api.post<TokenResponse>('/api/v1/auth/login', { email, password })
    setAccessToken(data.accessToken)
    setUser(data.user)
  }, [])

  const logout = useCallback(async () => {
    try {
      await api.post('/api/v1/auth/logout')
    } finally {
      setAccessToken(null)
      setUser(null)
    }
  }, [])

  const value = useMemo(() => ({ user, initializing: refresh.isLoading, login, logout }), [user, refresh.isLoading, login, logout])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const value = useContext(AuthContext)
  if (!value) throw new Error('useAuth must be used inside AuthProvider')
  return value
}
