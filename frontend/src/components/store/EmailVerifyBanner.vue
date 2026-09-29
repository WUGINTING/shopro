<template>
  <q-banner v-if="needsVerification" rounded class="bg-orange-1 text-orange-10 q-mb-md" role="status">
    <template #avatar>
      <q-icon name="mark_email_unread" color="orange-8" />
    </template>
    你的 Email（{{ authStore.user?.email }}）尚未驗證。完成驗證後才能在「我的訂單」查看以此 Email 下的訂單。
    <template #action>
      <q-btn
        flat
        no-caps
        color="orange-10"
        :label="sent ? '已寄出，重新寄送' : '寄送驗證信'"
        :loading="sending"
        @click="send"
      />
    </template>
  </q-banner>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

/** 會員 Email 未驗證時的提示與寄送驗證信按鈕 */
const $q = useQuasar()
const authStore = useAuthStore()
const sending = ref(false)
const sent = ref(false)

const needsVerification = computed(() => authStore.user?.emailVerified === false)

// 登入時存下的使用者資料可能是舊的，進入頁面時更新一次驗證狀態
onMounted(() => {
  authStore.refreshUser().catch(() => undefined)
})

const send = async () => {
  sending.value = true
  try {
    await authApi.sendEmailVerification()
    sent.value = true
    $q.notify({ type: 'positive', message: `驗證信已寄到 ${authStore.user?.email}，請於 24 小時內點擊信中連結。` })
  } catch {
    // 錯誤訊息由 axios 攔截器顯示
  } finally {
    sending.value = false
  }
}
</script>
