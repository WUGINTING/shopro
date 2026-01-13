package com.info.ecommerce.modules.calendar.service;

import com.info.ecommerce.modules.calendar.dto.CalendarConflictDTO;
import com.info.ecommerce.modules.calendar.entity.CalendarEvent;
import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import com.info.ecommerce.modules.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarConflictService {

    private final CalendarEventRepository eventRepository;

    /**
     * 檢查時間衝突
     */
    public List<CalendarConflictDTO> checkConflicts(Long eventId, LocalDateTime startTime, LocalDateTime endTime, String entityType) {
        List<CalendarConflictDTO> conflicts = new ArrayList<>();

        // 查詢重疊的事件
        List<CalendarEvent> overlappingEvents = eventRepository.findOverlappingEvents(
                eventId != null ? eventId : -1L,
                entityType,
                startTime,
                endTime
        );

        for (CalendarEvent event : overlappingEvents) {
            CalendarConflictDTO conflict = new CalendarConflictDTO();
            
            // 判斷衝突類型
            if (entityType.equals("PRODUCT") && event.getType() == CalendarEventType.MARKETING_ACTIVITY) {
                conflict.setConflictType("PRODUCT_DELISTING_DURING_ACTIVITY");
                conflict.setDescription("商品將在活動期間下架，可能影響活動效果");
                conflict.setSuggestion("建議延長商品下架時間或提前結束活動");
            } else if (entityType.equals("MARKETING_CAMPAIGN") && event.getType() == CalendarEventType.PRODUCT_LISTING) {
                conflict.setConflictType("ACTIVITY_WITH_DELISTED_PRODUCT");
                conflict.setDescription("活動期間內有商品下架，可能影響活動效果");
                conflict.setSuggestion("建議延長商品上架時間或調整活動時間");
            } else {
                conflict.setConflictType("TIME_OVERLAP");
                conflict.setDescription("時間範圍與其他事件重疊");
                conflict.setSuggestion("請檢查時間安排是否合理");
            }

            conflict.setConflictingEventIds(List.of(event.getId()));
            conflict.setConflictingEventTitles(List.of(event.getTitle()));
            conflicts.add(conflict);
        }

        return conflicts;
    }

    /**
     * 檢查所有事件的衝突
     */
    public List<CalendarConflictDTO> checkAllConflicts() {
        List<CalendarConflictDTO> allConflicts = new ArrayList<>();
        List<CalendarEvent> allEvents = eventRepository.findAll();

        for (CalendarEvent event : allEvents) {
            List<CalendarConflictDTO> conflicts = checkConflicts(
                    event.getId(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getEntityType()
            );
            allConflicts.addAll(conflicts);
        }

        return allConflicts;
    }

    /**
     * 檢查特定時間範圍內的衝突
     */
    public List<CalendarConflictDTO> checkConflictsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<CalendarConflictDTO> conflicts = new ArrayList<>();
        List<CalendarEvent> events = eventRepository.findEventsInTimeRange(startTime, endTime);

        for (CalendarEvent event : events) {
            List<CalendarConflictDTO> eventConflicts = checkConflicts(
                    event.getId(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getEntityType()
            );
            conflicts.addAll(eventConflicts);
        }

        return conflicts;
    }
}

