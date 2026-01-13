/**
 * 管理人員行事曆相關 API
 * @module CalendarAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

export type CalendarEventType = 'PRODUCT_LISTING' | 'MARKETING_ACTIVITY' | 'SPECIAL_EVENT' | 'ORDER_DEADLINE'

export type EntityType = 'PRODUCT' | 'MARKETING_CAMPAIGN' | 'PROMOTION' | 'ORDER'

export interface CalendarEvent {
  id?: number
  type: CalendarEventType
  entityId: number
  entityType: EntityType
  startTime: string
  endTime: string
  title: string
  description?: string
  color?: string
  createdBy?: number
  createdAt?: string
  updatedBy?: number
  updatedAt?: string
}

export interface CalendarEventResponse {
  data: CalendarEvent[]
  total: number
  page: number
  pageSize: number
}

export interface CalendarConflict {
  conflictType: string
  description: string
  conflictingEventIds: number[]
  conflictingEventTitles: string[]
  suggestion: string
}

export interface CalendarPreview {
  previewTime: string
  listedProducts: ProductPreview[]
  activeActivities: ActivityPreview[]
  impactDescription: string
}

export interface ProductPreview {
  productId: number
  productName: string
  sku: string
  stock: number
}

export interface ActivityPreview {
  activityId: number
  activityName: string
  activityType: string
}

export interface BatchUpdateRequest {
  eventIds: number[]
  newStartTime: string
  newEndTime: string
  updateRelatedProducts?: boolean
  updateRelatedActivities?: boolean
}

/**
 * 行事曆 API 服務
 * @namespace calendarApi
 */
const calendarApi = {
  /**
   * 獲取行事曆事件
   * @description 分頁查詢或條件查詢行事曆事件
   * @param {Object} params - 查詢參數
   * @param {number} [params.page=1] - 頁碼
   * @param {number} [params.pageSize=20] - 每頁數量
   * @param {CalendarEventType} [params.type] - 事件類型
   * @param {EntityType} [params.entityType] - 實體類型
   * @param {number} [params.entityId] - 實體ID
   * @param {string} [params.startTime] - 開始時間
   * @param {string} [params.endTime] - 結束時間
   * @param {string} [params.keyword] - 關鍵字
   * @returns {Promise<CalendarEventResponse>} 事件分頁資料
   */
  getEvents: async (params?: {
    page?: number
    pageSize?: number
    type?: CalendarEventType
    entityType?: EntityType
    entityId?: number
    startTime?: string
    endTime?: string
    keyword?: string
  }) => {
    const response = await axiosInstance.get<any, ApiResponse<CalendarEventResponse>>(
      '/admin/calendar/events',
      { params }
    )
    return response.data
  },

  /**
   * 獲取事件詳情
   * @param {number} id - 事件ID
   * @returns {Promise<CalendarEvent>} 事件資料
   */
  getEvent: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<CalendarEvent>>(
      `/admin/calendar/events/${id}`
    )
    return response.data
  },

  /**
   * 創建事件
   * @param {CalendarEvent} event - 事件資料
   * @returns {Promise<CalendarEvent>} 創建的事件
   */
  createEvent: async (event: Omit<CalendarEvent, 'id' | 'createdAt' | 'updatedAt'>) => {
    const response = await axiosInstance.post<any, ApiResponse<CalendarEvent>>(
      '/admin/calendar/events',
      event
    )
    return response.data
  },

  /**
   * 更新事件
   * @param {number} id - 事件ID
   * @param {CalendarEvent} event - 更新的事件資料
   * @returns {Promise<CalendarEvent>} 更新後的事件
   */
  updateEvent: async (id: number, event: Partial<CalendarEvent>) => {
    const response = await axiosInstance.put<any, ApiResponse<CalendarEvent>>(
      `/admin/calendar/events/${id}`,
      event
    )
    return response.data
  },

  /**
   * 刪除事件
   * @param {number} id - 事件ID
   */
  deleteEvent: async (id: number) => {
    await axiosInstance.delete<any, ApiResponse<void>>(`/admin/calendar/events/${id}`)
  },

  /**
   * 檢查時間衝突
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.eventId] - 事件ID
   * @param {string} [params.startTime] - 開始時間
   * @param {string} [params.endTime] - 結束時間
   * @param {EntityType} [params.entityType] - 實體類型
   * @returns {Promise<CalendarConflict[]>} 衝突列表
   */
  checkConflicts: async (params?: {
    eventId?: number
    startTime?: string
    endTime?: string
    entityType?: EntityType
  }) => {
    const response = await axiosInstance.get<any, ApiResponse<CalendarConflict[]>>(
      '/admin/calendar/conflicts',
      { params }
    )
    return response.data
  },

  /**
   * 預覽時間變更效果
   * @param {string} previewTime - 預覽時間點
   * @returns {Promise<CalendarPreview>} 預覽資料
   */
  preview: async (previewTime: string) => {
    const response = await axiosInstance.get<any, ApiResponse<CalendarPreview>>(
      '/admin/calendar/preview',
      { params: { previewTime } }
    )
    return response.data
  },

  /**
   * 批量更新事件
   * @param {BatchUpdateRequest} request - 批量更新請求
   * @returns {Promise<CalendarEvent[]>} 更新後的事件列表
   */
  batchUpdate: async (request: BatchUpdateRequest) => {
    const response = await axiosInstance.post<any, ApiResponse<CalendarEvent[]>>(
      '/admin/calendar/batch-update',
      request
    )
    return response.data
  },

  /**
   * 批量刪除事件
   * @param {number[]} eventIds - 事件ID列表
   */
  batchDelete: async (eventIds: number[]) => {
    await axiosInstance.post<any, ApiResponse<void>>(
      '/admin/calendar/batch-delete',
      eventIds
    )
  }
}

export default calendarApi

