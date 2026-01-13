-- 創建行事曆事件表
CREATE TABLE calendar_events (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    type NVARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    entity_type NVARCHAR(50) NOT NULL,
    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    color NVARCHAR(20),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 創建索引
CREATE INDEX idx_calendar_event_type ON calendar_events(type);
CREATE INDEX idx_calendar_event_entity ON calendar_events(entity_type, entity_id);
CREATE INDEX idx_calendar_event_start_time ON calendar_events(start_time);
CREATE INDEX idx_calendar_event_end_time ON calendar_events(end_time);

-- 添加註釋
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'行事曆事件表，用於管理商品上下架時間、行銷活動期間等事件', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'calendar_events';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'事件類型：PRODUCT_LISTING, MARKETING_ACTIVITY, SPECIAL_EVENT, ORDER_DEADLINE', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'calendar_events', 
    @level2type = N'COLUMN', @level2name = N'type';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'關聯實體ID（商品ID、活動ID等）', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'calendar_events', 
    @level2type = N'COLUMN', @level2name = N'entity_id';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'關聯實體類型：PRODUCT, MARKETING_CAMPAIGN, PROMOTION, ORDER等', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'calendar_events', 
    @level2type = N'COLUMN', @level2name = N'entity_type';

