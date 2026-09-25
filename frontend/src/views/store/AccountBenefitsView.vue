<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">會員優惠</div>
      <h1 class="sf-page-title">會員等級與消費紀錄</h1>
      <p class="sf-page-lead">累積消費達門檻即可升級，享有等級折扣（結帳時自動套用）。</p>
    </section>

    <EmailVerifyBanner />

    <div v-if="loading" class="row q-col-gutter-md">
      <div v-for="index in 3" :key="index" class="col-12 col-md-4"><q-skeleton type="rect" height="120px" /></div>
    </div>

    <q-banner v-else-if="member && !member.exists" rounded class="bg-grey-2">
      {{ member.emailVerified ? '完成第一筆訂單後，這裡會顯示你的會員等級與累積消費。' : '完成 Email 驗證後即可查看會員等級與累積消費。' }}
    </q-banner>

    <div v-else-if="member" class="row q-col-gutter-md">
      <div class="col-12 col-md-4">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">目前會員等級</div>
            <div class="benefit-value">{{ member.levelName }}</div>
            <div class="text-caption text-grey-7 q-mt-sm">
              {{ member.discountRate && member.discountRate < 1 ? `結帳享 ${formatDiscount(member.discountRate)}（與活動、優惠券取最優惠）` : '目前等級沒有額外折扣' }}
            </div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-md-4">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">累積消費</div>
            <div class="benefit-value">NT$ {{ money(member.totalSpent) }}</div>
            <div class="text-caption text-grey-7 q-mt-sm">
              <template v-if="member.nextLevelName">再消費 NT$ {{ money(member.nextLevelRemaining) }} 升級為「{{ member.nextLevelName }}」</template>
              <template v-else>已是最高等級</template>
            </div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-md-4">
        <q-card bordered class="sf-card full-height">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-sm">可用點數</div>
            <div class="benefit-value">{{ member.availablePoints ?? 0 }}</div>
            <div class="text-caption text-grey-7 q-mt-sm">點數由門市活動發放，使用方式請洽客服。</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import EmailVerifyBanner from '@/components/store/EmailVerifyBanner.vue'
import { accountApi, type AccountMember } from '@/api/account'
import { trackEvent } from '@/utils/tracking'

const member = ref<AccountMember | null>(null)
const loading = ref(true)
const money = (value?: number | null) => Number(value || 0).toLocaleString('zh-TW', { maximumFractionDigits: 0 })
// 0.95 → 95 折、0.9 → 9 折
const formatDiscount = (rate: number) => {
  const tenth = Math.round(rate * 100)
  return `${tenth % 10 === 0 ? tenth / 10 : tenth} 折`
}

onMounted(async () => {
  trackEvent('view_my_benefits')
  try {
    const response = await accountApi.getMember()
    member.value = response.data || null
  } catch {
    member.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.benefit-value {
  font-size: 1.8rem;
  font-weight: 800;
  color: #8f4f2d;
}
</style>
