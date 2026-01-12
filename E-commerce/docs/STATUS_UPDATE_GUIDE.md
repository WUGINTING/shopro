# 訂單狀態更新指南

## 問題：狀態還是「處理中」

如果您看到訂單狀態仍然是「處理中」（PROCESSING）而不是「已付款」（PAID），請按照以下步驟檢查：

## 步驟 1：執行數據庫腳本

**重要**：必須先執行數據庫腳本更新約束，才能使用 `PAID` 狀態。

1. 使用 SQL Server Management Studio (SSMS) 或其他 SQL 客戶端連接到數據庫
2. 執行文件：`E-commerce/database/update_order_status_constraint.sql`

```sql
USE [e-commerce];
GO

-- 刪除現有約束
ALTER TABLE orders
DROP CONSTRAINT CK__orders__status__6A30C649;
GO

-- 添加新約束（包含 PAID 狀態）
ALTER TABLE orders
ADD CONSTRAINT CK__orders__status__6A30C649
CHECK ([status]='REFUNDED' OR [status]='CANCELLED' OR [status]='COMPLETED' 
       OR [status]='PROCESSING' OR [status]='PENDING_PAYMENT' OR [status]='PAID');
GO
```

## 步驟 2：檢查應用程序日誌

如果數據庫約束還沒有更新，嘗試設置 `PAID` 狀態時會出現錯誤：

```
The UPDATE statement conflicted with the CHECK constraint "CK__orders__status__6A30C649"
```

請檢查應用程序日誌中是否有此錯誤。

## 步驟 3：測試新的支付流程

**重要**：如果您看到的訂單是之前已經處理過的（狀態已經是 PROCESSING），那麼這些訂單不會自動更新為 PAID。

要測試新的狀態流程，請：
1. 創建一個新訂單（狀態為 PENDING_PAYMENT）
2. 完成支付流程
3. 等待 ECPay 回調
4. 檢查訂單狀態是否更新為 **PAID（已付款）**

## 預期的狀態流程

```
PENDING_PAYMENT（待付款）
    ↓ 支付成功（ECPay 回調）
PAID（已付款）✅
    ↓ 物流狀態更新為「已出貨」
PROCESSING（處理中/已發貨）✅
    ↓ 訂單完成
COMPLETED（已完成）
```

## 驗證數據庫約束

執行以下 SQL 查詢，確認約束是否已更新：

```sql
SELECT 
    OBJECT_NAME(parent_object_id) AS TableName,
    name AS ConstraintName,
    definition
FROM sys.check_constraints
WHERE name = 'CK__orders__status__6A30C649';
```

如果約束中包含了 `'PAID'`，表示已成功更新。

## 注意事項

- 數據庫約束更新後，**新的支付成功**才會將狀態設置為 `PAID`
- 之前已經處理過的訂單（狀態為 PROCESSING）不會自動更新
- 如果需要將現有訂單的狀態更新為 PAID，需要手動執行 SQL 更新（不建議，除非是測試環境）

