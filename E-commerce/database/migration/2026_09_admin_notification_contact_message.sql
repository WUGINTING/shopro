-- 後台通知新增類型 CONTACT_MESSAGE（前台聯絡表單留言）
-- Hibernate 建表時會為 enum 欄位建立 CHECK 約束，JPA_DDL_AUTO=update 不會更新既有約束，
-- 舊資料庫未執行此腳本時，顧客送出聯絡表單會因約束失敗。
-- 約束名稱由 SQL Server 自動產生，因此先找出 admin_notifications.type 上的 CHECK 約束再刪除。

DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql = @sql + N'ALTER TABLE admin_notifications DROP CONSTRAINT ' + QUOTENAME(cc.name) + N';'
FROM sys.check_constraints cc
JOIN sys.columns c ON c.object_id = cc.parent_object_id AND c.column_id = cc.parent_column_id
WHERE cc.parent_object_id = OBJECT_ID(N'admin_notifications') AND c.name = N'type';
EXEC sp_executesql @sql;
GO

ALTER TABLE admin_notifications
ADD CONSTRAINT CK_admin_notifications_type
CHECK ([type] IN ('ORDER_CREATED', 'PAYMENT_COMPLETED', 'ORDER_CANCELLED', 'ORDER_QA', 'STOCK_LOW', 'CONTACT_MESSAGE'));
GO

-- 驗證
SELECT name, definition FROM sys.check_constraints WHERE parent_object_id = OBJECT_ID(N'admin_notifications');
GO
