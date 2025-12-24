<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <header>
      <div class="header-content">
        <h1 class="app-title">Shopro 電商管理系統</h1>
        <nav>
          <RouterLink to="/">首頁</RouterLink>
          <RouterLink to="/crm/members">會員管理</RouterLink>
          <RouterLink to="/products">商品管理</RouterLink>
          <RouterLink to="/orders">訂單管理</RouterLink>
        </nav>
      </div>
    </header>

    <main>
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    sans-serif;
  background-color: #f5f5f5;
}

.app-container {
  min-height: 100vh;
}

header {
  background-color: #2c3e50;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-title {
  font-size: 24px;
  margin-bottom: 15px;
}

nav {
  display: flex;
  gap: 20px;
}

nav a {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

nav a:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

nav a.router-link-exact-active {
  background-color: #3498db;
}

main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
