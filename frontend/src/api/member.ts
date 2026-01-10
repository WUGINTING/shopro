/**
 * 會員管理 API
 * @module MemberAPI
 */

import axios from 'axios'

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

const API_BASE = '/api/crm/members'

export const memberApi = {
  // 獲取會員列表
  getMembers: async (query?: MemberQueryRequest) => {
    const params = {
      page: query?.page || 0,
      size: query?.size || 20,
      ...query
    }
    const { data } = await axios.get<any>(`${API_BASE}`, { params })
    return data.data as PageResponse<Member>
  },

  // 獲取單個會員詳情
  getMember: async (id: number) => {
    const { data } = await axios.get<any>(`${API_BASE}/${id}`)
    return data.data as Member
  },

  // 創建會員
  createMember: async (member: Member) => {
    const { data } = await axios.post<any>(`${API_BASE}`, member)
    return data.data as Member
  },

  // 更新會員
  updateMember: async (id: number, member: Partial<Member>) => {
    const { data } = await axios.put<any>(`${API_BASE}/${id}`, member)
    return data.data as Member
  },

  // 刪除會員
  deleteMember: async (id: number) => {
    const { data } = await axios.delete<any>(`${API_BASE}/${id}`)
    return data.success
  },

  // 批量刪除
  deleteMembers: async (ids: number[]) => {
    const { data } = await axios.delete<any>(`${API_BASE}/batch`, {
      data: { ids }
    })
    return data.success
  },

  // 停用會員
  suspendMember: async (id: number) => {
    const { data } = await axios.patch<any>(`${API_BASE}/${id}/suspend`)
    return data.data as Member
  },

  // 啟用會員
  activateMember: async (id: number) => {
    const { data } = await axios.patch<any>(`${API_BASE}/${id}/activate`)
    return data.data as Member
  },

  // 搜尋會員
  searchMembers: async (keyword: string, page = 0) => {
    const { data } = await axios.get<any>(`${API_BASE}/search`, {
      params: { keyword, page, size: 20 }
    })
    return data.data as PageResponse<Member>
  }
}
