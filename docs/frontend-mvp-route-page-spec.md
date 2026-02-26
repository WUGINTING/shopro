# 最小可賣前台 MVP：路由與頁面規格

## 1) 目標與邊界
- 目標：在最短時間上線可下單、可追蹤轉化、可建立信任、可讓會員查單的前台。
- 技術邊界：以現有 Vue 3 + Vue Router（建議延續 Composition API + `<script setup>` + TypeScript）為基礎。
- 本版不做：完整會員地址簿、完整優惠券中心、複雜推薦系統。

## 2) MVP 路由清單（V1）

| 路由 | 頁面 | 是否需登入 | 核心目標 |
|---|---|---|---|
| `/` | 首頁 Home | 否 | 導流到商品、建立品牌與信任 |
| `/products` | 商品列表 | 否 | 搜尋/篩選商品、進入商品詳情 |
| `/products/:id` | 商品詳情 | 否 | 完整商品資訊、加入購物車 |
| `/cart` | 購物車 | 否 | 檢視品項與金額、前往結帳 |
| `/checkout` | 結帳 | 建議是（訪客可先填資料） | 建立訂單、建立付款請求 |
| `/order/success` | 訂單完成頁 | 否 | 顯示結果、回傳購買事件 |
| `/brand` | 品牌介紹 | 否 | 補齊品牌信任內容 |
| `/contact` | 聯絡我們 | 否 | 提供聯絡管道與客服期待 |
| `/policy/returns` | 退換貨政策 | 否 | 降低下單風險感 |
| `/policy/payment-shipping` | 付款與物流說明 | 否 | 降低付款/配送疑慮 |
| `/account` | 會員中心首頁 | 是 | 會員資料總覽與入口 |
| `/account/orders` | 我的訂單 | 是 | 查詢歷史訂單與狀態 |
| `/account/orders/:id` | 訂單詳情 | 是 | 查看訂單明細與物流資訊 |
| `/account/benefits` | 我的優惠/會員等級 | 是 | 顯示可用權益與等級資訊 |
| `/account/addresses` | 地址管理 | 是 | 管理常用收件地址（V1 可先單地址） |

## 3) 各頁最小功能規格

### A. 首頁 `/`
- 內容區塊：主視覺、主打商品、品牌信任區（退換貨/付款物流/客服入口）。
- CTA：前往商品列表、前往品牌頁。
- 埋點：`view_home`、`click_home_cta`。

### B. 商品列表 `/products`
- 功能：分類篩選、關鍵字搜尋、分頁。
- 埋點：`view_product_list`、`select_product`。

### C. 商品詳情 `/products/:id`
- 功能：商品主圖/規格/描述、數量選擇、加入購物車。
- 埋點：`view_product_detail`、`add_to_cart`（必要）。

### D. 購物車 `/cart`
- 功能：品項增減/移除、金額小計、前往結帳。
- 埋點：`view_cart`、`begin_checkout`（點擊前往結帳時）。

### E. 結帳 `/checkout`
- 功能：收件資料、付款方式、物流方式、訂單確認。
- 埋點：`checkout_submit`、`payment_create_request`。

### F. 訂單完成頁 `/order/success`
- 功能：顯示訂單號、金額、付款狀態、回首頁/查訂單按鈕。
- 埋點：`purchase`（必要，帶 `orderNumber`、`amount`、`items`）。

### G. 信任頁面（`/brand` `/contact` `/policy/*`）
- 內容：品牌故事、客服聯絡、退換貨政策、付款/物流說明。
- 埋點：`view_brand`、`view_contact`、`view_policy`。

### H. 會員中心（`/account/*`）
- 我的訂單：列表、訂單狀態、可進詳情。
- 我的優惠/會員等級：顯示點數餘額、會員等級、可用權益。
- 地址管理：V1 先做 1~3 筆地址（可設預設地址）。
- 埋點：`view_account`、`view_my_orders`、`view_my_benefits`、`manage_address`。

## 4) UTM 與事件追蹤規格（前端先埋）

### UTM
- 首次進站保存：`utm_source` `utm_medium` `utm_campaign` `utm_term` `utm_content`。
- 保存策略：`localStorage` + 30 天有效期。
- 每次重要事件都帶入 UTM 欄位。

### 必要轉化事件（V1）
- `add_to_cart`
- `begin_checkout`
- `purchase`

### 建議事件欄位
- 通用：`event_time`、`user_id`(可空)、`session_id`、`page_path`、`referrer`、UTM 全欄位。
- 商品/訂單：`product_id`、`product_name`、`quantity`、`price`、`order_number`、`order_amount`。

## 5) 可直接重用後端 API（先用）

### 商品與分類（可公開）
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/search`
- `GET /api/products/category/{categoryId}`
- `GET /api/product-categories`
- `GET /api/product-categories/top`
- `GET /api/product-categories/{parentId}/children`
- `GET /api/product-specifications/product/{productId}`
- `GET /api/product-images/product/{productId}`

### 訂單與會員（登入後）
- `POST /api/orders`（建立訂單）
- `GET /api/orders/my`（我的訂單）
- `GET /api/orders/{id}`（訂單詳情）
- `GET /api/orders/shipments/order/{orderId}`（物流資訊）
- `GET /api/auth/profile`、`PUT /api/auth/profile`（會員基本資料）

### 付款與物流設定（可用於結帳頁顯示）
- `GET /api/system/payment-config/enabled`
- `GET /api/system/shipping-config/enabled`
- `POST /api/payment-gateway/create?gateway=...`
- `GET /api/payment-gateway/query/{gateway}/{transactionId}`

### 品牌/文案內容（可重用）
- `GET /api/crm/custom-pages/slug/{slug}`（可放品牌、政策頁內容）

## 6) 需要補齊或調整的 API/權限

### 必補（MVP 建議）
- 地址管理 API（目前無明確地址簿）
  - 建議：`/api/customer/addresses` CRUD
- 我的優惠 API（目前缺「會員可用優惠」查詢）
  - 建議：`GET /api/customer/benefits`（整合點數、會員等級、可領優惠）
- 會員中心安全化查詢（避免前台用 `memberId` 直打）
  - 建議：提供 `GET /api/customer/me`、`GET /api/customer/points`、`GET /api/customer/rewards`

### 權限/安全設定調整（非常重要）
- 現況多數前台內容 API 仍走 `anyRequest().authenticated()`；若要公開前台，需在 SecurityConfig 放行：
  - `/api/store/**`
  - `/api/homepage-blocks/**`
  - `/api/popup-ads/active`
  - `/api/crm/custom-pages/slug/**`
- 建議新增 customer 專用安全路由，避免前台直接使用後台管理端點。

## 7) 實作優先順序（兩週版）
- P0：`/` `/products` `/products/:id` `/cart` `/checkout` `/order/success`
- P0：UTM + `add_to_cart` / `begin_checkout` / `purchase`
- P0：`/policy/returns` `/policy/payment-shipping` `/contact` `/brand`
- P1：`/account/orders` `/account/orders/:id` `/account/benefits`
- P1：`/account/addresses`（若後端地址 API 到位）

## 8) 驗收標準（MVP）
- 使用者可從首頁到商品到結帳完成一筆訂單。
- 三個轉化事件可在前端 log/上報中被完整看到（含 UTM）。
- 前台可直接看到退換貨、聯絡方式、品牌介紹、付款物流說明。
- 登入會員可查我的訂單與訂單詳情。
