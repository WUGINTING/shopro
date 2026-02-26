<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <q-card flat bordered class="flow-card">
        <q-card-section class="q-py-sm">
          <div class="sf-flow">
            <div v-for="step in cartFlowSteps" :key="step.key">
              <div class="sf-flow__step" :class="{ 'sf-flow__step--active': step.active }">
                <q-icon :name="step.icon" size="16px" />
                <span>{{ step.label }}</span>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </section>

    <div class="row items-center justify-between q-mb-md">
      <div>
        <h1 class="sf-page-title">購物車</h1>
        <p class="sf-page-lead">確認商品與數量後即可前往結帳，系統會保留你的購物車內容直到完成下單。</p>
      </div>
      <q-btn flat no-caps label="繼續購物" icon="arrow_back" @click="router.push('/products')" />
    </div>

    <q-banner v-if="items.length === 0" rounded class="empty-banner q-pa-lg">
      <template #avatar>
        <q-icon name="shopping_cart" color="grey-6" />
      </template>
      <div class="empty-cart-copy">
        <div class="text-weight-medium">你的購物車目前是空的</div>
        <div class="text-body2 text-grey-7">先到商品列表挑選需要的商品，再回到這裡確認訂單摘要與付款資訊。</div>
      </div>
      <template #action>
        <q-btn unelevated color="primary" no-caps label="前往商品列表" @click="router.push('/products')" />
      </template>
    </q-banner>

    <div v-else class="row q-col-gutter-lg">
      <div class="col-12 col-lg-8">
        <q-markup-table flat bordered class="cart-table">
          <thead>
            <tr>
              <th class="text-left">商品</th>
              <th class="text-right">單價</th>
              <th class="text-center">數量</th>
              <th class="text-right">小計</th>
              <th class="text-center">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.productId">
              <td class="text-weight-medium">{{ item.name }}</td>
              <td class="text-right">NT$ {{ formatPrice(item.price) }}</td>
              <td class="text-center">
                <q-input
                  :model-value="item.quantity"
                  type="number"
                  min="1"
                  dense
                  outlined
                  style="width: 92px"
                  :aria-label="`調整 ${item.name} 的購買數量`"
                  @update:model-value="(v) => updateQty(item.productId, Number(v))"
                />
              </td>
              <td class="text-right text-weight-medium">NT$ {{ formatPrice(item.price * item.quantity) }}</td>
              <td class="text-center">
                <q-btn flat color="negative" no-caps label="移除" @click="remove(item.productId)" />
              </td>
            </tr>
          </tbody>
        </q-markup-table>
      </div>

      <div class="col-12 col-lg-4">
        <q-card bordered class="summary-card sticky-summary">
          <q-card-section>
            <div class="text-subtitle1 text-weight-bold q-mb-md">訂單摘要</div>
            <div class="sf-kv">
              <span>商品金額</span>
              <span class="text-weight-medium">NT$ {{ formatPrice(total) }}</span>
            </div>
            <div class="sf-kv">
              <span>配送費用</span>
              <span class="text-positive text-weight-medium">依結帳配送方式計算</span>
            </div>
            <q-separator class="q-my-md" />
            <div class="row justify-between text-subtitle1 text-weight-bold">
              <span>預估總計</span>
              <span class="text-primary">NT$ {{ formatPrice(total) }}</span>
            </div>
            <q-banner rounded class="sf-success-note q-mt-md">
              <template #avatar>
                <q-icon name="verified_user" />
              </template>
              結帳前會再次確認收件資訊與付款方式，送出後將導向 ECPay 或對應付款流程。
            </q-banner>
            <div class="q-mt-md summary-trust-list">
              <div><q-icon name="lock" size="16px" /> 安全的訂單資訊傳輸</div>
              <div><q-icon name="payments" size="16px" /> 支援 ECPay / 貨到付款</div>
              <div><q-icon name="help_outline" size="16px" /> 付款前可返回修改商品與數量</div>
            </div>
          </q-card-section>
          <q-card-actions vertical class="q-pa-md q-pt-none">
            <q-btn color="primary" no-caps class="checkout-btn" label="前往結帳" :disable="items.length === 0" @click="goCheckout" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { getCartItems, getCartTotal, removeFromCart, updateCartQuantity, type CartItem } from '@/utils/storeCart'
import { trackEvent } from '@/utils/tracking'

const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()
const items = ref<CartItem[]>([])

const cartFlowSteps = [
  { key: 'cart', label: '購物車', icon: 'shopping_cart', active: true },
  { key: 'checkout', label: '填寫結帳資料', icon: 'edit_note', active: false },
  { key: 'payment', label: '付款', icon: 'payments', active: false }
]

const total = computed(() => getCartTotal())
const formatPrice = (value: number) => value.toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const reload = () => {
  items.value = getCartItems()
}

const updateQty = (productId: number, qty: number) => {
  updateCartQuantity(productId, Math.max(1, qty))
  reload()
}

const remove = (productId: number) => {
  removeFromCart(productId)
  reload()
}

const goCheckout = () => {
  if (!authStore.isAuthenticated) {
    $q.notify({
      type: 'warning',
      message: '請先登入會員後再進行結帳。'
    })
    router.push('/login')
    return
  }

  if (authStore.userRole !== 'CUSTOMER') {
    $q.notify({
      type: 'warning',
      message: '目前僅提供顧客帳號使用前台結帳流程。'
    })
    return
  }

  trackEvent('begin_checkout', {
    cart_total: total.value,
    items: items.value.map((x) => ({
      product_id: x.productId,
      quantity: x.quantity,
      price: x.price
    }))
  })
  router.push('/checkout')
}

onMounted(() => {
  reload()
  trackEvent('view_cart')
})
</script>

<style scoped>
.store-page {
  max-width: 1180px;
  margin: 0 auto;
}

.cart-table,
.summary-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-color: #eadfcd;
  box-shadow: 0 10px 26px rgba(36, 30, 24, 0.05);
}

.cart-table {
  display: block;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.flow-card {
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.88);
  border-color: #eadfcd;
}

.empty-cart-copy {
  display: grid;
  gap: 4px;
}

.empty-banner {
  background: rgba(255, 255, 255, 0.94);
  color: #334155;
  border: 1px solid #eadfcd;
}

.summary-trust-list {
  display: grid;
  gap: 8px;
  color: #556070;
  font-size: 0.86rem;
}

.summary-trust-list > div {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.checkout-btn {
  border-radius: 999px;
  min-height: 46px;
  font-weight: 700;
}

@media (min-width: 1024px) {
  .sticky-summary {
    position: sticky;
    top: 118px;
  }
}

@media (max-width: 700px) {
  .store-page > .row.items-center {
    gap: 10px;
    align-items: flex-start;
  }

  .checkout-btn {
    width: 100%;
  }
}
</style>
