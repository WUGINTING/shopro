/**
 * 物流相關 API
 * @module ShipmentAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 物流記錄介面
 * @interface OrderShipment
 */
export interface OrderShipment {
  /** 物流記錄 ID */
  id?: number
  /** 訂單 ID */
  orderId: number
  /** 物流狀態 */
  shippingStatus: 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'RETURNED'
  /** 物流公司 */
  shippingCompany?: string
  /** 物流單號 */
  trackingNumber?: string
  /** 出貨時間 */
  shippedAt?: string
  /** 送達時間 */
  deliveredAt?: string
  /** 收件人姓名 */
  recipientName?: string
  /** 收件人電話 */
  recipientPhone?: string
  /** 收件地址 */
  recipientAddress?: string
  /** 備註 */
  notes?: string
  /** 創建時間 */
  createdAt?: string
}

/**
 * 物流 API 服務
 * @namespace shipmentApi
 */
export const shipmentApi = {
  /**
   * 創建物流記錄
   * @description 為訂單創建物流記錄
   * @param {OrderShipment} data - 物流資料
   * @returns {Promise<ApiResponse<OrderShipment>>} 創建成功的物流記錄
   * @swagger POST /api/orders/shipments
   */
  createShipment: (data: OrderShipment) => {
    return axios.post<any, ApiResponse<OrderShipment>>('/orders/shipments', data)
  },

  /**
   * 更新物流狀態
   * @description 更新指定物流記錄的狀態
   * @param {number} shipmentId - 物流記錄 ID
   * @param {'PENDING' | 'SHIPPED' | 'DELIVERED' | 'RETURNED'} status - 物流狀態
   * @returns {Promise<ApiResponse<OrderShipment>>} 更新後的物流記錄
   * @swagger PATCH /api/orders/shipments/{shipmentId}/status
   */
  updateShippingStatus: (shipmentId: number, status: OrderShipment['shippingStatus']) => {
    return axios.patch<any, ApiResponse<OrderShipment>>(
      `/orders/shipments/${shipmentId}/status`,
      null,
      { params: { status } }
    )
  },

  /**
   * 更新物流單號
   * @description 更新指定物流記錄的物流單號
   * @param {number} shipmentId - 物流記錄 ID
   * @param {string} trackingNumber - 物流單號
   * @returns {Promise<ApiResponse<OrderShipment>>} 更新後的物流記錄
   * @swagger PATCH /api/orders/shipments/{shipmentId}/tracking
   */
  updateTrackingNumber: (shipmentId: number, trackingNumber: string) => {
    return axios.patch<any, ApiResponse<OrderShipment>>(
      `/orders/shipments/${shipmentId}/tracking`,
      null,
      { params: { trackingNumber } }
    )
  },

  /**
   * 取得訂單的物流記錄
   * @description 查詢指定訂單的所有物流記錄
   * @param {number} orderId - 訂單 ID
   * @returns {Promise<ApiResponse<OrderShipment[]>>} 物流記錄列表
   * @swagger GET /api/orders/shipments/order/{orderId}
   */
  getShipmentsByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderShipment[]>>(`/orders/shipments/order/${orderId}`)
  },

  /**
   * 根據物流單號查詢
   * @description 根據物流單號查詢物流記錄
   * @param {string} trackingNumber - 物流單號
   * @returns {Promise<ApiResponse<OrderShipment>>} 物流記錄
   * @swagger GET /api/orders/shipments/tracking/{trackingNumber}
   */
  findByTrackingNumber: (trackingNumber: string) => {
    return axios.get<any, ApiResponse<OrderShipment>>(`/orders/shipments/tracking/${trackingNumber}`)
  }
}

