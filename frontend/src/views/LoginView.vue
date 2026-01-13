<template>
  <q-layout view="hHh lpR fFf" class="login-layout">
    <q-page-container>
      <q-page class="flex flex-center login-page">
        <q-card class="login-card">
          <q-card-section class="bg-gradient-primary text-white">
            <div class="text-center">
              <q-icon name="shopping_cart" size="64px" />
              <div class="text-h4 q-mt-md text-weight-bold">遇日小舖</div>
              <div class="text-subtitle1">管理系統</div>
            </div>
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleLogin">
              <q-input
                v-model="loginForm.username"
                label="使用者名稱"
                outlined
                :rules="[val => !!val || '請輸入使用者名稱']"
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="person" />
                </template>
              </q-input>

              <q-input
                v-model="loginForm.password"
                label="密碼"
                type="password"
                outlined
                :rules="[val => !!val || '請輸入密碼']"
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="lock" />
                </template>
              </q-input>

              <q-btn
                type="submit"
                label="登入"
                color="primary"
                unelevated
                class="full-width q-mb-md"
                :loading="loading"
              />

              <q-separator class="q-my-md">
                <span class="text-caption text-grey-7 q-px-md">或</span>
              </q-separator>

              <div class="full-width q-mb-md">
                <div id="google-signin-button" class="google-signin-container"></div>
              </div>

              <div class="text-caption text-grey-7 q-mt-md">
                <p class="q-mb-sm"><strong>測試帳號：</strong></p>
                <p class="q-mb-xs">管理員：admin / admin123</p>
                <p class="q-mb-xs">經理：manager / manager123</p>
                <p class="q-mb-xs">員工：staff / staff123</p>
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const googleLoading = ref(false)

// Google OAuth 配置
const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID || ''

const handleLogin = async () => {
  loading.value = true
  try {
    const response = await authApi.login(loginForm.value)
    
    if (response.success && response.data) {
      // Save auth data
      authStore.setAuth(response.data.token, {
        username: response.data.username,
        email: response.data.email,
        role: response.data.role as any
      })

      $q.notify({
        type: 'positive',
        message: '登入成功！',
        position: 'top'
      })

      // Redirect to home
      router.push('/')
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '登入失敗，請檢查使用者名稱和密碼',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 初始化 Google OAuth
const initializeGoogleSignIn = () => {
  if (!GOOGLE_CLIENT_ID) {
    console.warn('Google Client ID 未配置，請在 .env 文件中設置 VITE_GOOGLE_CLIENT_ID')
    return
  }

  // 檢查是否已經載入
  if (window.google) {
    setupGoogleSignIn()
    return
  }

  // 載入 Google Identity Services
  const script = document.createElement('script')
  script.src = 'https://accounts.google.com/gsi/client'
  script.async = true
  script.defer = true
  script.onload = () => {
    if (window.google) {
      setupGoogleSignIn()
    }
  }
  script.onerror = () => {
    console.error('無法載入 Google Identity Services')
    $q.notify({
      type: 'negative',
      message: '無法載入 Google 登入服務',
      position: 'top'
    })
  }
  document.head.appendChild(script)
}

// 設置 Google Sign In
const setupGoogleSignIn = () => {
  if (!window.google || !GOOGLE_CLIENT_ID) return
  
  try {
    // 初始化 Google Identity Services
    window.google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: handleGoogleCredentialResponse,
      auto_select: false,
      cancel_on_tap_outside: true
    })

    // 渲染 Google 登入按鈕
    const buttonContainer = document.getElementById('google-signin-button')
    if (buttonContainer) {
      window.google.accounts.id.renderButton(buttonContainer, {
        type: 'standard',
        theme: 'outline',
        size: 'medium',
        text: 'signin_with',
        width: '100%',
        locale: 'zh_TW'
      })
    }
  } catch (error) {
    console.error('Google Sign In 設置失敗:', error)
    $q.notify({
      type: 'negative',
      message: 'Google 登入設置失敗',
      position: 'top'
    })
  }
}

// 處理 Google Credential Response (使用 ID Token)
const handleGoogleCredentialResponse = async (response: any) => {
  if (!response || !response.credential) {
    googleLoading.value = false
    return
  }

  googleLoading.value = true

  try {
    await handleGoogleLogin(response.credential)
  } catch (error) {
    googleLoading.value = false
  }
}

// 處理 Google 登入
const handleGoogleLogin = async (idToken: string) => {
  try {
    const response = await authApi.googleLogin(idToken)
    
    if (response.success && response.data) {
      // Save auth data
      authStore.setAuth(response.data.token, {
        username: response.data.username,
        email: response.data.email,
        role: response.data.role as any
      })

      $q.notify({
        type: 'positive',
        message: 'Google 登入成功！',
        position: 'top'
      })

      // Redirect to home
      router.push('/')
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Google 登入失敗，請稍後再試',
      position: 'top'
    })
  } finally {
    googleLoading.value = false
  }
}

// 擴展 Window 接口以支持 Google API
declare global {
  interface Window {
    google: any
  }
}

onMounted(() => {
  // 等待 DOM 渲染完成後再初始化
  nextTick(() => {
    initializeGoogleSignIn()
  })
})

onUnmounted(() => {
  // 清理 Google 登入按鈕
  const buttonContainer = document.getElementById('google-signin-button')
  if (buttonContainer) {
    buttonContainer.innerHTML = ''
  }
})
</script>

<style scoped>
.login-layout {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  width: 100%;
  height: 100vh;
}

.login-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 90%;
  max-width: 500px;
  border-radius: 16px;
  overflow: hidden;
}

.google-signin-container {
  width: 100%;
  display: flex;
  justify-content: center;
}

.google-signin-container :deep(div) {
  width: 100% !important;
}

.google-signin-container :deep(iframe) {
  width: 100% !important;
  min-width: 100% !important;
  max-height: 40px !important;
}

/* 調整 Google 按鈕內部的圖標和文字大小 */
.google-signin-container :deep([role="button"]) {
  height: 40px !important;
  min-height: 40px !important;
  max-height: 40px !important;
  padding: 0 12px !important;
}

/* 調整 Google 圖標容器 */
.google-signin-container :deep([role="button"] > div) {
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

/* 調整 Google 圖標大小 - 針對所有可能的圖標元素 */
.google-signin-container :deep(img),
.google-signin-container :deep(svg),
.google-signin-container :deep([data-iml]),
.google-signin-container :deep([class*="icon"]) {
  width: 14px !important;
  height: 14px !important;
  max-width: 14px !important;
  max-height: 14px !important;
  flex-shrink: 0 !important;
}

/* 針對 Google 按鈕內部的 SVG 路徑 - 縮小圖標 */
.google-signin-container :deep(svg) {
  width: 14px !important;
  height: 14px !important;
  max-width: 14px !important;
  max-height: 14px !important;
  display: block !important;
}

/* 縮小 SVG 路徑本身 */
.google-signin-container :deep(path) {
  transform: scale(0.6) !important;
  transform-origin: center !important;
}

/* 確保按鈕文字大小合適 */
.google-signin-container :deep(span) {
  font-size: 14px !important;
  line-height: 1.4 !important;
}
</style>
