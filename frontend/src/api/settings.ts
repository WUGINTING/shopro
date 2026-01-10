/**
 * 系統設定相關 API
 * @module SettingsAPI
 */

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

/**
 * 系統設定 API 服務
 * @namespace systemSettingsApi
 */
const systemSettingsApi = {
  /**
   * 獲取系統設置
   * @description 查詢系統的基本設定資訊
   * @returns {Promise<{ data: SystemSettings }>} 系統設定資料
   * @swagger GET /api/settings/system
   * @example
   * const settings = await systemSettingsApi.getSystemSettings()
   * console.log(settings.data.storeName)
   */
  getSystemSettings: async () => {
    return axiosInstance.get<any, { data: SystemSettings }>('/settings/system')
  },

  /**
   * 更新系統設置
   * @description 更新系統的基本設定
   * @param {Partial<SystemSettings>} settings - 更新的設定資料
   * @returns {Promise<{ data: SystemSettings }>} 更新後的設定資料
   * @swagger PUT /api/settings/system
   * @example
   * const updated = await systemSettingsApi.updateSystemSettings({
   *   storeName: '新商店名稱',
   *   currency: 'TWD'
   * })
   */
  updateSystemSettings: async (settings: Partial<SystemSettings>) => {
    return axiosInstance.put<any, { data: SystemSettings }>('/settings/system', settings)
  },

  /**
   * 獲取郵件設置
   * @description 查詢 SMTP 郵件伺服器設定
   * @returns {Promise<{ data: EmailSettings }>} 郵件設定資料
   * @swagger GET /api/settings/email
   * @example
   * const emailSettings = await systemSettingsApi.getEmailSettings()
   */
  getEmailSettings: async () => {
    return axiosInstance.get<any, { data: EmailSettings }>('/settings/email')
  },

  /**
   * 更新郵件設置
   * @description 更新 SMTP 郵件伺服器設定
   * @param {Partial<EmailSettings>} settings - 更新的郵件設定
   * @returns {Promise<{ data: EmailSettings }>} 更新後的郵件設定
   * @swagger PUT /api/settings/email
   * @example
   * const updated = await systemSettingsApi.updateEmailSettings({
   *   smtpServer: 'smtp.gmail.com',
   *   smtpPort: 587
   * })
   */
  updateEmailSettings: async (settings: Partial<EmailSettings>) => {
    return axiosInstance.put<any, { data: EmailSettings }>('/settings/email', settings)
  },

  /**
   * 測試郵件設置
   * @description 發送測試郵件驗證 SMTP 設定是否正確
   * @param {string} testEmail - 測試郵件地址
   * @returns {Promise<any>} 測試結果
   * @swagger POST /api/settings/email/test
   * @example
   * await systemSettingsApi.testEmailSettings('test@example.com')
   */
  testEmailSettings: async (testEmail: string) => {
    return axiosInstance.post('/settings/email/test', { testEmail })
  },

  /**
   * 獲取通知設置
   * @description 查詢系統通知功能設定
   * @returns {Promise<{ data: NotificationSettings }>} 通知設定資料
   * @swagger GET /api/settings/notification
   * @example
   * const notificationSettings = await systemSettingsApi.getNotificationSettings()
   */
  getNotificationSettings: async () => {
    return axiosInstance.get<any, { data: NotificationSettings }>('/settings/notification')
  },

  /**
   * 更新通知設置
   * @description 更新系統通知功能設定
   * @param {Partial<NotificationSettings>} settings - 更新的通知設定
   * @returns {Promise<{ data: NotificationSettings }>} 更新後的通知設定
   * @swagger PUT /api/settings/notification
   * @example
   * const updated = await systemSettingsApi.updateNotificationSettings({
   *   orderNotification: true,
   *   paymentNotification: true
   * })
   */
  updateNotificationSettings: async (settings: Partial<NotificationSettings>) => {
    return axiosInstance.put<any, { data: NotificationSettings }>('/settings/notification', settings)
  },

  /**
   * 獲取安全設置
   * @description 查詢系統安全相關設定
   * @returns {Promise<{ data: SecuritySettings }>} 安全設定資料
   * @swagger GET /api/settings/security
   * @example
   * const securitySettings = await systemSettingsApi.getSecuritySettings()
   */
  getSecuritySettings: async () => {
    return axiosInstance.get<any, { data: SecuritySettings }>('/settings/security')
  },

  /**
   * 更新安全設置
   * @description 更新系統安全相關設定
   * @param {Partial<SecuritySettings>} settings - 更新的安全設定
   * @returns {Promise<{ data: SecuritySettings }>} 更新後的安全設定
   * @swagger PUT /api/settings/security
   * @example
   * const updated = await systemSettingsApi.updateSecuritySettings({
   *   passwordMinLength: 8,
   *   requireUppercase: true
   * })
   */
  updateSecuritySettings: async (settings: Partial<SecuritySettings>) => {
    return axiosInstance.put<any, { data: SecuritySettings }>('/settings/security', settings)
  },

  /**
   * 獲取所有設置
   * @description 查詢系統所有設定資訊
   * @returns {Promise<any>} 所有設定資料
   * @swagger GET /api/settings/all
   * @example
   * const allSettings = await systemSettingsApi.getAllSettings()
   */
  getAllSettings: async () => {
    return axiosInstance.get<any, any>('/settings/all')
  },

  /**
   * 重置為默認設置
   * @description 將所有設定重置為系統預設值
   * @returns {Promise<any>} 重置結果
   * @swagger POST /api/settings/reset-defaults
   * @warning 此操作將會清除所有自定義設定
   * @example
   * await systemSettingsApi.resetToDefaults()
   */
  resetToDefaults: async () => {
    return axiosInstance.post('/settings/reset-defaults', {})
  },

  /**
   * 導出設置
   * @description 導出所有系統設定為檔案
   * @returns {Promise<any>} 設定檔案 (Blob)
   * @swagger GET /api/settings/export
   * @example
   * const blob = await systemSettingsApi.exportSettings()
   * // 下載檔案
   */
  exportSettings: async () => {
    return axiosInstance.get('/settings/export', { responseType: 'blob' })
  },

  /**
   * 導入設置
   * @description 從檔案導入系統設定
   * @param {File} file - 設定檔案
   * @returns {Promise<any>} 導入結果
   * @swagger POST /api/settings/import
   * @example
   * const file = new File([...], 'settings.json')
   * await systemSettingsApi.importSettings(file)
   */
  importSettings: async (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return axiosInstance.post('/settings/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default systemSettingsApi
