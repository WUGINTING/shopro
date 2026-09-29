<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg flex justify-center">
    <q-card bordered class="sf-card auth-card">
      <q-tabs v-model="tab" dense align="justify" active-color="primary" indicator-color="primary" class="text-grey-7">
        <q-tab name="login" label="會員登入" no-caps />
        <q-tab name="register" label="註冊新會員" no-caps />
      </q-tabs>
      <q-separator />

      <q-tab-panels v-model="tab" animated>
        <q-tab-panel name="login">
          <q-form class="q-gutter-md" @submit.prevent="login">
            <q-input v-model="loginForm.username" outlined label="帳號" autocomplete="username" :rules="[required('請輸入帳號')]" />
            <q-input
              v-model="loginForm.password"
              outlined
              :type="showPassword ? 'text' : 'password'"
              label="密碼"
              autocomplete="current-password"
              :rules="[required('請輸入密碼')]"
            >
              <template #append>
                <q-btn flat round dense :icon="showPassword ? 'visibility_off' : 'visibility'" :aria-label="showPassword ? '隱藏密碼' : '顯示密碼'" @click="showPassword = !showPassword" />
              </template>
            </q-input>
            <q-btn type="submit" color="primary" unelevated no-caps class="full-width" label="登入" :loading="submitting" />
            <div class="row justify-between text-caption">
              <router-link to="/forgot-password" class="auth-link">忘記密碼？</router-link>
              <a href="#" class="auth-link" @click.prevent="tab = 'register'">還沒有帳號？立即註冊</a>
            </div>
          </q-form>
        </q-tab-panel>

        <q-tab-panel name="register">
          <q-form class="q-gutter-md" @submit.prevent="register">
            <q-input
              v-model="registerForm.username"
              outlined
              label="帳號"
              autocomplete="username"
              hint="3 到 100 個字元，登入時使用"
              :rules="[required('請輸入帳號'), (val: string) => val.trim().length >= 3 || '帳號至少 3 個字元']"
            />
            <q-input
              v-model="registerForm.email"
              outlined
              type="email"
              label="Email"
              autocomplete="email"
              hint="訂單通知與 Email 驗證信會寄到這裡"
              :rules="[required('請輸入 Email'), (val: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email 格式不正確']"
            />
            <q-input
              v-model="registerForm.password"
              outlined
              :type="showPassword ? 'text' : 'password'"
              label="密碼"
              autocomplete="new-password"
              hint="至少 8 個字元"
              :rules="[required('請輸入密碼'), (val: string) => val.length >= 8 || '密碼至少 8 個字元']"
            />
            <q-input
              v-model="registerForm.confirmPassword"
              outlined
              :type="showPassword ? 'text' : 'password'"
              label="確認密碼"
              autocomplete="new-password"
              :rules="[(val: string) => val === registerForm.password || '兩次輸入的密碼不一致']"
            />
            <q-checkbox v-model="registerForm.agree" dense>
              我已閱讀並同意 <router-link to="/policy/returns" class="auth-link">退換貨政策</router-link> 與
              <router-link to="/policy/payment-shipping" class="auth-link">付款與配送說明</router-link>
            </q-checkbox>
            <q-btn
              type="submit"
              color="primary"
              unelevated
              no-caps
              class="full-width"
              label="註冊並登入"
              :disable="!registerForm.agree"
              :loading="submitting"
            />
            <div class="text-caption text-grey-7">
              註冊後會寄出 Email 驗證信；完成驗證後，就能在「我的訂單」查看以此 Email 下的所有訂單。
            </div>
          </q-form>
        </q-tab-panel>
      </q-tab-panels>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { authApi, type AuthResponse } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

/** 顧客登入 / 註冊（後台員工也可在此登入，會被導向後台） */
const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const tab = ref<'login' | 'register'>(route.query.tab === 'register' ? 'register' : 'login')
const showPassword = ref(false)
const submitting = ref(false)
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', email: '', password: '', confirmPassword: '', agree: false })

const required = (message: string) => (val: string) => !!val?.trim() || message

/** 只允許站內路徑，避免被導向外部網站 */
const redirectTarget = () => {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
  return redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/account'
}

const completeLogin = (data: AuthResponse) => {
  authStore.setAuth(data.token, {
    id: data.id,
    username: data.username,
    email: data.email,
    role: data.role as any,
    emailVerified: data.emailVerified
  })
  if (data.role !== 'CUSTOMER') {
    router.push('/admin')
    return
  }
  router.push(redirectTarget())
}

const login = async () => {
  submitting.value = true
  try {
    const response = await authApi.login({ username: loginForm.value.username.trim(), password: loginForm.value.password })
    if (response.data) {
      $q.notify({ type: 'positive', message: '登入成功' })
      completeLogin(response.data)
    }
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    submitting.value = false
  }
}

const register = async () => {
  submitting.value = true
  try {
    const response = await authApi.register({
      username: registerForm.value.username.trim(),
      email: registerForm.value.email.trim(),
      password: registerForm.value.password
    })
    if (response.data) {
      $q.notify({ type: 'positive', message: '註冊成功！驗證信已寄到你的 Email，請點擊信中連結完成驗證。', timeout: 6000 })
      completeLogin(response.data)
    }
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-card {
  width: 100%;
  max-width: 460px;
  align-self: flex-start;
}
.auth-link {
  color: var(--q-primary);
  text-decoration: none;
}
</style>
