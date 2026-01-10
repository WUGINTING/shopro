# API 文檔

> **專案名稱**: 遇日小舖購物車後台管理系統 API  
> **文檔版本**: 1.0.0  
> **最後更新**: 2026年1月10日

## 📋 目錄

1. [概述](#概述)
2. [模組列表](#模組列表)
3. [通用類型](#通用類型)
4. [商品模組 API](#商品模組-api)
5. [訂單模組 API](#訂單模組-api)
6. [認證模組 API](#認證模組-api)
7. [使用範例](#使用範例)

---

## 概述

本文檔遵循 JSDoc 規範，提供完整的 API 接口說明。所有 API 函式都包含詳細的參數說明、回應類型和使用範例。

### 技術規範

- **TypeScript**: 5.9.0+
- **Axios**: 1.13.2+
- **基礎路徑**: `/api`
- **認證方式**: JWT Bearer Token
- **JSDoc 規範**: 所有 API 函式遵循 JSDoc 註解標準

### 統一回應格式

所有 API 回應都遵循以下格式：

```typescript
interface ApiResponse<T> {
  success: boolean    // 是否成功
  message: string     // 回應訊息
  data: T            // 回應資料
  timestamp: string  // 時間戳記
}
```

---

## 模組列表

| 模組 | 檔案 | 說明 |
|------|------|------|
| **商品管理** | `product.ts` | 商品CRUD、上下架、分類管理 |
| **訂單管理** | `order.ts` | 訂單CRUD、狀態管理 |
| **認證授權** | `auth.ts` | 登入、註冊、個人資料 |
| **客戶關係** | `crm.ts` | 客戶資料、分組、積分 |
| **支付管理** | `payment.ts` | 支付統計、交易記錄、閘道設定 |
| **會員管理** | `member.ts` | 會員CRUD、等級管理 |
| **會員等級** | `memberLevel.ts` | 會員等級設定 |
| **會員群組** | `memberGroup.ts` | 會員分組管理 |
| **營銷活動** | `marketing.ts` | 促銷活動、優惠券 |
| **促銷優惠** | `promotion.ts` | 促銷規則、優惠券 |
| **積點系統** | `point.ts` | 積點增減、歷史記錄 |
| **EDM 管理** | `edm.ts` | 電子報發送、統計 |
| **部落格** | `blog.ts` | 文章CRUD、發佈管理 |
| **相冊管理** | `album.ts` | 相冊、圖片上傳管理 |
| **訂單折扣** | `orderDiscount.ts` | 訂單折扣記錄 |
| **訂單問答** | `orderQA.ts` | 訂單相關問答 |
| **儀表板** | `dashboard.ts` | 統計數據、圖表 |
| **操作日誌** | `operationLog.ts` | 系統操作記錄 |
| **系統設定** | `settings.ts` | 系統配置管理 |
| **統計分析** | `statistics.ts` | 數據分析報表 |
| **使用者管理** | `user.ts` | 使用者CRUD、權限 |

---

## 通用類型

### ApiResponse<T>
通用 API 回應介面

```typescript
interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}
```

### PageResponse<T>
分頁回應介面

```typescript
interface PageResponse<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
  }
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
  empty: boolean
}
```

---

## 商品模組 API

### ProductAPI

商品相關的所有 API 接口，位於 `product.ts`。

#### Interface: Product

```typescript
interface Product {
  id?: number
  name: string
  description: string
  price: number
  stock: number
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'
  // ... 更多屬性
}
```

#### API 方法

所有方法都包含完整的 JSDoc 註解，包括：
- @param - 參數說明
- @returns - 回應類型
- @example - 使用範例
- @throws - 可能的錯誤

詳細請參考 `product.ts` 文件。

---

## 訂單模組 API

### OrderAPI

訂單管理相關接口，位於 `order.ts`。

#### Interface: Order

```typescript
interface Order {
  id?: number
  orderNumber?: string
  totalAmount: number
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  // ... 更多屬性
}
```

詳細請參考 `order.ts` 文件。

---

## 認證模組 API

### AuthAPI

認證授權相關接口，位於 `auth.ts`。

#### Interface: User

```typescript
interface User {
  id?: number
  username: string
  email: string
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER'
  enabled?: boolean
}
```

詳細請參考 `auth.ts` 文件。

---

## 使用範例

### 在 Vue 元件中使用 API

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, type Product } from '@/api'

const $q = useQuasar()
const products = ref<Product[]>([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    products.value = response.data
    $q.notify({ type: 'positive', message: '載入成功' })
  } catch (error) {
    $q.notify({ type: 'negative', message: '載入失敗' })
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProducts()
})
</script>
```

### 錯誤處理範例

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

### TypeScript 類型使用

```typescript
import { productApi, type Product, type ApiResponse } from '@/api'

// 使用類型註解
const products: Product[] = []
const response: ApiResponse<Product[]> = await productApi.getProducts()

// 類型推斷
const product = await productApi.getProduct(123) // 自動推斷為 ApiResponse<Product>
```

---

## JSDoc 規範說明

所有 API 文件都遵循以下 JSDoc 規範：

### 模組註解
```typescript
/**
 * 商品相關 API
 * @module ProductAPI
 */
```

### Interface 註解
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
}
```

### 函式註解
```typescript
/**
 * 獲取商品列表
 * @param {Object} [params] - 查詢參數
 * @param {number} [params.page] - 頁碼
 * @param {number} [params.size] - 每頁數量
 * @returns {Promise<ApiResponse<Product[]>>} 商品列表回應
 * @example
 * const response = await productApi.getProducts({ page: 1, size: 10 })
 */
```

---

## 開發建議

### 1. 使用統一的匯入方式

```typescript
// ✅ 推薦：從統一出口匯入
import { productApi, orderApi, authApi } from '@/api'

// ❌ 不推薦：直接從個別文件匯入
import productApi from '@/api/product'
```

### 2. 使用類型定義

```typescript
// ✅ 推薦：明確的類型定義
const products = ref<Product[]>([])

// ❌ 不推薦：缺少類型定義
const products = ref([])
```

### 3. 錯誤處理

```typescript
// ✅ 推薦：完整的錯誤處理
try {
  const response = await productApi.getProducts()
  // 處理回應
} catch (error) {
  // 錯誤處理
  handleError(error)
}

// ❌ 不推薦：忽略錯誤
const response = await productApi.getProducts()
```

---

## 更新紀錄

| 版本 | 日期 | 更新內容 |
|-----|------|---------|
| 1.0.0 | 2026-01-10 | 初始版本，完整 JSDoc 規範文檔 |

---

**文檔維護者**: Development Team  
**最後更新**: 2026年1月10日  
**遵循規範**: [FRONTEND_DEVELOPMENT_GUIDE.md](../../../Manage_ENV/FRONTEND_DEVELOPMENT_GUIDE.md)
