import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  withCredentials: true,
})

let accessToken: string | null = null

export function setAccessToken(token: string | null) {
  accessToken = token
}

api.interceptors.request.use((config) => {
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`
  return config
})

type RetryConfig = InternalAxiosRequestConfig & { _hrgeniusRetried?: boolean }
let refreshInFlight: Promise<string> | null = null

api.interceptors.response.use((response) => response, async (error: unknown) => {
  if (!axios.isAxiosError(error)) return Promise.reject(error)
  const config = error.config as RetryConfig | undefined
  const endpoint = config?.url ?? ''
  const isAuthBootstrap = ['/api/v1/auth/login', '/api/v1/auth/refresh', '/api/v1/auth/logout'].includes(endpoint)
  if (error.response?.status !== 401 || !config || config._hrgeniusRetried || isAuthBootstrap) {
    return Promise.reject(error)
  }

  config._hrgeniusRetried = true
  refreshInFlight ??= api.post<{ accessToken: string }>('/api/v1/auth/refresh')
    .then(({ data }) => {
      setAccessToken(data.accessToken)
      return data.accessToken
    })
    .finally(() => { refreshInFlight = null })

  try {
    const token = await refreshInFlight
    config.headers.Authorization = `Bearer ${token}`
    return api(config)
  } catch (refreshError) {
    setAccessToken(null)
    window.dispatchEvent(new CustomEvent('hrgenius:session-expired'))
    return Promise.reject(refreshError)
  }
})
