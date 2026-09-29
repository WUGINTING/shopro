import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過會員群組管理導覽
const TOUR_STORAGE_KEY = 'shopro-member-group-tour-completed'

export const isMemberGroupTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markMemberGroupTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetMemberGroupTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initMemberGroupTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建會員群組管理導覽步驟
export const createMemberGroupTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到會員群組管理頁面！這裡是您管理所有會員群組的核心區域。您可以創建群組、管理群組成員、編輯群組資訊，並對群組進行啟用或停用操作。',
      title: '👥 會員群組管理導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markMemberGroupTourAsCompleted()
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
          // 優先使用 data-tour 屬性
          const titleElement = document.querySelector('[data-tour="title"]')
          if (titleElement) {
            return titleElement as HTMLElement
          }
          // 查找包含「會員群組管理」文字的標題
          const headings = document.querySelectorAll('h4, .text-h4, .text-h5, h5')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('會員群組管理')) {
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
      id: 'add-group',
      text: '點擊「新增群組」按鈕可以創建新的會員群組。您可以設定群組名稱、描述，並選擇是否啟用該群組。群組可以用來對會員進行分類管理，方便後續的營銷活動和權限設定。',
      title: '➕ 新增群組',
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
          // 優先使用 data-tour 屬性
          const addBtn = document.querySelector('[data-tour="add-group-btn"]')
          if (addBtn) {
            return addBtn as HTMLElement
          }
          // 查找包含「新增群組」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('新增群組')) {
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
      id: 'group-cards',
      text: '這裡顯示了所有的會員群組卡片。每個卡片顯示群組名稱、描述、啟用狀態和成員數量。您可以點擊卡片上的按鈕進行編輯、查看成員或刪除操作。停用的群組會以較淡的顏色顯示。',
      title: '📋 群組列表',
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
          // 優先使用 data-tour 屬性
          const groupList = document.querySelector('[data-tour="group-list"]')
          if (groupList) {
            return groupList as HTMLElement
          }
          // 查找群組卡片容器
          const cardContainer = document.querySelector('.row.q-col-gutter-md')
          if (cardContainer) {
            return cardContainer as HTMLElement
          }
          // 如果找不到，返回第一個卡片
          const firstCard = document.querySelector('.q-card')
          return firstCard as HTMLElement || document.querySelector('.q-page') as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'group-actions',
      text: '每個群組卡片都有三個操作按鈕：編輯（修改群組資訊）、成員（管理群組成員）、刪除（移除群組）。點擊「成員」按鈕可以查看該群組的所有成員，並可以添加或移除成員。',
      title: '⚙️ 群組操作',
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
          // 優先使用 data-tour 屬性
          const groupActions = document.querySelector('[data-tour="group-actions"]')
          if (groupActions) {
            return groupActions as HTMLElement
          }
          // 查找第一個群組卡片的操作按鈕區域
          const firstCard = document.querySelector('.q-card .q-card-actions')
          if (firstCard) {
            return firstCard as HTMLElement
          }
          // 如果找不到，返回第一個卡片
          const card = document.querySelector('.q-card')
          return card as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'group-form',
      text: '在新增或編輯群組時，您可以設定群組的基本資訊：群組名稱（必填）、描述（可選）和啟用狀態。啟用的群組才能正常使用，停用的群組將不會在相關功能中顯示。',
      title: '📝 群組表單',
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
          // 優先使用 data-tour 屬性
          const formDialog = document.querySelector('[data-tour="group-form-dialog"]')
          if (formDialog) {
            return formDialog as HTMLElement
          }
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
      id: 'member-management',
      text: '在成員管理對話框中，您可以查看該群組的所有成員，並可以通過搜尋功能添加新成員到群組中，或移除現有成員。這讓您可以靈活地管理每個群組的成員組成。',
      title: '👤 成員管理',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markMemberGroupTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 優先使用 data-tour 屬性
          const memberDialog = document.querySelector('[data-tour="member-management-dialog"]')
          if (memberDialog) {
            return memberDialog as HTMLElement
          }
          // 查找成員管理對話框
          const dialog = document.querySelector('.q-dialog')
          if (dialog) {
            const dialogText = dialog.textContent || ''
            if (dialogText.includes('群組成員管理') || dialogText.includes('成員')) {
              return dialog as HTMLElement
            }
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

// 啟動會員群組管理導覽
export const startMemberGroupTour = (force: boolean = false): void => {
  if (!force && isMemberGroupTourCompleted()) {
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
    const tourInstance = initMemberGroupTour()
    const steps = createMemberGroupTourSteps()
    
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
export const cancelMemberGroupTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

