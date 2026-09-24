import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過部落格管理導覽
const TOUR_STORAGE_KEY = 'shopro-blog-tour-completed'

export const isBlogTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markBlogTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetBlogTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initBlogTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建部落格管理導覽步驟
export const createBlogTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到部落格管理頁面！這裡是您管理所有部落格文章的核心區域。讓我為您介紹如何使用這個功能強大的內容管理系統。',
      title: '📝 部落格管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markBlogTourAsCompleted()
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
          // 查找包含「部落格管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('部落格管理')) {
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
      id: 'add-post',
      text: '點擊「新增文章」按鈕可以創建新的部落格文章。您可以設置文章標題、內容、摘要、封面圖片、標籤和 SEO 設定等重要資訊。',
      title: '➕ 新增文章',
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
          // 查找包含「新增文章」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增文章')) {
              return btn as HTMLElement
            }
            // 也檢查按鈕的 aria-label 或其他屬性
            const ariaLabel = btn.getAttribute('aria-label')
            if (ariaLabel && ariaLabel.includes('新增文章')) {
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
      id: 'status-tabs',
      text: '使用狀態標籤可以快速篩選文章。您可以查看全部文章，或根據狀態（草稿、已發布、排程中、已封存）進行篩選，方便管理不同階段的文章。',
      title: '🏷️ 狀態篩選',
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
          // 查找包含標籤的卡片
          const tabs = document.querySelectorAll('.q-tabs')
          const firstTab = tabs[0]
          if (firstTab) {
            const tabCard = firstTab.closest('.q-card')
            if (tabCard) {
              return tabCard as HTMLElement
            }
            return firstTab as HTMLElement
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
      id: 'post-table',
      text: '文章列表顯示了所有文章的基本資訊，包括 ID、標題、狀態、作者、瀏覽次數和發布時間。您可以在這裡一目了然地查看所有文章。',
      title: '📋 文章列表',
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
      id: 'post-actions',
      text: '每個文章都有豐富的操作選項：編輯（修改文章內容）、發布（立即發布文章）、排程上架/下架（設定自動發布或下架時間）、封存（歸檔文章）、刪除（移除文章）。您可以根據需要選擇合適的操作。',
      title: '⚙️ 文章操作',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markBlogTourAsCompleted()
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

// 啟動部落格管理導覽
export const startBlogTour = (force: boolean = false): void => {
  if (!force && isBlogTourCompleted()) {
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
    const tourInstance = initBlogTour()
    const steps = createBlogTourSteps()
    
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
export const cancelBlogTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

