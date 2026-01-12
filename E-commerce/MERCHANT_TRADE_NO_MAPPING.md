# MerchantTradeNo 與 order_number 對應方式說明

## 問題描述

ECPay 回調中的 `MerchantTradeNo` 與數據庫中的 `order_number` 不一致。

**示例**：
- `MerchantTradeNo` = `ORD20260112132537255` (21個字符)
- `order_number` = `ORD2026011213253561` (18個字符)

## 對應邏輯說明

### 1. 生成 MerchantTradeNo（發送支付請求時）

**位置**：`EcPayService.buildEcPayRequest()`

**邏輯**：
1. 獲取原始訂單編號（例如：`ORD2026011213253561`，18個字符）
2. 獲取當前時間戳（毫秒），取後4位（例如：`7255`）
3. 組合：`原始訂單編號 + 4位後綴` = `ORD20260112132535617255` (22個字符)
4. **如果超過20個字符**，則截取：
   - 計算：`maxOriginalLength = 20 - 4 = 16`
   - 截取原始訂單編號前16位：`ORD20260112132537`
   - 加上4位後綴：`ORD20260112132537 + 7255 = ORD20260112132537255` (20個字符)

**代碼位置**：
```java
String originalOrderNumber = request.getOrderNumber(); // ORD2026011213253561
String timestamp = String.valueOf(System.currentTimeMillis());
String uniqueSuffix = timestamp.substring(Math.max(0, timestamp.length() - 4)); // 7255
String merchantTradeNo = originalOrderNumber + uniqueSuffix; // ORD20260112132535617255

if (merchantTradeNo.length() > 20) {
    int maxOriginalLength = 20 - uniqueSuffix.length(); // 16
    merchantTradeNo = originalOrderNumber.substring(0, 16) + uniqueSuffix; // ORD20260112132537 + 7255
}
```

### 2. 提取原始訂單編號（回調處理時）

**位置**：`EcPayService.parseCallback()`

**邏輯**：
1. 獲取 `MerchantTradeNo`（例如：`ORD20260112132537255`）
2. 移除最後4位：`ORD20260112132537255` → `ORD20260112132537` (17個字符)

**問題**：如果原始訂單編號超過16個字符，會被截取，導致無法完全還原。

**代碼位置**：
```java
String originalOrderNumber = merchantTradeNo;
if (merchantTradeNo != null && merchantTradeNo.length() > 4) {
    originalOrderNumber = merchantTradeNo.substring(0, merchantTradeNo.length() - 4);
}
```

## 問題分析

### 您的實際情況

1. **數據庫中的訂單編號**：`ORD2026011213253561` (18個字符)
2. **回調中的 MerchantTradeNo**：`ORD20260112132537255` (21個字符，超過20個！)
3. **提取的訂單編號**：`ORD20260112132537` (移除最後4位 `7255`，得到17個字符)

**分析**：
- 如果原始訂單編號是18個字符，加上4位後綴應該是22個字符
- 如果超過20個字符，會被截取為20個字符
- 但您提供的 MerchantTradeNo 是21個字符，這不符合邏輯

**可能的原因**：
1. ECPay 的 MerchantTradeNo 長度限制實際不是20個字符？
2. 或者訂單編號格式不是固定的18個字符？
3. 或者有其他邏輯處理？

## 解決方案

### 方案 1：使用訂單編號前綴匹配（推薦）

由於無法完全還原被截取的訂單編號，建議使用**前綴匹配**來查找訂單：

```java
// 在 PaymentCallbackService.handlePaymentSuccess() 中
String extractedOrderNumber = response.getOrderNumber(); // ORD20260112132537

// 使用前綴匹配查找訂單
Optional<Order> orderOpt = orderRepository.findByOrderNumberStartingWith(extractedOrderNumber);
```

### 方案 2：記錄 MerchantTradeNo 映射關係

在發送支付請求時，記錄 `MerchantTradeNo` 與 `order_number` 的映射關係：

```java
// 在發送支付請求時保存映射
paymentGatewayTransactionRepository.save(
    PaymentGatewayTransaction.builder()
        .orderNumber(originalOrderNumber)
        .gatewayTransactionId(merchantTradeNo) // 使用 MerchantTradeNo 作為交易ID
        .build()
);

// 在回調時根據 MerchantTradeNo 查找
OrderPayment payment = orderPaymentRepository
    .findByGatewayTransactionId(merchantTradeNo);
```

### 方案 3：縮短訂單編號長度

修改訂單編號生成邏輯，確保加上4位後綴後不超過20個字符：

```java
// 當前：ORD + 12位時間戳 + 4位隨機數 = 19個字符
// 加上4位後綴 = 23個字符（超過20，會被截取）

// 建議：ORD + 10位時間戳 + 4位隨機數 = 17個字符
// 加上4位後綴 = 21個字符（仍然超過20）

// 或者：ORD + 12位時間戳 + 2位隨機數 = 17個字符
// 加上4位後綴 = 21個字符（仍然超過20）

// 最佳：使用更短的格式，如：ORD + 10位時間戳 + 4位隨機數 = 17個字符
// 但這樣仍然會超過20個字符（21個字符）
```

## 建議的修復方案

由於訂單編號格式已定，建議使用**方案 2**（記錄映射關係），這是最可靠的方式。

### 實施步驟

1. **發送支付請求時記錄映射**：
   - 在 `EcPayService.buildPaymentUrl()` 或相關方法中
   - 將 `MerchantTradeNo` 和 `order_number` 的映射保存到數據庫

2. **回調時使用映射查找**：
   - 在 `EcPayService.parseCallback()` 中
   - 根據 `MerchantTradeNo` 查找對應的 `order_number`

這樣可以確保即使訂單編號被截取，也能正確找到對應的訂單。

