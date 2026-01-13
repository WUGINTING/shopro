/**
 * CRM 客戶關係管理 API
 * @module CRMAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * CRM 客戶介面
 * @interface Customer
 */
export interface Customer {
  id?: number
  name: string
  email: string
  phone?: string
  memberLevel?: 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM'
  points?: number
  totalSpent?: number
  registeredAt?: string
}

export interface CustomerGroup {
  id?: number
  name: string
  description?: string
  memberCount?: number
}

/**
 * CRM API 服務
 * @namespace crmApi
 */
export const crmApi = {
  // 獲取客戶列表
  getCustomers: (params?: any) => {
    return axios.get<any, ApiResponse<Customer[]>>('/crm/members', { params })
  },
  
  // 搜索客戶
  searchCustomers: (keyword: string, params?: { page?: number; size?: number }) => {
    return axios.get<any, ApiResponse<Customer[]>>('/crm/members/search', {
      params: { keyword, ...params }
    })
  },
  
  // 獲取客戶詳情
  getCustomer: (id: number) => {
    return axios.get<any, ApiResponse<Customer>>(`/crm/members/${id}`)
  },
  
  // 創建客戶
  createCustomer: (data: Customer) => {
    return axios.post<any, ApiResponse<Customer>>('/crm/members', data)
  },
  
  // 更新客戶
  updateCustomer: (id: number, data: Customer) => {
    return axios.put<any, ApiResponse<Customer>>(`/crm/members/${id}`, data)
  },
  
  // 獲取客戶分組
  getCustomerGroups: () => {
    return axios.get<any, ApiResponse<CustomerGroup[]>>('/crm/member-groups')
  },
  
  // 增加客戶積分
  addCustomerPoints: (id: number, points: number) => {
    return axios.post<any, ApiResponse<Customer>>(`/crm/members/${id}/points/add`, null, {
      params: { points }
    })
  },
  
  // 扣除客戶積分
  deductCustomerPoints: (id: number, points: number) => {
    return axios.post<any, ApiResponse<Customer>>(`/crm/members/${id}/points/deduct`, null, {
      params: { points }
    })
  },

  // 刪除客戶
  deleteCustomer: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/members/${id}`)
  },

  // 重新計算單個客戶的總消費
  recalculateCustomerTotalSpent: (id: number) => {
    return axios.post<any, ApiResponse<Customer>>(`/crm/members/${id}/recalculate-total-spent`)
  },

  // 重新計算所有客戶的總消費
  recalculateAllCustomersTotalSpent: () => {
    return axios.post<any, ApiResponse<string>>('/crm/members/recalculate-all-total-spent')
  }
}

export default crmApi
