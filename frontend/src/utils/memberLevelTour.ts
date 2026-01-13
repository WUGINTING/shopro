import Shepherd from 'shepherd.js'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: Shepherd.Tour | null = null

// 檢查是否已經完成過會員等級管理導覽
const TOUR_STORAGE_KEY = 'shopro-member-level-tour-completed'

export const isMemberLevelTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markMemberLevelTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetMemberLevelTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initMemberLevelTour = (): Shepherd.Tour => {
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

// 創建會員等級管理導覽步驟
export const createMemberLevelTourSteps = (): Shepherd.Step.StepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到會員等級管理頁面！這裡是您管理會員等級、權益和折扣的核心區域。您可以創建不同等級的會員制度，設定等級權益、折扣率和積分倍率。',
      title: '⭐ 會員等級管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markMemberLevelTourAsCompleted()
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
          // 查找包含「會員等級管理」文字的標題
          const headings = document.querySelectorAll('.text-h5, h5, h4, .text-h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('會員等級管理')) {
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
      id: 'add-level',
      text: '點擊「新增等級」按鈕可以創建新的會員等級。您可以設定等級名稱、順序、最低消費金額、折扣率、積分倍率等權益。',
      title: '➕ 新增等級',
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
          // 查找包含「新增等級」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增等級')) {
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
      id: 'level-table',
      text: '會員等級列表顯示了所有等級的詳細資訊，包括ID、等級名稱、等級順序、最低消費金額、折扣率、積分倍率和啟用狀態。等級順序決定了等級的層級，數字越小等級越高。',
      title: '📋 會員等級列表',
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
      id: 'level-actions',
      text: '每個會員等級都有多個操作選項：編輯（修改等級資訊和權益）、刪除（移除等級）、啟用/停用切換（控制等級是否可用）。您可以通過切換開關快速啟用或停用某個等級。',
      title: '⚙️ 等級操作',
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
      id: 'level-dialog',
      text: '在新增或編輯會員等級時，您可以設定等級名稱、等級順序（決定等級層級）、最低消費金額（達到此金額才能獲得該等級）、折扣率（0.0-1.0，例如0.95表示95折）、積分倍率（例如1.5表示1.5倍積分）、圖標URL、等級描述和啟用狀態。',
      title: '📝 等級表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markMemberLevelTourAsCompleted()
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

// 啟動會員等級管理導覽
export const startMemberLevelTour = (force: boolean = false): void => {
  if (!force && isMemberLevelTourCompleted()) {
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
    const tourInstance = initMemberLevelTour()
    const steps = createMemberLevelTourSteps()
    
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
export const cancelMemberLevelTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

