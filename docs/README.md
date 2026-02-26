# Shopro 電子商務平台 - 技術文檔

歡迎閱讀 Shopro 電子商務平台技術文檔。本文檔提供完整的系統架構、開發指南和部署說明。

## 文檔目錄

| 文檔 | 說明 |
|------|------|
| [架構概覽](./architecture-overview.md) | 系統整體架構、技術棧、目錄結構 |
| [後端架構](./backend-architecture.md) | Spring Boot 後端模組詳細說明 |
| [前端架構](./frontend-architecture.md) | Vue 3 前端架構與組件說明 |
| [API 文檔](./api-reference.md) | RESTful API 端點參考 |
| [資料庫設計](./database-design.md) | 實體關係與資料模型 |
| [部署指南](./deployment-guide.md) | 環境配置與部署步驟 |
| [開發指南](./development-guide.md) | 開發規範與最佳實踐 |

## 快速開始

### 環境需求

**後端：**
- Java 17+
- Maven 3.6+
- MS SQL Server

**前端：**
- Node.js 20.19.0+ 或 22.12.0+
- npm 或 yarn

### 啟動開發環境

```bash
# 後端
cd E-commerce
./mvnw spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

### 存取應用程式

- 前端應用：http://localhost:5173
- 後端 API：http://localhost:8080/api
- Swagger 文檔：http://localhost:8080/swagger-ui.html

## 專案概述

Shopro 是一個功能完整的電子商務管理平台，採用前後端分離架構：

- **後端**：Spring Boot 3.4.1 + Java 17
- **前端**：Vue 3 + TypeScript + Quasar
- **資料庫**：MS SQL Server

### 核心功能

- 商品管理（支援 500~10,000 項商品）
- 訂單處理（含 O2O 門市取貨）
- 顧客關係管理（CRM）
- 支付整合（綠界 ECPay、LINE Pay）
- 行銷活動管理
- 統計報表

## 版本資訊

- **版本**：0.0.1-SNAPSHOT
- **最後更新**：2026-02
- **授權**：MIT License
