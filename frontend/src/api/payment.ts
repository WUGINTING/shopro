/**
 * 支付相關 API
 * @module PaymentAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

/**
 * 支付統計介面
 * @interface PaymentStatistics
 */
export interface PaymentStatistics {
  todayAmount: number
  todayCount: number
  todaySuccessRate: number
  monthAmount: number
  monthCount: number
  refundStatistics: {
    todayRefundCount: number
    todayRefundAmount: number
    monthRefundCount: number
    monthRefundAmount: number
  }
  gatewayShares: Record<string, {
    gateway: string
    count: number
    amount: number
    percentage: number
  }>
}

/**
 * 支付交易 DTO
 */
export interface PaymentTransaction {
  id: number
  orderId: number
  orderNumber: string
  gateway: 'LINE_PAY' | 'ECPAY' | 'MANUAL'
  transactionId: string
  status: 'INITIATED' | 'PROCESSING' | 'SUCCESS' | 'FAILED' | 'CANCELLED' | 'EXPIRED'
  amount: number
  currency: string
  paymentUrl?: string
  errorMessage?: string
  rawResponse?: string
  createdAt: string
  updatedAt: string
  customerName?: string
  customerEmail?: string
  customerPhone?: string
}

/**
 * 支付設定 DTO
 */
export interface PaymentSetting {
  id: number
  gateway: 'LINE_PAY' | 'ECPAY' | 'MANUAL'
  enabled: boolean
  displayName: string
  description?: string
  commissionRate?: number
  maintenanceMode: boolean
  maintenanceMessage?: string
  sortOrder?: number
  createdAt: string
  updatedAt: string
}

/**
 * 支付請求 DTO
 */
export interface PaymentRequest {
  orderId?: number
  orderNumber: string
  amount: number
  currency?: string
  productName: string
  customerName?: string
  customerEmail?: string
  customerPhone?: string
}

/**
 * 支付回應 DTO
 */
export interface PaymentResponse {
  gateway: string
  status: string
  transactionId: string
  orderNumber: string
  amount: number
  paymentUrl?: string
  webPaymentUrl?: string
  appPaymentUrl?: string
  errorMessage?: string
  rawResponse?: string
}

/**
 * 取得支付統計
 */
export const getPaymentStatistics = (): Promise<ApiResponse<PaymentStatistics>> => {
  return axiosInstance.get('/payment-management/statistics')
}

/**
 * 查詢交易記錄
 */
export const searchTransactions = (params: {
  keyword?: string
  gateway?: string
  status?: string
  startDate?: string
  endDate?: string
}): Promise<ApiResponse<PaymentTransaction[]>> => {
  return axiosInstance.get('/payment-management/transactions', { params })
}

/**
 * 取得交易詳情
 */
export const getTransaction = (id: number): Promise<ApiResponse<PaymentTransaction>> => {
  return axiosInstance.get(`/payment-management/transactions/${id}`)
}

/**
 * 取得所有支付設定
 */
export const getAllPaymentSettings = (): Promise<ApiResponse<PaymentSetting[]>> => {
  return axiosInstance.get('/payment-management/settings')
}

/**
 * 取得單一支付設定
 */
export const getPaymentSetting = (gateway: string): Promise<ApiResponse<PaymentSetting>> => {
  return axiosInstance.get(`/payment-management/settings/${gateway}`)
}

/**
 * 更新支付設定
 */
export const updatePaymentSetting = (setting: PaymentSetting): Promise<ApiResponse<PaymentSetting>> => {
  return axiosInstance.put('/payment-management/settings', setting)
}

/**
 * 檢查閘道可用性
 */
export const checkGatewayAvailability = (gateway: string): Promise<ApiResponse<boolean>> => {
  return axiosInstance.get(`/payment-management/settings/${gateway}/availability`)
}

/**
 * 創建支付請求
 */
export const createPayment = (gateway: string, request: PaymentRequest): Promise<ApiResponse<PaymentResponse>> => {
  return axiosInstance.post(`/payment-gateway/create?gateway=${gateway}`, request)
}

/**
 * 查詢支付狀態
 */
export const queryPaymentStatus = (gateway: string, transactionId: string): Promise<ApiResponse<PaymentResponse>> => {
  return axiosInstance.get(`/payment-gateway/query/${gateway}/${transactionId}`)
}
