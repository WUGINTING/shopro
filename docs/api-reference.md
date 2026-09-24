# API 參考文檔

## 概述

所有 API 端點都以 `/api` 為前綴，回傳統一的 JSON 格式。

### 統一回應格式

```json
{
  "success": true,
  "message": "Success",
  "data": { ... }
}
```

### 分頁回應格式

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  }
}
```

### 認證

需要認證的 API 必須在 Header 中攜帶 JWT Token：

```
Authorization: Bearer <token>
```

---

## 認證模組

### 註冊

```http
POST /api/auth/register
```

**請求：**
```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
```

**回應：**
```json
{
  "success": true,
  "message": "註冊成功",
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "role": "CUSTOMER"
  }
}
```

### 登入

```http
POST /api/auth/login
```

**請求：**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**回應：**
```json
{
  "success": true,
  "message": "登入成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "role": "CUSTOMER"
    }
  }
}
```

### Google 登入

```http
POST /api/auth/google-login
```

**請求：**
```json
{
  "idToken": "google_id_token_here"
}
```

---

## 商品模組

### 取得商品列表

```http
GET /api/products
```

**查詢參數：**
| 參數 | 類型 | 說明 |
|------|------|------|
| page | number | 頁碼 (從 0 開始) |
| size | number | 每頁筆數 |
| status | string | 狀態篩選 (DRAFT/ACTIVE/INACTIVE) |
| categoryId | number | 分類篩選 |
| keyword | string | 關鍵字搜尋 |

### 取得單一商品

```http
GET /api/products/{id}
```

### 創建商品

```http
POST /api/products
```

**請求：**
```json
{
  "name": "商品名稱",
  "description": "商品描述",
  "basePrice": 100.00,
  "salePrice": 80.00,
  "costPrice": 50.00,
  "stock": 100,
  "status": "DRAFT",
  "salesMode": "NORMAL",
  "categoryId": 1
}
```

### 更新商品

```http
PUT /api/products/{id}
```

### 刪除商品

```http
DELETE /api/products/{id}
```

### 上架商品

```http
PUT /api/products/{id}/activate
```

### 下架商品

```http
PUT /api/products/{id}/deactivate
```

### 添加相冊圖片

```http
POST /api/products/{id}/album-images
```

**請求：**
```json
[1, 2, 3]  // 相冊圖片 ID 陣列
```

---

## 分類模組

### 取得所有分類

```http
GET /api/product-categories
```

### 取得啟用的分類

```http
GET /api/product-categories/enabled
```

### 取得頂層分類

```http
GET /api/product-categories/top
```

### 取得子分類

```http
GET /api/product-categories/{id}/children
```

### 創建分類

```http
POST /api/product-categories
```

**請求：**
```json
{
  "name": "分類名稱",
  "description": "分類描述",
  "parentId": null,
  "icon": "category",
  "sortOrder": 1,
  "enabled": true
}
```

---

## 規格模組

### 取得商品規格

```http
GET /api/product-specifications/product/{productId}
```

### 添加規格

```http
POST /api/product-specifications
```

**請求：**
```json
{
  "productId": 1,
  "specName": "顏色:紅色,尺寸:L",
  "sku": "PROD-001-RED-L",
  "price": 100.00,
  "cost": 50.00,
  "stock": 50,
  "enabled": true
}
```

### 批量添加規格

```http
POST /api/product-specifications/batch
```

**請求：**
```json
[
  { "productId": 1, "specName": "紅色-S", "price": 100 },
  { "productId": 1, "specName": "紅色-M", "price": 100 }
]
```

---

## 訂單模組

### 取得訂單列表

```http
GET /api/orders
```

**查詢參數：**
| 參數 | 類型 | 說明 |
|------|------|------|
| page | number | 頁碼 |
| size | number | 每頁筆數 |
| status | string | 訂單狀態 |
| customerId | number | 客戶 ID |
| startDate | string | 開始日期 |
| endDate | string | 結束日期 |

### 取得訂單詳情

```http
GET /api/orders/{id}
```

### 創建訂單

```http
POST /api/orders
```

**請求：**
```json
{
  "customerId": 1,
  "items": [
    { "productId": 1, "specificationId": 1, "quantity": 2 }
  ],
  "shippingAddress": "台北市...",
  "pickupType": "DELIVERY"
}
```

### 更新訂單狀態

```http
PUT /api/orders/{id}/status
```

**請求：**
```json
{
  "status": "CONFIRMED"
}
```

---

## 前台訂單模組（訪客結帳，不需登入）

前台商城（`frontend-official`）使用。所有金額由後端依商品 / 規格價格與物流設定重新計算，前端不傳價格。
運費取自已啟用的物流設定（`shipping_config.shippingMethod` = `HOME_DELIVERY` / `STORE_PICKUP`）；未設定宅配時預設運費 100、滿 1000 免運，門市自取免運。

### 結帳試算

```http
POST /api/storefront/orders/quote
```

**請求：**
```json
{
  "items": [
    { "productId": 1, "specificationId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ],
  "shippingMethod": "HOME_DELIVERY"
}
```

**回應 `data`：** `lines[]`（含後端單價、小計）、`subtotalAmount`、`shippingFee`、`freeShippingThreshold`、`totalAmount`。
商品未上架、規格不符、未選規格或庫存不足時回傳 400 與可讀訊息（例如「商品「日式茶杯 (藍色)」庫存不足，目前剩餘 3 件」）。

### 訪客結帳

```http
POST /api/storefront/orders/checkout
```

**請求：**
```json
{
  "customerName": "王小明",
  "customerPhone": "0912345678",
  "customerEmail": "buyer@example.com",
  "shippingAddress": "台北市信義區市府路 1 號",
  "notes": "請下午送達",
  "shippingMethod": "HOME_DELIVERY",
  "paymentMethod": "ECPAY",
  "items": [{ "productId": 1, "specificationId": 1, "quantity": 2 }]
}
```

- `shippingMethod`：`HOME_DELIVERY`（需填地址）/ `STORE_PICKUP`
- `paymentMethod`：`ECPAY`（綠界線上付款）/ `COD`（貨到付款 / 取貨時付款）
- 依 Email 找到或自動建立 CRM 會員，訂單狀態為 `PENDING_PAYMENT`

**回應 `data`：** `order`（OrderDTO）、`paymentMethod`、`paymentUrl`（ECPAY 時提供，前端需以 POST 表單導向）、`paymentError`（建立付款失敗時的訊息，訂單仍保留）。

### 查詢訂單

```http
GET /api/storefront/orders/lookup?orderNumber={訂單編號}&email={下單 Email}
```

Email 不分大小寫；訂單編號與 Email 不相符時回傳 400「查無此訂單」。

---

## 訂單折扣模組

### 取得折扣列表

```http
GET /api/order-discounts
```

### 創建折扣

```http
POST /api/order-discounts
```

**請求：**
```json
{
  "name": "滿千折百",
  "type": "AMOUNT_OFF",
  "condition": 1000,
  "discount": 100,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "enabled": true
}
```

---

## 會員模組

### 取得會員列表

```http
GET /api/members
```

### 取得會員詳情

```http
GET /api/members/{id}
```

### 創建會員

```http
POST /api/members
```

---

## 會員等級模組

### 取得等級列表

```http
GET /api/member-levels
```

### 創建等級

```http
POST /api/member-levels
```

**請求：**
```json
{
  "name": "VIP",
  "minPoints": 1000,
  "discountRate": 0.1,
  "pointMultiplier": 2
}
```

---

## 積點模組

### 取得積點記錄

```http
GET /api/points
```

### 增加積點

```http
POST /api/points/add
```

**請求：**
```json
{
  "memberId": 1,
  "points": 100,
  "reason": "消費獎勵"
}
```

### 扣除積點

```http
POST /api/points/deduct
```

---

## 支付模組

### 創建支付

```http
POST /api/payment-gateway/create
```

**請求：**
```json
{
  "orderId": 1,
  "gateway": "ECPAY",
  "amount": 1000
}
```

### 支付回調

```http
POST /api/payment-gateway/callback/{gateway}
```

### 取得交易列表

```http
GET /api/payment/transactions
```

### 退款

```http
POST /api/payment/refund
```

**請求：**
```json
{
  "transactionId": "TXN123456",
  "amount": 500,
  "reason": "商品瑕疵"
}
```

---

## 相冊模組

### 取得相冊列表

```http
GET /api/albums
```

### 創建相冊

```http
POST /api/albums
```

**請求：**
```json
{
  "name": "商品圖片",
  "description": "用於儲存商品圖片"
}
```

### 上傳圖片

```http
POST /api/albums/{id}/images
Content-Type: multipart/form-data
```

**表單欄位：**
- `file`: 圖片檔案
- `title`: 圖片標題 (選填)

---

## 儀表板模組

### 取得統計數據

```http
GET /api/dashboard/stats
```

**回應：**
```json
{
  "success": true,
  "data": {
    "totalOrders": 1234,
    "totalRevenue": 123456.78,
    "totalProducts": 500,
    "totalCustomers": 1000,
    "todayOrders": 50,
    "todayRevenue": 5000
  }
}
```

### 取得最近訂單

```http
GET /api/dashboard/recent-orders
```

### 取得熱銷商品

```http
GET /api/dashboard/top-products
```

---

## 系統設定模組

### 取得系統設定

```http
GET /api/system-config
```

### 更新系統設定

```http
PUT /api/system-config
```

---

## 操作日誌模組

### 取得日誌列表

```http
GET /api/operation-logs
```

**查詢參數：**
| 參數 | 類型 | 說明 |
|------|------|------|
| page | number | 頁碼 |
| size | number | 每頁筆數 |
| userId | number | 使用者 ID |
| operationType | string | 操作類型 |
| startDate | string | 開始日期 |
| endDate | string | 結束日期 |

---

## 錯誤碼

| HTTP 狀態碼 | 說明 |
|-------------|------|
| 200 | 成功 |
| 201 | 創建成功 |
| 400 | 請求參數錯誤 |
| 401 | 未認證 |
| 403 | 權限不足 |
| 404 | 資源不存在 |
| 500 | 伺服器錯誤 |

---

## Swagger 文檔

後端啟動後，可存取完整的 API 文檔：

```
http://localhost:8080/swagger-ui.html
```
