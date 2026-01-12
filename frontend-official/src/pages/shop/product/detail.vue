<template>
  <q-page class="product-detail-page">
    <div v-if="loading" class="loading-wrapper">
      <q-spinner-dots color="primary" size="50px" />
    </div>

    <div v-else-if="!product" class="error-wrapper">
      <q-icon name="error_outline" size="80px" color="grey-5" />
      <p>找不到此商品</p>
      <q-btn outline color="primary" to="/shop">返回首頁</q-btn>
    </div>

    <div v-else class="product-container">
      <!-- 麵包屑 -->
      <Breadcrumb :items="breadcrumbItems" class="q-mb-lg" />

      <!-- 產品詳情區 -->
      <div class="product-detail-grid">
        <!-- 左側：產品圖片 -->
        <div class="product-images">
          <q-img
            :src="currentImage"
            :alt="product.name"
            class="main-image"
            ratio="1"
          >
            <template v-slot:error>
              <div class="absolute-full flex flex-center bg-grey-3">
                <q-icon name="broken_image" size="100px" />
              </div>
            </template>
          </q-img>

          <!-- 縮圖 -->
          <div class="thumbnail-list">
            <div
              v-for="(img, index) in product.images"
              :key="index"
              :class="['thumbnail', { active: currentImage === img }]"
              @click="currentImage = img"
            >
              <q-img :src="img" ratio="1" />
            </div>
          </div>
        </div>

        <!-- 右側：產品資訊 -->
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>

          <!-- 標籤 -->
          <div
            v-if="product.badges && product.badges.length"
            class="product-badges"
          >
            <q-chip
              v-for="badge in product.badges"
              :key="badge.text"
              :color="badge.color"
              text-color="white"
              size="sm"
            >
              {{ badge.text }}
            </q-chip>
          </div>

          <!-- 標籤列表 -->
          <div v-if="product.tags && product.tags.length" class="product-tags">
            <q-chip
              v-for="tag in product.tags"
              :key="tag"
              color="primary"
              text-color="white"
              size="sm"
              outline
            >
              {{ tag }}
            </q-chip>
          </div>

          <div class="product-price">
            <span class="current-price">${{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">
              ${{ product.originalPrice }}
            </span>
          </div>

          <q-separator class="q-my-md" />

          <!-- 產品簡介 -->
          <div v-if="product.description" class="product-description">
            <h3>商品簡介</h3>
            <p>{{ product.description }}</p>
          </div>

          <q-separator class="q-my-md" />

          <!-- 數量選擇 -->
          <div class="quantity-section">
            <label>數量：</label>
            <q-btn
              flat
              dense
              round
              icon="remove"
              @click="decreaseQuantity"
              :disable="quantity <= 1"
            />
            <span class="quantity-value">{{ quantity }}</span>
            <q-btn flat dense round icon="add" @click="increaseQuantity" />
          </div>

          <!-- 操作按鈕 -->
          <div class="action-buttons">
            <q-btn
              unelevated
              color="primary"
              text-color="white"
              size="lg"
              class="btn-add-cart"
              @click="handleAddToCart"
            >
              <q-icon name="shopping_cart" left />
              加入購物車
            </q-btn>
            <q-btn
              outline
              color="primary"
              size="lg"
              class="btn-buy-now"
              @click="handleBuyNow"
            >
              立即購買
            </q-btn>
          </div>
        </div>
      </div>

      <!-- 詳細說明 -->
      <div v-if="product.descriptionBlocks && product.descriptionBlocks.length" class="product-detail-tabs">
        <q-tabs v-model="tab" class="text-primary">
          <q-tab 
            v-for="block in product.descriptionBlocks" 
            :key="block.id"
            :name="`block-${block.id}`" 
            :label="block.title" 
          />
        </q-tabs>

        <q-separator />

        <q-tab-panels v-model="tab" animated>
          <q-tab-panel 
            v-for="block in product.descriptionBlocks" 
            :key="block.id"
            :name="`block-${block.id}`"
          >
            <div class="detail-content">
              <!-- 區塊標題 -->
              <h3 v-if="block.title">{{ block.title }}</h3>
              
              <!-- 區塊圖片 -->
              <div v-if="block.imageUrl" class="block-image">
                <q-img 
                  :src="block.imageUrl" 
                  :alt="block.title"
                  fit="contain"
                  class="q-mb-md"
                />
              </div>
              
              <!-- 區塊內容 -->
              <div v-if="block.content" v-html="formatBlockContent(block.content)"></div>
              
              <!-- 如果沒有內容和圖片 -->
              <p v-if="!block.content && !block.imageUrl" class="text-grey-6">
                此區塊暫無內容
              </p>
            </div>
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { addToCart } from 'src/utils/cart.js';
import Breadcrumb from 'src/components/shop/Breadcrumb.vue';
import { getProductDetail, getProductCategory } from 'src/api/product.js';

const route = useRoute();
const router = useRouter();
const $q = useQuasar();

const loading = ref(true);
const product = ref(null);
const currentImage = ref('');
const quantity = ref(1);
const tab = ref('');

// 格式化區塊內容（將換行符轉換為 HTML）
const formatBlockContent = (content) => {
  if (!content) return '';
  // 將換行符轉換為 <br>，並保持段落格式
  return content
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
    .replace(/^(.+)$/, '<p>$1</p>');
};

// 麵包屑項目
const breadcrumbItems = computed(() => {
  if (!product.value) return [];
  return [
    { label: '首頁', to: '/shop' },
    {
      label: getCategoryName(product.value.category),
      to: `/shop/product/list?category=${product.value.category}`,
    },
    { label: product.value.name, to: '' },
  ];
});

// 分類名稱映射
const categoryMap = {
  bath: '沐浴用品',
  toy: '玩具精品',
  food: '日系零食',
  home: '居家用品',
  daily: '生活雜貨',
};

const getCategoryName = category => {
  return categoryMap[category] || '其他商品';
};

const increaseQuantity = () => {
  quantity.value++;
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const handleAddToCart = () => {
  addToCart(product.value, quantity.value);
  $q.notify({
    message: `已將 ${quantity.value} 件「${product.value.name}」加入購物車`,
    color: 'positive',
    position: 'top',
    icon: 'check_circle',
    timeout: 2000,
  });
};

const handleBuyNow = () => {
  handleAddToCart();
  $q.notify({
    message: '正在前往結帳頁面...',
    color: 'info',
    position: 'top',
    timeout: 1500,
  });
};

// 轉換 API 資料格式
const mapProductData = (apiData) => {
  // 提取主圖片
  const primaryImage = apiData.images?.find(img => img.isPrimary);
  const allImages = apiData.images?.map(img => img.imageUrl) || [];
  
  // 提取標籤
  const tags = apiData.tags || [];
  
  // 計算是否有折扣
  const hasDiscount = apiData.salePrice && apiData.basePrice && apiData.salePrice < apiData.basePrice;
  
  return {
    id: apiData.id,
    name: apiData.name,
    price: apiData.salePrice || apiData.basePrice,
    originalPrice: hasDiscount ? apiData.basePrice : null,
    category: apiData.categoryId,
    sku: apiData.sku,
    description: apiData.description, // 商品簡介（右側顯示）
    images: allImages,
    tags: tags,
    badges: [],
    // 規格選項（如有多規格）
    specifications: apiData.specifications || [],
    // 商品描述區塊（下方 tab-panel 顯示，只顯示已啟用的區塊）
    descriptionBlocks: (apiData.descriptionBlocks || [])
      .filter(block => block.enabled)
      .sort((a, b) => a.blockOrder - b.blockOrder),
  };
};

// 載入產品資料
const fetchProduct = async () => {
  loading.value = true;
  try {
    const productId = route.params.id;
    const response = await getProductDetail(productId);
    
    console.log('商品詳情 API 回應:', response);
    
    if (response && response.data) {
      product.value = mapProductData(response.data);
      
      // 設定主圖片
      if (product.value.images && product.value.images.length > 0) {
        currentImage.value = product.value.images[0];
      }
      
      // 設定第一個 tab 為預設值
      if (product.value.descriptionBlocks && product.value.descriptionBlocks.length > 0) {
        tab.value = `block-${product.value.descriptionBlocks[0].id}`;
      }
      
      // 載入分類名稱
      if (product.value.category) {
        try {
          const categoryResponse = await getProductCategory(product.value.category);
          if (categoryResponse && categoryResponse.data) {
            categoryMap[product.value.category] = categoryResponse.data.name;
          }
        } catch (error) {
          console.warn('載入分類名稱失敗:', error);
        }
      }
    } else {
      $q.notify({
        type: 'negative',
        message: '找不到此商品',
        position: 'top',
      });
      product.value = null;
    }
  } catch (error) {
    console.error('載入商品失敗:', error);
    $q.notify({
      type: 'negative',
      message: '載入商品失敗，請稍後再試',
      position: 'top',
    });
    product.value = null;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchProduct();
});
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.product-detail-page {
  background: $shop-bg-light;
  padding: 30px 0;
  min-height: calc(100vh - 200px);
}

.loading-wrapper,
.error-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  gap: 20px;

  p {
    font-size: 1.1rem;
    color: $shop-text-secondary;
  }
}

.product-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.product-detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.product-images {
  .main-image {
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    border: 1px solid $shop-gray-light;
  }

  .thumbnail-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;

    .thumbnail {
      border: 2px solid transparent;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;

      &:hover,
      &.active {
        border-color: $shop-primary;
      }
    }
  }
}

.product-info {
  .product-title {
    font-size: 1.8rem;
    color: $shop-text;
    margin-bottom: 15px;
    font-weight: 600;
  }

  .product-badges,
  .product-tags {
    margin-bottom: 15px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .product-price {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 15px;

    .current-price {
      color: $shop-danger;
      font-size: 2rem;
      font-weight: bold;
    }

    .original-price {
      color: $shop-text-secondary;
      font-size: 1.2rem;
      text-decoration: line-through;
    }
  }

  .product-description {
    margin-bottom: 20px;

    h3 {
      font-size: 1.1rem;
      color: $shop-text;
      margin-bottom: 10px;
      font-weight: 600;
    }

    p {
      color: $shop-text-secondary;
      line-height: 1.6;
    }
  }

  .quantity-section {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;

    label {
      font-weight: 500;
      color: $shop-text;
    }

    .quantity-value {
      min-width: 40px;
      text-align: center;
      font-size: 1.2rem;
      font-weight: 500;
    }
  }

  .action-buttons {
    display: flex;
    gap: 15px;

    .q-btn {
      flex: 1;
      padding: 12px 0;
    }
  }
}

.product-detail-tabs {
  background: white;
  border-radius: 12px;
  padding: 30px;

  .detail-content {
    h3 {
      color: $shop-text;
      margin-bottom: 15px;
      font-weight: 600;
    }

    .block-image {
      max-width: 100%;
      margin-bottom: 20px;
      border-radius: 8px;
      overflow: hidden;
    }

    p {
      color: $shop-text-secondary;
      line-height: 1.8;
      margin-bottom: 15px;
    }

    ul {
      padding-left: 20px;
      color: $shop-text-secondary;

      li {
        margin-bottom: 10px;
        line-height: 1.6;
      }
    }
  }
}

@media (max-width: 768px) {
  .product-detail-grid {
    grid-template-columns: 1fr;
    padding: 20px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
