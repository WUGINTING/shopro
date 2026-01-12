/**
 * 促銷和優惠券管理 API
 * @module PromotionAPI
 */

import axiosInstance from './axios'
import type { ApiResponse } from './types'

export interface Promotion {
  id?: number
  name: string
  description?: string
  type: 'DISCOUNT' | 'FULL_SHOP' | 'FREE_SHIPPING' | 'BUY_GIFT'
  discountType?: 'PERCENTAGE' | 'FIXED'
  discountValue?: number
  minPurchaseAmount?: number
  maxDiscountAmount?: number
  startDate: string
  endDate: string
  enabled: boolean
  priority: number
  conditions?: string
  createdAt?: string
  updatedAt?: string
}

export interface Coupon {
  id?: number
  code: string
  name: string
  type: 'PERCENTAGE' | 'FIXED' | 'FREE_SHIPPING'
  discountValue: number
  minPurchaseAmount?: number
  maxDiscountAmount?: number
  totalCount: number
  usedCount?: number
  validFrom: string
  validUntil: string
  enabled: boolean
  applicable?: string
  createdAt?: string
  updatedAt?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

const PROMOTION_API = '/marketing/promotions'
const COUPON_API = '/marketing/coupons'

export const promotionApi = {
  // 促銷活動 API
  getPromotions: async (page = 0, size = 20) => {
    const response = await axiosInstance.get<any, ApiResponse<any>>(`${PROMOTION_API}`, {
      params: { page, size }
    })
    // 處理 Spring Data Page 格式或自定義格式
    const pageData = response.data
    if (pageData && typeof pageData === 'object') {
      // 如果是 Spring Data Page 格式
      if (pageData.content !== undefined) {
        return {
          content: pageData.content || [],
          totalElements: pageData.totalElements || 0,
          totalPages: pageData.totalPages || 0,
          currentPage: pageData.pageable?.pageNumber ?? pageData.number ?? page,
          pageSize: pageData.pageable?.pageSize ?? pageData.size ?? size
        } as PageResponse<Promotion>
      }
      // 如果是自定義格式（包含 data 字段）
      if (pageData.data !== undefined) {
        return {
          content: pageData.data || [],
          totalElements: pageData.total || 0,
          totalPages: Math.ceil((pageData.total || 0) / size),
          currentPage: pageData.page || page,
          pageSize: pageData.pageSize || size
        } as PageResponse<Promotion>
      }
    }
    return pageData as PageResponse<Promotion>
  },

  getPromotion: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<Promotion>>(`${PROMOTION_API}/${id}`)
    return response.data
  },

  createPromotion: async (promotion: Promotion) => {
    const response = await axiosInstance.post<any, ApiResponse<Promotion>>(`${PROMOTION_API}`, promotion)
    return response.data
  },

  updatePromotion: async (id: number, promotion: Partial<Promotion>) => {
    const response = await axiosInstance.put<any, ApiResponse<Promotion>>(`${PROMOTION_API}/${id}`, promotion)
    return response.data
  },

  deletePromotion: async (id: number) => {
    await axiosInstance.delete<any, ApiResponse<void>>(`${PROMOTION_API}/${id}`)
  },

  enablePromotion: async (id: number) => {
    await axiosInstance.patch<any, ApiResponse<void>>(`${PROMOTION_API}/${id}/enable`)
  },

  disablePromotion: async (id: number) => {
    await axiosInstance.patch<any, ApiResponse<void>>(`${PROMOTION_API}/${id}/disable`)
  }
}

export const couponApi = {
  // 優惠券 API
  getCoupons: async (page = 0, size = 20) => {
    const response = await axiosInstance.get<any, ApiResponse<any>>(`${COUPON_API}`, {
      params: { page, size }
    })
    // 處理 Spring Data Page 格式或自定義格式
    const pageData = response.data
    if (pageData && typeof pageData === 'object') {
      if (pageData.content !== undefined) {
        return {
          content: pageData.content || [],
          totalElements: pageData.totalElements || 0,
          totalPages: pageData.totalPages || 0,
          currentPage: pageData.pageable?.pageNumber ?? pageData.number ?? page,
          pageSize: pageData.pageable?.pageSize ?? pageData.size ?? size
        } as PageResponse<Coupon>
      }
      if (pageData.data !== undefined) {
        return {
          content: pageData.data || [],
          totalElements: pageData.total || 0,
          totalPages: Math.ceil((pageData.total || 0) / size),
          currentPage: pageData.page || page,
          pageSize: pageData.pageSize || size
        } as PageResponse<Coupon>
      }
    }
    return pageData as PageResponse<Coupon>
  },

  getCoupon: async (id: number) => {
    const response = await axiosInstance.get<any, ApiResponse<Coupon>>(`${COUPON_API}/${id}`)
    return response.data
  },

  createCoupon: async (coupon: Coupon) => {
    const response = await axiosInstance.post<any, ApiResponse<Coupon>>(`${COUPON_API}`, coupon)
    return response.data
  },

  updateCoupon: async (id: number, coupon: Partial<Coupon>) => {
    const response = await axiosInstance.put<any, ApiResponse<Coupon>>(`${COUPON_API}/${id}`, coupon)
    return response.data
  },

  deleteCoupon: async (id: number) => {
    await axiosInstance.delete<any, ApiResponse<void>>(`${COUPON_API}/${id}`)
  },

  enableCoupon: async (id: number) => {
    await axiosInstance.patch<any, ApiResponse<void>>(`${COUPON_API}/${id}/enable`)
  },

  disableCoupon: async (id: number) => {
    await axiosInstance.patch<any, ApiResponse<void>>(`${COUPON_API}/${id}/disable`)
  },

  validateCoupon: async (code: string) => {
    const response = await axiosInstance.get<any, ApiResponse<Coupon>>(`${COUPON_API}/validate/${code}`)
    return response.data
  }
}
