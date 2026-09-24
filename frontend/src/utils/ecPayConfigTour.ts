import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過 ECPay 支付配置導覽
const TOUR_STORAGE_KEY = 'shopro-ecpay-config-tour-completed'

export const isEcPayConfigTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markEcPayConfigTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetEcPayConfigTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initEcPayConfigTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建 ECPay 支付配置導覽步驟
export const createEcPayConfigTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到 ECPay 支付配置頁面！這裡可以設置綠界 ECPay 支付閘道的相關配置資訊。讓我為您介紹如何使用這個配置系統。',
      title: '⚙️ ECPay 支付配置導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markEcPayConfigTourAsCompleted()
            tour?.cancel()
          }
        },
        {
          text: '開始導覽',
          action: () => tour?.next()
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「ECPay 支付配置」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('ECPay 支付配置')) {
              return heading as HTMLElement
            }
          }
          // 如果找不到，返回頁面容器
          return document.querySelector('.q-page') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'basic-config',
      text: '基本配置區域包含商店代號（MerchantID）、API URL 等核心資訊。這些資訊由 ECPay 提供，請確保正確填寫。',
      title: '📝 基本配置',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '下一步',
          action: () => tour?.next()
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「商店代號」的輸入框
          const inputs = document.querySelectorAll('.q-input')
          for (const input of Array.from(inputs)) {
            const label = input.querySelector('label')
            if (label && label.textContent?.includes('商店代號')) {
              const card = input.closest('.q-card')
              if (card) {
                return card as HTMLElement
              }
              return input as HTMLElement
            }
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'security-config',
      text: '安全配置區域包含 HashKey 和 HashIV，這些是重要的安全參數。請妥善保管這些資訊，不要外洩。您可以點擊眼睛圖標來顯示或隱藏這些敏感資訊。',
      title: '🔐 安全配置',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '下一步',
          action: () => tour?.next()
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「HashKey」的輸入框
          const inputs = document.querySelectorAll('.q-input')
          for (const input of Array.from(inputs)) {
            const label = input.querySelector('label')
            if (label && (label.textContent?.includes('HashKey') || label.textContent?.includes('HashIV'))) {
              const card = input.closest('.q-card')
              if (card) {
                return card as HTMLElement
              }
              return input as HTMLElement
            }
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'url-config',
      text: 'URL 配置區域包含返回 URL 和通知 URL。返回 URL 是付款完成後導向的頁面，通知 URL 是 ECPay 回調通知的接口地址，用於接收支付結果。',
      title: '🔗 URL 配置',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '下一步',
          action: () => tour?.next()
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「返回 URL」或「通知 URL」的輸入框
          const inputs = document.querySelectorAll('.q-input')
          for (const input of Array.from(inputs)) {
            const label = input.querySelector('label')
            if (label && (label.textContent?.includes('返回 URL') || label.textContent?.includes('通知 URL'))) {
              const card = input.closest('.q-card')
              if (card) {
                return card as HTMLElement
              }
              return input as HTMLElement
            }
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'settings-config',
      text: '設定區域包含測試模式和啟用狀態。測試模式用於在測試環境中驗證支付流程，正式上線前請切換到正式環境。啟用狀態用於控制此配置是否生效。',
      title: '⚙️ 設定選項',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markEcPayConfigTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「測試模式」或「啟用配置」的切換開關
          const toggles = document.querySelectorAll('.q-toggle')
          const firstToggle = toggles[0]
          if (firstToggle) {
            const card = firstToggle.closest('.q-card')
            if (card) {
              return card as HTMLElement
            }
            return firstToggle as HTMLElement
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動 ECPay 支付配置導覽
export const startEcPayConfigTour = (force: boolean = false): void => {
  if (!force && isEcPayConfigTourCompleted()) {
    return
  }

  // 如果已經有導覽實例，先取消它
  if (tour) {
    try {
      tour.cancel()
    } catch (e) {
      // 忽略取消錯誤
    }
    tour = null
  }

  // 等待 DOM 準備好
  setTimeout(() => {
    const tourInstance = initEcPayConfigTour()
    const steps = createEcPayConfigTourSteps()
    
    // 清除所有舊步驟
    tourInstance.steps = []
    
    steps.forEach(step => {
      tourInstance.addStep(step)
    })

    try {
      tourInstance.start()
    } catch (error) {
      console.error('啟動導覽失敗:', error)
    }
  }, 100)
}

// 停止導覽
export const cancelEcPayConfigTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

