# Frontend 開發規範與指南

> **專案名稱**：遇日小舖購物車後台管理系統  
> **技術棧**：Vue 3 + TypeScript + Vite + Quasar Framework v2  
> **最後更新**：2026年1月10日

---

## 📋 目錄

1. [專案概述](#專案概述)
2. [技術架構](#技術架構)
3. [專案結構](#專案結構)
4. [開發規範](#開發規範)
5. [UI 框架使用規範](#ui-框架使用規範)
6. [樣式開發規範](#樣式開發規範)
7. [API 開發規範](#api-開發規範)
8. [路由與佈局](#路由與佈局)
9. [元件開發規範](#元件開發規範)
10. [狀態管理](#狀態管理)
11. [開發流程](#開發流程)
12. [常見問題](#常見問題)

---

## 專案概述

### 專案定位
本專案為 **遇日小舖電商平台的後台管理系統**，提供商品管理、訂單處理、會員管理、營銷活動、支付管理等完整的電商後台功能。

### 核心功能模組
- **商品管理**：商品列表、分類、規格、庫存管理
- **訂單管理**：訂單處理、狀態更新、折扣、問答
- **客戶管理**：會員管理、分組、等級、CRM
- **營銷管理**：促銷活動、優惠券、積分、EDM
- **內容管理**：部落格、相冊
- **支付管理**：支付儀表板、交易記錄、閘道設定
- **系統管理**：數據統計、操作日誌、用戶管理、系統設定

---

## 技術架構

### 核心技術
```json
{
  "框架": "Vue 3.5.25 (Composition API)",
  "語言": "TypeScript 5.9.0",
  "建構工具": "Vite 7.2.4",
  "UI框架": "Quasar Framework 2.18.6",
  "路由": "Vue Router 4.6.3",
  "狀態管理": "Pinia 3.0.4",
  "HTTP客戶端": "Axios 1.13.2",
  "圖表庫": "Chart.js 4.4.1",
  "國際化": "Vue I18n 9.14.5",
  "Cookie管理": "js-cookie 3.0.5"
}
```

### 開發環境要求
- **Node.js**：^20.19.0 或 >=22.12.0
- **包管理器**：npm
- **IDE 推薦**：VS Code + Volar 擴展

### 專案啟動
```bash
# 安裝依賴（首次必須執行）
npm install

# 啟動開發伺服器
npm run dev

# 類型檢查
npm run type-check

# 生產建置
npm run build

# 預覽建置
npm run preview
```

---

## 專案結構

```
frontend/
├── public/                      # 靜態資源
├── src/
│   ├── api/                     # API 服務層（模組化）
│   │   ├── axios.ts            # Axios 實例與攔截器配置
│   │   ├── types.ts            # 通用 API 類型定義
│   │   ├── index.ts            # API 統一出口
│   │   ├── api_docs.md         # API 文檔（JSDoc 規範）
│   │   ├── product.ts          # 商品相關 API
│   │   ├── order.ts            # 訂單相關 API
│   │   ├── auth.ts             # 認證相關 API
│   │   ├── crm.ts              # CRM 相關 API
│   │   ├── payment.ts          # 支付相關 API
│   │   ├── marketing.ts        # 營銷相關 API
│   │   ├── member.ts           # 會員相關 API
│   │   ├── blog.ts             # 部落格相關 API
│   │   ├── album.ts            # 相冊相關 API
│   │   └── ...                 # 其他模組 API
│   │
│   ├── assets/                 # 靜態資源（圖片、字體等）
│   │   ├── base.css
│   │   └── main.css
│   │
│   ├── components/             # 共用元件
│   │   ├── CouponManagement.vue
│   │   ├── HelloWorld.vue
│   │   └── icons/              # 圖標元件
│   │
│   ├── layouts/                # 佈局模板
│   │   └── MainLayout.vue      # 主佈局（含 Header、Sidebar）
│   │
│   ├── router/                 # 路由配置
│   │   └── index.ts            # 路由定義與導航守衛
│   │
│   ├── stores/                 # Pinia 狀態管理
│   │   ├── auth.ts             # 認證狀態
│   │   └── counter.ts          # 範例狀態
│   │
│   ├── styles/                 # 全域樣式（SCSS）
│   │   ├── app.scss            # 專案統一樣式
│   │   ├── variables.scss      # SCSS 變數
│   │   ├── animate.scss        # 動畫樣式
│   │   ├── theme-system.scss   # 主題系統
│   │   └── quasar-variables.sass  # Quasar 變數覆寫
│   │
│   ├── views/                  # 頁面元件
│   │   ├── HomeView.vue        # 首頁儀表板
│   │   ├── ProductView.vue     # 商品管理
│   │   ├── OrderView.vue       # 訂單管理
│   │   ├── CustomerView.vue    # 客戶管理
│   │   ├── PaymentDashboardView.vue    # 支付儀表板
│   │   ├── PaymentTransactionsView.vue # 交易記錄
│   │   ├── PaymentSettingsView.vue     # 支付設定
│   │   └── ...                 # 其他頁面
│   │
│   ├── App.vue                 # 根元件
│   └── main.ts                 # 應用入口
│
├── .env.example                # 環境變數範例
├── index.html                  # HTML 模板
├── vite.config.ts              # Vite 配置
├── tsconfig.json               # TypeScript 配置
├── tsconfig.app.json           # 應用 TS 配置
├── tsconfig.node.json          # Node TS 配置
└── package.json                # 依賴管理
```

---

## 開發規範

### 1. TypeScript 使用規範

#### ✅ 強制使用 `<script setup lang="ts">`

所有 Vue 元件必須使用 Composition API + TypeScript：

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product, type ApiResponse } from '@/api'

const $q = useQuasar()
const products = ref<Product[]>([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    products.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入商品失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProducts()
})
</script>
```

#### ✅ 類型定義
- 所有 API 回應必須定義類型介面
- 使用 `interface` 定義物件結構
- 使用 `type` 定義聯合類型或複雜類型

```typescript
// 定義介面
export interface Product {
  id?: number
  name: string
  price: number
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
}

// 定義類型
export type ProductStatus = 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
```

### 2. 命名規範

| 類型 | 規範 | 範例 |
|-----|------|------|
| **元件檔案** | PascalCase + .vue | `ProductView.vue`, `CouponManagement.vue` |
| **API 檔案** | camelCase + .ts | `product.ts`, `orderDiscount.ts` |
| **變數/函式** | camelCase | `loadProducts`, `isLoading` |
| **常數** | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_RETRY_COUNT` |
| **介面** | PascalCase | `Product`, `ApiResponse` |
| **類型** | PascalCase | `ProductStatus`, `OrderStatus` |
| **樣式檔案** | kebab-case + .scss | `app.scss`, `theme-system.scss` |

### 3. 檔案組織規範

#### 單一職責原則
- 每個 API 檔案只處理一個業務模組
- 每個 View 對應一個完整頁面
- 每個 Component 只處理一個獨立功能

#### 匯出規範
```typescript
// api/index.ts - 統一匯出
export * from './product'
export * from './order'

export { default as productApi } from './product'
export { default as orderApi } from './order'
```

---

## UI 框架使用規範

### Quasar Framework v2 官方文檔
**重要**：開發任何 UI 功能前，必須參考最新官方文檔：  
🔗 **https://quasar.dev/**

### 1. Quasar 元件使用規範

#### ✅ 推薦使用的元件

##### 表單元件
```vue
<template>
  <!-- 輸入框 -->
  <q-input 
    v-model="form.name" 
    label="商品名稱" 
    outlined 
    dense
    :rules="[val => !!val || '請輸入商品名稱']"
  />
  
  <!-- 選擇器 -->
  <q-select 
    v-model="form.category" 
    :options="categories" 
    label="商品分類" 
    outlined 
    dense
  />
  
  <!-- 日期選擇 -->
  <q-input 
    v-model="form.date" 
    label="上架日期" 
    outlined 
    dense
  >
    <template v-slot:append>
      <q-icon name="event" class="cursor-pointer">
        <q-popup-proxy>
          <q-date v-model="form.date" />
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>
</template>
```

##### 資料展示元件
```vue
<template>
  <!-- 表格 -->
  <q-table
    :rows="products"
    :columns="columns"
    row-key="id"
    :loading="loading"
    :pagination="pagination"
    flat
    bordered
  >
    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <q-btn flat dense icon="edit" color="primary" />
        <q-btn flat dense icon="delete" color="negative" />
      </q-td>
    </template>
  </q-table>
  
  <!-- 卡片 -->
  <q-card class="card-hover">
    <q-card-section>
      <div class="text-h6">標題</div>
    </q-card-section>
    <q-card-section>
      內容區域
    </q-card-section>
    <q-card-actions align="right">
      <q-btn flat label="取消" />
      <q-btn color="primary" label="確定" />
    </q-card-actions>
  </q-card>
</template>
```

##### 互動元件
```vue
<template>
  <!-- 按鈕 -->
  <q-btn 
    color="primary" 
    label="新增商品" 
    icon="add" 
    @click="handleAdd"
  />
  
  <!-- 對話框 -->
  <q-dialog v-model="showDialog" persistent>
    <q-card style="min-width: 400px">
      <q-card-section>
        <div class="text-h6">確認操作</div>
      </q-card-section>
      <q-card-section>
        確定要刪除此商品嗎？
      </q-card-section>
      <q-card-actions align="right">
        <q-btn flat label="取消" v-close-popup />
        <q-btn color="negative" label="刪除" @click="handleDelete" />
      </q-card-actions>
    </q-card>
  </q-dialog>
  
  <!-- 通知 -->
  <script setup lang="ts">
  import { useQuasar } from 'quasar'
  
  const $q = useQuasar()
  
  const showNotify = () => {
    $q.notify({
      type: 'positive',
      message: '操作成功',
      position: 'top'
    })
  }
  </script>
</template>
```

### 2. Quasar 插件配置

#### main.ts 引入
```typescript
import { createApp } from 'vue'
import { Quasar, Notify, Dialog, Loading } from 'quasar'

// 引入 Quasar 圖標庫
import '@quasar/extras/material-icons/material-icons.css'
import '@quasar/extras/fontawesome-v6/fontawesome-v6.css'

// 引入 Quasar CSS
import 'quasar/src/css/index.sass'

const app = createApp(App)

app.use(Quasar, {
  plugins: {
    Notify,   // 通知
    Dialog,   // 對話框
    Loading   // 載入遮罩
  },
  config: {
    notify: { position: 'top' },
    loading: {}
  }
})
```

### 3. 圖標使用規範

Quasar 支援多種圖標庫，專案已引入：
- **Material Icons**：`icon="home"`
- **Font Awesome 6**：`icon="fas fa-heart"`

範例：
```vue
<q-btn icon="add" label="新增" />
<q-icon name="shopping_cart" size="24px" />
<q-avatar icon="person" color="primary" text-color="white" />
```

---

## 樣式開發規範

### 1. SCSS 全域樣式系統

#### 樣式檔案結構
```
src/styles/
├── app.scss                # 主樣式檔案（專案統一樣式）
├── variables.scss          # SCSS 變數定義
├── animate.scss            # 動畫效果
├── theme-system.scss       # 主題系統
└── quasar-variables.sass   # Quasar 變數覆寫
```

#### app.scss - 專案統一樣式
已定義的全域樣式類別：

```scss
/* ============ 漸層背景 ============ */
.bg-gradient-primary { /* 藍色漸層 */ }
.bg-gradient-purple { /* 紫色漸層 */ }

/* ============ 卡片樣式 ============ */
.card-hover { /* 懸浮效果 */ }
.card-border-top { /* 頂部邊框（可配合顏色類別） */ }
  &.border-primary   /* 藍色邊框 */
  &.border-orange    /* 橘色邊框 */
  &.border-teal      /* 青色邊框 */
  &.border-green     /* 綠色邊框 */
  &.border-red       /* 紅色邊框 */

/* ============ 資訊行樣式 ============ */
.info-row { /* 資訊列容器 */ }
.info-label { /* 標籤樣式 */ }
.info-value { /* 值樣式 */ }

/* ============ 容器樣式 ============ */
.page-container { /* 頁面容器 */ }
.profile-container { /* 個人資料容器 */ }
.wide-container { /* 寬容器 */ }

/* ============ 圖片相關 ============ */
.album-card { /* 相冊卡片 */ }
.album-cover { /* 相冊封面 */ }
```

### 2. 樣式使用規範

#### ✅ 強制使用 class 管理樣式

**禁止**在 `<template>` 中使用內聯 `style`：
```vue
<!-- ❌ 錯誤示範 -->
<div style="color: red; font-size: 16px;">內容</div>

<!-- ✅ 正確示範 -->
<div class="text-negative text-body1">內容</div>
```

#### ✅ 單頁面專屬樣式使用 `<style scoped>`

如果樣式僅在單一頁面使用，可放在頁面元件底部：

```vue
<template>
  <div class="product-detail-container">
    <div class="product-header">...</div>
  </div>
</template>

<script setup lang="ts">
// ... 邏輯程式碼
</script>

<style scoped lang="scss">
.product-detail-container {
  padding: 24px;
  
  .product-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
```

#### ✅ 共用樣式必須寫入 `styles/app.scss`

如果樣式會在多個元件中使用，必須：
1. 定義在 `src/styles/app.scss`
2. 使用語意化的 class 名稱
3. 添加註解說明用途

```scss
/* ============ 狀態標籤 ============ */
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  
  &.status-active {
    background-color: #E8F5E9;
    color: #2E7D32;
  }
  
  &.status-inactive {
    background-color: #FFEBEE;
    color: #C62828;
  }
}
```

### 3. Quasar 內建樣式類別

優先使用 Quasar 提供的工具類別：

#### 間距類別
```vue
<template>
  <div class="q-pa-md">       <!-- padding: 16px -->
  <div class="q-ma-lg">       <!-- margin: 24px -->
  <div class="q-mt-sm">       <!-- margin-top: 8px -->
  <div class="q-px-xl">       <!-- padding-left/right: 48px -->
</template>
```

#### 文字類別
```vue
<template>
  <div class="text-h5">標題</div>              <!-- 標題樣式 h5 -->
  <div class="text-weight-bold">粗體</div>     <!-- 粗體 -->
  <div class="text-primary">主色文字</div>     <!-- 主題色 -->
  <div class="text-grey-7">灰色文字</div>      <!-- 灰色 -->
  <div class="text-center">置中</div>          <!-- 文字置中 -->
</template>
```

#### 佈局類別
```vue
<template>
  <div class="row items-center justify-between">
    <div class="col-6">左側</div>
    <div class="col-6">右側</div>
  </div>
</template>
```

---

## API 開發規範

### 1. API 模組化結構

#### 檔案組織
每個業務模組對應一個 API 檔案：

```
src/api/
├── axios.ts                # Axios 實例配置
├── types.ts                # 通用類型定義
├── index.ts                # 統一匯出
├── api_docs.md             # API 完整文檔（已更新至 v1.0.0）
├── product.ts              # 商品模組
├── order.ts                # 訂單模組
├── auth.ts                 # 認證模組
├── crm.ts                  # CRM 客戶管理
├── payment.ts              # 支付管理
├── member.ts               # 會員管理
├── memberLevel.ts          # 會員等級
├── memberGroup.ts          # 會員群組
├── marketing.ts            # 營銷活動
├── promotion.ts            # 促銷優惠
├── point.ts                # 積點系統
├── edm.ts                  # EDM 電子報
├── blog.ts                 # 部落格
├── album.ts                # 相冊管理
├── dashboard.ts            # 儀表板
├── statistics.ts           # 統計分析
├── settings.ts             # 系統設定
├── user.ts                 # 使用者管理
└── ...                     # 其他模組（共 24 個 API 檔案）
```

> 💡 **重要**: 所有 API 檔案已完整遵循 JSDoc 規範，詳細文檔請參考 [api_docs.md](../frontend/src/api/api_docs.md)

### 2. JSDoc 註解規範

**所有 API 檔案必須遵循 JSDoc 規範撰寫註解**。✅ **已完成 24 個 API 檔案的 JSDoc 規範更新**

#### 模組級別註解（必須）

```typescript
/**
 * 商品相關 API
 * @module ProductAPI
 */
```

#### Interface 註解（必須）

```typescript
/**
 * 商品介面
 * @interface Product
 */
export interface Product {
  /** 商品 ID */
  id?: number
  /** 商品名稱 */
  name: string
  /** 商品描述 */
  description: string
  /** 商品價格 */
  price: number
  /** 庫存數量 */
  stock: number
  /** 商品狀態 */
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
}
```

#### API 函式註解（必須）

所有 API 函式必須包含完整的 JSDoc 註解：

```typescript
/**
 * 商品 API 服務
 * @namespace productApi
 */
export const productApi = {
  /**
   * 獲取商品列表
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.page] - 頁碼
   * @param {number} [params.size] - 每頁數量
   * @param {string} [params.status] - 商品狀態篩選
   * @returns {Promise<ApiResponse<Product[]>>} 商品列表回應
   * @example
   * const response = await productApi.getProducts({ page: 1, size: 10 })
   */
  getProducts: (params?: any) => {
    return axios.get<any, ApiResponse<Product[]>>('/products', { params })
  },

  /**
   * 獲取單一商品詳情
   * @param {number} id - 商品 ID
   * @returns {Promise<ApiResponse<Product>>} 商品詳情回應
   * @throws {Error} 當商品不存在時拋出錯誤
   * @example
   * const response = await productApi.getProduct(123)
   */
  getProduct: (id: number) => {
    return axios.get<any, ApiResponse<Product>>(`/products/${id}`)
  },

  /**
   * 創建新商品
   * @param {Product} data - 商品資料
   * @returns {Promise<ApiResponse<Product>>} 創建成功的商品資料
   * @example
   * const newProduct = await productApi.createProduct({
   *   name: '新商品',
   *   price: 100,
   *   stock: 50
   * })
   */
  createProduct: (data: Product) => {
    return axios.post<any, ApiResponse<Product>>('/products', data)
  }
}

export default productApi
```

### 3. Axios 配置規範

#### axios.ts - 統一配置
```typescript
import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'

// 創建實例
const axiosInstance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器 - 添加 Token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 回應攔截器 - 統一錯誤處理
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // 未授權處理
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
```

### 4. 通用類型定義

#### types.ts - API 回應類型
```typescript
/**
 * 通用 API 類型定義
 * @module APITypes
 */

/**
 * 通用 API 回應介面
 * @interface ApiResponse
 * @template T - 資料類型
 */
export interface ApiResponse<T = any> {
  /** 是否成功 */
  success: boolean
  /** 回應訊息 */
  message: string
  /** 回應資料 */
  data: T
  /** 時間戳記 */
  timestamp: string
}

/**
 * 分頁回應介面
 * @interface PageResponse
 * @template T - 資料類型
 */
export interface PageResponse<T> {
  /** 資料內容 */
  content: T[]
  /** 分頁資訊 */
  pageable: {
    pageNumber: number
    pageSize: number
  }
  /** 總元素數 */
  totalElements: number
  /** 總頁數 */
  totalPages: number
  /** 是否最後一頁 */
  last: boolean
  /** 是否第一頁 */
  first: boolean
  /** 是否空資料 */
  empty: boolean
}
```

> 📖 **完整 API 文檔**: 所有 24 個 API 模組的詳細文檔請參考 [api_docs.md](../frontend/src/api/api_docs.md)，包含：
> - 完整的模組列表和說明
> - 所有 Interface 定義
> - API 方法使用範例
> - 錯誤處理建議
> - TypeScript 類型使用指南

### 5. API 使用範例

#### 在 Vue 元件中使用
```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product, type ApiResponse } from '@/api'

const $q = useQuasar()
const products = ref<Product[]>([])
const loading = ref(false)

/**
 * 載入商品列表
 */
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    products.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入商品失敗',
      position: 'top'
    })
    console.error('載入商品失敗:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 創建商品
 */
const createProduct = async (productData: Product) => {
  try {
    await productApi.createProduct(productData)
    $q.notify({
      type: 'positive',
      message: '商品創建成功'
    })
    await loadProducts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '商品創建失敗'
    })
  }
}

onMounted(() => {
  loadProducts()
})
</script>
```

#### 統一的匯入方式

```typescript
// ✅ 推薦：從統一出口匯入
import { productApi, orderApi, authApi, type Product, type Order } from '@/api'

// ❌ 不推薦：直接從個別文件匯入
import productApi from '@/api/product'
```

#### 完整的錯誤處理

```typescript
try {
  const response = await productApi.getProduct(123)
  // 處理成功回應
} catch (error) {
  if (error.response?.status === 401) {
    // 未授權，跳轉登入
    router.push('/login')
  } else if (error.response?.status === 404) {
    // 資源不存在
    showNotFound()
  } else {
    // 其他錯誤
    showError('操作失敗')
  }
}
```

> 📚 **更多範例**: 完整的 API 使用範例和最佳實踐請參考：
> - [api_docs.md](../frontend/src/api/api_docs.md) - 詳細的 API 文檔
> - 各個 API 文件中的 `@example` 註解

---

## 路由與佈局

### 1. 路由結構

#### router/index.ts
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 登入頁（不需要佈局）
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    // 主要頁面（使用 MainLayout）
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue')
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue')
        }
        // ... 其他路由
      ]
    }
  ]
})

// 導航守衛 - 認證檢查
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth !== false
  const isAuthenticated = authStore.isAuthenticated

  if (requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && isAuthenticated) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
```

### 2. 佈局系統

#### MainLayout.vue - 主佈局
```vue
<template>
  <q-layout view="hHh lpR fFf">
    <!-- Header -->
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />
        <q-toolbar-title>
          遇日小舖 - 管理系統
        </q-toolbar-title>
        <!-- 用戶選單 -->
      </q-toolbar>
    </q-header>

    <!-- Sidebar -->
    <q-drawer v-model="leftDrawerOpen" show-if-above :width="250">
      <q-scroll-area class="fit">
        <q-list padding>
          <!-- 導航項目 -->
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <!-- 主內容區 -->
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const leftDrawerOpen = ref(true)

const toggleLeftDrawer = () => {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

const navigateTo = (name: string) => {
  router.push({ name })
}
</script>
```

### 3. 佈局使用規範

#### ✅ 共用佈局放置位置
```
src/layouts/
└── MainLayout.vue          # 主佈局（Header + Sidebar）
```

#### ✅ 在路由中套用佈局
```typescript
{
  path: '/',
  component: MainLayout,      // 套用佈局
  children: [
    {
      path: 'products',
      component: () => import('../views/ProductView.vue')
    }
  ]
}
```

---

## 元件開發規範

### 1. 共用元件放置位置

```
src/components/
├── CouponManagement.vue    # 優惠券管理元件
├── ProductCard.vue         # 商品卡片元件
├── OrderStatusBadge.vue    # 訂單狀態標籤元件
└── icons/                  # 圖標元件
    ├── IconHome.vue
    └── IconShop.vue
```

### 2. 元件開發規範

#### ✅ 元件必須包含 Props 定義
```vue
<script setup lang="ts">
import { computed } from 'vue'

/**
 * Props 定義
 */
interface Props {
  /** 訂單狀態 */
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  /** 是否顯示圖標 */
  showIcon?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showIcon: true
})

/**
 * 狀態顏色對應
 */
const statusColor = computed(() => {
  const colorMap = {
    PENDING: 'grey',
    PROCESSING: 'warning',
    SHIPPED: 'info',
    DELIVERED: 'positive',
    CANCELLED: 'negative'
  }
  return colorMap[props.status]
})
</script>

<template>
  <q-badge :color="statusColor" :label="props.status" />
</template>
```

#### ✅ 元件必須包含 Emits 定義
```vue
<script setup lang="ts">
/**
 * Emits 定義
 */
interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'save', data: any): void
  (e: 'cancel'): void
}

const emit = defineEmits<Emits>()

const handleSave = () => {
  emit('save', { id: 1, name: '商品' })
}
</script>
```

### 3. 元件使用範例

```vue
<template>
  <OrderStatusBadge 
    :status="order.status" 
    :show-icon="true" 
  />
  
  <ProductCard
    :product="product"
    @add-to-cart="handleAddToCart"
    @view-detail="handleViewDetail"
  />
</template>

<script setup lang="ts">
import OrderStatusBadge from '@/components/OrderStatusBadge.vue'
import ProductCard from '@/components/ProductCard.vue'

const handleAddToCart = (productId: number) => {
  // 處理加入購物車
}
</script>
```

---

## 狀態管理

### 1. Pinia Store 結構

```
src/stores/
├── auth.ts                 # 認證狀態
├── cart.ts                 # 購物車狀態
└── settings.ts             # 系統設定狀態
```

### 2. Store 開發規範

#### auth.ts - 認證狀態範例
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type User } from '@/api'

/**
 * 認證狀態管理
 */
export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // Actions
  /**
   * 初始化認證狀態（從 localStorage 讀取）
   */
  function initialize() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }

  /**
   * 設定認證資訊
   * @param authToken - JWT Token
   * @param userData - 使用者資料
   */
  function setAuth(authToken: string, userData: User) {
    token.value = authToken
    user.value = userData
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  /**
   * 清除認證資訊
   */
  function clearAuth() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  /**
   * 登出
   */
  function logout() {
    clearAuth()
    authApi.logout()
  }

  return {
    // State
    user,
    token,
    // Getters
    isAuthenticated,
    userRole,
    isAdmin,
    // Actions
    initialize,
    setAuth,
    clearAuth,
    logout
  }
})
```

### 3. Store 使用範例

```vue
<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 使用 State
console.log(authStore.user)
console.log(authStore.token)

// 使用 Getters
console.log(authStore.isAuthenticated)
console.log(authStore.isAdmin)

// 呼叫 Actions
authStore.initialize()
authStore.setAuth('token123', { id: 1, username: 'admin', role: 'ADMIN' })
authStore.logout()
</script>
```

---

## 開發流程

### 1. 新功能開發流程

#### Step 1: 定義 API 介面
```typescript
// src/api/newModule.ts
export interface NewData {
  id?: number
  name: string
}

export const newModuleApi = {
  getData: () => axios.get<any, ApiResponse<NewData[]>>('/new-data'),
  createData: (data: NewData) => axios.post<any, ApiResponse<NewData>>('/new-data', data)
}
```

#### Step 2: 在 api/index.ts 中匯出
```typescript
export * from './newModule'
export { default as newModuleApi } from './newModule'
```

#### Step 3: 建立 View 元件
```vue
<!-- src/views/NewModuleView.vue -->
<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="text-h5 q-mb-md">新模組</div>
      <!-- 內容 -->
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { newModuleApi, type NewData } from '@/api'

const data = ref<NewData[]>([])

const loadData = async () => {
  const response = await newModuleApi.getData()
  data.value = response.data
}

onMounted(() => {
  loadData()
})
</script>
```

#### Step 4: 添加路由
```typescript
// src/router/index.ts
{
  path: 'new-module',
  name: 'newModule',
  component: () => import('../views/NewModuleView.vue')
}
```

#### Step 5: 在 MainLayout 添加導航
```vue
<!-- src/layouts/MainLayout.vue -->
<q-item
  clickable
  v-ripple
  :active="isActive('newModule')"
  active-class="bg-primary text-white"
  @click="navigateTo('newModule')"
>
  <q-item-section avatar>
    <q-icon name="new_releases" />
  </q-item-section>
  <q-item-section>
    <q-item-label>新模組</q-item-label>
  </q-item-section>
</q-item>
```

### 2. 程式碼提交規範

#### Commit Message 格式
```
<類型>: <簡短描述>

<詳細描述（可選）>

<相關 Issue（可選）>
```

#### 類型定義
- `feat`: 新功能
- `fix`: 修復錯誤
- `docs`: 文檔更新
- `style`: 程式碼格式調整
- `refactor`: 重構
- `perf`: 效能優化
- `test`: 測試相關
- `chore`: 建置工具或輔助工具變動

#### 範例
```
feat: 新增商品批次上架功能

- 實作批次選擇商品介面
- 新增批次上架 API 呼叫
- 新增成功/失敗提示

Closes #123
```

---

## 常見問題

### Q1: Chart.js 無法載入？
**錯誤訊息**: `Failed to resolve import "chart.js/auto"`

**解決方案**:
```bash
# 確保已安裝依賴
npm install

# 如果問題持續，清除並重新安裝
rm -rf node_modules package-lock.json
npm install
```

### Q2: Quasar 元件樣式異常？
**原因**: Quasar CSS 未正確引入

**解決方案**:
檢查 `main.ts` 是否包含：
```typescript
import 'quasar/src/css/index.sass'
```

### Q3: TypeScript 類型錯誤？
**解決方案**:
```bash
# 執行類型檢查
npm run type-check

# 確認 tsconfig.json 配置正確
```

### Q4: API 請求 401 錯誤？
**原因**: Token 過期或未正確設定

**解決方案**:
1. 檢查 `axios.ts` 請求攔截器
2. 確認 localStorage 中存在 token
3. 確認後端 JWT 驗證邏輯

### Q5: 路由導航守衛無效？
**原因**: Store 初始化時機錯誤

**解決方案**:
```typescript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 從 localStorage 初始化
  if (!authStore.token) {
    authStore.initialize()
  }
  
  // 繼續導航邏輯
})
```

### Q6: 樣式未生效？
**檢查清單**:
1. 是否使用 `scoped` 導致樣式隔離
2. 是否正確引入 SCSS 檔案
3. Quasar 內建類別是否拼寫正確

---

## 附錄

### A. 國際化（i18n）使用指南

#### 配置說明
專案已整合 Vue I18n 9 提供多語系支援，預設為繁體中文，支援切換至英文。

#### 語系檔案
```
src/locale/
├── index.ts      # i18n 配置與初始化
├── tc.ts         # 繁體中文語系
└── en.ts         # 英文語系
```

#### 在元件中使用
```vue
<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

// 切換語系
const switchLanguage = (lang: 'tc' | 'en') => {
  locale.value = lang
}
</script>

<template>
  <div>
    <h1>{{ t('common.confirm') }}</h1>
    <p>{{ t('product.name') }}</p>
    
    <q-btn @click="switchLanguage('en')">English</q-btn>
    <q-btn @click="switchLanguage('tc')">繁體中文</q-btn>
  </div>
</template>
```

#### 在 setup 外使用
```typescript
import { t, setLocale, getLocale } from '@/locale'

// 獲取翻譯
const message = t('common.success')

// 切換語系
setLocale('en')

// 獲取當前語系
const current = getLocale()
```

#### 新增語系
1. 在 `src/locale/` 建立新語系檔案（如 `jp.ts`）
2. 在 `src/locale/index.ts` 中引入並註冊
3. 更新 `Locale` 類型定義

### B. Cookie 管理工具

#### 基本使用
```typescript
import { cookieUtil, COOKIE_KEYS } from '@/utils/cookies'

// 設定 Cookie
cookieUtil.set('username', 'admin', { expires: 30 })

// 設定物件（自動 JSON 序列化）
cookieUtil.set(COOKIE_KEYS.USER, { id: 1, name: 'Admin' })

// 讀取 Cookie
const username = cookieUtil.get('username')
const user = cookieUtil.get(COOKIE_KEYS.USER)

// 檢查是否存在
if (cookieUtil.has(COOKIE_KEYS.TOKEN)) {
  // Token 存在
}

// 刪除 Cookie
cookieUtil.remove('username')

// 清除所有 Cookie（保留白名單）
cookieUtil.clearAll()
```

#### Cookie 鍵名常數
```typescript
import { COOKIE_KEYS } from '@/utils/cookies'

COOKIE_KEYS.TOKEN          // 'auth_token'
COOKIE_KEYS.USER           // 'user_info'
COOKIE_KEYS.LANGUAGE       // 'language'
COOKIE_KEYS.THEME          // 'theme'
COOKIE_KEYS.REMEMBER_ME    // 'remember_me'
```

#### 環境前綴
Cookie 會根據環境自動添加前綴：
- 開發環境：`dev_`
- 測試環境：`test_`
- 生產環境：`prod_`

### C. 表單驗證工具

#### Quasar 表單驗證（推薦）
```vue
<script setup lang="ts">
import { ref } from 'vue'
import { validateUtil } from '@/utils/validate'

const form = ref({
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})
</script>

<template>
  <q-form @submit="handleSubmit">
    <!-- 電子郵件驗證 -->
    <q-input
      v-model="form.email"
      label="電子郵件"
      :rules="[
        validateUtil.required('請輸入電子郵件'),
        validateUtil.email('請輸入有效的電子郵件')
      ]"
    />
    
    <!-- 手機號碼驗證 -->
    <q-input
      v-model="form.phone"
      label="手機號碼"
      :rules="[
        validateUtil.required(),
        validateUtil.phoneTW('請輸入有效的台灣手機號碼')
      ]"
    />
    
    <!-- 密碼驗證 -->
    <q-input
      v-model="form.password"
      label="密碼"
      type="password"
      :rules="[
        validateUtil.required(),
        validateUtil.password(),
        validateUtil.minLength(8, '密碼至少 8 個字元')
      ]"
    />
    
    <!-- 確認密碼 -->
    <q-input
      v-model="form.confirmPassword"
      label="確認密碼"
      type="password"
      :rules="[
        validateUtil.required(),
        validateUtil.confirmPassword(form.password)
      ]"
    />
    
    <!-- 數字範圍驗證 -->
    <q-input
      v-model="form.age"
      label="年齡"
      type="number"
      :rules="[
        validateUtil.required(),
        validateUtil.min(18, '年齡必須大於 18 歲'),
        validateUtil.max(100, '年齡必須小於 100 歲')
      ]"
    />
  </q-form>
</template>
```

#### 可用的驗證規則
```typescript
// 基本驗證
validateUtil.required(message?)           // 必填
validateUtil.email(message?)              // 電子郵件
validateUtil.phoneTW(message?)            // 台灣手機號碼
validateUtil.phoneCN(message?)            // 中國手機號碼
validateUtil.number(message?)             // 數字
validateUtil.positiveInteger(message?)    // 正整數

// 範圍驗證
validateUtil.min(min, message?)           // 最小值
validateUtil.max(max, message?)           // 最大值
validateUtil.minLength(min, message?)     // 最小長度
validateUtil.maxLength(max, message?)     // 最大長度

// 密碼驗證
validateUtil.password(message?)           // 基本密碼（8碼，含英數）
validateUtil.confirmPassword(password, message?)  // 確認密碼
```

#### 工具函式
```typescript
import { 
  validEmail, 
  validPhone, 
  formatMoney, 
  formatDateTime,
  isValidHttpUrl,
  copyText 
} from '@/utils/validate'

// 驗證電子郵件
if (validEmail('test@example.com')) {
  // 有效
}

// 格式化金額
const price = formatMoney(1234567.89, 2)  // "1,234,567.89"

// 格式化日期時間
const date = formatDateTime(new Date())   // "2026/01/10 15:30:00"

// 檢查 HTTP URL
if (isValidHttpUrl('https://example.com')) {
  // 有效的 URL
}

// 複製文字到剪貼簿
await copyText('要複製的內容')
```

### D. 專案路徑別名
```typescript
// vite.config.ts
resolve: {
  alias: {
    '@': fileURLToPath(new URL('./src', import.meta.url))
  }
}
```

使用範例：
```typescript
import { productApi } from '@/api'
import MainLayout from '@/layouts/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { cookieUtil } from '@/utils/cookies'
import { validateUtil } from '@/utils/validate'
import i18n, { t, setLocale } from '@/locale'
```

### E. 環境變數
```bash
# .env.example
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=遇日小舖管理系統
```

使用範例：
```typescript
const apiUrl = import.meta.env.VITE_API_BASE_URL
```

### F. 開發工具推薦

#### VS Code 擴展
- **Volar**: Vue 3 官方 TypeScript 支援
- **ESLint**: 程式碼檢查
- **Prettier**: 程式碼格式化
- **SCSS IntelliSense**: SCSS 自動完成
- **i18n Ally**: i18n 翻譯管理

#### 瀏覽器擴展
- **Vue DevTools**: Vue 3 除錯工具

---

## 更新紀錄

| 版本 | 日期 | 更新內容 |
|-----|------|---------|
| 1.0.0 | 2026-01-10 | 初始版本，完整開發規範建立 |
| 1.1.0 | 2026-01-10 | 新增 i18n 國際化、Cookie 管理、表單驗證工具 |
| 1.2.0 | 2026-01-10 | **完成所有 API 文件的 JSDoc 規範更新（24 個檔案）** |

---

**文檔維護者**: AI Assistant  
**最後更新**: 2026年1月10日  
**專案狀態**: ✅ 活躍開發中

**API 文檔**: 📖 [api_docs.md](../frontend/src/api/api_docs.md) - 完整的 API 模組文檔
