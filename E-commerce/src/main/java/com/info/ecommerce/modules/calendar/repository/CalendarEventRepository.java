package com.info.ecommerce.modules.calendar.repository;

import com.info.ecommerce.modules.calendar.entity.CalendarEvent;
import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * 根據事件類型查詢
     */
    Page<CalendarEvent> findByType(CalendarEventType type, Pageable pageable);

    /**
     * 根據實體類型和實體ID查詢
     */
    List<CalendarEvent> findByEntityTypeAndEntityId(String entityType, Long entityId);

    /**
     * 根據時間範圍查詢事件
     */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "(e.startTime <= :endTime AND e.endTime >= :startTime)")
    List<CalendarEvent> findEventsInTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根據時間範圍和事件類型查詢
     */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "e.type = :type AND " +
           "(e.startTime <= :endTime AND e.endTime >= :startTime)")
    List<CalendarEvent> findEventsByTypeInTimeRange(
            @Param("type") CalendarEventType type,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根據實體類型、實體ID和時間範圍查詢
     */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "e.entityType = :entityType AND e.entityId = :entityId AND " +
           "(e.startTime <= :endTime AND e.endTime >= :startTime)")
    List<CalendarEvent> findEventsByEntityInTimeRange(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根據關鍵字搜尋（標題或描述）
     */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "(e.title LIKE %:keyword% OR e.description LIKE %:keyword%)")
    Page<CalendarEvent> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查詢與指定事件時間重疊的事件
     */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "e.id != :excludeId AND " +
           "e.entityType = :entityType AND " +
           "(e.startTime <= :endTime AND e.endTime >= :startTime)")
    List<CalendarEvent> findOverlappingEvents(
            @Param("excludeId") Long excludeId,
            @Param("entityType") String entityType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}

