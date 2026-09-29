<template>
  <q-page padding>
    <div class="page-container">
      <!-- Header -->
      <div class="row items-center justify-between q-mb-md">
        <div class="text-h4">營業統計</div>
        <div class="row q-gutter-md">
          <q-input v-model="startDate" label="開始日期" type="date" outlined dense style="width: 150px" />
          <q-input v-model="endDate" label="結束日期" type="date" outlined dense style="width: 150px" />
          <q-btn color="primary" label="查詢" @click="loadStatistics" />
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">銷售額</div>
              <div class="text-h4 text-positive">NT$ {{ money(statistics?.totalSales) }}</div>
              <div v-if="Number(statistics?.refundAmount || 0) > 0" class="text-caption text-negative">
                期間退款 NT$ {{ money(statistics?.refundAmount) }}
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">成交訂單數</div>
              <div class="text-h4 text-info">{{ statistics?.totalOrders ?? 0 }}</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">下單顧客數</div>
              <div class="text-h4 text-warning">{{ statistics?.totalCustomers ?? 0 }}</div>
              <div class="text-caption text-grey-7">新會員 {{ statistics?.newCustomers ?? 0 }} 位</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">平均客單價</div>
              <div class="text-h4 text-negative">NT$ {{ money(statistics?.averageOrderValue) }}</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Sales Trend / Order Status -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-lg-8">
          <q-card>
            <q-card-section>
              <div class="text-subtitle1 q-mb-md">每日銷售趨勢</div>
              <div class="trend-chart">
                <canvas ref="trendCanvas" aria-label="每日銷售額與訂單數"></canvas>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-lg-4">
          <q-card>
            <q-card-section>
              <div class="text-subtitle1 q-mb-md">訂單狀態分布</div>
              <q-list separator>
                <q-item v-for="(count, status) in statistics?.orderStatus" :key="status">
                  <q-item-section>
                    <q-item-label>{{ status }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-item-label class="text-bold">{{ count }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Top Products & Categories -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-lg-6">
          <q-card>
            <q-card-section>
              <div class="text-subtitle1 q-mb-md">熱銷商品 Top 10</div>
              <q-table
                :rows="statistics?.topProducts || []"
                :columns="productColumns"
                row-key="id"
                flat
                :pagination="{ rowsPerPage: 0 }"
              >
                <template #body-cell-rank="props">
                  <q-td :props="props">
                    <q-badge color="primary" :label="props.rowIndex + 1" />
                  </q-td>
                </template>
              </q-table>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-lg-6">
          <q-card>
            <q-card-section>
              <div class="text-subtitle1 q-mb-md">熱門分類</div>
              <q-table
                :rows="statistics?.topCategories || []"
                :columns="categoryColumns"
                row-key="id"
                flat
                :pagination="{ rowsPerPage: 0 }"
              >
                <template #body-cell-rank="props">
                  <q-td :props="props">
                    <q-badge color="positive" :label="props.rowIndex + 1" />
                  </q-td>
                </template>
              </q-table>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Payment Methods -->
      <q-card>
        <q-card-section>
          <div class="text-subtitle1 q-mb-md">支付方式分布</div>
          <q-list separator>
            <q-item v-for="(count, method) in statistics?.paymentMethods" :key="method">
              <q-item-section>
                <q-item-label>{{ method }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-item-label class="text-bold text-primary">{{ count }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import type { QTableColumn } from 'quasar'
import Chart from 'chart.js/auto'
import statisticsApi, { type StatisticsData } from '@/api/statistics'


const statistics = ref<StatisticsData | null>(null)
const startDate = ref('')
const endDate = ref('')
const loading = ref(false)
const trendCanvas = ref<HTMLCanvasElement | null>(null)
let trendChart: Chart | null = null

const money = (value?: number | null) => Number(value || 0).toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const renderTrend = async () => {
  await nextTick()
  if (!trendCanvas.value || !statistics.value) return
  trendChart?.destroy()
  const trend = statistics.value.salesTrend || []
  trendChart = new Chart(trendCanvas.value, {
    type: 'bar',
    data: {
      labels: trend.map((day) => day.date.slice(5)),
      datasets: [
        { type: 'bar', label: '銷售額（NT$）', data: trend.map((day) => Number(day.sales)), backgroundColor: '#8f4f2d', yAxisID: 'y' },
        { type: 'line', label: '訂單數', data: trend.map((day) => day.orders), borderColor: '#1976d2', backgroundColor: '#1976d2', yAxisID: 'y1', tension: 0.3 }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      scales: {
        y: { beginAtZero: true, position: 'left' },
        y1: { beginAtZero: true, position: 'right', grid: { drawOnChartArea: false }, ticks: { precision: 0 } }
      },
      plugins: { legend: { position: 'bottom' } }
    }
  })
}

onBeforeUnmount(() => trendChart?.destroy())

const productColumns: QTableColumn[] = [
  { name: 'rank', label: '排名', align: 'center', field: 'rank' },
  { name: 'name', label: '商品名稱', align: 'left', field: 'name' },
  { name: 'sales', label: '銷售數量', align: 'center', field: 'sales', sortable: true },
  { name: 'revenue', label: '營收', align: 'right', field: 'revenue', sortable: true, format: (value: number) => `NT$ ${money(value)}` }
]

const categoryColumns: QTableColumn[] = [
  { name: 'rank', label: '排名', align: 'center', field: 'rank' },
  { name: 'name', label: '分類名稱', align: 'left', field: 'name' },
  { name: 'sales', label: '銷售數量', align: 'center', field: 'sales', sortable: true },
  { name: 'revenue', label: '營收', align: 'right', field: 'revenue', sortable: true, format: (value: number) => `NT$ ${money(value)}` }
]

const loadStatistics = async () => {
  loading.value = true
  try {
    const dateRange = startDate.value && endDate.value ? { startDate: startDate.value, endDate: endDate.value } : undefined
    const response = await statisticsApi.getOverallStatistics(dateRange)
    statistics.value = response.data
    renderTrend()
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    loading.value = false
  }
}

const initializeDateRange = () => {
  const end = new Date()
  const start = new Date(end)
  start.setDate(start.getDate() - 30)
  // 本地日期（避免 UTC 日期少一天）
  const localDate = (date: Date) =>
    `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  endDate.value = localDate(end)
  startDate.value = localDate(start)
}

onMounted(() => {
  initializeDateRange()
  loadStatistics()
})
</script>

<style scoped>
.trend-chart {
  position: relative;
  height: 300px;
}
</style>
