-- 支付回調記錄表初始化腳本
USE [e-commerce];
GO

-- 創建 payment_callback_log 表（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[payment_callback_log]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[payment_callback_log] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [gateway] NVARCHAR(50) NOT NULL,
        [order_number] NVARCHAR(100) NULL,
        [transaction_id] NVARCHAR(100) NULL,
        [status] NVARCHAR(50) NOT NULL,
        [raw_params] NVARCHAR(MAX) NULL,
        [parsed_response] NVARCHAR(MAX) NULL,
        [process_result] NVARCHAR(500) NULL,
        [error_message] NVARCHAR(1000) NULL,
        [request_ip] NVARCHAR(50) NULL,
        [user_agent] NVARCHAR(500) NULL,
        [process_time_ms] BIGINT NULL,
        [created_at] DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT [PK_payment_callback_log] PRIMARY KEY CLUSTERED ([id] ASC)
    );
    
    -- 創建索引以提高查詢性能
    CREATE INDEX [IX_payment_callback_log_order_number] ON [dbo].[payment_callback_log] ([order_number]);
    CREATE INDEX [IX_payment_callback_log_transaction_id] ON [dbo].[payment_callback_log] ([transaction_id]);
    CREATE INDEX [IX_payment_callback_log_gateway] ON [dbo].[payment_callback_log] ([gateway]);
    CREATE INDEX [IX_payment_callback_log_status] ON [dbo].[payment_callback_log] ([status]);
    CREATE INDEX [IX_payment_callback_log_created_at] ON [dbo].[payment_callback_log] ([created_at] DESC);
    
    PRINT 'payment_callback_log 表已創建';
END
ELSE
BEGIN
    PRINT 'payment_callback_log 表已存在';
END
GO

