-- ECPay 配置初始化腳本
USE [e-commerce];
GO

-- 創建 ecpay_config 表（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ecpay_config]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[ecpay_config] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [merchant_id] NVARCHAR(50) NOT NULL,
        [hash_key] NVARCHAR(100) NOT NULL,
        [hash_iv] NVARCHAR(100) NOT NULL,
        [api_url] NVARCHAR(200) NOT NULL,
        [return_url] NVARCHAR(500) NULL,
        [notify_url] NVARCHAR(500) NOT NULL,
        [sandbox] BIT NOT NULL DEFAULT 1,
        [enabled] BIT NOT NULL DEFAULT 1,
        [description] NVARCHAR(500) NULL,
        [created_at] DATETIME2 NOT NULL DEFAULT GETDATE(),
        [updated_at] DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT [PK_ecpay_config] PRIMARY KEY CLUSTERED ([id] ASC)
    );
    
    PRINT 'ecpay_config 表已創建';
END
GO

-- 插入預設配置（如果不存在）
IF NOT EXISTS (SELECT 1 FROM [dbo].[ecpay_config])
BEGIN
    INSERT INTO [dbo].[ecpay_config] (
        [merchant_id],
        [hash_key],
        [hash_iv],
        [api_url],
        [return_url],
        [notify_url],
        [sandbox],
        [enabled],
        [description],
        [created_at],
        [updated_at]
    ) VALUES (
        N'2000132',  -- 測試環境商店代號
        N'5294y06JbISpM5x9',  -- 測試環境 HashKey
        N'v77hoKGq4kWxNNIS',  -- 測試環境 HashIV
        N'https://payment-stage.ecpay.com.tw',  -- 測試環境 API URL
        N'https://13d9786c4b4a.ngrok-free.app/payment/result',  -- 返回 URL（請根據實際 ngrok URL 更新）
        N'https://13d9786c4b4a.ngrok-free.app/api/payment-gateway/callback/ecpay',  -- 通知 URL（請根據實際 ngrok URL 更新）
        1,  -- sandbox = true
        1,  -- enabled = true
        N'ECPay 測試環境配置（請更新 ngrok URL）',
        GETDATE(),
        GETDATE()
    );
    
    PRINT 'ECPay 預設配置已插入';
END
ELSE
BEGIN
    PRINT 'ECPay 配置已存在，跳過插入';
END
GO

