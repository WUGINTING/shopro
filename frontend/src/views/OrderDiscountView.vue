<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">訂單折扣管理</div>
          <div class="text-caption text-grey-7">管理訂單折扣和優惠代碼</div>
        </div>
        <q-btn
          color="primary"
          icon="add_circle"
          label="新增折扣"
          unelevated
          @click="showDialog = true; resetForm()"
        />
      </div>

      <!-- Search Filters -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
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
              <q-input
                v-model="searchDiscountCode"
                label="折扣代碼"
                outlined
                dense
                clearable
                @keyup.enter="searchByDiscountCode"
              >
                <template v-slot:append>
                  <q-btn flat dense icon="search" @click="searchByDiscountCode" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-4">
              <q-btn label="清除篩選" outline color="grey-7" @click="clearFilters" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Discounts Table -->
      <q-card>
        <q-table
          :rows="discounts"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-discountAmount="props">
            <q-td :props="props">
              <span class="text-weight-bold text-primary">¥{{ props.row.discountAmount?.toFixed(2) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-discountPercentage="props">
            <q-td :props="props">
              <span v-if="props.row.discountPercentage">{{ props.row.discountPercentage }}%</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-discountType="props">
            <q-td :props="props">
              <q-badge :color="getDiscountTypeColor(props.row.discountType)">
                {{ props.row.discountType }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add Discount Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">新增訂單折扣</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <q-input
                v-model.number="form.orderId"
                label="訂單ID *"
                outlined
                type="number"
                class="q-mb-md"
                :rules="[val => !!val || '請輸入訂單ID']"
              />

              <q-select
                v-model="form.discountType"
                label="折扣類型 *"
                outlined
                :options="discountTypeOptions"
                class="q-mb-md"
                :rules="[val => !!val || '請選擇折扣類型']"
              />

              <q-input
                v-model="form.discountCode"
                label="折扣代碼"
                outlined
                class="q-mb-md"
              />

              <q-input
                v-model.number="form.discountAmount"
                label="折扣金額 *"
                outlined
                type="number"
                step="0.01"
                class="q-mb-md"
                prefix="¥"
                :rules="[val => val >= 0 || '折扣金額不能小於0']"
              />

              <q-input
                v-model.number="form.discountPercentage"
                label="折扣百分比"
                outlined
                type="number"
                step="0.01"
                class="q-mb-md"
                suffix="%"
                :rules="[
                  val => val === undefined || val === null || (val >= 0 && val <= 100) || '折扣百分比必須在0到10之間'
                ]"
              />

              <q-input
                v-model="form.description"
                label="折扣描述"
                outlined
                type="textarea"
                rows="3"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="儲存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { orderDiscountApi, type OrderDiscount } from '@/api'

const $q = useQuasar()

const discounts = ref<OrderDiscount[]>([])
const loading = ref(false)
const showDialog = ref(false)
const searchOrderId = ref('')
const searchDiscountCode = ref('')

const form = ref<OrderDiscount>({
  orderId: 0,
  discountType: '',
  discountCode: '',
  discountAmount: 0,
  discountPercentage: 0,
  description: ''
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderId', label: '訂單ID', align: 'left' as const, field: 'orderId', sortable: true },
  { name: 'discountType', label: '折扣類型', align: 'center' as const, field: 'discountType' },
  { name: 'discountCode', label: '折扣代碼', align: 'left' as const, field: 'discountCode' },
  { name: 'discountAmount', label: '折扣金額', align: 'left' as const, field: 'discountAmount', sortable: true },
  { name: 'discountPercentage', label: '折扣百分比', align: 'center' as const, field: 'discountPercentage' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'createdAt', label: '創建時間', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const discountTypeOptions = ['COUPON', 'PROMOTION', 'MEMBER_DISCOUNT', 'SEASONAL', 'BULK_ORDER', 'OTHER']

const resetForm = () => {
  form.value = {
    orderId: 0,
    discountType: '',
    discountCode: '',
    discountAmount: 0,
    discountPercentage: 0,
    description: ''
  }
}

const searchByOrderId = async () => {
  if (!searchOrderId.value) return
  loading.value = true
  try {
    const response = await orderDiscountApi.getDiscountsByOrderId(Number(searchOrderId.value))
    discounts.value = response.data
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

const searchByDiscountCode = async () => {
  if (!searchDiscountCode.value) return
  loading.value = true
  try {
    const response = await orderDiscountApi.findByDiscountCode(searchDiscountCode.value)
    discounts.value = response.data
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

const clearFilters = () => {
  searchOrderId.value = ''
  searchDiscountCode.value = ''
  discounts.value = []
}

const getDiscountTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    COUPON: 'purple',
    PROMOTION: 'orange',
    MEMBER_DISCOUNT: 'teal',
    SEASONAL: 'pink',
    BULK_ORDER: 'blue',
    OTHER: 'grey'
  }
  return colorMap[type] || 'grey'
}

const handleSubmit = async () => {
  if (!form.value.orderId || !form.value.discountType || form.value.discountAmount === undefined) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    await orderDiscountApi.addDiscount(form.value)
    $q.notify({
      type: 'positive',
      message: '折扣添加成功',
      position: 'top'
    })
    showDialog.value = false
    resetForm()
    // Refresh list if we have a search active
    if (searchOrderId.value) {
      searchByOrderId()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '添加失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這條折扣記錄嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderDiscountApi.deleteDiscount(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      // Refresh list if we have a search active
      if (searchOrderId.value) {
        searchByOrderId()
      } else if (searchDiscountCode.value) {
        searchByDiscountCode()
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
