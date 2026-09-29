import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過客戶管理導覽
const TOUR_STORAGE_KEY = 'shopro-customer-tour-completed'

export const isCustomerTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markCustomerTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetCustomerTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initCustomerTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建客戶管理導覽步驟
export const createCustomerTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到客戶管理 (CRM) 頁面！這裡是您管理所有客戶資訊、會員等級和積分的核心區域。您可以查看客戶資料、管理會員等級和進行積分操作。',
      title: '👥 客戶管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markCustomerTourAsCompleted()
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
          // 查找包含「客戶管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('客戶管理')) {
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
      id: 'add-customer',
      text: '點擊「新增客戶」按鈕可以手動創建新的客戶記錄。您可以輸入客戶的基本資訊，包括姓名、郵箱、電話和會員等級。',
      title: '➕ 新增客戶',
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
          // 查找包含「新增客戶」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增客戶')) {
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
      id: 'customer-table',
      text: '客戶列表顯示了所有客戶的詳細資訊，包括ID、姓名、郵箱、電話、會員等級（BRONZE、SILVER、GOLD、PLATINUM）、積分和總消費金額。您可以在此查看和管理所有客戶。',
      title: '📋 客戶列表',
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
      id: 'customer-actions',
      text: '每個客戶都有兩個操作按鈕：編輯（修改客戶資訊和會員等級）和加積分（為客戶增加或扣除積分）。積分系統可以幫助您進行客戶忠誠度管理。',
      title: '⚙️ 客戶操作',
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
      id: 'customer-dialog',
      text: '在新增或編輯客戶時，您可以設定客戶的基本資訊，包括姓名、郵箱、電話和會員等級。系統支援四種會員等級：BRONZE（青銅）、SILVER（白銀）、GOLD（黃金）和 PLATINUM（白金）。',
      title: '📝 客戶表單',
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
      id: 'points-dialog',
      text: '積分操作功能允許您為客戶增加或扣除積分。您可以選擇操作類型（增加積分或扣除積分），然後輸入積分數量。這對於管理客戶忠誠度計劃和獎勵系統非常有用。',
      title: '⭐ 積分操作',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markCustomerTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 查找積分對話框（通常是第二個對話框）
          const dialogs = document.querySelectorAll('.q-dialog')
          // 如果有多個對話框，返回最後一個（通常是積分對話框）
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

// 啟動客戶管理導覽
export const startCustomerTour = (force: boolean = false): void => {
  if (!force && isCustomerTourCompleted()) {
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
    const tourInstance = initCustomerTour()
    const steps = createCustomerTourSteps()
    
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
export const cancelCustomerTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

