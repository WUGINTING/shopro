<template>
  <q-page class="q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="col">
        <h4 class="q-my-none">會員管理</h4>
        <div class="text-caption text-grey-7 q-mt-xs">管理會員資料、狀態、積點與消費資訊</div>
      </div>
      <div class="col-auto">
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            round
            icon="help_outline"
            color="grey-7"
            @click="handleStartTour"
          >
            <q-tooltip>會員管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            label="新增會員"
            icon="add"
            @click="showCreateDialog"
          />
        </div>
      </div>
    </div>

    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-12 col-sm-6 col-lg-3">
        <q-card flat bordered class="member-metric-card">
          <q-card-section>
            <div class="text-caption text-grey-7">目前列表筆數</div>
            <div class="member-metric-card__value">{{ members.length }}</div>
            <div class="text-caption text-grey-6">本頁已載入會員資料</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-lg-3">
        <q-card flat bordered class="member-metric-card member-metric-card--green">
          <q-card-section>
            <div class="text-caption text-grey-7">啟用會員</div>
            <div class="member-metric-card__value">{{ memberMetrics.active }}</div>
            <div class="text-caption text-grey-6">目前可正常使用的會員帳號</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-lg-3">
        <q-card flat bordered class="member-metric-card member-metric-card--amber">
          <q-card-section>
            <div class="text-caption text-grey-7">總積點</div>
            <div class="member-metric-card__value">{{ formatPoints(memberMetrics.points) }}</div>
            <div class="text-caption text-grey-6">本頁會員積點加總</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-lg-3">
        <q-card flat bordered class="member-metric-card member-metric-card--blue">
          <q-card-section>
            <div class="text-caption text-grey-7">總消費</div>
            <div class="member-metric-card__value">NT${{ formatCurrency(memberMetrics.spent) }}</div>
            <div class="text-caption text-grey-6">本頁會員累積消費加總</div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- 搜尋欄 -->
    <q-card class="q-mb-md member-filter-card">
      <q-card-section class="member-filter-card__header">
        <div class="row items-center justify-between q-mb-sm">
          <div>
            <div class="text-h6">搜尋與篩選</div>
            <div class="text-caption text-grey-7">快速定位會員帳號、狀態與聯絡資料</div>
          </div>
          <q-chip dense color="grey-2" text-color="grey-8" icon="groups">
            共 {{ pagination.rowsNumber || members.length }} 位會員
          </q-chip>
        </div>
        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-input
              v-model="searchForm.name"
              label="會員名稱"
              outlined
              dense
              name="member-name-search"
              autocomplete="off"
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-input
              v-model="searchForm.email"
              label="電子郵件"
              outlined
              dense
              type="email"
              name="member-email-search"
              autocomplete="off"
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-select
              v-model="searchForm.status"
              label="狀態"
              :options="['ACTIVE', 'INACTIVE', 'SUSPENDED']"
              outlined
              dense
              clearable
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-btn
              color="primary"
              label="重置"
              outline
              class="full-width full-height"
              @click="resetSearch"
            />
          </div>
        </div>
      </q-card-section>
    </q-card>

    <!-- 會員列表 -->
    <q-card class="member-table-card">
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        class="member-admin-table"
        :rows="members"
        :columns="columns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
        :rows-per-page-options="[10, 20, 50]"
        wrap-cells
        flat
        bordered
      >
        <template #top>
          <div class="member-table-toolbar full-width">
            <div>
              <div class="text-subtitle1 text-weight-bold">會員列表</div>
              <div class="text-caption text-grey-7">顯示 {{ members.length }} 位（總計 {{ pagination.rowsNumber || members.length }} 位）</div>
            </div>
            <div class="row items-center q-gutter-xs">
              <q-chip dense color="green-1" text-color="green-10">啟用 {{ memberMetrics.active }}</q-chip>
              <q-chip dense color="orange-1" text-color="orange-10">未啟用 {{ memberMetrics.inactive }}</q-chip>
              <q-chip dense color="red-1" text-color="red-10">停用 {{ memberMetrics.suspended }}</q-chip>
            </div>
          </div>
        </template>

        <template #body-cell-name="props">
          <q-td :props="props">
            <div class="column q-gutter-xs">
              <span class="text-weight-medium">{{ props.row.name }}</span>
              <span class="text-caption text-grey-6">{{ props.row.email }}</span>
            </div>
          </q-td>
        </template>

        <template #body-cell-status="props">
          <q-td :props="props">
            <q-badge
              :label="getStatusLabel(props.row.status)"
              :color="getStatusColor(props.row.status)"
            />
          </q-td>
        </template>

        <template #body-cell-totalPoints="props">
          <q-td :props="props">
            <q-chip dense color="amber" text-color="white" icon="stars">
              {{ formatPoints(props.row.totalPoints) }}
            </q-chip>
          </q-td>
        </template>

        <template #body-cell-totalSpent="props">
          <q-td :props="props">
            <div class="column q-gutter-xs">
              <span class="text-weight-bold text-primary">NT${{ formatCurrency(props.row.totalSpent) }}</span>
              <span class="text-caption text-grey-6">{{ getSpendHint(props.row.totalSpent) }}</span>
            </div>
          </q-td>
        </template>

        <template #body-cell-registeredDate="props">
          <q-td :props="props">
            <span>{{ formatDateTime(props.row.registeredDate) }}</span>
          </q-td>
        </template>

        <template #body-cell-actions="props">
          <q-td :props="props" class="member-actions-cell">
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="edit"
              color="primary"
              class="member-row-icon-btn"
              @click="showEditDialog(props.row)"
              title="編輯"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="delete"
              color="negative"
              class="member-row-icon-btn"
              @click="confirmDelete(props.row.id)"
              title="刪除"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              :icon="props.row.status === 'ACTIVE' ? 'block' : 'check_circle'"
              :color="props.row.status === 'ACTIVE' ? 'orange' : 'positive'"
              class="member-row-icon-btn"
            >
              <q-tooltip>{{ props.row.status === 'ACTIVE' ? '停用' : '啟用' }}</q-tooltip>
              <q-menu auto-close>
                <q-list style="min-width: 100px">
                  <q-item v-if="props.row.status === 'ACTIVE'" clickable @click="suspendMember(props.row.id)">
                    <q-item-section>停用</q-item-section>
                  </q-item>
                  <q-item v-else clickable @click="activateMember(props.row.id)">
                    <q-item-section>啟用</q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </q-btn>
          </q-td>
        </template>

        <template #no-data>
          <div class="text-center q-py-lg text-grey-7">
            沒有會員資料
          </div>
        </template>
      </q-table>
    </q-card>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog">
      <q-card class="member-dialog-card" style="width: 500px; max-width: 90vw">
        <q-card-section class="row items-center q-pb-none member-dialog-card__header">
          <div class="text-h6">
            {{ editingMember?.id ? '編輯會員' : '新增會員' }}
          </div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showDialog = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pt-none member-dialog-card__body">
          <q-form ref="memberForm" @submit="saveMember">
            <q-banner rounded dense class="member-dialog-banner q-mb-md">
              建議優先確認 Email、狀態與聯絡電話，避免通知與服務流程中斷。
            </q-banner>
            <q-input
              v-model="editingMember.name"
              label="會員名稱 *"
              outlined
              dense
              class="q-mb-md"
              name="member-name"
              autocomplete="name"
              :rules="[val => !!val || '請輸入會員名稱']"
            />

            <q-input
              v-model="editingMember.email"
              label="電子郵件 *"
              outlined
              dense
              type="email"
              class="q-mb-md"
              name="member-email"
              autocomplete="email"
              :rules="[
                val => !!val || '請輸入電子郵件',
                val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || '電子郵件格式不正確'
              ]"
            />

            <q-input
              v-model="editingMember.phone"
              label="電話"
              outlined
              dense
              class="q-mb-md"
              name="member-phone"
              type="tel"
              inputmode="tel"
              autocomplete="tel"
            />

            <q-select
              v-model="editingMember.status"
              label="狀態 *"
              outlined
              dense
              :options="['ACTIVE', 'INACTIVE', 'SUSPENDED']"
              class="q-mb-md"
              :rules="[val => !!val || '請選擇狀態']"
            />

            <q-input
              v-model.number="editingMember.totalPoints"
              label="總積點"
              outlined
              dense
              type="number"
              class="q-mb-md"
              inputmode="numeric"
            />

            <q-input
              v-model.number="editingMember.totalSpent"
              label="總消費金額"
              outlined
              dense
              type="number"
              class="q-mb-md"
              inputmode="decimal"
            />

            <q-input
              v-model="editingMember.notes"
              label="備註"
              outlined
              dense
              type="textarea"
              class="q-mb-md"
            />

            <div class="row q-gutter-md member-dialog-card__actions-row">
              <q-btn
                label="取消"
                flat
                @click="showDialog = false"
              />
              <q-btn
                label="保存"
                color="primary"
                type="submit"
                :loading="saving"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 刪除確認 -->
    <q-dialog v-model="showDeleteDialog">
      <q-card class="member-dialog-card member-dialog-card--danger">
        <q-card-section class="row items-center">
          <q-icon
            name="warning"
            size="md"
            color="negative"
          />
          <span class="q-ml-md">確定要刪除此會員嗎？</span>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-banner rounded dense class="member-dialog-banner member-dialog-banner--danger">
            刪除後無法復原，請先確認不會影響既有會員服務與資料追蹤。
          </q-banner>
        </q-card-section>

        <q-card-actions align="right" class="member-dialog-card__actions">
          <q-btn
            label="取消"
            flat
            @click="showDeleteDialog = false"
          />
          <q-btn
            label="刪除"
            color="negative"
            @click="deleteMember"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { memberApi, type Member, type PageResponse } from '@/api/member'
import { startMemberTour, isMemberTourCompleted } from '@/utils/memberTour'

const $q = useQuasar()
const memberForm = ref()

// 資料
const members = ref<Member[]>([])
const pagination = ref({
  page: 0,
  rowsPerPage: 20,
  rowsNumber: 0
})
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)

// 搜尋表單
const searchForm = ref({
  name: '',
  email: '',
  status: ''
})

// 對話框
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const deleteId = ref<number>()

// 編輯會員
const editingMember = ref<Partial<Member>>({
  name: '',
  email: '',
  status: 'ACTIVE',
  totalPoints: 0,
  totalSpent: 0
})

// 表格列定義
const columns = [
  { name: 'name', label: '名稱', field: 'name', align: 'left' },
  { name: 'email', label: '電子郵件', field: 'email', align: 'left' },
  { name: 'phone', label: '電話', field: 'phone', align: 'left' },
  { name: 'status', label: '狀態', field: 'status', align: 'center' },
  { name: 'totalPoints', label: '積點', field: 'totalPoints', align: 'right' },
  { name: 'totalSpent', label: '消費金額', field: 'totalSpent', align: 'right' },
  { name: 'registeredDate', label: '註冊日期', field: 'registeredDate', align: 'center' },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' }
]

const memberMetrics = computed(() => ({
  active: members.value.filter(m => m.status === 'ACTIVE').length,
  inactive: members.value.filter(m => m.status === 'INACTIVE').length,
  suspended: members.value.filter(m => m.status === 'SUSPENDED').length,
  points: members.value.reduce((sum, m) => sum + (Number(m.totalPoints) || 0), 0),
  spent: members.value.reduce((sum, m) => sum + (Number(m.totalSpent) || 0), 0)
}))

// 取得狀態顏色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    ACTIVE: 'green',
    INACTIVE: 'orange',
    SUSPENDED: 'red'
  }
  return colors[status] || 'grey'
}

const getStatusLabel = (status?: string) => {
  const labels: Record<string, string> = {
    ACTIVE: '啟用',
    INACTIVE: '未啟用',
    SUSPENDED: '停用'
  }
  return labels[status || ''] || (status || '-')
}

// 格式化金額
const formatCurrency = (amount: number | undefined | null) => {
  if (amount === undefined || amount === null || isNaN(amount)) {
    return '0'
  }
  return amount.toLocaleString('zh-TW')
}

const formatPoints = (points: number | undefined | null) => {
  return Number(points || 0).toLocaleString('zh-TW')
}

const formatDateTime = (value?: string | null) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const getSpendHint = (amount?: number | null) => {
  const value = Number(amount) || 0
  if (value >= 50000) return '高價值會員'
  if (value >= 10000) return '可重點經營'
  if (value > 0) return '已有消費'
  return '尚無消費'
}

// 載入會員列表
const loadMembers = async () => {
  loading.value = true
  try {
    const result = await memberApi.getMembers({
      page: pagination.value.page,
      size: pagination.value.rowsPerPage,
      ...searchForm.value
    })
    members.value = result?.content || []
    pagination.value.rowsNumber = result?.totalElements || 0
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入會員列表失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 搜尋
const onSearch = () => {
  pagination.value.page = 0
  loadMembers()
}

// 重置搜尋
const resetSearch = () => {
  searchForm.value = { name: '', email: '', status: '' }
  pagination.value.page = 0
  loadMembers()
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingMember.value = {
    name: '',
    email: '',
    status: 'ACTIVE',
    totalPoints: 0,
    totalSpent: 0
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (member: Member) => {
  editingMember.value = { ...member }
  showDialog.value = true
}

// 保存會員
const saveMember = async () => {
  try {
    if (editingMember.value.id) {
      await memberApi.updateMember(
        editingMember.value.id,
        editingMember.value
      )
      $q.notify({
        type: 'positive',
        message: '會員已更新',
        position: 'top'
      })
    } else {
      await memberApi.createMember(editingMember.value as Member)
      $q.notify({
        type: 'positive',
        message: '會員已建立',
        position: 'top'
      })
    }
    showDialog.value = false
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

// 確認刪除
const confirmDelete = (id: number) => {
  deleteId.value = id
  showDeleteDialog.value = true
}

// 刪除會員
const deleteMember = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await memberApi.deleteMember(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '會員已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '刪除失敗',
      position: 'top'
    })
  } finally {
    deleting.value = false
  }
}

// 停用會員
const suspendMember = async (id: number) => {
  try {
    await memberApi.suspendMember(id)
    $q.notify({
      type: 'positive',
      message: '會員已停用',
      position: 'top'
    })
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '停用失敗',
      position: 'top'
    })
  }
}

// 啟用會員
const activateMember = async (id: number) => {
  try {
    await memberApi.activateMember(id)
    $q.notify({
      type: 'positive',
      message: '會員已啟用',
      position: 'top'
    })
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '啟用失敗',
      position: 'top'
    })
  }
}

// 啟動會員管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startMemberTour(true)
  })
}

onMounted(() => {
  loadMembers()
  
  // 如果用戶是第一次訪問會員管理頁面，自動啟動導覽
  if (!isMemberTourCompleted()) {
    setTimeout(() => {
      startMemberTour()
    }, 1500)
  }
})
</script>

<style scoped>
.member-metric-card {
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255,255,255,.98), rgba(248,250,252,.96));
  box-shadow: 0 10px 26px rgba(15,23,42,.06);
  transition: transform .18s ease, box-shadow .18s ease;
}

.member-metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(15,23,42,.1);
}

.member-metric-card__value {
  margin: 6px 0 4px;
  font-size: 1.45rem;
  font-weight: 700;
  line-height: 1.1;
  color: #0f172a;
}

.member-metric-card--green {
  border-color: rgba(34,197,94,.22);
  background: linear-gradient(180deg, rgba(240,253,244,.98), rgba(236,253,245,.96));
}

.member-metric-card--amber {
  border-color: rgba(245,158,11,.24);
  background: linear-gradient(180deg, rgba(255,251,235,.98), rgba(255,247,237,.96));
}

.member-metric-card--blue {
  border-color: rgba(59,130,246,.2);
  background: linear-gradient(180deg, rgba(239,246,255,.98), rgba(238,242,255,.96));
}

.member-filter-card,
.member-table-card {
  border-radius: 18px;
  border: 1px solid rgba(148,163,184,.2);
  background: rgba(255,255,255,.96);
  box-shadow: 0 12px 32px rgba(15,23,42,.05);
}

.member-filter-card__header {
  padding-bottom: 12px;
}

.member-admin-table :deep(.q-table__top) {
  padding: 14px 16px 8px;
}

.member-table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.member-admin-table :deep(thead tr th) {
  background: #f8fafc;
  color: #334155;
  font-weight: 700;
}

.member-admin-table :deep(tbody tr:hover) {
  background: rgba(59,130,246,.04);
}

.member-actions-cell {
  min-width: 150px;
}

.member-row-icon-btn {
  margin-right: 4px;
  margin-bottom: 4px;
}

.member-dialog-card {
  border-radius: 18px;
  border: 1px solid rgba(148,163,184,.2);
  box-shadow: 0 18px 42px rgba(15,23,42,.12);
}

.member-dialog-card__header {
  border-bottom: 1px solid rgba(148,163,184,.14);
}

.member-dialog-card__body {
  padding-top: 14px;
}

.member-dialog-banner {
  background: linear-gradient(90deg, rgba(59,130,246,.08), rgba(34,197,94,.08));
  color: #0f172a;
  border: 1px solid rgba(59,130,246,.12);
}

.member-dialog-banner--danger {
  background: linear-gradient(90deg, rgba(239,68,68,.08), rgba(251,146,60,.08));
  border-color: rgba(239,68,68,.14);
}

.member-dialog-card--danger {
  border-color: rgba(239,68,68,.2);
}

.member-dialog-card__actions {
  border-top: 1px solid rgba(148,163,184,.12);
}

@media (max-width: 1023px) {
  .member-admin-table :deep(.q-table__middle) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .member-admin-table :deep(table) {
    min-width: 980px;
  }
}

@media (max-width: 599px) {
  .member-metric-card__value { font-size: 1.28rem; }
  .member-table-toolbar { align-items: flex-start; }
  .member-dialog-card { width: calc(100vw - 24px) !important; max-width: unset !important; }
}

@media (prefers-reduced-motion: reduce) {
  .member-metric-card { transition: none; }
}
</style>
