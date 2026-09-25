<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">帳戶設定</div>
      <h1 class="sf-page-title">帳號與密碼</h1>
      <p class="sf-page-lead">修改帳號、Email 或密碼。變更 Email 後需要重新驗證。</p>
    </section>

    <EmailVerifyBanner />

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-md">基本資料</div>
            <q-form class="q-gutter-md" @submit.prevent="saveProfile">
              <q-input v-model="profile.username" outlined label="帳號" :rules="[(val: string) => val.trim().length >= 3 || '帳號至少 3 個字元']" />
              <q-input
                v-model="profile.email"
                outlined
                type="email"
                label="Email"
                :hint="profile.email !== authStore.user?.email ? '變更後需要重新驗證 Email' : ''"
                :rules="[(val: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email 格式不正確']"
              />
              <q-btn type="submit" color="primary" unelevated no-caps label="儲存" :loading="savingProfile" />
            </q-form>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-md">變更密碼</div>
            <q-form ref="passwordFormRef" class="q-gutter-md" @submit.prevent="savePassword">
              <q-input v-model="passwords.current" outlined type="password" label="目前密碼" autocomplete="current-password" :rules="[(val: string) => !!val || '請輸入目前密碼']" />
              <q-input
                v-model="passwords.next"
                outlined
                type="password"
                label="新密碼"
                autocomplete="new-password"
                hint="至少 8 個字元"
                :rules="[(val: string) => val.length >= 8 || '密碼至少 8 個字元']"
              />
              <q-input
                v-model="passwords.confirm"
                outlined
                type="password"
                label="確認新密碼"
                autocomplete="new-password"
                :rules="[(val: string) => val === passwords.next || '兩次輸入的密碼不一致']"
              />
              <div class="row items-center justify-between">
                <q-btn type="submit" color="primary" unelevated no-caps label="變更密碼" :loading="savingPassword" />
                <router-link :to="{ name: 'forgotPassword' }" class="text-caption auth-link">忘記目前密碼？</router-link>
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useQuasar, type QForm } from 'quasar'
import EmailVerifyBanner from '@/components/store/EmailVerifyBanner.vue'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const $q = useQuasar()
const authStore = useAuthStore()
const profile = ref({ username: authStore.user?.username || '', email: authStore.user?.email || '' })
const passwords = ref({ current: '', next: '', confirm: '' })
const passwordFormRef = ref<QForm>()
const savingProfile = ref(false)
const savingPassword = ref(false)

const applyUser = (user: NonNullable<typeof authStore.user>) => {
  // 變更帳號或密碼時後端會回傳新的登入 token
  const { token, ...userData } = user
  const nextToken = token || authStore.token
  if (nextToken) authStore.setAuth(nextToken, userData)
  profile.value = { username: user.username, email: user.email }
}

const saveProfile = async () => {
  savingProfile.value = true
  try {
    const response = await authApi.updateProfile({ username: profile.value.username.trim(), email: profile.value.email.trim() })
    if (response.data) {
      applyUser(response.data)
      $q.notify({ type: 'positive', message: response.data.emailVerified === false ? '已儲存，請到新信箱完成 Email 驗證' : '已儲存' })
    }
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    savingProfile.value = false
  }
}

const savePassword = async () => {
  savingPassword.value = true
  try {
    const response = await authApi.updateProfile({ currentPassword: passwords.value.current, newPassword: passwords.value.next })
    // 變更密碼後舊 token 失效，改用後端回傳的新 token
    if (response.data) applyUser(response.data)
    passwords.value = { current: '', next: '', confirm: '' }
    passwordFormRef.value?.resetValidation()
    $q.notify({ type: 'positive', message: '密碼已變更' })
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    savingPassword.value = false
  }
}

onMounted(async () => {
  await authStore.refreshUser().catch(() => undefined)
  if (authStore.user) profile.value = { username: authStore.user.username, email: authStore.user.email }
})
</script>

<style scoped>
.auth-link { color: var(--q-primary); text-decoration: none; }
</style>
