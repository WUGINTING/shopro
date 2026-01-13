/**
 * 認證相關 API
 * @module AuthAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 登入請求介面
 * @interface LoginRequest
 */
export interface LoginRequest {
  /** 使用者名稱 */
  username: string
  /** 密碼 */
  password: string
}

/**
 * 認證回應介面
 * @interface AuthResponse
 */
export interface AuthResponse {
  /** JWT Token */
  token: string
  /** 使用者名稱 */
  username: string
  /** 電子郵件 */
  email: string
  /** 使用者角色 */
  role: string
}

/**
 * 使用者介面
 * @interface User
 */
export interface User {
  /** 使用者 ID */
  id?: number
  /** 使用者名稱 */
  username: string
  /** 電子郵件 */
  email: string
  /** 使用者角色 */
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'
  /** 是否啟用 */
  enabled?: boolean
  /** 創建時間 */
  createdAt?: string
  /** 更新時間 */
  updatedAt?: string
}

/**
 * 更新個人資料請求介面
 * @interface UpdateProfileRequest
 */
export interface UpdateProfileRequest {
  /** 使用者名稱 */
  username?: string
  /** 電子郵件 */
  email?: string
  /** 當前密碼 */
  currentPassword?: string
  /** 新密碼 */
  newPassword?: string
}

/**
 * 認證 API 服務
 * @namespace authApi
 */
export const authApi = {
  /**
   * 使用者登入
   * @param {LoginRequest} data - 登入資料
   * @returns {Promise<ApiResponse<AuthResponse>>} 登入回應
   * @example
   * const response = await authApi.login({ username: 'admin', password: 'password' })
   */
  login: (data: LoginRequest) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/login', data)
  },
  
  /**
   * 使用者註冊
   * @param {Object} data - 註冊資料
   * @param {string} data.username - 使用者名稱
   * @param {string} data.email - 電子郵件
   * @param {string} data.password - 密碼
   * @returns {Promise<ApiResponse<AuthResponse>>} 註冊回應
   * @example
   * const response = await authApi.register({ username: 'user', email: 'user@example.com', password: 'password' })
   */
  register: (data: { username: string; email: string; password: string }) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/register', data)
  },
  
  /**
   * 登出（客戶端清除 token）
   * @returns {void}
   * @example
   * authApi.logout()
   */
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  
  /**
   * 獲取當前使用者資訊
   * @returns {User | null} 使用者資訊或 null
   * @example
   * const user = authApi.getCurrentUser()
   */
  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },
  
  /**
   * 保存使用者資訊
   * @param {User} user - 使用者資訊
   * @returns {void}
   * @example
   * authApi.saveUser({ id: 1, username: 'admin', email: 'admin@example.com', role: 'ADMIN' })
   */
  saveUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user))
  },
  
  /**
   * 檢查是否已登入
   * @returns {boolean} 是否已登入
   * @example
   * if (authApi.isAuthenticated()) {
   *   console.log('已登入')
   * }
   */
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token')
  },

  /**
   * 取得個人資料
   * @returns {Promise<ApiResponse<User>>} 個人資料回應
   * @example
   * const profile = await authApi.getProfile()
   */
  getProfile: () => {
    return axios.get<any, ApiResponse<User>>('/auth/profile')
  },

  /**
   * 更新個人資料
   * @param {UpdateProfileRequest} data - 更新資料
   * @returns {Promise<ApiResponse<User>>} 更新後的個人資料
   * @example
   * const updated = await authApi.updateProfile({ username: '新名稱' })
   */
  updateProfile: (data: UpdateProfileRequest) => {
    return axios.put<any, ApiResponse<User>>('/auth/profile', data)
  },

  /**
   * Google SSO 登入/註冊
   * @param {string} idToken - Google ID Token
   * @returns {Promise<ApiResponse<AuthResponse>>} 登入回應
   * @example
   * const response = await authApi.googleLogin(idToken)
   */
  googleLogin: (idToken: string) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/google', { idToken })
  }
}

export default authApi
