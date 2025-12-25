# 支付閘道整合 - 實作注意事項

## 重要提醒

### ⚠️ 待完成事項 (Production Checklist)

在部署到生產環境之前，請務必完成以下事項：

#### 1. 訂單狀態同步 (Critical)
目前 `PaymentGatewayController` 中的支付回調處理包含 TODO 標記：

**位置**: 
- `ecpayCallback()` 方法 (line 126-127)
- `linepayConfirmCallback()` 方法 (line 159)
- `linepayCancelCallback()` 方法 (line 174)

**需要實作**:
```java
// 範例實作
@Transactional
private void updateOrderPaymentStatus(String orderNumber, PaymentResponseDTO response) {
    // 1. 根據 orderNumber 找到訂單
    Order order = orderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    
    // 2. 創建或更新 OrderPayment 記錄
    OrderPayment payment = OrderPayment.builder()
        .orderId(order.getId())
        .paymentStatus(mapToPaymentStatus(response.getStatus()))
        .paymentMethod(response.getGateway().getDisplayName())
        .paymentAmount(response.getAmount())
        .transactionId(response.getTransactionId())
        .paymentGateway(response.getGateway().name())
        .gatewayTransactionId(response.getTransactionId())
        .paymentTime(LocalDateTime.now())
        .build();
    
    orderPaymentRepository.save(payment);
    
    // 3. 更新訂單狀態
    if (response.getStatus() == PaymentGatewayStatus.SUCCESS) {
        order.setStatus(OrderStatus.PAID);
    }
    orderRepository.save(order);
    
    // 4. 記錄歷史
    orderHistoryService.recordHistory(order.getId(), "PAYMENT_COMPLETED", 
        "支付完成: " + response.getGateway().getDisplayName(), 
        null, null, null, null);
}
```

#### 2. 敏感資訊管理

**目前狀況**: 
配置檔案中包含佔位符值 (`YOUR_LINE_PAY_CHANNEL_ID` 等)

**建議做法**:
```properties
# 使用環境變數
payment.linepay.channel-id=${LINE_PAY_CHANNEL_ID}
payment.linepay.channel-secret-key=${LINE_PAY_CHANNEL_SECRET_KEY}

payment.ecpay.merchant-id=${ECPAY_MERCHANT_ID}
payment.ecpay.hash-key=${ECPAY_HASH_KEY}
payment.ecpay.hash-iv=${ECPAY_HASH_IV}
```

或使用 Spring Cloud Config、AWS Secrets Manager、Azure Key Vault 等服務。

#### 3. LINE PAY 確認金額

**已修正**: 
`LinePayService.confirmPayment()` 現在會：
1. 優先使用 `PaymentConfirmDTO.amount` (如果有提供)
2. 否則先查詢交易取得金額
3. 如果都無法取得則拋出異常

**實際應用建議**:
在 LINE PAY 回調處理時，應該：
```java
// 從資料庫查詢原始訂單金額
Order order = orderService.getByOrderNumber(orderId);
PaymentConfirmDTO confirm = PaymentConfirmDTO.builder()
    .transactionId(transactionId)
    .orderNumber(orderId)
    .amount(order.getTotalAmount()) // 使用訂單金額
    .build();
```

#### 4. ECPay 表單提交

**目前實作**: 
`payment-test.html` 中的 ECPay 重定向僅顯示提示訊息

**正式實作建議**:
```javascript
function redirectToECPay(paymentData) {
    // 創建隱藏表單
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = 'https://payment.ecpay.com.tw/Cashier/AioCheckOut/V5';
    
    // 從後端取得的參數
    Object.keys(paymentData.params).forEach(key => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = paymentData.params[key];
        form.appendChild(input);
    });
    
    document.body.appendChild(form);
    form.submit();
}
```

或者後端直接返回 HTML 表單讓前端渲染。

## 安全性檢查清單

### ✅ 已實作
- [x] LINE PAY HMAC-SHA256 簽章驗證
- [x] ECPay CheckMacValue 驗證
- [x] HTTPS 連線 (透過支付閘道 API)
- [x] 參數排序和編碼處理

### ⚠️ 建議加強
- [ ] IP 白名單驗證（限制只接受支付閘道的回調 IP）
- [ ] 請求速率限制（防止 DDoS）
- [ ] 回調請求的重複檢查（防止重複處理）
- [ ] 詳細的審計日誌
- [ ] 支付金額範圍驗證

**範例 - IP 白名單**:
```java
@Component
public class PaymentCallbackFilter implements Filter {
    private static final Set<String> LINEPAY_IPS = Set.of(
        "52.69.133.219", "52.193.218.232" // LINE PAY IP 範圍
    );
    
    private static final Set<String> ECPAY_IPS = Set.of(
        "211.20.145.178", "211.20.145.179" // ECPay IP 範圍
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String remoteAddr = request.getRemoteAddr();
        String requestUri = ((HttpServletRequest) request).getRequestURI();
        
        if (requestUri.contains("/callback/linepay") && !LINEPAY_IPS.contains(remoteAddr)) {
            throw new SecurityException("Invalid callback IP");
        }
        
        if (requestUri.contains("/callback/ecpay") && !ECPAY_IPS.contains(remoteAddr)) {
            throw new SecurityException("Invalid callback IP");
        }
        
        chain.doFilter(request, response);
    }
}
```

## 測試建議

### 單元測試
建議為以下類別添加單元測試：
- `LinePayService` - 測試簽章生成、請求構建、回應解析
- `EcPayService` - 測試 CheckMacValue 生成、回調驗證
- `PaymentGatewayFactory` - 測試閘道選擇邏輯

### 整合測試
1. **LINE PAY 測試**:
   - 使用 LINE PAY sandbox 環境
   - 測試完整的支付流程
   - 驗證回調處理

2. **ECPay 測試**:
   - 使用測試卡號 `4311-9522-2222-2222`
   - 測試各種支付方式
   - 驗證 CheckMacValue 計算

### 端對端測試
```bash
# 1. 啟動應用
mvn spring-boot:run

# 2. 創建 LINE PAY 支付
curl -X POST http://localhost:8080/api/payment-gateway/create?gateway=LINE_PAY \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "TEST001",
    "amount": 100,
    "productName": "測試商品"
  }'

# 3. 訪問返回的 paymentUrl
# 4. 完成支付
# 5. 驗證回調是否正確更新訂單狀態
```

## 監控與日誌

### 建議監控指標
1. **支付成功率**
   - LINE PAY 成功率
   - ECPay 成功率
   - 各支付方式成功率

2. **支付時間**
   - 平均支付完成時間
   - 回調延遲時間

3. **異常情況**
   - 支付失敗次數
   - 簽章驗證失敗次數
   - 回調超時次數

### 日誌建議
```java
// 關鍵操作都應該記錄詳細日誌
log.info("Payment request created: gateway={}, orderNumber={}, amount={}", 
    gateway, request.getOrderNumber(), request.getAmount());

log.info("Payment callback received: gateway={}, transactionId={}, status={}", 
    gateway, transactionId, status);

// 錯誤應該包含足夠的上下文
log.error("Payment confirmation failed: transactionId={}, error={}", 
    transactionId, e.getMessage(), e);
```

## 效能優化建議

1. **非同步處理**:
   - 考慮將支付確認和訂單更新改為非同步處理
   - 使用訊息佇列（如 RabbitMQ、Kafka）處理支付回調

2. **快取**:
   - 快取支付閘道配置
   - 快取交易查詢結果（短時間）

3. **連線池**:
   - WebClient 已經內建連線池
   - 可以調整連線池大小以應對高流量

## 維護與升級

### 定期檢查
- [ ] 每季度檢查支付閘道 API 更新
- [ ] 定期更新依賴套件版本
- [ ] 檢視支付閘道交易日誌

### 版本升級
當支付閘道 API 升級時：
1. 檢查 API 變更說明
2. 在測試環境驗證
3. 更新程式碼
4. 進行完整測試
5. 部署到生產環境

## 支援資源

### 官方文檔
- **LINE PAY**: https://pay.line.me/tw/developers/apis/onlineApis
- **ECPay**: https://www.ecpay.com.tw/Service/API_Dwnld

### 技術支援
- **LINE PAY**: pay_api@linecorp.com
- **ECPay**: techsupport@ecpay.com.tw

## 常見問題排查

### 支付失敗
1. 檢查 API Key 是否正確
2. 確認環境設定（sandbox/production）
3. 檢查 callback URL 是否可訪問
4. 查看詳細錯誤日誌

### 回調未收到
1. 確認 callback URL 設定正確
2. 檢查防火牆規則
3. 確認 HTTPS 憑證有效
4. 查看支付閘道後台是否有錯誤記錄

### 簽章驗證失敗
1. 確認 Secret Key 正確
2. 檢查參數排序是否正確
3. 確認編碼方式一致
4. 記錄原始字串用於除錯

## 總結

本整合提供了完整的 LINE PAY 和 ECPay 支付功能基礎架構。在部署到生產環境前，請務必：

1. ✅ 完成訂單狀態同步邏輯
2. ✅ 配置正確的 API 金鑰
3. ✅ 設定正確的 callback URL
4. ✅ 實作 IP 白名單驗證
5. ✅ 進行完整的端對端測試
6. ✅ 設定監控和告警
7. ✅ 準備應急處理流程

完成這些步驟後，系統將準備好處理真實的支付交易。
