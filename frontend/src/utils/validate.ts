/**
 * 表單驗證工具模組
 * 提供常用的表單欄位驗證規則與工具函式
 * @module ValidateUtils
 */

/**
 * 驗證規則函式類型（Quasar 相容）
 */
export type ValidationRule = (val: any) => boolean | string

/**
 * 正則表達式常數
 */
export const REGEX = {
  /** 電子郵件格式 */
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  /** 台灣手機號碼格式 (09xx-xxxxxx) */
  PHONE_TW: /^09\d{8}$/,
  /** 中國手機號碼格式 */
  PHONE_CN: /^(0|86|17951)?(13[0-9]|14[0-9]|15[0-9]|16[0-9]|17[0-9]|18[0-9])[0-9]{8}$/,
  /** 網址格式 */
  URL: /^(https?:|mailto:|tel:)/,
  /** 數字格式 */
  NUMBER: /^\d+$/,
  /** 正整數 */
  POSITIVE_INTEGER: /^[1-9]\d*$/,
  /** 正數（含小數） */
  POSITIVE_NUMBER: /^\d+(\.\d+)?$/,
  /** 密碼（至少 8 碼，包含英數字） */
  PASSWORD: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
  /** 密碼（強度高：至少 8 碼，包含大小寫英文、數字、特殊字元） */
  PASSWORD_STRONG: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
  /** HTTP URL 檢查 */
  HTTP_URL: /^(?!.*\s)(?!.*<.*>)(?!.*http.*http)(?=^https?:\/\/[^\s/$.?#].[^\s]*$).+$/
} as const

/**
 * 驗證工具類別
 */
class ValidateUtil {
  /**
   * 必填驗證（Quasar 相容）
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   * @example
   * <q-input :rules="[validateUtil.required('請輸入名稱')]" />
   */
  required(message: string = '此欄位為必填'): ValidationRule {
    return (val: any) => {
      if (val === null || val === undefined || val === '') return message
      if (typeof val === 'string' && val.trim() === '') return message
      if (Array.isArray(val) && val.length === 0) return message
      return true
    }
  }

  /**
   * 電子郵件格式驗證
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  email(message: string = '請輸入有效的電子郵件地址'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      const trimmedEmail = String(val).trim()
      if (trimmedEmail.length > 254) return message
      return REGEX.EMAIL.test(trimmedEmail) || message
    }
  }

  /**
   * 手機號碼驗證（台灣格式）
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  phoneTW(message: string = '請輸入有效的手機號碼'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      return REGEX.PHONE_TW.test(String(val).trim()) || message
    }
  }

  /**
   * 手機號碼驗證（中國格式）
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  phoneCN(message: string = '請輸入有效的手機號碼'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      return REGEX.PHONE_CN.test(String(val).trim()) || message
    }
  }

  /**
   * 數字驗證
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  number(message: string = '請輸入數字'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      return !isNaN(Number(val)) || message
    }
  }

  /**
   * 正整數驗證
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  positiveInteger(message: string = '請輸入正整數'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      return REGEX.POSITIVE_INTEGER.test(String(val)) || message
    }
  }

  /**
   * 最小值驗證
   * @param {number} min - 最小值
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  min(min: number, message?: string): ValidationRule {
    return (val: any) => {
      if (!val) return true
      const num = Number(val)
      const msg = message || `最小值為 ${min}`
      return num >= min || msg
    }
  }

  /**
   * 最大值驗證
   * @param {number} max - 最大值
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  max(max: number, message?: string): ValidationRule {
    return (val: any) => {
      if (!val) return true
      const num = Number(val)
      const msg = message || `最大值為 ${max}`
      return num <= max || msg
    }
  }

  /**
   * 最小長度驗證
   * @param {number} min - 最小長度
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  minLength(min: number, message?: string): ValidationRule {
    return (val: any) => {
      if (!val) return true
      const msg = message || `最少 ${min} 個字元`
      return String(val).length >= min || msg
    }
  }

  /**
   * 最大長度驗證
   * @param {number} max - 最大長度
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  maxLength(max: number, message?: string): ValidationRule {
    return (val: any) => {
      if (!val) return true
      const msg = message || `最多 ${max} 個字元`
      return String(val).length <= max || msg
    }
  }

  /**
   * 密碼強度驗證（基本：至少 8 碼，包含英數字）
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  password(message: string = '密碼至少 8 碼，需包含英文與數字'): ValidationRule {
    return (val: any) => {
      if (!val) return true
      return REGEX.PASSWORD.test(val) || message
    }
  }

  /**
   * 確認密碼驗證
   * @param {string} password - 原始密碼
   * @param {string} message - 錯誤訊息
   * @returns {ValidationRule} 驗證規則函式
   */
  confirmPassword(password: string, message: string = '兩次輸入的密碼不一致'): ValidationRule {
    return (val: any) => {
      return val === password || message
    }
  }
}

/**
 * 驗證工具實例
 */
export const validateUtil = new ValidateUtil()

// ==================== 以下為原有工具函式（保留相容性） ====================

/**
 * 檢查是否為外部連結
 * @param {string} path - 路徑
 * @returns {boolean} 是否為外部連結
 */
export function isExternal(path: string): boolean {
  return REGEX.URL.test(path)
}

/**
 * 驗證使用者名稱
 * @param {string} str - 使用者名稱
 * @returns {boolean} 是否有效
 */
export function validUsername(str: string): boolean {
  const valid_map = ['admin', 'editor', 'buyer']
  return valid_map.indexOf(str.trim()) >= 0
}

/**
 * 驗證電子郵件
 * @param {string} email - 電子郵件
 * @returns {boolean} 是否有效
 */
export function validEmail(email: string): boolean {
  if (!email || typeof email !== 'string') return false
  const trimmedEmail = email.trim()
  if (trimmedEmail.length > 254) return false
  return REGEX.EMAIL.test(trimmedEmail)
}

/**
 * 驗證字串長度（中文算 1 字元）
 * @param {string} str - 字串
 * @param {number} number - 最大長度
 * @returns {boolean} 是否符合長度限制
 */
export function validStr(str: string, number: number): boolean {
  let len = 0
  if (str) {
    for (let i = 0; i < str.length; i++) {
      if (str.charAt(i).match(/[^\x00-\xff]/gi) == null) len++
      else len += 1
    }
  }
  return parseInt(String(len)) <= number
}

/**
 * 驗證手機號碼（中國格式）
 * @param {string} arg - 手機號碼
 * @returns {boolean} 是否有效
 */
export function validPhone(arg: string): boolean {
  const val = String(arg).trim()
  return REGEX.PHONE_CN.test(val)
}

/**
 * 複製文字到剪貼簿
 * @param {string} data - 要複製的內容
 * @returns {Promise<void>}
 */
export async function copyText(data: string): Promise<void> {
  if (data) {
    try {
      await navigator.clipboard.writeText(data)
      return Promise.resolve()
    } catch (error) {
      return Promise.reject(error)
    }
  }
  return Promise.reject(new Error('No data to copy'))
}

/**
 * 格式化檔案副檔名
 * @param {string} name - 完整檔案路徑名稱
 * @returns {object} 包含檔名和副檔名的物件
 */
export function formatFileExtension(name: string): { fName: string; extension: string } {
  name = name.replace(/"/g, '')
  const fName = name.split('.').shift() || ''
  const extension = name.split('.').pop() || ''
  return { fName, extension }
}

/**
 * 格式化金額
 * @param {number|string} number - 要格式化的數字
 * @param {number|null} decimals - 保留幾位小數
 * @param {string} decPoint - 小數點符號
 * @param {string} thousandsSep - 千分位符號
 * @param {boolean} removeTrailingZeros - 是否移除小數末尾的零
 * @returns {string} 格式化後的金額字串
 */
export const formatMoney = (
  number: number | string,
  decimals: number | null = null,
  decPoint: string = '.',
  thousandsSep: string = ',',
  removeTrailingZeros: boolean = false
): string => {
  const numStr = (number + '').replace(/[^0-9+-Ee.]/g, '')
  const n = !isFinite(+numStr) ? 0 : +numStr

  let result: string[]
  if (decimals === null) {
    result = n.toString().split('.')
  } else {
    const prec = !isFinite(+decimals) ? 0 : Math.abs(decimals)
    const toFixedFix = (n: number, prec: number) => {
      const k = Math.pow(10, prec)
      return '' + Math.ceil(n * k) / k
    }
    result = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.')
  }

  const re = /(-?\d+)(\d{3})/
  while (re.test(result[0])) {
    result[0] = result[0].replace(re, '$1' + thousandsSep + '$2')
  }

  if (result[1]) {
    if (decimals !== null) {
      const prec = !isFinite(+decimals) ? 0 : Math.abs(decimals)
      if (result[1].length < prec) {
        result[1] += new Array(prec - result[1].length + 1).join('0')
      }
    }

    if (removeTrailingZeros) {
      result[1] = result[1].replace(/0+$/, '')
      if (result[1] === '') {
        result[1] = undefined as any
      }
    }
  }

  return result[1] ? result[0] + decPoint + result[1] : result[0]
}

/**
 * 格式化金額數字（移除末尾零的版本）
 * @param {number|string} num - 要格式化的數字
 * @returns {string} 格式化後的金額字串
 */
export const formatMoneyNumber = (num: number | string): string => {
  return formatMoney(num, null, '.', ',', true)
}

/**
 * 轉換成駝峰命名
 * @param {any} data - 物件或字串
 * @param {string} type - 類型（'object' 或其他）
 * @returns {any} 轉換後的結果
 */
export function toHump(data: any, type?: string): any {
  const convert = (str: string) => {
    return str.toLowerCase().replace(/_(\w)/g, (all, letter) => {
      return letter.toUpperCase()
    })
  }
  if (type === 'object') {
    for (const obj of Object.keys(data)) {
      data[convert(obj)] = data[obj]
      delete data[obj]
    }
    return data
  } else {
    return convert(data)
  }
}

/**
 * 檢查字串是否是有效的 HTTP URL
 * @param {string} str - 字串
 * @returns {boolean} 是否為有效的 HTTP URL
 */
export function isValidHttpUrl(str: string): boolean {
  try {
    const newUrl = new URL(str)
    if (newUrl.protocol !== 'http:' && newUrl.protocol !== 'https:') {
      return false
    }
    return REGEX.HTTP_URL.test(str)
  } catch (err) {
    return false
  }
}

/**
 * 取得 Blob 回傳
 * @param {Blob} blob - Blob 物件
 * @returns {Promise<any>} 解析後的資料
 */
export function getBlob(blob: Blob): Promise<any> {
  return blob
    .text()
    .then(data => {
      return JSON.parse(data)
    })
    .catch(err => {
      return err
    })
}

/**
 * 篩選檔案格式
 * @param {File} file - 檔案物件
 * @param {string[]} accept - 允許的格式陣列
 * @returns {boolean} 是否符合格式
 */
export function validFile(file: File, accept: string[]): boolean {
  return file && accept.some(el => file.name.includes(el))
}

/**
 * 檢查是否為數值
 * @param {any} val - 值
 * @returns {boolean} 是否為數值
 */
export const isNumber = (val: any): boolean => {
  return /^-?[\d.]+(?:e-?\d+)?$/.test(val)
}

/**
 * ISO 8601 日期時間格式轉換
 * @param {string|Date} input - 日期時間字串或 Date 物件
 * @returns {string} 格式化後的日期時間
 */
export const formatDateTime = (input: string | Date): string => {
  if (typeof input === 'string') {
    const date = new Date(input)
    const year = date.getUTCFullYear()
    const month = (date.getUTCMonth() + 1).toString().padStart(2, '0')
    const day = date.getUTCDate().toString().padStart(2, '0')
    const hours = date.getUTCHours()
    const minutes = date.getUTCMinutes()
    const seconds = date.getUTCSeconds()

    let formattedDateTime = `${year}/${month}/${day}`
    if (hours || minutes || seconds) {
      formattedDateTime += ` ${hours.toString().padStart(2, '0')}`
      formattedDateTime += `${minutes ? ':' + minutes.toString().padStart(2, '0') : ''}`
      formattedDateTime += `${seconds ? ':' + seconds.toString().padStart(2, '0') : ''}`
    }
    return formattedDateTime
  }

  if (input instanceof Date) {
    const year = input.getFullYear()
    const month = (input.getMonth() + 1).toString().padStart(2, '0')
    const day = input.getDate().toString().padStart(2, '0')
    const hours = input.getHours()
    const minutes = input.getMinutes()
    const seconds = input.getSeconds()

    let formattedDateTime = `${year}/${month}/${day}`
    if (hours || minutes || seconds) {
      formattedDateTime += ` ${hours.toString().padStart(2, '0')}`
      formattedDateTime += `${minutes ? ':' + minutes.toString().padStart(2, '0') : ''}`
      formattedDateTime += `${seconds ? ':' + seconds.toString().padStart(2, '0') : ''}`
    }
    return formattedDateTime
  }

  return formatDateTime(String(input))
}

/**
 * 檢查日期格式是否有效
 * @param {Date} date - Date 物件
 * @returns {boolean} 是否有效
 */
export const isValidDate = (date: Date): boolean => {
  return date instanceof Date && !isNaN(date.getTime())
}

/**
 * 將單字轉換為 ASCII 碼並加總
 * @param {string} word - 單字
 * @returns {number} ASCII 碼總和
 */
export const calculateASCIISum = (word: string): number => {
  let sum = 0
  for (let i = 0; i < word.length; i++) {
    sum += word.charCodeAt(i)
  }
  return sum
}

/**
 * 取得下一個字母
 * @param {string} column - 欄位字母
 * @returns {string} 下一個字母
 */
export const getNextColumn = (column: string): string => {
  const lastStr = column[column.length - 1]
  if (lastStr === 'Z') {
    return column.length > 1 ? `${getNextColumn(column.slice(0, -1))}A` : 'AA'
  } else {
    return `${column.slice(0, -1)}${String.fromCharCode(lastStr.charCodeAt(0) + 1)}`
  }
}

/**
 * 取得兩個字母之間的所有字母
 * @param {string} start - 起始字母
 * @param {string} end - 結束字母
 * @returns {string[]} 字母陣列
 */
export const getAlphabetRange = (start: string, end: string): string[] => {
  let newStart = start
  let newEnd = end
  if (calculateASCIISum(start) > calculateASCIISum(end)) {
    newStart = end
    newEnd = start
  }

  const result: string[] = []
  let current = newStart
  while (current !== newEnd) {
    result.push(current)
    current = getNextColumn(current)
  }
  result.push(newEnd)
  return result
}

/**
 * 預設匯出驗證工具實例
 */
export default validateUtil
