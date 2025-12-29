import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

// 創建 axios 實例
const axiosInstance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
axiosInstance.interceptors.request.use(
  (config) => {
    // 從 localStorage 獲取 token 並添加到請求標頭
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 回應攝截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error) => {
    // 處理錯誤回應
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授權，清除 token 並跳轉到登入頁
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
          break
        case 403:
          console.error('沒有權限存取')
          break
        case 404:
          console.error('請求的資源不存在')
          break
        case 500:
          console.error('服務器錯誤')
          break
        default:
          console.error('請求失敗')
      }
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
