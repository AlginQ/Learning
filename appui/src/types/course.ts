// 课程相关类型定义
export interface Category {
  id: number
  name: string
  icon?: string
  courseCount?: number
}

export interface Teacher {
  id: number
  name: string
  avatar?: string
  title?: string
  introduction?: string
}

export interface Course {
  id: number
  title: string
  description?: string
  coverImage?: string
  price: number
  discountPrice?: number
  isFree: boolean
  category: Category
  teacher: Teacher
  lessonCount: number
  studentCount: number
  rating: number
  createTime: string
}

export interface CourseDetail extends Course {
  introduction?: string
  requirements?: string[]
  targetAudience?: string[]
  lessons?: Chapter[]
}

export interface Chapter {
  id: number
  title: string
  chapterNumber: number
  lessons: Lesson[]
}

export interface Lesson {
  id: number
  title: string
  videoUrl?: string
  duration: number
  lessonNumber: number
  isFree: boolean
  isCompleted?: boolean
  progress?: number
}

// 分页相关
export interface PageRequest {
  page: number
  size: number
  keyword?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  pages: number
}