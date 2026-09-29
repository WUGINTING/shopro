<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg flex justify-center">
    <q-card bordered class="sf-card narrow-card">
      <q-card-section>
        <h1 class="sf-page-title">忘記密碼</h1>
        <p class="sf-page-lead">輸入註冊時的 Email，我們會寄送重設密碼連結（1 小時內有效）。</p>
      </q-card-section>
      <q-card-section v-if="sent">
        <q-banner rounded class="bg-green-1 text-green-10">
          <template #avatar><q-icon name="mark_email_read" color="positive" /></template>
          若此 Email 已註冊，重設連結會在幾分鐘內寄達，請到信箱（含垃圾郵件匣）查看。
        </q-banner>
        <q-btn flat no-caps color="primary" class="q-mt-md" label="回到登入" :to="{ name: 'storeLogin' }" />
      </q-card-section>
      <q-card-section v-else>
        <q-form class="q-gutter-md" @submit.prevent="submit">
          <q-input
            v-model="email"
            outlined
            type="email"
            label="Email"
            autocomplete="email"
            :rules="[(val: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || '請輸入正確的 Email']"
          />
          <q-btn type="submit" color="primary" unelevated no-caps class="full-width" label="寄送重設連結" :loading="submitting" />
          <router-link :to="{ name: 'storeLogin' }" class="text-caption auth-link">想起密碼了？回到登入</router-link>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { authApi } from '@/api/auth'

const email = ref('')
const submitting = ref(false)
const sent = ref(false)

const submit = async () => {
  submitting.value = true
  try {
    await authApi.requestPasswordReset(email.value.trim())
    sent.value = true
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
