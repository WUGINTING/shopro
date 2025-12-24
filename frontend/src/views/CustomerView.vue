<template>
  <q-page class="q-pa-md">
    <div class="customer-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">客户管理 (CRM)</div>
          <div class="text-caption text-grey-7">管理客户信息、会员等级和积分</div>
        </div>
        <q-btn
          color="primary"
          icon="person_add"
          label="新增客户"
          unelevated
          @click="showDialog = true; form = { name: '', email: '', phone: '', memberLevel: 'BRONZE' }"
        />
      </div>

      <!-- Customers Table -->
      <q-card>
        <q-table
          :rows="customers"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-memberLevel="props">
            <q-td :props="props">
              <q-badge :color="getLevelColor(props.row.memberLevel)">
                {{ props.row.memberLevel || 'BRONZE' }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-points="props">
            <q-td :props="props">
              <q-chip color="amber" text-color="white" icon="stars">
                {{ props.row.points || 0 }}
              </q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-totalSpent="props">
            <q-td :props="props">
              <span class="text-weight-bold text-primary">¥{{ (props.row.totalSpent || 0).toFixed(2) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>编辑</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="add_circle" color="positive" size="sm" @click="handleAddPoints(props.row)">
                <q-tooltip>加积分</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '编辑客户' : '新增客户' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model="form.name"
                label="姓名 *"
                outlined
                class="q-mb-md"
                :rules="[val => !!val || '请输入姓名']"
              />

              <q-input
                v-model="form.email"
                label="邮箱 *"
                outlined
                type="email"
                class="q-mb-md"
                :rules="[val => !!val || '请输入邮箱']"
              />

              <q-input
                v-model="form.phone"
                label="电话"
                outlined
                class="q-mb-md"
              />

              <q-select
                v-model="form.memberLevel"
                label="会员等级"
                outlined
                :options="memberLevelOptions"
                class="q-mb-md"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Points Dialog -->
      <q-dialog v-model="pointsDialogVisible" persistent>
        <q-card style="min-width: 400px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">增加积分</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-input
              v-model.number="pointsForm.points"
              label="积分数量"
              outlined
              type="number"
              :min="1"
            />
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="确定" color="primary" @click="handlePointsSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { crmApi, type Customer } from '@/api'

const $q = useQuasar()

const customers = ref<Customer[]>([])
const loading = ref(false)
const showDialog = ref(false)
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

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '姓名', align: 'left' as const, field: 'name' },
  { name: 'email', label: '邮箱', align: 'left' as const, field: 'email' },
  { name: 'phone', label: '电话', align: 'left' as const, field: 'phone' },
  { name: 'memberLevel', label: '会员等级', align: 'center' as const, field: 'memberLevel' },
  { name: 'points', label: '积分', align: 'center' as const, field: 'points' },
  { name: 'totalSpent', label: '总消费', align: 'left' as const, field: 'totalSpent' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const memberLevelOptions = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM']

const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await crmApi.getCustomers()
    // Backend returns Page<MemberDTO> with content array
    if (response.data && Array.isArray((response.data as any).content)) {
      customers.value = (response.data as any).content
    } else if (Array.isArray(response.data)) {
      customers.value = response.data
    } else {
      customers.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '加载客户列表失败',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLevelColor = (level?: Customer['memberLevel']) => {
  const colorMap = {
    BRONZE: 'brown',
    SILVER: 'grey',
    GOLD: 'amber',
    PLATINUM: 'purple'
  }
  return colorMap[level || 'BRONZE']
}

const handleEdit = (customer: Customer) => {
  form.value = { ...customer }
  showDialog.value = true
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
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await crmApi.createCustomer(form.value)
      $q.notify({
        type: 'positive',
        message: '创建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadCustomers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失败',
      position: 'top'
    })
  }
}

const handlePointsSubmit = async () => {
  try {
    await crmApi.addCustomerPoints(pointsForm.value.customerId, pointsForm.value.points)
    $q.notify({
      type: 'positive',
      message: '积分添加成功',
      position: 'top'
    })
    pointsDialogVisible.value = false
    pointsForm.value = { customerId: 0, points: 0 }
    loadCustomers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '积分添加失败',
      position: 'top'
    })
  }
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.customer-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
