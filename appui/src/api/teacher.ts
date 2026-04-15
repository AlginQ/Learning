import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 获取教师的课程列表
export const getTeacherCoursesApi = () => {
  return request.get<ApiResponse<any[]>>('/api/teacher/courses')
}

// 创建课程
export const createCourseApi = (courseData: any) => {
  return request.post<ApiResponse<any>>('/api/teacher/courses', courseData)
}

// 更新课程
export const updateCourseApi = (id: number, courseData: any) => {
  return request.put<ApiResponse<any>>(`/api/teacher/courses/${id}`, courseData)
}

// 删除课程
export const deleteCourseApi = (id: number) => {
  return request.delete<ApiResponse<null>>(`/api/teacher/courses/${id}`)
}