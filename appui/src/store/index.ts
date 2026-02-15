import { defineStore } from 'pinia'

export interface GlobalState {
  loading: boolean
  theme: string
}

export const useGlobalStore = defineStore('global', {
  state: (): GlobalState => ({
    loading: false,
    theme: 'light'
  }),

  actions: {
    setLoading(loading: boolean) {
      this.loading = loading
    },

    toggleTheme() {
      this.theme = this.theme === 'light' ? 'dark' : 'light'
    }
  }
})