<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">收件資料</div>
      <h1 class="sf-page-title">預設收件資料</h1>
      <p class="sf-page-lead">儲存後，結帳時會自動帶入，也可以在結帳頁修改。</p>
    </section>

    <EmailVerifyBanner />

    <q-card bordered class="sf-card">
      <q-card-section>
        <div v-if="loading" class="q-pa-md"><q-skeleton type="rect" height="180px" /></div>
        <q-form v-else @submit.prevent="save" style="max-width: 720px" class="q-gutter-md sf-form">
          <q-input v-model="form.name" outlined label="收件人姓名" maxlength="100" />
          <q-input
            v-model="form.phone"
            outlined
            label="收件人手機"
            inputmode="numeric"
            :rules="[(val: string) => !val || /^09\d{8}$/.test(val) || '請輸入正確的手機號碼格式 (09xxxxxxxx)']"
          />
          <q-input v-model="form.postalCode" outlined label="郵遞區號" maxlength="10" style="max-width: 200px" />
          <q-input v-model="form.address" outlined type="textarea" autogrow label="完整地址" maxlength="500" />
          <q-toggle v-model="form.marketingOptIn" label="接收優惠與新品通知 Email" />
          <div class="row q-gutter-sm">
            <q-btn color="primary" no-caps class="save-btn" label="儲存" type="submit" :loading="saving" :disable="!emailVerified" />
          </div>
          <div v-if="!emailVerified" class="text-caption text-orange-9">完成 Email 驗證後才能儲存收件資料。</div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import EmailVerifyBanner from '@/components/store/EmailVerifyBanner.vue'
import { accountApi } from '@/api/account'
import { useAuthStore } from '@/stores/auth'
import { trackEvent } from '@/utils/tracking'

const $q = useQuasar()
const authStore = useAuthStore()
const loading = ref(true)
const saving = ref(false)
const emailVerified = ref(true)
const form = reactive({ name: '', phone: '', postalCode: '', address: '', marketingOptIn: false })

const save = async () => {
  saving.value = true
  try {
    await accountApi.updateMember({ ...form })
    trackEvent('manage_address', { action: 'save_default' })
    $q.notify({ type: 'positive', message: '已儲存，下次結帳會自動帶入' })
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    const response = await accountApi.getMember()
    const member = response.data
    emailVerified.value = member?.emailVerified !== false
    form.name = member?.name || authStore.user?.username || ''
    form.phone = member?.phone || ''
    form.postalCode = member?.postalCode || ''
    form.address = member?.address || ''
    form.marketingOptIn = Boolean(member?.marketingOptIn)
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.save-btn {
  border-radius: 999px;
}
</style>
