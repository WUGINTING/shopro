import axiosInstance from './axios'

export interface EdmCampaign {
  id?: number
  name: string
  subject: string
  content: string
  targetGroupId?: number
  sendTime?: string
  status: 'DRAFT' | 'SCHEDULED' | 'SENT' | 'FAILED'
  sentCount?: number
  openCount?: number
  clickCount?: number
  createdAt?: string
  updatedAt?: string
}

export interface EdmSendLog {
  id?: number
  campaignId: number
  memberId: number
  memberEmail?: string
  status: 'PENDING' | 'SENT' | 'OPENED' | 'CLICKED' | 'FAILED'
  sentAt?: string
  openedAt?: string
  failedReason?: string
  createdAt?: string
}

export interface EdmStatistics {
  totalCampaigns: number
  totalSent: number
  totalOpened: number
  totalClicked: number
  averageOpenRate: number
  averageClickRate: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

const API_BASE = '/crm/edm'

export const edmApi = {
  // 獲取 EDM 活動列表
  getCampaigns: async (page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmCampaign>>(`${API_BASE}`, {
      params: { page, size }
    })
  },

  // 獲取單個 EDM 活動詳情
  getCampaign: async (id: number) => {
    return axiosInstance.get<any, EdmCampaign>(`${API_BASE}/${id}`)
  },

  // 創建 EDM 活動
  createCampaign: async (campaign: EdmCampaign) => {
    return axiosInstance.post<any, EdmCampaign>(`${API_BASE}`, campaign)
  },

  // 更新 EDM 活動
  updateCampaign: async (id: number, campaign: Partial<EdmCampaign>) => {
    return axiosInstance.put<any, EdmCampaign>(`${API_BASE}/${id}`, campaign)
  },

  // 刪除 EDM 活動
  deleteCampaign: async (id: number) => {
    return axiosInstance.delete<any, boolean>(`${API_BASE}/${id}`)
  },

  // 發送 EDM 活動
  sendCampaign: async (id: number) => {
    return axiosInstance.post<any, boolean>(`${API_BASE}/${id}/send`)
  },

  // 排程發送 EDM 活動
  scheduleCampaign: async (id: number, sendTime: string) => {
    return axiosInstance.post<any, boolean>(
      `${API_BASE}/${id}/schedule`,
      { sendTime }
    )
  },

  // 獲取 EDM 發送日誌
  getSendLogs: async (campaignId: number, page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmSendLog>>(
      `${API_BASE}/${campaignId}/logs`,
      { params: { page, size } }
    )
  },

  // 獲取 EDM 統計資訊
  getStatistics: async () => {
    return axiosInstance.get<any, EdmStatistics>(`${API_BASE}/statistics`)
  },

  // 按狀態查詢
  getCampaignsByStatus: async (status: string, page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<EdmCampaign>>(
      `${API_BASE}/status/${status}`,
      { params: { page, size } }
    )
  }
}
