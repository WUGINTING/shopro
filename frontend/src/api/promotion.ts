import axiosInstance from './axios'

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
    return axiosInstance.get<any, PageResponse<Promotion>>(`${PROMOTION_API}`, {
      params: { page, size }
    })
  },

  getPromotion: async (id: number) => {
    return axiosInstance.get<any, Promotion>(`${PROMOTION_API}/${id}`)
  },

  createPromotion: async (promotion: Promotion) => {
    return axiosInstance.post<any, Promotion>(`${PROMOTION_API}`, promotion)
  },

  updatePromotion: async (id: number, promotion: Partial<Promotion>) => {
    return axiosInstance.put<any, Promotion>(`${PROMOTION_API}/${id}`, promotion)
  },

  deletePromotion: async (id: number) => {
    return axiosInstance.delete<any, boolean>(`${PROMOTION_API}/${id}`)
  },

  enablePromotion: async (id: number) => {
    return axiosInstance.patch<any, boolean>(`${PROMOTION_API}/${id}/enable`)
  },

  disablePromotion: async (id: number) => {
    return axiosInstance.patch<any, boolean>(`${PROMOTION_API}/${id}/disable`)
  }
}

export const couponApi = {
  // 優惠券 API
  getCoupons: async (page = 0, size = 20) => {
    return axiosInstance.get<any, PageResponse<Coupon>>(`${COUPON_API}`, {
      params: { page, size }
    })
  },

  getCoupon: async (id: number) => {
    return axiosInstance.get<any, Coupon>(`${COUPON_API}/${id}`)
  },

  createCoupon: async (coupon: Coupon) => {
    return axiosInstance.post<any, Coupon>(`${COUPON_API}`, coupon)
  },

  updateCoupon: async (id: number, coupon: Partial<Coupon>) => {
    return axiosInstance.put<any, Coupon>(`${COUPON_API}/${id}`, coupon)
  },

  deleteCoupon: async (id: number) => {
    return axiosInstance.delete<any, boolean>(`${COUPON_API}/${id}`)
  },

  enableCoupon: async (id: number) => {
    return axiosInstance.patch<any, boolean>(`${COUPON_API}/${id}/enable`)
  },

  disableCoupon: async (id: number) => {
    return axiosInstance.patch<any, boolean>(`${COUPON_API}/${id}/disable`)
  },

  validateCoupon: async (code: string) => {
    return axiosInstance.get<any, Coupon>(`${COUPON_API}/validate/${code}`)
  }
}
