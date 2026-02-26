<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="q-mb-md">
      <div class="sf-chip q-mb-sm">我的訂單</div>
      <h1 class="sf-page-title">訂單紀錄</h1>
      <p class="sf-page-lead">集中查看訂單編號、狀態、金額與建立時間。若有問題可提供訂單編號聯繫客服。</p>
    </section>

    <q-card bordered class="sf-card">
      <q-card-section>
        <div v-if="loading" class="q-pa-md">
          <q-skeleton type="text" width="35%" />
          <q-skeleton type="rect" height="140px" class="q-mt-md" />
        </div>

        <div v-else-if="orders.length === 0" class="text-center q-py-xl">
          <q-icon name="receipt_long" size="42px" color="grey-6" />
          <div class="text-subtitle1 text-weight-medium q-mt-sm">尚無訂單紀錄</div>
          <p class="sf-section-subtitle q-mt-sm">完成首次購買後，訂單狀態會顯示在這裡。</p>
        </div>

        <q-markup-table v-else flat bordered class="orders-table">
          <thead>
            <tr>
              <th class="text-left">訂單編號</th>
              <th class="text-left">狀態</th>
              <th class="text-right">金額</th>
              <th class="text-left">建立時間</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="order in orders"
              :key="order.id"
              class="cursor-pointer"
              @click="order.id && router.push(`/account/orders/${order.id}`)"
            >
              <td class="text-weight-medium">
                <q-btn
                  flat
                  dense
                  no-caps
                  color="primary"
                  class="q-pa-none"
                  :label="String(order.orderNumber || order.id || '-')"
                  @click.stop="order.id && router.push(`/account/orders/${order.id}`)"
                />
              </td>
              <td>
                <q-chip
                  dense
                  square
                  :color="getOrderStatusColor(order.status)"
                  :text-color="getOrderStatusTextColor(order.status)"
                >
                  {{ getOrderStatusLabel(order.status) }}
                </q-chip>
              </td>
              <td class="text-right">NT$ {{ Number(order.totalAmount || 0).toLocaleString('zh-TW') }}</td>
              <td>{{ formatDate(order.createdAt) }}</td>
            </tr>
          </tbody>
        </q-markup-table>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { orderApi, type Order } from '@/api/order'
import { trackEvent } from '@/utils/tracking'
import { getOrderStatusColor, getOrderStatusLabel, getOrderStatusTextColor } from '@/utils/orderStatus'

const $q = useQuasar()
const router = useRouter()
const authStore = useAuthStore()
const orders = ref<Order[]>([])
const loading = ref(false)

const normalizeOrders = (payload: unknown): Order[] => {
  if (Array.isArray(payload)) return payload as Order[]
  if (payload && typeof payload === 'object' && 'content' in payload) {
    return (payload as { content?: Order[] }).content ?? []
  }
  return []
}

onMounted(async () => {
  loading.value = true
  try {
    const customerId = authStore.user?.id
    const response = typeof customerId === 'number'
      ? await orderApi.getOrdersByCustomerId(customerId, { page: 0, size: 20 })
      : await orderApi.getMyOrders({ page: 0, size: 20 })
    orders.value = normalizeOrders(response.data)
  } catch {
    $q.notify({ type: 'negative', message: '載入訂單紀錄失敗，請稍後再試。' })
  } finally {
    loading.value = false
  }

  trackEvent('view_my_orders')
})

const formatDate = (date?: string) => {
  if (!date) return '-'
  const parsed = new Date(date)
  if (Number.isNaN(parsed.getTime())) return date
  return parsed.toLocaleString('zh-TW')
}
</script>

<style scoped>
.orders-table {
  border-radius: 14px;
}
</style>
