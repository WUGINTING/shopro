<template>
  <q-page class="order-lookup-page">
    <div class="lookup-container">
      <h1 class="page-title">訂單查詢</h1>
      <p class="page-subtitle">輸入訂單編號與下單時填寫的電子郵件，即可查詢訂單狀態與明細。</p>

      <q-form class="lookup-form" @submit="search">
        <q-input
          v-model="form.orderNumber"
          outlined
          dense
          label="訂單編號 *"
          :rules="[val => !!(val && val.trim()) || '請輸入訂單編號']"
          class="lookup-input"
        >
          <template v-slot:prepend>
            <q-icon name="receipt_long" />
          </template>
        </q-input>
        <q-input
          v-model="form.email"
          outlined
          dense
          type="email"
          label="電子郵件 *"
          :rules="[
            val => !!(val && val.trim()) || '請輸入電子郵件',
            val => /.+@.+\..+/.test(val) || '請輸入正確的電子郵件格式',
          ]"
          class="lookup-input"
        >
          <template v-slot:prepend>
            <q-icon name="email" />
          </template>
        </q-input>
        <q-btn
          type="submit"
          unelevated
          color="primary"
          icon="search"
          label="查詢"
          :loading="loading"
          class="lookup-btn"
        />
      </q-form>

      <div v-if="errorMessage" class="lookup-error">
        <q-icon name="error_outline" size="20px" />
        <span>{{ errorMessage }}</span>
      </div>

      <div v-if="order" class="order-card">
        <div class="order-header">
          <div>
            <div class="order-number">{{ order.orderNumber }}</div>
            <div class="order-date">下單時間：{{ formatDate(order.createdAt, 'YYYY-MM-DD HH:mm') }}</div>
          </div>
          <q-badge
            :color="statusInfo.color"
            :label="statusInfo.label"
            class="status-badge"
          />
        </div>

        <div v-if="canPayOnline" class="pay-banner">
          <div class="pay-text">
            <q-icon name="schedule" size="20px" />
            <span>此訂單尚未完成付款，完成付款後我們將立即為您安排出貨。</span>
          </div>
          <q-btn
            unelevated
            color="primary"
            icon="credit_card"
            label="前往付款"
            :loading="paying"
            @click="payNow"
          />
        </div>

        <div v-if="shipments.length > 0" class="order-section">
          <h2 class="section-title">物流資訊</h2>
          <div v-for="(shipment, index) in shipments" :key="index" class="shipment-row">
            <div class="shipment-main">
              <q-badge :color="shipment.status === 'DELIVERED' ? 'positive' : 'info'" :label="shipment.statusLabel" />
              <span class="shipment-company">{{ shipment.shippingCompany || '物流' }}</span>
            </div>
            <div v-if="shipment.trackingNumber" class="shipment-tracking">
              物流單號：<strong>{{ shipment.trackingNumber }}</strong>
              <q-btn flat dense round size="sm" icon="content_copy" aria-label="複製物流單號" @click="copyText(shipment.trackingNumber)" />
            </div>
            <div v-if="shipment.shippedAt" class="shipment-time">出貨時間：{{ formatDate(shipment.shippedAt, 'YYYY-MM-DD HH:mm') }}</div>
            <div v-if="shipment.deliveredAt" class="shipment-time">送達時間：{{ formatDate(shipment.deliveredAt, 'YYYY-MM-DD HH:mm') }}</div>
          </div>
        </div>

        <div class="order-section">
          <h2 class="section-title">訂購商品</h2>
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-name">
              {{ item.productName }}
              <span v-if="item.productSpec" class="item-spec">（{{ item.productSpec }}）</span>
            </div>
            <div class="item-qty">NT$ {{ money(item.unitPrice) }} x {{ item.quantity }}</div>
            <div class="item-amount">NT$ {{ money(item.subtotalAmount) }}</div>
          </div>
        </div>

        <div class="order-section amounts">
          <div class="amount-row">
            <span>商品小計</span>
            <span>NT$ {{ money(order.subtotalAmount) }}</span>
          </div>
          <div v-if="Number(order.discountAmount) > 0" class="amount-row">
            <span>折扣</span>
            <span>-NT$ {{ money(order.discountAmount) }}</span>
          </div>
          <div class="amount-row">
            <span>運費</span>
            <span>NT$ {{ money(order.shippingFee) }}</span>
          </div>
          <div class="amount-row total">
            <span>訂單總額</span>
            <span>NT$ {{ money(order.totalAmount) }}</span>
          </div>
        </div>

        <div class="order-section">
          <h2 class="section-title">收件資訊</h2>
          <div class="info-row"><span>收件人</span><span>{{ order.customerName }}</span></div>
          <div class="info-row"><span>聯絡電話</span><span>{{ order.customerPhone }}</span></div>
          <div class="info-row">
            <span>配送方式</span><span>{{ pickupLabel }}</span>
          </div>
          <div v-if="paymentLabel" class="info-row">
            <span>付款方式</span><span>{{ paymentLabel }}</span>
          </div>
          <div v-if="order.shippingAddress" class="info-row">
            <span>收件地址</span><span>{{ order.shippingAddress }}</span>
          </div>
        </div>

        <div v-if="canCancel" class="cancel-row">
          <q-btn flat color="negative" icon="cancel" label="取消此訂單" :loading="cancelling" @click="confirmCancel" />
          <div class="cancel-hint">尚未付款、尚未出貨的訂單可以自行取消。</div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useQuasar } from 'quasar';
import { lookupOrder, payOrder, cancelOrder } from 'src/api/order.js';
import { formatDate } from 'src/utils/format.js';
import { ORDER_STATUS_MAP, PICKUP_TYPE_MAP, redirectToPayment } from 'src/utils/checkout.js';

const $q = useQuasar();

const route = useRoute();

const form = ref({
  orderNumber: route.query.orderNumber || '',
  email: route.query.email || '',
});

const loading = ref(false);
const errorMessage = ref('');
const order = ref(null);
const paymentMethod = ref(null);
const canPayOnline = ref(false);
const paying = ref(false);
const canCancel = ref(false);
const cancelling = ref(false);
const shipments = ref([]);

const applyLookup = data => {
  order.value = data.order;
  paymentMethod.value = data.paymentMethod;
  canPayOnline.value = !!data.canPayOnline;
  canCancel.value = !!data.canCancel;
  shipments.value = data.shipments || [];
};

const copyText = async text => {
  try {
    await navigator.clipboard.writeText(text);
    $q.notify({ type: 'positive', message: '已複製物流單號', position: 'top', timeout: 1200 });
  } catch (error) {
    // 無法存取剪貼簿時忽略
  }
};

const confirmCancel = () => {
  $q.dialog({
    title: '取消訂單',
    message: '確定要取消這筆訂單嗎？取消後無法恢復，如需購買請重新下單。',
    cancel: { label: '先不要', flat: true },
    ok: { label: '確定取消', color: 'negative', unelevated: true },
    persistent: true,
  }).onOk(async () => {
    cancelling.value = true;
    try {
      const res = await cancelOrder(order.value.orderNumber, form.value.email.trim());
      applyLookup(res.data);
      $q.notify({ type: 'positive', message: '訂單已取消', position: 'top' });
    } catch (error) {
      // 錯誤訊息已由 request 攔截器顯示
    } finally {
      cancelling.value = false;
    }
  });
};

const statusInfo = computed(
  () => ORDER_STATUS_MAP[order.value?.status] || { label: order.value?.status || '', color: 'grey' }
);

const pickupLabel = computed(() => PICKUP_TYPE_MAP[order.value?.pickupType] || '宅配到府');

const paymentLabel = computed(() => {
  if (paymentMethod.value === 'ECPAY') return '線上付款（綠界）';
  if (paymentMethod.value === 'COD') {
    return order.value?.pickupType === 'STORE_PICKUP' ? '取貨時付款' : '貨到付款';
  }
  return '';
});

const money = value => Number(value || 0).toLocaleString();

const search = async () => {
  loading.value = true;
  errorMessage.value = '';
  order.value = null;
  canPayOnline.value = false;
  canCancel.value = false;
  shipments.value = [];
  try {
    const res = await lookupOrder(form.value.orderNumber.trim(), form.value.email.trim());
    applyLookup(res.data);
  } catch (error) {
    errorMessage.value = error.displayMessage || '查詢失敗，請稍後再試';
  } finally {
    loading.value = false;
  }
};

const payNow = async () => {
  paying.value = true;
  try {
    const res = await payOrder(order.value.orderNumber, form.value.email.trim());
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

onMounted(() => {
  if (form.value.orderNumber && form.value.email) {
    search();
  }
});
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.shipment-row {
  padding: 10px 0;
  border-bottom: 1px dashed #eee;

  &:last-child {
    border-bottom: none;
  }

  .shipment-main {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .shipment-tracking,
  .shipment-time {
    margin-top: 4px;
    font-size: 14px;
    color: #555;
  }
}

.cancel-row {
  margin-top: 16px;
  text-align: center;

  .cancel-hint {
    font-size: 12px;
    color: #999;
  }
}

.order-lookup-page {
  background: $shop-bg-light;
  min-height: 100vh;
}

.lookup-container {
  max-width: 760px;
  margin: 0 auto;
  padding: 40px 16px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 1.6rem;
  font-weight: 600;
  line-height: 1.3;
  color: $shop-text;
}

.page-subtitle {
  margin: 0 0 24px;
  color: $shop-text-secondary;
}

.lookup-form {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 12px;
  align-items: start;
  background: $shop-white;
  border: 1px solid $shop-border;
  border-radius: 12px;
  padding: 20px 20px 4px;
  box-shadow: $shop-shadow-sm;

  .lookup-btn {
    height: 40px;
    min-width: 100px;
  }
}

.lookup-error {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  background: lighten($shop-danger, 40%);
  color: darken($shop-danger, 10%);
  border-left: 3px solid $shop-danger;
}

.order-card {
  margin-top: 24px;
  background: $shop-white;
  border: 1px solid $shop-border;
  border-radius: 12px;
  box-shadow: $shop-shadow-sm;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid $shop-border;

  .order-number {
    font-family: monospace;
    font-size: 1.1rem;
    font-weight: 600;
    color: $shop-text;
  }

  .order-date {
    font-size: 0.85rem;
    color: $shop-text-secondary;
    margin-top: 4px;
  }

  .status-badge {
    font-size: 0.85rem;
    padding: 6px 12px;
  }
}

.pay-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding: 16px 24px;
  background: lighten($shop-warning, 35%);
  border-bottom: 1px solid $shop-border;

  .pay-text {
    display: flex;
    align-items: center;
    gap: 8px;
    color: darken($shop-warning, 25%);
  }
}

.order-section {
  padding: 20px 24px;
  border-bottom: 1px solid $shop-border;

  &:last-child {
    border-bottom: none;
  }

  .section-title {
    margin: 0 0 12px;
    font-size: 1rem;
    font-weight: 600;
    line-height: 1.4;
    color: $shop-text;
  }
}

.order-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 16px;
  padding: 10px 0;
  border-bottom: 1px dashed $shop-border;

  &:last-child {
    border-bottom: none;
  }

  .item-name {
    color: $shop-text;
  }

  .item-spec {
    color: $shop-text-secondary;
    font-size: 0.9rem;
  }

  .item-qty {
    color: $shop-text-secondary;
    white-space: nowrap;
  }

  .item-amount {
    font-weight: 600;
    color: $shop-text;
    white-space: nowrap;
    text-align: right;
    min-width: 90px;
  }
}

.amounts {
  background: $shop-bg-light;

  .amount-row {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    color: $shop-text-secondary;

    &.total {
      margin-top: 6px;
      padding-top: 12px;
      border-top: 1px solid $shop-border;
      font-size: 1.1rem;
      font-weight: 700;
      color: $shop-text;

      span:last-child {
        color: $shop-danger;
      }
    }
  }
}

.info-row {
  display: flex;
  gap: 16px;
  padding: 6px 0;

  span:first-child {
    width: 80px;
    flex-shrink: 0;
    color: $shop-text-secondary;
  }

  span:last-child {
    color: $shop-text;
    word-break: break-all;
  }
}

@media (max-width: $breakpoint-sm) {
  .lookup-form {
    grid-template-columns: 1fr;

    .lookup-btn {
      width: 100%;
      margin-bottom: 16px;
    }
  }

  .order-item {
    grid-template-columns: 1fr auto;

    .item-qty {
      grid-column: 1;
      grid-row: 2;
    }

    .item-amount {
      grid-row: 1 / span 2;
      align-self: center;
    }
  }
}
</style>
