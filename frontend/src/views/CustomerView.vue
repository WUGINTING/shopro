<template>
  <div class="customer-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>顾客管理 / Customer Management (CRM)</h2>
          <el-button type="primary" @click="dialogVisible = true">新增顾客</el-button>
        </div>
      </template>

      <el-table :data="customers" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="memberLevel" label="会员等级" width="120">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.memberLevel)">
              {{ scope.row.memberLevel || 'BRONZE' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="totalSpent" label="总消费" width="120">
          <template #default="scope">
            ${{ (scope.row.totalSpent || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleAddPoints(scope.row)">加积分</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="顾客信息" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="form.memberLevel" placeholder="请选择">
            <el-option label="铜牌" value="BRONZE" />
            <el-option label="银牌" value="SILVER" />
            <el-option label="金牌" value="GOLD" />
            <el-option label="白金" value="PLATINUM" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pointsDialogVisible" title="增加积分" width="400px">
      <el-form :model="pointsForm" label-width="100px">
        <el-form-item label="积分数量">
          <el-input-number v-model="pointsForm.points" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePointsSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { crmApi, type Customer } from '@/api'

const customers = ref<Customer[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const pointsDialogVisible = ref(false)
const form = ref<Customer>({
  name: '',
  email: '',
  phone: '',
  memberLevel: 'BRONZE'
})
const pointsForm = ref({
  customerId: 0,
  points: 0
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await crmApi.getCustomers()
    customers.value = response.data || []
  } catch (error) {
    ElMessage.error('加载顾客列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLevelType = (level?: Customer['memberLevel']) => {
  const typeMap = {
    BRONZE: 'info',
    SILVER: '',
    GOLD: 'warning',
    PLATINUM: 'success'
  }
  return typeMap[level || 'BRONZE']
}

const handleEdit = (customer: Customer) => {
  form.value = { ...customer }
  dialogVisible.value = true
}

const handleAddPoints = (customer: Customer) => {
  pointsForm.value.customerId = customer.id || 0
  pointsForm.value.points = 0
  pointsDialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await crmApi.updateCustomer(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await crmApi.createCustomer(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadCustomers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handlePointsSubmit = async () => {
  try {
    await crmApi.updateCustomerPoints(pointsForm.value.customerId, pointsForm.value.points)
    ElMessage.success('积分添加成功')
    pointsDialogVisible.value = false
    pointsForm.value = { customerId: 0, points: 0 }
    loadCustomers()
  } catch (error) {
    ElMessage.error('积分添加失败')
  }
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.customer-view {
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
