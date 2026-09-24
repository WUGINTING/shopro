<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg flex flex-center">
    <q-card bordered class="sf-card verify-card text-center">
      <q-card-section v-if="state === 'loading'">
        <q-spinner color="primary" size="40px" />
        <div class="q-mt-md">正在驗證你的 Email…</div>
      </q-card-section>

      <q-card-section v-else-if="state === 'success'">
        <q-icon name="verified" color="positive" size="48px" />
        <h1 class="sf-page-title q-mt-sm">Email 驗證完成</h1>
        <p class="sf-page-lead">現在可以在會員中心查看以此 Email 下的訂單。</p>
        <q-btn color="primary" no-caps :label="authStore.isAuthenticated ? '前往我的訂單' : '登入'" @click="next" />
      </q-card-section>

      <q-card-section v-else>
        <q-icon name="error_outline" color="negative" size="48px" />
        <h1 class="sf-page-title q-mt-sm">無法完成驗證</h1>
        <p class="sf-page-lead">{{ errorMessage }}</p>
        <q-btn color="primary" flat no-caps label="回到會員中心重新寄送" @click="router.push('/account')" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const state = ref<'loading' | 'success' | 'error'>('loading')
const errorMessage = ref('驗證連結無效或已過期，請重新寄送驗證信。')

const next = () => {
  router.push(authStore.isAuthenticated ? '/account/orders' : '/login')
}

onMounted(async () => {
  const token = typeof route.query.token === 'string' ? route.query.token : ''
  if (!token) {
    state.value = 'error'
    return
  }
  try {
    await authApi.confirmEmailVerification(token)
    state.value = 'success'
    if (authStore.isAuthenticated) {
      await authStore.refreshUser().catch(() => undefined)
    }
  } catch (error: any) {
    const message = error?.response?.data?.message
    if (message) errorMessage.value = message
    state.value = 'error'
  }
})
</script>

<style scoped>
.verify-card {
  width: 100%;
  max-width: 480px;
}
</style>
