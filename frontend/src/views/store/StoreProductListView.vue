<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <section class="catalog-hero q-pa-lg q-mb-md">
      <div>
        <div class="hero-badge q-mb-sm">精選日常商品</div>
        <h1 class="hero-title">快速找到需要的商品，價格與庫存資訊一目了然</h1>
        <p class="hero-subtitle">可直接搜尋、排序、加入收藏或比較，並在確認後前往商品詳情與購物車。</p>
      </div>
      <div class="hero-panel">
        <div class="hero-row"><span>目前顯示</span><strong>{{ displayProducts.length }}</strong></div>
        <div class="hero-row"><span>收藏商品</span><strong>{{ favoriteIds.length }}</strong></div>
        <div class="hero-row"><span>比較清單</span><strong>{{ compareIds.length }}/4</strong></div>
      </div>
    </section>

    <q-banner v-if="compareProducts.length > 0" rounded class="compare-banner q-mb-md">
      <template #avatar>
        <q-icon name="balance" />
      </template>
      <div>
        <div class="text-weight-medium">比較清單</div>
        <div class="text-body2">{{ compareProducts.map((p) => p.name).join(' / ') }}</div>
      </div>
      <template #action>
        <q-btn flat no-caps label="清除" @click="clearCompareList" />
      </template>
    </q-banner>

    <section class="search-bar q-pa-md q-mb-md">
      <div class="row q-col-gutter-md items-end">
        <div class="col-12 col-md-6">
          <q-input
            v-model="keyword"
            outlined
            clearable
            label="搜尋商品"
            name="product-search"
            debounce="300"
            autocomplete="off"
            @keyup.enter="loadProducts"
          >
            <template #append>
              <q-icon name="search" class="cursor-pointer" @click="loadProducts" />
            </template>
          </q-input>
        </div>
        <div class="col-6 col-md-3">
          <q-select v-model="sortBy" outlined emit-value map-options label="排序方式" :options="sortOptions" />
        </div>
        <div class="col-6 col-md-3 text-right">
          <q-btn color="primary" no-caps icon="refresh" label="重新整理" @click="loadProducts" />
        </div>
      </div>
    </section>

    <section class="q-mb-sm row items-center justify-between" aria-live="polite">
      <div class="text-subtitle2 text-grey-8">共 {{ displayProducts.length }} 項商品</div>
      <div class="text-caption text-grey-7">商品資訊依目前搜尋與排序條件顯示</div>
    </section>

    <div v-if="loading" class="row q-col-gutter-md">
      <div v-for="idx in 6" :key="idx" class="col-12 col-sm-6 col-lg-4">
        <q-card bordered class="full-height">
          <q-skeleton type="rect" height="180px" square />
          <q-card-section>
            <q-skeleton type="text" width="70%" class="q-mb-sm" />
            <q-skeleton type="text" width="95%" />
            <q-skeleton type="text" width="80%" />
            <q-skeleton type="text" width="35%" class="q-mt-md" />
          </q-card-section>
        </q-card>
      </div>
    </div>

    <div v-else-if="displayProducts.length === 0" class="empty-state q-pa-xl text-center">
      <q-icon name="inventory_2" size="48px" color="grey-6" />
      <h2 class="text-h6 q-mt-md q-mb-sm">找不到符合條件的商品</h2>
      <p class="text-grey-7 q-mb-md">請嘗試更換關鍵字，或清除篩選與排序條件後重新查看。</p>
      <q-btn color="primary" no-caps label="清除搜尋條件" @click="resetSearch" />
    </div>

    <div v-else class="row q-col-gutter-md">
      <div v-for="product in displayProducts" :key="product.id" class="col-12 col-sm-6 col-lg-4">
        <q-card bordered class="product-card full-height">
          <div class="product-media">
            <q-img v-if="productImage(product)" :src="productImage(product)!" :alt="product.name" fit="cover" class="product-image" />
            <div v-else class="product-image-placeholder" :aria-label="`${product.name} 商品圖片尚未提供`">
              <q-icon name="inventory_2" size="34px" color="white" />
              <div class="placeholder-title">{{ product.name.slice(0, 1).toUpperCase() }}</div>
            </div>
            <div class="media-actions">
              <q-btn
                round
                flat
                color="white"
                class="media-action-btn"
                :icon="favoriteIds.includes(Number(product.id)) ? 'favorite' : 'favorite_border'"
                :aria-label="favoriteIds.includes(Number(product.id)) ? '從收藏移除' : '加入收藏'"
                @click.stop="toggleFavoriteItem(product)"
              />
              <q-btn
                round
                flat
                color="white"
                class="media-action-btn"
                :icon="compareIds.includes(Number(product.id)) ? 'done_all' : 'balance'"
                :aria-label="compareIds.includes(Number(product.id)) ? '從比較清單移除' : '加入比較清單'"
                @click.stop="toggleCompareItem(product)"
              />
            </div>
          </div>

          <q-card-section>
            <h2 class="text-subtitle1 text-weight-bold q-mb-xs">{{ product.name }}</h2>
            <p class="text-grey-7 product-description q-mb-md">{{ product.description || '尚未提供商品描述。' }}</p>
            <div class="row items-center justify-between q-gutter-sm">
              <div class="text-h6 text-primary">NT$ {{ formatPrice(productPrice(product)) }}</div>
              <q-chip dense square color="grey-2" text-color="dark">{{ productStock(product) > 0 ? '現貨供應' : '暫時缺貨' }}</q-chip>
            </div>
          </q-card-section>

          <q-card-actions align="between" class="q-pt-none q-px-md q-pb-md">
            <div class="row q-gutter-xs">
              <q-chip v-if="favoriteIds.includes(Number(product.id))" dense square color="pink-1" text-color="pink-9">已收藏</q-chip>
              <q-chip v-if="compareIds.includes(Number(product.id))" dense square color="blue-1" text-color="blue-9">比較中</q-chip>
            </div>
            <q-btn flat no-caps color="primary" label="查看詳情" @click="goDetail(product.id)" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { productApi, type Product } from '@/api/product'
import { trackEvent } from '@/utils/tracking'
import { clearCompare, getCompareIds, getFavoriteIds, toggleCompare, toggleFavorite } from '@/utils/storePreferences'

const router = useRouter()
const $q = useQuasar()
const products = ref<Product[]>([])
const loading = ref(false)
const keyword = ref('')
const sortBy = ref<'default' | 'priceAsc' | 'priceDesc'>('default')
const favoriteIds = ref<number[]>([])
const compareIds = ref<number[]>([])

const sortOptions = [
  { label: '預設排序', value: 'default' },
  { label: '價格由低到高', value: 'priceAsc' },
  { label: '價格由高到低', value: 'priceDesc' }
]

const normalizeList = (payload: unknown): Product[] => {
  if (Array.isArray(payload)) return payload as Product[]
  if (payload && typeof payload === 'object' && 'content' in payload) {
    return (payload as { content?: Product[] }).content ?? []
  }
  return []
}

const syncPrefs = () => {
  favoriteIds.value = getFavoriteIds()
  compareIds.value = getCompareIds()
}

const productPrice = (product: Product) => Number(product.price ?? product.salePrice ?? 0)

const productStock = (product: Product) => {
  // 檢查直接的 stock 欄位
  const directStock = Number((product as Product & { stock?: number | string }).stock)
  if (Number.isFinite(directStock) && directStock >= 0) return directStock

  // 檢查規格中的庫存
  if (Array.isArray(product.specifications) && product.specifications.length > 0) {
    return product.specifications.reduce((sum, spec) => {
      const stock = Number(spec.stock ?? 0)
      return sum + (Number.isFinite(stock) ? stock : 0)
    }, 0)
  }

  // 如果沒有庫存信息，預設為有貨（返回 1 表示有貨）
  // 這樣可以避免所有商品都顯示缺貨
  return 1
}

const productImage = (product: Product): string | null => {
  const images = product.images
  if (!images || !Array.isArray(images) || images.length === 0) return null
  const first = images[0]
  if (typeof first === 'string') return first || null
  if (first && typeof first === 'object' && 'imageUrl' in first) return (first.imageUrl as string) || null
  return null
}

const displayProducts = computed(() => {
  const list = [...products.value]
  if (sortBy.value === 'priceAsc') return list.sort((a, b) => productPrice(a) - productPrice(b))
  if (sortBy.value === 'priceDesc') return list.sort((a, b) => productPrice(b) - productPrice(a))
  return list
})

const compareProducts = computed(() => {
  const idSet = new Set(compareIds.value)
  return displayProducts.value.filter((p) => p.id && idSet.has(Number(p.id)))
})

const formatPrice = (value: number) => value.toLocaleString('zh-TW', { maximumFractionDigits: 0 })

const loadProducts = async () => {
  loading.value = true
  try {
    const response = keyword.value.trim()
      ? await productApi.searchProducts(keyword.value.trim(), 0, 20)
      : await productApi.getProducts({ page: 0, size: 20 })

    products.value = normalizeList(response.data)
  } catch {
    $q.notify({ type: 'negative', message: '載入商品失敗，請稍後再試。' })
  } finally {
    loading.value = false
  }
}

const resetSearch = async () => {
  keyword.value = ''
  sortBy.value = 'default'
  await loadProducts()
}

const goDetail = (id?: number) => {
  if (!id) return
  trackEvent('select_product', { product_id: id })
  router.push(`/products/${id}`)
}

const toggleFavoriteItem = (product: Product) => {
  if (!product.id) return
  const selected = toggleFavorite(product.id)
  syncPrefs()
  $q.notify({ type: selected ? 'positive' : 'info', message: selected ? '已加入收藏' : '已從收藏移除' })
}

const toggleCompareItem = (product: Product) => {
  if (!product.id) return
  const result = toggleCompare(product.id, 4)
  if (result.limited) {
    $q.notify({ type: 'warning', message: '比較清單最多可加入 4 項商品。' })
    return
  }
  syncPrefs()
  $q.notify({ type: result.selected ? 'positive' : 'info', message: result.selected ? '已加入比較清單' : '已從比較清單移除' })
}

const clearCompareList = () => {
  clearCompare()
  syncPrefs()
}

onMounted(async () => {
  trackEvent('view_product_list')
  syncPrefs()
  await loadProducts()
})
</script>

<style scoped>
.store-page { max-width: 1180px; margin: 0 auto; }

.catalog-hero {
  border-radius: 20px;
  border: 1px solid #e5dccf;
  background: linear-gradient(180deg, rgba(255,255,255,.96) 0%, rgba(251,247,241,.96) 100%);
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}
.hero-badge { display:inline-flex; padding:4px 10px; border-radius:999px; background:#fff1e3; color:#8f4f2d; font-weight:700; font-size:.78rem; }
.hero-title { margin:0; font-size:1.8rem; line-height:1.25; color:#2f241f; }
.hero-subtitle { margin:8px 0 0; color:#5b6472; line-height:1.6; }
.hero-panel { border:1px solid #ece2d4; border-radius:14px; background:#fff; padding:12px; }
.hero-row { display:flex; justify-content:space-between; gap:8px; }
.hero-row + .hero-row { margin-top:8px; }

.compare-banner { border: 1px solid #d8e7ff; background: #f4f8ff; color: #204a86; }

.search-bar { border:1px solid #eadfcd; border-radius:16px; background:#fff; }
.empty-state { background:#fff; border-radius:16px; border:1px dashed #d7cdb7; }

.product-card { border-radius:16px; border-color:#eadfcd; background:#fff; overflow:hidden; }
.product-card:hover { box-shadow:0 12px 28px rgba(15,23,42,.08); transform: translateY(-2px); transition: .2s ease; border-color:#d9ceb3; }

.product-media { position:relative; height:180px; background:#f8fafc; }
.product-image { width:100%; height:100%; }
.product-image-placeholder {
  width:100%; height:100%; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:8px;
  background: linear-gradient(135deg, #1d4ed8 0%, #0f172a 100%);
}
.placeholder-title { color:#dbeafe; font-size:1.2rem; font-weight:700; }
.media-actions { position:absolute; top:8px; right:8px; display:grid; gap:6px; }
.media-action-btn { background: rgba(15,23,42,.35); backdrop-filter: blur(4px); }

.product-description { min-height: 48px; line-height: 1.5; }

.product-description {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 900px) {
  .catalog-hero { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .product-media { height: 164px; }
  .media-actions { top: 6px; right: 6px; gap: 4px; }
  .media-action-btn { width: 38px; height: 38px; }
}
</style>
