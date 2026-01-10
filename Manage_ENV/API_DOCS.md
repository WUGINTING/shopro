# API 文檔

> **專案名稱**: 遇日小舖購物車後台管理系統 API  
> **文檔版本**: 2.1.0  
> **最後更新**: 2026年1月11日  
> **Swagger API 文檔**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📋 目錄

1. [概述](#概述)
2. [API 文檔來源](#api-文檔來源)
3. [模組列表](#模組列表)
4. [通用類型](#通用類型)
5. [重要模組 API 說明](#重要模組-api-說明)
6. [使用範例](#使用範例)
7. [JSDoc 規範說明](#jsdoc-規範說明)

---

## 概述

本文檔遵循 JSDoc 規範，提供完整的 API 接口說明。所有 API 函式都包含詳細的參數說明、回應類型和使用範例。

### 技術規範

- **TypeScript**: 5.9.0+
- **Axios**: 1.13.2+
- **基礎路徑**: `/api`
- **認證方式**: JWT Bearer Token
- **JSDoc 規範**: 所有 API 函式遵循 JSDoc 註解標準
- **API 版本**: 1.0
- **OAS 版本**: 3.1

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

## API 文檔來源

### Swagger UI 文檔

本專案使用 Swagger/OpenAPI 3.1 規範提供完整的 API 文檔：

**🔗 本地開發環境**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

> **注意**: 確保後端服務已啟動在 `localhost:8080` 才能訪問 Swagger UI

### 文檔特色

- ✅ **即時測試**: 可直接在 Swagger UI 中測試 API
- ✅ **完整定義**: 包含所有請求/回應的 Schema 定義
- ✅ **自動更新**: 後端更新時自動同步最新 API
- ✅ **分組管理**: 依功能模組分類（商品、訂單、CRM、支付等）

### 如何使用 Swagger UI

1. 啟動後端服務: `cd E-commerce && mvn spring-boot:run`
2. 開啟瀏覽器訪問: `http://localhost:8080/swagger-ui/index.html`
3. 選擇要測試的 API 端點
4. 點擊「Try it out」進行測試
5. 查看回應內容和 Schema 定義

---

## 模組列表

根據 Swagger 文檔，系統包含以下功能模組：

### 核心業務模組

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **商品管理** | `product.ts` | 商品管理、商品分類、商品規格、商品圖片、商品標籤、商品批次操作 | 商品CRUD、上下架、分類、規格、圖片管理 |
| **訂單管理** | `order.ts` | 訂單管理、訂單歷史、訂單統計、訂單查詢、訂單批次操作、訂單付款、訂單物流、訂單折扣、訂單問與答 | 訂單CRUD、狀態管理、物流、付款、折扣 |
| **支付管理** | `payment.ts` | 支付管理、支付閘道 | 支付統計、交易記錄、閘道設定、LINE PAY、ECPay |
| **儀表板** | `dashboard.ts` | 儀表板 | 統計數據、熱銷商品、最近訂單 |

### 客戶關係管理 (CRM)

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **會員管理** | `member.ts` | 會員管理 | 會員CRUD、等級管理、積點操作 |
| **會員等級** | `memberLevel.ts` | 會員等級管理 | 會員等級設定、升降級規則 |
| **會員群組** | `memberGroup.ts` | 會員群組管理 | 會員分組、群組成員管理 |
| **積點系統** | `point.ts` | 積點管理 | 積點增減、批次發放、歷史記錄 |
| **獎勵制度** | (需建立) | 獎勵制度管理 | 入會禮、生日禮、獎勵設定 |
| **EDM 管理** | `edm.ts` | EDM 電子報管理 | 電子報發送、排程、統計 |
| **部落格** | `blog.ts` | 部落格管理 | 文章CRUD、發佈管理、標籤 |
| **購物車提醒** | (需建立) | 購物車未結帳提醒 | 未結帳提醒、批次發送 |
| **自訂頁面** | (需建立) | 自訂頁面管理 | 自訂頁面CRUD、啟用管理 |

### 營銷與促銷

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **營銷活動** | `marketing.ts` | 營銷活動管理 | 促銷活動、優惠券 |
| **促銷優惠** | `promotion.ts` | (整合至營銷活動) | 促銷規則、優惠券管理 |

### 系統管理

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **使用者管理** | `user.ts` | 帳號管理 | 使用者CRUD、權限管理 |
| **員工管理** | (需建立) | 員工管理 | 員工帳號、權限（上限 50 組） |
| **出勤打卡** | (需建立) | 出勤打卡 | 員工打卡、出勤記錄 |
| **操作日誌** | `operationLog.ts` | 操作日誌 | 系統操作記錄、敏感操作 |
| **系統設定** | `settings.ts` | 系統設定、商店設定、金流設定、物流設定、通知設定 | 系統配置管理 |
| **倉庫管理** | (需建立) | 倉庫管理、庫存管理 | 倉儲管理、庫存警示 |
| **相冊管理** | `album.ts` | 相冊管理 | 相冊、圖片上傳管理 |
| **首頁區塊** | (需建立) | 首頁區塊 | 首頁設計區塊（Silver: 4 個 / Gold: 7 個） |
| **彈跳廣告** | (需建立) | 彈跳廣告 | 首頁彈跳廣告管理 |

### 認證與安全

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **認證授權** | `auth.ts` | 身份驗證 | 登入、註冊、個人資料 |

### 顧客服務

| 模組 | 檔案 | Swagger 標籤 | 說明 |
|------|------|-------------|------|
| **訂單問答** | `orderQA.ts` | 訂單問與答 | 訂單相關溝通、問答管理 |
| **訂單折扣** | `orderDiscount.ts` | 訂單折扣 | 即時折扣操作 |
| **顧客黑名單** | (需建立) | 顧客黑名單 | 封鎖顧客、拒絕交易 |

---

## 通用類型

### ApiResponse<T>
通用 API 回應介面

```typescript
interface ApiResponse<T = any> {
  success: boolean    // 是否成功
  message: string     // 回應訊息
  data: T            // 回應資料
  timestamp: string  // 時間戳記（ISO 8601 格式）
}
```

**使用範例**:
```typescript
const response: ApiResponse<Product> = await productApi.getProduct(123)
if (response.success) {
  console.log(response.data.name)
}
```

### PageResponse<T>
分頁回應介面

```typescript
interface PageResponse<T> {
  content: T[]                // 資料內容
  pageable: {                 // 分頁資訊
    pageNumber: number        // 當前頁碼（從 0 開始）
    pageSize: number          // 每頁數量
  }
  totalElements: number       // 總元素數
  totalPages: number          // 總頁數
  last: boolean               // 是否最後一頁
  first: boolean              // 是否第一頁
  empty: boolean              // 是否空資料
}
```

**使用範例**:
```typescript
const response: ApiResponse<PageResponse<Product>> = await productApi.getProducts({ page: 0, size: 20 })
console.log(`總共 ${response.data.totalElements} 筆資料`)
console.log(`共 ${response.data.totalPages} 頁`)
```

---

## 重要模組 API 說明

### 1. 商品管理 (ProductAPI)

**位置**: `src/api/product.ts`  
**Swagger 標籤**: 商品管理、商品分類、商品規格、商品圖片、商品標籤

#### 主要功能

| API 方法 | HTTP 方法 | 端點 | 說明 |
|---------|----------|------|------|
| `getProducts()` | GET | `/api/products` | 分頁查詢商品 |
| `getProduct(id)` | GET | `/api/products/{id}` | 取得商品詳情 |
| `createProduct(data)` | POST | `/api/products` | 創建商品 |
| `updateProduct(id, data)` | PUT | `/api/products/{id}` | 更新商品 |
| `deleteProduct(id)` | DELETE | `/api/products/{id}` | 刪除商品 |
| `activateProduct(id)` | PUT | `/api/products/{id}/activate` | 上架商品 |
| `deactivateProduct(id)` | PUT | `/api/products/{id}/deactivate` | 下架商品 |
| `addAlbumImages(id, imageIds)` | POST | `/api/products/{id}/album-images` | 從相冊添加圖片 |
| `getCategories()` | GET | `/api/product-categories` | 取得所有分類 |

#### Product Interface

```typescript
interface Product {
  id?: number
  name: string                  // 商品名稱（必填）
  description: string           // 商品描述（必填）
  price: number                 // 商品價格（必填）
  stock: number                 // 庫存數量（必填）
  status?: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED'  // 商品狀態
  categoryId?: number           // 分類 ID
  images?: Array<{ imageUrl: string; albumImageId?: number }>
  specifications?: ProductSpecification[]
}
```

**詳細文檔**: 請參考 `product.ts` 中的 JSDoc 註解和 [Swagger UI](http://localhost:8080/swagger-ui/index.html#/商品管理)

---

### 2. 訂單管理 (OrderAPI)

**位置**: `src/api/order.ts`  
**Swagger 標籤**: 訂單管理、訂單歷史、訂單統計、訂單查詢

#### 主要功能

| API 方法 | HTTP 方法 | 端點 | 說明 |
|---------|----------|------|------|
| `getOrders(params)` | GET | `/api/orders` | 分頁查詢訂單 |
| `getOrder(id)` | GET | `/api/orders/{id}` | 取得訂單詳情 |
| `createOrder(data)` | POST | `/api/orders` | 創建訂單 |
| `updateOrderStatus(id, status)` | PATCH | `/api/orders/{id}/status` | 更新訂單狀態 |
| `cancelOrder(id)` | DELETE | `/api/orders/{id}` | 刪除訂單 |

#### Order Interface

```typescript
interface Order {
  id?: number
  orderNumber?: string          // 訂單編號
  customerId?: number           // 客戶 ID
  customerName?: string         // 客戶名稱
  totalAmount: number           // 訂單總金額（必填）
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  shippingAddress?: string      // 配送地址
  orderItems?: OrderItem[]      // 訂單項目
  createdAt?: string
  updatedAt?: string
}
```

**詳細文檔**: 請參考 `order.ts` 中的 JSDoc 註解和 [Swagger UI](http://localhost:8080/swagger-ui/index.html#/訂單管理)

---

### 3. 支付管理 (PaymentAPI)

**位置**: `src/api/payment.ts`  
**Swagger 標籤**: 支付管理、支付閘道

#### 主要功能

| API 方法 | HTTP 方法 | 端點 | 說明 |
|---------|----------|------|------|
| `getPaymentStatistics()` | GET | `/api/payment-management/statistics` | 取得支付統計 |
| `searchTransactions(params)` | GET | `/api/payment-management/transactions` | 查詢交易記錄 |
| `getTransaction(id)` | GET | `/api/payment-management/transactions/{id}` | 取得交易詳情 |
| `getAllPaymentSettings()` | GET | `/api/payment-management/settings` | 取得所有支付設定 |
| `updatePaymentSetting(setting)` | PUT | `/api/payment-management/settings` | 更新支付設定 |
| `createPayment(gateway, request)` | POST | `/api/payment-gateway/create` | 創建支付請求 |
| `queryPaymentStatus(gateway, txId)` | GET | `/api/payment-gateway/query/{gateway}/{transactionId}` | 查詢支付狀態 |

#### 支援的支付閘道

- **LINE PAY**: 台灣主流行動支付
- **ECPay (綠界)**: 台灣金流服務
- **Manual (手動支付)**: 手動確認支付

**詳細文檔**: 請參考 `payment.ts` 中的 JSDoc 註解和 [Swagger UI](http://localhost:8080/swagger-ui/index.html#/支付管理)

---

### 4. 儀表板 (DashboardAPI)

**位置**: `src/api/dashboard.ts`  
**Swagger 標籤**: 儀表板

#### 主要功能

| API 方法 | HTTP 方法 | 端點 | 說明 |
|---------|----------|------|------|
| `getStats()` | GET | `/api/dashboard/stats` | 取得儀表板統計摘要 |
| `getRecentOrders(limit)` | GET | `/api/dashboard/recent-orders` | 取得最近訂單 |
| `getTopProducts(limit)` | GET | `/api/dashboard/top-products` | 取得熱銷商品 |
| `getTodayStatistics()` | GET | `/api/orders/statistics/today` | 取得今日訂單統計 |
| `getWeekStatistics()` | GET | `/api/orders/statistics/week` | 取得本週訂單統計 |
| `getMonthStatistics()` | GET | `/api/orders/statistics/month` | 取得本月訂單統計 |

**詳細文檔**: 請參考 `dashboard.ts` 中的 JSDoc 註解和 [Swagger UI](http://localhost:8080/swagger-ui/index.html#/儀表板)

---

### 5. 認證管理 (AuthAPI)

**位置**: `src/api/auth.ts`  
**Swagger 標籤**: 身份驗證

#### 主要功能

| API 方法 | HTTP 方法 | 端點 | 說明 |
|---------|----------|------|------|
| `login(data)` | POST | `/api/auth/login` | 使用者登入 |
| `register(data)` | POST | `/api/auth/register` | 使用者註冊 |
| `getProfile()` | GET | `/api/auth/profile` | 取得個人資料 |
| `updateProfile(data)` | PUT | `/api/auth/profile` | 更新個人資料 |

**詳細文檔**: 請參考 `auth.ts` 中的 JSDoc 註解和 [Swagger UI](http://localhost:8080/swagger-ui/index.html#/身份驗證)

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

### 模組註解（必須）
```typescript
/**
 * 商品相關 API
 * @module ProductAPI
 */
```

### Interface 註解（必須）
```typescript
/**
 * 商品介面
 * @interface Product
 * @property {number} [id] - 商品 ID（可選，創建時不需要）
 * @property {string} name - 商品名稱（必填）
 * @property {number} price - 商品價格（必填）
 */
export interface Product {
  /** 商品 ID */
  id?: number
  /** 商品名稱 */
  name: string
  /** 商品價格 */
  price: number
}
```

### API 函式註解（必須包含）
```typescript
/**
 * 獲取商品列表
 * @description 支援分頁查詢商品列表，可依狀態篩選
 * @param {Object} [params] - 查詢參數
 * @param {number} [params.page] - 頁碼（從 0 開始）
 * @param {number} [params.size] - 每頁數量（預設 20）
 * @param {string} [params.status] - 商品狀態篩選
 * @returns {Promise<ApiResponse<PageResponse<Product>>>} 商品列表回應
 * @swagger GET /api/products
 * @example
 * const response = await productApi.getProducts({ page: 0, size: 10 })
 * console.log(response.data.content) // 商品陣列
 */
getProducts: (params?: any) => {
  return axios.get<any, ApiResponse<PageResponse<Product>>>('/products', { params })
}
```

### JSDoc 標籤說明

| 標籤 | 用途 | 必須 |
|-----|------|------|
| `@module` | 模組名稱 | ✅ |
| `@namespace` | 命名空間（用於 API 物件） | ✅ |
| `@interface` | 介面定義 | ✅ |
| `@description` | 詳細描述 | 推薦 |
| `@param` | 參數說明 | ✅ |
| `@returns` | 回應類型 | ✅ |
| `@swagger` | Swagger 端點路徑 | 推薦 |
| `@example` | 使用範例 | 推薦 |
| `@throws` | 可能的錯誤 | 推薦 |
| `@warning` | 警告訊息 | 選用 |
| `@deprecated` | 已棄用標記 | 選用 |

### 完整範例

```typescript
/**
 * 商品相關 API
 * @module ProductAPI
 */

/**
 * 商品介面
 * @interface Product
 */
export interface Product {
  /** 商品 ID */
  id?: number
  /** 商品名稱 */
  name: string
  /** 商品價格 */
  price: number
}

/**
 * 商品 API 服務
 * @namespace productApi
 */
export const productApi = {
  /**
   * 創建商品
   * @description 創建新商品，預設狀態為 DRAFT
   * @param {Product} data - 商品資料
   * @param {string} data.name - 商品名稱（必填）
   * @param {number} data.price - 商品價格（必填）
   * @returns {Promise<ApiResponse<Product>>} 創建成功的商品資料
   * @swagger POST /api/products
   * @example
   * const newProduct = await productApi.createProduct({
   *   name: '新商品',
   *   price: 100
   * })
   * console.log(newProduct.data.id) // 新建商品的 ID
   */
  createProduct: (data: Product) => {
    return axios.post<any, ApiResponse<Product>>('/products', data)
  }
}
```

---

## 開發建議

### 1. 使用統一的匯入方式

```typescript
// ✅ 推薦：從統一出口匯入
import { productApi, orderApi, authApi, type Product, type Order } from '@/api'

// ❌ 不推薦：直接從個別文件匯入
import productApi from '@/api/product'
```

### 2. 使用類型定義

```typescript
// ✅ 推薦：明確的類型定義
const products = ref<Product[]>([])
const response: ApiResponse<Product> = await productApi.getProduct(123)

// ❌ 不推薦：缺少類型定義
const products = ref([])
const response = await productApi.getProduct(123)
```

### 3. 完整的錯誤處理

```typescript
// ✅ 推薦：完整的錯誤處理
try {
  const response = await productApi.getProducts()
  // 處理回應
} catch (error: any) {
  if (error.response?.status === 401) {
    // 未授權，跳轉登入
    router.push('/login')
  } else if (error.response?.status === 404) {
    // 資源不存在
    showNotFound()
  } else {
    // 其他錯誤
    console.error('操作失敗:', error)
    showError('操作失敗')
  }
}

// ❌ 不推薦：忽略錯誤
const response = await productApi.getProducts()
```

### 4. 使用 Swagger UI 進行測試

開發時建議：
1. 先在 [Swagger UI](http://localhost:8080/swagger-ui/index.html) 測試 API
2. 確認請求/回應格式正確
3. 再在前端程式碼中實作

### 5. 查閱最新 API 文檔

- **始終以 Swagger 文檔為準**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 後端 API 更新時，Swagger 會自動同步
- 前端 API 文件若與 Swagger 不一致，請以 Swagger 為準並更新前端程式碼

---

## 常見問題

### Q1: 如何知道 API 的最新變更？

**答**: 訪問 [Swagger UI](http://localhost:8080/swagger-ui/index.html) 查看最新的 API 定義。Swagger 會即時反映後端的變更。

### Q2: API 回應格式不一致怎麼辦？

**答**: 檢查 Swagger UI 中的 Schema 定義，確保前端的 Interface 定義與後端一致。

### Q3: 如何測試 API？

**答**: 可以使用以下方式：
1. **Swagger UI**: 直接在瀏覽器中測試
2. **Postman**: 匯入 OpenAPI 定義（可從 Swagger 下載）
3. **VS Code REST Client**: 使用 REST Client 擴展測試

### Q4: JSDoc 註解有什麼用？

**答**: JSDoc 註解提供：
- IDE 自動完成和類型提示
- 函式懸浮說明
- 自動生成 API 文檔
- 團隊協作時的程式碼可讀性

### Q5: 如何更新 API 文件？

**答**: 
1. 查看 Swagger UI 了解最新 API
2. 更新對應的 TypeScript 文件（如 `product.ts`）
3. 更新 Interface 定義
4. 更新 JSDoc 註解
5. 在 `@swagger` 標籤中註明對應的端點

---

## 更新紀錄

| 版本 | 日期 | 更新內容 |
|-----|------|---------|
| 1.0.0 | 2026-01-10 | 初始版本，完整 JSDoc 規範文檔 |
| 2.0.0 | 2026-01-10 | **重大更新**：整合 Swagger API 文檔、新增 Swagger UI 連結、更新所有 API 的 JSDoc 規範以符合後端定義、新增完整的模組列表和使用指南 |
| 2.1.0 | 2026-01-11 | **JSDoc 完整性更新**：依據附錄對照表完成13個API文件的JSDoc更新（訂單管理：orderDiscount.ts、orderQA.ts；會員管理：user.ts、member.ts、memberLevel.ts、memberGroup.ts、point.ts；行銷管理：edm.ts、blog.ts、marketing.ts；系統管理：operationLog.ts、settings.ts；其他：album.ts），所有函式新增完整的@namespace、@description、@param、@returns、@swagger、@example 標籤 |

---

**文檔維護者**: Development Team  
**最後更新**: 2026年1月11日  
**遵循規範**: [FRONTEND_DEVELOPMENT_GUIDE.md](../../../Manage_ENV/FRONTEND_DEVELOPMENT_GUIDE.md)  
**後端 API 文檔**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

## 附錄：Swagger API 完整模組對照表

| Swagger 標籤 | 前端 API 文件 | 狀態 | 備註 |
|-------------|-------------|------|------|
| 商品管理 | `product.ts` | ✅ 已更新 | 已整合 JSDoc |
| 商品分類 | `product.ts` | ✅ 已更新 | 整合至 product.ts |
| 商品規格 | `product.ts` | ✅ 已更新 | 整合至 product.ts |
| 商品圖片 | `product.ts` | ✅ 已更新 | 整合至 product.ts |
| 商品標籤 | `product.ts` | ✅ 已更新 | 整合至 product.ts |
| 商品批次操作 | `product.ts` | ⚠️ 待補充 | 需新增批次操作方法 |
| 訂單管理 | `order.ts` | ✅ 已更新 | 已整合 JSDoc |
| 訂單歷史 | `order.ts` | ⚠️ 待補充 | 需新增歷史查詢方法 |
| 訂單統計 | `dashboard.ts` | ✅ 已更新 | 整合至 dashboard.ts |
| 訂單查詢 | `order.ts` | ⚠️ 待補充 | 需新增多條件查詢 |
| 訂單批次操作 | `order.ts` | ⚠️ 待補充 | 需新增批次操作方法 |
| 訂單付款 | `payment.ts` | ✅ 已更新 | 整合至 payment.ts |
| 訂單物流 | (需建立) | ❌ 未建立 | 需建立 orderShipment.ts |
| 訂單折扣 | `orderDiscount.ts` | ✅ 已更新 | 已整合 JSDoc |
| 訂單問與答 | `orderQA.ts` | ✅ 已更新 | 已整合 JSDoc |
| 支付管理 | `payment.ts` | ✅ 已更新 | 已整合 JSDoc |
| 支付閘道 | `payment.ts` | ✅ 已更新 | 整合至 payment.ts |
| 儀表板 | `dashboard.ts` | ✅ 已更新 | 已整合 JSDoc |
| 身份驗證 | `auth.ts` | ✅ 已更新 | 已整合 JSDoc |
| 帳號管理 | `user.ts` | ✅ 已更新 | 已整合 JSDoc |
| 員工管理 | (需建立) | ❌ 未建立 | 需建立 staff.ts |
| 出勤打卡 | (需建立) | ❌ 未建立 | 需建立 attendance.ts |
| 會員管理 | `member.ts` | ✅ 已更新 | 已整合 JSDoc |
| 會員等級管理 | `memberLevel.ts` | ✅ 已更新 | 已整合 JSDoc |
| 會員群組管理 | `memberGroup.ts` | ✅ 已更新 | 已整合 JSDoc |
| 積點管理 | `point.ts` | ✅ 已更新 | 已整合 JSDoc |
| 獎勵制度管理 | (需建立) | ❌ 未建立 | 需建立 reward.ts |
| EDM 電子報管理 | `edm.ts` | ✅ 已更新 | 已整合 JSDoc |
| 部落格管理 | `blog.ts` | ✅ 已更新 | 已整合 JSDoc |
| 購物車未結帳提醒 | (需建立) | ❌ 未建立 | 需建立 cartReminder.ts |
| 自訂頁面管理 | (需建立) | ❌ 未建立 | 需建立 customPage.ts |
| 營銷活動管理 | `marketing.ts` | ✅ 已更新 | 已整合 JSDoc |
| 相冊管理 | `album.ts` | ✅ 已更新 | 已整合 JSDoc |
| 倉庫管理 | (需建立) | ❌ 未建立 | 需建立 warehouse.ts |
| 庫存管理 | (需建立) | ❌ 未建立 | 需建立 inventory.ts |
| 首頁區塊 | (需建立) | ❌ 未建立 | 需建立 homepageBlock.ts |
| 彈跳廣告 | (需建立) | ❌ 未建立 | 需建立 popupAd.ts |
| 系統設定 | `settings.ts` | ✅ 已更新 | 已整合 JSDoc |
| 商店設定 | `settings.ts` | ✅ 已更新 | 整合至 settings.ts |
| 金流設定 | `settings.ts` | ✅ 已更新 | 整合至 settings.ts |
| 物流設定 | `settings.ts` | ✅ 已更新 | 整合至 settings.ts |
| 通知設定 | `settings.ts` | ✅ 已更新 | 整合至 settings.ts |
| 操作日誌 | `operationLog.ts` | ✅ 已更新 | 已整合 JSDoc |
| 顧客黑名單 | (需建立) | ❌ 未建立 | 需建立 blacklist.ts |

**圖例**:
- ✅ 已更新：已整合 Swagger 定義和 JSDoc 規範
- ⚠️ 待更新：文件存在但需更新 JSDoc
- ❌ 未建立：需要新建對應的 API 文件

---

## API 文件更新紀錄

### 2026年1月11日更新

已完成以下 API 文件的 JSDoc 註解更新：

✅ **核心業務模組**
- `orderDiscount.ts` - 訂單折扣 API，添加完整 JSDoc
- `orderQA.ts` - 訂單問答 API，添加完整 JSDoc

✅ **系統管理模組**
- `user.ts` - 使用者管理 API，添加完整 JSDoc
- `operationLog.ts` - 操作日誌 API，添加完整 JSDoc
- `settings.ts` - 系統設定 API，添加完整 JSDoc

✅ **CRM 模組**
- `member.ts` - 會員管理 API，添加完整 JSDoc
- `memberLevel.ts` - 會員等級 API，添加完整 JSDoc
- `memberGroup.ts` - 會員群組 API，添加完整 JSDoc
- `point.ts` - 積點管理 API，添加完整 JSDoc
- `edm.ts` - EDM 電子報 API，添加完整 JSDoc
- `blog.ts` - 部落格 API，添加完整 JSDoc

✅ **營銷模組**
- `marketing.ts` - 營銷活動 API，添加完整 JSDoc

✅ **媒體管理**
- `album.ts` - 相冊管理 API（原本已有完整 JSDoc）

所有更新的 API 文件均包含：
- ✅ `@namespace` 或 `@module` 標籤
- ✅ `@description` 詳細描述
- ✅ `@param` 參數說明（包含類型和是否必填）
- ✅ `@returns` 回應類型說明
- ✅ `@swagger` Swagger 端點路徑
- ✅ `@example` 使用範例程式碼

### 統計資訊

- **已更新**: 13 個 API 文件
- **無需更新**: 5 個已完成（product.ts, order.ts, payment.ts, dashboard.ts, auth.ts）
- **未建立**: 保持原狀（需後續開發）

---

### 2026年1月10日更新

已完成以下 API 文件的 JSDoc 註解更新：

✅ **核心業務模組**
- `orderDiscount.ts` - 訂單折扣 API，添加完整 JSDoc
- `orderQA.ts` - 訂單問答 API，添加完整 JSDoc

✅ **系統管理模組**
- `user.ts` - 使用者管理 API，添加完整 JSDoc
- `operationLog.ts` - 操作日誌 API，添加完整 JSDoc

✅ **CRM 模組**
- `member.ts` - 會員管理 API，添加完整 JSDoc
- `memberLevel.ts` - 會員等級 API，添加完整 JSDoc
- `memberGroup.ts` - 會員群組 API，添加完整 JSDoc
- `point.ts` - 積點管理 API，添加完整 JSDoc
- `edm.ts` - EDM 電子報 API，添加完整 JSDoc
- `blog.ts` - 部落格 API，添加完整 JSDoc

✅ **營銷模組**
- `marketing.ts` - 營銷活動 API，添加完整 JSDoc

所有更新的 API 文件均包含：
- ✅ `@namespace` 或 `@module` 標籤
- ✅ `@description` 詳細描述
- ✅ `@param` 參數說明（包含類型和是否必填）
- ✅ `@returns` 回應類型說明
- ✅ `@swagger` Swagger 端點路徑
- ✅ `@example` 使用範例程式碼
