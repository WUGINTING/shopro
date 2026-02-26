<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">會員優惠</div>
      <h1 class="sf-page-title">會員等級與點數</h1>
      <p class="sf-page-lead">
        目前為 MVP 顯示頁。後續可串接會員等級與點數 API，提供更完整的回購誘因與優惠資訊。
      </p>
    </section>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">目前會員等級</div>
            <div class="benefit-value">{{ level }}</div>
            <div class="text-caption text-grey-7 q-mt-sm">示意資料，後續可改為後端實際回傳。</div>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-12 col-md-6">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">可用點數</div>
            <div class="benefit-value">{{ points }}</div>
            <div class="text-caption text-grey-7 q-mt-sm">後續可在結帳流程中顯示點數折抵規則。</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { trackEvent } from '@/utils/tracking'

const level = ref('一般會員')
const points = ref(0)

onMounted(() => {
  trackEvent('view_my_benefits')
})
</script>

<style scoped>
.benefit-value {
  font-size: 1.8rem;
  font-weight: 800;
  color: #8f4f2d;
}
</style>
