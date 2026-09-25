<template>
  <q-layout view="hHh lpR fFf" class="login-layout">
    <q-page-container>
      <q-page class="login-page q-pa-md q-pa-lg-lg">
        <div class="login-shell">
          <section class="login-showcase q-pa-lg q-pa-xl-md">
            <div class="showcase-badge">Shopro 後台管理</div>
            <h1 class="showcase-title">訂單、商品與營運，一個後台全部管理</h1>
            <p class="showcase-subtitle">
              依角色（管理員、經理、員工）控管可使用的功能。
            </p>

            <div class="showcase-grid q-mt-lg">
              <q-card flat bordered class="showcase-stat">
                <q-card-section>
                  <div class="showcase-stat__label">角色</div>
                  <div class="showcase-stat__value">3</div>
                  <div class="showcase-stat__hint">管理員 / 經理 / 員工</div>
                </q-card-section>
              </q-card>
              <q-card flat bordered class="showcase-stat">
                <q-card-section>
                  <div class="showcase-stat__label">功能</div>
                  <div class="showcase-stat__value">營運</div>
                  <div class="showcase-stat__hint">訂單、商品、會員與金流</div>
                </q-card-section>
              </q-card>
            </div>

            <div class="showcase-features q-mt-lg">
              <div class="showcase-feature"><q-icon name="verified_user" /> 角色權限控管</div>
              <div class="showcase-feature"><q-icon name="bolt" /> 即時營運儀表板</div>
              <div class="showcase-feature"><q-icon name="payments" /> 綠界金流整合</div>
            </div>
          </section>

          <section class="login-panel q-pa-lg">
            <div class="row items-center justify-between q-mb-md">
              <div>
                <div class="text-h5 text-weight-bold">員工登入</div>
                <div class="text-body2 text-grey-7">請輸入後台帳號與密碼。</div>
              </div>
              <q-avatar color="primary" text-color="white" icon="lock" />
            </div>

            <q-form @submit.prevent="handleLogin" class="q-gutter-md">
              <q-input
                v-model="loginForm.username"
                label="帳號"
                outlined
                autocomplete="username"
                :rules="[(val) => !!val || '請輸入帳號']"
              >
                <template #prepend>
                  <q-icon name="person" />
                </template>
              </q-input>

              <q-input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                label="密碼"
                outlined
                autocomplete="current-password"
                :rules="[(val) => !!val || '請輸入密碼']"
                @keyup.enter="handleLogin"
              >
                <template #prepend>
                  <q-icon name="key" />
                </template>
                <template #append>
                  <q-btn
                    flat
                    round
                    dense
                    :icon="showPassword ? 'visibility_off' : 'visibility'"
                    :aria-label="showPassword ? '隱藏密碼' : '顯示密碼'"
                    @click="showPassword = !showPassword"
                  />
                </template>
              </q-input>

              <q-btn
                type="submit"
                color="primary"
                unelevated
                no-caps
                class="full-width login-submit"
                :loading="loading"
                label="登入後台"
              />
            </q-form>

            <q-separator class="q-my-lg" />

            <template v-if="showTestAccounts">
            <div class="row items-center justify-between q-mb-sm">
              <div class="text-subtitle2 text-weight-medium">開發環境測試帳號</div>
              <q-btn flat dense no-caps label="清除" @click="clearForm" />
            </div>

            <div class="test-account-grid q-mb-md">
              <q-btn
                v-for="account in testAccounts"
                :key="account.username"
                flat
                no-caps
                class="test-account-btn"
                @click="fillTestAccount(account.username, account.password)"
              >
                <div class="text-left full-width">
                  <div class="text-weight-medium">{{ account.label }}</div>
                  <div class="text-caption text-grey-7">{{ account.username }} / {{ account.password }}</div>
                </div>
              </q-btn>
            </div>
            </template>

            <q-banner rounded class="login-help-banner">
              <template #avatar>
                <q-icon name="info" color="primary" />
              </template>
              <div class="text-body2">
                顧客請由<router-link :to="{ name: 'storeLogin' }">商城會員登入</router-link>；忘記密碼可<router-link :to="{ name: 'forgotPassword' }">重設密碼</router-link>。
              </div>
            </q-banner>
          </section>
        </div>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

interface TestAccount {
  label: string
  username: string
  password: string
}

const router = useRouter()
const route = useRoute()
const $q = useQuasar()
const authStore = useAuthStore()

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const showPassword = ref(false)

// 測試帳號只在開發環境顯示（正式環境不會建立這些帳號）
const showTestAccounts = import.meta.env.DEV

const testAccounts: TestAccount[] = [
  { label: 'Admin', username: 'admin', password: 'admin123' },
  { label: 'Manager', username: 'manager', password: 'manager123' },
  { label: 'Staff', username: 'staff', password: 'staff123' },
  { label: 'Customer', username: 'customer', password: 'customer123' }
]

const fillTestAccount = (username: string, password: string) => {
  loginForm.value.username = username
  loginForm.value.password = password
}

const clearForm = () => {
  loginForm.value.username = ''
  loginForm.value.password = ''
}

const redirectAfterLogin = () => {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
  const safeRedirect = redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : ''
  if (authStore.user?.role === 'CUSTOMER') {
    router.push(safeRedirect && !safeRedirect.startsWith('/admin') ? safeRedirect : '/')
  } else {
    router.push(safeRedirect || '/admin')
  }
}

const handleLogin = async () => {
  if (loading.value) return

  loading.value = true
  try {
    const response = await authApi.login(loginForm.value)

    if (response.success && response.data) {
      authStore.setAuth(response.data.token, {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email,
        role: response.data.role as any,
        emailVerified: response.data.emailVerified
      })

      $q.notify({
        type: 'positive',
        message: '登入成功',
        position: 'top'
      })

      redirectAfterLogin()
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || '登入失敗，請確認帳號與密碼。',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-layout {
  min-height: 100vh;
  background:
    radial-gradient(circle at 10% 12%, rgba(56, 189, 248, 0.18), transparent 40%),
    radial-gradient(circle at 90% 8%, rgba(147, 197, 253, 0.16), transparent 38%),
    linear-gradient(180deg, #0b1220 0%, #111827 55%, #0f172a 100%);
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-shell {
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: 1.15fr 0.95fr;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(2, 6, 23, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.login-showcase {
  color: #f8fafc;
  background:
    radial-gradient(circle at 75% 15%, rgba(59, 130, 246, 0.3), transparent 42%),
    radial-gradient(circle at 8% 85%, rgba(20, 184, 166, 0.24), transparent 40%),
    linear-gradient(160deg, #0f172a 0%, #111827 55%, #172554 100%);
}

.showcase-badge {
  display: inline-flex;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.03em;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.showcase-title {
  margin: 14px 0 10px;
  font-size: 2rem;
  line-height: 1.2;
  color: #fff;
}

.showcase-subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.6;
  max-width: 520px;
}

.showcase-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.showcase-stat {
  border-radius: 14px;
  border-color: rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.showcase-stat__label {
  font-size: 0.8rem;
  color: rgba(226, 232, 240, 0.85);
}

.showcase-stat__value {
  margin-top: 6px;
  font-size: 1.5rem;
  font-weight: 700;
}

.showcase-stat__hint {
  margin-top: 4px;
  color: rgba(226, 232, 240, 0.82);
  font-size: 0.82rem;
  line-height: 1.4;
}

.showcase-features {
  display: grid;
  gap: 10px;
}

.showcase-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(241, 245, 249, 0.95);
}

.login-panel {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.login-submit {
  min-height: 44px;
  border-radius: 12px;
  font-weight: 600;
}

.test-account-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.test-account-btn {
  justify-content: flex-start;
  align-items: stretch;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 8px 10px;
  background: #fff;
}

.test-account-btn:hover {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.login-help-banner {
  border: 1px solid #dbeafe;
  background: #f8fbff;
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-showcase {
    display: none;
  }

  .test-account-grid {
    grid-template-columns: 1fr;
  }
}
</style>
