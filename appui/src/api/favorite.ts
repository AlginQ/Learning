import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

export interface FavoriteItem {
  id: number
  courseId: number
  courseTitle: string
  courseCover: string
  price: number
  discountPrice: number
  rating: number
  studentCount: number
  categoryName: string
  teacherName: string
  createTime: string
}

export const addFavoriteApi = (courseId: number) => {
  return request.post<ApiResponse<{ success: boolean; isFavorite: boolean }>>(`/api/favorite/add/${courseId}`)
}

export const removeFavoriteApi = (courseId: number) => {
  return request.post<ApiResponse<{ success: boolean; isFavorite: boolean }>>(`/api/favorite/remove/${courseId}`)
}

export const getFavoriteStatusApi = (courseId: number) => {
  return request.get<ApiResponse<{ isFavorite: boolean }>>(`/api/favorite/status/${courseId}`)
}

export const getUserFavoritesApi = () => {
  return request.get<ApiResponse<FavoriteItem[]>>('/api/favorite/list')
}

export const getFavoriteCountApi = () => {
  return request.get<ApiResponse<{ count: number }>>('/api/favorite/count')
}