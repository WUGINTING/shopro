<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">會員等級管理</div>
          <div class="text-caption text-grey-7">管理會員等級、權益和折扣</div>
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
            <q-tooltip>會員等級管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="add_circle"
            label="新增等級"
            unelevated
            @click="showDialog = true; resetForm()"
          />
        </div>
      </div>

      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="level-metric-card admin-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">等級總數</div>
              <div class="level-metric-card__value">{{ levels.length }}</div>
              <div class="text-caption text-grey-6">目前已建立的會員等級</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="level-metric-card level-metric-card--green admin-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">啟用等級</div>
              <div class="level-metric-card__value">{{ levelMetrics.enabled }}</div>
              <div class="text-caption text-grey-6">目前可用的會員等級</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="level-metric-card level-metric-card--blue admin-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">平均折扣率</div>
              <div class="level-metric-card__value">{{ (levelMetrics.avgDiscountRate * 100).toFixed(0) }}%</div>
              <div class="text-caption text-grey-6">所有等級平均折扣比例</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="level-metric-card level-metric-card--amber admin-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">最高積分倍率</div>
              <div class="level-metric-card__value">{{ levelMetrics.maxPointsMultiplier || 0 }}x</div>
              <div class="text-caption text-grey-6">目前等級規則中的最高倍率</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Member Levels Table -->
      <q-card class="level-table-card admin-data-card">
        <q-table
          class="level-admin-table"
          :rows="levels"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          wrap-cells
          flat
        >
          <template v-slot:top>
            <div class="admin-table-toolbar level-table-toolbar full-width">
              <div>
                <div class="text-subtitle1 text-weight-bold">會員等級列表</div>
                <div class="text-caption text-grey-7">顯示 {{ levels.length }} 個等級規則</div>
              </div>
              <div class="row items-center q-gutter-xs">
                <q-chip dense color="green-1" text-color="green-10">啟用 {{ levelMetrics.enabled }}</q-chip>
                <q-chip dense color="grey-2" text-color="grey-8">停用 {{ levelMetrics.disabled }}</q-chip>
              </div>
            </div>
          </template>

          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="row items-center no-wrap q-gutter-sm">
                <q-icon v-if="props.row.iconUrl" name="verified" size="24px" :color="getLevelColor(props.row.levelOrder)" class="q-mr-sm" />
                <div class="column">
                  <span class="text-weight-bold">{{ props.row.name }}</span>
                  <span class="text-caption text-grey-6">{{ props.row.description || '無等級描述' }}</span>
                </div>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-levelOrder="props">
            <q-td :props="props">
              <q-badge :color="getLevelColor(props.row.levelOrder)" :label="`等級 ${props.row.levelOrder}`" />
            </q-td>
          </template>

          <template v-slot:body-cell-minSpendAmount="props">
            <q-td :props="props">
              <span v-if="props.row.minSpendAmount">{{ formatCurrency(props.row.minSpendAmount) }}</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-discountRate="props">
            <q-td :props="props">
              <span v-if="props.row.discountRate">{{ (props.row.discountRate * 100).toFixed(0) }}%折</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-pointsMultiplier="props">
            <q-td :props="props">
              <span v-if="props.row.pointsMultiplier">{{ props.row.pointsMultiplier }}倍</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-enabled="props">
            <q-td :props="props">
              <div class="column items-center q-gutter-xs">
                <q-toggle
                  :model-value="props.row.enabled"
                  @update:model-value="handleToggleEnabled(props.row.id)"
                  color="positive"
                />
                <span class="text-caption" :class="props.row.enabled ? 'text-positive' : 'text-grey-6'">
                  {{ props.row.enabled ? '啟用' : '停用' }}
                </span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="level-actions-cell">
              <q-btn flat dense round icon="edit" color="primary" size="sm" class="level-row-icon-btn" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" class="level-row-icon-btn" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>

          <template v-slot:no-data>
            <div class="text-center text-grey-6 q-py-lg">
              <div class="text-subtitle2 q-mb-xs">尚無會員等級資料</div>
              <div class="text-caption">建立第一個等級規則以啟用會員權益設定</div>
            </div>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card class="level-dialog-card" style="min-width: 600px">
          <q-card-section class="row items-center q-pb-none level-dialog-card__header">
            <div class="text-h6">{{ form.id ? '編輯會員等級' : '新增會員等級' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="level-dialog-card__body">
            <q-form>
              <q-banner rounded dense class="level-dialog-banner q-mb-md">
                等級順序、折扣率與積分倍率會直接影響會員權益，請確認規則一致性。
              </q-banner>
              <div class="row q-col-gutter-md">
                <div class="col-6">
                  <q-input
                    v-model="form.name"
                    label="等級名稱 *"
                    outlined
                    name="member-level-name"
                    autocomplete="off"
                    :rules="[val => !!val || '請輸入等級名稱']"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.levelOrder"
                    label="等級順序 *"
                    outlined
                    type="number"
                    inputmode="numeric"
                    :rules="[val => val > 0 || '等級順序必須大於0']"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.minSpendAmount"
                    label="最低消費金額"
                    outlined
                    type="number"
                    step="0.01"
                    prefix="NT$"
                    inputmode="decimal"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.discountRate"
                    label="折扣率 (0.0-1.0)"
                    outlined
                    type="number"
                    step="0.01"
                    hint="例如: 0.95 表示95折"
                    :rules="[
                      val => val === undefined || val === null || (val >= 0 && val <= 1) || '折扣率必須在0.0到1.0之間'
                    ]"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.pointsMultiplier"
                    label="積分倍率"
                    outlined
                    type="number"
                    step="0.1"
                    inputmode="decimal"
                    hint="例如: 1.5 表示1.5倍積分"
                    :rules="[
                      val => val === undefined || val === null || (val > 0 && val <= 9.99) || '積分倍率必須在0到9.99之間'
                    ]"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model="form.iconUrl"
                    label="圖示 URL"
                    outlined
                    name="member-level-icon-url"
                    autocomplete="off"
                  />
                </div>

                <div class="col-12">
                  <q-input
                    v-model="form.description"
                    label="等級描述"
                    outlined
                    type="textarea"
                    rows="3"
                  />
                </div>

                <div class="col-12">
                  <q-toggle
                    v-model="form.enabled"
                    label="啟用"
                    color="positive"
                  />
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md level-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useQuasar } from 'quasar'
import { memberLevelApi, type MemberLevel, type PageResponse } from '@/api'
import { startMemberLevelTour, isMemberLevelTourCompleted } from '@/utils/memberLevelTour'

const $q = useQuasar()

const levels = ref<MemberLevel[]>([])
const loading = ref(false)
const showDialog = ref(false)

const form = ref<MemberLevel>({
  name: '',
  levelOrder: 1,
  minSpendAmount: 0,
  discountRate: 1.0,
  pointsMultiplier: 1.0,
  description: '',
  iconUrl: '',
  enabled: true
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '等級名稱', align: 'left' as const, field: 'name' },
  { name: 'levelOrder', label: '等級順序', align: 'center' as const, field: 'levelOrder', sortable: true },
  { name: 'minSpendAmount', label: '最低消費', align: 'left' as const, field: 'minSpendAmount', sortable: true },
  { name: 'discountRate', label: '折扣率', align: 'center' as const, field: 'discountRate' },
  { name: 'pointsMultiplier', label: '積分倍率', align: 'center' as const, field: 'pointsMultiplier' },
  { name: 'enabled', label: '狀態', align: 'center' as const, field: 'enabled' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const levelMetrics = computed(() => ({
  enabled: levels.value.filter(l => l.enabled).length,
  disabled: levels.value.filter(l => !l.enabled).length,
  avgDiscountRate: levels.value.length
    ? levels.value.reduce((sum, l) => sum + (Number(l.discountRate) || 0), 0) / levels.value.length
    : 0,
  maxPointsMultiplier: levels.value.reduce((max, l) => Math.max(max, Number(l.pointsMultiplier) || 0), 0)
}))

const formatCurrency = (value?: number | null) => {
  const amount = Number(value) || 0
  return new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: 'TWD',
    maximumFractionDigits: 2
  }).format(amount)
}

const loadLevels = async () => {
  loading.value = true
  try {
    const data = await memberLevelApi.listMemberLevels()
    if (Array.isArray(data)) {
      levels.value = data
    } else if (data && 'content' in data) {
      levels.value = data.content
    } else {
      levels.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入會員等級列表失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLevelColor = (order: number) => {
  const colors = ['brown', 'grey', 'amber', 'purple', 'blue', 'teal']
  return colors[Math.min(order - 1, colors.length - 1)]
}

const resetForm = () => {
  form.value = {
    name: '',
    levelOrder: 1,
    minSpendAmount: 0,
    discountRate: 1.0,
    pointsMultiplier: 1.0,
    description: '',
    iconUrl: '',
    enabled: true
  }
}

const handleEdit = (level: MemberLevel) => {
  form.value = { ...level }
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.levelOrder) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    if (form.value.id) {
      await memberLevelApi.updateMemberLevel(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await memberLevelApi.createMemberLevel(form.value)
      $q.notify({
        type: 'positive',
        message: '創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadLevels()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handleToggleEnabled = async (id?: number) => {
  if (!id) return
  
  try {
    await memberLevelApi.toggleEnabled(id)
    $q.notify({
      type: 'positive',
      message: '狀態更新成功',
      position: 'top'
    })
    loadLevels()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '狀態更新失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除這個會員等級嗎？此操作無法復原。`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await memberLevelApi.deleteMemberLevel(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadLevels()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '操作失敗',
        position: 'top'
      })
    }
  })
}

// 啟動會員等級管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startMemberLevelTour(true)
  })
}

onMounted(() => {
  loadLevels()
  
  // 如果用戶是第一次訪問會員等級管理頁面，自動啟動導覽
  if (!isMemberLevelTourCompleted()) {
    setTimeout(() => {
      startMemberLevelTour()
    }, 1500)
  }
})
</script>

<style scoped>
.level-metric-card {
  border-radius: 16px;
}

.level-metric-card__value {
  margin: 6px 0 4px;
  font-size: 1.45rem;
  font-weight: 700;
  line-height: 1.1;
  color: #0f172a;
}

.level-metric-card--green {
  border-color: rgba(34,197,94,.22);
  background: linear-gradient(180deg, rgba(240,253,244,.98), rgba(236,253,245,.96));
}

.level-metric-card--blue {
  border-color: rgba(59,130,246,.2);
  background: linear-gradient(180deg, rgba(239,246,255,.98), rgba(238,242,255,.96));
}

.level-metric-card--amber {
  border-color: rgba(245,158,11,.24);
  background: linear-gradient(180deg, rgba(255,251,235,.98), rgba(255,247,237,.96));
}

.level-table-card,
.level-dialog-card {
  border-radius: 18px;
}

.level-admin-table :deep(.q-table__top) {
  padding: 14px 16px 8px;
}

.level-actions-cell {
  min-width: 92px;
}

.level-row-icon-btn {
  margin-right: 4px;
}

.level-dialog-card__header {
  border-bottom: 1px solid rgba(148,163,184,.14);
}

.level-dialog-card__body {
  padding-top: 14px;
}

.level-dialog-card__actions {
  border-top: 1px solid rgba(148,163,184,.12);
}

.level-dialog-banner {
  background: linear-gradient(90deg, rgba(59,130,246,.08), rgba(34,197,94,.08));
  color: #0f172a;
  border: 1px solid rgba(59,130,246,.12);
}

@media (max-width: 1023px) {
  .level-admin-table :deep(.q-table__middle) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .level-admin-table :deep(table) {
    min-width: 940px;
  }
}

@media (max-width: 599px) {
  .level-metric-card__value {
    font-size: 1.28rem;
  }

  .level-dialog-card {
    min-width: unset !important;
    width: calc(100vw - 24px);
  }

  .level-dialog-card__actions {
    justify-content: stretch;
    gap: 8px;
  }

  .level-dialog-card__actions :deep(.q-btn) {
    flex: 1 1 0;
    min-height: 44px;
  }
}
</style>
