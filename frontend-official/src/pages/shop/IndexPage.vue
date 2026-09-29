<template>
  <q-page class="shop-index-page">
    <!-- 彈跳廣告（僅在後台設定有效廣告時顯示） -->
    <PopupAd />

    <!-- 品牌主視覺 -->
    <section class="shop-hero">
      <div class="hero-inner">
        <p class="hero-eyebrow">遇日小舖</p>
        <h1 class="hero-title">每一天都是美好相遇</h1>
        <p class="hero-subtitle">日本選物、生活雜貨與手作甜點，用心挑選每一件好物。</p>
        <div class="hero-actions">
          <q-btn
            unelevated
            size="lg"
            color="white"
            text-color="primary"
            label="前往選購"
            icon-right="arrow_forward"
            to="/shop/product/list?category=all"
          />
          <q-btn outline size="lg" color="white" label="認識我們" to="/shop/introduce" />
        </div>
      </div>
    </section>

    <div class="shop-content-area">
      <div class="shop-container">
        <!-- 服務保證 -->
        <section class="trust-bar" aria-label="購物保障">
          <router-link to="/shop/page/faq" class="trust-item">
            <q-icon name="local_shipping" size="28px" />
            <div>
              <strong>{{ shippingTitle }}</strong>
              <span>{{ shippingText }}</span>
            </div>
          </router-link>
          <router-link to="/shop/page/terms" class="trust-item">
            <q-icon name="lock" size="28px" />
            <div>
              <strong>安全付款</strong>
              <span>綠界金流 · 貨到付款</span>
            </div>
          </router-link>
          <router-link to="/shop/page/returns" class="trust-item">
            <q-icon name="autorenew" size="28px" />
            <div>
              <strong>七天鑑賞期</strong>
              <span>退換貨說明</span>
            </div>
          </router-link>
          <router-link to="/shop/order/lookup" class="trust-item">
            <q-icon name="receipt_long" size="28px" />
            <div>
              <strong>訂單查詢</strong>
              <span>免登入即可查詢</span>
            </div>
          </router-link>
        </section>

        <!-- 商品分類 -->
        <section v-if="topCategories.length" class="product-section">
          <div class="section-header">
            <h2 class="section-title">商品分類</h2>
          </div>
          <div class="category-chips">
            <q-btn
              v-for="category in topCategories"
              :key="category.id"
              outline
              rounded
              no-caps
              color="primary"
              :label="category.name"
              :to="`/shop/product/list?category=${category.id}`"
            />
          </div>
        </section>

        <!-- 新品上市 -->
        <section class="product-section">
          <div class="section-header">
            <h2 class="section-title">新品上市</h2>
            <router-link to="/shop/product/list?category=all" class="section-link">
              查看全部商品 →
            </router-link>
          </div>

          <div v-if="loading" class="product-grid">
            <q-card v-for="n in 8" :key="n" flat bordered class="skeleton-card">
              <q-skeleton height="200px" square />
              <q-card-section>
                <q-skeleton type="text" />
                <q-skeleton type="text" width="50%" />
              </q-card-section>
            </q-card>
          </div>

          <div v-else-if="loadError" class="state-box">
            <q-icon name="cloud_off" size="56px" color="grey-5" />
            <p>{{ loadError }}</p>
            <q-btn unelevated color="primary" icon="refresh" label="重新載入" @click="fetchNewProducts" />
          </div>

          <div v-else-if="newProducts.length === 0" class="state-box">
            <q-icon name="inventory_2" size="56px" color="grey-5" />
            <p>商品即將上架，敬請期待！</p>
          </div>

          <div v-else class="product-grid">
            <ProductCard
              v-for="product in newProducts"
              :key="product.id"
              :product="product"
              @select="goToDetail(product.id)"
              @add-to-cart="handleAddToCart"
            />
          </div>
        </section>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { useShopMeta } from 'src/composables/useShopMeta.js';
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import PopupAd from 'components/shop/PopupAd.vue';
import ProductCard from 'components/shop/ProductCard.vue';
import { getStorefrontProducts, getEnabledCategories } from 'src/api/product.js';
import { mapProduct, quickAddToCart } from 'src/utils/product.js';
import { loadShippingOptions, shippingHeadline, shippingSubline } from 'src/utils/shipping.js';

const router = useRouter();
const $q = useQuasar();

const loading = ref(true);
const loadError = ref('');
const newProducts = ref([]);
const categories = ref([]);

// 首頁只顯示頂層分類
const topCategories = computed(() =>
  categories.value
    .filter(category => !category.parentId)
    .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
);

const goToDetail = productId => {
  router.push(`/shop/product/${productId}`);
};

const handleAddToCart = product => {
  quickAddToCart(product, { router, $q });
};

const fetchNewProducts = async () => {
  loading.value = true;
  loadError.value = '';
  try {
    const response = await getStorefrontProducts({ sort: 'newest', page: 0, size: 8 });
    newProducts.value = (response?.data?.content || []).map(mapProduct);
  } catch (error) {
    newProducts.value = [];
    loadError.value = error.displayMessage || '商品載入失敗，請稍後再試';
  } finally {
    loading.value = false;
  }
};

const fetchCategories = async () => {
  try {
    const response = await getEnabledCategories();
    categories.value = response?.data || [];
  } catch (error) {
    categories.value = [];
  }
};

// 運費標語依後台運費設定
const shippingTitle = ref('滿 NT$1,000 免運');
const shippingText = ref('宅配到府或門市自取');

onMounted(() => {
  fetchNewProducts();
  fetchCategories();
  loadShippingOptions().then(options => {
    if (options.length > 0) {
      shippingTitle.value = shippingHeadline(options);
      shippingText.value = shippingSubline(options);
    }
  });
});

useShopMeta(() => ({ description: '遇日小舖線上商店：精選甜品與伴手禮，線上付款或貨到付款，宅配到府或門市自取。' }));
</script>

<style lang="scss" scoped>
@import '../../css/common.scss';

.shop-index-page {
  background: $shop-bg-light;
  min-height: 100vh;
  padding: 0;
}

// 品牌主視覺
.shop-hero {
  background: linear-gradient(135deg, $shop-primary 0%, $shop-primary-dark 100%);
  color: $shop-white;
  padding: 88px 16px 96px;
  text-align: center;

  .hero-inner {
    max-width: 720px;
    margin: 0 auto;
  }

  .hero-eyebrow {
    margin: 0 0 12px;
    font-size: 1rem;
    letter-spacing: 0.3em;
    opacity: 0.9;
  }

  .hero-title {
    margin: 0 0 16px;
    font-size: clamp(2rem, 5vw, 3.25rem);
    font-weight: 700;
    line-height: 1.25;
  }

  .hero-subtitle {
    margin: 0 0 32px;
    font-size: 1.1rem;
    line-height: 1.7;
    opacity: 0.95;
  }

  .hero-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
    flex-wrap: wrap;
  }
}

.shop-content-area {
  padding: 32px 0 64px;
}

.shop-container {
  max-width: $shop-container-max-width;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

// 服務保證
.trust-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin: -64px 0 40px;
  position: relative;

  .trust-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 18px 16px;
    background: $shop-white;
    border-radius: 10px;
    box-shadow: $shop-shadow-md;
    color: $shop-text;
    text-decoration: none;
    transition: $shop-transition;

    .q-icon {
      color: $shop-primary;
      flex-shrink: 0;
    }

    strong {
      display: block;
      font-size: 0.95rem;
    }

    span {
      display: block;
      font-size: 0.8rem;
      color: $shop-text-secondary;
    }

    &:hover,
    &:focus-visible {
      transform: translateY(-2px);
      box-shadow: $shop-shadow-lg;
    }
  }
}

.product-section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;

  .section-title {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 700;
    line-height: 1.3;
    color: $shop-text;
  }

  .section-link {
    color: $shop-primary;
    text-decoration: none;
    font-weight: 500;
    white-space: nowrap;

    &:hover {
      text-decoration: underline;
    }
  }
}

.category-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.skeleton-card {
  border-radius: 6px;
  overflow: hidden;
}

.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 16px;
  background: $shop-white;
  border-radius: 10px;
  color: $shop-text-secondary;

  p {
    margin: 0;
  }
}

@media (max-width: $breakpoint-md) {
  .trust-bar {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: $breakpoint-sm) {
  .shop-hero {
    padding: 56px 16px 88px;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .trust-bar .trust-item {
    padding: 14px 12px;
    gap: 8px;
  }
}
</style>
