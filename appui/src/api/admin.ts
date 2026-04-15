import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 获取所有用户
export const getAllUsersApi = () => {
  return request.get<ApiResponse<any[]>>('/api/admin/users')
}

// 删除用户
export const deleteUserApi = (id: number) => {
  return request.delete<ApiResponse<null>>(`/api/admin/users/${id}`)
}

// 获取用户详情
export const getUserByIdApi = (id: number) => {
  return request.get<ApiResponse<any>>(`/api/admin/users/${id}`)
}

// 禁用用户
export const disableUserApi = (id: number) => {
  return request.put<ApiResponse<null>>(`/api/admin/users/${id}/disable`)
}

// 启用用户
export const enableUserApi = (id: number) => {
  return request.put<ApiResponse<null>>(`/api/admin/users/${id}/enable`)
}

// 获取待审核的教师申请
export const getPendingTeacherAppliesApi = () => {
  return request.get<ApiResponse<any[]>>('/api/teacher-apply/pending')
}

// 审核教师申请
export const reviewTeacherApplyApi = (id: number, status: number, remark?: string) => {
  return request.post<ApiResponse<null>>(`/api/teacher-apply/review/${id}`, { status, remark })
}

// 获取所有教师
export const getAllTeachersApi = () => {
  return request.get<ApiResponse<any[]>>('/api/teachers')
}

// 更新用户角色
export const updateUserRoleApi = (id: number, role: string) => {
  return request.put<ApiResponse<null>>(`/api/admin/users/${id}/role?role=${role}`)
}