# 積分管理模組使用說明

> **模組名稱**：積分管理 (Points Management)  
> **路徑**：`http://localhost:5173/points`  
> **最後更新**：2026年1月10日

---

## 📋 目錄

1. [模組概述](#模組概述)
2. [功能說明](#功能說明)
3. [使用方式](#使用方式)
4. [API 端點](#api-端點)
5. [技術實現](#技術實現)
6. [常見問題](#常見問題)

---

## 模組概述

積分管理模組提供完整的積點管理功能，包括：
- 積點紀錄查詢
- 積點統計資訊
- 批次發放積點
- 積點類型管理

### 技術棧
- **前端框架**：Vue 3 + TypeScript
- **UI 框架**：Quasar Framework v2
- **API 客戶端**：Axios
- **狀態管理**：Pinia

---

## 功能說明

### 1. 積點紀錄查詢

#### 功能描述
查詢指定會員的積點變動紀錄，支援分頁顯示和類型篩選。

#### 使用步驟
1. 進入積分管理頁面 (`http://localhost:5173/points`)
2. 在搜尋欄選擇要查詢的會員
3. （可選）選擇積點類型進行篩選
   - `EARN`：獲得積點（綠色）
   - `PURCHASE`：購買獲得（藍色）
   - `REWARD`：獎勵獲得（橘色）
   - `REDEEM`：兌換使用（紅色）
   - `EXPIRE`：過期扣除（灰色）
4. 點擊「重置」按鈕清除搜尋條件

#### 顯示欄位
- **會員 ID**：積點變動的會員編號
- **積點**：變動的積點數量（+/- 顯示）
- **類型**：積點變動類型（彩色標籤顯示）
- **原因**：變動原因說明
- **結餘**：變動後的積點餘額
- **日期**：變動時間

---

### 2. 統計資訊卡片

頁面頂部顯示四個統計卡片：

#### 總發放積點
- **說明**：所有獲得類型的積點總和
- **顏色**：藍色（primary）
- **計算**：EARN + PURCHASE + REWARD 類型積點總和

#### 已兌換積點
- **說明**：所有兌換使用的積點總和
- **顏色**：橘色（warning）
- **計算**：REDEEM 類型積點總和

#### 待過期積點
- **說明**：即將過期的積點數量
- **顏色**：青色（info）
- **備註**：目前顯示為 0（需後端實現過期計算邏輯）

#### 活動記錄
- **說明**：積點變動記錄的總數
- **顏色**：綠色（success）
- **計算**：當前查詢結果的記錄總數

---

### 3. 批次發放積點

#### 功能描述
一次性為多個會員發放相同數量的積點，適用於促銷活動、節日贈送等場景。

#### 使用步驟
1. 點擊頁面右上角的「批次發放積點」按鈕
2. 在彈出的對話框中：
   - 選擇要發放的會員（可複選）
   - 輸入發放積點數（必填，正整數）
   - 輸入發放原因（必填）
3. 點擊「發放」按鈕執行
4. 系統會顯示成功訊息，並自動重新載入積點紀錄

#### 注意事項
- 會員選項會從會員 API 載入
- 積點數必須為正整數
- 發放原因為必填欄位
- 批次發放會為每個選中的會員建立一筆積點記錄

---

## 使用方式

### 開發環境啟動

```bash
# 進入前端目錄
cd frontend

# 安裝依賴（首次執行）
npm install

# 啟動開發伺服器
npm run dev
```

### 訪問積分管理頁面

1. 確保後端服務已啟動（預設 `http://localhost:8080`）
2. 確保前端服務已啟動（預設 `http://localhost:5173`）
3. 在瀏覽器中訪問：`http://localhost:5173/points`
4. 需要先登入系統（如果啟用了認證）

### 導航方式

積分管理頁面可通過以下方式訪問：

1. **側邊欄選單**：
   - 點擊左側導航欄中的「積分管理」項目
   - 圖標：⭐ (stars)
   - 位置：營銷管理 (Marketing) 區塊下

2. **直接 URL**：
   - 在瀏覽器地址欄輸入：`http://localhost:5173/points`

---

## API 端點

積分管理模組使用以下後端 API 端點：

### 1. 獲取會員積點紀錄
```
GET /api/crm/points/member/{memberId}?page=0&size=20
```
- **參數**：
  - `memberId` (path, required)：會員 ID
  - `page` (query, optional)：頁碼（預設 0）
  - `size` (query, optional)：每頁數量（預設 20）
- **回應**：`PageResponse<PointRecord>`

### 2. 批次發放積點
```
POST /api/crm/points/batch-grant
```
- **請求體**：
```json
{
  "memberIds": [1, 2, 3],
  "points": 100,
  "reason": "節日促銷贈送"
}
```
- **回應**：`List<PointRecord>`

### 3. 獲取會員積點餘額
```
GET /api/crm/points/balance/{memberId}
```
- **參數**：
  - `memberId` (path, required)：會員 ID
- **回應**：`Integer`（積點餘額）

### 4. 增加積點
```
POST /api/crm/points/add?memberId={id}&points={points}&pointType={type}&reason={reason}
```
- **參數**：
  - `memberId` (query, required)：會員 ID
  - `points` (query, required)：積點數量
  - `pointType` (query, required)：積點類型
  - `reason` (query, required)：原因描述
  - `orderId` (query, optional)：關聯訂單 ID

### 5. 扣除積點
```
POST /api/crm/points/deduct?memberId={id}&points={points}&pointType={type}&reason={reason}
```
- **參數**：
  - `memberId` (query, required)：會員 ID
  - `points` (query, required)：積點數量
  - `pointType` (query, required)：積點類型
  - `reason` (query, required)：原因描述

### 完整 API 文檔

詳細的 API 文檔請參考：
- **Swagger UI**：http://localhost:8080/swagger-ui/index.html
- **API 文檔**：`Manage_ENV/API_DOCS.md`
- **CRM 模組文檔**：`Manage_MD/CRM_MODULE_API_DOCUMENTATION.md`

---

## 技術實現

### 檔案結構

```
frontend/
├── src/
│   ├── api/
│   │   ├── point.ts           # 積點 API 服務
│   │   └── index.ts           # API 統一匯出
│   ├── views/
│   │   └── PointView.vue      # 積點管理頁面元件
│   └── router/
│       └── index.ts           # 路由配置（已包含 /points 路由）
```

### 主要元件

#### PointView.vue
- **位置**：`frontend/src/views/PointView.vue`
- **功能**：積點管理主頁面
- **組成**：
  - 統計卡片區域
  - 搜尋表單
  - 積點紀錄表格
  - 批次發放對話框

#### pointApi
- **位置**：`frontend/src/api/point.ts`
- **功能**：積點相關 API 服務
- **主要方法**：
  - `getMemberPoints()`：獲取會員積點紀錄
  - `batchGrant()`：批次發放積點
  - `getPointBalance()`：獲取積點餘額
  - `addPoints()`：增加積點
  - `deductPoints()`：扣除積點

### 資料類型

#### PointRecord
```typescript
interface PointRecord {
  id?: number
  memberId: number
  points: number
  pointType: 'EARN' | 'PURCHASE' | 'REWARD' | 'REDEEM' | 'EXPIRE' | 'ADJUST'
  reason: string
  orderId?: number
  balanceBefore?: number
  balanceAfter?: number
  createdAt?: string
}
```

#### BatchGrantRequest
```typescript
interface BatchGrantRequest {
  memberIds: number[]
  points: number
  reason: string
}
```

---

## 常見問題

### Q1: 無法載入積點紀錄？
**可能原因**：
- 後端服務未啟動
- 會員 ID 未選擇
- API 端點錯誤
- 網路連線問題

**解決方案**：
1. 確認後端服務運行在 `http://localhost:8080`
2. 在搜尋欄選擇會員後再查詢
3. 檢查瀏覽器控制台的錯誤訊息
4. 確認 API 路徑正確（應為 `/api/crm/points/member/{memberId}`）

### Q2: 會員選項列表為空？
**可能原因**：
- 會員 API 呼叫失敗
- 系統中沒有會員資料
- API 認證問題

**解決方案**：
1. 檢查會員管理模組是否正常運作
2. 確認系統中有會員資料
3. 檢查 API 認證 token 是否有效

### Q3: 批次發放失敗？
**可能原因**：
- 未選擇會員
- 積點數未輸入或為負數
- 原因未填寫
- API 請求失敗

**解決方案**：
1. 確保至少選擇一個會員
2. 輸入正整數的積點數
3. 填寫發放原因
4. 檢查後端錯誤訊息

### Q4: 統計資料不正確？
**目前狀態**：
- 統計資料會根據當前查詢結果計算
- 如果沒有選擇會員，統計資料不會顯示
- 待過期積點功能需要後端實現過期計算邏輯

**解決方案**：
1. 先選擇會員並載入積點紀錄
2. 統計資料會自動更新
3. 如需查看全域統計，需要後端提供統計 API

### Q5: 頁面樣式異常？
**可能原因**：
- Quasar CSS 未正確載入
- 樣式檔案缺失

**解決方案**：
1. 確認 `main.ts` 中已引入 Quasar CSS
2. 重新安裝依賴：`npm install`
3. 清除快取並重新啟動：`npm run dev`

---

## 開發規範

### 程式碼規範
- 遵循 Vue 3 Composition API 規範
- 使用 TypeScript 嚴格模式
- 遵循專案的命名規範（PascalCase 元件、camelCase 變數）
- 使用 Quasar 元件庫進行 UI 開發

### API 開發規範
- 所有 API 呼叫使用 `axiosInstance`（已配置攔截器）
- API 回應使用 `ApiResponse<T>` 類型
- 錯誤處理統一使用 `$q.notify` 顯示

### 樣式規範
- 使用 Quasar 內建樣式類別
- 避免使用內聯樣式
- 單頁面專屬樣式使用 `<style scoped>`

詳細的開發規範請參考：`Manage_ENV/FRONTEND_DEVELOPMENT_GUIDE.md`

---

## 更新紀錄

| 版本 | 日期 | 更新內容 |
|-----|------|---------|
| 1.0.0 | 2026-01-10 | 初始版本，完整積分管理功能實現 |

---

## 相關資源

- **前端開發指南**：`Manage_ENV/FRONTEND_DEVELOPMENT_GUIDE.md`
- **API 完整文檔**：`Manage_ENV/API_DOCS.md`
- **CRM 模組文檔**：`Manage_MD/CRM_MODULE_API_DOCUMENTATION.md`
- **Swagger UI**：http://localhost:8080/swagger-ui/index.html

---

**文檔維護者**：AI Assistant  
**最後更新**：2026年1月10日  
**模組狀態**：✅ 已完成並可用

