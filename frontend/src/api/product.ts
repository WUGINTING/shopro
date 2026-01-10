/**
 * 商品相關 API
 * @module ProductAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

/**
 * 商品介面
 * @interface Product
 */
/**
 * 商品介面
 * @interface Product
 */
export interface Product {
  /** 商品 ID */
  id?: number
  /** 商品名稱 */
  name: string
  /** 商品描述 */
  description: string
  /** 商品價格 */
  price: number
  /** 庫存數量 */
  stock: number
  /** 基礎價格 */
  basePrice?: number
  /** 銷售價格 */
  salePrice?: number
  /** 成本價格 */
  costPrice?: number
  /** 商品狀態 */
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
  /** 銷售模式 */
  salesMode?: 'NORMAL' | 'PRE_ORDER' | 'TICKET' | 'SUBSCRIPTION' | 'STORE_ONLY'
  /** 分類 ID */
  categoryId?: number | null
  /** 商品圖片 */
  images?: Array<{ imageUrl: string; albumImageId?: number }> | string[]
  /** 商品規格 */
  specifications?: ProductSpecification[]
}

/**
 * 商品規格介面
 * @interface ProductSpecification
 */
export interface ProductSpecification {
  /** 規格 ID */
  id?: number
  /** 商品 ID */
  productId?: number
  /** 規格名稱 */
  name: string
  /** 規格值 */
  value: string
  /** 規格價格 */
  price?: number
}

/**
 * 商品分類介面
 * @interface ProductCategory
 */
export interface ProductCategory {
  /** 分類 ID */
  id?: number
  /** 分類名稱 */
  name: string
  /** 分類描述 */
  description?: string
  /** 父分類 ID */
  parentId?: number
}

/**
 * 商品 API 服務
 * @namespace productApi
 */
export const productApi = {
  /**
   * 獲取商品列表
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.page] - 頁碼
   * @param {number} [params.size] - 每頁數量
   * @param {string} [params.status] - 商品狀態篩選
   * @returns {Promise<ApiResponse<Product[]>>} 商品列表回應
   * @example
   * const response = await productApi.getProducts({ page: 1, size: 10 })
   */
  getProducts: (params?: any) => {
    return axios.get<any, ApiResponse<Product[]>>('/products', { params })
  },

  /**
   * 獲取單一商品詳情
   * @param {number} id - 商品 ID
   * @returns {Promise<ApiResponse<Product>>} 商品詳情回應
   * @throws {Error} 當商品不存在時拋出錯誤
   * @example
   * const response = await productApi.getProduct(123)
   */
  getProduct: (id: number) => {
    return axios.get<any, ApiResponse<Product>>(`/products/${id}`)
  },

  /**
   * 創建新商品
   * @param {Product} data - 商品資料
   * @returns {Promise<ApiResponse<Product>>} 創建成功的商品資料
   * @example
   * const newProduct = await productApi.createProduct({
   *   name: '新商品',
   *   description: '商品描述',
   *   price: 100,
   *   stock: 50
   * })
   */
  createProduct: (data: Product) => {
    return axios.post<any, ApiResponse<Product>>('/products', data)
  },

  /**
   * 更新商品資料
   * @param {number} id - 商品 ID
   * @param {Product} data - 更新的商品資料
   * @returns {Promise<ApiResponse<Product>>} 更新後的商品資料
   * @example
   * const updated = await productApi.updateProduct(123, { name: '更新後的名稱' })
   */
  updateProduct: (id: number, data: Product) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}`, data)
  },

  /**
   * 刪除商品
   * @param {number} id - 商品 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @example
   * await productApi.deleteProduct(123)
   */
  deleteProduct: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/products/${id}`)
  },

  /**
   * 上架商品
   * @param {number} id - 商品 ID
   * @returns {Promise<ApiResponse<Product>>} 上架後的商品資料
   * @example
   * const activated = await productApi.activateProduct(123)
   */
  activateProduct: (id: number) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}/activate`)
  },

  /**
   * 下架商品
   * @param {number} id - 商品 ID
   * @returns {Promise<ApiResponse<Product>>} 下架後的商品資料
   * @example
   * const deactivated = await productApi.deactivateProduct(123)
   */
  deactivateProduct: (id: number) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}/deactivate`)
  },

  /**
   * 從相冊添加圖片到商品
   * @param {number} productId - 商品 ID
   * @param {number[]} albumImageIds - 相冊圖片 ID 陣列
   * @returns {Promise<ApiResponse<Product>>} 更新後的商品資料
   * @example
   * const updated = await productApi.addAlbumImages(123, [1, 2, 3])
   */
  addAlbumImages: (productId: number, albumImageIds: number[]) => {
    return axios.post<any, ApiResponse<Product>>(`/products/${productId}/album-images`, albumImageIds)
  },

  /**
   * 獲取商品分類列表
   * @returns {Promise<ApiResponse<ProductCategory[]>>} 商品分類列表
   * @example
   * const categories = await productApi.getCategories()
   */
  getCategories: () => {
    return axios.get<any, ApiResponse<ProductCategory[]>>('/product-categories')
  }
}

export default productApi
