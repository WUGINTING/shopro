/**
 * ECPay 配置相關 API
 * @module EcPayConfigAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * ECPay 配置介面
 */
export interface EcPayConfig {
  /** 配置 ID */
  id?: number
  /** ECPay 商店代號 (MerchantID) */
  merchantId: string
  /** ECPay HashKey */
  hashKey: string
  /** ECPay HashIV */
  hashIV: string
  /** ECPay API Base URL */
  apiUrl: string
  /** 付款完成返回 URL */
  returnUrl?: string
  /** 付款結果通知 URL */
  notifyUrl: string
  /** 是否為測試環境 */
  sandbox: boolean
  /** 是否啟用 */
  enabled: boolean
  /** 備註說明 */
  description?: string
  /** 建立時間 */
  createdAt?: string
  /** 更新時間 */
  updatedAt?: string
}

/**
 * ECPay 配置 API 服務
 */
export const ecpayConfigApi = {
  /**
   * 取得 ECPay 配置
   */
  getConfig: () => {
    return axios.get<any, ApiResponse<EcPayConfig>>('/payment/ecpay/config')
  },

  /**
   * 根據 ID 取得配置
   */
  getConfigById: (id: number) => {
    return axios.get<any, ApiResponse<EcPayConfig>>(`/payment/ecpay/config/${id}`)
  },

  /**
   * 創建 ECPay 配置
   */
  createConfig: (data: EcPayConfig) => {
    return axios.post<any, ApiResponse<EcPayConfig>>('/payment/ecpay/config', data)
  },

  /**
   * 更新 ECPay 配置
   */
  updateConfig: (id: number, data: EcPayConfig) => {
    return axios.put<any, ApiResponse<EcPayConfig>>(`/payment/ecpay/config/${id}`, data)
  },

  /**
   * 刪除 ECPay 配置
   */
  deleteConfig: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/payment/ecpay/config/${id}`)
  }
}

export default ecpayConfigApi

