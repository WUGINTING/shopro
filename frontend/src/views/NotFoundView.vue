<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <q-icon name="search_off" size="120px" color="grey-5" />
      <h1 class="text-h3 q-mt-lg q-mb-sm">404</h1>
      <p class="text-h6 text-grey-7 q-mb-lg">抱歉，找不到您要的頁面</p>
      <p class="text-body1 text-grey-6 q-mb-xl">
        您訪問的頁面可能已被移除、名稱已更改，或暫時無法使用。
      </p>
      <div class="row q-gutter-md justify-center">
        <q-btn
          color="primary"
          label="返回首頁"
          icon="home"
          @click="goHome"
          aria-label="返回首頁"
        />
        <q-btn
          outline
          color="primary"
          label="返回上一頁"
          icon="arrow_back"
          @click="goBack"
          aria-label="返回上一頁"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push({ name: 'home' })
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push({ name: 'home' })
  }
}
</script>

<style scoped lang="scss">
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.not-found-content {
  text-align: center;
  padding: 40px;
  max-width: 500px;

  h1 {
    font-weight: 700;
    color: #1976d2;
  }
}
</style>
