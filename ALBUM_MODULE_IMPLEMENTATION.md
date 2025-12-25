# 相冊管理系統實現文檔

## 系統概述

本專案實現了一個完整的圖片檔案管理系統（相冊），包含前後端完整功能。

## 後端實現 (Spring Boot)

### 1. 資料庫實體 (Entity)

#### Album.java
- 相冊主實體
- 欄位：
  - `id`: 主鍵
  - `name`: 相冊名稱
  - `description`: 相冊描述
  - `coverImageUrl`: 封面圖片 URL
  - `images`: 相冊中的圖片清單（一對多關係）
  - `createdAt`: 創建時間
  - `updatedAt`: 更新時間

#### AlbumImage.java
- 相冊圖片實體
- 欄位：
  - `id`: 主鍵
  - `album`: 所屬相冊（多對一關係）
  - `imageUrl`: 圖片 URL
  - `fileName`: 檔案名稱
  - `title`: 圖片標題
  - `description`: 圖片描述
  - `fileSize`: 檔案大小
  - `fileType`: 檔案類型（MIME type）
  - `sortOrder`: 排序順序
  - `uploadedAt`: 上傳時間

### 2. 資料傳輸物件 (DTO)

- **AlbumDTO**: 用於相冊資料傳輸
- **AlbumImageDTO**: 用於圖片資料傳輸
- 包含驗證註解（@NotBlank, @Size 等）

### 3. Repository 層

- **AlbumRepository**: 相冊資料存取介面
- **AlbumImageRepository**: 圖片資料存取介面
  - 支援依相冊 ID 查詢並按排序順序排列

### 4. Service 層

#### FileStorageService
- 檔案儲存服務
- 功能：
  - `storeFile()`: 儲存上傳的檔案
  - `deleteFile()`: 刪除檔案
  - `getFilePath()`: 取得檔案路徑
- 支援圖片格式驗證
- 檔案大小限制：10MB

#### AlbumService
- 相冊業務邏輯服務
- 功能：
  - `createAlbum()`: 創建相冊
  - `updateAlbum()`: 更新相冊
  - `getAlbum()`: 取得相冊詳情
  - `deleteAlbum()`: 刪除相冊及其所有圖片
  - `listAlbums()`: 分頁查詢相冊
  - `uploadImage()`: 上傳圖片到相冊
  - `deleteImage()`: 刪除相冊中的圖片
  - `getAlbumImages()`: 取得相冊中的所有圖片

### 5. Controller 層

#### AlbumController
- RESTful API 控制器
- 端點：
  - `POST /api/albums`: 創建相冊
  - `PUT /api/albums/{id}`: 更新相冊
  - `GET /api/albums/{id}`: 取得相冊詳情
  - `DELETE /api/albums/{id}`: 刪除相冊
  - `GET /api/albums`: 分頁查詢相冊
  - `POST /api/albums/{albumId}/images`: 上傳圖片
  - `DELETE /api/albums/{albumId}/images/{imageId}`: 刪除圖片
  - `GET /api/albums/{albumId}/images`: 取得相冊圖片
  - `GET /api/albums/images/{filename}`: 下載/查看圖片

### 6. 安全配置

- 使用 Spring Security 角色權限控制
- `@PreAuthorize` 註解保護敏感操作
- 管理員和經理可以創建/更新相冊和上傳圖片
- 只有管理員可以刪除相冊

### 7. 檔案上傳配置

在 `application.properties` 中配置：
```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads/images
```

## 前端實現 (Vue 3 + TypeScript + Quasar)

### 1. API 服務層

#### album.ts
- 完整的相冊 API 服務
- 介面定義：
  - `Album`: 相冊介面
  - `AlbumImage`: 相冊圖片介面
- API 方法：
  - `getAlbums()`: 獲取相冊列表
  - `getAlbum()`: 獲取相冊詳情
  - `createAlbum()`: 創建相冊
  - `updateAlbum()`: 更新相冊
  - `deleteAlbum()`: 刪除相冊
  - `getAlbumImages()`: 獲取相冊圖片
  - `uploadImage()`: 上傳圖片
  - `deleteImage()`: 刪除圖片
  - `getImageUrl()`: 獲取圖片 URL

### 2. 視圖頁面

#### AlbumView.vue
相冊列表頁面，功能包括：
- 相冊網格展示
  - 封面圖片預覽
  - 相冊名稱和描述
  - 圖片數量顯示
- 操作功能
  - 新增相冊按鈕
  - 編輯相冊
  - 刪除相冊（附確認對話框）
  - 點擊相冊進入詳情頁
- 分頁導航
- 響應式設計（支援不同螢幕尺寸）

#### AlbumDetailView.vue
相冊詳情頁面，功能包括：
- 返回按鈕
- 相冊資訊展示
- 圖片網格展示
  - 圖片縮圖展示
  - 圖片標題和描述
  - 圖片刪除功能
  - 點擊圖片查看大圖
- 上傳圖片對話框
  - 檔案選擇
  - 圖片標題輸入
  - 圖片描述輸入
  - 檔案大小驗證（最大 10MB）
- 圖片查看對話框
  - 大圖預覽
  - 圖片資訊展示
- 響應式網格佈局

### 3. 路由配置

在 `router/index.ts` 中新增路由：
```typescript
{
  path: '/albums',
  name: 'albums',
  component: AlbumView,
  meta: { requiresAuth: true }
},
{
  path: '/albums/:id',
  name: 'albumDetail',
  component: AlbumDetailView,
  meta: { requiresAuth: true }
}
```

### 4. 導航整合

#### App.vue
- 在側邊欄新增「相冊管理」選單項
- 圖示：`photo_library`
- 連結到 `/albums` 路由

#### HomeView.vue
- 在快速操作區新增「相冊管理」按鈕
- 顏色：紫色（purple）
- 圖示：`photo_library`

### 5. UI 特性

- 使用 Quasar Framework 組件庫
- Material Design 風格
- 響應式設計
  - 手機：1 列
  - 平板：2-3 列
  - 桌面：4-6 列
- 懸停效果和轉場動畫
- 載入狀態指示器
- 錯誤處理和提示訊息
- 確認對話框（刪除操作）

## 功能特點

### 1. 相冊管理
- ✅ 創建相冊
- ✅ 編輯相冊資訊
- ✅ 刪除相冊（包含所有圖片）
- ✅ 分頁瀏覽相冊
- ✅ 自動設定封面圖片

### 2. 圖片管理
- ✅ 上傳圖片到相冊
- ✅ 新增圖片標題和描述
- ✅ 刪除圖片
- ✅ 查看大圖
- ✅ 圖片排序
- ✅ 檔案類型驗證
- ✅ 檔案大小限制（10MB）

### 3. 安全性
- ✅ 角色權限控制
- ✅ JWT 認證
- ✅ 檔案類型驗證
- ✅ 檔案大小限制
- ✅ XSS 防護（Spring Security）

### 4. 使用者體驗
- ✅ 響應式設計
- ✅ 直觀的 UI
- ✅ 即時反饋
- ✅ 錯誤處理
- ✅ 載入狀態
- ✅ 確認對話框
- ✅ 成功/錯誤通知

## 資料庫架構

### album 表
```sql
CREATE TABLE album (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(1000),
    cover_image_url NVARCHAR(500),
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2 NOT NULL
);

CREATE INDEX idx_created_at ON album(created_at);
```

### album_image 表
```sql
CREATE TABLE album_image (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    album_id BIGINT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    file_name NVARCHAR(255) NOT NULL,
    title NVARCHAR(200),
    description NVARCHAR(500),
    file_size BIGINT,
    file_type VARCHAR(100),
    sort_order INT,
    uploaded_at DATETIME2 NOT NULL,
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);

CREATE INDEX idx_album_id ON album_image(album_id);
CREATE INDEX idx_uploaded_at ON album_image(uploaded_at);
```

## API 文檔

完整的 API 文檔可透過 Swagger UI 查看：
- URL: `http://localhost:8080/swagger-ui.html`
- API 標籤：「相冊管理」

## 使用說明

### 啟動後端
```bash
cd E-commerce
./mvnw spring-boot:run
```

### 啟動前端
```bash
cd frontend
npm install
npm run dev
```

### 訪問應用
- 前端：http://localhost:5173
- 後端 API：http://localhost:8080
- Swagger UI：http://localhost:8080/swagger-ui.html

### 測試帳號
- 管理員：admin / admin123
- 經理：manager / manager123
- 員工：staff / staff123

## 技術棧

### 後端
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Security
- MS SQL Server
- Swagger/OpenAPI
- Lombok

### 前端
- Vue 3
- TypeScript
- Quasar Framework
- Vue Router
- Pinia (狀態管理)
- Axios (HTTP 客戶端)
- Vite (建置工具)

## 未來改進方向

1. 圖片壓縮和優化
2. 圖片批次上傳
3. 圖片標籤系統
4. 圖片搜尋功能
5. 圖片拖放排序
6. 相冊分類
7. 相冊分享功能
8. 圖片編輯功能
9. 圖片 CDN 整合
10. 圖片懶加載

## 總結

本專案成功實現了一個功能完整的圖片檔案管理系統（相冊），包含：
- ✅ 完整的後端 REST API
- ✅ 資料庫實體和關係設計
- ✅ 檔案儲存服務
- ✅ 前端 UI 介面
- ✅ 相冊和圖片 CRUD 操作
- ✅ 檔案上傳功能
- ✅ 安全認證和授權
- ✅ 響應式設計
- ✅ 錯誤處理和用戶反饋

系統已準備好進行測試和部署。
