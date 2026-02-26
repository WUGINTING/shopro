<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="contact-hero q-pa-lg q-mb-md">
      <div class="sf-chip q-mb-sm">客服與聯絡</div>
      <h1 class="sf-page-title">有問題？我們用最快方式協助你完成購物</h1>
      <p class="sf-page-lead">
        若對商品、訂單、付款或配送流程有疑問，請透過以下方式聯絡。我們會在營業時間內盡快回覆。
      </p>
    </section>

    <section class="sf-grid-auto q-mb-md">
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">Email</div>
          <div class="text-body1">support@shopro.example</div>
          <div class="text-caption text-grey-7 q-mt-sm">適合訂單查詢、售後與合作提案</div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">客服電話</div>
          <div class="text-body1">(02) 1234-5678</div>
          <div class="text-caption text-grey-7 q-mt-sm">營業日可即時諮詢購買與付款問題</div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">服務時間</div>
          <div class="text-body1">週一至週五 10:00 - 18:00</div>
          <div class="text-caption text-grey-7 q-mt-sm">例假日與國定假日依公告調整</div>
        </q-card-section>
      </q-card>
    </section>

    <q-banner rounded class="sf-success-note">
      <template #avatar>
        <q-icon name="support_agent" />
      </template>
      建議聯絡時附上訂單編號，可加快客服查詢與處理速度。
    </q-banner>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { trackEvent } from '@/utils/tracking'

onMounted(() => {
  trackEvent('view_contact')
})
</script>

<style scoped>
.contact-hero {
  border-radius: 18px;
  border: 1px solid #eadfcd;
  background:
    radial-gradient(circle at 90% 15%, rgba(228, 244, 238, 0.8), transparent 42%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.9) 0%, rgba(249, 255, 252, 0.95) 100%);
}
</style>
