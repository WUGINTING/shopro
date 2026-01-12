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
  /** 客戶電話 */
  customerPhone?: string
  /** 客戶電子郵件 */
  customerEmail?: string
  /** 訂單總金額 */
  totalAmount: number
  /** 訂單狀態 */
  status: 'PENDING' | 'PENDING_PAYMENT' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED'
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
  /** 商品規格 ID（關聯到 ProductSpecification） */
  specificationId?: number
  /** 商品名稱 */
  productName?: string
  /** 商品 SKU */
  productSku?: string
  /** 商品規格 */
  productSpec?: string
  /** 購買數量 */
  quantity: number
  /** 商品單價 */
  unitPrice: number
  /** 商品價格（兼容舊版） */
  price?: number
  /** 小計金額 */
  subtotal?: number
  /** 小計金額（後端字段） */
  subtotalAmount?: number
  /** 折扣金額 */
  discountAmount?: number
  /** 實際金額 */
  actualAmount?: number
}

/**
 * 訂單 API 服務
 * @namespace orderApi
 */
export const orderApi = {
  /**
   * 分頁查詢訂單
   * @description 支援分頁查詢訂單列表，可依狀態篩選
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.page] - 頁碼（從 0 開始）
   * @param {number} [params.size] - 每頁數量（預設 20）
   * @param {string} [params.status] - 訂單狀態篩選 (PENDING | PROCESSING | SHIPPED | DELIVERED | CANCELLED)
   * @returns {Promise<ApiResponse<Order[]>>} 訂單列表回應
   * @swagger GET /api/orders
   * @example
   * const response = await orderApi.getOrders({ page: 0, size: 10, status: 'PENDING' })
   */
  getOrders: (params?: any) => {
    return axios.get<any, ApiResponse<Order[]>>('/orders', { params })
  },
  
  /**
   * 取得訂單詳情
   * @description 根據訂單 ID 獲取完整訂單資訊（包含訂單項目）
   * @param {number} id - 訂單 ID
   * @returns {Promise<ApiResponse<Order>>} 訂單詳情回應
   * @throws {Error} 當訂單不存在時拋出 404 錯誤
   * @swagger GET /api/orders/{id}
   * @example
   * const response = await orderApi.getOrder(123)
   * console.log(response.data.orderNumber) // 訂單編號
   */
  getOrder: (id: number) => {
    return axios.get<any, ApiResponse<Order>>(`/orders/${id}`)
  },
  
  /**
   * 創建訂單
   * @description 創建新訂單，預設狀態為 PENDING
   * @param {Order} data - 訂單資料
   * @param {number} data.customerId - 客戶 ID（必填）
   * @param {number} data.totalAmount - 訂單總金額（必填）
   * @param {OrderItem[]} [data.orderItems] - 訂單項目列表
   * @returns {Promise<ApiResponse<Order>>} 創建成功的訂單資料
   * @swagger POST /api/orders
   * @example
   * const newOrder = await orderApi.createOrder({
   *   customerId: 1,
   *   totalAmount: 1000,
   *   status: 'PENDING',
   *   orderItems: [{ productId: 1, quantity: 2, price: 500 }]
   * })
   */
  createOrder: (data: Order) => {
    return axios.post<any, ApiResponse<Order>>('/orders', data)
  },
  
  /**
   * 更新訂單
   * @description 更新訂單資料（包含訂單項目）
   * @param {number} id - 訂單 ID
   * @param {Order} data - 訂單資料
   * @returns {Promise<ApiResponse<Order>>} 更新後的訂單資料
   * @swagger PUT /api/orders/{id}
   * @example
   * const updated = await orderApi.updateOrder(123, {
   *   customerId: 1,
   *   totalAmount: 1000,
   *   status: 'PROCESSING',
   *   items: [{ productId: 1, quantity: 2, unitPrice: 500 }]
   * })
   */
  updateOrder: (id: number, data: Order) => {
    return axios.put<any, ApiResponse<Order>>(`/orders/${id}`, data)
  },
  
  /**
   * 更新訂單狀態
   * @description 更新指定訂單的狀態（PENDING → PROCESSING → SHIPPED → DELIVERED）
   * @param {number} id - 訂單 ID
   * @param {Order['status']} status - 新的訂單狀態
   * @returns {Promise<ApiResponse<Order>>} 更新後的訂單資料
   * @swagger PATCH /api/orders/{id}/status
   * @example
   * const updated = await orderApi.updateOrderStatus(123, 'PROCESSING')
   * console.log(updated.data.status) // 'PROCESSING'
   */
  updateOrderStatus: (id: number, status: Order['status']) => {
    return axios.patch<any, ApiResponse<Order>>(`/orders/${id}/status`, null, {
      params: { status }
    })
  },
  
  /**
   * 刪除訂單
   * @description 刪除指定訂單（建議使用 updateOrderStatus 設為 CANCELLED 代替）
   * @param {number} id - 訂單 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/orders/{id}
   * @warning 此操作無法復原
   * @example
   * await orderApi.deleteOrder(123)
   */
  deleteOrder: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/${id}`)
  },

  /**
   * 多條件搜索訂單
   * @description 根據多條件篩選訂單（訂單編號、客戶、狀態、日期、金額範圍等）
   * @param {OrderQueryParams} params - 查詢參數
   * @returns {Promise<ApiResponse<PageResponse<Order>>>} 訂單列表回應
   * @swagger POST /api/orders/search
   * @example
   * const response = await orderApi.searchOrders({
   *   orderNumber: 'ORD20260112',
   *   status: 'PAID',
   *   startDate: '2026-01-01',
   *   endDate: '2026-01-31',
   *   minAmount: 100,
   *   maxAmount: 1000,
   *   page: 0,
   *   size: 20
   * })
   */
  searchOrders: (params: OrderQueryParams) => {
    return axios.post<any, ApiResponse<PageResponse<Order>>>('/orders/search', params)
  }
}

/**
 * 訂單查詢參數接口
 */
export interface OrderQueryParams {
  /** 訂單 ID */
  orderId?: number
  /** 訂單編號 */
  orderNumber?: string
  /** 客戶 ID */
  customerId?: number
  /** 客戶姓名 */
  customerName?: string
  /** 訂單狀態 */
  status?: Order['status']
  /** 開始日期 */
  startDate?: string
  /** 結束日期 */
  endDate?: string
  /** 最小金額 */
  minAmount?: number
  /** 最大金額 */
  maxAmount?: number
  /** 是否暫存訂單 */
  isDraft?: boolean
  /** 門市 ID */
  storeId?: number
  /** 頁碼 */
  page?: number
  /** 每頁數量 */
  size?: number
}

export default orderApi
