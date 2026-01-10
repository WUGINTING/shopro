/**
 * 操作日誌相關 API
 * @module OperationLogAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 操作日誌相關接口
export interface OperationLog {
  id?: number
  userId?: number
  username?: string
  operationType?: string
  module?: string
  description?: string
  targetType?: string
  targetId?: number
  targetName?: string
  requestMethod?: string
  requestUrl?: string
  requestParams?: string
  requestBody?: string
  responseStatus?: number
  responseBody?: string
  ipAddress?: string
  userAgent?: string
  executionTime?: number
  success?: boolean
  errorMessage?: string
  sensitive?: boolean
  notes?: string
  createdAt?: string
}

// 操作日志 API
export const operationLogApi = {
  // 取得日誌詳情
  getLog: (id: number) => {
    return axios.get<any, ApiResponse<OperationLog>>(`/system/operation-logs/${id}`)
  },
  
  // 分頁查詢所有日誌
  listLogs: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs', {
      params: { page, size }
    })
  },
  
  // 依使用者查詢日誌
  listByUser: (userId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}`, {
      params: { page, size }
    })
  },
  
  // 依操作類型查詢
  listByOperationType: (operationType: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/operation-type/${operationType}`, {
      params: { page, size }
    })
  },
  
  // 依模塊查詢
  listByModule: (module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/module/${module}`, {
      params: { page, size }
    })
  },
  
  // 查詢敏感操作
  listSensitiveOperations: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/sensitive', {
      params: { page, size }
    })
  },
  
  // 依時間範圍查詢
  listByDateRange: (start: string, end: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/date-range', {
      params: { start, end, page, size }
    })
  },
  
  // 依使用者和模組查詢
  listByUserAndModule: (userId: number, module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}/module/${module}`, {
      params: { page, size }
    })
  }
}

export default operationLogApi
