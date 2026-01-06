<template>
  <div id="q-app">
    <router-view />
  </div>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'App',
});
</script>

<style lang="scss">
// 確保購物車頁面使用白色背景
body {
  &.body--light {
    background-color: #ffffff;
  }
}

#q-app {
  background-color: transparent;
}

// 購物車專用的全域樣式覆蓋
.q-layout.shop-layout {
  background-color: #ffffff !important;

  .q-page-container,
  .q-page {
    background-color: #ffffff !important;
  }
}
</style>
