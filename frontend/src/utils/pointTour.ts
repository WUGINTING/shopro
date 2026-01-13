import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過積點管理導覽
const TOUR_STORAGE_KEY = 'shopro-point-tour-completed'

export const isPointTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPointTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPointTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPointTour = (): Shepherd.Tour => {
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

// 創建積點管理導覽步驟
export const createPointTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到積點管理頁面！這裡是您管理所有會員積點的核心區域。您可以查看積點統計、查詢積點紀錄、批次發放積點給會員。',
      title: '⭐ 積點管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPointTourAsCompleted()
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
          // 優先使用 data-tour 屬性
          const titleElement = document.querySelector('[data-tour="title"]')
          if (titleElement) {
            return titleElement as HTMLElement
          }
          // 查找包含「積點管理」文字的標題
          const headings = document.querySelectorAll('h4, .text-h4, .text-h5, h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('積點管理')) {
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
      id: 'stats-cards',
      text: '這裡顯示了四個重要的統計卡片：總發放積點（所有獲得的積點總和）、已兌換積點（會員使用的積點）、待過期積點（即將過期的積點）、活動記錄（總記錄數）。這些數據幫助您快速了解積點系統的整體狀況。',
      title: '📊 統計資訊',
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
          // 優先使用 data-tour 屬性
          const statsCards = document.querySelector('[data-tour="stats-cards"]')
          if (statsCards) {
            return statsCards as HTMLElement
          }
          // 查找統計卡片容器
          const cardContainer = document.querySelector('.row.q-col-gutter-md')
          if (cardContainer) {
            return cardContainer as HTMLElement
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || document.querySelector('.q-page') as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'batch-grant',
      text: '點擊「批次發放積點」按鈕可以一次性給多個會員發放積點。這對於促銷活動、獎勵發放等場景非常有用。您可以選擇多個會員，設定積點數量和發放原因。',
      title: '🎁 批次發放積點',
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
          // 優先使用 data-tour 屬性
          const batchBtn = document.querySelector('[data-tour="batch-grant-btn"]')
          if (batchBtn) {
            return batchBtn as HTMLElement
          }
          // 查找包含「批次發放積點」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('批次發放積點')) {
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
      text: '搜尋功能可以幫助您快速找到特定的積點紀錄。您可以按會員進行篩選（留空則顯示全部會員的紀錄），也可以按積點類型進行篩選，包括：獲得、購買、獎勵、兌換、過期、調整等類型。點擊「重置」可以清除所有搜尋條件。',
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
          // 優先使用 data-tour 屬性
          const searchCard = document.querySelector('[data-tour="search-card"]')
          if (searchCard) {
            return searchCard as HTMLElement
          }
          // 查找包含搜尋輸入框的卡片
          const cards = document.querySelectorAll('.q-card')
          for (const card of Array.from(cards)) {
            const selects = card.querySelectorAll('.q-select')
            if (selects.length > 0) {
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
      id: 'points-table',
      text: '積點紀錄表格顯示了所有積點變動的詳細資訊，包括會員 ID、積點數量（正數顯示為綠色，負數顯示為紅色）、類型（用不同顏色的標籤顯示）、原因說明、結餘和日期。您可以通過分頁瀏覽所有紀錄。',
      title: '📋 積點紀錄列表',
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
          // 優先使用 data-tour 屬性
          const tableCard = document.querySelector('[data-tour="points-table"]')
          if (tableCard) {
            return tableCard as HTMLElement
          }
          // 查找表格
          const table = document.querySelector('.q-table')
          if (table) {
            return table.closest('.q-card') as HTMLElement || table as HTMLElement
          }
          // 如果找不到，返回頁面作為後備
          return document.querySelector('.q-page') as HTMLElement || document.body
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'batch-dialog',
      text: '在批次發放對話框中，您可以選擇多個會員（支援搜尋和複選）、設定要發放的積點數量（必須為正整數）、填寫發放原因（必填）。系統會為每個選中的會員發放相同數量的積點，並記錄在積點紀錄中。',
      title: '📝 批次發放表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPointTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 優先使用 data-tour 屬性
          const batchDialog = document.querySelector('[data-tour="batch-dialog"]')
          if (batchDialog) {
            return batchDialog as HTMLElement
          }
          // 查找對話框
          const dialog = document.querySelector('.q-dialog')
          if (dialog) {
            const dialogText = dialog.textContent || ''
            if (dialogText.includes('批次發放積點') || dialogText.includes('發放')) {
              return dialog as HTMLElement
            }
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

// 啟動積點管理導覽
export const startPointTour = (force: boolean = false): void => {
  if (!force && isPointTourCompleted()) {
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
    const tourInstance = initPointTour()
    const steps = createPointTourSteps()
    
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
export const cancelPointTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

