<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">常用地址</div>
      <h1 class="sf-page-title">管理常用收件地址</h1>
      <p class="sf-page-lead">先存好常用地址可降低下次結帳填寫成本，提升回購速度。</p>
    </section>

    <q-card bordered class="sf-card">
      <q-card-section>
        <q-form @submit.prevent="saveAddress" style="max-width: 720px" class="q-gutter-md sf-form">
          <q-input v-model="address.name" outlined label="收件人姓名" />
          <q-input v-model="address.phone" outlined label="收件人手機" />
          <q-input v-model="address.fullAddress" outlined type="textarea" autogrow label="完整地址" />
          <div class="row q-gutter-sm">
            <q-btn color="primary" no-caps class="save-btn" label="儲存地址" type="submit" />
            <q-btn flat no-caps label="清空" @click="resetAddress" />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useQuasar } from 'quasar'
import { trackEvent } from '@/utils/tracking'

const $q = useQuasar()

const address = reactive({
  name: '',
  phone: '',
  fullAddress: ''
})

const resetAddress = () => {
  address.name = ''
  address.phone = ''
  address.fullAddress = ''
}

const saveAddress = () => {
  localStorage.setItem('mvp_default_address', JSON.stringify(address))
  trackEvent('manage_address', { action: 'save_default' })
  $q.notify({ type: 'positive', message: '已儲存常用地址（MVP 版本暫存於本機）' })
}

onMounted(() => {
  const raw = localStorage.getItem('mvp_default_address')
  if (raw) {
    try {
      Object.assign(address, JSON.parse(raw))
    } catch {
      // ignore invalid local value
    }
  }
})
</script>

<style scoped>
.save-btn {
  border-radius: 999px;
}
</style>
