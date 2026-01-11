/**
 * 積點管理 API
 * @module PointAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

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
   * @param {string} pointType - 積點類型
   * @param {string} reason - 增加原因
   * @param {number} [orderId] - 關聯訂單 ID（可選）
   * @returns {Promise<PointRecord>} 積點記錄
   * @swagger POST /api/crm/points/add
   * @example
   * const record = await pointApi.addPoints(123, 100, 'EARN', '購物獲得')
   */
  addPoints: async (memberId: number, points: number, pointType: string, reason: string, orderId?: number) => {
    const response = await axiosInstance.post<any, ApiResponse<PointRecord>>('/crm/points/add', null, {
      params: { memberId, points, pointType, reason, orderId }
    })
    return response.data
  },

  /**
   * 扣除積點
   * @description 從指定會員扣除積點
   * @param {number} memberId - 會員 ID
   * @param {number} points - 積點數量
   * @param {string} pointType - 積點類型
   * @param {string} reason - 扣除原因
   * @returns {Promise<PointRecord>} 積點記錄
   * @swagger POST /api/crm/points/deduct
   * @example
   * const record = await pointApi.deductPoints(123, 50, 'REDEEM', '兌換商品')
   */
  deductPoints: async (memberId: number, points: number, pointType: string, reason: string) => {
    const response = await axiosInstance.post<any, ApiResponse<PointRecord>>('/crm/points/deduct', null, {
      params: { memberId, points, pointType, reason }
    })
    return response.data
  },

  /**
   * 批次發放積點
   * @description 批量為多個會員發放積點
   * @param {BatchGrantRequest} request - 批次發放請求
   * @param {number[]} request.memberIds - 會員 ID 陣列
   * @param {number} request.points - 積點數量
   * @param {string} request.reason - 發放原因
   * @returns {Promise<PointRecord[]>} 積點記錄列表
   * @swagger POST /api/crm/points/batch-grant
   * @example
   * const records = await pointApi.batchGrant({
   *   memberIds: [123, 456, 789],
   *   points: 100,
   *   reason: '節日促銷贈送'
   * })
   */
  batchGrant: async (request: BatchGrantRequest) => {
    const response = await axiosInstance.post<any, ApiResponse<PointRecord[]>>('/crm/points/batch-grant', request)
    return response.data
  },

  /**
   * 獲取所有積點紀錄
   * @description 分頁查詢所有會員的積點記錄（不限定會員）
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<PointRecord>>} 積點記錄分頁資料
   * @swagger GET /api/crm/points
   * @example
   * const records = await pointApi.getAllPoints(0, 20)
   */
  getAllPoints: async (page = 0, size = 20) => {
    const response = await axiosInstance.get<any, ApiResponse<any>>(
      '/crm/points',
      { params: { page, size } }
    )
    // 處理 Spring Data Page 格式
    const pageData = response.data
    if (pageData && typeof pageData === 'object') {
      // Spring Data Page 格式轉換為自定義 PageResponse 格式
      return {
        content: pageData.content || [],
        totalElements: pageData.totalElements || 0,
        totalPages: pageData.totalPages || 0,
        currentPage: pageData.pageable?.pageNumber ?? pageData.number ?? page,
        pageSize: pageData.pageable?.pageSize ?? pageData.size ?? size
      } as PageResponse<PointRecord>
    }
    return pageData as PageResponse<PointRecord>
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
    const response = await axiosInstance.get<any, ApiResponse<any>>(
      `/crm/points/member/${memberId}`,
      { params: { page, size } }
    )
    // 處理 Spring Data Page 格式
    const pageData = response.data
    if (pageData && typeof pageData === 'object') {
      // Spring Data Page 格式轉換為自定義 PageResponse 格式
      return {
        content: pageData.content || [],
        totalElements: pageData.totalElements || 0,
        totalPages: pageData.totalPages || 0,
        currentPage: pageData.pageable?.pageNumber ?? pageData.number ?? page,
        pageSize: pageData.pageable?.pageSize ?? pageData.size ?? size
      } as PageResponse<PointRecord>
    }
    return pageData as PageResponse<PointRecord>
  },

  /**
   * 獲取會員積點餘額
   * @description 查詢指定會員的積點餘額資訊
   * @param {number} memberId - 會員 ID
   * @returns {Promise<number>} 積點餘額
   * @swagger GET /api/crm/points/balance/{memberId}
   * @example
   * const balance = await pointApi.getPointBalance(123)
   */
  getPointBalance: async (memberId: number) => {
    const response = await axiosInstance.get<any, ApiResponse<number>>(`/crm/points/balance/${memberId}`)
    return response.data
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
    const response = await axiosInstance.get<any, ApiResponse<PointRecord>>(`/crm/points/${id}`)
    return response.data
  }
}

export default pointApi
