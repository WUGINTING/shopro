<template>
  <q-page class="q-pa-md">
    <div class="profile-page">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">個人中心</div>
          <div class="text-caption text-grey-7">管理您的個人資料和帳號設定</div>
        </div>
      </div>

      <div class="row q-col-gutter-md">
        <!-- Profile Info Card -->
        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="text-h6 q-mb-md">
                <q-icon name="person" class="q-mr-sm" />
                基本資料
              </div>

              <div class="q-gutter-md">
                <div class="info-row">
                  <div class="info-label">使用者名稱</div>
                  <div class="info-value">{{ profile?.username || '-' }}</div>
                </div>

                <div class="info-row">
                  <div class="info-label">Email</div>
                  <div class="info-value">{{ profile?.email || '-' }}</div>
                </div>

                <div class="info-row">
                  <div class="info-label">角色</div>
                  <div class="info-value">
                    <q-badge :color="getRoleColor(profile?.role || '')">
                      {{ getRoleLabel(profile?.role || '') }}
                    </q-badge>
                  </div>
                </div>

                <div class="info-row">
                  <div class="info-label">帳號狀態</div>
                  <div class="info-value">
                    <q-chip
                      :color="profile?.enabled ? 'positive' : 'negative'"
                      text-color="white"
                      :icon="profile?.enabled ? 'check_circle' : 'cancel'"
                      size="sm"
                    >
                      {{ profile?.enabled ? '啟用' : '停用' }}
                    </q-chip>
                  </div>
                </div>

                <div class="info-row">
                  <div class="info-label">建立時間</div>
                  <div class="info-value">{{ formatDate(profile?.createdAt) }}</div>
                </div>

                <div class="info-row">
                  <div class="info-label">最後更新</div>
                  <div class="info-value">{{ formatDate(profile?.updatedAt) }}</div>
                </div>
              </div>

              <q-separator class="q-my-md" />

              <q-btn
                label="編輯資料"
                color="primary"
                icon="edit"
                unelevated
                @click="showEditDialog = true"
                class="full-width"
              />
            </q-card-section>
          </q-card>
        </div>

        <!-- Security Card -->
        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="text-h6 q-mb-md">
                <q-icon name="security" class="q-mr-sm" />
                安全設定
              </div>

              <div class="q-gutter-md">
                <q-btn
                  label="變更密碼"
                  color="primary"
                  icon="lock"
                  outline
                  @click="showPasswordDialog = true"
                  class="full-width"
                />
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Edit Profile Dialog -->
      <q-dialog v-model="showEditDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">編輯個人資料</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleUpdateProfile">
              <q-input
                v-model="editForm.username"
                label="使用者名稱 *"
                outlined
                class="q-mb-md"
                :rules="[
                  val => !!val || '請輸入使用者名稱',
                  val => val.length >= 3 || '使用者名稱至少需要3個字元'
                ]"
              />

              <q-input
                v-model="editForm.email"
                label="Email *"
                outlined
                type="email"
                class="q-mb-md"
                :rules="[
                  val => !!val || '請輸入Email',
                  val => /.+@.+\..+/.test(val) || 'Email格式不正確'
                ]"
              />

              <div class="row justify-end q-gutter-sm">
                <q-btn label="取消" flat color="grey" v-close-popup />
                <q-btn
                  label="儲存"
                  type="submit"
                  color="primary"
                  unelevated
                  :loading="submitting"
                />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>

      <!-- Change Password Dialog -->
      <q-dialog v-model="showPasswordDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">變更密碼</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleChangePassword">
              <q-input
                v-model="passwordForm.currentPassword"
                label="目前密碼 *"
                outlined
                :type="showCurrentPassword ? 'text' : 'password'"
                class="q-mb-md"
                :rules="[val => !!val || '請輸入目前密碼']"
              >
                <template v-slot:append>
                  <q-icon
                    :name="showCurrentPassword ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer"
                    @click="showCurrentPassword = !showCurrentPassword"
                  />
                </template>
              </q-input>

              <q-input
                v-model="passwordForm.newPassword"
                label="新密碼 *"
                outlined
                :type="showNewPassword ? 'text' : 'password'"
                class="q-mb-md"
                :rules="[
                  val => !!val || '請輸入新密碼',
                  val => val.length >= 6 || '密碼至少需要6個字元'
                ]"
              >
                <template v-slot:append>
                  <q-icon
                    :name="showNewPassword ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer"
                    @click="showNewPassword = !showNewPassword"
                  />
                </template>
              </q-input>

              <q-input
                v-model="passwordForm.confirmPassword"
                label="確認新密碼 *"
                outlined
                :type="showConfirmPassword ? 'text' : 'password'"
                class="q-mb-md"
                :rules="[
                  val => !!val || '請確認新密碼',
                  val => val === passwordForm.newPassword || '密碼不一致'
                ]"
              >
                <template v-slot:append>
                  <q-icon
                    :name="showConfirmPassword ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer"
                    @click="showConfirmPassword = !showConfirmPassword"
                  />
                </template>
              </q-input>

              <div class="row justify-end q-gutter-sm">
                <q-btn label="取消" flat color="grey" v-close-popup />
                <q-btn
                  label="變更密碼"
                  type="submit"
                  color="primary"
                  unelevated
                  :loading="submitting"
                />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { authApi, type User, type UpdateProfileRequest } from '@/api/auth'

const $q = useQuasar()
const authStore = useAuthStore()

const profile = ref<User | null>(null)
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const submitting = ref(false)
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const editForm = ref({
  username: '',
  email: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const roleOptions = [
  { label: '管理員', value: 'ADMIN' },
  { label: '經理', value: 'MANAGER' },
  { label: '員工', value: 'STAFF' },
  { label: '客戶', value: 'CUSTOMER' }
]

const getRoleLabel = (role: string) => {
  const option = roleOptions.find(opt => opt.value === role)
  return option ? option.label : role
}

const getRoleColor = (role: string) => {
  const colors: Record<string, string> = {
    'ADMIN': 'red',
    'MANAGER': 'purple',
    'STAFF': 'blue',
    'CUSTOMER': 'green'
  }
  return colors[role] || 'grey'
}

const formatDate = (dateString: string | undefined) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-TW')
}

const loadProfile = async () => {
  try {
    const response = await authApi.getProfile()
    if (response.success) {
      profile.value = response.data
      editForm.value = {
        username: response.data.username,
        email: response.data.email
      }
      // Update auth store with latest profile data
      authStore.setAuth(authStore.token!, response.data)
    } else {
      $q.notify({
        type: 'negative',
        message: response.message || '載入個人資料失敗'
      })
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '載入個人資料失敗'
    })
  }
}

const handleUpdateProfile = async () => {
  submitting.value = true
  try {
    const updateData: UpdateProfileRequest = {
      username: editForm.value.username,
      email: editForm.value.email
    }

    const response = await authApi.updateProfile(updateData)
    if (response.success) {
      $q.notify({
        type: 'positive',
        message: '個人資料更新成功'
      })
      showEditDialog.value = false
      await loadProfile()
    } else {
      $q.notify({
        type: 'negative',
        message: response.message || '更新失敗'
      })
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '更新失敗'
    })
  } finally {
    submitting.value = false
  }
}

const handleChangePassword = async () => {
  submitting.value = true
  try {
    const updateData: UpdateProfileRequest = {
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword
    }

    const response = await authApi.updateProfile(updateData)
    if (response.success) {
      $q.notify({
        type: 'positive',
        message: '密碼變更成功'
      })
      showPasswordDialog.value = false
      passwordForm.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      $q.notify({
        type: 'negative',
        message: response.message || '密碼變更失敗'
      })
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '密碼變更失敗'
    })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #e0e0e0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: #616161;
}

.info-value {
  font-weight: 400;
  color: #212121;
}
</style>
