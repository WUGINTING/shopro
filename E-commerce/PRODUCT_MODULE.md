# Product Module (商品管理模組)

## 概述 (Overview)

本模組實現電商系統的完整商品管理功能，支持以下核心需求：

- **商品管理功能**：支持 500 ~ 10,000 項商品、100 ~ 600 個分類、10 ~ 15 張商品圖、100 個以上標籤
- **銷售模式**：預購商品、票券商品、訂閱購、門市限定商品
- **規格功能**：多規格多價格、多規格圖切換、商品購買數量限制
- **效率工具**：批次操作、庫存安全提醒、貨到通知、倉儲管理

## 模組架構 (Module Architecture)

```
com.info.ecommerce.modules.product
├── entity/           # 實體類（對應資料庫表）
├── dto/              # 數據傳輸對象
├── repository/       # 資料存取層
├── service/          # 業務邏輯層
├── controller/       # REST API 控制器
└── enums/            # 枚舉類型
```

## 核心實體 (Core Entities)

### 1. Product (商品主表)
- **用途**：存儲商品基本資訊
- **關鍵欄位**：
  - `name`: 商品名稱
  - `sku`: 商品編號
  - `categoryId`: 商品分類
  - `status`: 商品狀態（草稿、上架、下架、缺貨）
  - `salesMode`: 銷售模式（一般、預購、票券、訂閱、門市限定）
  - `basePrice`, `salePrice`, `costPrice`: 價格資訊
  - `minPurchaseQuantity`, `maxPurchaseQuantity`: 購買數量限制
- **索引**：categoryId, status, salesMode

### 2. ProductCategory (商品分類)
- **用途**：管理商品分類，支持多層級結構
- **關鍵欄位**：
  - `name`: 分類名稱
  - `parentId`: 父分類 ID（支持樹狀結構）
  - `sortOrder`: 排序
- **容量**：支持 100 ~ 600 個分類

### 3. ProductImage (商品圖片)
- **用途**：管理商品圖片
- **關鍵欄位**：
  - `productId`: 關聯商品
  - `imageUrl`: 圖片路徑
  - `isPrimary`: 是否主圖
  - `sortOrder`: 排序
- **容量**：每個商品支持 10 ~ 15 張圖片

### 4. ProductTag (商品標籤)
- **用途**：標籤管理
- **關鍵欄位**：
  - `name`: 標籤名稱（唯一）
  - `color`: 標籤顏色
- **容量**：支持 100 個以上標籤

### 5. ProductSpecification (商品規格)
- **用途**：管理商品規格（如顏色、尺寸等）
- **關鍵欄位**：
  - `productId`: 關聯商品
  - `specName`: 規格名稱（例如：顏色:紅色,尺寸:L）
  - `sku`: 規格獨立 SKU
  - `price`, `cost`: 規格價格和成本
  - `stock`: 規格庫存
  - `image`: 規格圖片
- **功能**：支持多規格多價格、規格圖切換

### 6. ProductInventory (商品庫存)
- **用途**：庫存管理
- **關鍵欄位**：
  - `productId`, `specificationId`: 關聯商品和規格
  - `warehouseId`: 關聯倉庫
  - `availableStock`: 可用庫存
  - `lockedStock`: 鎖定庫存（已下單未出貨）
  - `safetyStock`: 安全庫存量
- **功能**：自動計算庫存警示等級

## 銷售模式實體 (Sales Mode Entities)

### 1. ProductPreOrder (預購商品)
- `preOrderStartTime`, `preOrderEndTime`: 預購時間範圍
- `estimatedShipTime`: 預計出貨時間
- `quantityLimit`: 預購數量限制

### 2. ProductVoucher (票券商品)
- `validFrom`, `validTo`: 票券有效期
- `usageInstructions`: 使用說明
- `refundable`: 是否可退款

### 3. ProductSubscription (訂閱商品)
- `subscriptionPeriodDays`: 訂閱週期（天數）
- `periodType`: 週期類型（每日、每週、每月）
- `minPeriods`, `maxPeriods`: 訂閱期數限制
- `autoRenew`: 是否自動續訂

## 效率工具實體 (Efficiency Tools Entities)

### 1. InventoryAlert (庫存警示)
- **用途**：記錄庫存警示
- **警示等級**：
  - `LOW`: 低庫存
  - `CRITICAL`: 嚴重缺貨
  - `OUT_OF_STOCK`: 無庫存

### 2. StockNotification (貨到通知)
- **用途**：用戶訂閱缺貨商品的到貨通知
- **關鍵欄位**：
  - `userEmail`, `userPhone`: 聯絡資訊
  - `notified`: 是否已通知

### 3. Warehouse (倉庫管理)
- **用途**：管理多倉庫
- **關鍵欄位**：
  - `name`, `code`: 倉庫名稱和編號
  - `address`: 倉庫地址
  - `isDefault`: 是否預設倉庫

## REST API 端點 (REST API Endpoints)

### 商品管理 API
- `POST /api/products` - 創建商品
- `PUT /api/products/{id}` - 更新商品
- `GET /api/products/{id}` - 取得商品詳情
- `DELETE /api/products/{id}` - 刪除商品
- `GET /api/products` - 分頁查詢商品
- `GET /api/products/category/{categoryId}` - 依分類查詢
- `GET /api/products/status/{status}` - 依狀態查詢
- `GET /api/products/search?keyword={keyword}` - 搜尋商品
- `PUT /api/products/{id}/activate` - 上架商品
- `PUT /api/products/{id}/deactivate` - 下架商品

### 商品分類 API
- `POST /api/product-categories` - 創建分類
- `PUT /api/product-categories/{id}` - 更新分類
- `GET /api/product-categories/{id}` - 取得分類詳情
- `DELETE /api/product-categories/{id}` - 刪除分類
- `GET /api/product-categories` - 取得所有分類
- `GET /api/product-categories/enabled` - 取得已啟用分類
- `GET /api/product-categories/top` - 取得頂層分類
- `GET /api/product-categories/{parentId}/children` - 取得子分類

### 批次操作 API
- `PUT /api/products/batch` - 批次更新商品
- `DELETE /api/products/batch` - 批次刪除商品
- `PUT /api/products/batch/activate` - 批次上架
- `PUT /api/products/batch/deactivate` - 批次下架

### 倉庫管理 API
- `POST /api/warehouses` - 創建倉庫
- `PUT /api/warehouses/{id}` - 更新倉庫
- `GET /api/warehouses/{id}` - 取得倉庫詳情
- `DELETE /api/warehouses/{id}` - 刪除倉庫
- `GET /api/warehouses` - 取得所有倉庫
- `GET /api/warehouses/enabled` - 取得已啟用倉庫
- `GET /api/warehouses/default` - 取得預設倉庫

### 庫存管理 API
- `POST /api/inventory/check-alerts` - 檢查庫存並創建警示
- `PUT /api/inventory/alerts/{alertId}/resolve` - 解決庫存警示
- `GET /api/inventory/alerts` - 取得未解決的警示
- `GET /api/inventory/alerts/product/{productId}` - 取得指定商品的警示
- `POST /api/inventory/notifications/subscribe` - 訂閱貨到通知
- `POST /api/inventory/notifications/process/{productId}` - 處理貨到通知
- `PUT /api/inventory/update` - 更新庫存

## 核心服務 (Core Services)

### 1. ProductService
- 商品的 CRUD 操作
- 商品上架/下架管理
- 商品搜尋和分頁查詢

### 2. ProductCategoryService
- 分類管理
- 支持多層級分類結構
- 分類篩選（已啟用、頂層、子分類）

### 3. ProductBatchService
- 批次更新商品
- 批次刪除商品
- 批次上架/下架

### 4. InventoryManagementService
- 庫存檢查和警示創建
- 庫存警示管理
- 貨到通知訂閱和處理
- 庫存更新

### 5. WarehouseService
- 倉庫管理
- 預設倉庫設定

## 技術特點 (Technical Features)

1. **可擴展性**：
   - 支持 500 ~ 10,000 項商品
   - 分類數 100 ~ 600 個
   - 標籤數量無上限

2. **性能優化**：
   - 資料庫索引優化（categoryId, status, salesMode）
   - 分頁查詢支持

3. **業務彈性**：
   - 支持多種銷售模式
   - 多規格多價格
   - 多倉庫管理

4. **自動化功能**：
   - 庫存自動警示
   - 貨到自動通知

## 資料庫設計 (Database Design)

所有實體使用 JPA/Hibernate 自動映射到資料庫表：
- 表名使用下劃線命名法（如 `product_category`）
- 中文欄位使用 `NVARCHAR` 類型
- 外鍵使用 `_id` 後綴
- 自動管理 `createdAt` 和 `updatedAt` 時間戳

## API 文檔 (API Documentation)

所有 API 端點通過 Swagger/OpenAPI 自動生成文檔，可訪問：
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

## 使用範例 (Usage Examples)

### 創建商品
```json
POST /api/products
{
  "name": "經典白T恤",
  "sku": "SKU001",
  "categoryId": 1,
  "salesMode": "NORMAL",
  "basePrice": 499.00,
  "salePrice": 399.00,
  "status": "ACTIVE"
}
```

### 批次更新商品
```json
PUT /api/products/batch
{
  "productIds": [1, 2, 3],
  "status": "ACTIVE",
  "categoryId": 5
}
```

### 訂閱貨到通知
```
POST /api/inventory/notifications/subscribe
?productId=1&email=user@example.com
```

## 未來擴展 (Future Extensions)

1. 商品評論和評分系統
2. 商品推薦算法
3. 價格歷史追蹤
4. 批次匯入/匯出功能
5. 商品圖片壓縮和 CDN 整合
6. 庫存預警郵件/簡訊通知
7. 多語系商品資訊支持
