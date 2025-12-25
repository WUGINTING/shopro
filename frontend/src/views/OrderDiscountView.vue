<template>
  <q-page class="q-pa-md">
    <div class="order-discount-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">订单折扣管理</div>
          <div class="text-caption text-grey-7">管理订单折扣和优惠代码</div>
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
                label="订单ID"
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
                label="折扣代码"
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
              <q-btn label="清除筛选" outline color="grey-7" @click="clearFilters" />
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
                <q-tooltip>删除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add Discount Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">新增订单折扣</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <q-input
                v-model.number="form.orderId"
                label="订单ID *"
                outlined
                type="number"
                class="q-mb-md"
                :rules="[val => !!val || '请输入订单ID']"
              />

              <q-select
                v-model="form.discountType"
                label="折扣类型 *"
                outlined
                :options="discountTypeOptions"
                class="q-mb-md"
                :rules="[val => !!val || '请选择折扣类型']"
              />

              <q-input
                v-model="form.discountCode"
                label="折扣代码"
                outlined
                class="q-mb-md"
              />

              <q-input
                v-model.number="form.discountAmount"
                label="折扣金额 *"
                outlined
                type="number"
                step="0.01"
                class="q-mb-md"
                prefix="¥"
                :rules="[val => val >= 0 || '折扣金额不能小于0']"
              />

              <q-input
                v-model.number="form.discountPercentage"
                label="折扣百分比"
                outlined
                type="number"
                step="0.01"
                class="q-mb-md"
                suffix="%"
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
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
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
  { name: 'orderId', label: '订单ID', align: 'left' as const, field: 'orderId', sortable: true },
  { name: 'discountType', label: '折扣类型', align: 'center' as const, field: 'discountType' },
  { name: 'discountCode', label: '折扣代码', align: 'left' as const, field: 'discountCode' },
  { name: 'discountAmount', label: '折扣金额', align: 'left' as const, field: 'discountAmount', sortable: true },
  { name: 'discountPercentage', label: '折扣百分比', align: 'center' as const, field: 'discountPercentage' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'createdAt', label: '创建时间', align: 'left' as const, field: 'createdAt' },
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
      message: '查询失败',
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
      message: '查询失败',
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
      message: '请填写必填字段',
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
      message: '添加失败',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '确认删除',
    message: '确定要删除这条折扣记录吗？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderDiscountApi.deleteDiscount(id)
      $q.notify({
        type: 'positive',
        message: '删除成功',
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
        message: '删除失败',
        position: 'top'
      })
    }
  })
}
</script>

<style scoped>
.order-discount-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
