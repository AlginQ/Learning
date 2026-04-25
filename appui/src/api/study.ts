import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 添加学习记录
export const addStudyRecord = (data: {
  courseId: number
  lessonId: number
  duration: number
  progress: number
}) => {
  console.log('发送学习记录请求:', data)
  return request.post<ApiResponse<null>>('/api/study/record', data)
}

// 获取用户学习记录
export const getUserStudyRecords = (params?: {
  startDate?: string
  endDate?: string
}) => {
  return request.get<ApiResponse<any[]>>('/api/study/records', { params })
}

// 获取用户学习统计
export const getUserStudyStatistics = () => {
  return request.get<ApiResponse<any>>('/api/study/statistics')
}

// 获取最近学习记录
export const getRecentStudyRecords = (limit: number = 10) => {
  return request.get<ApiResponse<any[]>>('/api/study/recent', { params: { limit } })
}