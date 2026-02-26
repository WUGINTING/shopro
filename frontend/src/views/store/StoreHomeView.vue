<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <section class="hero-card sf-hero-glow q-pa-lg q-pa-xl-md q-mb-lg">
      <div class="hero-content">
        <div class="sf-chip sf-chip--warm q-mb-sm">
          <q-icon name="workspace_premium" size="16px" />
          精選日常好物
        </div>
        <p class="hero-eyebrow">Clear price. Faster checkout. Better confidence.</p>
        <h1 class="hero-title">把好用、安心、值得回購的商品，一次挑好</h1>
        <p class="hero-subtitle">
          從商品資訊到付款與配送說明都更透明，降低猶豫成本，讓你在最短時間完成安心下單。
        </p>
        <div class="row q-gutter-sm q-mt-md">
          <q-btn unelevated color="primary" no-caps class="hero-primary-btn" label="立即逛商品" @click="go('/products')" />
          <q-btn flat color="primary" no-caps class="hero-secondary-btn" label="了解品牌理念" @click="go('/brand')" />
        </div>
        <div class="hero-proof q-mt-md" aria-label="Conversion support information">
          <div class="hero-proof__item"><q-icon name="payments" /> 支援 ECPay / 貨到付款</div>
          <div class="hero-proof__item"><q-icon name="local_shipping" /> 配送進度可追蹤</div>
          <div class="hero-proof__item"><q-icon name="shield" /> 退換貨政策公開透明</div>
        </div>
      </div>
      <div class="hero-stats" aria-label="Store highlights">
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">48H</div>
            <div class="stat-label">平均出貨時程</div>
          </q-card-section>
        </q-card>
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">4.9/5</div>
            <div class="stat-label">顧客滿意度</div>
          </q-card-section>
        </q-card>
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">100%</div>
            <div class="stat-label">訂單狀態可視化</div>
          </q-card-section>
        </q-card>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="Trust signals">
      <div class="sf-section-title q-mb-sm">為什麼現在就買更輕鬆</div>
      <p class="sf-section-subtitle q-mb-md">
        透過清楚資訊與流程設計，降低比較與結帳的不確定感，幫你更快做決策。
      </p>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="signal in trustSignals" :key="signal.title">
          <q-card flat bordered class="trust-card sf-elevate-hover">
            <q-card-section class="row items-start no-wrap q-gutter-sm">
              <q-icon :name="signal.icon" :color="signal.color" size="24px" class="q-mt-xs" />
              <div>
                <div class="text-subtitle2 text-weight-bold">{{ signal.title }}</div>
                <div class="text-body2 text-grey-7">{{ signal.description }}</div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="Quick links">
      <div class="sf-section-title q-mb-sm">快速開始</div>
      <p class="sf-section-subtitle q-mb-md">依你目前需求選擇一條最短路徑，減少搜尋時間與選擇疲勞。</p>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="entry in quickEntries" :key="entry.title">
          <q-card bordered class="entry-card full-height sf-elevate-hover">
            <q-card-section>
              <div class="text-subtitle1 text-weight-bold">{{ entry.title }}</div>
              <p class="entry-text q-mt-sm">{{ entry.description }}</p>
              <q-chip
                v-if="entry.badge"
                dense
                square
                class="q-mt-sm"
                color="orange-1"
                text-color="deep-orange-9"
              >
                {{ entry.badge }}
              </q-chip>
            </q-card-section>
            <q-card-actions align="right">
              <q-btn flat no-caps color="primary" :label="entry.cta" @click="go(entry.path)" />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section>
      <q-banner rounded class="sf-note q-py-md">
        <template #avatar>
          <q-icon name="verified_user" size="28px" />
        </template>
        已內建訂單追蹤與付款狀態更新機制，若有疑問可直接從頁尾進入聯絡與政策頁面。
      </q-banner>
    </section>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { trackEvent } from '@/utils/tracking'

interface QuickEntry {
  title: string
  description: string
  cta: string
  path: string
  badge?: string
}

const router = useRouter()

const quickEntries: QuickEntry[] = [
  {
    title: '商品總覽',
    description: '快速瀏覽商品、價格與基本說明，縮短比價時間。',
    cta: '前往選購',
    path: '/products',
    badge: '最多人先逛這裡'
  },
  {
    title: '付款與配送',
    description: '先看清楚付款方式與配送時間，降低結帳前的不確定感。',
    cta: '查看政策',
    path: '/policy/payment-shipping',
    badge: '降低猶豫'
  },
  {
    title: '會員與訂單',
    description: '登入後查看訂單、常用地址與會員資訊，回購更順手。',
    cta: '打開會員中心',
    path: '/account',
    badge: '回購更快'
  }
]

const trustSignals = [
  {
    title: 'Transparent checkout',
    description: '送出訂單前提供清楚金額與付款方式摘要。',
    icon: 'receipt_long',
    color: 'primary'
  },
  {
    title: '付款狀態追蹤',
    description: '付款回傳流程與訂單狀態同步，減少等待焦慮。',
    icon: 'payments',
    color: 'positive'
  },
  {
    title: '售後支援清楚',
    description: '政策頁與聯絡方式固定可見，決策風險更低。',
    icon: 'support_agent',
    color: 'amber-8'
  }
]

const go = (path: string) => {
  if (path === '/products') {
    trackEvent('click_home_cta', { target: 'products' })
  }
  router.push(path)
}

onMounted(() => {
  trackEvent('view_home')
})
</script>

<style scoped>
.store-page {
  max-width: 1180px;
  margin: 0 auto;
}

.hero-card {
  border-radius: 24px;
  background: linear-gradient(135deg, #2f241f 0%, #8f4f2d 46%, #d89d57 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 24px 60px rgba(71, 47, 33, 0.18);
  color: #fffdf9;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  position: relative;
  overflow: hidden;
}

.hero-eyebrow {
  margin: 0;
  font-size: 0.82rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #fde7cf;
}

.hero-title {
  margin: 10px 0;
  font-size: 2.2rem;
  line-height: 1.18;
  max-width: 18ch;
}

.hero-subtitle {
  margin: 0;
  color: rgba(255, 251, 245, 0.9);
  max-width: 56ch;
  line-height: 1.65;
}

.hero-primary-btn {
  border-radius: 999px;
  padding-inline: 16px;
  background: #fff !important;
  color: #7c4023 !important;
  font-weight: 700;
}

.hero-secondary-btn {
  border-radius: 999px;
  color: #fff4e6 !important;
}

.hero-proof {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hero-proof__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 36px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.22);
  padding: 0 12px;
  font-size: 0.82rem;
  color: #fff7ed;
}

.hero-stats {
  display: grid;
  gap: 10px;
  align-content: start;
}

.stat-item {
  border-color: rgba(255, 255, 255, 0.26);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(6px);
  border-radius: 16px;
}

.stat-value {
  font-size: 1.35rem;
  font-weight: 800;
}

.stat-label {
  color: #fef3e7;
  font-size: 0.85rem;
}

.entry-card {
  border-radius: 16px;
  border-color: #eadfcd;
}

.trust-card {
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  border-color: #e9e4d7;
}

.entry-text {
  color: #64748b;
  min-height: 48px;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .hero-card {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 640px) {
  .hero-content .row.q-gutter-sm {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .hero-content .row.q-gutter-sm > .q-btn {
    width: 100%;
    min-height: 44px;
  }

  .hero-proof__item {
    width: 100%;
    justify-content: center;
  }

  .entry-text {
    min-height: 0;
  }
}
</style>
