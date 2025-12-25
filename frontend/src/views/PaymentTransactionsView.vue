<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4 q-mb-md">金流交易紀錄</div>

      <!-- 搜尋欄位 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input
                v-model="searchKeyword"
                label="搜尋關鍵字"
                placeholder="訂單編號或交易序號"
                dense
                outlined
                clearable
              >
                <template v-slot:prepend>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="searchGateway"
                :options="gatewayOptions"
                label="支付閘道"
                dense
                outlined
                clearable
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="searchStatus"
                :options="statusOptions"
                label="交易狀態"
                dense
                outlined
                clearable
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-md-2">
              <q-btn
                color="primary"
                label="搜尋"
                icon="search"
                @click="search"
                class="full-width"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 交易列表 -->
      <q-table
        :rows="transactions"
        :columns="columns"
        row-key="id"
        :loading="loading"
        :pagination="pagination"
        @request="onRequest"
      >
        <template v-slot:body-cell-gateway="props">
          <q-td :props="props">
            <q-badge :color="getGatewayColor(props.value)">
              {{ getGatewayLabel(props.value) }}
            </q-badge>
          </q-td>
        </template>

        <template v-slot:body-cell-status="props">
          <q-td :props="props">
            <q-badge :color="getStatusColor(props.value)">
              {{ getStatusLabel(props.value) }}
            </q-badge>
          </q-td>
        </template>

        <template v-slot:body-cell-amount="props">
          <q-td :props="props">
            <div class="text-weight-medium">
              $ {{ formatCurrency(props.value) }}
            </div>
          </q-td>
        </template>

        <template v-slot:body-cell-createdAt="props">
          <q-td :props="props">
            {{ formatDateTime(props.value) }}
          </q-td>
        </template>

        <template v-slot:body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              flat
              dense
              color="primary"
              icon="visibility"
              @click="viewDetail(props.row)"
            >
              <q-tooltip>查看詳情</q-tooltip>
            </q-btn>
            <q-btn
              flat
              dense
              color="info"
              icon="sync"
              @click="syncStatus(props.row)"
              v-if="props.row.status === 'PROCESSING' || props.row.status === 'INITIATED'"
            >
              <q-tooltip>同步狀態</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>

      <!-- 詳情對話框 -->
      <q-dialog v-model="showDetailDialog" full-width>
        <q-card style="max-width: 800px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">交易詳情</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section v-if="selectedTransaction">
            <q-list>
              <q-item>
                <q-item-section>
                  <q-item-label caption>訂單編號</q-item-label>
                  <q-item-label>{{ selectedTransaction.orderNumber }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item>
                <q-item-section>
                  <q-item-label caption>交易 ID</q-item-label>
                  <q-item-label>{{ selectedTransaction.transactionId }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item>
                <q-item-section>
                  <q-item-label caption>支付閘道</q-item-label>
                  <q-item-label>
                    <q-badge :color="getGatewayColor(selectedTransaction.gateway)">
                      {{ getGatewayLabel(selectedTransaction.gateway) }}
                    </q-badge>
                  </q-item-label>
                </q-item-section>
              </q-item>
              <q-item>
                <q-item-section>
                  <q-item-label caption>交易狀態</q-item-label>
                  <q-item-label>
                    <q-badge :color="getStatusColor(selectedTransaction.status)">
                      {{ getStatusLabel(selectedTransaction.status) }}
                    </q-badge>
                  </q-item-label>
                </q-item-section>
              </q-item>
              <q-item>
                <q-item-section>
                  <q-item-label caption>交易金額</q-item-label>
                  <q-item-label class="text-h6 text-primary">
                    $ {{ formatCurrency(selectedTransaction.amount) }}
                  </q-item-label>
                </q-item-section>
              </q-item>
              <q-item>
                <q-item-section>
                  <q-item-label caption>建立時間</q-item-label>
                  <q-item-label>{{ formatDateTime(selectedTransaction.createdAt) }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item v-if="selectedTransaction.customerName">
                <q-item-section>
                  <q-item-label caption>客戶姓名</q-item-label>
                  <q-item-label>{{ selectedTransaction.customerName }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item v-if="selectedTransaction.errorMessage">
                <q-item-section>
                  <q-item-label caption>錯誤訊息</q-item-label>
                  <q-item-label class="text-negative">{{ selectedTransaction.errorMessage }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item v-if="selectedTransaction.rawResponse">
                <q-item-section>
                  <q-item-label caption>原始回應</q-item-label>
                  <q-item-label>
                    <pre class="raw-response">{{ selectedTransaction.rawResponse }}</pre>
                  </q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { searchTransactions, queryPaymentStatus, type PaymentTransaction } from '@/api/payment'
import { Notify } from 'quasar'

const transactions = ref<PaymentTransaction[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const searchGateway = ref('')
const searchStatus = ref('')
const showDetailDialog = ref(false)
const selectedTransaction = ref<PaymentTransaction | null>(null)

const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const gatewayOptions = [
  { label: 'LINE PAY', value: 'LINE_PAY' },
  { label: '綠界 ECPay', value: 'ECPAY' },
  { label: '手動付款', value: 'MANUAL' }
]

const statusOptions = [
  { label: '已發起', value: 'INITIATED' },
  { label: '處理中', value: 'PROCESSING' },
  { label: '成功', value: 'SUCCESS' },
  { label: '失敗', value: 'FAILED' },
  { label: '已取消', value: 'CANCELLED' },
  { label: '已過期', value: 'EXPIRED' }
]

const columns = [
  {
    name: 'orderNumber',
    label: '訂單編號',
    field: 'orderNumber',
    align: 'left' as const,
    sortable: true
  },
  {
    name: 'transactionId',
    label: '交易序號',
    field: 'transactionId',
    align: 'left' as const
  },
  {
    name: 'gateway',
    label: '支付閘道',
    field: 'gateway',
    align: 'center' as const
  },
  {
    name: 'status',
    label: '狀態',
    field: 'status',
    align: 'center' as const
  },
  {
    name: 'amount',
    label: '金額',
    field: 'amount',
    align: 'right' as const,
    sortable: true
  },
  {
    name: 'createdAt',
    label: '建立時間',
    field: 'createdAt',
    align: 'left' as const,
    sortable: true
  },
  {
    name: 'actions',
    label: '操作',
    field: 'id',
    align: 'center' as const
  }
]

const getGatewayColor = (gateway: string): string => {
  const colors: Record<string, string> = {
    LINE_PAY: 'green',
    ECPAY: 'orange',
    MANUAL: 'grey'
  }
  return colors[gateway] || 'blue'
}

const getGatewayLabel = (gateway: string): string => {
  const labels: Record<string, string> = {
    LINE_PAY: 'LINE PAY',
    ECPAY: '綠界',
    MANUAL: '手動'
  }
  return labels[gateway] || gateway
}

const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    INITIATED: 'info',
    PROCESSING: 'warning',
    SUCCESS: 'positive',
    FAILED: 'negative',
    CANCELLED: 'grey',
    EXPIRED: 'grey-7'
  }
  return colors[status] || 'grey'
}

const getStatusLabel = (status: string): string => {
  const labels: Record<string, string> = {
    INITIATED: '已發起',
    PROCESSING: '處理中',
    SUCCESS: '成功',
    FAILED: '失敗',
    CANCELLED: '已取消',
    EXPIRED: '已過期'
  }
  return labels[status] || status
}

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}

const formatDateTime = (dateTime: string): string => {
  return new Date(dateTime).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const search = async () => {
  loading.value = true
  try {
    const response = await searchTransactions({
      keyword: searchKeyword.value,
      gateway: searchGateway.value,
      status: searchStatus.value
    })
    if (response.success && response.data) {
      transactions.value = response.data
      pagination.value.rowsNumber = response.data.length
    }
  } catch (error) {
    console.error('Search failed:', error)
    Notify.create({
      type: 'negative',
      message: '搜尋失敗'
    })
  } finally {
    loading.value = false
  }
}

const onRequest = (props: any) => {
  // Handle pagination request
  search()
}

const viewDetail = (transaction: PaymentTransaction) => {
  selectedTransaction.value = transaction
  showDetailDialog.value = true
}

const syncStatus = async (transaction: PaymentTransaction) => {
  try {
    const response = await queryPaymentStatus(transaction.gateway, transaction.transactionId)
    if (response.success) {
      Notify.create({
        type: 'positive',
        message: '狀態已同步'
      })
      search()
    }
  } catch (error) {
    console.error('Sync failed:', error)
    Notify.create({
      type: 'negative',
      message: '同步失敗'
    })
  }
}

onMounted(() => {
  search()
})
</script>

<style scoped>
.raw-response {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}
</style>
