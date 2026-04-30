import request from '@/utils/request'
import type { ApiResponse, ApiPageResponse } from '@/types/api'
import type { Course, CourseDetail, Category, PageRequest } from '@/types/course'

// 获取课程分类
export const getCategoryApi = () => {
  return request.get<ApiResponse<Category[]>>('/api/categories')
}

// 获取课程列表
export const getCourseListApi = (params: PageRequest & { categoryId?: number }) => {
  return request.get<ApiPageResponse<Course>>('/api/courses', params)
}

// 搜索课程
export const searchCourseApi = (params: PageRequest) => {
  return request.get<ApiPageResponse<Course>>('/api/courses/search', params)
}

// 获取课程详情
export const getCourseDetailApi = (id: number) => {
  return request.get<ApiResponse<CourseDetail>>(`/api/courses/${id}`)
}

// 获取课程章节列表
export const getCourseLessonsApi = (courseId: number) => {
  return request.get<ApiResponse<any>>(`/api/courses/${courseId}/lessons`)
}

// 获取课程章节和课时
export const getCourseChaptersApi = (courseId: number) => {
  return request.get<ApiResponse<any>>(`/api/courses/${courseId}/chapters`)
}

// 获取教师列表
export const getTeacherListApi = () => {
  return request.get<ApiResponse<any[]>>('/api/teachers')
}

// 添加课程
export const addCourseApi = (course: any) => {
  return request.post<ApiResponse<any>>('/api/courses', course)
}

// 编辑课程
export const updateCourseApi = (id: number, course: any) => {
  return request.put<ApiResponse<any>>(`/api/courses/${id}`, course)
}

// 删除课程
export const deleteCourseApi = (id: number) => {
  return request.delete<ApiResponse<null>>(`/api/courses/${id}`)
}

// 切换课程状态
export const toggleCourseStatusApi = (id: number) => {
  return request.put<ApiResponse<any>>(`/api/courses/${id}/status`)
}

// 切换课程推荐状态
export const toggleCourseRecommendApi = (id: number) => {
  return request.put<ApiResponse<any>>(`/api/courses/${id}/recommend`)
}

// 获取待审核课程列表
export const getPendingCoursesApi = () => {
  return request.get<ApiResponse<any[]>>('/api/courses/pending')
}

// 审核课程
export const auditCourseApi = (id: number, status: number) => {
  return request.put<ApiResponse<null>>(`/api/courses/${id}/audit`, { status })
}

// 获取教师课程列表
export const getTeacherCoursesApi = (teacherId: number) => {
  return request.get<ApiResponse<Course[]>>(`/api/courses/teacher/${teacherId}`)
}

// 获取所有审核通过的课程（管理员专用，包含下架课程）
export const getAllApprovedCoursesApi = (params: PageRequest & { keyword?: string; status?: number }) => {
  return request.get<ApiPageResponse<any>>('/api/courses/all', params)
}