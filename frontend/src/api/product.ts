import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 商品相关接口
export interface Product {
  id?: number
  name: string
  description: string
  price: number
  stock: number
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
  salesMode?: 'NORMAL' | 'PRE_ORDER' | 'TICKET' | 'SUBSCRIPTION' | 'STORE_ONLY'
  categoryId?: number | null
  images?: string[]
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
  // 获取商品列表
  getProducts: (params?: any) => {
    return axios.get<any, ApiResponse<Product[]>>('/products', { params })
  },
  
  // 获取商品详情
  getProduct: (id: number) => {
    return axios.get<any, ApiResponse<Product>>(`/products/${id}`)
  },
  
  // 创建商品
  createProduct: (data: Product) => {
    return axios.post<any, ApiResponse<Product>>('/products', data)
  },
  
  // 更新商品
  updateProduct: (id: number, data: Product) => {
    return axios.put<any, ApiResponse<Product>>(`/products/${id}`, data)
  },
  
  // 删除商品
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
  
  // 获取商品分类
  getCategories: () => {
    return axios.get<any, ApiResponse<ProductCategory[]>>('/product-categories')
  }
}

export default productApi
