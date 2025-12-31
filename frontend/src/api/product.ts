import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 商品相關接口
export interface Product {
  id?: number
  name: string
  description: string
  price: number
  stock: number
  basePrice?: number
  salePrice?: number
  costPrice?: number
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
  salesMode?: 'NORMAL' | 'PRE_ORDER' | 'TICKET' | 'SUBSCRIPTION' | 'STORE_ONLY'
  categoryId?: number | null
  images?: Array<{ imageUrl: string; albumImageId?: number }> | string[]
  specifications?: ProductSpecification[]
}

export interface ProductSpecification {
  id?: number
  productId?: number
  name: string
  value: string
  price?: number
}

export interface ProductCategory {
  id?: number
  name: string
  description?: string
  parentId?: number
}

// 商品 API
export const productApi = {
  // 獲取商品列表
  getProducts: (params?: any) => {
    return axios.get<any, ApiResponse<Product[]>>('/products', { params })
  },

  // 獲取商品詳情
  getProduct: (id: number) => {
    return axios.get<any, ApiResponse<Product>>(`/products/${id}`)
  },

  // 創建商品
  createProduct: (data: Product) => {
    return axios.post<any, ApiResponse<Product>>('/products', data)
  },

  // 更新商品
  updateProduct: (id: number, data: Product) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}`, data)
  },

  // 刪除商品
  deleteProduct: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/products/${id}`)
  },

  // 上架商品
  activateProduct: (id: number) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}/activate`)
  },

  // 下架商品
  deactivateProduct: (id: number) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}/deactivate`)
  },

  // 從相冊添加圖片到商品
  addAlbumImages: (productId: number, albumImageIds: number[]) => {
    return axios.post<any, ApiResponse<Product>>(`/products/${productId}/album-images`, albumImageIds)
  },

  // 獲取商品分類
  getCategories: () => {
    return axios.get<any, ApiResponse<ProductCategory[]>>('/product-categories')
  }
}

export default productApi
