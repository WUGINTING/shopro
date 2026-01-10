/**
 * 通用 API 類型定義
 * @module APITypes
 */

/**
 * 通用 API 回應介面
 * @interface ApiResponse
 * @template T - 資料類型
 */
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

/**
 * 分頁回應介面
 * @interface PageResponse
 * @template T - 資料類型
 */
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
