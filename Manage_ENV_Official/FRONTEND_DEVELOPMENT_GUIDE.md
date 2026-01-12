````markdown
# Frontend 開發規範與指南（官方網站 & 購物車）

> **專案名稱**：遇日小舖官方網站與線上購物車  
> **技術棧**：Vue 3 + JavaScript + Vite + Quasar Framework v2  
> **最後更新**：2026年1月13日

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
10. [開發流程](#開發流程)
11. [購物車功能開發](#購物車功能開發)
12. [常見問題](#常見問題)

---

## 專案概述

### 專案定位
本專案為 **遇日小舖的官方網站與線上購物車前台系統**，包含品牌官網展示與完整的電商購物功能。

### 雙站點架構

本專案包含兩個主要站點：

#### 1. 官方網站 (`frontend-official/src/pages/official`)
展示型網站，包含：
- 🏠 首頁
- 📖 關於我們
- 👨‍🍳 創辦人介紹
- 📋 菜單展示
- 📦 案例展示
- 💬 常見問題
- 📞 聯絡我們

#### 2. 購物車網站 (`frontend-official/src/pages/shop`) **[主要開發區域]**
電商購物功能，包含：
- 🛍️ 商品列表與分類
- 📦 商品詳情頁
- 🔍 商品搜尋
- 🛒 購物車功能
- 💳 結帳流程
- 📱 會員中心
- 📰 最新消息

---

## 技術架構

### 核心技術
```json
{
  "框架": "Vue 3.4.15 (Composition API)",
  "語言": "JavaScript (ES6+)",
  "建構工具": "Vite + Quasar CLI",
  "UI框架": "Quasar Framework 2.14.2",
  "路由": "Vue Router 4.2.5",
  "HTTP客戶端": "Axios 1.13.2",
  "圖標庫": "Iconify Vue 4.1.1",
  "Cookie管理": "js-cookie 3.0.5"
}
```

### 開發環境要求
- **Node.js**：^18 或 ^20 或 ^21
- **包管理器**：npm >= 6.13.4
- **IDE 推薦**：VS Code + Volar 擴展

### 專案啟動
```bash
# 安裝依賴（首次必須執行）
npm install

# 啟動開發伺服器
npm run dev

# 生產建置
npm run build

# 預覽建置
npm run preview

# 清理並重新安裝
npm run clean
npm install
```

---

## 專案結構

```
frontend-official/
├── public/                      # 靜態資源
│   ├── robots.txt              # SEO 機器人檔案
│   ├── sitemap.xml             # 網站地圖
│   └── icons/                  # 網站圖標
│
├── src/
│   ├── api/                     # API 服務層
│   │   ├── product.js          # 商品相關 API（購物車專用）
│   │   ├── account.js          # 帳戶相關 API
│   │   └── cart-apis.md        # 購物車 API 文檔
│   │
│   ├── assets/                 # 靜態資源（圖片、字體等）
│   │
│   ├── components/             # 共用元件
│   │   ├── common/             # 通用元件
│   │   ├── shop/               # 購物車專用元件
│   │   └── official/           # 官網專用元件
│   │
│   ├── config/                 # 配置檔案
│   │   └── api.js              # API 基本配置
│   │
│   ├── css/                    # 全域樣式（SCSS）
│   │   ├── app.scss            # 主樣式檔案
│   │   ├── animate.scss        # 動畫樣式
│   │   └── variables.scss      # SCSS 變數
│   │
│   ├── layouts/                # 佈局模板
│   │   ├── MainLayout.vue      # 主佈局（官網用）
│   │   └── ShopLayout.vue      # 購物車佈局
│   │
│   ├── pages/                  # 頁面元件
│   │   ├── official/           # 官方網站頁面
│   │   │   ├── IndexPage.vue   # 官網首頁
│   │   │   ├── AboutPage.vue   # 關於我們
│   │   │   ├── FounderPage.vue # 創辦人
│   │   │   ├── MenuPage.vue    # 菜單
│   │   │   ├── CasesPage.vue   # 案例
│   │   │   ├── FaqPage.vue     # 常見問題
│   │   │   └── ContactPage.vue # 聯絡我們
│   │   │
│   │   ├── shop/               # 購物車網站頁面 **[主要開發區域]**
│   │   │   ├── IndexPage.vue   # 購物首頁
│   │   │   ├── introduce.vue   # 品牌介紹
│   │   │   ├── news/           # 最新消息
│   │   │   └── product/        # 商品相關頁面
│   │   │       ├── list.vue    # 商品列表
│   │   │       └── detail.vue  # 商品詳情
│   │   │
│   │   └── 404.vue             # 404 錯誤頁面
│   │
│   ├── router/                 # 路由配置
│   │   └── routes.js           # 路由定義
│   │
│   ├── utils/                  # 工具函式
│   │
│   ├── App.vue                 # 根元件
│   └── main.js                 # 應用入口
│
├── quasar.config.js            # Quasar 配置
├── vite.config.js              # Vite 配置
└── package.json                # 依賴管理
```

---

## 開發規範

### 1. JavaScript 使用規範

#### ✅ 強制使用 `<script setup>`

所有 Vue 元件必須使用 Composition API：

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getProducts } from '@/api/product'

const $q = useQuasar()
const products = ref([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getProducts({ page: 0, size: 10 })
    if (response.success) {
      products.value = response.data.content
    }
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

#### ✅ 變數與函式命名
```javascript
// 使用 camelCase
const productList = []
const isLoading = false

// 函式使用動詞開頭
const loadProducts = () => {}
const handleClick = () => {}
const fetchUserData = () => {}

// Boolean 變數使用 is/has 開頭
const isActive = true
const hasError = false
const canSubmit = false
```

#### ✅ 常數命名
```javascript
// 使用 UPPER_SNAKE_CASE
const API_BASE_URL = '/api'
const MAX_ITEMS_PER_PAGE = 20
const DEFAULT_CATEGORY_ID = 1
```

### 2. 命名規範

| 類型 | 規範 | 範例 |
|-----|------|------|
| **元件檔案** | PascalCase + .vue | `ProductList.vue`, `ShoppingCart.vue` |
| **API 檔案** | camelCase + .js | `product.js`, `account.js` |
| **變數/函式** | camelCase | `loadProducts`, `isLoading` |
| **常數** | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_ITEMS` |
| **樣式檔案** | kebab-case + .scss | `app.scss`, `animate.scss` |

### 3. 檔案組織規範

#### 單一職責原則
- 每個 API 檔案只處理一個業務模組
- 每個 Page 對應一個完整頁面
- 每個 Component 只處理一個獨立功能

#### 路徑別名（已配置）
```javascript
// quasar.config.js 中已設定
'@': 'src'
'components': 'src/components'
'layouts': 'src/layouts'
'pages': 'src/pages'
'assets': 'src/assets'
'css': 'src/css'
```

使用範例：
```javascript
import ProductCard from '@/components/shop/ProductCard.vue'
import { getProducts } from '@/api/product'
```

---

## UI 框架使用規範

### Quasar Framework v2 官方文檔
**重要**：開發任何 UI 功能前，必須參考最新官方文檔：  
🔗 **https://quasar.dev/**

### 1. Quasar 元件使用規範

#### ✅ 推薦使用的元件

##### 電商常用元件
```vue
<template>
  <!-- 商品卡片 -->
  <q-card class="product-card">
    <q-img :src="product.image" ratio="1">
      <div class="absolute-bottom text-h6">
        {{ product.name }}
      </div>
    </q-img>
    <q-card-section>
      <div class="text-h6 text-negative">NT$ {{ product.price }}</div>
    </q-card-section>
    <q-card-actions>
      <q-btn 
        flat 
        color="primary" 
        label="加入購物車" 
        icon="shopping_cart"
        @click="addToCart(product)"
      />
    </q-card-actions>
  </q-card>
  
  <!-- 數量選擇器 -->
  <div class="row items-center q-gutter-sm">
    <q-btn 
      flat 
      round 
      dense 
      icon="remove" 
      @click="decreaseQuantity"
    />
    <q-input 
      v-model.number="quantity" 
      type="number" 
      dense 
      outlined 
      style="width: 60px"
    />
    <q-btn 
      flat 
      round 
      dense 
      icon="add" 
      @click="increaseQuantity"
    />
  </div>
  
  <!-- 商品篩選 -->
  <q-select 
    v-model="selectedCategory" 
    :options="categories" 
    label="商品分類"
    outlined
    emit-value
    map-options
  />
  
  <!-- 購物車徽章 -->
  <q-btn flat round dense icon="shopping_cart">
    <q-badge color="red" floating>{{ cartItemCount }}</q-badge>
  </q-btn>
</template>
```

##### 表單元件
```vue
<template>
  <!-- 會員登入表單 -->
  <q-form @submit="handleLogin">
    <q-input 
      v-model="email" 
      label="電子郵件" 
      type="email"
      outlined 
      :rules="[val => !!val || '請輸入電子郵件']"
    />
    
    <q-input 
      v-model="password" 
      label="密碼" 
      type="password"
      outlined 
      :rules="[val => !!val || '請輸入密碼']"
    />
    
    <q-btn 
      type="submit" 
      color="primary" 
      label="登入" 
      class="full-width"
    />
  </q-form>
</template>
```

### 2. 圖標使用規範

專案已整合兩種圖標庫：

#### Material Icons（Quasar 內建）
```vue
<q-icon name="shopping_cart" size="24px" />
<q-icon name="favorite" color="red" />
<q-btn icon="search" label="搜尋" />
```

#### Iconify（更豐富的圖標選擇）
```vue
<script setup>
import { Icon } from '@iconify/vue'
</script>

<template>
  <Icon icon="mdi:cart" width="24" />
  <Icon icon="carbon:user-avatar" color="#1976d2" />
</template>
```

🔗 **圖標搜尋**：https://icon-sets.iconify.design/

---

## 樣式開發規範

### 1. SCSS 全域樣式系統

#### 樣式檔案結構
```
src/css/
├── app.scss                # 主樣式檔案
├── animate.scss            # 動畫效果
└── variables.scss          # SCSS 變數（全域可用）
```

#### variables.scss - 全域變數
專案已配置自動載入，所有 Vue 元件都可直接使用變數：

```scss
// 無需手動 import，可直接使用
<style scoped lang="scss">
.product-card {
  background: $primary-color;  // 直接使用變數
  padding: $spacing-md;
}
</style>
```

### 2. 樣式使用規範

#### ✅ 優先使用 Quasar 工具類別

```vue
<template>
  <!-- 間距 -->
  <div class="q-pa-md">       <!-- padding: 16px -->
  <div class="q-ma-lg">       <!-- margin: 24px -->
  <div class="q-mt-sm">       <!-- margin-top: 8px -->
  
  <!-- 文字 -->
  <div class="text-h4">大標題</div>
  <div class="text-weight-bold">粗體</div>
  <div class="text-primary">主色文字</div>
  <div class="text-center">置中</div>
  
  <!-- 佈局 -->
  <div class="row items-center justify-between">
    <div class="col-6">左側</div>
    <div class="col-6">右側</div>
  </div>
  
  <!-- 背景 -->
  <div class="bg-primary text-white">主色背景</div>
  <div class="bg-grey-1">淺灰背景</div>
</template>
```

#### ✅ 元件專屬樣式使用 `<style scoped>`

```vue
<template>
  <div class="product-grid">
    <div class="product-item">商品</div>
  </div>
</template>

<style scoped lang="scss">
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
  
  .product-item {
    border: 1px solid #eee;
    border-radius: 8px;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
  }
}
</style>
```

#### ❌ 禁止使用內聯 style

```vue
<!-- ❌ 錯誤示範 -->
<div style="color: red; font-size: 16px;">內容</div>

<!-- ✅ 正確示範 -->
<div class="text-negative text-body1">內容</div>
```

### 3. 響應式設計規範

#### Quasar 斷點
```vue
<template>
  <!-- 手機版隱藏，桌面版顯示 -->
  <div class="gt-xs">桌面版內容</div>
  
  <!-- 手機版顯示，桌面版隱藏 -->
  <div class="lt-sm">手機版內容</div>
  
  <!-- 響應式網格 -->
  <div class="row">
    <div class="col-12 col-md-6 col-lg-4">
      響應式欄位
    </div>
  </div>
</template>
```

#### 斷點說明
| 斷點 | 尺寸 | 裝置 |
|-----|------|------|
| xs | < 600px | 手機 |
| sm | 600px - 1024px | 平板 |
| md | 1024px - 1440px | 小桌面 |
| lg | 1440px - 1920px | 桌面 |
| xl | > 1920px | 大桌面 |

---

## API 開發規範

### 📖 完整 API 文檔

**所有 API 相關的詳細文檔、規範、使用範例，請直接參考：**

🔗 **[PRODUCT_PUBLIC_API.md](./PRODUCT_PUBLIC_API.md)** - 商品公開 API 完整文檔（v1.0.0）

該文檔包含完整的：
- ✅ **10 個 API 端點詳細說明** - 商品查詢、分類管理
- ✅ **請求參數與回應範例** - 完整的 JSON 範例
- ✅ **JavaScript 使用範例** - 實際可用的程式碼
- ✅ **資料結構說明** - ProductDTO、ProductCategoryDTO 完整欄位
- ✅ **常見使用情境** - 首頁、分類頁、商品詳情頁、搜尋頁

### 1. API 檔案組織

```
src/api/
├── product.js              # 商品相關 API（參照 PRODUCT_PUBLIC_API.md）
├── account.js              # 帳戶相關 API
└── cart-apis.md            # 購物車 API 文檔
```

### 2. 基本使用方式

#### 引入 API
```javascript
import { 
  getProducts,
  getProductDetail,
  getProductsByCategory,
  searchProducts,
  getEnabledCategories 
} from '@/api/product'
```

#### 在元件中使用
```vue
<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getProducts } from '@/api/product'

const $q = useQuasar()
const products = ref([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  
  try {
    const response = await getProducts({ page: 0, size: 12 })
    
    if (response.data.success) {
      products.value = response.data.data.content
    }
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

### 3. 錯誤處理

```javascript
/**
 * 統一的錯誤處理
 */
const handleApiError = (error, defaultMessage = '操作失敗') => {
  const $q = useQuasar()
  
  let message = defaultMessage
  
  if (error.response) {
    const status = error.response.status
    if (status === 404) {
      message = '商品不存在'
    } else if (status === 500) {
      message = '伺服器錯誤，請稍後再試'
    } else {
      message = error.response.data?.message || message
    }
  } else if (error.request) {
    message = '網路連線異常，請檢查網路連線'
  }
  
  $q.notify({
    type: 'negative',
    message,
    position: 'top',
    timeout: 3000
  })
}
```

### 4. 開發注意事項

- 📖 **詳細的 API 說明、參數、回應格式請參考 PRODUCT_PUBLIC_API.md**
- 🔍 前台建議使用 `getProductsByStatus('ACTIVE')` 只顯示上架商品
- 🔄 搜尋功能需實作防抖（debounce），建議 300ms
- 📄 分頁從 0 開始，不是從 1
- 🖼️ 圖片路徑可能是相對路徑，需組合完整 URL
- 💰 價格顯示優先使用 `salePrice`（銷售價）

---

## 路由與佈局

### 1. 路由結構

```javascript
// src/router/routes.js
const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      // 官方網站頁面
      { 
        path: '', 
        name: 'official-home',
        component: () => import('pages/official/IndexPage.vue') 
      },
      { 
        path: 'about', 
        name: 'about',
        component: () => import('pages/official/AboutPage.vue') 
      },
      { 
        path: 'founder', 
        name: 'founder',
        component: () => import('pages/official/FounderPage.vue') 
      },
      { 
        path: 'menu', 
        name: 'menu',
        component: () => import('pages/official/MenuPage.vue') 
      },
      { 
        path: 'cases', 
        name: 'cases',
        component: () => import('pages/official/CasesPage.vue') 
      },
      { 
        path: 'faq', 
        name: 'faq',
        component: () => import('pages/official/FaqPage.vue') 
      },
      { 
        path: 'contact', 
        name: 'contact',
        component: () => import('pages/official/ContactPage.vue') 
      }
    ]
  },
  {
    path: '/shop',
    component: () => import('layouts/ShopLayout.vue'),
    children: [
      // 購物車網站頁面
      { 
        path: '', 
        name: 'shop-home',
        component: () => import('pages/shop/IndexPage.vue') 
      },
      { 
        path: 'introduce', 
        name: 'shop-introduce',
        component: () => import('pages/shop/introduce.vue') 
      },
      { 
        path: 'products', 
        name: 'shop-products',
        component: () => import('pages/shop/product/list.vue') 
      },
      { 
        path: 'products/:id', 
        name: 'shop-product-detail',
        component: () => import('pages/shop/product/detail.vue') 
      },
      { 
        path: 'cart', 
        name: 'shop-cart',
        component: () => import('pages/shop/cart.vue') 
      },
      { 
        path: 'checkout', 
        name: 'shop-checkout',
        component: () => import('pages/shop/checkout.vue') 
      }
    ]
  },
  // 404 錯誤頁面
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/404.vue')
  }
]

export default routes
```

### 2. 路由導航

```vue
<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 導航到商品詳情頁
const goToProductDetail = (productId) => {
  router.push({ 
    name: 'shop-product-detail', 
    params: { id: productId } 
  })
}

// 導航到購物車
const goToCart = () => {
  router.push({ name: 'shop-cart' })
}

// 導航到分類頁面（帶查詢參數）
const goToCategory = (categoryId) => {
  router.push({ 
    name: 'shop-products',
    query: { category: categoryId }
  })
}
</script>

<template>
  <!-- 使用 router-link -->
  <router-link :to="{ name: 'shop-home' }">
    回到首頁
  </router-link>
  
  <!-- 使用按鈕導航 -->
  <q-btn 
    label="查看詳情" 
    @click="goToProductDetail(product.id)"
  />
</template>
```

---

## 元件開發規範

### 1. 共用元件放置位置

```
src/components/
├── common/                 # 通用元件（兩個站點共用）
│   ├── BaseButton.vue
│   └── BaseInput.vue
│
├── shop/                   # 購物車專用元件
│   ├── ProductCard.vue     # 商品卡片
│   ├── CartItem.vue        # 購物車項目
│   ├── CategoryFilter.vue  # 分類篩選
│   └── QuantitySelector.vue # 數量選擇器
│
└── official/               # 官網專用元件
    ├── HeroSection.vue
    └── FeatureCard.vue
```

### 2. 商品卡片元件範例

```vue
<!-- src/components/shop/ProductCard.vue -->
<script setup>
import { computed } from 'vue'

/**
 * Props 定義
 */
const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  showAddToCart: {
    type: Boolean,
    default: true
  }
})

/**
 * Emits 定義
 */
const emit = defineEmits(['click', 'add-to-cart'])

/**
 * 計算主圖片
 */
const mainImage = computed(() => {
  const primaryImage = props.product.images?.find(img => img.isPrimary)
  return primaryImage?.imageUrl || '/placeholder.jpg'
})

/**
 * 計算折扣百分比
 */
const discountPercent = computed(() => {
  if (!props.product.basePrice || !props.product.salePrice) return 0
  const discount = ((props.product.basePrice - props.product.salePrice) / props.product.basePrice) * 100
  return Math.round(discount)
})

/**
 * 點擊卡片
 */
const handleClick = () => {
  emit('click', props.product)
}

/**
 * 加入購物車
 */
const handleAddToCart = () => {
  emit('add-to-cart', props.product)
}
</script>

<template>
  <q-card 
    class="product-card cursor-pointer" 
    @click="handleClick"
  >
    <!-- 商品圖片 -->
    <q-img 
      :src="mainImage" 
      :ratio="1"
      spinner-color="primary"
    >
      <!-- 折扣標籤 -->
      <div 
        v-if="discountPercent > 0" 
        class="absolute-top-right q-ma-sm"
      >
        <q-badge color="red" :label="`-${discountPercent}%`" />
      </div>
      
      <!-- 標籤 -->
      <div 
        v-if="product.tags?.length" 
        class="absolute-bottom-left q-ma-sm"
      >
        <q-badge 
          v-for="tag in product.tags" 
          :key="tag"
          color="primary" 
          :label="tag"
          class="q-mr-xs"
        />
      </div>
    </q-img>

    <!-- 商品資訊 -->
    <q-card-section>
      <div class="text-h6 text-weight-medium ellipsis-2-lines">
        {{ product.name }}
      </div>
      
      <div class="row items-center q-mt-sm">
        <!-- 售價 -->
        <div class="text-h5 text-negative text-weight-bold">
          NT$ {{ product.salePrice?.toLocaleString() }}
        </div>
        
        <!-- 原價 -->
        <div 
          v-if="product.basePrice > product.salePrice"
          class="text-body2 text-grey-7 text-strike q-ml-sm"
        >
          NT$ {{ product.basePrice?.toLocaleString() }}
        </div>
      </div>
    </q-card-section>

    <!-- 操作按鈕 -->
    <q-card-actions v-if="showAddToCart">
      <q-btn 
        flat 
        color="primary" 
        icon="shopping_cart"
        label="加入購物車"
        class="full-width"
        @click.stop="handleAddToCart"
      />
    </q-card-actions>
  </q-card>
</template>

<style scoped lang="scss">
.product-card {
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }
}

.ellipsis-2-lines {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
```

---

## 開發流程

### 1. 新功能開發流程（以商品列表頁為例）

#### Step 1: 建立頁面元件
```vue
<!-- src/pages/shop/product/list.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProducts, getEnabledCategories } from '@/api/product'
import ProductCard from '@/components/shop/ProductCard.vue'

const route = useRoute()
const products = ref([])
const categories = ref([])
const loading = ref(false)
const pagination = ref({
  page: 0,
  size: 12,
  totalPages: 0
})

/**
 * 載入商品列表
 */
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getProducts(pagination.value)
    if (response.data.success) {
      products.value = response.data.data.content
      pagination.value.totalPages = response.data.data.totalPages
    }
  } catch (error) {
    console.error('載入商品失敗:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 載入分類列表
 */
const loadCategories = async () => {
  try {
    const response = await getEnabledCategories()
    if (response.data.success) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('載入分類失敗:', error)
  }
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<template>
  <q-page class="q-pa-md">
    <div class="container">
      <!-- 頁面標題 -->
      <div class="text-h4 text-weight-bold q-mb-lg">
        商品列表
      </div>

      <!-- 商品網格 -->
      <div class="row q-col-gutter-md">
        <div 
          v-for="product in products" 
          :key="product.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3"
        >
          <ProductCard 
            :product="product"
            @click="goToDetail(product.id)"
            @add-to-cart="addToCart(product)"
          />
        </div>
      </div>

      <!-- 載入中 -->
      <div v-if="loading" class="text-center q-py-xl">
        <q-spinner-dots color="primary" size="50px" />
      </div>

      <!-- 分頁 -->
      <div class="row justify-center q-mt-xl">
        <q-pagination
          v-model="pagination.page"
          :max="pagination.totalPages"
          direction-links
          @update:model-value="loadProducts"
        />
      </div>
    </div>
  </q-page>
</template>

<style scoped lang="scss">
.container {
  max-width: 1440px;
  margin: 0 auto;
}
</style>
```

#### Step 2: 建立相關元件（如需要）
參考「元件開發規範」章節

#### Step 3: 設定路由
參考「路由與佈局」章節

#### Step 4: 測試功能
```bash
npm run dev
```
在瀏覽器中測試：
- 資料載入是否正確
- 錯誤處理是否完善
- RWD 是否正常
- 效能是否良好

---

## 購物車功能開發

### 購物車核心功能清單

#### 1. 商品瀏覽
- ✅ 商品列表（分頁、篩選）
- ✅ 商品詳情頁
- ✅ 商品搜尋
- ✅ 分類瀏覽

#### 2. 購物車管理
- 🛒 加入購物車
- 🛒 購物車清單
- 🛒 修改數量
- 🛒 刪除商品
- 🛒 清空購物車

#### 3. 結帳流程
- 💳 填寫收件資訊
- 💳 選擇付款方式
- 💳 選擇配送方式
- 💳 訂單確認
- 💳 送出訂單

#### 4. 會員功能
- 👤 會員註冊
- 👤 會員登入
- 👤 個人資料管理
- 👤 訂單查詢
- 👤 收藏商品

### 購物車狀態管理建議

建議使用 Pinia 或 Composition API 的 provide/inject 管理購物車狀態。

#### 購物車 Composable 範例
```javascript
// src/composables/useCart.js
import { ref, computed } from 'vue'

const cartItems = ref([])

export function useCart() {
  /**
   * 加入購物車
   */
  const addToCart = (product, quantity = 1) => {
    const existingItem = cartItems.value.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cartItems.value.push({
        ...product,
        quantity
      })
    }
    
    // 儲存到 localStorage
    saveCart()
  }

  /**
   * 更新商品數量
   */
  const updateQuantity = (productId, quantity) => {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      item.quantity = quantity
      saveCart()
    }
  }

  /**
   * 移除商品
   */
  const removeFromCart = (productId) => {
    const index = cartItems.value.findIndex(item => item.id === productId)
    if (index > -1) {
      cartItems.value.splice(index, 1)
      saveCart()
    }
  }

  /**
   * 清空購物車
   */
  const clearCart = () => {
    cartItems.value = []
    saveCart()
  }

  /**
   * 計算總數量
   */
  const totalQuantity = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  /**
   * 計算總金額
   */
  const totalAmount = computed(() => {
    return cartItems.value.reduce((sum, item) => {
      return sum + (item.salePrice * item.quantity)
    }, 0)
  })

  /**
   * 儲存到 localStorage
   */
  const saveCart = () => {
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  /**
   * 從 localStorage 載入
   */
  const loadCart = () => {
    const saved = localStorage.getItem('cart')
    if (saved) {
      cartItems.value = JSON.parse(saved)
    }
  }

  return {
    cartItems,
    totalQuantity,
    totalAmount,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
    loadCart
  }
}
```

---

## 常見問題

### Q1: 如何區分官網和購物車的頁面？

**A**: 依照資料夾結構區分：
- 官網頁面：`src/pages/official/`
- 購物車頁面：`src/pages/shop/`

路由上也有區別：
- 官網：`/about`, `/menu`, `/contact` 等
- 購物車：`/shop/products`, `/shop/cart` 等

### Q2: 圖片資源應該放在哪裡？

**A**: 靜態圖片放在 `src/assets/` 或 `public/` 資料夾

```
src/assets/          # 會被 Vite 處理（建議放小圖片、圖標）
public/              # 不會被處理（建議放大型資源）
```

使用方式：
```vue
<!-- assets 中的圖片 -->
<img src="@/assets/logo.png" />

<!-- public 中的圖片 -->
<img src="/images/banner.jpg" />
```

### Q3: 如何處理商品圖片路徑？

**A**: 後端回傳的圖片路徑可能是相對路徑，需組合完整 URL：

```javascript
const getImageUrl = (path) => {
  if (!path) return '/placeholder.jpg'
  if (path.startsWith('http')) return path
  return `${import.meta.env.VITE_API_BASE_URL}${path}`
}
```

### Q4: 如何實作防抖搜尋？

**A**: 使用 setTimeout 實作防抖：

```javascript
let searchTimer = null

const handleSearch = (keyword) => {
  clearTimeout(searchTimer)
  
  searchTimer = setTimeout(async () => {
    // 執行搜尋
    await searchProducts(keyword)
  }, 300)
}
```

### Q5: Quasar 樣式無法生效？

**A**: 確認以下幾點：
1. 是否正確引入 Quasar CSS
2. 是否使用正確的 class 名稱
3. 是否有 scoped 樣式覆蓋問題

### Q6: 如何處理 RWD？

**A**: 優先使用 Quasar 的響應式工具類別：

```vue
<template>
  <!-- 手機：隱藏，桌面：顯示 -->
  <div class="gt-sm">桌面版內容</div>
  
  <!-- 手機：顯示，桌面：隱藏 -->
  <div class="lt-md">手機版內容</div>
  
  <!-- 響應式網格 -->
  <div class="row">
    <div class="col-12 col-sm-6 col-md-4">
      自動調整寬度
    </div>
  </div>
</template>
```

---

## 附錄

### A. 專案路徑別名（已配置）

```javascript
// quasar.config.js 中已設定
{
  '@': 'src',
  'components': 'src/components',
  'layouts': 'src/layouts',
  'pages': 'src/pages',
  'assets': 'src/assets',
  'css': 'src/css'
}
```

使用範例：
```javascript
import ProductCard from '@/components/shop/ProductCard.vue'
import { getProducts } from '@/api/product'
```

### B. 環境變數

建議建立 `.env` 檔案：
```bash
# .env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=遇日小舖
```

使用方式：
```javascript
const apiUrl = import.meta.env.VITE_API_BASE_URL
```

### C. 開發工具推薦

#### VS Code 擴展
- **Volar**: Vue 3 官方支援
- **ESLint**: 程式碼檢查
- **Prettier**: 程式碼格式化
- **SCSS IntelliSense**: SCSS 自動完成

#### 瀏覽器擴展
- **Vue DevTools**: Vue 3 除錯工具

### D. 快速命令參考

```bash
# 開發伺服器
npm run dev

# 生產建置
npm run build

# 預覽建置
npm run preview

# 清理並重新安裝
npm run clean
npm install
```

---

## 更新紀錄

| 版本 | 日期 | 更新內容 |
|-----|------|---------|
| 1.0.0 | 2026-01-13 | 初始版本，完整開發規範建立 |

---

**文檔維護者**: AI Assistant  
**最後更新**: 2026年1月13日  
**專案狀態**: ✅ 活躍開發中

**相關文檔**:  
- 📖 [PRODUCT_PUBLIC_API.md](./PRODUCT_PUBLIC_API.md) - 商品公開 API 完整文檔
- 📝 [cart-apis.md](../frontend-official/src/api/cart-apis.md) - 購物車 API 文檔

````