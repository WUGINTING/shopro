/**
 * 庫存管理 API
 * @module InventoryAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 庫存警示介面
 */
export interface InventoryAlert {
  id: number
  productId: number
  productName?: string
  specificationId?: number
  specificationName?: string
  alertLevel: 'LOW' | 'CRITICAL' | 'OUT_OF_STOCK'
  currentStock: number
  safetyStock?: number
  resolved: boolean
  resolvedAt?: string
  createdAt: string
}

/**
 * 庫存資訊介面
 */
export interface ProductInventory {
  id: number
  productId: number
  specificationId?: number
  warehouseId?: number
  availableStock: number
  lockedStock: number
  safetyStock?: number
  createdAt: string
  updatedAt: string
}

export interface InventoryMovementLog {
  id: number
  productId: number
  specificationId?: number | null
  warehouseId?: number | null
  changeType: 'INCREASE' | 'DECREASE' | 'SET' | string
  source: string
  changeQuantity: number
  beforeStock: number
  afterStock: number
  remark?: string
  createdAt: string
}

/**
 * 庫存管理 API
 */
export const inventoryApi = {
  /**
   * 獲取未解決的庫存警示
   */
  getUnresolvedAlerts: () => {
    return axios.get<any, ApiResponse<InventoryAlert[]>>('/inventory/alerts')
  },

  /**
   * 獲取指定商品的未解決警示
   */
  getProductAlerts: (productId: number) => {
    return axios.get<any, ApiResponse<InventoryAlert[]>>(`/inventory/alerts/product/${productId}`)
  },

  /**
   * 取得庫存異動紀錄（最近 100 筆）
   */
  getInventoryLogs: () => {
    return axios.get<any, ApiResponse<InventoryMovementLog[]>>('/inventory/logs')
  },

  /**
   * 取得單一商品庫存異動紀錄（最近 100 筆）
   */
  getProductInventoryLogs: (productId: number) => {
    return axios.get<any, ApiResponse<InventoryMovementLog[]>>(`/inventory/logs/product/${productId}`)
  },

  /**
   * 解決庫存警示
   */
  resolveAlert: (alertId: number) => {
    return axios.put<any, ApiResponse<void>>(`/inventory/alerts/${alertId}/resolve`)
  },

  /**
   * 檢查庫存並創建警示
   */
  checkAlerts: () => {
    return axios.post<any, ApiResponse<void>>('/inventory/check-alerts')
  },

  /**
   * 更新庫存（快速補貨）
   */
  updateInventory: (productId: number, quantity: number, specificationId?: number, warehouseId: number = 1) => {
    return axios.put<any, ApiResponse<void>>('/inventory/update', null, {
      params: {
        productId,
        specificationId,
        warehouseId,
        quantity
      }
    })
  },

  /**
   * 訂閱貨到通知
   */
  subscribeNotification: (productId: number, email: string, phone?: string, specificationId?: number) => {
    return axios.post<any, ApiResponse<void>>('/inventory/notifications/subscribe', null, {
      params: {
        productId,
        specificationId,
        email,
        phone
      }
    })
  }
}

export default inventoryApi
