import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過支付回調記錄導覽
const TOUR_STORAGE_KEY = 'shopro-payment-callback-log-tour-completed'

export const isPaymentCallbackLogTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPaymentCallbackLogTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPaymentCallbackLogTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPaymentCallbackLogTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建支付回調記錄導覽步驟
export const createPaymentCallbackLogTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到支付回調記錄頁面！這裡是您查看所有支付閘道回調請求記錄的核心區域。您可以查看回調請求的詳細資訊，用於除錯和追蹤支付流程。',
      title: '📞 支付回調記錄導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPaymentCallbackLogTourAsCompleted()
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
          // 查找包含「支付回調記錄」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .text-h4, h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('支付回調記錄')) {
              return heading as HTMLElement
            }
          }
          // 如果找不到，返回頁面容器
          return document.querySelector('.q-pa-md') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'filter-panel',
      text: '篩選功能可以幫助您快速找到特定的回調記錄。您可以按支付閘道（ECPay、LINE PAY）、訂單編號、交易ID或狀態（成功、失敗、錯誤）進行篩選。輸入篩選條件後會自動應用。',
      title: '🔍 篩選功能',
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
          // 查找包含篩選輸入框的卡片
          const cards = document.querySelectorAll('.q-card')
          for (const card of Array.from(cards)) {
            const inputs = card.querySelectorAll('.q-input, .q-select')
            if (inputs.length > 0) {
              return card as HTMLElement
            }
          }
          // 如果找不到，返回第一個卡片作為後備
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
      id: 'callback-log-table',
      text: '回調記錄列表顯示了所有回調請求的詳細資訊，包括ID、支付閘道、訂單編號、交易ID、狀態、處理時間、請求IP和建立時間。您可以點擊表頭進行排序，方便查找和管理記錄。',
      title: '📋 回調記錄列表',
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
        element: '.q-table',
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'callback-log-actions',
      text: '每個回調記錄都有「查看詳情」按鈕，可以查看完整的回調請求資訊，包括原始請求參數、解析後的響應、處理結果和錯誤訊息。這對於排查支付問題非常有用。',
      title: '⚙️ 記錄操作',
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
        element: '.q-table',
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'callback-log-detail',
      text: '回調記錄詳情對話框顯示了完整的回調請求資訊，包括基本信息（記錄ID、支付閘道、訂單編號、交易ID、狀態、處理時間、請求IP、建立時間）、處理結果、原始請求參數（可複製）、解析後的響應（可複製）和User-Agent。這些資訊對於除錯和審計非常重要。',
      title: '📝 回調記錄詳情',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPaymentCallbackLogTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 查找對話框
          const dialog = document.querySelector('.q-dialog')
          if (dialog) {
            return dialog as HTMLElement
          }
          // 如果找不到，使用頁面作為後備
          return document.querySelector('.q-pa-md') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動支付回調記錄導覽
export const startPaymentCallbackLogTour = (force: boolean = false): void => {
  if (!force && isPaymentCallbackLogTourCompleted()) {
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
    const tourInstance = initPaymentCallbackLogTour()
    const steps = createPaymentCallbackLogTourSteps()
    
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
export const cancelPaymentCallbackLogTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

