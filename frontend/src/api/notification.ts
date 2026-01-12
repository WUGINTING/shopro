/**
 * 通知相關 API
 * @module NotificationAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

export interface Notification {
  id?: number
  userId?: number
  type: string
  title: string
  content?: string
  relatedId?: number
  relatedType?: string
  isRead: boolean
  readAt?: string
  priority?: string
  icon?: string
  color?: string
  createdAt?: string
}

/**
 * 通知 API 服務
 */
export const notificationApi = {
  /**
   * 獲取未讀通知
   */
  getUnreadNotifications: () => {
    return axiosInstance.get<any, ApiResponse<Notification[]>>('/notifications/unread')
  },

  /**
   * 獲取未讀通知數量
   */
  getUnreadCount: () => {
    return axiosInstance.get<any, ApiResponse<number>>('/notifications/unread/count')
  },

  /**
   * 標記通知為已讀
   */
  markAsRead: (id: number) => {
    return axiosInstance.put<any, ApiResponse<string>>(`/notifications/${id}/read`)
  },

  /**
   * 標記所有通知為已讀
   */
  markAllAsRead: () => {
    return axiosInstance.put<any, ApiResponse<string>>('/notifications/read-all')
  }
}

export default notificationApi

