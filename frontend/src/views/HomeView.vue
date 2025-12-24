<template>
  <div class="home-view">
    <el-card class="welcome-card">
      <h1>欢迎使用 Shopro E-Commerce 管理系统</h1>
      <p>Welcome to Shopro E-Commerce Management System</p>
      
      <el-divider />
      
      <div class="modules-grid">
        <el-card class="module-card" shadow="hover" @click="navigateTo('/products')">
          <div class="module-icon">🛍️</div>
          <h3>商品管理</h3>
          <p>Product Management</p>
          <small>管理商品、分类、规格、库存等</small>
        </el-card>

        <el-card class="module-card" shadow="hover" @click="navigateTo('/orders')">
          <div class="module-icon">📦</div>
          <h3>订单管理</h3>
          <p>Order Management</p>
          <small>处理订单、发货、物流追踪</small>
        </el-card>

        <el-card class="module-card" shadow="hover" @click="navigateTo('/customers')">
          <div class="module-icon">👥</div>
          <h3>顾客管理</h3>
          <p>CRM</p>
          <small>会员管理、积分系统、客户分析</small>
        </el-card>

        <el-card class="module-card" shadow="hover">
          <div class="module-icon">📊</div>
          <h3>统计报表</h3>
          <p>Statistics</p>
          <small>销售分析、库存报表</small>
        </el-card>

        <el-card class="module-card" shadow="hover">
          <div class="module-icon">🎯</div>
          <h3>行销活动</h3>
          <p>Marketing</p>
          <small>优惠券、促销活动管理</small>
        </el-card>

        <el-card class="module-card" shadow="hover">
          <div class="module-icon">🏪</div>
          <h3>商店设定</h3>
          <p>Store Settings</p>
          <small>商店配置、主题设置</small>
        </el-card>
      </div>

      <el-divider />

      <div class="tech-stack">
        <h3>技术栈 / Tech Stack</h3>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card>
              <strong>后端 Backend</strong>
              <ul>
                <li>Spring Boot 3.4.1</li>
                <li>Java 17</li>
                <li>JPA / Hibernate</li>
                <li>MS SQL Server</li>
              </ul>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <strong>前端 Frontend</strong>
              <ul>
                <li>Vue 3</li>
                <li>TypeScript</li>
                <li>Vite</li>
                <li>Element Plus</li>
              </ul>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <strong>工具 Tools</strong>
              <ul>
                <li>Vue Router</li>
                <li>Pinia (State Management)</li>
                <li>Axios</li>
                <li>Vitest (Testing)</li>
              </ul>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.home-view {
  padding: 20px;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card h1 {
  text-align: center;
  color: #409eff;
  margin-bottom: 10px;
}

.welcome-card > p {
  text-align: center;
  color: #666;
  font-size: 18px;
  margin-bottom: 30px;
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 30px 0;
}

.module-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.module-card:hover {
  transform: translateY(-5px);
}

.module-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.module-card h3 {
  margin: 10px 0 5px 0;
  color: #333;
}

.module-card p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.module-card small {
  color: #999;
  font-size: 12px;
}

.tech-stack {
  margin-top: 30px;
}

.tech-stack h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.tech-stack ul {
  list-style: none;
  padding: 0;
  margin: 10px 0;
}

.tech-stack li {
  padding: 5px 0;
  color: #666;
}
</style>
