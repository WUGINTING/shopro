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
    return axiosInstance.get<any>(`${PROMOTION_API}`, {
      params: { page, size }
    }) as Promise<PageResponse<Promotion>>
  },

  getPromotion: async (id: number) => {
    return axiosInstance.get<any>(`${PROMOTION_API}/${id}`) as Promise<Promotion>
  },

  createPromotion: async (promotion: Promotion) => {
    return axiosInstance.post<any>(`${PROMOTION_API}`, promotion) as Promise<Promotion>
  },

  updatePromotion: async (id: number, promotion: Partial<Promotion>) => {
    return axiosInstance.put<any>(`${PROMOTION_API}/${id}`, promotion) as Promise<Promotion>
  },

  deletePromotion: async (id: number) => {
    return axiosInstance.delete<any>(`${PROMOTION_API}/${id}`) as Promise<boolean>
  },

  enablePromotion: async (id: number) => {
    return axiosInstance.patch<any>(`${PROMOTION_API}/${id}/enable`) as Promise<boolean>
  },

  disablePromotion: async (id: number) => {
    return axiosInstance.patch<any>(`${PROMOTION_API}/${id}/disable`) as Promise<boolean>
  }
}

export const couponApi = {
  // 優惠券 API
  getCoupons: async (page = 0, size = 20) => {
    return axiosInstance.get<any>(`${COUPON_API}`, {
      params: { page, size }
    }) as Promise<PageResponse<Coupon>>
  },

  getCoupon: async (id: number) => {
    return axiosInstance.get<any>(`${COUPON_API}/${id}`) as Promise<Coupon>
  },

  createCoupon: async (coupon: Coupon) => {
    return axiosInstance.post<any>(`${COUPON_API}`, coupon) as Promise<Coupon>
  },

  updateCoupon: async (id: number, coupon: Partial<Coupon>) => {
    return axiosInstance.put<any>(`${COUPON_API}/${id}`, coupon) as Promise<Coupon>
  },

  deleteCoupon: async (id: number) => {
    return axiosInstance.delete<any>(`${COUPON_API}/${id}`) as Promise<boolean>
  },

  enableCoupon: async (id: number) => {
    return axiosInstance.patch<any>(`${COUPON_API}/${id}/enable`) as Promise<boolean>
  },

  disableCoupon: async (id: number) => {
    return axiosInstance.patch<any>(`${COUPON_API}/${id}/disable`) as Promise<boolean>
  },

  validateCoupon: async (code: string) => {
    return axiosInstance.get<any>(`${COUPON_API}/validate/${code}`) as Promise<Coupon>
  }
}
