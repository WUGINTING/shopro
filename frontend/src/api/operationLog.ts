import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 操作日志相关接口
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
  // 取得日志详情
  getLog: (id: number) => {
    return axios.get<any, ApiResponse<OperationLog>>(`/system/operation-logs/${id}`)
  },
  
  // 分页查询所有日志
  listLogs: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs', {
      params: { page, size }
    })
  },
  
  // 依用户查询日志
  listByUser: (userId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}`, {
      params: { page, size }
    })
  },
  
  // 依操作类型查询
  listByOperationType: (operationType: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/operation-type/${operationType}`, {
      params: { page, size }
    })
  },
  
  // 依模块查询
  listByModule: (module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/module/${module}`, {
      params: { page, size }
    })
  },
  
  // 查询敏感操作
  listSensitiveOperations: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/sensitive', {
      params: { page, size }
    })
  },
  
  // 依时间范围查询
  listByDateRange: (start: string, end: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>('/system/operation-logs/date-range', {
      params: { start, end, page, size }
    })
  },
  
  // 依用户和模块查询
  listByUserAndModule: (userId: number, module: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<OperationLog>>>(`/system/operation-logs/user/${userId}/module/${module}`, {
      params: { page, size }
    })
  }
}

export default operationLogApi
