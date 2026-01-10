/**
 * Cookie 工具模組
 * 提供統一的 Cookie 操作方法
 * @module CookieUtils
 */

import Cookies from 'js-cookie'

/**
 * Cookie 配置選項介面
 * @interface CookieOptions
 */
export interface CookieOptions {
  /** Cookie 過期時間（天數） */
  expires?: number | Date
  /** Cookie 路徑 */
  path?: string
  /** Cookie 網域 */
  domain?: string
  /** 是否只在 HTTPS 下傳輸 */
  secure?: boolean
  /** SameSite 屬性 */
  sameSite?: 'strict' | 'lax' | 'none'
}

/**
 * 預設 Cookie 配置
 */
const defaultOptions: CookieOptions = {
  expires: 7,
  path: '/'
}

/**
 * Cookie 白名單（清除時保留的鍵名）
 */
const cookieWhiteList: string[] = ['language', 'theme']

/**
 * 獲取環境前綴
 * @returns {string} 環境前綴
 */
const getEnvPrefix = (): string => {
  const mode = import.meta.env.MODE
  if (mode === 'development') return 'dev_'
  if (mode === 'production') return 'prod_'
  return 'test_'
}

/**
 * Cookie 工具類別
 */
class CookieUtil {
  private prefix: string
  private baseParams: CookieOptions

  constructor() {
    this.prefix = getEnvPrefix()
    this.baseParams = {
      expires: 7,
      path: '/',
      domain: window.location.hostname || undefined
    }
  }

  /**
   * 設定 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {any} value - Cookie 值（自動轉為 JSON 字串）
   * @param {CookieOptions} options - Cookie 選項
   * @example
   * cookieUtil.set('user', { id: 1, name: 'Admin' }, { expires: 30 })
   */
  set(key: string, value: any, options?: CookieOptions): void {
    const mergedOptions = options === undefined ? this.baseParams : { ...this.baseParams, ...options }
    const keyStr = this.prefix + key
    const stringValue = typeof value === 'string' ? value : JSON.stringify(value)
    Cookies.set(keyStr, stringValue, mergedOptions)
  }

  /**
   * 獲取 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {boolean} hasPrefix - 是否包含環境前綴
   * @returns {any} Cookie 值（自動解析 JSON）
   * @example
   * const user = cookieUtil.get('user')
   */
  get(key: string, hasPrefix: boolean = true): any {
    const keyStr = hasPrefix ? this.prefix + key : key
    const value = Cookies.get(keyStr)
    if (!value) return null

    try {
      // 嘗試解析 JSON
      return JSON.parse(value)
    } catch {
      // 如果不是 JSON，返回原始字串
      return value
    }
  }

  /**
   * 刪除 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {boolean} hasPrefix - 是否包含環境前綴
   * @example
   * cookieUtil.remove('user')
   */
  remove(key: string, hasPrefix: boolean = true): void {
    const keyStr = hasPrefix ? this.prefix + key : key
    Cookies.remove(keyStr, {
      path: '/',
      domain: window.location.hostname
    })
  }

  /**
   * 檢查 Cookie 是否存在
   * @param {string} key - Cookie 鍵名
   * @param {boolean} hasPrefix - 是否包含環境前綴
   * @returns {boolean} 是否存在
   * @example
   * if (cookieUtil.has('token')) { ... }
   */
  has(key: string, hasPrefix: boolean = true): boolean {
    const keyStr = hasPrefix ? this.prefix + key : key
    return Cookies.get(keyStr) !== undefined
  }

  /**
   * 獲取所有 Cookie
   * @returns {Record<string, any>} 所有 Cookie 物件
   * @example
   * const allCookies = cookieUtil.getAll()
   */
  getAll(): Record<string, any> {
    const cookies = Cookies.get()
    const result: Record<string, any> = {}

    for (const [key, value] of Object.entries(cookies)) {
      try {
        result[key] = JSON.parse(value)
      } catch {
        result[key] = value
      }
    }

    return result
  }

  /**
   * 清除所有 Cookie（保留白名單）
   * @example
   * cookieUtil.clearAll()
   */
  clearAll(): void {
    const keys = Object.keys(this.getAll())
    keys.forEach(key => {
      const newKey = key.replace(this.prefix, '')
      if (cookieWhiteList.indexOf(newKey) === -1) {
        this.remove(key, false)
      }
    })
  }
}

/**
 * Cookie 工具實例
 */
export const cookieUtil = new CookieUtil()

/**
 * 常用 Cookie 鍵名常數
 */
export const COOKIE_KEYS = {
  /** 認證 Token */
  TOKEN: 'auth_token',
  /** 使用者資訊 */
  USER: 'user_info',
  /** 語言設定 */
  LANGUAGE: 'language',
  /** 主題設定 */
  THEME: 'theme',
  /** 記住帳號 */
  REMEMBER_ME: 'remember_me'
} as const

/**
 * 預設匯出
 */
export default cookieUtil
