import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 订单折扣相关接口
export interface OrderDiscount {
  id?: number
  orderId: number
  discountType: string
  discountCode?: string
  discountAmount: number
  discountPercentage?: number
  description?: string
  createdAt?: string
}

// 订单折扣 API
export const orderDiscountApi = {
  // 新增订单折扣
  addDiscount: (data: OrderDiscount) => {
    return axios.post<any, ApiResponse<OrderDiscount>>('/orders/discounts', data)
  },
  
  // 取得订单的所有折扣
  getDiscountsByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/order/${orderId}`)
  },
  
  // 根据折扣代码查询
  findByDiscountCode: (discountCode: string) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/code/${discountCode}`)
  },
  
  // 删除订单折扣
  deleteDiscount: (discountId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/discounts/${discountId}`)
  }
}

export default orderDiscountApi
