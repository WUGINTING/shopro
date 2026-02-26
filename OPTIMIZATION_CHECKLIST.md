# Shopro 優化檢查清單 (Quick Reference)

快速參考指南，用於追蹤優化項目的實施進度。

---

## 🔴 立即實施 (This Sprint)

- [ ] **安全性 - JWT 密鑰**
  - [ ] 從環境變數讀取密鑰
  - [ ] 設置強密鑰: `openssl rand -base64 32`
  - [ ] 移除版本控制中的默認值
  - [ ] 添加密鑰輪換機制
  - 文件: `application.properties`, `SecurityConfig.java`

- [ ] **效能 - 資料庫連接池**
  - [ ] 配置 HikariCP 參數
  - [ ] 設置 `maximum-pool-size=20`
  - [ ] 設置 `minimum-idle=5`
  - [ ] 添加連接洩漏檢測
  - 文件: `application.properties`

- [ ] **安全性 - CORS 配置**
  - [ ] 創建 `CorsConfig.java`
  - [ ] 指定具體的 allowed-origins（不用萬用字元）
  - [ ] 設置正確的 HTTP 方法
  - [ ] 添加 `allowCredentials=true`
  - 文件: `src/main/java/com/info/ecommerce/config/CorsConfig.java`

- [ ] **效能 - N+1 查詢修復**
  - [ ] 審查 `OrderQueryService.java`
  - [ ] 添加 `@EntityGraph` 或 `LEFT JOIN FETCH`
  - [ ] 修改 `searchOrders()` 方法
  - [ ] 添加整合測試驗證
  - 文件: `OrderQueryService.java`, `OrderRepository.java`

- [ ] **效能 - 緩存實施**
  - [ ] 添加 Redis 依賴到 `pom.xml`
  - [ ] 配置 `CacheConfig.java`
  - [ ] 添加 `@Cacheable` 到 `ProductService`
  - [ ] 配置緩存過期時間
  - 文件: `pom.xml`, 各個 Service 類

---

## 🟡 下週衝刺 (Next Sprint)

- [ ] **資料庫 - 複合索引**
  - [ ] Order: `idx_customer_status` (customer_id, status)
  - [ ] Order: `idx_created_status` (created_at, status)
  - [ ] Product: `idx_category_enabled` (category_id, enabled)
  - [ ] Member: `idx_level_enabled` (level_id, enabled)
  - 文件: 各個 Entity 類

- [ ] **API - 響應壓縮**
  - [ ] 啟用 Gzip 壓縮
  - [ ] 設置 `min-response-size=1024`
  - [ ] 添加 `Cache-Control` 頭
  - [ ] 配置瀏覽器緩存策略
  - 文件: `application.properties`, `WebConfig.java`

- [ ] **前端 - Bundle 優化**
  - [ ] 審查 `package.json` 依賴
  - [ ] 移除重複的 UI 庫
  - [ ] 配置代碼分割策略
  - [ ] 移除 console.log（生產環境）
  - 文件: `vite.config.ts`, `package.json`

- [ ] **安全性 - 密碼驗證**
  - [ ] 檢查 BCrypt 配置
  - [ ] 設置 strength=12
  - [ ] 實施密碼強度驗證
  - [ ] 添加登入嘗試計數
  - 文件: `SecurityConfig.java`, `AuthService.java`

- [ ] **測試 - JUnit 5 遷移**
  - [ ] 添加 JUnit 5 依賴
  - [ ] 寫 20 個 Payment 模組測試
  - [ ] 寫 25 個 Order 模組測試
  - [ ] 設定代碼覆蓋率目標 (80%)
  - 文件: `src/test/java/**/*Test.java`

---

## 📊 中期計劃 (2-4 Weeks)

- [ ] **架構 - 應用層分層**
  - [ ] 創建 `*ApplicationService` 類
  - [ ] 創建 `*DomainService` 類
  - [ ] 分離業務邏輯與資料訪問
  - [ ] 添加應用層單元測試
  - 文件: 新建 `application` 和 `domain` 包

- [ ] **架構 - 事件驅動**
  - [ ] 定義領域事件類
  - [ ] 實施 `ApplicationEventPublisher`
  - [ ] 創建事件監聽器
  - [ ] 實施非同步事件處理
  - 文件: 新建 `event` 包

- [ ] **監控 - Actuator & Prometheus**
  - [ ] 添加 Spring Boot Actuator
  - [ ] 配置 Micrometer
  - [ ] 暴露關鍵指標
  - [ ] 集成 Prometheus/Grafana
  - 文件: `pom.xml`, `application.properties`

- [ ] **安全性 - 速率限制**
  - [ ] 添加 Bucket4j 依賴
  - [ ] 實施 `RateLimitingInterceptor`
  - [ ] 配置 API 限速規則
  - [ ] 添加測試用例
  - 文件: `src/main/java/com/info/ecommerce/config/RateLimitingConfig.java`

---

## 🎯 長期計劃 (4-8 Weeks)

- [ ] **性能測試**
  - [ ] 編寫 JMeter 測試計劃
  - [ ] 測試 1000+ 並發用戶
  - [ ] 生成基準報告
  - [ ] 識別瓶頸點
  - 文件: 新建 `performance-tests` 目錄

- [ ] **代碼質量 - SonarQube**
  - [ ] 安裝 SonarQube 伺服器
  - [ ] 集成 Maven SonarQube 插件
  - [ ] 掃描代碼
  - [ ] 修復關鍵問題
  - 文件: `pom.xml`, 新建 `sonar-project.properties`

- [ ] **CI/CD 優化**
  - [ ] 配置自動化測試
  - [ ] 添加代碼掃描
  - [ ] 實施自動部署
  - [ ] 添加性能基準檢查
  - 文件: `.github/workflows/` 或 `.gitlab-ci.yml`

- [ ] **前端狀態管理**
  - [ ] 拆分 Pinia stores
  - [ ] 優化狀態訂閱
  - [ ] 減少不必要的重新渲染
  - [ ] 添加單元測試
  - 文件: `src/stores/**`

- [ ] **日誌聚合**
  - [ ] 配置 ELK 或 Splunk
  - [ ] 集成應用日誌
  - [ ] 設置告警規則
  - [ ] 監控關鍵事件
  - 文件: `logback-spring.xml`

---

## ✅ 驗證檢查清單

實施完成後的驗收標準：

### 效能指標
- [ ] 訂單列表查詢 < 200ms
- [ ] API 平均響應 < 100ms
- [ ] 首屏加載 < 2s
- [ ] Bundle 大小 < 200KB (gzip)
- [ ] 資料庫查詢數 <= 3 個

### 安全性指標
- [ ] JWT 密鑰從環境變數讀取 ✓
- [ ] 密碼使用 BCrypt(12) 加密 ✓
- [ ] CORS 安全配置生效 ✓
- [ ] 無 SQL 注入漏洞 ✓
- [ ] 依賴無已知 CVE ✓

### 代碼品質指標
- [ ] 單元測試覆蓋率 >= 80% ✓
- [ ] 無 P0/P1 代碼異味 ✓
- [ ] SonarQube 評分 >= A ✓
- [ ] 循環複雜度 <= 10 ✓

### 可靠性指標
- [ ] 應用無內存洩漏 ✓
- [ ] 資料庫連接穩定 ✓
- [ ] 高可用性配置 ✓
- [ ] 災難恢復計劃 ✓

---

## 📚 相關文檔參考

| 文檔 | 位置 | 用途 |
|------|------|------|
| 架構概覽 | `docs/architecture-overview.md` | 系統整體理解 |
| 後端架構 | `docs/backend-architecture.md` | 模組詳情 |
| API 文檔 | `docs/api-reference.md` | API 端點 |
| 資料庫設計 | `docs/database-design.md` | ER 圖、索引 |
| 開發指南 | `docs/development-guide.md` | 開發規範 |
| 部署指南 | `docs/deployment-guide.md` | 部署步驟 |

---

## 🔧 常用命令

### 後端相關
```bash
# 運行項目
mvn spring-boot:run

# 運行測試
mvn test

# 運行特定測試
mvn test -Dtest=OrderServiceTest

# 代碼覆蓋率
mvn clean test jacoco:report

# SonarQube 掃描
mvn clean verify sonar:sonar

# 查看依賴樹
mvn dependency:tree

# 檢查依賴更新
mvn versions:display-dependency-updates
```

### 前端相關
```bash
# 開發伺服器
npm run dev

# 類型檢查
npm run type-check

# 構建生產版本
npm run build

# 分析包大小
npm install -g webpack-bundle-analyzer
# 在 vite.config.ts 中集成

# Lighthouse 性能測試
npm install -g @googlechromelabs/lighthouserc
lhci autorun
```

### 資料庫相關
```sql
-- 查看已存在的索引
SELECT * FROM sys.indexes
WHERE object_id = OBJECT_ID('table_name')

-- 分析查詢執行計劃
SET STATISTICS IO ON
SELECT * FROM your_query
SET STATISTICS IO OFF

-- 檢查 fragmentation
SELECT * FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'LIMITED')
```

---

## 📞 支持和反饋

- **問題報告**: 在各自的優化任務 PR 中提出
- **性能指標**: 每週在團隊會議中報告進度
- **技術討論**: 在 Slack #architecture 頻道討論

---

**最後更新**: 2026-02-25  
**下次檢查**: 每週一 10:00

