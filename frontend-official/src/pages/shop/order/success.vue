<template>
  <q-page class="order-success-page">
    <div class="success-container">
      <div class="success-card">
        <q-icon :name="headline.icon" size="72px" :color="headline.color" />
        <h1 class="success-title">{{ headline.title }}</h1>
        <p class="success-subtitle">{{ headline.subtitle }}</p>

        <div v-if="checkingPayment" class="payment-checking">
          <q-spinner size="18px" color="primary" />
          <span>正在確認付款結果...</span>
        </div>

        <q-btn
          v-if="canPayOnline && !checkingPayment"
          unelevated
          color="primary"
          icon="credit_card"
          label="前往付款"
          :loading="paying"
          class="pay-btn"
          @click="payNow"
        />

        <div v-if="orderNumber" class="order-info">
          <div class="info-row">
            <span class="label">訂單編號</span>
            <span class="value order-number">
              {{ orderNumber }}
              <q-btn
                flat
                round
                dense
                size="sm"
                icon="content_copy"
                color="grey-7"
                @click="copyOrderNumber"
              >
                <q-tooltip>複製訂單編號</q-tooltip>
              </q-btn>
            </span>
          </div>
          <div v-if="lastOrder?.totalAmount != null" class="info-row">
            <span class="label">訂單金額</span>
            <span class="value amount">NT$ {{ Number(lastOrder.totalAmount).toLocaleString() }}</span>
          </div>
          <div v-if="lastOrder?.paymentMethod" class="info-row">
            <span class="label">付款方式</span>
            <span class="value">{{ paymentLabel }}</span>
          </div>
          <div v-if="lastOrder?.email" class="info-row">
            <span class="label">通知信箱</span>
            <span class="value">{{ lastOrder.email }}</span>
          </div>
        </div>

        <p class="lookup-hint">
          <q-icon name="info" size="16px" />
          請保留訂單編號，日後可使用「訂單編號 + 電子郵件」查詢訂單狀態。
        </p>

        <div class="actions">
          <q-btn
            unelevated
            color="primary"
            icon="receipt_long"
            label="查詢訂單"
            :to="lookupLink"
            class="action-btn"
          />
          <q-btn
            outline
            color="primary"
            icon="storefront"
            label="繼續購物"
            to="/shop/product/list?category=all"
            class="action-btn"
          />
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useQuasar, copyToClipboard } from 'quasar';
import { getLastOrder, redirectToPayment } from 'src/utils/checkout.js';
import { lookupOrder, payOrder } from 'src/api/order.js';

const route = useRoute();
const $q = useQuasar();

const storedOrder = getLastOrder();

const orderNumber = computed(() => route.query.orderNumber || storedOrder?.orderNumber || '');

// 只顯示與網址訂單編號相符的暫存摘要
const lastOrder = computed(() =>
  storedOrder && storedOrder.orderNumber === orderNumber.value ? storedOrder : null
);

// 後端即時狀態（綠界付款結果以伺服器通知為準）
const liveStatus = ref(null);
const canPayOnline = ref(false);
const checkingPayment = ref(false);
const paying = ref(false);
let pollTimer = null;

const isOnlinePayment = computed(() => lastOrder.value?.paymentMethod === 'ECPAY');

const headline = computed(() => {
  if (liveStatus.value === 'PAID' || liveStatus.value === 'PROCESSING' || liveStatus.value === 'COMPLETED') {
    return {
      icon: 'check_circle',
      color: 'positive',
      title: '付款完成，感謝您的訂購！',
      subtitle: '我們已收到您的付款，將盡快為您安排出貨。',
    };
  }
  if (liveStatus.value === 'CANCELLED') {
    return {
      icon: 'cancel',
      color: 'grey',
      title: '訂單已取消',
      subtitle: '此訂單已取消，如有疑問請聯繫客服。',
    };
  }
  if (isOnlinePayment.value) {
    return {
      icon: 'schedule',
      color: 'warning',
      title: '訂單已建立，尚未完成付款',
      subtitle: lastOrder.value?.paymentError
        ? `${lastOrder.value.paymentError}。您可以稍後再試一次付款。`
        : '完成付款後我們將立即為您安排出貨。若已付款，付款結果可能需要幾分鐘才會更新。',
    };
  }
  return {
    icon: 'check_circle',
    color: 'positive',
    title: '感謝您的訂購！',
    subtitle: '我們已收到您的訂單，將盡快為您備貨出貨。',
  };
});

const paymentLabel = computed(() => {
  if (lastOrder.value?.paymentMethod === 'COD') {
    return lastOrder.value.shippingMethod === 'STORE_PICKUP' ? '取貨時付款' : '貨到付款';
  }
  return '線上付款（綠界）';
});

const lookupLink = computed(() => ({
  path: '/shop/order/lookup',
  query: {
    ...(orderNumber.value ? { orderNumber: orderNumber.value } : {}),
    ...(lastOrder.value?.email ? { email: lastOrder.value.email } : {}),
  },
}));

const refreshStatus = async () => {
  if (!orderNumber.value || !lastOrder.value?.email) return;
  try {
    const res = await lookupOrder(orderNumber.value, lastOrder.value.email);
    liveStatus.value = res.data.order?.status || null;
    canPayOnline.value = !!res.data.canPayOnline;
  } catch (error) {
    // 查詢失敗時維持暫存摘要顯示
  }
};

// 從綠界返回時付款通知可能稍晚到達，短暫輪詢幾次
const pollPaymentStatus = async (remaining = 5) => {
  checkingPayment.value = true;
  await refreshStatus();
  if (remaining > 1 && liveStatus.value === 'PENDING_PAYMENT') {
    pollTimer = setTimeout(() => pollPaymentStatus(remaining - 1), 3000);
    return;
  }
  checkingPayment.value = false;
};

const payNow = async () => {
  paying.value = true;
  try {
    const res = await payOrder(orderNumber.value, lastOrder.value.email);
    if (res.data.paymentUrl) {
      redirectToPayment(res.data.paymentUrl);
      return;
    }
    $q.notify({
      type: 'warning',
      message: res.data.paymentError || '暫時無法建立付款，請稍後再試或聯繫客服',
      position: 'top',
    });
  } catch (error) {
    // 錯誤訊息已由 request 攔截器顯示
  } finally {
    paying.value = false;
  }
};

const copyOrderNumber = async () => {
  try {
    await copyToClipboard(orderNumber.value);
    $q.notify({ type: 'positive', message: '已複製訂單編號', position: 'top', timeout: 1500 });
  } catch (error) {
    $q.notify({ type: 'warning', message: '複製失敗，請手動記下訂單編號', position: 'top' });
  }
};

onMounted(() => {
  if (isOnlinePayment.value) {
    pollPaymentStatus();
  } else {
    refreshStatus();
  }
});

onBeforeUnmount(() => {
  clearTimeout(pollTimer);
});
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.order-success-page {
  background: $shop-bg-light;
  min-height: 100vh;
}

.success-container {
  max-width: 640px;
  margin: 0 auto;
  padding: 48px 16px;
}

.success-card {
  background: $shop-white;
  border-radius: 12px;
  border: 1px solid $shop-border;
  box-shadow: $shop-shadow-sm;
  padding: 40px 32px;
  text-align: center;
}

.success-title {
  margin: 16px 0 8px;
  font-size: 1.5rem;
  font-weight: 600;
  line-height: 1.4;
  color: $shop-text;
}

.success-subtitle {
  margin: 0 0 24px;
  color: $shop-text-secondary;
  line-height: 1.6;
}

.payment-checking {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: $shop-text-secondary;
  margin-bottom: 20px;
}

.pay-btn {
  margin-bottom: 24px;
  min-width: 200px;
}

.order-info {
  text-align: left;
  background: $shop-bg-light;
  border-radius: 8px;
  padding: 8px 20px;
  margin-bottom: 20px;

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 12px 0;
    border-bottom: 1px solid $shop-border;

    &:last-child {
      border-bottom: none;
    }
  }

  .label {
    color: $shop-text-secondary;
    flex-shrink: 0;
  }

  .value {
    color: $shop-text;
    font-weight: 500;
    text-align: right;
    word-break: break-all;
  }

  .order-number {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-family: monospace;
    font-size: 1rem;
  }

  .amount {
    color: $shop-danger;
    font-weight: 700;
  }
}

.lookup-hint {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 6px;
  font-size: 0.85rem;
  color: $shop-text-secondary;
  margin: 0 0 24px;
  text-align: left;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;

  .action-btn {
    min-width: 160px;
  }
}

@media (max-width: $breakpoint-sm) {
  .success-card {
    padding: 32px 20px;
  }

  .actions .action-btn {
    width: 100%;
  }
}
</style>
