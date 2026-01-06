# 電商平台系統 API 完整文檔

本文件整理了電商平台系統的所有 API 接口，涵蓋商品、訂單、行銷、CRM 等全通路功能。

## 目錄

- [商品相關 API](#商品相關-api)
- [商品批次操作 API](#商品批次操作-api)
- [商品標籤 API](#商品標籤-api)
- [商品規格 API](#商品規格-api)
- [商品圖片 API](#商品圖片-api)
- [商品分類 API](#商品分類-api)
- [購物車操作 API](#購物車操作-api)
- [訂單相關 API](#訂單相關-api)
- [訂單批次操作 API](#訂單批次操作-api)
- [訂單查詢 API](#訂單查詢-api)
- [訂單統計 API](#訂單統計-api)
- [訂單物流 API](#訂單物流-api)
- [訂單付款 API](#訂單付款-api)
- [訂單折扣 API](#訂單折扣-api)
- [訂單問與答 API](#訂單問與答-api)
- [訂單歷史 API](#訂單歷史-api)
- [顧客黑名單 API](#顧客黑名單-api)
- [會員與身份驗證 API](#會員與身份驗證-api)
- [會員管理 API](#會員管理-api)
- [會員等級管理 API](#會員等級管理-api)
- [會員群組管理 API](#會員群組管理-api)
- [積點管理 API](#積點管理-api)
- [獎勵制度管理 API](#獎勵制度管理-api)
- [支付相關 API](#支付相關-api)
- [支付管理 API](#支付管理-api)
- [物流相關 API](#物流相關-api)
- [優惠與促銷 API](#優惠與促銷-api)
- [購物車未結帳提醒 API](#購物車未結帳提醒-api)
- [EDM 電子報管理 API](#edm-電子報管理-api)
- [部落格管理 API](#部落格管理-api)
- [自訂頁面管理 API](#自訂頁面管理-api)
- [首頁區塊 API](#首頁區塊-api)
- [彈跳廣告 API](#彈跳廣告-api)
- [庫存管理 API](#庫存管理-api)
- [倉庫管理 API](#倉庫管理-api)
- [帳號管理 API](#帳號管理-api)
- [員工管理 API](#員工管理-api)
- [出勤打卡 API](#出勤打卡-api)
- [相冊管理 API](#相冊管理-api)
- [系統設定 API](#系統設定-api)
- [商店設定 API](#商店設定-api)
- [物流設定 API](#物流設定-api)
- [金流設定 API](#金流設定-api)
- [通知設定 API](#通知設定-api)
- [操作日誌 API](#操作日誌-api)
- [儀表板 API](#儀表板-api)

---

## 商品相關 API

### 1. 商品列表查詢

**GET** `/api/products`

- **描述**: 分頁查詢商品列表
- **參數**:
  - `page` (int, optional): 頁碼，預設 0
  - `size` (int, optional): 每頁數量，預設 20
- **回應**: `PageProductDTO`

### 2. 商品詳情

**GET** `/api/products/{id}`

- **描述**: 取得單一商品的詳細資訊
- **參數**:
  - `id` (path, required): 商品 ID
- **回應**: `ProductDTO`

### 3. 搜尋商品

**GET** `/api/products/search`

- **描述**: 根據關鍵字搜尋商品
- **參數**:
  - `keyword` (query, required): 搜尋關鍵字
  - `page` (int, optional): 頁碼
  - `size` (int, optional): 每頁數量
- **回應**: `PageProductDTO`

### 4. 依分類查詢商品

**GET** `/api/products/category/{categoryId}`

- **描述**: 查詢特定分類下的商品
- **參數**:
  - `categoryId` (path, required): 分類 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageProductDTO`

### 5. 依狀態查詢商品

**GET** `/api/products/status/{status}`

- **描述**: 根據商品狀態篩選（通常用於顯示上架商品）
- **參數**:
  - `status` (path, required): DRAFT, ACTIVE, INACTIVE, OUT_OF_STOCK
- **回應**: `PageProductDTO`

### 6. 取得商品規格

**GET** `/api/product-specifications/product/{productId}`

- **描述**: 取得商品的所有規格選項（顏色、尺寸等）
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<ProductSpecificationDTO>`

### 7. 取得商品圖片

**GET** `/api/product-images/product/{productId}`

- **描述**: 取得商品的所有圖片
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<ProductImageDTO>`

### 8. 取得商品分類

**GET** `/api/product-categories`

- **描述**: 取得所有商品分類
- **回應**: `List<ProductCategoryDTO>`

### 9. 取得頂層分類

**GET** `/api/product-categories/top`

- **描述**: 取得主要分類（用於導航選單）
- **回應**: `List<ProductCategoryDTO>`

### 10. 取得子分類

**GET** `/api/product-categories/{parentId}/children`

- **描述**: 取得指定分類的子分類
- **參數**:
  - `parentId` (path, required): 父分類 ID
- **回應**: `List<ProductCategoryDTO>`

### 11. 上架商品

**PUT** `/api/products/{id}/activate`

- **描述**: 將商品設為上架狀態
- **參數**:
  - `id` (path, required): 商品 ID
- **回應**: `ProductDTO`

### 12. 下架商品

**PUT** `/api/products/{id}/deactivate`

- **描述**: 將商品設為下架狀態
- **參數**:
  - `id` (path, required): 商品 ID
- **回應**: `ProductDTO`

### 13. 從相冊添加圖片到商品

**POST** `/api/products/{id}/album-images`

- **描述**: 從相冊系統選擇圖片添加到商品
- **參數**:
  - `id` (path, required): 商品 ID
- **請求體**: 相冊圖片 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ProductDTO`

---

## 商品批次操作 API

### 1. 批次更新商品

**PUT** `/api/products/batch`

- **描述**: 批量更新多個商品的屬性
- **請求體**: `BatchUpdateProductDTO`
  ```json
  {
    "productIds": [1, 2, 3],
    "status": "ACTIVE",
    "categoryId": 5,
    "salePrice": 299.0
  }
  ```
- **回應**: `ApiResponseVoid`

### 2. 批次刪除商品

**DELETE** `/api/products/batch`

- **描述**: 批量刪除商品
- **請求體**: 商品 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ApiResponseVoid`

### 3. 批次上架商品

**PUT** `/api/products/batch/activate`

- **描述**: 批量上架商品
- **請求體**: 商品 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ApiResponseVoid`

### 4. 批次下架商品

**PUT** `/api/products/batch/deactivate`

- **描述**: 批量下架商品
- **請求體**: 商品 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ApiResponseVoid`

---

## 商品標籤 API

### 1. 取得所有標籤

**GET** `/api/product-tags`

- **描述**: 取得系統中的所有商品標籤
- **回應**: `List<ProductTag>`

### 2. 創建標籤

**POST** `/api/product-tags`

- **描述**: 創建新的商品標籤
- **請求體**: `ProductTag`
  ```json
  {
    "name": "新品",
    "color": "#FF0000",
    "description": "新上架商品"
  }
  ```
- **回應**: `ProductTag`

### 3. 更新標籤

**PUT** `/api/product-tags/{id}`

- **描述**: 更新標籤資訊
- **參數**:
  - `id` (path, required): 標籤 ID
- **請求體**: `ProductTag`
- **回應**: `ProductTag`

### 4. 刪除標籤

**DELETE** `/api/product-tags/{id}`

- **描述**: 刪除標籤
- **參數**:
  - `id` (path, required): 標籤 ID
- **回應**: `ApiResponseVoid`

### 5. 取得商品的所有標籤

**GET** `/api/product-tags/product/{productId}`

- **描述**: 取得指定商品的所有標籤
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<ProductTag>`

### 6. 為商品添加標籤

**POST** `/api/product-tags/product/{productId}/tag/{tagId}`

- **描述**: 為商品添加單個標籤
- **參數**:
  - `productId` (path, required): 商品 ID
  - `tagId` (path, required): 標籤 ID
- **回應**: `ApiResponseVoid`

### 7. 移除商品標籤

**DELETE** `/api/product-tags/product/{productId}/tag/{tagId}`

- **描述**: 移除商品的指定標籤
- **參數**:
  - `productId` (path, required): 商品 ID
  - `tagId` (path, required): 標籤 ID
- **回應**: `ApiResponseVoid`

### 8. 批量設置商品標籤

**PUT** `/api/product-tags/product/{productId}`

- **描述**: 替換商品的所有標籤（先清除後設置）
- **參數**:
  - `productId` (path, required): 商品 ID
- **請求體**: 標籤 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ApiResponseVoid`

---

## 商品規格 API

### 1. 添加商品規格

**POST** `/api/product-specifications`

- **描述**: 為商品添加單個規格
- **請求體**: `ProductSpecificationDTO`
  ```json
  {
    "productId": 1,
    "specName": "顏色:紅色,尺寸:L",
    "sku": "SKU001-RED-L",
    "price": 399.0,
    "stock": 100
  }
  ```
- **回應**: `ProductSpecificationDTO`

### 2. 批量創建商品規格

**POST** `/api/product-specifications/batch`

- **描述**: 批量添加多個商品規格
- **請求體**: `List<ProductSpecificationDTO>`
- **回應**: `List<ProductSpecificationDTO>`

### 3. 取得規格詳情

**GET** `/api/product-specifications/{id}`

- **描述**: 取得單一規格的詳細資訊
- **參數**:
  - `id` (path, required): 規格 ID
- **回應**: `ProductSpecificationDTO`

### 4. 更新商品規格

**PUT** `/api/product-specifications/{id}`

- **描述**: 更新規格資訊
- **參數**:
  - `id` (path, required): 規格 ID
- **請求體**: `ProductSpecificationDTO`
- **回應**: `ProductSpecificationDTO`

### 5. 刪除商品規格

**DELETE** `/api/product-specifications/{id}`

- **描述**: 刪除規格
- **參數**:
  - `id` (path, required): 規格 ID
- **回應**: `ApiResponseVoid`

### 6. 取得商品的所有規格

**GET** `/api/product-specifications/product/{productId}`

- **描述**: 取得商品的所有規格選項
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<ProductSpecificationDTO>`

---

## 商品圖片 API

### 1. 添加商品圖片

**POST** `/api/product-images`

- **描述**: 為商品添加圖片
- **請求體**: `ProductImageDTO`
  ```json
  {
    "productId": 1,
    "imageUrl": "/images/product1.jpg",
    "imageType": "MAIN",
    "isPrimary": true
  }
  ```
- **回應**: `ProductImageDTO`

### 2. 更新商品圖片

**PUT** `/api/product-images/{id}`

- **描述**: 更新圖片資訊
- **參數**:
  - `id` (path, required): 圖片 ID
- **請求體**: `ProductImageDTO`
- **回應**: `ProductImageDTO`

### 3. 刪除商品圖片

**DELETE** `/api/product-images/{id}`

- **描述**: 刪除圖片
- **參數**:
  - `id` (path, required): 圖片 ID
- **回應**: `ApiResponseVoid`

### 4. 設置為主圖

**PUT** `/api/product-images/{id}/set-primary`

- **描述**: 將指定圖片設置為商品主圖
- **參數**:
  - `id` (path, required): 圖片 ID
- **回應**: `ProductImageDTO`

### 5. 取得商品的所有圖片

**GET** `/api/product-images/product/{productId}`

- **描述**: 取得商品的所有圖片
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<ProductImageDTO>`

---

## 商品分類 API

### 1. 取得所有分類

**GET** `/api/product-categories`

- **描述**: 取得所有商品分類
- **回應**: `List<ProductCategoryDTO>`

### 2. 創建分類

**POST** `/api/product-categories`

- **描述**: 創建新的商品分類
- **請求體**: `ProductCategoryDTO`
  ```json
  {
    "name": "服飾",
    "parentId": 0,
    "description": "各類服飾商品",
    "enabled": true
  }
  ```
- **回應**: `ProductCategoryDTO`

### 3. 取得分類詳情

**GET** `/api/product-categories/{id}`

- **描述**: 取得單一分類的詳細資訊
- **參數**:
  - `id` (path, required): 分類 ID
- **回應**: `ProductCategoryDTO`

### 4. 更新分類

**PUT** `/api/product-categories/{id}`

- **描述**: 更新分類資訊
- **參數**:
  - `id` (path, required): 分類 ID
- **請求體**: `ProductCategoryDTO`
- **回應**: `ProductCategoryDTO`

### 5. 刪除分類

**DELETE** `/api/product-categories/{id}`

- **描述**: 刪除分類
- **參數**:
  - `id` (path, required): 分類 ID
- **回應**: `ApiResponseVoid`

### 6. 取得頂層分類

**GET** `/api/product-categories/top`

- **描述**: 取得主要分類（用於導航選單）
- **回應**: `List<ProductCategoryDTO>`

### 7. 取得子分類

**GET** `/api/product-categories/{parentId}/children`

- **描述**: 取得指定分類的子分類
- **參數**:
  - `parentId` (path, required): 父分類 ID
- **回應**: `List<ProductCategoryDTO>`

### 8. 取得已啟用的分類

**GET** `/api/product-categories/enabled`

- **描述**: 取得所有已啟用的分類
- **回應**: `List<ProductCategoryDTO>`

---

## 購物車操作 API

> **注意**: 此 API 文檔中未發現專門的購物車 CRUD 接口。
> 購物車資料可能：
>
> 1. 由前端 LocalStorage/IndexedDB 管理
> 2. 使用訂單草稿功能 (isDraft=true)
> 3. 需要後端補充購物車相關 API

### 可用的替代方案

#### 使用訂單草稿作為購物車

**POST** `/api/orders`

- **描述**: 創建訂單（設定 `isDraft: true` 作為購物車）
- **請求體**: `OrderDTO`
  ```json
  {
    "customerId": 1,
    "status": "PENDING_PAYMENT",
    "pickupType": "DELIVERY",
    "isDraft": true,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 399.0
      }
    ]
  }
  ```

#### 查詢暫存訂單（購物車）

**GET** `/api/orders/draft`

- **描述**: 查詢所有暫存的未完成訂單
- **參數**:
  - `isDraft` (boolean): true
  - `page`, `size`: 分頁參數
- **回應**: `PageOrderDTO`

#### 購物車未結帳提醒

**POST** `/api/crm/cart-reminders`

- **描述**: 創建購物車提醒記錄
- **請求體**: `CartReminderDTO`

**GET** `/api/crm/cart-reminders/pending`

- **描述**: 取得待發送的購物車提醒
- **參數**:
  - `hours` (int, optional): 截止時間（小時前），預設 24

---

## 訂單相關 API

### 1. 創建訂單（結帳）

**POST** `/api/orders`

- **描述**: 從購物車創建正式訂單
- **請求體**: `OrderDTO`
  ```json
  {
    "customerId": 1,
    "customerName": "王小明",
    "customerPhone": "0912345678",
    "customerEmail": "wang@example.com",
    "status": "PENDING_PAYMENT",
    "pickupType": "DELIVERY",
    "subtotalAmount": 1000.0,
    "discountAmount": 100.0,
    "shippingFee": 80.0,
    "totalAmount": 980.0,
    "shippingAddress": "台北市信義區...",
    "items": [
      {
        "productId": 1,
        "productName": "商品名稱",
        "quantity": 2,
        "unitPrice": 500.0
      }
    ]
  }
  ```
- **回應**: `OrderDTO`

### 2. 取得訂單詳情

**GET** `/api/orders/{id}`

- **描述**: 查詢訂單詳細資料
- **參數**:
  - `id` (path, required): 訂單 ID
- **回應**: `OrderDTO`

### 3. 更新訂單

**PUT** `/api/orders/{id}`

- **描述**: 更新訂單資料（修改購物車）
- **參數**:
  - `id` (path, required): 訂單 ID
- **請求體**: `OrderDTO`
- **回應**: `OrderDTO`

### 4. 根據客戶查詢訂單

**GET** `/api/orders/customer/{customerId}`

- **描述**: 查詢會員的所有訂單
- **參數**:
  - `customerId` (path, required): 客戶 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageOrderDTO`

### 5. 根據狀態查詢訂單

**GET** `/api/orders/status/{status}`

- **描述**: 根據訂單狀態查詢
- **參數**:
  - `status` (path): PENDING_PAYMENT, PROCESSING, COMPLETED, CANCELLED, REFUNDED

### 6. 新增訂單折扣

**POST** `/api/orders/discounts`

- **描述**: 為訂單添加折扣（使用優惠券）
- **請求體**: `OrderDiscountDTO`
  ```json
  {
    "orderId": 1,
    "discountType": "COUPON",
    "discountCode": "SUMMER2024",
    "discountAmount": 100.0
  }
  ```

### 7. 取得訂單折扣

**GET** `/api/orders/discounts/order/{orderId}`

- **描述**: 查詢訂單的所有折扣
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderDiscountDTO>`

### 8. 刪除訂單

**DELETE** `/api/orders/{id}`

- **描述**: 刪除單一訂單
- **參數**:
  - `id` (path, required): 訂單 ID
- **回應**: `ApiResponseVoid`

### 9. 更新訂單狀態

**PATCH** `/api/orders/{id}/status`

- **描述**: 更新訂單狀態
- **參數**:
  - `id` (path, required): 訂單 ID
  - `status` (query, required): 新狀態 (PENDING_PAYMENT, PROCESSING, COMPLETED, CANCELLED, REFUNDED)
  - `operatorId` (query, optional): 操作者 ID
  - `operatorName` (query, optional): 操作者名稱
- **回應**: `OrderDTO`

### 10. 查詢暫存訂單（購物車）

**GET** `/api/orders/draft`

- **描述**: 查詢所有暫存的未完成訂單
- **參數**:
  - `isDraft` (query): true
  - `page`, `size`: 分頁參數
- **回應**: `PageOrderDTO`

---

## 訂單批次操作 API

### 1. 批量更新訂單狀態

**PUT** `/api/orders/batch/status`

- **描述**: 批量更新多個訂單的狀態
- **請求體**: `BatchOrderUpdateDTO`
  ```json
  {
    "orderIds": [1, 2, 3],
    "targetStatus": "PROCESSING",
    "operatorId": 1,
    "operatorName": "管理員",
    "notes": "批次處理訂單"
  }
  ```
- **回應**: `List<Long>` (已更新的訂單 ID 列表)

### 2. 批量刪除訂單

**DELETE** `/api/orders/batch`

- **描述**: 批量刪除訂單
- **請求體**: 訂單 ID 列表
  ```json
  [1, 2, 3]
  ```
- **回應**: `ApiResponseVoid`

### 3. 批量導出訂單資料

**POST** `/api/orders/batch/export`

- **描述**: 批量導出訂單資料為 Excel/CSV
- **請求體**: 訂單 ID 列表（為空則導出全部）
  ```json
  [1, 2, 3]
  ```
- **回應**: `List<Order>` (訂單列表)

---

## 訂單查詢 API

### 1. 多條件查詢訂單

**POST** `/api/orders/search`

- **描述**: 根據多條件篩選訂單
- **請求體**: `OrderQueryDTO`
  ```json
  {
    "orderNumber": "ORD20240101001",
    "customerId": 1,
    "status": "COMPLETED",
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "minAmount": 100.0,
    "maxAmount": 10000.0,
    "page": 0,
    "size": 20
  }
  ```
- **回應**: `PageOrderDTO`

### 2. 根據訂單編號查詢

**GET** `/api/orders/search/order-number/{orderNumber}`

- **描述**: 根據訂單編號精確查詢
- **參數**:
  - `orderNumber` (path, required): 訂單編號
- **回應**: `OrderDTO`

### 3. 根據客戶姓名模糊查詢

**GET** `/api/orders/search/customer-name`

- **描述**: 根據客戶姓名模糊搜尋訂單
- **參數**:
  - `customerName` (query, required): 客戶姓名
- **回應**: `List<OrderDTO>`

---

## 訂單統計 API

### 1. 取得訂單統計資料

**GET** `/api/orders/statistics`

- **描述**: 取得指定時間範圍的訂單統計資料
- **參數**:
  - `startDate` (query, required): 開始日期
  - `endDate` (query, required): 結束日期
- **回應**: `OrderStatisticsDTO`
  ```typescript
  {
    totalOrders: 1000,
    totalAmount: 500000.0,
    averageOrderAmount: 500.0,
    dailyOrderTrend: {
      "2024-01-01": 10,
      "2024-01-02": 15
    },
    statusDistribution: {
      "COMPLETED": 800,
      "CANCELLED": 50
    }
  }
  ```

### 2. 取得今日訂單統計

**GET** `/api/orders/statistics/today`

- **描述**: 取得今日訂單統計資料
- **回應**: `OrderStatisticsDTO`

### 3. 取得本週訂單統計

**GET** `/api/orders/statistics/week`

- **描述**: 取得本週訂單統計資料
- **回應**: `OrderStatisticsDTO`

### 4. 取得本月訂單統計

**GET** `/api/orders/statistics/month`

- **描述**: 取得本月訂單統計資料
- **回應**: `OrderStatisticsDTO`

### 5. 根據狀態取得統計

**GET** `/api/orders/statistics/status/{status}`

- **描述**: 根據訂單狀態取得統計資料
- **參數**:
  - `status` (path, required): 訂單狀態
- **回應**: `Map<String, Object>`

---

## 訂單物流 API

### 1. 創建物流記錄

**POST** `/api/orders/shipments`

- **描述**: 建立訂單出貨單
- **請求體**: `OrderShipmentDTO`
  ```json
  {
    "orderId": 1,
    "shippingStatus": "PENDING",
    "recipientName": "王小明",
    "recipientPhone": "0912345678",
    "recipientAddress": "台北市信義區..."
  }
  ```
- **回應**: `OrderShipmentDTO`

### 2. 取得訂單物流記錄

**GET** `/api/orders/shipments/order/{orderId}`

- **描述**: 查詢訂單的所有物流記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderShipmentDTO>`

### 3. 根據物流單號查詢

**GET** `/api/orders/shipments/tracking/{trackingNumber}`

- **描述**: 追蹤物流狀態
- **參數**:
  - `trackingNumber` (path, required): 物流單號
- **回應**: `OrderShipmentDTO`

### 4. 更新物流單號

**PATCH** `/api/orders/shipments/{shipmentId}/tracking`

- **描述**: 更新物流追蹤號碼
- **參數**:
  - `shipmentId` (path, required): 物流記錄 ID
  - `trackingNumber` (query, required): 物流單號
- **回應**: `OrderShipmentDTO`

### 5. 更新物流狀態

**PATCH** `/api/orders/shipments/{shipmentId}/status`

- **描述**: 更新物流狀態
- **參數**:
  - `shipmentId` (path, required): 物流記錄 ID
  - `status` (query, required): 物流狀態 (PENDING, SHIPPED, DELIVERED, RETURNED)
- **回應**: `OrderShipmentDTO`

---

## 訂單付款 API

### 1. 創建付款記錄

**POST** `/api/orders/payments`

- **描述**: 創建付款記錄
- **請求體**: `OrderPaymentDTO`
  ```json
  {
    "orderId": 1,
    "paymentStatus": "PENDING",
    "paymentMethod": "CREDIT_CARD",
    "paymentAmount": 980.0
  }
  ```
- **回應**: `OrderPaymentDTO`

### 2. 取得訂單付款記錄

**GET** `/api/orders/payments/order/{orderId}`

- **描述**: 查詢訂單的所有付款記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderPaymentDTO>`

### 3. 更新付款狀態

**PATCH** `/api/orders/payments/{paymentId}/status`

- **描述**: 更新付款狀態
- **參數**:
  - `paymentId` (path, required): 付款記錄 ID
  - `status` (query, required): 付款狀態 (PENDING, PAID, REFUNDING, REFUNDED)
- **回應**: `OrderPaymentDTO`

### 4. 申請退款

**POST** `/api/orders/payments/{paymentId}/refund`

- **描述**: 申請退款
- **參數**:
  - `paymentId` (path, required): 付款記錄 ID
  - `refundAmount` (query, required): 退款金額
- **回應**: `OrderPaymentDTO`

---

## 訂單折扣 API

### 1. 新增訂單折扣

**POST** `/api/orders/discounts`

- **描述**: 為訂單添加折扣
- **請求體**: `OrderDiscountDTO`
  ```json
  {
    "orderId": 1,
    "discountType": "COUPON",
    "discountCode": "SUMMER2024",
    "discountAmount": 100.0
  }
  ```
- **回應**: `OrderDiscountDTO`

### 2. 取得訂單的所有折扣

**GET** `/api/orders/discounts/order/{orderId}`

- **描述**: 查詢訂單的所有折扣記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderDiscountDTO>`

### 3. 根據折扣代碼查詢

**GET** `/api/orders/discounts/code/{discountCode}`

- **描述**: 根據折扣代碼查詢折扣記錄
- **參數**:
  - `discountCode` (path, required): 折扣代碼
- **回應**: `List<OrderDiscountDTO>`

### 4. 刪除訂單折扣

**DELETE** `/api/orders/discounts/{discountId}`

- **描述**: 刪除訂單折扣記錄
- **參數**:
  - `discountId` (path, required): 折扣記錄 ID
- **回應**: `ApiResponseVoid`

---

## 訂單問與答 API

### 1. 新增問題

**POST** `/api/orders/qa`

- **描述**: 顧客或店家對訂單提問
- **請求體**: `OrderQADTO`
  ```json
  {
    "orderId": 1,
    "askerType": "CUSTOMER",
    "askerId": 1,
    "askerName": "王小明",
    "question": "請問訂單何時出貨？"
  }
  ```
- **回應**: `OrderQADTO`

### 2. 回答問題

**PATCH** `/api/orders/qa/{qaId}/answer`

- **描述**: 店家或客服回答訂單問題
- **參數**:
  - `qaId` (path, required): Q&A ID
  - `answer` (query, required): 回答內容
  - `answererId` (query, required): 回答者 ID
  - `answererName` (query, required): 回答者名稱
- **回應**: `OrderQADTO`

### 3. 取得訂單的所有問答

**GET** `/api/orders/qa/order/{orderId}`

- **描述**: 取得訂單的所有問答記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderQADTO>`

### 4. 分頁取得訂單問答

**GET** `/api/orders/qa/order/{orderId}/page`

- **描述**: 分頁查詢訂單問答
- **參數**:
  - `orderId` (path, required): 訂單 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageOrderQADTO`

### 5. 取得客戶的所有提問

**GET** `/api/orders/qa/asker/{askerId}`

- **描述**: 取得指定客戶的所有提問
- **參數**:
  - `askerId` (path, required): 提問者 ID
- **回應**: `List<OrderQADTO>`

### 6. 刪除問答

**DELETE** `/api/orders/qa/{qaId}`

- **描述**: 刪除問答記錄
- **參數**:
  - `qaId` (path, required): Q&A ID
- **回應**: `ApiResponseVoid`

---

## 訂單歷史 API

### 1. 取得訂單歷史記錄

**GET** `/api/orders/history/order/{orderId}`

- **描述**: 查看訂單的所有操作記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderHistoryDTO>`

### 2. 分頁取得訂單歷史記錄

**GET** `/api/orders/history/order/{orderId}/page`

- **描述**: 分頁查詢訂單歷史記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageOrderHistoryDTO`

---

## 顧客黑名單 API

### 1. 新增顧客到黑名單

**POST** `/api/orders/blacklist`

- **描述**: 將顧客加入黑名單，拒絕其交易
- **請求體**: `CustomerBlacklistDTO`
  ```json
  {
    "customerId": 1,
    "customerName": "王小明",
    "customerPhone": "0912345678",
    "reason": "惡意退貨"
  }
  ```
- **回應**: `CustomerBlacklistDTO`

### 2. 分頁取得黑名單

**GET** `/api/orders/blacklist`

- **描述**: 分頁查詢黑名單記錄
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageCustomerBlacklistDTO`

### 3. 取得顧客的黑名單記錄

**GET** `/api/orders/blacklist/customer/{customerId}`

- **描述**: 查詢指定顧客的黑名單記錄
- **參數**:
  - `customerId` (path, required): 客戶 ID
- **回應**: `CustomerBlacklistDTO`

### 4. 檢查顧客是否在黑名單中

**GET** `/api/orders/blacklist/check/{customerId}`

- **描述**: 檢查顧客是否被列入黑名單
- **參數**:
  - `customerId` (path, required): 客戶 ID
- **回應**: `Boolean`

### 5. 取得所有啟用的黑名單

**GET** `/api/orders/blacklist/active`

- **描述**: 取得所有啟用的黑名單記錄
- **回應**: `List<CustomerBlacklistDTO>`

### 6. 從黑名單移除

**PATCH** `/api/orders/blacklist/{blacklistId}/remove`

- **描述**: 將顧客從黑名單移除
- **參數**:
  - `blacklistId` (path, required): 黑名單記錄 ID
- **回應**: `CustomerBlacklistDTO`

### 7. 更新黑名單原因

**PATCH** `/api/orders/blacklist/{blacklistId}/reason`

- **描述**: 更新黑名單原因
- **參數**:
  - `blacklistId` (path, required): 黑名單記錄 ID
  - `reason` (query, required): 黑名單原因
- **回應**: `CustomerBlacklistDTO`

---

## 會員與身份驗證 API

### 1. 用戶註冊

**POST** `/api/auth/register`

- **描述**: 註冊新會員
- **請求體**: `RegisterRequest`
  ```json
  {
    "username": "user123",
    "email": "user@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }
  ```
- **回應**: `AuthResponse` (包含 JWT token)

### 2. 用戶登入

**POST** `/api/auth/login`

- **描述**: 會員登入
- **請求體**: `LoginRequest`
  ```json
  {
    "username": "user123",
    "password": "password123"
  }
  ```
- **回應**: `AuthResponse` (JWT token)

### 3. 取得個人資料

**GET** `/api/auth/profile`

- **描述**: 取得當前登入會員的個人資料
- **需要**: Bearer Token
- **回應**: `UserDTO`

### 4. 更新個人資料

**PUT** `/api/auth/profile`

- **描述**: 更新會員個人資料
- **需要**: Bearer Token
- **請求體**: `UpdateProfileRequest`
- **回應**: `UserDTO`

### 5. 取得會員詳情

**GET** `/api/crm/members/{id}`

- **描述**: 查詢會員詳細資訊
- **參數**:
  - `id` (path, required): 會員 ID
- **回應**: `MemberDTO`

### 6. 依電子郵件取得會員

**GET** `/api/crm/members/email/{email}`

- **描述**: 通過 email 查詢會員
- **參數**:
  - `email` (path, required): 電子郵件
- **回應**: `MemberDTO`

### 7. 創建會員

**POST** `/api/crm/members`

- **描述**: 創建新會員
- **請求體**: `MemberDTO`
  ```json
  {
    "name": "王小明",
    "email": "wang@example.com",
    "phone": "0912345678",
    "birthday": "1990-01-01"
  }
  ```
- **回應**: `MemberDTO`

### 8. 更新會員

**PUT** `/api/crm/members/{id}`

- **描述**: 更新會員資訊
- **參數**:
  - `id` (path, required): 會員 ID
- **請求體**: `MemberDTO`
- **回應**: `MemberDTO`

### 9. 刪除會員

**DELETE** `/api/crm/members/{id}`

- **描述**: 刪除會員
- **參數**:
  - `id` (path, required): 會員 ID
- **回應**: `ApiResponseVoid`

### 10. 分頁查詢會員

**GET** `/api/crm/members`

- **描述**: 分頁查詢所有會員
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberDTO`

### 11. 依狀態查詢會員

**GET** `/api/crm/members/status/{status}`

- **描述**: 根據會員狀態查詢
- **參數**:
  - `status` (path, required): 會員狀態 (ACTIVE, INACTIVE, SUSPENDED, DELETED)
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberDTO`

### 12. 搜尋會員

**GET** `/api/crm/members/search`

- **描述**: 根據關鍵字搜尋會員
- **參數**:
  - `keyword` (query, required): 搜尋關鍵字
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberDTO`

### 13. 依等級查詢會員

**GET** `/api/crm/members/level/{levelId}`

- **描述**: 查詢指定等級的所有會員
- **參數**:
  - `levelId` (path, required): 等級 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberDTO`

### 14. 更新會員狀態

**PUT** `/api/crm/members/{id}/status`

- **描述**: 更新會員狀態
- **參數**:
  - `id` (path, required): 會員 ID
  - `status` (query, required): 會員狀態
- **回應**: `MemberDTO`

### 15. 更新會員等級

**PUT** `/api/crm/members/{id}/level`

- **描述**: 更新會員等級
- **參數**:
  - `id` (path, required): 會員 ID
  - `levelId` (query, required): 等級 ID
- **回應**: `MemberDTO`

### 16. 增加會員積點

**POST** `/api/crm/members/{id}/points/add`

- **描述**: 為會員增加積點
- **參數**:
  - `id` (path, required): 會員 ID
  - `points` (query, required): 積點數量
- **回應**: `MemberDTO`

### 17. 扣除會員積點

**POST** `/api/crm/members/{id}/points/deduct`

- **描述**: 扣除會員積點
- **參數**:
  - `id` (path, required): 會員 ID
  - `points` (query, required): 積點數量
- **回應**: `MemberDTO`

---

## 會員等級管理 API

### 1. 分頁查詢會員等級

**GET** `/api/crm/member-levels`

- **描述**: 分頁查詢所有會員等級
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberLevelDTO`

### 2. 取得所有會員等級

**GET** `/api/crm/member-levels/all`

- **描述**: 取得所有會員等級（不分頁）
- **回應**: `List<MemberLevelDTO>`

### 3. 取得已啟用的會員等級

**GET** `/api/crm/member-levels/enabled`

- **描述**: 取得所有已啟用的會員等級
- **回應**: `List<MemberLevelDTO>`

### 4. 取得會員等級詳情

**GET** `/api/crm/member-levels/{id}`

- **描述**: 取得單一會員等級的詳細資訊
- **參數**:
  - `id` (path, required): 等級 ID
- **回應**: `MemberLevelDTO`

### 5. 創建會員等級

**POST** `/api/crm/member-levels`

- **描述**: 創建新的會員等級
- **請求體**: `MemberLevelDTO`
  ```json
  {
    "name": "黃金會員",
    "levelOrder": 1,
    "minSpendAmount": 10000.0,
    "discountRate": 0.95,
    "pointsMultiplier": 1.5
  }
  ```
- **回應**: `MemberLevelDTO`

### 6. 更新會員等級

**PUT** `/api/crm/member-levels/{id}`

- **描述**: 更新會員等級資訊
- **參數**:
  - `id` (path, required): 等級 ID
- **請求體**: `MemberLevelDTO`
- **回應**: `MemberLevelDTO`

### 7. 刪除會員等級

**DELETE** `/api/crm/member-levels/{id}`

- **描述**: 刪除會員等級
- **參數**:
  - `id` (path, required): 等級 ID
- **回應**: `ApiResponseVoid`

### 8. 切換會員等級啟用狀態

**PUT** `/api/crm/member-levels/{id}/toggle-enabled`

- **描述**: 切換會員等級的啟用狀態
- **參數**:
  - `id` (path, required): 等級 ID
- **回應**: `MemberLevelDTO`

---

## 會員群組管理 API

### 1. 分頁查詢會員群組

**GET** `/api/crm/member-groups`

- **描述**: 分頁查詢所有會員群組
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageMemberGroupDTO`

### 2. 取得已啟用的會員群組

**GET** `/api/crm/member-groups/enabled`

- **描述**: 取得所有已啟用的會員群組
- **回應**: `List<MemberGroupDTO>`

### 3. 取得會員群組詳情

**GET** `/api/crm/member-groups/{id}`

- **描述**: 取得單一會員群組的詳細資訊
- **參數**:
  - `id` (path, required): 群組 ID
- **回應**: `MemberGroupDTO`

### 4. 創建會員群組

**POST** `/api/crm/member-groups`

- **描述**: 創建新的會員群組
- **請求體**: `MemberGroupDTO`
  ```json
  {
    "name": "VIP 客戶群組",
    "description": "高價值客戶群組",
    "enabled": true
  }
  ```
- **回應**: `MemberGroupDTO`

### 5. 更新會員群組

**PUT** `/api/crm/member-groups/{id}`

- **描述**: 更新會員群組資訊
- **參數**:
  - `id` (path, required): 群組 ID
- **請求體**: `MemberGroupDTO`
- **回應**: `MemberGroupDTO`

### 6. 刪除會員群組

**DELETE** `/api/crm/member-groups/{id}`

- **描述**: 刪除會員群組
- **參數**:
  - `id` (path, required): 群組 ID
- **回應**: `ApiResponseVoid`

### 7. 取得群組內的會員

**GET** `/api/crm/member-groups/{groupId}/members`

- **描述**: 取得群組內的所有會員 ID
- **參數**:
  - `groupId` (path, required): 群組 ID
- **回應**: `List<Long>` (會員 ID 列表)

### 8. 取得會員所屬群組

**GET** `/api/crm/member-groups/member/{memberId}`

- **描述**: 取得會員所屬的所有群組 ID
- **參數**:
  - `memberId` (path, required): 會員 ID
- **回應**: `List<Long>` (群組 ID 列表)

### 9. 將會員加入群組

**POST** `/api/crm/member-groups/{groupId}/members/{memberId}`

- **描述**: 將會員加入指定群組
- **參數**:
  - `groupId` (path, required): 群組 ID
  - `memberId` (path, required): 會員 ID
- **回應**: `ApiResponseVoid`

### 10. 將會員從群組移除

**DELETE** `/api/crm/member-groups/{groupId}/members/{memberId}`

- **描述**: 將會員從群組中移除
- **參數**:
  - `groupId` (path, required): 群組 ID
  - `memberId` (path, required): 會員 ID
- **回應**: `ApiResponseVoid`

---

## 積點管理 API

(保留原有內容)

---

## 獎勵制度管理 API

(保留原有內容)

---

## 支付相關 API

### 1. 創建支付請求

**POST** `/api/payment-gateway/create`

- **描述**: 透過支付閘道創建支付請求
- **參數**:
  - `gateway` (query, required): LINE_PAY, ECPAY, MANUAL
- **請求體**: `PaymentRequestDTO`
  ```json
  {
    "orderId": 1,
    "orderNumber": "ORD20240101001",
    "amount": 980.0,
    "currency": "TWD",
    "productName": "購物車商品",
    "customerName": "王小明",
    "customerEmail": "wang@example.com",
    "customerPhone": "0912345678"
  }
  ```
- **回應**: `PaymentResponseDTO` (包含支付 URL)

### 2. 確認支付

**POST** `/api/payment-gateway/confirm`

- **描述**: 確認支付完成
- **參數**:
  - `gateway` (query, required): 支付閘道類型
- **請求體**: `PaymentConfirmDTO`
- **回應**: `PaymentResponseDTO`

### 3. 查詢支付狀態

**GET** `/api/payment-gateway/query/{gateway}/{transactionId}`

- **描述**: 查詢支付交易狀態
- **參數**:
  - `gateway` (path, required): 支付閘道
  - `transactionId` (path, required): 交易 ID
- **回應**: `PaymentResponseDTO`

### 4. 取消支付

**POST** `/api/payment-gateway/cancel/{gateway}/{transactionId}`

- **描述**: 取消支付交易
- **回應**: `PaymentResponseDTO`

### 5. 取得支付設定

**GET** `/api/payment-management/settings`

- **描述**: 取得所有可用的支付方式
- **回應**: `List<PaymentSetting>`

### 6. 創建訂單付款記錄

**POST** `/api/orders/payments`

- **描述**: 創建付款記錄
- **請求體**: `OrderPaymentDTO`

### 7. 取得訂單付款記錄

**GET** `/api/orders/payments/order/{orderId}`

- **描述**: 查詢訂單的付款記錄
- **回應**: `List<OrderPaymentDTO>`

---

## 物流相關 API

### 1. 取得已啟用的物流設定

**GET** `/api/system/shipping-config/enabled`

- **描述**: 取得可用的配送方式
- **回應**: `List<ShippingConfigDTO>`

### 2. 創建物流記錄

**POST** `/api/orders/shipments`

- **描述**: 建立訂單出貨記錄
- **請求體**: `OrderShipmentDTO`
  ```json
  {
    "orderId": 1,
    "shippingStatus": "PENDING",
    "recipientName": "王小明",
    "recipientPhone": "0912345678",
    "recipientAddress": "台北市信義區..."
  }
  ```

### 3. 取得訂單物流記錄

**GET** `/api/orders/shipments/order/{orderId}`

- **描述**: 查詢訂單的物流記錄
- **參數**:
  - `orderId` (path, required): 訂單 ID
- **回應**: `List<OrderShipmentDTO>`

### 4. 根據物流單號查詢

**GET** `/api/orders/shipments/tracking/{trackingNumber}`

- **描述**: 追蹤物流狀態
- **參數**:
  - `trackingNumber` (path, required): 物流單號
- **回應**: `OrderShipmentDTO`

---

## 優惠與促銷 API

### 1. 查詢營銷活動

**GET** `/api/marketing/campaigns`

- **描述**: 取得進行中的促銷活動
- **參數**:
  - `page`, `pageSize`: 分頁參數
  - `status` (optional): DRAFT, ACTIVE, ENDED, SCHEDULED
  - `type` (optional): DISCOUNT, PROMOTION, FLASH_SALE, FREE_SHIPPING, COUPON
- **回應**: `MapStringObject` (包含活動列表)

### 2. 取得活動詳情

**GET** `/api/marketing/campaigns/{id}`

- **描述**: 查詢單一營銷活動詳情
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `MarketingCampaignDTO`

### 3. 創建營銷活動

**POST** `/api/marketing/campaigns`

- **描述**: 創建新的營銷活動
- **請求體**: `MarketingCampaignDTO`
  ```json
  {
    "name": "雙11購物節",
    "description": "全館商品8折優惠",
    "type": "DISCOUNT",
    "status": "ACTIVE",
    "startDate": "2024-11-11",
    "endDate": "2024-11-12",
    "discountRate": 20.0
  }
  ```
- **回應**: `MarketingCampaignDTO`

### 4. 更新營銷活動

**PUT** `/api/marketing/campaigns/{id}`

- **描述**: 更新營銷活動資訊
- **參數**:
  - `id` (path, required): 活動 ID
- **請求體**: `MarketingCampaignDTO`
- **回應**: `MarketingCampaignDTO`

### 5. 刪除營銷活動

**DELETE** `/api/marketing/campaigns/{id}`

- **描述**: 刪除營銷活動
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `ApiResponseVoid`

### 6. 更新活動狀態

**PATCH** `/api/marketing/campaigns/{id}/status`

- **描述**: 更新活動狀態
- **參數**:
  - `id` (path, required): 活動 ID
- **請求體**: 狀態對象
- **回應**: `MarketingCampaignDTO`

---

## 購物車未結帳提醒 API

### 1. 分頁查詢購物車提醒

**GET** `/api/crm/cart-reminders`

- **描述**: 分頁查詢購物車提醒記錄
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageCartReminderDTO`

### 2. 創建購物車提醒

**POST** `/api/crm/cart-reminders`

- **描述**: 創建購物車提醒記錄
- **請求體**: `CartReminderDTO`
  ```json
  {
    "memberId": 1,
    "cartSummary": "商品A x1, 商品B x2",
    "itemCount": 3
  }
  ```
- **回應**: `CartReminderDTO`

### 3. 取得購物車提醒詳情

**GET** `/api/crm/cart-reminders/{id}`

- **描述**: 取得單一購物車提醒的詳細資訊
- **參數**:
  - `id` (path, required): 提醒 ID
- **回應**: `CartReminderDTO`

### 4. 更新購物車提醒

**PUT** `/api/crm/cart-reminders/{id}`

- **描述**: 更新購物車提醒資訊
- **參數**:
  - `id` (path, required): 提醒 ID
- **請求體**: `CartReminderDTO`
- **回應**: `CartReminderDTO`

### 5. 刪除購物車提醒

**DELETE** `/api/crm/cart-reminders/{id}`

- **描述**: 刪除購物車提醒
- **參數**:
  - `id` (path, required): 提醒 ID
- **回應**: `ApiResponseVoid`

### 6. 取得待發送的提醒

**GET** `/api/crm/cart-reminders/pending`

- **描述**: 取得待發送的購物車提醒
- **參數**:
  - `hours` (query, optional): 截止時間（小時前），預設 24
- **回應**: `List<CartReminderDTO>`

### 7. 取得會員的購物車提醒

**GET** `/api/crm/cart-reminders/member/{memberId}`

- **描述**: 取得指定會員的購物車提醒記錄
- **參數**:
  - `memberId` (path, required): 會員 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageCartReminderDTO`

### 8. 發送購物車提醒

**POST** `/api/crm/cart-reminders/{id}/send`

- **描述**: 發送單個購物車提醒
- **參數**:
  - `id` (path, required): 提醒 ID
- **回應**: `CartReminderDTO`

### 9. 批次發送待發送的提醒

**POST** `/api/crm/cart-reminders/send-pending`

- **描述**: 批次發送所有待發送的提醒
- **參數**:
  - `hoursThreshold` (query, optional): 時間閾值（小時），預設 24
- **回應**: `ApiResponseVoid`

---

## EDM 電子報管理 API

### 1. 分頁查詢 EDM 活動

**GET** `/api/crm/edm`

- **描述**: 分頁查詢所有 EDM 活動
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageEdmCampaignDTO`

### 2. 取得 EDM 活動詳情

**GET** `/api/crm/edm/{id}`

- **描述**: 取得單一 EDM 活動的詳細資訊
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `EdmCampaignDTO`

### 3. 創建 EDM 活動

**POST** `/api/crm/edm`

- **描述**: 創建新的 EDM 活動
- **請求體**: `EdmCampaignDTO`
  ```json
  {
    "name": "週年慶促銷活動",
    "subject": "週年慶優惠，全館 8 折起！",
    "content": "<html>...</html>",
    "status": "DRAFT",
    "targetGroupId": 1
  }
  ```
- **回應**: `EdmCampaignDTO`

### 4. 更新 EDM 活動

**PUT** `/api/crm/edm/{id}`

- **描述**: 更新 EDM 活動資訊
- **參數**:
  - `id` (path, required): 活動 ID
- **請求體**: `EdmCampaignDTO`
- **回應**: `EdmCampaignDTO`

### 5. 刪除 EDM 活動

**DELETE** `/api/crm/edm/{id}`

- **描述**: 刪除 EDM 活動
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `ApiResponseVoid`

### 6. 依狀態查詢 EDM 活動

**GET** `/api/crm/edm/status/{status}`

- **描述**: 根據 EDM 狀態查詢
- **參數**:
  - `status` (path, required): EDM 狀態 (DRAFT, SCHEDULED, SENDING, SENT, FAILED, CANCELLED)
  - `page`, `size`: 分頁參數
- **回應**: `PageEdmCampaignDTO`

### 7. 發送 EDM 活動

**POST** `/api/crm/edm/{id}/send`

- **描述**: 立即發送 EDM 活動
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `EdmCampaignDTO`

### 8. 排程 EDM 活動

**POST** `/api/crm/edm/{id}/schedule`

- **描述**: 排程 EDM 活動發送時間
- **參數**:
  - `id` (path, required): 活動 ID
  - `scheduledAt` (query, required): 排程時間
- **回應**: `EdmCampaignDTO`

### 9. 取消 EDM 活動

**POST** `/api/crm/edm/{id}/cancel`

- **描述**: 取消 EDM 活動
- **參數**:
  - `id` (path, required): 活動 ID
- **回應**: `EdmCampaignDTO`

### 10. 取得 EDM 發送紀錄

**GET** `/api/crm/edm/{id}/logs`

- **描述**: 取得 EDM 活動的發送紀錄
- **參數**:
  - `id` (path, required): 活動 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageEdmSendLog`

---

## 部落格管理 API

### 1. 分頁查詢部落格文章

**GET** `/api/crm/blog`

- **描述**: 分頁查詢所有部落格文章
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageBlogPostDTO`

### 2. 取得部落格文章詳情

**GET** `/api/crm/blog/{id}`

- **描述**: 取得單一部落格文章的詳細資訊
- **參數**:
  - `id` (path, required): 文章 ID
- **回應**: `BlogPostDTO`

### 3. 依別名取得部落格文章

**GET** `/api/crm/blog/slug/{slug}`

- **描述**: 根據文章別名查詢
- **參數**:
  - `slug` (path, required): 文章別名
- **回應**: `BlogPostDTO`

### 4. 創建部落格文章

**POST** `/api/crm/blog`

- **描述**: 創建新的部落格文章
- **請求體**: `BlogPostDTO`
  ```json
  {
    "title": "2024 春季新品發表",
    "slug": "2024-spring-collection",
    "content": "<html>...</html>",
    "status": "DRAFT",
    "authorId": 1,
    "tags": "春季,新品,促銷"
  }
  ```
- **回應**: `BlogPostDTO`

### 5. 更新部落格文章

**PUT** `/api/crm/blog/{id}`

- **描述**: 更新部落格文章資訊
- **參數**:
  - `id` (path, required): 文章 ID
- **請求體**: `BlogPostDTO`
- **回應**: `BlogPostDTO`

### 6. 刪除部落格文章

**DELETE** `/api/crm/blog/{id}`

- **描述**: 刪除部落格文章
- **參數**:
  - `id` (path, required): 文章 ID
- **回應**: `ApiResponseVoid`

### 7. 依狀態查詢部落格文章

**GET** `/api/crm/blog/status/{status}`

- **描述**: 根據文章狀態查詢
- **參數**:
  - `status` (path, required): 文章狀態 (DRAFT, PUBLISHED, ARCHIVED, SCHEDULED)
  - `page`, `size`: 分頁參數
- **回應**: `PageBlogPostDTO`

### 8. 依標籤查詢部落格文章

**GET** `/api/crm/blog/tag/{tag}`

- **描述**: 根據標籤查詢文章
- **參數**:
  - `tag` (path, required): 標籤
  - `page`, `size`: 分頁參數
- **回應**: `PageBlogPostDTO`

### 9. 依作者查詢部落格文章

**GET** `/api/crm/blog/author/{authorId}`

- **描述**: 查詢指定作者的所有文章
- **參數**:
  - `authorId` (path, required): 作者 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageBlogPostDTO`

### 10. 發布部落格文章

**POST** `/api/crm/blog/{id}/publish`

- **描述**: 發布部落格文章
- **參數**:
  - `id` (path, required): 文章 ID
- **回應**: `BlogPostDTO`

### 11. 排程發布部落格文章

**POST** `/api/crm/blog/{id}/schedule`

- **描述**: 排程部落格文章發布時間
- **參數**:
  - `id` (path, required): 文章 ID
  - `scheduledAt` (query, required): 排程時間
- **回應**: `BlogPostDTO`

### 12. 封存部落格文章

**POST** `/api/crm/blog/{id}/archive`

- **描述**: 封存部落格文章
- **參數**:
  - `id` (path, required): 文章 ID
- **回應**: `BlogPostDTO`

---

## 自訂頁面管理 API

### 1. 分頁查詢自訂頁面

**GET** `/api/crm/custom-pages`

- **描述**: 分頁查詢所有自訂頁面
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageCustomPageDTO`

### 2. 取得所有自訂頁面

**GET** `/api/crm/custom-pages/all`

- **描述**: 取得所有自訂頁面（不分頁）
- **回應**: `List<CustomPageDTO>`

### 3. 取得已啟用的自訂頁面

**GET** `/api/crm/custom-pages/enabled`

- **描述**: 取得所有已啟用的自訂頁面
- **回應**: `List<CustomPageDTO>`

### 4. 取得自訂頁面詳情

**GET** `/api/crm/custom-pages/{id}`

- **描述**: 取得單一自訂頁面的詳細資訊
- **參數**:
  - `id` (path, required): 頁面 ID
- **回應**: `CustomPageDTO`

### 5. 依別名取得自訂頁面

**GET** `/api/crm/custom-pages/slug/{slug}`

- **描述**: 根據頁面別名查詢
- **參數**:
  - `slug` (path, required): 頁面別名
- **回應**: `CustomPageDTO`

### 6. 創建自訂頁面

**POST** `/api/crm/custom-pages`

- **描述**: 創建新的自訂頁面
- **請求體**: `CustomPageDTO`
  ```json
  {
    "title": "關於我們",
    "slug": "about-us",
    "content": "<html>...</html>",
    "enabled": true
  }
  ```
- **回應**: `CustomPageDTO`

### 7. 更新自訂頁面

**PUT** `/api/crm/custom-pages/{id}`

- **描述**: 更新自訂頁面資訊
- **參數**:
  - `id` (path, required): 頁面 ID
- **請求體**: `CustomPageDTO`
- **回應**: `CustomPageDTO`

### 8. 刪除自訂頁面

**DELETE** `/api/crm/custom-pages/{id}`

- **描述**: 刪除自訂頁面
- **參數**:
  - `id` (path, required): 頁面 ID
- **回應**: `ApiResponseVoid`

### 9. 切換自訂頁面啟用狀態

**PUT** `/api/crm/custom-pages/{id}/toggle-enabled`

- **描述**: 切換自訂頁面的啟用狀態
- **參數**:
  - `id` (path, required): 頁面 ID
- **回應**: `CustomPageDTO`

---

## 首頁區塊 API

### 1. 取得所有區塊（後台）

**GET** `/api/homepage-blocks`

- **描述**: 取得所有首頁區塊（包含未啟用）
- **回應**: `List<HomepageBlockDTO>`

### 2. 取得啟用的區塊（前台）

**GET** `/api/homepage-blocks/enabled`

- **描述**: 取得所有已啟用的首頁區塊
- **回應**: `List<HomepageBlockDTO>`

### 3. 取得單一區塊

**GET** `/api/homepage-blocks/{id}`

- **描述**: 取得單一首頁區塊的詳細資訊
- **參數**:
  - `id` (path, required): 區塊 ID
- **回應**: `HomepageBlockDTO`

### 4. 新增區塊

**POST** `/api/homepage-blocks`

- **描述**: 創建新的首頁區塊
- **請求體**: `HomepageBlockDTO`
  ```json
  {
    "blockType": "BANNER",
    "title": "熱門商品",
    "sortOrder": 1,
    "enabled": true,
    "content": "{\"images\": [\"img1.jpg\", \"img2.jpg\"]}"
  }
  ```
- **回應**: `HomepageBlockDTO`

### 5. 更新區塊

**PUT** `/api/homepage-blocks/{id}`

- **描述**: 更新首頁區塊資訊
- **參數**:
  - `id` (path, required): 區塊 ID
- **請求體**: `HomepageBlockDTO`
- **回應**: `HomepageBlockDTO`

### 6. 刪除區塊

**DELETE** `/api/homepage-blocks/{id}`

- **描述**: 刪除首頁區塊
- **參數**:
  - `id` (path, required): 區塊 ID
- **回應**: `ApiResponseVoid`

---

## 彈跳廣告 API

### 1. 取得所有廣告（後台）

**GET** `/api/popup-ads`

- **描述**: 取得所有彈跳廣告（包含未啟用）
- **回應**: `List<PopupAdDTO>`

### 2. 取得有效廣告（前台）

**GET** `/api/popup-ads/active`

- **描述**: 取得當前有效的彈跳廣告（根據時間區間篩選）
- **回應**: `List<PopupAdDTO>`

### 3. 取得單一廣告

**GET** `/api/popup-ads/{id}`

- **描述**: 取得單一彈跳廣告的詳細資訊
- **參數**:
  - `id` (path, required): 廣告 ID
- **回應**: `PopupAdDTO`

### 4. 新增廣告

**POST** `/api/popup-ads`

- **描述**: 創建新的彈跳廣告
- **請求體**: `PopupAdDTO`
  ```json
  {
    "title": "新年特賣",
    "imageUrl": "/images/popup.jpg",
    "linkUrl": "/promotion/newyear",
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-01-31T23:59:59",
    "enabled": true,
    "displayFrequency": "ONCE_PER_DAY"
  }
  ```
- **回應**: `PopupAdDTO`

### 5. 更新廣告

**PUT** `/api/popup-ads/{id}`

- **描述**: 更新彈跳廣告資訊
- **參數**:
  - `id` (path, required): 廣告 ID
- **請求體**: `PopupAdDTO`
- **回應**: `PopupAdDTO`

### 6. 刪除廣告

**DELETE** `/api/popup-ads/{id}`

- **描述**: 刪除彈跳廣告
- **參數**:
  - `id` (path, required): 廣告 ID
- **回應**: `ApiResponseVoid`

---

## 會員積點 API

### 1. 取得會員積點餘額

**GET** `/api/crm/points/balance/{memberId}`

- **描述**: 查詢會員當前積點
- **參數**:
  - `memberId` (path, required): 會員 ID
- **回應**: `Integer` (積點餘額)

### 2. 取得會員積點紀錄

**GET** `/api/crm/points/member/{memberId}`

- **描述**: 查詢會員的積點變動紀錄
- **參數**:
  - `memberId` (path, required): 會員 ID
  - `page`, `size`: 分頁參數
- **回應**: `PagePointRecordDTO`

### 3. 增加積點

**POST** `/api/crm/points/add`

- **描述**: 為會員增加積點（如購物回饋）
- **參數**:
  - `memberId` (query, required): 會員 ID
  - `points` (query, required): 積點數量
  - `pointType` (query, required): EARN, PURCHASE, REWARD
  - `reason` (query, required): 原因描述
  - `orderId` (query, optional): 關聯訂單 ID

### 4. 扣除積點

**POST** `/api/crm/points/deduct`

- **描述**: 扣除會員積點（如積點兌換）
- **參數**:
  - `memberId` (query, required): 會員 ID
  - `points` (query, required): 積點數量
  - `pointType` (query, required): REDEEM
  - `reason` (query, required): 原因描述

### 5. 取得積點紀錄詳情

**GET** `/api/crm/points/{id}`

- **描述**: 取得單一積點紀錄的詳細資訊
- **參數**:
  - `id` (path, required): 積點紀錄 ID
- **回應**: `PointRecordDTO`

### 6. 批次發放積點

**POST** `/api/crm/points/batch-grant`

- **描述**: 批次為多個會員發放積點
- **請求體**: `PointBatchDTO`
  ```json
  {
    "memberIds": [1, 2, 3],
    "points": 100,
    "reason": "週年慶活動贈送"
  }
  ```
- **回應**: `List<PointRecordDTO>`

---

## 獎勵制度管理 API

### 1. 分頁查詢獎勵設定

**GET** `/api/crm/rewards`

- **描述**: 分頁查詢所有獎勵設定
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageRewardConfigDTO`

### 2. 取得已啟用的獎勵設定

**GET** `/api/crm/rewards/enabled`

- **描述**: 取得所有已啟用的獎勵設定
- **回應**: `List<RewardConfigDTO>`

### 3. 取得獎勵設定詳情

**GET** `/api/crm/rewards/{id}`

- **描述**: 取得單一獎勵設定的詳細資訊
- **參數**:
  - `id` (path, required): 獎勵設定 ID
- **回應**: `RewardConfigDTO`

### 4. 創建獎勵設定

**POST** `/api/crm/rewards`

- **描述**: 創建新的獎勵設定
- **請求體**: `RewardConfigDTO`
  ```json
  {
    "rewardType": "WELCOME",
    "name": "新會員歡迎禮",
    "rewardPoints": 100,
    "enabled": true
  }
  ```
- **回應**: `RewardConfigDTO`

### 5. 更新獎勵設定

**PUT** `/api/crm/rewards/{id}`

- **描述**: 更新獎勵設定資訊
- **參數**:
  - `id` (path, required): 獎勵設定 ID
- **請求體**: `RewardConfigDTO`
- **回應**: `RewardConfigDTO`

### 6. 刪除獎勵設定

**DELETE** `/api/crm/rewards/{id}`

- **描述**: 刪除獎勵設定
- **參數**:
  - `id` (path, required): 獎勵設定 ID
- **回應**: `ApiResponseVoid`

### 7. 領取入會禮

**POST** `/api/crm/rewards/claim/welcome/{memberId}`

- **描述**: 會員領取入會禮
- **參數**:
  - `memberId` (path, required): 會員 ID
- **回應**: `ApiResponseVoid`

### 8. 領取生日禮

**POST** `/api/crm/rewards/claim/birthday/{memberId}`

- **描述**: 會員領取生日禮
- **參數**:
  - `memberId` (path, required): 會員 ID
- **回應**: `ApiResponseVoid`

### 9. 取得會員的獎勵領取紀錄

**GET** `/api/crm/rewards/claims/member/{memberId}`

- **描述**: 取得會員的獎勵領取紀錄
- **參數**:
  - `memberId` (path, required): 會員 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageRewardClaim`

---

## 庫存管理 API

### 1. 更新庫存

**PUT** `/api/inventory/update`

- **描述**: 更新商品庫存數量
- **參數**:
  - `productId` (query, required): 商品 ID
  - `specificationId` (query, optional): 規格 ID
  - `warehouseId` (query, required): 倉庫 ID
  - `quantity` (query, required): 數量
- **回應**: `ApiResponseVoid`

### 2. 取得未解決的警示

**GET** `/api/inventory/alerts`

- **描述**: 取得所有未解決的庫存警示
- **回應**: `List<InventoryAlert>`

### 3. 取得指定商品的未解決警示

**GET** `/api/inventory/alerts/product/{productId}`

- **描述**: 取得指定商品的所有未解決庫存警示
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `List<InventoryAlert>`

### 4. 解決庫存警示

**PUT** `/api/inventory/alerts/{alertId}/resolve`

- **描述**: 標記庫存警示為已解決
- **參數**:
  - `alertId` (path, required): 警示 ID
- **回應**: `ApiResponseVoid`

### 5. 檢查庫存並創建警示

**POST** `/api/inventory/check-alerts`

- **描述**: 檢查所有商品庫存並創建警示
- **回應**: `ApiResponseVoid`

### 6. 訂閱貨到通知

**POST** `/api/inventory/notifications/subscribe`

- **描述**: 訂閱商品貨到通知
- **參數**:
  - `productId` (query, required): 商品 ID
  - `specificationId` (query, optional): 規格 ID
  - `email` (query, required): Email
  - `phone` (query, optional): 電話
- **回應**: `ApiResponseVoid`

### 7. 處理貨到通知

**POST** `/api/inventory/notifications/process/{productId}`

- **描述**: 處理指定商品的貨到通知
- **參數**:
  - `productId` (path, required): 商品 ID
- **回應**: `ApiResponseVoid`

---

## 倉庫管理 API

### 1. 取得所有倉庫

**GET** `/api/warehouses`

- **描述**: 取得所有倉庫列表
- **回應**: `List<WarehouseDTO>`

### 2. 取得已啟用的倉庫

**GET** `/api/warehouses/enabled`

- **描述**: 取得所有已啟用的倉庫
- **回應**: `List<WarehouseDTO>`

### 3. 取得預設倉庫

**GET** `/api/warehouses/default`

- **描述**: 取得預設倉庫
- **回應**: `WarehouseDTO`

### 4. 取得倉庫詳情

**GET** `/api/warehouses/{id}`

- **描述**: 取得單一倉庫的詳細資訊
- **參數**:
  - `id` (path, required): 倉庫 ID
- **回應**: `WarehouseDTO`

### 5. 創建倉庫

**POST** `/api/warehouses`

- **描述**: 創建新的倉庫
- **請求體**: `WarehouseDTO`
  ```json
  {
    "name": "台北倉庫",
    "code": "WH001",
    "address": "台北市信義區...",
    "contactPerson": "張三",
    "contactPhone": "02-1234-5678",
    "enabled": true
  }
  ```
- **回應**: `WarehouseDTO`

### 6. 更新倉庫

**PUT** `/api/warehouses/{id}`

- **描述**: 更新倉庫資訊
- **參數**:
  - `id` (path, required): 倉庫 ID
- **請求體**: `WarehouseDTO`
- **回應**: `WarehouseDTO`

### 7. 刪除倉庫

**DELETE** `/api/warehouses/{id}`

- **描述**: 刪除倉庫
- **參數**:
  - `id` (path, required): 倉庫 ID
- **回應**: `ApiResponseVoid`

---

## 帳號管理 API

### 1. 取得所有使用者

**GET** `/api/users`

- **描述**: 取得系統中所有使用者列表（僅限管理員）
- **需要**: Bearer Token
- **回應**: `List<UserDTO>`

### 2. 取得使用者詳情

**GET** `/api/users/{id}`

- **描述**: 根據 ID 取得特定使用者的詳細資訊（僅限管理員）
- **需要**: Bearer Token
- **參數**:
  - `id` (path, required): 使用者 ID
- **回應**: `UserDTO`

### 3. 新增使用者

**POST** `/api/users`

- **描述**: 建立新的使用者帳號（僅限管理員）
- **需要**: Bearer Token
- **請求體**: `UserDTO`
  ```json
  {
    "username": "user123",
    "email": "user@example.com",
    "password": "password123",
    "role": "STAFF",
    "enabled": true
  }
  ```
- **回應**: `UserDTO`

### 4. 更新使用者

**PUT** `/api/users/{id}`

- **描述**: 更新現有使用者的資訊（僅限管理員）
- **需要**: Bearer Token
- **參數**:
  - `id` (path, required): 使用者 ID
- **請求體**: `UserDTO`
- **回應**: `UserDTO`

### 5. 刪除使用者

**DELETE** `/api/users/{id}`

- **描述**: 刪除指定的使用者帳號（僅限管理員）
- **需要**: Bearer Token
- **參數**:
  - `id` (path, required): 使用者 ID
- **回應**: `ApiResponseVoid`

### 6. 啟用/停用使用者

**PATCH** `/api/users/{id}/status`

- **描述**: 切換使用者帳號的啟用狀態（僅限管理員）
- **需要**: Bearer Token
- **參數**:
  - `id` (path, required): 使用者 ID
  - `enabled` (query, required): true/false
- **回應**: `UserDTO`

---

## 員工管理 API

### 1. 取得所有員工

**GET** `/api/staff`

- **描述**: 取得所有員工列表
- **回應**: `List<StaffDTO>`

### 2. 取得單一員工

**GET** `/api/staff/{id}`

- **描述**: 取得單一員工的詳細資訊
- **參數**:
  - `id` (path, required): 員工 ID
- **回應**: `StaffDTO`

### 3. 新增員工

**POST** `/api/staff`

- **描述**: 創建新員工（帳號上限 50 組）
- **請求體**: `StaffDTO`
  ```json
  {
    "account": "staff001",
    "password": "password123",
    "name": "王小明",
    "phone": "0912-345-678",
    "email": "staff@example.com",
    "role": "STAFF",
    "enabled": true
  }
  ```
- **回應**: `StaffDTO`

### 4. 更新員工

**PUT** `/api/staff/{id}`

- **描述**: 更新員工資訊
- **參數**:
  - `id` (path, required): 員工 ID
- **請求體**: `StaffDTO`
- **回應**: `StaffDTO`

### 5. 刪除員工

**DELETE** `/api/staff/{id}`

- **描述**: 刪除員工
- **參數**:
  - `id` (path, required): 員工 ID
- **回應**: `ApiResponseVoid`

### 6. 切換員工啟用狀態

**PATCH** `/api/staff/{id}/toggle-enabled`

- **描述**: 切換員工帳號的啟用狀態
- **參數**:
  - `id` (path, required): 員工 ID
- **回應**: `StaffDTO`

---

## 出勤打卡 API

### 1. 上班打卡

**POST** `/api/attendance/clock-in/{staffId}`

- **描述**: 員工上班打卡
- **參數**:
  - `staffId` (path, required): 員工 ID
- **回應**: `AttendanceRecordDTO`

### 2. 下班打卡

**POST** `/api/attendance/clock-out/{staffId}`

- **描述**: 員工下班打卡
- **參數**:
  - `staffId` (path, required): 員工 ID
- **回應**: `AttendanceRecordDTO`

### 3. 查詢員工出勤紀錄

**GET** `/api/attendance/staff/{staffId}`

- **描述**: 查詢員工指定時間範圍的出勤紀錄
- **參數**:
  - `staffId` (path, required): 員工 ID
  - `startDate` (query, required): 開始日期 (yyyy-MM-dd)
  - `endDate` (query, required): 結束日期 (yyyy-MM-dd)
- **回應**: `List<AttendanceRecordDTO>`

### 4. 查詢某日全部出勤

**GET** `/api/attendance/date/{date}`

- **描述**: 查詢指定日期的所有員工出勤紀錄
- **參數**:
  - `date` (path, required): 日期 (yyyy-MM-dd)
- **回應**: `List<AttendanceRecordDTO>`

### 5. 修改出勤紀錄

**PUT** `/api/attendance/{recordId}`

- **描述**: 主管用：補登或修正打卡時間
- **參數**:
  - `recordId` (path, required): 紀錄 ID
- **請求體**: `AttendanceRecordDTO`
- **回應**: `AttendanceRecordDTO`

---

## 相冊管理 API

### 1. 分頁查詢相冊

**GET** `/api/albums`

- **描述**: 分頁查詢所有相冊
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageAlbumDTO`

### 2. 取得相冊詳情

**GET** `/api/albums/{id}`

- **描述**: 取得單一相冊的詳細資訊
- **參數**:
  - `id` (path, required): 相冊 ID
- **回應**: `AlbumDTO`

### 3. 創建相冊

**POST** `/api/albums`

- **描述**: 創建新的相冊
- **請求體**: `AlbumDTO`
  ```json
  {
    "name": "2024 春季商品",
    "description": "春季新品照片集"
  }
  ```
- **回應**: `AlbumDTO`

### 4. 更新相冊

**PUT** `/api/albums/{id}`

- **描述**: 更新相冊資訊
- **參數**:
  - `id` (path, required): 相冊 ID
- **請求體**: `AlbumDTO`
- **回應**: `AlbumDTO`

### 5. 刪除相冊

**DELETE** `/api/albums/{id}`

- **描述**: 刪除相冊
- **參數**:
  - `id` (path, required): 相冊 ID
- **回應**: `ApiResponseVoid`

### 6. 取得相冊中的所有圖片

**GET** `/api/albums/{albumId}/images`

- **描述**: 取得相冊內的所有圖片
- **參數**:
  - `albumId` (path, required): 相冊 ID
- **回應**: `List<AlbumImageDTO>`

### 7. 上傳圖片到相冊

**POST** `/api/albums/{albumId}/images`

- **描述**: 上傳圖片到指定相冊
- **參數**:
  - `albumId` (path, required): 相冊 ID
  - `title` (query, optional): 圖片標題
  - `description` (query, optional): 圖片描述
- **請求體**: 圖片檔案 (multipart/form-data)
- **回應**: `AlbumImageDTO`

### 8. 刪除相冊中的圖片

**DELETE** `/api/albums/{albumId}/images/{imageId}`

- **描述**: 刪除相冊中的指定圖片
- **參數**:
  - `albumId` (path, required): 相冊 ID
  - `imageId` (path, required): 圖片 ID
- **回應**: `ApiResponseVoid`

### 9. 下載或查看圖片

**GET** `/api/albums/images/{filename}`

- **描述**: 下載或查看圖片檔案
- **參數**:
  - `filename` (path, required): 檔案名稱
- **回應**: 圖片檔案 (binary)

---

## 系統設定 API

### 1. 取得系統設定

**GET** `/api/system/config`

- **描述**: 取得系統基本設定
- **回應**: `SystemConfigDTO`

### 2. 更新系統設定

**PUT** `/api/system/config`

- **描述**: 更新系統基本設定
- **請求體**: `SystemConfigDTO`
  ```json
  {
    "siteName": "Shopro 電商平台",
    "contactEmail": "contact@shopro.com",
    "contactPhone": "02-1234-5678",
    "defaultCurrency": "TWD",
    "taxRate": 5.0,
    "autoOrderCancelMinutes": 30,
    "stockDeductionTiming": "PAYMENT_COMPLETED"
  }
  ```
- **回應**: `SystemConfigDTO`

---

## 商店設定 API

### 1. 取得商店設定

**GET** `/api/store`

- **描述**: 取得商店設定
- **回應**: `StoreDTO`

### 2. 初始化商店設定

**POST** `/api/store`

- **描述**: 初始化商店設定
- **請求體**: `StoreDTO`
- **回應**: `StoreDTO`

### 3. 更新商店設定

**PUT** `/api/store`

- **描述**: 更新商店設定
- **請求體**: `StoreDTO`
  ```json
  {
    "name": "我的商店",
    "logo": "/images/logo.png",
    "themeLevel": "GOLD",
    "defaultCurrency": "TWD",
    "address": "台北市信義區...",
    "phone": "02-1234-5678",
    "email": "store@example.com"
  }
  ```
- **回應**: `StoreDTO`

---

## 物流設定 API

### 1. 分頁查詢物流設定

**GET** `/api/system/shipping-config`

- **描述**: 分頁查詢所有物流設定
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageShippingConfigDTO`

### 2. 取得已啟用的物流設定

**GET** `/api/system/shipping-config/enabled`

- **描述**: 取得所有已啟用的物流設定
- **回應**: `List<ShippingConfigDTO>`

### 3. 依配送方式查詢

**GET** `/api/system/shipping-config/method/{shippingMethod}`

- **描述**: 根據配送方式查詢物流設定
- **參數**:
  - `shippingMethod` (path, required): 配送方式
- **回應**: `List<ShippingConfigDTO>`

### 4. 取得物流設定詳情

**GET** `/api/system/shipping-config/{id}`

- **描述**: 取得單一物流設定的詳細資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `ShippingConfigDTO`

### 5. 創建物流設定

**POST** `/api/system/shipping-config`

- **描述**: 創建新的物流設定
- **請求體**: `ShippingConfigDTO`
  ```json
  {
    "providerName": "BLACK_CAT",
    "enabled": true,
    "shippingMethod": "HOME_DELIVERY",
    "baseShippingFee": 80.0,
    "freeShippingThreshold": 1000.0
  }
  ```
- **回應**: `ShippingConfigDTO`

### 6. 更新物流設定

**PUT** `/api/system/shipping-config/{id}`

- **描述**: 更新物流設定資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **請求體**: `ShippingConfigDTO`
- **回應**: `ShippingConfigDTO`

### 7. 刪除物流設定

**DELETE** `/api/system/shipping-config/{id}`

- **描述**: 刪除物流設定
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `ApiResponseVoid`

---

## 金流設定 API

### 1. 分頁查詢金流設定

**GET** `/api/system/payment-config`

- **描述**: 分頁查詢所有金流設定
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PagePaymentConfigDTO`

### 2. 取得已啟用的金流設定

**GET** `/api/system/payment-config/enabled`

- **描述**: 取得所有已啟用的金流設定
- **回應**: `List<PaymentConfigDTO>`

### 3. 依付款方式查詢

**GET** `/api/system/payment-config/method/{paymentMethod}`

- **描述**: 根據付款方式查詢金流設定
- **參數**:
  - `paymentMethod` (path, required): 付款方式
- **回應**: `List<PaymentConfigDTO>`

### 4. 取得金流設定詳情

**GET** `/api/system/payment-config/{id}`

- **描述**: 取得單一金流設定的詳細資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `PaymentConfigDTO`

### 5. 創建金流設定

**POST** `/api/system/payment-config`

- **描述**: 創建新的金流設定
- **請求體**: `PaymentConfigDTO`
  ```json
  {
    "providerName": "ECPAY",
    "enabled": true,
    "paymentMethod": "CREDIT_CARD",
    "chargeTransactionFee": true,
    "transactionFeeRate": 2.5
  }
  ```
- **回應**: `PaymentConfigDTO`

### 6. 更新金流設定

**PUT** `/api/system/payment-config/{id}`

- **描述**: 更新金流設定資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **請求體**: `PaymentConfigDTO`
- **回應**: `PaymentConfigDTO`

### 7. 刪除金流設定

**DELETE** `/api/system/payment-config/{id}`

- **描述**: 刪除金流設定
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `ApiResponseVoid`

---

## 通知設定 API

### 1. 分頁查詢通知設定

**GET** `/api/system/notification-config`

- **描述**: 分頁查詢所有通知設定
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageNotificationConfigDTO`

### 2. 取得已啟用的通知設定

**GET** `/api/system/notification-config/enabled`

- **描述**: 取得所有已啟用的通知設定
- **回應**: `List<NotificationConfigDTO>`

### 3. 依通知類型查詢

**GET** `/api/system/notification-config/type/{notificationType}`

- **描述**: 根據通知類型查詢
- **參數**:
  - `notificationType` (path, required): 通知類型
  - `page`, `size`: 分頁參數
- **回應**: `PageNotificationConfigDTO`

### 4. 依事件類型查詢

**GET** `/api/system/notification-config/event/{eventType}`

- **描述**: 根據事件類型查詢
- **參數**:
  - `eventType` (path, required): 事件類型
- **回應**: `List<NotificationConfigDTO>`

### 5. 取得通知設定詳情

**GET** `/api/system/notification-config/{id}`

- **描述**: 取得單一通知設定的詳細資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `NotificationConfigDTO`

### 6. 創建通知設定

**POST** `/api/system/notification-config`

- **描述**: 創建新的通知設定
- **請求體**: `NotificationConfigDTO`
  ```json
  {
    "notificationType": "EMAIL",
    "enabled": true,
    "eventType": "ORDER_CREATED",
    "templateSubject": "訂單確認通知",
    "templateBody": "您的訂單已成功建立..."
  }
  ```
- **回應**: `NotificationConfigDTO`

### 7. 更新通知設定

**PUT** `/api/system/notification-config/{id}`

- **描述**: 更新通知設定資訊
- **參數**:
  - `id` (path, required): 設定 ID
- **請求體**: `NotificationConfigDTO`
- **回應**: `NotificationConfigDTO`

### 8. 刪除通知設定

**DELETE** `/api/system/notification-config/{id}`

- **描述**: 刪除通知設定
- **參數**:
  - `id` (path, required): 設定 ID
- **回應**: `ApiResponseVoid`

---

## 操作日誌 API

### 1. 分頁查詢所有日誌

**GET** `/api/system/operation-logs`

- **描述**: 分頁查詢所有操作日誌
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 2. 取得日誌詳情

**GET** `/api/system/operation-logs/{id}`

- **描述**: 取得單一操作日誌的詳細資訊
- **參數**:
  - `id` (path, required): 日誌 ID
- **回應**: `OperationLogDTO`

### 3. 依用戶查詢日誌

**GET** `/api/system/operation-logs/user/{userId}`

- **描述**: 查詢指定用戶的操作日誌
- **參數**:
  - `userId` (path, required): 用戶 ID
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 4. 依模組查詢

**GET** `/api/system/operation-logs/module/{module}`

- **描述**: 根據模組查詢操作日誌
- **參數**:
  - `module` (path, required): 模組名稱
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 5. 依操作類型查詢

**GET** `/api/system/operation-logs/operation-type/{operationType}`

- **描述**: 根據操作類型查詢
- **參數**:
  - `operationType` (path, required): 操作類型
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 6. 依時間範圍查詢

**GET** `/api/system/operation-logs/date-range`

- **描述**: 根據時間範圍查詢操作日誌
- **參數**:
  - `start` (query, required): 開始時間
  - `end` (query, required): 結束時間
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 7. 查詢敏感操作

**GET** `/api/system/operation-logs/sensitive`

- **描述**: 查詢所有敏感操作記錄
- **參數**:
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

### 8. 依用戶和模組查詢

**GET** `/api/system/operation-logs/user/{userId}/module/{module}`

- **描述**: 查詢指定用戶在特定模組的操作記錄
- **參數**:
  - `userId` (path, required): 用戶 ID
  - `module` (path, required): 模組名稱
  - `page`, `size`: 分頁參數
- **回應**: `PageOperationLogDTO`

---

## 儀表板 API

### 1. 取得儀表板統計摘要

**GET** `/api/dashboard/stats`

- **描述**: 取得儀表板統計摘要（包含商品、訂單、客戶和銷售額統計）
- **回應**: `DashboardStatsDTO`
  ```typescript
  {
    totalProducts: 1000,
    totalProductsChange: 5.2,
    pendingOrders: 50,
    pendingOrdersChange: -3.1,
    totalCustomers: 5000,
    totalCustomersChange: 10.5,
    monthlySales: 500000.0,
    monthlySalesChange: 15.8
  }
  ```

### 2. 取得最近訂單

**GET** `/api/dashboard/recent-orders`

- **描述**: 取得最近的訂單列表
- **參數**:
  - `limit` (query, optional): 限制數量，預設 10
- **回應**: `List<RecentOrderDTO>`

### 3. 取得熱銷商品

**GET** `/api/dashboard/top-products`

- **描述**: 取得銷量最高的商品列表
- **參數**:
  - `limit` (query, optional): 限制數量，預設 5
- **回應**: `List<TopProductDTO>`

---

## 支付管理 API

### 1. 取得支付統計資料

**GET** `/api/payment-management/statistics`

- **描述**: 取得今日、本月的交易統計及各閘道佔比
- **回應**: `PaymentStatisticsDTO`

### 2. 查詢交易記錄

**GET** `/api/payment-management/transactions`

- **描述**: 支援多條件查詢交易記錄
- **參數**:
  - `keyword` (query, optional): 關鍵字 (訂單編號或交易 ID)
  - `gateway` (query, optional): 支付閘道
  - `status` (query, optional): 交易狀態
  - `startDate` (query, optional): 開始時間
  - `endDate` (query, optional): 結束時間
- **回應**: `List<PaymentTransactionDTO>`

### 3. 取得交易詳情

**GET** `/api/payment-management/transactions/{id}`

- **描述**: 根據 ID 取得單筆交易的詳細資訊
- **參數**:
  - `id` (path, required): 交易 ID
- **回應**: `PaymentTransactionDTO`

### 4. 取得所有支付設定

**GET** `/api/payment-management/settings`

- **描述**: 取得所有支付閘道的設定資訊
- **回應**: `List<PaymentSetting>`

### 5. 取得單一支付設定

**GET** `/api/payment-management/settings/{gateway}`

- **描述**: 根據閘道類型取得設定
- **參數**:
  - `gateway` (path, required): 支付閘道類型 (LINE_PAY, ECPAY, MANUAL)
- **回應**: `PaymentSetting`

### 6. 更新支付設定

**PUT** `/api/payment-management/settings`

- **描述**: 更新支付閘道的設定資訊
- **請求體**: `PaymentSetting`
- **回應**: `PaymentSetting`

### 7. 初始化支付設定

**POST** `/api/payment-management/settings/initialize`

- **描述**: 初始化所有支付閘道的預設設定（僅在設定不存在時）
- **回應**: `String`

### 8. 檢查閘道可用性

**GET** `/api/payment-management/settings/{gateway}/availability`

- **描述**: 檢查支付閘道是否可用
- **參數**:
  - `gateway` (path, required): 支付閘道類型
- **回應**: `Boolean`

---

## 資料結構說明

### ProductDTO

```typescript
interface ProductDTO {
  id: number;
  name: string;
  sku: string;
  description: string;
  categoryId: number;
  status: 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK';
  salesMode: 'NORMAL' | 'PRE_ORDER' | 'VOUCHER' | 'SUBSCRIPTION' | 'STORE_ONLY';
  basePrice: number;
  salePrice: number;
  weight: number;
  minPurchaseQuantity: number;
  maxPurchaseQuantity: number;
  images: ProductImageDTO[];
  specifications: ProductSpecificationDTO[];
}
```

### OrderDTO

```typescript
interface OrderDTO {
  id?: number;
  orderNumber?: string;
  customerId: number;
  customerName?: string;
  customerPhone?: string;
  customerEmail?: string;
  status:
    | 'PENDING_PAYMENT'
    | 'PROCESSING'
    | 'COMPLETED'
    | 'CANCELLED'
    | 'REFUNDED';
  pickupType: 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP';
  storeId?: number;
  subtotalAmount: number;
  discountAmount?: number;
  shippingFee?: number;
  totalAmount: number;
  shippingAddress?: string;
  isDraft?: boolean;
  items: OrderItemDTO[];
}
```

### OrderItemDTO

```typescript
interface OrderItemDTO {
  productId: number;
  productName?: string;
  productSku?: string;
  productSpec?: string;
  unitPrice: number;
  quantity: number;
  subtotalAmount?: number;
  discountAmount?: number;
  actualAmount?: number;
}
```

### PaymentRequestDTO

```typescript
interface PaymentRequestDTO {
  orderId: number;
  orderNumber: string;
  amount: number;
  currency: string;
  productName: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
}
```

---

## 使用流程範例

### 完整購物流程

```
1. 瀏覽商品
   GET /api/products?page=0&size=20

2. 查看商品詳情
   GET /api/products/{id}
   GET /api/product-specifications/product/{id}
   GET /api/product-images/product/{id}

3. 加入購物車 (使用訂單草稿)
   POST /api/orders (isDraft: true)

4. 查看購物車
   GET /api/orders/draft

5. 更新購物車
   PUT /api/orders/{id}

6. 結帳 - 更新為正式訂單
   PUT /api/orders/{id} (isDraft: false, status: PENDING_PAYMENT)

7. 選擇物流方式
   GET /api/system/shipping-config/enabled

8. 套用優惠碼
   POST /api/orders/discounts

9. 創建支付
   POST /api/payment-gateway/create?gateway=ECPAY

10. 確認支付
    POST /api/payment-gateway/confirm

11. 查詢訂單
    GET /api/orders/{id}
```

---

## 注意事項

1. **購物車實作方式**:

   - 目前 API 未提供專門的購物車接口
   - 建議使用 `isDraft: true` 的訂單作為購物車
   - 或在前端使用 LocalStorage/Vuex 管理購物車

2. **認證方式**:

   - 大部分 API 需要 Bearer Token 認證
   - 在請求 Header 加入: `Authorization: Bearer {token}`

3. **錯誤處理**:

   - 所有 API 回應格式統一為 `ApiResponse<T>`
   - 檢查 `success` 欄位判斷請求是否成功
   - 錯誤訊息在 `message` 欄位

4. **分頁參數**:

   - 預設 `page=0` (第一頁)
   - 預設 `size=20` (每頁 20 筆)

5. **金額單位**:
   - 所有金額欄位使用 `number` 類型
   - 幣別由系統設定決定（預設 TWD）

---

## API Base URL

開發環境: `http://localhost:8080`

## 更新日誌

- 2024-12-28: 初始版本建立
