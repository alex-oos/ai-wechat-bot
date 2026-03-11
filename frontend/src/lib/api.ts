import { useAuthStore } from '../stores/auth'

export type ApiResult<T> = { success: true } & T
export type ApiError = { success: false; message?: string }

const API_BASE = import.meta.env.VITE_API_BASE ?? ''

async function request<T>(path: string, init: RequestInit & { disableAuth?: boolean } = {}) {
  const auth = useAuthStore()

  const headers = new Headers(init.headers)
  if (!headers.has('Content-Type') && init.body) {
    headers.set('Content-Type', 'application/json')
  }
  if (!init.disableAuth && auth.token) {
    headers.set('Authorization', `Bearer ${auth.token}`)
  }

  const res = await fetch(`${API_BASE}${path}`, { ...init, headers })

  const contentType = res.headers.get('content-type') ?? ''
  const isJson = contentType.includes('application/json')
  const body = isJson ? await res.json() : await res.text()

  if (res.status === 401) {
    auth.clear()
    throw new Error('未登录或登录已过期，请重新登录')
  }

  if (!res.ok) {
    const msg =
      typeof body === 'object' && body && 'message' in body ? String((body as any).message) : `请求失败: ${res.status}`
    throw new Error(msg)
  }

  return body as T
}

export const api = {
  health: () => request<{ success: boolean; status: string }>('/api/health'),
  login: (username: string, password: string) =>
    request<{ success: boolean; token?: string; message?: string }>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
      disableAuth: true,
    }),
  getBotConfig: () => request<{ success: boolean; config: any }>('/api/config/bot'),
  updateBotConfig: (payload: Record<string, any>) =>
    request<{ success: boolean; config: any }>('/api/config/bot', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  getChannelConfig: () => request<{ success: boolean; config: any }>('/api/config/channel'),
  updateChannelConfig: (payload: Record<string, any>) =>
    request<{ success: boolean; config: any }>('/api/config/channel', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  getAiConfig: () => request<{ success: boolean; config: any }>('/api/config/ai'),
  updateAiConfig: (payload: Record<string, any>) =>
    request<{ success: boolean; config: any }>('/api/config/ai', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  createAiProvider: (payload: Record<string, any>) =>
    request<{ success: boolean; provider?: any }>('/api/config/ai/providers', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  updateAiProvider: (id: number, payload: Record<string, any>) =>
    request<{ success: boolean; provider?: any }>(`/api/config/ai/providers/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  deleteAiProvider: (id: number) =>
    request<{ success: boolean }>(`/api/config/ai/providers/${id}`, {
      method: 'DELETE',
    }),
  setActiveAiProvider: (id: number) =>
    request<{ success: boolean }>(`/api/config/ai/active/${id}`, {
      method: 'PUT',
    }),
  wechatQr: () =>
    request<{
      success: boolean
      appId?: string
      uuid?: string
      qrData?: string
      qrImage?: string
      message?: string
    }>('/api/wechat/qr'),
  wechatQrStatus: (appId: string, uuid: string, captchCode?: string) =>
    request<{
      success: boolean
      status?: number
      expiredTime?: number
      nickName?: string
      loggedIn?: boolean
      message?: string
    }>('/api/wechat/qr/status', {
      method: 'POST',
      body: JSON.stringify({ appId, uuid, captchCode }),
    }),
}
