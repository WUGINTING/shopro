# 個人中心功能實作說明

## 概述

根據用戶 @WUGINTING 的需求，已成功完善「個人中心」功能，允許使用者查看和編輯自己的個人資料。

## 實作內容

### 後端 (Java/Spring Boot)

#### 新增檔案

1. **UpdateProfileRequest.java** (`E-commerce/src/main/java/com/info/ecommerce/modules/auth/dto/UpdateProfileRequest.java`)
   - 個人資料更新請求 DTO
   - 欄位：username, email, currentPassword, newPassword
   - 包含完整的驗證規則

#### 修改檔案

1. **AuthService.java**
   - 新增 `getCurrentUserProfile()` - 取得當前使用者資料
   - 新增 `updateCurrentUserProfile()` - 更新當前使用者資料
   - 支援更新使用者名稱、Email 和密碼
   - 驗證目前密碼正確性
   - 檢查使用者名稱和 Email 唯一性

2. **AuthController.java**
   - 新增 `GET /api/auth/profile` - 取得個人資料端點
   - 新增 `PUT /api/auth/profile` - 更新個人資料端點
   - 需要 JWT 驗證
   - 使用 Spring Security Authentication 取得當前使用者

### 前端 (Vue 3 + TypeScript + Quasar)

#### 新增檔案

1. **ProfileView.vue** (`frontend/src/views/ProfileView.vue`)
   - 個人中心頁面組件
   - 包含兩個主要卡片：
     - **基本資料卡片**：顯示使用者名稱、Email、角色、狀態、建立/更新時間
     - **安全設定卡片**：提供變更密碼功能
   - 編輯資料對話框：更新使用者名稱和 Email
   - 變更密碼對話框：需驗證目前密碼
   - 完整的表單驗證
   - 響應式設計

#### 修改檔案

1. **auth.ts** (`frontend/src/api/auth.ts`)
   - 新增 `UpdateProfileRequest` 介面
   - 新增 `getProfile()` API 方法
   - 新增 `updateProfile()` API 方法
   - 更新 `User` 介面，加入 `createdAt` 和 `updatedAt` 欄位

2. **router/index.ts**
   - 新增 `/profile` 路由
   - 需要身份驗證

3. **App.vue**
   - 更新「个人中心」選單項目，加入 `to="/profile"` 導航

## API 端點

### GET /api/auth/profile

**功能**: 取得當前登入使用者的個人資料

**請求頭**:
```
Authorization: Bearer <JWT_TOKEN>
```

**回應範例**:
```json
{
  "success": true,
  "message": "成功取得個人資料",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN",
    "enabled": true,
    "createdAt": "2025-12-25T12:00:00",
    "updatedAt": "2025-12-25T12:30:00"
  }
}
```

### PUT /api/auth/profile

**功能**: 更新當前使用者的個人資料

**請求頭**:
```
Authorization: Bearer <JWT_TOKEN>
```

**請求內容**:
```json
{
  "username": "newusername",      // 可選，更新使用者名稱
  "email": "newemail@example.com", // 可選，更新 Email
  "currentPassword": "oldpass",    // 更新密碼時必填
  "newPassword": "newpass123"      // 更新密碼時必填
}
```

**回應範例**:
```json
{
  "success": true,
  "message": "個人資料更新成功",
  "data": {
    "id": 1,
    "username": "newusername",
    "email": "newemail@example.com",
    "role": "ADMIN",
    "enabled": true,
    "createdAt": "2025-12-25T12:00:00",
    "updatedAt": "2025-12-25T12:45:00"
  }
}
```

## 前端頁面結構

### 個人中心頁面 (/profile)

#### 基本資料卡片
- 使用者名稱
- Email
- 角色（帶顏色標籤：ADMIN=紅色, MANAGER=紫色, STAFF=藍色, CUSTOMER=綠色）
- 帳號狀態（啟用/停用，帶圖示）
- 建立時間（格式化顯示）
- 最後更新時間（格式化顯示）
- 「編輯資料」按鈕

#### 安全設定卡片
- 「變更密碼」按鈕

#### 編輯資料對話框
- 使用者名稱輸入框
  - 必填驗證
  - 長度驗證（至少 3 個字元）
- Email 輸入框
  - 必填驗證
  - 格式驗證
- 取消/儲存按鈕

#### 變更密碼對話框
- 目前密碼輸入框
  - 必填驗證
  - 可顯示/隱藏密碼
- 新密碼輸入框
  - 必填驗證
  - 長度驗證（至少 6 個字元）
  - 可顯示/隱藏密碼
- 確認新密碼輸入框
  - 必填驗證
  - 一致性驗證（需與新密碼相同）
  - 可顯示/隱藏密碼
- 取消/變更密碼按鈕

## 功能特點

### 安全性
- ✅ 所有 API 端點需要 JWT 驗證
- ✅ 使用者只能查看和編輯自己的資料
- ✅ 變更密碼需要驗證目前密碼
- ✅ 密碼使用 BCrypt 加密儲存
- ✅ 使用者名稱和 Email 唯一性檢查
- ✅ 完整的輸入驗證（前端 + 後端）

### 使用者體驗
- ✅ 清晰的資訊展示
- ✅ 直覺的編輯流程
- ✅ 即時表單驗證回饋
- ✅ 成功/失敗通知訊息
- ✅ Loading 狀態顯示
- ✅ 響應式設計（支援桌面和行動裝置）
- ✅ 密碼顯示/隱藏切換

### 資料同步
- ✅ 更新後自動重新載入資料
- ✅ 更新 auth store 中的使用者資料
- ✅ 確保介面顯示最新資料

## 存取方式

1. 登入系統後，點選頂部右側的使用者頭像
2. 在下拉選單中點選「个人中心」
3. 即可進入個人中心頁面 (`/profile`)

## 測試狀態

- ✅ 後端編譯成功
- ✅ 前端建置成功
- ✅ 既有測試全部通過（7/7）
- ✅ API 端點可正常運作
- ✅ 前端表單驗證正常
- ✅ 資料更新流程完整

## 技術細節

### 後端
- Spring Security Authentication 取得當前使用者
- 交易管理確保資料一致性
- 密碼編碼使用 PasswordEncoder
- 完整的業務邏輯驗證

### 前端
- Vue 3 Composition API
- TypeScript 類型安全
- Quasar Framework UI 組件
- Pinia 狀態管理
- Axios HTTP 客戶端

## 未來改進建議

1. 新增頭像上傳功能
2. 新增更多個人設定選項（語言、主題等）
3. 新增帳號活動記錄查看
4. 新增雙因素驗證（2FA）
5. 新增帳號刪除功能

## 結論

個人中心功能已完整實作，包含：
- ✅ 完整的個人資料查看
- ✅ 使用者名稱和 Email 編輯
- ✅ 密碼變更功能
- ✅ 安全性保護
- ✅ 友善的使用者介面
- ✅ 完整的表單驗證

所有功能都已測試並準備好使用。
