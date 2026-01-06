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
              <q-item
                clickable
                v-ripple
                :active="!categoryId || categoryId === 'all'"
                active-class="category-active"
                @click="filterByCategory('all')"
              >
                <q-item-section>
                  <q-item-label>所有商品</q-item-label>
                  <q-item-label caption
                    >{{ allProducts.length }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-separator spaced />

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'dessert'"
                active-class="category-active"
                @click="filterByCategory('dessert')"
              >
                <q-item-section avatar>
                  <q-icon name="icecream" color="pink" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>甜筒</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('dessert') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'japanese'"
                active-class="category-active"
                @click="filterByCategory('japanese')"
              >
                <q-item-section avatar>
                  <q-icon name="pets" color="orange" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>日系產品</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('japanese') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'bath'"
                active-class="category-active"
                @click="filterByCategory('bath')"
              >
                <q-item-section avatar>
                  <q-icon name="bathtub" color="blue" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>沐浴用品</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('bath') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'toy'"
                active-class="category-active"
                @click="filterByCategory('toy')"
              >
                <q-item-section avatar>
                  <q-icon name="toys" color="purple" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>玩具精品</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('toy') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'food'"
                active-class="category-active"
                @click="filterByCategory('food')"
              >
                <q-item-section avatar>
                  <q-icon name="restaurant" color="red" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>日系零食</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('food') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'home'"
                active-class="category-active"
                @click="filterByCategory('home')"
              >
                <q-item-section avatar>
                  <q-icon name="home" color="green" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>居家用品</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('home') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'daily'"
                active-class="category-active"
                @click="filterByCategory('daily')"
              >
                <q-item-section avatar>
                  <q-icon name="shopping_basket" color="teal" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>生活雜貨</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('daily') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="categoryId === 'other'"
                active-class="category-active"
                @click="filterByCategory('other')"
              >
                <q-item-section avatar>
                  <q-icon name="category" color="grey" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>其他</q-item-label>
                  <q-item-label caption
                    >{{ getCategoryCount('other') }} 件</q-item-label
                  >
                </q-item-section>
              </q-item>
            </q-list>
          </div>
        </aside>

        <!-- 右側商品列表區 -->
        <div class="product-content">
          <!-- 篩選和排序 -->
          <div class="filter-section">
            <div class="result-count">
              共 {{ allFilteredProducts.length }} 件商品
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
              />
            </div>
          </div>

          <!-- 商品列表 -->
          <div v-if="loading" class="loading-wrapper">
            <q-spinner-dots color="primary" size="50px" />
          </div>

          <div v-else-if="filteredProducts.length === 0" class="no-products">
            <q-icon name="inventory_2" size="80px" color="grey-5" />
            <p>目前此分類暫無商品</p>
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
import ProductCard from 'src/components/shop/ProductCard.vue';
import Breadcrumb from 'src/components/shop/Breadcrumb.vue';
import { Notify } from 'quasar';
import {
  getProductList,
  getProductsByCategory,
  getProductsByStatus,
  getTopCategories,
} from 'src/api/product.js';
import { shopAllProducts } from 'src/utils/testData.js';

const route = useRoute();
const router = useRouter();

// API 模式切換 (true: 使用真實 API, false: 使用測試數據)
const useRealAPI = ref(true);

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
const pageSize = 9; // 每页显示9笔

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

// 使用统一的测试数据
const allProducts = ref(shopAllProducts);

// 根據分類 ID 篩選商品（不分页）
const allFilteredProducts = computed(() => {
  let products = [...allProducts.value];

  // 如果有分類 ID，則篩選該分類的商品
  if (categoryId.value && categoryId.value !== 'all') {
    products = products.filter(p => p.category === categoryId.value);
  }

  // 排序
  if (sortBy.value === 'price-asc') {
    products.sort((a, b) => a.price - b.price);
  } else if (sortBy.value === 'price-desc') {
    products.sort((a, b) => b.price - a.price);
  } else if (sortBy.value === 'newest') {
    products.sort((a, b) => (b.new ? 1 : 0) - (a.new ? 1 : 0));
  }

  return products;
});

// 当前页显示的商品（分页后）
const filteredProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return allFilteredProducts.value.slice(start, end);
});

// 总页数
const totalPages = computed(() => {
  return Math.ceil(allFilteredProducts.value.length / pageSize);
});

// 切换页码
const changePage = page => {
  currentPage.value = page;
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 分類名稱映射 (用於測試數據模式)
const categoryMap = {
  all: '所有商品',
  dessert: '甜筒',
  japanese: '日系產品',
  bath: '沐浴用品',
  toy: '玩具精品',
  food: '日系零食',
  home: '居家用品',
  daily: '生活雜貨',
  other: '其他',
};

// 取得特定分類的商品數量
const getCategoryCount = category => {
  if (useRealAPI.value) {
    // API 模式：從分類列表中查找
    const cat = categories.value.find(
      c => c.code === category || c.id == category
    );
    return cat?.productCount || 0;
  } else {
    // 測試數據模式
    if (category === 'japanese') {
      return allProducts.value.filter(p =>
        ['bath', 'toy', 'food'].includes(p.category)
      ).length;
    } else if (category === 'dessert') {
      return allProducts.value.filter(p => p.category === 'dessert').length;
    } else if (category === 'other') {
      return allProducts.value.filter(p => p.category === 'other').length;
    }
    return allProducts.value.filter(p => p.category === category).length;
  }
};

// 切換分類
const filterByCategory = category => {
  currentPage.value = 1; // 切换分类时重置页码
  router.push(`/shop/product/list?category=${category}`);
};

// 前往商品詳情頁
const goToDetail = productId => {
  router.push(`/shop/product/${productId}`);
};

// 獲取分類列表
const fetchCategories = async () => {
  try {
    const response = await getTopCategories();
    if (response && response.data) {
      categories.value = response.data;
    }
  } catch (error) {
    console.error('獲取分類列表失敗:', error);
    Notify.create({
      type: 'negative',
      message: '獲取分類列表失敗，請稍後再試',
      position: 'top',
    });
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
      response = await getProductsByCategory(categoryId.value, params);
    } else {
      // 查詢所有商品 (只顯示上架商品)
      response = await getProductsByStatus('ACTIVE', params);
    }

    console.log('商品列表 API 回應:', response);

    if (response && response.data) {
      // 假設 API 返回格式：{ content: [], totalElements: 0, totalPages: 0 }
      const data = response.data;
      if (data.content) {
        allProducts.value = data.content.map(item => ({
          id: item.id,
          name: item.name,
          category: item.categoryId,
          price: item.salePrice || item.basePrice,
          originalPrice: item.salePrice ? item.basePrice : null,
          image:
            item.images && item.images.length > 0
              ? item.images[0].imageUrl
              : '',
          images: item.images || [],
          description: item.description,
          sku: item.sku,
          status: item.status,
          new: item.isNew || false,
          hot: item.isHot || false,
          discount: item.salePrice
            ? Math.round((1 - item.salePrice / item.basePrice) * 100)
            : 0,
        }));
      }
    }
  } catch (error) {
    console.error('獲取商品列表失敗:', error);
    Notify.create({
      type: 'negative',
      message: '獲取商品列表失敗，切換至測試數據模式',
      position: 'top',
    });
    // 切換到測試數據模式
    useRealAPI.value = false;
    allProducts.value = shopAllProducts;
  } finally {
    loading.value = false;
  }
};

// 監聽分類變化
watch(
  () => categoryId.value,
  () => {
    currentPage.value = 1;
    if (useRealAPI.value) {
      fetchProducts();
    }
    // 更新分類名稱
    if (useRealAPI.value && categories.value.length > 0) {
      const cat = categories.value.find(
        c => c.code === categoryId.value || c.id == categoryId.value
      );
      categoryName.value = cat?.name || '所有商品';
    } else {
      categoryName.value = categoryMap[categoryId.value] || '所有商品';
    }
  }
);

// 載入資料
onMounted(async () => {
  if (useRealAPI.value) {
    // 使用真實 API
    await fetchCategories();
    await fetchProducts();

    // 設定分類名稱
    if (categories.value.length > 0) {
      const cat = categories.value.find(
        c => c.code === categoryId.value || c.id == categoryId.value
      );
      categoryName.value = cat?.name || '所有商品';
    }
  } else {
    // 使用測試數據
    setTimeout(() => {
      categoryName.value = categoryMap[categoryId.value] || '所有商品';
      loading.value = false;
    }, 500);
  }
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
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .sidebar-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: $shop-text;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid $shop-primary;
  }

  .category-list {
    .q-item {
      border-radius: 8px;
      margin-bottom: 5px;
      transition: all 0.3s;

      &:hover {
        background: $shop-bg-light;
      }

      &.category-active {
        background: rgba($shop-primary, 0.1);
        color: $shop-primary;
        font-weight: 600;

        .q-item__label {
          color: $shop-primary;
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
  padding: 20px;
  border-radius: 8px;

  .result-count {
    font-size: 1rem;
    color: $shop-text-secondary;
  }

  .filter-controls {
    display: flex;
    gap: 15px;
  }
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.no-products {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: $shop-text-secondary;

  p {
    margin-top: 20px;
    font-size: 1.1rem;
  }
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
