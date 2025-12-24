<template>
  <q-page class="q-pa-md">
    <div class="order-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">订单管理</div>
          <div class="text-caption text-grey-7">管理订单状态和发货信息</div>
        </div>
      </div>

      <!-- Orders Table -->
      <q-card>
        <q-table
          :rows="orders"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-orderNumber="props">
            <q-td :props="props">
              <span class="text-weight-bold">{{ props.row.orderNumber }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-totalAmount="props">
            <q-td :props="props">
              <span class="text-weight-bold text-primary">¥{{ props.row.totalAmount.toFixed(2) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn-dropdown flat dense color="primary" label="更新状态" size="sm">
                <q-list>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'PROCESSING')">
                    <q-item-section>
                      <q-item-label>处理中</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'SHIPPED')">
                    <q-item-section>
                      <q-item-label>已发货</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'DELIVERED')">
                    <q-item-section>
                      <q-item-label>已送达</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-separator />
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'CANCELLED')">
                    <q-item-section>
                      <q-item-label class="text-negative">已取消</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>
              
              <q-btn flat dense round icon="visibility" color="primary" size="sm">
                <q-tooltip>查看详情</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { orderApi, type Order } from '@/api'

const $q = useQuasar()

const orders = ref<Order[]>([])
const loading = ref(false)

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderNumber', label: '订单号', align: 'left' as const, field: 'orderNumber' },
  { name: 'customerName', label: '客户', align: 'left' as const, field: 'customerName' },
  { name: 'totalAmount', label: '总金额', align: 'left' as const, field: 'totalAmount', sortable: true },
  { name: 'status', label: '状态', align: 'center' as const, field: 'status' },
  { name: 'createdAt', label: '创建时间', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderApi.getOrders()
    if (response.data && Array.isArray((response.data as any).content)) {
      orders.value = (response.data as any).content
    } else if (Array.isArray(response.data)) {
      orders.value = response.data
    } else {
      orders.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '加载订单列表失败',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusColor = (status: Order['status']) => {
  const colorMap = {
    PENDING: 'grey',
    PROCESSING: 'warning',
    SHIPPED: 'info',
    DELIVERED: 'positive',
    CANCELLED: 'negative'
  }
  return colorMap[status] || 'grey'
}

const getStatusLabel = (status: Order['status']) => {
  const labelMap = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    SHIPPED: '已发货',
    DELIVERED: '已送达',
    CANCELLED: '已取消'
  }
  return labelMap[status] || status
}

const handleStatusChange = async (id?: number, status?: Order['status']) => {
  if (!id || !status) return
  
  // 对于取消状态，需要确认
  if (status === 'CANCELLED') {
    $q.dialog({
      title: '警告',
      message: '确定要取消此订单吗？此操作不可恢复。',
      cancel: true,
      persistent: true
    }).onOk(async () => {
      await updateStatus(id, status)
    })
  } else {
    await updateStatus(id, status)
  }
}

const updateStatus = async (id: number, status: Order['status']) => {
  try {
    await orderApi.updateOrderStatus(id, status)
    $q.notify({
      type: 'positive',
      message: '状态更新成功',
      position: 'top'
    })
    loadOrders()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '状态更新失败',
      position: 'top'
    })
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
