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
    const data = await axiosInstance.get<any>(`${API_BASE}`, {
      params: { page, size }
    })
    return data.data as PageResponse<EdmCampaign>
  },

  // 獲取單個 EDM 活動詳情
  getCampaign: async (id: number) => {
    const data = await axiosInstance.get<any>(`${API_BASE}/${id}`)
    return data.data as EdmCampaign
  },

  // 創建 EDM 活動
  createCampaign: async (campaign: EdmCampaign) => {
    const data = await axiosInstance.post<any>(`${API_BASE}`, campaign)
    return data.data as EdmCampaign
  },

  // 更新 EDM 活動
  updateCampaign: async (id: number, campaign: Partial<EdmCampaign>) => {
    const data = await axiosInstance.put<any>(`${API_BASE}/${id}`, campaign)
    return data.data as EdmCampaign
  },

  // 刪除 EDM 活動
  deleteCampaign: async (id: number) => {
    const data = await axiosInstance.delete<any>(`${API_BASE}/${id}`)
    return data.success
  },

  // 發送 EDM 活動
  sendCampaign: async (id: number) => {
    const data = await axiosInstance.post<any>(`${API_BASE}/${id}/send`)
    return data.success
  },

  // 排程發送 EDM 活動
  scheduleCampaign: async (id: number, sendTime: string) => {
    const data = await axiosInstance.post<any>(
      `${API_BASE}/${id}/schedule`,
      { sendTime }
    )
    return data.success
  },

  // 獲取 EDM 發送日誌
  getSendLogs: async (campaignId: number, page = 0, size = 20) => {
    const data = await axiosInstance.get<any>(
      `${API_BASE}/${campaignId}/logs`,
      { params: { page, size } }
    )
    return data.data as PageResponse<EdmSendLog>
  },

  // 獲取 EDM 統計資訊
  getStatistics: async () => {
    const data = await axiosInstance.get<any>(`${API_BASE}/statistics`)
    return data.data as EdmStatistics
  },

  // 按狀態查詢
  getCampaignsByStatus: async (status: string, page = 0, size = 20) => {
    const data = await axiosInstance.get<any>(
      `${API_BASE}/status/${status}`,
      { params: { page, size } }
    )
    return data.data as PageResponse<EdmCampaign>
  }
}
