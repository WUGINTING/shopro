# 台灣支付閘道整合 - 快速開始

## 功能概述

本專案整合了台灣兩大主流線上支付平台：

### 🟢 LINE PAY
- 台灣最受歡迎的行動支付平台
- 支援 APP 和 Web 支付
- 即時支付確認
- 支援主動查詢交易狀態

### 🟠 綠界科技 (ECPay)
- 台灣領先的第三方支付服務
- 支援多種支付方式：
  - 💳 信用卡
  - 🏦 ATM 轉帳
  - 🏪 超商代碼
  - 📱 超商條碼

## 快速設定

### 1. 申請支付閘道帳號

#### LINE PAY
1. 前往 [LINE PAY 商家中心](https://pay.line.me/center/merchant)
2. 註冊商家帳號
3. 取得以下資訊：
   - Channel ID
   - Channel Secret Key

#### ECPay (綠界)
1. 前往 [ECPay 商家後台](https://www.ecpay.com.tw/)
2. 註冊商家帳號
3. 取得以下資訊：
   - Merchant ID (商店代號)
   - Hash Key
   - Hash IV

### 2. 配置 application.properties

編輯 `/E-commerce/src/main/resources/application.properties`：

```properties
# LINE PAY 配置
payment.linepay.channel-id=YOUR_LINE_PAY_CHANNEL_ID
payment.linepay.channel-secret-key=YOUR_LINE_PAY_CHANNEL_SECRET_KEY
payment.linepay.api-url=https://sandbox-api-pay.line.me
payment.linepay.confirm-url=http://localhost:8080/api/payment-gateway/callback/linepay/confirm
payment.linepay.cancel-url=http://localhost:8080/api/payment-gateway/callback/linepay/cancel
payment.linepay.sandbox=true

# ECPay 配置
payment.ecpay.merchant-id=YOUR_ECPAY_MERCHANT_ID
payment.ecpay.hash-key=YOUR_ECPAY_HASH_KEY
payment.ecpay.hash-iv=YOUR_ECPAY_HASH_IV
payment.ecpay.api-url=https://payment-stage.ecpay.com.tw
payment.ecpay.return-url=http://localhost:8080/payment/result
payment.ecpay.notify-url=http://localhost:8080/api/payment-gateway/callback/ecpay
payment.ecpay.sandbox=true
```

### 3. 啟動應用程式

```bash
cd E-commerce
mvn spring-boot:run
```

### 4. 測試支付功能

開啟瀏覽器訪問測試頁面：
```
http://localhost:8080/payment-test.html
```

## API 使用範例

### 創建支付請求

#### LINE PAY
```bash
curl -X POST http://localhost:8080/api/payment-gateway/create?gateway=LINE_PAY \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD20241225001",
    "amount": 1000.00,
    "currency": "TWD",
    "productName": "測試商品",
    "customerName": "王小明",
    "customerEmail": "test@example.com",
    "customerPhone": "0912345678"
  }'
```

#### ECPay (綠界)
```bash
curl -X POST http://localhost:8080/api/payment-gateway/create?gateway=ECPAY \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD20241225001",
    "amount": 1000.00,
    "currency": "TWD",
    "productName": "測試商品",
    "customerName": "王小明",
    "customerEmail": "test@example.com",
    "customerPhone": "0912345678"
  }'
```

### 回應範例

```json
{
  "success": true,
  "message": "支付請求已建立",
  "data": {
    "gateway": "LINE_PAY",
    "status": "INITIATED",
    "transactionId": "2024122500001",
    "orderNumber": "ORD20241225001",
    "amount": 1000.00,
    "paymentUrl": "https://sandbox-web-pay.line.me/...",
    "webPaymentUrl": "https://sandbox-web-pay.line.me/...",
    "appPaymentUrl": "line://pay/..."
  }
}
```

## 支付流程

### LINE PAY 流程圖

```
1. 商家系統 → 創建支付請求 → 本系統
2. 本系統 → 生成支付 URL → 商家系統
3. 商家系統 → 重定向用戶 → LINE PAY 支付頁面
4. 用戶 → 完成支付 → LINE PAY
5. LINE PAY → 重定向 → 本系統確認頁面
6. 本系統 → 確認支付 → LINE PAY API
7. 本系統 → 更新訂單狀態 → 資料庫
8. 本系統 → 重定向用戶 → 商家訂單完成頁面
```

### ECPay 流程圖

```
1. 商家系統 → 創建支付請求 → 本系統
2. 本系統 → 生成支付 URL → 商家系統
3. 商家系統 → 表單 POST → ECPay 支付頁面
4. 用戶 → 選擇支付方式並完成支付 → ECPay
5. ECPay → 回調通知 → 本系統 (notifyUrl)
6. 本系統 → 驗證並更新訂單狀態 → 資料庫
7. ECPay → 重定向用戶 → 商家訂單完成頁面 (returnUrl)
```

## 測試資訊

### LINE PAY 測試
- 需要使用 LINE PAY 測試帳號
- 在 LINE PAY 開發者後台申請測試帳號

### ECPay 測試卡號
- **信用卡號**: `4311-9522-2222-2222`
- **有效期限**: 任何未來日期 (例如：12/25)
- **CVV**: `222`

## 環境切換

### 開發環境 (測試)
```properties
payment.linepay.api-url=https://sandbox-api-pay.line.me
payment.linepay.sandbox=true

payment.ecpay.api-url=https://payment-stage.ecpay.com.tw
payment.ecpay.sandbox=true
```

### 正式環境 (生產)
```properties
payment.linepay.api-url=https://api-pay.line.me
payment.linepay.sandbox=false

payment.ecpay.api-url=https://payment.ecpay.com.tw
payment.ecpay.sandbox=false
```

**⚠️ 重要**: 切換到正式環境時，請確保：
1. 使用正式環境的 API Key
2. Callback URL 使用 HTTPS
3. Callback URL 可從外部網路訪問

## 資料庫表結構

系統會自動建立以下資料表：

### payment_gateway_transactions
記錄所有支付閘道交易

| 欄位 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| order_id | BIGINT | 訂單 ID |
| order_number | VARCHAR(100) | 訂單編號 |
| gateway | VARCHAR(20) | 支付閘道類型 |
| transaction_id | VARCHAR(100) | 支付閘道交易 ID |
| status | VARCHAR(20) | 支付狀態 |
| amount | DECIMAL(10,2) | 支付金額 |
| currency | VARCHAR(10) | 幣別 |
| payment_url | VARCHAR(500) | 支付 URL |
| error_message | NVARCHAR(500) | 錯誤訊息 |
| raw_response | NVARCHAR(MAX) | 原始回應 |
| created_at | DATETIME | 建立時間 |
| updated_at | DATETIME | 更新時間 |

### order_payments (已更新)
新增支付閘道相關欄位

| 新增欄位 | 類型 | 說明 |
|---------|------|------|
| payment_gateway | VARCHAR(20) | 支付閘道類型 |
| gateway_transaction_id | VARCHAR(100) | 支付閘道交易 ID |

## API 文檔

啟動應用程式後，訪問 Swagger UI：
```
http://localhost:8080/swagger-ui.html
```

在 **支付閘道** 標籤下可以看到所有相關的 API 端點。

## 常見問題

### Q: 如何在正式環境中使用？
A: 
1. 申請正式環境的支付閘道帳號
2. 更新 `application.properties` 中的 API Key
3. 設定 `sandbox=false`
4. 確保 callback URL 使用 HTTPS 並可從外部訪問

### Q: 支付成功後如何更新訂單狀態？
A: 系統會在收到支付閘道的回調通知時自動更新訂單狀態。您需要在 `PaymentGatewayController` 中的回調處理方法中加入訂單狀態更新邏輯。

### Q: 如何處理支付失敗？
A: 支付失敗時，系統會返回包含錯誤訊息的 `PaymentResponseDTO`。建議記錄錯誤並提供友善的錯誤訊息給用戶。

### Q: 如何設定 webhook URL？
A: 
- LINE PAY: 在配置檔中設定 `confirm-url` 和 `cancel-url`
- ECPay: 在配置檔中設定 `return-url` 和 `notify-url`

確保這些 URL 在正式環境中可從外部訪問。

### Q: 支付金額有限制嗎？
A: 
- 測試環境通常有金額限制（如 1-30000 元）
- 正式環境限制依支付閘道商的規定
- 建議在創建支付前驗證金額範圍

## 技術支援

### LINE PAY
- 官方文檔: https://pay.line.me/tw/developers/apis/onlineApis
- 開發者後台: https://pay.line.me/center/merchant

### ECPay
- 官方文檔: https://www.ecpay.com.tw/Service/API_Dwnld
- 開發者後台: https://www.ecpay.com.tw/

## 後續改進建議

1. **增強安全性**
   - 實作 IP 白名單驗證
   - 加強 webhook 簽章驗證
   - 實作請求速率限制

2. **改善使用者體驗**
   - 添加支付進度追蹤
   - 實作支付超時自動取消
   - 提供友善的錯誤訊息

3. **系統功能擴充**
   - 實作退款功能
   - 添加對帳功能
   - 支援分期付款
   - 實作定期扣款

4. **監控與日誌**
   - 添加支付統計報表
   - 實作異常監控告警
   - 記錄完整的交易日誌

## 授權

本專案採用 MIT 授權條款。
