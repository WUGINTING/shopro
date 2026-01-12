-- 修改訂單狀態 CHECK 約束，添加 PAID 狀態
-- 執行此腳本需要數據庫管理員權限

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

-- 驗證約束
SELECT 
    OBJECT_NAME(parent_object_id) AS TableName,
    name AS ConstraintName,
    definition
FROM sys.check_constraints
WHERE name = 'CK__orders__status__6A30C649';
GO

