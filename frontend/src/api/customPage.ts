/**
 * 自訂頁面相關 API
 * @module CustomPageAPI
 * @description 對應後端 CustomPageController（/api/crm/custom-pages）。
 * 前台商城以 /shop/page/<slug> 顯示已啟用的自訂頁面。
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

/**
 * 自訂頁面介面（對應後端 CustomPageDTO）
 * @interface CustomPage
 */
export interface CustomPage {
  /** 頁面 ID */
  id?: number
  /** 頁面標題（必填，最多 200 字） */
  title: string
  /** 頁面別名（必填，最多 100 字，只能包含小寫字母、數字和連字號） */
  slug: string
  /** 頁面內容（HTML） */
  content?: string
  /** SEO 標題（最多 100 字） */
  metaTitle?: string
  /** SEO 描述（最多 300 字） */
  metaDescription?: string
  /** SEO 關鍵字（最多 200 字） */
  metaKeywords?: string
  /** 是否啟用 */
  enabled?: boolean
  /** 排序 */
  sortOrder?: number
  /** 建立時間（目前後端 DTO 未回傳，保留相容） */
  createdAt?: string
  /** 更新時間（目前後端 DTO 未回傳，保留相容） */
  updatedAt?: string
}

/** 頁面別名格式：小寫字母、數字和連字號（與後端 @Pattern 相同） */
export const CUSTOM_PAGE_SLUG_PATTERN = /^[a-z0-9-]+$/

/**
 * 自訂頁面 API 服務
 * @namespace customPageApi
 */
export const customPageApi = {
  /**
   * 分頁查詢自訂頁面
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<CustomPage>>>} 分頁頁面資料
   * @swagger GET /api/crm/custom-pages
   */
  listCustomPages: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<CustomPage>>>('/crm/custom-pages', {
      params: { page, size }
    })
  },

  /**
   * 取得所有自訂頁面（依排序）
   * @returns {Promise<ApiResponse<CustomPage[]>>} 頁面清單
   * @swagger GET /api/crm/custom-pages/all
   */
  listAllCustomPages: () => {
    return axios.get<any, ApiResponse<CustomPage[]>>('/crm/custom-pages/all')
  },

  /**
   * 取得自訂頁面詳情
   * @param {number} id - 頁面 ID
   * @returns {Promise<ApiResponse<CustomPage>>} 頁面資料
   * @swagger GET /api/crm/custom-pages/{id}
   */
  getCustomPage: (id: number) => {
    return axios.get<any, ApiResponse<CustomPage>>(`/crm/custom-pages/${id}`)
  },

  /**
   * 依別名取得自訂頁面（僅回傳已啟用的頁面）
   * @param {string} slug - 頁面別名
   * @returns {Promise<ApiResponse<CustomPage>>} 頁面資料
   * @swagger GET /api/crm/custom-pages/slug/{slug}
   */
  getCustomPageBySlug: (slug: string) => {
    return axios.get<any, ApiResponse<CustomPage>>(
      `/crm/custom-pages/slug/${encodeURIComponent(slug)}`
    )
  },

  /**
   * 創建自訂頁面
   * @param {CustomPage} data - 頁面資料
   * @returns {Promise<ApiResponse<CustomPage>>} 創建成功的頁面資料
   * @swagger POST /api/crm/custom-pages
   */
  createCustomPage: (data: CustomPage) => {
    return axios.post<any, ApiResponse<CustomPage>>('/crm/custom-pages', data)
  },

  /**
   * 更新自訂頁面
   * @description 後端會以請求內容整筆覆蓋（含 enabled），請傳入完整資料
   * @param {number} id - 頁面 ID
   * @param {CustomPage} data - 頁面資料
   * @returns {Promise<ApiResponse<CustomPage>>} 更新後的頁面資料
   * @swagger PUT /api/crm/custom-pages/{id}
   */
  updateCustomPage: (id: number, data: CustomPage) => {
    return axios.put<any, ApiResponse<CustomPage>>(`/crm/custom-pages/${id}`, data)
  },

  /**
   * 刪除自訂頁面
   * @param {number} id - 頁面 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/crm/custom-pages/{id}
   */
  deleteCustomPage: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/custom-pages/${id}`)
  },

  /**
   * 切換自訂頁面啟用狀態
   * @param {number} id - 頁面 ID
   * @returns {Promise<ApiResponse<CustomPage>>} 更新後的頁面資料
   * @swagger PUT /api/crm/custom-pages/{id}/toggle-enabled
   */
  toggleEnabled: (id: number) => {
    return axios.put<any, ApiResponse<CustomPage>>(`/crm/custom-pages/${id}/toggle-enabled`)
  }
}

export default customPageApi
