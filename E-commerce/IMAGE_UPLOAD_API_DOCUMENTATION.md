# 圖片上傳管理模組 API 文檔

## 概述

圖片上傳管理模組提供安全、高效的圖片和附件上傳功能，包括：
- 多檔案批量上傳支援
- 安全性檢查（Magic Number 驗證）
- 自動生成縮圖（異步處理）
- 檔案大小和格式驗證
- UUID 重新命名防止檔案衝突
- 支援本地和 AWS S3 儲存

## API 端點

### 1. 上傳單個檔案

**POST** `/api/attachments/upload/single`

上傳單個圖片或附件檔案。

#### 請求參數

| 參數名 | 類型 | 必填 | 說明 |
|--------|------|------|------|
| file | File | 是 | 要上傳的檔案 |
| category | String | 否 | 用途分類（例如：UserAvatar, ProductImage） |
| createdBy | String | 否 | 上傳者 ID |

#### 請求範例

```bash
curl -X POST "http://localhost:8080/api/attachments/upload/single" \
  -F "file=@/path/to/image.jpg" \
  -F "category=UserAvatar" \
  -F "createdBy=user123"
```

#### 回應範例

```json
{
  "success": true,
  "message": "檔案上傳成功",
  "data": {
    "id": 1,
    "fileName": "cat.jpg",
    "filePath": "2024/12/24/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "fileSize": 102400,
    "fileType": "image/jpeg",
    "category": "UserAvatar",
    "thumbnailPath": "2024/12/24/a1b2c3d4-e5f6-7890-abcd-ef1234567890_thumb.jpg",
    "createdBy": "user123",
    "createdAt": "2024-12-24T13:45:00"
  }
}
```

---

### 2. 批量上傳檔案

**POST** `/api/attachments/upload`

批量上傳多個圖片或附件檔案。

#### 請求參數

| 參數名 | 類型 | 必填 | 說明 |
|--------|------|------|------|
| files | File[] | 是 | 要上傳的檔案列表 |
| category | String | 否 | 用途分類 |
| createdBy | String | 否 | 上傳者 ID |

#### 請求範例

```bash
curl -X POST "http://localhost:8080/api/attachments/upload" \
  -F "files=@/path/to/image1.jpg" \
  -F "files=@/path/to/image2.jpg" \
  -F "category=ProductGallery" \
  -F "createdBy=admin"
```

#### 回應範例

```json
{
  "success": true,
  "message": "檔案上傳完成",
  "data": {
    "successFiles": [
      {
        "id": 1,
        "fileName": "image1.jpg",
        "filePath": "2024/12/24/uuid1.jpg",
        "fileSize": 204800,
        "fileType": "image/jpeg",
        "category": "ProductGallery",
        "thumbnailPath": "2024/12/24/uuid1_thumb.jpg",
        "createdBy": "admin",
        "createdAt": "2024-12-24T13:45:00"
      },
      {
        "id": 2,
        "fileName": "image2.jpg",
        "filePath": "2024/12/24/uuid2.jpg",
        "fileSize": 153600,
        "fileType": "image/png",
        "category": "ProductGallery",
        "thumbnailPath": "2024/12/24/uuid2_thumb.jpg",
        "createdBy": "admin",
        "createdAt": "2024-12-24T13:45:01"
      }
    ],
    "failedFiles": []
  }
}
```

---

### 3. 查詢附件詳情

**GET** `/api/attachments/{id}`

根據 ID 查詢附件的詳細資訊。

#### 路徑參數

| 參數名 | 類型 | 必填 | 說明 |
|--------|------|------|------|
| id | Long | 是 | 附件 ID |

#### 請求範例

```bash
curl -X GET "http://localhost:8080/api/attachments/1"
```

#### 回應範例

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "fileName": "cat.jpg",
    "filePath": "2024/12/24/uuid.jpg",
    "fileSize": 102400,
    "fileType": "image/jpeg",
    "category": "UserAvatar",
    "thumbnailPath": "2024/12/24/uuid_thumb.jpg",
    "createdBy": "user123",
    "createdAt": "2024-12-24T13:45:00"
  }
}
```

---

### 4. 根據分類查詢附件

**GET** `/api/attachments/category/{category}`

查詢指定分類的所有附件。

#### 路徑參數

| 參數名 | 類型 | 必填 | 說明 |
|--------|------|------|------|
| category | String | 是 | 用途分類 |

#### 請求範例

```bash
curl -X GET "http://localhost:8080/api/attachments/category/UserAvatar"
```

#### 回應範例

```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "fileName": "avatar1.jpg",
      "filePath": "2024/12/24/uuid1.jpg",
      "fileSize": 51200,
      "fileType": "image/jpeg",
      "category": "UserAvatar",
      "thumbnailPath": "2024/12/24/uuid1_thumb.jpg",
      "createdBy": "user1",
      "createdAt": "2024-12-24T10:30:00"
    },
    {
      "id": 2,
      "fileName": "avatar2.jpg",
      "filePath": "2024/12/24/uuid2.jpg",
      "fileSize": 76800,
      "fileType": "image/png",
      "category": "UserAvatar",
      "thumbnailPath": "2024/12/24/uuid2_thumb.jpg",
      "createdBy": "user2",
      "createdAt": "2024-12-24T11:15:00"
    }
  ]
}
```

---

### 5. 刪除附件

**DELETE** `/api/attachments/{id}`

刪除指定的附件及其實體檔案（包括縮圖）。

#### 路徑參數

| 參數名 | 類型 | 必填 | 說明 |
|--------|------|------|------|
| id | Long | 是 | 附件 ID |

#### 請求範例

```bash
curl -X DELETE "http://localhost:8080/api/attachments/1"
```

#### 回應範例

```json
{
  "success": true,
  "message": "附件刪除成功",
  "data": null
}
```

---

## 錯誤回應

當發生錯誤時，API 會返回以下格式的錯誤訊息：

```json
{
  "success": false,
  "message": "錯誤訊息描述",
  "data": null
}
```

### 常見錯誤訊息

| 錯誤訊息 | 說明 | HTTP 狀態碼 |
|---------|------|------------|
| 檔案不能為空 | 上傳的檔案為空 | 400 |
| 檔案大小超過限制，最大允許 10MB | 檔案超過大小限制 | 400 |
| 不支援的檔案類型 | 檔案類型不在允許列表中 | 400 |
| 附件不存在 | 查詢或刪除時找不到指定 ID 的附件 | 400 |

---

## 配置說明

### application.properties 配置項

```properties
# Spring 內建檔案上傳配置
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB

# 自定義檔案上傳配置
file.upload.storage-type=LOCAL
file.upload.local-path=./uploads
file.upload.max-file-size=10485760
file.upload.thumbnail-width=200
file.upload.thumbnail-height=200

# AWS S3 配置（選用）
file.upload.s3.bucket-name=your-bucket-name
file.upload.s3.region=us-east-1
file.upload.s3.access-key=your-access-key
file.upload.s3.secret-key=your-secret-key
```

### 配置說明

- **storage-type**: 儲存類型，可選 `LOCAL`（本地儲存）或 `S3`（AWS S3）
- **local-path**: 本地儲存路徑
- **max-file-size**: 最大檔案大小（bytes）
- **thumbnail-width**: 縮圖寬度（像素）
- **thumbnail-height**: 縮圖高度（像素）

---

## 安全特性

1. **Magic Number 驗證**：使用 Apache Tika 檢測檔案的真實類型，防止偽裝攻擊
2. **檔案重新命名**：使用 UUID 重新命名檔案，防止路徑遍歷攻擊
3. **檔案大小限制**：限制上傳檔案的大小，防止資源耗盡
4. **檔案類型白名單**：只允許特定類型的圖片格式上傳

---

## 資料庫結構

### sys_attachment 表

| 欄位名 | 型態 | 說明 |
|--------|------|------|
| id | BIGINT | 主鍵，自增 ID |
| file_name | NVARCHAR(255) | 原始檔名 |
| file_path | NVARCHAR(500) | 儲存路徑 / Key |
| file_size | BIGINT | 檔案大小（Bytes） |
| file_type | VARCHAR(100) | MIME 類型 |
| category | NVARCHAR(50) | 用途分類 |
| thumbnail_path | NVARCHAR(500) | 縮圖路徑 |
| created_by | NVARCHAR(100) | 上傳者 ID |
| created_at | DATETIME | 上傳時間 |

### 索引

- `idx_category`: category 欄位索引
- `idx_created_by`: created_by 欄位索引
- `idx_created_at`: created_at 欄位索引

---

## 使用範例

### 前端上傳範例（JavaScript）

```javascript
// 單檔案上傳
const uploadSingleFile = async (file, category, createdBy) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('category', category);
  formData.append('createdBy', createdBy);

  const response = await fetch('/api/attachments/upload/single', {
    method: 'POST',
    body: formData
  });

  return await response.json();
};

// 多檔案上傳
const uploadMultipleFiles = async (files, category, createdBy) => {
  const formData = new FormData();
  files.forEach(file => {
    formData.append('files', file);
  });
  formData.append('category', category);
  formData.append('createdBy', createdBy);

  const response = await fetch('/api/attachments/upload', {
    method: 'POST',
    body: formData
  });

  return await response.json();
};
```

---

## 技術棧

- **Spring Boot 3.4.1**: 應用框架
- **Spring Data JPA**: 資料持久化
- **Thumbnailator 0.4.20**: 圖片縮圖處理
- **Apache Tika 2.9.2**: 檔案類型檢測
- **AWS SDK for Java 2.28.29**: AWS S3 整合（選用）

---

## 未來擴展

1. **浮水印功能**：在圖片上添加浮水印
2. **圖片壓縮**：自動壓縮大型圖片
3. **CDN 整合**：整合 CDN 加速圖片載入
4. **圖片裁剪**：支援自定義尺寸裁剪
5. **批次操作**：支援批次刪除和移動
