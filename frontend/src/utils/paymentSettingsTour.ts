import { createShepherdTour, type ShepherdTour, type ShepherdStepOptions } from './tour'
import 'shepherd.js/dist/css/shepherd.css'

// 創建 Shepherd 實例
let tour: ShepherdTour | null = null

// 檢查是否已經完成過支付參數設定導覽
const TOUR_STORAGE_KEY = 'shopro-payment-settings-tour-completed'

export const isPaymentSettingsTourCompleted = (): boolean => {
  return localStorage.getItem(TOUR_STORAGE_KEY) === 'true'
}

export const markPaymentSettingsTourAsCompleted = (): void => {
  localStorage.setItem(TOUR_STORAGE_KEY, 'true')
}

export const resetPaymentSettingsTour = (): void => {
  localStorage.removeItem(TOUR_STORAGE_KEY)
}

// 初始化導覽
export const initPaymentSettingsTour = (): ShepherdTour => {
  // 每次都創建新實例，避免重用舊的步驟
  tour = createShepherdTour()

  return tour
}

// 創建支付參數設定導覽步驟
export const createPaymentSettingsTourSteps = (): ShepherdStepOptions[] => {
  return [
    {
      id: 'welcome',
      text: '歡迎來到支付參數設定頁面！這裡是您管理所有支付閘道設定的核心區域。您可以配置 LINE Pay、ECPay、手動付款等支付方式的參數，包括啟用狀態、維護模式、抽成比率等。',
      title: '💳 支付參數設定導覽',
      buttons: [
        {
          text: '跳過導覽',
          action: () => {
            markPaymentSettingsTourAsCompleted()
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
          // 查找包含「支付參數設定」文字的標題
          const headings = document.querySelectorAll('.text-h4, h4')
          for (const heading of Array.from(headings)) {
            if (heading.textContent?.includes('支付參數設定')) {
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
      id: 'initialize-button',
      text: '如果這是第一次使用，頁面會顯示「初始化設定」按鈕。點擊此按鈕可以自動建立預設的支付閘道設定（包括 LINE Pay、ECPay、手動付款等）。初始化後，您就可以開始配置各個支付方式的參數了。',
      title: '🚀 初始化設定',
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
          const initBtn = document.querySelector('[data-tour="init-btn"]')
          if (initBtn) {
            return initBtn as HTMLElement
          }
          // 查找包含「初始化設定」文字的按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const label = btn.querySelector('.q-btn__content')
            if (label && label.textContent?.includes('初始化設定')) {
              return btn as HTMLElement
            }
          }
          // 如果找不到，返回第一個主要按鈕作為後備
          const primaryBtn = document.querySelector('.q-btn[color="primary"]')
          return primaryBtn as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'settings-list',
      text: '這裡顯示了所有已配置的支付閘道設定列表。每個支付方式都會顯示圖標、顯示名稱、說明文字，以及啟用/停用和維護模式的切換開關。您可以快速切換支付方式的啟用狀態，無需進入編輯頁面。',
      title: '📋 支付閘道列表',
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
          const settingsList = document.querySelector('[data-tour="settings-list"]')
          if (settingsList) {
            return settingsList as HTMLElement
          }
          // 查找支付設定列表
          const list = document.querySelector('.q-list')
          if (list) {
            return list as HTMLElement
          }
          // 如果找不到，返回卡片作為後備
          const card = document.querySelector('.q-card')
          return card as HTMLElement || document.querySelector('.q-page') as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'toggle-switches',
      text: '每個支付方式都有兩個切換開關：啟用開關（綠色）控制該支付方式是否可用，維護模式開關（橘色）用於暫時停用支付方式並顯示維護訊息。切換這些開關會立即生效，無需額外保存。',
      title: '🔄 快速切換開關',
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
          const toggles = document.querySelector('[data-tour="toggle-switches"]')
          if (toggles) {
            return toggles as HTMLElement
          }
          // 查找切換開關
          const toggle = document.querySelector('.q-toggle')
          if (toggle) {
            return toggle.closest('.q-item-section') as HTMLElement || toggle as HTMLElement
          }
          // 如果找不到，返回第一個列表項作為後備
          const listItem = document.querySelector('.q-item')
          return listItem as HTMLElement || null
        },
        on: 'top'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'edit-button',
      text: '點擊編輯按鈕（鉛筆圖標）可以進入詳細設定頁面，配置該支付方式的完整參數，包括顯示名稱、說明文字、抽成比率、排序順序、維護訊息等。',
      title: '✏️ 編輯設定',
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
          const editBtn = document.querySelector('[data-tour="edit-btn"]')
          if (editBtn) {
            return editBtn as HTMLElement
          }
          // 查找編輯按鈕
          const buttons = document.querySelectorAll('.q-btn')
          for (const btn of Array.from(buttons)) {
            const icon = btn.querySelector('.q-icon')
            if (icon && icon.textContent?.includes('edit')) {
              return btn as HTMLElement
            }
          }
          // 如果找不到，返回第一個按鈕作為後備
          const firstBtn = document.querySelector('.q-btn')
          return firstBtn as HTMLElement || null
        },
        on: 'bottom'
      },
      beforeShowPromise: () => {
        return Promise.resolve()
      }
    },
    {
      id: 'edit-dialog',
      text: '在編輯對話框中，您可以設定支付方式的詳細參數：顯示名稱（在前端顯示的名稱）、說明文字（支付方式的描述）、抽成比率（支付手續費百分比）、排序順序（在支付選項中的顯示順序）、維護說明（維護模式時顯示的訊息）。您也可以在此處切換啟用狀態和維護模式。',
      title: '📝 詳細設定表單',
      buttons: [
        {
          text: '上一步',
          action: () => tour?.back()
        },
        {
          text: '完成',
          action: () => {
            markPaymentSettingsTourAsCompleted()
            tour?.complete()
          }
        }
      ],
      attachTo: {
        element: () => {
          // 優先使用 data-tour 屬性
          const editDialog = document.querySelector('[data-tour="edit-dialog"]')
          if (editDialog) {
            return editDialog as HTMLElement
          }
          // 查找編輯對話框
          const dialog = document.querySelector('.q-dialog')
          if (dialog) {
            const dialogText = dialog.textContent || ''
            if (dialogText.includes('編輯支付設定') || dialogText.includes('支付設定')) {
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

// 啟動支付參數設定導覽
export const startPaymentSettingsTour = (force: boolean = false): void => {
  if (!force && isPaymentSettingsTourCompleted()) {
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
    const tourInstance = initPaymentSettingsTour()
    const steps = createPaymentSettingsTourSteps()
    
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
export const cancelPaymentSettingsTour = (): void => {
  if (tour) {
    tour.cancel()
  }
}

