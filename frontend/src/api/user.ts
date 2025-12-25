import axios from './axios'
import type { ApiResponse } from './types'

// 使用者資料類型
export interface User {
  id?: number
  username: string
  email: string
  password?: string
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'
  enabled?: boolean
  createdAt?: string
  updatedAt?: string
}

// 使用者 API
export const userApi = {
  // 取得所有使用者
  getAllUsers: () => {
    return axios.get<any, ApiResponse<User[]>>('/users')
  },
  
  // 根據 ID 取得使用者
  getUserById: (id: number) => {
    return axios.get<any, ApiResponse<User>>(`/users/${id}`)
  },
  
  // 建立新使用者
  createUser: (user: User) => {
    return axios.post<any, ApiResponse<User>>('/users', user)
  },
  
  // 更新使用者
  updateUser: (id: number, user: User) => {
    return axios.put<any, ApiResponse<User>>(`/users/${id}`, user)
  },
  
  // 刪除使用者
  deleteUser: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/users/${id}`)
  },
  
  // 啟用/停用使用者
  toggleUserStatus: (id: number, newStatus: boolean) => {
    return axios.patch<any, ApiResponse<User>>(`/users/${id}/status`, null, {
      params: { enabled: newStatus }
    })
  }
}

export default userApi
