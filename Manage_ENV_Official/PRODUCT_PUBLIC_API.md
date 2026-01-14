# 商品公開 API 文件 (Product Public API Documentation)

> **版本**：1.0.0  
> **最後更新**：2026-01-12  
> **適用對象**：前端開發人員、第三方串接開發者

---

## 概述 (Overview)

本文件說明商品模組中 **不需要身份驗證** 的公開 API 端點，適用於官網前台、行動 App 或第三方串接使用。

### 基本資訊

| 項目 | 說明 |
|------|------|
| Base URL | `http://your-domain/api` |
| 認證方式 | **無需認證**（公開端點） |
| 回應格式 | JSON |
| 字元編碼 | UTF-8 |

### 通用回應格式

所有 API 回應皆遵循以下格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... }
}
```

### 錯誤回應格式

```json
{
  "success": false,
  "message": "錯誤訊息",
  "data": null
}
```

---

## 商品查詢 API (Product Query APIs)

### 1. 分頁查詢商品列表

取得所有商品的分頁列表，適用於首頁商品展示、商品列表頁。

**端點**：`GET /api/products`

**使用情境**：
- 🏠 首頁商品列表展示
- 📋 全部商品頁面
- 🔄 無限滾動載入更多商品

**請求參數**：

| 參數 | 類型 | 必填 | 預設值 | 說明 |
|------|------|------|--------|------|
| `page` | Integer | 否 | 0 | 頁碼（從 0 開始） |
| `size` | Integer | 否 | 20 | 每頁數量 |

**請求範例**：

```bash
# 取得第一頁，每頁 10 筆
GET /api/products?page=0&size=10

# 取得第二頁，使用預設每頁數量
GET /api/products?page=1
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "經典白T恤",
        "sku": "SKU001",
        "description": "100% 純棉材質，舒適透氣",
        "categoryId": 1,
        "status": "ACTIVE",
        "salesMode": "NORMAL",
        "basePrice": 499.00,
        "salePrice": 399.00,
        "weight": 200,
        "minPurchaseQuantity": 1,
        "maxPurchaseQuantity": 10,
        "sortOrder": 1,
        "enabled": true,
        "images": [
          {
            "id": 1,
            "imageUrl": "/uploads/images/product1.jpg",
            "isPrimary": true,
            "sortOrder": 1
          }
        ],
        "tags": ["新品", "熱銷"],
        "specifications": [
          {
            "id": 1,
            "specName": "顏色:白色,尺寸:M",
            "sku": "SKU001-WH-M",
            "price": 399.00,
            "stock": 100
          }
        ]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 150,
    "totalPages": 15,
    "first": true,
    "last": false,
    "empty": false
  }
}
```

**前端串接範例 (JavaScript)**：

```javascript
// 使用 fetch
async function getProducts(page = 0, size = 20) {
  const response = await fetch(`/api/products?page=${page}&size=${size}`);
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}

// 使用 axios
const getProducts = (params = {}) => {
  return axios.get('/api/products', {
    params: {
      page: params.page || 0,
      size: params.size || 20
    }
  });
};
```

---

### 2. 取得單一商品詳情

取得特定商品的完整資訊，適用於商品詳情頁。

**端點**：`GET /api/products/{id}`

**使用情境**：
- 📦 商品詳情頁
- 🛒 加入購物車前確認商品資訊
- 📱 分享商品連結

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `id` | Long | 是 | 商品 ID |

**請求範例**：

```bash
GET /api/products/1
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "name": "經典白T恤",
    "sku": "SKU001",
    "description": "100% 純棉材質，舒適透氣",
    "categoryId": 1,
    "status": "ACTIVE",
    "salesMode": "NORMAL",
    "basePrice": 499.00,
    "salePrice": 399.00,
    "costPrice": 200.00,
    "weight": 200,
    "minPurchaseQuantity": 1,
    "maxPurchaseQuantity": 10,
    "sortOrder": 1,
    "metaTitle": "經典白T恤 - 100% 純棉",
    "metaDescription": "舒適透氣的經典白T恤",
    "metaKeywords": "T恤,白色,純棉",
    "enabled": true,
    "images": [
      {
        "id": 1,
        "imageUrl": "/uploads/images/product1-main.jpg",
        "isPrimary": true,
        "sortOrder": 1
      },
      {
        "id": 2,
        "imageUrl": "/uploads/images/product1-detail.jpg",
        "isPrimary": false,
        "sortOrder": 2
      }
    ],
    "tags": ["新品", "熱銷"],
    "specifications": [
      {
        "id": 1,
        "specName": "顏色:白色,尺寸:S",
        "sku": "SKU001-WH-S",
        "price": 399.00,
        "stock": 50,
        "image": "/uploads/images/product1-white.jpg"
      },
      {
        "id": 2,
        "specName": "顏色:白色,尺寸:M",
        "sku": "SKU001-WH-M",
        "price": 399.00,
        "stock": 100,
        "image": "/uploads/images/product1-white.jpg"
      },
      {
        "id": 3,
        "specName": "顏色:黑色,尺寸:M",
        "sku": "SKU001-BK-M",
        "price": 419.00,
        "stock": 80,
        "image": "/uploads/images/product1-black.jpg"
      }
    ],
    "descriptionBlocks": [
      {
        "id": 1,
        "blockType": "TEXT",
        "content": "商品特色說明",
        "sortOrder": 1
      }
    ]
  }
}
```

**錯誤回應（商品不存在）**：

```json
{
  "success": false,
  "message": "商品不存在",
  "data": null
}
```

**前端串接範例**：

```javascript
// 取得商品詳情
async function getProductDetail(productId) {
  const response = await fetch(`/api/products/${productId}`);
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}
```

---

### 3. 依分類查詢商品

取得特定分類下的商品列表，適用於分類頁面。

**端點**：`GET /api/products/category/{categoryId}`

**使用情境**：
- 📂 分類頁面商品列表
- 🏷️ 側邊欄分類篩選
- 🔍 分類導航

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `categoryId` | Long | 是 | 分類 ID |

**請求參數**：

| 參數 | 類型 | 必填 | 預設值 | 說明 |
|------|------|------|--------|------|
| `page` | Integer | 否 | 0 | 頁碼（從 0 開始） |
| `size` | Integer | 否 | 20 | 每頁數量 |

**請求範例**：

```bash
# 取得分類 ID 為 1 的商品，第一頁
GET /api/products/category/1?page=0&size=12
```

**回應格式**：與「分頁查詢商品列表」相同

**前端串接範例**：

```javascript
// 依分類取得商品
async function getProductsByCategory(categoryId, page = 0, size = 12) {
  const response = await fetch(
    `/api/products/category/${categoryId}?page=${page}&size=${size}`
  );
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}
```

---

### 4. 依狀態查詢商品

取得特定狀態的商品列表。

**端點**：`GET /api/products/status/{status}`

**使用情境**：
- ✅ 只顯示上架中商品（前台）
- 📋 後台商品管理篩選

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `status` | String | 是 | 商品狀態（見下表） |

**商品狀態說明**：

| 狀態值 | 說明 | 前台建議用途 |
|--------|------|--------------|
| `DRAFT` | 草稿 | ❌ 不建議顯示 |
| `ACTIVE` | 上架中 | ✅ 前台主要使用 |
| `INACTIVE` | 已下架 | ❌ 不建議顯示 |
| `OUT_OF_STOCK` | 缺貨 | ⚠️ 可顯示但標示缺貨 |

**請求參數**：

| 參數 | 類型 | 必填 | 預設值 | 說明 |
|------|------|------|--------|------|
| `page` | Integer | 否 | 0 | 頁碼（從 0 開始） |
| `size` | Integer | 否 | 20 | 每頁數量 |

**請求範例**：

```bash
# 取得所有上架中的商品
GET /api/products/status/ACTIVE?page=0&size=20
```

**前端串接範例**：

```javascript
// 取得上架中的商品（前台最常用）
async function getActiveProducts(page = 0, size = 20) {
  const response = await fetch(
    `/api/products/status/ACTIVE?page=${page}&size=${size}`
  );
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}
```

---

### 5. 搜尋商品

依關鍵字搜尋商品，適用於搜尋功能。

**端點**：`GET /api/products/search`

**使用情境**：
- 🔍 網站搜尋列
- 📱 App 搜尋功能
- 🏷️ 關鍵字行銷頁面

**請求參數**：

| 參數 | 類型 | 必填 | 預設值 | 說明 |
|------|------|------|--------|------|
| `keyword` | String | 是 | - | 搜尋關鍵字 |
| `page` | Integer | 否 | 0 | 頁碼（從 0 開始） |
| `size` | Integer | 否 | 20 | 每頁數量 |

**請求範例**：

```bash
# 搜尋包含 "T恤" 的商品
GET /api/products/search?keyword=T恤&page=0&size=20

# URL 編碼後
GET /api/products/search?keyword=T%E6%81%A4&page=0&size=20
```

**回應格式**：與「分頁查詢商品列表」相同

**前端串接範例**：

```javascript
// 搜尋商品
async function searchProducts(keyword, page = 0, size = 20) {
  const params = new URLSearchParams({
    keyword,
    page: page.toString(),
    size: size.toString()
  });
  
  const response = await fetch(`/api/products/search?${params}`);
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}

// 帶防抖的搜尋（建議使用）
const debouncedSearch = debounce(async (keyword) => {
  const products = await searchProducts(keyword);
  // 更新 UI
}, 300);
```

---

## 商品分類 API (Product Category APIs)

### 6. 取得所有分類

取得系統中所有商品分類。

**端點**：`GET /api/product-categories`

**使用情境**：
- 📂 後台分類管理
- 🗂️ 完整分類樹建構

**請求範例**：

```bash
GET /api/product-categories
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "name": "服飾",
      "parentId": null,
      "description": "各類服飾商品",
      "image": "/uploads/images/category-clothes.jpg",
      "icon": "shopping_bag",
      "sortOrder": 1,
      "enabled": true
    },
    {
      "id": 2,
      "name": "上衣",
      "parentId": 1,
      "description": "各類上衣",
      "image": null,
      "icon": "checkroom",
      "sortOrder": 1,
      "enabled": true
    },
    {
      "id": 3,
      "name": "褲子",
      "parentId": 1,
      "description": "各類褲子",
      "image": null,
      "icon": "dry_cleaning",
      "sortOrder": 2,
      "enabled": true
    }
  ]
}
```

---

### 7. 取得已啟用的分類

取得所有已啟用的分類，適用於前台顯示。

**端點**：`GET /api/product-categories/enabled`

**使用情境**：
- 🏠 前台分類導航
- 📱 App 分類選單
- 🔍 篩選條件選項

**請求範例**：

```bash
GET /api/product-categories/enabled
```

**回應格式**：與「取得所有分類」相同，但只回傳 `enabled = true` 的分類

**前端串接範例**：

```javascript
// 取得啟用的分類（前台建議使用）
async function getEnabledCategories() {
  const response = await fetch('/api/product-categories/enabled');
  const result = await response.json();
  
  if (result.success) {
    return result.data;
  }
  throw new Error(result.message);
}
```

---

### 8. 取得頂層分類

取得所有頂層（無父分類）的分類。

**端點**：`GET /api/product-categories/top`

**使用情境**：
- 🗂️ 主導航選單
- 📂 分類樹第一層
- 🏠 首頁分類區塊

**請求範例**：

```bash
GET /api/product-categories/top
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "name": "服飾",
      "parentId": null,
      "description": "各類服飾商品",
      "image": "/uploads/images/category-clothes.jpg",
      "icon": "shopping_bag",
      "sortOrder": 1,
      "enabled": true
    },
    {
      "id": 10,
      "name": "配件",
      "parentId": null,
      "description": "各類配件",
      "image": "/uploads/images/category-accessories.jpg",
      "icon": "watch",
      "sortOrder": 2,
      "enabled": true
    }
  ]
}
```

---

### 9. 取得子分類

取得特定分類下的子分類。

**端點**：`GET /api/product-categories/{parentId}/children`

**使用情境**：
- 🔽 下拉式分類選單
- 📂 分類樹展開
- 🗂️ 階層式導航

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `parentId` | Long | 是 | 父分類 ID |

**請求範例**：

```bash
# 取得分類 ID 1 的子分類
GET /api/product-categories/1/children
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 2,
      "name": "上衣",
      "parentId": 1,
      "description": "各類上衣",
      "image": null,
      "icon": "checkroom",
      "sortOrder": 1,
      "enabled": true
    },
    {
      "id": 3,
      "name": "褲子",
      "parentId": 1,
      "description": "各類褲子",
      "image": null,
      "icon": "dry_cleaning",
      "sortOrder": 2,
      "enabled": true
    }
  ]
}
```

**前端串接範例**：

```javascript
// 建構完整分類樹
async function buildCategoryTree() {
  // 1. 先取得頂層分類
  const topCategories = await fetch('/api/product-categories/top')
    .then(res => res.json())
    .then(res => res.data);
  
  // 2. 為每個頂層分類取得子分類
  const tree = await Promise.all(
    topCategories.map(async (category) => {
      const children = await fetch(`/api/product-categories/${category.id}/children`)
        .then(res => res.json())
        .then(res => res.data);
      
      return {
        ...category,
        children
      };
    })
  );
  
  return tree;
}
```

---

### 10. 取得單一分類詳情

取得特定分類的詳細資訊。

**端點**：`GET /api/product-categories/{id}`

**使用情境**：
- 📂 分類頁面標題顯示
- 🏷️ SEO 分類描述
- 📝 分類編輯頁面

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `id` | Long | 是 | 分類 ID |

**請求範例**：

```bash
GET /api/product-categories/1
```

**回應範例**：

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "name": "服飾",
    "parentId": null,
    "description": "各類服飾商品",
    "image": "/uploads/images/category-clothes.jpg",
    "icon": "shopping_bag",
    "sortOrder": 1,
    "enabled": true
  }
}
```

---

## 資料結構說明 (Data Structure Reference)

### ProductDTO 欄位說明

| 欄位 | 類型 | 說明 |
|------|------|------|
| `id` | Long | 商品 ID |
| `name` | String | 商品名稱（最多 200 字） |
| `sku` | String | 商品編號（最多 50 字） |
| `description` | String | 商品描述 |
| `categoryId` | Long | 商品分類 ID |
| `status` | String | 商品狀態：`DRAFT`、`ACTIVE`、`INACTIVE`、`OUT_OF_STOCK` |
| `salesMode` | String | 銷售模式：`NORMAL`、`PRE_ORDER`、`VOUCHER`、`SUBSCRIPTION`、`STORE_ONLY` |
| `basePrice` | BigDecimal | 基礎價格（原價） |
| `salePrice` | BigDecimal | 銷售價格（實際售價） |
| `costPrice` | BigDecimal | 成本價格 |
| `weight` | Integer | 商品重量（克） |
| `minPurchaseQuantity` | Integer | 最小購買數量 |
| `maxPurchaseQuantity` | Integer | 最大購買數量 |
| `sortOrder` | Integer | 排序順序 |
| `metaTitle` | String | SEO 標題 |
| `metaDescription` | String | SEO 描述 |
| `metaKeywords` | String | SEO 關鍵字 |
| `enabled` | Boolean | 是否啟用 |
| `images` | Array | 商品圖片列表 |
| `tags` | Array | 商品標籤列表 |
| `specifications` | Array | 商品規格列表 |
| `descriptionBlocks` | Array | 商品描述區塊列表 |

### ProductCategoryDTO 欄位說明

| 欄位 | 類型 | 說明 |
|------|------|------|
| `id` | Long | 分類 ID |
| `name` | String | 分類名稱（最多 100 字） |
| `parentId` | Long | 父分類 ID（null 表示頂層分類） |
| `description` | String | 分類描述（最多 500 字） |
| `image` | String | 分類圖片 URL |
| `icon` | String | 分類圖標（Material Icons 名稱，最多 100 字） |
| `sortOrder` | Integer | 排序順序 |
| `enabled` | Boolean | 是否啟用 |

**圖標欄位說明**：
- `icon` 欄位使用 **Material Icons** 圖標庫的圖標名稱
- 圖標名稱需符合 Material Icons 命名規範（例如：`shopping_bag`、`store`、`category`）
- 可在前端使用 `<q-icon name="圖標名稱" />` 或 `<i class="material-icons">圖標名稱</i>` 顯示
- 如果 `icon` 為 `null` 或空字串，前端可顯示預設圖標或僅顯示文字
- 常用圖標範例：`shopping_bag`、`shopping_cart`、`store`、`category`、`local_mall`、`inventory_2` 等

### 商品狀態 (ProductStatus) 列舉

| 值 | 說明 | 建議前台處理 |
|----|------|--------------|
| `DRAFT` | 草稿 | 不顯示 |
| `ACTIVE` | 上架中 | 正常顯示，可購買 |
| `INACTIVE` | 已下架 | 不顯示或顯示「已下架」 |
| `OUT_OF_STOCK` | 缺貨 | 顯示但標示「缺貨」，禁止購買 |

### 銷售模式 (ProductSalesMode) 列舉

| 值 | 說明 | 前台處理建議 |
|----|------|--------------|
| `NORMAL` | 一般銷售 | 正常購買流程 |
| `PRE_ORDER` | 預購商品 | 顯示預購資訊、預計出貨時間 |
| `VOUCHER` | 票券商品 | 顯示票券有效期、使用說明 |
| `SUBSCRIPTION` | 訂閱商品 | 顯示訂閱週期、自動續訂選項 |
| `STORE_ONLY` | 門市限定 | 顯示可取貨門市、不支援宅配 |

---

## 商品規格 API (Product Specification APIs)

### 11. 取得商品所有規格

取得特定商品的所有規格選項，適用於商品詳情頁的規格選擇功能。

**端點**：`GET /api/product-specifications/product/{productId}`

**使用情境**：
- 📦 商品詳情頁規格選擇器
- 💰 多規格價格顯示
- 📊 庫存狀態檢查
- 🛒 加入購物車前的規格驗證

**路徑參數**：

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `productId` | Long | 是 | 商品 ID |

**請求範例**：

```bash
# 取得商品 ID 為 1 的所有規格
GET /api/product-specifications/product/1
```

**回應範例**：

```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "specName": "顏色:白色,尺寸:S",
      "sku": "SKU001-WH-S",
      "price": 399.00,
      "stock": 50,
      "image": "/uploads/images/spec-white-s.jpg"
    },
    {
      "id": 2,
      "specName": "顏色:白色,尺寸:M",
      "sku": "SKU001-WH-M",
      "price": 399.00,
      "stock": 100,
      "image": "/uploads/images/spec-white-m.jpg"
    },
    {
      "id": 3,
      "specName": "顏色:黑色,尺寸:M",
      "sku": "SKU001-BK-M",
      "price": 419.00,
      "stock": 80,
      "image": "/uploads/images/spec-black-m.jpg"
    },
    {
      "id": 4,
      "specName": "顏色:黑色,尺寸:L",
      "sku": "SKU001-BK-L",
      "price": 419.00,
      "stock": 0,
      "image": "/uploads/images/spec-black-l.jpg"
    }
  ]
}
```

**錯誤回應（商品不存在或無規格）**：

```json
{
  "success": true,
  "message": null,
  "data": []
}
```

**前端串接範例**：

```javascript
// Vue 3 + Quasar 範例
import { ref, computed } from 'vue';
import { getProductSpecifications } from 'src/api/product.js';

// 規格資料和選中規格
const specifications = ref([]);
const selectedSpec = ref(null);

// 載入規格
async function loadSpecifications(productId) {
  try {
    const response = await getProductSpecifications(productId);
    
    if (response && response.data) {
      specifications.value = response.data;
      
      // 預設選擇第一個有庫存的規格
      const firstAvailable = specifications.value.find(spec => spec.stock > 0);
      if (firstAvailable) {
        selectedSpec.value = firstAvailable;
      }
    }
  } catch (error) {
    console.error('載入規格失敗:', error);
  }
}

// 選擇規格
function selectSpecification(spec) {
  if (spec.stock === 0) {
    // 提示已售完
    return;
  }
  selectedSpec.value = spec;
}

// 計算顯示價格
const displayPrice = computed(() => {
  return selectedSpec.value?.price || product.value?.price || 0;
});

// 檢查是否可加入購物車
const canAddToCart = computed(() => {
  // 如果有規格，必須選擇規格且有庫存
  if (specifications.value.length > 0) {
    return selectedSpec.value && selectedSpec.value.stock > 0;
  }
  // 沒有規格的商品可直接加入
  return true;
});
```

**React 範例**：

```javascript
import { useState, useEffect } from 'react';
import { getProductSpecifications } from './api/product';

function ProductDetail({ productId }) {
  const [specifications, setSpecifications] = useState([]);
  const [selectedSpec, setSelectedSpec] = useState(null);
  
  useEffect(() => {
    loadSpecifications();
  }, [productId]);
  
  const loadSpecifications = async () => {
    try {
      const response = await getProductSpecifications(productId);
      if (response?.data) {
        setSpecifications(response.data);
        
        // 自動選擇第一個有庫存的規格
        const firstAvailable = response.data.find(spec => spec.stock > 0);
        if (firstAvailable) {
          setSelectedSpec(firstAvailable);
        }
      }
    } catch (error) {
      console.error('載入規格失敗:', error);
    }
  };
  
  const handleSelectSpec = (spec) => {
    if (spec.stock > 0) {
      setSelectedSpec(spec);
    }
  };
  
  return (
    <div>
      {specifications.length > 0 && (
        <div className="specifications">
          <h3>選擇規格</h3>
          <div className="spec-options">
            {specifications.map(spec => (
              <div
                key={spec.id}
                className={`spec-option ${selectedSpec?.id === spec.id ? 'selected' : ''} ${spec.stock === 0 ? 'out-of-stock' : ''}`}
                onClick={() => handleSelectSpec(spec)}
              >
                <div className="spec-name">{spec.specName}</div>
                <div className="spec-price">NT$ {spec.price}</div>
                <div className="spec-stock">
                  {spec.stock > 0 ? `庫存: ${spec.stock}` : '售完'}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
      
      <div className="price">
        NT$ {selectedSpec?.price || product.price}
      </div>
      
      <button 
        onClick={handleAddToCart}
        disabled={specifications.length > 0 && (!selectedSpec || selectedSpec.stock === 0)}
      >
        加入購物車
      </button>
    </div>
  );
}
```

**jQuery 範例**：

```javascript
// 載入規格
function loadProductSpecifications(productId) {
  $.ajax({
    url: `/api/product-specifications/product/${productId}`,
    method: 'GET',
    success: function(response) {
      if (response.success && response.data.length > 0) {
        renderSpecifications(response.data);
      } else {
        // 無規格，隱藏規格選擇器
        $('#specifications-section').hide();
      }
    },
    error: function(error) {
      console.error('載入規格失敗:', error);
    }
  });
}

// 渲染規格選擇器
function renderSpecifications(specifications) {
  const container = $('#spec-options');
  container.empty();
  
  specifications.forEach(spec => {
    const specHtml = `
      <div class="spec-option ${spec.stock === 0 ? 'out-of-stock' : ''}" 
           data-spec-id="${spec.id}" 
           data-price="${spec.price}"
           data-stock="${spec.stock}">
        <div class="spec-name">${spec.specName}</div>
        <div class="spec-price">NT$ ${spec.price}</div>
        <div class="spec-stock">
          ${spec.stock > 0 ? `庫存: ${spec.stock}` : '售完'}
        </div>
      </div>
    `;
    container.append(specHtml);
  });
  
  // 綁定點擊事件
  $('.spec-option').on('click', function() {
    if ($(this).hasClass('out-of-stock')) {
      alert('此規格已售完');
      return;
    }
    
    $('.spec-option').removeClass('selected');
    $(this).addClass('selected');
    
    // 更新價格顯示
    const price = $(this).data('price');
    $('#product-price').text(`NT$ ${price}`);
  });
}
```

### ProductSpecificationDTO 欄位說明

| 欄位 | 類型 | 說明 |
|------|------|------|
| `id` | Long | 規格 ID |
| `specName` | String | 規格名稱（例如："顏色:白色,尺寸:M"） |
| `sku` | String | 規格 SKU 編號（唯一） |
| `price` | BigDecimal | 規格價格（可能與商品基礎價格不同） |
| `stock` | Integer | 庫存數量（0 表示售完） |
| `image` | String | 規格圖片 URL（可選，用於切換主圖） |

### 規格選擇邏輯建議

```javascript
// 完整的規格選擇邏輯
class ProductSpecificationManager {
  constructor(product, specifications) {
    this.product = product;
    this.specifications = specifications;
    this.selectedSpec = null;
  }
  
  // 選擇規格
  selectSpec(specId) {
    const spec = this.specifications.find(s => s.id === specId);
    
    if (!spec) {
      throw new Error('規格不存在');
    }
    
    if (spec.stock === 0) {
      throw new Error('此規格已售完');
    }
    
    this.selectedSpec = spec;
    return spec;
  }
  
  // 取得當前價格
  getCurrentPrice() {
    return this.selectedSpec?.price || this.product.salePrice;
  }
  
  // 取得當前庫存
  getCurrentStock() {
    return this.selectedSpec?.stock || 999;
  }
  
  // 取得當前 SKU
  getCurrentSku() {
    return this.selectedSpec?.sku || this.product.sku;
  }
  
  // 檢查是否可以加入購物車
  canAddToCart(quantity) {
    // 有規格時必須選擇規格
    if (this.specifications.length > 0 && !this.selectedSpec) {
      return { valid: false, message: '請先選擇商品規格' };
    }
    
    // 檢查庫存
    const stock = this.getCurrentStock();
    if (quantity > stock) {
      return { valid: false, message: `庫存不足，目前剩餘 ${stock} 件` };
    }
    
    return { valid: true };
  }
  
  // 取得購物車項目資料
  getCartItemData(quantity) {
    return {
      productId: this.product.id,
      productName: this.product.name,
      specificationId: this.selectedSpec?.id,
      specificationName: this.selectedSpec?.specName,
      sku: this.getCurrentSku(),
      price: this.getCurrentPrice(),
      quantity: quantity,
      image: this.selectedSpec?.image || this.product.images[0]?.imageUrl
    };
  }
}

// 使用範例
const manager = new ProductSpecificationManager(product, specifications);

// 選擇規格
try {
  manager.selectSpec(specId);
  updatePriceDisplay(manager.getCurrentPrice());
} catch (error) {
  showError(error.message);
}

// 加入購物車前驗證
const validation = manager.canAddToCart(quantity);
if (!validation.valid) {
  showError(validation.message);
  return;
}

const cartItem = manager.getCartItemData(quantity);
addToCart(cartItem);
```

---

## 常見使用情境範例

### 情境 1：首頁商品展示

```javascript
// 首頁載入時取得推薦商品
async function loadHomePage() {
  // 取得上架中的商品，只取前 8 筆
  const products = await fetch('/api/products/status/ACTIVE?page=0&size=8')
    .then(res => res.json())
    .then(res => res.data.content);
  
  // 取得頂層分類
  const categories = await fetch('/api/product-categories/top')
    .then(res => res.json())
    .then(res => res.data);
  
  // 渲染頁面
  renderHomePage({ products, categories });
}
```

### 情境 2：分類頁面

```javascript
// 進入分類頁面
async function loadCategoryPage(categoryId, page = 0) {
  // 同時取得分類資訊和商品列表
  const [categoryRes, productsRes] = await Promise.all([
    fetch(`/api/product-categories/${categoryId}`),
    fetch(`/api/products/category/${categoryId}?page=${page}&size=12`)
  ]);
  
  const category = (await categoryRes.json()).data;
  const products = (await productsRes.json()).data;
  
  // 渲染頁面
  renderCategoryPage({ category, products });
}
```

### 情境 3：商品詳情頁

```javascript
// 進入商品詳情頁
async function loadProductDetail(productId) {
  const product = await fetch(`/api/products/${productId}`)
    .then(res => res.json())
    .then(res => res.data);
  
  // 設定 SEO meta tags
  document.title = product.metaTitle || product.name;
  setMetaDescription(product.metaDescription);
  
  // 載入商品規格
  const specifications = await fetch(`/api/product-specifications/product/${productId}`)
    .then(res => res.json())
    .then(res => res.data || []);
  
  // 渲染商品詳情
  renderProductDetail(product, specifications);
}
```

### 情境 4：搜尋結果頁

```javascript
// 搜尋商品
async function searchAndDisplay(keyword) {
  if (!keyword || keyword.trim().length < 2) {
    showMessage('請輸入至少 2 個字元');
    return;
  }
  
  const result = await fetch(`/api/products/search?keyword=${encodeURIComponent(keyword)}`)
    .then(res => res.json())
    .then(res => res.data);
  
  renderSearchResults({
    keyword,
    products: result.content,
    totalCount: result.totalElements
  });
}
```

### 情境 5：多規格商品加入購物車

```javascript
// 完整的規格商品加入購物車流程
async function addSpecProductToCart(productId, quantity) {
  // 1. 載入商品資訊
  const product = await fetch(`/api/products/${productId}`)
    .then(res => res.json())
    .then(res => res.data);
  
  // 2. 載入規格資訊
  const specifications = await fetch(`/api/product-specifications/product/${productId}`)
    .then(res => res.json())
    .then(res => res.data || []);
  
  // 3. 如果有規格，必須先選擇
  if (specifications.length > 0) {
    if (!selectedSpec) {
      showError('請先選擇商品規格');
      return;
    }
    
    // 檢查庫存
    if (selectedSpec.stock < quantity) {
      showError(`庫存不足，目前剩餘 ${selectedSpec.stock} 件`);
      return;
    }
    
    // 使用規格的價格和 SKU
    const cartItem = {
      productId: product.id,
      productName: product.name,
      specificationId: selectedSpec.id,
      specificationName: selectedSpec.specName,
      sku: selectedSpec.sku,
      price: selectedSpec.price,
      quantity: quantity,
      image: selectedSpec.image || product.images[0]?.imageUrl
    };
    
    addToCart(cartItem);
  } else {
    // 無規格商品，直接使用商品資訊
    const cartItem = {
      productId: product.id,
      productName: product.name,
      sku: product.sku,
      price: product.salePrice,
      quantity: quantity,
      image: product.images[0]?.imageUrl
    };
    
    addToCart(cartItem);
  }
}
```

---

## 注意事項

1. **分頁索引**：頁碼從 `0` 開始，不是從 `1` 開始
2. **字元編碼**：搜尋關鍵字需進行 URL 編碼
3. **空結果處理**：當查詢無結果時，`data.content` 為空陣列，`data.empty` 為 `true`
4. **圖片路徑**：圖片 URL 可能為相對路徑，需根據實際部署環境組合完整 URL
5. **價格顯示**：建議使用 `salePrice` 作為顯示價格，`basePrice` 作為原價（可顯示折扣）
6. **庫存判斷**：可透過 `status` 或 `specifications[].stock` 判斷商品是否可購買
7. **規格處理**：
   - 有規格的商品必須先載入規格列表
   - 使用者必須選擇規格後才能加入購物車
   - 規格價格可能與商品基礎價格不同，需以規格價格為準
   - 規格庫存獨立計算，需檢查選中規格的庫存
8. **規格圖片切換**：選擇規格時，如果規格有 `image` 欄位，建議更新商品主圖顯示

---

## 聯絡資訊

如有 API 串接問題，請聯繫後端開發團隊。

