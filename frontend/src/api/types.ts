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

/**
 * 相容舊格式的分頁介面
 * @description 部分舊版回應以 total 表示總筆數，前端會以 totalElements || total 作為後備
 * @template T - 資料類型
 */
export type LegacyPageResponse<T> = PageResponse<T> & { total?: number }

/**
 * 前端轉換後的簡化分頁介面
 * @description 部分 API 會將 Spring Data Page 轉換成此扁平格式（currentPage / pageSize）
 * @interface SimplePageResponse
 * @template T - 資料類型
 */
export interface SimplePageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}
