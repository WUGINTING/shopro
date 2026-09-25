<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <div class="row justify-center">
      <div class="col-12 col-md-10 col-lg-8">
        <q-card bordered class="success-card">
          <q-card-section class="q-pa-lg">
            <div class="success-icon"><q-icon name="task_alt" size="38px" /></div>
            <div class="sf-chip sf-chip--green q-mb-sm">訂單已建立</div>
            <h1 class="sf-page-title q-mb-sm">訂單已送出，正在等待付款或後續處理</h1>
            <p class="sf-page-lead q-mt-none">
              我們已收到你的訂單。若為線上付款，請依付款流程完成交易；完成後可於會員中心查看訂單進度。
            </p>

            <div class="summary-grid q-my-md">
              <div class="summary-item">
                <div class="summary-item__label">訂單編號</div>
                <div class="summary-item__value">{{ orderNumber || '-' }}</div>
              </div>
            <div class="summary-item">
              <div class="summary-item__label">訂單金額</div>
              <div class="summary-item__value">
                <span v-if="hasTrustedQuery">NT$ {{ amountDisplay }}</span>
                <span v-else>-</span>
              </div>
            </div>
              <div class="summary-item">
                <div class="summary-item__label">訂單狀態</div>
                <div class="summary-item__value">
                  <q-chip
                    dense
                    square
                    :color="getOrderStatusColor(orderStatus)"
                    :text-color="getOrderStatusTextColor(orderStatus)"
                  >
                    {{ getOrderStatusLabel(orderStatus) }}
                  </q-chip>
                </div>
              </div>
            </div>

            <q-banner v-if="canPayOnline && orderStatus === 'PENDING_PAYMENT'" rounded class="bg-orange-1 text-orange-10 q-mb-md">
              <template #avatar>
                <q-icon name="payments" />
              </template>
              這筆訂單尚未完成付款。若付款頁已關閉或逾時，可以重新前往付款。
              <template #action>
                <q-btn color="primary" unelevated no-caps label="前往付款" :loading="paying" @click="payNow" />
              </template>
            </q-banner>
            <q-banner v-if="!hasTrustedQuery" rounded class="sf-warning-note q-mb-md">
              <template #avatar>
                <q-icon name="report_problem" />
              </template>
              這筆訂單資訊尚未完成後端驗證，請以會員中心的訂單紀錄為準。
            </q-banner>

            <q-banner rounded class="sf-success-note q-mb-md">
              <template #avatar>
                <q-icon name="history" />
              </template>
              在會員中心可查看訂單狀態與歷史紀錄，若有問題請附訂單編號聯絡客服。
            </q-banner>
          </q-card-section>

          <q-card-actions align="right" class="q-pa-md q-pt-none">
            <q-btn flat no-caps label="繼續逛逛" @click="router.push('/')" />
            <q-btn color="primary" no-caps class="success-cta" label="查看我的訂單" @click="router.push('/account/orders')" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api/order'
import { redirectToEcPay } from '@/utils/ecpay'
import { useAuthStore } from '@/stores/auth'
import { trackEvent } from '@/utils/tracking'
import { getOrderStatusColor, getOrderStatusLabel, getOrderStatusTextColor } from '@/utils/orderStatus'

const route = useRoute()
const router = useRouter()

const authStore = useAuthStore()

// 從綠界返回時網址只有訂單編號，以登入會員的 Email 向後端查詢實際金額與付款狀態
const liveOrder = ref<{ status?: string; totalAmount?: number } | null>(null)
const canPayOnline = ref(false)
const paying = ref(false)

const orderNumber = computed(() => String(route.query.orderNumber || ''))
const amount = computed(() => Number(liveOrder.value?.totalAmount ?? route.query.amount ?? 0))
const orderStatus = computed(() => String(liveOrder.value?.status || route.query.status || 'PENDING_PAYMENT'))

const loadLiveStatus = async () => {
  const email = authStore.user?.email
  if (!orderNumber.value || !email) return
  try {
    const response = await orderApi.storefrontLookup(orderNumber.value, email)
    liveOrder.value = (response.data?.order as { status?: string; totalAmount?: number }) ?? null
    canPayOnline.value = Boolean(response.data?.canPayOnline)
  } catch {
    // 查詢失敗時沿用網址資訊
  }
}
const payNow = async () => {
  const email = authStore.user?.email
  if (!orderNumber.value || !email) return
  paying.value = true
  try {
    const response = await orderApi.storefrontPay({ orderNumber: orderNumber.value, email, channel: 'ADMIN_STORE' })
    if (response.data?.paymentUrl) {
      redirectToEcPay(response.data.paymentUrl)
      return
    }
  } catch {
    // 錯誤訊息由系統通知顯示
  }
  paying.value = false
}

const hasTrustedQuery = computed(() => Boolean(orderNumber.value) && amount.value > 0)
const amountDisplay = computed(() => amount.value.toLocaleString('zh-TW'))

onMounted(() => {
  loadLiveStatus()
  let items: Array<Record<string, unknown>> = []
  const raw = sessionStorage.getItem('last_purchase_items')
  if (raw) {
    try {
      items = JSON.parse(raw)
    } catch {
      items = []
    }
  }

  if (hasTrustedQuery.value) {
    trackEvent('purchase', {
      order_number: orderNumber.value,
      order_amount: amount.value,
      items
    })
  }

  sessionStorage.removeItem('last_purchase_items')
})
</script>

<style scoped>
.success-card {
  border-radius: 20px;
  border-color: #dfe9df;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 16px 40px rgba(28, 54, 44, 0.07);
}

.success-icon {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ecfdf5;
  color: #0f766e;
  border: 1px solid #bdebd4;
  margin-bottom: 12px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.summary-item {
  border: 1px solid #eadfcd;
  border-radius: 14px;
  background: #fffaf4;
  padding: 12px;
}

.summary-item__label {
  color: #6b7280;
  font-size: 0.82rem;
}

.summary-item__value {
  margin-top: 4px;
  font-weight: 700;
  color: #2f241f;
}

.success-cta {
  border-radius: 999px;
}

.sf-warning-note {
  border: 1px solid #f5d7b2;
  background: #fff5e6;
  color: #7a4a1a;
}

@media (max-width: 700px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
