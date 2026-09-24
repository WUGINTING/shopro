import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過營銷活動管理導覽
const TOUR_STORAGE_KEY = 'shopro-marketing-tour-completed'

export const isMarketingTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markMarketingTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetMarketingTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initMarketingTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建營銷活動管理導覽步驟
export const createMarketingTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到營銷活動管理頁面！這裡是您管理所有營銷活動的核心區域。讓我為您介紹如何使用這個功能強大的管理系統。',
      title: '🎯 營銷活動管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markMarketingTourAsCompleted()
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
          // 查找包含「營銷活動管理」文字的標題
          const headings = document.querySelectorAll('.text-h4, h4, .q-page .text-h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('營銷活動管理')) {
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
      id: 'add-campaign',
      text: '點擊「新增活動」按鈕可以創建新的營銷活動。您可以設置活動名稱、類型、折扣金額或百分比、活動時間等重要資訊。',
      title: '➕ 新增活動',
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
          // 查找包含「新增活動」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增活動')) {
              return btn as HTMLElement
            }
            // 也檢查按鈕的 aria-label 或其他屬性
            const ariaLabel = btn.getAttribute('aria-label')
            if (ariaLabel && ariaLabel.includes('新增活動')) {
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
      id: 'search-filter',
      text: '使用搜尋和篩選功能可以快速找到您需要的營銷活動。您可以按活動名稱搜尋，或根據類型（折扣、促銷、限時搶購等）和狀態（草稿、進行中、已結束等）進行篩選。',
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
          const inputs = document.querySelectorAll('.q-input')
          for (const input of Array.from(inputs)) {
            const label = input.querySelector('label')
            if (label && label.textContent?.includes('搜尋活動名稱')) {
              const card = input.closest('.q-card')
              if (card) {
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
      id: 'campaign-table',
      text: '活動列表顯示了所有營銷活動的基本資訊，包括 ID、活動名稱、類型、狀態、折扣、開始日期和結束日期。您可以在這裡一目了然地查看所有活動。',
      title: '📋 活動列表',
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
      id: 'campaign-actions',
      text: '每個活動都有三個操作按鈕：編輯（修改活動資訊）、啟用/暫停（控制活動狀態）、刪除（移除活動）。您可以使用這些按鈕來管理您的營銷活動。',
      title: '⚙️ 活動操作',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markMarketingTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: '.q-table',
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動營銷活動管理導覽
export const startMarketingTour = (force: boolean = false): void => {
  if (!force && isMarketingTourCompleted()) {
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
    const tourInstance = initMarketingTour()
    const steps = createMarketingTourSteps()
    
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
export const cancelMarketingTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

