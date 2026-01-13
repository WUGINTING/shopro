# Google SSO 登入功能配置指南

## 問題說明

如果您遇到以下錯誤：
- `The fetch of the id assertion endpoint resulted in a network error: ERR_FAILED`
- `Server did not send the correct CORS headers.`
- `[GSI_LOGGER]: FedCM get() rejects with IdentityCredentialError: Error retrieving a token.`

這些錯誤通常是因為 Google OAuth 配置不正確導致的。

## 解決步驟

### 1. 在 Google Cloud Console 建立 OAuth 2.0 憑證

1. 前往 [Google Cloud Console](https://console.cloud.google.com/)
2. 選擇或建立一個專案
3. 啟用 **Google+ API** 或 **Google Identity Services API**
4. 前往「憑證」頁面：`APIs & Services` > `Credentials`
5. 點擊「建立憑證」>「OAuth 用戶端 ID」
6. 選擇應用程式類型：**網頁應用程式**
7. 設定以下內容：

#### 已授權的 JavaScript 來源
```
http://localhost:5173
http://localhost:3000
https://your-production-domain.com
```

#### 已授權的重新導向 URI
```
http://localhost:5173
http://localhost:5173/login
https://your-production-domain.com
https://your-production-domain.com/login
```

8. 點擊「建立」
9. 複製 **用戶端 ID**（Client ID）

### 2. 設定前端環境變數

在 `frontend` 目錄下建立或編輯 `.env` 檔案：

```env
VITE_GOOGLE_CLIENT_ID=你的-google-client-id-here
```

**重要**：請將 `你的-google-client-id-here` 替換為您在步驟 1 中獲得的實際 Client ID。

### 3. 重新啟動開發伺服器

```bash
cd frontend
npm run dev
```

### 4. 測試登入功能

1. 前往 `http://localhost:5173/login`
2. 點擊「使用 Google 登入」按鈕
3. 選擇您的 Google 帳號
4. 授權應用程式存取您的資訊

## 瀏覽器設定（如仍有問題）

### Chrome 瀏覽器

如果仍然遇到 CORS 錯誤，請檢查以下設定：

1. 在地址欄輸入：`chrome://settings/content/federatedIdentityApi`
2. 確保「第三方登入」選項已啟用

### 開發環境特殊設定

如果是在開發環境中使用 `localhost`，可能需要：

1. 在地址欄輸入：`chrome://flags/#fedcm-without-well-known-enforcement`
2. 將該標誌設為「Enabled」
3. 重新啟動瀏覽器

## 常見問題

### Q: 按鈕顯示為停用狀態
A: 檢查 `.env` 檔案中的 `VITE_GOOGLE_CLIENT_ID` 是否正確設定，並確保已重新啟動開發伺服器。

### Q: 點擊按鈕沒有反應
A: 檢查瀏覽器控制台是否有錯誤訊息，並確認 Google Identity Services 腳本已正確載入。

### Q: 授權後出現錯誤
A: 檢查後端 `/api/auth/google` 端點是否正常運作，並確認後端日誌中的錯誤訊息。

## 安全性注意事項

1. **不要將 Client ID 提交到版本控制系統**
   - 確保 `.env` 檔案已加入 `.gitignore`
   - 使用 `.env.example` 作為範本

2. **生產環境設定**
   - 在生產環境中使用 HTTPS
   - 更新 Google Cloud Console 中的授權來源為實際域名
   - 考慮使用環境變數管理服務（如 AWS Secrets Manager）

3. **後端驗證**
   - 後端應該驗證 Google ID Token 的簽名
   - 檢查 Token 的發行者和受眾
   - 驗證 Token 的過期時間

## 技術支援

如果問題持續存在，請檢查：
1. 瀏覽器控制台的完整錯誤訊息
2. 後端日誌中的錯誤訊息
3. Google Cloud Console 中的 API 使用情況

