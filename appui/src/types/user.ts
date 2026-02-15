// 用户相关类型定义
export interface UserInfo {
  id: number
  username: string
  email: string
  nickname?: string
  avatar?: string
  role: string
  createTime: string
}

export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

export interface RegisterResponse {
  id: number
  username: string
  email: string
  nickname?: string
  createTime: string
}