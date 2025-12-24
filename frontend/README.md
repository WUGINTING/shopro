# Shopro E-Commerce Frontend

Vue 3 + TypeScript + Vite 前端應用

## 技術棧 / Tech Stack

- **Vue 3** - 漸進式 JavaScript 框架
- **TypeScript** - 類型安全的 JavaScript
- **Vite** - 快速的前端建構工具
- **Vue Router** - Vue.js 官方路由管理器
- **Pinia** - Vue 3 狀態管理
- **Element Plus** - Vue 3 UI 組件庫
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

## 開發指南

### 安裝依賴

```sh
npm install
```

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
- `/about` - 關於頁面

## 狀態管理

使用 Pinia 進行全局狀態管理。

## 組件使用指南

### Element Plus 組件

項目使用 Element Plus 作為 UI 組件庫，已全局引入：

```vue
<template>
  <el-button type="primary">主要按鈕</el-button>
  <el-table :data="tableData">
    <el-table-column prop="name" label="名稱" />
  </el-table>
</template>
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
- [Element Plus 文檔](https://element-plus.org/)
- [Vue Router 文檔](https://router.vuejs.org/)
- [Pinia 文檔](https://pinia.vuejs.org/)

