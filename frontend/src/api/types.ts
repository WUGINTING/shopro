// 通用 API 响应类型定义

// 通用响应接口
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

// 分页响应接口
export interface PageResponse<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
  }
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
  empty: boolean
}
