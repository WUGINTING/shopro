import apiClient from './client'
import type { ApiResponse, PageResponse, Product, ProductCategory, ProductTag } from './types'

/**
 * 商品管理 API
 */
export const productApi = {
  // 創建商品
  create: (data: Product) => {
    return apiClient.post<ApiResponse<Product>>('/api/products', data)
  },

  // 更新商品
  update: (id: number, data: Product) => {
    return apiClient.put<ApiResponse<Product>>(`/api/products/${id}`, data)
  },

  // 取得商品詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<Product>>(`/api/products/${id}`)
  },

  // 刪除商品
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/products/${id}`)
  },

  // 分頁查詢商品
  list: (page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Product>>>('/api/products', {
      params: { page, size }
    })
  },

  // 依分類查詢商品
  listByCategory: (categoryId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Product>>>(
      `/api/products/category/${categoryId}`,
      {
        params: { page, size }
      }
    )
  },

  // 依狀態查詢商品
  listByStatus: (status: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Product>>>(`/api/products/status/${status}`, {
      params: { page, size }
    })
  },

  // 依銷售模式查詢商品
  listBySalesMode: (salesMode: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Product>>>(
      `/api/products/sales-mode/${salesMode}`,
      {
        params: { page, size }
      }
    )
  },

  // 搜尋商品
  search: (keyword: string, page: number = 0, size: number = 20) => {
    return apiClient.get<ApiResponse<PageResponse<Product>>>('/api/products/search', {
      params: { keyword, page, size }
    })
  }
}

/**
 * 商品分類 API
 */
export const productCategoryApi = {
  // 創建分類
  create: (data: ProductCategory) => {
    return apiClient.post<ApiResponse<ProductCategory>>('/api/product-categories', data)
  },

  // 更新分類
  update: (id: number, data: ProductCategory) => {
    return apiClient.put<ApiResponse<ProductCategory>>(`/api/product-categories/${id}`, data)
  },

  // 取得分類詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<ProductCategory>>(`/api/product-categories/${id}`)
  },

  // 刪除分類
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/product-categories/${id}`)
  },

  // 取得所有分類
  listAll: () => {
    return apiClient.get<ApiResponse<ProductCategory[]>>('/api/product-categories')
  },

  // 取得已啟用的分類
  listEnabled: () => {
    return apiClient.get<ApiResponse<ProductCategory[]>>('/api/product-categories/enabled')
  },

  // 取得頂層分類
  listTop: () => {
    return apiClient.get<ApiResponse<ProductCategory[]>>('/api/product-categories/top')
  },

  // 取得子分類
  listChildren: (parentId: number) => {
    return apiClient.get<ApiResponse<ProductCategory[]>>(
      `/api/product-categories/${parentId}/children`
    )
  }
}

/**
 * 商品標籤 API
 */
export const productTagApi = {
  // 創建標籤
  create: (data: ProductTag) => {
    return apiClient.post<ApiResponse<ProductTag>>('/api/product-tags', data)
  },

  // 更新標籤
  update: (id: number, data: ProductTag) => {
    return apiClient.put<ApiResponse<ProductTag>>(`/api/product-tags/${id}`, data)
  },

  // 取得標籤詳情
  get: (id: number) => {
    return apiClient.get<ApiResponse<ProductTag>>(`/api/product-tags/${id}`)
  },

  // 刪除標籤
  delete: (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/product-tags/${id}`)
  },

  // 取得所有標籤
  listAll: () => {
    return apiClient.get<ApiResponse<ProductTag[]>>('/api/product-tags')
  },

  // 新增商品標籤
  addToProduct: (productId: number, tagId: number) => {
    return apiClient.post<ApiResponse<void>>(`/api/products/${productId}/tags/${tagId}`)
  },

  // 移除商品標籤
  removeFromProduct: (productId: number, tagId: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/products/${productId}/tags/${tagId}`)
  },

  // 取得商品的標籤
  listByProduct: (productId: number) => {
    return apiClient.get<ApiResponse<ProductTag[]>>(`/api/products/${productId}/tags`)
  }
}

/**
 * 批次操作 API
 */
export const productBatchApi = {
  // 批次更新商品狀態
  updateStatus: (productIds: number[], status: string) => {
    return apiClient.post<ApiResponse<void>>('/api/products/batch/status', {
      productIds,
      status
    })
  },

  // 批次刪除商品
  delete: (productIds: number[]) => {
    return apiClient.post<ApiResponse<void>>('/api/products/batch/delete', {
      productIds
    })
  },

  // 批次更新商品價格
  updatePrice: (
    productIds: number[],
    priceData: { basePrice?: number; salePrice?: number; discountRate?: number }
  ) => {
    return apiClient.post<ApiResponse<void>>('/api/products/batch/price', {
      productIds,
      ...priceData
    })
  },

  // 批次更新商品分類
  updateCategory: (productIds: number[], categoryId: number) => {
    return apiClient.post<ApiResponse<void>>('/api/products/batch/category', {
      productIds,
      categoryId
    })
  }
}
