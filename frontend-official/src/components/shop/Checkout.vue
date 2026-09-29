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
                      NT$ {{ unitPrice(item).toLocaleString() }}
                    </span>
                    <span class="item-quantity">x {{ item.quantity }}</span>
                  </div>
                </div>

                <!-- 小計 -->
                <div class="item-subtotal">
                  NT$ {{ (unitPrice(item) * item.quantity).toLocaleString() }}
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

            <q-form ref="recipientFormRef" class="form-content" @submit.prevent>
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
                  v-if="shippingMethod === 'HOME_DELIVERY'"
                  v-model="recipientInfo.address"
                  outlined
                  label="收件地址 *"
                  dense
                  :rules="[val => !!(val && val.trim()) || '請輸入收件地址']"
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
            </q-form>
          </div>

          <!-- 配送方式 -->
          <div class="section shipping-section">
            <div class="section-header">
              <q-icon name="local_shipping" size="24px" color="primary" />
              <h5 class="section-title">配送方式</h5>
            </div>

            <div class="payment-options">
              <q-option-group
                v-model="shippingMethod"
                :options="shippingOptions"
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

              <div class="payment-note">
                <q-icon name="info" size="18px" color="grey-6" />
                <span v-if="shippingMethod === 'HOME_DELIVERY'">
                  商品將寄送至您填寫的收件地址
                </span>
                <span v-else>
                  訂單備妥後將以電話或 Email 通知您至門市取貨
                </span>
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
                <span v-if="paymentMethod === 'ECPAY'">
                  送出訂單後將導向綠界金流頁面，可選擇信用卡、ATM 轉帳或超商代碼繳費
                </span>
                <span v-else-if="shippingMethod === 'STORE_PICKUP'">
                  請於門市取貨時付款
                </span>
                <span v-else>
                  商品送達時請準備現金付款給配送人員
                </span>
              </div>
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
                    <span v-else-if="shippingMethod === 'STORE_PICKUP'" class="free-shipping">門市自取免運</span>
                    <span v-else class="free-shipping">免運費</span>
                  </span>
                </div>

                <div v-for="discount in amountDiscounts" :key="discount.type + discount.name" class="summary-row discount">
                  <span class="label">
                    {{ discountLabel(discount) }}
                  </span>
                  <span class="value">-NT$ {{ Number(discount.amount).toLocaleString() }}</span>
                </div>
                <div v-if="freeShippingDiscount && shippingMethod === 'HOME_DELIVERY'" class="summary-row discount">
                  <span class="label">{{ discountLabel(freeShippingDiscount) }}</span>
                  <span class="value">免運</span>
                </div>

                <!-- 優惠券 -->
                <div class="coupon-box">
                  <div class="coupon-input-row">
                    <q-input
                      v-model="couponInput"
                      dense
                      outlined
                      placeholder="輸入優惠券代碼"
                      class="coupon-input"
                      maxlength="50"
                      :disable="quoting"
                      @keyup.enter="applyCoupon"
                    />
                    <q-btn
                      v-if="!requestedCoupon"
                      unelevated
                      color="primary"
                      label="套用"
                      :disable="!couponInput.trim() || quoting"
                      @click="applyCoupon"
                    />
                    <q-btn v-else flat color="grey-8" label="移除" @click="removeCoupon" />
                  </div>
                  <div v-if="requestedCoupon && quote?.couponCode" class="coupon-ok">
                    <q-icon name="check_circle" size="16px" /> 已套用優惠券 {{ quote.couponCode }}
                  </div>
                  <div v-else-if="requestedCoupon && quote?.couponMessage" class="coupon-warn">
                    <q-icon name="info" size="16px" /> {{ quote.couponMessage }}
                  </div>
                  <div class="coupon-hint">促銷活動、優惠券與會員折扣會自動套用折抵最多的一項；免運可與折扣併用。</div>
                </div>

                <q-separator class="summary-divider" />

                <!-- 總計 -->
                <div class="summary-row total">
                  <span class="label">訂單總額</span>
                  <span class="value">NT$ {{ totalAmount.toLocaleString() }}</span>
                </div>

                <!-- 免運費提示 -->
                <div v-if="freeShippingThreshold && subtotal < freeShippingThreshold && shippingFee > 0" class="free-shipping-tip">
                  <q-icon name="local_shipping" size="18px" />
                  <span>
                    再消費 NT$ {{ (freeShippingThreshold - subtotal).toLocaleString() }} 即可免運費
                  </span>
                </div>

                <!-- 試算中 / 試算錯誤（如庫存不足） -->
                <div v-if="quoting" class="quote-status">
                  <q-spinner size="18px" color="primary" />
                  <span>正在確認最新價格與庫存...</span>
                </div>
                <div v-else-if="quoteError" class="quote-error">
                  <q-icon name="error_outline" size="18px" />
                  <span>{{ quoteError }}</span>
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
                  <q-checkbox v-model="marketingOptIn" dense color="primary" class="terms-checkbox q-mt-xs">
                    <span class="terms-text">我願意收到優惠與新品通知 Email（可隨時取消訂閱）</span>
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
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { getCartItems, clearCart, removeFromCart } from 'src/utils/cart.js';
import { quoteOrder, checkoutOrder, toOrderItems, getShippingOptions, getPaymentOptions } from 'src/api/order.js';
import {
  getCheckoutDraft,
  saveCheckoutDraft,
  saveLastOrder,
  redirectToPayment,
} from 'src/utils/checkout.js';

const router = useRouter();
const $q = useQuasar();

const recipientFormRef = ref(null);

// 購物車商品
const cartItems = ref([]);

// 收件人資訊（自動帶入上次填寫的資料）
const recipientInfo = ref({
  name: '',
  phone: '',
  email: '',
  address: '',
  note: '',
});

// 配送方式
const shippingMethod = ref('HOME_DELIVERY');
const ALL_SHIPPING_OPTIONS = [
  { label: '宅配到府', value: 'HOME_DELIVERY', icon: 'local_shipping' },
  { label: '門市自取', value: 'STORE_PICKUP', icon: 'storefront' },
];
const shippingOptions = ref(ALL_SHIPPING_OPTIONS);

// 只顯示後台目前開放的配送方式；目前選擇的方式暫停服務時改用第一個可用的方式
const loadShippingOptions = async () => {
  try {
    const res = await getShippingOptions();
    const available = (res?.data || []).map(option => option.method);
    if (!available.length) return;
    shippingOptions.value = ALL_SHIPPING_OPTIONS.filter(option => available.includes(option.value));
    if (!available.includes(shippingMethod.value)) {
      shippingMethod.value = available[0];
    }
  } catch {
    // 取不到時保留全部選項，由後端試算告知是否暫停服務
  }
};

// 付款方式
const paymentMethod = ref('ECPAY');
// 線上付款依後台金流設定（停用 / 維護中時不可選）
const onlinePaymentUnavailable = ref('');
const loadPaymentOptions = async () => {
  try {
    const res = await getPaymentOptions();
    const online = (res?.data || []).find(option => option.method === 'ECPAY');
    onlinePaymentUnavailable.value = online && !online.available ? online.message || '線上付款暫停服務' : '';
    if (onlinePaymentUnavailable.value && paymentMethod.value === 'ECPAY') {
      paymentMethod.value = 'COD';
    }
  } catch {
    // 取不到時保留選項，由後端下單時檢查
  }
};
const paymentOptions = computed(() => [
  {
    label: onlinePaymentUnavailable.value
      ? `線上付款（${onlinePaymentUnavailable.value}）`
      : '線上付款（信用卡 / ATM / 超商）',
    value: 'ECPAY',
    icon: 'credit_card',
    disable: !!onlinePaymentUnavailable.value,
  },
  {
    label: shippingMethod.value === 'STORE_PICKUP' ? '取貨時付款' : '貨到付款',
    value: 'COD',
    icon: 'payments',
  },
]);

// 條款同意
const agreeTerms = ref(false);

// 提交狀態
const submitting = ref(false);

// 後端試算結果（價格、運費一律以後端為準）
const quote = ref(null);
const quoting = ref(false);
const quoteError = ref('');
let quoteSeq = 0;

const itemKey = (productId, specId) => `${productId}-${specId || 'default'}`;

const quotedLines = computed(() => {
  const map = new Map();
  (quote.value?.lines || []).forEach(line => {
    map.set(itemKey(line.productId, line.specificationId), line);
  });
  return map;
});

// 單價：有後端試算時使用後端價格，否則暫以購物車價格顯示
const unitPrice = item => {
  const line = quotedLines.value.get(itemKey(item.id, item.specification?.id));
  return Number(line?.unitPrice ?? item.selectedPrice ?? item.price ?? 0);
};

// 計算商品小計
const subtotal = computed(() => {
  if (quote.value) return Number(quote.value.subtotalAmount);
  return cartItems.value.reduce((total, item) => total + unitPrice(item) * item.quantity, 0);
});

// 運費
const shippingFee = computed(() => Number(quote.value?.shippingFee ?? 0));

// 優惠券：requestedCoupon 為顧客送出試算的代碼；實際是否套用以試算結果 quote.couponCode 為準
const couponInput = ref('');
// 行銷 Email 同意（預設不勾選）
const marketingOptIn = ref(false);
const requestedCoupon = ref('');
const amountDiscounts = computed(() =>
  (quote.value?.discounts || []).filter(discount => Number(discount.amount) > 0)
);
const freeShippingDiscount = computed(() =>
  (quote.value?.discounts || []).find(discount => discount.type === 'FREE_SHIPPING') || null
);
const DISCOUNT_TYPE_LABELS = {
  PROMOTION: '活動折扣',
  COUPON: '優惠券',
  MEMBER_LEVEL: '會員折扣',
  FREE_SHIPPING: '免運優惠',
};
const discountLabel = discount => {
  const base = DISCOUNT_TYPE_LABELS[discount.type] || '折扣';
  const detail = discount.code ? `${discount.name}（${discount.code}）` : discount.name;
  return detail ? `${base}：${detail}` : base;
};
const applyCoupon = () => {
  const code = couponInput.value.trim().toUpperCase();
  if (!code) return;
  couponInput.value = code;
  requestedCoupon.value = code;
  refreshQuote();
};
const removeCoupon = () => {
  couponInput.value = '';
  requestedCoupon.value = '';
  refreshQuote();
};

// 免運門檻
const freeShippingThreshold = computed(() =>
  quote.value?.freeShippingThreshold ? Number(quote.value.freeShippingThreshold) : null
);

// 總金額
const totalAmount = computed(() => {
  if (quote.value) return Number(quote.value.totalAmount);
  return subtotal.value + shippingFee.value;
});

// 是否可以提交
const canSubmit = computed(() => {
  return (
    cartItems.value.length > 0 &&
    !!quote.value &&
    !quoteError.value &&
    !quoting.value &&
    recipientInfo.value.name &&
    recipientInfo.value.phone &&
    recipientInfo.value.email &&
    (shippingMethod.value === 'STORE_PICKUP' || recipientInfo.value.address) &&
    agreeTerms.value &&
    !submitting.value
  );
});

// 向後端試算金額並檢查庫存
const refreshQuote = async () => {
  if (cartItems.value.length === 0) {
    quote.value = null;
    quoteError.value = '';
    return;
  }

  const seq = ++quoteSeq;
  quoting.value = true;
  try {
    const res = await quoteOrder({
      items: toOrderItems(cartItems.value),
      shippingMethod: shippingMethod.value,
      couponCode: requestedCoupon.value || null,
    });
    if (seq !== quoteSeq) return;
    quote.value = res.data;
    quoteError.value = '';
  } catch (error) {
    if (seq !== quoteSeq) return;
    quote.value = null;
    quoteError.value = error.displayMessage || '無法確認商品價格與庫存，請稍後再試';
  } finally {
    if (seq === quoteSeq) quoting.value = false;
  }
};

// 載入購物車資料
const loadCartData = () => {
  cartItems.value = getCartItems();
};

// 移除購物車商品
const removeCartItem = item => {
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
    refreshQuote();
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

// 顯示服務條款
const showTerms = () => {
  $q.dialog({
    title: '服務條款',
    message: [
      '1. 訂單成立後，我們將以您填寫的電子郵件與電話聯繫出貨及取貨事宜。',
      '2. 商品價格、運費以結帳頁面最終顯示金額為準。',
      '3. 如遇商品缺貨，我們將主動聯繫您並協助退款或更換商品。',
      '4. 商品到貨後享有七天鑑賞期（食品等依法不適用之商品除外），如需退換貨請聯繫客服。',
    ].join('<br>'),
    html: true,
  });
};

// 顯示隱私政策
const showPrivacy = () => {
  $q.dialog({
    title: '隱私政策',
    message: [
      '您於結帳時提供的姓名、電話、電子郵件與地址，僅用於訂單處理、配送及客服聯繫。',
      '線上付款由綠界科技處理，本站不會儲存您的信用卡資訊。',
      '除法令要求或配送所需外，我們不會將您的個人資料提供給第三方。',
    ].join('<br>'),
    html: true,
  });
};

// 提交訂單
const submitOrder = async () => {
  if (!canSubmit.value) {
    return;
  }

  const valid = await recipientFormRef.value?.validate();
  if (!valid) {
    return;
  }

  submitting.value = true;

  try {
    const res = await checkoutOrder({
      customerName: recipientInfo.value.name,
      customerPhone: recipientInfo.value.phone,
      customerEmail: recipientInfo.value.email,
      shippingAddress: shippingMethod.value === 'HOME_DELIVERY' ? recipientInfo.value.address : null,
      notes: recipientInfo.value.note,
      shippingMethod: shippingMethod.value,
      paymentMethod: paymentMethod.value,
      // 只送出試算確認會套用的優惠券
      couponCode: quote.value?.couponCode || null,
      marketingOptIn: marketingOptIn.value,
      items: toOrderItems(cartItems.value),
    });

    const result = res.data;
    const order = result.order;

    saveLastOrder({
      orderNumber: order.orderNumber,
      email: order.customerEmail,
      totalAmount: order.totalAmount,
      paymentMethod: result.paymentMethod,
      paymentError: result.paymentError || null,
      shippingMethod: shippingMethod.value,
      itemCount: order.items?.length || cartItems.value.length,
    });

    clearCart();

    if (result.paymentMethod === 'ECPAY' && result.paymentUrl) {
      $q.notify({
        type: 'positive',
        message: '訂單已建立，正在前往付款頁面...',
        position: 'top',
        timeout: 2000,
      });
      redirectToPayment(result.paymentUrl);
      return;
    }

    router.push({
      path: '/shop/order/success',
      query: { orderNumber: order.orderNumber },
    });
  } catch (error) {
    // 錯誤訊息已由 request 攔截器顯示；若為庫存或價格問題，重新試算讓畫面同步
    refreshQuote();
  } finally {
    submitting.value = false;
  }
};

// 自動保存填寫中的收件資料（不含備註）
watch(
  () => ({
    name: recipientInfo.value.name,
    phone: recipientInfo.value.phone,
    email: recipientInfo.value.email,
    address: recipientInfo.value.address,
    shippingMethod: shippingMethod.value,
  }),
  draft => saveCheckoutDraft(draft),
  { deep: true }
);

watch(shippingMethod, () => {
  refreshQuote();
});

// 購物車抽屜中修改數量或規格時，同步結帳明細並重新試算（送出訂單清空購物車時不處理）
const handleCartUpdated = () => {
  if (submitting.value) return;
  loadCartData();
  refreshQuote();
};

onBeforeUnmount(() => {
  window.removeEventListener('cart-updated', handleCartUpdated);
});

onMounted(() => {
  const draft = getCheckoutDraft();
  recipientInfo.value.name = draft.name || '';
  recipientInfo.value.phone = draft.phone || '';
  recipientInfo.value.email = draft.email || '';
  recipientInfo.value.address = draft.address || '';
  if (draft.shippingMethod === 'STORE_PICKUP') {
    shippingMethod.value = 'STORE_PICKUP';
  }

  loadCartData();
  refreshQuote();
  loadShippingOptions();
  loadPaymentOptions();
  window.addEventListener('cart-updated', handleCartUpdated);
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

  .summary-row.discount .value {
    color: #c62828;
  }

  .coupon-box {
    margin: 12px 0 4px;

    .coupon-input-row {
      display: flex;
      gap: 8px;
      align-items: center;
    }

    .coupon-input {
      flex: 1;
    }

    .coupon-ok,
    .coupon-warn {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 6px;
      font-size: 13px;
    }

    .coupon-ok {
      color: #2e7d32;
    }

    .coupon-warn {
      color: #b26a00;
    }

    .coupon-hint {
      margin-top: 6px;
      font-size: 12px;
      color: #888;
    }
  }

  .quote-status,
  .quote-error {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 0.85rem;
    margin-top: 12px;
  }

  .quote-status {
    background: $shop-bg-light;
    color: $shop-text-secondary;
  }

  .quote-error {
    background: lighten($shop-danger, 40%);
    color: darken($shop-danger, 10%);
    border-left: 3px solid $shop-danger;
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
