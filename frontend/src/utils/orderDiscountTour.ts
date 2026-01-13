import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過訂單折扣導覽
const TOUR_STORAGE_KEY = 'shopro-order-discount-tour-completed'

export const isOrderDiscountTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markOrderDiscountTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetOrderDiscountTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initOrderDiscountTour = (): Shepherd.Tour => {
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

// 創建訂單折扣管理導覽步驟
export const createOrderDiscountTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到訂單折扣管理頁面！這裡是您管理所有訂單折扣和優惠代碼的核心區域。您可以為訂單添加各種類型的折扣。',
      title: '💰 訂單折扣管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markOrderDiscountTourAsCompleted()
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
          // 查找包含「訂單折扣管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('訂單折扣管理')) {
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
      id: 'add-discount',
      text: '點擊「新增折扣」按鈕可以為訂單添加折扣記錄。您可以設定折扣類型、折扣金額或百分比、折扣代碼等。',
      title: '➕ 新增折扣',
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
          // 查找包含「新增折扣」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增折扣')) {
              return btn as HTMLElement
            }
          }
          // 如果找不到，返回第一個主要按鈕作為後備
          const primaryBtn = document.querySelector('.q-btn--unelevated, .q-btn[color="primary"]')
          return primaryBtn as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'search-filters',
      text: '搜尋功能可以幫助您快速找到特定的折扣記錄。您可以按訂單ID或折扣代碼進行搜尋。點擊「清除篩選」可以重置搜尋條件並顯示所有折扣記錄。',
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
            const inputs = card.querySelectorAll('.q-input')
            for (const input of Array.from(inputs)) {
              const label = input.querySelector('label')
              if (label && (label.textContent?.includes('訂單ID') || label.textContent?.includes('折扣代碼'))) {
                return card as HTMLElement
              }
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
      id: 'discount-table',
      text: '折扣列表顯示了所有折扣記錄的詳細資訊，包括訂單ID、折扣類型、折扣代碼、折扣金額、折扣百分比、描述和創建時間。',
      title: '📋 折扣列表',
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
      id: 'discount-actions',
      text: '每個折扣記錄都有兩個操作按鈕：編輯（修改折扣資訊）和刪除（移除折扣記錄）。注意：刪除折扣會影響訂單的總金額。',
      title: '⚙️ 折扣操作',
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
      id: 'discount-dialog',
      text: '在新增或編輯折扣時，您需要選擇訂單ID、設定折扣類型（優惠券、促銷活動、會員折扣等）、輸入折扣金額或百分比、折扣代碼和描述。系統支援多種折扣類型，可以靈活應對不同的營銷需求。',
      title: '📝 折扣表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markOrderDiscountTourAsCompleted()
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
          return document.querySelector('.q-page') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動訂單折扣管理導覽
export const startOrderDiscountTour = (force: boolean = false): void => {
  if (!force && isOrderDiscountTourCompleted()) {
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
    const tourInstance = initOrderDiscountTour()
    const steps = createOrderDiscountTourSteps()
    
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
export const cancelOrderDiscountTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

