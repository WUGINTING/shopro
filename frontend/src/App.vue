<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <h1>Shopro E-Commerce 管理系统</h1>
        <el-menu
          mode="horizontal"
          router
          :default-active="$route.path"
          class="app-menu"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/products">商品管理</el-menu-item>
          <el-menu-item index="/orders">订单管理</el-menu-item>
          <el-menu-item index="/customers">顾客管理 (CRM)</el-menu-item>
          <el-menu-item index="/about">关于</el-menu-item>
        </el-menu>
      </div>
    </el-header>

    <el-main class="app-main">
      <RouterView />
    </el-main>

    <el-footer class="app-footer">
      <p>&copy; 2024 Shopro E-Commerce. Built with Vue 3 + TypeScript + Element Plus</p>
    </el-footer>
  </el-container>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  background-color: #409eff;
  color: white;
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-content h1 {
  margin: 10px 0;
  font-size: 24px;
  color: white;
}

.app-menu {
  background-color: transparent;
  border: none;
}

.app-menu .el-menu-item {
  color: white;
}

.app-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.app-menu .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.2);
  border-bottom: 3px solid white;
}

.app-main {
  background-color: #f5f5f5;
  min-height: calc(100vh - 180px);
}

.app-footer {
  background-color: #333;
  color: white;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-footer p {
  margin: 0;
}
</style>
