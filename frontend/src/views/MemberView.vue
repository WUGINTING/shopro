<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none">會員管理</h4>
      </div>
      <div class="col-auto">
        <q-btn
          color="primary"
          label="新增會員"
          icon="add"
          @click="showCreateDialog"
        />
      </div>
    </div>

    <!-- 搜尋欄 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-input
              v-model="searchForm.name"
              label="會員名稱"
              outlined
              dense
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-input
              v-model="searchForm.email"
              label="電子郵件"
              outlined
              dense
              type="email"
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-select
              v-model="searchForm.status"
              label="狀態"
              :options="['ACTIVE', 'INACTIVE', 'SUSPENDED']"
              outlined
              dense
              clearable
              @update:model-value="onSearch"
            />
          </div>
          <div class="col-12 col-sm-6">
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

    <!-- 會員列表 -->
    <q-card>
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        :rows="members"
        :columns="columns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
        flat
        bordered
      >
        <template #body-cell-status="props">
          <q-td :props="props">
            <q-badge
              :label="props.row.status"
              :color="getStatusColor(props.row.status)"
            />
          </q-td>
        </template>

        <template #body-cell-totalSpent="props">
          <q-td :props="props">
            <span class="text-weight-bold">
              NT${{ formatCurrency(props.row.totalSpent) }}
            </span>
          </q-td>
        </template>

        <template #body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="edit"
              color="primary"
              @click="showEditDialog(props.row)"
              title="編輯"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="delete"
              color="negative"
              @click="confirmDelete(props.row.id)"
              title="刪除"
            />
            <q-menu
              v-if="props.row.status === 'ACTIVE'"
              auto-close
            >
              <q-list style="min-width: 100px">
                <q-item
                  clickable
                  @click="suspendMember(props.row.id)"
                >
                  <q-item-section>停用</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-menu
              v-else-if="props.row.status !== 'ACTIVE'"
              auto-close
            >
              <q-list style="min-width: 100px">
                <q-item
                  clickable
                  @click="activateMember(props.row.id)"
                >
                  <q-item-section>啟用</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-td>
        </template>

        <template #no-data>
          <div class="text-center q-py-lg text-grey-7">
            沒有會員資料
          </div>
        </template>
      </q-table>
    </q-card>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog">
      <q-card style="width: 500px; max-width: 90vw">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">
            {{ editingMember?.id ? '編輯會員' : '新增會員' }}
          </div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showDialog = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pt-none">
          <q-form ref="memberForm" @submit="saveMember">
            <q-input
              v-model="editingMember.name"
              label="會員名稱 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入會員名稱']"
            />

            <q-input
              v-model="editingMember.email"
              label="電子郵件 *"
              outlined
              dense
              type="email"
              class="q-mb-md"
              :rules="[
                val => !!val || '請輸入電子郵件',
                val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || '電子郵件格式不正確'
              ]"
            />

            <q-input
              v-model="editingMember.phone"
              label="電話"
              outlined
              dense
              class="q-mb-md"
            />

            <q-select
              v-model="editingMember.status"
              label="狀態 *"
              outlined
              dense
              :options="['ACTIVE', 'INACTIVE', 'SUSPENDED']"
              class="q-mb-md"
              :rules="[val => !!val || '請選擇狀態']"
            />

            <q-input
              v-model.number="editingMember.totalPoints"
              label="總積點"
              outlined
              dense
              type="number"
              class="q-mb-md"
            />

            <q-input
              v-model.number="editingMember.totalSpent"
              label="總消費金額"
              outlined
              dense
              type="number"
              class="q-mb-md"
            />

            <q-input
              v-model="editingMember.notes"
              label="備註"
              outlined
              dense
              type="textarea"
              class="q-mb-md"
            />

            <div class="row q-gutter-md">
              <q-btn
                label="取消"
                flat
                @click="showDialog = false"
              />
              <q-btn
                label="保存"
                color="primary"
                type="submit"
                :loading="saving"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 刪除確認 -->
    <q-dialog v-model="showDeleteDialog">
      <q-card>
        <q-card-section class="row items-center">
          <q-icon
            name="warning"
            size="md"
            color="negative"
          />
          <span class="q-ml-md">確定要刪除此會員嗎？</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            label="取消"
            flat
            @click="showDeleteDialog = false"
          />
          <q-btn
            label="刪除"
            color="negative"
            @click="deleteMember"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { memberApi, type Member, type PageResponse } from '@/api/member'

const $q = useQuasar()
const memberForm = ref()

// 資料
const members = ref<Member[]>([])
const pagination = ref({
  page: 0,
  rowsPerPage: 20,
  rowsNumber: 0
})
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)

// 搜尋表單
const searchForm = ref({
  name: '',
  email: '',
  status: ''
})

// 對話框
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const deleteId = ref<number>()

// 編輯會員
const editingMember = ref<Partial<Member>>({
  name: '',
  email: '',
  status: 'ACTIVE',
  totalPoints: 0,
  totalSpent: 0
})

// 表格列定義
const columns = [
  { name: 'name', label: '名稱', field: 'name', align: 'left' },
  { name: 'email', label: '電子郵件', field: 'email', align: 'left' },
  { name: 'phone', label: '電話', field: 'phone', align: 'left' },
  { name: 'status', label: '狀態', field: 'status', align: 'center' },
  { name: 'totalPoints', label: '積點', field: 'totalPoints', align: 'right' },
  { name: 'totalSpent', label: '消費金額', field: 'totalSpent', align: 'right' },
  { name: 'registeredDate', label: '註冊日期', field: 'registeredDate', align: 'center' },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' }
]

// 取得狀態顏色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    ACTIVE: 'green',
    INACTIVE: 'orange',
    SUSPENDED: 'red'
  }
  return colors[status] || 'grey'
}

// 格式化金額
const formatCurrency = (amount: number | undefined | null) => {
  if (amount === undefined || amount === null || isNaN(amount)) {
    return '0'
  }
  return amount.toLocaleString('zh-TW')
}

// 載入會員列表
const loadMembers = async () => {
  loading.value = true
  try {
    const result = await memberApi.getMembers({
      page: pagination.value.page,
      size: pagination.value.rowsPerPage,
      ...searchForm.value
    })
    members.value = result?.content || []
    pagination.value.rowsNumber = result?.totalElements || 0
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入會員列表失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 搜尋
const onSearch = () => {
  pagination.value.page = 0
  loadMembers()
}

// 重置搜尋
const resetSearch = () => {
  searchForm.value = { name: '', email: '', status: '' }
  pagination.value.page = 0
  loadMembers()
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingMember.value = {
    name: '',
    email: '',
    status: 'ACTIVE',
    totalPoints: 0,
    totalSpent: 0
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (member: Member) => {
  editingMember.value = { ...member }
  showDialog.value = true
}

// 保存會員
const saveMember = async () => {
  try {
    if (editingMember.value.id) {
      await memberApi.updateMember(
        editingMember.value.id,
        editingMember.value
      )
      $q.notify({
        type: 'positive',
        message: '會員已更新',
        position: 'top'
      })
    } else {
      await memberApi.createMember(editingMember.value as Member)
      $q.notify({
        type: 'positive',
        message: '會員已建立',
        position: 'top'
      })
    }
    showDialog.value = false
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

// 確認刪除
const confirmDelete = (id: number) => {
  deleteId.value = id
  showDeleteDialog.value = true
}

// 刪除會員
const deleteMember = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await memberApi.deleteMember(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '會員已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '刪除失敗',
      position: 'top'
    })
  } finally {
    deleting.value = false
  }
}

// 停用會員
const suspendMember = async (id: number) => {
  try {
    await memberApi.suspendMember(id)
    $q.notify({
      type: 'positive',
      message: '會員已停用',
      position: 'top'
    })
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '停用失敗',
      position: 'top'
    })
  }
}

// 啟用會員
const activateMember = async (id: number) => {
  try {
    await memberApi.activateMember(id)
    $q.notify({
      type: 'positive',
      message: '會員已啟用',
      position: 'top'
    })
    loadMembers()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '啟用失敗',
      position: 'top'
    })
  }
}

onMounted(() => {
  loadMembers()
})
</script>
