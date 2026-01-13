import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過金流儀表板導覽
const TOUR_STORAGE_KEY = 'shopro-payment-dashboard-tour-completed'

export const isPaymentDashboardTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPaymentDashboardTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPaymentDashboardTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPaymentDashboardTour = (): Shepherd.Tour => {
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

// 創建金流儀表板導覽步驟
export const createPaymentDashboardTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到金流儀表板！這裡提供了完整的支付統計資訊和數據視覺化。讓我為您介紹各個功能區域。',
      title: '💰 金流儀表板導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPaymentDashboardTourAsCompleted()
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
          // 查找包含「金流儀表板」文字的標題
          const headings = document.querySelectorAll('.text-h4, h4, .q-page .text-h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('金流儀表板')) {
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
      id: 'statistics-cards',
      text: '這裡顯示了重要的金流統計數據：今日成交金額、今日成功率、本月成交金額和今日退款數量。這些指標幫助您快速了解業務狀況。',
      title: '📊 統計數據卡片',
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
          // 查找第一個統計卡片
          const statCards = document.querySelectorAll('.stat-card')
          if (statCards.length > 0) {
            // 找到包含所有統計卡片的行
            const row = statCards[0].closest('.row')
            if (row) {
              return row as HTMLElement
            }
            return statCards[0] as HTMLElement
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
      id: 'gateway-chart',
      text: '支付管道佔比圓餅圖以視覺化方式展示各支付管道的交易金額分佈。您可以清楚地看到 LINE PAY、ECPay 等不同支付方式的佔比。',
      title: '📈 支付管道佔比圖',
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
          // 查找包含「支付管道佔比」文字的卡片
          const cards = document.querySelectorAll('.q-card')
          for (const card of Array.from(cards)) {
            const heading = card.querySelector('.text-h6, h6')
            if (heading && heading.textContent?.includes('支付管道佔比')) {
              return card as HTMLElement
            }
          }
          // 如果找不到，返回第一個包含 canvas 的卡片
          const chartCard = document.querySelector('canvas')?.closest('.q-card')
          return chartCard as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'gateway-list',
      text: '支付管道統計列表詳細顯示每個支付管道的交易筆數、金額和百分比。幫助您深入了解各支付方式的實際使用情況。',
      title: '📋 支付管道統計',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPaymentDashboardTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 查找包含「支付管道統計」文字的卡片
          const cards = document.querySelectorAll('.q-card')
          for (const card of Array.from(cards)) {
            const heading = card.querySelector('.text-h6, h6')
            if (heading && heading.textContent?.includes('支付管道統計')) {
              return card as HTMLElement
            }
          }
          // 如果找不到，返回第二個卡片作為後備
          const cardsArray = Array.from(document.querySelectorAll('.q-card'))
          return cardsArray[1] as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動金流儀表板導覽
export const startPaymentDashboardTour = (force: boolean = false): void => {
  if (!force && isPaymentDashboardTourCompleted()) {
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
    const tourInstance = initPaymentDashboardTour()
    const steps = createPaymentDashboardTourSteps()
    
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
export const cancelPaymentDashboardTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

