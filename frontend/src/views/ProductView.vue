<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">商品管理</div>
          <div class="text-caption text-grey-7">管理商品資訊、上架狀態和庫存</div>
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
                placeholder="搜尋商品名稱"
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
                placeholder="商品狀態"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                  v-model="categoryFilter"
                  outlined
                  dense
                  :options="categoryOptions"
                  clearable
                  placeholder="商品分類"

                  option-value="value"
                  option-label="label"
                  emit-value
                  map-options
              />
            </div>
            <div class="col-12 col-sm-6 col-md-2">
              <q-btn color="primary" unelevated class="full-width" label="搜尋" icon="search" />
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
              <q-avatar v-if="getProductImageUrl(props.row)" rounded size="50px">
                <q-img 
                  :src="getProductImageUrl(props.row)" 
                  :ratio="1"
                  @error="(err) => console.error('圖片載入失敗:', getProductImageUrl(props.row), err, props.row)"
                />
              </q-avatar>
              <q-avatar v-else rounded size="50px" color="grey-3">
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
                <q-tooltip>編輯</q-tooltip>
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
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 600px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯商品' : '新增商品' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model="form.name"
                label="商品名稱 *"
                outlined
                class="q-mb-md"
                :rules="[val => !!val || '請輸入商品名稱']"
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
                    label="價格 *"
                    outlined
                    type="number"
                    prefix="¥"
                    :rules="[val => val >= 0 || '價格不能為負數']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="form.stock"
                    label="庫存 *"
                    outlined
                    type="number"
                    :rules="[val => val >= 0 || '庫存不能為負數']"
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-select
                    v-model="form.status"
                    label="商品狀態 *"
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
                    label="銷售模式 *"
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
                      label="商品分類"
                      outlined
                      :options="categoryOptions"

                      option-value="value"
                      option-label="label"
                      emit-value
                      map-options
                  />
                </div>
              </div>

              <q-file
                v-model="productImage"
                label="商品圖片"
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
            <q-btn flat label="取消" color="grey-7" @click="closeDialog" />
            <q-btn unelevated label="儲存" color="primary" @click="handleSubmit" />
            <q-btn v-if="form.id" unelevated label="完成" color="positive" @click="closeDialog" />
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
import { ref, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, categoryApi, type Product, type ProductCategory, type PageResponse } from '@/api'
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
const defaultAlbumId = ref<number | null>(null)

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
  { name: 'image', label: '圖片', align: 'left' as const, field: 'image' },
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '商品名稱', align: 'left' as const, field: 'name', sortable: true },
  { name: 'price', label: '價格', align: 'left' as const, field: 'price', sortable: true },
  { name: 'stock', label: '庫存', align: 'center' as const, field: 'stock', sortable: true },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已上架', value: 'PUBLISHED' },
  { label: '已下架', value: 'UNPUBLISHED' }
]

const salesModeOptions = [
  { label: '正常銷售', value: 'NORMAL' },
  { label: '預購商品', value: 'PRE_ORDER' },
  { label: '票券商品', value: 'TICKET' },
  { label: '訂閱商品', value: 'SUBSCRIPTION' },
  { label: '門市限定', value: 'STORE_ONLY' }
]

const categories = ref<ProductCategory[]>([])
const categoryOptions = computed(() => {
  return categories.value.map(cat => ({
    label: cat.name,
    value: cat.id
  })).filter(opt => opt.value !== undefined)
})

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    const data = response.data as PageResponse<Product> | Product[]
    let productList: Product[] = []
    if (Array.isArray(data)) {
      productList = data
    } else if (data && 'content' in data) {
      productList = data.content
    }
    
    // 將 basePrice/salePrice 轉換為 price（優先使用 salePrice，如果沒有則使用 basePrice）
    products.value = productList.map(product => ({
      ...product,
      price: product.salePrice ?? product.basePrice ?? 0
    }))
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入商品清單失敗',
      position: 'top'
    })
    console.error('API Error:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = async (product: Product) => {
  // 將 basePrice/salePrice 轉換為 price（優先使用 salePrice，如果沒有則使用 basePrice）
  form.value = {
    ...product,
    price: product.salePrice ?? product.basePrice ?? 0
  }
  showDialog.value = true

  // Load existing album images for this product if it has images
  if (product.id && product.images && product.images.length > 0) {
    try {
      // Try to match product images with album images
      // This is a best-effort approach since we need to query albums for matching URLs
      selectedAlbumImages.value = []

      // Load all albums and their images to find matches
      const albumsResponse = await albumApi.getAlbums({ page: 0, size: 100 })
      if (albumsResponse.success && albumsResponse.data) {
        const allAlbums = albumsResponse.data.content || []

        // For each album, load images and check if they match product images
        for (const album of allAlbums) {
          if (album.id) {
            const imagesResponse = await albumApi.getAlbumImages(album.id)
            if (imagesResponse.success && imagesResponse.data) {
              const albumImages = imagesResponse.data
              // Check if any product images match album images
              for (const productImage of product.images) {
                const matchingAlbumImage = albumImages.find(
                  (albumImg) => albumImg.imageUrl === productImage
                )
                if (matchingAlbumImage && !selectedAlbumImages.value.some(img => img.id === matchingAlbumImage.id)) {
                  selectedAlbumImages.value.push(matchingAlbumImage)
                }
              }
            }
          }
        }
      }
    } catch (error) {
      console.error('Failed to load existing album images:', error)
    }
  } else {
    selectedAlbumImages.value = []
  }
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
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這個商品嗎？此操作無法復原。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await productApi.deleteProduct(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadProducts()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '操作失敗',
        position: 'top'
      })
    }
  })
}
const handleSubmit = async () => {
  try {
    // 1. 複製一份表單資料，避免直接修改影響 UI 顯示
    // 使用 'any' 類型轉換是為了方便刪除屬性而不報錯
    const payload: any = { ...form.value }

    // 2. --- 關鍵修改：欄位轉換 ---
    // 將前端的 'price' 填入後端需要的價格欄位
    payload.basePrice = form.value.price
    payload.salePrice = form.value.price
    payload.costPrice = 0 // 必須給個預設值，否則後端 @DecimalMin 檢查可能會擋

    // 3. 移除後端不認識的欄位
    delete payload.price // 後端沒有 'price' 欄位，刪掉避免報錯

    // !!! 注意 !!!
    // 如果您還沒在 Java DTO 加入 'stock' 欄位，請把下面這行取消註解，否則後端會報錯
    // delete payload.stock

    // 4. 先創建或更新商品（需要先有商品ID才能添加圖片）
    let productId = form.value.id
    if (!productId) {
      const response = await productApi.createProduct(payload)
      // 更新 form ID 以便後續操作
      if (response.data && response.data.id) {
        form.value.id = response.data.id
        productId = response.data.id
      }
    } else {
      await productApi.updateProduct(productId, payload)
    }

    // 5. 處理直接上傳的圖片（需要商品ID）
    let uploadedImageId: number | null = null
    if (productImage.value && productId) {
      // 確保預設相冊存在
      if (!defaultAlbumId.value) {
        await ensureDefaultAlbum()
      }

      if (defaultAlbumId.value) {
        try {
          // 將圖片上傳到預設相冊
          const uploadResponse = await albumApi.uploadImage(
            defaultAlbumId.value,
            productImage.value as File,
            `商品圖片 - ${form.value.name || '未命名'}`
          )

          if (uploadResponse.success && uploadResponse.data && uploadResponse.data.id) {
            uploadedImageId = uploadResponse.data.id
            // 將上傳的圖片添加到 selectedAlbumImages（用於顯示）
            selectedAlbumImages.value.push(uploadResponse.data)
          }
        } catch (uploadError) {
          console.error('上傳圖片失敗:', uploadError)
          $q.notify({
            type: 'negative',
            message: '圖片上傳失敗',
            position: 'top'
          })
        }
      }
    }

    // 6. 收集所有要添加到商品的圖片ID（包括新上傳的和已選中的相冊圖片）
    const imageIdsToAdd: number[] = []
    
    // 添加新上傳的圖片ID
    if (uploadedImageId) {
      imageIdsToAdd.push(uploadedImageId)
    }
    
    // 添加已選中的相冊圖片ID
    const selectedImageIds = selectedAlbumImages.value
      .map(img => img.id)
      .filter((id): id is number => id !== undefined && id !== uploadedImageId) // 避免重複添加
    
    imageIdsToAdd.push(...selectedImageIds)

    // 7. 將所有圖片添加到商品
    if (productId && imageIdsToAdd.length > 0) {
      try {
        await productApi.addAlbumImages(productId, imageIdsToAdd)
      } catch (error) {
        console.error('添加圖片到商品失敗:', error)
        $q.notify({
          type: 'negative',
          message: '添加圖片到商品失敗',
          position: 'top'
        })
      }
    }

    // 8. 顯示成功訊息
    $q.notify({
      type: 'positive',
      message: productId ? '更新成功' : '創建成功',
      position: 'top'
    })

    // 9. 重置圖片上傳欄位
    productImage.value = null

    loadProducts()
  } catch (error) {
    console.error(error) // 建議印出錯誤以便除錯
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const closeDialog = async () => {
  // If there are selected album images and a product ID, save them before closing
  if (form.value.id && selectedAlbumImages.value.length > 0) {
    try {
      // Convert images to ProductImageDTO format for backend
      const productData = { ...form.value }
      productData.images = selectedAlbumImages.value.map(img => ({
        imageUrl: img.imageUrl || '',
        albumImageId: img.id
      }))
      await productApi.updateProduct(form.value.id, productData)
      await loadProducts()
    } catch (error) {
      console.error('Failed to save images on close:', error)
    }
  }

  showDialog.value = false
  form.value = { name: '', description: '', price: 0, stock: 0, status: 'DRAFT', salesMode: 'NORMAL', categoryId: null }
  selectedAlbumImages.value = []
  productImage.value = null
}

// Album-related functions
const loadAlbums = async () => {
  try {
    const response = await albumApi.getAlbums({ page: 0, size: 100 })
    if (response.success && response.data) {
      albums.value = response.data.content || []
      // 查找或創建預設相冊（用於儲存直接上傳的商品圖片）
      await ensureDefaultAlbum()
    }
  } catch (error) {
    console.error('Failed to load albums:', error)
  }
}

// 確保預設相冊存在
const ensureDefaultAlbum = async () => {
  try {
    // 查找名為「商品圖片」的相冊
    const defaultAlbumName = '商品圖片'
    const existingAlbum = albums.value.find(album => album.name === defaultAlbumName)
    
    if (existingAlbum && existingAlbum.id) {
      defaultAlbumId.value = existingAlbum.id
    } else {
      // 如果不存在，創建一個
      const response = await albumApi.createAlbum({
        name: defaultAlbumName,
        description: '用於儲存商品圖片的預設相冊'
      })
      if (response.success && response.data && response.data.id) {
        defaultAlbumId.value = response.data.id
        albums.value.push(response.data)
      }
    }
  } catch (error) {
    console.error('Failed to ensure default album:', error)
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

    // Add only new images to avoid duplicates
    tempSelectedImages.value.forEach(img => {
      if (!selectedAlbumImages.value.some(existing => existing.id === img.id)) {
        selectedAlbumImages.value.push(img)
      }
    })

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
  // Remove from local preview
  // The actual backend update happens when user clicks "保存" or "完成"
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

// 獲取商品圖片 URL（獲取第一張圖片）
const getProductImageUrl = (product: Product): string | null => {
  if (!product.images || product.images.length === 0) {
    return null
  }
  
  let imageUrl: string | null = null
  
  // 如果 images 是字符串數組
  if (typeof product.images[0] === 'string') {
    imageUrl = product.images[0] as string
  } else if (typeof product.images[0] === 'object' && product.images[0] !== null) {
    // 如果 images 是對象數組
    const firstImage = product.images[0] as { imageUrl?: string; albumImageId?: number }
    imageUrl = firstImage?.imageUrl || null
  }
  
  if (!imageUrl) {
    return null
  }
  
  // 如果 URL 已經是完整 URL（http/https），直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // 如果 URL 已經以 /api 開頭，直接返回
  if (imageUrl.startsWith('/api')) {
    return imageUrl
  }
  
  // 否則加上 /api 前綴
  return `/api${imageUrl}`
}

const loadCategories = async () => {
  try {
    const response = await categoryApi.getEnabledCategories()
    if (response.success && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

onMounted(() => {
  loadProducts()
  loadAlbums()
  loadCategories()
})
</script>
