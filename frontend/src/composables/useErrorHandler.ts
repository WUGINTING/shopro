/**
 * 統一錯誤處理 Composable
 * 提供一致的錯誤處理和用戶反饋
 */
import { ref } from 'vue'
import { Notify } from 'quasar'
import type { AxiosError } from 'axios'

interface ApiError {
  message?: string
  error?: string
  errors?: Record<string, string[]>
}

interface UseErrorHandlerOptions {
  defaultMessage?: string
  showNotify?: boolean
}

export function useErrorHandler(options: UseErrorHandlerOptions = {}) {
  const {
    defaultMessage = '操作失敗，請稍後再試',
    showNotify = true
  } = options

  const error = ref<string | null>(null)
  const errors = ref<Record<string, string[]>>({})

  /**
   * 從 Axios 錯誤中提取錯誤訊息
   */
  const extractErrorMessage = (err: AxiosError<ApiError>): string => {
    if (err.response?.data) {
      const data = err.response.data
      return data.message || data.error || defaultMessage
    }
    if (err.message) {
      return err.message
    }
    return defaultMessage
  }

  /**
   * 從 Axios 錯誤中提取欄位錯誤
   */
  const extractFieldErrors = (err: AxiosError<ApiError>): Record<string, string[]> => {
    if (err.response?.data?.errors) {
      return err.response.data.errors
    }
    return {}
  }

  /**
   * 處理 API 錯誤
   */
  const handleError = (err: unknown, customMessage?: string) => {
    const axiosError = err as AxiosError<ApiError>
    const message = customMessage || extractErrorMessage(axiosError)

    error.value = message
    errors.value = extractFieldErrors(axiosError)

    if (showNotify) {
      Notify.create({
        type: 'negative',
        message,
        position: 'top',
        timeout: 3000,
        actions: [{ icon: 'close', color: 'white' }]
      })
    }

    console.error('API Error:', err)
  }

  /**
   * 處理成功訊息
   */
  const handleSuccess = (message: string) => {
    error.value = null
    errors.value = {}

    if (showNotify) {
      Notify.create({
        type: 'positive',
        message,
        position: 'top',
        timeout: 2000
      })
    }
  }

  /**
   * 清除錯誤狀態
   */
  const clearError = () => {
    error.value = null
    errors.value = {}
  }

  /**
   * 取得特定欄位的錯誤
   */
  const getFieldError = (field: string): string | undefined => {
    return errors.value[field]?.[0]
  }

  /**
   * 檢查是否有錯誤
   */
  const hasError = (): boolean => {
    return error.value !== null || Object.keys(errors.value).length > 0
  }

  return {
    error,
    errors,
    handleError,
    handleSuccess,
    clearError,
    getFieldError,
    hasError
  }
}
