/**
 * Axios 實例配置與攔截器
 * @module AxiosConfig
 */

import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { Notify } from 'quasar'

/**
 * 錯誤訊息對應表
 */
const ERROR_MESSAGES: Record<number, string> = {
  400: '請求參數錯誤，請檢查輸入內容',
  401: '登入已過期，請重新登入',
  403: '您沒有權限執行此操作',
  404: '請求的資源不存在',
  408: '請求超時，請稍後再試',
  429: '請求過於頻繁，請稍後再試',
  500: '伺服器發生錯誤，請稍後再試',
  502: '網路閘道錯誤，請稍後再試',
  503: '服務暫時不可用，請稍後再試',
  504: '網路閘道超時，請稍後再試'
}

/**
 * 顯示錯誤通知
 */
const showErrorNotify = (message: string) => {
  Notify.create({
    type: 'negative',
    message,
    position: 'top',
    timeout: 3000,
    actions: [{ icon: 'close', color: 'white' }]
  })
}

/**
 * 創建 axios 實例
 * @constant {AxiosInstance} axiosInstance - 配置好的 axios 實例
 */
const axiosInstance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 從 localStorage 獲取 token 並添加到請求標頭
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    showErrorNotify('請求發送失敗，請檢查網路連線')
    return Promise.reject(error)
  }
)

// 回應攔截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error) => {
    // 網路錯誤（無回應）
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        showErrorNotify('請求超時，請檢查網路連線後重試')
      } else {
        showErrorNotify('網路連線失敗，請檢查網路設定')
      }
      return Promise.reject(error)
    }

    const { status, data } = error.response

    // 401 未授權特殊處理
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (window.location.pathname !== '/login') {
        showErrorNotify('登入已過期，正在跳轉到登入頁...')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
      }
      return Promise.reject(error)
    }

    // 從後端回應取得錯誤訊息，或使用預設訊息
    const serverMessage = data?.message || data?.error
    const defaultMessage = ERROR_MESSAGES[status] || '請求失敗，請稍後再試'
    const errorMessage = serverMessage || defaultMessage

    showErrorNotify(errorMessage)

    return Promise.reject(error)
  }
)

export default axiosInstance
