<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <div class="sr-only" aria-live="polite">{{ liveMessage }}</div>
    <section class="promo-hero q-pa-lg q-pa-xl-md q-mb-lg">
      <div class="promo-hero__content">
        <div class="sf-chip sf-chip--warm q-mb-sm">
          <q-icon name="local_offer" size="16px" aria-hidden="true" />
          限時促銷與折扣
        </div>
        <h1 class="promo-title">把握當期優惠，結帳更划算</h1>
        <p class="promo-subtitle">
          活動促銷與折扣碼都在這裡整理好。先領券、再下單，省得更有感。
        </p>
        <div class="row q-gutter-sm q-mt-md">
          <q-btn unelevated color="primary" no-caps class="promo-primary-btn" label="前往選購" to="/products" />
          <q-btn flat color="primary" no-caps class="promo-secondary-btn" label="查看最新日誌" to="/blog" />
        </div>
      </div>
      <div class="promo-hero__meta">
        <q-card flat bordered class="promo-meta-card">
          <q-card-section>
            <div class="promo-meta-title">今日提醒</div>
            <div class="promo-meta-text">優惠活動以活動期間為準，錯過就要等下次。</div>
          </q-card-section>
        </q-card>
        <q-card flat bordered class="promo-meta-card">
          <q-card-section>
            <div class="promo-meta-title">省錢策略</div>
            <div class="promo-meta-text">可同時搭配折扣碼與活動折扣時，系統會自動套用最優惠方案。</div>
          </q-card-section>
        </q-card>
      </div>
    </section>

    <section class="q-mb-xl" aria-label="Active promotions">
      <div class="section-head q-mb-md">
        <div>
          <h2 class="sf-section-title">活動促銷</h2>
          <p class="sf-section-subtitle">把握限時優惠，越早下單越划算。</p>
        </div>
        <q-btn flat no-caps color="primary" icon="shopping_bag" label="去逛商品" to="/products" />
      </div>

      <div v-if="loading" class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="index in 3" :key="`promo-skeleton-${index}`">
          <q-card bordered class="promo-card promo-card--skeleton">
            <q-card-section>
              <div class="skeleton-line skeleton-line--lg"></div>
              <div class="skeleton-line"></div>
              <div class="skeleton-line skeleton-line--short"></div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <div v-else-if="activePromotions.length === 0" class="empty-state">
        <q-icon name="info" size="32px" color="grey-6" aria-hidden="true" />
        <div class="text-subtitle2 q-mt-sm">目前沒有進行中的活動促銷</div>
        <p class="text-body2 text-grey-7 q-mt-xs">你可以先逛商品或訂閱日誌，下一波優惠會第一時間通知。</p>
      </div>

      <div v-else class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="promo in activePromotions" :key="promo.id">
          <q-card bordered class="promo-card sf-elevate-hover">
            <q-card-section>
              <div class="promo-card__top">
                <q-chip dense square color="orange-2" text-color="deep-orange-9">
                  {{ formatPromotionType(promo.type) }}
                </q-chip>
                <q-chip v-if="getDaysLeft(promo.endDate) !== null" dense square color="red-1" text-color="red-8">
                  剩 {{ getDaysLeft(promo.endDate) }} 天
                </q-chip>
              </div>
              <div class="text-subtitle1 text-weight-bold q-mt-sm">{{ promo.name }}</div>
              <div class="text-body2 text-grey-7 q-mt-xs">{{ promo.description || '結帳自動套用優惠。' }}</div>
              <div class="promo-benefit q-mt-md">
                <div class="benefit-value">{{ formatPromotionBenefit(promo) }}</div>
                <div class="benefit-label">活動優惠</div>
              </div>
              <div class="promo-info q-mt-md">
                <div><q-icon name="event" size="16px" aria-hidden="true" /> {{ formatDateRange(promo.startDate, promo.endDate) }}</div>
                <div v-if="promo.minPurchaseAmount"><q-icon name="shopping_cart" size="16px" aria-hidden="true" /> 滿 {{ formatCurrency(promo.minPurchaseAmount) }} 起</div>
                <div v-if="promo.maxDiscountAmount"><q-icon name="savings" size="16px" aria-hidden="true" /> 最高折抵 {{ formatCurrency(promo.maxDiscountAmount) }}</div>
              </div>
            </q-card-section>
            <q-card-actions align="right" class="promo-actions">
              <q-btn flat no-caps color="primary" label="查看商品" to="/products" />
              <q-btn unelevated color="primary" no-caps label="立即購買" to="/products" />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section class="q-mb-xl" aria-label="Coupons">
      <div class="section-head q-mb-md">
        <div>
          <h2 class="sf-section-title">折扣碼專區</h2>
          <p class="sf-section-subtitle">領取折扣碼，結帳時輸入即可折抵。</p>
        </div>
        <q-btn flat no-caps color="primary" icon="confirmation_number" label="結帳頁使用" to="/cart" />
      </div>

      <div v-if="loading" class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="index in 3" :key="`coupon-skeleton-${index}`">
          <q-card bordered class="coupon-card promo-card--skeleton">
            <q-card-section>
              <div class="skeleton-line skeleton-line--lg"></div>
              <div class="skeleton-line"></div>
              <div class="skeleton-line skeleton-line--short"></div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <div v-else-if="activeCoupons.length === 0" class="empty-state">
        <q-icon name="redeem" size="32px" color="grey-6" aria-hidden="true" />
        <div class="text-subtitle2 q-mt-sm">目前沒有可用的折扣碼</div>
        <p class="text-body2 text-grey-7 q-mt-xs">先把喜歡的商品加入購物車，等下一波優惠更容易下單。</p>
      </div>

      <div v-else class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="coupon in activeCoupons" :key="coupon.id">
          <q-card bordered class="coupon-card sf-elevate-hover">
            <q-card-section>
              <div class="coupon-card__top">
                <div class="coupon-code">{{ coupon.code }}</div>
                <q-btn
                  flat
                  dense
                  icon="content_copy"
                  aria-label="Copy coupon code"
                  @click="copyCouponCode(coupon.code)"
                />
              </div>
              <div class="text-subtitle1 text-weight-bold q-mt-sm">{{ coupon.name }}</div>
              <div class="text-body2 text-grey-7 q-mt-xs">{{ coupon.applicable || '結帳時輸入即可套用。' }}</div>
              <div class="promo-benefit q-mt-md">
                <div class="benefit-value">{{ formatCouponBenefit(coupon) }}</div>
                <div class="benefit-label">折扣碼優惠</div>
              </div>
              <div class="promo-info q-mt-md">
                <div><q-icon name="event" size="16px" aria-hidden="true" /> {{ formatDateRange(coupon.validFrom, coupon.validUntil) }}</div>
                <div v-if="coupon.minPurchaseAmount"><q-icon name="shopping_cart" size="16px" aria-hidden="true" /> 滿 {{ formatCurrency(coupon.minPurchaseAmount) }} 起</div>
                <div v-if="coupon.maxDiscountAmount"><q-icon name="savings" size="16px" aria-hidden="true" /> 最高折抵 {{ formatCurrency(coupon.maxDiscountAmount) }}</div>
              </div>
            </q-card-section>
            <q-card-actions align="right" class="promo-actions">
              <q-btn flat no-caps color="primary" label="加入購物車" to="/products" />
              <q-btn unelevated color="primary" no-caps label="去結帳" to="/cart" />
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </section>

    <section class="promo-guide" aria-label="Promotion guide">
      <q-banner rounded class="sf-note q-py-md">
        <template #avatar>
        <q-icon name="tips_and_updates" size="28px" aria-hidden="true" />
      </template>
      結帳時自動比對活動與折扣碼，系統會選擇最划算的方案。若想鎖定優惠，建議先把商品加入購物車。
    </q-banner>
  </section>
</q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Notify } from 'quasar'
import { promotionApi, couponApi, type Promotion, type Coupon } from '@/api/promotion'

const promotions = ref<Promotion[]>([])
const coupons = ref<Coupon[]>([])
const loading = ref(true)
const liveMessage = ref('')

const now = () => new Date().getTime()

const isActiveByDate = (start?: string, end?: string) => {
  if (!start || !end) return true
  const startTime = new Date(start).getTime()
  const endTime = new Date(end).getTime()
  return now() >= startTime && now() <= endTime
}

const activePromotions = computed(() =>
  promotions.value
    .filter((promo) => promo.enabled && isActiveByDate(promo.startDate, promo.endDate))
    .sort((a, b) => (a.priority ?? 0) - (b.priority ?? 0))
)

const activeCoupons = computed(() =>
  coupons.value
    .filter((coupon) => coupon.enabled && isActiveByDate(coupon.validFrom, coupon.validUntil))
    .sort((a, b) => new Date(a.validUntil).getTime() - new Date(b.validUntil).getTime())
)

const formatCurrency = (value?: number) => {
  if (value === undefined || value === null) return '0'
  return new Intl.NumberFormat('zh-TW', { maximumFractionDigits: 0 }).format(value)
}

const formatDateRange = (start?: string, end?: string) => {
  if (!start || !end) return '期限依活動公告'
  const startDate = new Date(start).toLocaleDateString('zh-TW')
  const endDate = new Date(end).toLocaleDateString('zh-TW')
  return `${startDate} - ${endDate}`
}

const getDaysLeft = (end?: string) => {
  if (!end) return null
  const diff = new Date(end).getTime() - now()
  if (diff <= 0) return 0
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
}

const formatPromotionType = (type: Promotion['type']) => {
  const map: Record<Promotion['type'], string> = {
    DISCOUNT: '折扣活動',
    FULL_SHOP: '滿額優惠',
    FREE_SHIPPING: '免運活動',
    BUY_GIFT: '買就送'
  }
  return map[type] || '活動'
}

const formatPromotionBenefit = (promo: Promotion) => {
  if (promo.type === 'FREE_SHIPPING') return '免運'
  if (promo.discountType === 'PERCENTAGE') return `${promo.discountValue ?? 0}% OFF`
  if (promo.discountType === 'FIXED') return `折抵 ${formatCurrency(promo.discountValue)}`
  return promo.discountValue ? `折抵 ${formatCurrency(promo.discountValue)}` : '優惠中'
}

const formatCouponBenefit = (coupon: Coupon) => {
  if (coupon.type === 'FREE_SHIPPING') return '免運'
  if (coupon.type === 'PERCENTAGE') return `${coupon.discountValue}% OFF`
  return `折抵 ${formatCurrency(coupon.discountValue)}`
}

const copyCouponCode = async (code: string) => {
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(code)
    } else {
      const textarea = document.createElement('textarea')
      textarea.value = code
      textarea.style.position = 'fixed'
      textarea.style.left = '-9999px'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
    }
    liveMessage.value = '折扣碼已複製'
    Notify.create({ type: 'positive', message: '折扣碼已複製', position: 'top' })
  } catch (error) {
    liveMessage.value = '複製失敗，請手動輸入'
    Notify.create({ type: 'negative', message: '複製失敗，請手動輸入', position: 'top' })
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const [promotionResult, couponResult] = await Promise.all([
      promotionApi.getPromotions(0, 50),
      couponApi.getCoupons(0, 50)
    ])
    promotions.value = promotionResult.content || []
    coupons.value = couponResult.content || []
  } catch (error) {
    liveMessage.value = '載入促銷資訊失敗，請稍後再試'
    Notify.create({ type: 'negative', message: '載入促銷資訊失敗，請稍後再試', position: 'top' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.store-page {
  max-width: 1180px;
  margin: 0 auto;
}

.promo-hero {
  border-radius: 24px;
  background: linear-gradient(135deg, #1f2937 0%, #374151 40%, #c08457 100%);
  color: #fffaf2;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 60px rgba(30, 41, 59, 0.2);
}

.promo-hero__content {
  max-width: 560px;
}

.promo-title {
  margin: 8px 0;
  font-size: 2.2rem;
  line-height: 1.2;
  text-wrap: balance;
}

.promo-subtitle {
  margin: 0;
  color: rgba(255, 251, 245, 0.9);
  line-height: 1.65;
  text-wrap: pretty;
}

.promo-primary-btn {
  border-radius: 999px;
  background: #fff !important;
  color: #6b3b1c !important;
  font-weight: 700;
}

.promo-secondary-btn {
  border-radius: 999px;
  color: #fff3e0 !important;
}

.promo-hero__meta {
  display: grid;
  gap: 12px;
}

.promo-meta-card {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  color: #fff7ed;
}

.promo-meta-title {
  font-weight: 700;
  font-size: 0.95rem;
}

.promo-meta-text {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.85);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sf-section-title {
  text-wrap: balance;
}

.sf-section-subtitle {
  text-wrap: pretty;
}

.promo-card,
.coupon-card {
  border-radius: 18px;
  border-color: #eadfcd;
  height: 100%;
}

.promo-card .text-subtitle1,
.coupon-card .text-subtitle1 {
  overflow-wrap: anywhere;
}

.promo-card .text-body2,
.coupon-card .text-body2 {
  overflow-wrap: anywhere;
}

.promo-card__top,
.coupon-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.coupon-code {
  font-weight: 800;
  letter-spacing: 0.08em;
  font-size: 1rem;
  color: #8f4f2d;
}

.promo-benefit {
  background: #fff6eb;
  border-radius: 12px;
  padding: 10px 12px;
}

.benefit-value {
  font-size: 1.1rem;
  font-weight: 800;
  color: #7c4023;
}

.benefit-label {
  font-size: 0.75rem;
  color: #8b6f5a;
  margin-top: 2px;
}

.promo-info {
  display: grid;
  gap: 6px;
  font-size: 0.82rem;
  color: #6b5c50;
}

.promo-info :deep(.q-icon) {
  margin-right: 4px;
}

.promo-actions :deep(.q-btn) {
  border-radius: 999px;
  min-height: 40px;
}

.empty-state {
  border: 1px dashed #eadfcd;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  background: #fffdf9;
}

.promo-card--skeleton {
  border-radius: 16px;
  background: #fffaf5;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.skeleton-line {
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(90deg, #f2e7d6 0%, #fff6eb 50%, #f2e7d6 100%);
  margin-bottom: 10px;
}

.skeleton-line--lg {
  height: 16px;
}

.skeleton-line--short {
  width: 60%;
}

@media (max-width: 900px) {
  .promo-hero {
    grid-template-columns: 1fr;
  }

  .promo-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 640px) {
  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .promo-actions {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
