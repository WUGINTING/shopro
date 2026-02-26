﻿﻿<template>
  <q-page class="store-page q-pa-md q-pa-lg-lg">
    <q-btn flat no-caps icon="arrow_back" label="返回商品列表" class="q-mb-sm" @click="router.push('/products')" />

    <q-card bordered class="detail-card">
      <q-card-section v-if="loading">
        <div class="row q-col-gutter-xl items-start">
          <div class="col-12 col-md-6">
            <q-skeleton type="rect" height="360px" class="rounded-borders" />
          </div>
          <div class="col-12 col-md-6">
            <q-skeleton type="text" width="30%" class="q-mb-md" />
            <q-skeleton type="text" width="70%" class="q-mb-sm" />
            <q-skeleton type="text" width="95%" />
            <q-skeleton type="text" width="90%" />
            <q-skeleton type="text" width="35%" class="q-mt-lg q-mb-md" />
            <q-skeleton type="QBtn" width="140px" class="q-mr-sm" />
            <q-skeleton type="QBtn" width="140px" />
          </div>
        </div>
      </q-card-section>

      <q-card-section v-else-if="errorMessage">
        <q-banner rounded class="bg-red-1 text-negative">
          <template #avatar>
            <q-icon name="error" />
          </template>
          {{ errorMessage }}
          <template #action>
            <q-btn flat no-caps color="negative" label="重新載入" @click="fetchProduct" />
          </template>
        </q-banner>
      </q-card-section>

      <q-card-section v-else-if="product">
        <div class="row q-col-gutter-xl items-start">
          <div class="col-12 col-md-6">
            <div class="detail-media">
              <q-img v-if="imageUrl" :src="imageUrl" :alt="product.name || '商品圖片'" fit="cover" class="detail-image" />
              <div v-else class="detail-placeholder" :aria-label="`${product.name} 商品圖片尚未提供`">
                <q-icon name="inventory_2" size="48px" color="white" />
                <div class="placeholder-name">{{ (product.name || 'P').slice(0, 1).toUpperCase() }}</div>
              </div>
            </div>

            <!-- 縮圖列表 -->
            <div v-if="allThumbnails.length > 1" class="thumbnail-list q-mt-md">
              <div
                v-for="(thumb, index) in allThumbnails"
                :key="'thumb-' + index"
                :class="['thumbnail', { 'thumbnail--active': currentImageIndex === index && !selectedSpec?.image }]"
                @click="selectThumbnail(index)"
              >
                <q-img :src="thumb.url" :alt="thumb.specName || `商品圖片 ${index + 1}`" ratio="1" fit="cover">
                  <template v-slot:error>
                    <div class="absolute-full flex flex-center bg-grey-2">
                      <q-icon name="image" size="16px" color="grey-4" />
                    </div>
                  </template>
                </q-img>
                <div v-if="thumb.specName" class="thumbnail__label">{{ thumb.specName }}</div>
              </div>
            </div>
          </div>

          <div class="col-12 col-md-6">
            <div class="row items-center q-gutter-sm q-mb-sm">
              <q-chip color="blue-1" text-color="primary" square dense>精選商品</q-chip>
              <q-chip v-if="product.id && favoriteOn" color="pink-1" text-color="pink-9" square dense>已收藏</q-chip>
              <q-chip v-if="product.id && compareOn" color="blue-1" text-color="blue-9" square dense>比較中</q-chip>
            </div>

            <h1 class="text-h5 text-weight-bold q-mb-sm">{{ product.name }}</h1>
            <p class="text-grey-7 q-mb-md detail-desc">{{ product.description || '尚未提供商品詳細描述。' }}</p>
            <div class="text-h4 text-primary text-weight-bold">NT$ {{ formatPrice(price) }}</div>

            <div v-if="specifications.length > 0" class="q-mt-md">
              <p class="text-subtitle2 text-weight-medium q-mb-sm">選擇規格</p>
              <div class="spec-grid">
                <div
                  v-for="spec in specifications"
                  :key="spec.id"
                  :class="['spec-card', {
                    'spec-card--selected': selectedSpecId === spec.id,
                    'spec-card--disabled': spec.stock === 0
                  }]"
                  @click="selectSpec(spec)"
                >
                  <q-img
                    v-if="spec.image"
                    :src="spec.image"
                    :alt="spec.specName"
                    :ratio="1"
                    fit="cover"
                    class="spec-card__image"
                  >
                    <template v-slot:error>
                      <div class="absolute-full flex flex-center bg-grey-2">
                        <q-icon name="image" size="24px" color="grey-4" />
                      </div>
                    </template>
                  </q-img>
                  <div class="spec-card__body">
                    <div class="spec-card__name">{{ spec.specName }}</div>
                    <div class="spec-card__price">NT$ {{ formatPrice(Number(spec.price ?? 0)) }}</div>
                    <div v-if="(spec.stock ?? 0) > 0" class="spec-card__stock">庫存: {{ spec.stock }}</div>
                    <div v-else class="spec-card__stock spec-card__stock--out">售完</div>
                  </div>
                  <q-icon v-if="selectedSpecId === spec.id" name="check_circle" color="primary" size="20px" class="spec-card__check" />
                </div>
              </div>
              <div v-if="selectedSpec" class="text-caption text-grey-7 q-mt-sm">
                已選擇：<strong>{{ selectedSpec.specName }}</strong>（庫存 {{ selectedSpec.stock }} 件）
              </div>
              <div v-else class="text-caption text-warning q-mt-sm">
                <q-icon name="warning" size="14px" /> 請選擇商品規格
              </div>
            </div>

            <div class="row q-gutter-sm q-mt-md q-mb-md">
              <q-btn
                outline
                no-caps
                color="pink"
                :icon="favoriteOn ? 'favorite' : 'favorite_border'"
                :label="favoriteOn ? '已收藏' : '加入收藏'"
                @click="toggleFavoriteCurrent"
              />
              <q-btn
                outline
                no-caps
                color="primary"
                :icon="compareOn ? 'done_all' : 'balance'"
                :label="compareOn ? '比較中' : '加入比較'"
                @click="toggleCompareCurrent"
              />
            </div>

            <q-card flat bordered class="buy-box q-pa-md">
              <p class="text-subtitle2 text-weight-medium q-mb-sm">購買數量</p>
              <q-input
                v-model.number="quantity"
                type="number"
                min="1"
                max="99"
                outlined
                dense
                aria-label="商品購買數量"
                @update:model-value="normalizeQty"
              />

              <div class="row q-gutter-sm q-mt-md">
                <q-btn unelevated color="primary" no-caps label="加入購物車" @click="handleAddToCart" />
                <q-btn outline color="primary" no-caps label="前往購物車" @click="router.push('/cart')" />
              </div>

              <q-separator class="q-my-md" />
              <ul class="service-points">
                <li>支援 ECPay 與貨到付款（依結帳選項顯示）</li>
                <li>下單後可於會員中心查看訂單狀態</li>
                <li>提供清楚售後與聯絡資訊</li>
              </ul>
            </q-card>
          </div>
        </div>
      </q-card-section>

      <q-card-section v-else>
        <q-banner rounded class="bg-grey-2 text-grey-8">
          <template #avatar>
            <q-icon name="inventory_2" />
          </template>
          找不到此商品，請返回商品列表重新選擇。
          <template #action>
            <q-btn flat no-caps color="primary" label="返回商品列表" @click="router.push('/products')" />
          </template>
        </q-banner>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { productApi, type Product } from '@/api/product'
import { addToCart } from '@/utils/storeCart'
import { trackEvent } from '@/utils/tracking'
import { getCompareIds, getFavoriteIds, toggleCompare, toggleFavorite } from '@/utils/storePreferences'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const product = ref<Product | null>(null)
const quantity = ref(1)
const favoriteOn = ref(false)
const compareOn = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const selectedSpecId = ref<number | null>(null)
const currentImageIndex = ref(0)

const productId = computed(() => Number(route.params.id))
const baseProductPrice = computed(() => Number(product.value?.price ?? product.value?.salePrice ?? 0))
const specifications = computed(() => (product.value?.specifications ?? []).filter((spec) => spec.enabled !== false))
const selectedSpec = computed(() => {
  const available = specifications.value
  if (!available.length) return null
  if (selectedSpecId.value) {
    const found = available.find(spec => spec.id === selectedSpecId.value)
    if (found) return found
  }
  return null
})
const price = computed(() => Number(selectedSpec.value?.price ?? baseProductPrice.value))

const selectSpec = (spec: any) => {
  if (spec.stock === 0) {
    $q.notify({ type: 'warning', message: '此規格已售完', position: 'top', timeout: 1500 })
    return
  }
  // 再次點擊可取消選擇
  if (selectedSpecId.value === spec.id) {
    selectedSpecId.value = null
  } else {
    selectedSpecId.value = spec.id
  }
  quantity.value = 1
}

const imageUrl = computed(() => {
  // 優先顯示已選規格的圖片
  const specImage = selectedSpec.value?.image
  if (specImage) return specImage

  // 使用 allThumbnails 中當前選中的圖片
  const thumbs = allThumbnails.value
  if (thumbs.length > 0 && currentImageIndex.value < thumbs.length) {
    const thumb = thumbs[currentImageIndex.value]
    return thumb?.url ?? null
  }

  return null
})

// 合併產品圖片和規格圖片到縮圖列表
const allThumbnails = computed(() => {
  const thumbs: { url: string; specName: string | null }[] = []
  const existingUrls = new Set<string>()

  // 商品主圖片
  const images = product.value?.images
  if (images && Array.isArray(images)) {
    images.forEach(img => {
      let url: string | null = null
      if (typeof img === 'string') {
        url = img
      } else if (img && typeof img === 'object' && 'imageUrl' in img) {
        url = img.imageUrl
      }
      if (url && !existingUrls.has(url)) {
        thumbs.push({ url, specName: null })
        existingUrls.add(url)
      }
    })
  }

  // 規格圖片（排除與商品主圖重複的）
  specifications.value.forEach(spec => {
    if (spec.image && !existingUrls.has(spec.image)) {
      thumbs.push({ url: spec.image, specName: spec.specName || null })
      existingUrls.add(spec.image)
    }
  })

  return thumbs
})

// 選擇縮圖
const selectThumbnail = (index: number) => {
  currentImageIndex.value = index
  // 清除已選規格（這樣 imageUrl 會使用縮圖）
  // 注意：如果不想清除規格，可以移除這行
}

const syncPrefs = () => {
  if (!product.value?.id) return
  favoriteOn.value = getFavoriteIds().includes(product.value.id)
  compareOn.value = getCompareIds().includes(product.value.id)
}

const normalizeQty = () => {
  quantity.value = Math.max(1, Math.min(99, Number(quantity.value || 1)))
}

const formatPrice = (value: number) => value.toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const handleAddToCart = () => {
  if (!product.value?.id) return

  // 有規格但未選擇時提示
  if (specifications.value.length > 0 && !selectedSpecId.value) {
    $q.notify({ type: 'warning', message: '請先選擇商品規格' })
    return
  }

  // 檢查庫存
  if (selectedSpec.value && selectedSpec.value.stock !== undefined && selectedSpec.value.stock < quantity.value) {
    $q.notify({ type: 'warning', message: '庫存不足' })
    return
  }

  normalizeQty()

  addToCart({
    productId: product.value.id,
    name: product.value.name,
    price: price.value,
    quantity: quantity.value,
    specificationId: selectedSpec.value?.id,
    specName: selectedSpec.value?.specName || selectedSpec.value?.sku
  })

  trackEvent('add_to_cart', {
    product_id: product.value.id,
    specification_id: selectedSpec.value?.id,
    spec_name: selectedSpec.value?.specName || selectedSpec.value?.sku,
    product_name: product.value.name,
    quantity: quantity.value,
    price: price.value
  })

  $q.notify({ type: 'positive', message: '已加入購物車' })
}

const toggleFavoriteCurrent = () => {
  if (!product.value?.id) return
  const selected = toggleFavorite(product.value.id)
  syncPrefs()
  $q.notify({ type: selected ? 'positive' : 'info', message: selected ? '已加入收藏' : '已從收藏移除' })
}

const toggleCompareCurrent = () => {
  if (!product.value?.id) return
  const result = toggleCompare(product.value.id, 4)
  if (result.limited) {
    $q.notify({ type: 'warning', message: '比較清單最多可加入 4 項商品。' })
    return
  }
  syncPrefs()
  $q.notify({ type: result.selected ? 'positive' : 'info', message: result.selected ? '已加入比較清單' : '已從比較清單移除' })
}

const fetchProduct = async () => {
  if (!Number.isFinite(productId.value) || productId.value <= 0) {
    product.value = null
    errorMessage.value = '商品編號無效。'
    return
  }

  loading.value = true
  errorMessage.value = ''
  product.value = null
  quantity.value = 1

  try {
    const response = await productApi.getProduct(productId.value)
    product.value = response.data || null
    if (!product.value) {
      errorMessage.value = '找不到此商品資訊。'
      return
    }
    syncPrefs()
    trackEvent('view_product_detail', { product_id: productId.value })
  } catch (error: any) {
    errorMessage.value = error?.response?.data?.message || '載入商品詳情失敗，請稍後再試。'
  } finally {
    loading.value = false
  }
}

watch(
  () => productId.value,
  () => {
    fetchProduct()
  },
  { immediate: true }
)

watch(
  () => specifications.value,
  (specs) => {
    if (!specs.length) {
      selectedSpecId.value = null
      return
    }
    // 如果當前選擇的規格不在可用列表中，重置
    if (selectedSpecId.value && !specs.some(spec => spec.id === selectedSpecId.value)) {
      selectedSpecId.value = null
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.store-page { max-width: 1180px; margin: 0 auto; }
.detail-card { border-radius: 20px; border-color: #eadfcd; background: #fff; }
.detail-media { border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb; background: #f8fafc; height: 360px; }
.detail-image { width: 100%; height: 100%; }
.detail-placeholder {
  width: 100%; height: 100%; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:10px;
  background: linear-gradient(135deg, #0f172a 0%, #1d4ed8 100%);
}
.placeholder-name { color: #dbeafe; font-size: 1.5rem; font-weight: 700; }

/* 縮圖列表樣式 */
.thumbnail-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.thumbnail {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.thumbnail:hover {
  border-color: #1d4ed8;
  transform: scale(1.05);
}

.thumbnail--active {
  border-color: #1d4ed8;
  box-shadow: 0 0 0 2px rgba(29, 78, 216, 0.3);
}

.thumbnail__label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 0.65rem;
  padding: 2px 4px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.detail-desc { line-height: 1.7; }
.buy-box { border-radius: 14px; background: #f8fafc; }
.service-points { margin: 0; padding-left: 18px; color: #475569; display: grid; gap: 4px; }

.spec-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.spec-card {
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
  position: relative;
  background: #fff;
}

.spec-card:hover:not(.spec-card--disabled) {
  border-color: #1d4ed8;
  box-shadow: 0 2px 12px rgba(29, 78, 216, 0.15);
}

.spec-card--selected {
  border-color: #1d4ed8;
  background: #eff6ff;
}

.spec-card--selected .spec-card__name {
  color: #1d4ed8;
  font-weight: 600;
}

.spec-card--disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

.spec-card__image {
  width: 100%;
  aspect-ratio: 1;
  border-bottom: 1px solid #e2e8f0;
}

.spec-card__body {
  padding: 10px 12px;
}

.spec-card__name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 4px;
}

.spec-card__price {
  font-size: 0.85rem;
  color: #dc2626;
  font-weight: 600;
  margin-bottom: 2px;
}

.spec-card__stock {
  font-size: 0.75rem;
  color: #64748b;
}

.spec-card__stock--out {
  color: #dc2626;
  font-weight: 500;
}

.spec-card__check {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #fff;
  border-radius: 50%;
}

@media (max-width: 900px) {
  .detail-media { height: 260px; }
}

@media (max-width: 640px) {
  .buy-box .row.q-gutter-sm {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .buy-box .row.q-gutter-sm > .q-btn {
    width: 100%;
    min-height: 44px;
    border-radius: 12px;
  }
}
</style>
