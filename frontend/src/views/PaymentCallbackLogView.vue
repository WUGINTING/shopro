<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">支付回調記錄</div>
          <div class="text-caption text-grey-7">查看所有支付閘道的回調請求記錄，用於除錯和追蹤</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            round
            icon="help_outline"
            color="grey-7"
            @click="handleStartTour"
          >
            <q-tooltip>支付回調記錄教學</q-tooltip>
          </q-btn>
          <q-btn
            flat
            dense
            icon="refresh"
            label="重新載入"
            @click="loadCallbackLogs"
          />
        </div>
      </div>

      <!-- Filter Panel -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <!-- 支付閘道類型 -->
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterGateway"
                label="支付閘道"
                outlined
                dense
                clearable
                :options="gatewayOptions"
                emit-value
                map-options
                @update:model-value="applyFilters"
              />
            </div>

            <!-- 訂單編號 -->
            <div class="col-12 col-md-3">
              <q-input
                v-model="filterOrderNumber"
                label="訂單編號"
                outlined
                dense
                clearable
                placeholder="輸入訂單編號"
                @update:model-value="applyFilters"
              />
            </div>

            <!-- 交易 ID -->
            <div class="col-12 col-md-3">
              <q-input
                v-model="filterTransactionId"
                label="交易 ID"
                outlined
                dense
                clearable
                placeholder="輸入交易 ID"
                @update:model-value="applyFilters"
              />
            </div>

            <!-- 狀態 -->
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterStatus"
                label="狀態"
                outlined
                dense
                clearable
                :options="statusOptions"
                emit-value
                map-options
                @update:model-value="applyFilters"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Callback Logs Table -->
      <q-card>
        <q-table
          :rows="callbackLogs"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
          @request="onRequest"
        >
          <template v-slot:body-cell-gateway="props">
            <q-td :props="props">
              <q-badge :color="getGatewayColor(props.row.gateway)" :label="props.row.gateway" />
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
            </q-td>
          </template>

          <template v-slot:body-cell-orderNumber="props">
            <q-td :props="props">
              <span class="text-weight-bold">{{ props.row.orderNumber || '-' }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-transactionId="props">
            <q-td :props="props">
              {{ props.row.transactionId || '-' }}
            </q-td>
          </template>

          <template v-slot:body-cell-processTimeMs="props">
            <q-td :props="props">
              {{ props.row.processTimeMs ? `${props.row.processTimeMs}ms` : '-' }}
            </q-td>
          </template>

          <template v-slot:body-cell-createdAt="props">
            <q-td :props="props">
              {{ props.row.createdAt ? new Date(props.row.createdAt).toLocaleString('zh-TW') : '-' }}
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn
                flat
                dense
                round
                icon="visibility"
                color="primary"
                size="sm"
                @click="handleViewDetail(props.row)"
              >
                <q-tooltip>查看詳情</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Detail Dialog -->
      <q-dialog v-model="showDetailDialog" maximized>
        <q-card>
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">回調記錄詳情</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="q-pt-md">
            <div class="row q-col-gutter-md">
              <!-- 左側：基本信息 -->
              <div class="col-12 col-md-6">
                <q-card flat bordered>
                  <q-card-section>
                    <div class="text-h6 q-mb-md">基本信息</div>
                    <q-list bordered separator>
                      <q-item>
                        <q-item-section>
                          <q-item-label>記錄 ID</q-item-label>
                          <q-item-label caption>{{ selectedLog?.id }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>支付閘道</q-item-label>
                          <q-item-label caption>
                            <q-badge :color="getGatewayColor(selectedLog?.gateway || '')" :label="selectedLog?.gateway" />
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>訂單編號</q-item-label>
                          <q-item-label caption>{{ selectedLog?.orderNumber || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>交易 ID</q-item-label>
                          <q-item-label caption>{{ selectedLog?.transactionId || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>狀態</q-item-label>
                          <q-item-label caption>
                            <q-badge :color="getStatusColor(selectedLog?.status || '')" :label="getStatusLabel(selectedLog?.status || '')" />
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>處理時間</q-item-label>
                          <q-item-label caption>{{ selectedLog?.processTimeMs ? `${selectedLog.processTimeMs}ms` : '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>請求 IP</q-item-label>
                          <q-item-label caption>{{ selectedLog?.requestIp || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label>建立時間</q-item-label>
                          <q-item-label caption>
                            {{ selectedLog?.createdAt ? new Date(selectedLog.createdAt).toLocaleString('zh-TW') : '-' }}
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-card-section>
                </q-card>

                <!-- 處理結果 -->
                <q-card flat bordered class="q-mt-md">
                  <q-card-section>
                    <div class="text-h6 q-mb-md">處理結果</div>
                    <div class="text-body2">{{ selectedLog?.processResult || '-' }}</div>
                    <div v-if="selectedLog?.errorMessage" class="q-mt-md">
                      <div class="text-subtitle2 text-negative q-mb-sm">錯誤訊息</div>
                      <div class="text-body2 text-negative">{{ selectedLog.errorMessage }}</div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>

              <!-- 右側：請求數據 -->
              <div class="col-12 col-md-6">
                <!-- 原始請求參數 -->
                <q-card flat bordered>
                  <q-card-section>
                    <div class="row items-center justify-between q-mb-md">
                      <div class="text-h6">原始請求參數</div>
                      <q-btn
                        flat
                        dense
                        icon="content_copy"
                        size="sm"
                        @click="copyToClipboard(selectedLog?.rawParams || '')"
                      >
                        <q-tooltip>複製</q-tooltip>
                      </q-btn>
                    </div>
                    <q-input
                      :model-value="formatJson(selectedLog?.rawParams)"
                      type="textarea"
                      readonly
                      outlined
                      rows="10"
                      class="font-mono"
                    />
                  </q-card-section>
                </q-card>

                <!-- 解析後的響應 -->
                <q-card flat bordered class="q-mt-md">
                  <q-card-section>
                    <div class="row items-center justify-between q-mb-md">
                      <div class="text-h6">解析後的響應</div>
                      <q-btn
                        flat
                        dense
                        icon="content_copy"
                        size="sm"
                        @click="copyToClipboard(selectedLog?.parsedResponse || '')"
                      >
                        <q-tooltip>複製</q-tooltip>
                      </q-btn>
                    </div>
                    <q-input
                      :model-value="formatJson(selectedLog?.parsedResponse)"
                      type="textarea"
                      readonly
                      outlined
                      rows="10"
                      class="font-mono"
                    />
                  </q-card-section>
                </q-card>

                <!-- User-Agent -->
                <q-card flat bordered class="q-mt-md" v-if="selectedLog?.userAgent">
                  <q-card-section>
                    <div class="text-h6 q-mb-md">User-Agent</div>
                    <div class="text-body2 text-grey-7">{{ selectedLog.userAgent }}</div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="關閉" color="grey-7" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { paymentCallbackLogApi, type PaymentCallbackLog } from '@/api/paymentCallbackLog'
import type { PageResponse } from '@/api/types'
import { startPaymentCallbackLogTour, isPaymentCallbackLogTourCompleted } from '@/utils/paymentCallbackLogTour'

const $q = useQuasar()

const callbackLogs = ref<PaymentCallbackLog[]>([])
const loading = ref(false)
const showDetailDialog = ref(false)
const selectedLog = ref<PaymentCallbackLog | null>(null)

// 篩選條件
const filterGateway = ref<string | null>(null)
const filterOrderNumber = ref('')
const filterTransactionId = ref('')
const filterStatus = ref<string | null>(null)

const gatewayOptions = [
  { label: 'ECPay', value: 'ECPAY' },
  { label: 'LINE PAY', value: 'LINE_PAY' }
]

const statusOptions = [
  { label: '成功', value: 'SUCCESS' },
  { label: '失敗', value: 'FAILED' },
  { label: '錯誤', value: 'ERROR' }
]

const pagination = ref({
  page: 1,
  rowsPerPage: 20,
  rowsNumber: 0
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'gateway', label: '支付閘道', align: 'center' as const, field: 'gateway' },
  { name: 'orderNumber', label: '訂單編號', align: 'left' as const, field: 'orderNumber' },
  { name: 'transactionId', label: '交易 ID', align: 'left' as const, field: 'transactionId' },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'processTimeMs', label: '處理時間', align: 'center' as const, field: 'processTimeMs' },
  { name: 'requestIp', label: '請求 IP', align: 'left' as const, field: 'requestIp' },
  { name: 'createdAt', label: '建立時間', align: 'left' as const, field: 'createdAt', sortable: true },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const getGatewayColor = (gateway: string) => {
  const colorMap: Record<string, string> = {
    ECPAY: 'primary',
    LINE_PAY: 'positive'
  }
  return colorMap[gateway] || 'grey'
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    SUCCESS: 'positive',
    FAILED: 'warning',
    ERROR: 'negative'
  }
  return colorMap[status] || 'grey'
}

const getStatusLabel = (status: string) => {
  const labelMap: Record<string, string> = {
    SUCCESS: '成功',
    FAILED: '失敗',
    ERROR: '錯誤'
  }
  return labelMap[status] || status
}

const formatJson = (jsonString?: string) => {
  if (!jsonString) return ''
  try {
    const obj = JSON.parse(jsonString)
    return JSON.stringify(obj, null, 2)
  } catch (e) {
    return jsonString
  }
}

const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    $q.notify({
      type: 'positive',
      message: '已複製到剪貼板',
      position: 'top'
    })
  }).catch(() => {
    $q.notify({
      type: 'negative',
      message: '複製失敗',
      position: 'top'
    })
  })
}

const loadCallbackLogs = async () => {
  loading.value = true
  try {
    let response

    if (filterOrderNumber.value) {
      // 根據訂單編號查詢
      response = await paymentCallbackLogApi.getCallbackLogsByOrderNumber(filterOrderNumber.value)
      if (response.success && response.data) {
        callbackLogs.value = Array.isArray(response.data) ? response.data : []
        pagination.value.rowsNumber = callbackLogs.value.length
      }
    } else if (filterTransactionId.value) {
      // 根據交易 ID 查詢
      response = await paymentCallbackLogApi.getCallbackLogsByTransactionId(filterTransactionId.value)
      if (response.success && response.data) {
        callbackLogs.value = Array.isArray(response.data) ? response.data : []
        pagination.value.rowsNumber = callbackLogs.value.length
      }
    } else if (filterGateway.value) {
      // 根據支付閘道類型查詢
      response = await paymentCallbackLogApi.getCallbackLogsByGateway(filterGateway.value, {
        page: pagination.value.page - 1,
        size: pagination.value.rowsPerPage
      })
      if (response.success && response.data) {
        const data = response.data as PageResponse<PaymentCallbackLog> | PaymentCallbackLog[]
        if (Array.isArray(data)) {
          callbackLogs.value = data
          pagination.value.rowsNumber = data.length
        } else {
          callbackLogs.value = data.content || []
          pagination.value.rowsNumber = data.totalElements || data.total || 0
        }
      }
    } else {
      // 查詢所有記錄
      response = await paymentCallbackLogApi.getCallbackLogs({
        page: pagination.value.page - 1,
        size: pagination.value.rowsPerPage
      })
      if (response.success && response.data) {
        const data = response.data as PageResponse<PaymentCallbackLog> | PaymentCallbackLog[]
        if (Array.isArray(data)) {
          callbackLogs.value = data
          pagination.value.rowsNumber = data.length
        } else {
          callbackLogs.value = data.content || []
          pagination.value.rowsNumber = data.totalElements || data.total || 0
        }
      }
    }

    // 如果有狀態篩選，在客戶端過濾
    if (filterStatus.value) {
      callbackLogs.value = callbackLogs.value.filter(log => log.status === filterStatus.value)
    }
  } catch (error: any) {
    console.error('Failed to load callback logs:', error)
    $q.notify({
      type: 'negative',
      message: '載入回調記錄失敗：' + (error.response?.data?.message || error.message),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  pagination.value.page = 1
  loadCallbackLogs()
}

const onRequest = (props: any) => {
  pagination.value.page = props.pagination.page
  pagination.value.rowsPerPage = props.pagination.rowsPerPage
  loadCallbackLogs()
}

const handleViewDetail = (log: PaymentCallbackLog) => {
  selectedLog.value = log
  showDetailDialog.value = true
}

// 啟動支付回調記錄導覽
const handleStartTour = () => {
  nextTick(() => {
    startPaymentCallbackLogTour(true)
  })
}

onMounted(() => {
  loadCallbackLogs()
  
  // 如果用戶是第一次訪問支付回調記錄頁面，自動啟動導覽
  if (!isPaymentCallbackLogTourCompleted()) {
    setTimeout(() => {
      startPaymentCallbackLogTour()
    }, 1500)
  }
})
</script>

<style scoped>
.font-mono {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}
</style>

