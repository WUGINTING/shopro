/**
 * 支付回調記錄相關 API
 * @module PaymentCallbackLogAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

/**
 * 支付回調記錄介面
 */
export interface PaymentCallbackLog {
  /** 記錄 ID */
  id?: number
  /** 支付閘道類型 */
  gateway: string
  /** 訂單編號 */
  orderNumber?: string
  /** 交易 ID */
  transactionId?: string
  /** 回調狀態 */
  status: string
  /** 原始請求參數（JSON） */
  rawParams?: string
  /** 解析後的響應數據（JSON） */
  parsedResponse?: string
  /** 處理結果 */
  processResult?: string
  /** 錯誤訊息 */
  errorMessage?: string
  /** 請求 IP */
  requestIp?: string
  /** User-Agent */
  userAgent?: string
  /** 處理時間（毫秒） */
  processTimeMs?: number
  /** 建立時間 */
  createdAt?: string
}

/**
 * 支付回調記錄 API 服務
 */
export const paymentCallbackLogApi = {
  /**
   * 取得回調記錄列表（分頁）
   */
  getCallbackLogs: (params?: { page?: number; size?: number }) => {
    return axios.get<any, ApiResponse<PageResponse<PaymentCallbackLog>>>('/payment/callback-logs', { params })
  },

  /**
   * 根據 ID 取得回調記錄詳情
   */
  getCallbackLog: (id: number) => {
    return axios.get<any, ApiResponse<PaymentCallbackLog>>(`/payment/callback-logs/${id}`)
  },

  /**
   * 根據訂單編號查詢回調記錄
   */
  getCallbackLogsByOrderNumber: (orderNumber: string) => {
    return axios.get<any, ApiResponse<PaymentCallbackLog[]>>(`/payment/callback-logs/order/${orderNumber}`)
  },

  /**
   * 根據交易 ID 查詢回調記錄
   */
  getCallbackLogsByTransactionId: (transactionId: string) => {
    return axios.get<any, ApiResponse<PaymentCallbackLog[]>>(`/payment/callback-logs/transaction/${transactionId}`)
  },

  /**
   * 根據支付閘道類型查詢回調記錄
   */
  getCallbackLogsByGateway: (gateway: string, params?: { page?: number; size?: number }) => {
    return axios.get<any, ApiResponse<PageResponse<PaymentCallbackLog>>>(`/payment/callback-logs/gateway/${gateway}`, { params })
  }
}

export default paymentCallbackLogApi

