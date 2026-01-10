/**
 * 會員群組管理 API
 * @module MemberGroupAPI
 */

import axios from 'axios'

export interface MemberGroup {
  id?: number
  name: string
  description?: string
  memberCount?: number
  enabled: boolean
  createdAt?: string
  updatedAt?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

const API_BASE = '/api/crm/member-groups'

export const memberGroupApi = {
  // 獲取群組列表
  getGroups: async (page = 0, size = 20) => {
    const { data } = await axios.get<any>(`${API_BASE}`, {
      params: { page, size }
    })
    return data.data as PageResponse<MemberGroup>
  },

  // 獲取已啟用的群組
  getEnabledGroups: async () => {
    const { data } = await axios.get<any>(`${API_BASE}/enabled`)
    return data.data as MemberGroup[]
  },

  // 獲取單個群組詳情
  getGroup: async (id: number) => {
    const { data } = await axios.get<any>(`${API_BASE}/${id}`)
    return data.data as MemberGroup
  },

  // 創建群組
  createGroup: async (group: MemberGroup) => {
    const { data } = await axios.post<any>(`${API_BASE}`, group)
    return data.data as MemberGroup
  },

  // 更新群組
  updateGroup: async (id: number, group: Partial<MemberGroup>) => {
    const { data } = await axios.put<any>(`${API_BASE}/${id}`, group)
    return data.data as MemberGroup
  },

  // 刪除群組
  deleteGroup: async (id: number) => {
    const { data } = await axios.delete<any>(`${API_BASE}/${id}`)
    return data.success
  },

  // 將會員加入群組
  addMemberToGroup: async (groupId: number, memberId: number) => {
    const { data } = await axios.post<any>(
      `${API_BASE}/${groupId}/members/${memberId}`
    )
    return data.success
  },

  // 將會員從群組移除
  removeMemberFromGroup: async (groupId: number, memberId: number) => {
    const { data } = await axios.delete<any>(
      `${API_BASE}/${groupId}/members/${memberId}`
    )
    return data.success
  },

  // 取得群組內的會員
  getGroupMembers: async (groupId: number, page = 0, size = 50) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/${groupId}/members`,
      { params: { page, size } }
    )
    return data.data as number[]
  },

  // 取得會員所屬群組
  getMemberGroups: async (memberId: number) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/member/${memberId}`
    )
    return data.data as number[]
  }
}
