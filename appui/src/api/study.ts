import request from '@/utils/request'
import type { ApiResponse, ApiPageResponse } from '@/types/api'
import type { PageRequest } from '@/types/course'

// 记录学习进度
export const recordProgressApi = (data: {
  lessonId: number
  progress: number
  watchDuration: number
}) => {
  return request.post<ApiResponse<any>>('/study/progress', data)
}

// 获取学习记录
export const getStudyRecordsApi = (params: PageRequest) => {
  return request.get<ApiPageResponse<any>>('/study/records', params)
}