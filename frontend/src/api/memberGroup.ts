/**
 * 會員群組管理 API
 * @module MemberGroupAPI
 */

import axiosInstance from './axios'
import type { ApiResponse, PageResponse as ApiPageResponse } from './types'

export interface MemberGroup {
  id?: number
  name: string
  description?: string
  memberCount?: number
  enabled: boolean
  createdAt?: string
  updatedAt?: string
}

// Use unified PageResponse type (imported from types.ts)
// But for backward compatibility, keep this alias
export type PageResponse<T> = ApiPageResponse<T>

/**
 * 會員群組 API 服務
 * @namespace memberGroupApi
 */
export const memberGroupApi = {
  /**
   * 獲取群組列表
   * @description 分頁查詢會員群組列表
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<PageResponse<MemberGroup>>} 群組分頁資料
   * @swagger GET /api/crm/member-groups
   * @example
   * const groups = await memberGroupApi.getGroups(0, 20)
   */
  getGroups: async (page = 0, size = 20) => {
    const response = await axiosInstance.get<any, ApiResponse<any>>(
      '/crm/member-groups',
      { params: { page, size } }
    )
    // 處理 Spring Data Page 格式
    const pageData = response.data
    if (pageData && typeof pageData === 'object') {
      return {
        content: pageData.content || [],
        totalElements: pageData.totalElements || 0,
        totalPages: pageData.totalPages || 0,
        currentPage: pageData.pageable?.pageNumber ?? pageData.number ?? page,
        pageSize: pageData.pageable?.pageSize ?? pageData.size ?? size
      } as PageResponse<MemberGroup>
    }
    return pageData as PageResponse<MemberGroup>
  },

  /**
   * 獲取已啟用的群組
   * @description 查詢所有已啟用的會員群組
   * @returns {Promise<MemberGroup[]>} 已啟用群組列表
   * @swagger GET /api/crm/member-groups/enabled
   * @example
   * const enabledGroups = await memberGroupApi.getEnabledGroups()
   */
  getEnabledGroups: async () => {
    const response = await axiosInstance.get<any, ApiResponse<MemberGroup[]>>(
      '/crm/member-groups/enabled'
    )
    return response.data
  },

  /**
   * 獲取單個群組詳情
   * @description 查詢指定 ID 的會員群組詳細資訊
   * @param {number} id - 群組 ID
   * @returns {Promise<MemberGroup>} 群組資料
   * @swagger GET /api/crm/member-groups/{id}
   * @example
   * const group = await memberGroupApi.getGroup(123)
   */
  getGroup: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<MemberGroup>>(
      `/crm/member-groups/${id}`
    )
    return response.data
  },

  /**
   * 創建群組
   * @description 創建新的會員群組
   * @param {MemberGroup} group - 群組資料
   * @param {string} group.name - 群組名稱（必填）
   * @param {boolean} group.enabled - 是否啟用（必填）
   * @returns {Promise<MemberGroup>} 創建成功的群組資料
   * @swagger POST /api/crm/member-groups
   * @example
   * const newGroup = await memberGroupApi.createGroup({
   *   name: 'VIP客戶',
   *   description: '高價值客戶群組',
   *   enabled: true
   * })
   */
  createGroup: async (group: MemberGroup) => {
    const response = await axiosInstance.post<any, ApiResponse<MemberGroup>>(
      '/crm/member-groups',
      group
    )
    return response.data
  },

  /**
   * 更新群組
   * @description 更新指定會員群組的資訊
   * @param {number} id - 群組 ID
   * @param {Partial<MemberGroup>} group - 更新的群組資料
   * @returns {Promise<MemberGroup>} 更新後的群組資料
   * @swagger PUT /api/crm/member-groups/{id}
   * @example
   * const updated = await memberGroupApi.updateGroup(123, { name: '超級VIP' })
   */
  updateGroup: async (id: number, group: Partial<MemberGroup>) => {
    const response = await axiosInstance.put<any, ApiResponse<MemberGroup>>(
      `/crm/member-groups/${id}`,
      group
    )
    return response.data
  },

  /**
   * 刪除群組
   * @description 刪除指定的會員群組
   * @param {number} id - 群組 ID
   * @returns {Promise<boolean>} 刪除是否成功
   * @swagger DELETE /api/crm/member-groups/{id}
   * @example
   * const success = await memberGroupApi.deleteGroup(123)
   */
  deleteGroup: async (id: number) => {
    const response = await axiosInstance.delete<any, ApiResponse<void>>(
      `/crm/member-groups/${id}`
    )
    return response.success || true
  },

  /**
   * 將會員加入群組
   * @description 將指定會員添加到群組中
   * @param {number} groupId - 群組 ID
   * @param {number} memberId - 會員 ID
   * @returns {Promise<boolean>} 是否成功
   * @swagger POST /api/crm/member-groups/{groupId}/members/{memberId}
   * @example
   * const success = await memberGroupApi.addMemberToGroup(123, 456)
   */
  addMemberToGroup: async (groupId: number, memberId: number) => {
    const response = await axiosInstance.post<any, ApiResponse<void>>(
      `/crm/member-groups/${groupId}/members/${memberId}`
    )
    return response.success || true
  },

  /**
   * 將會員從群組移除
   * @description 將指定會員從群組中移除
   * @param {number} groupId - 群組 ID
   * @param {number} memberId - 會員 ID
   * @returns {Promise<boolean>} 是否成功
   * @swagger DELETE /api/crm/member-groups/{groupId}/members/{memberId}
   * @example
   * const success = await memberGroupApi.removeMemberFromGroup(123, 456)
   */
  removeMemberFromGroup: async (groupId: number, memberId: number) => {
    const response = await axiosInstance.delete<any, ApiResponse<void>>(
      `/crm/member-groups/${groupId}/members/${memberId}`
    )
    return response.success || true
  },

  /**
   * 取得群組內的會員
   * @description 查詢指定群組中的所有會員 ID
   * @param {number} groupId - 群組 ID
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=50] - 每頁數量
   * @returns {Promise<number[]>} 會員 ID 陣列
   * @swagger GET /api/crm/member-groups/{groupId}/members
   * @example
   * const memberIds = await memberGroupApi.getGroupMembers(123, 0, 50)
   */
  getGroupMembers: async (groupId: number, page = 0, size = 50) => {
    const response = await axiosInstance.get<any, ApiResponse<number[]>>(
      `/crm/member-groups/${groupId}/members`,
      { params: { page, size } }
    )
    return response.data
  },

  /**
   * 取得會員所屬群組
   * @description 查詢指定會員所屬的所有群組 ID
   * @param {number} memberId - 會員 ID
   * @returns {Promise<number[]>} 群組 ID 陣列
   * @swagger GET /api/crm/member-groups/member/{memberId}
   * @example
   * const groupIds = await memberGroupApi.getMemberGroups(456)
   */
  getMemberGroups: async (memberId: number) => {
    const response = await axiosInstance.get<any, ApiResponse<number[]>>(
      `/crm/member-groups/member/${memberId}`
    )
    return response.data
  }
}
