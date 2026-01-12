# 數據庫狀態約束說明

## 問題描述

數據庫中存在 CHECK 約束 `CK__orders__status__6A30C649`，限制了訂單狀態的值。

## 約束定義

數據庫 CHECK 約束允許以下狀態值：
- `PENDING_PAYMENT`（待付款）
- `PAID`（已付款）✅ **已添加**
- `PROCESSING`（處理中）
- `COMPLETED`（已完成）
- `CANCELLED`（已取消）
- `REFUNDED`（已退款）

## 當前行為

支付成功後，訂單狀態會更新為 **PAID（已付款）**。

當物流狀態更新為「已出貨」時，訂單狀態會從 **PAID（已付款）** 更新為 **PROCESSING（處理中/已發貨）**。

## 狀態轉換

根據數據庫約束，允許的狀態值為：
- `PENDING_PAYMENT`（待付款）
- `PROCESSING`（處理中）
- `COMPLETED`（已完成）
- `CANCELLED`（已取消）
- `REFUNDED`（已退款）

支付成功後的狀態轉換：
- **PENDING_PAYMENT（待付款）** → **PROCESSING（處理中）** ✅ 符合約束

## 業務邏輯說明

雖然訂單狀態顯示為"處理中"，但實際上：
- ✅ 支付已經成功完成
- ✅ 支付記錄已創建
- ✅ 訂單已更新（狀態變更為 PROCESSING，payment_time 已設置）
- ✅ 回調處理成功

"處理中"狀態表示訂單已付款，正在等待後續處理（如發貨、完成等）。

## 約束定義查詢

查看約束定義：
```sql
SELECT 
    OBJECT_NAME(parent_object_id) AS TableName,
    name AS ConstraintName,
    definition
FROM sys.check_constraints
WHERE name = 'CK__orders__status__6A30C649'
```

結果：
```
TableName: orders
ConstraintName: CK__orders__status__6A30C649
Definition: ([status]='REFUNDED' OR [status]='CANCELLED' OR [status]='COMPLETED' OR [status]='PROCESSING' OR [status]='PENDING_PAYMENT')
```

## 如果需要添加 PAID 狀態

如果您需要在數據庫中添加 **PAID（已付款）** 狀態，需要：

1. **修改約束**（需要數據庫管理員權限）：
   ```sql
   -- 刪除現有約束
   ALTER TABLE orders
   DROP CONSTRAINT CK__orders__status__6A30C649;
   
   -- 添加新約束（包含 PAID 狀態）
   ALTER TABLE orders
   ADD CONSTRAINT CK__orders__status__6A30C649
   CHECK ([status]='REFUNDED' OR [status]='CANCELLED' OR [status]='COMPLETED' 
          OR [status]='PROCESSING' OR [status]='PENDING_PAYMENT' OR [status]='PAID');
   ```

2. **更新代碼**：修改 `PaymentCallbackService.java` 中的狀態更新邏輯

## 當前解決方案

目前代碼使用 **PROCESSING（處理中）** 作為支付成功後的狀態，這是安全的選擇，因為：
- ✅ 符合數據庫約束
- ✅ 支付已成功處理
- ✅ 訂單狀態已正確更新
- ✅ 業務邏輯正常運作

## 狀態流程

根據數據庫約束的狀態流程：

```
PENDING_PAYMENT（待付款）
    ↓ 支付成功
PAID（已付款）← 支付成功後的狀態 ✅
    ↓ 物流狀態更新為「已出貨」
PROCESSING（處理中/已發貨）← 已發貨狀態 ✅
    ↓ 後續處理（送達等）
COMPLETED（已完成）

其他狀態：
- CANCELLED（已取消）
- REFUNDED（已退款）
```

**狀態轉換說明**：
1. **支付成功**：`PENDING_PAYMENT` → `PAID`
2. **已發貨**：`PAID` → `PROCESSING`（當物流狀態更新為 `SHIPPED` 時）
3. **完成訂單**：`PROCESSING` → `COMPLETED`

---

**最後更新**：2026-01-12  
**相關文件**：`PaymentCallbackService.java`

