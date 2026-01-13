# 管理人員行事曆模組 API 文檔

## 模組概述

管理人員行事曆模組提供完整的行事曆事件管理功能，整合商品上下架時間、行銷活動期間、特殊活動和訂單相關截止日期，支援時間衝突檢測、預覽功能和批量操作。

## API 端點

### 1. 獲取行事曆事件

**GET** `/api/admin/calendar/events`

獲取行事曆事件列表，支援分頁和條件查詢。

**查詢參數：**
- `page` (可選): 頁碼，預設為 1
- `pageSize` (可選): 每頁數量，預設為 20
- `type` (可選): 事件類型 (PRODUCT_LISTING, MARKETING_ACTIVITY, SPECIAL_EVENT, ORDER_DEADLINE)
- `entityType` (可選): 關聯實體類型 (PRODUCT, MARKETING_CAMPAIGN, PROMOTION, ORDER)
- `entityId` (可選): 關聯實體ID
- `startTime` (可選): 開始時間 (ISO 8601 格式)
- `endTime` (可選): 結束時間 (ISO 8601 格式)
- `keyword` (可選): 關鍵字搜尋（標題或描述）

**回應範例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "data": [
      {
        "id": 1,
        "type": "PRODUCT_LISTING",
        "entityId": 1,
        "entityType": "PRODUCT",
        "startTime": "2024-12-01T00:00:00",
        "endTime": "2024-12-31T23:59:59",
        "title": "商品上架",
        "description": "商品A上架",
        "color": "#3498db"
      }
    ],
    "total": 1,
    "page": 1,
    "pageSize": 20
  }
}
```

### 2. 獲取事件詳情

**GET** `/api/admin/calendar/events/{id}`

獲取指定事件的詳細資訊。

**路徑參數：**
- `id`: 事件ID

**回應範例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "type": "PRODUCT_LISTING",
    "entityId": 1,
    "entityType": "PRODUCT",
    "startTime": "2024-12-01T00:00:00",
    "endTime": "2024-12-31T23:59:59",
    "title": "商品上架",
    "description": "商品A上架",
    "color": "#3498db"
  }
}
```

### 3. 建立事件

**POST** `/api/admin/calendar/events`

建立新的行事曆事件。

**請求體：**
```json
{
  "type": "PRODUCT_LISTING",
  "entityId": 1,
  "entityType": "PRODUCT",
  "startTime": "2024-12-01T00:00:00",
  "endTime": "2024-12-31T23:59:59",
  "title": "商品上架",
  "description": "商品A上架",
  "color": "#3498db"
}
```

**回應範例：**
```json
{
  "success": true,
  "message": "事件已建立",
  "data": {
    "id": 1,
    "type": "PRODUCT_LISTING",
    ...
  }
}
```

### 4. 更新事件

**PUT** `/api/admin/calendar/events/{id}`

更新指定事件的資訊。

**路徑參數：**
- `id`: 事件ID

**請求體：** 同建立事件

### 5. 刪除事件

**DELETE** `/api/admin/calendar/events/{id}`

刪除指定事件。

**路徑參數：**
- `id`: 事件ID

### 6. 檢查時間衝突

**GET** `/api/admin/calendar/conflicts`

檢查時間衝突。

**查詢參數：**
- `eventId` (可選): 事件ID（檢查特定事件）
- `startTime` (可選): 開始時間
- `endTime` (可選): 結束時間
- `entityType` (可選): 實體類型

**回應範例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "conflictType": "PRODUCT_DELISTING_DURING_ACTIVITY",
      "description": "商品將在活動期間下架，可能影響活動效果",
      "conflictingEventIds": [2],
      "conflictingEventTitles": ["雙12購物節"],
      "suggestion": "建議延長商品下架時間或提前結束活動"
    }
  ]
}
```

### 7. 預覽時間變更效果

**GET** `/api/admin/calendar/preview`

預覽特定時間點的實際效果。

**查詢參數：**
- `previewTime` (必填): 預覽時間點 (ISO 8601 格式)

**回應範例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "previewTime": "2024-12-15T12:00:00",
    "listedProducts": [
      {
        "productId": 1,
        "productName": "商品A",
        "sku": "SKU001",
        "stock": 100
      }
    ],
    "activeActivities": [
      {
        "activityId": 1,
        "activityName": "雙12購物節",
        "activityType": "MARKETING_CAMPAIGN"
      }
    ],
    "impactDescription": "預計有 1 個商品上架，1 個活動進行中"
  }
}
```

### 8. 批量更新

**POST** `/api/admin/calendar/batch-update`

批量更新多個事件的時間。

**請求體：**
```json
{
  "eventIds": [1, 2, 3],
  "newStartTime": "2024-12-01T00:00:00",
  "newEndTime": "2024-12-31T23:59:59",
  "updateRelatedProducts": false,
  "updateRelatedActivities": false
}
```

### 9. 批量刪除

**POST** `/api/admin/calendar/batch-delete`

批量刪除多個事件。

**請求體：**
```json
[1, 2, 3]
```

## 事件類型說明

- **PRODUCT_LISTING**: 商品上架/下架時間
- **MARKETING_ACTIVITY**: 行銷活動期間
- **SPECIAL_EVENT**: 特殊活動
- **ORDER_DEADLINE**: 訂單相關截止日期

## 實體類型說明

- **PRODUCT**: 商品
- **MARKETING_CAMPAIGN**: 行銷活動
- **PROMOTION**: 促銷活動
- **ORDER**: 訂單

## 顏色編碼

系統會根據事件類型自動設置預設顏色：
- PRODUCT_LISTING: #3498db (藍色)
- MARKETING_ACTIVITY: #2ecc71 (綠色)
- SPECIAL_EVENT: #f39c12 (橙色)
- ORDER_DEADLINE: #9b59b6 (紫色)

## 資料庫初始化

執行以下 SQL 腳本初始化資料庫表：

```sql
-- 見 E-commerce/database/migration/create_calendar_events_table.sql
```

## 使用範例

### 建立商品上架事件

```bash
curl -X POST http://localhost:8080/api/admin/calendar/events \
  -H "Content-Type: application/json" \
  -d '{
    "type": "PRODUCT_LISTING",
    "entityId": 1,
    "entityType": "PRODUCT",
    "startTime": "2024-12-01T00:00:00",
    "endTime": "2024-12-31T23:59:59",
    "title": "商品A上架",
    "description": "商品A將於12月1日上架"
  }'
```

### 查詢特定時間範圍的事件

```bash
curl "http://localhost:8080/api/admin/calendar/events?startTime=2024-12-01T00:00:00&endTime=2024-12-31T23:59:59"
```

### 檢查衝突

```bash
curl "http://localhost:8080/api/admin/calendar/conflicts"
```

