import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過分類管理導覽
const TOUR_STORAGE_KEY = 'shopro-category-tour-completed'

export const isCategoryTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markCategoryTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetCategoryTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initCategoryTour = (): Shepherd.Tour => {
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

// 創建分類管理導覽步驟
export const createCategoryTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到分類管理頁面！這裡是您管理商品分類的核心區域。系統支援多層級分類結構，最多可管理 100 ~ 600 個分類。',
      title: '📂 分類管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markCategoryTourAsCompleted()
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
          // 查找包含「分類管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('分類管理')) {
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
      id: 'add-category',
      text: '點擊「新增分類」按鈕可以創建新的商品分類。您可以創建頂層分類或子分類，建立多層級的分類結構。',
      title: '➕ 新增分類',
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
          // 查找包含「新增分類」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增分類')) {
              return btn as HTMLElement
            }
            // 也檢查按鈕的 aria-label 或其他屬性
            const ariaLabel = btn.getAttribute('aria-label')
            if (ariaLabel && ariaLabel.includes('新增分類')) {
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
      text: '使用搜尋和篩選功能可以快速找到您需要的分類。您可以按分類名稱搜尋，或根據啟用狀態、父分類進行篩選。',
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
            const placeholder = input.querySelector('input')?.placeholder
            if (placeholder && placeholder.includes('搜尋分類名稱')) {
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
      id: 'category-table',
      text: '分類列表顯示了所有分類的基本資訊，包括 ID、分類名稱、父分類、描述、排序和啟用狀態。您可以在此查看和管理所有分類。',
      title: '📋 分類列表',
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
      id: 'category-actions',
      text: '每個分類都有兩個操作按鈕：編輯（修改分類資訊）和刪除（移除分類）。注意：刪除分類前請確保沒有商品使用該分類。',
      title: '⚙️ 分類操作',
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
      id: 'category-dialog',
      text: '在新增或編輯分類時，您可以設定分類名稱、父分類（建立多層級結構）、描述、排序順序和啟用狀態。父分類功能讓您可以建立樹狀的分類結構。',
      title: '📝 分類表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markCategoryTourAsCompleted()
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

// 啟動分類管理導覽
export const startCategoryTour = (force: boolean = false): void => {
  if (!force && isCategoryTourCompleted()) {
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
    const tourInstance = initCategoryTour()
    const steps = createCategoryTourSteps()
    
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
export const cancelCategoryTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

