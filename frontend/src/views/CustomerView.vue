<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">會員管理（CRM）</div>
          <div class="text-caption text-grey-7">管理會員資料、等級、點數與消費統計</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn flat dense round icon="help_outline" color="grey-7" @click="handleStartTour">
            <q-tooltip>會員管理導覽</q-tooltip>
          </q-btn>
          <q-btn color="primary" icon="refresh" label="重新載入" unelevated :loading="loading" @click="loadCustomers" />
        </div>
      </div>

      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="metric-card">
            <q-card-section>
              <div class="metric-label">會員總數</div>
              <div class="metric-value">{{ customerMetrics.total }}</div>
              <div class="metric-sub">目前清單中的會員筆數</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="metric-card metric-card--gold">
            <q-card-section>
              <div class="metric-label">高等級會員</div>
              <div class="metric-value">{{ customerMetrics.highTier }}</div>
              <div class="metric-sub">Gold / Platinum</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="metric-card metric-card--blue">
            <q-card-section>
              <div class="metric-label">會員總點數</div>
              <div class="metric-value">{{ formatNumber(customerMetrics.totalPoints) }}</div>
              <div class="metric-sub">篩選結果累積點數</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="metric-card metric-card--green">
            <q-card-section>
              <div class="metric-label">會員總消費</div>
              <div class="metric-value">{{ formatCurrency(customerMetrics.totalSpent) }}</div>
              <div class="metric-sub">篩選結果累積消費</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md items-end">
            <div class="col-12 col-md-4">
              <q-input v-model="filters.keyword" outlined dense clearable label="搜尋會員" placeholder="姓名 / Email / 電話">
                <template v-slot:prepend><q-icon name="search" /></template>
              </q-input>
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="filters.memberLevel"
                outlined
                dense
                clearable
                label="會員等級"
                :options="memberLevelOptions"
                emit-value
                map-options
                option-label="label"
                option-value="value"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-input v-model.number="filters.minPoints" outlined dense clearable type="number" label="最低點數" min="0" />
            </div>
            <div class="col-12 col-md-2">
              <q-btn flat color="grey-7" icon="close" label="清除" class="full-width" @click="resetFilters" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <q-card>
        <q-table
          :rows="filteredCustomers"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          flat
        >
          <template v-slot:top>
            <div class="row items-center justify-between full-width">
              <div>
                <div class="text-subtitle1 text-weight-bold">會員清單</div>
                <div class="text-caption text-grey-7">顯示 {{ filteredCustomers.length }} 筆（共 {{ customers.length }} 筆）</div>
              </div>
            </div>
          </template>

          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="column q-gutter-xs">
                <span class="text-weight-medium">{{ props.row.name || '-' }}</span>
                <span class="text-caption text-grey-6">{{ props.row.phone || '未提供電話' }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-memberLevel="props">
            <q-td :props="props">
              <q-badge :color="memberLevelColor(props.row.memberLevel)">{{ memberLevelLabel(props.row.memberLevel) }}</q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-points="props">
            <q-td :props="props"><span class="text-weight-bold">{{ formatNumber(props.row.points || 0) }}</span></q-td>
          </template>

          <template v-slot:body-cell-totalSpent="props">
            <q-td :props="props"><span class="text-primary text-weight-bold">{{ formatCurrency(props.row.totalSpent || 0) }}</span></q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="visibility" color="primary" size="sm" @click="showDetail(props.row)" />
              <q-btn flat dense round icon="add_circle" color="positive" size="sm" @click="openPointsDialog(props.row)" />
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="confirmDelete(props.row)" />
            </q-td>
          </template>

          <template v-slot:no-data>
            <div class="q-pa-lg text-center text-grey-7">查無符合條件的會員資料</div>
          </template>
        </q-table>
      </q-card>

      <q-dialog v-model="detailDialog">
        <q-card style="min-width: 460px; max-width: 92vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">會員資訊</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>
          <q-card-section v-if="selectedCustomer">
            <div class="text-subtitle1 text-weight-bold">{{ selectedCustomer.name }}</div>
            <div class="text-body2 text-grey-7 q-mb-md">{{ selectedCustomer.email }}</div>
            <div class="row q-col-gutter-md">
              <div class="col-6">
                <div class="text-caption text-grey-7">電話</div>
                <div>{{ selectedCustomer.phone || '未提供' }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-7">等級</div>
                <q-badge :color="memberLevelColor(selectedCustomer.memberLevel)">{{ memberLevelLabel(selectedCustomer.memberLevel) }}</q-badge>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-7">點數</div>
                <div>{{ formatNumber(selectedCustomer.points || 0) }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-7">累積消費</div>
                <div>{{ formatCurrency(selectedCustomer.totalSpent || 0) }}</div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </q-dialog>

      <q-dialog v-model="pointsDialog">
        <q-card style="min-width: 420px; max-width: 92vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">調整會員點數</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>
          <q-card-section>
            <div class="text-body2 q-mb-sm">會員：{{ selectedCustomer?.name || '-' }}</div>
            <q-input v-model.number="pointsDelta" outlined dense type="number" label="增加點數" min="1" />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="取消" v-close-popup />
            <q-btn color="primary" unelevated label="確認新增" :loading="savingPoints" @click="addPoints" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import crmApi, { type Customer } from '@/api/crm'
import { isCustomerTourCompleted, startCustomerTour } from '@/utils/customerTour'

type MemberLevel = 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM'

const $q = useQuasar()

const loading = ref(false)
const customers = ref<Customer[]>([])
const selectedCustomer = ref<Customer | null>(null)
const detailDialog = ref(false)
const pointsDialog = ref(false)
const pointsDelta = ref<number | null>(null)
const savingPoints = ref(false)

const filters = ref({
  keyword: '',
  memberLevel: null as MemberLevel | null,
  minPoints: null as number | null
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  { name: 'name', label: '會員', field: 'name', align: 'left' as const, sortable: true },
  { name: 'email', label: 'Email', field: 'email', align: 'left' as const, sortable: true },
  { name: 'memberLevel', label: '等級', field: 'memberLevel', align: 'center' as const, sortable: true },
  { name: 'points', label: '點數', field: 'points', align: 'right' as const, sortable: true },
  { name: 'totalSpent', label: '累積消費', field: 'totalSpent', align: 'right' as const, sortable: true },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' as const }
]

const memberLevelOptions = [
  { label: '銅級', value: 'BRONZE' },
  { label: '銀級', value: 'SILVER' },
  { label: '金級', value: 'GOLD' },
  { label: '白金級', value: 'PLATINUM' }
] as const

const filteredCustomers = computed(() => {
  const keyword = filters.value.keyword.trim().toLowerCase()

  return customers.value.filter((customer) => {
    const matchKeyword =
      !keyword ||
      String(customer.name || '').toLowerCase().includes(keyword) ||
      String(customer.email || '').toLowerCase().includes(keyword) ||
      String(customer.phone || '').toLowerCase().includes(keyword)

    const matchLevel = !filters.value.memberLevel || customer.memberLevel === filters.value.memberLevel
    const matchPoints = filters.value.minPoints == null || Number(customer.points || 0) >= Number(filters.value.minPoints)

    return matchKeyword && matchLevel && matchPoints
  })
})

const customerMetrics = computed(() => {
  const list = filteredCustomers.value
  return {
    total: list.length,
    highTier: list.filter(c => c.memberLevel === 'GOLD' || c.memberLevel === 'PLATINUM').length,
    totalPoints: list.reduce((sum, c) => sum + Number(c.points || 0), 0),
    totalSpent: list.reduce((sum, c) => sum + Number(c.totalSpent || 0), 0)
  }
})

const normalizeList = <T,>(payload: unknown): T[] => {
  if (Array.isArray(payload)) return payload as T[]
  if (payload && typeof payload === 'object') {
    const obj = payload as Record<string, unknown>
    if (Array.isArray(obj.data)) return obj.data as T[]
    if (obj.data && typeof obj.data === 'object' && Array.isArray((obj.data as Record<string, unknown>).content)) {
      return (obj.data as { content: T[] }).content
    }
    if (Array.isArray(obj.content)) return obj.content as T[]
  }
  return []
}

const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await crmApi.getCustomers()
    customers.value = normalizeList<Customer>(response)
  } catch (error) {
    $q.notify({ type: 'negative', message: '載入會員資料失敗', position: 'top' })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value.keyword = ''
  filters.value.memberLevel = null
  filters.value.minPoints = null
  pagination.value.page = 1
}

const memberLevelLabel = (level?: string) => {
  switch (level) {
    case 'BRONZE':
      return '銅級'
    case 'SILVER':
      return '銀級'
    case 'GOLD':
      return '金級'
    case 'PLATINUM':
      return '白金級'
    default:
      return level || '-'
  }
}

const memberLevelColor = (level?: string) => {
  switch (level) {
    case 'PLATINUM':
      return 'deep-purple'
    case 'GOLD':
      return 'amber'
    case 'SILVER':
      return 'blue-grey'
    case 'BRONZE':
      return 'brown'
    default:
      return 'grey'
  }
}

const formatNumber = (value: number) => value.toLocaleString('zh-TW')
const formatCurrency = (value: number) => `NT$ ${Number(value || 0).toLocaleString('zh-TW', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`

const showDetail = (customer: Customer) => {
  selectedCustomer.value = customer
  detailDialog.value = true
}

const openPointsDialog = (customer: Customer) => {
  selectedCustomer.value = customer
  pointsDelta.value = null
  pointsDialog.value = true
}

const addPoints = async () => {
  if (!selectedCustomer.value?.id || !pointsDelta.value || pointsDelta.value <= 0) {
    $q.notify({ type: 'warning', message: '請輸入大於 0 的點數', position: 'top' })
    return
  }

  savingPoints.value = true
  try {
    await crmApi.addCustomerPoints(selectedCustomer.value.id, pointsDelta.value)
    $q.notify({ type: 'positive', message: '會員點數已更新', position: 'top' })
    pointsDialog.value = false
    await loadCustomers()
  } catch (error) {
    $q.notify({ type: 'negative', message: '更新會員點數失敗', position: 'top' })
    console.error(error)
  } finally {
    savingPoints.value = false
  }
}

const confirmDelete = (customer: Customer) => {
  if (!customer.id) return

  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除會員「${customer.name || customer.email}」嗎？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await crmApi.deleteCustomer(customer.id!)
      $q.notify({ type: 'positive', message: '會員已刪除', position: 'top' })
      await loadCustomers()
    } catch (error) {
      $q.notify({ type: 'negative', message: '刪除會員失敗', position: 'top' })
      console.error(error)
    }
  })
}

const handleStartTour = () => {
  nextTick(() => startCustomerTour(true))
}

onMounted(async () => {
  await loadCustomers()
  if (!isCustomerTourCompleted()) {
    setTimeout(() => startCustomerTour(), 600)
  }
})
</script>

<style scoped>
.metric-card {
  border-radius: 14px;
}

.metric-label {
  color: #64748b;
  font-size: 12px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.metric-sub {
  color: #94a3b8;
  font-size: 12px;
  margin-top: 4px;
}

.metric-card--gold {
  border-top: 3px solid #f59e0b;
}

.metric-card--blue {
  border-top: 3px solid #3b82f6;
}

.metric-card--green {
  border-top: 3px solid #10b981;
}
</style>
