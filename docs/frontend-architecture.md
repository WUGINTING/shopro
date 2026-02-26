# 前端架構

## 概述

前端使用 Vue 3 + TypeScript + Quasar Framework 建構，採用 Composition API 風格開發。

## 目錄結構

```
frontend/src/
├── api/                    # API 服務層
│   ├── axios.ts           # Axios 配置
│   ├── index.ts           # 統一匯出
│   ├── types.ts           # 類型定義
│   ├── auth.ts            # 認證 API
│   ├── product.ts         # 商品 API
│   ├── order.ts           # 訂單 API
│   └── ...                # 其他 API 模組
│
├── assets/                # 靜態資源
│   ├── images/
│   └── icons/
│
├── components/            # 共用組件
│   ├── IconPicker.vue
│   └── CouponManagement.vue
│
├── composables/           # 組合式函數
│   ├── useErrorHandler.ts
│   ├── useDebounce.ts
│   └── index.ts
│
├── layouts/               # 佈局組件
│   └── MainLayout.vue
│
├── locale/                # 國際化
│   ├── index.ts
│   └── lang/
│       ├── zh-TW.ts
│       └── en.ts
│
├── router/                # 路由配置
│   └── index.ts
│
├── stores/                # Pinia 狀態管理
│   └── auth.ts
│
├── styles/                # 全局樣式
│   ├── app.scss
│   └── theme-system.scss
│
├── utils/                 # 工具函數
│   ├── validate.ts
│   └── *Tour.ts           # 頁面導覽
│
├── views/                 # 頁面視圖
│   ├── HomeView.vue
│   ├── LoginView.vue
│   ├── ProductView.vue
│   └── ...
│
├── App.vue
└── main.ts
```

---

## 核心模組

### API 服務層

**Axios 配置 (api/axios.ts)**

```typescript
import axios from 'axios'
import { Notify } from 'quasar'

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 請求攔截器 - 自動添加 Token
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 回應攔截器 - 統一錯誤處理
axiosInstance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const { status } = error.response || {}

    if (status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }

    Notify.create({
      type: 'negative',
      message: ERROR_MESSAGES[status] || '請求失敗'
    })

    return Promise.reject(error)
  }
)
```

**API 服務範例 (api/product.ts)**

```typescript
import axios from './axios'
import type { ApiResponse, Product, PageResponse } from './types'

export const productApi = {
  getProducts: (params?: Record<string, any>) =>
    axios.get<ApiResponse<PageResponse<Product>>>('/products', { params }),

  getProduct: (id: number) =>
    axios.get<ApiResponse<Product>>(`/products/${id}`),

  createProduct: (data: Product) =>
    axios.post<ApiResponse<Product>>('/products', data),

  updateProduct: (id: number, data: Product) =>
    axios.put<ApiResponse<Product>>(`/products/${id}`, data),

  deleteProduct: (id: number) =>
    axios.delete<ApiResponse<void>>(`/products/${id}`),

  activateProduct: (id: number) =>
    axios.put<ApiResponse<Product>>(`/products/${id}/activate`),

  deactivateProduct: (id: number) =>
    axios.put<ApiResponse<Product>>(`/products/${id}/deactivate`)
}
```

---

### 狀態管理 (Pinia)

**Auth Store (stores/auth.ts)**

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface User {
  id: number
  username: string
  email: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role)
  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isManager = computed(() => userRole.value === 'MANAGER')

  // Actions
  function initialize() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken) token.value = savedToken
    if (savedUser) user.value = JSON.parse(savedUser)
  }

  function setAuth(newToken: string, newUser: User) {
    token.value = newToken
    user.value = newUser
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 權限檢查
  function hasRole(role: string) {
    return userRole.value === role
  }

  function hasAnyRole(roles: string[]) {
    return roles.includes(userRole.value || '')
  }

  return {
    user, token,
    isAuthenticated, userRole, isAdmin, isManager,
    initialize, setAuth, logout,
    hasRole, hasAnyRole
  }
})
```

---

### 路由配置

**路由結構 (router/index.ts)**

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Loading, Notify } from 'quasar'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        // ... 其他路由
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
})

// 路由守衛
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 顯示載入動畫
  Loading.show({ message: '頁面載入中...' })

  // 認證檢查
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next({ name: 'login' })
  }

  // 角色權限檢查
  const allowedRoles = to.meta.roles as string[]
  if (allowedRoles && !authStore.hasAnyRole(allowedRoles)) {
    Notify.create({ type: 'warning', message: '您沒有權限訪問此頁面' })
    return next({ name: 'home' })
  }

  next()
})

router.afterEach(() => {
  Loading.hide()
})
```

**權限對照表：**

| 路由 | ADMIN | MANAGER | STAFF | CUSTOMER |
|------|:-----:|:-------:|:-----:|:--------:|
| / (首頁) | ✓ | ✓ | ✓ | ✓ |
| /products | ✓ | ✓ | ✓ | ✓ |
| /orders | ✓ | ✓ | ✓ | ✓ |
| /customers | ✓ | ✓ | ✓ | ✗ |
| /statistics | ✓ | ✓ | ✗ | ✗ |
| /system-settings | ✓ | ✗ | ✗ | ✗ |

---

### 組合式函數 (Composables)

**錯誤處理 (composables/useErrorHandler.ts)**

```typescript
import { ref } from 'vue'
import { Notify } from 'quasar'

export function useErrorHandler() {
  const error = ref<string | null>(null)
  const errors = ref<Record<string, string[]>>({})

  const handleError = (err: unknown, customMessage?: string) => {
    const axiosError = err as any
    const message = customMessage ||
      axiosError.response?.data?.message ||
      '操作失敗'

    error.value = message
    Notify.create({ type: 'negative', message })
  }

  const handleSuccess = (message: string) => {
    error.value = null
    Notify.create({ type: 'positive', message })
  }

  const clearError = () => {
    error.value = null
    errors.value = {}
  }

  return { error, errors, handleError, handleSuccess, clearError }
}
```

**防抖函數 (composables/useDebounce.ts)**

```typescript
import { ref, watch, onUnmounted } from 'vue'
import type { Ref } from 'vue'

export function useDebouncedRef<T>(value: Ref<T>, delay = 300): Ref<T> {
  const debouncedValue = ref(value.value) as Ref<T>
  let timeout: ReturnType<typeof setTimeout> | null = null

  watch(value, (newValue) => {
    if (timeout) clearTimeout(timeout)
    timeout = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  onUnmounted(() => {
    if (timeout) clearTimeout(timeout)
  })

  return debouncedValue
}
```

---

### 佈局組件

**主佈局 (layouts/MainLayout.vue)**

```vue
<template>
  <q-layout view="lHh Lpr lFf">
    <!-- 頂部導航 -->
    <q-header elevated>
      <q-toolbar>
        <q-btn flat @click="drawer = !drawer" icon="menu" />
        <q-toolbar-title>Shopro</q-toolbar-title>
        <q-btn flat icon="person" />
      </q-toolbar>
    </q-header>

    <!-- 側邊欄 -->
    <q-drawer v-model="drawer" show-if-above bordered>
      <q-list>
        <q-item
          v-for="link in menuLinks"
          :key="link.path"
          :to="link.path"
          clickable
        >
          <q-item-section avatar>
            <q-icon :name="link.icon" />
          </q-item-section>
          <q-item-section>{{ link.label }}</q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <!-- 主內容 -->
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>
```

---

## 頁面開發範例

**商品管理頁面結構：**

```vue
<template>
  <q-page class="q-pa-md">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h5">商品管理</div>
      <q-btn color="primary" icon="add" label="新增商品" />
    </div>

    <!-- 搜尋篩選 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-4">
            <q-input v-model="searchQuery" placeholder="搜尋商品" />
          </div>
          <div class="col-12 col-sm-4">
            <q-select v-model="statusFilter" :options="statusOptions" />
          </div>
        </div>
      </q-card-section>
    </q-card>

    <!-- 資料表格 -->
    <q-card>
      <q-table
        :rows="products"
        :columns="columns"
        :loading="loading"
        :grid="$q.screen.lt.md"
      >
        <!-- 手機版卡片模式 -->
        <template v-slot:item="props">
          <!-- 響應式卡片 -->
        </template>
      </q-table>
    </q-card>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog" persistent>
      <!-- 表單內容 -->
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product } from '@/api'
import { useErrorHandler } from '@/composables'

const $q = useQuasar()
const { handleError, handleSuccess } = useErrorHandler()

const products = ref<Product[]>([])
const loading = ref(false)
const searchQuery = ref('')

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    products.value = response.data.content
  } catch (error) {
    handleError(error, '載入商品失敗')
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>
```

---

## UI 組件庫

### Quasar 主要組件

| 組件 | 用途 |
|------|------|
| QLayout / QDrawer | 佈局 |
| QTable | 資料表格 |
| QDialog | 對話框 |
| QForm / QInput / QSelect | 表單 |
| QBtn | 按鈕 |
| QCard | 卡片 |
| QTabs | 分頁 |
| Notify | 通知提示 |
| Loading | 載入動畫 |

### 響應式設計

```vue
<!-- 使用 Quasar 響應式類 -->
<div class="row q-col-gutter-md">
  <div class="col-12 col-sm-6 col-md-4">
    <!-- 手機: 100%, 平板: 50%, 桌面: 33% -->
  </div>
</div>

<!-- 手機版使用卡片模式 -->
<q-table :grid="$q.screen.lt.md">
  <template v-slot:item="props">
    <!-- 卡片佈局 -->
  </template>
</q-table>

<!-- 響應式對話框 -->
<q-dialog maximized-on-mobile>
  <q-card :style="$q.screen.lt.md ? 'width: 100%' : 'min-width: 600px'">
    <!-- 內容 -->
  </q-card>
</q-dialog>
```

---

## 建置配置

**vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar } from '@quasar/vite-plugin'

export default defineConfig({
  plugins: [vue(), quasar()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 開發規範

### 組件命名

- 頁面視圖：`*View.vue` (如 ProductView.vue)
- 佈局組件：`*Layout.vue`
- 通用組件：PascalCase (如 IconPicker.vue)

### TypeScript 規範

```typescript
// 明確定義類型
interface Product {
  id: number
  name: string
  price: number
  status: 'DRAFT' | 'ACTIVE' | 'INACTIVE'
}

// 使用 ref 時指定類型
const products = ref<Product[]>([])
const loading = ref(false)
```

### 錯誤處理規範

```typescript
// 統一使用 try-catch
try {
  await productApi.createProduct(data)
  handleSuccess('創建成功')
} catch (error) {
  handleError(error, '創建失敗')
}
```
