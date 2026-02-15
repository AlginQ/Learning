import request from '@/utils/request'
import type { ApiResponse, ApiPageResponse } from '@/types/api'
import type { Course, CourseDetail, Category, PageRequest } from '@/types/course'

// 获取课程分类
export const getCategoryApi = () => {
  return request.get<ApiResponse<Category[]>>('/categories')
}

// 获取课程列表
export const getCourseListApi = (params: PageRequest & { categoryId?: number }) => {
  return request.get<ApiPageResponse<Course>>('/courses', params)
}

// 搜索课程
export const searchCourseApi = (params: PageRequest) => {
  return request.get<ApiPageResponse<Course>>('/courses/search', params)
}

// 获取课程详情
export const getCourseDetailApi = (id: number) => {
  return request.get<ApiResponse<CourseDetail>>(`/courses/${id}`)
}

// 获取课程章节列表
export const getCourseLessonsApi = (courseId: number) => {
  return request.get<ApiResponse<any>>(`/courses/${courseId}/lessons`)
}