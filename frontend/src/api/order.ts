/**
 * 訂單相關 API
 * @module OrderAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 訂單介面
 * @interface Order
 */
export interface Order {
  /** 訂單 ID */
  id?: number
  /** 訂單編號 */
  orderNumber?: string
  /** 客戶 ID */
  customerId?: number
  /** 客戶名稱 */
  customerName?: string
  /** 訂單總金額 */
  totalAmount: number
  /** 訂單狀態 */
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  /** 配送地址 */
  shippingAddress?: string
  /** 訂單項目 */
  orderItems?: OrderItem[]
  /** 創建時間 */
  createdAt?: string
  /** 更新時間 */
  updatedAt?: string
}

/**
 * 訂單項目介面
 * @interface OrderItem
 */
export interface OrderItem {
  /** 訂單項目 ID */
  id?: number
  /** 訂單 ID */
  orderId?: number
  /** 商品 ID */
  productId: number
  /** 商品名稱 */
  productName?: string
  /** 購買數量 */
  quantity: number
  /** 商品價格 */
  price: number
  /** 小計金額 */
  subtotal?: number
}

/**
 * 訂單 API 服務
 * @namespace orderApi
 */
export const orderApi = {
  /**
   * 獲取訂單列表
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.page] - 頁碼
   * @param {number} [params.size] - 每頁數量
   * @param {string} [params.status] - 訂單狀態篩選
   * @returns {Promise<ApiResponse<Order[]>>} 訂單列表回應
   * @example
   * const response = await orderApi.getOrders({ page: 1, size: 10 })
   */
  getOrders: (params?: any) => {
    return axios.get<any, ApiResponse<Order[]>>('/orders', { params })
  },
  
  /**
   * 獲取單一訂單詳情
   * @param {number} id - 訂單 ID
   * @returns {Promise<ApiResponse<Order>>} 訂單詳情回應
   * @throws {Error} 當訂單不存在時拋出錯誤
   * @example
   * const response = await orderApi.getOrder(123)
   */
  getOrder: (id: number) => {
    return axios.get<any, ApiResponse<Order>>(`/orders/${id}`)
  },
  
  /**
   * 創建新訂單
   * @param {Order} data - 訂單資料
   * @returns {Promise<ApiResponse<Order>>} 創建成功的訂單資料
   * @example
   * const newOrder = await orderApi.createOrder({
   *   customerId: 1,
   *   totalAmount: 1000,
   *   status: 'PENDING'
   * })
   */
  createOrder: (data: Order) => {
    return axios.post<any, ApiResponse<Order>>('/orders', data)
  },
  
  /**
   * 更新訂單狀態
   * @param {number} id - 訂單 ID
   * @param {Order['status']} status - 新的訂單狀態
   * @returns {Promise<ApiResponse<Order>>} 更新後的訂單資料
   * @example
   * const updated = await orderApi.updateOrderStatus(123, 'PROCESSING')
   */
  updateOrderStatus: (id: number, status: Order['status']) => {
    return axios.patch<any, ApiResponse<Order>>(`/orders/${id}/status`, null, {
      params: { status }
    })
  },
  
  /**
   * 取消訂單
   * @param {number} id - 訂單 ID
   * @returns {Promise<ApiResponse<void>>} 取消結果
   * @example
   * await orderApi.cancelOrder(123)
   */
  cancelOrder: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/${id}`)
  }
}

export default orderApi
