<template>
  <q-page class="unsubscribe-page">
    <div class="unsubscribe-card">
      <template v-if="state === 'loading'">
        <q-spinner size="32px" color="primary" />
        <p>正在處理…</p>
      </template>
      <template v-else-if="state === 'done'">
        <q-icon name="mark_email_read" size="48px" color="positive" />
        <h1>已取消訂閱</h1>
        <p>之後不會再寄送優惠與新品通知給您。訂單相關通知（成立、付款、出貨）不受影響。</p>
        <q-btn unelevated color="primary" label="回到商店" to="/shop" />
      </template>
      <template v-else>
        <q-icon name="error_outline" size="48px" color="negative" />
        <h1>無法取消訂閱</h1>
        <p>{{ errorMessage }}</p>
        <q-btn flat color="primary" label="回到商店" to="/shop" />
      </template>
    </div>
  </q-page>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import http from 'src/utils/request.js';

/** EDM 信件中的退訂連結：/shop/unsubscribe?token=... */
const route = useRoute();
const state = ref('loading');
const errorMessage = ref('退訂連結無效或已過期，請聯繫客服協助。');

onMounted(async () => {
  const token = typeof route.query.token === 'string' ? route.query.token : '';
  if (!token) {
    state.value = 'error';
    return;
  }
  try {
    await http.post('/storefront/unsubscribe', { token }, { silent: true });
    state.value = 'done';
  } catch (error) {
    if (error.displayMessage) errorMessage.value = error.displayMessage;
    state.value = 'error';
  }
});
</script>

<style lang="scss" scoped>
.unsubscribe-page {
  display: flex;
  justify-content: center;
  padding: 48px 16px;
}

.unsubscribe-card {
  max-width: 480px;
  width: 100%;
  text-align: center;
  padding: 32px 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);

  h1 {
    font-size: 22px;
    margin: 12px 0 8px;
  }

  p {
    color: #666;
    margin-bottom: 20px;
  }
}
</style>
