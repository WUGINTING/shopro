import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過訂單管理導覽
const TOUR_STORAGE_KEY = 'shopro-order-tour-completed'

export const isOrderTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markOrderTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetOrderTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initOrderTour = (): Shepherd.Tour => {
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

// 創建訂單管理導覽步驟
export const createOrderTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到訂單管理頁面！這裡是您管理所有訂單的核心區域。您可以查看訂單狀態、處理付款、管理發貨等操作。',
      title: '📦 訂單管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markOrderTourAsCompleted()
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
          // 查找包含「訂單管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('訂單管理')) {
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
      id: 'status-info',
      text: '點擊「狀態說明」按鈕可以查看訂單狀態的詳細說明，了解每個狀態的含義和訂單流程。',
      title: 'ℹ️ 狀態說明',
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
          // 查找包含「狀態說明」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('狀態說明')) {
              return btn as HTMLElement
            }
          }
          return null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'add-order',
      text: '點擊「新增訂單」按鈕可以手動創建新訂單。您可以選擇客戶、添加商品、設定取貨方式和地址等。',
      title: '➕ 新增訂單',
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
          // 查找包含「新增訂單」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增訂單')) {
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
      id: 'filter-panel',
      text: '篩選面板提供了強大的搜尋功能。您可以按訂單編號、客戶姓名、訂單狀態、日期範圍、金額範圍等條件進行篩選。點擊「展開篩選」查看所有篩選選項。',
      title: '🔍 篩選面板',
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
          // 查找篩選面板卡片
          const cards = document.querySelectorAll('.q-card')
          for (const card of Array.from(cards)) {
            const text = card.textContent || ''
            if (text.includes('篩選條件') || text.includes('展開篩選')) {
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
      id: 'order-table',
      text: '訂單列表顯示了所有訂單的基本資訊，包括訂單編號、客戶、總金額、訂單狀態和創建時間。您可以在此查看和管理所有訂單。',
      title: '📋 訂單列表',
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
      id: 'order-actions',
      text: '每個訂單都有多個操作按鈕：更新狀態（更改訂單狀態）、前往付款（處理待付款訂單）、已出貨（標記已發貨）、編輯訂單、查看詳情和刪除訂單。',
      title: '⚙️ 訂單操作',
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
      id: 'order-dialog',
      text: '在新增或編輯訂單時，您可以選擇客戶、添加訂單項目（商品）、設定取貨方式、填寫收貨地址等。系統會自動計算訂單總金額。',
      title: '📝 訂單表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markOrderTourAsCompleted()
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

// 啟動訂單管理導覽
export const startOrderTour = (force: boolean = false): void => {
  if (!force && isOrderTourCompleted()) {
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
    const tourInstance = initOrderTour()
    const steps = createOrderTourSteps()
    
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
export const cancelOrderTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

