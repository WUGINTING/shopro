import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 訂單折扣相關接口
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

// 訂單折扣 API
export const orderDiscountApi = {
  // 新增訂單折扣
  addDiscount: (data: OrderDiscount) => {
    return axios.post<any, ApiResponse<OrderDiscount>>('/orders/discounts', data)
  },
  
  // 取得訂單的所有折扣
  getDiscountsByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/order/${orderId}`)
  },
  
  // 根據折扣代碼查詢
  findByDiscountCode: (discountCode: string) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/code/${discountCode}`)
  },
  
  // 刪除訂單折扣
  deleteDiscount: (discountId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/discounts/${discountId}`)
  }
}

export default orderDiscountApi
