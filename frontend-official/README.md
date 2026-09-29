# Official Website - 官方網站

本專案包含兩個網站：

1. **官方首頁** - 伸遠國際官網
2. **購物車網站** - 遇日小舖 電商平台

## 🚀 技術堆疊

- **框架**: Quasar Framework (Vue 3)
- **構建工具**: Vite
- **樣式**: SCSS
- **圖標**: Iconify
- **Node.js**: v20.x

## 📁 專案結構

```
official/
├── src/
│   ├── components/          # 共用元件
│   │   └── shop/           # 購物車專用元件
│   │       ├── ProductCard.vue
│   │       ├── CouponSection.vue
│   │       └── PopupAd.vue
│   ├── css/                # 樣式檔案
│   │   └── shop/           # 購物車專用樣式
│   │       ├── variables.scss
│   │       └── common.scss
│   ├── layouts/            # 頁面框架
│   │   ├── MainLayout.vue       # 官網框架
│   │   └── MainLayoutShop.vue   # 購物車框架
│   ├── pages/              # 頁面元件
│   │   ├── IndexPage.vue        # 官網首頁
│   │   ├── AboutPage.vue
│   │   ├── MenuPage.vue
│   │   ├── ContactPage.vue
│   │   └── shop/                # 購物車頁面
│   │       └── IndexPage.vue
│   └── router/             # 路由設定
│       ├── index.js
│       └── routes.js
├── public/                 # 靜態資源
└── 參考資料/              # 參考範例和素材
```

## 🎯 網站路由

### 官方網站 (MainLayout)

- `/` - 首頁
- `/about` - 關於我們
- `/menu` - 菜單
- `/cases` - 案例分享
- `/contact` - 聯絡我們
- `/faq` - 常見問題

### 購物車網站 (MainLayoutShop)

- `/shop` - 購物車首頁 (遇日小舖)
- `/shop/introduce` - 商店介紹
- `/shop/news`、`/shop/news/:id` - 最新消息
- `/shop/product/list`、`/shop/product/:id` - 商品列表 / 商品詳情
- `/shop/checkout` - 結帳（訪客結帳，呼叫 `POST /api/storefront/orders/checkout`）
- `/shop/order/success` - 訂單完成頁
- `/shop/order/lookup` - 訂單查詢（訂單編號 + 電子郵件）

> 結帳金額、運費與庫存一律以後端 `POST /api/storefront/orders/quote` 試算結果為準；
> 選擇「線上付款」時，後端建立綠界付款並回傳付款網址，前端以 POST 表單導向綠界。

## 🛠️ 安裝與啟動

### 環境需求

- Node.js v20.x 或更高版本
- npm 或 yarn

### 安裝依賴

```bash
npm install
# 或
yarn install
```

### 開發模式

```bash
# 使用 npm
npm run dev

# 使用 yarn
yarn dev

# 或直接執行 quasar
npx quasar dev
```

開發伺服器啟動後，訪問：

- **官方網站**: http://localhost:9000/
- **購物車網站**: http://localhost:9000/shop

### 建置生產版本

```bash
npm run build
# 或
yarn build
```

## 🎨 購物車網站特色

### Gold 主題設計

- 主色調: `#c5a059` (金色)
- 響應式設計，支援桌面版與手機版
- 現代化的電商界面

### 核心功能

- ✅ 彈跳廣告系統
- ✅ 優惠券領取功能
- ✅ 商品展示（含標籤：HOT/NEW/預購）
- ✅ 購物車計數器
- ✅ 商品加入購物車（含動畫提示）
- ✅ 橫向滾動優惠券區
- ✅ 響應式導航選單

### 元件說明

1. **ProductCard** - 商品卡片元件

   - 支援價格、原價、圖片、標籤顯示
   - 加入購物車按鈕

2. **CouponSection** - 優惠券區塊

   - 橫向滾動顯示多個優惠券
   - 一鍵領取功能

3. **PopupAd** - 彈跳廣告
   - 自動延遲顯示
   - 可自訂內容和圖片

## 🔧 常見問題

### PowerShell 執行政策問題

如果遇到 "無法載入檔案，因為這個系統上已停用指令碼執行" 錯誤，請使用以下方式之一：

```bash
# 方式 1: 使用 npx
npx quasar dev

# 方式 2: 直接使用 node
node node_modules/.bin/quasar dev

# 方式 3: 修改執行政策（需要管理員權限）
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### SASS Import 路徑問題

- 在 Vue 的 `<style>` 標籤中使用 `~@/` 前綴來導入樣式
- 例如: `@import '~@/css/shop/variables.scss';`

## 📝 開發指南

### 新增購物車頁面

1. 在 `src/pages/shop/` 建立新的 Vue 檔案
2. 在 `src/router/routes.js` 的 `/shop` 路由下新增子路由
3. 使用 `MainLayoutShop` 作為父層 layout

### 自訂主題色彩

編輯 `src/css/shop/variables.scss` 檔案來修改購物車主題：

```scss
$shop-primary: #c5a059; // 主色
$shop-primary-dark: #b08d4b; // 主色深色版
$shop-danger: #e74c3c; // 危險色/價格色
$shop-success: #2ecc71; // 成功色
```

## 🚧 後續開發建議

- [ ] 整合 Pinia 狀態管理（購物車狀態）
- [ ] 新增商品詳情頁面
- [ ] 實作購物車結帳流程
- [ ] 連接後端 API
- [ ] 會員登入/註冊系統
- [ ] 訂單管理功能
- [ ] 支付整合

## 📧 聯絡資訊

- GitLab: https://gitlab.com/smileyum/website-design/official.git

## 📄 授權

本專案為私有專案，未經授權不得使用。
