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
import { ref } from 'vue'
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
</style>
