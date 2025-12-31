<template>
  <q-page class="q-pa-md" style="max-width: none; width: 100%;">
    <div class="dashboard">
      <!-- Welcome Banner -->
      <q-card class="bg-gradient-primary q-mb-md">
        <q-card-section class="row items-center">
          <div class="col">
            <div class="text-h4 text-weight-bold">歡迎回來！</div>
            <div class="text-subtitle1 q-mt-sm">今天是 {{ currentDate }}，讓我們開始新的一天吧</div>
          </div>
          <div class="col-auto">
            <q-icon name="shopping_cart" size="80px" style="opacity: 0.3" />
          </div>
        </q-card-section>
      </q-card>

      <!-- Stats Row -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="card-hover card-border-top border-primary">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="inventory_2" size="40px" color="primary" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.totalProducts) }}</div>
                  <div class="text-caption text-grey-7">總商品數</div>
                </div>
              </div>
              <q-badge 
                v-if="stats.totalProductsChange !== undefined"
                :color="stats.totalProductsChange >= 0 ? 'positive' : 'negative'" 
                class="q-mt-sm"
              >
                {{ stats.totalProductsChange >= 0 ? '+' : '' }}{{ stats.totalProductsChange }}% 
                {{ stats.totalProductsChange >= 0 ? '↑' : '↓' }}
              </q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="card-hover card-border-top border-orange">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="shopping_bag" size="40px" color="orange" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.pendingOrders) }}</div>
                  <div class="text-caption text-grey-7">待處理訂單</div>
                </div>
              </div>
              <q-badge 
                v-if="stats.pendingOrdersChange !== undefined"
                :color="stats.pendingOrdersChange >= 0 ? 'warning' : 'positive'" 
                class="q-mt-sm"
              >
                {{ stats.pendingOrdersChange >= 0 ? '+' : '' }}{{ stats.pendingOrdersChange }}% 
                {{ stats.pendingOrdersChange >= 0 ? '↑' : '↓' }}
              </q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="card-hover card-border-top border-teal">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="people" size="40px" color="teal" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.totalCustomers) }}</div>
                  <div class="text-caption text-grey-7">總客戶數</div>
                </div>
              </div>
              <q-badge 
                v-if="stats.totalCustomersChange !== undefined"
                :color="stats.totalCustomersChange >= 0 ? 'positive' : 'negative'" 
                class="q-mt-sm"
              >
                {{ stats.totalCustomersChange >= 0 ? '+' : '' }}{{ stats.totalCustomersChange }}% 
                {{ stats.totalCustomersChange >= 0 ? '↑' : '↓' }}
              </q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="card-hover card-border-top border-green">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="attach_money" size="40px" color="green" />
                <div>
                  <div class="text-h4 text-weight-bold">¥{{ formatCurrency(stats.monthlySales) }}</div>
                  <div class="text-caption text-grey-7">本月銷售額</div>
                </div>
              </div>
              <q-badge 
                v-if="stats.monthlySalesChange !== undefined"
                :color="stats.monthlySalesChange >= 0 ? 'positive' : 'negative'" 
                class="q-mt-sm"
              >
                {{ stats.monthlySalesChange >= 0 ? '+' : '' }}{{ stats.monthlySalesChange }}% 
                {{ stats.monthlySalesChange >= 0 ? '↑' : '↓' }}
              </q-badge>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Quick Actions -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="text-h6 text-weight-bold">快速操作</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <div class="row q-col-gutter-md">
            <div class="col-6 col-sm-4 col-md-2" v-for="action in quickActions" :key="action.label">
              <q-btn
                unelevated
                no-caps
                stack
                :color="action.color"
                :icon="action.icon"
                :label="action.label"
                class="full-width q-py-md"
                @click="action.onClick"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Recent Activity -->
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="row items-center justify-between">
                <div class="text-h6 text-weight-bold">最近訂單</div>
                <q-btn flat dense color="primary" label="查看全部" @click="$router.push('/orders')" />
              </div>
            </q-card-section>
            <q-card-section class="q-pt-none">
              <q-timeline color="secondary" v-if="recentOrders.length > 0">
                <q-timeline-entry
                  v-for="order in recentOrders"
                  :key="order.id"
                  :title="`訂單 ${order.orderNumber} - ${getStatusText(order.status)}`"
                  :subtitle="formatDate(order.createdAt)"
                  :icon="getStatusIcon(order.status)"
                  :color="getStatusColor(order.status)"
                >
                  <div>客戶: {{ order.customerName }}</div>
                  <div class="text-weight-bold">金額: ¥{{ order.totalAmount }}</div>
                </q-timeline-entry>
              </q-timeline>
              <div v-else class="text-center text-grey-7 q-py-md">暫無訂單資料</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="row items-center justify-between">
                <div class="text-h6 text-weight-bold">熱銷商品</div>
                <q-btn flat dense color="primary" label="查看全部" @click="$router.push('/products')" />
              </div>
            </q-card-section>
            <q-card-section class="q-pt-none">
              <q-list separator v-if="topProducts.length > 0">
                <q-item v-for="product in topProducts" :key="product.id">
                  <q-item-section avatar>
                    <q-avatar rounded size="50px" color="grey-3" text-color="primary">
                      <q-icon name="inventory_2" />
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ product.name }}</q-item-label>
                    <q-item-label caption>已售 {{ product.salesCount }} 件</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-item-label class="text-primary text-weight-bold">
                      ¥{{ product.price }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
              <div v-else class="text-center text-grey-7 q-py-md">暫無商品資料</div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { dashboardApi, type DashboardStats, type RecentOrder, type TopProduct } from '@/api'

const router = useRouter()
const $q = useQuasar()

const stats = ref<DashboardStats>({
  totalProducts: 0,
  pendingOrders: 0,
  totalCustomers: 0,
  monthlySales: 0
})

const recentOrders = ref<RecentOrder[]>([])
const topProducts = ref<TopProduct[]>([])
const loading = ref(false)

const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
})

const quickActions = [
  {
    icon: 'add_circle',
    label: '新增商品',
    color: 'primary',
    onClick: () => router.push('/products')
  },
  {
    icon: 'receipt',
    label: '處理訂單',
    color: 'orange',
    onClick: () => router.push('/orders')
  },
  {
    icon: 'person_add',
    label: '管理客戶',
    color: 'teal',
    onClick: () => router.push('/customers')
  },
  {
    icon: 'photo_library',
    label: '相冊管理',
    color: 'purple',
    onClick: () => router.push('/albums')
  },
  {
    icon: 'bar_chart',
    label: '查看報表',
    color: 'green',
    onClick: () => {}
  },
  {
    icon: 'settings',
    label: '系統設定',
    color: 'blue-grey',
    onClick: () => {}
  }
]

const loadDashboardData = async () => {
  loading.value = true
  try {
    // Load stats
    const statsResponse = await dashboardApi.getStats()
    stats.value = statsResponse.data

    // Load recent orders
    const ordersResponse = await dashboardApi.getRecentOrders(5)
    recentOrders.value = ordersResponse.data

    // Load top products
    const productsResponse = await dashboardApi.getTopProducts(5)
    topProducts.value = productsResponse.data
  } catch (error) {
    // Silently log error (visible in browser console for debugging)
    if (error instanceof Error) {
      console.warn('Dashboard data load error:', error.message)
    }
    
    $q.notify({
      type: 'warning',
      message: '載入儀表板資料失敗，顯示預設資料',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const formatNumber = (num: number | undefined) => {
  if (num === undefined || num === null) return '0'
  return num.toLocaleString('zh-CN')
}

const formatCurrency = (amount: number | undefined) => {
  if (amount === undefined || amount === null) return '0.00'
  return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusText = (status: string) => {
  const statusMap: { [key: string]: string } = {
    PENDING: '待處理',
    PROCESSING: '處理中',
    SHIPPED: '已發貨',
    DELIVERED: '已送達',
    CANCELLED: '已取消'
  }
  return statusMap[status] || status
}

const getStatusIcon = (status: string) => {
  const iconMap: { [key: string]: string } = {
    PENDING: 'schedule',
    PROCESSING: 'local_shipping',
    SHIPPED: 'flight_takeoff',
    DELIVERED: 'done_all',
    CANCELLED: 'cancel'
  }
  return iconMap[status] || 'help'
}

const getStatusColor = (status: string) => {
  const colorMap: { [key: string]: string } = {
    PENDING: 'orange',
    PROCESSING: 'primary',
    SHIPPED: 'teal',
    DELIVERED: 'positive',
    CANCELLED: 'negative'
  }
  return colorMap[status] || 'grey'
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  width: 100%;
  padding: 0;
}
</style>
