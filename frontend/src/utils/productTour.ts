import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過商品管理導覽
const TOUR_STORAGE_KEY = 'shopro-product-tour-completed'

export const isProductTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markProductTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetProductTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initProductTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建商品管理導覽步驟
export const createProductTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到商品管理頁面！這裡是您管理所有商品的核心區域。讓我為您介紹如何使用這個功能強大的管理系統。',
      title: '🛍️ 商品管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markProductTourAsCompleted()
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
          // 查找包含「商品管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, .q-page .text-h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('商品管理')) {
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
      id: 'add-product',
      text: '點擊「新增商品」按鈕可以創建新商品。您需要填寫商品名稱、價格、庫存等基本資訊。',
      title: '➕ 新增商品',
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
          // 查找包含「新增商品」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增商品')) {
              return btn as HTMLElement
            }
            // 也檢查按鈕的 aria-label 或其他屬性
            const ariaLabel = btn.getAttribute('aria-label')
            if (ariaLabel && ariaLabel.includes('新增商品')) {
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
      text: '使用搜尋和篩選功能可以快速找到您需要的商品。您可以按商品名稱搜尋，或根據狀態、分類進行篩選。',
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
            if (placeholder && placeholder.includes('搜尋商品名稱')) {
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
      id: 'product-table',
      text: '商品列表顯示了所有商品的基本資訊，包括圖片、名稱、價格、庫存和狀態。您可以在此查看和管理所有商品。',
      title: '📋 商品列表',
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
      id: 'product-actions',
      text: '每個商品都有三個操作按鈕：編輯（修改商品資訊）、上架/下架（控制商品顯示狀態）、刪除（移除商品）。',
      title: '⚙️ 商品操作',
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
      id: 'product-dialog-basic',
      text: '在新增或編輯商品時，您會看到三個標籤頁。基本資訊標籤頁用於填寫商品的核心資訊，包括名稱、描述、價格、庫存、狀態和分類。',
      title: '📝 基本資訊',
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
          // 查找包含「基本資訊」文字的標籤
          const tabs = document.querySelectorAll('.q-tab')
          for (const tab of Array.from(tabs)) {
            if (tab.textContent?.includes('基本資訊')) {
              return tab as HTMLElement
            }
          }
          // 如果找不到，使用對話框或頁面作為後備
          const dialog = document.querySelector('.q-dialog')
          return (dialog || document.querySelector('.q-page') || document.body) as HTMLElement
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'product-dialog-spec',
      text: '商品規格（SKU）標籤頁允許您為同一商品創建多個規格變體，例如不同顏色、尺寸等。每個規格可以有自己的價格、庫存和圖片。注意：需要先保存商品基本資訊才能添加規格。',
      title: '📦 商品規格（SKU）',
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
          const tabs = document.querySelectorAll('.q-tab')
          for (const tab of Array.from(tabs)) {
            if (tab.textContent?.includes('商品規格') || tab.textContent?.includes('SKU')) {
              return tab as HTMLElement
            }
          }
          // 如果找不到，使用對話框或頁面作為後備
          const dialog = document.querySelector('.q-dialog')
          return (dialog || document.querySelector('.q-page') || document.body) as HTMLElement
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'product-dialog-description',
      text: '描述區塊標籤頁用於創建豐富的商品描述內容。您可以添加手動區塊（3個）和自動區塊（7個），每個區塊可以包含標題、內容和圖片，讓商品頁面更加豐富和吸引人。',
      title: '📄 描述區塊',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markProductTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          const tabs = document.querySelectorAll('.q-tab')
          for (const tab of Array.from(tabs)) {
            if (tab.textContent?.includes('描述區塊')) {
              return tab as HTMLElement
            }
          }
          // 如果找不到，使用對話框或頁面作為後備
          const dialog = document.querySelector('.q-dialog')
          return (dialog || document.querySelector('.q-page') || document.body) as HTMLElement
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動商品管理導覽
export const startProductTour = (force: boolean = false): void => {
  if (!force && isProductTourCompleted()) {
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
    const tourInstance = initProductTour()
    const steps = createProductTourSteps()
    
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
export const cancelProductTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

