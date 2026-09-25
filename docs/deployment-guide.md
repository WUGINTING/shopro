# 部署指南

## 環境需求

### 後端

| 軟體 | 最低版本 | 建議版本 |
|------|----------|----------|
| Java | 17 | 17 LTS |
| Maven | 3.6 | 3.9+ |
| MS SQL Server | 2017 | 2019+ |

### 前端

| 軟體 | 最低版本 | 建議版本 |
|------|----------|----------|
| Node.js | 20.19.0 | 22.x |
| npm | 10.x | 10.x |

---

## 本地開發環境

### 1. 複製專案

```bash
git clone <repository-url>
cd shopro
```

### 2. 後端設定

```bash
cd E-commerce

# 複製環境變數範本
cp .env.example .env

# 編輯 .env 檔案
```

所有機敏設定（資料庫密碼、JWT 金鑰、金流金鑰）都只從環境變數讀取，**不得寫入 `application.properties` 或提交到 git**。
完整清單見 [`E-commerce/.env.example`](../E-commerce/.env.example)。Spring Boot 不會自動讀取 `.env`，請先匯出：

```bash
set -a; source .env; set +a
```

| 變數 | 必填 | 說明 |
|------|------|------|
| `SPRING_PROFILES_ACTIVE` | 建議 | `dev`（admin/admin123 + 示範帳號）或 `prod`（缺少必要變數時啟動失敗） |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | 是 | SQL Server 連線 |
| `JWT_SECRET` | prod 必填 | base64、至少 32 bytes（`openssl rand -base64 32`）；未設定時使用暫時金鑰，重啟後需重新登入 |
| `ADMIN_INITIAL_PASSWORD` | 建議 | 使用者資料表為空時建立的管理員密碼；未設定會產生隨機密碼並只在啟動日誌顯示一次 |
| `DEMO_USERS` | 否 | `true` 時建立 manager/staff/customer 示範帳號（僅限開發） |
| `CORS_ALLOWED_ORIGINS` | prod 必填 | 後台與前台網址，逗號分隔 |
| `STOREFRONT_URL` | prod 必填 | 前台商城網址，綠界付款完成後導回 `/shop/order/success` |
| `ADMIN_STORE_URL` | prod 必填 | 後台 App 網址：在後台 App 顧客商城下單的訂單付款後導回 `/order/success`，會員 Email 驗證信的連結也指向 `/verify-email` |
| `FILE_UPLOAD_DIR` | 建議 | 上傳圖片存放目錄（預設 `./uploads/images`） |
| `SPRING_MAIL_HOST` / `SPRING_MAIL_PORT` / `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | 建議 | SMTP 設定；設定後才會寄送：訂單通知（成立、付款、出貨、取消、退款）、會員 Email 驗證、重設密碼、到貨通知、EDM 電子報。未設定時以上都不會寄出（EDM 會拒絕發送、到貨通知保留待寄） |
| `MAIL_FROM` / `STORE_NAME` | 建議 | 通知信寄件地址與顯示名稱（預設「遇日小舖」）；前台「聯絡我們」的留言也會轉寄到 `MAIL_FROM`（可直接回覆顧客） |
| `LOW_STOCK_THRESHOLD` | 否 | 規格庫存低於等於此數量時產生低庫存警示（預設 5；無規格商品使用各自的安全庫存） |
| `ORDER_UNPAID_TIMEOUT_HOURS` | 否 | 前台線上付款訂單自最後一次建立付款起，逾期未付款自動取消並歸還庫存的時數（預設 72；0 = 停用） |
| `ECPAY_EXPIRE_DAYS` | 否 | 綠界 ATM / 超商代碼繳費期限天數（預設 2），須短於上一項期限 |
| `ECPAY_MERCHANT_ID` / `ECPAY_HASH_KEY` / `ECPAY_HASH_IV` | prod 必填 | 綠界金鑰（未設定時使用綠界公開測試商店） |
| `ECPAY_NOTIFY_URL` | prod 必填 | 綠界伺服器付款通知網址，必須能從網際網路連線：`https://<api 網域>/api/payment-gateway/callback/ecpay` |
| `ECPAY_RETURN_URL` | prod 必填 | 後台商城付款後的「返回商店」網址 |
| `LINEPAY_CHANNEL_ID` / `LINEPAY_CHANNEL_SECRET` | 選用 | LINE Pay |
| `GOOGLE_CLIENT_ID` | 選用 | Google 登入的 OAuth Client ID（與後台 `VITE_GOOGLE_CLIENT_ID` 相同）；未設定時停用 Google 登入。後端會向 Google 驗證 token 簽章與 aud |

### 3. 啟動後端

```bash
# 方式一：使用 Maven Wrapper
./mvnw spring-boot:run

# 方式二：打包後運行
./mvnw clean package
java -jar target/E-commerce-0.0.1-SNAPSHOT.jar
```

### 4. 前端設定

```bash
cd frontend

# 複製環境變數範本
cp .env.example .env.local

# 安裝依賴
npm install

# 啟動開發伺服器
npm run dev
```

### 5. 存取應用程式

- 前端：http://localhost:5173
- 後端 API：http://localhost:8080/api
- Swagger：http://localhost:8080/swagger-ui.html

---

## 生產環境部署

### 後端部署

#### 1. 建置 JAR 檔

```bash
cd E-commerce
./mvnw clean package -DskipTests
```

#### 2. 設定環境變數

建立 `/etc/shopro/application.env`（權限設為 600，僅服務帳號可讀）：

```properties
SPRING_PROFILES_ACTIVE=prod

DB_URL=jdbc:sqlserver://db-server:1433;DatabaseName=e-commerce;encrypt=true
DB_USERNAME=shopro_user
DB_PASSWORD=<secure_password>

JWT_SECRET=<openssl rand -base64 32>
ADMIN_INITIAL_PASSWORD=<首次啟動的管理員密碼，登入後請立即修改>

CORS_ALLOWED_ORIGINS=https://admin.yourdomain.com,https://shop.yourdomain.com
STOREFRONT_URL=https://shop.yourdomain.com
ADMIN_STORE_URL=https://admin.yourdomain.com
FILE_UPLOAD_DIR=/var/lib/shopro/uploads/images

ECPAY_MERCHANT_ID=<production_merchant_id>
ECPAY_HASH_KEY=<production_hash_key>
ECPAY_HASH_IV=<production_hash_iv>
ECPAY_RETURN_URL=https://admin.yourdomain.com/payment/result
ECPAY_NOTIFY_URL=https://api.yourdomain.com/api/payment-gateway/callback/ecpay
```

`prod` profile 預設關閉 Swagger（需要時設 `API_DOCS_ENABLED=true`），並以 `ddl-auto=validate` 啟動；首次部署或升級需要變更資料表時，可暫時設 `JPA_DDL_AUTO=update`。

> 本版的資料表變更：`users.email_verified`、`member.marketing_opt_in` 欄位，以及新資料表 `coupon`。從舊版升級時請以 `JPA_DDL_AUTO=update` 啟動一次，之後再改回 `validate`。
> - 既有帳號的 `email_verified` 為 NULL，視為已驗證；新註冊的會員需點擊驗證信（或使用 Google 登入）後，才能在「我的訂單」看到以該 Email 下的訂單。
> - 既有會員的 `marketing_opt_in` 為 NULL，視為**未同意**接收 EDM；只有之後在結帳或會員中心勾選同意的會員會收到電子報。
> - 後台通知新增類型 `CONTACT_MESSAGE`。SQL Server 既有的 `admin_notifications.type` CHECK 約束不會被 `update` 修改，請執行 `E-commerce/database/migration/2026_09_admin_notification_contact_message.sql`，否則前台聯絡表單會送出失敗。

#### 3. 建立 systemd 服務

建立 `/etc/systemd/system/shopro.service`：

```ini
[Unit]
Description=Shopro E-Commerce Backend
After=network.target

[Service]
Type=simple
User=shopro
WorkingDirectory=/opt/shopro
EnvironmentFile=/etc/shopro/application.env
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m E-commerce-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 4. 啟動服務

```bash
sudo systemctl daemon-reload
sudo systemctl enable shopro
sudo systemctl start shopro
sudo systemctl status shopro
```

---

### 前端部署

#### 1. 建置生產版本

```bash
cd frontend

# 設定生產環境變數
echo "VITE_API_BASE_URL=https://api.yourdomain.com" > .env.production

# 建置
npm run build
```

#### 2. 部署靜態檔案

建置完成後，`dist/` 目錄包含所有靜態檔案。

**選項 A：Nginx**

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    root /var/www/shopro;
    index index.html;

    # 處理 SPA 路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 靜態資源快取
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

**選項 B：Vercel / Netlify**

建立 `vercel.json`：

```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

#### 3. 前台商城（frontend-official）

```bash
cd frontend-official
npm ci
npx quasar build          # 輸出至 dist/spa
```

前台預設呼叫同網域的 `/api`（見 `frontend-official/.env.production`），請讓網站伺服器反向代理到後端；路由為 history 模式，需要 SPA fallback：

```nginx
# 公開的結帳 / 訂單查詢 API 限流（每個 IP 每秒 2 次，允許短暫突增）
limit_req_zone $binary_remote_addr zone=storefront_orders:10m rate=2r/s;

server {
    listen 443 ssl;
    server_name shop.yourdomain.com;
    root /var/www/shopro-shop;          # dist/spa 的內容
    index index.html;

    client_max_body_size 12m;           # 與後端上傳上限一致

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/storefront/orders/ {
        limit_req zone=storefront_orders burst=10 nodelay;
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

上線前檢查：
- 後端 `STOREFRONT_URL` 設為前台網址（綠界付款完成後導回 `/shop/order/success`），`ADMIN_STORE_URL` 設為後台 App 網址。
- 設定 SMTP（`SPRING_MAIL_*`、`MAIL_FROM`），否則不會寄出訂單通知信與會員 Email 驗證信。
- `ECPAY_NOTIFY_URL` 必須是綠界伺服器能連到的 HTTPS 網址，否則訂單不會變成「已付款」。
- 後台「商店內容設定」填好聯絡 Email、電話、營業時間與地址（前台頁尾與商店介紹會顯示）。
- 需要自訂退換貨、隱私權、服務條款、常見問題內容時，建立對應 slug（`returns`、`privacy`、`terms`、`faq`）的自訂頁面；未建立時顯示內建預設內容。
- 後台「系統設定 → 上線檢查」會列出寄信、登入金鑰、綠界正式環境與通知網址、網址設定是否完成；「系統設定 → 運費設定」設定宅配 / 門市自取運費與免運門檻。

### 營運流程重點

| 情境 | 操作 |
|------|------|
| 線上付款訂單 | 綠界通知後自動變成「已付款」；未付款超過 `ORDER_UNPAID_TIMEOUT_HOURS` 自動取消並歸還庫存與優惠券。顧客可在訂單查詢頁重新付款或自行取消 |
| 貨到付款訂單 | 待付款狀態即可出貨；出貨後為「處理中」，物流標記「已送達」後自動「已完成」 |
| 出貨 | 訂單詳情「新增物流」填物流公司與單號；狀態為已出貨時寄出貨通知給顧客，顧客在訂單查詢頁可看到單號 |
| 退款 | 系統**不會自動呼叫綠界退款**：信用卡請在綠界廠商後台退刷、ATM/超商以匯款退還，完成後在訂單「登記退款」（可部分退款、可選擇歸還庫存）。全額退款後訂單變「已退款」、扣回會員累計消費並通知顧客 |
| 已付款訂單取消 | 不可直接取消，請使用「登記退款」 |
| 折扣 | 促銷活動、優惠券、會員等級折扣取折抵最多的一項（不累加）；免運可併用。優惠券於下單扣次數、訂單取消歸還 |
| 缺貨 | 顧客可登記到貨通知；補貨（含規格補貨）後自動寄信 |
| 電子報（EDM） | 只寄給同意接收的會員，信中附退訂連結 |
| 報表 | 後台「報表統計」依期間統計；訂單列表可匯出 CSV（經理以上） |

---

### Docker 部署

#### Dockerfile (後端)

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/E-commerce-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "app.jar"]
```

#### Dockerfile (前端)

```dockerfile
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./E-commerce
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:sqlserver://db:1433;DatabaseName=ecommerce
      - DB_USERNAME=sa
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - db

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend

  db:
    image: mcr.microsoft.com/mssql/server:2019-latest
    environment:
      - ACCEPT_EULA=Y
      - SA_PASSWORD=${DB_PASSWORD}
    ports:
      - "1433:1433"
    volumes:
      - sqlserver_data:/var/opt/mssql

volumes:
  sqlserver_data:
```

---

## SSL/HTTPS 設定

### 使用 Let's Encrypt

```bash
# 安裝 certbot
sudo apt install certbot python3-certbot-nginx

# 取得憑證
sudo certbot --nginx -d yourdomain.com -d api.yourdomain.com

# 自動更新
sudo certbot renew --dry-run
```

---

## 監控與日誌

### 日誌設定

在 `application-prod.properties` 中：

```properties
logging.file.name=/var/log/shopro/application.log
logging.level.root=WARN
logging.level.com.info.ecommerce=INFO
```

### 日誌輪替

建立 `/etc/logrotate.d/shopro`：

```
/var/log/shopro/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 shopro shopro
}
```

### 健康檢查

```bash
# 檢查後端狀態
curl http://localhost:8080/actuator/health

# 檢查前端
curl -I http://localhost
```

---

## 備份策略

### 資料庫備份

```sql
-- 完整備份
BACKUP DATABASE ecommerce
TO DISK = '/backup/ecommerce_full.bak'
WITH FORMAT, COMPRESSION;

-- 差異備份
BACKUP DATABASE ecommerce
TO DISK = '/backup/ecommerce_diff.bak'
WITH DIFFERENTIAL, COMPRESSION;
```

### 自動備份腳本

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/backup/shopro

# 備份資料庫
sqlcmd -S localhost -U sa -P $DB_PASSWORD \
  -Q "BACKUP DATABASE ecommerce TO DISK='$BACKUP_DIR/db_$DATE.bak' WITH COMPRESSION"

# 備份上傳檔案
tar -czf $BACKUP_DIR/uploads_$DATE.tar.gz /opt/shopro/uploads

# 清理 30 天前的備份
find $BACKUP_DIR -mtime +30 -delete
```

---

## 故障排除

### 常見問題

**1. 後端無法連接資料庫**

```bash
# 檢查資料庫連線
telnet db-server 1433

# 檢查環境變數
printenv | grep DB_
```

**2. 前端 API 請求失敗**

```bash
# 檢查 CORS 設定
curl -I -X OPTIONS http://api.yourdomain.com/api/products

# 檢查代理設定
nginx -t
```

**3. JWT Token 問題**

```bash
# 檢查 Token 有效性
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/users/me
```

### 日誌檢視

```bash
# 後端日誌
journalctl -u shopro -f

# Nginx 日誌
tail -f /var/log/nginx/error.log
```
