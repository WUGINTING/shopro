/**
 * 訂單問答相關 API
 * @module OrderQAAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 訂嗢Q&A相關接口
export interface OrderQA {
  id?: number
  orderId: number
  askerType: 'CUSTOMER' | 'STORE'
  askerId?: number
  askerName?: string
  question: string
  answer?: string
  answererId?: number
  answererName?: string
  answeredAt?: string
  createdAt?: string
}

/**
 * 訂單問答 API 服務
 * @namespace orderQAApi
 */
export const orderQAApi = {
  /**
   * 新增問題
   * @description 為指定訂單新增問題
   * @param {OrderQA} data - 問題資料
   * @param {number} data.orderId - 訂單 ID（必填）
   * @param {'CUSTOMER' | 'STORE'} data.askerType - 提問者類型（必填）
   * @param {string} data.question - 問題內容（必填）
   * @returns {Promise<ApiResponse<OrderQA>>} 創建成功的問答資料
   * @swagger POST /api/orders/qa
   * @example
   * const qa = await orderQAApi.askQuestion({
   *   orderId: 123,
   *   askerType: 'CUSTOMER',
   *   question: '請問何時出貨？'
   * })
   */
  askQuestion: (data: OrderQA) => {
    return axios.post<any, ApiResponse<OrderQA>>('/orders/qa', data)
  },
  
  /**
   * 回答問題
   * @description 對指定問題進行回答
   * @param {number} qaId - 問答 ID
   * @param {string} answer - 回答內容
   * @param {number} answererId - 回答者 ID
   * @param {string} answererName - 回答者姓名
   * @returns {Promise<ApiResponse<OrderQA>>} 更新後的問答資料
   * @swagger PATCH /api/orders/qa/{qaId}/answer
   * @example
   * const qa = await orderQAApi.answerQuestion(456, '預計明天出貨', 1, '客服人員')
   */
  answerQuestion: (qaId: number, answer: string, answererId: number, answererName: string) => {
    return axios.patch<any, ApiResponse<OrderQA>>(`/orders/qa/${qaId}/answer`, null, {
      params: { answer, answererId, answererName }
    })
  },
  
  /**
   * 取得訂單的所有問答
   * @description 查詢指定訂單的所有問答記錄
   * @param {number} orderId - 訂單 ID
   * @returns {Promise<ApiResponse<OrderQA[]>>} 問答列表
   * @swagger GET /api/orders/qa/order/{orderId}
   * @example
   * const qaList = await orderQAApi.getQAByOrderId(123)
   */
  getQAByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderQA[]>>(`/orders/qa/order/${orderId}`)
  },
  
  /**
   * 分頁取得訂單問答
   * @description 分頁查詢指定訂單的問答記錄
   * @param {number} orderId - 訂單 ID
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OrderQA>>>} 分頁問答資料
   * @swagger GET /api/orders/qa/order/{orderId}/page
   * @example
   * const page = await orderQAApi.getQAByOrderIdPage(123, 0, 10)
   */
  getQAByOrderIdPage: (orderId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OrderQA>>>(`/orders/qa/order/${orderId}/page`, {
      params: { page, size }
    })
  },
  
  /**
   * 取得客戶的所有提問
   * @description 查詢指定客戶的所有提問記錄
   * @param {number} askerId - 提問者 ID
   * @returns {Promise<ApiResponse<OrderQA[]>>} 問答列表
   * @swagger GET /api/orders/qa/asker/{askerId}
   * @example
   * const qaList = await orderQAApi.getQAByAskerId(789)
   */
  getQAByAskerId: (askerId: number) => {
    return axios.get<any, ApiResponse<OrderQA[]>>(`/orders/qa/asker/${askerId}`)
  },
  
  /**
   * 刪除問答
   * @description 刪除指定的問答記錄
   * @param {number} qaId - 問答 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/orders/qa/{qaId}
   * @example
   * await orderQAApi.deleteQA(456)
   */
  deleteQA: (qaId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/qa/${qaId}`)
  }
}

export default orderQAApi
