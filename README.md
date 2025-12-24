# Shopro E-Commerce 電商平台

全棧電商管理系統，使用 Spring Boot + Vue 3 構建

## 專案概述

Shopro 是一個完整的電商管理平台，提供商店管理、商品管理、訂單處理、客戶關係管理（CRM）等全方位功能。

## 技術架構

### 後端 (Backend)
- **Spring Boot 3.4.1** - Java 企業級框架
- **Java 17** - 長期支援版本
- **Spring Data JPA** - 資料持久層
- **MS SQL Server** - 關聯式資料庫
- **Swagger/OpenAPI** - API 文檔

### 前端 (Frontend)
- **Vue 3** - 漸進式 JavaScript 框架
- **TypeScript** - 類型安全
- **Vite** - 快速建置工具
- **Element Plus** - UI 組件庫
- **Vue Router** - 路由管理
- **Pinia** - 狀態管理
- **Axios** - HTTP 客戶端

## 專案結構

```
shopro/
├── E-commerce/              # Spring Boot 後端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/info/ecommerce/
│   │   │   │   ├── common/          # 通用工具、異常處理
│   │   │   │   ├── config/          # 配置類
│   │   │   │   └── modules/         # 業務模組
│   │   │   │       ├── product/     # 商品管理
│   │   │   │       ├── order/       # 訂單管理
│   │   │   │       ├── crm/         # 顧客經營
│   │   │   │       ├── store/       # 商店設定
│   │   │   │       ├── marketing/   # 行銷活動
│   │   │   │       ├── bulletin/    # 布告欄與部落格
│   │   │   │       ├── statistics/  # 統計資訊
│   │   │   │       └── ads/         # 廣告與追蹤
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── frontend/                # Vue 3 前端
│   ├── src/
│   │   ├── api/            # API 服務層
│   │   ├── assets/         # 靜態資源
│   │   ├── components/     # Vue 組件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 狀態管理
│   │   ├── views/          # 頁面視圖
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   └── README.md
└── README.md               # 本文件
```

## 核心功能模組

### 1. 開店支援與設計
- 視覺設計：商店佈景、首頁設計區塊、彈跳廣告
- 基礎設定：多國語系、SEO、電子發票、多國貨幣
- 管理權限：多帳號管理、員工打卡、小結與關帳

### 2. 商品管理
- 規模限制：商品數（500~10,000項）、分類數（100~600個）
- 銷售模式：預購商品、票券商品、訂閱購、門市限定
- 規格功能：多規格多價格、規格圖切換、數量限制
- 效率工具：批次更新/匯入/刪除、庫存警示、倉儲管理

### 3. 訂單管理
- 自動化與批次：訂單匯出、批次更新、批次列印
- 通知系統：簡訊提醒、門市取貨通知、訂單問答
- 彈性處理：黑名單、訂單暫存、分批/合併出貨
- O2O 功能：門市取貨付款、寄存訂單、跨店領取

### 4. 行銷活動
- 促銷工具：優惠券、特價、加價購、隱形賣場
- 組合玩法：全館活動、任選活動、滿額購、組合活動
- 分潤管理：推薦分潤管理

### 5. 顧客經營 (CRM)
- 會員體系：會員管理、會員分級、自訂頁面
- 互動提醒：EDM 電子報、購物車提醒、群組訊息
- 獎勵制度：現金積點、會員專屬價格、生日禮

### 6. 布告欄與部落格
- 公告管理：CRUD、分類管理、置頂、優先等級
- 排程與狀態：排程發布、自動下架、狀態管理
- 互動追蹤：已讀紀錄、瀏覽次數統計
- 部落格功能：文章管理、標籤、SEO、封面圖片

### 7. 統計資訊
- 報表系統：庫存警示、訂單報表、員工業績、銷售統計、顧客統計

### 8. 廣告與追蹤服務
- Google 生態：GA4、Google Ads、GTM、動態再行銷
- Meta 生態：FB Business Extension、FB 廣告像素
- 其他通路：Bing、Yahoo 廣告

## 快速開始

### 環境需求

- **Java**: JDK 17 或以上
- **Node.js**: v20 或以上
- **Maven**: 3.6 或以上
- **資料庫**: MS SQL Server

### 後端啟動

```bash
cd E-commerce
./mvnw spring-boot:run
```

後端服務將在 `http://localhost:8080` 運行

### 前端啟動

```bash
cd frontend
npm install
npm run dev
```

前端應用將在 `http://localhost:5173` 運行

### 建置生產版本

**後端建置:**
```bash
cd E-commerce
./mvnw clean package
```

**前端建置:**
```bash
cd frontend
npm run build
```

## API 文檔

後端啟動後，可訪問 Swagger UI 查看 API 文檔：
```
http://localhost:8080/swagger-ui.html
```

## 開發指南

### 後端開發
- 遵循 Spring Boot 最佳實踐
- 使用 JPA 進行資料持久化
- 統一異常處理
- RESTful API 設計

### 前端開發
- Vue 3 Composition API
- TypeScript 類型安全
- Element Plus 組件庫
- 統一的 API 服務層
- Pinia 狀態管理

## 截圖

### 首頁
![Homepage](https://github.com/user-attachments/assets/edcccb21-84ff-420e-b5f8-ea7137c3a8d1)

### 商品管理
![Products](https://github.com/user-attachments/assets/342337e4-5ee2-4be1-b440-fab625783aa8)

### 訂單管理
![Orders](https://github.com/user-attachments/assets/2d32f583-a708-420e-bb8d-be872be50ee0)

### 顧客管理 (CRM)
![Customers](https://github.com/user-attachments/assets/95135b1d-9d6b-4f7c-972d-a2517dae7d5c)

## 更多資訊

- [後端文檔](./E-commerce/README.md)
- [前端文檔](./frontend/README.md)
- [產品模組 API](./產品模組API文檔.md)
- [訂單管理 API](./訂單管理模組API文檔.md)
- [CRM 模組 API](./CRM_MODULE_API_DOCUMENTATION.md)

## 授權

MIT License

## 主要變更：

- 新增 Vue 3 + TypeScript 前端應用
- 實現商品、訂單、顧客管理界面
- 配置前後端 API 連接
- 使用 Element Plus UI 組件庫