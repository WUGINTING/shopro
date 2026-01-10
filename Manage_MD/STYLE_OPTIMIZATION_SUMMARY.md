# Shopro 專案樣式優化總結

## 優化目標
1. 優先使用 Quasar v2 內建樣式
2. 整合重複的自定義樣式
3. 建立統一的樣式管理系統

## 完成項目

### 1. 建立統一樣式檔案 (`src/styles/app.scss`)

整合了以下重複出現的樣式：

#### 漸層背景
- `.bg-gradient-primary` - 主要藍色漸層
- `.bg-gradient-purple` - 登入頁紫色漸層

#### 卡片樣式
- `.card-hover` - 懸浮效果（上移 + 陰影）
- `.card-border-top` - 頂部邊框，配合顏色變體：
  - `.border-primary`
  - `.border-orange`
  - `.border-teal`
  - `.border-green`
  - `.border-red`
  - `.border-blue`

#### 容器樣式
- `.page-container` - 一般頁面容器 (max-width: 1400px)
- `.profile-container` - 個人資料頁容器 (max-width: 1200px)

#### 資訊顯示
- `.info-row` - 資訊行（左右排列）
- `.info-label` - 資訊標籤
- `.info-value` - 資訊值

#### 導航樣式
- `.q-item` - 導航項目圓角間距
- `.q-expansion-item` - 展開項目圓角間距

#### 圖片相關
- `.album-card` - 相冊卡片懸浮效果
- `.album-cover` - 相冊封面定位

### 2. 優化的檔案清單

#### App.vue
- ✅ 移除重複的全局樣式定義
- ✅ 移除 scoped 和非 scoped 的重複樣式
- ✅ 引入統一樣式檔案
- ✅ 僅保留必要的表格樣式覆寫

#### Views 檔案
- ✅ **HomeView.vue** - 使用統一漸層和卡片樣式類別
- ✅ **LoginView.vue** - 使用 `.bg-gradient-primary`
- ✅ **ProductView.vue** - 使用 `.page-container`
- ✅ **OrderView.vue** - 使用 `.page-container`
- ✅ **CustomerView.vue** - 使用 `.page-container`
- ✅ **ProfileView.vue** - 使用 `.profile-container` 和 `.info-row`
- ✅ **AlbumView.vue** - 使用 `.album-card` 和 `.album-cover`
- ✅ **MarketingView.vue** - 使用 `.page-container`
- ✅ **PaymentDashboardView.vue** - 使用 `.card-border-top`

#### Layouts
- ✅ **MainLayout.vue** - 移除本地樣式，使用統一導航樣式

### 3. 使用 Quasar 內建樣式替代

以下原本自定義的樣式改用 Quasar 內建：

#### 文字樣式
- ~~`.text-h4/.text-h5/.text-h6`~~ → Quasar 內建
- ~~`.text-weight-bold`~~ → Quasar 內建
- ~~`.text-caption/.text-subtitle1/.text-subtitle2`~~ → Quasar 內建
- ~~`.text-grey-7/.text-grey-6`~~ → Quasar 內建
- ~~`.text-primary/.text-positive/.text-negative`~~ → Quasar 內建

#### 間距樣式
- ~~`.q-pa-md/.q-mb-md/.q-mt-md/.q-gutter-md`~~ → Quasar 內建

#### 布局樣式
- ~~`.row/.col/.q-col-gutter-md`~~ → Quasar 內建
- ~~`.flex/.flex-center/.items-center/.justify-between`~~ → Quasar 內建

### 4. 移除的重複樣式

#### 刪除的本地 scoped 樣式：
- `welcome-banner` (HomeView)
- `stat-card`, `stat-card-1~4` (HomeView)
- `product-management` (ProductView)
- `order-management` (OrderView)
- `customer-management` (CustomerView)
- `profile-page`, `info-row`, `info-label`, `info-value` (ProfileView)
- `album-card`, `album-cover` (AlbumView)
- `marketing-management` (MarketingView)

### 5. 保留的進階樣式系統

保留 `theme-system.scss` 作為進階設計系統，包含：
- CSS 變數系統（色彩、字體、間距等）
- 響應式設計
- 深色模式支援
- 動畫效果

## 使用指南

### 基本原則
1. **優先使用 Quasar 內建樣式**
   ```vue
   <div class="text-h5 text-weight-bold q-mb-md">
   ```

2. **使用統一的自定義類別**
   ```vue
   <q-card class="card-hover card-border-top border-primary">
   ```

3. **避免在組件中重複定義樣式**

### 常用組合範例

#### 頁面容器
```vue
<div class="page-container">
  <!-- 內容，max-width: 1400px，自動居中 -->
</div>
```

#### 統計卡片
```vue
<q-card class="card-hover card-border-top border-primary">
  <q-card-section>
    <!-- 內容 -->
  </q-card-section>
</q-card>
```

#### 漸層背景
```vue
<q-card class="bg-gradient-primary text-white">
  <!-- 內容 -->
</q-card>
```

#### 資訊顯示
```vue
<div class="info-row">
  <div class="info-label">標籤</div>
  <div class="info-value">值</div>
</div>
```

## 效益

### 程式碼減少
- 移除約 200+ 行重複的樣式定義
- 每個 View 檔案平均減少 10-30 行樣式程式碼

### 維護性提升
- 樣式集中管理，修改一處即可全域生效
- 命名規範統一，易於理解和使用
- 減少樣式衝突的可能性

### 效能優化
- 減少樣式重複加載
- 更好的快取利用率
- 減小打包體積

## 後續建議

1. **持續優化**
   - 監控新增頁面，確保使用統一樣式
   - 定期檢查是否有新的重複樣式模式

2. **文件維護**
   - 保持樣式指南文件更新
   - 在團隊中推廣統一樣式的使用

3. **擴展性**
   - 根據需求擴展 `app.scss`
   - 保持與 Quasar 設計語言的一致性
