<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">分類管理</div>
          <div class="text-caption text-grey-7">管理商品分類、支援多層級結構（100 ~ 600 個分類）</div>
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
            <q-tooltip>分類管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="add"
            label="新增分類"
            unelevated
            @click="handleAdd"
          />
        </div>
      </div>

      <!-- Search and Filter Bar -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6 col-md-4">
              <q-input
                v-model="searchQuery"
                outlined
                dense
                placeholder="搜尋分類名稱"
                clearable
              >
                <template v-slot:prepend>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="statusFilter"
                outlined
                dense
                :options="statusOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                clearable
                placeholder="啟用狀態"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="parentFilter"
                outlined
                dense
                :options="parentOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                clearable
                placeholder="父分類"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-2">
              <q-btn color="primary" unelevated class="full-width" label="搜尋" icon="search" @click="loadCategories" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Categories Table -->
      <q-card>
        <q-table
          :rows="categories"
          :columns="columns"
          row-key="id"
          :loading="loading"
          flat
          :filter="searchQuery"
          :filter-method="filterCategories"
        >
          <template v-slot:body-cell-enabled="props">
            <q-td :props="props">
              <q-badge :color="props.row.enabled ? 'positive' : 'grey'" :label="props.row.enabled ? '啟用' : '停用'" />
            </q-td>
          </template>

          <template v-slot:body-cell-parentId="props">
            <q-td :props="props">
              <span v-if="props.row.parentId">{{ getParentName(props.row.parentId) }}</span>
              <span v-else class="text-grey-6">頂層分類</span>
            </q-td>
          </template>

          <template v-slot:body-cell-sortOrder="props">
            <q-td :props="props">
              {{ props.row.sortOrder || 0 }}
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯分類' : '新增分類' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <q-input
                v-model="form.name"
                label="分類名稱 *"
                outlined
                class="q-mb-md"
                :rules="[val => !!val || '請輸入分類名稱']"
              />

              <q-select
                v-model="form.parentId"
                label="父分類"
                outlined
                :options="parentCategoryOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                clearable
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="category" />
                </template>
              </q-select>

              <q-input
                v-model="form.description"
                label="分類描述"
                outlined
                type="textarea"
                rows="3"
                class="q-mb-md"
              />

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-input
                    v-model.number="form.sortOrder"
                    label="排序"
                    outlined
                    type="number"
                    :rules="[val => val >= 0 || '排序必須大於等於 0']"
                  />
                </div>
                <div class="col-6">
                  <q-toggle
                    v-model="form.enabled"
                    label="啟用"
                    color="positive"
                  />
                </div>
              </div>

              <q-card-actions align="right" class="q-px-none">
                <q-btn flat label="取消" color="grey-7" @click="closeDialog" />
                <q-btn unelevated label="儲存" color="primary" type="submit" />
              </q-card-actions>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { categoryApi, type ProductCategory } from '@/api'
import { startCategoryTour, isCategoryTourCompleted } from '@/utils/categoryTour'

const $q = useQuasar()

const categories = ref<ProductCategory[]>([])
const loading = ref(false)
const showDialog = ref(false)
const searchQuery = ref('')
const statusFilter = ref<boolean | null>(null)
const parentFilter = ref<number | null>(null)

const form = ref<ProductCategory>({
  name: '',
  description: '',
  parentId: null,
  sortOrder: 0,
  enabled: true
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '分類名稱', align: 'left' as const, field: 'name', sortable: true },
  { name: 'parentId', label: '父分類', align: 'left' as const, field: 'parentId' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'sortOrder', label: '排序', align: 'center' as const, field: 'sortOrder', sortable: true },
  { name: 'enabled', label: '狀態', align: 'center' as const, field: 'enabled' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '啟用', value: true },
  { label: '停用', value: false }
]

const parentOptions = computed(() => {
  const options = [{ label: '頂層分類', value: null }]
  categories.value.forEach(cat => {
    if (cat.id) {
      options.push({ label: cat.name, value: cat.id })
    }
  })
  return options
})

const parentCategoryOptions = computed(() => {
  const options = [{ label: '無（頂層分類）', value: null }]
  categories.value.forEach(cat => {
    // 編輯時，排除自己和自己的子分類（避免循環引用）
    if (cat.id && cat.id !== form.value.id) {
      options.push({ label: cat.name, value: cat.id })
    }
  })
  return options
})

const loadCategories = async () => {
  loading.value = true
  try {
    const response = await categoryApi.getAllCategories()
    if (response.success && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入分類清單失敗',
      position: 'top'
    })
    console.error('API Error:', error)
  } finally {
    loading.value = false
  }
}

const filterCategories = (rows: ProductCategory[], terms: string) => {
  if (!terms) return rows

  const lowerTerms = terms.toLowerCase()
  return rows.filter(row => {
    return row.name.toLowerCase().includes(lowerTerms) ||
           (row.description && row.description.toLowerCase().includes(lowerTerms))
  })
}

const getParentName = (parentId: number): string => {
  const parent = categories.value.find(cat => cat.id === parentId)
  return parent?.name || ''
}

const handleAdd = () => {
  form.value = {
    name: '',
    description: '',
    parentId: null,
    sortOrder: 0,
    enabled: true
  }
  showDialog.value = true
}

const handleEdit = (category: ProductCategory) => {
  form.value = { ...category }
  showDialog.value = true
}

const handleDelete = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這個分類嗎？此操作無法復原。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await categoryApi.deleteCategory(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadCategories()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗',
        position: 'top'
      })
      console.error('Delete Error:', error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await categoryApi.updateCategory(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await categoryApi.createCategory(form.value)
      $q.notify({
        type: 'positive',
        message: '創建成功',
        position: 'top'
      })
    }
    closeDialog()
    loadCategories()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
    console.error('Submit Error:', error)
  }
}

const closeDialog = () => {
  showDialog.value = false
  form.value = {
    name: '',
    description: '',
    parentId: null,
    sortOrder: 0,
    enabled: true
  }
}

// 啟動分類管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startCategoryTour(true)
  })
}

onMounted(() => {
  loadCategories()
  
  // 如果用戶是第一次訪問分類管理頁面，自動啟動導覽
  if (!isCategoryTourCompleted()) {
    setTimeout(() => {
      startCategoryTour()
    }, 1500)
  }
})
</script>

