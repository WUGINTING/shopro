<template>
  <q-page class="q-pa-md">
    <div class="dashboard">
      <!-- Welcome Banner -->
      <q-card class="welcome-banner q-mb-md">
        <q-card-section class="row items-center">
          <div class="col">
            <div class="text-h4 text-weight-bold">欢迎回来！</div>
            <div class="text-subtitle1 q-mt-sm">今天是 {{ currentDate }}，让我们开始新的一天吧</div>
          </div>
          <div class="col-auto">
            <q-icon name="shopping_cart" size="80px" style="opacity: 0.3" />
          </div>
        </q-card-section>
      </q-card>

      <!-- Stats Row -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="stat-card stat-card-1">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="inventory_2" size="40px" color="primary" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.totalProducts) }}</div>
                  <div class="text-caption text-grey-7">总商品数</div>
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
          <q-card class="stat-card stat-card-2">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="shopping_bag" size="40px" color="orange" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.pendingOrders) }}</div>
                  <div class="text-caption text-grey-7">待处理订单</div>
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
          <q-card class="stat-card stat-card-3">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="people" size="40px" color="teal" />
                <div>
                  <div class="text-h4 text-weight-bold">{{ formatNumber(stats.totalCustomers) }}</div>
                  <div class="text-caption text-grey-7">总客户数</div>
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
          <q-card class="stat-card stat-card-4">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="attach_money" size="40px" color="green" />
                <div>
                  <div class="text-h4 text-weight-bold">¥{{ formatCurrency(stats.monthlySales) }}</div>
                  <div class="text-caption text-grey-7">本月销售额</div>
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
                <div class="text-h6 text-weight-bold">最近订单</div>
                <q-btn flat dense color="primary" label="查看全部" @click="$router.push('/orders')" />
              </div>
            </q-card-section>
            <q-card-section class="q-pt-none">
              <q-timeline color="secondary" v-if="recentOrders.length > 0">
                <q-timeline-entry
                  v-for="order in recentOrders"
                  :key="order.id"
                  :title="`订单 ${order.orderNumber} - ${getStatusText(order.status)}`"
                  :subtitle="formatDate(order.createdAt)"
                  :icon="getStatusIcon(order.status)"
                  :color="getStatusColor(order.status)"
                >
                  <div>客户: {{ order.customerName }}</div>
                  <div class="text-weight-bold">金额: ¥{{ order.totalAmount }}</div>
                </q-timeline-entry>
              </q-timeline>
              <div v-else class="text-center text-grey-7 q-py-md">暂无订单数据</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="row items-center justify-between">
                <div class="text-h6 text-weight-bold">热销商品</div>
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
              <div v-else class="text-center text-grey-7 q-py-md">暂无商品数据</div>
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
    label: '处理订单',
    color: 'orange',
    onClick: () => router.push('/orders')
  },
  {
    icon: 'person_add',
    label: '管理客户',
    color: 'teal',
    onClick: () => router.push('/customers')
  },
  {
    icon: 'bar_chart',
    label: '查看报表',
    color: 'green',
    onClick: () => {}
  },
  {
    icon: 'campaign',
    label: '创建活动',
    color: 'deep-purple',
    onClick: () => {}
  },
  {
    icon: 'settings',
    label: '系统设置',
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
    console.error('Failed to load dashboard data:', error)
    $q.notify({
      type: 'warning',
      message: '加载仪表板数据失败，显示默认数据',
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
    PENDING: '待处理',
    PROCESSING: '处理中',
    SHIPPED: '已发货',
    DELIVERED: '已送达',
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
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
  color: white;
}

.stat-card {
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-card-1 {
  border-top: 4px solid #1976D2;
}

.stat-card-2 {
  border-top: 4px solid #ff9800;
}

.stat-card-3 {
  border-top: 4px solid #26A69A;
}

.stat-card-4 {
  border-top: 4px solid #21BA45;
}
</style>
