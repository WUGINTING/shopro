/**
 * EDM 電子報相關 API
 * @module EDMAPI
 */

import axiosInstance from './axios'

export interface EdmCampaign {
  id?: number
  name: string
  subject: string
  content: string
  targetGroupId?: number
  sendTime?: string
  status: 'DRAFT' | 'SCHEDULED' | 'SENT' | 'FAILED'
  sentCount?: number
  openCount?: number
  clickCount?: number
  createdAt?: string
  updatedAt?: string
}

export interface EdmSendLog {
  id?: number
  campaignId: number
  memberId: number
  memberEmail?: string
  status: 'PENDING' | 'SENT' | 'OPENED' | 'CLICKED' | 'FAILED'
  sentAt?: string
  openedAt?: string
  failedReason?: string
  createdAt?: string
}

export interface EdmStatistics {
  totalCampaigns: number
  totalSent: number
  totalOpened: number
  totalClicked: number
  averageOpenRate: number
  averageClickRate: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

/**
 * EDM 電子報 API 服務
 * @namespace edmApi
 */
export const edmApi = {
  /**
   * 獲取 EDM 活動列表
   * @description 分頁查詢 EDM 行銷活動列表
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<EdmCampaign>>} EDM 活動分頁資料
   * @swagger GET /api/crm/edm
   * @example
   * const campaigns = await edmApi.getCampaigns(0, 20)
   */
  getCampaigns: async (page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmCampaign>>(`${API_BASE}`, {
      params: { page, size }
    })
  },

  /**
   * 獲取單個 EDM 活動詳情
   * @description 查詢指定 ID 的 EDM 活動詳細資訊
   * @param {number} id - EDM 活動 ID
   * @returns {Promise<EdmCampaign>} EDM 活動資料
   * @swagger GET /api/crm/edm/{id}
   * @example
   * const campaign = await edmApi.getCampaign(123)
   */
  getCampaign: async (id: number) => {
    return axiosInstance.get<any, EdmCampaign>(`${API_BASE}/${id}`)
  },

  /**
   * 創建 EDM 活動
   * @description 創建新的 EDM 行銷活動
   * @param {EdmCampaign} campaign - EDM 活動資料
   * @param {string} campaign.name - 活動名稱（必填）
   * @param {string} campaign.subject - 郵件主旨（必填）
   * @param {string} campaign.content - 郵件內容（必填）
   * @param {'DRAFT' | 'SCHEDULED' | 'SENT' | 'FAILED'} campaign.status - 活動狀態（必填）
   * @returns {Promise<EdmCampaign>} 創建成功的 EDM 活動
   * @swagger POST /api/crm/edm
   * @example
   * const newCampaign = await edmApi.createCampaign({
   *   name: '春節促銷',
   *   subject: '春節特惠來了！',
   *   content: '<html>...</html>',
   *   status: 'DRAFT'
   * })
   */
  createCampaign: async (campaign: EdmCampaign) => {
    return axiosInstance.post<any, EdmCampaign>(`${API_BASE}`, campaign)
  },

  /**
   * 更新 EDM 活動
   * @description 更新指定 EDM 活動的資訊
   * @param {number} id - EDM 活動 ID
   * @param {Partial<EdmCampaign>} campaign - 更新的活動資料
   * @returns {Promise<EdmCampaign>} 更新後的 EDM 活動
   * @swagger PUT /api/crm/edm/{id}
   * @example
   * const updated = await edmApi.updateCampaign(123, { subject: '新主旨' })
   */
  updateCampaign: async (id: number, campaign: Partial<EdmCampaign>) => {
    return axiosInstance.put<any, EdmCampaign>(`${API_BASE}/${id}`, campaign)
  },

  /**
   * 刪除 EDM 活動
   * @description 刪除指定的 EDM 活動
   * @param {number} id - EDM 活動 ID
   * @returns {Promise<boolean>} 是否成功
   * @swagger DELETE /api/crm/edm/{id}
   * @example
   * const success = await edmApi.deleteCampaign(123)
   */
  deleteCampaign: async (id: number) => {
    return axiosInstance.delete<any, boolean>(`${API_BASE}/${id}`)
  },

  /**
   * 發送 EDM 活動
   * @description 立即發送 EDM 活動給目標群組
   * @param {number} id - EDM 活動 ID
   * @returns {Promise<boolean>} 是否成功
   * @swagger POST /api/crm/edm/{id}/send
   * @example
   * const success = await edmApi.sendCampaign(123)
   */
  sendCampaign: async (id: number) => {
    return axiosInstance.post<any, boolean>(`${API_BASE}/${id}/send`)
  },

  /**
   * 排程發送 EDM 活動
   * @description 設定 EDM 活動的發送時間
   * @param {number} id - EDM 活動 ID
   * @param {string} sendTime - 發送時間（ISO 8601 格式）
   * @returns {Promise<boolean>} 是否成功
   * @swagger POST /api/crm/edm/{id}/schedule
   * @example
   * const success = await edmApi.scheduleCampaign(123, '2026-02-01T10:00:00Z')
   */
  scheduleCampaign: async (id: number, sendTime: string) => {
    return axiosInstance.post<any, boolean>(
      `${API_BASE}/${id}/schedule`,
      { sendTime }
    )
  },

  /**
   * 獲取 EDM 發送日誌
   * @description 分頁查詢 EDM 活動的發送記錄
   * @param {number} campaignId - EDM 活動 ID
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<EdmSendLog>>} 發送日誌分頁資料
   * @swagger GET /api/crm/edm/{campaignId}/logs
   * @example
   * const logs = await edmApi.getSendLogs(123, 0, 20)
   */
  getSendLogs: async (campaignId: number, page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmSendLog>>(
      `${API_BASE}/${campaignId}/logs`,
      { params: { page, size } }
    )
  },

  /**
   * 獲取 EDM 統計資訊
   * @description 查詢 EDM 系統的總體統計數據
   * @returns {Promise<EdmStatistics>} EDM 統計資料
   * @swagger GET /api/crm/edm/statistics
   * @example
   * const stats = await edmApi.getStatistics()
   * console.log(stats.averageOpenRate) // 平均開信率
   */
  getStatistics: async () => {
    return axiosInstance.get<any, EdmStatistics>(`${API_BASE}/statistics`)
  },

  /**
   * 按狀態查詢 EDM 活動
   * @description 根據狀態分頁查詢 EDM 活動
   * @param {string} status - 活動狀態（DRAFT/SCHEDULED/SENT/FAILED）
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<EdmCampaign>>} EDM 活動分頁資料
   * @swagger GET /api/crm/edm/status/{status}
   * @example
   * const campaigns = await edmApi.getCampaignsByStatus('SENT', 0, 20)
   */
  getCampaignsByStatus: async (status: string, page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmCampaign>>(
      `${API_BASE}/status/${status}`,
      { params: { page, size } }
    )
  }
}
