import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過會員管理導覽
const TOUR_STORAGE_KEY = 'shopro-member-tour-completed'

export const isMemberTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markMemberTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetMemberTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initMemberTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建會員管理導覽步驟
export const createMemberTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到會員管理頁面！這裡是您管理所有會員資訊的核心區域。您可以查看會員資料、管理會員狀態、積點和消費記錄。',
      title: '👤 會員管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markMemberTourAsCompleted()
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
          // 查找包含「會員管理」文字的標題
          const headings = document.querySelectorAll('h4, .text-h4, .text-h5, h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('會員管理')) {
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
      id: 'add-member',
      text: '點擊「新增會員」按鈕可以手動創建新的會員記錄。您可以輸入會員的基本資訊，包括名稱、電子郵件、電話、狀態、積點和消費金額。',
      title: '➕ 新增會員',
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
          // 查找包含「新增會員」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增會員')) {
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
      text: '搜尋功能可以幫助您快速找到特定的會員。您可以按會員名稱、電子郵件或狀態（ACTIVE、INACTIVE、SUSPENDED）進行搜尋。輸入後會自動搜尋，點擊「重置」可以清除所有搜尋條件。',
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
      id: 'member-table',
      text: '會員列表顯示了所有會員的詳細資訊，包括名稱、電子郵件、電話、狀態、積點、消費金額和註冊日期。您可以在此查看和管理所有會員。',
      title: '📋 會員列表',
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
      id: 'member-actions',
      text: '每個會員都有多個操作按鈕：編輯（修改會員資訊）、刪除（移除會員記錄）。此外，對於啟用狀態的會員，您可以停用；對於停用或非啟用狀態的會員，您可以啟用。',
      title: '⚙️ 會員操作',
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
      id: 'member-dialog',
      text: '在新增或編輯會員時，您可以設定會員的基本資訊，包括名稱、電子郵件、電話、狀態（ACTIVE、INACTIVE、SUSPENDED）、總積點、總消費金額和備註。系統會自動驗證電子郵件格式。',
      title: '📝 會員表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markMemberTourAsCompleted()
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

// 啟動會員管理導覽
export const startMemberTour = (force: boolean = false): void => {
  if (!force && isMemberTourCompleted()) {
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
    const tourInstance = initMemberTour()
    const steps = createMemberTourSteps()
    
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
export const cancelMemberTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

