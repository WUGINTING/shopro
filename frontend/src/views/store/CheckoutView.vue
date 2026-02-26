<template>
  <q-page class="store-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <q-card flat bordered class="flow-card">
        <q-card-section class="q-py-sm">
          <div class="flow-wrap">
            <div v-for="step in flowSteps" :key="step.key" class="flow-step" :class="{ 'flow-step--active': step.active, 'flow-step--done': step.done }">
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
                <h1 class="text-h5 text-weight-bold q-mb-xs">結帳資訊</h1>
                <p class="text-grey-7 q-mb-none">系統會在本機暫存最近使用的電話與地址，方便下次更快完成結帳。</p>
              </div>
              <q-btn flat no-caps icon="history" label="套用暫存資料" @click="applySavedDraft" />
            </div>

            <q-form @submit.prevent="submitCheckout" class="q-gutter-md" ref="checkoutFormRef">
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
                label="收件人手機"
                name="customer-phone"
                autocomplete="tel"
                inputmode="numeric"
                hint="若有暫存資料，會自動帶入上次結帳資訊"
                :rules="[(val: string) => !!val || '請輸入收件人手機']"
              />

              <q-input
                v-model="form.shippingAddress"
                outlined
                type="textarea"
                autogrow
                label="配送地址"
                name="shipping-address"
                autocomplete="street-address"
                hint="若有暫存資料，會自動帶入上次結帳資訊"
                :rules="[(val: string) => !!val || '請輸入配送地址']"
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

              <q-banner rounded class="bg-blue-1 text-primary">
                <template #avatar><q-icon name="lock" /></template>
                安全結帳流程。若選擇 ECPay，建立訂單後會自動導向付款頁完成交易。
              </q-banner>

              <q-btn color="primary" no-caps label="送出訂單" type="submit" :disable="items.length === 0 || submitting" :loading="submitting" />
            </q-form>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-12 col-lg-4">
        <q-card bordered class="summary-card sticky-summary">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-md">訂單摘要</div>
            <div v-if="items.length === 0" class="text-grey-7">購物車目前沒有商品，請先加入商品後再結帳。</div>
            <div v-else>
              <div v-for="item in items" :key="item.productId" class="row justify-between q-mb-sm text-body2">
                <span>{{ item.name }} x {{ item.quantity }}</span>
                <span>NT$ {{ formatPrice(item.price * item.quantity) }}</span>
              </div>
            </div>
            <q-separator class="q-my-md" />
            <div class="row justify-between text-weight-bold text-subtitle1">
              <span>總計</span>
              <span class="text-primary">NT$ {{ formatPrice(total) }}</span>
            </div>
            <div class="trust-list q-mt-md">
              <div class="trust-item"><q-icon name="verified" color="positive" /> 付款回傳狀態追蹤</div>
              <div class="trust-item"><q-icon name="local_shipping" color="primary" /> 配送狀態可見</div>
              <div class="trust-item"><q-icon name="support_agent" color="amber-8" /> 可從聯絡頁快速取得支援</div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { QForm, useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api'
import { orderApi } from '@/api/order'
import { createEcPayPayment } from '@/api/payment'
import { clearCart, getCartItems, getCartTotal } from '@/utils/storeCart'
import { getCheckoutDraft, saveCheckoutDraft } from '@/utils/storePreferences'
import { trackEvent } from '@/utils/tracking'

const router = useRouter()
const authStore = useAuthStore()
const $q = useQuasar()

const checkoutFormRef = ref<QForm>()
const items = ref(getCartItems())
const submitting = ref(false)
const total = computed(() => getCartTotal())
const defaultGateway = (import.meta.env.VITE_DEFAULT_PAYMENT_GATEWAY || 'ECPAY').toUpperCase()

const paymentOptions = [
  { label: 'ECPay', value: 'ECPAY' },
  { label: '貨到付款', value: 'COD' }
]

const shippingOptions = [
  { label: '宅配', value: 'DELIVERY' },
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
  { key: 'cart', label: 'Cart', icon: 'shopping_cart', done: true, active: false },
  { key: 'checkout', label: 'Checkout', icon: 'edit_note', done: false, active: true },
  { key: 'payment', label: 'Payment', icon: 'payments', done: false, active: false },
  { key: 'done', label: 'Done', icon: 'task_alt', done: false, active: false }
])

const formatPrice = (value: number) => value.toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const applySavedDraft = () => {
  const draft = getCheckoutDraft()
  form.value.customerName = form.value.customerName || draft.customerName || authStore.user?.username || ''
  form.value.customerPhone = draft.customerPhone || form.value.customerPhone
  form.value.shippingAddress = draft.shippingAddress || form.value.shippingAddress
  $q.notify({ type: 'info', message: '已套用暫存結帳資料（若有）。' })
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

const redirectToEcPay = (paymentUrl: string) => {
  const url = new URL(paymentUrl)
  const formEl = document.createElement('form')
  formEl.method = 'POST'
  formEl.action = `${url.origin}${url.pathname}`
  formEl.style.display = 'none'

  url.searchParams.forEach((value, key) => {
    const input = document.createElement('input')
    input.type = 'hidden'
    input.name = key
    input.value = value
    formEl.appendChild(input)
  })

  document.body.appendChild(formEl)
  formEl.submit()
}

const submitCheckout = async () => {
  const valid = await checkoutFormRef.value?.validate()
  if (!valid || items.value.length === 0 || total.value <= 0) return

  submitting.value = true
  try {
    let customerId = authStore.user?.id
    if (!customerId) {
      const profileResponse = await authApi.getProfile()
      customerId = profileResponse.data?.id
      if (customerId && authStore.user) {
        authStore.setAuth(authStore.token || '', { ...authStore.user, id: customerId })
      }
    }

    if (!customerId) {
      throw new Error('缺少顧客資料，請重新登入後再試。')
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

    const payload = {
      customerId,
      customerName: form.value.customerName,
      customerPhone: form.value.customerPhone,
      customerEmail: authStore.user?.email,
      pickupType: form.value.shippingMethod,
      subtotalAmount: total.value,
      discountAmount: 0,
      shippingFee: 0,
      shippingAddress: form.value.shippingAddress,
      totalAmount: total.value,
      status: 'PENDING_PAYMENT' as const,
      items: items.value.map((item) => ({ productId: item.productId, quantity: item.quantity, unitPrice: item.price }))
    }

    const response = await orderApi.createOrder(payload as any)
    const orderNumber = response.data?.orderNumber ?? `TEMP-${Date.now()}`
    const amount = Number(response.data?.totalAmount ?? total.value)

    sessionStorage.setItem(
      'last_purchase_items',
      JSON.stringify(items.value.map((item) => ({ product_id: item.productId, product_name: item.name, quantity: item.quantity, price: item.price })))
    )

    clearCart()

    if (form.value.paymentMethod === 'ECPAY') {
      const paymentResponse = await createEcPayPayment({
        orderId: response.data?.id,
        orderNumber,
        amount,
        currency: 'TWD',
        productName: `Shopro Order ${orderNumber}`,
        customerName: form.value.customerName || authStore.user?.username,
        customerEmail: authStore.user?.email,
        customerPhone: form.value.customerPhone
      })

      const paymentUrl = paymentResponse.data?.paymentUrl
      if (!paymentUrl) throw new Error('ECPay payment URL not found')

      redirectToEcPay(paymentUrl)
      return
    }

    router.push({ path: '/order/success', query: { orderNumber, amount: String(amount) } })
  } catch (error: any) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || error?.message || '建立訂單失敗，請稍後再試。' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  trackEvent('view_checkout')
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
.checkout-card :deep(.q-btn) { min-height: 44px; border-radius: 12px; }
.checkout-card :deep(.q-field--outlined .q-field__control) { border-radius: 14px; }
@media (min-width: 1024px) { .sticky-summary { position: sticky; top: 118px; } }
@media (max-width: 700px) {
  .flow-wrap { gap: 6px; }
  .flow-step { width: 100%; justify-content: center; }
  .checkout-card :deep(.q-btn) { width: 100%; }
}
</style>
