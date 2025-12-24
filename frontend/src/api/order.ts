import apiClient from './client'
import type { ApiResponse, PageResponse, Order, OrderStatistics } from './types'

/**
 * 訂單管理 API
 */
export const orderApi = {
  // 創建訂單
  create: (data: Order) => {
    return apiClient.post<ApiResponse<Order>>('/api/orders', data)
  },

  // 更新訂單
  update: (id: number, data: Order) => {
    return apiClient.put<ApiResponse<Order>>(`/api/orders/${id}`, data)
  },

  // 取得訂單詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<Order>>(`/api/orders/${id}`)
  },

  // 刪除訂單
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/orders/${id}`)
  },

  // 分頁查詢訂單
  list: (page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>('/api/orders', {
      params: { page, size }
    })
  },

  // 根據客戶 ID 查詢訂單
  listByCustomer: (customerId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>(`/api/orders/customer/${customerId}`, {
      params: { page, size }
    })
  },

  // 根據訂單狀態查詢
  listByStatus: (status: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>(`/api/orders/status/${status}`, {
      params: { page, size }
    })
  },

  // 根據付款狀態查詢
  listByPaymentStatus: (paymentStatus: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>(
      `/api/orders/payment-status/${paymentStatus}`,
      {
        params: { page, size }
      }
    )
  },

  // 根據物流狀態查詢
  listByShippingStatus: (shippingStatus: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>(
      `/api/orders/shipping-status/${shippingStatus}`,
      {
        params: { page, size }
      }
    )
  },

  // 根據日期範圍查詢訂單
  listByDateRange: (startDate: string, endDate: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>('/api/orders/date-range', {
      params: { startDate, endDate, page, size }
    })
  },

  // 根據金額範圍查詢訂單
  listByAmountRange: (minAmount: number, maxAmount: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Order>>>('/api/orders/amount-range', {
      params: { minAmount, maxAmount, page, size }
    })
  }
}

/**
 * 訂單批次操作 API
 */
export const orderBatchApi = {
  // 批次更新訂單狀態
  updateStatus: (orderIds: number[], status: string) => {
    return apiClient.post<ApiResponse<void>>('/api/orders/batch/status', {
      orderIds,
      status
    })
  },

  // 批次刪除訂單
  delete: (orderIds: number[]) => {
    return apiClient.post<ApiResponse<void>>('/api/orders/batch/delete', {
      orderIds
    })
  },

  // 批次匯出訂單
  export: (orderIds: number[]) => {
    return apiClient.post<Blob>(
      '/api/orders/batch/export',
      { orderIds },
      {
        responseType: 'blob'
      }
    )
  }
}

/**
 * 訂單統計 API
 */
export const orderStatisticsApi = {
  // 取得訂單統計概覽
  getOverview: (startDate?: string, endDate?: string) => {
    return apiClient.get<ApiResponse<OrderStatistics>>('/api/orders/statistics/overview', {
      params: { startDate, endDate }
    })
  },

  // 取得每日訂單趨勢
  getDailyTrend: (startDate: string, endDate: string) => {
    return apiClient.get<
      ApiResponse<{ date: string; count: number; amount: number }[]>
    >('/api/orders/statistics/daily-trend', {
      params: { startDate, endDate }
    })
  },

  // 取得訂單狀態分布
  getStatusDistribution: () => {
    return apiClient.get<ApiResponse<{ [key: string]: number }>>(
      '/api/orders/statistics/status-distribution'
    )
  },

  // 取得訂單金額分布
  getAmountDistribution: () => {
    return apiClient.get<
      ApiResponse<{ range: string; count: number }[]>
    >('/api/orders/statistics/amount-distribution')
  }
}

/**
 * 訂單折扣 API
 */
export const orderDiscountApi = {
  // 應用折扣到訂單
  apply: (orderId: number, discountData: { type: string; value: number; reason?: string }) => {
    return apiClient.post<ApiResponse<Order>>(`/api/orders/${orderId}/discount`, discountData)
  },

  // 移除訂單折扣
  remove: (orderId: number, discountId: number) => {
    return apiClient.delete<ApiResponse<Order>>(`/api/orders/${orderId}/discount/${discountId}`)
  },

  // 取得訂單的折扣記錄
  list: (orderId: number) => {
    return apiClient.get<
      ApiResponse<{ id: number; type: string; value: number; reason: string }[]>
    >(`/api/orders/${orderId}/discounts`)
  }
}
