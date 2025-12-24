import axios from './axios'
import type { ApiResponse } from './product'

// 订单相关接口
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

// 订单 API
export const orderApi = {
  // 获取订单列表
  getOrders: (params?: any) => {
    return axios.get<any, ApiResponse<Order[]>>('/orders', { params })
  },
  
  // 获取订单详情
  getOrder: (id: number) => {
    return axios.get<any, ApiResponse<Order>>(`/orders/${id}`)
  },
  
  // 创建订单
  createOrder: (data: Order) => {
    return axios.post<any, ApiResponse<Order>>('/orders', data)
  },
  
  // 更新订单状态
  updateOrderStatus: (id: number, status: Order['status']) => {
    return axios.put<any, ApiResponse<Order>>(`/orders/${id}/status`, { status })
  },
  
  // 取消订单
  cancelOrder: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/${id}`)
  }
}

export default orderApi
