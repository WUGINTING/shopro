<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">使用者管理</div>
          <div class="text-caption text-grey-7">管理後台使用者帳號、角色權限與啟用狀態。</div>
        </div>
        <q-btn
          color="primary"
          icon="person_add"
          label="新增使用者"
          unelevated
          @click="showDialog = true; resetForm()"
        />
      </div>

      <q-card>
        <q-table
          :rows="users"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template #body-cell-role="props">
            <q-td :props="props">
              <q-badge :color="getRoleColor(props.row.role)">
                {{ getRoleLabel(props.row.role) }}
              </q-badge>
            </q-td>
          </template>

          <template #body-cell-enabled="props">
            <q-td :props="props">
              <q-chip
                :color="props.row.enabled ? 'positive' : 'negative'"
                text-color="white"
                :icon="props.row.enabled ? 'check_circle' : 'cancel'"
              >
                {{ props.row.enabled ? '啟用' : '停用' }}
              </q-chip>
            </q-td>
          </template>

          <template #body-cell-createdAt="props">
            <q-td :props="props">
              {{ formatDate(props.row.createdAt) }}
            </q-td>
          </template>

          <template #body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn
                flat
                dense
                round
                :icon="props.row.enabled ? 'block' : 'check_circle'"
                :color="props.row.enabled ? 'negative' : 'positive'"
                size="sm"
                @click="handleToggleStatus(props.row)"
              >
                <q-tooltip>{{ props.row.enabled ? '停用' : '啟用' }}</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯使用者' : '新增使用者' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <q-input
                v-model="form.username"
                label="使用者名稱 *"
                outlined
                class="q-mb-md"
                :rules="[
                  (val) => !!val || '請輸入使用者名稱',
                  (val) => String(val || '').length >= 3 || '使用者名稱至少需要 3 個字元'
                ]"
              />

              <q-input
                v-model="form.email"
                label="電子郵件 *"
                outlined
                type="email"
                class="q-mb-md"
                :rules="[
                  (val) => !!val || '請輸入電子郵件',
                  (val) => /.+@.+\..+/.test(String(val || '')) || '電子郵件格式不正確'
                ]"
              />

              <q-input
                v-model="form.password"
                label="密碼"
                outlined
                :type="showPassword ? 'text' : 'password'"
                class="q-mb-md"
                :hint="form.id ? '留空表示不變更密碼' : '密碼至少需要 6 個字元'"
                :rules="form.id
                  ? [
                      (val) => !val || String(val).length >= 6 || '密碼至少需要 6 個字元'
                    ]
                  : [
                      (val) => !!val || '請輸入密碼',
                      (val) => String(val || '').length >= 6 || '密碼至少需要 6 個字元'
                    ]"
              >
                <template #append>
                  <q-icon
                    :name="showPassword ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer"
                    @click="showPassword = !showPassword"
                  />
                </template>
              </q-input>

              <q-select
                v-model="form.role"
                label="角色 *"
                outlined
                :options="roleOptions"
                emit-value
                map-options
                class="q-mb-md"
                :rules="[(val) => !!val || '請選擇角色']"
              />

              <q-toggle v-model="form.enabled" label="啟用狀態" class="q-mb-md" />

              <div class="row justify-end q-gutter-sm">
                <q-btn label="取消" flat color="grey" v-close-popup />
                <q-btn label="儲存" type="submit" color="primary" unelevated :loading="submitting" />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { userApi, type User } from '@/api/user'

const $q = useQuasar()

const users = ref<User[]>([])
const loading = ref(false)
const showDialog = ref(false)
const showPassword = ref(false)
const submitting = ref(false)

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const form = ref<User>({
  username: '',
  email: '',
  password: '',
  role: 'CUSTOMER',
  enabled: true
})

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  { name: 'username', label: '使用者名稱', field: 'username', align: 'left' as const, sortable: true },
  { name: 'email', label: '電子郵件', field: 'email', align: 'left' as const, sortable: true },
  { name: 'role', label: '角色', field: 'role', align: 'center' as const, sortable: true },
  { name: 'enabled', label: '狀態', field: 'enabled', align: 'center' as const, sortable: true },
  { name: 'createdAt', label: '建立時間', field: 'createdAt', align: 'left' as const, sortable: true },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' as const }
]

const roleOptions = [
  { label: '管理員', value: 'ADMIN' },
  { label: '經理', value: 'MANAGER' },
  { label: '員工', value: 'STAFF' },
  { label: '顧客', value: 'CUSTOMER' }
]

const getRoleLabel = (role: string) => roleOptions.find((opt) => opt.value === role)?.label || role

const getRoleColor = (role: string) => {
  const colors: Record<string, string> = {
    ADMIN: 'red',
    MANAGER: 'purple',
    STAFF: 'blue',
    CUSTOMER: 'green'
  }
  return colors[role] || 'grey'
}

const formatDate = (dateString: string | undefined) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-TW')
}

const resetForm = () => {
  showPassword.value = false
  form.value = {
    username: '',
    email: '',
    password: '',
    role: 'CUSTOMER',
    enabled: true
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await userApi.getAllUsers()
    if (response.success) {
      users.value = response.data || []
    } else {
      $q.notify({ type: 'negative', message: response.message || '載入使用者資料失敗' })
    }
  } catch (error: any) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || '載入使用者資料失敗' })
  } finally {
    loading.value = false
  }
}

const handleEdit = (user: User) => {
  showPassword.value = false
  form.value = {
    id: user.id,
    username: user.username,
    email: user.email,
    password: '',
    role: user.role,
    enabled: user.enabled ?? true
  }
  showDialog.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    let response
    if (form.value.id) {
      const updateData = { ...form.value }
      if (!updateData.password) delete updateData.password
      response = await userApi.updateUser(form.value.id, updateData)
    } else {
      response = await userApi.createUser(form.value)
    }

    if (response.success) {
      $q.notify({ type: 'positive', message: form.value.id ? '使用者更新成功' : '使用者建立成功' })
      showDialog.value = false
      await loadUsers()
    } else {
      $q.notify({ type: 'negative', message: response.message || '儲存失敗' })
    }
  } catch (error: any) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || '儲存失敗' })
  } finally {
    submitting.value = false
  }
}

const handleToggleStatus = async (user: User) => {
  $q.dialog({
    title: '確認操作',
    message: `確認要${user.enabled ? '停用' : '啟用'}使用者「${user.username}」嗎？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      const response = await userApi.toggleUserStatus(user.id!, !user.enabled)
      if (response.success) {
        $q.notify({ type: 'positive', message: `使用者已${!user.enabled ? '啟用' : '停用'}` })
        await loadUsers()
      } else {
        $q.notify({ type: 'negative', message: response.message || '更新狀態失敗' })
      }
    } catch (error: any) {
      $q.notify({ type: 'negative', message: error?.response?.data?.message || '更新狀態失敗' })
    }
  })
}

const handleDelete = async (user: User) => {
  $q.dialog({
    title: '確認刪除',
    message: `確認要刪除使用者「${user.username}」嗎？此操作無法復原。`,
    cancel: true,
    persistent: true,
    color: 'negative'
  }).onOk(async () => {
    try {
      const response = await userApi.deleteUser(user.id!)
      if (response.success) {
        $q.notify({ type: 'positive', message: '使用者已刪除' })
        await loadUsers()
      } else {
        $q.notify({ type: 'negative', message: response.message || '刪除失敗' })
      }
    } catch (error: any) {
      $q.notify({ type: 'negative', message: error?.response?.data?.message || '刪除失敗' })
    }
  })
}

onMounted(() => {
  loadUsers()
})
</script>
