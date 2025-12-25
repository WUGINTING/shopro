import axios from 'axios'

export interface PointRecord {
  id?: number
  memberId: number
  points: number
  pointType: 'EARN' | 'PURCHASE' | 'REWARD' | 'REDEEM' | 'EXPIRE' | 'ADJUST'
  reason: string
  orderId?: number
  balanceBefore?: number
  balanceAfter?: number
  createdAt?: string
}

export interface PointBalance {
  memberId: number
  totalPoints: number
  usablePoints: number
  expiredPoints: number
}

export interface BatchGrantRequest {
  memberIds: number[]
  points: number
  reason: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

const API_BASE = '/api/crm/points'

export const pointApi = {
  // 增加積點
  addPoints: async (memberId: number, points: number, reason: string) => {
    const { data } = await axios.post<any>(`${API_BASE}/add`, {
      memberId,
      points,
      pointType: 'EARN',
      reason
    })
    return data.data as PointRecord
  },

  // 扣除積點
  deductPoints: async (memberId: number, points: number, reason: string) => {
    const { data } = await axios.post<any>(`${API_BASE}/deduct`, {
      memberId,
      points,
      pointType: 'REDEEM',
      reason
    })
    return data.data as PointRecord
  },

  // 批次發放積點
  batchGrant: async (request: BatchGrantRequest) => {
    const { data } = await axios.post<any>(
      `${API_BASE}/batch-grant`,
      request
    )
    return data.success
  },

  // 獲取會員積點紀錄
  getMemberPoints: async (memberId: number, page = 0, size = 20) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/member/${memberId}`,
      { params: { page, size } }
    )
    return data.data as PageResponse<PointRecord>
  },

  // 獲取會員積點餘額
  getPointBalance: async (memberId: number) => {
    const { data } = await axios.get<any>(
      `${API_BASE}/balance/${memberId}`
    )
    return data.data as PointBalance
  },

  // 獲取積點紀錄詳情
  getPointRecord: async (id: number) => {
    const { data } = await axios.get<any>(`${API_BASE}/${id}`)
    return data.data as PointRecord
  },

  // 查詢積點紀錄
  searchPoints: async (memberId?: number, pointType?: string, page = 0, size = 20) => {
    const { data } = await axios.get<any>(`${API_BASE}/search`, {
      params: { memberId, pointType, page, size }
    })
    return data.data as PageResponse<PointRecord>
  }
}
