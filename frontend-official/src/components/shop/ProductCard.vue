<template>
  <q-card class="product-card" flat bordered>
    <!-- 商品標籤 -->
    <div v-if="tag" :class="['shop-tag', tagClass]">
      {{ tagText }}
    </div>

    <!-- 商品圖片 -->
    <div class="product-image-box">
      <q-img
        :src="
          product.image || 'https://via.placeholder.com/250x200?text=Product'
        "
        :alt="product.name"
        ratio="1"
        spinner-color="primary"
      >
        <template v-slot:error>
          <div class="absolute-full flex flex-center bg-grey-3 text-grey-6">
            <q-icon name="broken_image" size="50px" />
          </div>
        </template>
      </q-img>
    </div>

    <!-- 商品詳情 -->
    <q-card-section class="product-details">
      <div class="product-name text-subtitle1 q-mb-sm ellipsis">
        {{ product.name }}
      </div>

      <!-- 價格 -->
      <div class="product-price">
        <span v-if="product.originalPrice" class="original-price">
          ${{ product.originalPrice.toLocaleString() }}
        </span>
        <span class="current-price">
          ${{ product.price.toLocaleString() }}
        </span>
      </div>

      <!-- 加入購物車按鈕 -->
      <q-btn
        unelevated
        color="dark"
        text-color="white"
        class="full-width q-mt-sm add-cart-btn"
        label="加入購物車"
        @click="handleAddToCart"
      />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['add-to-cart']);

// 計算商品標籤
const tag = computed(() => props.product.tag);

const tagClass = computed(() => {
  const tagMap = {
    hot: 'hot',
    new: 'new',
    'pre-order': 'pre-order',
  };
  return tagMap[tag.value] || '';
});

const tagText = computed(() => {
  const textMap = {
    hot: 'HOT',
    new: 'NEW',
    'pre-order': '預購',
  };
  return textMap[tag.value] || '';
});

const handleAddToCart = () => {
  emit('add-to-cart', props.product);
};
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

// =========================================
// 商品卡片 (參考 demo1.html)
// =========================================
.product-card {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  position: relative;
  background: white;
  border: 1px solid #eee;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.12);
  }
}

// 商品標籤
.shop-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px 10px;
  font-size: 0.75rem;
  color: white;
  font-weight: bold;
  border-radius: 4px;
  z-index: 2;
  letter-spacing: 0.5px;

  &.hot {
    background: #e74c3c;
  }

  &.new {
    background: #2ecc71;
  }

  &.pre-order {
    background: #9b59b6;
  }
}

// 商品圖片容器
.product-image-box {
  background: #f4f4f4;
  height: 200px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  :deep(.q-img) {
    width: 100%;
    height: 100%;
  }
}

// 商品詳情
.product-details {
  padding: 15px;
  background: white;
}

// 商品名稱
.product-name {
  font-size: 1rem;
  color: #333;
  line-height: 1.4;
  min-height: 2.8em;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

// 價格顯示
.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;

  .original-price {
    color: #999;
    font-size: 0.85rem;
    text-decoration: line-through;
    font-weight: normal;
  }

  .current-price {
    color: #e74c3c;
    font-weight: bold;
    font-size: 1.1rem;
  }
}

// 加入購物車按鈕
.add-cart-btn {
  width: 100%;
  padding: 8px;
  background: #333 !important;
  color: white !important;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;

  &:hover {
    background: $shop-primary !important;
  }
}

// =========================================
// 響應式設計 - 平板
// =========================================
@media (max-width: 768px) {
  .product-image-box {
    height: 180px;
  }

  .product-details {
    padding: 12px;
  }

  .product-name {
    font-size: 0.95rem;
    min-height: 2.6em;
  }

  .product-price {
    .current-price {
      font-size: 1rem;
    }

    .original-price {
      font-size: 0.8rem;
    }
  }

  .add-cart-btn {
    padding: 7px;
    font-size: 0.9rem;
  }
}

// =========================================
// 響應式設計 - 手機
// =========================================
@media (max-width: 480px) {
  .product-image-box {
    height: 150px;
  }

  .product-details {
    padding: 10px;
  }

  .product-name {
    font-size: 0.9rem;
    min-height: 2.4em;
    margin-bottom: 6px;
  }

  .product-price {
    gap: 6px;
    margin-bottom: 8px;

    .current-price {
      font-size: 0.95rem;
    }

    .original-price {
      font-size: 0.75rem;
    }
  }

  .add-cart-btn {
    padding: 6px;
    font-size: 0.85rem;
  }

  .shop-tag {
    top: 8px;
    left: 8px;
    padding: 4px 8px;
    font-size: 0.7rem;
  }
}

// =========================================
// 響應式設計 - 超小螢幕
// =========================================
@media (max-width: 360px) {
  .product-image-box {
    height: 130px;
  }

  .product-details {
    padding: 8px;
  }

  .product-name {
    font-size: 0.85rem;
    min-height: 2.2em;
  }

  .product-price {
    .current-price {
      font-size: 0.9rem;
    }

    .original-price {
      font-size: 0.7rem;
    }
  }

  .add-cart-btn {
    padding: 5px;
    font-size: 0.8rem;
  }
}
</style>
