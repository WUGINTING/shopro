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

// 訂單 Q&A API
export const orderQAApi = {
  // 新增問題
  askQuestion: (data: OrderQA) => {
    return axios.post<any, ApiResponse<OrderQA>>('/orders/qa', data)
  },
  
  // 回答问题
  answerQuestion: (qaId: number, answer: string, answererId: number, answererName: string) => {
    return axios.patch<any, ApiResponse<OrderQA>>(`/orders/qa/${qaId}/answer`, null, {
      params: { answer, answererId, answererName }
    })
  },
  
  // 取得訂單的所有問答
  getQAByOrderId: (orderId: number) => {
    return axios.get<any, ApiResponse<OrderQA[]>>(`/orders/qa/order/${orderId}`)
  },
  
  // 分頁取得訂單問答
  getQAByOrderIdPage: (orderId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OrderQA>>>(`/orders/qa/order/${orderId}/page`, {
      params: { page, size }
    })
  },
  
  // 取得客戶的所有提問
  getQAByAskerId: (askerId: number) => {
    return axios.get<any, ApiResponse<OrderQA[]>>(`/orders/qa/asker/${askerId}`)
  },
  
  // 刪除問答
  deleteQA: (qaId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/orders/qa/${qaId}`)
  }
}

export default orderQAApi
