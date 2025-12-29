<script setup lang="ts">
import { RouterView } from 'vue-router'
import '@/styles/app.scss'
</script>

<template>
  <RouterView />
</template>

<style>
/* 全局樣式 - 僅保留必要的覆寫 */
.q-table tbody td {
  font-size: 0.9rem;
}

.q-table thead th {
  font-weight: 600;
  font-size: 0.95rem;
}
</style>
