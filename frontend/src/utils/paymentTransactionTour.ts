import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過金流交易紀錄導覽
const TOUR_STORAGE_KEY = 'shopro-payment-transaction-tour-completed'

export const isPaymentTransactionTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPaymentTransactionTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPaymentTransactionTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPaymentTransactionTour = (): Shepherd.Tour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = new Shepherd.Tour({
    useModalOverlay: true,
    defaultStepOptions: {
      cancelIcon: {
        enabled: true
      },
      classes: 'shepherd-theme-custom',
      scrollTo: { behavior: 'smooth', block: 'center' },
      popperOptions: {
        modifiers: [
          {
            name: 'offset',
            options: {
              offset: [0, 12]
            }
          }
        ]
      }
    }
  })

  return tour
}

// 創建金流交易紀錄導覽步驟
export const createPaymentTransactionTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到金流交易紀錄頁面！這裡是您查看和管理所有支付交易的核心區域。您可以搜尋交易、查看交易詳情、同步交易狀態等。',
      title: '💳 金流交易紀錄導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPaymentTransactionTourAsCompleted()
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
          // 查找包含「金流交易紀錄」文字的標題
          const headings = document.querySelectorAll('.text-h4, h4, .text-h5, h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('金流交易紀錄')) {
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
      id: 'search-filters',
      text: '搜尋功能可以幫助您快速找到特定的交易。您可以按關鍵字（訂單編號或交易序號）、支付閘道（LINE PAY、綠界 ECPay、手動付款）或交易狀態（已發起、處理中、成功、失敗、已取消、已過期）進行搜尋。',
      title: '🔍 搜尋與篩選',
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
          // 查找包含搜尋輸入框的卡片
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
      id: 'transaction-table',
      text: '交易列表顯示了所有交易的詳細資訊，包括訂單編號、交易序號、支付閘道、交易狀態、金額和建立時間。您可以點擊表頭進行排序，方便查找和管理交易。',
      title: '📋 交易列表',
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
      id: 'transaction-actions',
      text: '每個交易都有操作按鈕：查看詳情（查看完整的交易資訊，包括錯誤訊息和原始回應）、同步狀態（對於處理中或已發起的交易，可以手動同步最新狀態）。',
      title: '⚙️ 交易操作',
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
      id: 'transaction-detail',
      text: '交易詳情對話框顯示了完整的交易資訊，包括訂單編號、交易ID、支付閘道、交易狀態、交易金額、建立時間、客戶姓名（如有）、錯誤訊息（如有）和原始回應（如有）。這對於排查問題和審計非常有用。',
      title: '📝 交易詳情',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPaymentTransactionTourAsCompleted()
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

// 啟動金流交易紀錄導覽
export const startPaymentTransactionTour = (force: boolean = false): void => {
  if (!force && isPaymentTransactionTourCompleted()) {
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
    const tourInstance = initPaymentTransactionTour()
    const steps = createPaymentTransactionTourSteps()
    
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
export const cancelPaymentTransactionTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

