# 訂單狀態流程說明

## 狀態定義

根據數據庫 CHECK 約束 `CK__orders__status__6A30C649`，訂單狀態允許以下值：

- `PENDING_PAYMENT`（待付款）
- `PAID`（已付款）
- `PROCESSING`（處理中/已發貨）
- `COMPLETED`（已完成）
- `CANCELLED`（已取消）
- `REFUNDED`（已退款）

## 狀態流程

```
PENDING_PAYMENT（待付款）
    ↓ 支付成功（ECPay/LINE PAY 回調）
PAID（已付款）✅
    ↓ 物流狀態更新為「已出貨」（SHIPPED）
PROCESSING（處理中/已發貨）✅
    ↓ 訂單完成
COMPLETED（已完成）
```

## 狀態轉換觸發點

### 1. PENDING_PAYMENT → PAID
- **觸發**：支付閘道回調成功（`PaymentCallbackService.handlePaymentSuccess`）
- **條件**：訂單狀態為 `PENDING_PAYMENT` 或 `PAID`
- **操作**：更新訂單狀態為 `PAID`，記錄付款時間

### 2. PAID → PROCESSING
- **觸發**：物流狀態更新為「已出貨」（`OrderShipmentService.updateShippingStatus`）
- **條件**：訂單狀態為 `PAID`，物流狀態更新為 `SHIPPED`
- **操作**：更新訂單狀態為 `PROCESSING`，表示訂單已發貨

### 3. PROCESSING → COMPLETED
- **觸發**：手動更新訂單狀態或物流送達
- **條件**：訂單狀態為 `PROCESSING`
- **操作**：更新訂單狀態為 `COMPLETED`，記錄完成時間

## 數據庫約束更新

要使用 `PAID` 狀態，需要執行以下 SQL 腳本：

```sql
-- 文件位置：E-commerce/database/update_order_status_constraint.sql
```

執行後，數據庫約束將允許 `PAID` 狀態。

## 相關文件

- `PaymentCallbackService.java` - 處理支付成功回調
- `OrderShipmentService.java` - 處理物流狀態更新
- `OrderStatus.java` - 訂單狀態枚舉定義

