// Export API client
export { default as apiClient } from './client'

// Export types
export * from './types'

// Export CRM APIs
export {
  memberApi,
  memberLevelApi,
  memberGroupApi,
  pointApi,
  edmApi,
  blogApi
} from './crm'

// Export Product APIs
export {
  productApi,
  productCategoryApi,
  productTagApi,
  productBatchApi
} from './product'

// Export Order APIs
export {
  orderApi,
  orderBatchApi,
  orderStatisticsApi,
  orderDiscountApi
} from './order'
