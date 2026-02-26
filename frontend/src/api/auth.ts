/**
 * èªè??¸é? API
 * @module AuthAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * ?»å…¥è«‹æ?ä»‹é¢
 * @interface LoginRequest
 */
export interface LoginRequest {
  /** ä½¿ç”¨?…å?ç¨?*/
  username: string
  /** å¯†ç¢¼ */
  password: string
}

/**
 * èªè??æ?ä»‹é¢
 * @interface AuthResponse
 */
export interface AuthResponse {
  /** JWT Token */
  token: string
  /** User ID */
  id?: number
  /** ä½¿ç”¨?…å?ç¨?*/
  username: string
  /** ?»å??µä»¶ */
  email: string
  /** ä½¿ç”¨?…è???*/
  role: string
}

/**
 * ä½¿ç”¨?…ä???
 * @interface User
 */
export interface User {
  /** ä½¿ç”¨??ID */
  id?: number
  /** ä½¿ç”¨?…å?ç¨?*/
  username: string
  /** ?»å??µä»¶ */
  email: string
  /** ä½¿ç”¨?…è???*/
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'
  /** ?¯å¦?Ÿç”¨ */
  enabled?: boolean
  /** ?µå»º?‚é? */
  createdAt?: string
  /** ?´æ–°?‚é? */
  updatedAt?: string
}

/**
 * ?´æ–°?‹äººè³‡æ?è«‹æ?ä»‹é¢
 * @interface UpdateProfileRequest
 */
export interface UpdateProfileRequest {
  /** ä½¿ç”¨?…å?ç¨?*/
  username?: string
  /** ?»å??µä»¶ */
  email?: string
  /** ?¶å?å¯†ç¢¼ */
  currentPassword?: string
  /** ?°å?ç¢?*/
  newPassword?: string
}

/**
 * èªè? API ?å?
 * @namespace authApi
 */
export const authApi = {
  /**
   * ä½¿ç”¨?…ç™»??
   * @param {LoginRequest} data - ?»å…¥è³‡æ?
   * @returns {Promise<ApiResponse<AuthResponse>>} ?»å…¥?æ?
   * @example
   * const response = await authApi.login({ username: 'admin', password: 'password' })
   */
  login: (data: LoginRequest) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/login', data)
  },
  
  /**
   * ä½¿ç”¨?…è¨»??
   * @param {Object} data - è¨»å?è³‡æ?
   * @param {string} data.username - ä½¿ç”¨?…å?ç¨?
   * @param {string} data.email - ?»å??µä»¶
   * @param {string} data.password - å¯†ç¢¼
   * @returns {Promise<ApiResponse<AuthResponse>>} è¨»å??æ?
   * @example
   * const response = await authApi.register({ username: 'user', email: 'user@example.com', password: 'password' })
   */
  register: (data: { username: string; email: string; password: string }) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/register', data)
  },
  
  /**
   * ?»å‡ºï¼ˆå®¢?¶ç«¯æ¸…é™¤ tokenï¼?
   * @returns {void}
   * @example
   * authApi.logout()
   */
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  
  /**
   * ?²å??¶å?ä½¿ç”¨?…è?è¨?
   * @returns {User | null} ä½¿ç”¨?…è?è¨Šæ? null
   * @example
   * const user = authApi.getCurrentUser()
   */
  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },
  
  /**
   * ä¿å?ä½¿ç”¨?…è?è¨?
   * @param {User} user - ä½¿ç”¨?…è?è¨?
   * @returns {void}
   * @example
   * authApi.saveUser({ id: 1, username: 'admin', email: 'admin@example.com', role: 'ADMIN' })
   */
  saveUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user))
  },
  
  /**
   * æª¢æŸ¥?¯å¦å·²ç™»??
   * @returns {boolean} ?¯å¦å·²ç™»??
   * @example
   * if (authApi.isAuthenticated()) {
   *   console.log('å·²ç™»??)
   * }
   */
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token')
  },

  /**
   * ?–å??‹äººè³‡æ?
   * @returns {Promise<ApiResponse<User>>} ?‹äººè³‡æ??æ?
   * @example
   * const profile = await authApi.getProfile()
   */
  getProfile: () => {
    return axios.get<any, ApiResponse<User>>('/auth/profile')
  },

  /**
   * ?´æ–°?‹äººè³‡æ?
   * @param {UpdateProfileRequest} data - ?´æ–°è³‡æ?
   * @returns {Promise<ApiResponse<User>>} ?´æ–°å¾Œç??‹äººè³‡æ?
   * @example
   * const updated = await authApi.updateProfile({ username: '?°å?ç¨? })
   */
  updateProfile: (data: UpdateProfileRequest) => {
    return axios.put<any, ApiResponse<User>>('/auth/profile', data)
  },

  /**
   * Google SSO ?»å…¥/è¨»å?
   * @param {string} idToken - Google ID Token
   * @returns {Promise<ApiResponse<AuthResponse>>} ?»å…¥?æ?
   * @example
   * const response = await authApi.googleLogin(idToken)
   */
  googleLogin: (idToken: string) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/google', { idToken })
  }
}

export default authApi
