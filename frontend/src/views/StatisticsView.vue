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
              <div class="text-subtitle2 text-grey-7">總營收</div>
              <div class="text-h4 text-positive">
                {{ statistics?.totalSales?.toFixed(2) ?? '0.00' }}
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">總訂單數</div>
              <div class="text-h4 text-info">{{ statistics?.totalOrders ?? 0 }}</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">總顧客數</div>
              <div class="text-h4 text-warning">{{ statistics?.totalCustomers ?? 0 }}</div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
          <q-card>
            <q-card-section>
              <div class="text-subtitle2 text-grey-7">平均訂單額</div>
              <div class="text-h4 text-negative">
                {{ statistics?.averageOrderValue?.toFixed(2) ?? '0.00' }}
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Sales Trend / Order Status -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-lg-8">
          <q-card>
            <q-card-section>
              <div class="text-subtitle1 q-mb-md">銷售趨勢</div>
              <div class="trend-placeholder">
                <div class="text-grey-7">待串接圖表套件</div>
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
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import statisticsApi, { type StatisticsData } from '@/api/statistics'

const $q = useQuasar()

const statistics = ref<StatisticsData | null>(null)
const startDate = ref('')
const endDate = ref('')
const loading = ref(false)

const productColumns = [
  { name: 'rank', label: '排名', align: 'center', field: 'rank' },
  { name: 'name', label: '商品名稱', align: 'left', field: 'name' },
  { name: 'sales', label: '銷售數量', align: 'center', field: 'sales', sortable: true },
  { name: 'revenue', label: '營收', align: 'right', field: 'revenue', sortable: true }
]

const categoryColumns = [
  { name: 'rank', label: '排名', align: 'center', field: 'rank' },
  { name: 'name', label: '分類名稱', align: 'left', field: 'name' },
  { name: 'sales', label: '銷售數量', align: 'center', field: 'sales', sortable: true }
]

const loadStatistics = async () => {
  loading.value = true
  try {
    const dateRange = startDate.value && endDate.value ? { startDate: startDate.value, endDate: endDate.value } : undefined
    const response = await statisticsApi.getOverallStatistics(dateRange)
    statistics.value = response.data
  } catch (error) {
    console.error('統計資料載入失敗', error)
    $q.notify({ type: 'negative', message: '統計資料載入失敗，請稍後再試', position: 'top' })
  } finally {
    loading.value = false
  }
}

const initializeDateRange = () => {
  const end = new Date()
  const start = new Date(end)
  start.setDate(start.getDate() - 30)
  endDate.value = end.toISOString().split('T')[0]
  startDate.value = start.toISOString().split('T')[0]
}

onMounted(() => {
  initializeDateRange()
  loadStatistics()
})
</script>

<style scoped>
.trend-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 4px;
}
</style>
