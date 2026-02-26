<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">訂單問答管理</div>
          <div class="text-caption text-grey-7">管理訂單相關的客戶問答</div>
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
            <q-tooltip>訂單問答管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="add_circle"
            label="新增問題"
            unelevated
            @click="handleOpenDialog"
          />
        </div>
      </div>

      <!-- Search Filter -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md items-center">
            <div class="col-12 col-md-4">
              <q-input
                v-model="searchOrderId"
                label="訂單ID"
                outlined
                dense
                clearable
                @keyup.enter="searchByOrderId"
              >
                <template v-slot:append>
                  <q-btn flat dense icon="search" @click="searchByOrderId" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-4">
              <q-select
                v-model="filterAnswered"
                label="回答狀態"
                outlined
                dense
                :options="[
                  { label: '全部', value: 'all' },
                  { label: '已回答', value: 'answered' },
                  { label: '未回答', value: 'unanswered' }
                ]"
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-md-4">
              <q-btn label="清除篩選" outline color="grey-7" @click="clearFilters" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Q&A Table -->
      <q-card>
        <q-table
          :rows="filteredQAs"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-question="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.row.question }}</div>
              <div class="text-caption text-grey-7">
                提問者: {{ props.row.askerName }} ({{ props.row.askerType }})
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-answer="props">
            <q-td :props="props">
              <div v-if="props.row.answer">
                <div>{{ props.row.answer }}</div>
                <div class="text-caption text-grey-7">
                  回答者: {{ props.row.answererName }}
                </div>
              </div>
              <q-badge v-else color="warning">未回答</q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="props.row.answer ? 'positive' : 'warning'">
                {{ props.row.answer ? '已回答' : '待回答' }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn
                flat
                dense
                round
                :icon="props.row.answer ? 'edit' : 'reply'"
                color="primary"
                size="sm"
                @click="handleAnswer(props.row)"
              >
                <q-tooltip>{{ props.row.answer ? '編輯回答' : '回答' }}</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add Question Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">新增問題</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-select
                v-model="form.orderId"
                label="訂單ID *"
                outlined
                use-input
                input-debounce="300"
                :options="filteredOrderOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                filterable
                :loading="ordersLoading"
                class="q-mb-md"
                :rules="[val => !!val || '請輸入訂單ID']"
                @filter="filterOrders"
              >
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey">
                      {{ ordersLoading ? '載入中...' : '無訂單資料' }}
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>

              <q-select
                v-model="form.askerType"
                label="提問者類型 *"
                outlined
                :options="[
                  { label: '客戶', value: 'CUSTOMER' },
                  { label: '商家', value: 'STORE' }
                ]"
                emit-value
                map-options
                class="q-mb-md"
                :rules="[val => !!val || '請選擇提問者類型']"
              />

              <q-input
                v-model="form.askerName"
                label="提問者名稱"
                outlined
                class="q-mb-md"
              />

              <q-input
                v-model="form.question"
                label="問題內容 *"
                outlined
                type="textarea"
                rows="4"
                :rules="[val => !!val || '請輸入問題內容']"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="提交" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Answer Dialog -->
      <q-dialog v-model="showAnswerDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ currentQA?.answer ? '編輯回答' : '回答問題' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <div class="q-mb-md">
              <div class="text-subtitle2">問題:</div>
              <div class="text-body2">{{ currentQA?.question }}</div>
            </div>

            <q-form>
              <q-input
                v-model="answerForm.answer"
                label="回答內容 *"
                outlined
                type="textarea"
                rows="4"
                class="q-mb-md"
                :rules="[val => !!val || '請輸入回答內容']"
              />

              <q-input
                v-model.number="answerForm.answererId"
                label="回答者ID"
                outlined
                type="number"
                class="q-mb-md"
              />

              <q-input
                v-model="answerForm.answererName"
                label="回答者名稱 *"
                outlined
                :rules="[val => !!val || '請輸入回答者名稱']"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated :label="currentQA?.answer ? '更新回答' : '提交回答'" color="primary" @click="handleAnswerSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute } from 'vue-router'
import { orderQAApi, orderApi, type OrderQA, type Order } from '@/api'
import { startOrderQATour, isOrderQATourCompleted } from '@/utils/orderQATour'

const $q = useQuasar()
const route = useRoute()

const qas = ref<OrderQA[]>([])
const loading = ref(false)
const showDialog = ref(false)
const showAnswerDialog = ref(false)
const searchOrderId = ref('')
const filterAnswered = ref('all')
const currentQA = ref<OrderQA | null>(null)
const orders = ref<Order[]>([])
const ordersLoading = ref(false)

const form = ref<OrderQA>({
  orderId: 0,
  askerType: 'CUSTOMER',
  askerName: '',
  question: ''
})

const answerForm = ref({
  answer: '',
  answererId: 0,
  answererName: ''
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderId', label: '訂單ID', align: 'left' as const, field: 'orderId', sortable: true },
  { name: 'question', label: '問題', align: 'left' as const, field: 'question' },
  { name: 'answer', label: '回答', align: 'left' as const, field: 'answer' },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'createdAt', label: '提問時間', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const filteredQAs = computed(() => {
  if (filterAnswered.value === 'all') return qas.value
  if (filterAnswered.value === 'answered') return qas.value.filter(qa => qa.answer)
  if (filterAnswered.value === 'unanswered') return qas.value.filter(qa => !qa.answer)
  return qas.value
})

const resetForm = () => {
  form.value = {
    orderId: 0,
    askerType: 'CUSTOMER',
    askerName: '',
    question: ''
  }
  filteredOrderOptions.value = orderOptions.value
}

const orderOptions = computed(() => {
  return orders.value.map(order => ({
    label: `${order.orderNumber || `訂單 #${order.id}`} - ${order.customerName || '未知客戶'}`,
    value: order.id,
    order: order
  }))
})

const filteredOrderOptions = ref<Array<{ label: string; value: number; order: Order }>>([])

const loadOrders = async () => {
  ordersLoading.value = true
  try {
    const response = await orderApi.getOrders()
    const data = response.data as any
    let orderList: Order[] = []
    if (Array.isArray(data)) {
      orderList = data
    } else if (data && 'content' in data) {
      orderList = data.content
    }
    orders.value = orderList
    filteredOrderOptions.value = orderOptions.value
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    ordersLoading.value = false
  }
}

watch(orderOptions, (newVal) => {
  filteredOrderOptions.value = newVal
}, { immediate: true })

watch(() => route.query.orderId, (value) => {
  if (value) {
    void applyRouteOrderFilter()
  }
})

const filterOrders = (val: string, update: (callback: () => void) => void) => {
  update(() => {
    if (val === '') {
      filteredOrderOptions.value = orderOptions.value
    } else {
      const needle = val.toLowerCase()
      filteredOrderOptions.value = orderOptions.value.filter(
        option =>
          option.label.toLowerCase().indexOf(needle) > -1 ||
          String(option.value).indexOf(needle) > -1
      )
    }
  })
}


const searchByOrderId = async () => {
  if (!searchOrderId.value) {
    $q.notify({
      type: 'warning',
      message: '請輸入訂單ID',
      position: 'top'
    })
    return
  }

  loading.value = true
  try {
    const response = await orderQAApi.getQAByOrderId(Number(searchOrderId.value))
    qas.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '查詢失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}


const applyRouteOrderFilter = async () => {
  const orderIdParam = route.query.orderId
  if (!orderIdParam) return
  const numericId = Number(orderIdParam)
  if (!Number.isFinite(numericId) || numericId <= 0) return
  searchOrderId.value = String(numericId)
  await searchByOrderId()
}

const clearFilters = () => {
  searchOrderId.value = ''
  filterAnswered.value = 'all'
  loadAllQAs()
}

const handleSubmit = async () => {
  if (!form.value.orderId || !form.value.askerType || !form.value.question) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    // 找到選中的訂單
    const selectedOrder = orders.value.find(o => o.id === form.value.orderId)
    const submittedOrderId = Number(form.value.orderId)

    // 確保 orderId 是數字類型，並設置 askerId
    const submitData = {
      ...form.value,
      orderId: submittedOrderId,
      askerId: selectedOrder?.customerId || 0
    }

    await orderQAApi.askQuestion(submitData)
    $q.notify({
      type: 'positive',
      message: '問題提交成功',
      position: 'top'
    })
    showDialog.value = false
    resetForm()
    // 使用剛才提交的訂單ID來刷新列表
    searchOrderId.value = String(submittedOrderId)
    await searchByOrderId()
  } catch (error: any) {
    console.error('提交失敗:', error)
    const errorMessage = error?.response?.data?.message || error?.message || '提交失敗'
    $q.notify({
      type: 'negative',
      message: errorMessage,
      position: 'top',
      timeout: 5000
    })
  }
}

const handleAnswer = (qa: OrderQA) => {
  currentQA.value = qa
  answerForm.value = {
    answer: qa.answer || '',
    answererId: qa.answererId || 0,
    answererName: qa.answererName || ''
  }
  showAnswerDialog.value = true
}

const handleAnswerSubmit = async () => {
  if (!currentQA.value?.id || !answerForm.value.answer || !answerForm.value.answererName) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    await orderQAApi.answerQuestion(
      currentQA.value.id,
      answerForm.value.answer,
      answerForm.value.answererId,
      answerForm.value.answererName
    )
    $q.notify({
      type: 'positive',
      message: currentQA.value?.answer ? '回答更新成功' : '回答提交成功',
      position: 'top'
    })
    showAnswerDialog.value = false
    // 使用當前問題的訂單ID來刷新列表
    const orderIdToRefresh = currentQA.value.orderId
    if (orderIdToRefresh) {
      searchOrderId.value = String(orderIdToRefresh)
      await searchByOrderId()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '回答提交失敗',
      position: 'top'
    })
  }
}

const loadAllQAs = async () => {
  loading.value = true
  try {
    const response = await orderQAApi.getAllQA()
    qas.value = response.data
  } catch (error) {
    console.error('Failed to load QAs:', error)
  } finally {
    loading.value = false
  }
}

// 啟動訂單問答管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startOrderQATour(true)
  })
}

onMounted(() => {
  loadOrders()
  loadAllQAs()
  void applyRouteOrderFilter()

  // 如果用戶是第一次訪問訂單問答管理頁面，自動啟動導覽
  if (!isOrderQATourCompleted()) {
    setTimeout(() => {
      startOrderQATour()
    }, 1500)
  }
})

const handleOpenDialog = () => {
  resetForm()
  if (orders.value.length === 0) {
    loadOrders()
  }
  showDialog.value = true
}

const handleDelete = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這條問答記錄嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderQAApi.deleteQA(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      // Refresh list
      if (searchOrderId.value) {
        searchByOrderId()
      } else {
        loadAllQAs()
      }
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗',
        position: 'top'
      })
    }
  })
}
</script>
