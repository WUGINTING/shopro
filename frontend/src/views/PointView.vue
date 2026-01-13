<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none" data-tour="title">積點管理</h4>
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
            <q-tooltip>積點管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            label="批次發放積點"
            icon="add"
            data-tour="batch-grant-btn"
            @click="showBatchDialog"
          />
        </div>
      </div>
    </div>

    <!-- 統計卡片 -->
    <div class="row q-col-gutter-md q-mb-md" data-tour="stats-cards">
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-primary q-mb-xs">
              總發放積點
            </div>
            <div class="text-h4">{{ stats.totalEarned || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-warning q-mb-xs">
              已兌換積點
            </div>
            <div class="text-h4">{{ stats.totalRedeemed || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-info q-mb-xs">
              待過期積點
            </div>
            <div class="text-h4">{{ stats.expiringPoints || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-success q-mb-xs">
              活動記錄
            </div>
            <div class="text-h4">{{ stats.totalRecords || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- 搜尋欄 -->
    <q-card class="q-mb-md" data-tour="search-card">
      <q-card-section>
        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-select
              v-model="searchForm.memberId"
              label="選擇會員（留空顯示全部）"
              :options="memberOptions"
              option-label="label"
              option-value="value"
              outlined
              dense
              clearable
              map-options
              emit-value
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-select
              v-model="searchForm.pointType"
              label="積點類型"
              :options="pointTypeOptions"
              option-label="label"
              option-value="value"
              outlined
              dense
              clearable
              map-options
              emit-value
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12">
            <q-btn
              color="primary"
              label="重置"
              outline
              @click="resetSearch"
            />
          </div>
        </div>
      </q-card-section>
    </q-card>

    <!-- 積點紀錄列表 -->
    <q-card data-tour="points-table">
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        :rows="records"
        :columns="columns"
        row-key="id"
        v-model:pagination="pagination"
        :loading="loading"
        @request="onRequest"
        flat
        bordered
      >
        <template #body-cell-points="props">
          <q-td :props="props">
            <span :class="getPointsClass(props.row.pointType)">
              {{ getPointsSign(props.row.pointType) }}{{ props.row.points }}
            </span>
          </q-td>
        </template>

        <template #body-cell-pointType="props">
          <q-td :props="props">
            <q-badge :label="getTypeLabel(props.row.pointType)" :color="getTypeColor(props.row.pointType)" />
          </q-td>
        </template>

        <template #body-cell-createdAt="props">
          <q-td :props="props">
            {{ formatDate(props.row.createdAt) }}
          </q-td>
        </template>

        <template #no-data>
          <div class="text-center q-py-lg text-grey-7">
            沒有積點紀錄
          </div>
        </template>
      </q-table>
    </q-card>

    <!-- 批次發放對話框 -->
    <q-dialog v-model="showBatchGrantDialog">
      <q-card style="width: 500px; max-width: 90vw" data-tour="batch-dialog">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">批次發放積點</div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showBatchGrantDialog = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pt-none">
          <q-form ref="batchForm" @submit="executeBatchGrant">
            <q-select
              v-model="batchData.memberIds"
              :options="memberOptions"
              option-label="label"
              option-value="value"
              label="選擇會員（可複選） *"
              outlined
              dense
              multiple
              use-chips
              use-input
              input-debounce="0"
              map-options
              emit-value
              @filter="filterMembers"
              class="q-mb-md"
              :rules="[val => val && val.length > 0 || '請至少選擇一個會員']"
            >
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey">
                    無會員資料
                  </q-item-section>
                </q-item>
              </template>
            </q-select>

            <q-input
              v-model.number="batchData.points"
              label="發放積點數 *"
              outlined
              dense
              type="number"
              min="1"
              class="q-mb-md"
              :rules="[val => val && val > 0 || '請輸入正整數']"
            />

            <q-input
              v-model="batchData.reason"
              label="發放原因 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入發放原因']"
            />

            <div class="row q-gutter-md">
              <q-btn
                label="取消"
                flat
                @click="showBatchGrantDialog = false"
              />
              <q-btn
                label="發放"
                color="primary"
                type="submit"
                :loading="batchLoading"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { pointApi, memberApi, type PointRecord } from '@/api'
import { startPointTour, isPointTourCompleted } from '@/utils/pointTour'

const $q = useQuasar()
const batchForm = ref()

// 資料
const records = ref<PointRecord[]>([])
const pagination = ref({
  page: 0,
  rowsPerPage: 20,
  rowsNumber: 0
})
const loading = ref(false)
const batchLoading = ref(false)

// 統計資料
const stats = ref({
  totalEarned: 0,
  totalRedeemed: 0,
  expiringPoints: 0,
  totalRecords: 0
})

// 搜尋表單
const searchForm = ref({
  memberId: undefined as number | undefined,
  pointType: undefined as string | undefined
})

// 積點類型選項
const pointTypeOptions = [
  { label: '獲得', value: 'EARN' },
  { label: '購買', value: 'PURCHASE' },
  { label: '獎勵', value: 'REWARD' },
  { label: '兌換', value: 'REDEEM' },
  { label: '過期', value: 'EXPIRE' },
  { label: '調整', value: 'ADJUST' }
]

// 對話框
const showBatchGrantDialog = ref(false)

// 批次發放資料
const batchData = ref({
  memberIds: [] as number[],
  points: 0,
  reason: ''
})

// 會員選項
const memberOptions = ref<Array<{ label: string; value: number }>>([])
const allMembers = ref<Array<{ label: string; value: number }>>([])

// 載入會員列表
const loadMembers = async () => {
  try {
    const result = await memberApi.getMembers({ page: 0, size: 100 })
    allMembers.value = (result.content || []).map((member: { id?: number; name: string }) => ({
      label: `${member.name} (ID: ${member.id})`,
      value: member.id!
    }))
    memberOptions.value = allMembers.value
  } catch (error) {
    console.error('載入會員列表失敗', error)
    $q.notify({
      type: 'negative',
      message: '載入會員列表失敗',
      position: 'top'
    })
  }
}

// 篩選會員
const filterMembers = (val: string, update: (callback: () => void) => void) => {
  if (val === '') {
    update(() => {
      memberOptions.value = allMembers.value
    })
    return
  }

  update(() => {
    const needle = val.toLowerCase()
    memberOptions.value = allMembers.value.filter(
      v => v.label.toLowerCase().indexOf(needle) > -1
    )
  })
}

// 表格列定義
const columns = [
  { name: 'memberId', label: '會員 ID', field: 'memberId', align: 'left' },
  { name: 'points', label: '積點', field: 'points', align: 'right' },
  { name: 'pointType', label: '類型', field: 'pointType', align: 'center' },
  { name: 'reason', label: '原因', field: 'reason', align: 'left' },
  { name: 'balanceAfter', label: '結餘', field: 'balanceAfter', align: 'right' },
  { name: 'createdAt', label: '日期', field: 'createdAt', align: 'center' }
]

// 獲取積點符號
const getPointsSign = (type: string) => {
  return ['REDEEM', 'EXPIRE'].includes(type) ? '-' : '+'
}

// 獲取積點樣式類別
const getPointsClass = (type: string) => {
  if (['REDEEM', 'EXPIRE'].includes(type)) {
    return 'text-negative text-weight-bold'
  }
  return 'text-positive text-weight-bold'
}

// 獲取類型顏色
const getTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    EARN: 'green',
    PURCHASE: 'blue',
    REWARD: 'orange',
    REDEEM: 'red',
    EXPIRE: 'grey',
    ADJUST: 'purple'
  }
  return colors[type] || 'grey'
}

// 獲取類型中文標籤
const getTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    EARN: '獲得',
    PURCHASE: '購買',
    REWARD: '獎勵',
    REDEEM: '兌換',
    EXPIRE: '過期',
    ADJUST: '調整'
  }
  return labels[type] || type
}

// 格式化日期
const formatDate = (date?: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-TW')
}

// 載入積點紀錄
const loadRecords = async () => {
  loading.value = true
  try {
    let result
    // 如果有選擇會員，查詢該會員的積點紀錄；否則查詢所有積點紀錄
    if (searchForm.value.memberId) {
      result = await pointApi.getMemberPoints(
        searchForm.value.memberId,
        pagination.value.page,
        pagination.value.rowsPerPage
      )
    } else {
      result = await pointApi.getAllPoints(
        pagination.value.page,
        pagination.value.rowsPerPage
      )
    }
    
    // 處理 Spring Data Page 格式或自定義 PageResponse 格式
    const content = result?.content || []
    const totalElements = result?.totalElements || 0
    
    // 如果指定了積點類型，進行前端篩選
    let filteredRecords = content
    if (searchForm.value.pointType) {
      filteredRecords = content.filter(
        record => record.pointType === searchForm.value.pointType
      )
    }
    
    records.value = filteredRecords
    pagination.value.rowsNumber = totalElements
    
    // 更新統計資料
    stats.value.totalRecords = totalElements
    
    // 計算統計資料（使用所有記錄，不只是當前頁）
    if (content.length > 0) {
      stats.value.totalEarned = content
        .filter(r => ['EARN', 'PURCHASE', 'REWARD', 'BATCH_GRANT'].includes(r.pointType))
        .reduce((sum, r) => sum + (r.points || 0), 0)
      stats.value.totalRedeemed = content
        .filter(r => r.pointType === 'REDEEM')
        .reduce((sum, r) => sum + Math.abs(r.points || 0), 0)
    }
  } catch (error: any) {
    console.error('載入積點紀錄錯誤:', error)
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || error?.message || '載入積點紀錄失敗',
      position: 'top'
    })
    records.value = []
    pagination.value.rowsNumber = 0
  } finally {
    loading.value = false
  }
}

// 表格分頁請求處理
const onRequest = (props: any) => {
  pagination.value.page = props.pagination.page
  pagination.value.rowsPerPage = props.pagination.rowsPerPage
  loadRecords()
}

// 搜尋
const onSearch = () => {
  pagination.value.page = 0
  loadRecords()
}

// 重置搜尋
const resetSearch = () => {
  searchForm.value = { memberId: undefined, pointType: undefined }
  pagination.value.page = 0
  loadRecords()
}

// 顯示批次發放對話框
const showBatchDialog = () => {
  batchData.value = { memberIds: [], points: 0, reason: '' }
  showBatchGrantDialog.value = true
}

// 執行批次發放
const executeBatchGrant = async () => {
  batchLoading.value = true
  try {
    await pointApi.batchGrant({
      memberIds: batchData.value.memberIds,
      points: batchData.value.points,
      reason: batchData.value.reason
    })
    $q.notify({
      type: 'positive',
      message: `已發放 ${batchData.value.points} 積點給 ${batchData.value.memberIds.length} 位會員`,
      position: 'top'
    })
    showBatchGrantDialog.value = false
    loadRecords()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '發放失敗',
      position: 'top'
    })
  } finally {
    batchLoading.value = false
  }
}

// 啟動導覽
const handleStartTour = () => {
  startPointTour(true)
}

onMounted(async () => {
  // 確保初始狀態：沒有選擇會員
  searchForm.value.memberId = undefined
  searchForm.value.pointType = undefined
  pagination.value.page = 0
  
  // 載入會員列表
  await loadMembers()
  
  // 自動載入所有積點紀錄
  await loadRecords()
  
  // 如果是第一次訪問，自動啟動導覽
  if (!isPointTourCompleted()) {
    setTimeout(() => {
      startPointTour()
    }, 500)
  }
})
</script>
