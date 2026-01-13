/**
 * Cookie 工具模組
 * 提供統一的 Cookie 操作方法，支援多環境前綴管理
 * @module CookieUtils
 */

import Cookies from 'js-cookie';
import { getEnvs } from '@/utils/envs';
import { cookieWhiteList } from '@/config/constant';

const { hostname } = window.location;

/**
 * Cookie 代理類別
 * 提供環境隔離的 Cookie 操作，自動加上環境前綴
 * @class CookieProxy
 */
class CookieProxy {
  constructor() {
    this.prefix = this.getPrefix();
    this.baseParams = {
      expires: 7,
      path: '/',
      domain: hostname || undefined,
      // Secure : true,
      // SameSite : 'none',
    };
  }

  /**
   * 獲取環境前綴
   * 用途：打包一次，讓多個環境同時部署時不互相影響
   * @returns {string} 環境前綴 (dev_/prod_/test_)
   * @private
   */
  getPrefix() {
    const { envStr } = getEnvs();
    let cookiePreFix;
    if (envStr === 'dev') {
      cookiePreFix = 'dev_';
    } else if (envStr === 'prod') {
      cookiePreFix = 'prod_';
    } else {
      cookiePreFix = 'test_';
    }
    return cookiePreFix;
  }

  /**
   * 獲取所有 Cookie
   * @returns {Object} 所有 Cookie 物件
   * @example
   * const allCookies = cookies.getAll()
   */
  getAll() {
    return Cookies.get();
  }

  /**
   * 清除所有 Cookie（保留白名單）
   * 白名單定義在 @/config/constant.js 中
   * @example
   * cookies.clearAll()
   */
  clearAll() {
    const keys = Object.keys(this.getAll());
    keys.forEach(key => {
      const newKey = key.replace(this.prefix, '');
      if (cookieWhiteList.indexOf(newKey) === -1) {
        this.remove(key, false);
      }
    });
  }

  /**
   * 獲取 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {boolean} [hasPrefix=true] - 是否包含環境前綴
   * @returns {string|null} Cookie 值
   * @example
   * // 獲取帶前綴的 Cookie
   * const token = cookies.get('vite__token')
   * 
   * // 獲取不帶前綴的 Cookie
   * const rawValue = cookies.get('some_key', false)
   */
  get(key, hasPrefix = true) {
    const keyStr = hasPrefix ? this.prefix + '' + key : key;
    return Cookies.get(keyStr);
  }

  /**
   * 設定 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {string|Object} value - Cookie 值
   * @param {Object} [params] - Cookie 選項
   * @param {number|Date} [params.expires] - Cookie 過期時間（天數）
   * @param {string} [params.path] - Cookie 路徑
   * @param {string} [params.domain] - Cookie 網域
   * @param {boolean} [params.secure] - 是否只在 HTTPS 下傳輸
   * @param {string} [params.sameSite] - SameSite 屬性 ('strict' | 'lax' | 'none')
   * @returns {string} 設定後的 Cookie 值
   * @example
   * // 使用預設選項（7天過期）
   * cookies.set('vite__token', 'abc123')
   * 
   * // 自訂過期時間
   * cookies.set('remember__me', 'true', { expires: 30 })
   * 
   * // 儲存 JSON 物件
   * cookies.set('user__info', JSON.stringify({ id: 1, name: 'User' }))
   */
  set(key, value, params) {
    const options = params === undefined ? this.baseParams : params;
    const keyStr = this.prefix + '' + key;
    return Cookies.set(keyStr, value, options);
  }

  /**
   * 刪除 Cookie
   * @param {string} key - Cookie 鍵名
   * @param {boolean} [hasPrefix=true] - 是否包含環境前綴
   * @returns {void}
   * @example
   * // 刪除帶前綴的 Cookie
   * cookies.remove('vite__token')
   * 
   * // 刪除不帶前綴的 Cookie
   * cookies.remove('some_key', false)
   */
  remove(key, hasPrefix = true) {
    const keyStr = !hasPrefix ? key : this.prefix + '' + key;
    return Cookies.remove(keyStr, {
      path: '/',
      domain: hostname,
    });
  }

  /**
   * 檢查 Cookie 是否存在
   * @param {string} key - Cookie 鍵名
   * @param {boolean} [hasPrefix=true] - 是否包含環境前綴
   * @returns {boolean} 是否存在
   * @example
   * if (cookies.has('vite__token')) {
   *   console.log('使用者已登入')
   * }
   */
  has(key, hasPrefix = true) {
    const keyStr = hasPrefix ? this.prefix + '' + key : key;
    return Cookies.get(keyStr) !== undefined;
  }
}

/**
 * Cookie 工具實例（單例模式）
 * @type {CookieProxy}
 */
const cookies = new CookieProxy();

export default cookies;
