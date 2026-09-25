/**
 * 運費設定 API（/api/system/shipping-config，限管理員）
 */
import axios from './axios'
import type { ApiResponse } from './types'

export interface ShippingConfig {
  id?: number
  providerName: string
  enabled: boolean
  shippingMethod: 'HOME_DELIVERY' | 'STORE_PICKUP'
  shippingMethodName?: string
  baseShippingFee: number
  freeShippingThreshold?: number | null
  estimatedDeliveryDays?: number | null
  sortOrder?: number
  testMode: boolean
  notes?: string
}

export interface SystemCheck {
  key: string
  label: string
  ok: boolean
  detail: string
}

export const shippingConfigApi = {
  list: () => axios.get<any, ApiResponse<{ content: ShippingConfig[] }>>('/system/shipping-config', { params: { page: 0, size: 100 } }),
  create: (data: ShippingConfig) => axios.post<any, ApiResponse<ShippingConfig>>('/system/shipping-config', data),
  update: (id: number, data: ShippingConfig) => axios.put<any, ApiResponse<ShippingConfig>>(`/system/shipping-config/${id}`, data),
  remove: (id: number) => axios.delete<any, ApiResponse<void>>(`/system/shipping-config/${id}`)
}

export const systemStatusApi = {
  get: () => axios.get<any, ApiResponse<{ checks: SystemCheck[]; unpaidTimeoutHours: number; ecpayExpireDays: number }>>('/system/status')
}
