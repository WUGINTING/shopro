/**
 * 會員中心 API（登入會員自己的會員資料）
 */
import axios from './axios'
import type { ApiResponse } from './types'

export interface AccountMember {
  exists: boolean
  emailVerified: boolean
  name?: string
  email?: string
  phone?: string | null
  address?: string | null
  postalCode?: string | null
  marketingOptIn?: boolean
  totalSpent?: number
  availablePoints?: number
  levelName?: string
  discountRate?: number | null
  nextLevelName?: string
  nextLevelRemaining?: number
}

export const accountApi = {
  getMember: () => axios.get<any, ApiResponse<AccountMember>>('/account/member'),
  updateMember: (data: { name?: string; phone?: string; address?: string; postalCode?: string; marketingOptIn?: boolean }) =>
    axios.put<any, ApiResponse<AccountMember>>('/account/member', data)
}
