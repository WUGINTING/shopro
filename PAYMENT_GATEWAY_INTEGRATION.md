# 台灣支付閘道整合文檔

## 概述

本系統整合了台灣兩大主流線上支付平台：
- **LINE PAY** - 台灣最受歡迎的行動支付平台
- **綠界科技 (ECPay)** - 台灣領先的第三方支付服務提供商

## 支付閘道功能

### 支援的支付閘道

1. **LINE PAY**
   - 行動支付優先
   - 支援 App 和 Web 支付
   - 即時支付確認
   - 支援主動查詢交易狀態

2. **綠界 ECPay**
   - 支援多種支付方式（信用卡、ATM、超商代碼、超商條碼）
   - 廣泛的商店支援
   - 透過回調通知支付結果

## API 端點

### 1. 創建支付請求

**端點**: `POST /api/payment-gateway/create`

**參數**:
- `gateway`: 支付閘道類型 (LINE_PAY, ECPAY)
- `request`: 支付請求資料

**請求範例**:
```json
{
  "orderId": 123,
  "orderNumber": "ORD20241225001",
  "amount": 1000.00,
  "currency": "TWD",
  "productName": "商品購買",
  "customerName": "王小明",
  "customerEmail": "customer@example.com",
  "customerPhone": "0912345678"
}
```

**回應範例**:
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

### 2. 確認支付

**端點**: `POST /api/payment-gateway/confirm`

**參數**:
- `gateway`: 支付閘道類型
- `confirm`: 支付確認資料

**請求範例**:
```json
{
  "transactionId": "2024122500001",
  "orderNumber": "ORD20241225001",
  "confirmationCode": "abc123"
}
```

### 3. 查詢支付狀態

**端點**: `GET /api/payment-gateway/query/{gateway}/{transactionId}`

**回應範例**:
```json
{
  "success": true,
  "data": {
    "gateway": "LINE_PAY",
    "status": "SUCCESS",
    "transactionId": "2024122500001"
  }
}
```

### 4. 取消支付

**端點**: `POST /api/payment-gateway/cancel/{gateway}/{transactionId}`

### 5. ECPay 支付回調

**端點**: `POST /api/payment-gateway/callback/ecpay`

此端點用於接收 ECPay 的支付結果通知。

### 6. LINE PAY 確認回調

**端點**: `GET /api/payment-gateway/callback/linepay/confirm`

此端點用於處理 LINE PAY 支付完成後的重定向。

### 7. LINE PAY 取消回調

**端點**: `GET /api/payment-gateway/callback/linepay/cancel`

此端點用於處理 LINE PAY 支付取消後的重定向。

## 配置說明

### application.properties 配置

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

### 環境設定

#### 開發環境 (測試)
- LINE PAY: 使用 `sandbox-api-pay.line.me`
- ECPay: 使用 `payment-stage.ecpay.com.tw`
- 設定 `sandbox=true`

#### 正式環境
- LINE PAY: 使用 `api-pay.line.me`
- ECPay: 使用 `payment.ecpay.com.tw`
- 設定 `sandbox=false`

## 使用流程

### LINE PAY 支付流程

1. **創建支付請求**
   ```
   POST /api/payment-gateway/create?gateway=LINE_PAY
   ```
   
2. **用戶重定向到支付頁面**
   - 使用回應中的 `webPaymentUrl` (網頁) 或 `appPaymentUrl` (App)
   
3. **用戶完成支付**
   - LINE PAY 重定向回 `confirmUrl`
   
4. **系統確認支付**
   - 系統自動呼叫 LINE PAY API 確認支付
   - 更新訂單狀態

### ECPay 支付流程

1. **創建支付請求**
   ```
   POST /api/payment-gateway/create?gateway=ECPAY
   ```
   
2. **用戶重定向到支付頁面**
   - 使用回應中的 `paymentUrl`
   - 前端需要使用 HTML 表單 POST 提交
   
3. **用戶選擇支付方式並完成支付**
   - 信用卡
   - ATM 轉帳
   - 超商代碼
   - 超商條碼
   
4. **ECPay 回調通知**
   - ECPay 發送支付結果到 `notifyUrl`
   - 系統驗證 CheckMacValue
   - 更新訂單狀態
   
5. **用戶返回**
   - ECPay 重定向用戶到 `returnUrl`

## 安全性

### LINE PAY
- 使用 HMAC-SHA256 簽章驗證
- 每個請求需要唯一的 nonce
- Channel Secret Key 必須妥善保管

### ECPay
- 使用 SHA256 CheckMacValue 驗證
- 參數需要按照字母順序排序
- HashKey 和 HashIV 必須妥善保管
- 回調資料必須驗證 CheckMacValue

## 錯誤處理

### 常見錯誤碼

#### LINE PAY
- `1104`: 商店不存在
- `1105`: 交易不存在
- `1198`: API 呼叫超過限制
- `2101`: 參數錯誤

#### ECPay
- `RtnCode=1`: 交易成功
- `RtnCode=0`: 交易失敗
- 其他錯誤代碼請參考 ECPay 文檔

## 測試

### 測試卡號 (ECPay)
- 信用卡號: `4311-9522-2222-2222`
- 有效期限: 任何未來日期
- CVV: `222`

### LINE PAY 測試
需要使用 LINE PAY 測試帳號，請在 LINE PAY 開發者後台申請。

## 資料庫結構

### payment_gateway_transactions 表
記錄所有支付閘道交易:
- `id`: 主鍵
- `order_id`: 訂單 ID
- `order_number`: 訂單編號
- `gateway`: 支付閘道類型
- `transaction_id`: 支付閘道交易 ID
- `status`: 支付狀態
- `amount`: 支付金額
- `currency`: 幣別
- `payment_url`: 支付 URL
- `error_message`: 錯誤訊息
- `raw_response`: 原始回應
- `created_at`: 建立時間
- `updated_at`: 更新時間

### order_payments 表 (已更新)
新增支付閘道相關欄位:
- `payment_gateway`: 支付閘道類型
- `gateway_transaction_id`: 支付閘道交易 ID

## 整合注意事項

1. **URL 配置**
   - 確保 callback URL 可從外部訪問
   - 正式環境必須使用 HTTPS

2. **超時設定**
   - LINE PAY 支付有效期為 5 分鐘
   - ECPay 支付有效期可在建立時設定

3. **金額精度**
   - 台幣使用整數金額
   - 避免浮點數精度問題

4. **交易記錄**
   - 建議保存完整的請求和回應資料
   - 用於對帳和問題追蹤

5. **訂單狀態同步**
   - 支付成功後立即更新訂單狀態
   - 使用交易鎖避免重複處理

## 參考資源

### LINE PAY
- 官方文檔: https://pay.line.me/tw/developers/apis/onlineApis
- 開發者後台: https://pay.line.me/center/merchant

### ECPay
- 官方文檔: https://www.ecpay.com.tw/Service/API_Dwnld
- 開發者後台: https://www.ecpay.com.tw/

## 後續改進建議

1. **webhook 優化**
   - 實作 webhook 重試機制
   - 添加 webhook 簽章驗證中間件

2. **支付狀態同步**
   - 實作定時任務查詢未完成支付
   - 自動取消逾期未支付訂單

3. **支付方式管理**
   - 添加支付方式啟用/停用功能
   - 支援多商店配置

4. **報表功能**
   - 支付統計報表
   - 對帳功能
   - 退款管理

5. **通知功能**
   - 支付成功通知（Email/SMS）
   - 支付失敗提醒
