# ECPay 回調問題排查指南

如果回調沒有成功處理，請按照以下步驟排查：

## 🔍 排查步驟

### 1. 檢查回調是否到達服務器

**查看後端日誌：**

查找以下日誌訊息：
```
Received ECPay callback with params: {...}
Parsing ECPay callback params: {...}
```

**如果沒有這些日誌：**
- ✅ 回調沒有到達服務器
- ✅ 檢查 ngrok 是否正在運行
- ✅ 檢查 ngrok URL 是否正確配置
- ✅ 檢查 application.properties 中的 notify-url 是否正確

**使用 ngrok Web Interface 查看：**
- 訪問：http://127.0.0.1:4040
- 查看是否有 POST 請求到 `/api/payment-gateway/callback/ecpay`
- 查看請求詳情（請求頭、請求體、響應）

---

### 2. 檢查 CheckMacValue 驗證

**查看後端日誌：**

查找以下日誌訊息：
```
CheckMacValue verification result: true/false
```

**如果驗證失敗：**
- ✅ CheckMacValue 計算邏輯可能有問題
- ✅ 查看日誌中的錯誤訊息
- ✅ 檢查 HashKey 和 HashIV 是否正確

**如果驗證成功但仍有問題：**
- ✅ 繼續檢查下一步

---

### 3. 檢查訂單編號提取

**查看後端日誌：**

查找以下日誌訊息：
```
Extracted original order number: ORD... from MerchantTradeNo: ORD..._...
```

**如果訂單編號提取失敗：**
- ✅ 查看日誌中的 MerchantTradeNo 值
- ✅ 確認訂單編號格式是否正確

---

### 4. 檢查訂單查找

**查看後端日誌：**

查找以下日誌訊息：
```
Handling payment success for order: ORD..., transaction: ...
Order found: ORD..., current status: ...
```

**如果訂單未找到：**
- ✅ 查看日誌中的 "Order not found for payment success"
- ✅ 查看日誌中的 "Sample order numbers in database"
- ✅ 確認訂單編號是否匹配
- ✅ 確認訂單是否已創建

**如果訂單狀態不正確：**
- ✅ 查看日誌中的 "Order ... is not in PENDING_PAYMENT or PROCESSING status"
- ✅ 確認訂單狀態是否為 PENDING_PAYMENT 或 PROCESSING

---

### 5. 檢查訂單狀態更新

**查看後端日誌：**

查找以下日誌訊息：
```
Payment successfully processed for order: ORD..., transaction: ...
ECPay payment successful for order: ORD...
```

**如果沒有這些日誌：**
- ✅ 查看是否有錯誤日誌
- ✅ 查看訂單狀態是否已更新

---

## 📋 完整日誌範例

成功的回調處理應該看到以下日誌：

```
INFO  - Received ECPay callback with params: {MerchantTradeNo=ORD2026011210134575_123456, TradeNo=..., RtnCode=1, ...}
INFO  - Parsing ECPay callback params: {...}
INFO  - CheckMacValue verification result: true
INFO  - ECPay callback parsed - MerchantTradeNo: ORD2026011210134575_123456, TradeNo: ..., RtnCode: 1, ...
INFO  - Extracted original order number: ORD2026011210134575 from MerchantTradeNo: ORD2026011210134575_123456
INFO  - Handling payment success for order: ORD2026011210134575, transaction: ...
INFO  - Order found: ORD2026011210134575, current status: PENDING_PAYMENT
INFO  - Payment successfully processed for order: ORD2026011210134575, transaction: ...
INFO  - ECPay payment successful for order: ORD2026011210134575
```

---

## ❓ 常見問題

### Q1: 回調沒有到達服務器

**可能原因：**
- ngrok 沒有運行
- ngrok URL 配置錯誤
- 防火牆阻止了請求
- ECPay 測試環境問題

**解決方法：**
1. 確認 ngrok 正在運行
2. 確認 ngrok URL 是否正確（訪問 http://127.0.0.1:4040 查看）
3. 確認 application.properties 中的 notify-url 是否正確
4. 嘗試手動發送測試請求到回調端點

---

### Q2: CheckMacValue 驗證失敗

**可能原因：**
- HashKey 或 HashIV 配置錯誤
- CheckMacValue 計算邏輯錯誤
- 參數編碼問題

**解決方法：**
1. 確認 HashKey 和 HashIV 是否正確
2. 查看日誌中的 CheckMacValue 計算過程
3. 對比 ECPay 文檔中的計算方式

---

### Q3: 訂單未找到

**可能原因：**
- 訂單編號提取錯誤
- 訂單尚未創建
- 訂單編號不匹配

**解決方法：**
1. 查看日誌中的訂單編號提取過程
2. 確認訂單是否已創建
3. 確認訂單編號是否正確

---

### Q4: 訂單狀態更新失敗

**可能原因：**
- 訂單狀態不正確（不是 PENDING_PAYMENT）
- 數據庫連接問題
- 事務回滾

**解決方法：**
1. 查看日誌中的訂單狀態
2. 確認訂單狀態是否為 PENDING_PAYMENT
3. 查看是否有數據庫錯誤

---

## 🔧 調試技巧

### 1. 使用 ngrok Web Interface

訪問 http://127.0.0.1:4040 查看：
- 所有轉發的請求
- 請求詳情（請求頭、請求體）
- 響應詳情
- 錯誤訊息

### 2. 查看後端日誌

查看 Spring Boot 應用程式的日誌，特別關注：
- `Received ECPay callback` - 回調是否到達
- `CheckMacValue verification` - 驗證是否通過
- `Order found/not found` - 訂單是否找到
- `Payment successfully processed` - 處理是否成功

### 3. 手動測試回調端點

使用 curl 或 Postman 手動發送測試請求：

```bash
curl -X POST "https://YOUR_NGROK_URL/api/payment-gateway/callback/ecpay" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "MerchantID=2000132&MerchantTradeNo=ORD2026011210134575_123456&..."
```

---

## 📝 檢查清單

請確認以下項目：

- [ ] ngrok 正在運行
- [ ] ngrok URL 正確配置在 application.properties
- [ ] 後端服務正在運行（端口 8080）
- [ ] 後端日誌可以看到回調請求
- [ ] CheckMacValue 驗證通過
- [ ] 訂單編號正確提取
- [ ] 訂單已創建且在數據庫中
- [ ] 訂單狀態為 PENDING_PAYMENT 或 PROCESSING
- [ ] 訂單狀態成功更新為 PAID

---

**需要更多幫助？** 請查看後端日誌並提供相關錯誤訊息。

