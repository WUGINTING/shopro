import axios from './axios'
import type { ApiResponse } from './types'

// Dashboard statistics interfaces
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

export interface OrderStatistics {
  totalOrders: number
  totalAmount: number
  averageOrderAmount: number
  dailyOrderTrend?: { [key: string]: number }
  amountDistribution?: { [key: string]: number }
  statusDistribution?: { [key: string]: number }
  dailyAmountTrend?: { [key: string]: number }
}

// Dashboard API
export const dashboardApi = {
  // Get dashboard summary statistics
  getStats: () => {
    return axios.get<any, ApiResponse<DashboardStats>>('/dashboard/stats')
  },

  // Get recent orders (limit to recent 10)
  getRecentOrders: (limit: number = 10) => {
    return axios.get<any, ApiResponse<RecentOrder[]>>('/dashboard/recent-orders', {
      params: { limit }
    })
  },

  // Get top selling products
  getTopProducts: (limit: number = 5) => {
    return axios.get<any, ApiResponse<TopProduct[]>>('/dashboard/top-products', {
      params: { limit }
    })
  },

  // Get order statistics for today
  getTodayStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/today')
  },

  // Get order statistics for this week
  getWeekStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/week')
  },

  // Get order statistics for this month
  getMonthStatistics: () => {
    return axios.get<any, ApiResponse<OrderStatistics>>('/orders/statistics/month')
  }
}

export default dashboardApi
