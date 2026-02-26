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
    </template>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { orderApi, type Order } from '@/api/order'
import { trackEvent } from '@/utils/tracking'
import { getOrderStatusColor, getOrderStatusLabel, getOrderStatusTextColor } from '@/utils/orderStatus'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const id = computed(() => Number(route.params.id))
const order = ref<Order | null>(null)
const loading = ref(false)
const loadError = ref(false)

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
  } catch {
    loadError.value = true
    $q.notify({ type: 'negative', message: '載入訂單詳情失敗，請稍後再試。' })
  } finally {
    loading.value = false
  }
})
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
</style>
