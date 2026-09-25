<template>
  <q-page class="shop-not-found">
    <q-icon name="storefront" size="64px" color="grey-5" />
    <h1>找不到這個頁面</h1>
    <p>商品可能已下架，或網址有誤。看看其他商品吧！</p>
    <div class="actions">
      <q-btn unelevated color="primary" label="回到商店首頁" to="/shop" />
      <q-btn outline color="primary" label="瀏覽全部商品" to="/shop/product/list" />
    </div>
  </q-page>
</template>

<script setup>
import { useShopMeta } from 'src/composables/useShopMeta.js';

useShopMeta(() => ({ title: '找不到頁面' }));
</script>

<style lang="scss" scoped>
.shop-not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 64px 16px;

  h1 {
    font-size: 24px;
    margin: 16px 0 8px;
  }

  p {
    color: #777;
    margin-bottom: 24px;
  }

  .actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
