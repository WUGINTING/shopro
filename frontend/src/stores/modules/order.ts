import { defineStore } from 'pinia'
import { ref } from 'vue'
import { orderApi, orderBatchApi, orderStatisticsApi } from '@/api'
import type { Order, OrderStatistics, PageResponse } from '@/api'

export const useOrderStore = defineStore('order', () => {
  // State
  const orders = ref<Order[]>([])
  const currentOrder = ref<Order | null>(null)
  const statistics = ref<OrderStatistics | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0
  })

  // Actions - 訂單管理
  async function fetchOrders(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.list(page, size) as any
      if (response.success && response.data) {
        orders.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取訂單列表失敗'
      console.error('Error fetching orders:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchOrdersByCustomer(customerId: number, page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.listByCustomer(customerId, page, size) as any
      if (response.success && response.data) {
        orders.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取客戶訂單失敗'
      console.error('Error fetching orders by customer:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchOrdersByStatus(status: string, page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.listByStatus(status, page, size) as any
      if (response.success && response.data) {
        orders.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取訂單失敗'
      console.error('Error fetching orders by status:', e)
    } finally {
      loading.value = false
    }
  }

  async function createOrder(orderData: Order) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.create(orderData) as any
      if (response.success && response.data) {
        orders.value.unshift(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建訂單失敗'
      console.error('Error creating order:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateOrder(id: number, orderData: Order) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.update(id, orderData) as any
      if (response.success && response.data) {
        const index = orders.value.findIndex((o) => o.id === id)
        if (index !== -1) {
          orders.value[index] = response.data
        }
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '更新訂單失敗'
      console.error('Error updating order:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteOrder(id: number) {
    loading.value = true
    error.value = null
    try {
      await orderApi.delete(id)
      orders.value = orders.value.filter((o) => o.id !== id)
    } catch (e: any) {
      error.value = e.message || '刪除訂單失敗'
      console.error('Error deleting order:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function getOrder(id: number) {
    loading.value = true
    error.value = null
    try {
      const response = await orderApi.get(id) as any
      if (response.success && response.data) {
        currentOrder.value = response.data
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取訂單詳情失敗'
      console.error('Error fetching order:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - 訂單統計
  async function fetchStatistics(startDate?: string, endDate?: string) {
    loading.value = true
    error.value = null
    try {
      const response = await orderStatisticsApi.getOverview(startDate, endDate) as any
      if (response.success && response.data) {
        statistics.value = response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取訂單統計失敗'
      console.error('Error fetching statistics:', e)
    } finally {
      loading.value = false
    }
  }

  // Actions - 批次操作
  async function batchUpdateStatus(orderIds: number[], status: string) {
    loading.value = true
    error.value = null
    try {
      await orderBatchApi.updateStatus(orderIds, status) as any
      // 更新本地訂單狀態
      orders.value = orders.value.map((order) => {
        if (orderIds.includes(order.id!)) {
          return { ...order, status: status as any }
        }
        return order
      })
    } catch (e: any) {
      error.value = e.message || '批次更新訂單狀態失敗'
      console.error('Error batch updating order status:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function batchDeleteOrders(orderIds: number[]) {
    loading.value = true
    error.value = null
    try {
      await orderBatchApi.delete(orderIds) as any
      orders.value = orders.value.filter((order) => !orderIds.includes(order.id!))
    } catch (e: any) {
      error.value = e.message || '批次刪除訂單失敗'
      console.error('Error batch deleting orders:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    orders,
    currentOrder,
    statistics,
    loading,
    error,
    pagination,
    // Actions
    fetchOrders,
    fetchOrdersByCustomer,
    fetchOrdersByStatus,
    createOrder,
    updateOrder,
    deleteOrder,
    getOrder,
    fetchStatistics,
    batchUpdateStatus,
    batchDeleteOrders
  }
})
