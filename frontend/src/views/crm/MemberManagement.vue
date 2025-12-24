<template>
  <div class="member-management">
    <h1>會員管理</h1>

    <!-- 搜尋和操作區 -->
    <div class="toolbar">
      <div class="search-box">
        <input
          v-model="searchEmail"
          type="email"
          placeholder="輸入電子郵件搜尋會員"
          @keyup.enter="searchByEmail"
        />
        <button @click="searchByEmail">搜尋</button>
      </div>
      <button class="btn-primary" @click="showCreateDialog = true">新增會員</button>
    </div>

    <!-- 錯誤提示 -->
    <div v-if="crmStore.error" class="error-message">
      {{ crmStore.error }}
    </div>

    <!-- 會員列表 -->
    <div v-if="crmStore.loading" class="loading">載入中...</div>
    <table v-else class="member-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>電子郵件</th>
          <th>電話</th>
          <th>狀態</th>
          <th>會員等級</th>
          <th>積點</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="member in crmStore.members" :key="member.id">
          <td>{{ member.id }}</td>
          <td>{{ member.name }}</td>
          <td>{{ member.email }}</td>
          <td>{{ member.phone || '-' }}</td>
          <td>
            <span :class="['status', member.status?.toLowerCase()]">
              {{ member.status }}
            </span>
          </td>
          <td>{{ member.levelId || '-' }}</td>
          <td>{{ member.totalPoints || 0 }}</td>
          <td>
            <button class="btn-small" @click="editMember(member)">編輯</button>
            <button class="btn-small btn-danger" @click="confirmDelete(member.id!)">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 分頁 -->
    <div class="pagination">
      <button
        :disabled="crmStore.pagination.page === 0"
        @click="changePage(crmStore.pagination.page - 1)"
      >
        上一頁
      </button>
      <span>
        第 {{ crmStore.pagination.page + 1 }} / {{ crmStore.pagination.totalPages }} 頁
      </span>
      <button
        :disabled="crmStore.pagination.page >= crmStore.pagination.totalPages - 1"
        @click="changePage(crmStore.pagination.page + 1)"
      >
        下一頁
      </button>
    </div>

    <!-- 新增/編輯會員對話框 -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>{{ editingMember ? '編輯會員' : '新增會員' }}</h2>
        <form @submit.prevent="saveMember">
          <div class="form-group">
            <label for="name">姓名 *</label>
            <input id="name" v-model="memberForm.name" type="text" required />
          </div>
          <div class="form-group">
            <label for="email">電子郵件 *</label>
            <input id="email" v-model="memberForm.email" type="email" required />
          </div>
          <div class="form-group">
            <label for="phone">電話</label>
            <input id="phone" v-model="memberForm.phone" type="tel" />
          </div>
          <div class="form-group">
            <label for="birthday">生日</label>
            <input id="birthday" v-model="memberForm.birthday" type="date" />
          </div>
          <div class="form-group">
            <label for="gender">性別</label>
            <select id="gender" v-model="memberForm.gender">
              <option value="">請選擇</option>
              <option value="M">男性</option>
              <option value="F">女性</option>
              <option value="OTHER">其他</option>
            </select>
          </div>
          <div class="form-group">
            <label for="address">地址</label>
            <input id="address" v-model="memberForm.address" type="text" />
          </div>
          <div class="form-group">
            <label for="postalCode">郵遞區號</label>
            <input id="postalCode" v-model="memberForm.postalCode" type="text" />
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeDialog">取消</button>
            <button type="submit" class="btn-primary">儲存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCrmStore } from '@/stores/modules/crm'
import type { Member } from '@/api'
import { showSuccess, showError, showConfirm } from '@/utils/notification'

const crmStore = useCrmStore()
const showCreateDialog = ref(false)
const editingMember = ref<Member | null>(null)
const searchEmail = ref('')

const memberForm = ref<Member>({
  name: '',
  email: '',
  phone: '',
  birthday: '',
  gender: undefined,
  address: '',
  postalCode: ''
})

onMounted(() => {
  crmStore.fetchMembers()
})

function changePage(page: number) {
  crmStore.fetchMembers(page, crmStore.pagination.size)
}

function editMember(member: Member) {
  editingMember.value = member
  memberForm.value = { ...member }
  showCreateDialog.value = true
}

async function saveMember() {
  try {
    if (editingMember.value && editingMember.value.id) {
      await crmStore.updateMember(editingMember.value.id, memberForm.value)
      showSuccess('會員更新成功')
    } else {
      await crmStore.createMember(memberForm.value)
      showSuccess('會員新增成功')
    }
    closeDialog()
  } catch (error) {
    showError('操作失敗，請稍後再試')
  }
}

async function confirmDelete(id: number) {
  if (showConfirm('確定要刪除此會員嗎？')) {
    try {
      await crmStore.deleteMember(id)
      showSuccess('會員刪除成功')
    } catch (error) {
      showError('刪除失敗，請稍後再試')
    }
  }
}

async function searchByEmail() {
  if (searchEmail.value) {
    try {
      await crmStore.getMemberByEmail(searchEmail.value)
      if (crmStore.currentMember) {
        crmStore.members = [crmStore.currentMember]
      }
    } catch (error) {
      showError('查詢失敗，請確認電子郵件是否正確')
    }
  } else {
    crmStore.fetchMembers()
  }
}

function closeDialog() {
  showCreateDialog.value = false
  editingMember.value = null
  memberForm.value = {
    name: '',
    email: '',
    phone: '',
    birthday: '',
    gender: undefined,
    address: '',
    postalCode: ''
  }
}
</script>

<style scoped>
.member-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 300px;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.member-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.member-table th,
.member-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.member-table th {
  background-color: #f5f5f5;
  font-weight: 600;
}

.member-table tbody tr:hover {
  background-color: #f9f9f9;
}

.status {
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 0.85em;
  font-weight: 500;
}

.status.active {
  background-color: #d4edda;
  color: #155724;
}

.status.inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog h2 {
  margin-top: 0;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
  margin-right: 5px;
  background-color: #17a2b8;
  color: white;
}

.btn-small:hover {
  background-color: #138496;
}

.btn-danger {
  background-color: #dc3545;
}

.btn-danger:hover {
  background-color: #c82333;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
