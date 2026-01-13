package com.info.ecommerce.modules.calendar.entity;

import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 行事曆事件實體
 */
@Entity
@Table(name = "calendar_events", indexes = {
    @Index(name = "idx_calendar_event_type", columnList = "type"),
    @Index(name = "idx_calendar_event_entity", columnList = "entity_type,entity_id"),
    @Index(name = "idx_calendar_event_start_time", columnList = "start_time"),
    @Index(name = "idx_calendar_event_end_time", columnList = "end_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件類型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CalendarEventType type;

    /**
     * 關聯實體ID（商品ID、活動ID等）
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * 關聯實體類型（PRODUCT, MARKETING_CAMPAIGN, PROMOTION, ORDER等）
     */
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    /**
     * 開始時間
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 結束時間
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 事件標題
     */
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    /**
     * 事件描述
     */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    /**
     * 顏色編碼（用於前端顯示）
     */
    @Column(length = 20)
    private String color;

    /**
     * 創建者ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 創建時間
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新者ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 更新時間
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

