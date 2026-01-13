# 電商系統角色權限規劃

## 角色定義

| 角色 | 英文代碼 | 說明 |
|-----|---------|------|
| 管理員 | ADMIN | 最高權限，可管理系統所有功能 |
| 經理 | MANAGER | 營運管理，負責銷售與商品管理 |
| 員工 | STAFF | 日常操作，處理訂單與庫存 |
| 客戶 | CUSTOMER | 前台購物，瀏覽商品與下單 |

---

## 角色權限矩陣

| 功能模組 | 管理員 (ADMIN) | 經理 (MANAGER) | 員工 (STAFF) | 客戶 (CUSTOMER) |
|---------|:-------------:|:-------------:|:------------:|:--------------:|
| **儀表板** | ✅ 完整統計 | ✅ 銷售統計 | ✅ 基本統計 | ❌ |
| **用戶管理** | ✅ CRUD 全部 | ✅ 查看/編輯 | ❌ | ❌ |
| **角色權限管理** | ✅ | ❌ | ❌ | ❌ |
| **商品管理** | ✅ CRUD | ✅ CRUD | ✅ 查看/編輯 | ❌ |
| **分類管理** | ✅ CRUD | ✅ CRUD | ✅ 查看 | ❌ |
| **訂單管理** | ✅ 全部訂單 | ✅ 全部訂單 | ✅ 處理訂單 | ✅ 自己訂單 |
| **庫存管理** | ✅ | ✅ | ✅ | ❌ |
| **支付管理** | ✅ 設定/查看 | ✅ 查看 | ✅ 查看 | ❌ |
| **退款處理** | ✅ 審批 | ✅ 審批 | ✅ 申請 | ✅ 申請 |
| **報表統計** | ✅ 全部 | ✅ 銷售報表 | ❌ | ❌ |
| **系統設定** | ✅ | ❌ | ❌ | ❌ |
| **商品瀏覽** | ✅ | ✅ | ✅ | ✅ |
| **購物車** | ❌ | ❌ | ❌ | ✅ |
| **結帳付款** | ❌ | ❌ | ❌ | ✅ |
| **個人資料** | ✅ | ✅ | ✅ | ✅ |

---

## 頁面路由規劃

| 角色 | 可訪問路徑 |
|-----|-----------|
| **ADMIN** | `/admin/**`, `/manager/**`, `/staff/**` |
| **MANAGER** | `/manager/**`, `/staff/**` |
| **STAFF** | `/staff/**` |
| **CUSTOMER** | `/`, `/products/**`, `/cart/**`, `/checkout/**`, `/orders/my/**`, `/profile/**` |

---

## 角色實體設計

```java
public enum Role {
    ADMIN,      // 管理員 - 最高權限
    MANAGER,    // 經理 - 營運管理
    STAFF,      // 員工 - 日常操作
    CUSTOMER    // 客戶 - 前台購物
}
```

---

## Spring Security 配置範例

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            // 公開頁面
            .requestMatchers("/", "/products/**", "/api/auth/**").permitAll()
            // 管理員專屬
            .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
            // 經理以上
            .requestMatchers("/manager/**", "/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
            // 員工以上
            .requestMatchers("/staff/**", "/api/staff/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
            // 客戶功能
            .requestMatchers("/cart/**", "/checkout/**", "/orders/my/**").hasRole("CUSTOMER")
            // 其他需登入
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

---

## API 權限對應表

### 管理員專屬 API (`/api/admin/**`)
- `POST /api/admin/users` - 創建用戶
- `DELETE /api/admin/users/{id}` - 刪除用戶
- `PUT /api/admin/roles` - 角色權限設定
- `GET /api/admin/system/config` - 系統設定
- `GET /api/admin/statistics/all` - 完整統計報表

### 經理級 API (`/api/manager/**`)
- `GET /api/manager/users` - 查看用戶列表
- `PUT /api/manager/users/{id}` - 編輯用戶資料
- `GET /api/manager/statistics/sales` - 銷售報表
- `GET /api/manager/orders` - 所有訂單
- `PUT /api/manager/refunds/{id}/approve` - 審批退款

### 員工級 API (`/api/staff/**`)
- `GET /api/staff/products` - 商品列表
- `PUT /api/staff/products/{id}` - 編輯商品
- `GET /api/staff/orders` - 訂單列表
- `PUT /api/staff/orders/{id}/status` - 更新訂單狀態
- `PUT /api/staff/inventory` - 庫存管理

### 客戶 API (`/api/customer/**`)
- `GET /api/customer/products` - 瀏覽商品
- `POST /api/customer/cart` - 加入購物車
- `POST /api/customer/orders` - 建立訂單
- `GET /api/customer/orders/my` - 我的訂單
- `POST /api/customer/refunds` - 申請退款
- `GET /api/customer/profile` - 個人資料

---

## 備註

1. **權限繼承**: ADMIN > MANAGER > STAFF，上級角色自動擁有下級權限
2. **客戶角色獨立**: CUSTOMER 角色專注於前台購物功能，與後台管理分離
3. **細粒度控制**: 可根據需求進一步細分權限，例如商品只讀、訂單只讀等
4. **動態權限**: 建議使用資料庫存儲權限配置，支援動態調整

---

*文件建立日期: 2026-01-13*

