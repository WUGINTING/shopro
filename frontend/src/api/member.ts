/**
 * 會員管理 API
 * @module MemberAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

export interface Member {
  id?: number
  name: string
  email: string
  phone?: string
  memberLevel?: number
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  registeredDate?: string
  totalPoints: number
  totalSpent: number
  notes?: string
  createdAt?: string
  updatedAt?: string
}

export interface MemberQueryRequest {
  name?: string
  email?: string
  status?: string
  page?: number
  size?: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

/**
 * 會員 API 服務
 * @namespace memberApi
 */
export const memberApi = {
  /**
   * 獲取會員列表
   * @description 分頁查詢會員列表，支援名稱、郵件、狀態等條件篩選
   * @param {MemberQueryRequest} [query] - 查詢參數
   * @param {string} [query.name] - 會員名稱
   * @param {string} [query.email] - 會員郵件
   * @param {string} [query.status] - 會員狀態
   * @param {number} [query.page=0] - 頁碼
   * @param {number} [query.size=20] - 每頁數量
   * @returns {Promise<PageResponse<Member>>} 會員分頁資料
   * @swagger GET /api/crm/members
   * @example
   * const members = await memberApi.getMembers({ page: 0, size: 20, status: 'ACTIVE' })
   */
  getMembers: async (query?: MemberQueryRequest) => {
    const params = {
      page: query?.page || 0,
      size: query?.size || 20,
      ...query
    }
    const response = await axiosInstance.get<any, ApiResponse<PageResponse<Member>>>('/crm/members', { params })
    return response.data
  },

  /**
   * 獲取單個會員詳情
   * @description 查詢指定 ID 的會員詳細資訊
   * @param {number} id - 會員 ID
   * @returns {Promise<Member>} 會員資料
   * @swagger GET /api/crm/members/{id}
   * @example
   * const member = await memberApi.getMember(123)
   */
  getMember: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<Member>>(`/crm/members/${id}`)
    return response.data
  },

  /**
   * 創建會員
   * @description 創建新的會員帳號
   * @param {Member} member - 會員資料
   * @param {string} member.name - 會員姓名（必填）
   * @param {string} member.email - 電子郵件（必填）
   * @param {'ACTIVE' | 'INACTIVE' | 'SUSPENDED'} member.status - 會員狀態（必填）
   * @returns {Promise<Member>} 創建成功的會員資料
   * @swagger POST /api/crm/members
   * @example
   * const newMember = await memberApi.createMember({
   *   name: '王小明',
   *   email: 'wang@example.com',
   *   status: 'ACTIVE',
   *   totalPoints: 0,
   *   totalSpent: 0
   * })
   */
  createMember: async (member: Member) => {
    const response = await axiosInstance.post<any, ApiResponse<Member>>('/crm/members', member)
    return response.data
  },

  /**
   * 更新會員
   * @description 更新指定會員的資訊
   * @param {number} id - 會員 ID
   * @param {Partial<Member>} member - 更新的會員資料
   * @returns {Promise<Member>} 更新後的會員資料
   * @swagger PUT /api/crm/members/{id}
   * @example
   * const updated = await memberApi.updateMember(123, { phone: '0912345678' })
   */
  updateMember: async (id: number, member: Partial<Member>) => {
    const response = await axiosInstance.put<any, ApiResponse<Member>>(`/crm/members/${id}`, member)
    return response.data
  },

  /**
   * 刪除會員
   * @description 刪除指定的會員帳號
   * @param {number} id - 會員 ID
   * @returns {Promise<boolean>} 刪除是否成功
   * @swagger DELETE /api/crm/members/{id}
   * @example
   * const success = await memberApi.deleteMember(123)
   */
  deleteMember: async (id: number) => {
    const response = await axiosInstance.delete<any, ApiResponse<boolean>>(`/crm/members/${id}`)
    return response.data
  },

  /**
   * 批量刪除會員
   * @description 批量刪除多個會員帳號
   * @param {number[]} ids - 會員 ID 陣列
   * @returns {Promise<boolean>} 刪除是否成功
   * @swagger DELETE /api/crm/members/batch
   * @example
   * const success = await memberApi.deleteMembers([123, 456, 789])
   */
  deleteMembers: async (ids: number[]) => {
    const response = await axiosInstance.delete<any, ApiResponse<boolean>>('/crm/members/batch', {
      data: { ids }
    })
    return response.data
  },

  /**
   * 停用會員
   * @description 將會員狀態設為 SUSPENDED
   * @param {number} id - 會員 ID
   * @returns {Promise<Member>} 更新後的會員資料
   * @swagger PATCH /api/crm/members/{id}/suspend
   * @example
   * const suspended = await memberApi.suspendMember(123)
   */
  suspendMember: async (id: number) => {
    const response = await axiosInstance.patch<any, ApiResponse<Member>>(`/crm/members/${id}/suspend`)
    return response.data
  },

  /**
   * 啟用會員
   * @description 將會員狀態設為 ACTIVE
   * @param {number} id - 會員 ID
   * @returns {Promise<Member>} 更新後的會員資料
   * @swagger PATCH /api/crm/members/{id}/activate
   * @example
   * const activated = await memberApi.activateMember(123)
   */
  activateMember: async (id: number) => {
    const response = await axiosInstance.patch<any, ApiResponse<Member>>(`/crm/members/${id}/activate`)
    return response.data
  },

  /**
   * 搜尋會員
   * @description 使用關鍵字搜尋會員（搜尋名稱、郵件等）
   * @param {string} keyword - 搜尋關鍵字
   * @param {number} [page=0] - 頁碼
   * @returns {Promise<PageResponse<Member>>} 會員分頁資料
   * @swagger GET /api/crm/members/search
   * @example
   * const results = await memberApi.searchMembers('王', 0)
   */
  searchMembers: async (keyword: string, page = 0) => {
    const response = await axiosInstance.get<any, ApiResponse<PageResponse<Member>>>('/crm/members/search', {
      params: { keyword, page, size: 20 }
    })
    return response.data
  }
}
