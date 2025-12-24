# 訂單管理模組 API 文檔

## 概述

訂單管理模組提供完整的電商訂單處理功能，包含訂單建立、查詢、更新、刪除、付款管理、物流追蹤、統計分析等核心功能，同時支援 O2O（門市取貨）、暫存訂單、批次操作等進階功能。

## 核心功能

### 1. 基礎功能

#### 1.1 訂單建立
- **端點**: `POST /api/orders`
- **功能**: 手動建立新訂單，包含選擇客戶、商品、金額
- **特色**: 
  - 自動生成唯一訂單編號
  - 支援多個商品項目
  - 自動檢查客戶黑名單
  - 自動計算訂單金額
  - 記錄操作歷史

**請求範例**:
```json
{
  "customerId": 1,
  "customerName": "張三",
  "customerPhone": "0912345678",
  "customerEmail": "test@example.com",
  "status": "PENDING_PAYMENT",
  "pickupType": "DELIVERY",
  "subtotalAmount": 1000.00,
  "discountAmount": 100.00,
  "shippingFee": 60.00,
  "totalAmount": 960.00,
  "shippingAddress": "台北市信義區信義路五段7號",
  "isDraft": false,
  "items": [
    {
      "productId": 1,
      "productName": "商品A",
      "productSku": "SKU001",
      "unitPrice": 500.00,
      "quantity": 2,
      "subtotalAmount": 1000.00,
      "discountAmount": 0.00,
      "actualAmount": 1000.00
    }
  ]
}
```

#### 1.2 訂單查詢
- **多條件查詢**: `POST /api/orders/search`
  - 支援按訂單ID、客戶ID、狀態、日期範圍、金額範圍等條件篩選
  
- **單一訂單查詢**: `GET /api/orders/{id}`
  
- **按客戶查詢**: `GET /api/orders/customer/{customerId}`
  
- **按狀態查詢**: `GET /api/orders/status/{status}`
  
- **按訂單編號查詢**: `GET /api/orders/search/order-number/{orderNumber}`

**多條件查詢範例**:
```json
{
  "customerId": 1,
  "status": "COMPLETED",
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "minAmount": 500.00,
  "maxAmount": 5000.00,
  "page": 0,
  "size": 20
}
```

#### 1.3 訂單更新
- **端點**: `PUT /api/orders/{id}`
- **功能**: 更新訂單資料
- **更新狀態**: `PATCH /api/orders/{id}/status?status={newStatus}`

#### 1.4 訂單刪除
- **單一刪除**: `DELETE /api/orders/{id}`
- **批量刪除**: `DELETE /api/orders/batch` (請求體傳送 ID 列表)

#### 1.5 通知功能
訂單狀態變更時自動發送通知（需整合郵件/簡訊服務）：
- Email 通知
- SMS 簡訊通知
- 門市取貨通知

#### 1.6 數據儀表板
- **端點**: `GET /api/orders/statistics`
- **功能**: 提供可視化統計資料
  - 每日訂單趨勢
  - 金額分布
  - 狀態占比
  - 每日金額趨勢

**快速統計**:
- 今日統計: `GET /api/orders/statistics/today`
- 本週統計: `GET /api/orders/statistics/week`
- 本月統計: `GET /api/orders/statistics/month`

### 2. 進階功能

#### 2.1 訂單項目管理
每張訂單可包含多個商品項目，每個項目包含：
- 商品資訊（名稱、SKU、規格）
- 單價與數量
- 折扣金額
- 小計與實際金額

#### 2.2 金流管理
- **創建付款記錄**: `POST /api/orders/payments`
- **更新付款狀態**: `PATCH /api/orders/payments/{paymentId}/status`
- **申請退款**: `POST /api/orders/payments/{paymentId}/refund`
- **查詢付款記錄**: `GET /api/orders/payments/order/{orderId}`

**付款狀態**:
- `PENDING`: 待付款
- `PAID`: 已付款
- `REFUNDING`: 退款中
- `REFUNDED`: 已退款

#### 2.3 物流管理
- **創建物流記錄**: `POST /api/orders/shipments`
- **更新物流狀態**: `PATCH /api/orders/shipments/{shipmentId}/status`
- **更新物流單號**: `PATCH /api/orders/shipments/{shipmentId}/tracking`
- **查詢物流記錄**: `GET /api/orders/shipments/order/{orderId}`
- **追蹤物流**: `GET /api/orders/shipments/tracking/{trackingNumber}`

**物流狀態**:
- `PENDING`: 待出貨
- `SHIPPED`: 已出貨
- `DELIVERED`: 已送達
- `RETURNED`: 已退貨

#### 2.4 批次操作
- **批量更新狀態**: `PUT /api/orders/batch/status`
- **批量刪除**: `DELETE /api/orders/batch`
- **導出訂單**: `POST /api/orders/batch/export`

**批量更新範例**:
```json
{
  "orderIds": [1, 2, 3, 4, 5],
  "targetStatus": "PROCESSING",
  "operatorId": 100,
  "operatorName": "管理員",
  "notes": "批次處理訂單"
}
```

#### 2.5 O2O 支援
**取貨方式**:
- `DELIVERY`: 宅配
- `STORE_PICKUP`: 門市取貨
- `CROSS_STORE_PICKUP`: 跨店取貨

門市取貨時需指定 `storeId`，系統會發送取貨通知給客戶。

#### 2.6 暫存功能
- **查詢暫存訂單**: `GET /api/orders/draft?isDraft=true`
- 建立訂單時設定 `isDraft: true` 即可暫存

#### 2.7 黑名單管理
- **新增黑名單**: `POST /api/orders/blacklist`
- **移除黑名單**: `PATCH /api/orders/blacklist/{blacklistId}/remove`
- **檢查黑名單**: `GET /api/orders/blacklist/check/{customerId}`
- **查詢黑名單**: `GET /api/orders/blacklist`

被加入黑名單的客戶無法建立新訂單。

#### 2.8 折扣管理
- **新增折扣**: `POST /api/orders/discounts`
- **查詢折扣**: `GET /api/orders/discounts/order/{orderId}`
- **刪除折扣**: `DELETE /api/orders/discounts/{discountId}`

#### 2.9 問與答（Q&A）功能
- **提問**: `POST /api/orders/qa`
- **回答**: `PATCH /api/orders/qa/{qaId}/answer`
- **查詢問答**: `GET /api/orders/qa/order/{orderId}`

允許顧客與店家針對訂單進行溝通。

#### 2.10 歷史紀錄管理
- **查詢歷史**: `GET /api/orders/history/order/{orderId}`
- 自動記錄所有訂單操作，包括：
  - 訂單建立、更新、刪除
  - 狀態變更
  - 付款記錄
  - 物流更新
  - 折扣添加

## 訂單狀態流程

```
PENDING_PAYMENT (待付款)
    ↓
PROCESSING (處理中)
    ↓
COMPLETED (已完成)

或

CANCELLED (已取消)
REFUNDED (已退款)
```

## 權限管理建議

雖然當前版本未實作權限控制，但建議實作以下角色權限：

### 管理員 (ADMIN)
- 完整訂單管理權限
- 批次操作
- 統計查詢
- 黑名單管理

### 客服 (CUSTOMER_SERVICE)
- 查詢訂單
- 更新訂單狀態
- 回覆問答
- 查看歷史記錄

### 倉庫人員 (WAREHOUSE)
- 查詢訂單
- 更新物流狀態
- 更新物流單號

### 客戶 (CUSTOMER)
- 查詢自己的訂單
- 提問問題

## 資料庫結構

### 核心資料表

1. **orders** - 訂單主表
2. **order_items** - 訂單項目
3. **order_payments** - 付款記錄
4. **order_shipments** - 物流記錄
5. **order_history** - 歷史記錄
6. **order_notifications** - 通知記錄
7. **order_discounts** - 折扣記錄
8. **order_qa** - 問與答
9. **customer_blacklist** - 顧客黑名單

## Swagger API 文件

啟動應用程式後，可透過以下網址訪問 Swagger UI：

```
http://localhost:8080/swagger-ui.html
```

所有 API 端點都已加上完整的 OpenAPI 註解，可在 Swagger UI 中直接測試。

## 使用範例

### 完整訂單流程範例

#### 1. 建立訂單
```bash
POST /api/orders
```

#### 2. 建立付款記錄
```bash
POST /api/orders/payments
{
  "orderId": 1,
  "paymentStatus": "PAID",
  "paymentMethod": "信用卡",
  "paymentAmount": 960.00
}
```

#### 3. 更新訂單狀態為處理中
```bash
PATCH /api/orders/1/status?status=PROCESSING
```

#### 4. 建立物流記錄
```bash
POST /api/orders/shipments
{
  "orderId": 1,
  "shippingStatus": "SHIPPED",
  "shippingCompany": "黑貓宅急便",
  "trackingNumber": "123456789",
  "recipientName": "張三",
  "recipientPhone": "0912345678",
  "recipientAddress": "台北市信義區信義路五段7號"
}
```

#### 5. 更新訂單狀態為已完成
```bash
PATCH /api/orders/1/status?status=COMPLETED
```

## 技術細節

- **後端框架**: Spring Boot 3.4.1
- **Java 版本**: 17
- **資料庫**: Microsoft SQL Server
- **ORM**: Spring Data JPA / Hibernate
- **API 文件**: SpringDoc OpenAPI (Swagger)
- **驗證**: Jakarta Validation
- **日誌**: SLF4J + Logback

## 測試

單元測試範例請參考 `src/test/java` 目錄。

運行測試：
```bash
mvn test
```

## 注意事項

1. **通知功能**: 當前版本的通知功能僅記錄日誌，需整合實際的郵件服務（如 SendGrid、AWS SES）和簡訊服務（如 Twilio）。

2. **安全性**: 建議整合 Spring Security 實作完整的身份驗證和授權機制。

3. **交易管理**: 所有涉及資料庫寫入的操作都使用 `@Transactional` 確保資料一致性。

4. **錯誤處理**: 使用全局異常處理器 `GlobalExceptionHandler` 統一處理異常。

5. **分頁**: 所有列表查詢都支援分頁，預設每頁 20 筆記錄。

## 擴展建議

1. **支付整合**: 整合第三方支付平台（如綠界、藍新金流）
2. **物流整合**: 整合物流 API 自動更新物流狀態
3. **訂單匯出**: 實作 Excel/CSV 匯出功能
4. **發票整合**: 整合電子發票系統
5. **多語系支援**: 支援多國語言
6. **快取機制**: 使用 Redis 快取熱門查詢
7. **訊息佇列**: 使用 RabbitMQ 或 Kafka 處理非同步任務

## 授權

本專案遵循專案主要授權條款。

## 聯絡方式

如有問題或建議，請聯絡開發團隊或提交 Issue。
