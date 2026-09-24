<template>
  <q-page class="order-success-page">
    <div class="success-container">
      <div class="success-card">
        <q-icon
          :name="hasPaymentIssue ? 'schedule' : 'check_circle'"
          size="72px"
          :color="hasPaymentIssue ? 'warning' : 'positive'"
        />
        <h1 class="success-title">
          {{ hasPaymentIssue ? '訂單已建立，尚未完成付款' : '感謝您的訂購！' }}
        </h1>
        <p class="success-subtitle">
          <template v-if="hasPaymentIssue">
            {{ lastOrder.paymentError }}。訂單已保留為「待付款」，請聯繫客服協助完成付款。
          </template>
          <template v-else-if="lastOrder?.paymentMethod === 'COD'">
            我們已收到您的訂單，將盡快為您備貨出貨。
          </template>
          <template v-else>
            我們已收到您的訂單，付款結果確認後將為您安排出貨。
          </template>
        </p>

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
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useQuasar, copyToClipboard } from 'quasar';
import { getLastOrder } from 'src/utils/checkout.js';

const route = useRoute();
const $q = useQuasar();

const storedOrder = getLastOrder();

const orderNumber = computed(() => route.query.orderNumber || storedOrder?.orderNumber || '');

// 只顯示與網址訂單編號相符的暫存摘要
const lastOrder = computed(() =>
  storedOrder && storedOrder.orderNumber === orderNumber.value ? storedOrder : null
);

const hasPaymentIssue = computed(
  () => lastOrder.value?.paymentMethod === 'ECPAY' && !!lastOrder.value?.paymentError
);

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

const copyOrderNumber = async () => {
  try {
    await copyToClipboard(orderNumber.value);
    $q.notify({ type: 'positive', message: '已複製訂單編號', position: 'top', timeout: 1500 });
  } catch (error) {
    $q.notify({ type: 'warning', message: '複製失敗，請手動記下訂單編號', position: 'top' });
  }
};
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
