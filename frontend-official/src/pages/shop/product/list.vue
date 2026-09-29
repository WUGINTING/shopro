<template>
  <q-page class="product-list-page">
    <div class="list-container">
      <!-- 標題區 -->
      <div class="page-header">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <!-- 主要內容區：左側分類 + 右側商品 -->
      <div class="main-content">
        <!-- 左側分類清單 -->
        <aside class="category-sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">商品分類</h3>
            <q-list class="category-list">
              <!-- 所有商品 -->
              <q-item
                clickable
                v-ripple
                :active="!categoryId || categoryId === 'all'"
                active-class="category-active"
                @click="filterByCategory('all')"
              >
                <q-item-section avatar>
                  <q-icon name="apps" color="primary" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-medium">所有商品</q-item-label>
                </q-item-section>
              </q-item>

              <q-separator spaced="sm" />

              <!-- 動態分類列表 -->
              <q-item
                v-for="category in categories"
                :key="category.id"
                clickable
                v-ripple
                :inset-level="category.depth ? 0.3 : 0"
                :active="categoryId == category.id"
                active-class="category-active"
                @click="filterByCategory(category.id)"
              >
                <q-item-section avatar>
                  <q-avatar
                    v-if="category.image"
                    size="32px"
                  >
                    <img :src="category.image" :alt="category.name" />
                  </q-avatar>
                  <q-icon
                    v-else
                    name="category"
                    color="grey-6"
                  />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-medium">{{ category.name }}</q-item-label>
                </q-item-section>
              </q-item>

              <!-- 分類載入中 -->
              <q-item v-if="categories.length === 0 && !categoriesLoading">
                <q-item-section>
                  <q-item-label class="text-grey-6 text-center">
                    <q-icon name="info" size="xs" />
                    暫無分類
                  </q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </div>
        </aside>

        <!-- 右側商品列表區 -->
        <div class="product-content">
          <!-- 篩選和排序 -->
          <div class="filter-section">
            <div class="result-info">
              <div class="result-count">
                <q-icon name="list" size="xs" class="q-mr-xs" />
                <template v-if="keyword">「{{ keyword }}」的搜尋結果：</template>
                共 <strong>{{ totalElements }}</strong> 件商品
                <q-btn
                  v-if="keyword"
                  flat
                  dense
                  size="sm"
                  color="primary"
                  icon="close"
                  label="清除搜尋"
                  class="q-ml-sm"
                  @click="clearKeyword"
                />
              </div>
              <div v-if="totalPages > 1" class="result-page text-grey-7">
                第 {{ currentPage }} / {{ totalPages }} 頁
              </div>
            </div>
            <div class="filter-controls">
              <q-select
                v-model="sortBy"
                :options="sortOptions"
                outlined
                dense
                emit-value
                map-options
                label="排序方式"
                style="min-width: 180px"
              >
                <template v-slot:prepend>
                  <q-icon name="sort" />
                </template>
              </q-select>
            </div>
          </div>

          <!-- 商品列表 -->
          <div v-if="loading" class="loading-wrapper">
            <q-spinner-dots color="primary" size="60px" />
            <p class="text-grey-7 q-mt-md">載入中...</p>
          </div>

          <div v-else-if="loadError" class="no-products">
            <q-icon name="cloud_off" size="80px" color="grey-5" />
            <p class="text-h6 text-grey-7 q-mt-md">{{ loadError }}</p>
            <q-btn unelevated color="primary" label="重新載入" icon="refresh" class="q-mt-md" @click="fetchProducts" />
          </div>

          <div v-else-if="products.length === 0" class="no-products">
            <q-icon name="inventory_2" size="80px" color="grey-5" />
            <p class="text-h6 text-grey-7 q-mt-md">
              {{ keyword ? '找不到符合的商品' : '目前此分類暫無商品' }}
            </p>
            <q-btn
              flat
              color="primary"
              label="查看所有商品"
              icon="arrow_back"
              @click="filterByCategory('all')"
              class="q-mt-md"
            />
          </div>

          <div v-else class="products-grid">
            <ProductCard
              v-for="product in products"
              :key="product.id"
              :product="product"
              @select="goToDetail(product.id)"
              @add-to-cart="handleAddToCart"
            />
          </div>

          <!-- 分页组件 -->
          <div v-if="totalPages > 1" class="pagination-wrapper">
            <q-pagination
              v-model="currentPage"
              :max="totalPages"
              :max-pages="7"
              direction-links
              boundary-links
              color="primary"
              active-color="primary"
              @update:model-value="changePage"
            />
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { useShopMeta } from 'src/composables/useShopMeta.js';
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import ProductCard from 'src/components/shop/ProductCard.vue';
import Breadcrumb from 'src/components/shop/Breadcrumb.vue';
import { getStorefrontProducts, getEnabledCategories } from 'src/api/product.js';
import { mapProduct, quickAddToCart } from 'src/utils/product.js';

const route = useRoute();
const router = useRouter();
const $q = useQuasar();

const loading = ref(true);
const loadError = ref('');
const categoriesLoading = ref(true);
const sortBy = ref('newest');

// 網址參數：?category=<id|all>&keyword=<text>
const categoryId = computed(() => {
  const value = route.query.category;
  return value && value !== 'all' && /^\d+$/.test(String(value)) ? Number(value) : null;
});
const keyword = computed(() => (route.query.keyword ? String(route.query.keyword).trim() : ''));

// 分類列表（父分類在前，子分類緊接其後）
const categories = ref([]);

// 分頁（後端分頁）
const currentPage = ref(1);
const pageSize = 12;
const totalPages = ref(0);
const totalElements = ref(0);

const products = ref([]);

const categoryName = computed(() => {
  if (keyword.value) return `搜尋：${keyword.value}`;
  if (!categoryId.value) return '所有商品';
  return categories.value.find(c => c.id === categoryId.value)?.name || '商品分類';
});

const breadcrumbItems = computed(() => [
  { label: '首頁', to: '/shop' },
  { label: categoryName.value, to: '' },
]);

// 排序選項（對應後端 sort 參數）
const sortOptions = [
  { label: '最新上架', value: 'newest' },
  { label: '價格：低到高', value: 'price_asc' },
  { label: '價格：高到低', value: 'price_desc' },
  { label: '名稱', value: 'name' },
];

const changePage = page => {
  currentPage.value = page;
  fetchProducts();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 切換分類時清除搜尋關鍵字
const filterByCategory = categoryIdOrAll => {
  router.push({ path: '/shop/product/list', query: { category: String(categoryIdOrAll) } });
};

const clearKeyword = () => {
  const query = { ...route.query };
  delete query.keyword;
  router.push({ path: '/shop/product/list', query });
};

const goToDetail = productId => {
  router.push(`/shop/product/${productId}`);
};

const handleAddToCart = product => {
  quickAddToCart(product, { router, $q });
};

// 將分類整理為樹狀順序
const orderCategories = list => {
  const byParent = new Map();
  list.forEach(cat => {
    const key = cat.parentId || 0;
    if (!byParent.has(key)) byParent.set(key, []);
    byParent.get(key).push(cat);
  });
  byParent.forEach(children => children.sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0)));
  const ids = new Set(list.map(cat => cat.id));
  const result = [];
  const visit = (parentKey, depth) => {
    (byParent.get(parentKey) || []).forEach(cat => {
      result.push({ ...cat, depth });
      visit(cat.id, depth + 1);
    });
  };
  visit(0, 0);
  // 父分類未啟用的子分類仍列出
  list.filter(cat => cat.parentId && !ids.has(cat.parentId)).forEach(cat => result.push({ ...cat, depth: 0 }));
  return result;
};

const fetchCategories = async () => {
  categoriesLoading.value = true;
  try {
    const response = await getEnabledCategories();
    categories.value = orderCategories(response?.data || []);
  } catch (error) {
    categories.value = [];
  } finally {
    categoriesLoading.value = false;
  }
};

const fetchProducts = async () => {
  loading.value = true;
  loadError.value = '';
  try {
    const response = await getStorefrontProducts({
      categoryId: categoryId.value || undefined,
      keyword: keyword.value || undefined,
      sort: sortBy.value,
      page: currentPage.value - 1,
      size: pageSize,
    });
    const data = response?.data || {};
    products.value = (data.content || []).map(mapProduct);
    totalPages.value = data.totalPages || 0;
    totalElements.value = data.totalElements || 0;
  } catch (error) {
    products.value = [];
    totalPages.value = 0;
    totalElements.value = 0;
    loadError.value = error.displayMessage || '商品載入失敗，請稍後再試';
  } finally {
    loading.value = false;
  }
};

// 分類或關鍵字變更時回到第一頁
watch([categoryId, keyword], () => {
  currentPage.value = 1;
  fetchProducts();
});

watch(sortBy, () => {
  currentPage.value = 1;
  fetchProducts();
});

onMounted(() => {
  fetchCategories();
  fetchProducts();
});

useShopMeta(() => ({ title: '全部商品', description: '遇日小舖線上商店：瀏覽全部商品，線上付款或貨到付款。' }));
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.product-list-page {
  background: $shop-bg-light;
  padding: 30px 0;
  min-height: calc(100vh - 200px);
}

.list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 30px;
  align-items: start;
}

.category-sidebar {
  position: sticky;
  top: 90px;

  .sidebar-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.05);
  }

  .sidebar-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: $shop-text;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 3px solid $shop-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .category-list {
    .q-item {
      border-radius: 10px;
      margin-bottom: 6px;
      padding: 12px 16px;
      transition: all 0.3s ease;

      &:hover {
        background: rgba($shop-primary, 0.05);
        transform: translateX(4px);
      }

      &.category-active {
        background: linear-gradient(135deg, rgba($shop-primary, 0.15), rgba($shop-primary, 0.08));
        color: $shop-primary;
        font-weight: 600;
        border-left: 4px solid $shop-primary;
        padding-left: 12px;

        .q-item__label {
          color: $shop-primary;
        }

        .q-icon {
          color: $shop-primary !important;
        }
      }
    }
  }
}

.product-content {
  flex: 1;
}

.page-header {
  margin-bottom: 30px;

  .page-title {
    font-size: 2rem;
    color: $shop-text;
    margin-bottom: 10px;
    font-weight: 600;
  }
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: white;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);

  .result-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .result-count {
    font-size: 1.05rem;
    color: $shop-text;
    display: flex;
    align-items: center;

    strong {
      color: $shop-primary;
      font-size: 1.2rem;
      margin: 0 4px;
    }
  }

  .result-page {
    font-size: 0.85rem;
  }

  .filter-controls {
    display: flex;
    gap: 15px;
  }
}

.loading-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: white;
  border-radius: 12px;
  padding: 40px;
}

.no-products {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: white;
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  margin-bottom: 40px;

  :deep(.q-pagination) {
    .q-btn {
      min-width: 40px;
      height: 40px;
      border-radius: 8px;
      margin: 0 4px;
      font-weight: 500;

      &.q-btn--outline {
        border-color: $shop-border;
        color: $shop-text;

        &:hover {
          background: $shop-bg-light;
          border-color: $shop-primary;
          color: $shop-primary;
        }
      }

      &.q-btn--unelevated {
        background: $shop-primary;
        color: white;
      }
    }
  }
}

@media (max-width: 992px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .category-sidebar {
    position: static;

    .sidebar-card {
      margin-bottom: 20px;
    }

    .category-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 10px;
    }
  }
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;

    .filter-controls {
      width: 100%;

      .q-select {
        width: 100%;
      }
    }
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .category-list {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}
</style>
