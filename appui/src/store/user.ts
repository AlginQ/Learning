import { defineStore } from 'pinia'
import type { UserInfo } from '@/types/user'
import { loginApi, registerApi, getUserInfoApi } from '@/api/auth'
import type { LoginRequest, RegisterRequest } from '@/types/auth'

interface UserState {
  userInfo: UserInfo | null
  token: string | null
  isAuthenticated: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token')
  }),

  getters: {
    isLogin: (state) => state.isAuthenticated,
    currentUser: (state) => state.userInfo
  },

  actions: {
    // 用户登录
    async login(loginData: LoginRequest) {
      try {
        const response = await loginApi(loginData)
        const { token, username, role } = response.data
        
        // 保存token到localStorage，以便后续API调用使用
        localStorage.setItem('token', token)
        
        // 获取用户详细信息
        const userInfoResponse = await getUserInfoApi()
        const userInfo = userInfoResponse.data
        
        this.token = token
        this.userInfo = userInfo
        this.isAuthenticated = true
        
        // 保存到localStorage
        localStorage.setItem('role', role)
        localStorage.setItem('username', username)
        
        return response
      } catch (error) {
        console.error('登录过程出错:', error)
        throw error
      }
    },

    // 用户注册
    async register(registerData: RegisterRequest) {
      try {
        const response = await registerApi(registerData)
        return response
      } catch (error) {
        throw error
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await getUserInfoApi()
        this.userInfo = response.data
        return response
      } catch (error) {
        throw error
      }
    },

    // 退出登录
    logout() {
      this.userInfo = null
      this.token = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('username')
    }
  }
})