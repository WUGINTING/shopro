/**
 * 彈跳廣告相關 API
 * @module PopupAdAPI
 * @description 對應後端 PopupAdController（/api/popup-ads）。
 * 前台商城首頁會讀取 GET /api/popup-ads/active 顯示目前有效的廣告。
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 顯示頻率
 * - ONCE：只顯示一次
 * - EVERY_VISIT：每次造訪
 * - ONCE_PER_DAY：每天一次
 */
export type DisplayFrequency = 'ONCE' | 'EVERY_VISIT' | 'ONCE_PER_DAY'

/**
 * 彈跳廣告介面（對應後端 PopupAdDTO）
 * @interface PopupAd
 */
export interface PopupAd {
  /** 廣告 ID */
  id?: number
  /** 廣告標題（必填） */
  title: string
  /** 圖片網址 */
  imageUrl?: string | null
  /** 點擊連結 */
  linkUrl?: string | null
  /** 開始時間（LocalDateTime，例如 2026-01-01T00:00:00；空值表示立即開始） */
  startTime?: string | null
  /** 結束時間（LocalDateTime；空值表示不限） */
  endTime?: string | null
  /** 是否啟用 */
  enabled?: boolean
  /** 顯示頻率（未指定時後端預設 ONCE_PER_DAY） */
  displayFrequency?: DisplayFrequency | null
}

/** 顯示頻率選項 */
export const DISPLAY_FREQUENCY_OPTIONS: { label: string; value: DisplayFrequency }[] = [
  { label: '只顯示一次', value: 'ONCE' },
  { label: '每天一次', value: 'ONCE_PER_DAY' },
  { label: '每次造訪', value: 'EVERY_VISIT' }
]

/**
 * 彈跳廣告 API 服務
 * @namespace popupAdApi
 */
export const popupAdApi = {
  /**
   * 取得所有廣告（後台）
   * @returns {Promise<ApiResponse<PopupAd[]>>} 廣告清單
   * @swagger GET /api/popup-ads
   */
  getAllAds: () => {
    return axios.get<any, ApiResponse<PopupAd[]>>('/popup-ads')
  },

  /**
   * 取得目前有效的廣告（前台使用）
   * @returns {Promise<ApiResponse<PopupAd[]>>} 有效廣告清單
   * @swagger GET /api/popup-ads/active
   */
  getActiveAds: () => {
    return axios.get<any, ApiResponse<PopupAd[]>>('/popup-ads/active')
  },

  /**
   * 取得單一廣告
   * @param {number} id - 廣告 ID
   * @returns {Promise<ApiResponse<PopupAd>>} 廣告資料
   * @swagger GET /api/popup-ads/{id}
   */
  getAd: (id: number) => {
    return axios.get<any, ApiResponse<PopupAd>>(`/popup-ads/${id}`)
  },

  /**
   * 新增廣告
   * @param {PopupAd} data - 廣告資料
   * @returns {Promise<ApiResponse<PopupAd>>} 新增後的廣告資料
   * @swagger POST /api/popup-ads
   */
  createAd: (data: PopupAd) => {
    return axios.post<any, ApiResponse<PopupAd>>('/popup-ads', data)
  },

  /**
   * 更新廣告
   * @description 後端會以請求內容整筆覆蓋（含 enabled），請傳入完整資料
   * @param {number} id - 廣告 ID
   * @param {PopupAd} data - 廣告資料
   * @returns {Promise<ApiResponse<PopupAd>>} 更新後的廣告資料
   * @swagger PUT /api/popup-ads/{id}
   */
  updateAd: (id: number, data: PopupAd) => {
    return axios.put<any, ApiResponse<PopupAd>>(`/popup-ads/${id}`, data)
  },

  /**
   * 刪除廣告
   * @param {number} id - 廣告 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/popup-ads/{id}
   */
  deleteAd: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/popup-ads/${id}`)
  }
}

export default popupAdApi
