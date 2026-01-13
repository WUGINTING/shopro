<template>
  <q-drawer
    v-model="isOpen"
    side="right"
    overlay
    elevated
    no-swipe-backdrop
    :width="400"
    :breakpoint="500"
    class="cart-drawer"
  >
    <q-scroll-area class="fit">
      <!-- 標題列 -->
      <div class="cart-header">
        <h5 class="cart-title">購物車</h5>
        <q-btn
          flat
          round
          dense
          icon="close"
          color="grey-7"
          @click="isOpen = false"
        />
      </div>

      <q-separator />

      <!-- 購物車內容 -->
      <div v-if="cartItems.length > 0" class="cart-content">
        <!-- 商品列表 -->
        <div class="cart-items">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <!-- 商品圖片 -->
            <div class="item-image">
              <q-img
                :src="item.image"
                :alt="item.name"
                ratio="1"
                spinner-color="primary"
              />
            </div>

            <!-- 商品資訊 -->
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              
              <!-- 規格選擇（如果有多個規格） -->
              <div 
                v-if="getItemSpecifications(item).length > 0" 
                class="item-spec-selector"
              >
                <q-select
                  :model-value="item.specification?.id"
                  :options="getItemSpecifications(item).map(spec => ({
                    label: `${spec.specName} - NT$ ${spec.price} (庫存: ${spec.stock})`,
                    value: spec.id,
                    disable: spec.stock === 0
                  }))"
                  dense
                  outlined
                  options-dense
                  emit-value
                  map-options
                  label="選擇規格"
                  @update:model-value="(val) => handleSpecChange(item, val)"
                  class="spec-select"
                >
                  <template v-slot:selected>
                    <div class="selected-spec">
                      {{ item.specification?.specName || '請選擇規格' }}
                    </div>
                  </template>
                </q-select>
              </div>
              
              <!-- 顯示當前規格（只顯示） -->
              <div v-else-if="item.specification" class="item-spec-info">
                <q-chip size="sm" color="primary" text-color="white" dense>
                  {{ item.specification.specName }}
                </q-chip>
              </div>
              
              <div class="item-price">
                NT$ {{ (item.selectedPrice || item.price).toLocaleString() }}
              </div>

              <!-- 數量控制 -->
              <div class="quantity-control">
                <q-btn
                  flat
                  dense
                  round
                  size="sm"
                  icon="remove"
                  @click="decreaseQuantity(item)"
                />
                <span class="quantity">{{ item.quantity }}</span>
                <q-btn
                  flat
                  dense
                  round
                  size="sm"
                  icon="add"
                  @click="increaseQuantity(item)"
                />
              </div>
            </div>

            <!-- 刪除按鈕 -->
            <q-btn
              flat
              round
              dense
              icon="delete"
              color="negative"
              class="delete-btn"
              @click="removeItem(item)"
            />
          </div>
        </div>

        <!-- 總計 -->
        <div class="cart-summary">
          <div class="summary-row">
            <span class="label">商品數量</span>
            <span class="value">{{ totalQuantity }} 件</span>
          </div>
          <div class="summary-row total">
            <span class="label">總計</span>
            <span class="value">NT$ {{ totalAmount.toLocaleString() }}</span>
          </div>
        </div>

        <!-- 操作按鈕 -->
        <div class="cart-actions">
          <q-btn
            unelevated
            color="negative"
            label="清空購物車"
            class="full-width q-mb-sm"
            @click="clearCartConfirm"
          />
          <q-btn
            unelevated
            color="primary"
            label="前往結帳"
            class="full-width"
            @click="checkout"
          />
        </div>
      </div>

      <!-- 空購物車 -->
      <div v-else class="empty-cart">
        <q-icon name="shopping_cart" size="80px" color="grey-5" />
        <div class="empty-text">購物車是空的</div>
        <q-btn
          unelevated
          color="primary"
          label="開始購物"
          @click="closeDrawer"
        />
      </div>
    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue';
import { useQuasar } from 'quasar';
import {
  getCartItems,
  updateCartItemQuantity,
  removeFromCart,
  clearCart,
  updateCartItemSpec,
} from 'src/utils/cart.js';
import { getProductSpecifications } from 'src/api/product.js';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue', 'cart-updated']);

const $q = useQuasar();

const isOpen = ref(props.modelValue);
const cartItems = ref(getCartItems());
const productSpecifications = ref({}); // 儲存各商品的規格列表

// 監聽 modelValue 變化
watch(
  () => props.modelValue,
  val => {
    isOpen.value = val;
    if (val) {
      // 打開側邊欄時重新載入購物車資料
      refreshCart();
    }
  }
);

// 監聽 isOpen 變化
watch(isOpen, val => {
  emit('update:modelValue', val);
});

// 計算總數量
const totalQuantity = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.quantity, 0);
});

// 計算總金額
const totalAmount = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);
});

// 重新載入購物車
const refreshCart = async () => {
  cartItems.value = getCartItems();
  // 載入每個商品的規格
  for (const item of cartItems.value) {
    if (item.id && !productSpecifications.value[item.id]) {
      await loadProductSpecifications(item.id);
    }
  }
};

// 載入商品規格
const loadProductSpecifications = async (productId) => {
  try {
    const response = await getProductSpecifications(productId);
    if (response && response.data) {
      productSpecifications.value[productId] = response.data;
    }
  } catch (error) {
    console.warn(`載入商品 ${productId} 規格失敗:`, error);
  }
};

// 取得商品的規格列表
const getItemSpecifications = (item) => {
  return productSpecifications.value[item.id] || [];
};

// 處理規格變更
const handleSpecChange = (item, newSpecId) => {
  const specs = getItemSpecifications(item);
  const newSpec = specs.find(s => s.id === newSpecId);
  
  if (!newSpec) return;
  
  const oldSpecId = item.specification?.id || null;
  
  // 檢查庫存
  if (newSpec.stock === 0) {
    $q.notify({
      type: 'warning',
      message: '此規格已售完',
      position: 'top',
      timeout: 1500,
    });
    return;
  }
  
  updateCartItemSpec(item.id, oldSpecId, newSpec);
  refreshCart();
  emit('cart-updated');
};

// 增加數量
const increaseQuantity = item => {
  const specId = item.specification?.id || null;
  const maxStock = item.specification?.stock || 999;
  
  if (item.quantity >= maxStock) {
    $q.notify({
      type: 'warning',
      message: '已達最大購買數量',
      position: 'top',
      timeout: 1000,
    });
    return;
  }
  
  updateCartItemQuantity(item.id, item.quantity + 1, specId);
  refreshCart();
  emit('cart-updated');
};

// 減少數量
const decreaseQuantity = item => {
  const specId = item.specification?.id || null;
  if (item.quantity > 1) {
    updateCartItemQuantity(item.id, item.quantity - 1, specId);
    refreshCart();
    emit('cart-updated');
  } else {
    removeItem(item);
  }
};

// 移除商品
const removeItem = item => {
  const specId = item.specification?.id || null;
  const displayName = item.specification 
    ? `${item.name} (${item.specification.specName})`
    : item.name;
    
  $q.dialog({
    title: '確認刪除',
    message: `確定要將「${displayName}」從購物車移除嗎？`,
    cancel: true,
    persistent: true,
  }).onOk(() => {
    removeFromCart(item.id, specId);
    refreshCart();
    emit('cart-updated');
    $q.notify({
      message: '已移除商品',
      color: 'positive',
      position: 'top',
      timeout: 1500,
    });
  });
};

// 清空購物車確認
const clearCartConfirm = () => {
  $q.dialog({
    title: '確認清空',
    message: '確定要清空購物車嗎？',
    cancel: true,
    persistent: true,
  }).onOk(() => {
    clearCart();
    refreshCart();
    emit('cart-updated');
    $q.notify({
      message: '購物車已清空',
      color: 'positive',
      position: 'top',
      timeout: 1500,
    });
  });
};

// 前往結帳
const checkout = () => {
  $q.notify({
    message: '前往結帳頁面...',
    color: 'primary',
    position: 'top',
    timeout: 2000,
  });
  // 這裡可以導航到結帳頁面
  // router.push('/shop/checkout')
};

// 關閉側邊欄
const closeDrawer = () => {
  isOpen.value = false;
};

// 監聽購物車更新事件（即時同步）
const handleCartUpdate = (event) => {
  refreshCart();
};

onMounted(() => {
  // 監聽自定義的購物車更新事件
  window.addEventListener('cart-updated', handleCartUpdate);
});

onBeforeUnmount(() => {
  // 清理事件監聽器
  window.removeEventListener('cart-updated', handleCartUpdate);
});
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.cart-drawer {
  z-index: 6000 !important;

  :deep(.q-drawer__content) {
    background: white !important;
    top: 0 !important;
    height: 100vh !important;
    box-shadow: -4px 0 16px rgba(0, 0, 0, 0.2);
  }

  :deep(.q-drawer__backdrop) {
    z-index: 5999 !important;
  }
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: white;

  .cart-title {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: $shop-text;
  }
}

.cart-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  margin-bottom: 12px;
  background: white;
  border: 1px solid $shop-gray-light;
  border-radius: 8px;
  position: relative;

  .item-image {
    width: 80px;
    height: 80px;
    flex-shrink: 0;
    border-radius: 4px;
    overflow: hidden;
    background: #f5f5f5;
  }

  .item-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .item-name {
      font-size: 0.95rem;
      font-weight: 500;
      color: $shop-text;
      line-height: 1.4;
    }

    .item-spec-selector {
      margin: 4px 0;

      .spec-select {
        font-size: 0.85rem;
        
        :deep(.q-field__control) {
          min-height: 32px;
          padding: 0 8px;
        }
        
        :deep(.q-field__native) {
          padding: 4px 0;
        }
      }

      .selected-spec {
        font-size: 0.85rem;
        color: $shop-primary;
        font-weight: 500;
      }
    }

    .item-spec-info {
      margin: 2px 0;
    }

    .item-price {
      font-size: 1rem;
      font-weight: 600;
      color: $shop-danger;
    }
  }

  .quantity-control {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: auto;

    .quantity {
      min-width: 30px;
      text-align: center;
      font-weight: 500;
      color: $shop-text;
    }

    :deep(.q-btn) {
      background: $shop-bg-light;
      color: $shop-text;
    }
  }

  .delete-btn {
    position: absolute;
    top: 8px;
    right: 8px;
  }
}

.cart-summary {
  padding: 16px 20px;
  background: $shop-bg-light;
  border-top: 1px solid $shop-gray-light;

  .summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-size: 0.95rem;

    &.total {
      margin-top: 8px;
      padding-top: 12px;
      border-top: 2px solid $shop-primary;
      font-size: 1.1rem;
      font-weight: 600;

      .label {
        color: $shop-text;
      }

      .value {
        color: $shop-danger;
      }
    }

    .label {
      color: $shop-text-secondary;
    }

    .value {
      color: $shop-text;
      font-weight: 500;
    }
  }
}

.cart-actions {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid $shop-gray-light;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 100px);
  padding: 40px 20px;
  text-align: center;

  .empty-text {
    font-size: 1.1rem;
    color: $shop-text-secondary;
    margin: 20px 0 30px;
  }
}
</style>
