<template>
  <div class="product-management">
    <h1>商品管理</h1>

    <!-- 工具欄 -->
    <div class="toolbar">
      <div class="filters">
        <select v-model="selectedCategory" @change="filterByCategory">
          <option value="">所有分類</option>
          <option v-for="category in productStore.categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <select v-model="selectedStatus" @change="filterByStatus">
          <option value="">所有狀態</option>
          <option value="ACTIVE">上架</option>
          <option value="INACTIVE">下架</option>
          <option value="DRAFT">草稿</option>
          <option value="OUT_OF_STOCK">缺貨</option>
        </select>
      </div>
      <button class="btn-primary" @click="showCreateDialog = true">新增商品</button>
    </div>

    <!-- 錯誤提示 -->
    <div v-if="productStore.error" class="error-message">
      {{ productStore.error }}
    </div>

    <!-- 商品列表 -->
    <div v-if="productStore.loading" class="loading">載入中...</div>
    <div v-else class="product-grid">
      <div v-for="product in productStore.products" :key="product.id" class="product-card">
        <div class="product-header">
          <h3>{{ product.name }}</h3>
          <span :class="['status-badge', product.status?.toLowerCase()]">
            {{ product.status }}
          </span>
        </div>
        <div class="product-info">
          <p><strong>SKU:</strong> {{ product.sku }}</p>
          <p><strong>價格:</strong> NT$ {{ product.basePrice }}</p>
          <p v-if="product.salePrice"><strong>特價:</strong> NT$ {{ product.salePrice }}</p>
          <p><strong>庫存:</strong> {{ product.stock || 0 }}</p>
          <p><strong>銷售模式:</strong> {{ product.salesMode }}</p>
        </div>
        <div class="product-actions">
          <button class="btn-small" @click="editProduct(product)">編輯</button>
          <button class="btn-small btn-danger" @click="confirmDelete(product.id!)">刪除</button>
        </div>
      </div>
    </div>

    <!-- 分頁 -->
    <div class="pagination">
      <button
        :disabled="productStore.pagination.page === 0"
        @click="changePage(productStore.pagination.page - 1)"
      >
        上一頁
      </button>
      <span>
        第 {{ productStore.pagination.page + 1 }} / {{ productStore.pagination.totalPages }} 頁
      </span>
      <button
        :disabled="productStore.pagination.page >= productStore.pagination.totalPages - 1"
        @click="changePage(productStore.pagination.page + 1)"
      >
        下一頁
      </button>
    </div>

    <!-- 新增/編輯商品對話框 -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>{{ editingProduct ? '編輯商品' : '新增商品' }}</h2>
        <form @submit.prevent="saveProduct">
          <div class="form-group">
            <label for="name">商品名稱 *</label>
            <input id="name" v-model="productForm.name" type="text" required />
          </div>
          <div class="form-group">
            <label for="sku">SKU *</label>
            <input id="sku" v-model="productForm.sku" type="text" required />
          </div>
          <div class="form-group">
            <label for="categoryId">商品分類</label>
            <select id="categoryId" v-model="productForm.categoryId">
              <option value="">請選擇分類</option>
              <option
                v-for="category in productStore.categories"
                :key="category.id"
                :value="category.id"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="basePrice">基礎價格 *</label>
            <input id="basePrice" v-model.number="productForm.basePrice" type="number" required />
          </div>
          <div class="form-group">
            <label for="salePrice">特價</label>
            <input id="salePrice" v-model.number="productForm.salePrice" type="number" />
          </div>
          <div class="form-group">
            <label for="stock">庫存</label>
            <input id="stock" v-model.number="productForm.stock" type="number" />
          </div>
          <div class="form-group">
            <label for="status">狀態</label>
            <select id="status" v-model="productForm.status">
              <option value="DRAFT">草稿</option>
              <option value="ACTIVE">上架</option>
              <option value="INACTIVE">下架</option>
              <option value="OUT_OF_STOCK">缺貨</option>
            </select>
          </div>
          <div class="form-group">
            <label for="salesMode">銷售模式</label>
            <select id="salesMode" v-model="productForm.salesMode">
              <option value="NORMAL">一般</option>
              <option value="PRE_ORDER">預購</option>
              <option value="VOUCHER">票券</option>
              <option value="SUBSCRIPTION">訂閱</option>
              <option value="STORE_ONLY">門市限定</option>
            </select>
          </div>
          <div class="form-group">
            <label for="description">商品描述</label>
            <textarea id="description" v-model="productForm.description" rows="4"></textarea>
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
import { useProductStore } from '@/stores/modules/product'
import type { Product } from '@/api'

const productStore = useProductStore()
const showCreateDialog = ref(false)
const editingProduct = ref<Product | null>(null)
const selectedCategory = ref<number | string>('')
const selectedStatus = ref('')

const productForm = ref<Product>({
  name: '',
  sku: '',
  categoryId: undefined,
  basePrice: 0,
  salePrice: undefined,
  stock: 0,
  status: 'DRAFT',
  salesMode: 'NORMAL',
  description: ''
})

onMounted(() => {
  productStore.fetchProducts()
  productStore.fetchCategories()
})

function changePage(page: number) {
  if (selectedCategory.value) {
    productStore.fetchProductsByCategory(Number(selectedCategory.value), page)
  } else if (selectedStatus.value) {
    productStore.fetchProductsByStatus(selectedStatus.value, page)
  } else {
    productStore.fetchProducts(page, productStore.pagination.size)
  }
}

function filterByCategory() {
  if (selectedCategory.value) {
    selectedStatus.value = ''
    productStore.fetchProductsByCategory(Number(selectedCategory.value))
  } else {
    productStore.fetchProducts()
  }
}

function filterByStatus() {
  if (selectedStatus.value) {
    selectedCategory.value = ''
    productStore.fetchProductsByStatus(selectedStatus.value)
  } else {
    productStore.fetchProducts()
  }
}

function editProduct(product: Product) {
  editingProduct.value = product
  productForm.value = { ...product }
  showCreateDialog.value = true
}

async function saveProduct() {
  try {
    if (editingProduct.value && editingProduct.value.id) {
      await productStore.updateProduct(editingProduct.value.id, productForm.value)
      alert('商品更新成功')
    } else {
      await productStore.createProduct(productForm.value)
      alert('商品新增成功')
    }
    closeDialog()
  } catch (error) {
    alert('操作失敗，請稍後再試')
  }
}

async function confirmDelete(id: number) {
  if (confirm('確定要刪除此商品嗎？')) {
    try {
      await productStore.deleteProduct(id)
      alert('商品刪除成功')
    } catch (error) {
      alert('刪除失敗，請稍後再試')
    }
  }
}

function closeDialog() {
  showCreateDialog.value = false
  editingProduct.value = null
  productForm.value = {
    name: '',
    sku: '',
    categoryId: undefined,
    basePrice: 0,
    salePrice: undefined,
    stock: 0,
    status: 'DRAFT',
    salesMode: 'NORMAL',
    description: ''
  }
}
</script>

<style scoped>
.product-management {
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
}

.filters select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
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

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 15px;
}

.product-header h3 {
  margin: 0;
  font-size: 18px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.status-badge.draft {
  background-color: #fff3cd;
  color: #856404;
}

.product-info {
  margin-bottom: 15px;
}

.product-info p {
  margin: 5px 0;
  font-size: 14px;
}

.product-actions {
  display: flex;
  gap: 10px;
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
  padding: 6px 12px;
  font-size: 12px;
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
