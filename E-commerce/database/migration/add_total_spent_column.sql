-- 為 crm_member 表添加 total_spent 欄位
-- 執行此腳本以添加總消費金額欄位

USE [e-commerce];
GO

-- 檢查欄位是否已存在，如果不存在則添加
IF NOT EXISTS (
    SELECT * 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'[dbo].[crm_member]') 
    AND name = 'total_spent'
)
BEGIN
    ALTER TABLE [dbo].[crm_member]
    ADD [total_spent] DECIMAL(10, 2) NOT NULL DEFAULT 0;
    
    PRINT '欄位 total_spent 已成功添加到 crm_member 表';
END
ELSE
BEGIN
    PRINT '欄位 total_spent 已存在於 crm_member 表';
END
GO

