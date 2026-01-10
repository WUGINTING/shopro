/**
 * i18n 國際化配置模組
 * 提供多語系支援（繁體中文、英文）
 * @module I18n
 */

import { createI18n } from 'vue-i18n'
import tc from './tc'
import en from './en'
import { cookieUtil, COOKIE_KEYS } from '@/utils/cookies'

/**
 * 支援的語系類型
 */
export type Locale = 'tc' | 'en'

/**
 * 語系選項
 */
export const LOCALES = {
  TC: 'tc' as Locale,
  EN: 'en' as Locale
}

/**
 * 語系顯示名稱
 */
export const LOCALE_NAMES: Record<Locale, string> = {
  tc: '繁體中文',
  en: 'English'
}

/**
 * 獲取預設語系
 * 優先順序：Cookie > 瀏覽器語言 > 繁體中文
 * @returns {Locale} 語系代碼
 */
const getDefaultLocale = (): Locale => {
  // 1. 從 Cookie 讀取
  const savedLocale = cookieUtil.get(COOKIE_KEYS.LANGUAGE)
  if (savedLocale && (savedLocale === 'tc' || savedLocale === 'en')) {
    return savedLocale
  }

  // 2. 從瀏覽器語言判斷
  const browserLang = navigator.language.toLowerCase()
  if (browserLang.includes('zh-tw') || browserLang.includes('zh-hk')) {
    return LOCALES.TC
  }
  if (browserLang.includes('en')) {
    return LOCALES.EN
  }

  // 3. 預設繁體中文
  return LOCALES.TC
}

/**
 * 語系訊息
 */
const messages = {
  tc,
  en
}

/**
 * 建立 i18n 實例
 */
const i18n = createI18n({
  // 使用 Composition API 模式
  legacy: false,
  // 全域注入 $t
  globalInjection: true,
  // 預設語系
  locale: getDefaultLocale(),
  // 備用語系
  fallbackLocale: LOCALES.TC,
  // 語系訊息
  messages,
  // 缺少翻譯時不顯示警告
  missingWarn: false,
  fallbackWarn: false
})

/**
 * 切換語系
 * @param {Locale} locale - 目標語系
 * @example
 * setLocale('en')
 */
export const setLocale = (locale: Locale): void => {
  if (i18n.global.locale) {
    i18n.global.locale.value = locale
    // 儲存到 Cookie
    cookieUtil.set(COOKIE_KEYS.LANGUAGE, locale, { expires: 365 })
    // 更新 HTML lang 屬性
    document.documentElement.lang = locale
  }
}

/**
 * 獲取當前語系
 * @returns {Locale} 當前語系代碼
 * @example
 * const currentLocale = getLocale()
 */
export const getLocale = (): Locale => {
  return (i18n.global.locale?.value || LOCALES.TC) as Locale
}

/**
 * 獲取翻譯函式（在 setup 外使用）
 * @param {string} key - 翻譯鍵名
 * @param {any} params - 參數
 * @returns {string} 翻譯後的文字
 * @example
 * const text = t('common.confirm')
 */
export const t = (key: string, params?: any): string => {
  return i18n.global.t(key, params)
}

/**
 * 預設匯出 i18n 實例
 */
export default i18n
