/**
 * 訂單折扣相關 API
 * @module OrderDiscountAPI
 */

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

/**
 * 訂單折扣 API 服務
 * @namespace orderDiscountApi
 */
export const orderDiscountApi = {
  /**
   * 新增訂單折扣
   * @description 為指定訂單添加折扣資訊
   * @param {OrderDiscount} data - 折扣資料
   * @param {number} data.orderId - 訂單 ID（必填）
   * @param {string} data.discountType - 折扣類型（必填）
   * @param {number} data.discountAmount - 折扣金額（必填）
   * @returns {Promise<ApiResponse<OrderDiscount>>} 創建成功的折扣資料
   * @swagger POST /api/orders/discounts
   * @example
   * const discount = await orderDiscountApi.addDiscount({
   *   orderId: 123,
   *   discountType: 'COUPON',
   *   discountAmount: 100
   * })
   */
  addDiscount: (data: OrderDiscount) => {
    return axios.post<any, ApiResponse<OrderDiscount>>('/orders/discounts', data)
  },
  
  /**
   * 取得所有折扣
   * @description 取得所有折扣記錄
   * @returns {Promise<ApiResponse<OrderDiscount[]>>} 折扣列表
   * @swagger GET /api/orders/discounts
   * @example
   * const discounts = await orderDiscountApi.getAllDiscounts()
   */
  getAllDiscounts: () => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>('/orders/discounts')
  },
  
  /**
   * 取得訂單的所有折扣
   * @description 查詢指定訂單的所有折扣記錄
   * @param {number} orderId - 訂單 ID
   * @returns {Promise<ApiResponse<OrderDiscount[]>>} 折扣列表
   * @swagger GET /api/orders/discounts/order/{orderId}
   * @example
   * const discounts = await orderDiscountApi.getDiscountsByOrderId(123)
   */
  getDiscountsByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/order/${orderId}`)
  },
  
  /**
   * 根據折扣代碼查詢
   * @description 使用折扣代碼查詢相關的折扣記錄
   * @param {string} discountCode - 折扣代碼
   * @returns {Promise<ApiResponse<OrderDiscount[]>>} 折扣列表
   * @swagger GET /api/orders/discounts/code/{discountCode}
   * @example
   * const discounts = await orderDiscountApi.findByDiscountCode('SAVE100')
   */
  findByDiscountCode: (discountCode: string) => {
    return axios.get<any, ApiResponse<OrderDiscount[]>>(`/orders/discounts/code/${discountCode}`)
  },
  
  /**
   * 更新訂單折扣
   * @description 更新指定折扣記錄的資訊
   * @param {number} discountId - 折扣記錄 ID
   * @param {OrderDiscount} data - 折扣資料
   * @returns {Promise<ApiResponse<OrderDiscount>>} 更新後的折扣資料
   * @swagger PUT /api/orders/discounts/{discountId}
   * @example
   * const discount = await orderDiscountApi.updateDiscount(456, {
   *   orderId: 123,
   *   discountType: 'COUPON',
   *   discountAmount: 150
   * })
   */
  updateDiscount: (discountId: number, data: OrderDiscount) => {
    return axios.put<any, ApiResponse<OrderDiscount>>(`/orders/discounts/${discountId}`, data)
  },
  
  /**
   * 刪除訂單折扣
   * @description 刪除指定的訂單折扣記錄
   * @param {number} discountId - 折扣 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/orders/discounts/{discountId}
   * @example
   * await orderDiscountApi.deleteDiscount(456)
   */
  deleteDiscount: (discountId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/discounts/${discountId}`)
  }
}

export default orderDiscountApi
