/**
 * 顧客黑名單 API（黑名單中的顧客無法下單）
 */
import axios from './axios'
import type { ApiResponse } from './types'

export interface BlacklistEntry {
  id?: number
  customerId: number
  customerName?: string
  customerPhone?: string
  customerEmail?: string
  reason: string
  isActive?: boolean
  createdByName?: string
  createdAt?: string
}

export const blacklistApi = {
  listActive: () => axios.get<any, ApiResponse<BlacklistEntry[]>>('/orders/blacklist/active'),
  add: (data: BlacklistEntry) => axios.post<any, ApiResponse<BlacklistEntry>>('/orders/blacklist', data),
  remove: (id: number) => axios.patch<any, ApiResponse<BlacklistEntry>>(`/orders/blacklist/${id}/remove`)
}
