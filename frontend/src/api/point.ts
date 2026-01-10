/**
 * 積點管理 API
 * @module PointAPI
 */

import axios from 'axios'

export interface PointRecord {
  id?: number
  memberId: number
  points: number
  pointType: 'EARN' | 'PURCHASE' | 'REWARD' | 'REDEEM' | 'EXPIRE' | 'ADJUST'
  reason: string
  orderId?: number
  balanceBefore?: number
  balanceAfter?: number
  createdAt?: string
}

export interface PointBalance {
  memberId: number
  totalPoints: number
  usablePoints: number
  expiredPoints: number
}

export interface BatchGrantRequest {
  memberIds: number[]
  points: number
  reason: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

/**
 * 積點 API 服務
 * @namespace pointApi
 */
export const pointApi = {
  /**
   * 增加積點
   * @description 為指定會員增加積點
   * @param {number} memberId - 會員 ID
   * @param {number} points - 積點數量
   * @param {string} reason - 增加原因
   * @returns {Promise<PointRecord>} 積點記錄
   * @swagger POST /api/crm/points/add
   * @example
   * const record = await pointApi.addPoints(123, 100, '購物獲得')
   */
  addPoints: async (memberId: number, points: number, reason: string) => {
    const { data } = await axios.post<any>(`${API_BASE}/add`, {
      memberId,
      points,
      pointType: 'EARN',
      reason
    })
    return data.data as PointRecord
  },

  /**
   * 扣除積點
   * @description 從指定會員扣除積點
   * @param {number} memberId - 會員 ID
   * @param {number} points - 積點數量
   * @param {string} reason - 扣除原因
   * @returns {Promise<PointRecord>} 積點記錄
   * @swagger POST /api/crm/points/deduct
   * @example
   * const record = await pointApi.deductPoints(123, 50, '兌換商品')
   */
  deductPoints: async (memberId: number, points: number, reason: string) => {
    const { data } = await axios.post<any>(`${API_BASE}/deduct`, {
      memberId,
      points,
      pointType: 'REDEEM',
      reason
    })
    return data.data as PointRecord
  },

  /**
   * 批次發放積點
   * @description 批量為多個會員發放積點
   * @param {BatchGrantRequest} request - 批次發放請求
   * @param {number[]} request.memberIds - 會員 ID 陣列
   * @param {number} request.points - 積點數量
   * @param {string} request.reason - 發放原因
   * @returns {Promise<boolean>} 是否成功
   * @swagger POST /api/crm/points/batch-grant
   * @example
   * const success = await pointApi.batchGrant({
   *   memberIds: [123, 456, 789],
   *   points: 100,
   *   reason: '節日促銷贈送'
   * })
   */
  batchGrant: async (request: BatchGrantRequest) => {
    const { data } = await axios.post<any>(
      `${API_BASE}/batch-grant`,
      request
    )
    return data.success
  },

  /**
   * 獲取會員積點紀錄
   * @description 分頁查詢指定會員的積點記錄
   * @param {number} memberId - 會員 ID
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<PointRecord>>} 積點記錄分頁資料
   * @swagger GET /api/crm/points/member/{memberId}
   * @example
   * const records = await pointApi.getMemberPoints(123, 0, 20)
   */
  getMemberPoints: async (memberId: number, page = 0, size = 20) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/member/${memberId}`,
      { params: { page, size } }
    )
    return data.data as PageResponse<PointRecord>
  },

  /**
   * 獲取會員積點餘額
   * @description 查詢指定會員的積點餘額資訊
   * @param {number} memberId - 會員 ID
   * @returns {Promise<PointBalance>} 積點餘額資訊
   * @swagger GET /api/crm/points/balance/{memberId}
   * @example
   * const balance = await pointApi.getPointBalance(123)
   * console.log(balance.usablePoints) // 可用積點
   */
  getPointBalance: async (memberId: number) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/balance/${memberId}`
    )
    return data.data as PointBalance
  },

  /**
   * 獲取積點紀錄詳情
   * @description 查詢指定 ID 的積點記錄詳細資訊
   * @param {number} id - 積點記錄 ID
   * @returns {Promise<PointRecord>} 積點記錄
   * @swagger GET /api/crm/points/{id}
   * @example
   * const record = await pointApi.getPointRecord(456)
   */
  getPointRecord: async (id: number) => {
    const { data } = await axios.get<any>(`${API_BASE}/${id}`)
    return data.data as PointRecord
  },

  /**
   * 查詢積點紀錄
   * @description 根據條件搜尋積點記錄
   * @param {number} [memberId] - 會員 ID
   * @param {string} [pointType] - 積點類型
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<PointRecord>>} 積點記錄分頁資料
   * @swagger GET /api/crm/points/search
   * @example
   * const records = await pointApi.searchPoints(123, 'EARN', 0, 20)
   */
  searchPoints: async (memberId?: number, pointType?: string, page = 0, size = 20) => {
    const { data } = await axios.get<any>(`${API_BASE}/search`, {
      params: { memberId, pointType, page, size }
    })
    return data.data as PageResponse<PointRecord>
  }
}
