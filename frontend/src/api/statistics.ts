import axiosInstance from './axios'

export interface StatisticsData {
  totalSales: number
  totalOrders: number
  totalCustomers: number
  averageOrderValue: number
  conversionRate: number
  topProducts: Array<{
    id: number
    name: string
    sales: number
    revenue: number
  }>
  topCategories: Array<{
    id: number
    name: string
    sales: number
  }>
  salesTrend: Array<{
    date: string
    sales: number
    orders: number
  }>
  orderStatus: {
    PENDING: number
    COMPLETED: number
    CANCELLED: number
    RETURNED: number
  }
  paymentMethods: {
    [key: string]: number
  }
}

export interface DailyStatistics {
  date: string
  totalSales: number
  totalOrders: number
  newCustomers: number
  averageOrderValue: number
  topProduct?: string
}

export interface ChartData {
  labels: string[]
  datasets: Array<{
    label: string
    data: number[]
    backgroundColor?: string | string[]
    borderColor?: string
    borderWidth?: number
  }>
}

const statisticsApi = {
  // 獲取總體統計數據
  getOverallStatistics: async (dateRange?: { startDate: string; endDate: string }) => {
    return axiosInstance.get<any, { data: StatisticsData }>('/statistics/overall', {
      params: dateRange
    })
  },

  // 獲取日期範圍內的統計數據
  getStatisticsByDateRange: async (startDate: string, endDate: string) => {
    return axiosInstance.get<any, { data: DailyStatistics[] }>('/statistics/date-range', {
      params: { startDate, endDate }
    })
  },

  // 獲取銷售趨勢
  getSalesTrend: async (days: number = 30) => {
    return axiosInstance.get<any, { data: ChartData }>('/statistics/sales-trend', {
      params: { days }
    })
  },

  // 獲取產品銷售排行
  getTopProducts: async (limit: number = 10) => {
    return axiosInstance.get<any, { data: any[] }>('/statistics/top-products', {
      params: { limit }
    })
  },

  // 獲取分類銷售統計
  getCategorySales: async () => {
    return axiosInstance.get<any, { data: ChartData }>('/statistics/category-sales')
  },

  // 獲取訂單狀態分布
  getOrderStatusDistribution: async () => {
    return axiosInstance.get<any, { data: any }>('/statistics/order-status')
  },

  // 獲取支付方式統計
  getPaymentMethodStatistics: async () => {
    return axiosInstance.get<any, { data: ChartData }>('/statistics/payment-methods')
  },

  // 獲取客戶統計
  getCustomerStatistics: async (dateRange?: { startDate: string; endDate: string }) => {
    return axiosInstance.get<any, { data: any }>('/statistics/customers', {
      params: dateRange
    })
  },

  // 獲取收入統計
  getRevenueStatistics: async (dateRange?: { startDate: string; endDate: string }) => {
    return axiosInstance.get<any, { data: any }>('/statistics/revenue', {
      params: dateRange
    })
  }
}

export default statisticsApi
