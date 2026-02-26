<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <section class="hero-card sf-hero-glow q-pa-lg q-pa-xl-md q-mb-lg">
      <div class="hero-content">
        <div class="sf-chip sf-chip--warm q-mb-sm">
          <q-icon name="workspace_premium" size="16px" aria-hidden="true" />
          精選日常選品，安心快速到貨
        </div>
        <p class="hero-eyebrow">Clear price. Faster checkout. Better confidence.</p>
        <h1 class="hero-title">把常用好物一次買齊，結帳流程更清楚</h1>
        <p class="hero-subtitle">
          Shopro 聚焦日常實用商品，提供清楚價格、透明配送資訊與順暢付款體驗，讓你從挑選到完成下單都更放心。
        </p>

        <div class="row q-gutter-sm q-mt-md">
          <q-btn
            unelevated
            color="primary"
            no-caps
            class="hero-primary-btn"
            label="立即選購"
            to="/products"
            @click="go('/products')"
          />
          <q-btn flat color="primary" no-caps class="hero-secondary-btn" label="認識品牌" to="/brand" />
        </div>

        <div class="hero-proof q-mt-md" aria-label="購物信任資訊">
          <div class="hero-proof__item"><q-icon name="payments" aria-hidden="true" /> 支援 ECPay 安全付款</div>
          <div class="hero-proof__item"><q-icon name="local_shipping" aria-hidden="true" /> 出貨進度清楚可追蹤</div>
          <div class="hero-proof__item"><q-icon name="shield" aria-hidden="true" /> 資料傳輸加密保護</div>
        </div>
      </div>

      <div class="hero-stats" aria-label="商店特色數據">
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">48H</div>
            <div class="stat-label">熱門商品快速出貨</div>
          </q-card-section>
        </q-card>
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">4.9/5</div>
            <div class="stat-label">顧客整體評價</div>
          </q-card-section>
        </q-card>
        <q-card flat bordered class="stat-item">
          <q-card-section>
            <div class="stat-value">100%</div>
            <div class="stat-label">價格與流程資訊透明</div>
          </q-card-section>
        </q-card>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="信任亮點">
      <h2 class="sf-section-title q-mb-sm">為什麼在 Shopro 購物更安心</h2>
      <p class="sf-section-subtitle q-mb-md">
        從訂單建立、付款導向到配送與售後資訊，我們提供一致清楚的流程說明，降低結帳猶豫與操作成本。
      </p>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="signal in trustSignals" :key="signal.title">
          <q-card flat bordered class="trust-card sf-elevate-hover">
            <q-card-section class="row items-start no-wrap q-gutter-sm">
              <q-icon :name="signal.icon" :color="signal.color" size="24px" class="q-mt-xs" aria-hidden="true" />
              <div>
                <div class="text-subtitle2 text-weight-bold">{{ signal.title }}</div>
                <div class="text-body2 text-grey-7">{{ signal.description }}</div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="快速入口">
      <h2 class="sf-section-title q-mb-sm">快速開始</h2>
      <p class="sf-section-subtitle q-mb-md">依你的購物目的快速進入常用頁面，縮短尋找時間。</p>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="entry in quickEntries" :key="entry.title">
          <q-card bordered class="entry-card full-height sf-elevate-hover">
            <q-card-section>
              <div class="text-subtitle1 text-weight-bold">{{ entry.title }}</div>
              <p class="entry-text q-mt-sm">{{ entry.description }}</p>
              <q-chip v-if="entry.badge" dense square class="q-mt-sm" color="orange-1" text-color="deep-orange-9">
                {{ entry.badge }}
              </q-chip>
            </q-card-section>
            <q-card-actions align="right">
              <q-btn
                flat
                no-caps
                color="primary"
                :label="entry.cta"
                :to="entry.path"
                @click="entry.path === '/products' ? go('/products') : null"
              />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="精選優惠">
      <h2 class="sf-section-title q-mb-sm">精選優惠活動</h2>
      <p class="sf-section-subtitle q-mb-md">近期有效活動優先顯示，方便你快速掌握折扣與免運方案。</p>

      <div v-if="homeLoading" class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="index in 3" :key="`home-promo-${index}`">
          <q-card bordered class="entry-card full-height">
            <q-card-section>
              <div class="skeleton-line skeleton-line--lg"></div>
              <div class="skeleton-line"></div>
              <div class="skeleton-line skeleton-line--short"></div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <div v-else-if="featuredPromotions.length === 0" class="empty-state">
        <q-icon name="info" size="24px" class="q-mb-sm" aria-hidden="true" />
        <div class="text-body2 text-grey-7">目前沒有進行中的精選優惠，稍後再回來看看。</div>
      </div>

      <div v-else class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="promo in featuredPromotions" :key="promo.id">
          <q-card bordered class="entry-card full-height sf-elevate-hover">
            <q-card-section>
              <div class="text-subtitle1 text-weight-bold">{{ promo.name }}</div>
              <p class="entry-text q-mt-sm">{{ promo.description || '活動詳情將於近期更新，歡迎先收藏頁面或稍後再查看。' }}</p>
              <div class="text-caption text-grey-6">{{ formatPromoDate(promo.startDate, promo.endDate) }}</div>
            </q-card-section>
            <q-card-actions align="right">
              <q-btn flat no-caps color="primary" label="查看優惠" to="/promotions" />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section class="q-mb-lg" aria-label="最新文章">
      <h2 class="sf-section-title q-mb-sm">最新專欄</h2>
      <p class="sf-section-subtitle q-mb-md">購物指南、選品建議與品牌資訊，協助你更快做出適合的選擇。</p>

      <div v-if="homeLoading" class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="index in 3" :key="`home-blog-${index}`">
          <q-card bordered class="entry-card full-height">
            <q-card-section>
              <div class="skeleton-line skeleton-line--lg"></div>
              <div class="skeleton-line"></div>
              <div class="skeleton-line skeleton-line--short"></div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <div v-else-if="latestPosts.length === 0" class="empty-state">
        <q-icon name="article" size="24px" class="q-mb-sm" aria-hidden="true" />
        <div class="text-body2 text-grey-7">目前尚無最新文章。</div>
      </div>

      <div v-else class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="post in latestPosts" :key="post.id || post.slug">
          <q-card bordered class="entry-card full-height sf-elevate-hover">
            <q-card-section>
              <div class="text-subtitle1 text-weight-bold">{{ post.title }}</div>
              <p class="entry-text q-mt-sm">{{ post.summary || '文章摘要整理中，點擊查看完整內容。' }}</p>
              <div class="text-caption text-grey-6">{{ formatBlogDate(post.publishedAt || post.scheduledAt) }}</div>
            </q-card-section>
            <q-card-actions align="right">
              <q-btn flat no-caps color="primary" label="閱讀文章" :to="post.slug ? `/blog/${post.slug}` : '/blog'" :disable="!post.slug" />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section>
      <q-banner rounded class="sf-note q-py-md">
        <template #avatar>
          <q-icon name="verified_user" size="28px" aria-hidden="true" />
        </template>
        結帳前可先登入會員並填寫收件資料，系統會自動暫存常用資訊，後續導向 ECPay 付款流程也更順暢。
      </q-banner>
    </section>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { trackEvent } from '@/utils/tracking'
import { promotionApi, type Promotion } from '@/api/promotion'
import blogApi, { type BlogPost } from '@/api/blog'

interface QuickEntry {
  title: string
  description: string
  cta: string
  path: string
  badge?: string
}

const quickEntries: QuickEntry[] = [
  {
    title: '商品瀏覽',
    description: '快速查看上架商品與價格資訊，從常用選品開始加入購物車。',
    cta: '前往商品列表',
    path: '/products',
    badge: '熱門入口'
  },
  {
    title: '付款與配送說明',
    description: '先了解付款方式、配送時程與常見問題，降低結帳時的不確定感。',
    cta: '查看說明',
    path: '/policy/payment-shipping',
    badge: '結帳前必看'
  },
  {
    title: '會員訂單管理',
    description: '登入後可查看訂單狀態、收件資訊與歷史購買紀錄。',
    cta: '前往會員中心',
    path: '/account',
    badge: '支援訂單追蹤'
  }
]

const trustSignals = [
  {
    title: '清楚的商品與訂單資訊',
    description: '商品價格、庫存與訂單摘要皆在前台清楚呈現，降低下單前疑慮。',
    icon: 'receipt_long',
    color: 'primary'
  },
  {
    title: '安全付款流程',
    description: '支援 ECPay 導向付款，付款頁面與流程說明清楚可辨識。',
    icon: 'payments',
    color: 'positive'
  },
  {
    title: '即時協助與售後支援',
    description: '提供聯絡入口與常見資訊，購買前後都能快速找到協助。',
    icon: 'support_agent',
    color: 'amber-8'
  }
]

const homeLoading = ref(true)
const featuredPromotions = ref<Promotion[]>([])
const latestPosts = ref<BlogPost[]>([])

const isActiveByDate = (start?: string, end?: string) => {
  if (!start || !end) return true
  const now = Date.now()
  return now >= new Date(start).getTime() && now <= new Date(end).getTime()
}

const formatPromoDate = (start?: string, end?: string) => {
  if (!start || !end) return '活動期間待公告'
  const startDate = new Date(start).toLocaleDateString('zh-TW')
  const endDate = new Date(end).toLocaleDateString('zh-TW')
  return `${startDate} - ${endDate}`
}

const formatBlogDate = (value?: string) => {
  if (!value) return '發佈日期待更新'
  return new Date(value).toLocaleDateString('zh-TW')
}

const loadHomeHighlights = async () => {
  homeLoading.value = true
  try {
    const [promoResult, blogResult] = await Promise.all([
      promotionApi.getPromotions(0, 6),
      blogApi.listBlogPostsByStatus('PUBLISHED', 0, 6)
    ])

    featuredPromotions.value = (promoResult.content || [])
      .filter((promo) => promo.enabled && isActiveByDate(promo.startDate, promo.endDate))
      .slice(0, 3)

    latestPosts.value = (blogResult.data?.content || []).slice(0, 3)
  } finally {
    homeLoading.value = false
  }
}

const go = (path: string) => {
  if (path === '/products') {
    trackEvent('click_home_cta', { target: 'products' })
  }
}

onMounted(() => {
  trackEvent('view_home')
  loadHomeHighlights()
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
  text-wrap: balance;
}

.hero-subtitle {
  margin: 0;
  color: rgba(255, 251, 245, 0.9);
}

.hero-primary-btn {
  border-radius: 12px;
  padding-inline: 14px;
  background: #fff !important;
  color: #7a4527 !important;
  font-weight: 700;
}

.hero-secondary-btn {
  border-radius: 12px;
  color: #fffdf9;
  border: 1px solid rgba(255, 253, 249, 0.3);
}

.hero-proof {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-proof__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 0.82rem;
}

.hero-stats {
  display: grid;
  gap: 12px;
  align-content: start;
}

.stat-item {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
}

.stat-label {
  color: rgba(255, 251, 245, 0.85);
  font-size: 0.85rem;
}

.sf-section-title {
  font-size: 1.15rem;
  font-weight: 800;
  color: #2f241f;
  text-wrap: balance;
}

.sf-section-subtitle {
  margin: 0;
  color: #6e645e;
}

.trust-card,
.entry-card {
  border-radius: 16px;
  border-color: #e9e2d6;
}

.entry-text {
  margin: 0;
  color: #665e58;
  line-height: 1.5;
  overflow-wrap: anywhere;
}

.sf-elevate-hover {
  transition: transform 0.16s ease, box-shadow 0.16s ease;
}

.sf-elevate-hover:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(47, 36, 31, 0.08);
}

.empty-state {
  border: 1px dashed #ddd1bc;
  border-radius: 14px;
  background: #fffdf8;
  padding: 18px;
  text-align: center;
}

.sf-note {
  background: linear-gradient(90deg, #fff8ef 0%, #f7fbff 100%);
  border: 1px solid #eadfce;
  color: #5a4c41;
  border-radius: 16px;
}

.skeleton-line {
  height: 12px;
  border-radius: 999px;
  background: #f0e8db;
  margin-bottom: 10px;
}

.skeleton-line--lg {
  height: 16px;
  width: 80%;
}

.skeleton-line--short {
  width: 60%;
}

@media (max-width: 980px) {
  .hero-card {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 1.8rem;
    max-width: none;
  }
}
</style>
