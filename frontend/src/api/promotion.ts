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
    const data = await axiosInstance.get<any>(`${PROMOTION_API}`, {
      params: { page, size }
    })
    return data.data as PageResponse<Promotion>
  },

  getPromotion: async (id: number) => {
    const data = await axiosInstance.get<any>(`${PROMOTION_API}/${id}`)
    return data.data as Promotion
  },

  createPromotion: async (promotion: Promotion) => {
    const data = await axiosInstance.post<any>(`${PROMOTION_API}`, promotion)
    return data.data as Promotion
  },

  updatePromotion: async (id: number, promotion: Partial<Promotion>) => {
    const data = await axiosInstance.put<any>(`${PROMOTION_API}/${id}`, promotion)
    return data.data as Promotion
  },

  deletePromotion: async (id: number) => {
    const data = await axiosInstance.delete<any>(`${PROMOTION_API}/${id}`)
    return data.success
  },

  enablePromotion: async (id: number) => {
    const data = await axiosInstance.patch<any>(`${PROMOTION_API}/${id}/enable`)
    return data.success
  },

  disablePromotion: async (id: number) => {
    const data = await axiosInstance.patch<any>(`${PROMOTION_API}/${id}/disable`)
    return data.success
  }
}

export const couponApi = {
  // 優惠券 API
  getCoupons: async (page = 0, size = 20) => {
    const data = await axiosInstance.get<any>(`${COUPON_API}`, {
      params: { page, size }
    })
    return data.data as PageResponse<Coupon>
  },

  getCoupon: async (id: number) => {
    const data = await axiosInstance.get<any>(`${COUPON_API}/${id}`)
    return data.data as Coupon
  },

  createCoupon: async (coupon: Coupon) => {
    const data = await axiosInstance.post<any>(`${COUPON_API}`, coupon)
    return data.data as Coupon
  },

  updateCoupon: async (id: number, coupon: Partial<Coupon>) => {
    const data = await axiosInstance.put<any>(`${COUPON_API}/${id}`, coupon)
    return data.data as Coupon
  },

  deleteCoupon: async (id: number) => {
    const data = await axiosInstance.delete<any>(`${COUPON_API}/${id}`)
    return data.success
  },

  enableCoupon: async (id: number) => {
    const data = await axiosInstance.patch<any>(`${COUPON_API}/${id}/enable`)
    return data.success
  },

  disableCoupon: async (id: number) => {
    const data = await axiosInstance.patch<any>(`${COUPON_API}/${id}/disable`)
    return data.success
  },

  validateCoupon: async (code: string) => {
    const data = await axiosInstance.get<any>(`${COUPON_API}/validate/${code}`)
    return data.data as Coupon
  }
}
