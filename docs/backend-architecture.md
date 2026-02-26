# 後端架構

## 概述

後端使用 Spring Boot 3.4.1 建構，採用標準的分層架構：Controller → Service → Repository。

## 目錄結構

```
E-commerce/src/main/java/com/info/ecommerce/
├── common/                          # 通用層
│   ├── ApiResponse.java            # 統一 API 回應格式
│   └── exception/
│       ├── BusinessException.java  # 業務異常
│       └── GlobalExceptionHandler.java
│
├── config/                          # 配置層
│   ├── CorsConfig.java             # CORS 配置
│   └── OpenApiConfig.java          # Swagger 配置
│
└── modules/                         # 業務模組
    ├── auth/                        # 認證授權
    ├── product/                     # 商品管理
    ├── order/                       # 訂單管理
    ├── crm/                         # CRM
    ├── payment/                     # 支付整合
    ├── marketing/                   # 行銷活動
    ├── store/                       # 商店設定
    ├── system/                      # 系統設定
    ├── album/                       # 相冊管理
    ├── calendar/                    # 行事曆
    ├── dashboard/                   # 儀表板
    ├── statistics/                  # 統計報表
    └── ads/                         # 廣告追蹤
```

## 標準模組結構

每個業務模組遵循統一結構：

```
module/
├── controller/          # 控制層 - REST API
├── dto/                 # 資料傳輸物件
├── entity/              # JPA 實體
├── enums/               # 列舉類型
├── repository/          # 資料存取層
├── service/             # 業務邏輯層
├── config/              # 模組配置 (選用)
└── task/                # 排程任務 (選用)
```

---

## 核心模組詳解

### 1. Auth 模組 (認證授權)

**功能：**
- JWT Token 認證
- 使用者註冊/登入
- Google OAuth 整合
- 角色權限管理

**關鍵檔案：**

```
auth/
├── config/
│   ├── SecurityConfig.java        # Spring Security 配置
│   └── JwtAuthenticationFilter.java
├── controller/
│   ├── AuthController.java        # 登入/註冊 API
│   └── UserController.java        # 使用者管理
├── entity/
│   ├── User.java                  # 使用者實體
│   └── Role.java                  # 角色列舉
└── service/
    ├── AuthService.java           # 認證服務
    └── JwtService.java            # JWT 處理
```

**角色定義：**

```java
public enum Role {
    ADMIN,      // 最高權限
    MANAGER,    // 管理者
    STAFF,      # 員工
    CUSTOMER    // 客戶
}
```

**安全配置重點：**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 公開端點
    private static final String[] PUBLIC_URLS = {
        "/api/auth/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    // JWT 過濾器
    // 角色權限控制
}
```

---

### 2. Product 模組 (商品管理)

**功能：**
- 商品 CRUD (500~10,000 項)
- 分類管理 (階層式)
- 規格管理 (SKU)
- 庫存管理
- 描述區塊

**關鍵實體：**

```java
@Entity
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private Integer stock;
    private ProductStatus status;      // DRAFT, ACTIVE, INACTIVE
    private ProductSalesMode salesMode; // NORMAL, PRE_ORDER, TICKET...
    private ProductCategory category;
    private List<ProductSpecification> specifications;
    private List<ProductImage> images;
    private List<ProductDescriptionBlock> descriptionBlocks;
}
```

**銷售模式：**

```java
public enum ProductSalesMode {
    NORMAL,       // 一般商品
    PRE_ORDER,    // 預購
    TICKET,       // 票券
    SUBSCRIPTION, // 訂閱
    STORE_ONLY    // 門市限定
}
```

---

### 3. Order 模組 (訂單管理)

**功能：**
- 訂單處理
- O2O 支援 (門市取貨)
- 出貨管理
- 訂單問答

**關鍵實體：**

```java
@Entity
public class Order {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private PickupType pickupType;     // DELIVERY, STORE_PICKUP
    private Long storeId;              // O2O 門市
    private List<OrderItem> items;
    private List<OrderPayment> payments;
    private List<OrderShipment> shipments;
}
```

**訂單狀態流程：**

```
PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED
    ↓         ↓           ↓          ↓
CANCELLED  CANCELLED  CANCELLED   RETURNED
```

---

### 4. Payment 模組 (支付整合)

**支援的支付閘道：**
- ECPay (綠界)
- LINE Pay

**架構設計：**

```java
// 支付閘道介面
public interface PaymentGatewayService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse confirmPayment(String transactionId);
    PaymentResponse handleCallback(Map<String, String> params);
}

// 工廠模式取得服務
public class PaymentGatewayFactory {
    public PaymentGatewayService getService(PaymentGateway gateway) {
        return switch (gateway) {
            case ECPAY -> ecPayService;
            case LINEPAY -> linePayService;
        };
    }
}
```

**回調處理流程：**

```
1. 接收第三方回調
2. 驗證簽名
3. 記錄 PaymentCallbackLog
4. 更新交易狀態
5. 更新訂單狀態
6. 發送通知
```

---

### 5. CRM 模組 (顧客關係管理)

**子模組：**
- 會員管理
- 會員等級
- 會員群組
- 積點系統
- EDM 電子報
- 部落格

**會員等級系統：**

```java
@Entity
public class MemberLevel {
    private Long id;
    private String name;
    private Integer minPoints;        // 升級門檻
    private BigDecimal discountRate;  // 折扣率
    private Integer pointMultiplier;  // 積點倍數
}

@Entity
public class Member {
    private Long id;
    private User user;
    private MemberLevel level;
    private Integer totalPoints;
    private Integer availablePoints;
}
```

---

## 通用層

### ApiResponse - 統一回應格式

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
```

### 全局異常處理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e) {
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("系統發生錯誤，請稍後再試"));
    }
}
```

---

## 配置管理

### 環境配置

**application.properties** - 主配置

```properties
# 環境
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# 資料庫
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

**application-dev.properties** - 開發環境

```properties
spring.jpa.show-sql=true
logging.level.com.info.ecommerce=DEBUG
```

**application-prod.properties** - 生產環境

```properties
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
```

---

## 開發規範

### Controller 規範

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(
            productService.getProducts(PageRequest.of(page, size))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO dto) {
        return ApiResponse.success(productService.createProduct(dto));
    }
}
```

### Service 規範

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(this::toDTO);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = toEntity(dto);
        return toDTO(productRepository.save(product));
    }
}
```

### Repository 規範

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );
}
```

---

## 測試

### 測試配置

```properties
# test/resources/application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 單元測試範例

```java
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Test Product");
        dto.setBasePrice(new BigDecimal("100"));

        ProductDTO result = productService.createProduct(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
    }
}
```
