<template>
  <div class="home">
    <h1>歡迎使用 Shopro 電商管理系統</h1>
    
    <div class="cards">
      <div class="card">
        <h2>會員管理 (CRM)</h2>
        <p>管理會員資料、會員等級、積點系統、EDM 電子報、部落格等功能。</p>
        <router-link to="/crm/members" class="btn">進入會員管理</router-link>
      </div>

      <div class="card">
        <h2>商品管理</h2>
        <p>管理商品資料、商品分類、標籤、庫存、價格等功能。</p>
        <router-link to="/products" class="btn">進入商品管理</router-link>
      </div>

      <div class="card">
        <h2>訂單管理</h2>
        <p>管理訂單、訂單狀態、物流、付款等功能。</p>
        <router-link to="/orders" class="btn">進入訂單管理</router-link>
      </div>
    </div>

    <div class="features">
      <h2>系統功能</h2>
      <ul>
        <li>✅ 完整的 RESTful API 整合</li>
        <li>✅ 會員管理：新增、編輯、刪除、查詢會員</li>
        <li>✅ 商品管理：商品 CRUD、分類管理、標籤管理</li>
        <li>✅ 訂單管理：訂單 CRUD、狀態篩選、客戶查詢</li>
        <li>✅ 分頁查詢功能</li>
        <li>✅ 錯誤處理與使用者提示</li>
        <li>✅ 響應式設計</li>
      </ul>
    </div>

    <div class="api-info">
      <h2>API 配置</h2>
      <p>當前 API 基礎 URL: <code>{{ apiBaseUrl }}</code></p>
      <p>請確保後端服務正在運行於此位址。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
</script>

<style scoped>
.home {
  max-width: 1000px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 40px;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.card {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.card h2 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.card p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.btn:hover {
  background-color: #2980b9;
}

.features {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.features h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.features ul {
  list-style: none;
  padding: 0;
}

.features li {
  padding: 10px 0;
  color: #555;
  font-size: 16px;
}

.api-info {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #3498db;
}

.api-info h2 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.api-info p {
  color: #666;
  margin: 10px 0;
}

.api-info code {
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e74c3c;
}
</style>
