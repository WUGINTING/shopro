<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="brand-hero q-pa-lg q-mb-md">
      <div class="sf-chip sf-chip--warm q-mb-sm">品牌理念</div>
      <h1 class="sf-page-title">我們把「好買」定義成更透明、更安心的購物體驗</h1>
      <p class="sf-page-lead">
        Shopro 專注在清楚的商品資訊、直覺的結帳流程與可追蹤的訂單狀態，讓你每一次下單都更有把握。
      </p>
    </section>

    <section class="sf-grid-auto q-mb-md">
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">透明資訊</div>
          <p class="sf-section-subtitle">
            商品價格、付款與配送方式、退換貨政策都在前台可快速查詢，降低下單猶豫與資訊不對稱。
          </p>
        </q-card-section>
      </q-card>
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">流程可追蹤</div>
          <p class="sf-section-subtitle">
            從購物車、結帳到付款狀態與訂單紀錄，都以可視化流程呈現，讓使用者知道目前進度。
          </p>
        </q-card-section>
      </q-card>
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">售後友善</div>
          <p class="sf-section-subtitle">
            政策頁與客服入口保持容易找到，減少購買風險感，也提升首次購買的信任度。
          </p>
        </q-card-section>
      </q-card>
    </section>

    <q-banner rounded class="sf-note">
      <template #avatar>
        <q-icon name="lightbulb" />
      </template>
      我們相信好的電商介面不只要好看，更要幫助使用者快速做出安心決策。
    </q-banner>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { trackEvent } from '@/utils/tracking'

onMounted(() => {
  trackEvent('view_brand')
})
</script>

<style scoped>
.brand-hero {
  border-radius: 18px;
  border: 1px solid #eadfcd;
  background:
    radial-gradient(circle at 85% 15%, rgba(202, 242, 231, 0.6), transparent 40%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.92) 0%, rgba(255, 250, 242, 0.95) 100%);
}
</style>
