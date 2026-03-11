import { defineStore } from 'pinia'

type AuthState = {
  token: string | null
}

const TOKEN_KEY = 'admin_token'

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem(TOKEN_KEY),
  }),
  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem(TOKEN_KEY, token)
    },
    clear() {
      this.token = null
      localStorage.removeItem(TOKEN_KEY)
    },
  },
})

