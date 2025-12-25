import axios from './axios'
import type { ApiResponse } from './types'

// 认证相关接口
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

// 认证 API
export const authApi = {
  // 用户登录
  login: (data: LoginRequest) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/login', data)
  },
  
  // 用户注册
  register: (data: { username: string; email: string; password: string }) => {
    return axios.post<any, ApiResponse<AuthResponse>>('/auth/register', data)
  },
  
  // 登出（客户端清除 token）
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  
  // 获取当前用户信息
  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },
  
  // 保存用户信息
  saveUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user))
  },
  
  // 检查是否已登录
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
