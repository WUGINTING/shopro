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

/**
 * 操作日誌 API 服務
 * @namespace operationLogApi
 */
export const operationLogApi = {
  /**
   * 取得日誌詳情
   * @description 查詢指定 ID 的操作日誌詳細資訊
   * @param {number} id - 日誌 ID
   * @returns {Promise<ApiResponse<OperationLog>>} 日誌資料
   * @swagger GET /api/system/operation-logs/{id}
   * @example
   * const log = await operationLogApi.getLog(123)
   */
  getLog: (id: number) => {
    return axios.get<any, ApiResponse<OperationLog>>(`/system/operation-logs/${id}`)
  },
  
  /**
   * 分頁查詢所有日誌
   * @description 分頁查詢系統操作日誌
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs
   * @example
   * const logs = await operationLogApi.listLogs(0, 20)
   */
  listLogs: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs', {
      params: { page, size }
    })
  },
  
  /**
   * 依使用者查詢日誌
   * @description 查詢指定使用者的操作日誌
   * @param {number} userId - 使用者 ID
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/user/{userId}
   * @example
   * const logs = await operationLogApi.listByUser(123, 0, 20)
   */
  listByUser: (userId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}`, {
      params: { page, size }
    })
  },
  
  /**
   * 依操作類型查詢
   * @description 查詢指定操作類型的日誌
   * @param {string} operationType - 操作類型
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/operation-type/{operationType}
   * @example
   * const logs = await operationLogApi.listByOperationType('CREATE', 0, 20)
   */
  listByOperationType: (operationType: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/operation-type/${operationType}`, {
      params: { page, size }
    })
  },
  
  /**
   * 依模塊查詢
   * @description 查詢指定模塊的操作日誌
   * @param {string} module - 模塊名稱
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/module/{module}
   * @example
   * const logs = await operationLogApi.listByModule('PRODUCT', 0, 20)
   */
  listByModule: (module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/module/${module}`, {
      params: { page, size }
    })
  },
  
  /**
   * 查詢敏感操作
   * @description 查詢標記為敏感的操作日誌
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/sensitive
   * @example
   * const sensitive = await operationLogApi.listSensitiveOperations(0, 20)
   */
  listSensitiveOperations: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/sensitive', {
      params: { page, size }
    })
  },
  
  /**
   * 依時間範圍查詢
   * @description 查詢指定時間範圍內的操作日誌
   * @param {string} start - 開始時間（ISO 8601 格式）
   * @param {string} end - 結束時間（ISO 8601 格式）
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/date-range
   * @example
   * const logs = await operationLogApi.listByDateRange('2026-01-01', '2026-01-31', 0, 20)
   */
  listByDateRange: (start: string, end: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/date-range', {
      params: { start, end, page, size }
    })
  },
  
  /**
   * 依使用者和模組查詢
   * @description 查詢指定使用者在指定模組的操作日誌
   * @param {number} userId - 使用者 ID
   * @param {string} module - 模塊名稱
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<OperationLog>>>} 分頁日誌資料
   * @swagger GET /api/system/operation-logs/user/{userId}/module/{module}
   * @example
   * const logs = await operationLogApi.listByUserAndModule(123, 'PRODUCT', 0, 20)
   */
  listByUserAndModule: (userId: number, module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}/module/${module}`, {
      params: { page, size }
    })
  }
}

export default operationLogApi
