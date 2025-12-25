<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none">積點管理</h4>
      </div>
      <div class="col-auto">
        <q-btn
          color="primary"
          label="批次發放積點"
          icon="add"
          @click="showBatchDialog"
        />
      </div>
    </div>

    <!-- 統計卡片 -->
    <div class="row q-col-gutter-md q-mb-md">
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
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-input
              v-model.number="searchForm.memberId"
              label="會員 ID"
              outlined
              dense
              type="number"
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-select
              v-model="searchForm.pointType"
              label="積點類型"
              :options="['EARN', 'PURCHASE', 'REWARD', 'REDEEM', 'EXPIRE']"
              outlined
              dense
              clearable
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
    <q-card>
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        :rows="records"
        :columns="columns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
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
            <q-badge :label="props.row.pointType" :color="getTypeColor(props.row.pointType)" />
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
      <q-card style="width: 500px; max-width: 90vw">
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
            <p class="text-caption text-grey-7 q-mb-md">
              選擇要發放的會員（可複選）
            </p>

            <q-option-group
              v-model="batchData.memberIds"
              :options="memberOptions"
              color="primary"
              inline
              class="q-mb-md"
            />

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
import { pointApi, type PointRecord } from '@/api/point'

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

// 對話框
const showBatchGrantDialog = ref(false)

// 批次發放資料
const batchData = ref({
  memberIds: [] as number[],
  points: 0,
  reason: ''
})

// 會員選項
const memberOptions = computed(() => [
  { label: '會員 1', value: 1 },
  { label: '會員 2', value: 2 },
  { label: '會員 3', value: 3 }
])

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
    EXPIRE: 'grey'
  }
  return colors[type] || 'grey'
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
    const result = await pointApi.searchPoints(
      searchForm.value.memberId,
      searchForm.value.pointType,
      pagination.value.page,
      pagination.value.rowsPerPage
    )
    records.value = result.content
    pagination.value.rowsNumber = result.totalElements
    
    // 更新統計資料
    stats.value.totalRecords = result.totalElements
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入積點紀錄失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  loadRecords()
})
</script>
