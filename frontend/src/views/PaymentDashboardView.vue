<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4 q-mb-md">金流儀表板</div>
      
      <!-- 統計卡片 -->
      <div class="row q-col-gutter-md q-mb-md">
        <!-- 今日成交金額 -->
        <div class="col-12 col-md-3">
          <q-card class="stat-card">
            <q-card-section>
              <div class="text-grey-7 text-subtitle2">今日成交金額</div>
              <div class="text-h5 text-primary q-mt-sm">
                $ {{ formatCurrency(statistics.todayAmount) }}
              </div>
              <div class="text-caption text-grey-6 q-mt-xs">
                {{ statistics.todayCount }} 筆交易
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- 今日成功率 -->
        <div class="col-12 col-md-3">
          <q-card class="stat-card">
            <q-card-section>
              <div class="text-grey-7 text-subtitle2">今日成功率</div>
              <div class="text-h5 text-positive q-mt-sm">
                {{ statistics.todaySuccessRate.toFixed(2) }}%
              </div>
              <div class="text-caption text-grey-6 q-mt-xs">
                支付成功率
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- 本月成交金額 -->
        <div class="col-12 col-md-3">
          <q-card class="stat-card">
            <q-card-section>
              <div class="text-grey-7 text-subtitle2">本月成交金額</div>
              <div class="text-h5 text-info q-mt-sm">
                $ {{ formatCurrency(statistics.monthAmount) }}
              </div>
              <div class="text-caption text-grey-6 q-mt-xs">
                {{ statistics.monthCount }} 筆交易
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- 退款統計 -->
        <div class="col-12 col-md-3">
          <q-card class="stat-card">
            <q-card-section>
              <div class="text-grey-7 text-subtitle2">今日退款</div>
              <div class="text-h5 text-negative q-mt-sm">
                {{ statistics.refundStatistics.todayRefundCount }}
              </div>
              <div class="text-caption text-grey-6 q-mt-xs">
                本月 {{ statistics.refundStatistics.monthRefundCount }} 筆
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 支付閘道佔比圓餅圖 -->
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="text-h6">支付管道佔比</div>
              <div class="q-mt-md" style="height: 300px">
                <canvas ref="gatewayChart"></canvas>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="text-h6">支付管道統計</div>
              <q-list class="q-mt-md">
                <q-item v-for="(share, key) in statistics.gatewayShares" :key="key">
                  <q-item-section avatar>
                    <q-avatar :color="getGatewayColor(key)" text-color="white">
                      {{ getGatewayIcon(key) }}
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ share.gateway }}</q-item-label>
                    <q-item-label caption>
                      {{ share.count }} 筆 | $ {{ formatCurrency(share.amount) }}
                    </q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-badge :color="getGatewayColor(key)">
                      {{ share.percentage.toFixed(1) }}%
                    </q-badge>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { getPaymentStatistics, type PaymentStatistics } from '@/api/payment'
import { Notify } from 'quasar'
import Chart from 'chart.js/auto'

const statistics = ref<PaymentStatistics>({
  todayAmount: 0,
  todayCount: 0,
  todaySuccessRate: 0,
  monthAmount: 0,
  monthCount: 0,
  refundStatistics: {
    todayRefundCount: 0,
    todayRefundAmount: 0,
    monthRefundCount: 0,
    monthRefundAmount: 0
  },
  gatewayShares: {}
})

const gatewayChart = ref<HTMLCanvasElement>()
let chartInstance: Chart | null = null

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}

const getGatewayColor = (gateway: string): string => {
  const colors: Record<string, string> = {
    LINE_PAY: 'green',
    ECPAY: 'orange',
    MANUAL: 'grey'
  }
  return colors[gateway] || 'blue'
}

const getGatewayIcon = (gateway: string): string => {
  const icons: Record<string, string> = {
    LINE_PAY: 'L',
    ECPAY: 'E',
    MANUAL: 'M'
  }
  return icons[gateway] || '?'
}

const loadStatistics = async () => {
  try {
    const response = await getPaymentStatistics()
    if (response.success && response.data) {
      statistics.value = response.data
      await nextTick()
      renderChart()
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
    Notify.create({
      type: 'negative',
      message: '載入統計資料失敗'
    })
  }
}

const renderChart = () => {
  if (!gatewayChart.value) return

  const shares = Object.values(statistics.value.gatewayShares)
  const labels = shares.map(s => s.gateway)
  const data = shares.map(s => s.amount)
  const colors = Object.keys(statistics.value.gatewayShares).map(key => {
    const colorMap: Record<string, string> = {
      LINE_PAY: '#00B900',
      ECPAY: '#FF6C00',
      MANUAL: '#757575'
    }
    return colorMap[key] || '#2196F3'
  })

  if (chartInstance) {
    chartInstance.destroy()
  }

  chartInstance = new Chart(gatewayChart.value, {
    type: 'pie',
    data: {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: colors
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  })
}

onMounted(() => {
  loadStatistics()
  // 每 30 秒自動更新
  setInterval(loadStatistics, 30000)
})
</script>

<style scoped>
.stat-card {
  border-left: 4px solid #2196F3;
}

.stat-card:nth-child(1) {
  border-left-color: #2196F3;
}

.stat-card:nth-child(2) {
  border-left-color: #4CAF50;
}

.stat-card:nth-child(3) {
  border-left-color: #00BCD4;
}

.stat-card:nth-child(4) {
  border-left-color: #F44336;
}
</style>
