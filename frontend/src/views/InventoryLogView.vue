<template>
  <q-page class="q-pa-md">
    <div class="inventory-log-page">
      <div class="row items-center justify-between q-col-gutter-md q-mb-md">
        <div class="col">
          <div class="text-h5 text-weight-bold">庫存異動紀錄</div>
          <div class="text-caption text-grey-7">追蹤補貨、手動調整與商品編輯造成的庫存變化（最近 100 筆）</div>
        </div>
        <div class="col-auto row q-gutter-sm">
          <q-btn
            flat
            color="grey-7"
            icon="refresh"
            label="重新整理"
            :loading="loading"
            @click="loadLogs"
          />
        </div>
      </div>

      <q-card class="q-mb-md">
        <q-card-section class="row q-col-gutter-md items-end">
          <div class="col-12 col-md-3">
            <q-input
              v-model.number="filters.productId"
              type="number"
              label="商品 ID"
              outlined
              dense
              clearable
              name="inventory-log-product-id"
              autocomplete="off"
              inputmode="numeric"
            />
          </div>
          <div class="col-12 col-md-3">
            <q-select
              v-model="filters.changeType"
              :options="changeTypeOptions"
              label="異動類型"
              outlined
              dense
              clearable
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3">
            <q-select
              v-model="filters.source"
              :options="sourceOptions"
              label="來源"
              outlined
              dense
              clearable
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3">
            <q-input
              v-model="filters.keyword"
              label="備註關鍵字"
              outlined
              dense
              clearable
              name="inventory-log-keyword"
              autocomplete="off"
            />
          </div>
        </q-card-section>
        <q-separator />
        <q-card-section class="row items-center q-col-gutter-sm">
          <div class="col-auto">
            <q-chip color="primary" text-color="white" icon="inventory_2">
              顯示 {{ filteredLogs.length }} / {{ logs.length }} 筆
            </q-chip>
          </div>
          <div class="col-auto" v-if="activeFilterCount > 0">
            <q-chip color="orange-1" text-color="deep-orange-8" icon="filter_alt">
              已啟用 {{ activeFilterCount }} 個條件
            </q-chip>
          </div>
          <div class="col">
            <div class="text-caption text-grey-7">
              正值代表增加庫存，負值代表減少庫存；`SET` 為直接覆寫庫存值。
            </div>
          </div>
          <div class="col-auto">
            <q-btn flat color="grey-7" label="清除條件" @click="resetFilters" />
          </div>
        </q-card-section>
      </q-card>

      <q-card>
        <q-table
          flat
          :rows="filteredLogs"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :rows-per-page-options="[10, 20, 50, 100]"
          :pagination="{ rowsPerPage: 20, sortBy: 'createdAt', descending: true }"
        >
          <template #body-cell-createdAt="props">
            <q-td :props="props">
              <div class="text-body2">{{ formatDateTime(props.row.createdAt) }}</div>
              <div class="text-caption text-grey-6">ID #{{ props.row.id }}</div>
            </q-td>
          </template>

          <template #body-cell-product="props">
            <q-td :props="props">
              <div class="row items-center q-gutter-xs">
                <q-chip dense square color="blue-1" text-color="blue-9">#{{ props.row.productId }}</q-chip>
                <q-chip v-if="props.row.specificationId" dense square color="purple-1" text-color="purple-9">
                  規格 #{{ props.row.specificationId }}
                </q-chip>
              </div>
            </q-td>
          </template>

          <template #body-cell-changeType="props">
            <q-td :props="props">
              <q-badge :color="changeTypeColor(props.row.changeType)" rounded>
                {{ changeTypeLabel(props.row.changeType) }}
              </q-badge>
            </q-td>
          </template>

          <template #body-cell-changeQuantity="props">
            <q-td :props="props">
              <span :class="quantityClass(props.row.changeQuantity)">
                {{ signedNumber(props.row.changeQuantity) }}
              </span>
            </q-td>
          </template>

          <template #body-cell-stockFlow="props">
            <q-td :props="props">
              <div class="row items-center no-wrap q-gutter-xs">
                <q-chip dense color="grey-2" text-color="grey-9">{{ props.row.beforeStock }}</q-chip>
                <q-icon name="trending_flat" size="16px" color="grey-6" />
                <q-chip dense color="teal-1" text-color="teal-9">{{ props.row.afterStock }}</q-chip>
              </div>
            </q-td>
          </template>

          <template #body-cell-source="props">
            <q-td :props="props">
              <q-chip dense :color="sourceColor(props.row.source)" text-color="white">
                {{ props.row.source }}
              </q-chip>
            </q-td>
          </template>

          <template #body-cell-remark="props">
            <q-td :props="props">
              <div class="text-body2 ellipsis" style="max-width: 240px">
                {{ props.row.remark || '-' }}
              </div>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar, type QTableColumn } from 'quasar'
import { inventoryApi, type InventoryMovementLog } from '@/api/inventory'

type ChangeType = 'INCREASE' | 'DECREASE' | 'SET' | ''

interface Filters {
  productId: number | null
  changeType: ChangeType
  source: string
  keyword: string
}

const $q = useQuasar()

const loading = ref(false)
const logs = ref<InventoryMovementLog[]>([])
const filters = ref<Filters>({
  productId: null,
  changeType: '',
  source: '',
  keyword: ''
})

const changeTypeOptions = [
  { label: '增加庫存', value: 'INCREASE' },
  { label: '減少庫存', value: 'DECREASE' },
  { label: '直接設定', value: 'SET' }
]

const sourceOptions = [
  { label: '補貨 API', value: 'INVENTORY_API' },
  { label: '商品編輯', value: 'PRODUCT_EDIT' }
]

const columns: QTableColumn<InventoryMovementLog & { product?: unknown; stockFlow?: unknown }>[] = [
  { name: 'createdAt', label: '時間', field: 'createdAt', align: 'left', sortable: true },
  { name: 'product', label: '商品 / 規格', field: 'productId', align: 'left' },
  { name: 'warehouseId', label: '倉庫', field: (row) => row.warehouseId ?? '-', align: 'center' },
  { name: 'changeType', label: '異動類型', field: 'changeType', align: 'center' },
  { name: 'changeQuantity', label: '異動數量', field: 'changeQuantity', align: 'right', sortable: true },
  { name: 'stockFlow', label: '庫存變化', field: 'afterStock', align: 'left' },
  { name: 'source', label: '來源', field: 'source', align: 'center' },
  { name: 'remark', label: '備註', field: 'remark', align: 'left' }
]

const filteredLogs = computed(() => {
  const keyword = filters.value.keyword.trim().toLowerCase()

  return logs.value.filter((log) => {
    if (filters.value.productId != null && Number.isFinite(filters.value.productId)) {
      if (log.productId !== Number(filters.value.productId)) return false
    }
    if (filters.value.changeType && log.changeType !== filters.value.changeType) return false
    if (filters.value.source && log.source !== filters.value.source) return false
    if (keyword) {
      const haystack = `${log.remark ?? ''} ${log.source} ${log.changeType}`.toLowerCase()
      if (!haystack.includes(keyword)) return false
    }
    return true
  })
})

const activeFilterCount = computed(() => {
  let count = 0
  if (filters.value.productId != null && String(filters.value.productId) !== '') count++
  if (filters.value.changeType) count++
  if (filters.value.source) count++
  if (filters.value.keyword.trim()) count++
  return count
})

const loadLogs = async () => {
  loading.value = true
  try {
    const response = await inventoryApi.getInventoryLogs()
    logs.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('載入庫存異動紀錄失敗', error)
    $q.notify({
      type: 'negative',
      message: '載入庫存異動紀錄失敗，請稍後再試'
    })
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value = {
    productId: null,
    changeType: '',
    source: '',
    keyword: ''
  }
}

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return new Intl.DateTimeFormat('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).format(date)
}

const signedNumber = (value?: number) => {
  const num = Number(value ?? 0)
  if (num > 0) return `+${num}`
  return `${num}`
}

const quantityClass = (value?: number) => {
  const num = Number(value ?? 0)
  if (num > 0) return 'text-positive text-weight-bold'
  if (num < 0) return 'text-negative text-weight-bold'
  return 'text-grey-7'
}

const changeTypeLabel = (type?: string) => {
  switch (type) {
    case 'INCREASE':
      return '增加'
    case 'DECREASE':
      return '減少'
    case 'SET':
      return '設定'
    default:
      return type || '-'
  }
}

const changeTypeColor = (type?: string) => {
  switch (type) {
    case 'INCREASE':
      return 'positive'
    case 'DECREASE':
      return 'negative'
    case 'SET':
      return 'primary'
    default:
      return 'grey-6'
  }
}

const sourceColor = (source?: string) => {
  switch (source) {
    case 'INVENTORY_API':
      return 'teal'
    case 'PRODUCT_EDIT':
      return 'indigo'
    default:
      return 'grey-7'
  }
}

onMounted(() => {
  void loadLogs()
})
</script>

<style scoped>
.inventory-log-page {
  max-width: 1280px;
  margin: 0 auto;
}

@media (prefers-reduced-motion: reduce) {
  * {
    transition: none !important;
  }
}
</style>
