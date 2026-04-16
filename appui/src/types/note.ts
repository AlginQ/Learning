// 笔记相关类型定义
export interface Note {
  id: number
  userId: number
  courseId: number
  lessonId: number
  title: string
  content: string
  type: number
  createTime: string
  updateTime: string
}