import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 添加学习记录
export const addStudyRecord = (data: {
  courseId: number
  lessonId: number
  duration: number
  progress: number
}) => {
  return request.post<ApiResponse<null>>('/study/record', data)
}

// 获取用户学习记录
export const getUserStudyRecords = (params?: {
  startDate?: string
  endDate?: string
}) => {
  return request.get<ApiResponse<any[]>>('/study/records', { params })
}

// 获取用户学习统计
export const getUserStudyStatistics = () => {
  return request.get<ApiResponse<any>>('/study/statistics')
}

// 获取最近学习记录
export const getRecentStudyRecords = (limit: number = 10) => {
  return request.get<ApiResponse<any[]>>('/study/recent', { params: { limit } })
}