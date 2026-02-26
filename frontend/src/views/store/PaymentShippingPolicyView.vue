<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">付款與配送政策</div>
      <h1 class="sf-page-title">先看清楚付款與配送，再安心結帳</h1>
      <p class="sf-page-lead">我們將常見問題整理在同一頁，降低結帳前的不確定感與來回確認成本。</p>
    </section>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">付款方式</div>
            <ul class="sf-bullet-list">
              <li>ECPay 線上付款（推薦，付款狀態同步較完整）</li>
              <li>貨到付款（依配送方式與地區提供）</li>
              <li>實際可用付款方式以結帳頁顯示為準</li>
            </ul>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">配送說明</div>
            <ul class="sf-bullet-list">
              <li>宅配配送：一般約 2-3 個工作天（依地區與物流狀況調整）</li>
              <li>門市自取：商品備妥後會通知取貨</li>
              <li>實際出貨與到貨時間以訂單狀態更新為準</li>
            </ul>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { trackEvent } from '@/utils/tracking'

onMounted(() => {
  trackEvent('view_policy', { policy_type: 'payment_shipping' })
})
</script>
