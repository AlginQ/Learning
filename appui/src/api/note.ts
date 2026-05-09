import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { Note, NoteForm } from '@/types/note'

// 获取笔记列表
export const getNotesApi = (params: { courseId?: number; keyword?: string }) => {
  return request.get<ApiResponse<Note[]>>('/api/notes', params)
}

// 获取笔记详情
export const getNoteApi = (id: number) => {
  return request.get<ApiResponse<Note>>(`/api/notes/${id}`)
}

// 创建笔记
export const createNoteApi = (note: NoteForm) => {
  return request.post<ApiResponse<Note>>('/api/notes', note)
}

// 更新笔记
export const updateNoteApi = (id: number, note: NoteForm) => {
  return request.put<ApiResponse<Note>>(`/api/notes/${id}`, note)
}

// 删除笔记
export const deleteNoteApi = (id: number) => {
  return request.delete<ApiResponse<null>>(`/api/notes/${id}`)
}