import axios, { type AxiosInstance, type AxiosError } from 'axios'

// API 基礎配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 創建 axios 實例
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在這裡添加認證 token
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

// 回應攔截器
apiClient.interceptors.response.use(
  (response) => {
    // 統一處理 API 回應格式
    return response.data
  },
  (error: AxiosError) => {
    // 統一錯誤處理
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          // 未授權，可以跳轉到登入頁
          console.error('未授權，請重新登入')
          break
        case 403:
          console.error('無權限存取')
          break
        case 404:
          console.error('請求的資源不存在')
          break
        case 500:
          console.error('伺服器錯誤')
          break
        default:
          console.error(`請求失敗: ${status}`)
      }
    } else if (error.request) {
      console.error('無法連接到伺服器')
    } else {
      console.error('請求設定錯誤')
    }
    return Promise.reject(error)
  }
)

export default apiClient
