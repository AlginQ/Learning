import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

export interface StudyProgress {
  id: number
  userId: number
  lessonId: number
  progress: number
  watchDuration: number
  isCompleted: boolean
  lastStudyTime: string
  createTime: string
  updateTime: string
}

export const getProgressApi = (lessonId: number) => {
  return request.get<ApiResponse<StudyProgress>>(`/api/progress/${lessonId}`)
}

export const saveProgressApi = (lessonId: number, progress: number, watchDuration: number, isCompleted: boolean) => {
  return request.post<ApiResponse<any>>(`/api/progress/${lessonId}`, {
    progress,
    watchDuration,
    isCompleted
  })
}