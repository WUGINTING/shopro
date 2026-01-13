import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過促銷活動管理導覽
const TOUR_STORAGE_KEY = 'shopro-promotion-tour-completed'

export const isPromotionTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPromotionTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPromotionTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPromotionTour = (): Shepherd.Tour => {
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

// 創建促銷活動管理導覽步驟
export const createPromotionTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到促銷活動管理頁面！這裡是您管理所有促銷活動和優惠券的核心區域。您可以創建各種類型的促銷活動，包括折扣、全館活動、免運和買贈等。',
      title: '🎉 促銷活動管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPromotionTourAsCompleted()
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
          // 查找包含「促銷活動管理」文字的標題
          const headings = document.querySelectorAll('h4, .text-h4, .text-h5, h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('促銷活動管理')) {
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
      id: 'add-promotion',
      text: '點擊「新增促銷」按鈕可以創建新的促銷活動。您可以設定活動名稱、類型、折扣、活動時間等資訊。',
      title: '➕ 新增促銷',
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
          // 查找包含「新增促銷」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增促銷')) {
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
      id: 'coupon-management',
      text: '點擊「優惠券管理」按鈕可以進入優惠券管理功能。您可以在那裡創建和管理優惠券，設定優惠券的使用條件和有效期。',
      title: '🎫 優惠券管理',
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
          // 查找包含「優惠券管理」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('優惠券管理')) {
              return btn as HTMLElement
            }
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
      id: 'promotion-table',
      text: '促銷活動列表顯示了所有活動的詳細資訊，包括活動名稱、類型（折扣、全館活動、免運、買贈）、折扣值、最低購買金額、優先級和啟用狀態。優先級決定了當多個活動同時適用時的優先順序。',
      title: '📋 促銷活動列表',
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
      id: 'promotion-actions',
      text: '每個促銷活動都有多個操作選項：編輯（修改活動資訊）、刪除（移除活動）、啟用/停用切換（控制活動是否生效）。您可以通過切換開關快速啟用或停用某個活動。',
      title: '⚙️ 活動操作',
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
      id: 'promotion-dialog',
      text: '在新增或編輯促銷活動時，您可以設定活動名稱、描述、活動類型（折扣、全館活動、免運、買贈）、開始和結束日期、折扣類型（百分比或固定金額）和折扣值、最低購買金額、優先級和啟用狀態。系統會根據優先級自動選擇最優惠的活動應用。',
      title: '📝 促銷活動表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPromotionTourAsCompleted()
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

// 啟動促銷活動管理導覽
export const startPromotionTour = (force: boolean = false): void => {
  if (!force && isPromotionTourCompleted()) {
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
    const tourInstance = initPromotionTour()
    const steps = createPromotionTourSteps()
    
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
export const cancelPromotionTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

