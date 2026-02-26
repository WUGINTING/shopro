<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">退換貨政策</div>
      <h1 class="sf-page-title">購買前先了解退換貨規則，降低風險感</h1>
      <p class="sf-page-lead">
        我們提供清楚的退換貨流程說明，讓你在購買前就知道若商品不符合期待時該如何處理。
      </p>
    </section>

    <q-card bordered class="sf-card">
      <q-card-section>
        <ol class="policy-list">
          <li>商品到貨後請儘速確認內容物與外觀狀況，如有異常請先保留包裝並聯絡客服。</li>
          <li>若申請退換貨，請依客服指示提供訂單編號、問題描述與必要照片，以加速處理。</li>
          <li>實際退換貨資格與流程仍以商品性質、活動條件與法規規定為準。</li>
        </ol>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { trackEvent } from '@/utils/tracking'

onMounted(() => {
  trackEvent('view_policy', { policy_type: 'returns' })
})
</script>

<style scoped>
.policy-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 12px;
  line-height: 1.7;
  color: #475569;
}
</style>
