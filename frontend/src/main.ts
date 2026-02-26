import './assets/main.css'
import '@/styles/theme-system.scss'
import '@/styles/app.scss'
import '@/styles/storefront-theme.scss'
import '@/styles/tour.scss'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Quasar, Notify, Dialog, Loading } from 'quasar'
import quasarLangZhTW from 'quasar/lang/zh-TW'

// Import icon libraries
import '@quasar/extras/material-icons/material-icons.css'
import '@quasar/extras/material-icons-outlined/material-icons-outlined.css'
import '@quasar/extras/material-icons-round/material-icons-round.css'
import '@quasar/extras/fontawesome-v6/fontawesome-v6.css'

// Import Quasar css
import 'quasar/src/css/index.sass'

// Import i18n
import i18n from '@/locale'
import { initUtmFromUrl } from '@/utils/tracking'

import App from './App.vue'
import router from './router'

const app = createApp(App)
initUtmFromUrl()

// 全局錯誤處理
app.config.errorHandler = (err, instance, info) => {
  console.error('Global Vue Error:', err)
  console.error('Component:', instance)
  console.error('Info:', info)

  // 顯示用戶友善的錯誤提示
  Notify.create({
    type: 'negative',
    message: '頁面發生錯誤，請重新整理頁面',
    position: 'top',
    timeout: 5000,
    actions: [
      { label: '重新整理', color: 'white', handler: () => window.location.reload() }
    ]
  })
}

// 未捕獲的 Promise 錯誤處理
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled Promise Rejection:', event.reason)

  // 避免重複顯示 axios 已處理的錯誤
  if (event.reason?.isAxiosError) {
    return
  }

  Notify.create({
    type: 'negative',
    message: '操作失敗，請稍後再試',
    position: 'top',
    timeout: 3000
  })
})

app.use(createPinia())
app.use(router)
app.use(i18n)
app.use(Quasar, {
  plugins: {
    Notify,
    Dialog,
    Loading
  },
  config: {
    notify: {
      position: 'top',
      timeout: 3000
    },
    loading: {
      message: '載入中...',
      spinnerSize: 40
    }
  },
  lang: quasarLangZhTW
})

app.mount('#app')
