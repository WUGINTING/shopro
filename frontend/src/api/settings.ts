import axiosInstance from './axios'

export interface SystemSettings {
  id?: number
  storeName: string
  storeDescription?: string
  storeEmail: string
  storePhone: string
  storeAddress?: string
  currency: string
  timezone: string
  language: string
  taxRate?: number
  shippingFeeRate?: number
  enableNotification: boolean
  enableEmail: boolean
  enableSMS: boolean
  maintenanceMode: boolean
  siteName?: string
  siteUrl?: string
  logoUrl?: string
  faviconUrl?: string
  createdAt?: string
  updatedAt?: string
}

export interface EmailSettings {
  id?: number
  smtpServer: string
  smtpPort: number
  smtpUsername: string
  smtpPassword: string
  emailFrom: string
  emailReplyTo?: string
  enableTLS: boolean
  enableSSL: boolean
  createdAt?: string
  updatedAt?: string
}

export interface NotificationSettings {
  id?: number
  orderNotification: boolean
  paymentNotification: boolean
  userRegistrationNotification: boolean
  lowStockNotification: boolean
  notificationEmail?: string
  notificationPhone?: string
  createdAt?: string
  updatedAt?: string
}

export interface SecuritySettings {
  id?: number
  passwordMinLength: number
  passwordMaxLength: number
  requireUppercase: boolean
  requireLowercase: boolean
  requireNumbers: boolean
  requireSpecialChars: boolean
  sessionTimeout: number
  ipWhitelist?: string[]
  ipBlacklist?: string[]
  twoFactorAuthEnabled: boolean
  createdAt?: string
  updatedAt?: string
}

const systemSettingsApi = {
  // 獲取系統設置
  getSystemSettings: async () => {
    return axiosInstance.get<any, { data: SystemSettings }>('/settings/system')
  },

  // 更新系統設置
  updateSystemSettings: async (settings: Partial<SystemSettings>) => {
    return axiosInstance.put<any, { data: SystemSettings }>('/settings/system', settings)
  },

  // 獲取郵件設置
  getEmailSettings: async () => {
    return axiosInstance.get<any, { data: EmailSettings }>('/settings/email')
  },

  // 更新郵件設置
  updateEmailSettings: async (settings: Partial<EmailSettings>) => {
    return axiosInstance.put<any, { data: EmailSettings }>('/settings/email', settings)
  },

  // 測試郵件設置
  testEmailSettings: async (testEmail: string) => {
    return axiosInstance.post('/settings/email/test', { testEmail })
  },

  // 獲取通知設置
  getNotificationSettings: async () => {
    return axiosInstance.get<any, { data: NotificationSettings }>('/settings/notification')
  },

  // 更新通知設置
  updateNotificationSettings: async (settings: Partial<NotificationSettings>) => {
    return axiosInstance.put<any, { data: NotificationSettings }>('/settings/notification', settings)
  },

  // 獲取安全設置
  getSecuritySettings: async () => {
    return axiosInstance.get<any, { data: SecuritySettings }>('/settings/security')
  },

  // 更新安全設置
  updateSecuritySettings: async (settings: Partial<SecuritySettings>) => {
    return axiosInstance.put<any, { data: SecuritySettings }>('/settings/security', settings)
  },

  // 獲取所有設置
  getAllSettings: async () => {
    return axiosInstance.get<any, any>('/settings/all')
  },

  // 重置為默認設置
  resetToDefaults: async () => {
    return axiosInstance.post('/settings/reset-defaults', {})
  },

  // 導出設置
  exportSettings: async () => {
    return axiosInstance.get('/settings/export', { responseType: 'blob' })
  },

  // 導入設置
  importSettings: async (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return axiosInstance.post('/settings/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default systemSettingsApi
