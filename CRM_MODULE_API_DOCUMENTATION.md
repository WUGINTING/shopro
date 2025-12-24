# CRM 模組 API 文檔

## 模組概述

本 CRM（Customer Relationship Management）模組為電子商務系統提供完整的顧客關係管理功能，包含會員管理、積點系統、會員等級與群組、自訂頁面、EDM 電子報、購物車提醒、獎勵制度及部落格管理。

## 模組結構

```
com.info.ecommerce.modules.crm
├── controller/          # REST API 控制器
│   ├── MemberController.java
│   ├── MemberLevelController.java
│   ├── MemberGroupController.java
│   ├── PointController.java
│   ├── CustomPageController.java
│   ├── EdmController.java
│   ├── CartReminderController.java
│   ├── RewardController.java
│   └── BlogController.java
├── dto/                 # 數據傳輸對象
│   ├── MemberDTO.java
│   ├── MemberLevelDTO.java
│   ├── MemberGroupDTO.java
│   ├── PointRecordDTO.java
│   ├── PointBatchDTO.java
│   ├── CustomPageDTO.java
│   ├── EdmCampaignDTO.java
│   ├── CartReminderDTO.java
│   ├── RewardConfigDTO.java
│   └── BlogPostDTO.java
├── entity/              # JPA 實體
│   ├── Member.java
│   ├── MemberLevel.java
│   ├── MemberGroup.java
│   ├── MemberGroupMapping.java
│   ├── PointRecord.java
│   ├── CustomPage.java
│   ├── EdmCampaign.java
│   ├── EdmSendLog.java
│   ├── CartReminder.java
│   ├── RewardConfig.java
│   ├── RewardClaim.java
│   └── BlogPost.java
├── enums/               # 枚舉類型
│   ├── MemberStatus.java
│   ├── PointType.java
│   ├── EdmStatus.java
│   └── BlogStatus.java
├── repository/          # JPA Repository
│   └── (各實體對應的 Repository)
└── service/             # 業務邏輯服務
    ├── MemberService.java
    ├── MemberLevelService.java
    ├── MemberGroupService.java
    ├── PointService.java
    ├── CustomPageService.java
    ├── EdmService.java
    ├── CartReminderService.java
    ├── RewardService.java
    └── BlogService.java
```

## API 端點

### 1. 會員管理 API (`/api/crm/members`)

#### 1.1 創建會員
- **端點**: `POST /api/crm/members`
- **描述**: 創建新會員
- **請求體**:
```json
{
  "name": "王小明",
  "email": "wang@example.com",
  "phone": "0912345678",
  "birthday": "1990-01-01",
  "gender": "M",
  "address": "台北市信義區",
  "postalCode": "100"
}
```

#### 1.2 更新會員
- **端點**: `PUT /api/crm/members/{id}`
- **描述**: 更新會員資訊

#### 1.3 取得會員詳情
- **端點**: `GET /api/crm/members/{id}`
- **描述**: 取得單一會員詳細資訊

#### 1.4 依電子郵件查詢會員
- **端點**: `GET /api/crm/members/email/{email}`
- **描述**: 使用電子郵件查詢會員

#### 1.5 刪除會員
- **端點**: `DELETE /api/crm/members/{id}`
- **描述**: 刪除會員

#### 1.6 分頁查詢會員
- **端點**: `GET /api/crm/members?page=0&size=20`
- **描述**: 分頁查詢所有會員

#### 1.7 依狀態查詢會員
- **端點**: `GET /api/crm/members/status/{status}?page=0&size=20`
- **描述**: 依會員狀態查詢
- **狀態值**: ACTIVE, INACTIVE, SUSPENDED, DELETED

#### 1.8 依等級查詢會員
- **端點**: `GET /api/crm/members/level/{levelId}?page=0&size=20`
- **描述**: 查詢特定等級的會員

#### 1.9 搜尋會員
- **端點**: `GET /api/crm/members/search?keyword=王&page=0&size=20`
- **描述**: 依關鍵字搜尋會員

#### 1.10 更新會員狀態
- **端點**: `PUT /api/crm/members/{id}/status?status=ACTIVE`
- **描述**: 更新會員狀態

#### 1.11 更新會員等級
- **端點**: `PUT /api/crm/members/{id}/level?levelId=1`
- **描述**: 更新會員等級

#### 1.12 增加會員積點
- **端點**: `POST /api/crm/members/{id}/points/add?points=100`
- **描述**: 增加會員積點

#### 1.13 扣除會員積點
- **端點**: `POST /api/crm/members/{id}/points/deduct?points=50`
- **描述**: 扣除會員積點

---

### 2. 會員等級管理 API (`/api/crm/member-levels`)

#### 2.1 創建會員等級
- **端點**: `POST /api/crm/member-levels`
- **請求體**:
```json
{
  "name": "黃金會員",
  "levelOrder": 1,
  "minSpendAmount": 10000.00,
  "discountRate": 0.95,
  "pointsMultiplier": 1.5,
  "description": "累積消費滿 10,000 元",
  "enabled": true
}
```

#### 2.2 更新會員等級
- **端點**: `PUT /api/crm/member-levels/{id}`

#### 2.3 取得會員等級詳情
- **端點**: `GET /api/crm/member-levels/{id}`

#### 2.4 刪除會員等級
- **端點**: `DELETE /api/crm/member-levels/{id}`

#### 2.5 分頁查詢會員等級
- **端點**: `GET /api/crm/member-levels?page=0&size=20`

#### 2.6 取得所有會員等級
- **端點**: `GET /api/crm/member-levels/all`
- **描述**: 取得所有會員等級（依順序排列）

#### 2.7 取得已啟用的會員等級
- **端點**: `GET /api/crm/member-levels/enabled`

#### 2.8 切換啟用狀態
- **端點**: `PUT /api/crm/member-levels/{id}/toggle-enabled`

---

### 3. 會員群組管理 API (`/api/crm/member-groups`)

#### 3.1 創建會員群組
- **端點**: `POST /api/crm/member-groups`
- **請求體**:
```json
{
  "name": "VIP 客戶群組",
  "description": "高價值客戶群組",
  "enabled": true
}
```

#### 3.2 更新會員群組
- **端點**: `PUT /api/crm/member-groups/{id}`

#### 3.3 取得會員群組詳情
- **端點**: `GET /api/crm/member-groups/{id}`

#### 3.4 刪除會員群組
- **端點**: `DELETE /api/crm/member-groups/{id}`

#### 3.5 分頁查詢會員群組
- **端點**: `GET /api/crm/member-groups?page=0&size=20`

#### 3.6 取得已啟用的會員群組
- **端點**: `GET /api/crm/member-groups/enabled`

#### 3.7 將會員加入群組
- **端點**: `POST /api/crm/member-groups/{groupId}/members/{memberId}`

#### 3.8 將會員從群組移除
- **端點**: `DELETE /api/crm/member-groups/{groupId}/members/{memberId}`

#### 3.9 取得會員所屬群組
- **端點**: `GET /api/crm/member-groups/member/{memberId}`
- **回應**: 群組 ID 列表

#### 3.10 取得群組內的會員
- **端點**: `GET /api/crm/member-groups/{groupId}/members`
- **回應**: 會員 ID 列表

---

### 4. 積點管理 API (`/api/crm/points`)

#### 4.1 增加積點
- **端點**: `POST /api/crm/points/add`
- **參數**:
  - `memberId`: 會員 ID
  - `points`: 積點數量
  - `pointType`: 積點類型 (EARN, PURCHASE, REWARD 等)
  - `reason`: 原因描述
  - `orderId`: 關聯訂單 ID（可選）

#### 4.2 扣除積點
- **端點**: `POST /api/crm/points/deduct`
- **參數**:
  - `memberId`: 會員 ID
  - `points`: 積點數量
  - `pointType`: 積點類型 (REDEEM, EXPIRE 等)
  - `reason`: 原因描述

#### 4.3 批次發放積點
- **端點**: `POST /api/crm/points/batch-grant`
- **請求體**:
```json
{
  "memberIds": [1, 2, 3],
  "points": 100,
  "reason": "週年慶活動贈送"
}
```

#### 4.4 取得會員積點紀錄
- **端點**: `GET /api/crm/points/member/{memberId}?page=0&size=20`

#### 4.5 取得積點紀錄詳情
- **端點**: `GET /api/crm/points/{id}`

#### 4.6 取得會員積點餘額
- **端點**: `GET /api/crm/points/balance/{memberId}`

---

### 5. 自訂頁面管理 API (`/api/crm/custom-pages`)

#### 5.1 創建自訂頁面
- **端點**: `POST /api/crm/custom-pages`
- **請求體**:
```json
{
  "title": "關於我們",
  "slug": "about-us",
  "content": "<h1>關於我們</h1><p>我們是...</p>",
  "metaTitle": "關於我們 - 公司簡介",
  "metaDescription": "了解我們的故事",
  "enabled": true,
  "sortOrder": 1
}
```

#### 5.2 更新自訂頁面
- **端點**: `PUT /api/crm/custom-pages/{id}`

#### 5.3 取得自訂頁面詳情
- **端點**: `GET /api/crm/custom-pages/{id}`

#### 5.4 依別名取得自訂頁面
- **端點**: `GET /api/crm/custom-pages/slug/{slug}`

#### 5.5 刪除自訂頁面
- **端點**: `DELETE /api/crm/custom-pages/{id}`

#### 5.6 分頁查詢自訂頁面
- **端點**: `GET /api/crm/custom-pages?page=0&size=20`

#### 5.7 取得所有自訂頁面
- **端點**: `GET /api/crm/custom-pages/all`

#### 5.8 取得已啟用的自訂頁面
- **端點**: `GET /api/crm/custom-pages/enabled`

#### 5.9 切換啟用狀態
- **端點**: `PUT /api/crm/custom-pages/{id}/toggle-enabled`

---

### 6. EDM 電子報管理 API (`/api/crm/edm`)

#### 6.1 創建 EDM 活動
- **端點**: `POST /api/crm/edm`
- **請求體**:
```json
{
  "name": "週年慶促銷活動",
  "subject": "週年慶優惠，全館 8 折起！",
  "content": "<html>...</html>",
  "targetGroupId": 1,
  "status": "DRAFT"
}
```

#### 6.2 更新 EDM 活動
- **端點**: `PUT /api/crm/edm/{id}`

#### 6.3 取得 EDM 活動詳情
- **端點**: `GET /api/crm/edm/{id}`

#### 6.4 刪除 EDM 活動
- **端點**: `DELETE /api/crm/edm/{id}`

#### 6.5 分頁查詢 EDM 活動
- **端點**: `GET /api/crm/edm?page=0&size=20`

#### 6.6 依狀態查詢 EDM 活動
- **端點**: `GET /api/crm/edm/status/{status}?page=0&size=20`
- **狀態值**: DRAFT, SCHEDULED, SENDING, SENT, FAILED, CANCELLED

#### 6.7 排程 EDM 活動
- **端點**: `POST /api/crm/edm/{id}/schedule?scheduledAt=2024-01-01T10:00:00`

#### 6.8 發送 EDM 活動
- **端點**: `POST /api/crm/edm/{id}/send`

#### 6.9 取消 EDM 活動
- **端點**: `POST /api/crm/edm/{id}/cancel`

#### 6.10 取得 EDM 發送紀錄
- **端點**: `GET /api/crm/edm/{id}/logs?page=0&size=20`

---

### 7. 購物車未結帳提醒 API (`/api/crm/cart-reminders`)

#### 7.1 創建購物車提醒
- **端點**: `POST /api/crm/cart-reminders`
- **請求體**:
```json
{
  "memberId": 1,
  "cartSummary": "共 3 件商品",
  "itemCount": 3
}
```

#### 7.2 更新購物車提醒
- **端點**: `PUT /api/crm/cart-reminders/{id}`

#### 7.3 取得購物車提醒詳情
- **端點**: `GET /api/crm/cart-reminders/{id}`

#### 7.4 刪除購物車提醒
- **端點**: `DELETE /api/crm/cart-reminders/{id}`

#### 7.5 分頁查詢購物車提醒
- **端點**: `GET /api/crm/cart-reminders?page=0&size=20`

#### 7.6 取得會員的購物車提醒
- **端點**: `GET /api/crm/cart-reminders/member/{memberId}?page=0&size=20`

#### 7.7 取得待發送的提醒
- **端點**: `GET /api/crm/cart-reminders/pending?hours=24`

#### 7.8 發送購物車提醒
- **端點**: `POST /api/crm/cart-reminders/{id}/send`

#### 7.9 批次發送待發送的提醒
- **端點**: `POST /api/crm/cart-reminders/send-pending?hoursThreshold=24`

---

### 8. 獎勵制度管理 API (`/api/crm/rewards`)

#### 8.1 創建獎勵設定
- **端點**: `POST /api/crm/rewards`
- **請求體**:
```json
{
  "rewardType": "WELCOME",
  "name": "新會員歡迎禮",
  "description": "註冊即送 100 積點",
  "rewardPoints": 100,
  "enabled": true
}
```

#### 8.2 更新獎勵設定
- **端點**: `PUT /api/crm/rewards/{id}`

#### 8.3 取得獎勵設定詳情
- **端點**: `GET /api/crm/rewards/{id}`

#### 8.4 刪除獎勵設定
- **端點**: `DELETE /api/crm/rewards/{id}`

#### 8.5 分頁查詢獎勵設定
- **端點**: `GET /api/crm/rewards?page=0&size=20`

#### 8.6 取得已啟用的獎勵設定
- **端點**: `GET /api/crm/rewards/enabled`

#### 8.7 領取入會禮
- **端點**: `POST /api/crm/rewards/claim/welcome/{memberId}`

#### 8.8 領取生日禮
- **端點**: `POST /api/crm/rewards/claim/birthday/{memberId}`

#### 8.9 取得會員的獎勵領取紀錄
- **端點**: `GET /api/crm/rewards/claims/member/{memberId}?page=0&size=20`

---

### 9. 部落格管理 API (`/api/crm/blog`)

#### 9.1 創建部落格文章
- **端點**: `POST /api/crm/blog`
- **請求體**:
```json
{
  "title": "2024 春季新品發布",
  "slug": "2024-spring-collection",
  "content": "<h1>春季新品</h1>...",
  "summary": "探索我們最新的春季系列",
  "coverImageUrl": "https://example.com/image.jpg",
  "status": "DRAFT",
  "authorName": "張小華",
  "tags": "春季,新品,促銷"
}
```

#### 9.2 更新部落格文章
- **端點**: `PUT /api/crm/blog/{id}`

#### 9.3 取得部落格文章詳情
- **端點**: `GET /api/crm/blog/{id}`

#### 9.4 依別名取得部落格文章
- **端點**: `GET /api/crm/blog/slug/{slug}`
- **描述**: 會自動增加瀏覽次數

#### 9.5 刪除部落格文章
- **端點**: `DELETE /api/crm/blog/{id}`

#### 9.6 分頁查詢部落格文章
- **端點**: `GET /api/crm/blog?page=0&size=20`

#### 9.7 依狀態查詢部落格文章
- **端點**: `GET /api/crm/blog/status/{status}?page=0&size=20`
- **狀態值**: DRAFT, PUBLISHED, ARCHIVED, SCHEDULED

#### 9.8 依作者查詢部落格文章
- **端點**: `GET /api/crm/blog/author/{authorId}?page=0&size=20`

#### 9.9 依標籤查詢部落格文章
- **端點**: `GET /api/crm/blog/tag/{tag}?page=0&size=20`

#### 9.10 發布部落格文章
- **端點**: `POST /api/crm/blog/{id}/publish`

#### 9.11 排程發布部落格文章
- **端點**: `POST /api/crm/blog/{id}/schedule?scheduledAt=2024-01-01T10:00:00`

#### 9.12 封存部落格文章
- **端點**: `POST /api/crm/blog/{id}/archive`

---

## 數據模型

### 會員 (Member)
- id: Long
- name: String (會員姓名)
- email: String (電子郵件，唯一)
- phone: String (手機號碼)
- birthday: LocalDate (生日)
- status: MemberStatus (會員狀態)
- levelId: Long (會員等級 ID)
- totalPoints: Integer (總積點)
- availablePoints: Integer (可用積點)
- address: String (地址)
- postalCode: String (郵遞區號)
- gender: String (性別)
- notes: String (備註)
- registeredAt: LocalDateTime (註冊日期)
- lastLoginAt: LocalDateTime (最後登入時間)

### 會員等級 (MemberLevel)
- id: Long
- name: String (等級名稱)
- levelOrder: Integer (等級順序)
- minSpendAmount: BigDecimal (所需最低消費金額)
- discountRate: BigDecimal (折扣率)
- pointsMultiplier: BigDecimal (積點倍率)
- description: String (等級描述)
- iconUrl: String (等級圖示 URL)
- enabled: Boolean (是否啟用)

### 會員群組 (MemberGroup)
- id: Long
- name: String (群組名稱)
- description: String (群組描述)
- enabled: Boolean (是否啟用)

### 積點紀錄 (PointRecord)
- id: Long
- memberId: Long (會員 ID)
- pointType: PointType (積點類型)
- points: Integer (積點數量)
- balanceAfter: Integer (變更後餘額)
- orderId: Long (關聯訂單 ID)
- reason: String (原因描述)
- expiresAt: LocalDateTime (到期時間)

### 自訂頁面 (CustomPage)
- id: Long
- title: String (頁面標題)
- slug: String (頁面別名)
- content: String (頁面內容)
- metaTitle: String (SEO 標題)
- metaDescription: String (SEO 描述)
- metaKeywords: String (SEO 關鍵字)
- enabled: Boolean (是否啟用)
- sortOrder: Integer (排序)

### EDM 活動 (EdmCampaign)
- id: Long
- name: String (活動名稱)
- subject: String (郵件主旨)
- content: String (郵件內容)
- status: EdmStatus (EDM 狀態)
- targetGroupId: Long (目標群組 ID)
- scheduledAt: LocalDateTime (排程發送時間)
- sentAt: LocalDateTime (實際發送時間)
- totalSent: Integer (總發送數)
- successCount: Integer (成功數)
- failureCount: Integer (失敗數)

### 購物車提醒 (CartReminder)
- id: Long
- memberId: Long (會員 ID)
- cartSummary: String (購物車內容摘要)
- itemCount: Integer (購物車商品數量)
- isSent: Boolean (是否已發送提醒)
- sentAt: LocalDateTime (提醒發送時間)

### 獎勵設定 (RewardConfig)
- id: Long
- rewardType: String (獎勵類型: WELCOME/BIRTHDAY/REFERRAL)
- name: String (獎勵名稱)
- description: String (獎勵描述)
- rewardPoints: Integer (獎勵積點)
- couponId: Long (優惠券 ID)
- enabled: Boolean (是否啟用)
- validDays: Integer (有效天數)

### 部落格文章 (BlogPost)
- id: Long
- title: String (文章標題)
- slug: String (文章別名)
- content: String (文章內容)
- summary: String (摘要)
- coverImageUrl: String (封面圖片 URL)
- status: BlogStatus (文章狀態)
- authorId: Long (作者 ID)
- authorName: String (作者名稱)
- tags: String (標籤)
- metaTitle: String (SEO 標題)
- metaDescription: String (SEO 描述)
- metaKeywords: String (SEO 關鍵字)
- viewCount: Integer (瀏覽次數)
- scheduledAt: LocalDateTime (排程發布時間)
- publishedAt: LocalDateTime (發布時間)

---

## 枚舉類型

### MemberStatus（會員狀態）
- ACTIVE: 啟用
- INACTIVE: 停用
- SUSPENDED: 暫停
- DELETED: 已刪除

### PointType（積點類型）
- EARN: 獲得積點
- REDEEM: 兌換積點
- EXPIRE: 過期
- ADJUST: 調整
- BATCH_GRANT: 批次發放
- PURCHASE: 購買獲得
- REFUND: 退款扣除
- REWARD: 獎勵

### EdmStatus（EDM 狀態）
- DRAFT: 草稿
- SCHEDULED: 已排程
- SENDING: 發送中
- SENT: 已發送
- FAILED: 失敗
- CANCELLED: 已取消

### BlogStatus（部落格狀態）
- DRAFT: 草稿
- PUBLISHED: 已發布
- ARCHIVED: 已封存
- SCHEDULED: 已排程

---

## 整合說明

### 與訂單模組整合
- 購買訂單時自動累積積點
- 訂單完成後觸發積點發放
- 支援使用積點折抵訂單金額

### 與行銷模組整合
- 會員群組可用於優惠券目標對象
- 會員等級可設定專屬優惠
- EDM 可針對特定群組發送促銷訊息

### 與商品模組整合
- 會員等級享有不同折扣
- 部落格可推薦商品
- 自訂頁面可展示商品分類

---

## 使用範例

### 1. 創建新會員並發放入會禮
```bash
# 步驟 1: 創建會員
POST /api/crm/members
{
  "name": "王小明",
  "email": "wang@example.com",
  "phone": "0912345678"
}

# 步驟 2: 領取入會禮
POST /api/crm/rewards/claim/welcome/1
```

### 2. 設定並發送 EDM 活動
```bash
# 步驟 1: 創建 EDM 活動
POST /api/crm/edm
{
  "name": "週年慶",
  "subject": "週年慶優惠",
  "content": "<html>...</html>",
  "targetGroupId": 1
}

# 步驟 2: 排程發送
POST /api/crm/edm/1/schedule?scheduledAt=2024-01-01T10:00:00

# 步驟 3: 立即發送（或等待排程時間）
POST /api/crm/edm/1/send
```

### 3. 批次發放積點
```bash
POST /api/crm/points/batch-grant
{
  "memberIds": [1, 2, 3, 4, 5],
  "points": 100,
  "reason": "週年慶回饋"
}
```

---

## 注意事項

1. 所有 API 端點都遵循 RESTful 設計原則
2. 支援分頁查詢，預設每頁 20 筆資料
3. 使用 Swagger/OpenAPI 文檔進行 API 測試
4. 所有日期時間格式使用 ISO 8601 標準
5. 資料驗證使用 Jakarta Validation（Bean Validation）
6. 異常處理統一使用 BusinessException
7. API 回應格式統一使用 ApiResponse<T>

---

## 版本資訊
- 版本: 1.0.0
- 最後更新: 2024-12-24
- Spring Boot: 3.4.1
- Java: 17
