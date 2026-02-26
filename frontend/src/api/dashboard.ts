/**
 * 儀表板相關 API
 * @module DashboardAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 儀表板統計介面
 * @interface DashboardStats
 */
export interface DashboardStats {
  totalProducts: number
  totalProductsChange?: number
  pendingOrders: number
  pendingOrdersChange?: number
  totalCustomers: number
  totalCustomersChange?: number
  monthlySales: number
  monthlySalesChange?: number
}

export interface RecentOrder {
  id: number
  orderNumber: string
  status: string
  totalAmount: number
  customerName: string
  createdAt: string
}

export interface TopProduct {
  id: number
  name: string
  salesCount: number
  price: number
  imageUrl?: string
}

export interface DashboardOption {
  code: string
  label: string
}

export interface DashboardShortcut {
  key: string
  label: string
  route: string
  description: string
  badgeCount?: number
}

export interface DashboardLookups {
  roles: DashboardOption[]
  orderStatuses: DashboardOption[]
  paymentMethods: DashboardOption[]
  shippingMethods: DashboardOption[]
  pickupTypes: DashboardOption[]
}

export interface DashboardOverview {
  stats: DashboardStats
  recentOrders: RecentOrder[]
  topProducts: TopProduct[]
  lookups?: DashboardLookups
  shortcuts?: DashboardShortcut[]
}

export interface OrderStatistics {
  totalOrders: number
  totalAmount: number
  averageOrderAmount: number
  dailyOrderTrend?: { [key: string]: number }
  amountDistribution?: { [key: string]: number }
  statusDistribution?: { [key: string]: number }
  dailyAmountTrend?: { [key: string]: number }
}

/**
 * 儀表板 API 服務
 * @namespace dashboardApi
 */
export const dashboardApi = {
  /**
   * 取得儀表板統計摘要
   * @description 獲取後台首頁統計數據（總商品數、待處理訂單、客戶數、月銷售額等）
   * @returns {Promise<ApiResponse<DashboardStats>>} 統計數據回應
   * @swagger GET /api/dashboard/stats
   * @example
   * const stats = await dashboardApi.getStats()
   * console.log(stats.data.monthlySales) // 本月銷售額
   */
  getStats: () => {
    return axios.get<any, ApiResponse<DashboardStats>>('/dashboard/stats')
  },

  /**
   * 取得最近訂單
   * @description 獲取最近的訂單列表，用於儀表板顯示
   * @param {number} [limit=10] - 返回的訂單數量上限
   * @returns {Promise<ApiResponse<RecentOrder[]>>} 最近訂單列表
   * @swagger GET /api/dashboard/recent-orders
   * @example
   * const recentOrders = await dashboardApi.getRecentOrders(10)
   */
  getRecentOrders: (limit: number = 10) => {
    return axios.get<any, ApiResponse<RecentOrder[]>>('/dashboard/recent-orders', {
      params: { limit }
    })
  },

  /**
   * 取得熱銷商品
   * @description 獲取銷售量最高的商品列表
   * @param {number} [limit=5] - 返回的商品數量上限
   * @returns {Promise<ApiResponse<TopProduct[]>>} 熱銷商品列表
   * @swagger GET /api/dashboard/top-products
   * @example
   * const topProducts = await dashboardApi.getTopProducts(5)
   */
  getTopProducts: (limit: number = 5) => {
    return axios.get<any, ApiResponse<TopProduct[]>>('/dashboard/top-products', {
      params: { limit }
    })
  },

  /**
   * Unified dashboard overview payload for admin home screen
   */
  getOverview: (params?: { recentOrderLimit?: number; topProductLimit?: number }) => {
    return axios.get<any, ApiResponse<DashboardOverview>>('/dashboard/overview', { params })
  },

  /**
   * 取得今日訂單統計
   * @description 獲取今日訂單統計數據（訂單數、總金額、平均訂單金額等）
   * @returns {Promise<ApiResponse<OrderStatistics>>} 今日統計數據
   * @swagger GET /api/orders/statistics/today
   * @example
   * const todayStats = await dashboardApi.getTodayStatistics()
   */
  getTodayStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/today')
  },

  /**
   * 取得本週訂單統計
   * @description 獲取本週訂單統計數據
   * @returns {Promise<ApiResponse<OrderStatistics>>} 本週統計數據
   * @swagger GET /api/orders/statistics/week
   * @example
   * const weekStats = await dashboardApi.getWeekStatistics()
   */
  getWeekStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/week')
  },

  /**
   * 取得本月訂單統計
   * @description 獲取本月訂單統計數據
   * @returns {Promise<ApiResponse<OrderStatistics>>} 本月統計數據
   * @swagger GET /api/orders/statistics/month
   * @example
   * const monthStats = await dashboardApi.getMonthStatistics()
   */
  getMonthStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/month')
  }
}

export default dashboardApi
