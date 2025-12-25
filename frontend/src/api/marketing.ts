import axiosInstance from './axios'

export interface MarketingCampaign {
  id?: number
  name: string
  description?: string
  type: 'DISCOUNT' | 'PROMOTION' | 'FLASH_SALE' | 'FREE_SHIPPING' | 'COUPON' | 'OTHER'
  status: 'DRAFT' | 'ACTIVE' | 'ENDED' | 'SCHEDULED'
  startDate: string
  endDate: string
  discountRate?: number
  discountAmount?: number
  minPurchaseAmount?: number
  targetAudience?: string
  createdAt?: string
  updatedAt?: string
}

export interface MarketingResponse {
  data: MarketingCampaign[]
  total: number
  page: number
  pageSize: number
}

const marketingApi = {
  // 獲取所有營銷活動
  getAllCampaigns: async (page: number = 1, pageSize: number = 10) => {
    return axiosInstance.get<any, MarketingResponse>('/marketing/campaigns', {
      params: { page, pageSize }
    })
  },

  // 按 ID 獲取營銷活動
  getCampaignById: async (id: number) => {
    return axiosInstance.get<any, { data: MarketingCampaign }>(`/marketing/campaigns/${id}`)
  },

  // 按狀態獲取營銷活動
  getCampaignsByStatus: async (status: string, page: number = 1, pageSize: number = 10) => {
    return axiosInstance.get<any, MarketingResponse>('/marketing/campaigns', {
      params: { status, page, pageSize }
    })
  },

  // 按類型獲取營銷活動
  getCampaignsByType: async (type: string, page: number = 1, pageSize: number = 10) => {
    return axiosInstance.get<any, MarketingResponse>('/marketing/campaigns', {
      params: { type, page, pageSize }
    })
  },

  // 創建營銷活動
  createCampaign: async (campaign: MarketingCampaign) => {
    return axiosInstance.post<any, { data: MarketingCampaign }>('/marketing/campaigns', campaign)
  },

  // 更新營銷活動
  updateCampaign: async (id: number, campaign: MarketingCampaign) => {
    return axiosInstance.put<any, { data: MarketingCampaign }>(`/marketing/campaigns/${id}`, campaign)
  },

  // 刪除營銷活動
  deleteCampaign: async (id: number) => {
    return axiosInstance.delete(`/marketing/campaigns/${id}`)
  },

  // 更新活動狀態
  updateCampaignStatus: async (id: number, status: string) => {
    return axiosInstance.patch(`/marketing/campaigns/${id}/status`, { status })
  }
}

export default marketingApi
