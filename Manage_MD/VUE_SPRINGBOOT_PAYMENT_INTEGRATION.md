# Vue + Spring Boot 支付系統整合指南

## 系統架構

本系統採用 **策略模式 (Strategy Pattern)** 與 **工廠模式 (Factory Pattern)**，確保未來若要增加「街口支付」或「藍新支付」時，不需要修改核心邏輯。

### 分層架構

```
┌─────────────────────────────────────────────────────────────┐
│             前端層 (Quasar/Vue 3)                            │
├─────────────────────────────────────────────────────────────┤
│  • Payment Dashboard (金流儀表板)                            │
│    - 今日/本月成交金額、成功率統計                           │
│    - 支付管道佔比圓餅圖                                      │
│    - 退款統計                                               │
│                                                              │
│  • Transaction Manager (金流交易紀錄)                        │
│    - 交易列表與搜尋                                          │
│    - 狀態同步功能                                           │
│    - 交易詳情查看                                           │
│                                                              │
│  • Gateway Configuration (支付參數設定)                      │
│    - 啟用/停用支付方式                                       │
│    - 維護模式設定                                           │
│    - 抽成比率設定                                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ REST API (JSON)
                              │
┌─────────────────────────────▼─────────────────────────────────┐
│             後端 API 層 (Spring Boot)                         │
├─────────────────────────────────────────────────────────────┤
│  • PaymentGatewayController                                 │
│    - 統一的 Entry Point                                      │
│    - /api/payment-gateway/create                           │
│    - /api/payment-gateway/confirm                          │
│    - /api/payment-gateway/query                            │
│                                                              │
│  • PaymentManagementController (新增)                       │
│    - /api/payment-management/statistics                    │
│    - /api/payment-management/transactions                  │
│    - /api/payment-management/settings                      │
│                                                              │
│  • Webhook / Callback Listener                             │
│    - /api/payment-gateway/callback/ecpay                   │
│    - /api/payment-gateway/callback/linepay/confirm         │
│    (需跳過 JWT 驗證但必須驗證簽章)                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              │
┌─────────────────────────────▼─────────────────────────────────┐
│           業務邏輯層 (Service Layer)                          │
├─────────────────────────────────────────────────────────────┤
│  • PaymentGatewayFactory                                    │
│    - 根據 gateway 參數動態分派                               │
│    - 支援 LINE_PAY, ECPAY, MANUAL                           │
│                                                              │
│  • LinePayService / EcPayService                            │
│    - 實作 PaymentGatewayService 介面                        │
│    - HMAC-SHA256 / CheckMacValue 驗證                       │
│                                                              │
│  • PaymentManagementService (新增)                          │
│    - 統計數據彙總                                           │
│    - 交易查詢與管理                                          │
│    - 設定管理                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              │
┌─────────────────────────────▼─────────────────────────────────┐
│         資料持久層 (Persistence Layer)                        │
├─────────────────────────────────────────────────────────────┤
│  • payment_gateway_transactions                             │
│    - 紀錄所有交易，包含 request/response log                │
│                                                              │
│  • payment_settings (新增)                                  │
│    - 各支付閘道的啟用狀態、抽成比率                          │
│                                                              │
│  • order_payments                                           │
│    - 關聯訂單與支付狀態                                      │
└─────────────────────────────────────────────────────────────┘
```

## 前後端整合工作流

### 1. 交易狀態監控流程 (Transaction Monitoring)

```
用戶完成支付
    │
    ▼
外部閘道 POST 通知 → /api/payment-gateway/callback/ecpay
    │
    ▼
後端驗證 CheckMacValue/HMAC
    │
    ▼
更新 payment_gateway_transactions 狀態
    │
    ▼
觸發訂單狀態更新 (Order.status = PAID)
    │
    ▼
前端透過輪詢或 WebSocket 接收更新
    │
    ▼
更新後台管理介面顯示
```

### 2. 後台管理介面功能佈局

| 選單名稱 | 功能說明 | 對應後端 API | 前端路由 |
|---------|---------|------------|---------|
| 金流儀表板 | 圓餅圖顯示各支付管道佔比 (LINE PAY vs 信用卡) | GET /api/payment-management/statistics | /payment-dashboard |
| 金流交易紀錄 | 搜尋特定訂單編號、交易序號 (TradeNo) | GET /api/payment-management/transactions | /payment-transactions |
| 支付參數設定 | 設定各個支付方式的啟用狀態、說明文字 | GET/PUT /api/payment-management/settings | /payment-settings |

## 安全性強化

### 已實作功能

#### 1. 敏感資訊管理
- 配置檔案使用環境變數佔位符
- 建議使用 Spring Cloud Config 或 Azure Key Vault
- 資料庫中的 HashKey 等資訊應加密存儲

#### 2. 簽章驗證
- **LINE PAY**: HMAC-SHA256 簽章
  ```java
  String signatureData = channelSecret + uri + requestBody + nonce;
  Mac mac = Mac.getInstance("HmacSHA256");
  ```

- **ECPay**: SHA256 CheckMacValue
  ```java
  String rawString = "HashKey=" + hashKey + "&" + sortedParams + "&HashIV=" + hashIV;
  String checkMacValue = DigestUtils.sha256Hex(urlEncode(rawString).toLowerCase()).toUpperCase();
  ```

### 建議加強項目

#### 3. 冪等性處理 (Idempotency)

**問題**: 支付回調 (Webhook) 可能因網路問題收到多次

**解決方案**:
```java
@Transactional
public void processCallback(String transactionId, PaymentResponseDTO response) {
    // 1. 檢查交易是否已處理
    Optional<PaymentGatewayTransaction> existing = 
        transactionRepository.findByTransactionId(transactionId);
    
    if (existing.isPresent() && existing.get().getStatus() == PaymentGatewayStatus.SUCCESS) {
        log.warn("Transaction already processed: {}", transactionId);
        return; // 已處理，直接返回
    }
    
    // 2. 使用悲觀鎖確保只處理一次
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PaymentGatewayTransaction transaction = transactionRepository.findByTransactionId(transactionId)
        .orElseThrow(() -> new RuntimeException("Transaction not found"));
    
    // 3. 更新狀態
    transaction.setStatus(response.getStatus());
    transactionRepository.save(transaction);
    
    // 4. 更新訂單狀態
    updateOrderStatus(transaction.getOrderNumber(), response);
}
```

#### 4. IP 白名單驗證

**實作範例**:
```java
@Component
public class PaymentCallbackFilter implements Filter {
    
    private static final Set<String> LINEPAY_IPS = Set.of(
        "52.69.133.219", 
        "52.193.218.232"
    );
    
    private static final Set<String> ECPAY_IPS = Set.of(
        "211.20.145.178", 
        "211.20.145.179"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String remoteAddr = request.getRemoteAddr();
        String requestUri = httpRequest.getRequestURI();
        
        // 檢查 LINE PAY 回調
        if (requestUri.contains("/callback/linepay")) {
            if (!LINEPAY_IPS.contains(remoteAddr)) {
                log.error("Invalid callback IP for LINE PAY: {}", remoteAddr);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        
        // 檢查 ECPay 回調
        if (requestUri.contains("/callback/ecpay")) {
            if (!ECPAY_IPS.contains(remoteAddr)) {
                log.error("Invalid callback IP for ECPay: {}", remoteAddr);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
}

@Configuration
public class SecurityConfig {
    @Bean
    public FilterRegistrationBean<PaymentCallbackFilter> paymentCallbackFilter() {
        FilterRegistrationBean<PaymentCallbackFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PaymentCallbackFilter());
        registrationBean.addUrlPatterns("/api/payment-gateway/callback/*");
        return registrationBean;
    }
}
```

#### 5. 日誌審計

**實作範例**:
```java
@Aspect
@Component
@Slf4j
public class PaymentAuditAspect {
    
    @Around("execution(* com.info.ecommerce.modules.payment.service.*.*(..))")
    public Object auditPaymentOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // 記錄請求
        log.info("Payment operation started: method={}, args={}", methodName, 
            Arrays.toString(args));
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            // 記錄成功
            long duration = System.currentTimeMillis() - startTime;
            log.info("Payment operation completed: method={}, duration={}ms, result={}", 
                methodName, duration, result);
            
            return result;
        } catch (Exception e) {
            // 記錄失敗
            long duration = System.currentTimeMillis() - startTime;
            log.error("Payment operation failed: method={}, duration={}ms, error={}", 
                methodName, duration, e.getMessage(), e);
            throw e;
        }
    }
}
```

## 前端整合步驟

### 1. 安裝依賴

```bash
cd frontend
npm install chart.js
```

### 2. 更新路由配置

在 `src/router/index.ts` 中已新增：

```typescript
{
  path: '/payment-dashboard',
  name: 'paymentDashboard',
  component: () => import('../views/PaymentDashboardView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/payment-transactions',
  name: 'paymentTransactions',
  component: () => import('../views/PaymentTransactionsView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/payment-settings',
  name: 'paymentSettings',
  component: () => import('../views/PaymentSettingsView.vue'),
  meta: { requiresAuth: true }
}
```

### 3. 更新導航選單

在主佈局中新增支付管理選單項目：

```vue
<template>
  <q-drawer>
    <q-list>
      <!-- 其他選單項目 -->
      
      <q-item-label header>支付管理</q-item-label>
      
      <q-item clickable :to="{ name: 'paymentDashboard' }">
        <q-item-section avatar>
          <q-icon name="dashboard" />
        </q-item-section>
        <q-item-section>
          <q-item-label>金流儀表板</q-item-label>
        </q-item-section>
      </q-item>
      
      <q-item clickable :to="{ name: 'paymentTransactions' }">
        <q-item-section avatar>
          <q-icon name="receipt_long" />
        </q-item-section>
        <q-item-section>
          <q-item-label>金流交易紀錄</q-item-label>
        </q-item-section>
      </q-item>
      
      <q-item clickable :to="{ name: 'paymentSettings' }">
        <q-item-section avatar>
          <q-icon name="settings" />
        </q-item-section>
        <q-item-section>
          <q-item-label>支付參數設定</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-drawer>
</template>
```

### 4. API 整合範例

```typescript
// 在 component 中使用
import { getPaymentStatistics, searchTransactions } from '@/api/payment'

// 取得統計資料
const loadStatistics = async () => {
  const response = await getPaymentStatistics()
  if (response.success) {
    statistics.value = response.data
  }
}

// 搜尋交易
const searchPayments = async () => {
  const response = await searchTransactions({
    keyword: searchKeyword.value,
    gateway: 'LINE_PAY',
    status: 'SUCCESS'
  })
  if (response.success) {
    transactions.value = response.data
  }
}
```

## 測試指南

### 後端測試

```bash
cd E-commerce

# 編譯專案
mvn clean compile

# 執行測試
mvn test

# 啟動應用
mvn spring-boot:run
```

### 前端測試

```bash
cd frontend

# 安裝依賴
npm install

# 開發模式
npm run dev

# 建置生產版本
npm run build
```

### API 測試

使用 Swagger UI 測試 API：
```
http://localhost:8080/swagger-ui.html
```

## 資料庫初始化

### 1. 支付設定初始化

```sql
-- LINE PAY 初始設定
INSERT INTO payment_settings (gateway, enabled, display_name, description, commission_rate, maintenance_mode, sort_order, created_at, updated_at)
VALUES ('LINE_PAY', true, 'LINE PAY', 'LINE PAY 行動支付', 2.5, false, 1, GETDATE(), GETDATE());

-- ECPay 初始設定
INSERT INTO payment_settings (gateway, enabled, display_name, description, commission_rate, maintenance_mode, sort_order, created_at, updated_at)
VALUES ('ECPAY', true, '綠界支付', 'ECPay 多元支付 (信用卡/ATM/超商)', 2.8, false, 2, GETDATE(), GETDATE());

-- 手動付款初始設定
INSERT INTO payment_settings (gateway, enabled, display_name, description, commission_rate, maintenance_mode, sort_order, created_at, updated_at)
VALUES ('MANUAL', true, '手動付款', '現金/銀行轉帳等手動記錄', 0.0, false, 3, GETDATE(), GETDATE());
```

## 部署注意事項

### 1. 環境變數設定

```bash
# LINE PAY
export LINE_PAY_CHANNEL_ID=your_channel_id
export LINE_PAY_CHANNEL_SECRET_KEY=your_secret_key

# ECPay
export ECPAY_MERCHANT_ID=your_merchant_id
export ECPAY_HASH_KEY=your_hash_key
export ECPAY_HASH_IV=your_hash_iv
```

### 2. Callback URL 設定

確保 callback URL 可從外部訪問且使用 HTTPS：

```properties
# 生產環境設定
payment.linepay.confirm-url=https://yourdomain.com/api/payment-gateway/callback/linepay/confirm
payment.linepay.cancel-url=https://yourdomain.com/api/payment-gateway/callback/linepay/cancel
payment.ecpay.notify-url=https://yourdomain.com/api/payment-gateway/callback/ecpay
```

### 3. CORS 設定

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://yourdomain.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

## 監控與維護

### 1. 日誌監控

重要日誌應包含：
- 支付請求與回應的完整內容
- 簽章驗證結果
- 狀態變更記錄
- 錯誤與異常

### 2. 效能監控

建議監控指標：
- API 回應時間
- 支付成功率
- 回調處理時間
- 資料庫查詢效能

### 3. 異常告警

設定告警規則：
- 支付成功率低於 95%
- API 錯誤率超過 5%
- 回調處理失敗
- 簽章驗證失敗次數異常

## 常見問題

### Q: 如何測試支付流程？

A: 使用測試頁面 `/payment-test.html` 或透過 Swagger UI 測試 API。

### Q: 支付回調沒有收到？

A: 
1. 檢查 callback URL 是否正確設定
2. 確認防火牆允許支付閘道 IP
3. 檢查 HTTPS 憑證是否有效
4. 查看支付閘道後台的通知記錄

### Q: 如何切換測試/正式環境？

A: 修改 `application.properties` 中的設定：
```properties
payment.linepay.sandbox=false
payment.ecpay.sandbox=false
```

## 後續擴充建議

1. **WebSocket 即時通知** - 支付完成即時推送到前端
2. **退款功能** - 支援部分退款與全額退款
3. **對帳功能** - 每日自動對帳報表
4. **多幣別支援** - 支援美金、港幣等
5. **分期付款** - 整合信用卡分期功能

## 參考資源

- [LINE PAY 官方文檔](https://pay.line.me/tw/developers/apis/onlineApis)
- [ECPay 官方文檔](https://www.ecpay.com.tw/Service/API_Dwnld)
- [Quasar Framework](https://quasar.dev/)
- [Vue 3 官方文檔](https://vuejs.org/)
- [Spring Boot 官方文檔](https://spring.io/projects/spring-boot)
