import apiClient from './client'
import type {
  ApiResponse,
  PageResponse,
  Member,
  MemberLevel,
  MemberGroup,
  PointRecord,
  PointBatch,
  EdmCampaign,
  BlogPost
} from './types'

/**
 * 會員管理 API
 */
export const memberApi = {
  // 創建會員
  create: (data: Member) => {
    return apiClient.post<ApiResponse<Member>>('/api/crm/members', data)
  },

  // 更新會員
  update: (id: number, data: Member) => {
    return apiClient.put<ApiResponse<Member>>(`/api/crm/members/${id}`, data)
  },

  // 取得會員詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<Member>>(`/api/crm/members/${id}`)
  },

  // 依電子郵件取得會員
  getByEmail: (email: string) => {
    return apiClient.get<ApiResponse<Member>>(`/api/crm/members/email/${email}`)
  },

  // 刪除會員
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/crm/members/${id}`)
  },

  // 分頁查詢會員
  list: (page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Member>>>('/api/crm/members', {
      params: { page, size }
    })
  },

  // 依狀態查詢會員
  listByStatus: (status: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Member>>>(`/api/crm/members/status/${status}`, {
      params: { page, size }
    })
  },

  // 依等級查詢會員
  listByLevel: (levelId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Member>>>(
      `/api/crm/members/level/${levelId}`,
      {
        params: { page, size }
      }
    )
  }
}

/**
 * 會員等級 API
 */
export const memberLevelApi = {
  // 創建會員等級
  create: (data: MemberLevel) => {
    return apiClient.post<ApiResponse<MemberLevel>>('/api/crm/member-levels', data)
  },

  // 更新會員等級
  update: (id: number, data: MemberLevel) => {
    return apiClient.put<ApiResponse<MemberLevel>>(`/api/crm/member-levels/${id}`, data)
  },

  // 取得會員等級詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<MemberLevel>>(`/api/crm/member-levels/${id}`)
  },

  // 刪除會員等級
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/crm/member-levels/${id}`)
  },

  // 取得所有會員等級
  listAll: () => {
    return apiClient.get<ApiResponse<MemberLevel[]>>('/api/crm/member-levels')
  },

  // 取得已啟用的會員等級
  listEnabled: () => {
    return apiClient.get<ApiResponse<MemberLevel[]>>('/api/crm/member-levels/enabled')
  }
}

/**
 * 會員群組 API
 */
export const memberGroupApi = {
  // 創建會員群組
  create: (data: MemberGroup) => {
    return apiClient.post<ApiResponse<MemberGroup>>('/api/crm/member-groups', data)
  },

  // 更新會員群組
  update: (id: number, data: MemberGroup) => {
    return apiClient.put<ApiResponse<MemberGroup>>(`/api/crm/member-groups/${id}`, data)
  },

  // 取得會員群組詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<MemberGroup>>(`/api/crm/member-groups/${id}`)
  },

  // 刪除會員群組
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/crm/member-groups/${id}`)
  },

  // 取得所有會員群組
  listAll: () => {
    return apiClient.get<ApiResponse<MemberGroup[]>>('/api/crm/member-groups')
  },

  // 新增會員到群組
  addMember: (groupId: number, memberId: number) => {
    return apiClient.post<ApiResponse<void>>(`/api/crm/member-groups/${groupId}/members/${memberId}`)
  },

  // 從群組移除會員
  removeMember: (groupId: number, memberId: number) => {
    return apiClient.delete<ApiResponse<void>>(
      `/api/crm/member-groups/${groupId}/members/${memberId}`
    )
  },

  // 取得群組內的會員
  listMembers: (groupId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Member>>>(
      `/api/crm/member-groups/${groupId}/members`,
      {
        params: { page, size }
      }
    )
  }
}

/**
 * 積點管理 API
 */
export const pointApi = {
  // 發放積點
  earn: (data: PointRecord) => {
    return apiClient.post<ApiResponse<PointRecord>>('/api/crm/points/earn', data)
  },

  // 兌換積點
  redeem: (data: PointRecord) => {
    return apiClient.post<ApiResponse<PointRecord>>('/api/crm/points/redeem', data)
  },

  // 調整積點
  adjust: (data: PointRecord) => {
    return apiClient.post<ApiResponse<PointRecord>>('/api/crm/points/adjust', data)
  },

  // 批次發放積點
  batchEarn: (data: PointBatch) => {
    return apiClient.post<ApiResponse<void>>('/api/crm/points/batch-earn', data)
  },

  // 取得會員積點記錄
  listByMember: (memberId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<PointRecord>>>(
      `/api/crm/points/member/${memberId}`,
      {
        params: { page, size }
      }
    )
  },

  // 取得會員目前積點
  getMemberPoints: (memberId: number) => {
    return apiClient.get<ApiResponse<{ points: number }>>(`/api/crm/points/member/${memberId}/total`)
  }
}

/**
 * EDM 電子報 API
 */
export const edmApi = {
  // 創建 EDM 活動
  create: (data: EdmCampaign) => {
    return apiClient.post<ApiResponse<EdmCampaign>>('/api/crm/edm', data)
  },

  // 更新 EDM 活動
  update: (id: number, data: EdmCampaign) => {
    return apiClient.put<ApiResponse<EdmCampaign>>(`/api/crm/edm/${id}`, data)
  },

  // 取得 EDM 活動詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<EdmCampaign>>(`/api/crm/edm/${id}`)
  },

  // 刪除 EDM 活動
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/crm/edm/${id}`)
  },

  // 分頁查詢 EDM 活動
  list: (page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<EdmCampaign>>>('/api/crm/edm', {
      params: { page, size }
    })
  },

  // 立即發送 EDM
  send: (id: number) => {
    return apiClient.post<ApiResponse<void>>(`/api/crm/edm/${id}/send`)
  },

  // 取得 EDM 統計
  getStatistics: (id: number) => {
    return apiClient.get<ApiResponse<{ sentCount: number; openCount: number; clickCount: number }>>(
      `/api/crm/edm/${id}/statistics`
    )
  }
}

/**
 * 部落格 API
 */
export const blogApi = {
  // 創建部落格文章
  create: (data: BlogPost) => {
    return apiClient.post<ApiResponse<BlogPost>>('/api/crm/blog', data)
  },

  // 更新部落格文章
  update: (id: number, data: BlogPost) => {
    return apiClient.put<ApiResponse<BlogPost>>(`/api/crm/blog/${id}`, data)
  },

  // 取得部落格文章詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<BlogPost>>(`/api/crm/blog/${id}`)
  },

  // 刪除部落格文章
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/crm/blog/${id}`)
  },

  // 分頁查詢部落格文章
  list: (page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<BlogPost>>>('/api/crm/blog', {
      params: { page, size }
    })
  },

  // 依狀態查詢部落格文章
  listByStatus: (status: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<BlogPost>>>(`/api/crm/blog/status/${status}`, {
      params: { page, size }
    })
  },

  // 發布部落格文章
  publish: (id: number) => {
    return apiClient.post<ApiResponse<BlogPost>>(`/api/crm/blog/${id}/publish`)
  },

  // 封存部落格文章
  archive: (id: number) => {
    return apiClient.post<ApiResponse<BlogPost>>(`/api/crm/blog/${id}/archive`)
  }
}
