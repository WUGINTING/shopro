# Ngrok 測試環境設置指南

本指南說明如何使用 ngrok 來設置本地測試環境，以接收 ECPay 的支付回調（Webhook）。

## 📋 目錄

1. [什麼是 ngrok](#什麼是-ngrok)
2. [安裝 ngrok](#安裝-ngrok)
3. [啟動 ngrok 隧道](#啟動-ngrok-隧道)
4. [配置應用程式](#配置應用程式)
5. [測試回調](#測試回調)
6. [常見問題](#常見問題)

---

## 🔍 什麼是 ngrok

ngrok 是一個內網穿透工具，可以將本地服務器暴露到公網，讓外部服務（如 ECPay）能夠訪問您的本地 API。

**為什麼需要 ngrok？**

- ECPay 的支付回調（Webhook）需要從互聯網訪問您的服務器
- 本地開發環境（localhost）無法直接從互聯網訪問
- ngrok 創建一個公網 URL，將請求轉發到您的本地服務器

---

## 📥 安裝 ngrok

### Windows 安裝方法

#### 方法 1：使用 Chocolatey（推薦）

```powershell
# 如果已安裝 Chocolatey
choco install ngrok
```

#### 方法 2：手動下載安裝

1. 前往 ngrok 官網：https://ngrok.com/download
2. 下載 Windows 版本
3. 解壓縮到任意目錄（例如：`C:\ngrok`）
4. 將 ngrok.exe 所在目錄添加到系統 PATH 環境變量

#### 方法 3：使用 Scoop

```powershell
scoop install ngrok
```

### 驗證安裝

打開 PowerShell 或命令提示符，執行：

```powershell
ngrok version
```

如果顯示版本號，說明安裝成功。

---

## 🚀 啟動 ngrok 隧道

### 1. 確保後端服務正在運行

確保您的 Spring Boot 應用程式正在 `http://localhost:8080` 運行。

### 2. 註冊 ngrok 帳號（免費）

1. 前往 https://dashboard.ngrok.com/signup
2. 註冊一個免費帳號
3. 登入後，在 Dashboard 中複製您的 Authtoken

### 3. 配置 ngrok（首次使用）

```powershell
ngrok config add-authtoken YOUR_AUTHTOKEN
```

將 `YOUR_AUTHTOKEN` 替換為您在 Dashboard 中複製的 token。

### 4. 啟動 HTTP 隧道

在 PowerShell 或命令提示符中執行：

```powershell
ngrok http 8080
```

### 5. 獲取公網 URL

ngrok 啟動後，會顯示類似以下的輸出：

```
Session Status                online
Account                       Your Name (Plan: Free)
Version                       3.x.x
Region                        Asia Pacific (ap)
Latency                       -
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://abc123def456.ngrok-free.app -> http://localhost:8080

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

**重要資訊：**

- **Forwarding URL**：`https://abc123def456.ngrok-free.app`
  - 這是您的公網 URL（每次啟動 ngrok 可能會不同）
  - 使用免費版時，每次重新啟動 ngrok 都會獲得新的 URL
  - 付費版可以設置固定域名

---

## ⚙️ 配置應用程式

### 1. 更新 application.properties

編輯 `E-commerce/src/main/resources/application.properties`：

```properties
# Payment Gateway Configuration - ECPay (測試環境)
payment.ecpay.merchant-id=2000132
payment.ecpay.hash-key=5294y06JbISpM5x9
payment.ecpay.hash-iv=v77hoKGq4kWxNNIS
payment.ecpay.api-url=https://payment-stage.ecpay.com.tw

# 使用 ngrok 的公網 URL
# 將 YOUR_NGROK_URL 替換為您的實際 ngrok URL（例如：https://abc123def456.ngrok-free.app）
payment.ecpay.return-url=https://YOUR_NGROK_URL/payment/result
payment.ecpay.notify-url=https://YOUR_NGROK_URL/api/payment-gateway/callback/ecpay
payment.ecpay.sandbox=true
```

**範例：**

```properties
payment.ecpay.return-url=https://abc123def456.ngrok-free.app/payment/result
payment.ecpay.notify-url=https://abc123def456.ngrok-free.app/api/payment-gateway/callback/ecpay
```

### 2. 重啟應用程式

更新配置後，重啟 Spring Boot 應用程式以使配置生效。

---

## 🧪 測試回調

### 1. 啟動 ngrok

確保 ngrok 正在運行，並且指向正確的端口（8080）：

```powershell
ngrok http 8080
```

### 2. 創建測試訂單

1. 登入管理後台
2. 創建一個新訂單
3. 點擊「付款」按鈕
4. 完成支付流程

### 3. 查看回調日誌

#### 方式 1：查看 ngrok Web Interface

在瀏覽器中打開：`http://127.0.0.1:4040`

這是 ngrok 的 Web Interface，可以看到：
- 所有轉發的請求
- 請求和響應的詳細資訊
- 方便調試

#### 方式 2：查看後端日誌

在後端日誌中查找：

```
Received ECPay callback with params: {...}
ECPay payment successful for order: ORD...
```

### 4. 驗證回調是否成功

成功接收回調的標誌：

- ✅ 後端日誌顯示「Received ECPay callback」
- ✅ 訂單狀態自動更新為「已付款」
- ✅ ngrok Web Interface 顯示 POST 請求到 `/api/payment-gateway/callback/ecpay`
- ✅ 返回狀態碼 200，內容為 `1|OK`

---

## ❓ 常見問題

### Q1: ngrok URL 每次啟動都不同？

**A:** 免費版的 ngrok 每次啟動都會生成新的隨機 URL。解決方法：

1. **使用 ngrok Web Interface 查看當前 URL**：訪問 `http://127.0.0.1:4040`
2. **保持 ngrok 運行**：不要關閉 ngrok 終端窗口
3. **升級到付費版**：可以設置固定域名

### Q2: 如何讓 ngrok URL 保持不變？

**A:** 免費版無法固定 URL，但有幾種解決方案：

1. **使用 ngrok 的固定域名功能**（需要付費）
2. **手動更新配置**：每次啟動 ngrok 後，更新 `application.properties` 並重啟應用程式
3. **使用環境變量**：在啟動應用程式時通過環境變量設置 URL

### Q3: ngrok 連接失敗？

**A:** 檢查以下項目：

1. ✅ ngrok 是否正在運行
2. ✅ 後端服務是否在 `http://localhost:8080` 運行
3. ✅ ngrok 的 Authtoken 是否正確配置
4. ✅ 防火牆是否阻止了 ngrok

### Q4: 如何查看 ngrok 的請求記錄？

**A:** 使用 ngrok Web Interface：

1. 打開瀏覽器
2. 訪問 `http://127.0.0.1:4040`
3. 可以看到所有轉發的請求記錄
4. 點擊請求可以查看詳細資訊（請求頭、請求體、響應等）

### Q5: 回調沒有收到怎麼辦？

**A:** 排查步驟：

1. **檢查 ngrok 狀態**：確認 ngrok 正在運行且 URL 正確
2. **檢查配置**：確認 `notify-url` 是否正確設置
3. **查看 ngrok Web Interface**：確認是否有請求到達
4. **查看後端日誌**：確認應用程式是否收到請求
5. **測試回調端點**：手動發送測試請求到回調 URL

### Q6: 如何手動測試回調端點？

**A:** 使用 curl 或 Postman：

```bash
# 使用 curl（在 ngrok URL 正確的情況下）
curl -X POST "https://YOUR_NGROK_URL/api/payment-gateway/callback/ecpay" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "MerchantID=2000132&MerchantTradeNo=TEST123&RtnCode=1&..."
```

### Q7: ngrok 有限制嗎？

**A:** 免費版限制：

- ✅ 每次啟動 URL 會改變
- ✅ 連接數有限制
- ✅ 請求速率有限制
- ✅ 適合開發測試使用

付費版功能：
- ✅ 固定域名
- ✅ 更高連接數
- ✅ 更高請求速率
- ✅ 更多功能

---

## 🔄 工作流程

```
1. 啟動後端服務（localhost:8080）
   ↓
2. 啟動 ngrok（ngrok http 8080）
   ↓
3. 複製 ngrok 提供的公網 URL
   ↓
4. 更新 application.properties 中的回調 URL
   ↓
5. 重啟後端服務
   ↓
6. 創建訂單並進行支付
   ↓
7. ECPay 發送回調到 ngrok URL
   ↓
8. ngrok 轉發請求到 localhost:8080
   ↓
9. 後端接收並處理回調
   ↓
10. 訂單狀態自動更新
```

---

## 📝 快速參考

### 啟動 ngrok

```powershell
# 基本用法
ngrok http 8080

# 指定區域（可選）
ngrok http 8080 --region ap

# 顯示更多資訊
ngrok http 8080 --log stdout
```

### 查看當前 URL

1. 訪問 `http://127.0.0.1:4040`（Web Interface）
2. 或者在 ngrok 終端窗口中查看 Forwarding URL

### 更新配置的 URL

1. 從 ngrok 獲取公網 URL（例如：`https://abc123def456.ngrok-free.app`）
2. 更新 `application.properties`：
   ```properties
   payment.ecpay.notify-url=https://abc123def456.ngrok-free.app/api/payment-gateway/callback/ecpay
   payment.ecpay.return-url=https://abc123def456.ngrok-free.app/payment/result
   ```
3. 重啟應用程式

---

## ⚠️ 注意事項

1. **免費版 URL 會變動**：每次重新啟動 ngrok 都會獲得新的 URL，需要更新配置
2. **保持 ngrok 運行**：測試期間不要關閉 ngrok
3. **生產環境**：不要使用 ngrok，應使用實際的公網域名和 HTTPS
4. **安全性**：ngrok 的 URL 是公開的，任何人都可以訪問，僅用於開發測試

---

## 🔗 相關資源

- **ngrok 官網**：https://ngrok.com
- **ngrok 文檔**：https://ngrok.com/docs
- **ngrok Dashboard**：https://dashboard.ngrok.com
- **ECPay 文檔**：https://www.ecpay.com.tw/Service/API_Dwnld

---

**最後更新**：2026-01-12  
**版本**：1.0.0

