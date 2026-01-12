# Ngrok 快速開始指南

## 🚀 快速設置步驟

### 1. 下載並安裝 ngrok

前往 https://ngrok.com/download 下載 Windows 版本，解壓縮到任意目錄。

### 2. 註冊並獲取 Authtoken

1. 前往 https://dashboard.ngrok.com/signup 註冊（免費）
2. 登入後，在 Dashboard 中複製您的 Authtoken

### 3. 配置 ngrok（首次使用）

打開 PowerShell 或命令提示符（需要管理員權限），執行：

```powershell
ngrok config add-authtoken YOUR_AUTHTOKEN
```

將 `YOUR_AUTHTOKEN` 替換為您從 Dashboard 複製的 token。

### 4. 啟動 ngrok 隧道

在 PowerShell 或命令提示符中執行：

```powershell
ngrok http 8080
```

**輸出範例：**

```
Session Status                online
Account                       Your Name (Plan: Free)
Version                       3.x.x
Region                        Asia Pacific (ap)
Forwarding                    https://abc123def456.ngrok-free.app -> http://localhost:8080

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

**重要：** 複製 `Forwarding` 中的 URL（例如：`https://abc123def456.ngrok-free.app`）

### 5. 更新 application.properties

編輯 `E-commerce/src/main/resources/application.properties`，更新回調 URL：

```properties
# 將 YOUR_NGROK_URL 替換為您的實際 ngrok URL
# 例如：https://abc123def456.ngrok-free.app
payment.ecpay.return-url=https://YOUR_NGROK_URL/payment/result
payment.ecpay.notify-url=https://YOUR_NGROK_URL/api/payment-gateway/callback/ecpay
```

**完整範例：**

```properties
# Payment Gateway Configuration - ECPay (測試環境)
payment.ecpay.merchant-id=2000132
payment.ecpay.hash-key=5294y06JbISpM5x9
payment.ecpay.hash-iv=v77hoKGq4kWxNNIS
payment.ecpay.api-url=https://payment-stage.ecpay.com.tw
payment.ecpay.return-url=https://abc123def456.ngrok-free.app/payment/result
payment.ecpay.notify-url=https://abc123def456.ngrok-free.app/api/payment-gateway/callback/ecpay
payment.ecpay.sandbox=true
```

### 6. 重啟應用程式

更新配置後，重啟 Spring Boot 應用程式。

### 7. 測試回調

1. 創建一個測試訂單
2. 完成支付
3. 查看 ngrok Web Interface：http://127.0.0.1:4040
4. 查看後端日誌確認是否收到回調

---

## 📊 查看回調記錄

### Ngrok Web Interface

在瀏覽器中打開：`http://127.0.0.1:4040`

可以查看：
- ✅ 所有轉發的請求
- ✅ 請求詳情（請求頭、請求體）
- ✅ 響應詳情
- ✅ 方便調試

### 後端日誌

查看後端日誌中的回調訊息：

```
Received ECPay callback with params: {...}
ECPay payment successful for order: ORD...
```

---

## ⚠️ 重要提醒

1. **免費版 URL 會變動**：每次重新啟動 ngrok 都會獲得新的 URL，需要更新配置
2. **保持 ngrok 運行**：測試期間不要關閉 ngrok 終端窗口
3. **同時運行**：確保後端服務（8080 端口）和 ngrok 都在運行

---

## 🔄 完整工作流程

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

## 📝 常用命令

```powershell
# 啟動 ngrok（基本）
ngrok http 8080

# 啟動 ngrok（顯示詳細日誌）
ngrok http 8080 --log stdout

# 配置 Authtoken
ngrok config add-authtoken YOUR_AUTHTOKEN

# 查看 ngrok 配置
ngrok config check
```

---

**需要更多資訊？** 請查看 `NGROK_SETUP_GUIDE.md` 獲取詳細說明。

