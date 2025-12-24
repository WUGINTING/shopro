<template>
  <q-page class="q-pa-md">
    <div class="product-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">商品管理</div>
          <div class="text-caption text-grey-7">管理商品信息、上架状态和库存</div>
        </div>
        <q-btn
          color="primary"
          icon="add"
          label="新增商品"
          unelevated
          @click="showDialog = true; form = { name: '', description: '', price: 0, stock: 0, status: 'DRAFT', salesMode: 'NORMAL', categoryId: null }"
        />
      </div>

      <!-- Search and Filter Bar -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6 col-md-4">
              <q-input
                v-model="searchQuery"
                outlined
                dense
                placeholder="搜索商品名称"
                clearable
              >
                <template v-slot:prepend>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="statusFilter"
                outlined
                dense
                :options="statusOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                clearable
                placeholder="商品状态"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="categoryFilter"
                outlined
                dense
                :options="categoryOptions"
                clearable
                placeholder="商品分类"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-2">
              <q-btn color="primary" unelevated class="full-width" label="搜索" icon="search" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Products Table -->
      <q-card>
        <q-table
          :rows="products"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-image="props">
            <q-td :props="props">
              <q-avatar rounded size="50px" color="grey-3">
                <q-icon name="image" />
              </q-avatar>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
            </q-td>
          </template>

          <template v-slot:body-cell-price="props">
            <q-td :props="props">
    <span class="text-weight-bold text-primary">
      ¥{{ (props.row.price || 0).toFixed(2) }}
    </span>
            </q-td>
          </template>

          <template v-slot:body-cell-stock="props">
            <q-td :props="props">
              <q-badge
                :color="props.row.stock > 10 ? 'positive' : props.row.stock > 0 ? 'warning' : 'negative'"
                :label="props.row.stock"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>编辑</q-tooltip>
              </q-btn>

              <q-btn
                flat
                dense
                round
                :icon="props.row.status === 'PUBLISHED' ? 'visibility_off' : 'visibility'"
                :color="props.row.status === 'PUBLISHED' ? 'warning' : 'positive'"
                size="sm"
                @click="handlePublishToggle(props.row)"
              >
                <q-tooltip>{{ props.row.status === 'PUBLISHED' ? '下架' : '上架' }}</q-tooltip>
              </q-btn>

              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>删除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 600px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '编辑商品' : '新增商品' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model="form.name"
                label="商品名称 *"
                outlined
                class="q-mb-md"
                :rules="[val => !!val || '请输入商品名称']"
              />

              <q-input
                v-model="form.description"
                label="商品描述"
                outlined
                type="textarea"
                rows="3"
                class="q-mb-md"
              />

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-input
                    v-model.number="form.price"
                    label="价格 *"
                    outlined
                    type="number"
                    prefix="¥"
                    :rules="[val => val >= 0 || '价格不能为负数']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="form.stock"
                    label="库存 *"
                    outlined
                    type="number"
                    :rules="[val => val >= 0 || '库存不能为负数']"
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-select
                    v-model="form.status"
                    label="商品状态 *"
                    outlined
                    :options="statusOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                  />
                </div>
                <div class="col-6">
                  <q-select
                    v-model="form.salesMode"
                    label="销售模式 *"
                    outlined
                    :options="salesModeOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-12">
                  <q-select
                    v-model="form.categoryId"
                    label="商品分类"
                    outlined
                    :options="categoryOptions"
                  />
                </div>
              </div>

              <q-file
                v-model="productImage"
                label="商品图片"
                outlined
                accept="image/*"
                class="q-mb-md"
              >
                <template v-slot:prepend>
                  <q-icon name="image" />
                </template>
              </q-file>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product } from '@/api'

const $q = useQuasar()

const products = ref<Product[]>([])
const loading = ref(false)
const showDialog = ref(false)
const searchQuery = ref('')
const statusFilter = ref(null)
const categoryFilter = ref(null)
const productImage = ref(null)

const form = ref<Product>({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  status: 'DRAFT',
  salesMode: 'NORMAL',
  categoryId: null
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'image', label: '图片', align: 'left' as const, field: 'image' },
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '商品名称', align: 'left' as const, field: 'name', sortable: true },
  { name: 'price', label: '价格', align: 'left' as const, field: 'price', sortable: true },
  { name: 'stock', label: '库存', align: 'center' as const, field: 'stock', sortable: true },
  { name: 'status', label: '状态', align: 'center' as const, field: 'status' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已上架', value: 'PUBLISHED' },
  { label: '已下架', value: 'UNPUBLISHED' }
]

const salesModeOptions = [
  { label: '正常销售', value: 'NORMAL' },
  { label: '预购商品', value: 'PRE_ORDER' },
  { label: '票券商品', value: 'TICKET' },
  { label: '订阅商品', value: 'SUBSCRIPTION' },
  { label: '门市限定', value: 'STORE_ONLY' }
]

const categoryOptions = ['电子产品', '服装', '食品', '图书', '家居']

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    if (response.data && Array.isArray((response.data as any).content)) {
      products.value = (response.data as any).content
    } else if (Array.isArray(response.data)) {
      products.value = response.data
    } else {
      products.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '加载商品列表失败',
      position: 'top'
    })
    console.error('API Error:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (product: Product) => {
  form.value = { ...product }
  showDialog.value = true
}

const handlePublishToggle = async (product: Product) => {
  try {
    if (product.status === 'PUBLISHED') {
      await productApi.deactivateProduct(product.id!)
      $q.notify({
        type: 'positive',
        message: '商品已下架',
        position: 'top'
      })
    } else {
      await productApi.activateProduct(product.id!)
      $q.notify({
        type: 'positive',
        message: '商品已上架',
        position: 'top'
      })
    }
    loadProducts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失败',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '确认删除',
    message: '确定要删除这个商品吗？此操作不可恢复。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await productApi.deleteProduct(id)
      $q.notify({
        type: 'positive',
        message: '删除成功',
        position: 'top'
      })
      loadProducts()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '删除失败',
        position: 'top'
      })
    }
  })
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await productApi.updateProduct(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await productApi.createProduct(form.value)
      $q.notify({
        type: 'positive',
        message: '创建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    form.value = { name: '', description: '', price: 0, stock: 0, status: 'DRAFT', salesMode: 'NORMAL', categoryId: null }
    loadProducts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失败',
      position: 'top'
    })
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return 'positive'
    case 'UNPUBLISHED': return 'warning'
    case 'DRAFT': return 'grey'
    default: return 'grey'
  }
}

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return '已上架'
    case 'UNPUBLISHED': return '已下架'
    case 'DRAFT': return '草稿'
    default: return status
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
