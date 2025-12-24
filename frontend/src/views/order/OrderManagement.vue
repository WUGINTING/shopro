<template>
  <div class="order-management">
    <h1>訂單管理</h1>

    <!-- 工具欄 -->
    <div class="toolbar">
      <div class="filters">
        <select v-model="selectedStatus" @change="filterByStatus">
          <option value="">所有狀態</option>
          <option value="PENDING_PAYMENT">待付款</option>
          <option value="PROCESSING">處理中</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
          <option value="REFUNDED">已退款</option>
        </select>
        <input
          v-model="customerIdFilter"
          type="number"
          placeholder="輸入客戶 ID"
          @keyup.enter="filterByCustomer"
        />
        <button @click="filterByCustomer">搜尋</button>
      </div>
      <button class="btn-primary" @click="showCreateDialog = true">新增訂單</button>
    </div>

    <!-- 錯誤提示 -->
    <div v-if="orderStore.error" class="error-message">
      {{ orderStore.error }}
    </div>

    <!-- 訂單列表 -->
    <div v-if="orderStore.loading" class="loading">載入中...</div>
    <table v-else class="order-table">
      <thead>
        <tr>
          <th>訂單編號</th>
          <th>客戶 ID</th>
          <th>客戶姓名</th>
          <th>訂單狀態</th>
          <th>付款狀態</th>
          <th>物流狀態</th>
          <th>總金額</th>
          <th>建立時間</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orderStore.orders" :key="order.id">
          <td>{{ order.orderNumber || order.id }}</td>
          <td>{{ order.customerId }}</td>
          <td>{{ order.customerName || '-' }}</td>
          <td>
            <span :class="['status', order.status?.toLowerCase()]">
              {{ order.status }}
            </span>
          </td>
          <td>
            <span :class="['status', order.paymentStatus?.toLowerCase()]">
              {{ order.paymentStatus }}
            </span>
          </td>
          <td>
            <span :class="['status', order.shippingStatus?.toLowerCase()]">
              {{ order.shippingStatus }}
            </span>
          </td>
          <td>NT$ {{ order.finalAmount || order.totalAmount }}</td>
          <td>{{ formatDate(order.createdAt) }}</td>
          <td>
            <button class="btn-small" @click="viewOrderDetail(order)">查看</button>
            <button class="btn-small btn-danger" @click="confirmDelete(order.id!)">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 分頁 -->
    <div class="pagination">
      <button
        :disabled="orderStore.pagination.page === 0"
        @click="changePage(orderStore.pagination.page - 1)"
      >
        上一頁
      </button>
      <span>
        第 {{ orderStore.pagination.page + 1 }} / {{ orderStore.pagination.totalPages }} 頁
      </span>
      <button
        :disabled="orderStore.pagination.page >= orderStore.pagination.totalPages - 1"
        @click="changePage(orderStore.pagination.page + 1)"
      >
        下一頁
      </button>
    </div>

    <!-- 新增訂單對話框 -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>新增訂單</h2>
        <form @submit.prevent="saveOrder">
          <div class="form-group">
            <label for="customerId">客戶 ID *</label>
            <input id="customerId" v-model.number="orderForm.customerId" type="number" required />
          </div>
          <div class="form-group">
            <label for="customerName">客戶姓名</label>
            <input id="customerName" v-model="orderForm.customerName" type="text" />
          </div>
          <div class="form-group">
            <label for="totalAmount">總金額 *</label>
            <input id="totalAmount" v-model.number="orderForm.totalAmount" type="number" required />
          </div>
          <div class="form-group">
            <label for="discountAmount">折扣金額</label>
            <input id="discountAmount" v-model.number="orderForm.discountAmount" type="number" />
          </div>
          <div class="form-group">
            <label for="shippingFee">運費</label>
            <input id="shippingFee" v-model.number="orderForm.shippingFee" type="number" />
          </div>
          <div class="form-group">
            <label for="shippingAddress">配送地址</label>
            <textarea id="shippingAddress" v-model="orderForm.shippingAddress" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label for="shippingMethod">配送方式</label>
            <input id="shippingMethod" v-model="orderForm.shippingMethod" type="text" />
          </div>
          <div class="form-group">
            <label for="pickupType">取貨方式</label>
            <select id="pickupType" v-model="orderForm.pickupType">
              <option value="DELIVERY">宅配</option>
              <option value="STORE_PICKUP">門市取貨</option>
              <option value="CROSS_STORE_PICKUP">跨店取貨</option>
            </select>
          </div>
          <div class="form-group">
            <label for="notes">備註</label>
            <textarea id="notes" v-model="orderForm.notes" rows="3"></textarea>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeDialog">取消</button>
            <button type="submit" class="btn-primary">儲存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 訂單詳情對話框 -->
    <div v-if="showDetailDialog" class="dialog-overlay" @click.self="closeDetailDialog">
      <div class="dialog">
        <h2>訂單詳情</h2>
        <div v-if="orderStore.currentOrder" class="order-detail">
          <div class="detail-section">
            <h3>基本資訊</h3>
            <p><strong>訂單編號:</strong> {{ orderStore.currentOrder.orderNumber }}</p>
            <p><strong>客戶 ID:</strong> {{ orderStore.currentOrder.customerId }}</p>
            <p><strong>客戶姓名:</strong> {{ orderStore.currentOrder.customerName }}</p>
            <p><strong>訂單狀態:</strong> {{ orderStore.currentOrder.status }}</p>
            <p><strong>付款狀態:</strong> {{ orderStore.currentOrder.paymentStatus }}</p>
            <p><strong>物流狀態:</strong> {{ orderStore.currentOrder.shippingStatus }}</p>
          </div>
          <div class="detail-section">
            <h3>金額資訊</h3>
            <p><strong>總金額:</strong> NT$ {{ orderStore.currentOrder.totalAmount }}</p>
            <p v-if="orderStore.currentOrder.discountAmount">
              <strong>折扣金額:</strong> NT$ {{ orderStore.currentOrder.discountAmount }}
            </p>
            <p v-if="orderStore.currentOrder.shippingFee">
              <strong>運費:</strong> NT$ {{ orderStore.currentOrder.shippingFee }}
            </p>
            <p>
              <strong>最終金額:</strong> NT$
              {{ orderStore.currentOrder.finalAmount || orderStore.currentOrder.totalAmount }}
            </p>
          </div>
          <div class="detail-section">
            <h3>配送資訊</h3>
            <p><strong>配送地址:</strong> {{ orderStore.currentOrder.shippingAddress || '-' }}</p>
            <p><strong>配送方式:</strong> {{ orderStore.currentOrder.shippingMethod || '-' }}</p>
            <p><strong>取貨方式:</strong> {{ orderStore.currentOrder.pickupType || '-' }}</p>
          </div>
          <div v-if="orderStore.currentOrder.notes" class="detail-section">
            <h3>備註</h3>
            <p>{{ orderStore.currentOrder.notes }}</p>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn-secondary" @click="closeDetailDialog">關閉</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useOrderStore } from '@/stores/modules/order'
import type { Order } from '@/api'

const orderStore = useOrderStore()
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const selectedStatus = ref('')
const customerIdFilter = ref<number | null>(null)

const orderForm = ref<Order>({
  customerId: 0,
  customerName: '',
  totalAmount: 0,
  discountAmount: 0,
  shippingFee: 0,
  shippingAddress: '',
  shippingMethod: '',
  pickupType: 'DELIVERY',
  notes: ''
})

onMounted(() => {
  orderStore.fetchOrders()
})

function changePage(page: number) {
  if (selectedStatus.value) {
    orderStore.fetchOrdersByStatus(selectedStatus.value, page)
  } else if (customerIdFilter.value) {
    orderStore.fetchOrdersByCustomer(customerIdFilter.value, page)
  } else {
    orderStore.fetchOrders(page, orderStore.pagination.size)
  }
}

function filterByStatus() {
  if (selectedStatus.value) {
    customerIdFilter.value = null
    orderStore.fetchOrdersByStatus(selectedStatus.value)
  } else {
    orderStore.fetchOrders()
  }
}

function filterByCustomer() {
  if (customerIdFilter.value) {
    selectedStatus.value = ''
    orderStore.fetchOrdersByCustomer(customerIdFilter.value)
  } else {
    orderStore.fetchOrders()
  }
}

async function viewOrderDetail(order: Order) {
  try {
    await orderStore.getOrder(order.id!)
    showDetailDialog.value = true
  } catch (error) {
    alert('獲取訂單詳情失敗')
  }
}

async function saveOrder() {
  try {
    await orderStore.createOrder(orderForm.value)
    alert('訂單新增成功')
    closeDialog()
  } catch (error) {
    alert('操作失敗，請稍後再試')
  }
}

async function confirmDelete(id: number) {
  if (confirm('確定要刪除此訂單嗎？')) {
    try {
      await orderStore.deleteOrder(id)
      alert('訂單刪除成功')
    } catch (error) {
      alert('刪除失敗，請稍後再試')
    }
  }
}

function formatDate(date?: string) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-TW')
}

function closeDialog() {
  showCreateDialog.value = false
  orderForm.value = {
    customerId: 0,
    customerName: '',
    totalAmount: 0,
    discountAmount: 0,
    shippingFee: 0,
    shippingAddress: '',
    shippingMethod: '',
    pickupType: 'DELIVERY',
    notes: ''
  }
}

function closeDetailDialog() {
  showDetailDialog.value = false
}
</script>

<style scoped>
.order-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filters select,
.filters input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filters input {
  width: 150px;
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

.order-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.order-table th,
.order-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.order-table th {
  background-color: #f5f5f5;
  font-weight: 600;
}

.order-table tbody tr:hover {
  background-color: #f9f9f9;
}

.status {
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 0.85em;
  font-weight: 500;
}

.status.completed,
.status.paid {
  background-color: #d4edda;
  color: #155724;
}

.status.processing,
.status.pending {
  background-color: #fff3cd;
  color: #856404;
}

.status.cancelled,
.status.refunded {
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
  width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog h2 {
  margin-top: 0;
}

.order-detail {
  margin: 20px 0;
}

.detail-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.detail-section p {
  margin: 5px 0;
  font-size: 14px;
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
.form-group select,
.form-group textarea {
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
