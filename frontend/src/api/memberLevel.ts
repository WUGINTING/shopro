/**
 * 會員等級管理 API
 * @module MemberLevelAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 會員等級相關接口
export interface MemberLevel {
  id?: number
  name: string
  levelOrder: number
  minSpendAmount?: number
  discountRate?: number
  pointsMultiplier?: number
  description?: string
  iconUrl?: string
  enabled?: boolean
}

/**
 * 會員等級 API 服務
 * @namespace memberLevelApi
 */
export const memberLevelApi = {
  /**
   * 創建會員等級
   * @description 創建新的會員等級設定
   * @param {MemberLevel} data - 會員等級資料
   * @param {string} data.name - 等級名稱（必填）
   * @param {number} data.levelOrder - 等級順序（必填）
   * @returns {Promise<ApiResponse<MemberLevel>>} 創建成功的等級資料
   * @swagger POST /api/crm/member-levels
   * @example
   * const level = await memberLevelApi.createMemberLevel({
   *   name: '黃金會員',
   *   levelOrder: 3,
   *   minSpendAmount: 10000,
   *   discountRate: 0.9
   * })
   */
  createMemberLevel: (data: MemberLevel) => {
    return axios.post<any, ApiResponse<MemberLevel>>('/crm/member-levels', data)
  },
  
  /**
   * 更新會員等級
   * @description 更新指定會員等級的設定
   * @param {number} id - 等級 ID
   * @param {MemberLevel} data - 更新的等級資料
   * @returns {Promise<ApiResponse<MemberLevel>>} 更新後的等級資料
   * @swagger PUT /api/crm/member-levels/{id}
   * @example
   * const updated = await memberLevelApi.updateMemberLevel(1, {
   *   name: '白金會員',
   *   minSpendAmount: 50000
   * })
   */
  updateMemberLevel: (id: number, data: MemberLevel) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`, data)
  },
  
  /**
   * 取得會員等級詳情
   * @description 查詢指定 ID 的會員等級詳細資訊
   * @param {number} id - 等級 ID
   * @returns {Promise<ApiResponse<MemberLevel>>} 等級資料
   * @swagger GET /api/crm/member-levels/{id}
   * @example
   * const level = await memberLevelApi.getMemberLevel(1)
   */
  getMemberLevel: (id: number) => {
    return axios.get<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`)
  },
  
  /**
   * 刪除會員等級
   * @description 刪除指定的會員等級
   * @param {number} id - 等級 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/crm/member-levels/{id}
   * @example
   * await memberLevelApi.deleteMemberLevel(1)
   */
  deleteMemberLevel: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/member-levels/${id}`)
  },
  
  /**
   * 分頁查詢會員等級
   * @description 分頁查詢所有會員等級
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<MemberLevel>>>} 分頁等級資料
   * @swagger GET /api/crm/member-levels
   * @example
   * const page = await memberLevelApi.listMemberLevels(0, 10)
   */
  listMemberLevels: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<MemberLevel>>>('/crm/member-levels', {
      params: { page, size }
    })
  },
  
  /**
   * 取得所有會員等級
   * @description 查詢系統中所有會員等級（不分頁）
   * @returns {Promise<ApiResponse<MemberLevel[]>>} 等級列表
   * @swagger GET /api/crm/member-levels/all
   * @example
   * const allLevels = await memberLevelApi.listAllMemberLevels()
   */
  listAllMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/all')
  },
  
  /**
   * 取得已啟用的會員等級
   * @description 查詢所有已啟用的會員等級
   * @returns {Promise<ApiResponse<MemberLevel[]>>} 已啟用等級列表
   * @swagger GET /api/crm/member-levels/enabled
   * @example
   * const enabledLevels = await memberLevelApi.listEnabledMemberLevels()
   */
  listEnabledMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/enabled')
  },
  
  /**
   * 切換會員等級啟用狀態
   * @description 切換指定會員等級的啟用/停用狀態
   * @param {number} id - 等級 ID
   * @returns {Promise<ApiResponse<MemberLevel>>} 更新後的等級資料
   * @swagger PUT /api/crm/member-levels/{id}/toggle-enabled
   * @example
   * const toggled = await memberLevelApi.toggleEnabled(1)
   */
  toggleEnabled: (id: number) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}/toggle-enabled`)
  }
}

export default memberLevelApi
