import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過訂單問答導覽
const TOUR_STORAGE_KEY = 'shopro-order-qa-tour-completed'

export const isOrderQATourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markOrderQATourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetOrderQATour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initOrderQATour = (): Shepherd.Tour => {
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

// 創建訂單問答管理導覽步驟
export const createOrderQATourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到訂單問答管理頁面！這裡是您管理訂單相關客戶問答的核心區域。您可以查看客戶問題、回答問題，並管理所有問答記錄。',
      title: '💬 訂單問答管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markOrderQATourAsCompleted()
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
          // 查找包含「訂單問答管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('訂單問答管理')) {
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
      id: 'add-question',
      text: '點擊「新增問題」按鈕可以手動創建新的問答記錄。您可以為特定訂單添加問題，並設定提問者類型和名稱。',
      title: '➕ 新增問題',
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
          // 查找包含「新增問題」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增問題')) {
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
      text: '搜尋功能可以幫助您快速找到特定的問答記錄。您可以按訂單ID搜尋，或根據回答狀態（全部、已回答、未回答）進行篩選。點擊「清除篩選」可以重置搜尋條件。',
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
              if (label && label.textContent?.includes('訂單ID')) {
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
      id: 'qa-table',
      text: '問答列表顯示了所有問答記錄的詳細資訊，包括ID、訂單ID、問題內容、回答內容、狀態（已回答/待回答）和提問時間。您可以在此查看所有客戶問題和回答。',
      title: '📋 問答列表',
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
      id: 'qa-actions',
      text: '每個問答記錄都有兩個操作按鈕：回答/編輯回答（為問題提供回答或修改現有回答）和刪除（移除問答記錄）。未回答的問題會顯示「回答」按鈕，已回答的問題會顯示「編輯回答」按鈕。',
      title: '⚙️ 問答操作',
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
      id: 'question-dialog',
      text: '在新增問題時，您需要選擇訂單ID、設定提問者類型（客戶或商家）、輸入提問者名稱和問題內容。這讓您可以為任何訂單創建問答記錄。',
      title: '❓ 新增問題表單',
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
    },
    {
      id: 'answer-dialog',
      text: '在回答問題時，您可以查看問題內容，然後輸入回答內容、回答者ID和回答者名稱。系統會記錄回答時間，幫助您追蹤客戶服務的響應速度。',
      title: '💬 回答問題表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markOrderQATourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 查找回答對話框
          const dialogs = document.querySelectorAll('.q-dialog')
          // 如果有多個對話框，返回最後一個（通常是回答對話框）
          if (dialogs.length > 0) {
            return dialogs[dialogs.length - 1] as HTMLElement
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

// 啟動訂單問答管理導覽
export const startOrderQATour = (force: boolean = false): void => {
  if (!force && isOrderQATourCompleted()) {
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
    const tourInstance = initOrderQATour()
    const steps = createOrderQATourSteps()
    
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
export const cancelOrderQATour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

