import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import systemSettingsApi from '@/api/settings'

export const useSettingsStore = defineStore('settings', () => {
  const storeName = ref<string>('遇日小舖')
  const loading = ref(false)

  // 計算屬性：完整的標題
  const appTitle = computed(() => {
    return storeName.value ? `${storeName.value} - 管理系統` : '管理系統'
  })

  // 加載系統設置
  async function loadSystemSettings() {
    loading.value = true
    try {
      const response = await systemSettingsApi.getSystemSettings()
      if (response.data && response.data.storeName) {
        storeName.value = response.data.storeName
        // 同時保存到 localStorage 以便快速訪問
        localStorage.setItem('storeName', response.data.storeName)
      }
    } catch (error) {
      console.error('加載系統設置失敗:', error)
      // 從 localStorage 讀取備份
      const savedStoreName = localStorage.getItem('storeName')
      if (savedStoreName) {
        storeName.value = savedStoreName
      }
    } finally {
      loading.value = false
    }
  }

  // 更新商店名稱
  function updateStoreName(name: string) {
    storeName.value = name
    localStorage.setItem('storeName', name)
  }

  // 初始化：從 localStorage 讀取
  function initialize() {
    const savedStoreName = localStorage.getItem('storeName')
    if (savedStoreName) {
      storeName.value = savedStoreName
    }
    // 異步加載最新設置
    loadSystemSettings()
  }

  return {
    storeName,
    appTitle,
    loading,
    loadSystemSettings,
    updateStoreName,
    initialize
  }
})

