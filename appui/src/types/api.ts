// API 响应通用类型
export interface ApiResponse<T> {
  code: number
  data: T
  msg: string
}

// 分页响应类型
export interface ApiPageResponse<T> {
  code: number
  data: {
    records: T[]
    total: number
    current: number
    pages: number
  }
  msg: string
}