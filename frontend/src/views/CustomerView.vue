<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">客戶管理 (CRM)</div>
          <div class="text-caption text-grey-7">管理客戶資訊、會員等級和積分</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn flat dense round icon="help_outline" color="grey-7" @click="handleStartTour">
            <q-tooltip>客戶管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="info"
            icon="calculate"
            label="重新計算總消費"
            unelevated
            :loading="recalculating"
            @click="confirmRecalculateAll"
          >
            <q-tooltip>從所有已付款或已完成的訂單中重新計算所有客戶的總消費</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="person_add"
            label="新增客戶"
            unelevated
            @click="showDialog = true; form = { name: '', email: '', phone: '', memberLevel: 'BRONZE' }"
          />
        </div>
      </div>

      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="customer-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">會員總數</div>
              <div class="customer-metric-card__value">{{ customers.length }}</div>
              <div class="text-caption text-grey-6">資料庫中的客戶總筆數</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="customer-metric-card customer-metric-card--gold">
            <q-card-section>
              <div class="text-caption text-grey-7">高價值會員</div>
              <div class="customer-metric-card__value">{{ customerMetrics.highTierCount }}</div>
              <div class="text-caption text-grey-6">Gold / Platinum 會員</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="customer-metric-card customer-metric-card--blue">
            <q-card-section>
              <div class="text-caption text-grey-7">本頁積分總量</div>
              <div class="customer-metric-card__value">{{ formatNumber(customerMetrics.totalPoints) }}</div>
              <div class="text-caption text-grey-6">目前篩選結果的積分加總</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="customer-metric-card customer-metric-card--green">
            <q-card-section>
              <div class="text-caption text-grey-7">本頁總消費</div>
              <div class="customer-metric-card__value">{{ formatCurrency(customerMetrics.totalSpent) }}</div>
              <div class="text-caption text-grey-6">目前篩選結果的總消費加總</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <q-card class="q-mb-md customer-filter-card">
        <q-card-section class="customer-filter-card__header">
          <div class="row items-center justify-between">
            <div>
              <div class="text-h6">篩選條件</div>
              <div class="text-caption text-grey-7">快速找到高價值會員、待維護資料或特定等級名單</div>
            </div>
            <q-btn flat dense icon="refresh" label="重置" @click="resetFilters" />
          </div>
          <div class="row items-center justify-between q-mt-sm customer-filter-meta">
            <div class="text-caption text-grey-7">
              {{ hasActiveFilters ? `已啟用 ${activeFilterCount} 個篩選條件` : '尚未套用篩選條件' }}
            </div>
            <q-chip dense color="grey-2" text-color="grey-8" icon="groups">
              顯示 {{ filteredCustomers.length }} / {{ customers.length }} 位會員
            </q-chip>
          </div>
        </q-card-section>
        <q-separator />
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input
                v-model="filterForm.keyword"
                outlined
                dense
                clearable
                label="搜尋會員"
                placeholder="姓名 / Email / 電話"
                name="customer-admin-search"
                autocomplete="off"
              >
                <template v-slot:prepend><q-icon name="search" /></template>
              </q-input>
            </div>
            <div class="col-12 col-md-3">
              <q-select v-model="filterForm.memberLevel" outlined dense clearable label="會員等級" :options="memberLevelFilterOptions" />
            </div>
            <div class="col-12 col-md-3">
              <q-input
                v-model.number="filterForm.minPoints"
                outlined
                dense
                clearable
                type="number"
                label="最低積分"
                min="0"
                inputmode="numeric"
                name="customer-min-points-filter"
              />
            </div>
            <div class="col-12 col-md-2">
              <q-input
                v-model.number="filterForm.minSpent"
                outlined
                dense
                clearable
                type="number"
                label="最低消費"
                min="0"
                inputmode="decimal"
                prefix="NT$"
                name="customer-min-spent-filter"
              />
            </div>
          </div>

          <div v-if="hasActiveFilters" class="q-mt-md">
            <div class="row items-center justify-between q-mb-sm">
              <div class="text-caption text-grey-7">已選條件：</div>
              <q-btn flat dense size="sm" color="grey-7" icon="close" label="清除全部" @click="resetFilters" />
            </div>
            <div class="row q-gutter-xs">
              <q-chip v-if="filterForm.keyword" removable color="primary" text-color="white" @remove="filterForm.keyword = ''">
                關鍵字：{{ filterForm.keyword }}
              </q-chip>
              <q-chip v-if="filterForm.memberLevel" removable color="primary" text-color="white" @remove="filterForm.memberLevel = null">
                等級：{{ getMemberLevelLabel(filterForm.memberLevel) }}
              </q-chip>
              <q-chip v-if="filterForm.minPoints !== null" removable color="primary" text-color="white" @remove="filterForm.minPoints = null">
                最低積分：{{ filterForm.minPoints }}
              </q-chip>
              <q-chip v-if="filterForm.minSpent !== null" removable color="primary" text-color="white" @remove="filterForm.minSpent = null">
                最低消費：{{ formatCurrency(filterForm.minSpent) }}
              </q-chip>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="customer-table-card">
        <q-table
          class="customer-admin-table"
          :rows="filteredCustomers"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          wrap-cells
          separator="horizontal"
          flat
        >
          <template v-slot:top>
            <div class="customer-table-toolbar full-width">
              <div>
                <div class="text-subtitle1 text-weight-bold">會員列表</div>
                <div class="text-caption text-grey-7">顯示 {{ filteredCustomers.length }} 位會員（共 {{ customers.length }} 位）</div>
              </div>
              <div class="row items-center q-gutter-xs">
                <q-chip dense color="amber-1" text-color="amber-10">Gold {{ customerMetrics.goldCount }}</q-chip>
                <q-chip dense color="deep-purple-1" text-color="deep-purple-10">Platinum {{ customerMetrics.platinumCount }}</q-chip>
              </div>
            </div>
          </template>

          <template v-slot:body-cell-id="props">
            <q-td :props="props">
              <q-chip dense square color="grey-2" text-color="grey-8" class="text-caption">#{{ props.row.id }}</q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="column q-gutter-xs">
                <span class="text-weight-medium">{{ props.row.name || '-' }}</span>
                <span class="text-caption text-grey-6">{{ props.row.phone || '無電話資料' }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-email="props">
            <q-td :props="props"><span>{{ props.row.email || '-' }}</span></q-td>
          </template>

          <template v-slot:body-cell-phone="props">
            <q-td :props="props"><span>{{ props.row.phone || '-' }}</span></q-td>
          </template>

          <template v-slot:body-cell-memberLevel="props">
            <q-td :props="props">
              <div class="column items-start q-gutter-xs">
                <q-badge :color="getLevelColor(props.row.memberLevel)">{{ getMemberLevelLabel(props.row.memberLevel) }}</q-badge>
                <span class="text-caption text-grey-6">{{ props.row.memberLevel || 'BRONZE' }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-points="props">
            <q-td :props="props">
              <q-chip color="amber" text-color="white" icon="stars" class="customer-points-chip">{{ props.row.points || 0 }}</q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-totalSpent="props">
            <q-td :props="props">
              <div class="column q-gutter-xs">
                <span class="text-weight-bold text-primary">{{ formatCurrency(props.row.totalSpent || 0) }}</span>
                <span class="text-caption text-grey-6">{{ getSpentTierHint(props.row.totalSpent || 0) }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="customer-actions-cell">
              <q-btn flat dense round icon="edit" color="primary" size="sm" class="customer-row-icon-btn" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="add_circle" color="positive" size="sm" class="customer-row-icon-btn" @click="handleAddPoints(props.row)">
                <q-tooltip>積分調整</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="calculate" color="info" size="sm" class="customer-row-icon-btn" @click="recalculateSingleCustomer(props.row)">
                <q-tooltip>重新計算總消費</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" class="customer-row-icon-btn" @click="confirmDelete(props.row)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>

          <template v-slot:no-data>
            <div class="text-center text-grey-6 q-py-lg">
              <div class="text-subtitle2 q-mb-xs">沒有符合條件的會員</div>
              <div class="text-caption">調整篩選條件或重置後再試一次</div>
            </div>
          </template>
        </q-table>
      </q-card>

      <q-dialog v-model="showDialog" persistent>
        <q-card class="customer-dialog-card" style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none customer-dialog-card__header">
            <div>
              <div class="text-h6">{{ form.id ? '編輯客戶' : '新增客戶' }}</div>
              <div class="text-caption text-grey-7">{{ form.id ? '更新會員基本資料與等級設定' : '建立新會員資料，後續可再調整積分' }}</div>
            </div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="customer-dialog-card__body">
            <q-form>
              <q-banner rounded dense class="customer-dialog-banner q-mb-md">
                建議先確認 Email 與電話正確，再設定會員等級。
              </q-banner>
              <q-input v-model="form.name" label="姓名 *" outlined class="q-mb-md" name="customer-name" autocomplete="name" :rules="[val => !!val || '請輸入姓名']" />
              <q-input v-model="form.email" label="電子郵件 *" outlined type="email" class="q-mb-md" name="customer-email" autocomplete="email" :rules="[val => !!val || '請輸入郵箱']" />
              <q-input v-model="form.phone" label="電話" outlined class="q-mb-md" name="customer-phone" type="tel" inputmode="tel" autocomplete="tel" />
              <q-select v-model="form.memberLevel" label="會員等級" outlined :options="memberLevelOptions" class="q-mb-md" />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md customer-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <q-dialog v-model="recalculateDialogVisible" persistent>
        <q-card class="customer-dialog-card customer-dialog-card--info">
          <q-card-section class="row items-center">
            <q-icon name="calculate" color="info" size="md" />
            <span class="q-ml-sm text-weight-medium">確定要重新計算所有客戶的總消費嗎？</span>
          </q-card-section>
          <q-card-section>
            <q-banner rounded dense class="customer-dialog-banner customer-dialog-banner--info q-mb-md">
              此操作會掃描所有已付款/已完成訂單，可能需要一些時間。
            </q-banner>
            <div class="text-body2 text-grey-7">
              此操作將從所有已付款或已完成的訂單中重新計算每個客戶的總消費金額。
              <br />
              <strong>注意：</strong>此操作可能需要一些時間，請耐心等待。
            </div>
          </q-card-section>
          <q-card-actions align="right" class="customer-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" @click="recalculateDialogVisible = false" />
            <q-btn unelevated label="確定" color="info" :loading="recalculating" @click="handleRecalculateAll" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <q-dialog v-model="deleteDialogVisible" persistent>
        <q-card class="customer-dialog-card customer-dialog-card--danger">
          <q-card-section class="row items-center">
            <q-icon name="warning" color="negative" size="md" />
            <span class="q-ml-sm text-weight-medium">確定要刪除此客戶嗎？</span>
          </q-card-section>
          <q-card-section v-if="customerToDelete">
            <q-banner rounded dense class="customer-dialog-banner customer-dialog-banner--danger q-mb-md">
              刪除後無法復原，請先確認是否仍需保留會員資料與交易紀錄關聯。
            </q-banner>
            <div class="text-body2">
              <div><strong>姓名：</strong>{{ customerToDelete.name }}</div>
              <div><strong>Email：</strong>{{ customerToDelete.email }}</div>
            </div>
          </q-card-section>
          <q-card-actions align="right" class="customer-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" @click="deleteDialogVisible = false" />
            <q-btn unelevated label="刪除" color="negative" :loading="deleting" @click="handleDelete" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <q-dialog v-model="pointsDialogVisible" persistent>
        <q-card class="customer-dialog-card" style="min-width: 400px">
          <q-card-section class="row items-center q-pb-none customer-dialog-card__header">
            <div>
              <div class="text-h6">積分操作</div>
              <div class="text-caption text-grey-7">請確認操作類型與積分數量，避免誤調整</div>
            </div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="customer-dialog-card__body">
            <q-banner rounded dense class="customer-dialog-banner q-mb-md">
              積分異動會立即生效，建議保留後續客服追蹤說明。
            </q-banner>
            <q-select
              v-model="pointsForm.operation"
              label="操作類型"
              outlined
              :options="[
                { label: '增加積分', value: 'add' },
                { label: '扣除積分', value: 'deduct' }
              ]"
              option-value="value"
              option-label="label"
              emit-value
              map-options
              class="q-mb-md"
            />
            <q-input v-model.number="pointsForm.points" label="積分數量" outlined type="number" :min="1" name="customer-points-amount" inputmode="numeric" />
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md customer-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="確定" color="primary" @click="handlePointsSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useQuasar } from 'quasar'
import { crmApi, type Customer, type PageResponse } from '@/api'
import { startCustomerTour, isCustomerTourCompleted } from '@/utils/customerTour'

const $q = useQuasar()

const customers = ref<Customer[]>([])
const loading = ref(false)
const showDialog = ref(false)
const pointsDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const deleting = ref(false)
const customerToDelete = ref<Customer | null>(null)
const recalculateDialogVisible = ref(false)
const recalculating = ref(false)

const form = ref<Customer>({
  name: '',
  email: '',
  phone: '',
  memberLevel: 'BRONZE'
})

const pointsForm = ref({
  customerId: 0,
  points: 0,
  operation: 'add' as 'add' | 'deduct'
})

const filterForm = ref({
  keyword: '',
  memberLevel: null as Customer['memberLevel'] | null,
  minPoints: null as number | null,
  minSpent: null as number | null
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '姓名', align: 'left' as const, field: 'name' },
  { name: 'email', label: '邮箱', align: 'left' as const, field: 'email' },
  { name: 'phone', label: '電話', align: 'left' as const, field: 'phone' },
  { name: 'memberLevel', label: '會員等級', align: 'center' as const, field: 'memberLevel' },
  { name: 'points', label: '積分', align: 'center' as const, field: 'points' },
  { name: 'totalSpent', label: '總消費', align: 'left' as const, field: 'totalSpent' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const memberLevelOptions = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM']
const memberLevelFilterOptions = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM']

const filteredCustomers = computed(() => {
  const keyword = filterForm.value.keyword.trim().toLowerCase()
  return customers.value.filter(customer => {
    const matchesKeyword = !keyword || [
      customer.name,
      customer.email,
      customer.phone
    ].some(value => String(value || '').toLowerCase().includes(keyword))

    const matchesLevel = !filterForm.value.memberLevel || customer.memberLevel === filterForm.value.memberLevel
    const matchesPoints = filterForm.value.minPoints === null || (customer.points || 0) >= filterForm.value.minPoints
    const matchesSpent = filterForm.value.minSpent === null || (customer.totalSpent || 0) >= filterForm.value.minSpent

    return matchesKeyword && matchesLevel && matchesPoints && matchesSpent
  })
})

const hasActiveFilters = computed(() => {
  return !!(
    filterForm.value.keyword ||
    filterForm.value.memberLevel ||
    filterForm.value.minPoints !== null ||
    filterForm.value.minSpent !== null
  )
})

const activeFilterCount = computed(() => {
  let count = 0
  if (filterForm.value.keyword) count++
  if (filterForm.value.memberLevel) count++
  if (filterForm.value.minPoints !== null) count++
  if (filterForm.value.minSpent !== null) count++
  return count
})

const customerMetrics = computed(() => {
  const list = filteredCustomers.value
  const goldCount = list.filter(c => c.memberLevel === 'GOLD').length
  const platinumCount = list.filter(c => c.memberLevel === 'PLATINUM').length
  return {
    goldCount,
    platinumCount,
    highTierCount: goldCount + platinumCount,
    totalPoints: list.reduce((sum, c) => sum + (Number(c.points) || 0), 0),
    totalSpent: list.reduce((sum, c) => sum + (Number(c.totalSpent) || 0), 0)
  }
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await crmApi.getCustomers()
    // Backend returns Page<MemberDTO> with content array
    const data = response.data as PageResponse<Customer> | Customer[]
    if (Array.isArray(data)) {
      customers.value = data
    } else if (data && 'content' in data) {
      customers.value = data.content
    } else {
      customers.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入客戶列表失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLevelColor = (level?: Customer['memberLevel']) => {
  const colorMap = {
    BRONZE: 'brown',
    SILVER: 'grey',
    GOLD: 'amber',
    PLATINUM: 'purple'
  }
  return colorMap[level || 'BRONZE']
}

const getMemberLevelLabel = (level?: Customer['memberLevel'] | null) => {
  const labelMap = {
    BRONZE: '銅級',
    SILVER: '銀級',
    GOLD: '金級',
    PLATINUM: '白金級'
  } as const
  return labelMap[(level || 'BRONZE') as keyof typeof labelMap] || String(level || 'BRONZE')
}

const formatCurrency = (amount?: number | null) => {
  const value = Number(amount) || 0
  return new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: 'TWD',
    maximumFractionDigits: 2
  }).format(value)
}

const getSpentTierHint = (amount: number) => {
  if (amount >= 50000) return '高價值會員'
  if (amount >= 10000) return '可重點經營'
  if (amount > 0) return '已有消費紀錄'
  return '尚無消費'
}

const resetFilters = () => {
  filterForm.value = {
    keyword: '',
    memberLevel: null,
    minPoints: null,
    minSpent: null
  }
}

const handleEdit = (customer: Customer) => {
  form.value = { ...customer }
  showDialog.value = true
}

const handleAddPoints = (customer: Customer) => {
  pointsForm.value.customerId = customer.id || 0
  pointsForm.value.points = 0
  pointsForm.value.operation = 'add'
  pointsDialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await crmApi.updateCustomer(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await crmApi.createCustomer(form.value)
      $q.notify({
        type: 'positive',
        message: '創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadCustomers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handlePointsSubmit = async () => {
  try {
    if (pointsForm.value.operation === 'add') {
      await crmApi.addCustomerPoints(pointsForm.value.customerId, pointsForm.value.points)
      $q.notify({
        type: 'positive',
        message: '積分添加成功',
        position: 'top'
      })
    } else {
      await crmApi.deductCustomerPoints(pointsForm.value.customerId, pointsForm.value.points)
      $q.notify({
        type: 'positive',
        message: '積分扣除成功',
        position: 'top'
      })
    }
    pointsDialogVisible.value = false
    pointsForm.value = { customerId: 0, points: 0, operation: 'add' }
    loadCustomers()
  } catch (error) {
    const operationText = pointsForm.value.operation === 'add' ? '添加' : '扣除'
    $q.notify({
      type: 'negative',
      message: `積分${operationText}失敗`,
      position: 'top'
    })
  }
}

const confirmDelete = (customer: Customer) => {
  customerToDelete.value = customer
  deleteDialogVisible.value = true
}

const handleDelete = async () => {
  if (!customerToDelete.value || !customerToDelete.value.id) {
    return
  }

  deleting.value = true
  try {
    await crmApi.deleteCustomer(customerToDelete.value.id)
    $q.notify({
      type: 'positive',
      message: '客戶已刪除',
      position: 'top'
    })
    deleteDialogVisible.value = false
    customerToDelete.value = null
    loadCustomers()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '刪除失敗',
      position: 'top'
    })
  } finally {
    deleting.value = false
  }
}

const confirmRecalculateAll = () => {
  recalculateDialogVisible.value = true
}

const handleRecalculateAll = async () => {
  recalculating.value = true
  try {
    await crmApi.recalculateAllCustomersTotalSpent()
    $q.notify({
      type: 'positive',
      message: '所有客戶的總消費已重新計算',
      position: 'top'
    })
    recalculateDialogVisible.value = false
    loadCustomers()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '重新計算失敗',
      position: 'top'
    })
  } finally {
    recalculating.value = false
  }
}

const recalculateSingleCustomer = async (customer: Customer) => {
  if (!customer.id) {
    return
  }

  $q.dialog({
    title: '重新計算總消費',
    message: `確定要重新計算「${customer.name}」的總消費嗎？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await crmApi.recalculateCustomerTotalSpent(customer.id!)
      $q.notify({
        type: 'positive',
        message: '總消費已重新計算',
        position: 'top'
      })
      loadCustomers()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '重新計算失敗',
        position: 'top'
      })
    }
  })
}

// 啟動客戶管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startCustomerTour(true)
  })
}

onMounted(() => {
  loadCustomers()
  
  // 如果用戶是第一次訪問客戶管理頁面，自動啟動導覽
  if (!isCustomerTourCompleted()) {
    setTimeout(() => {
      startCustomerTour()
    }, 1500)
  }
})
</script>

<style scoped>
.customer-metric-card {
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255,255,255,.98), rgba(248,250,252,.96));
  box-shadow: 0 10px 26px rgba(15,23,42,.06);
  transition: transform .18s ease, box-shadow .18s ease;
}

.customer-metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(15,23,42,.1);
}

.customer-metric-card__value {
  margin: 6px 0 4px;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1.1;
  color: #0f172a;
}

.customer-metric-card--gold {
  border-color: rgba(245, 158, 11, .24);
  background: linear-gradient(180deg, rgba(255,251,235,.98), rgba(255,247,237,.96));
}

.customer-metric-card--blue {
  border-color: rgba(59,130,246,.2);
  background: linear-gradient(180deg, rgba(239,246,255,.98), rgba(238,242,255,.96));
}

.customer-metric-card--green {
  border-color: rgba(34,197,94,.22);
  background: linear-gradient(180deg, rgba(240,253,244,.98), rgba(236,253,245,.96));
}

.customer-filter-card,
.customer-table-card {
  border-radius: 18px;
  border: 1px solid rgba(148,163,184,.2);
  background: rgba(255,255,255,.96);
  box-shadow: 0 12px 32px rgba(15,23,42,.05);
}

.customer-filter-card__header {
  padding-bottom: 12px;
}

.customer-filter-meta {
  gap: 8px;
}

.customer-admin-table :deep(.q-table__top) {
  padding: 14px 16px 8px;
}

.customer-table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.customer-admin-table :deep(thead tr th) {
  background: #f8fafc;
  color: #334155;
  font-weight: 700;
  border-bottom: 1px solid rgba(148,163,184,.22);
}

.customer-admin-table :deep(tbody tr) {
  transition: background-color .18s ease;
}

.customer-admin-table :deep(tbody tr:hover) {
  background: rgba(59,130,246,.04);
}

.customer-admin-table :deep(td),
.customer-admin-table :deep(th) {
  white-space: normal;
  vertical-align: middle;
}

.customer-points-chip {
  font-weight: 600;
}

.customer-actions-cell {
  min-width: 190px;
}

.customer-row-icon-btn {
  margin-right: 4px;
  margin-bottom: 4px;
}

.customer-dialog-card {
  border-radius: 18px;
  border: 1px solid rgba(148,163,184,.22);
  box-shadow: 0 18px 42px rgba(15,23,42,.12);
}

.customer-dialog-card__header {
  border-bottom: 1px solid rgba(148,163,184,.14);
}

.customer-dialog-card__body {
  padding-top: 14px;
}

.customer-dialog-card__actions {
  border-top: 1px solid rgba(148,163,184,.12);
}

.customer-dialog-banner {
  background: linear-gradient(90deg, rgba(59,130,246,.08), rgba(34,197,94,.08));
  color: #0f172a;
  border: 1px solid rgba(59,130,246,.12);
}

.customer-dialog-banner--info {
  background: linear-gradient(90deg, rgba(14,165,233,.09), rgba(59,130,246,.08));
}

.customer-dialog-banner--danger {
  background: linear-gradient(90deg, rgba(239,68,68,.08), rgba(251,146,60,.08));
  border-color: rgba(239,68,68,.14);
}

.customer-dialog-card--info {
  border-color: rgba(14,165,233,.2);
}

.customer-dialog-card--danger {
  border-color: rgba(239,68,68,.2);
}

@media (max-width: 1023px) {
  .customer-admin-table :deep(.q-table__middle) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .customer-admin-table :deep(table) {
    min-width: 980px;
  }
}

@media (max-width: 599px) {
  .customer-metric-card__value {
    font-size: 1.3rem;
  }

  .customer-filter-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .customer-table-toolbar {
    align-items: flex-start;
  }

  .customer-actions-cell {
    min-width: 170px;
  }

  .customer-dialog-card {
    min-width: unset !important;
    width: calc(100vw - 24px);
  }

  .customer-dialog-card__actions {
    justify-content: stretch;
    gap: 8px;
  }

  .customer-dialog-card__actions :deep(.q-btn) {
    flex: 1 1 0;
    min-height: 44px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .customer-metric-card,
  .customer-admin-table :deep(tbody tr) {
    transition: none;
  }
}
</style>
