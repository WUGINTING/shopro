import Shepherd, { type StepOptions, type Tour, type TourOptions } from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 共用的 Shepherd 型別（Shepherd v14 以具名型別匯出，不再提供 Shepherd.* 命名空間）
export type ShepherdTour = Tour
export type ShepherdStepOptions = StepOptions

// 所有導覽共用的 Tour 設定（每次回傳新物件，避免實例間共享可變狀態）
// 註：Shepherd v14 已改用 Floating UI，舊的 popperOptions 設定不會被讀取，因此不列入
export const getDefaultTourOptions = (): TourOptions => ({
  useModalOverlay: true,
  defaultStepOptions: {
    cancelIcon: {
      enabled: true
    },
    classes: 'shepherd-theme-custom',
    scrollTo: { behavior: 'smooth', block: 'center' }
  }
})

// 建立新的 Shepherd Tour 實例
export const createShepherdTour = (): ShepherdTour => new Shepherd.Tour(getDefaultTourOptions())

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過導覽
const TOUR_STORAGE_KEY = 'shopro-admin-tour-completed'

export const isTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initTour = (): ShepherdTour => {
  if (tour) {
    return tour
  }

  tour = createShepherdTour()

  return tour
}

// 創建導覽步驟
export const createTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎使用遇日小舖管理系統！讓我為您介紹系統的主要功能。',
      title: '👋 歡迎',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markTourAsCompleted()
            tour?.cancel()
          }
        },
        {
          text: '開始導覽',
          action: () => tour?.next()
        }
      ],
      attachTo: {
        element: '.q-toolbar-title',
        on: 'bottom'
      }
    },
    {
      id: 'sidebar',
      text: '這是側邊導航欄，您可以快速訪問各個功能模組，包括商品管理、訂單管理、客戶管理等。',
      title: '📋 側邊導航',
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
        element: '.q-drawer',
        on: 'right'
      },
      beforeShowPromise: () => {
        // 確保側邊欄是打開的
        const drawer = document.querySelector('.q-drawer')
        if (drawer && drawer.classList.contains('q-drawer--on-top')) {
          // 如果側邊欄被隱藏，嘗試觸發打開
          const menuButton = document.querySelector('.q-btn[aria-label*="menu"]') as HTMLElement
          if (menuButton) {
            menuButton.click()
            return new Promise((resolve) => setTimeout(resolve, 300))
          }
        }
        return Promise.resolve()
      }
    },
    {
      id: 'dashboard',
      text: '儀表板是您的工作中心，這裡顯示了重要的統計數據和快速操作入口。點擊可以進入儀表板查看詳細資訊。',
      title: '📊 儀表板',
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
        element: '.q-item[data-tour="dashboard"]',
        on: 'right'
      },
      beforeShowPromise: () => {
        const element = document.querySelector('.q-item[data-tour="dashboard"]')
        if (!element) {
          return Promise.reject(new Error('找不到儀表板元素'))
        }
        return Promise.resolve()
      }
    },
    {
      id: 'products',
      text: '商品管理模組：您可以在此新增、編輯和管理所有商品，包括商品分類。',
      title: '🛍️ 商品管理',
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
        element: '.q-expansion-item[data-tour="products"]',
        on: 'right'
      }
    },
    {
      id: 'orders',
      text: '訂單管理模組：處理所有訂單，查看訂單詳情，管理訂單狀態和折扣。',
      title: '📦 訂單管理',
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
        element: '.q-expansion-item[data-tour="orders"]',
        on: 'right'
      }
    },
    {
      id: 'customers',
      text: '客戶管理模組：管理客戶資料、會員資訊、會員分組和等級設定。',
      title: '👥 客戶管理',
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
        element: '.q-expansion-item[data-tour="customers"]',
        on: 'right'
      }
    },
    {
      id: 'marketing',
      text: '營銷管理模組：創建營銷活動、促銷方案、積分管理和 EDM 電子報。',
      title: '📢 營銷管理',
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
        element: '.q-expansion-item[data-tour="marketing"]',
        on: 'right'
      }
    },
    {
      id: 'notifications',
      text: '通知中心：查看系統通知和重要訊息，及時了解訂單和系統狀態。',
      title: '🔔 通知中心',
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
        element: '.q-btn[data-tour="notifications"]',
        on: 'bottom'
      },
      beforeShowPromise: () => {
        const element = document.querySelector('.q-btn[data-tour="notifications"]')
        if (!element) {
          return Promise.reject(new Error('找不到通知按鈕'))
        }
        return Promise.resolve()
      }
    },
    {
      id: 'user-menu',
      text: '用戶菜單：查看個人資料、修改設定或登出系統。',
      title: '👤 用戶菜單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: '.q-btn[data-tour="user-menu"]',
        on: 'bottom'
      },
      beforeShowPromise: () => {
        const element = document.querySelector('.q-btn[data-tour="user-menu"]')
        if (!element) {
          return Promise.reject(new Error('找不到用戶菜單按鈕'))
        }
        return Promise.resolve()
      }
    }
  ]
}

// 啟動導覽
export const startTour = (force: boolean = false): void => {
  if (!force && isTourCompleted()) {
    return
  }

  // 如果已經有導覽實例，先取消它
  if (tour) {
    tour.cancel()
    tour = null
  }

  const tourInstance = initTour()
  const steps = createTourSteps()
  
  steps.forEach(step => {
    tourInstance.addStep(step)
  })

  tourInstance.start()
}

// 停止導覽
export const cancelTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

