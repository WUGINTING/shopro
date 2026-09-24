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
| `FILE_UPLOAD_DIR` | 建議 | 上傳圖片存放目錄（預設 `./uploads/images`） |
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
FILE_UPLOAD_DIR=/var/lib/shopro/uploads/images

ECPAY_MERCHANT_ID=<production_merchant_id>
ECPAY_HASH_KEY=<production_hash_key>
ECPAY_HASH_IV=<production_hash_iv>
ECPAY_RETURN_URL=https://admin.yourdomain.com/payment/result
ECPAY_NOTIFY_URL=https://api.yourdomain.com/api/payment-gateway/callback/ecpay
```

`prod` profile 預設關閉 Swagger（需要時設 `API_DOCS_ENABLED=true`），並以 `ddl-auto=validate` 啟動；首次部署或升級需要變更資料表時，可暫時設 `JPA_DDL_AUTO=update`。

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
