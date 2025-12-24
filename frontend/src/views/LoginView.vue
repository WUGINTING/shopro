<template>
  <q-layout view="hHh lpR fFf" class="login-layout">
    <q-page-container>
      <q-page class="flex flex-center login-page">
        <q-card class="login-card">
          <q-card-section class="bg-gradient text-white">
            <div class="text-center">
              <q-icon name="shopping_cart" size="64px" />
              <div class="text-h4 q-mt-md text-weight-bold">Shopro</div>
              <div class="text-subtitle1">电商管理系统</div>
            </div>
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleLogin">
              <q-input
                v-model="loginForm.username"
                label="用户名"
                outlined
                :rules="[val => !!val || '请输入用户名']"
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="person" />
                </template>
              </q-input>

              <q-input
                v-model="loginForm.password"
                label="密码"
                type="password"
                outlined
                :rules="[val => !!val || '请输入密码']"
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="lock" />
                </template>
              </q-input>

              <q-btn
                type="submit"
                label="登录"
                color="primary"
                unelevated
                class="full-width q-mb-md"
                :loading="loading"
              />

              <div class="text-caption text-grey-7 q-mt-md">
                <p class="q-mb-sm"><strong>测试账号：</strong></p>
                <p class="q-mb-xs">管理员：admin / admin123</p>
                <p class="q-mb-xs">经理：manager / manager123</p>
                <p class="q-mb-xs">员工：staff / staff123</p>
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
        message: '登录成功！',
        position: 'top'
      })

      // Redirect to home
      router.push('/')
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '登录失败，请检查用户名和密码',
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
}

.login-page {
  min-height: 100vh;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  overflow: hidden;
}

.bg-gradient {
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
}

.text-grey-7 {
  color: #757575;
}
</style>
