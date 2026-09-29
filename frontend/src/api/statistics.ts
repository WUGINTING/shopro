/**
 * 營運統計 API
 * @module StatisticsAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

/** 期間營運統計（銷售數字只計已付款 / 處理中 / 已完成的訂單） */
export interface StatisticsData {
  startDate: string
  endDate: string
  totalSales: number
  totalOrders: number
  totalCustomers: number
  newCustomers: number
  averageOrderValue: number
  refundAmount: number
  topProducts: Array<{ id: number; name: string; sales: number; revenue: number }>
  topCategories: Array<{ id: number; name: string; sales: number; revenue: number }>
  salesTrend: Array<{ date: string; sales: number; orders: number }>
  /** 訂單狀態（中文）→ 筆數 */
  orderStatus: Record<string, number>
  /** 付款方式 → 成交筆數 */
  paymentMethods: Record<string, number>
}

const statisticsApi = {
  getOverallStatistics: (dateRange?: { startDate: string; endDate: string }) => {
    return axiosInstance.get<any, ApiResponse<StatisticsData>>('/statistics/overall', { params: dateRange })
  }
}

export default statisticsApi
