<template>
  <q-layout view="hHh lpR fFf" class="login-layout">
    <q-page-container>
      <q-page class="login-page q-pa-md q-pa-lg-lg">
        <div class="login-shell">
          <section class="login-showcase q-pa-lg q-pa-xl-md">
            <div class="showcase-badge">Shopro Admin Console</div>
            <h1 class="showcase-title">Manage orders, products, and operations from one place</h1>
            <p class="showcase-subtitle">
              Secure role-based access for admin, manager, staff, and customer accounts.
            </p>

            <div class="showcase-grid q-mt-lg">
              <q-card flat bordered class="showcase-stat">
                <q-card-section>
                  <div class="showcase-stat__label">Roles</div>
                  <div class="showcase-stat__value">4</div>
                  <div class="showcase-stat__hint">Admin / Manager / Staff / Customer</div>
                </q-card-section>
              </q-card>
              <q-card flat bordered class="showcase-stat">
                <q-card-section>
                  <div class="showcase-stat__label">Use Cases</div>
                  <div class="showcase-stat__value">Ops</div>
                  <div class="showcase-stat__hint">Orders, catalog, CRM and payment operations</div>
                </q-card-section>
              </q-card>
            </div>

            <div class="showcase-features q-mt-lg">
              <div class="showcase-feature"><q-icon name="verified_user" /> Role-based route protection</div>
              <div class="showcase-feature"><q-icon name="bolt" /> Fast admin dashboard overview API</div>
              <div class="showcase-feature"><q-icon name="payments" /> ECPay payment flow support</div>
            </div>
          </section>

          <section class="login-panel q-pa-lg">
            <div class="row items-center justify-between q-mb-md">
              <div>
                <div class="text-h5 text-weight-bold">Sign in</div>
                <div class="text-body2 text-grey-7">Enter your account credentials to continue.</div>
              </div>
              <q-avatar color="primary" text-color="white" icon="lock" />
            </div>

            <q-form @submit.prevent="handleLogin" class="q-gutter-md">
              <q-input
                v-model="loginForm.username"
                label="Username"
                outlined
                autocomplete="username"
                :rules="[(val) => !!val || 'Please enter username']"
              >
                <template #prepend>
                  <q-icon name="person" />
                </template>
              </q-input>

              <q-input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                label="Password"
                outlined
                autocomplete="current-password"
                :rules="[(val) => !!val || 'Please enter password']"
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
                    :aria-label="showPassword ? 'Hide password' : 'Show password'"
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
                label="Login to dashboard"
              />
            </q-form>

            <q-separator class="q-my-lg" />

            <div class="row items-center justify-between q-mb-sm">
              <div class="text-subtitle2 text-weight-medium">Quick fill test accounts</div>
              <q-btn flat dense no-caps label="Clear" @click="clearForm" />
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

            <q-banner rounded class="login-help-banner">
              <template #avatar>
                <q-icon name="info" color="primary" />
              </template>
              <div class="text-body2">
                Customer accounts are redirected to the storefront after login; staff roles go to the admin dashboard.
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
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

interface TestAccount {
  label: string
  username: string
  password: string
}

const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const showPassword = ref(false)

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
  if (authStore.user?.role === 'CUSTOMER') {
    router.push('/')
  } else {
    router.push('/admin')
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
        role: response.data.role as any
      })

      $q.notify({
        type: 'positive',
        message: 'Login successful',
        position: 'top'
      })

      redirectAfterLogin()
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || 'Login failed. Please check your username and password.',
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
