import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

export interface CommentItem {
  id: number
  courseId: number
  userId: number
  username: string
  rating: number
  content: string
  likes: number
  createTime: string
}

export const addCommentApi = (courseId: number, content: string) => {
  return request.post<ApiResponse<any>>(`/api/comments/add/${courseId}`, { content })
}

export const getCommentsByCourseIdApi = (courseId: number) => {
  return request.get<ApiResponse<CommentItem[]>>(`/api/comments/course/${courseId}`)
}

export const getCommentCountApi = (courseId: number) => {
  return request.get<ApiResponse<number>>(`/api/comments/count/${courseId}`)
}

export const likeCommentApi = (commentId: number) => {
  return request.post<ApiResponse<any>>(`/api/comments/like/${commentId}`)
}