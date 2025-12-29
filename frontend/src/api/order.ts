import axios from './axios'
import type { ApiResponse } from './types'

// 訂單相關接口
export interface Order {
  id?: number
  orderNumber?: string
  customerId?: number
  customerName?: string
  totalAmount: number
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  shippingAddress?: string
  orderItems?: OrderItem[]
  createdAt?: string
  updatedAt?: string
}

export interface OrderItem {
  id?: number
  orderId?: number
  productId: number
  productName?: string
  quantity: number
  price: number
  subtotal?: number
}

// 訂單 API
export const orderApi = {
  // 獲取訂單列表
  getOrders: (params?: any) => {
    return axios.get<any, ApiResponse<Order[]>>('/orders', { params })
  },
  
  // 獲取訂單詳情
  getOrder: (id: number) => {
    return axios.get<any, ApiResponse<Order>>(`/orders/${id}`)
  },
  
  // 創建訂單
  createOrder: (data: Order) => {
    return axios.post<any, ApiResponse<Order>>('/orders', data)
  },
  
  // 更新訂單狀態
  updateOrderStatus: (id: number, status: Order['status']) => {
    return axios.patch<any, ApiResponse<Order>>(`/orders/${id}/status`, null, {
      params: { status }
    })
  },
  
  // 取消訂單
  cancelOrder: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/${id}`)
  }
}

export default orderApi
