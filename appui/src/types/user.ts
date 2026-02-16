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

// 登录响应类型 - 匹配后端LoginResponseVO
export interface LoginResponse {
  token: string
  username: string
  role: string
}

// 注册响应类型
export interface RegisterResponse {
  id: number
  username: string
  email: string
  nickname?: string
  createTime: string
}

// 完整的登录响应类型（包含用户详细信息）
export interface FullLoginResponse {
  token: string
  userInfo: UserInfo
}