<template>
  <div class="order-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>订单管理 / Order Management</h2>
        </div>
      </template>

      <el-table :data="orders" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户" width="150" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="scope">
            ${{ scope.row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-dropdown @command="(cmd: string) => handleStatusChange(scope.row.id, cmd as Order['status'])">
              <el-button size="small">
                更新状态 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="PROCESSING">处理中</el-dropdown-item>
                  <el-dropdown-item command="SHIPPED">已发货</el-dropdown-item>
                  <el-dropdown-item command="DELIVERED">已送达</el-dropdown-item>
                  <el-dropdown-item command="CANCELLED">已取消</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button size="small" @click="handleViewDetails(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { orderApi, type Order } from '@/api'

const orders = ref<Order[]>([])
const loading = ref(false)

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderApi.getOrders()
    orders.value = response.data || []
  } catch (error) {
    ElMessage.error('加载订单列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: Order['status']) => {
  const typeMap = {
    PENDING: 'info',
    PROCESSING: 'warning',
    SHIPPED: 'primary',
    DELIVERED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: Order['status']) => {
  const textMap = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    SHIPPED: '已发货',
    DELIVERED: '已送达',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const handleStatusChange = async (id?: number, status?: Order['status']) => {
  if (!id || !status) return
  try {
    await orderApi.updateOrderStatus(id, status)
    ElMessage.success('状态更新成功')
    loadOrders()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleViewDetails = (order: Order) => {
  ElMessage.info(`查看订单 ${order.orderNumber} 的详情`)
  // 可以导航到详情页面或打开对话框
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-view {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}
</style>
