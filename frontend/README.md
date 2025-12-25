# Shopro E-Commerce Frontend

Vue 3 + TypeScript + Vite 前端應用

## 技術棧 / Tech Stack

- **Vue 3** - 漸進式 JavaScript 框架
- **TypeScript** - 類型安全的 JavaScript
- **Vite** - 快速的前端建構工具
- **Quasar Framework** - Vue 3 UI 組件庫
- **Vue Router** - Vue.js 官方路由管理器
- **Pinia** - Vue 3 狀態管理
- **Chart.js** - 資料視覺化圖表庫
- **Axios** - HTTP 客戶端

## 專案結構

```
frontend/
├── src/
│   ├── api/              # API 服務層
│   │   ├── axios.ts      # Axios 配置與攔截器
│   │   ├── product.ts    # 商品相關 API
│   │   ├── order.ts      # 訂單相關 API
│   │   └── crm.ts        # CRM 相關 API
│   ├── assets/           # 靜態資源
│   ├── components/       # 通用組件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 狀態管理
│   ├── views/            # 頁面組件
│   │   ├── HomeView.vue      # 首頁
│   │   ├── ProductView.vue   # 商品管理
│   │   ├── OrderView.vue     # 訂單管理
│   │   └── CustomerView.vue  # 顧客管理
│   ├── App.vue           # 根組件
│   └── main.ts           # 應用入口
├── public/               # 公共資源
├── index.html            # HTML 模板
├── vite.config.ts        # Vite 配置
└── package.json          # 依賴管理

```

## 主要功能模組

### 1. 商品管理 (Product Management)
- 商品列表展示
- 新增/編輯/刪除商品
- 商品分類管理
- 規格與庫存管理

### 2. 訂單管理 (Order Management)
- 訂單列表與篩選
- 訂單狀態管理
- 訂單詳情查看
- 批次操作支援

### 3. 顧客管理 (CRM)
- 顧客資訊管理
- 會員等級系統
- 積分管理
- 消費記錄追蹤

### 4. 支付管理 (Payment Management) ✨ NEW
- **金流儀表板** - 今日/本月交易統計、成功率、支付管道佔比圖表
- **金流交易紀錄** - 交易搜尋、狀態同步、詳情查看
- **支付參數設定** - 支付閘道啟用/停用、維護模式、抽成比率設定
- 支援 LINE PAY、綠界 ECPay 等台灣支付閘道

## 開發指南

### ⚠️ 重要：首次安裝依賴

**在啟動開發模式之前，務必先安裝依賴！**

```sh
npm install
```

這會安裝所有必要的套件，包括：
- Chart.js (用於支付儀表板的圖表顯示)
- Quasar Framework
- Vue Router, Pinia 等核心套件

### 開發模式

啟動開發伺服器，支援熱更新：

```sh
npm run dev
```

應用將在 `http://localhost:5173` 運行

### 生產建置

```sh
npm run build
```

建置產物將輸出到 `dist/` 目錄

### 預覽生產建置

```sh
npm run preview
```

### 類型檢查

```sh
npm run type-check
```

## API 配置

前端透過 Vite 代理連接到後端 API：

```typescript
// vite.config.ts
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // Spring Boot 後端地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### 使用 API 服務

```typescript
import { productApi } from '@/api'

// 獲取商品列表
const products = await productApi.getProducts()

// 創建商品
await productApi.createProduct(productData)

// 更新商品
await productApi.updateProduct(id, productData)
```

## 路由結構

- `/` - 首頁儀表板
- `/products` - 商品管理
- `/orders` - 訂單管理
- `/customers` - 顧客管理 (CRM)
- `/payment-dashboard` - 金流儀表板 ✨ NEW
- `/payment-transactions` - 金流交易紀錄 ✨ NEW
- `/payment-settings` - 支付參數設定 ✨ NEW
- `/about` - 關於頁面

## 狀態管理

使用 Pinia 進行全局狀態管理。

## 組件使用指南

### Quasar 組件

項目使用 Quasar Framework 作為 UI 組件庫：

```vue
<template>
  <q-btn color="primary" label="主要按鈕" />
  <q-table :rows="rows" :columns="columns" />
  <q-card>
    <q-card-section>內容</q-card-section>
  </q-card>
</template>
```

### Chart.js 圖表

支付儀表板使用 Chart.js 進行資料視覺化：

```vue
<script setup>
import Chart from 'chart.js/auto'

const chartInstance = new Chart(canvas.value, {
  type: 'pie',
  data: { ... },
  options: { ... }
})
</script>
```

## 連接後端

確保 Spring Boot 後端運行在 `http://localhost:8080`：

```bash
cd ../E-commerce
./mvnw spring-boot:run
```

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## 資源連結

- [Vue 3 文檔](https://vuejs.org/)
- [Vite 文檔](https://vitejs.dev/)
- [Quasar Framework 文檔](https://quasar.dev/)
- [Chart.js 文檔](https://www.chartjs.org/)
- [Vue Router 文檔](https://router.vuejs.org/)
- [Pinia 文檔](https://pinia.vuejs.org/)

## 常見問題

### Q: 為什麼 Chart.js 無法載入？

**錯誤訊息**: `Failed to resolve import "chart.js/auto"`

**解決方案**: 
```bash
# 確保已安裝依賴
npm install

# 如果問題持續，清除並重新安裝
rm -rf node_modules package-lock.json
npm install
```

### Q: 開發伺服器無法啟動？

確保：
1. 已執行 `npm install`
2. Node.js 版本符合要求（>= 20.19.0）
3. 後端 Spring Boot 服務運行在 port 8080

