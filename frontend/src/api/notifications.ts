/**
 * 管理後台通知相關 API
 * @module NotificationsAPI
 */

import axiosInstance from './axios'

/**
 * 通知類型
 */
export type AdminNotificationType =
  | 'ORDER_CREATED'      // 訂單新增
  | 'PAYMENT_COMPLETED'  // 訂單收款完成
  | 'ORDER_CANCELLED'    // 訂單取消
  | 'ORDER_QA'           // 訂單問答
  | 'STOCK_LOW'          // 商品庫存不足

/**
 * 管理通知 DTO
 */
export interface AdminNotificationDTO {
  id: number
  type: AdminNotificationType
  orderId?: number
  productId?: number
  title: string
  message: string
  createdAt: string
  read: boolean
}

/**
 * 通知 API 服務
 */
const notificationsApi = {
  /**
   * 獲取最新的系統通知
   * @returns {Promise<{ data: AdminNotificationDTO[] }>} 通知列表
   */
  getNotifications: async () => {
    return axiosInstance.get<any, { data: AdminNotificationDTO[] }>('/notifications')
  },

  /**
   * 獲取未讀通知
   * @returns {Promise<{ data: AdminNotificationDTO[] }>} 未讀通知列表
   */
  getUnreadNotifications: async () => {
    return axiosInstance.get<any, { data: AdminNotificationDTO[] }>('/notifications/unread')
  },

  /**
   * 獲取未讀通知數量
   * @returns {Promise<{ data: number }>} 未讀通知數量
   */
  getUnreadCount: async () => {
    return axiosInstance.get<any, { data: number }>('/notifications/count')
  },

  /**
   * 標記通知為已讀
   * @param {number} id - 通知 ID
   */
  markAsRead: async (id: number) => {
    return axiosInstance.put(`/notifications/${id}/read`)
  },

  /**
   * 全部標記為已讀
   */
  markAllAsRead: async () => {
    return axiosInstance.put('/notifications/read-all')
  }
}

export default notificationsApi

