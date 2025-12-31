import axios from './axios'
import type { ApiResponse } from './types'

// 認證相關接口
export interface LoginRequest {
  username: string
  password: string
}

export interface AuthResponse {
  token: string
  username: string
  email: string
  role: string
}

export interface User {
  id?: number
  username: string
  email: string
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'
  enabled?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface UpdateProfileRequest {
  username?: string
  email?: string
  currentPassword?: string
  newPassword?: string
}

// 認證 API
export const authApi = {
  // 使用者登入
  login: (data: LoginRequest) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/login', data)
  },
  
  // 使用者註冊
  register: (data: { username: string; email: string; password: string }) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/register', data)
  },
  
  // 登出（客戶端清除 token）
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  
  // 獲取當前使用者資訊
  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },
  
  // 保存使用者資訊
  saveUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user))
  },
  
  // 檢查是否已登入
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token')
  },

  // 取得個人資料
  getProfile: () => {
    return axios.get<any, ApiResponse<User>>('/auth/profile')
  },

  // 更新個人資料
  updateProfile: (data: UpdateProfileRequest) => {
    return axios.put<any, ApiResponse<User>>('/auth/profile', data)
  }
}

export default authApi
