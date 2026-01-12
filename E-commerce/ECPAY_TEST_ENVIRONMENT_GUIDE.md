# ECPay 測試環境使用指南

本指南說明如何在當前系統中使用 ECPay 測試環境進行支付測試。

## 📋 目錄

1. [測試環境金鑰資訊](#測試環境金鑰資訊)
2. [如何申請測試帳號](#如何申請測試帳號)
3. [配置說明](#配置說明)
4. [使用測試訂單](#使用測試訂單)
5. [測試信用卡資訊](#測試信用卡資訊)
6. [支付流程說明](#支付流程說明)
7. [常見問題](#常見問題)

---

## 🔑 測試環境金鑰資訊

系統已預設配置 ECPay 測試環境，以下為測試環境的金鑰資訊：

### 測試環境憑證

```
Merchant ID (商店代號): 2000132
HashKey: 5294y06JbISpM5x9
HashIV: v77hoKGq4kWxNNIS
API URL: https://payment-stage.ecpay.com.tw
```

### 配置文件位置

配置文件位於：`E-commerce/src/main/resources/application.properties`

目前配置如下：

```properties
# Payment Gateway Configuration - ECPay (測試環境)
payment.ecpay.merchant-id=2000132
payment.ecpay.hash-key=5294y06JbISpM5x9
payment.ecpay.hash-iv=v77hoKGq4kWxNNIS
payment.ecpay.api-url=https://payment-stage.ecpay.com.tw
payment.ecpay.return-url=http://localhost:8080/payment/result
payment.ecpay.notify-url=http://localhost:8080/api/payment-gateway/callback/ecpay
payment.ecpay.sandbox=true
```

---

## 📝 如何申請測試帳號

### 步驟 1：註冊 ECPay 測試帳號

1. 前往 ECPay 測試環境網站：
   - 網址：https://www.ecpay.com.tw
   
2. 點選「免費註冊」或「商家註冊」
   
3. 填寫基本資料：
   - 公司/個人名稱
   - 聯絡人資訊
   - 聯絡電話
   - 電子郵件
   
4. 選擇「測試環境」註冊（如有選項）

### 步驟 2：登入商家後台

1. 前往 ECPay 商家後台：
   - 測試環境：https://vendor-stage.ecpay.com.tw（如有提供）
   - 或聯繫客服取得測試帳號

2. 使用註冊的帳號密碼登入

### 步驟 3：取得測試金鑰

登入後台後，在「系統開發管理」或「API 設定」頁面可以找到：

- **Merchant ID（商店代號）**：測試環境通常為 `2000132`
- **HashKey**：系統自動生成或由客服提供
- **HashIV**：系統自動生成或由客服提供

### 步驟 4：更新配置文件

如果取得自己的測試帳號金鑰，請更新 `application.properties`：

```properties
payment.ecpay.merchant-id=你的商店代號
payment.ecpay.hash-key=你的HashKey
payment.ecpay.hash-iv=你的HashIV
```

⚠️ **注意**：目前系統已使用官方提供的測試環境金鑰，通常可以直接使用，無需申請。

---

## ⚙️ 配置說明

### 環境配置

系統已預設為測試環境（`sandbox=true`），這表示：

- ✅ 使用測試 API 端點
- ✅ 不會產生真實交易
- ✅ 可以使用測試信用卡進行支付
- ✅ 訂單不會產生實際扣款

### 回調 URL 配置

```properties
# 付款完成返回 URL（用戶支付完成後跳轉的頁面）
payment.ecpay.return-url=http://localhost:8080/payment/result

# 付款結果通知 URL（ECPay 服務器通知的端點）
payment.ecpay.notify-url=http://localhost:8080/api/payment-gateway/callback/ecpay
```

⚠️ **注意**：在本地測試時，`localhost` 的回調 URL 可能無法被 ECPay 服務器訪問。建議：

1. **本地開發**：可使用內網穿透工具（如 ngrok）來提供公網可訪問的 URL
2. **正式環境**：需使用 HTTPS 的域名 URL

---

## 🛒 使用測試訂單

### 在系統中創建測試訂單

1. **登入管理後台**

2. **進入訂單管理頁面**
   - 路徑：`訂單管理` → `訂單列表`

3. **創建新訂單**
   - 點擊「新增訂單」按鈕
   - 填寫訂單資訊：
     - 選擇或輸入客戶資訊
     - 添加商品項目
     - 設定金額
     - 訂單狀態選擇「待付款」
   - 點擊「儲存」

4. **發起支付**
   - 在訂單列表中找到狀態為「待付款」的訂單
   - 點擊該訂單的「付款」按鈕（💰 圖標）
   - 確認訂單資訊和金額
   - 選擇支付方式（預設為「綠界 ECPay」）
   - 點擊「前往付款」

5. **完成支付**
   - 系統會跳轉到 ECPay 測試環境支付頁面
   - 使用測試信用卡資訊完成支付
   - 支付完成後會自動返回系統

### 訂單狀態流程

```
待付款 (PENDING_PAYMENT)
  ↓
[點擊付款按鈕]
  ↓
[跳轉 ECPay 支付頁面]
  ↓
[完成支付]
  ↓
已付款 (PAID) → 處理中 (PROCESSING) → 已完成 (COMPLETED)
```

---

## 💳 測試信用卡資訊

ECPay 測試環境提供以下測試信用卡資訊：

### 測試信用卡

```
卡號：4311-9522-2222-2222
有效期限：任何未來日期（例如：12/25）
安全碼：222
持卡人姓名：任意填寫（例如：測試用戶）
```

### 使用說明

1. 在 ECPay 支付頁面選擇「信用卡付款」
2. 輸入上述測試信用卡資訊
3. 有效期限可以是任何未來的日期（例如：2025年12月）
4. 安全碼固定為 `222`
5. 點擊「確認付款」

⚠️ **重要**：這些是測試環境專用的測試卡號，**不會產生真實扣款**。

---

## 🔄 支付流程說明

### 完整支付流程

```
1. 用戶操作
   └─> 在訂單列表點擊「付款」按鈕
   
2. 前端處理
   └─> 顯示支付對話框
   └─> 選擇支付方式（ECPay）
   └─> 調用支付 API
   
3. 後端處理
   └─> 創建支付請求
   └─> 生成 ECPay 支付參數
   └─> 計算 CheckMacValue（安全驗證碼）
   └─> 返回支付 URL
   
4. 跳轉支付
   └─> 前端跳轉到 ECPay 支付頁面
   └─> 用戶選擇支付方式並完成支付
   
5. 支付回調
   └─> ECPay 服務器發送支付結果通知
   └─> 後端驗證 CheckMacValue
   └─> 更新訂單狀態為「已付款」
   └─> 記錄支付交易
   
6. 用戶返回
   └─> 支付完成後跳轉回系統
   └─> 顯示支付結果
```

### 支付狀態說明

- **INITIATED**：支付請求已建立，等待用戶完成支付
- **PROCESSING**：支付處理中
- **SUCCESS**：支付成功
- **FAILED**：支付失敗
- **CANCELLED**：支付已取消

---

## ❓ 常見問題

### Q1: 為什麼點擊付款按鈕沒有反應？

**A:** 請檢查：
1. 訂單狀態是否為「待付款」
2. 瀏覽器控制台是否有錯誤訊息
3. 後端服務是否正常運行
4. 網絡連接是否正常

### Q2: 支付完成後訂單狀態沒有更新？

**A:** 可能的原因：
1. 回調 URL 無法被 ECPay 訪問（localhost 問題）
2. 檢查後端日誌查看回調是否成功接收
3. 確認 CheckMacValue 驗證是否通過
4. 查看訂單詳情確認是否有支付記錄

### Q3: 如何測試本地回調功能？

**A:** 建議使用內網穿透工具：

1. **使用 ngrok**（推薦）：
   ```bash
   ngrok http 8080
   ```
   取得公網 URL 後，更新配置：
   ```properties
   payment.ecpay.notify-url=https://你的ngrok地址/api/payment-gateway/callback/ecpay
   payment.ecpay.return-url=https://你的ngrok地址/payment/result
   ```

2. **使用其他工具**：
   - localtunnel
   - serveo
   - cloudflare tunnel

### Q4: 測試信用卡一直支付失敗？

**A:** 請確認：
1. 使用正確的測試卡號：`4311-9522-2222-2222`
2. 有效期限為未來日期
3. 安全碼為 `222`
4. 確認是測試環境而非正式環境

### Q5: 如何查看支付交易記錄？

**A:** 
1. 前往「支付管理」→「交易記錄」
2. 可以根據訂單編號、交易 ID 等條件查詢
3. 查看支付詳情了解支付狀態和錯誤訊息

### Q6: 如何切換到正式環境？

**A:** 需要：
1. 申請 ECPay 正式環境帳號
2. 取得正式環境的 Merchant ID、HashKey、HashIV
3. 更新配置文件：
   ```properties
   payment.ecpay.merchant-id=正式商店代號
   payment.ecpay.hash-key=正式HashKey
   payment.ecpay.hash-iv=正式HashIV
   payment.ecpay.api-url=https://payment.ecpay.com.tw
   payment.ecpay.sandbox=false
   ```
4. 更新回調 URL 為正式域名（需 HTTPS）

---

## 📞 相關資源

- **ECPay 官方網站**：https://www.ecpay.com.tw
- **ECPay API 文檔**：https://www.ecpay.com.tw/Service/API_Dwnld
- **ECPay 開發者中心**：https://developers.ecpay.com.tw
- **測試環境**：https://payment-stage.ecpay.com.tw

---

## ⚠️ 重要提醒

1. **測試環境不會產生真實交易**，所有支付都是模擬的
2. **測試金鑰僅用於開發測試**，請勿在正式環境使用
3. **回調 URL 必須使用 HTTPS**（正式環境）
4. **生產環境請務必申請正式帳號**並使用正式金鑰
5. **保護好您的金鑰資訊**，不要提交到公開的版本控制系統

---

**最後更新**：2026-01-12  
**版本**：1.0.0

