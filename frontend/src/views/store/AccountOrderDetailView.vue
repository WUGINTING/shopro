<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md row items-center justify-between q-gutter-sm">
      <q-btn flat no-caps icon="arrow_back" label="返回訂單列表" @click="router.push('/account/orders')" />
      <q-chip
        square
        :color="getOrderStatusColor(order?.status)"
        :text-color="getOrderStatusTextColor(order?.status)"
      >
        {{ getOrderStatusLabel(order?.status) }}
      </q-chip>
    </section>

    <q-card v-if="loading" bordered class="sf-card">
      <q-card-section>
        <q-skeleton type="text" width="40%" />
        <q-skeleton type="text" width="70%" class="q-mt-sm" />
        <q-skeleton type="rect" height="180px" class="q-mt-md" />
      </q-card-section>
    </q-card>

    <q-banner v-else-if="loadError" rounded class="bg-red-1 text-red-9 q-mb-md">
      載入訂單詳情失敗，請稍後再試。
    </q-banner>

    <template v-else-if="order">
      <q-card bordered class="sf-card q-mb-md">
        <q-card-section class="row q-col-gutter-md">
          <div class="col-12 col-md-8">
            <div class="sf-chip q-mb-sm">訂單詳情</div>
            <h1 class="sf-page-title q-mb-sm">{{ order.orderNumber || `訂單 #${order.id}` }}</h1>
            <div class="text-body2 text-grey-7">建立時間：{{ formatDate(order.createdAt) }}</div>
            <div class="text-body2 text-grey-7 q-mt-xs">更新時間：{{ formatDate(order.updatedAt) }}</div>
            <div class="text-body2 text-grey-7 q-mt-xs">收件人：{{ order.customerName || '-' }}</div>
            <div class="text-body2 text-grey-7 q-mt-xs">聯絡電話：{{ order.customerPhone || '-' }}</div>
            <div class="text-body2 text-grey-7 q-mt-xs">收件地址：{{ order.shippingAddress || '-' }}</div>
          </div>
          <div class="col-12 col-md-4">
            <div class="summary-card">
              <div class="summary-row">
                <span>訂單金額</span>
                <strong>NT$ {{ formatMoney(order.totalAmount) }}</strong>
              </div>
              <div class="summary-row text-grey-7">
                <span>訂單狀態</span>
                <span>{{ getOrderStatusLabel(order.status) }}</span>
              </div>
              <div class="summary-row text-grey-7">
                <span>訂單編號</span>
                <span>{{ order.orderNumber || order.id }}</span>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-md">商品明細</div>
          <div v-if="detailItems.length === 0" class="text-grey-7">
            目前沒有商品明細資料。
          </div>
          <q-markup-table v-else flat bordered class="detail-table">
            <thead>
              <tr>
                <th class="text-left">商品</th>
                <th class="text-left">規格</th>
                <th class="text-right">單價</th>
                <th class="text-right">數量</th>
                <th class="text-right">小計</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in detailItems" :key="item.id || `${item.productId}-${item.specificationId || 'na'}`">
                <td>
                  <div class="text-weight-medium">{{ item.productName || `商品 #${item.productId}` }}</div>
                  <div class="text-caption text-grey-7">SKU: {{ item.productSku || '-' }}</div>
                </td>
                <td>{{ item.productSpec || '-' }}</td>
                <td class="text-right">NT$ {{ formatMoney(item.unitPrice ?? item.price ?? 0) }}</td>
                <td class="text-right">{{ item.quantity }}</td>
                <td class="text-right">NT$ {{ formatMoney(item.subtotalAmount ?? item.subtotal ?? item.actualAmount ?? ((item.unitPrice ?? item.price ?? 0) * (item.quantity || 0))) }}</td>
              </tr>
            </tbody>
          </q-markup-table>
          </q-card-section>
        </q-card>
      <q-card bordered class="sf-card q-mt-md">
        <q-card-section>
          <div class="row items-center justify-between q-mb-sm">
            <div class="text-h6">訂單問答</div>
            <div class="text-caption text-grey-7"
              >系統會同步至客服管理中心，可送出問題或查看回覆。</div
            >
          </div>

          <div v-if="qaLoading" class="q-py-md">
            <q-skeleton type="text" width="40%" />
            <q-skeleton type="rect" height="140px" class="q-mt-md" />
          </div>

          <div v-else-if="orderQAs.length === 0" class="text-center text-grey-7 q-py-lg">
            <q-icon name="forum" size="40px" color="grey-5" />
            <div class="text-subtitle2 q-mt-sm">目前沒有問答紀錄</div>
            <p class="sf-section-subtitle">
              透過下方表單提出問題，客服團隊會以文字回覆並同步更新管理後台。
            </p>
          </div>

          <div v-else class="qa-list q-mb-sm">
            <div
              v-for="qa in orderQAs"
              :key="qa.id"
              class="qa-row q-mb-sm"
            >
              <div class="qa-header">
                <span class="text-body2 text-weight-medium">{{ qa.question }}</span>
                <q-badge
                  :color="qa.answer ? 'positive' : 'warning'"
                  :label="qa.answer ? '已回覆' : '待回覆'"
                  align="top"
                />
              </div>
              <div class="qa-meta text-caption text-grey-6 q-mt-xs">
                提問者：{{ qa.askerName || '會員' }}
                <span class="q-ml-sm">時間：{{ formatDate(qa.createdAt) }}</span>
              </div>
              <div v-if="qa.answer" class="qa-answer text-body2 text-grey-8 q-mt-xs">
                {{ qa.answer }}
              </div>
            </div>
          </div>
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pt-none">
          <div class="text-subtitle2 q-mb-sm">我要發問</div>
          <q-input
            v-model="questionForm.question"
            label="請描述您的問題或需求"
            type="textarea"
            rows="3"
            filled
          />
          <div class="row justify-end q-mt-sm">
            <q-btn
              color="primary"
              label="送出問題"
              :loading="askLoading"
              @click="handleAskQuestion"
            />
          </div>
        </q-card-section>
      </q-card>
    </template>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { orderApi, type Order } from '@/api/order'
import orderQAApi, { type OrderQA } from '@/api/orderQA'
import { trackEvent } from '@/utils/tracking'
import { getOrderStatusColor, getOrderStatusLabel, getOrderStatusTextColor } from '@/utils/orderStatus'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const id = computed(() => Number(route.params.id))
const order = ref<Order | null>(null)
const loading = ref(false)
const loadError = ref(false)
const orderQAs = ref<OrderQA[]>([])
const qaLoading = ref(false)
const questionForm = ref({ question: '' })
const askLoading = ref(false)

const detailItems = computed(() => {
  const value = order.value as (Order & { items?: Order['orderItems'] }) | null
  return value?.items ?? value?.orderItems ?? []
})

const formatDate = (date?: string) => {
  if (!date) return '-'
  const parsed = new Date(date)
  if (Number.isNaN(parsed.getTime())) return date
  return parsed.toLocaleString('zh-TW')
}

const formatMoney = (value?: number) => Number(value || 0).toLocaleString('zh-TW')

onMounted(async () => {
  trackEvent('view_order_detail', { order_id: id.value })

  if (!Number.isFinite(id.value) || id.value <= 0) {
    loadError.value = true
    return
  }

  loading.value = true
  loadError.value = false
  try {
    const response = await orderApi.getOrder(id.value)
    order.value = response.data
    await loadOrderQAs()
  } catch {
    loadError.value = true
    $q.notify({ type: 'negative', message: '載入訂單詳情失敗，請稍後再試。' })
  } finally {
    loading.value = false
  }
})

const loadOrderQAs = async () => {
  if (!order.value?.id) return
  qaLoading.value = true
  try {
    const response = await orderQAApi.getQAByOrderId(order.value.id)
    orderQAs.value = response.data ?? []
  } catch (error) {
    console.error('載入訂單問答失敗', error)
  } finally {
    qaLoading.value = false
  }
}

const handleAskQuestion = async () => {
  if (!order.value?.id) {
    $q.notify({ type: 'warning', message: '訂單資料尚未載入，無法提出問題。', position: 'top' })
    return
  }
  const question = questionForm.value.question?.trim()
  if (!question) {
    $q.notify({ type: 'warning', message: '請輸入問題內容', position: 'top' })
    return
  }

  askLoading.value = true
  try {
    await orderQAApi.askQuestion({
      orderId: order.value.id,
      askerType: 'CUSTOMER',
      askerId: authStore.user?.id,
      askerName: authStore.user?.name || order.value.customerName || '會員',
      question
    })
    questionForm.value.question = ''
    $q.notify({ type: 'positive', message: '問題已送出，客服將盡快回覆。', position: 'top' })
    await loadOrderQAs()
  } catch (error) {
    console.error('問答提交失敗', error)
    $q.notify({ type: 'negative', message: '送出問題失敗，請稍後再試。', position: 'top' })
  } finally {
    askLoading.value = false
  }
}
</script>

<style scoped>
.summary-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  background: #fafafa;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 6px 0;
}

.detail-table,
.detail-table :deep(table) {
  border-radius: 12px;
}

.qa-list {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  background: #fafafa;
}
.qa-row {
  padding: 12px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: #fff;
}
.qa-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}
.qa-answer {
  background: rgba(34, 197, 94, 0.08);
  border-radius: 8px;
  padding: 8px;
  margin-top: 6px;
}
</style>
