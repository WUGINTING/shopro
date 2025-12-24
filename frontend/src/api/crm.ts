import axios from './axios'
import type { ApiResponse } from './product'

// CRM 顾客相关接口
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

// CRM API
export const crmApi = {
  // 获取客户列表
  getCustomers: (params?: any) => {
    return axios.get<any, ApiResponse<Customer[]>>('/crm/members', { params })
  },
  
  // 获取客户详情
  getCustomer: (id: number) => {
    return axios.get<any, ApiResponse<Customer>>(`/crm/members/${id}`)
  },
  
  // 创建客户
  createCustomer: (data: Customer) => {
    return axios.post<any, ApiResponse<Customer>>('/crm/members', data)
  },
  
  // 更新客户
  updateCustomer: (id: number, data: Customer) => {
    return axios.put<any, ApiResponse<Customer>>(`/crm/members/${id}`, data)
  },
  
  // 获取客户分组
  getCustomerGroups: () => {
    return axios.get<any, ApiResponse<CustomerGroup[]>>('/crm/member-groups')
  },
  
  // 增加客户积分
  addCustomerPoints: (id: number, points: number) => {
    return axios.post<any, ApiResponse<Customer>>(`/crm/members/${id}/points/add`, null, {
      params: { points }
    })
  },
  
  // 扣除客户积分
  deductCustomerPoints: (id: number, points: number) => {
    return axios.post<any, ApiResponse<Customer>>(`/crm/members/${id}/points/deduct`, null, {
      params: { points }
    })
  }
}

export default crmApi
