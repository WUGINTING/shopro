<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">會員中心</div>
      <h1 class="sf-page-title">歡迎回來，快速前往常用功能</h1>
      <p class="sf-page-lead">把高頻任務放在最前面，讓查訂單、管理地址與回購流程更順暢。</p>
    </section>

    <div class="row q-col-gutter-md">
      <div v-for="card in cards" :key="card.path" class="col-12 col-md-4">
        <q-card bordered class="sf-card full-height sf-elevate-hover">
          <q-card-section>
            <div class="row items-center q-gutter-sm q-mb-sm">
              <q-icon :name="card.icon" color="primary" size="22px" />
              <div class="text-subtitle1 text-weight-bold">{{ card.title }}</div>
            </div>
            <p class="sf-section-subtitle">{{ card.description }}</p>
          </q-card-section>
          <q-card-actions align="right" class="q-pa-md q-pt-none">
            <q-btn color="primary" flat no-caps label="前往" @click="go(card.path)" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { trackEvent } from '@/utils/tracking'

const router = useRouter()

const cards = [
  { title: '我的訂單', description: '查看訂單狀態、金額與建立時間。', path: '/account/orders', icon: 'receipt_long' },
  { title: '會員優惠', description: '查看目前等級、點數與回購相關資訊。', path: '/account/benefits', icon: 'workspace_premium' },
  { title: '常用地址', description: '儲存常用收件資料，縮短下次結帳時間。', path: '/account/addresses', icon: 'location_on' }
]

const go = (path: string) => router.push(path)

onMounted(() => {
  trackEvent('view_account')
})
</script>
