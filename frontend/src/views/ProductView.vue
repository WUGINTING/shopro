<template>
  <div class="product-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>商品管理 / Product Management</h2>
          <el-button type="primary" @click="dialogVisible = true">新增商品</el-button>
        </div>
      </template>

      <el-table :data="products" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            ${{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="商品信息" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi, type Product } from '@/api'

const products = ref<Product[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref<Product>({
  name: '',
  description: '',
  price: 0,
  stock: 0
})
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()

    // 關鍵修正點：
    // Spring Boot Page 物件的資料在 .content 屬性中
    // response.data 是 axios 回傳的 body，裡面的 .content 才是陣列
    if (response.data && Array.isArray(response.data.content)) {
      products.value = response.data.content
    } else if (Array.isArray(response.data)) {
      // 萬一後端改回傳純陣列時的相容處理
      products.value = response.data
    } else {
      products.value = []
    }

  } catch (error) {
    ElMessage.error('加载商品列表失败')
    console.error('API Error:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (product: Product) => {
  form.value = { ...product }
  dialogVisible.value = true
}

const handleDelete = async (id?: number) => {
  if (!id) return
  try {
    await productApi.deleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await productApi.updateProduct(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await productApi.createProduct(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    form.value = { name: '', description: '', price: 0, stock: 0 }
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-view {
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
