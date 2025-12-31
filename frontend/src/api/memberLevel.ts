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

// 會員等級 API
export const memberLevelApi = {
  // 創建會員等級
  createMemberLevel: (data: MemberLevel) => {
    return axios.post<any, ApiResponse<MemberLevel>>('/crm/member-levels', data)
  },
  
  // 更新會員等級
  updateMemberLevel: (id: number, data: MemberLevel) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`, data)
  },
  
  // 取得會員等級詳情
  getMemberLevel: (id: number) => {
    return axios.get<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`)
  },
  
  // 刪除會員等級
  deleteMemberLevel: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/member-levels/${id}`)
  },
  
  // 分頁查詢會員等級
  listMemberLevels: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<MemberLevel>>>('/crm/member-levels', {
      params: { page, size }
    })
  },
  
  // 取得所有會員等級
  listAllMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/all')
  },
  
  // 取得已啟用的會員等級
  listEnabledMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/enabled')
  },
  
  // 切換會員等級啟用狀態
  toggleEnabled: (id: number) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}/toggle-enabled`)
  }
}

export default memberLevelApi
