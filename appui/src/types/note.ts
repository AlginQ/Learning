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

// 笔记表单类型（用于新增和编辑）
export interface NoteForm {
  id?: number
  title: string
  content: string
  courseId: number
  lessonId: number
  type: number
}