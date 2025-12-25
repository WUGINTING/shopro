import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 会员等级相关接口
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

// 会员等级 API
export const memberLevelApi = {
  // 创建会员等级
  createMemberLevel: (data: MemberLevel) => {
    return axios.post<any, ApiResponse<MemberLevel>>('/crm/member-levels', data)
  },
  
  // 更新会员等级
  updateMemberLevel: (id: number, data: MemberLevel) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`, data)
  },
  
  // 取得会员等级详情
  getMemberLevel: (id: number) => {
    return axios.get<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}`)
  },
  
  // 删除会员等级
  deleteMemberLevel: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/member-levels/${id}`)
  },
  
  // 分页查询会员等级
  listMemberLevels: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<MemberLevel>>>('/crm/member-levels', {
      params: { page, size }
    })
  },
  
  // 取得所有会员等级
  listAllMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/all')
  },
  
  // 取得已启用的会员等级
  listEnabledMemberLevels: () => {
    return axios.get<any, ApiResponse<MemberLevel[]>>('/crm/member-levels/enabled')
  },
  
  // 切换会员等级启用状态
  toggleEnabled: (id: number) => {
    return axios.put<any, ApiResponse<MemberLevel>>(`/crm/member-levels/${id}/toggle-enabled`)
  }
}

export default memberLevelApi
