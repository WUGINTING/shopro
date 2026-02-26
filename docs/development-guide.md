# 開發指南

## 開發環境設定

### IDE 推薦

**後端：**
- IntelliJ IDEA (推薦)
- Eclipse with Spring Tools

**前端：**
- VS Code with Volar extension
- WebStorm

### VS Code 擴充套件

```json
{
  "recommendations": [
    "Vue.volar",
    "Vue.vscode-typescript-vue-plugin",
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "bradlc.vscode-tailwindcss"
  ]
}
```

---

## 程式碼規範

### 後端規範

#### 命名規則

| 類型 | 規則 | 範例 |
|------|------|------|
| 類別 | PascalCase | `ProductService` |
| 方法 | camelCase | `getProducts()` |
| 常數 | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE` |
| 套件 | 小寫 | `com.info.ecommerce` |

#### Controller 規範

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品 CRUD 操作")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "取得商品列表")
    public ApiResponse<Page<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(
            productService.getProducts(PageRequest.of(page, size), status)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "創建商品")
    public ApiResponse<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO dto) {
        return ApiResponse.success(productService.createProduct(dto));
    }
}
```

#### Service 規範

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductDTO> getProducts(Pageable pageable, String status) {
        log.info("Getting products with status: {}", status);
        return productRepository.findByStatus(status, pageable)
            .map(productMapper::toDTO);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        log.info("Creating product: {}", dto.getName());
        Product product = productMapper.toEntity(dto);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }
}
```

#### DTO 規範

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "商品名稱不能為空")
    @Size(max = 200, message = "商品名稱不能超過 200 字")
    private String name;

    private String description;

    @NotNull(message = "價格不能為空")
    @DecimalMin(value = "0", message = "價格不能為負數")
    private BigDecimal basePrice;

    @Min(value = 0, message = "庫存不能為負數")
    private Integer stock;

    private String status;
}
```

---

### 前端規範

#### 檔案命名

| 類型 | 規則 | 範例 |
|------|------|------|
| 頁面 | *View.vue | `ProductView.vue` |
| 組件 | PascalCase | `IconPicker.vue` |
| Composable | use*.ts | `useDebounce.ts` |
| Store | *.ts | `auth.ts` |
| API | *.ts | `product.ts` |

#### Vue 組件規範

```vue
<template>
  <q-page class="q-pa-md">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h5">{{ title }}</div>
      <q-btn
        color="primary"
        icon="add"
        label="新增"
        @click="handleAdd"
        aria-label="新增項目"
      />
    </div>

    <!-- 內容區域 -->
    <q-card>
      <q-table
        :rows="items"
        :columns="columns"
        :loading="loading"
        :grid="$q.screen.lt.md"
      />
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useErrorHandler } from '@/composables'
import { itemApi, type Item } from '@/api'

// Props & Emits
const props = defineProps<{
  title: string
}>()

const emit = defineEmits<{
  (e: 'update', item: Item): void
}>()

// Composables
const $q = useQuasar()
const { handleError, handleSuccess } = useErrorHandler()

// State
const items = ref<Item[]>([])
const loading = ref(false)

// Methods
const loadItems = async () => {
  loading.value = true
  try {
    const response = await itemApi.getItems()
    items.value = response.data
  } catch (error) {
    handleError(error, '載入失敗')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  // 處理新增
}

// Lifecycle
onMounted(loadItems)
</script>

<style scoped lang="scss">
// 組件專屬樣式
</style>
```

#### TypeScript 規範

```typescript
// 明確定義介面
interface Product {
  id: number
  name: string
  price: number
  status: ProductStatus
}

type ProductStatus = 'DRAFT' | 'ACTIVE' | 'INACTIVE'

// 使用 ref 時指定類型
const products = ref<Product[]>([])
const selectedProduct = ref<Product | null>(null)

// API 回應類型
interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// 避免使用 any
// ❌ const data: any = response.data
// ✓ const data: Product[] = response.data
```

---

## Git 工作流程

### 分支策略

```
main
  │
  ├── develop
  │     │
  │     ├── feature/product-management
  │     ├── feature/payment-integration
  │     └── bugfix/order-status
  │
  └── release/v1.0.0
```

### Commit 規範

```
<type>(<scope>): <subject>

<body>

<footer>
```

**類型：**
- `feat`: 新功能
- `fix`: 修復 Bug
- `docs`: 文檔更新
- `style`: 程式碼格式
- `refactor`: 重構
- `test`: 測試
- `chore`: 雜項

**範例：**

```
feat(product): add product specification management

- Add ProductSpecification entity
- Add CRUD endpoints for specifications
- Add batch specification import

Closes #123
```

### PR 流程

1. 從 `develop` 建立功能分支
2. 開發完成後發起 PR
3. 至少一位成員 Code Review
4. 通過 CI 測試
5. 合併至 `develop`

---

## 測試規範

### 後端測試

```java
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("應該成功創建商品")
    void shouldCreateProduct() {
        // Given
        ProductDTO dto = ProductDTO.builder()
            .name("測試商品")
            .basePrice(new BigDecimal("100"))
            .stock(10)
            .build();

        // When
        ProductDTO result = productService.createProduct(dto);

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("測試商品");

        // 驗證資料庫
        Product saved = productRepository.findById(result.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("測試商品");
    }

    @Test
    @DisplayName("創建商品時名稱為空應拋出異常")
    void shouldThrowWhenNameIsEmpty() {
        ProductDTO dto = ProductDTO.builder()
            .name("")
            .basePrice(new BigDecimal("100"))
            .build();

        assertThatThrownBy(() -> productService.createProduct(dto))
            .isInstanceOf(ConstraintViolationException.class);
    }
}
```

### 前端測試

```typescript
// 使用 Vitest
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ProductView from '@/views/ProductView.vue'

describe('ProductView', () => {
  it('應該顯示商品列表', async () => {
    const wrapper = mount(ProductView)

    // 等待資料載入
    await wrapper.vm.$nextTick()

    // 驗證表格存在
    expect(wrapper.find('.q-table').exists()).toBe(true)
  })

  it('點擊新增應開啟對話框', async () => {
    const wrapper = mount(ProductView)

    await wrapper.find('[data-test="add-btn"]').trigger('click')

    expect(wrapper.find('.q-dialog').exists()).toBe(true)
  })
})
```

---

## 除錯技巧

### 後端除錯

```java
// 使用 @Slf4j
@Slf4j
public class ProductService {
    public void process(Product product) {
        log.debug("Processing product: {}", product);
        log.info("Product processed: id={}", product.getId());
        log.warn("Stock low: {}", product.getStock());
        log.error("Failed to process", exception);
    }
}
```

### 前端除錯

```typescript
// Vue Devtools
// 安裝 Vue Devtools 瀏覽器擴充套件

// Console 除錯
console.log('data:', data)
console.table(products)
console.group('API Request')
console.log('URL:', url)
console.log('Params:', params)
console.groupEnd()

// 條件斷點
debugger  // 在程式碼中設置斷點
```

### API 除錯

```bash
# 使用 curl 測試 API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 使用 httpie
http POST localhost:8080/api/products \
  Authorization:"Bearer <token>" \
  name="Test Product" \
  price:=100
```

---

## 效能優化

### 後端優化

```java
// 1. 使用分頁
Page<Product> findAll(Pageable pageable);

// 2. 避免 N+1 查詢
@Query("SELECT p FROM Product p JOIN FETCH p.category")
List<Product> findAllWithCategory();

// 3. 使用投影
interface ProductSummary {
    Long getId();
    String getName();
    BigDecimal getPrice();
}
List<ProductSummary> findAllSummary();

// 4. 快取常用資料
@Cacheable("categories")
List<Category> findAllEnabled();
```

### 前端優化

```typescript
// 1. 路由懶載入
component: () => import('../views/ProductView.vue')

// 2. 圖片懶載入
<q-img :src="imageUrl" loading="lazy" />

// 3. 搜尋防抖
const debouncedSearch = useDebouncedRef(searchQuery, 300)

// 4. 虛擬列表 (大量資料)
<q-virtual-scroll :items="items" v-slot="{ item }">
  <div>{{ item.name }}</div>
</q-virtual-scroll>
```

---

## 常見問題

### Q: 如何新增一個模組？

1. **後端：**
   ```
   modules/newmodule/
   ├── controller/NewModuleController.java
   ├── dto/NewModuleDTO.java
   ├── entity/NewModule.java
   ├── repository/NewModuleRepository.java
   └── service/NewModuleService.java
   ```

2. **前端：**
   ```
   api/newmodule.ts       # API 服務
   views/NewModuleView.vue # 頁面視圖
   router/index.ts        # 添加路由
   ```

### Q: 如何新增 API 端點？

1. 在 Controller 添加方法
2. 在 Service 實作業務邏輯
3. 在前端 api/ 添加對應方法
4. 更新 Swagger 註解

### Q: 如何處理跨模組依賴？

- 使用 Service 層互相呼叫
- 避免 Controller 直接呼叫其他模組的 Repository
- 複雜流程考慮使用 Event 機制

---

## 資源

### 官方文檔

- [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Vue 3](https://vuejs.org/)
- [Quasar](https://quasar.dev/)
- [TypeScript](https://www.typescriptlang.org/docs/)

### 專案文檔

- [架構概覽](./architecture-overview.md)
- [API 參考](./api-reference.md)
- [資料庫設計](./database-design.md)
- [部署指南](./deployment-guide.md)
