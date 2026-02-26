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

export type PaymentGatewayType = 'LINE_PAY' | 'ECPAY' | 'MANUAL'

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
 * @description 獲取支付相關統計數據（今日/本月金額、成功率、退款統計、閘道佔比等）
 * @returns {Promise<ApiResponse<PaymentStatistics>>} 支付統計數據
 * @swagger GET /api/payment-management/statistics
 * @example
 * const stats = await getPaymentStatistics()
 * console.log(stats.data.todayAmount) // 今日交易金額
 * console.log(stats.data.todaySuccessRate) // 今日成功率
 */
export const getPaymentStatistics = (): Promise<ApiResponse<PaymentStatistics>> => {
  return axiosInstance.get('/payment-management/statistics')
}

/**
 * 查詢交易記錄
 * @description 根據多個條件查詢支付交易記錄
 * @param {Object} params - 查詢參數
 * @param {string} [params.keyword] - 關鍵字搜尋（訂單編號、交易 ID）
 * @param {string} [params.gateway] - 支付閘道篩選 (LINE_PAY | ECPAY | MANUAL)
 * @param {string} [params.status] - 交易狀態篩選
 * @param {string} [params.startDate] - 起始日期 (YYYY-MM-DD)
 * @param {string} [params.endDate] - 結束日期 (YYYY-MM-DD)
 * @returns {Promise<ApiResponse<PaymentTransaction[]>>} 交易記錄列表
 * @swagger GET /api/payment-management/transactions
 * @example
 * const transactions = await searchTransactions({
 *   gateway: 'LINE_PAY',
 *   status: 'SUCCESS',
 *   startDate: '2026-01-01',
 *   endDate: '2026-01-31'
 * })
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
 * @description 根據交易 ID 獲取單筆交易的完整資訊
 * @param {number} id - 交易 ID
 * @returns {Promise<ApiResponse<PaymentTransaction>>} 交易詳情
 * @swagger GET /api/payment-management/transactions/{id}
 * @example
 * const transaction = await getTransaction(123)
 * console.log(transaction.data.orderNumber)
 */
export const getTransaction = (id: number): Promise<ApiResponse<PaymentTransaction>> => {
  return axiosInstance.get(`/payment-management/transactions/${id}`)
}

/**
 * 取得所有支付設定
 * @description 獲取所有支付閘道的設定資訊（LINE PAY、ECPay、手動支付）
 * @returns {Promise<ApiResponse<PaymentSetting[]>>} 支付設定列表
 * @swagger GET /api/payment-management/settings
 * @example
 * const settings = await getAllPaymentSettings()
 * settings.data.forEach(s => console.log(s.gateway, s.enabled))
 */
export const getAllPaymentSettings = (): Promise<ApiResponse<PaymentSetting[]>> => {
  return axiosInstance.get('/payment-management/settings')
}

/**
 * 取得單一支付設定
 * @description 根據閘道類型獲取特定支付設定
 * @param {string} gateway - 支付閘道 (LINE_PAY | ECPAY | MANUAL)
 * @returns {Promise<ApiResponse<PaymentSetting>>} 支付設定詳情
 * @swagger GET /api/payment-management/settings/{gateway}
 * @example
 * const linePaySetting = await getPaymentSetting('LINE_PAY')
 */
export const getPaymentSetting = (gateway: string): Promise<ApiResponse<PaymentSetting>> => {
  return axiosInstance.get(`/payment-management/settings/${gateway}`)
}

/**
 * 更新支付設定
 * @description 更新支付閘道的設定（啟用/停用、手續費率、維護模式等）
 * @param {PaymentSetting} setting - 支付設定資料
 * @returns {Promise<ApiResponse<PaymentSetting>>} 更新後的設定
 * @swagger PUT /api/payment-management/settings
 * @example
 * const updated = await updatePaymentSetting({
 *   id: 1,
 *   gateway: 'LINE_PAY',
 *   enabled: true,
 *   commissionRate: 2.5,
 *   maintenanceMode: false
 * })
 */
export const updatePaymentSetting = (setting: PaymentSetting): Promise<ApiResponse<PaymentSetting>> => {
  return axiosInstance.put('/payment-management/settings', setting)
}

/**
 * 檢查閘道可用性
 * @description 檢查指定支付閘道是否可用（考慮啟用狀態和維護模式）
 * @param {string} gateway - 支付閘道 (LINE_PAY | ECPAY | MANUAL)
 * @returns {Promise<ApiResponse<boolean>>} 是否可用
 * @swagger GET /api/payment-management/settings/{gateway}/availability
 * @example
 * const isAvailable = await checkGatewayAvailability('LINE_PAY')
 * if (isAvailable.data) {
 *   console.log('LINE PAY 可用')
 * }
 */
export const checkGatewayAvailability = (gateway: string): Promise<ApiResponse<boolean>> => {
  return axiosInstance.get(`/payment-management/settings/${gateway}/availability`)
}

/**
 * 創建支付請求
 * @description 創建新的支付請求，返回支付 URL 供用戶完成支付
 * @param {string} gateway - 支付閘道 (LINE_PAY | ECPAY)
 * @param {PaymentRequest} request - 支付請求資料
 * @returns {Promise<ApiResponse<PaymentResponse>>} 支付回應（含支付 URL）
 * @swagger POST /api/payment-gateway/create
 * @example
 * const payment = await createPayment('LINE_PAY', {
 *   orderNumber: 'ORD20260110001',
 *   amount: 1000,
 *   productName: '商品名稱',
 *   customerName: '王小明',
 *   customerEmail: 'customer@example.com'
 * })
 * window.location.href = payment.data.paymentUrl // 導向支付頁面
 */
export const createPayment = (gateway: string, request: PaymentRequest): Promise<ApiResponse<PaymentResponse>> => {
  return axiosInstance.post(`/payment-gateway/create?gateway=${gateway}`, request)
}

export const createEcPayPayment = (request: PaymentRequest): Promise<ApiResponse<PaymentResponse>> => {
  return createPayment('ECPAY', request)
}

/**
 * 查詢支付狀態
 * @description 查詢指定交易的當前支付狀態
 * @param {string} gateway - 支付閘道 (LINE_PAY | ECPAY)
 * @param {string} transactionId - 交易 ID
 * @returns {Promise<ApiResponse<PaymentResponse>>} 支付狀態回應
 * @swagger GET /api/payment-gateway/query/{gateway}/{transactionId}
 * @example
 * const status = await queryPaymentStatus('LINE_PAY', 'TXN123456')
 * console.log(status.data.status) // SUCCESS | FAILED | PROCESSING
 */
export const queryPaymentStatus = (gateway: string, transactionId: string): Promise<ApiResponse<PaymentResponse>> => {
  return axiosInstance.get(`/payment-gateway/query/${gateway}/${transactionId}`)
}
