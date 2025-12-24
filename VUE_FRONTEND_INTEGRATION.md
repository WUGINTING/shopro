# Vue 前端整合說明文檔

## 概述

本文檔說明 Vue.js 前端應用程式如何與後端 Spring Boot API 整合，解決了前端無法正確調用後端 API 的問題。

## 問題分析

原始問題包括：
1. Vue 組件無法正常調用後端 API
2. 前端傳遞的參數與後端預期的參數格式不符
3. API 回應後，前端未能正確處理並渲染回應數據

## 解決方案

### 1. 創建完整的 Vue.js 前端應用程式

使用 Vue 3 + TypeScript + Vite 創建了一個現代化的前端應用程式，位於 `frontend/` 目錄中。

**技術棧：**
- Vue 3 (Composition API)
- TypeScript (類型安全)
- Pinia (狀態管理)
- Vue Router (路由管理)
- Axios (HTTP 客戶端)
- Vite (建構工具)

### 2. API 服務層設計

#### 2.1 API 客戶端 (`src/api/client.ts`)

創建了統一的 Axios 客戶端配置，包含：

```typescript
// 基礎配置
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 請求攔截器 - 添加認證 token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 響應攔截器 - 統一錯誤處理
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    // 處理 401, 403, 404, 500 等錯誤
    return Promise.reject(error)
  }
)
```

#### 2.2 類型定義 (`src/api/types.ts`)

定義了與後端 DTO 完全匹配的 TypeScript 類型：

```typescript
// 會員類型
export interface Member {
  id?: number
  name: string
  email: string
  phone?: string
  birthday?: string
  gender?: 'M' | 'F' | 'OTHER'
  status?: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  // ... 其他欄位
}

// 商品類型
export interface Product {
  id?: number
  name: string
  sku: string
  categoryId?: number
  status?: 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'
  basePrice: number
  // ... 其他欄位
}

// 訂單類型
export interface Order {
  id?: number
  customerId: number
  status?: 'PENDING_PAYMENT' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED'
  totalAmount: number
  // ... 其他欄位
}
```

#### 2.3 API 服務模組

為每個後端模組創建了對應的 API 服務：

**CRM API (`src/api/crm.ts`)**
```typescript
export const memberApi = {
  create: (data: Member) => apiClient.post('/api/crm/members', data),
  update: (id: number, data: Member) => apiClient.put(`/api/crm/members/${id}`, data),
  get: (id: number) => apiClient.get(`/api/crm/members/${id}`),
  delete: (id: number) => apiClient.delete(`/api/crm/members/${id}`),
  list: (page = 0, size = 20) => apiClient.get('/api/crm/members', { params: { page, size } }),
  getByEmail: (email: string) => apiClient.get(`/api/crm/members/email/${email}`)
}
```

**Product API (`src/api/product.ts`)**
```typescript
export const productApi = {
  create: (data: Product) => apiClient.post('/api/products', data),
  update: (id: number, data: Product) => apiClient.put(`/api/products/${id}`, data),
  get: (id: number) => apiClient.get(`/api/products/${id}`),
  delete: (id: number) => apiClient.delete(`/api/products/${id}`),
  list: (page = 0, size = 20) => apiClient.get('/api/products', { params: { page, size } }),
  listByCategory: (categoryId: number, page = 0, size = 20) => 
    apiClient.get(`/api/products/category/${categoryId}`, { params: { page, size } })
}
```

**Order API (`src/api/order.ts`)**
```typescript
export const orderApi = {
  create: (data: Order) => apiClient.post('/api/orders', data),
  update: (id: number, data: Order) => apiClient.put(`/api/orders/${id}`, data),
  get: (id: number) => apiClient.get(`/api/orders/${id}`),
  delete: (id: number) => apiClient.delete(`/api/orders/${id}`),
  list: (page = 0, size = 20) => apiClient.get('/api/orders', { params: { page, size } }),
  listByStatus: (status: string, page = 0, size = 20) =>
    apiClient.get(`/api/orders/status/${status}`, { params: { page, size } }),
  listByCustomer: (customerId: number, page = 0, size = 20) =>
    apiClient.get(`/api/orders/customer/${customerId}`, { params: { page, size } })
}
```

### 3. 狀態管理 (Pinia Stores)

為每個模組創建了 Pinia store 來管理狀態：

#### 3.1 CRM Store (`src/stores/modules/crm.ts`)

```typescript
export const useCrmStore = defineStore('crm', () => {
  const members = ref<Member[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  async function fetchMembers(page = 0, size = 20) {
    loading.value = true
    try {
      const response = await memberApi.list(page, size)
      members.value = response.data.content
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }
  
  return { members, loading, error, fetchMembers }
})
```

### 4. Vue 組件

創建了完整的 CRUD 組件：

#### 4.1 會員管理組件 (`src/views/crm/MemberManagement.vue`)

功能：
- ✅ 分頁顯示會員列表
- ✅ 搜尋會員（依電子郵件）
- ✅ 新增會員（含表單驗證）
- ✅ 編輯會員
- ✅ 刪除會員（含確認對話框）
- ✅ 錯誤處理與使用者提示

#### 4.2 商品管理組件 (`src/views/product/ProductManagement.vue`)

功能：
- ✅ 網格佈局顯示商品
- ✅ 依分類篩選商品
- ✅ 依狀態篩選商品
- ✅ 新增商品（含分類選擇）
- ✅ 編輯商品
- ✅ 刪除商品
- ✅ 分頁導航

#### 4.3 訂單管理組件 (`src/views/order/OrderManagement.vue`)

功能：
- ✅ 表格顯示訂單
- ✅ 依狀態篩選訂單
- ✅ 依客戶 ID 查詢訂單
- ✅ 新增訂單
- ✅ 查看訂單詳情（對話框）
- ✅ 刪除訂單
- ✅ 分頁導航

## API 端點對應表

### CRM 模組

| 前端功能 | API 端點 | HTTP 方法 | 參數 | 回應格式 |
|---------|---------|----------|------|---------|
| 獲取會員列表 | `/api/crm/members` | GET | `page`, `size` | `ApiResponse<PageResponse<Member>>` |
| 創建會員 | `/api/crm/members` | POST | `Member` body | `ApiResponse<Member>` |
| 更新會員 | `/api/crm/members/{id}` | PUT | `id` path, `Member` body | `ApiResponse<Member>` |
| 刪除會員 | `/api/crm/members/{id}` | DELETE | `id` path | `ApiResponse<void>` |
| 依電子郵件查詢 | `/api/crm/members/email/{email}` | GET | `email` path | `ApiResponse<Member>` |

### Product 模組

| 前端功能 | API 端點 | HTTP 方法 | 參數 | 回應格式 |
|---------|---------|----------|------|---------|
| 獲取商品列表 | `/api/products` | GET | `page`, `size` | `ApiResponse<PageResponse<Product>>` |
| 創建商品 | `/api/products` | POST | `Product` body | `ApiResponse<Product>` |
| 更新商品 | `/api/products/{id}` | PUT | `id` path, `Product` body | `ApiResponse<Product>` |
| 刪除商品 | `/api/products/{id}` | DELETE | `id` path | `ApiResponse<void>` |
| 依分類查詢 | `/api/products/category/{categoryId}` | GET | `categoryId` path, `page`, `size` | `ApiResponse<PageResponse<Product>>` |
| 依狀態查詢 | `/api/products/status/{status}` | GET | `status` path, `page`, `size` | `ApiResponse<PageResponse<Product>>` |
| 獲取分類列表 | `/api/product-categories` | GET | - | `ApiResponse<List<ProductCategory>>` |

### Order 模組

| 前端功能 | API 端點 | HTTP 方法 | 參數 | 回應格式 |
|---------|---------|----------|------|---------|
| 獲取訂單列表 | `/api/orders` | GET | `page`, `size` | `ApiResponse<PageResponse<Order>>` |
| 創建訂單 | `/api/orders` | POST | `Order` body | `ApiResponse<Order>` |
| 更新訂單 | `/api/orders/{id}` | PUT | `id` path, `Order` body | `ApiResponse<Order>` |
| 刪除訂單 | `/api/orders/{id}` | DELETE | `id` path | `ApiResponse<void>` |
| 依狀態查詢 | `/api/orders/status/{status}` | GET | `status` path, `page`, `size` | `ApiResponse<PageResponse<Order>>` |
| 依客戶查詢 | `/api/orders/customer/{customerId}` | GET | `customerId` path, `page`, `size` | `ApiResponse<PageResponse<Order>>` |

## 修正的關鍵問題

### 1. 參數類型匹配

**問題**: 前端傳遞的參數類型與後端不符
**解決**: 
- 使用 TypeScript 定義與後端 DTO 完全匹配的介面
- 使用 `v-model.number` 確保數字類型的輸入
- 分頁參數使用正確的類型 (number)

```vue
<!-- 正確的數字輸入 -->
<input v-model.number="productForm.basePrice" type="number" required />

<!-- 正確的分頁請求 -->
await productApi.list(0, 20)  // page=0, size=20
```

### 2. 分頁參數

**問題**: 分頁參數不正確
**解決**: 
- Spring Data 分頁從 0 開始，而非 1
- 正確使用 `page` 和 `size` 參數名稱

```typescript
// 正確的分頁請求
function fetchMembers(page = 0, size = 20) {
  return apiClient.get('/api/crm/members', {
    params: { page, size }  // ?page=0&size=20
  })
}
```

### 3. 狀態枚舉值

**問題**: 狀態值與後端枚舉不匹配
**解決**: 
- 使用與後端完全一致的枚舉值
- TypeScript 類型限制確保只能使用有效值

```typescript
// 會員狀態
type MemberStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'

// 商品狀態
type ProductStatus = 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'

// 訂單狀態
type OrderStatus = 'PENDING_PAYMENT' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED'
```

### 4. API 回應處理

**問題**: 前端未正確處理 API 回應
**解決**: 
- Axios 攔截器自動提取 `response.data`
- 正確處理 `ApiResponse<T>` 包裝格式
- 正確處理分頁回應 `PageResponse<T>`

```typescript
// 響應攔截器自動提取 data
apiClient.interceptors.response.use(
  (response) => response.data,  // 返回 response.data
  (error) => Promise.reject(error)
)

// Store 中正確處理回應
async function fetchMembers(page = 0, size = 20) {
  const response = await memberApi.list(page, size) as any
  if (response.success && response.data) {
    members.value = response.data.content      // 提取分頁內容
    pagination.value.totalElements = response.data.totalElements
    pagination.value.totalPages = response.data.totalPages
  }
}
```

### 5. 錯誤處理

**問題**: 缺少適當的錯誤處理
**解決**: 
- 統一的錯誤攔截器
- Store 中的 error 狀態
- 使用者友好的錯誤訊息

```typescript
// 錯誤攔截器
apiClient.interceptors.response.use(
  (response) => response.data,
  (error: AxiosError) => {
    if (error.response) {
      switch (error.response.status) {
        case 401: console.error('未授權'); break
        case 403: console.error('無權限'); break
        case 404: console.error('資源不存在'); break
        case 500: console.error('伺服器錯誤'); break
      }
    }
    return Promise.reject(error)
  }
)

// Store 中的錯誤處理
try {
  await memberApi.create(memberData)
} catch (e: any) {
  error.value = e.message || '創建會員失敗'
  throw e
}
```

## 運行說明

### 1. 啟動後端服務

```bash
cd E-commerce
mvn spring-boot:run
```

後端服務將在 `http://localhost:8080` 啟動。

### 2. 啟動前端服務

```bash
cd frontend
npm install
npm run dev
```

前端服務將在 `http://localhost:5173` 啟動。

### 3. 訪問應用程式

開啟瀏覽器，訪問 `http://localhost:5173`

可用的路由：
- `/` - 首頁
- `/crm/members` - 會員管理
- `/products` - 商品管理
- `/orders` - 訂單管理

## 測試建議

### 1. 使用 Swagger UI 測試後端 API

訪問 `http://localhost:8080/swagger-ui.html` 來測試後端 API。

### 2. 使用瀏覽器開發工具

- 開啟 Network 標籤查看 API 請求和響應
- 查看 Console 標籤檢查錯誤訊息
- 使用 Vue DevTools 查看組件狀態

### 3. 測試流程

1. **會員管理測試**
   - 新增會員
   - 查詢會員列表
   - 搜尋會員（依電子郵件）
   - 編輯會員
   - 刪除會員

2. **商品管理測試**
   - 新增商品
   - 查詢商品列表
   - 依分類篩選
   - 依狀態篩選
   - 編輯商品
   - 刪除商品

3. **訂單管理測試**
   - 新增訂單
   - 查詢訂單列表
   - 依狀態篩選
   - 依客戶查詢
   - 查看訂單詳情
   - 刪除訂單

## 未來擴展

可以進一步實現以下功能：

1. **CRM 模組**
   - 會員等級管理組件
   - 積點管理組件
   - EDM 電子報管理組件
   - 部落格管理組件

2. **Product 模組**
   - 商品標籤管理組件
   - 商品圖片上傳
   - 商品規格管理
   - 庫存管理

3. **Order 模組**
   - 訂單統計圖表
   - 批次操作介面
   - 訂單導出功能
   - 訂單折扣管理

4. **通用功能**
   - 使用者認證與授權
   - 權限管理
   - 多語言支援
   - 主題切換
   - 更多的資料視覺化

## 總結

透過創建完整的 Vue.js 前端應用程式，我們成功解決了以下問題：

1. ✅ 前端組件現在能夠正確調用後端 API
2. ✅ 參數類型與格式完全匹配後端要求
3. ✅ API 回應被正確處理並渲染到介面
4. ✅ 實現了完整的錯誤處理機制
5. ✅ 提供了良好的使用者體驗

所有三個模組（CRM、Product、Order）都已實現基本的 CRUD 功能，並且與後端 API 完美整合。
