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
                  <q-item-label caption class="text-grey-7"
                    >{{ totalElements }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-separator spaced="sm" />

              <!-- 動態分類列表 -->
              <q-item
                v-for="category in categories"
                :key="category.id"
                clickable
                v-ripple
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
                  <q-item-label caption class="text-grey-7">
                    {{ category.productCount || 0 }} 件
                  </q-item-label>
                </q-item-section>
              </q-item>

              <!-- 分類載入中 -->
              <q-item v-if="categories.length === 0 && !loading">
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
                共 <strong>{{ totalElements }}</strong> 件商品
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

          <div v-else-if="filteredProducts.length === 0" class="no-products">
            <q-icon name="inventory_2" size="80px" color="grey-5" />
            <p class="text-h6 text-grey-7 q-mt-md">目前此分類暫無商品</p>
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
              v-for="product in filteredProducts"
              :key="product.id"
              :product="product"
              @click="goToDetail(product.id)"
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
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import ProductCard from 'src/components/shop/ProductCard.vue';
import Breadcrumb from 'src/components/shop/Breadcrumb.vue';
import {
  getProductList,
  getProductsByCategory,
  getProductsByStatus,
  getEnabledCategories,
} from 'src/api/product.js';

const route = useRoute();
const router = useRouter();
const $q = useQuasar();

const loading = ref(true);
const sortBy = ref('default');
const categoryId = computed(
  () => route.params.categoryId || route.query.category
);
const categoryName = ref('所有商品');

// 分類列表
const categories = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = 12; // 每页显示12笔
const totalPages = ref(0);
const totalElements = ref(0);

// 麵包屑項目
const breadcrumbItems = computed(() => [
  { label: '首頁', to: '/shop' },
  { label: categoryName.value, to: '' },
]);

// 排序選項
const sortOptions = [
  { label: '預設排序', value: 'default' },
  { label: '價格：低到高', value: 'price-asc' },
  { label: '價格：高到低', value: 'price-desc' },
  { label: '最新上架', value: 'newest' },
];

// 商品列表
const products = ref([]);

// 所有商品列表（用於計算分類數量）
const allProducts = ref([]);

// 當前頁顯示的商品
const filteredProducts = computed(() => {
  let result = [...products.value];

  // 前端排序（如果需要）
  if (sortBy.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price);
  } else if (sortBy.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price);
  } else if (sortBy.value === 'newest') {
    result.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));
  }

  return result;
});

// 切换页码
const changePage = page => {
  currentPage.value = page;
  fetchProducts();
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 取得特定分類的商品數量
const getCategoryCount = category => {
  const cat = categories.value.find(
    c => c.id == category || c.name === category
  );
  return cat?.productCount || 0;
};

// 切換分類
const filterByCategory = categoryIdOrAll => {
  currentPage.value = 1;
  router.push(`/shop/product/list?category=${categoryIdOrAll}`);
};

// 前往商品詳情頁
const goToDetail = productId => {
  router.push(`/shop/product/${productId}`);
};

// 統一錯誤處理
const handleError = (error, message = '操作失敗') => {
  console.error(message, error);
  let errorMsg = message;
  
  if (error.response) {
    const status = error.response.status;
    if (status === 404) {
      errorMsg = '找不到相關資料';
    } else if (status === 500) {
      errorMsg = '伺服器錯誤，請稍後再試';
    } else if (error.response.data?.message) {
      errorMsg = error.response.data.message;
    }
  } else if (error.request) {
    errorMsg = '網路連線異常，請檢查網路連線';
  }
  
  $q.notify({
    type: 'negative',
    message: errorMsg,
    position: 'top',
    timeout: 3000,
  });
};

// 獲取分類列表
const fetchCategories = async () => {
  try {
    const response = await getEnabledCategories();
    console.log('分類列表 API 回應:', response);
    
    // 根據 PRODUCT_PUBLIC_API.md 規範，回應格式為 { success, message, data }
    // 注意：axios 攔截器已經返回 response.data，所以這裡直接使用 response
    if (response?.success && response?.data) {
      categories.value = response.data.map(cat => ({
        ...cat,
        productCount: 0, // 初始化為 0，後面會更新
      }));
    }
  } catch (error) {
    handleError(error, '獲取分類列表失敗');
    // 設置空數組以避免頁面錯誤
    categories.value = [];
  }
};

// 獲取所有商品用於計算分類數量
const fetchAllProductsForCount = async () => {
  try {
    // 獲取所有上架商品（使用較大的 size 確保獲取所有商品）
    const response = await getProductsByStatus('ACTIVE', { page: 0, size: 1000 });
    
    if (response?.success && response?.data?.content) {
      allProducts.value = response.data.content;
      
      // 計算每個分類的商品數量
      categories.value = categories.value.map(cat => ({
        ...cat,
        productCount: allProducts.value.filter(p => p.categoryId === cat.id).length,
      }));
      
      console.log('已更新分類商品數量:', categories.value);
    }
  } catch (error) {
    console.error('獲取商品數量失敗:', error);
    // 不顯示錯誤訊息，因為這不影響主要功能
  }
};

// 獲取商品列表
const fetchProducts = async () => {
  loading.value = true;
  try {
    let response;
    const params = {
      page: currentPage.value - 1, // API 使用 0-based 索引
      size: pageSize,
    };

    if (categoryId.value && categoryId.value !== 'all') {
      // 根據分類查詢
      console.log('查詢分類商品:', { categoryId: categoryId.value, params });
      response = await getProductsByCategory(categoryId.value, params);
    } else {
      // 查詢所有商品（不限制狀態，使用 getProductList 而非 getProductsByStatus）
      console.log('查詢所有商品:', { params });
      response = await getProductList(params);
    }

    console.log('商品列表 API 回應:', response);
    console.log('商品數據:', response?.data?.content);

    // 根據 PRODUCT_PUBLIC_API.md 規範處理回應
    // 注意：axios 攔截器已經返回 response.data，所以這裡直接使用 response
    if (response?.success && response?.data) {
      const data = response.data;
      
      // 處理分頁資料
      if (data.content && Array.isArray(data.content)) {
        // 過濾掉非 ACTIVE 狀態的商品（確保一致性）
        const filteredContent = data.content.filter(item => item.status === 'ACTIVE');
        
        products.value = filteredContent.map(item => ({
          id: item.id,
          name: item.name,
          category: item.categoryId,
          categoryName: item.categoryName,
          price: item.salePrice || item.basePrice,
          originalPrice: item.salePrice && item.basePrice > item.salePrice ? item.basePrice : null,
          image: item.images && item.images.length > 0 ? item.images[0].imageUrl : '/placeholder.jpg',
          images: item.images || [],
          description: item.description,
          sku: item.sku,
          status: item.status,
          salesMode: item.salesMode,
          tags: item.tags || [],
          discount: item.salePrice && item.basePrice
            ? Math.round((1 - item.salePrice / item.basePrice) * 100)
            : 0,
          createdAt: item.createdAt,
        }));
        
        // 更新總數為過濾後的數量
        totalPages.value = Math.ceil(products.value.length / pageSize);
        totalElements.value = products.value.length;
      } else {
        products.value = [];
        totalPages.value = 0;
        totalElements.value = 0;
      }
    } else {
      products.value = [];
      totalPages.value = 0;
      totalElements.value = 0;
    }
  } catch (error) {
    handleError(error, '獲取商品列表失敗');
    products.value = [];
    totalPages.value = 0;
    totalElements.value = 0;
  } finally {
    loading.value = false;
  }
};

// 更新分類名稱
const updateCategoryName = () => {
  if (categoryId.value === 'all' || !categoryId.value) {
    categoryName.value = '所有商品';
  } else if (categories.value.length > 0) {
    const cat = categories.value.find(c => c.id == categoryId.value);
    categoryName.value = cat?.name || '所有商品';
  }
};

// 監聽分類變化
watch(
  () => categoryId.value,
  () => {
    currentPage.value = 1;
    updateCategoryName();
    fetchProducts();
  }
);

// 監聽排序變化
watch(sortBy, () => {
  // 排序變化時不需要重新載入資料，computed 會自動處理
});

// 載入資料
onMounted(async () => {
  await fetchCategories();
  await fetchAllProductsForCount(); // 獲取所有商品並計算分類數量
  updateCategoryName();
  await fetchProducts();
});
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
