# 產品模組 API 文檔

## 概述

產品模組提供完整的電子商務產品管理功能，包括商品、分類、圖片、標籤、規格等管理。

## 基礎資訊

- **Base URL**: `/api`
- **Content-Type**: `application/json`
- **回應格式**: 
  ```json
  {
    "success": true/false,
    "message": "操作訊息",
    "data": {...}
  }
  ```

---

## 1. 商品管理 (Products)

### 1.1 創建商品

**POST** `/api/products`

```json
{
  "name": "經典白T恤",
  "sku": "SKU001",
  "description": "100% 純棉材質，舒適透氣",
  "categoryId": 1,
  "salesMode": "NORMAL",
  "basePrice": 499.00,
  "salePrice": 399.00,
  "costPrice": 200.00,
  "weight": 200,
  "minPurchaseQuantity": 1,
  "maxPurchaseQuantity": 10,
  "sortOrder": 1,
  "enabled": true
}
```

**回應**:
```json
{
  "success": true,
  "message": "商品已創建",
  "data": {
    "id": 1,
    "name": "經典白T恤",
    "sku": "SKU001",
    ...
  }
}
```

### 1.2 更新商品

**PUT** `/api/products/{id}`

請求同創建商品，但需在 URL 中指定商品 ID。

### 1.3 取得商品詳情

**GET** `/api/products/{id}`

### 1.4 刪除商品

**DELETE** `/api/products/{id}`

### 1.5 分頁查詢商品

**GET** `/api/products?page=0&size=20`

**查詢參數**:
- `page`: 頁碼（從 0 開始）
- `size`: 每頁數量（預設 20，最大 100）

### 1.6 依分類查詢商品

**GET** `/api/products/category/{categoryId}?page=0&size=20`

### 1.7 依狀態查詢商品

**GET** `/api/products/status/{status}?page=0&size=20`

**狀態值**: `DRAFT`, `ACTIVE`, `INACTIVE`, `OUT_OF_STOCK`

### 1.8 搜尋商品

**GET** `/api/products/search?keyword=T恤&page=0&size=20`

### 1.9 上架商品

**PUT** `/api/products/{id}/activate`

### 1.10 下架商品

**PUT** `/api/products/{id}/deactivate`

---

## 2. 商品分類 (Categories)

### 2.1 創建分類

**POST** `/api/product-categories`

```json
{
  "name": "服裝",
  "parentId": null,
  "description": "所有服裝類商品",
  "image": "https://example.com/category.jpg",
  "sortOrder": 1,
  "enabled": true
}
```

### 2.2 更新分類

**PUT** `/api/product-categories/{id}`

### 2.3 取得分類詳情

**GET** `/api/product-categories/{id}`

### 2.4 刪除分類

**DELETE** `/api/product-categories/{id}`

### 2.5 取得所有分類

**GET** `/api/product-categories`

### 2.6 取得已啟用的分類

**GET** `/api/product-categories/enabled`

### 2.7 取得頂層分類

**GET** `/api/product-categories/top`

### 2.8 取得子分類

**GET** `/api/product-categories/{parentId}/children`

---

## 3. 商品圖片 (Images)

### 3.1 添加商品圖片

**POST** `/api/product-images`

```json
{
  "productId": 1,
  "imageUrl": "https://example.com/image.jpg",
  "imageType": "MAIN",
  "sortOrder": 1,
  "isPrimary": true
}
```

### 3.2 更新商品圖片

**PUT** `/api/product-images/{id}`

### 3.3 刪除商品圖片

**DELETE** `/api/product-images/{id}`

### 3.4 取得商品的所有圖片

**GET** `/api/product-images/product/{productId}`

**回應**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "imageUrl": "https://example.com/image1.jpg",
      "imageType": "MAIN",
      "sortOrder": 1,
      "isPrimary": true
    },
    {
      "id": 2,
      "productId": 1,
      "imageUrl": "https://example.com/image2.jpg",
      "imageType": "DETAIL",
      "sortOrder": 2,
      "isPrimary": false
    }
  ]
}
```

### 3.5 設置為主圖

**PUT** `/api/product-images/{id}/set-primary`

---

## 4. 商品標籤 (Tags)

### 4.1 創建標籤

**POST** `/api/product-tags`

```json
{
  "name": "熱賣",
  "color": "#FF0000",
  "description": "熱銷商品標籤"
}
```

### 4.2 更新標籤

**PUT** `/api/product-tags/{id}`

### 4.3 刪除標籤

**DELETE** `/api/product-tags/{id}`

### 4.4 取得所有標籤

**GET** `/api/product-tags`

### 4.5 為商品添加標籤

**POST** `/api/product-tags/product/{productId}/tag/{tagId}`

### 4.6 移除商品標籤

**DELETE** `/api/product-tags/product/{productId}/tag/{tagId}`

### 4.7 取得商品的所有標籤

**GET** `/api/product-tags/product/{productId}`

### 4.8 批量設置商品標籤（替換現有標籤）

**PUT** `/api/product-tags/product/{productId}`

```json
[1, 2, 3, 5]
```

---

## 5. 商品規格 (Specifications)

### 5.1 添加商品規格

**POST** `/api/product-specifications`

```json
{
  "productId": 1,
  "specName": "顏色:紅色,尺寸:L",
  "sku": "SKU001-RED-L",
  "price": 399.00,
  "cost": 200.00,
  "stock": 100,
  "image": "https://example.com/red-l.jpg",
  "weight": 200,
  "enabled": true
}
```

### 5.2 批量創建商品規格

**POST** `/api/product-specifications/batch`

```json
[
  {
    "productId": 1,
    "specName": "顏色:紅色,尺寸:L",
    "sku": "SKU001-RED-L",
    "price": 399.00,
    "cost": 200.00,
    "stock": 100
  },
  {
    "productId": 1,
    "specName": "顏色:藍色,尺寸:L",
    "sku": "SKU001-BLUE-L",
    "price": 399.00,
    "cost": 200.00,
    "stock": 80
  }
]
```

### 5.3 更新商品規格

**PUT** `/api/product-specifications/{id}`

### 5.4 刪除商品規格

**DELETE** `/api/product-specifications/{id}`

### 5.5 取得規格詳情

**GET** `/api/product-specifications/{id}`

### 5.6 取得商品的所有規格

**GET** `/api/product-specifications/product/{productId}`

---

## 6. 批量操作 (Batch Operations)

### 6.1 批量更新商品

**POST** `/api/product-batch/update`

```json
{
  "productIds": [1, 2, 3],
  "status": "ACTIVE",
  "categoryId": 5,
  "salePrice": 299.00,
  "sortOrder": 10,
  "enabled": true
}
```

### 6.2 批量刪除商品

**POST** `/api/product-batch/delete`

```json
[1, 2, 3, 4, 5]
```

### 6.3 批量上架商品

**POST** `/api/product-batch/activate`

```json
[1, 2, 3]
```

### 6.4 批量下架商品

**POST** `/api/product-batch/deactivate`

```json
[1, 2, 3]
```

---

## 7. 庫存管理 (Inventory)

### 7.1 調整庫存

**POST** `/api/inventory/adjust`

```json
{
  "productId": 1,
  "warehouseId": 1,
  "quantity": 100,
  "type": "IN",
  "reason": "採購入庫",
  "referenceNo": "PO-2024-001"
}
```

### 7.2 取得商品庫存

**GET** `/api/inventory/product/{productId}`

### 7.3 取得倉庫庫存

**GET** `/api/inventory/warehouse/{warehouseId}`

### 7.4 設置庫存預警

**POST** `/api/inventory/alert`

```json
{
  "productId": 1,
  "warehouseId": 1,
  "alertLevel": "LOW",
  "threshold": 10
}
```

---

## 8. 倉庫管理 (Warehouses)

### 8.1 創建倉庫

**POST** `/api/warehouses`

```json
{
  "code": "WH001",
  "name": "中央倉庫",
  "address": "台北市信義區信義路五段7號",
  "contactPerson": "張三",
  "contactPhone": "02-1234-5678",
  "enabled": true
}
```

### 8.2 更新倉庫

**PUT** `/api/warehouses/{id}`

### 8.3 取得倉庫詳情

**GET** `/api/warehouses/{id}`

### 8.4 刪除倉庫

**DELETE** `/api/warehouses/{id}`

### 8.5 取得所有倉庫

**GET** `/api/warehouses`

### 8.6 取得已啟用的倉庫

**GET** `/api/warehouses/enabled`

---

## 前端使用範例

### 範例 1: 創建完整商品（含圖片和標籤）

```javascript
// 1. 創建商品
const createProduct = async () => {
  const response = await fetch('/api/products', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      name: '經典白T恤',
      sku: 'SKU001',
      categoryId: 1,
      salesMode: 'NORMAL',
      basePrice: 499.00,
      salePrice: 399.00,
      enabled: true
    })
  });
  const result = await response.json();
  return result.data.id;
};

// 2. 添加商品圖片
const addProductImages = async (productId) => {
  const images = [
    { imageUrl: 'image1.jpg', isPrimary: true, sortOrder: 1 },
    { imageUrl: 'image2.jpg', isPrimary: false, sortOrder: 2 }
  ];
  
  for (const image of images) {
    await fetch('/api/product-images', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId, ...image })
    });
  }
};

// 3. 設置商品標籤
const setProductTags = async (productId, tagIds) => {
  await fetch(`/api/product-tags/product/${productId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(tagIds)
  });
};

// 使用
const productId = await createProduct();
await addProductImages(productId);
await setProductTags(productId, [1, 2, 3]);
```

### 範例 2: 查詢並顯示商品列表

```javascript
const fetchProducts = async (page = 0, size = 20) => {
  const response = await fetch(`/api/products?page=${page}&size=${size}`);
  const result = await response.json();
  return result.data;
};

// 使用
const products = await fetchProducts(0, 20);
console.log('總數:', products.totalElements);
console.log('商品列表:', products.content);
```

### 範例 3: 搜尋商品

```javascript
const searchProducts = async (keyword, page = 0) => {
  const response = await fetch(
    `/api/products/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=20`
  );
  const result = await response.json();
  return result.data;
};

// 使用
const results = await searchProducts('T恤');
```

### 範例 4: 取得商品詳情（含圖片、標籤、規格）

```javascript
const getProductDetails = async (productId) => {
  // 取得基本資料
  const productRes = await fetch(`/api/products/${productId}`);
  const product = (await productRes.json()).data;
  
  // 取得圖片
  const imagesRes = await fetch(`/api/product-images/product/${productId}`);
  product.images = (await imagesRes.json()).data;
  
  // 取得標籤
  const tagsRes = await fetch(`/api/product-tags/product/${productId}`);
  product.tags = (await tagsRes.json()).data;
  
  // 取得規格
  const specsRes = await fetch(`/api/product-specifications/product/${productId}`);
  product.specifications = (await specsRes.json()).data;
  
  return product;
};

// 使用
const productDetail = await getProductDetails(1);
console.log(productDetail);
```

### 範例 5: 分類樹狀選單

```javascript
const buildCategoryTree = async () => {
  // 取得頂層分類
  const topRes = await fetch('/api/product-categories/top');
  const topCategories = (await topRes.json()).data;
  
  // 為每個頂層分類取得子分類
  for (const category of topCategories) {
    const childRes = await fetch(`/api/product-categories/${category.id}/children`);
    category.children = (await childRes.json()).data;
  }
  
  return topCategories;
};

// 使用
const categoryTree = await buildCategoryTree();
```

---

## 錯誤處理

所有 API 都會返回標準格式：

**成功**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {...}
}
```

**失敗**:
```json
{
  "success": false,
  "message": "錯誤訊息",
  "data": null
}
```

### 常見錯誤訊息

- `商品不存在` - 指定的商品 ID 不存在
- `商品編號（SKU）已存在，請使用其他編號` - SKU 重複
- `商品分類不存在` - 指定的分類 ID 不存在
- `標籤名稱已存在` - 標籤名稱重複
- `規格編號（SKU）已存在` - 規格 SKU 重複
- `圖片不存在` - 指定的圖片 ID 不存在
- `標籤不存在` - 指定的標籤 ID 不存在

---

## 資料驗證規則

### 商品
- `name`: 必填，最多 200 字
- `sku`: 選填，最多 50 字，唯一
- `categoryId`: 選填，但必須是有效的分類 ID
- `salesMode`: 必填，可選值: NORMAL, PRE_ORDER, SUBSCRIPTION, VOUCHER
- `basePrice`, `salePrice`, `costPrice`: 選填，必須 >= 0
- `minPurchaseQuantity`: 選填，必須 >= 1

### 分類
- `name`: 必填，最多 100 字

### 標籤
- `name`: 必填，最多 50 字，唯一

### 圖片
- `productId`: 必填
- `imageUrl`: 必填，最多 500 字

### 規格
- `productId`: 必填
- `specName`: 必填，最多 200 字
- `sku`: 選填，最多 50 字，唯一

---

## Swagger 文檔

本系統集成 Swagger UI，可訪問以下地址查看完整 API 文檔：

**Swagger UI**: `http://your-domain/swagger-ui/index.html`

在 Swagger UI 中可以：
- 查看所有 API 端點
- 查看請求和回應模型
- 直接測試 API
- 查看詳細的參數說明

---

## 注意事項

1. **SKU 唯一性**: 商品的 SKU 和規格的 SKU 必須全局唯一
2. **分類驗證**: 創建或更新商品時，系統會驗證 categoryId 是否存在
3. **主圖設置**: 設置新主圖時，原有主圖會自動取消
4. **級聯刪除**: 刪除商品時不會自動刪除相關圖片、標籤關聯和規格，需手動處理
5. **標籤刪除**: 刪除標籤會自動刪除所有相關的商品標籤關聯
6. **分頁查詢**: 頁碼從 0 開始，size 最大為 100
7. **批量操作**: 批量操作建議單次不超過 100 條記錄

---

## 支援的業務場景

### 多規格商品
通過規格管理實現同一商品的多種規格（如不同顏色、尺寸），每個規格可以有獨立的 SKU、價格和庫存。

### 預購商品
通過 `salesMode = PRE_ORDER` 和 ProductPreOrder 實體管理預購商品。

### 訂閱商品
通過 `salesMode = SUBSCRIPTION` 和 ProductSubscription 實體管理訂閱制商品。

### 優惠券商品
通過 `salesMode = VOUCHER` 和 ProductVoucher 實體管理優惠券類商品。

### 多倉庫管理
支援多個倉庫的庫存管理，每個商品可以在不同倉庫有獨立的庫存。

### 庫存預警
支援設置庫存預警閾值，當庫存低於閾值時觸發預警。
