/**
 * 營銷活動相關 API
 * @module MarketingAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

export interface MarketingCampaign {
  id?: number
  name: string
  description?: string
  type: 'DISCOUNT' | 'PROMOTION' | 'FLASH_SALE' | 'FREE_SHIPPING' | 'COUPON' | 'OTHER'
  status: 'DRAFT' | 'ACTIVE' | 'ENDED' | 'SCHEDULED'
  startDate: string
  endDate: string
  discountRate?: number
  discountAmount?: number
  minPurchaseAmount?: number
  targetAudience?: string
  createdAt?: string
  updatedAt?: string
}

export interface MarketingResponse {
  data: MarketingCampaign[]
  total: number
  page: number
  pageSize: number
}

/**
 * 營銷活動 API 服務
 * @namespace marketingApi
 */
const marketingApi = {
  /**
   * 獲取所有營銷活動
   * @description 分頁查詢營銷活動列表
   * @param {number} [page=1] - 頁碼（從 1 開始）
   * @param {number} [pageSize=10] - 每頁數量
   * @returns {Promise<MarketingResponse>} 營銷活動分頁資料
   * @swagger GET /api/marketing/campaigns
   * @example
   * const response = await marketingApi.getAllCampaigns(1, 20)
   */
  getAllCampaigns: async (page: number = 1, pageSize: number = 10) => {
    const response = await axiosInstance.get<any, ApiResponse<MarketingResponse>>('/marketing/campaigns', {
      params: { page, pageSize }
    })
    return response.data
  },

  /**
   * 按 ID 獲取營銷活動
   * @description 查詢指定 ID 的營銷活動詳細資訊
   * @param {number} id - 活動 ID
   * @returns {Promise<{ data: MarketingCampaign }>} 活動資料
   * @swagger GET /api/marketing/campaigns/{id}
   * @example
   * const response = await marketingApi.getCampaignById(123)
   */
  getCampaignById: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<MarketingCampaign>>(`/marketing/campaigns/${id}`)
    return response.data
  },

  /**
   * 按狀態獲取營銷活動
   * @description 根據活動狀態分頁查詢
   * @param {string} status - 活動狀態（DRAFT/ACTIVE/ENDED/SCHEDULED）
   * @param {number} [page=1] - 頁碼
   * @param {number} [pageSize=10] - 每頁數量
   * @returns {Promise<MarketingResponse>} 營銷活動分頁資料
   * @swagger GET /api/marketing/campaigns
   * @example
   * const active = await marketingApi.getCampaignsByStatus('ACTIVE', 1, 20)
   */
  getCampaignsByStatus: async (status: string, page: number = 1, pageSize: number = 10) => {
    const response = await axiosInstance.get<any, ApiResponse<MarketingResponse>>('/marketing/campaigns', {
      params: { status, page, pageSize }
    })
    return response.data
  },

  /**
   * 按類型獲取營銷活動
   * @description 根據活動類型分頁查詢
   * @param {string} type - 活動類型（DISCOUNT/PROMOTION/FLASH_SALE/FREE_SHIPPING/COUPON/OTHER）
   * @param {number} [page=1] - 頁碼
   * @param {number} [pageSize=10] - 每頁數量
   * @returns {Promise<MarketingResponse>} 營銷活動分頁資料
   * @swagger GET /api/marketing/campaigns
   * @example
   * const discounts = await marketingApi.getCampaignsByType('DISCOUNT', 1, 20)
   */
  getCampaignsByType: async (type: string, page: number = 1, pageSize: number = 10) => {
    const response = await axiosInstance.get<any, ApiResponse<MarketingResponse>>('/marketing/campaigns', {
      params: { type, page, pageSize }
    })
    return response.data
  },

  /**
   * 創建營銷活動
   * @description 創建新的營銷活動
   * @param {MarketingCampaign} campaign - 活動資料
   * @param {string} campaign.name - 活動名稱（必填）
   * @param {string} campaign.type - 活動類型（必填）
   * @param {string} campaign.status - 活動狀態（必填）
   * @param {string} campaign.startDate - 開始日期（必填）
   * @param {string} campaign.endDate - 結束日期（必填）
   * @returns {Promise<{ data: MarketingCampaign }>} 創建成功的活動資料
   * @swagger POST /api/marketing/campaigns
   * @example
   * const newCampaign = await marketingApi.createCampaign({
   *   name: '春節促銷',
   *   type: 'DISCOUNT',
   *   status: 'DRAFT',
   *   startDate: '2026-02-01',
   *   endDate: '2026-02-14',
   *   discountRate: 0.2
   * })
   */
  createCampaign: async (campaign: MarketingCampaign) => {
    const response = await axiosInstance.post<any, ApiResponse<MarketingCampaign>>('/marketing/campaigns', campaign)
    return response.data
  },

  /**
   * 更新營銷活動
   * @description 更新指定營銷活動的資訊
   * @param {number} id - 活動 ID
   * @param {MarketingCampaign} campaign - 更新的活動資料
   * @returns {Promise<{ data: MarketingCampaign }>} 更新後的活動資料
   * @swagger PUT /api/marketing/campaigns/{id}
   * @example
   * const updated = await marketingApi.updateCampaign(123, { name: '新名稱' })
   */
  updateCampaign: async (id: number, campaign: MarketingCampaign) => {
    const response = await axiosInstance.put<any, ApiResponse<MarketingCampaign>>(`/marketing/campaigns/${id}`, campaign)
    return response.data
  },

  /**
   * 刪除營銷活動
   * @description 刪除指定的營銷活動
   * @param {number} id - 活動 ID
   * @returns {Promise<any>} 刪除結果
   * @swagger DELETE /api/marketing/campaigns/{id}
   * @example
   * await marketingApi.deleteCampaign(123)
   */
  deleteCampaign: async (id: number) => {
    await axiosInstance.delete<any, ApiResponse<void>>(`/marketing/campaigns/${id}`)
  },

  /**
   * 更新活動狀態
   * @description 更新指定活動的狀態
   * @param {number} id - 活動 ID
   * @param {string} status - 新狀態（DRAFT/ACTIVE/ENDED/SCHEDULED）
   * @returns {Promise<any>} 更新結果
   * @swagger PATCH /api/marketing/campaigns/{id}/status
   * @example
   * await marketingApi.updateCampaignStatus(123, 'ACTIVE')
   */
  updateCampaignStatus: async (id: number, status: string) => {
    await axiosInstance.patch<any, ApiResponse<void>>(`/marketing/campaigns/${id}/status`, { status })
  }
}

export default marketingApi
