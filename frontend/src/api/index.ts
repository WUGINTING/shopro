/**
 * API 統一匯出模組
 * @module API
 * @description 此模組統一匯出所有 API 服務，方便在應用中使用
 */

// 匯出所有類型定義
export * from './product'
export * from './order'
export * from './crm'
export * from './auth'
export * from './types'
export * from './dashboard'
export * from './orderDiscount'
export * from './memberLevel'
export * from './member'
export * from './blog'
export * from './orderQA'
export * from './shipment'
export * from './operationLog'
export * from './point'

// 匯出所有 API 服務
export { default as productApi, categoryApi } from './product'
export { default as orderApi } from './order'
export { default as crmApi } from './crm'
export { default as authApi } from './auth'
export { default as dashboardApi } from './dashboard'
export { default as orderDiscountApi } from './orderDiscount'
export { default as memberLevelApi } from './memberLevel'
export { memberApi } from './member'
export { default as blogApi } from './blog'
export { default as orderQAApi } from './orderQA'
export { shipmentApi } from './shipment'
export { default as operationLogApi } from './operationLog'
export { default as pointApi } from './point'
