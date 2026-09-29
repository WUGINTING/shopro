<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg flex justify-center">
    <q-card bordered class="sf-card narrow-card">
      <q-card-section>
        <h1 class="sf-page-title">設定新密碼</h1>
      </q-card-section>
      <q-card-section v-if="!token">
        <q-banner rounded class="bg-orange-1 text-orange-10">重設連結不完整，請重新申請。</q-banner>
        <q-btn flat no-caps color="primary" class="q-mt-md" label="重新申請" :to="{ name: 'forgotPassword' }" />
      </q-card-section>
      <q-card-section v-else-if="done">
        <q-banner rounded class="bg-green-1 text-green-10">密碼已重設，請使用新密碼登入。</q-banner>
        <q-btn color="primary" unelevated no-caps class="q-mt-md" label="前往登入" :to="{ name: 'storeLogin' }" />
      </q-card-section>
      <q-card-section v-else>
        <q-form class="q-gutter-md" @submit.prevent="submit">
          <q-input
            v-model="password"
            outlined
            type="password"
            label="新密碼"
            autocomplete="new-password"
            hint="至少 8 個字元"
            :rules="[(val: string) => val.length >= 8 || '密碼至少 8 個字元']"
          />
          <q-input
            v-model="confirmPassword"
            outlined
            type="password"
            label="確認新密碼"
            autocomplete="new-password"
            :rules="[(val: string) => val === password || '兩次輸入的密碼不一致']"
          />
          <q-btn type="submit" color="primary" unelevated no-caps class="full-width" label="設定新密碼" :loading="submitting" />
          <router-link :to="{ name: 'forgotPassword' }" class="text-caption auth-link">連結失效？重新申請</router-link>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { authApi } from '@/api/auth'

const route = useRoute()
const token = typeof route.query.token === 'string' ? route.query.token : ''
const password = ref('')
const confirmPassword = ref('')
const submitting = ref(false)
const done = ref(false)

const submit = async () => {
  submitting.value = true
  try {
    await authApi.confirmPasswordReset(token, password.value)
    done.value = true
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.narrow-card { width: 100%; max-width: 460px; align-self: flex-start; }
.auth-link { color: var(--q-primary); text-decoration: none; }
</style>
