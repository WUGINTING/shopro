<template>
  <q-page class="store-page q-pa-md q-pa-lg-lg">
    <q-btn flat no-caps icon="arrow_back" label="Back to products" class="q-mb-sm" @click="router.push('/products')" />

    <q-card bordered class="detail-card">
      <q-card-section>
        <div class="row q-col-gutter-xl items-start">
          <div class="col-12 col-md-6">
            <div class="detail-media">
              <q-img v-if="imageUrl" :src="imageUrl" :alt="product?.name || 'Product image'" fit="cover" class="detail-image" />
              <div v-else class="detail-placeholder">
                <q-icon name="inventory_2" size="48px" color="white" />
                <div class="placeholder-name">{{ (product?.name || 'P').slice(0, 1).toUpperCase() }}</div>
              </div>
            </div>
          </div>

          <div class="col-12 col-md-6">
            <div class="row items-center q-gutter-sm q-mb-sm">
              <q-chip color="blue-1" text-color="primary" square dense>Featured</q-chip>
              <q-chip v-if="product?.id && favoriteOn" color="pink-1" text-color="pink-9" square dense>Favorite</q-chip>
              <q-chip v-if="product?.id && compareOn" color="blue-1" text-color="blue-9" square dense>Compare</q-chip>
            </div>

            <h1 class="text-h5 text-weight-bold q-mb-sm">{{ product?.name || 'Loading product' }}</h1>
            <p class="text-grey-7 q-mb-md detail-desc">{{ product?.description || 'No detailed description yet.' }}</p>
            <div class="text-h4 text-primary text-weight-bold">NT$ {{ formatPrice(price) }}</div>

            <div class="row q-gutter-sm q-mt-md q-mb-md">
              <q-btn
                outline
                no-caps
                color="pink"
                :icon="favoriteOn ? 'favorite' : 'favorite_border'"
                :label="favoriteOn ? 'Favorited' : 'Add favorite'"
                @click="toggleFavoriteCurrent"
              />
              <q-btn
                outline
                no-caps
                color="primary"
                :icon="compareOn ? 'done_all' : 'balance'"
                :label="compareOn ? 'Compared' : 'Add compare'"
                @click="toggleCompareCurrent"
              />
            </div>

            <q-card flat bordered class="buy-box q-pa-md">
              <p class="text-subtitle2 text-weight-medium q-mb-sm">Quantity</p>
              <q-input
                v-model.number="quantity"
                type="number"
                min="1"
                max="99"
                outlined
                dense
                aria-label="Product quantity"
                @update:model-value="normalizeQty"
              />

              <div class="row q-gutter-sm q-mt-md">
                <q-btn unelevated color="primary" no-caps label="Add to cart" @click="handleAddToCart" />
                <q-btn outline color="primary" no-caps label="Open cart" @click="router.push('/cart')" />
              </div>

              <q-separator class="q-my-md" />
              <ul class="service-points">
                <li>ECPay and cash on delivery supported</li>
                <li>Track status in your account after order placement</li>
                <li>Fast support and transparent after-sales flow</li>
              </ul>
            </q-card>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
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

const productId = computed(() => Number(route.params.id))
const price = computed(() => Number(product.value?.price ?? product.value?.salePrice ?? 0))

const imageUrl = computed(() => {
  const images = product.value?.images
  if (!images || !Array.isArray(images) || images.length === 0) return null
  const first = images[0]
  if (typeof first === 'string') return first || null
  return first?.imageUrl || null
})

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
  normalizeQty()

  addToCart({ productId: product.value.id, name: product.value.name, price: price.value, quantity: quantity.value })

  trackEvent('add_to_cart', {
    product_id: product.value.id,
    product_name: product.value.name,
    quantity: quantity.value,
    price: price.value
  })

  $q.notify({ type: 'positive', message: 'Added to cart' })
}

const toggleFavoriteCurrent = () => {
  if (!product.value?.id) return
  const selected = toggleFavorite(product.value.id)
  syncPrefs()
  $q.notify({ type: selected ? 'positive' : 'info', message: selected ? 'Added to favorites' : 'Removed from favorites' })
}

const toggleCompareCurrent = () => {
  if (!product.value?.id) return
  const result = toggleCompare(product.value.id, 4)
  if (result.limited) {
    $q.notify({ type: 'warning', message: 'You can compare up to 4 products.' })
    return
  }
  syncPrefs()
  $q.notify({ type: result.selected ? 'positive' : 'info', message: result.selected ? 'Added to compare list' : 'Removed from compare list' })
}

onMounted(async () => {
  const response = await productApi.getProduct(productId.value)
  product.value = response.data
  syncPrefs()

  trackEvent('view_product_detail', { product_id: productId.value })
})
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
.detail-desc { line-height: 1.7; }
.buy-box { border-radius: 14px; background: #f8fafc; }
.service-points { margin: 0; padding-left: 18px; color: #475569; display: grid; gap: 4px; }

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
