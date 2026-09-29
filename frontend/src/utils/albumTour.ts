import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過相冊管理導覽
const TOUR_STORAGE_KEY = 'shopro-album-tour-completed'

export const isAlbumTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markAlbumTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetAlbumTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initAlbumTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建相冊管理導覽步驟
export const createAlbumTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到相冊管理頁面！這裡是您管理所有相冊和圖片的核心區域。您可以創建相冊、上傳圖片、組織和管理您的圖片資源。',
      title: '📷 相冊管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markAlbumTourAsCompleted()
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
          // 查找包含「相冊管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, h4, .text-h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('相冊管理')) {
              return heading as HTMLElement
            }
          }
          // 如果找不到，返回頁面容器
          return document.querySelector('.q-pa-md') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'add-album',
      text: '點擊「新增相冊」按鈕可以創建新的相冊。您可以為相冊設定名稱和描述，用於組織和管理您的圖片。',
      title: '➕ 新增相冊',
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
          // 查找包含「新增相冊」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增相冊')) {
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
      id: 'album-list',
      text: '相冊列表以卡片形式展示所有相冊。每個相冊卡片顯示封面圖片、相冊名稱、描述和圖片數量。點擊相冊卡片可以進入相冊詳情頁面查看和管理其中的圖片。',
      title: '📋 相冊列表',
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
          // 查找相冊卡片列表
          const albumCards = document.querySelectorAll('.album-card')
          if (albumCards.length > 0) {
            return albumCards[0] as HTMLElement
          }
          // 如果找不到，返回包含相冊列表的容器
          const row = document.querySelector('.row.q-col-gutter-md')
          return row as HTMLElement || document.querySelector('.q-pa-md') as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'album-actions',
      text: '每個相冊卡片都有兩個操作按鈕：編輯（修改相冊名稱和描述）和刪除（移除相冊及其中的所有圖片）。點擊相冊卡片本身可以進入相冊詳情頁面。',
      title: '⚙️ 相冊操作',
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
          // 查找相冊卡片
          const albumCards = document.querySelectorAll('.album-card')
          if (albumCards.length > 0) {
            return albumCards[0] as HTMLElement
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
      id: 'pagination',
      text: '如果相冊數量較多，系統會自動分頁顯示。您可以使用底部的分頁控件來瀏覽不同頁面的相冊。',
      title: '📄 分頁功能',
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
          // 查找分頁控件
          const pagination = document.querySelector('.q-pagination')
          if (pagination) {
            return pagination as HTMLElement
          }
          // 如果找不到，返回頁面底部
          return document.querySelector('.q-pa-md') as HTMLElement || document.body
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'album-dialog',
      text: '在新增或編輯相冊時，您可以設定相冊名稱（必填）和相冊描述（可選）。相冊名稱用於識別和組織相冊，描述可以提供更多關於相冊用途的資訊。',
      title: '📝 相冊表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markAlbumTourAsCompleted()
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
          return document.querySelector('.q-pa-md') as HTMLElement || document.body
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    }
  ]
}

// 啟動相冊管理導覽
export const startAlbumTour = (force: boolean = false): void => {
  if (!force && isAlbumTourCompleted()) {
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
    const tourInstance = initAlbumTour()
    const steps = createAlbumTourSteps()
    
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
export const cancelAlbumTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

