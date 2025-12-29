// 通用 API 回應類型定義

// 通用回應接口
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

// 分頁回應接口
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
