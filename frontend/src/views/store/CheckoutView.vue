﻿<template>
  <q-page class="store-page q-pa-md q-pa-lg-lg">
    <EmailVerifyBanner />
    <section class="q-mb-md">
      <q-card flat bordered class="flow-card">
        <q-card-section class="q-py-sm">
          <div class="flow-wrap">
            <div
              v-for="step in flowSteps"
              :key="step.key"
              class="flow-step"
              :class="{ 'flow-step--active': step.active, 'flow-step--done': step.done }"
            >
              <div class="flow-step__dot"><q-icon :name="step.done ? 'check' : step.icon" size="16px" /></div>
              <span>{{ step.label }}</span>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </section>

    <div class="row q-col-gutter-lg">
      <div class="col-12 col-lg-8">
        <q-card bordered class="checkout-card">
          <q-card-section>
            <div class="row items-center justify-between q-mb-md q-gutter-sm">
              <div>
                <h1 class="text-h5 text-weight-bold q-mb-xs">填寫結帳資料</h1>
                <p class="text-grey-7 q-mb-none">請確認收件人資訊、配送方式與付款方式，送出後將建立訂單並導向付款頁面。</p>
              </div>
              <q-btn flat no-caps icon="history" label="套用暫存資料" @click="applySavedDraft" />
            </div>

            <q-form ref="checkoutFormRef" class="q-gutter-md" @submit.prevent="submitCheckout">
              <q-input
                v-model="form.customerName"
                outlined
                label="收件人姓名"
                name="customer-name"
                autocomplete="name"
                :rules="[(val: string) => !!val || '請輸入收件人姓名']"
              />

              <q-input
                v-model="form.customerPhone"
                type="tel"
                outlined
                label="收件人電話"
                name="customer-phone"
                autocomplete="tel"
                inputmode="numeric"
                hint="此欄位會自動暫存，方便下次結帳快速帶入。"
                :rules="[(val: string) => !!val || '請輸入聯絡電話', (val: string) => /^09\d{8}$/.test(val) || '請輸入正確的手機號碼格式 (09xxxxxxxx)']"
              />

              <q-input
                v-if="form.shippingMethod === 'DELIVERY'"
                v-model="form.shippingAddress"
                outlined
                type="textarea"
                autogrow
                label="收件地址"
                name="shipping-address"
                autocomplete="street-address"
                hint="此欄位會自動暫存，送出前請再次確認內容正確。"
                :rules="[(val: string) => !!val || '請輸入收件地址']"
              />

              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.paymentMethod"
                    outlined
                    label="付款方式"
                    :options="paymentOptions"
                    emit-value
                    map-options
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.shippingMethod"
                    outlined
                    label="配送方式"
                    :options="shippingOptions"
                    emit-value
                    map-options
                  />
                </div>
              </div>

              <q-checkbox v-model="marketingOptIn" dense label="我願意收到優惠與新品通知 Email（可隨時取消訂閱）" />

              <q-banner rounded class="bg-blue-1 text-primary">
                <template #avatar><q-icon name="lock" /></template>
                訂單建立後若選擇 ECPay，系統會導向 ECPay 付款頁完成付款；請勿關閉或重整頁面直到導轉完成。
              </q-banner>

              <div class="row q-gutter-md q-mt-sm checkout-actions">
                <q-btn
                  outline
                  no-caps
                  color="grey-7"
                  icon="arrow_back"
                  label="上一步"
                  class="back-btn"
                  @click="router.push('/cart')"
                />
                <q-btn
                  color="primary"
                  no-caps
                  icon-right="arrow_forward"
                  label="送出訂單並前往付款"
                  type="submit"
                  class="submit-btn"
                  :disable="items.length === 0 || submitting || quoting || !quote || !!quoteError"
                  :loading="submitting"
                />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-12 col-lg-4">
        <q-card bordered class="summary-card sticky-summary">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-md">訂單摘要</div>
            <div v-if="items.length === 0" class="text-grey-7">購物車目前沒有商品，請先回到商品列表加入商品後再進行結帳。</div>
            <div v-else>
              <div v-for="item in items" :key="`${item.productId}-${item.specificationId ?? 'base'}`" class="summary-item q-mb-sm">
                <div class="summary-item__info">
                  <span class="text-body2 text-weight-medium">{{ item.name }}</span>
                  <span v-if="item.specName" class="text-caption text-grey-6"> ({{ item.specName }})</span>
                  <span class="text-body2"> x {{ item.quantity }}</span>
                </div>
                <div class="summary-item__actions">
                  <span class="text-body2 text-weight-medium">NT$ {{ formatPrice(item.price * item.quantity) }}</span>
                  <q-btn
                    flat
                    dense
                    round
                    icon="close"
                    size="sm"
                    color="grey-5"
                    @click="removeItem(item)"
                    class="q-ml-xs"
                  >
                    <q-tooltip>移除商品</q-tooltip>
                  </q-btn>
                </div>
              </div>
            </div>
            <q-separator class="q-my-md" />
            <div class="amount-row">
              <span>商品小計</span>
              <span>NT$ {{ formatPrice(subtotal) }}</span>
            </div>
            <div class="amount-row">
              <span>運費</span>
              <span v-if="!quote">—</span>
              <span v-else-if="shippingFee > 0">NT$ {{ formatPrice(shippingFee) }}</span>
              <span v-else class="text-positive">{{ form.shippingMethod === 'STORE_PICKUP' ? '門市自取免運' : '免運費' }}</span>
            </div>
            <div v-for="discount in amountDiscounts" :key="discount.type + (discount.name || '')" class="amount-row text-negative">
              <span>{{ discountLabel(discount) }}</span>
              <span>-NT$ {{ formatPrice(Number(discount.amount)) }}</span>
            </div>
            <div v-if="freeShippingDiscount && form.shippingMethod === 'DELIVERY'" class="amount-row text-positive">
              <span>{{ discountLabel(freeShippingDiscount) }}</span>
              <span>免運</span>
            </div>
            <div
              v-if="quote && quote.freeShippingThreshold && shippingFee > 0 && subtotal < Number(quote.freeShippingThreshold)"
              class="text-caption text-grey-7 q-mb-sm"
            >
              再消費 NT$ {{ formatPrice(Number(quote.freeShippingThreshold) - subtotal) }} 即可免運費
            </div>

            <div class="coupon-row q-mt-sm">
              <q-input
                v-model="couponInput"
                dense
                outlined
                placeholder="優惠券代碼"
                maxlength="50"
                class="col"
                :disable="quoting"
                @keyup.enter="applyCoupon"
              />
              <q-btn v-if="!requestedCoupon" color="primary" unelevated no-caps label="套用" :disable="!couponInput.trim() || quoting" @click="applyCoupon" />
              <q-btn v-else flat no-caps color="grey-8" label="移除" @click="removeCoupon" />
            </div>
            <div v-if="requestedCoupon && quote?.couponCode" class="text-caption text-positive q-mt-xs">已套用優惠券 {{ quote.couponCode }}</div>
            <div v-else-if="requestedCoupon && quote?.couponMessage" class="text-caption text-orange-9 q-mt-xs">{{ quote.couponMessage }}</div>
            <div class="text-caption text-grey-6 q-mt-xs">活動、優惠券與會員折扣自動取折抵最多的一項；免運可併用。</div>

            <q-separator class="q-my-md" />
            <div class="row justify-between text-weight-bold text-subtitle1">
              <span>總計</span>
              <span class="text-primary">
                <q-spinner v-if="quoting" size="16px" class="q-mr-xs" />
                NT$ {{ formatPrice(total) }}
              </span>
            </div>
            <div v-if="quoteError" class="text-caption text-negative q-mt-sm">{{ quoteError }}</div>
            <div class="trust-list q-mt-md">
              <div class="trust-item"><q-icon name="verified" color="positive" /> 付款資訊於安全流程處理</div>
              <div class="trust-item"><q-icon name="local_shipping" color="primary" /> 配送方式可於下單前確認</div>
              <div class="trust-item"><q-icon name="support_agent" color="amber-8" /> 如需協助可聯絡客服</div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import EmailVerifyBanner from '@/components/store/EmailVerifyBanner.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { QForm, useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api'
import { orderApi, type StorefrontQuote, type StorefrontQuoteDiscount } from '@/api/order'
import { clearCart, getCartItems, removeFromCart, type CartItem } from '@/utils/storeCart'
import { getCheckoutDraft, saveCheckoutDraft } from '@/utils/storePreferences'
import { trackEvent } from '@/utils/tracking'
import { redirectToEcPay } from '@/utils/ecpay'

const router = useRouter()
const authStore = useAuthStore()
const $q = useQuasar()

const checkoutFormRef = ref<QForm>()
const items = ref<CartItem[]>(getCartItems())
const submitting = ref(false)
// 金額以後端試算為準（含運費、折扣與優惠券）
const quote = ref<StorefrontQuote | null>(null)
const quoting = ref(false)
const quoteError = ref('')
let quoteSeq = 0
const couponInput = ref('')
const marketingOptIn = ref(false)
const requestedCoupon = ref('')
const localSubtotal = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const subtotal = computed(() => (quote.value ? Number(quote.value.subtotalAmount) : localSubtotal.value))
const shippingFee = computed(() => Number(quote.value?.shippingFee ?? 0))
const total = computed(() => (quote.value ? Number(quote.value.totalAmount) : localSubtotal.value))
const amountDiscounts = computed(() => (quote.value?.discounts || []).filter((discount) => Number(discount.amount) > 0))
const freeShippingDiscount = computed(() => (quote.value?.discounts || []).find((discount) => discount.type === 'FREE_SHIPPING') || null)
const DISCOUNT_TYPE_LABELS: Record<string, string> = {
  PROMOTION: '活動折扣',
  COUPON: '優惠券',
  MEMBER_LEVEL: '會員折扣',
  FREE_SHIPPING: '免運優惠'
}
const discountLabel = (discount: StorefrontQuoteDiscount) => {
  const base = DISCOUNT_TYPE_LABELS[discount.type] || '折扣'
  const detail = discount.code ? `${discount.name}（${discount.code}）` : discount.name
  return detail ? `${base}：${detail}` : base
}

const toCheckoutItems = () =>
  items.value.map((item) => ({
    productId: item.productId,
    specificationId: item.specificationId ?? null,
    quantity: item.quantity
  }))

const refreshQuote = async () => {
  if (items.value.length === 0) {
    quote.value = null
    quoteError.value = ''
    return
  }
  const seq = ++quoteSeq
  quoting.value = true
  try {
    const response = await orderApi.storefrontQuote({
      items: toCheckoutItems(),
      shippingMethod: form.value.shippingMethod === 'STORE_PICKUP' ? 'STORE_PICKUP' : 'HOME_DELIVERY',
      couponCode: requestedCoupon.value || null
    })
    if (seq !== quoteSeq) return
    quote.value = response.data
    quoteError.value = ''
  } catch (error: any) {
    if (seq !== quoteSeq) return
    quote.value = null
    quoteError.value = error?.response?.data?.message || '無法確認商品價格與庫存，請稍後再試。'
  } finally {
    if (seq === quoteSeq) quoting.value = false
  }
}

const applyCoupon = () => {
  const code = couponInput.value.trim().toUpperCase()
  if (!code) return
  couponInput.value = code
  requestedCoupon.value = code
  refreshQuote()
}

const removeCoupon = () => {
  couponInput.value = ''
  requestedCoupon.value = ''
  refreshQuote()
}
const defaultGateway = (import.meta.env.VITE_DEFAULT_PAYMENT_GATEWAY || 'ECPAY').toUpperCase()

const paymentOptions = [
  { label: 'ECPay', value: 'ECPAY' },
  { label: '貨到付款', value: 'COD' }
]

const shippingOptions = [
  { label: '宅配到府', value: 'DELIVERY' },
  { label: '門市自取', value: 'STORE_PICKUP' }
]

const form = ref({
  customerName: '',
  customerPhone: '',
  shippingAddress: '',
  paymentMethod: defaultGateway === 'COD' ? 'COD' : 'ECPAY',
  shippingMethod: 'DELIVERY'
})

const flowSteps = computed(() => [
  { key: 'cart', label: '購物車', icon: 'shopping_cart', done: true, active: false },
  { key: 'checkout', label: '填寫資料', icon: 'edit_note', done: false, active: true },
  { key: 'payment', label: '付款', icon: 'payments', done: false, active: false },
  { key: 'done', label: '完成', icon: 'task_alt', done: false, active: false }
])

const formatPrice = (value: number) => value.toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const applySavedDraft = () => {
  const draft = getCheckoutDraft()
  form.value.customerName = form.value.customerName || draft.customerName || authStore.user?.username || ''
  form.value.customerPhone = draft.customerPhone || form.value.customerPhone
  form.value.shippingAddress = draft.shippingAddress || form.value.shippingAddress
  $q.notify({ type: 'info', message: '已套用先前暫存的結帳資料。' })
}

const removeItem = (item: CartItem) => {
  removeFromCart(item.productId, item.specificationId)
  items.value = getCartItems()

  refreshQuote()
  if (items.value.length === 0) {
    $q.notify({ type: 'warning', message: '購物車已清空，即將返回購物車頁面。' })
    setTimeout(() => router.push('/cart'), 1500)
  } else {
    $q.notify({ type: 'info', message: `已移除「${item.name}${item.specName ? ` (${item.specName})` : ''}」` })
  }
}

watch(
  () => ({
    customerName: form.value.customerName,
    customerPhone: form.value.customerPhone,
    shippingAddress: form.value.shippingAddress
  }),
  (draft) => saveCheckoutDraft(draft),
  { deep: true }
)

const submitCheckout = async () => {
  const valid = await checkoutFormRef.value?.validate()
  if (!valid || items.value.length === 0 || !quote.value || quoteError.value) return

  submitting.value = true
  try {
    let customerEmail = authStore.user?.email
    if (!customerEmail) {
      const profileResponse = await authApi.getProfile()
      customerEmail = profileResponse.data?.email
    }

    if (!customerEmail) {
      throw new Error('無法取得會員 Email，請重新登入後再試。')
    }

    trackEvent('checkout_submit', {
      payment_method: form.value.paymentMethod,
      shipping_method: form.value.shippingMethod,
      order_amount: total.value
    })

    trackEvent('payment_create_request', {
      payment_method: form.value.paymentMethod,
      order_amount: total.value
    })

    // 價格、運費與付款金額一律由後端依商品資料計算
    const response = await orderApi.storefrontCheckout({
      customerName: form.value.customerName,
      customerPhone: form.value.customerPhone,
      customerEmail,
      shippingAddress: form.value.shippingMethod === 'DELIVERY' ? form.value.shippingAddress : null,
      shippingMethod: form.value.shippingMethod === 'STORE_PICKUP' ? 'STORE_PICKUP' : 'HOME_DELIVERY',
      paymentMethod: form.value.paymentMethod === 'COD' ? 'COD' : 'ECPAY',
      channel: 'ADMIN_STORE',
      couponCode: quote.value?.couponCode || null,
      marketingOptIn: marketingOptIn.value,
      items: toCheckoutItems()
    })

    const result = response.data
    const orderNumber = result.order.orderNumber
    const amount = Number(result.order.totalAmount)

    sessionStorage.setItem(
      'last_purchase_items',
      JSON.stringify(items.value.map((item) => ({ product_id: item.productId, product_name: item.name, quantity: item.quantity, price: item.price })))
    )

    clearCart()

    if (result.paymentMethod === 'ECPAY') {
      if (!result.paymentUrl) {
        $q.notify({ type: 'warning', message: result.paymentError || '訂單已建立，但付款頁面暫時無法開啟，請在訂單頁點「前往付款」重試。' })
      } else {
        redirectToEcPay(result.paymentUrl)
        return
      }
    }

    router.push({ path: '/order/success', query: { orderNumber, amount: String(amount) } })
  } catch (error: any) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || error?.message || '建立訂單失敗，請稍後再試。' })
  } finally {
    submitting.value = false
  }
}

watch(() => form.value.shippingMethod, () => refreshQuote())

onMounted(() => {
  trackEvent('view_checkout')
  refreshQuote()
  const draft = getCheckoutDraft()
  form.value.customerName = draft.customerName || authStore.user?.username || ''
  form.value.customerPhone = draft.customerPhone || ''
  form.value.shippingAddress = draft.shippingAddress || ''
})
</script>

<style scoped>
.store-page { max-width: 1180px; margin: 0 auto; }
.checkout-card, .summary-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-color: #eadfcd;
  box-shadow: 0 10px 28px rgba(36, 30, 24, 0.05);
}
.flow-card {
  border-radius: 14px;
  background: rgba(255,255,255,.9);
  border-color: #eadfcd;
}
.flow-wrap { display:flex; gap:8px; flex-wrap:wrap; }
.flow-step {
  display:inline-flex; align-items:center; gap:8px; border:1px solid #e5e7eb; border-radius:999px; padding:8px 12px; color:#64748b; min-height: 44px;
}
.flow-step--active { border-color:#93c5fd; background:#eff6ff; color:#1d4ed8; font-weight:600; }
.flow-step--done { border-color:#86efac; background:#f0fdf4; color:#15803d; }
.flow-step__dot { width:22px; height:22px; border-radius:999px; display:inline-flex; align-items:center; justify-content:center; background:rgba(148,163,184,.15); }
.trust-list { display:grid; gap:6px; color:#475569; font-size:.88rem; }
.trust-item { display:flex; align-items:center; gap:8px; }
.amount-row { display:flex; justify-content:space-between; gap:12px; margin-bottom:6px; font-size:.92rem; }
.coupon-row { display:flex; gap:8px; align-items:center; }
.summary-item { display:flex; justify-content:space-between; align-items:center; }
.summary-item__info { flex:1; min-width:0; }
.summary-item__actions { display:flex; align-items:center; gap:4px; flex-shrink:0; }
.checkout-actions { display:flex; justify-content:space-between; align-items:center; }
.checkout-actions .back-btn { min-height:44px; border-radius:12px; }
.checkout-actions .submit-btn { flex:1; min-height:44px; border-radius:12px; }
.checkout-card :deep(.q-btn) { min-height: 44px; border-radius: 12px; }
.checkout-card :deep(.q-field--outlined .q-field__control) { border-radius: 14px; }
@media (min-width: 1024px) { .sticky-summary { position: sticky; top: 118px; } }
@media (max-width: 700px) {
  .flow-wrap { gap: 6px; }
  .flow-step { width: 100%; justify-content: center; }
  .checkout-card :deep(.q-btn) { width: 100%; }
}
</style>
