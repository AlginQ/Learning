import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { LoginRequest, RegisterRequest } from '@/types/auth'
import type { LoginResponse, RegisterResponse, UserInfo } from '@/types/user'

// 用户登录
export const loginApi = (data: LoginRequest) => {
  return request.post<ApiResponse<LoginResponse>>('/auth/login', data)
}

// 用户注册
export const registerApi = (data: RegisterRequest) => {
  return request.post<ApiResponse<RegisterResponse>>('/auth/register', data)
}

// 获取当前用户信息
export const getUserInfoApi = () => {
  return request.get<ApiResponse<UserInfo>>('/auth/info')
}

// 退出登录
export const logoutApi = () => {
  return request.post<ApiResponse<null>>('/auth/logout')
}