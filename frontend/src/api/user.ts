/**
 * 使用者管理 API
 * @module UserAPI
 */

import axios from './axios'
import type { ApiResponse } from './types'

/**
 * 使用者資料介面
 * @interface User
 */
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

/**
 * 使用者 API 服務
 * @namespace userApi
 */
export const userApi = {
  /**
   * 取得所有使用者
   * @description 查詢系統中所有使用者的列表
   * @returns {Promise<ApiResponse<User[]>>} 使用者列表
   * @swagger GET /api/users
   * @example
   * const users = await userApi.getAllUsers()
   * console.log(users.data) // 使用者陣列
   */
  getAllUsers: () => {
    return axios.get<any, ApiResponse<User[]>>('/users')
  },
  
  /**
   * 根據 ID 取得使用者
   * @description 查詢指定 ID 的使用者詳細資訊
   * @param {number} id - 使用者 ID
   * @returns {Promise<ApiResponse<User>>} 使用者資料
   * @swagger GET /api/users/{id}
   * @example
   * const user = await userApi.getUserById(123)
   */
  getUserById: (id: number) => {
    return axios.get<any, ApiResponse<User>>(`/users/${id}`)
  },
  
  /**
   * 建立新使用者
   * @description 創建新的系統使用者帳號
   * @param {User} user - 使用者資料
   * @param {string} user.username - 使用者名稱（必填）
   * @param {string} user.email - 電子郵件（必填）
   * @param {string} user.password - 密碼（必填）
   * @param {'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'} user.role - 使用者角色（必填）
   * @returns {Promise<ApiResponse<User>>} 創建成功的使用者資料
   * @swagger POST /api/users
   * @example
   * const newUser = await userApi.createUser({
   *   username: 'john_doe',
   *   email: 'john@example.com',
   *   password: 'SecurePass123',
   *   role: 'STAFF'
   * })
   */
  createUser: (user: User) => {
    return axios.post<any, ApiResponse<User>>('/users', user)
  },
  
  /**
   * 更新使用者
   * @description 更新指定使用者的資訊
   * @param {number} id - 使用者 ID
   * @param {User} user - 更新的使用者資料
   * @returns {Promise<ApiResponse<User>>} 更新後的使用者資料
   * @swagger PUT /api/users/{id}
   * @example
   * const updated = await userApi.updateUser(123, {
   *   username: 'john_doe',
   *   email: 'john.new@example.com',
   *   role: 'MANAGER'
   * })
   */
  updateUser: (id: number, user: User) => {
    return axios.put<any, ApiResponse<User>>(`/users/${id}`, user)
  },
  
  /**
   * 刪除使用者
   * @description 刪除指定的使用者帳號
   * @param {number} id - 使用者 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/users/{id}
   * @example
   * await userApi.deleteUser(123)
   */
  deleteUser: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/users/${id}`)
  },
  
  /**
   * 啟用/停用使用者
   * @description 切換使用者帳號的啟用狀態
   * @param {number} id - 使用者 ID
   * @param {boolean} newStatus - 新的啟用狀態（true=啟用, false=停用）
   * @returns {Promise<ApiResponse<User>>} 更新後的使用者資料
   * @swagger PATCH /api/users/{id}/status
   * @example
   * const updated = await userApi.toggleUserStatus(123, false) // 停用使用者
   */
  toggleUserStatus: (id: number, newStatus: boolean) => {
    return axios.patch<any, ApiResponse<User>>(`/users/${id}/status`, null, {
      params: { enabled: newStatus }
    })
  }
}

export default userApi
