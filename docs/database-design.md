# 資料庫設計

## 概述

系統使用 MS SQL Server 作為主要資料庫，透過 Spring Data JPA 進行資料存取。

## 實體關係圖

```
┌─────────────────┐     ┌─────────────────┐
│      User       │     │   ProductCategory│
├─────────────────┤     ├─────────────────┤
│ id              │     │ id              │
│ username        │     │ name            │
│ email           │     │ parent_id (FK)  │◄─┐
│ password        │     │ icon            │  │ 自我參照
│ role            │     │ enabled         │──┘
│ created_at      │     └────────┬────────┘
└────────┬────────┘              │
         │                       │ 1:N
         │                       ▼
         │              ┌─────────────────┐
         │              │    Product      │
         │              ├─────────────────┤
         │              │ id              │
         │              │ name            │
         │              │ description     │
         │              │ base_price      │
         │              │ sale_price      │
         │              │ stock           │
         │              │ status          │
         │              │ sales_mode      │
         │              │ category_id (FK)│
         │              └──────┬──────────┘
         │                     │
         │    ┌────────────────┼────────────────┐
         │    │                │                │
         │    ▼                ▼                ▼
         │  ┌────────┐  ┌───────────┐  ┌────────────┐
         │  │ Spec   │  │  Image    │  │ DescBlock  │
         │  └────────┘  └───────────┘  └────────────┘
         │
         │ 1:N (as customer)
         ▼
┌─────────────────┐     ┌─────────────────┐
│     Order       │     │   OrderItem     │
├─────────────────┤     ├─────────────────┤
│ id              │     │ id              │
│ order_number    │     │ order_id (FK)   │
│ customer_id (FK)│1:N  │ product_id (FK) │
│ status          │────►│ spec_id (FK)    │
│ total_amount    │     │ quantity        │
│ pickup_type     │     │ unit_price      │
│ store_id        │     └─────────────────┘
└────────┬────────┘
         │
    ┌────┴────┬──────────┐
    │         │          │
    ▼         ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐
│Payment │ │Shipment│ │History │
└────────┘ └────────┘ └────────┘
```

---

## 核心實體

### User (使用者)

```sql
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    phone NVARCHAR(20),
    avatar_url NVARCHAR(500),
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

**角色列舉：**
- ADMIN
- MANAGER
- STAFF
- CUSTOMER

---

### ProductCategory (商品分類)

```sql
CREATE TABLE product_categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    parent_id BIGINT REFERENCES product_categories(id),
    icon NVARCHAR(50),
    sort_order INT DEFAULT 0,
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_categories_parent ON product_categories(parent_id);
CREATE INDEX idx_categories_enabled ON product_categories(enabled);
```

---

### Product (商品)

```sql
CREATE TABLE products (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    base_price DECIMAL(10,2) NOT NULL,
    sale_price DECIMAL(10,2),
    cost_price DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    sales_mode NVARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    category_id BIGINT REFERENCES product_categories(id),
    sku NVARCHAR(50),
    weight DECIMAL(10,2),
    seo_title NVARCHAR(200),
    seo_description NVARCHAR(500),
    seo_keywords NVARCHAR(200),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_sku ON products(sku);
```

**狀態列舉：**
- DRAFT - 草稿
- ACTIVE - 上架中
- INACTIVE - 已下架

**銷售模式：**
- NORMAL - 一般商品
- PRE_ORDER - 預購
- TICKET - 票券
- SUBSCRIPTION - 訂閱
- STORE_ONLY - 門市限定

---

### ProductSpecification (商品規格)

```sql
CREATE TABLE product_specifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    spec_name NVARCHAR(200) NOT NULL,
    sku NVARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0,
    image NVARCHAR(500),
    weight DECIMAL(10,2),
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_specs_product ON product_specifications(product_id);
CREATE INDEX idx_specs_sku ON product_specifications(sku);
```

---

### ProductImage (商品圖片)

```sql
CREATE TABLE product_images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    image_url NVARCHAR(500) NOT NULL,
    album_image_id BIGINT REFERENCES album_images(id),
    sort_order INT DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_product_images_product ON product_images(product_id);
```

---

### Order (訂單)

```sql
CREATE TABLE orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_number NVARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES users(id),
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    shipping_fee DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    pickup_type NVARCHAR(20) NOT NULL DEFAULT 'DELIVERY',
    store_id BIGINT REFERENCES stores(id),
    shipping_address NVARCHAR(500),
    recipient_name NVARCHAR(50),
    recipient_phone NVARCHAR(20),
    note NVARCHAR(500),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_created ON orders(created_at);
```

**訂單狀態：**
- PENDING - 待確認
- CONFIRMED - 已確認
- PROCESSING - 處理中
- SHIPPED - 已出貨
- DELIVERED - 已送達
- COMPLETED - 已完成
- CANCELLED - 已取消
- RETURNED - 已退貨

**取貨方式：**
- DELIVERY - 宅配
- STORE_PICKUP - 門市取貨
- CROSS_STORE - 跨店取貨

---

### OrderItem (訂單項目)

```sql
CREATE TABLE order_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    specification_id BIGINT REFERENCES product_specifications(id),
    product_name NVARCHAR(200) NOT NULL,
    spec_name NVARCHAR(200),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
```

---

### Member (會員)

```sql
CREATE TABLE members (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id),
    level_id BIGINT REFERENCES member_levels(id),
    total_points INT NOT NULL DEFAULT 0,
    available_points INT NOT NULL DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0,
    order_count INT DEFAULT 0,
    birthday DATE,
    gender NVARCHAR(10),
    status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_members_level ON members(level_id);
CREATE INDEX idx_members_status ON members(status);
```

---

### MemberLevel (會員等級)

```sql
CREATE TABLE member_levels (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    min_points INT NOT NULL DEFAULT 0,
    discount_rate DECIMAL(3,2) DEFAULT 0,
    point_multiplier INT DEFAULT 1,
    description NVARCHAR(200),
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
```

---

### PointRecord (積點記錄)

```sql
CREATE TABLE point_records (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id),
    points INT NOT NULL,
    type NVARCHAR(20) NOT NULL,
    reason NVARCHAR(200),
    order_id BIGINT REFERENCES orders(id),
    expired_at DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_points_member ON point_records(member_id);
CREATE INDEX idx_points_type ON point_records(type);
```

**積點類型：**
- EARN - 獲得
- SPEND - 消費
- EXPIRE - 過期
- ADJUST - 調整

---

### PaymentGatewayTransaction (支付交易)

```sql
CREATE TABLE payment_gateway_transactions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    transaction_id NVARCHAR(100) NOT NULL UNIQUE,
    gateway NVARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    gateway_transaction_id NVARCHAR(100),
    gateway_response NVARCHAR(MAX),
    paid_at DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_payment_order ON payment_gateway_transactions(order_id);
CREATE INDEX idx_payment_transaction ON payment_gateway_transactions(transaction_id);
CREATE INDEX idx_payment_status ON payment_gateway_transactions(status);
```

---

### Album (相冊)

```sql
CREATE TABLE albums (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE TABLE album_images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    album_id BIGINT NOT NULL REFERENCES albums(id),
    image_url NVARCHAR(500) NOT NULL,
    title NVARCHAR(200),
    sort_order INT DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_album_images_album ON album_images(album_id);
```

---

### Store (門市)

```sql
CREATE TABLE stores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    address NVARCHAR(300),
    phone NVARCHAR(20),
    opening_hours NVARCHAR(200),
    warehouse_id BIGINT REFERENCES warehouses(id),
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
```

---

### OperationLog (操作日誌)

```sql
CREATE TABLE operation_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    username NVARCHAR(50),
    operation_type NVARCHAR(50) NOT NULL,
    module NVARCHAR(50),
    description NVARCHAR(500),
    request_url NVARCHAR(500),
    request_method NVARCHAR(10),
    request_params NVARCHAR(MAX),
    ip_address NVARCHAR(50),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_logs_user ON operation_logs(user_id);
CREATE INDEX idx_logs_type ON operation_logs(operation_type);
CREATE INDEX idx_logs_created ON operation_logs(created_at);
```

---

## 索引策略

### 主要索引

| 表格 | 索引 | 用途 |
|------|------|------|
| users | email | 登入查詢 |
| products | status, category_id | 列表篩選 |
| orders | customer_id, status, created_at | 訂單查詢 |
| members | level_id, status | 會員管理 |

### 建議新增索引

```sql
-- 商品搜尋優化
CREATE INDEX idx_products_name ON products(name);

-- 訂單時間區間查詢
CREATE INDEX idx_orders_date_range ON orders(created_at, status);

-- 積點過期查詢
CREATE INDEX idx_points_expired ON point_records(expired_at)
    WHERE expired_at IS NOT NULL;
```

---

## 資料初始化

### 預設使用者

```sql
INSERT INTO users (username, email, password, role)
VALUES ('admin', 'admin@shopro.com', '<bcrypt_hash>', 'ADMIN');
```

### 預設會員等級

```sql
INSERT INTO member_levels (name, min_points, discount_rate, point_multiplier)
VALUES
    ('一般會員', 0, 0, 1),
    ('銀卡會員', 1000, 0.05, 1),
    ('金卡會員', 5000, 0.10, 2),
    ('鑽石會員', 10000, 0.15, 3);
```
