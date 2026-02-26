<template>
  <div class="checkout-page">
    <!-- 頁面標題 -->
    <div class="page-header">
      <q-btn
        flat
        round
        icon="arrow_back"
        color="grey-8"
        @click="goBack"
        class="back-btn"
      />
      <h4 class="page-title">結帳</h4>
      <div class="placeholder"></div>
    </div>

    <div class="checkout-container">
      <div class="checkout-content">
        <!-- 左側：購物明細 -->
        <div class="checkout-left">
          <!-- 訂單商品列表 -->
          <div class="section order-items-section">
            <div class="section-header">
              <q-icon name="shopping_bag" size="24px" color="primary" />
              <h5 class="section-title">訂單商品 ({{ cartItems.length }})</h5>
            </div>

            <div v-if="cartItems.length > 0" class="order-items">
              <div
                v-for="item in cartItems"
                :key="`${item.id}-${item.specification?.id || 'default'}`"
                class="order-item"
              >
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
                <div class="item-details">
                  <div class="item-name">{{ item.name }}</div>

                  <!-- 規格 -->
                  <div v-if="item.specification" class="item-spec">
                    <q-chip size="sm" color="primary" text-color="white" dense>
                      {{ item.specification.specName }}
                    </q-chip>
                  </div>

                  <div class="item-meta">
                    <span class="item-price">
                      NT$ {{ (item.selectedPrice || item.price).toLocaleString() }}
                    </span>
                    <span class="item-quantity">x {{ item.quantity }}</span>
                  </div>
                </div>

                <!-- 小計 -->
                <div class="item-subtotal">
                  NT$ {{ ((item.selectedPrice || item.price) * item.quantity).toLocaleString() }}
                </div>

                <!-- 移除按鈕 -->
                <q-btn
                  flat
                  round
                  dense
                  icon="close"
                  size="sm"
                  color="grey-5"
                  class="item-remove-btn"
                  @click="removeCartItem(item)"
                >
                  <q-tooltip>移除商品</q-tooltip>
                </q-btn>
              </div>
            </div>

            <!-- 空購物車提示 -->
            <div v-else class="empty-cart-notice">
              <q-icon name="shopping_cart" size="60px" color="grey-5" />
              <p class="empty-text">購物車是空的</p>
              <q-btn
                unelevated
                color="primary"
                label="開始購物"
                @click="goToShop"
              />
            </div>
          </div>

          <!-- 收件人資訊 -->
          <div class="section recipient-section">
            <div class="section-header">
              <q-icon name="person" size="24px" color="primary" />
              <h5 class="section-title">收件人資訊</h5>
            </div>

            <div class="form-content">
              <div class="form-row">
                <q-input
                  v-model="recipientInfo.name"
                  outlined
                  label="姓名 *"
                  dense
                  :rules="[val => !!val || '請輸入姓名']"
                  class="form-input"
                >
                  <template v-slot:prepend>
                    <q-icon name="badge" />
                  </template>
                </q-input>
              </div>

              <div class="form-row">
                <q-input
                  v-model="recipientInfo.phone"
                  outlined
                  label="聯絡電話 *"
                  dense
                  :rules="[
                    val => !!val || '請輸入聯絡電話',
                    val => /^09\d{8}$/.test(val) || '請輸入正確的手機號碼格式 (09xxxxxxxx)'
                  ]"
                  class="form-input"
                >
                  <template v-slot:prepend>
                    <q-icon name="phone" />
                  </template>
                </q-input>
              </div>

              <div class="form-row">
                <q-input
                  v-model="recipientInfo.email"
                  outlined
                  label="電子郵件 *"
                  type="email"
                  dense
                  :rules="[
                    val => !!val || '請輸入電子郵件',
                    val => /.+@.+\..+/.test(val) || '請輸入正確的電子郵件格式'
                  ]"
                  class="form-input"
                >
                  <template v-slot:prepend>
                    <q-icon name="email" />
                  </template>
                </q-input>
              </div>

              <div class="form-row">
                <q-input
                  v-model="recipientInfo.address"
                  outlined
                  label="收件地址 *"
                  dense
                  :rules="[val => !!val || '請輸入收件地址']"
                  class="form-input"
                >
                  <template v-slot:prepend>
                    <q-icon name="home" />
                  </template>
                </q-input>
              </div>

              <div class="form-row">
                <q-input
                  v-model="recipientInfo.note"
                  outlined
                  label="訂單備註"
                  type="textarea"
                  dense
                  rows="3"
                  class="form-input"
                >
                  <template v-slot:prepend>
                    <q-icon name="note" />
                  </template>
                </q-input>
              </div>
            </div>
          </div>

          <!-- 付款方式 -->
          <div class="section payment-section">
            <div class="section-header">
              <q-icon name="payment" size="24px" color="primary" />
              <h5 class="section-title">付款方式</h5>
            </div>

            <div class="payment-options">
              <q-option-group
                v-model="paymentMethod"
                :options="paymentOptions"
                color="primary"
                inline
                class="payment-group"
              >
                <template v-slot:label="opt">
                  <div class="payment-option-label">
                    <q-icon :name="opt.icon" size="24px" />
                    <span>{{ opt.label }}</span>
                  </div>
                </template>
              </q-option-group>

              <!-- 付款方式說明 -->
              <div class="payment-note">
                <q-icon name="info" size="18px" color="grey-6" />
                <span v-if="paymentMethod === 'credit-card'">
                  支援 VISA、MasterCard、JCB 等主要信用卡
                </span>
                <span v-else-if="paymentMethod === 'atm'">
                  提交訂單後將提供銀行帳號資訊，請於 3 天內完成轉帳
                </span>
                <span v-else-if="paymentMethod === 'cod'">
                  商品送達時請準備現金付款給配送人員
                </span>
                <span v-else>
                  訂單提交後將導向第三方支付平台完成付款
                </span>
              </div>
            </div>
          </div>

          <!-- 優惠券 -->
          <div class="section coupon-section">
            <div class="section-header">
              <q-icon name="local_offer" size="24px" color="primary" />
              <h5 class="section-title">優惠券</h5>
            </div>

            <div class="coupon-input-wrapper">
              <q-input
                v-model="couponCode"
                outlined
                label="輸入優惠券代碼"
                dense
                class="coupon-input"
              >
                <template v-slot:prepend>
                  <q-icon name="confirmation_number" />
                </template>
              </q-input>
              <q-btn
                unelevated
                color="primary"
                label="套用"
                @click="applyCoupon"
                :loading="applyingCoupon"
                :disable="!couponCode"
                class="apply-coupon-btn"
              />
            </div>

            <!-- 已套用的優惠券 -->
            <div v-if="appliedCoupon" class="applied-coupon">
              <div class="coupon-info">
                <q-icon name="check_circle" color="positive" size="20px" />
                <span class="coupon-name">{{ appliedCoupon.name }}</span>
                <span class="coupon-discount">-NT$ {{ appliedCoupon.discount.toLocaleString() }}</span>
              </div>
              <q-btn
                flat
                round
                dense
                icon="close"
                size="sm"
                @click="removeCoupon"
                class="remove-coupon-btn"
              />
            </div>
          </div>
        </div>

        <!-- 右側：訂單摘要 -->
        <div class="checkout-right">
          <div class="order-summary-sticky">
            <div class="section order-summary-section">
              <div class="section-header">
                <q-icon name="receipt_long" size="24px" color="primary" />
                <h5 class="section-title">訂單摘要</h5>
              </div>

              <div class="summary-content">
                <!-- 金額明細 -->
                <div class="summary-row">
                  <span class="label">商品小計</span>
                  <span class="value">NT$ {{ subtotal.toLocaleString() }}</span>
                </div>

                <div class="summary-row">
                  <span class="label">運費</span>
                  <span class="value">
                    <span v-if="shippingFee > 0">NT$ {{ shippingFee.toLocaleString() }}</span>
                    <span v-else class="free-shipping">免運費</span>
                  </span>
                </div>

                <div v-if="appliedCoupon" class="summary-row discount">
                  <span class="label">優惠折抵</span>
                  <span class="value">-NT$ {{ appliedCoupon.discount.toLocaleString() }}</span>
                </div>

                <q-separator class="summary-divider" />

                <!-- 總計 -->
                <div class="summary-row total">
                  <span class="label">訂單總額</span>
                  <span class="value">NT$ {{ totalAmount.toLocaleString() }}</span>
                </div>

                <!-- 免運費提示 -->
                <div v-if="freeShippingThreshold && subtotal < freeShippingThreshold" class="free-shipping-tip">
                  <q-icon name="local_shipping" size="18px" />
                  <span>
                    再消費 NT$ {{ (freeShippingThreshold - subtotal).toLocaleString() }} 即可免運費
                  </span>
                </div>
              </div>

              <!-- 提交訂單按鈕 -->
              <div class="submit-actions">
                <q-btn
                  unelevated
                  color="primary"
                  label="確認送出訂單"
                  icon-right="send"
                  size="lg"
                  class="full-width submit-btn"
                  @click="submitOrder"
                  :loading="submitting"
                  :disable="!canSubmit"
                />

                <div class="terms-agreement">
                  <q-checkbox
                    v-model="agreeTerms"
                    dense
                    color="primary"
                    class="terms-checkbox"
                  >
                    <template v-slot:default>
                      <span class="terms-text">
                        我已閱讀並同意
                        <a href="#" @click.prevent="showTerms" class="terms-link">服務條款</a>
                        與
                        <a href="#" @click.prevent="showPrivacy" class="terms-link">隱私政策</a>
                      </span>
                    </template>
                  </q-checkbox>
                </div>
              </div>

              <!-- 安全提示 -->
              <div class="security-badge">
                <q-icon name="lock" size="18px" color="positive" />
                <span>安全加密結帳</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { getCartItems, clearCart, removeFromCart } from 'src/utils/cart.js';
import { getProductDetail, getProductSpecifications } from 'src/api/product.js';

const router = useRouter();
const $q = useQuasar();

// 購物車商品
const cartItems = ref([]);

// 收件人資訊
const recipientInfo = ref({
  name: '',
  phone: '',
  email: '',
  address: '',
  note: '',
});

// 付款方式
const paymentMethod = ref('credit-card');
const paymentOptions = [
  { label: '信用卡', value: 'credit-card', icon: 'credit_card' },
  { label: 'ATM轉帳', value: 'atm', icon: 'account_balance' },
  { label: '貨到付款', value: 'cod', icon: 'local_shipping' },
  { label: '第三方支付', value: 'third-party', icon: 'account_balance_wallet' },
];

// 優惠券
const couponCode = ref('');
const appliedCoupon = ref(null);
const applyingCoupon = ref(false);

// 條款同意
const agreeTerms = ref(false);

// 提交狀態
const submitting = ref(false);

// 運費設定
const freeShippingThreshold = 1000; // 滿千免運
const baseShippingFee = 100; // 基本運費

// 計算商品小計
const subtotal = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.selectedPrice || item.price) * item.quantity;
  }, 0);
});

// 計算運費
const shippingFee = computed(() => {
  if (subtotal.value >= freeShippingThreshold) {
    return 0;
  }
  return baseShippingFee;
});

// 計算總金額
const totalAmount = computed(() => {
  let total = subtotal.value + shippingFee.value;
  if (appliedCoupon.value) {
    total -= appliedCoupon.value.discount;
  }
  return Math.max(total, 0);
});

// 是否可以提交
const canSubmit = computed(() => {
  return (
    cartItems.value.length > 0 &&
    recipientInfo.value.name &&
    recipientInfo.value.phone &&
    recipientInfo.value.email &&
    recipientInfo.value.address &&
    agreeTerms.value &&
    !submitting.value
  );
});

// 載入購物車資料
const loadCartData = () => {
  cartItems.value = getCartItems();
};

// 移除購物車商品
const removeCartItem = (item) => {
  const specId = item.specification?.id || null;
  removeFromCart(item.id, specId);
  cartItems.value = getCartItems();

  if (cartItems.value.length === 0) {
    $q.notify({
      type: 'warning',
      message: '購物車已清空，即將返回商店',
      position: 'top',
      timeout: 1500,
    });
    setTimeout(() => router.push('/shop'), 1500);
  } else {
    $q.notify({
      type: 'info',
      message: `已移除「${item.name}」`,
      position: 'top',
      timeout: 1500,
    });
  }
};

// 返回上一頁
const goBack = () => {
  router.back();
};

// 前往商店
const goToShop = () => {
  router.push('/shop');
};

// 套用優惠券
const applyCoupon = async () => {
  if (!couponCode.value.trim()) {
    return;
  }

  applyingCoupon.value = true;

  try {
    // 模擬 API 呼叫
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 模擬優惠券驗證
    if (couponCode.value.toUpperCase() === 'DISCOUNT100') {
      appliedCoupon.value = {
        code: couponCode.value,
        name: '滿額折扣',
        discount: 100,
      };
      $q.notify({
        type: 'positive',
        message: '優惠券套用成功！',
        position: 'top',
        timeout: 2000,
      });
      couponCode.value = '';
    } else {
      $q.notify({
        type: 'negative',
        message: '優惠券代碼無效或已過期',
        position: 'top',
        timeout: 2000,
      });
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '套用優惠券失敗，請稍後再試',
      position: 'top',
      timeout: 2000,
    });
  } finally {
    applyingCoupon.value = false;
  }
};

// 移除優惠券
const removeCoupon = () => {
  appliedCoupon.value = null;
  $q.notify({
    type: 'info',
    message: '已移除優惠券',
    position: 'top',
    timeout: 1500,
  });
};

// 顯示服務條款
const showTerms = () => {
  $q.dialog({
    title: '服務條款',
    message: '這裡是服務條款內容...',
    html: true,
  });
};

// 顯示隱私政策
const showPrivacy = () => {
  $q.dialog({
    title: '隱私政策',
    message: '這裡是隱私政策內容...',
    html: true,
  });
};

// 提交訂單
const submitOrder = async () => {
  if (!canSubmit.value) {
    return;
  }

  submitting.value = true;

  try {
    // 準備訂單資料
    const orderData = {
      items: cartItems.value,
      recipient: recipientInfo.value,
      payment: paymentMethod.value,
      coupon: appliedCoupon.value,
      subtotal: subtotal.value,
      shippingFee: shippingFee.value,
      totalAmount: totalAmount.value,
    };

    console.log('訂單資料:', orderData);

    // 模擬 API 呼叫
    await new Promise(resolve => setTimeout(resolve, 2000));

    // 清空購物車
    clearCart();

    // 顯示成功訊息
    $q.notify({
      type: 'positive',
      message: '訂單已成功送出！',
      position: 'top',
      timeout: 2000,
    });

    // 導向訂單完成頁面或訂單列表
    setTimeout(() => {
      $q.dialog({
        title: '訂單提交成功',
        message: '感謝您的訂購！我們將盡快處理您的訂單。',
        persistent: true,
        ok: {
          label: '查看訂單',
          color: 'primary',
        },
      }).onOk(() => {
        // 導向訂單頁面
        router.push('/shop');
      });
    }, 500);
  } catch (error) {
    console.error('提交訂單失敗:', error);
    $q.notify({
      type: 'negative',
      message: '訂單提交失敗，請稍後再試',
      position: 'top',
      timeout: 2000,
    });
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  loadCartData();
});
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.checkout-page {
  min-height: 100vh;
  background: $shop-bg-light;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: $shop-white;
  border-bottom: 1px solid $shop-border;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: $shop-shadow-sm;

  .back-btn {
    flex-shrink: 0;
  }

  .page-title {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: $shop-text;
  }

  .placeholder {
    width: 40px;
    flex-shrink: 0;
  }
}

.checkout-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px;
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.checkout-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.checkout-right {
  @media (min-width: 1025px) {
    position: sticky;
    top: 90px;
    align-self: flex-start;
  }
}

.section {
  background: $shop-white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: $shop-shadow-sm;
  border: 1px solid $shop-border;

  .section-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 2px solid $shop-bg-light;

    .section-title {
      margin: 0;
      font-size: 1.1rem;
      font-weight: 600;
      color: $shop-text;
      flex: 1;
    }
  }
}

// 訂單商品列表
.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: $shop-bg-light;
  border-radius: 8px;
  border: 1px solid $shop-border;
  transition: $shop-transition;
  position: relative;

  &:hover {
    border-color: $shop-primary;
  }

  .item-remove-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    opacity: 0.5;
    transition: opacity 0.2s;
  }

  &:hover .item-remove-btn {
    opacity: 1;
  }

  .item-image {
    width: 80px;
    height: 80px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    background: $shop-white;
  }

  .item-details {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;

    .item-name {
      font-size: 1rem;
      font-weight: 500;
      color: $shop-text;
      line-height: 1.4;
    }

    .item-spec {
      margin: 2px 0;
    }

    .item-meta {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-top: auto;

      .item-price {
        font-size: 0.95rem;
        font-weight: 600;
        color: $shop-danger;
      }

      .item-quantity {
        font-size: 0.9rem;
        color: $shop-text-secondary;
      }
    }
  }

  .item-subtotal {
    display: flex;
    align-items: center;
    font-size: 1.1rem;
    font-weight: 600;
    color: $shop-text;
  }
}

.empty-cart-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;

  .empty-text {
    font-size: 1.1rem;
    color: $shop-text-secondary;
    margin: 20px 0 30px;
  }
}

// 收件人資訊表單
.form-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  .form-input {
    width: 100%;
  }
}

// 付款方式
.payment-options {
  .payment-group {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 16px;

    :deep(.q-radio) {
      background: $shop-bg-light;
      padding: 12px 20px;
      border-radius: 8px;
      border: 2px solid $shop-border;
      transition: $shop-transition;
      cursor: pointer;

      &:hover {
        border-color: $shop-primary;
      }
    }

    :deep(.q-radio__inner) {
      color: $shop-primary;
    }
  }

  .payment-option-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
    color: $shop-text;
  }

  .payment-note {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: lighten($shop-primary, 45%);
    border-radius: 8px;
    font-size: 0.9rem;
    color: $shop-text-secondary;
    border-left: 3px solid $shop-primary;
  }
}

// 優惠券
.coupon-input-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;

  .coupon-input {
    flex: 1;
  }

  .apply-coupon-btn {
    min-width: 80px;
  }
}

.applied-coupon {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: lighten($shop-success, 50%);
  border-radius: 8px;
  border: 1px solid $shop-success;

  .coupon-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .coupon-name {
      font-weight: 500;
      color: $shop-text;
    }

    .coupon-discount {
      font-weight: 600;
      color: $shop-success;
    }
  }
}

// 訂單摘要
.order-summary-section {
  .summary-content {
    margin-bottom: 24px;
  }

  .summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    font-size: 0.95rem;

    .label {
      color: $shop-text-secondary;
      font-weight: 500;
    }

    .value {
      color: $shop-text;
      font-weight: 500;
    }

    .free-shipping {
      color: $shop-success;
      font-weight: 600;
    }

    &.discount {
      color: $shop-success;

      .value {
        color: $shop-success;
        font-weight: 600;
      }
    }

    &.total {
      padding: 16px 0;
      margin-top: 8px;
      border-top: 2px solid $shop-border;
      font-size: 1.2rem;

      .label {
        color: $shop-text;
        font-weight: 600;
      }

      .value {
        color: $shop-danger;
        font-weight: 700;
      }
    }
  }

  .summary-divider {
    margin: 8px 0;
  }

  .free-shipping-tip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: lighten($shop-warning, 35%);
    border-radius: 8px;
    font-size: 0.85rem;
    color: darken($shop-warning, 20%);
    border-left: 3px solid $shop-warning;
    margin-top: 12px;
  }
}

.submit-actions {
  .submit-btn {
    height: 50px;
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 12px;
  }

  .terms-agreement {
    text-align: center;

    .terms-text {
      font-size: 0.85rem;
      color: $shop-text-secondary;

      .terms-link {
        color: $shop-primary;
        text-decoration: none;
        font-weight: 500;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}

.security-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: lighten($shop-success, 50%);
  border-radius: 8px;
  font-size: 0.9rem;
  color: $shop-success;
  font-weight: 500;
}

// 響應式設計
@media (max-width: 768px) {
  .checkout-content {
    padding: 16px 12px;
  }

  .section {
    padding: 16px;
  }

  .order-item {
    flex-direction: column;

    .item-subtotal {
      justify-content: flex-end;
      padding-top: 8px;
      border-top: 1px solid $shop-border;
    }
  }

  .payment-options {
    .payment-group {
      flex-direction: column;

      :deep(.q-radio) {
        width: 100%;
      }
    }
  }
}
</style>
