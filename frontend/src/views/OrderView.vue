<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">訂單管理</div>
          <div class="text-caption text-grey-7">管理訂單狀態和發貨資訊</div>
        </div>
        <div>
          <q-btn
            color="primary"
            icon="add"
            label="新增訂單"
            unelevated
            @click="handleAdd"
          />
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
              <span class="text-weight-bold text-primary">${{ props.row.totalAmount.toFixed(2) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-customerName="props">
            <q-td :props="props">
              {{ props.row.customerName || `客戶 #${props.row.customerId}` || '-' }}
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn-dropdown flat dense color="primary" label="更新狀態" size="sm">
                <q-list>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'PROCESSING')">
                    <q-item-section>
                      <q-item-label>處理中</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'COMPLETED')">
                    <q-item-section>
                      <q-item-label>已完成</q-item-label>
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
              
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>編輯訂單</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="visibility" color="primary" size="sm">
                <q-tooltip>查看詳情</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row)">
                <q-tooltip>刪除訂單</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Order Dialog -->
      <q-dialog v-model="showDialog" maximized>
        <q-card style="min-width: 800px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ editingOrderId ? '編輯訂單' : '新增訂單' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <div class="row q-col-gutter-md">
                <!-- 客戶選擇或輸入 -->
                <div class="col-12">
                  <q-select
                    v-model="form.customerId"
                    :input-value="customerInputValue"
                    label="客戶（可選或輸入）"
                    outlined
                    :options="customerOptions"
                    option-value="id"
                    option-label="name"
                    emit-value
                    map-options
                    use-input
                    fill-input
                    hide-selected
                    :loading="customerSearchLoading"
                    input-debounce="500"
                    @filter="filterCustomers"
                    @update:model-value="onCustomerChange"
                    @input-value="onCustomerInput"
                    clearable
                  >
                    <template v-slot:option="scope">
                      <q-item v-bind="scope.itemProps">
                        <q-item-section>
                          <q-item-label>{{ scope.opt.name }}</q-item-label>
                          <q-item-label caption>{{ scope.opt.email }}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </template>
                    <template v-slot:no-option>
                      <q-item>
                        <q-item-section class="text-grey">
                          沒有找到客戶，可以手動輸入
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>

                <!-- 客戶信息輸入（始終顯示，選擇客戶後自動填充） -->
                <div class="col-12">
                  <div class="text-subtitle2 q-mb-sm">客戶信息（可選）</div>
                  <div class="row q-col-gutter-md">
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerName"
                        label="客戶姓名"
                        outlined
                        dense
                      />
                    </div>
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerPhone"
                        label="客戶電話"
                        outlined
                        dense
                      />
                    </div>
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerEmail"
                        label="客戶郵箱"
                        outlined
                        dense
                        type="email"
                      />
                    </div>
                  </div>
                </div>

                <!-- 取貨方式 -->
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.pickupType"
                    label="取貨方式 *"
                    outlined
                    :options="pickupTypeOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                    :rules="[val => !!val || '請選擇取貨方式']"
                  />
                </div>

                <!-- 收貨地址 -->
                <div class="col-12" v-if="form.pickupType === 'DELIVERY'">
                  <q-input
                    v-model="form.shippingAddress"
                    label="收貨地址"
                    outlined
                    type="textarea"
                    rows="2"
                  />
                </div>

                <!-- 訂單項目 -->
                <div class="col-12">
                  <div class="text-subtitle2 q-mb-sm">訂單項目 *</div>
                  <q-table
                    :rows="form.items"
                    :columns="itemColumns"
                    row-key="tempId"
                    flat
                    hide-pagination
                    :rows-per-page-options="[0]"
                  >
                    <template v-slot:body-cell-product="props">
                      <q-td :props="props">
                        <q-select
                          v-model="props.row.productId"
                          outlined
                          dense
                          :options="productOptions"
                          option-value="id"
                          option-label="name"
                          emit-value
                          map-options
                          @update:model-value="(val) => onProductChange(props.row, val)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-quantity="props">
                      <q-td :props="props">
                        <q-input
                          v-model.number="props.row.quantity"
                          outlined
                          dense
                          type="number"
                          min="1"
                          @update:model-value="calculateItemSubtotal(props.row)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-unitPrice="props">
                      <q-td :props="props">
                        <q-input
                          v-model.number="props.row.unitPrice"
                          outlined
                          dense
                          type="number"
                          prefix="$"
                          step="0.01"
                          @update:model-value="calculateItemSubtotal(props.row)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-subtotal="props">
                      <q-td :props="props">
                        <span class="text-weight-bold">${{ (props.row.subtotal || 0).toFixed(2) }}</span>
                      </q-td>
                    </template>

                    <template v-slot:body-cell-actions="props">
                      <q-td :props="props">
                        <q-btn
                          flat
                          dense
                          round
                          icon="delete"
                          color="negative"
                          @click="removeOrderItem(props.rowIndex)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:top>
                      <q-btn
                        color="primary"
                        icon="add"
                        label="添加商品"
                        dense
                        unelevated
                        @click="addOrderItem"
                      />
                    </template>
                  </q-table>
                </div>

                <!-- 折扣金額 -->
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="form.discountAmount"
                    label="折扣金額"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    min="0"
                    @update:model-value="calculateTotal"
                  />
                </div>

                <!-- 運費 -->
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="form.shippingFee"
                    label="運費"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    min="0"
                    @update:model-value="calculateTotal"
                  />
                </div>

                <!-- 訂單總金額 -->
                <div class="col-12">
                  <q-input
                    v-model.number="form.totalAmount"
                    label="訂單總金額"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    readonly
                    class="text-h6 text-weight-bold"
                  />
                </div>

                <!-- 備註 -->
                <div class="col-12">
                  <q-input
                    v-model="form.notes"
                    label="備註"
                    outlined
                    type="textarea"
                    rows="3"
                  />
                </div>
              </div>

              <q-card-actions align="right" class="q-mt-md">
                <q-btn flat label="取消" color="grey" v-close-popup />
                <q-btn flat label="儲存" color="primary" type="submit" />
              </q-card-actions>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>

      <!-- Discount Management Dialog -->
      <q-dialog v-model="showDiscountDialog" persistent>
        <q-card style="min-width: 600px; max-width: 800px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">管理折扣記錄</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <!-- Order ID Display -->
            <div class="q-mb-md" v-if="discountForm.orderId">
              <q-banner class="bg-primary text-white">
                <template v-slot:avatar>
                  <q-icon name="receipt" />
                </template>
                訂單ID: <strong>{{ discountForm.orderId }}</strong>
              </q-banner>
            </div>
            
            <q-banner v-else class="bg-warning text-white q-mb-md">
              <template v-slot:avatar>
                <q-icon name="warning" />
              </template>
              請先保存訂單後才能添加折扣記錄
            </q-banner>

            <!-- Discount List -->
            <div class="q-mb-md">
              <div class="text-subtitle2 q-mb-sm">折扣記錄列表</div>
              <q-table
                :rows="orderDiscounts"
                :columns="discountColumns"
                row-key="id"
                flat
                hide-pagination
                :rows-per-page-options="[0]"
              >
                <template v-slot:body-cell-discountAmount="props">
                  <q-td :props="props">
                    <span class="text-weight-bold text-primary">${{ props.row.discountAmount?.toFixed(2) }}</span>
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
                    <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDeleteDiscount(props.row.id!)">
                      <q-tooltip>刪除</q-tooltip>
                    </q-btn>
                  </q-td>
                </template>
                <template v-slot:no-data>
                  <div class="text-center text-grey-6 q-py-md">暫無折扣記錄</div>
                </template>
              </q-table>
              <div class="text-caption text-grey-7 q-mt-sm">
                總折扣金額：<span class="text-weight-bold text-primary">${{ totalDiscountAmount.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Add Discount Form -->
            <q-separator class="q-my-md" />
            <div class="text-subtitle2 q-mb-sm">新增折扣記錄</div>
            <q-form @submit="handleAddDiscount">
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="discountForm.discountType"
                    label="折扣類型 *"
                    outlined
                    dense
                    :options="discountTypeOptions"
                    :rules="[val => !!val || '請選擇折扣類型']"
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model="discountForm.discountCode"
                    label="折扣代碼"
                    outlined
                    dense
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="discountForm.discountAmount"
                    label="折扣金額 *"
                    outlined
                    dense
                    type="number"
                    step="0.01"
                    prefix="$"
                    :rules="[val => val >= 0 || '折扣金額不能小於0']"
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="discountForm.discountPercentage"
                    label="折扣百分比"
                    outlined
                    dense
                    type="number"
                    step="0.01"
                    suffix="%"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="discountForm.description"
                    label="折扣描述"
                    outlined
                    dense
                    type="textarea"
                    rows="2"
                  />
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="關閉" color="grey-7" v-close-popup />
            <q-btn unelevated label="新增折扣" color="primary" @click="handleAddDiscount" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useQuasar } from 'quasar'
import { orderApi, type Order, type OrderItem, type PageResponse } from '@/api'
import { crmApi, type Customer } from '@/api/crm'
import { productApi, type Product } from '@/api/product'
import { orderDiscountApi, type OrderDiscount } from '@/api/orderDiscount'

const $q = useQuasar()

const orders = ref<Order[]>([])
const loading = ref(false)
const showDialog = ref(false)
const editingOrderId = ref<number | null>(null)
const customers = ref<Customer[]>([])
const products = ref<Product[]>([])
const orderDiscounts = ref<OrderDiscount[]>([])
const showDiscountDialog = ref(false)
const discountForm = ref<OrderDiscount>({
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

// 訂單表單
const form = ref<{
  customerId: number | null
  status: Order['status']
  pickupType: 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP'
  items: Array<OrderItem & { tempId?: number; subtotal?: number }>
  subtotalAmount: number
  discountAmount: number
  shippingFee: number
  totalAmount: number
  shippingAddress?: string
  notes?: string
}>({
  customerId: null,
  status: 'PENDING_PAYMENT',
  pickupType: 'DELIVERY',
  items: [],
  subtotalAmount: 0,
  discountAmount: 0,
  shippingFee: 0,
  totalAmount: 0,
  shippingAddress: '',
  notes: ''
})

let tempIdCounter = 0

const customerOptions = ref<Customer[]>([])
const allCustomers = ref<Customer[]>([])
const customerInputValue = ref<string>('')
const customerSearchLoading = ref(false)

const filterCustomers = async (val: string, update: (callback: () => void) => void) => {
  customerInputValue.value = val
  
  if (val === '') {
    update(() => {
      customerOptions.value = allCustomers.value
    })
    return
  }

  // 如果輸入長度小於2，只從已載入的客戶中搜索
  if (val.length < 2) {
    update(() => {
      const needle = val.toLowerCase()
      customerOptions.value = allCustomers.value.filter(
        customer => 
          customer.name?.toLowerCase().includes(needle) ||
          customer.email?.toLowerCase().includes(needle) ||
          customer.phone?.toLowerCase().includes(needle)
      )
    })
    return
  }

  // 當輸入長度 >= 2 時，從服務器搜索
  customerSearchLoading.value = true
  try {
    // 使用後端的搜索 API
    const response = await crmApi.searchCustomers(val, { page: 0, size: 20 })
    
    update(() => {
      if (response.success && response.data) {
        const searchResults = Array.isArray(response.data) 
          ? response.data 
          : (response.data as any)?.content || []
        
        // 合併搜索結果和已載入的客戶，去重
        const merged = [...allCustomers.value]
        searchResults.forEach((customer: Customer) => {
          if (!merged.find(c => c.id === customer.id)) {
            merged.push(customer)
          }
        })
        
        // 更新已載入的客戶列表
        allCustomers.value = merged
        
        // 過濾匹配的客戶（支持姓名、郵箱、電話）
        const needle = val.toLowerCase()
        customerOptions.value = merged.filter(
          customer => 
            customer.name?.toLowerCase().includes(needle) ||
            customer.email?.toLowerCase().includes(needle) ||
            customer.phone?.toLowerCase().includes(needle)
        )
      } else {
        // 如果搜索失敗，使用本地過濾
        const needle = val.toLowerCase()
        customerOptions.value = allCustomers.value.filter(
          customer => 
            customer.name?.toLowerCase().includes(needle) ||
            customer.email?.toLowerCase().includes(needle) ||
            customer.phone?.toLowerCase().includes(needle)
        )
      }
    })
  } catch (error) {
    console.error('Failed to search customers:', error)
    // 搜索失敗時使用本地過濾
    update(() => {
      const needle = val.toLowerCase()
      customerOptions.value = allCustomers.value.filter(
        customer => 
          customer.name?.toLowerCase().includes(needle) ||
          customer.email?.toLowerCase().includes(needle) ||
          customer.phone?.toLowerCase().includes(needle)
      )
    })
  } finally {
    customerSearchLoading.value = false
  }
}

const onCustomerInput = (val: string) => {
  customerInputValue.value = val
  // 如果用戶輸入文字但沒有選擇客戶，將輸入的文字保存到客戶姓名
  if (val && !form.value.customerId) {
    form.value.customerName = val
  }
}
const productOptions = computed(() => products.value)

const pickupTypeOptions = [
  { label: '宅配', value: 'DELIVERY' },
  { label: '門市取貨', value: 'STORE_PICKUP' },
  { label: '跨店取貨', value: 'CROSS_STORE_PICKUP' }
]

const itemColumns = [
  { name: 'product', label: '商品', align: 'left' as const, field: 'productId' },
  { name: 'quantity', label: '數量', align: 'center' as const, field: 'quantity' },
  { name: 'unitPrice', label: '單價', align: 'left' as const, field: 'unitPrice' },
  { name: 'subtotal', label: '小計', align: 'right' as const, field: 'subtotal' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderNumber', label: '訂單號', align: 'left' as const, field: 'orderNumber' },
  { name: 'customerName', label: '客戶', align: 'left' as const, field: 'customerName' },
  { name: 'totalAmount', label: '總金額', align: 'left' as const, field: 'totalAmount', sortable: true },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'createdAt', label: '創建時間', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const discountColumns = [
  { name: 'discountType', label: '折扣類型', align: 'center' as const, field: 'discountType' },
  { name: 'discountCode', label: '折扣代碼', align: 'left' as const, field: 'discountCode' },
  { name: 'discountAmount', label: '折扣金額', align: 'right' as const, field: 'discountAmount' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const discountTypeOptions = ['COUPON', 'PROMOTION', 'MEMBER_DISCOUNT', 'SEASONAL', 'BULK_ORDER', 'OTHER']

const totalDiscountAmount = computed(() => {
  return orderDiscounts.value.reduce((sum, discount) => sum + (discount.discountAmount || 0), 0)
})

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

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderApi.getOrders()
    const data = response.data as PageResponse<Order> | Order[]
    let orderList: Order[] = []
    if (Array.isArray(data)) {
      orderList = data
    } else if (data && 'content' in data) {
      orderList = data.content
    } else {
      orderList = []
    }
    
    // 如果訂單的 customerName 為空但有 customerId，從已載入的客戶列表中查找並填充
    orderList.forEach(order => {
      if (!order.customerName && order.customerId) {
        const customer = customers.value.find(c => c.id === order.customerId)
        if (customer) {
          order.customerName = customer.name
        }
      }
    })
    
    orders.value = orderList
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入訂單清單失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusColor = (status: Order['status']) => {
  const colorMap: Record<string, string> = {
    PENDING: 'grey',
    PENDING_PAYMENT: 'grey',
    PROCESSING: 'warning',
    SHIPPED: 'info',
    DELIVERED: 'positive',
    COMPLETED: 'positive',
    CANCELLED: 'negative',
    REFUNDED: 'negative'
  }
  return colorMap[status] || 'grey'
}

const getStatusLabel = (status: Order['status']) => {
  const labelMap: Record<string, string> = {
    PENDING: '待處理',
    PENDING_PAYMENT: '待付款',
    PROCESSING: '處理中',
    SHIPPED: '已發貨',
    DELIVERED: '已送達',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return labelMap[status] || status
}

const handleStatusChange = async (id?: number, status?: Order['status']) => {
  if (!id || !status) return
  
  // 對於取消狀態，需要確認
  if (status === 'CANCELLED') {
    $q.dialog({
      title: '警告',
      message: '確定要取消此訂單嗎？此操作無法復原。',
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
      message: '狀態更新成功',
      position: 'top'
    })
    loadOrders()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '狀態更新失敗',
      position: 'top'
    })
  }
}

const loadCustomers = async () => {
  try {
    // 載入前20個客戶作為初始列表
    const response = await crmApi.getCustomers({ page: 0, size: 20 })
    if (response.success && response.data) {
      const customerList = Array.isArray(response.data) 
        ? response.data 
        : (response.data as any)?.content || []
      customers.value = customerList
      allCustomers.value = customerList
      customerOptions.value = customerList
    }
  } catch (error) {
    console.error('Failed to load customers:', error)
  }
}

const loadProducts = async () => {
  try {
    const response = await productApi.getProducts()
    const data = response.data as PageResponse<Product> | Product[]
    if (Array.isArray(data)) {
      products.value = data
    } else if (data && 'content' in data) {
      products.value = data.content
    }
  } catch (error) {
    console.error('Failed to load products:', error)
  }
}

const handleAdd = () => {
  resetForm()
  showDialog.value = true
}

const resetForm = () => {
  form.value = {
    customerId: null,
    customerName: '',
    customerPhone: '',
    customerEmail: '',
    status: 'PENDING_PAYMENT',
    pickupType: 'DELIVERY',
    items: [],
    subtotalAmount: 0,
    discountAmount: 0,
    shippingFee: 0,
    totalAmount: 0,
    shippingAddress: '',
    notes: ''
  }
  editingOrderId.value = null
  tempIdCounter = 0
  customerInputValue.value = ''
  orderDiscounts.value = []
  discountForm.value = {
    orderId: 0,
    discountType: '',
    discountCode: '',
    discountAmount: 0,
    discountPercentage: 0,
    description: ''
  }
}

const handleDelete = (order: Order) => {
  if (!order.id) return
  
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除訂單 ${order.orderNumber || order.id} 嗎？此操作無法復原。`,
    cancel: true,
    persistent: true,
    color: 'negative'
  }).onOk(async () => {
    try {
      await orderApi.deleteOrder(order.id!)
      $q.notify({
        type: 'positive',
        message: '訂單已刪除',
        position: 'top'
      })
      loadOrders()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '刪除訂單失敗',
        position: 'top'
      })
      console.error('Failed to delete order:', error)
    }
  })
}

const handleEdit = async (order: Order) => {
  if (!order.id) return
  
  editingOrderId.value = order.id
  showDialog.value = true
  
  try {
    // 載入訂單詳情
    const response = await orderApi.getOrder(order.id)
    if (response.success && response.data) {
      const orderData = response.data
      
      // 填充表單數據
      form.value.customerId = orderData.customerId || null
      form.value.customerName = orderData.customerName || ''
      form.value.customerPhone = orderData.customerPhone || ''
      form.value.customerEmail = orderData.customerEmail || ''
      form.value.status = orderData.status
      form.value.pickupType = orderData.pickupType as 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP'
      form.value.subtotalAmount = orderData.subtotalAmount || 0
      form.value.discountAmount = orderData.discountAmount || 0
      form.value.shippingFee = orderData.shippingFee || 0
      form.value.totalAmount = orderData.totalAmount || 0
      form.value.shippingAddress = orderData.shippingAddress || ''
      form.value.notes = orderData.notes || ''
      
      // 填充訂單項目
      if (orderData.items && orderData.items.length > 0) {
        form.value.items = orderData.items.map((item: any, index: number) => ({
          tempId: ++tempIdCounter,
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice || item.price || 0,
          subtotal: (item.unitPrice || item.price || 0) * (item.quantity || 0),
          productName: item.productName
        }))
        calculateTotal()
      }
      
      // 設置客戶輸入值
      if (orderData.customerName) {
        customerInputValue.value = orderData.customerName
      }
      
      // 載入訂單的折扣記錄
      await loadOrderDiscounts(order.id!)
    }
  } catch (error) {
    console.error('Failed to load order:', error)
    $q.notify({
      type: 'negative',
      message: '載入訂單失敗',
      position: 'top'
    })
  }
}

const addOrderItem = () => {
  form.value.items.push({
    tempId: ++tempIdCounter,
    productId: 0,
    quantity: 1,
    unitPrice: 0,
    subtotal: 0
  })
}

const removeOrderItem = (index: number) => {
  form.value.items.splice(index, 1)
  calculateTotal()
}

const onProductChange = (item: any, productId: number) => {
  const product = products.value.find(p => p.id === productId)
  if (product) {
    item.unitPrice = product.salePrice ?? product.basePrice ?? 0
    item.productName = product.name
    calculateItemSubtotal(item)
  }
}

const onCustomerChange = (customerId: number | null) => {
  if (customerId) {
    const customer = customers.value.find(c => c.id === customerId)
    if (customer) {
      // 自動填充客戶資訊
      form.value.customerName = customer.name
      form.value.customerPhone = customer.phone || ''
      form.value.customerEmail = customer.email
      customerInputValue.value = customer.name
    }
  } else {
    // 清除選擇時，保持輸入框中的文字
    // customerInputValue 會保持用戶輸入的文字
  }
}

const calculateItemSubtotal = (item: any) => {
  item.subtotal = (item.unitPrice || 0) * (item.quantity || 0)
  calculateTotal()
}

const openDiscountDialog = () => {
  if (!editingOrderId.value) {
    $q.notify({
      type: 'negative',
      message: '請先保存訂單後才能管理折扣記錄',
      position: 'top',
      timeout: 3000,
      actions: [
        { label: '知道了', color: 'white' }
      ]
    })
    return
  }
  
  // 設置訂單ID
  discountForm.value.orderId = editingOrderId.value
  showDiscountDialog.value = true
  
  // 載入該訂單的折扣記錄
  loadOrderDiscounts(editingOrderId.value)
}

const loadOrderDiscounts = async (orderId: number) => {
  try {
    const response = await orderDiscountApi.getDiscountsByOrderId(orderId)
    if (response.success && response.data) {
      orderDiscounts.value = response.data
    } else {
      orderDiscounts.value = []
    }
  } catch (error) {
    console.error('Failed to load order discounts:', error)
    orderDiscounts.value = []
  }
}

// 監聽折扣記錄變化，自動更新折扣金額
watch(totalDiscountAmount, (newValue) => {
  if (editingOrderId.value) {
    form.value.discountAmount = newValue
    calculateTotal()
  }
}, { immediate: true })

// 監聽折扣對話框打開，自動設置訂單ID
watch(showDiscountDialog, (isOpen) => {
  if (isOpen && editingOrderId.value) {
    discountForm.value.orderId = editingOrderId.value
  }
})

const handleAddDiscount = async () => {
  if (!discountForm.value.discountType || discountForm.value.discountAmount === undefined || discountForm.value.discountAmount < 0) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  // 確保訂單ID已設置
  if (!editingOrderId.value) {
    $q.notify({
      type: 'warning',
      message: '請先保存訂單',
      position: 'top'
    })
    return
  }

  // 確保訂單ID與當前編輯的訂單一致（防止不一致的情況）
  if (!discountForm.value.orderId || discountForm.value.orderId !== editingOrderId.value) {
    discountForm.value.orderId = editingOrderId.value
  }

  try {
    await orderDiscountApi.addDiscount(discountForm.value)
    $q.notify({
      type: 'positive',
      message: '折扣添加成功',
      position: 'top'
    })
    // 重置表單
    discountForm.value = {
      orderId: editingOrderId.value,
      discountType: '',
      discountCode: '',
      discountAmount: 0,
      discountPercentage: 0,
      description: ''
    }
    // 重新載入折扣記錄
    await loadOrderDiscounts(editingOrderId.value)
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '添加折扣失敗',
      position: 'top'
    })
  }
}

const handleDeleteDiscount = (discountId: number) => {
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這條折扣記錄嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderDiscountApi.deleteDiscount(discountId)
      $q.notify({
        type: 'positive',
        message: '折扣刪除成功',
        position: 'top'
      })
      // 重新載入折扣記錄
      if (editingOrderId.value) {
        await loadOrderDiscounts(editingOrderId.value)
      }
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '刪除折扣失敗',
        position: 'top'
      })
    }
  })
}

const calculateTotal = () => {
  // 計算小計
  form.value.subtotalAmount = form.value.items.reduce((sum, item) => {
    return sum + (item.subtotal || 0)
  }, 0)

  // 計算總金額 = 小計 - 折扣 + 運費
  form.value.totalAmount = form.value.subtotalAmount - (form.value.discountAmount || 0) + (form.value.shippingFee || 0)
}

const handleSubmit = async () => {
  // 驗證訂單項目
  if (!form.value.items || form.value.items.length === 0) {
    $q.notify({
      type: 'warning',
      message: '請至少添加一個訂單項目',
      position: 'top'
    })
    return
  }

  // 驗證所有項目
  for (const item of form.value.items) {
    if (!item.productId || item.quantity <= 0 || item.unitPrice <= 0) {
      $q.notify({
        type: 'warning',
        message: '請完整填寫所有訂單項目',
        position: 'top'
      })
      return
    }
  }

  try {
    // 準備訂單數據
    const orderData: any = {
      status: form.value.status,
      pickupType: form.value.pickupType,
      subtotalAmount: form.value.subtotalAmount,
      discountAmount: form.value.discountAmount || 0,
      shippingFee: form.value.shippingFee || 0,
      totalAmount: form.value.totalAmount,
      shippingAddress: form.value.shippingAddress || '',
      notes: form.value.notes || '',
      items: form.value.items.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      }))
    }

    // 如果有選擇客戶ID，使用ID；否則使用手動輸入的信息（設置 customerId 為 0 以避免後端驗證失敗）
    if (form.value.customerId) {
      orderData.customerId = form.value.customerId
      // 即使有 customerId，也保存客戶信息（用於顯示和備份）
      if (form.value.customerName) {
        orderData.customerName = form.value.customerName
      }
      if (form.value.customerPhone) {
        orderData.customerPhone = form.value.customerPhone
      }
      if (form.value.customerEmail) {
        orderData.customerEmail = form.value.customerEmail
      }
    } else {
      // 手動輸入時，customerId 設為 0（表示未註冊客戶），使用客戶信息
      orderData.customerId = 0
      if (form.value.customerName) {
        orderData.customerName = form.value.customerName
      }
      if (form.value.customerPhone) {
        orderData.customerPhone = form.value.customerPhone
      }
      if (form.value.customerEmail) {
        orderData.customerEmail = form.value.customerEmail
      }
    }

    if (editingOrderId.value) {
      // 更新訂單
      const response = await orderApi.updateOrder(editingOrderId.value, orderData)
      $q.notify({
        type: 'positive',
        message: '訂單更新成功',
        position: 'top'
      })
      // 更新訂單ID（雖然通常不會改變）
      if (response.success && response.data?.id) {
        editingOrderId.value = response.data.id
      }
    } else {
      // 創建訂單
      const response = await orderApi.createOrder(orderData)
      if (response.success && response.data?.id) {
        // 設置訂單ID，以便可以添加折扣記錄
        editingOrderId.value = response.data.id
        // 更新折扣表單的訂單ID
        discountForm.value.orderId = response.data.id
      }
      $q.notify({
        type: 'positive',
        message: '訂單創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    resetForm()
    loadOrders()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '創建訂單失敗',
      position: 'top'
    })
    console.error('Failed to create order:', error)
  }
}

onMounted(() => {
  loadOrders()
  loadCustomers()
  loadProducts()
})
</script>
