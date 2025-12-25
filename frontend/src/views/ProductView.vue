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

              <!-- Album Images Section -->
              <div class="q-mb-md">
                <div class="text-subtitle2 q-mb-sm">從相冊選擇圖片</div>
                <q-btn
                  outline
                  color="primary"
                  icon="photo_library"
                  label="選擇相冊圖片"
                  @click="showAlbumSelector = true"
                  :disable="!form.id"
                />
                <div class="text-caption text-grey-7 q-mt-xs" v-if="!form.id">
                  請先保存商品後再添加相冊圖片
                </div>

                <!-- Selected Album Images Preview -->
                <div v-if="form.id && selectedAlbumImages.length > 0" class="q-mt-md">
                  <div class="text-caption q-mb-sm">已選擇的相冊圖片：</div>
                  <div class="row q-col-gutter-sm">
                    <div v-for="img in selectedAlbumImages" :key="img.id" class="col-auto">
                      <q-img
                        :src="img.imageUrl"
                        style="width: 80px; height: 80px"
                        class="rounded-borders"
                      >
                        <q-btn
                          round
                          dense
                          flat
                          icon="close"
                          size="xs"
                          color="white"
                          class="absolute-top-right q-ma-xs"
                          @click="removeSelectedImage(img.id)"
                        />
                      </q-img>
                    </div>
                  </div>
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Album Image Selector Dialog -->
      <q-dialog v-model="showAlbumSelector" persistent>
        <q-card style="min-width: 800px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">從相冊選擇圖片</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <!-- Album Selection -->
            <q-select
              v-model="selectedAlbum"
              label="選擇相冊"
              outlined
              :options="albums"
              option-label="name"
              option-value="id"
              emit-value
              map-options
              class="q-mb-md"
              @update:model-value="loadAlbumImages"
            >
              <template v-slot:prepend>
                <q-icon name="photo_library" />
              </template>
            </q-select>

            <!-- Images Grid -->
            <div v-if="albumImages.length > 0">
              <div class="text-subtitle2 q-mb-md">選擇圖片（可多選）</div>
              <div class="row q-col-gutter-md" style="max-height: 400px; overflow-y: auto">
                <div
                  v-for="image in albumImages"
                  :key="image.id"
                  class="col-6 col-sm-4 col-md-3"
                >
                  <q-card
                    flat
                    bordered
                    class="cursor-pointer"
                    :class="{ 'bg-blue-1 border-primary': isImageSelected(image.id) }"
                    @click="toggleImageSelection(image)"
                  >
                    <q-img
                      :src="image.imageUrl"
                      :ratio="1"
                      style="height: 150px"
                    >
                      <div v-if="isImageSelected(image.id)" class="absolute-top-right q-ma-sm">
                        <q-icon name="check_circle" color="primary" size="md" />
                      </div>
                    </q-img>
                    <q-card-section v-if="image.title" class="q-pa-sm">
                      <div class="text-caption text-weight-medium ellipsis">
                        {{ image.title }}
                      </div>
                    </q-card-section>
                  </q-card>
                </div>
              </div>
            </div>

            <div v-else-if="selectedAlbum" class="text-center text-grey q-pa-xl">
              <q-icon name="image" size="64px" />
              <div class="q-mt-md">此相冊暫無圖片</div>
            </div>

            <div v-else class="text-center text-grey q-pa-xl">
              <q-icon name="photo_library" size="64px" />
              <div class="q-mt-md">請選擇相冊</div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn
              unelevated
              label="確認添加"
              color="primary"
              @click="addSelectedImagesToProduct"
              :disable="tempSelectedImages.length === 0"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product, type PageResponse } from '@/api'
import { albumApi, type Album, type AlbumImage } from '@/api/album'

const $q = useQuasar()

const products = ref<Product[]>([])
const loading = ref(false)
const showDialog = ref(false)
const searchQuery = ref('')
const statusFilter = ref(null)
const categoryFilter = ref(null)
const productImage = ref(null)

// Album-related state
const showAlbumSelector = ref(false)
const albums = ref<Album[]>([])
const selectedAlbum = ref<number | null>(null)
const albumImages = ref<AlbumImage[]>([])
const tempSelectedImages = ref<AlbumImage[]>([])
const selectedAlbumImages = ref<AlbumImage[]>([])

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
    const data = response.data as PageResponse<Product> | Product[]
    if (Array.isArray(data)) {
      products.value = data
    } else if (data && 'content' in data) {
      products.value = data.content
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
      const response = await productApi.createProduct(form.value)
      // Set the new product ID for album image selection
      if (response.data && response.data.id) {
        form.value.id = response.data.id
      }
      $q.notify({
        type: 'positive',
        message: '创建成功',
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

// Album-related functions
const loadAlbums = async () => {
  try {
    const response = await albumApi.getAlbums({ page: 0, size: 100 })
    if (response.success && response.data) {
      albums.value = response.data.content || []
    }
  } catch (error) {
    console.error('Failed to load albums:', error)
  }
}

const loadAlbumImages = async () => {
  if (!selectedAlbum.value) return
  
  try {
    const response = await albumApi.getAlbumImages(selectedAlbum.value)
    if (response.success && response.data) {
      albumImages.value = response.data
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入相冊圖片失敗',
      position: 'top'
    })
  }
}

const isImageSelected = (imageId?: number) => {
  return tempSelectedImages.value.some(img => img.id === imageId)
}

const toggleImageSelection = (image: AlbumImage) => {
  const index = tempSelectedImages.value.findIndex(img => img.id === image.id)
  if (index > -1) {
    tempSelectedImages.value.splice(index, 1)
  } else {
    tempSelectedImages.value.push(image)
  }
}

const addSelectedImagesToProduct = async () => {
  if (!form.value.id || tempSelectedImages.value.length === 0) return

  try {
    const imageIds = tempSelectedImages.value.map(img => img.id).filter((id): id is number => id !== undefined)
    await productApi.addAlbumImages(form.value.id, imageIds)
    
    // Add to selected images list
    selectedAlbumImages.value.push(...tempSelectedImages.value)
    
    $q.notify({
      type: 'positive',
      message: `已添加 ${tempSelectedImages.value.length} 張圖片`,
      position: 'top'
    })
    
    // Reset and close
    tempSelectedImages.value = []
    showAlbumSelector.value = false
    selectedAlbum.value = null
    albumImages.value = []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '添加圖片失敗',
      position: 'top'
    })
  }
}

const removeSelectedImage = (imageId?: number) => {
  const index = selectedAlbumImages.value.findIndex(img => img.id === imageId)
  if (index > -1) {
    selectedAlbumImages.value.splice(index, 1)
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
  loadAlbums()
})
</script>

<style scoped>
.product-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
