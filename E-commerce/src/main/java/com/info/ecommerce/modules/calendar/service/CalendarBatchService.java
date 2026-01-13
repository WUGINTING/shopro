package com.info.ecommerce.modules.calendar.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.calendar.dto.BatchUpdateDTO;
import com.info.ecommerce.modules.calendar.dto.CalendarEventDTO;
import com.info.ecommerce.modules.calendar.entity.CalendarEvent;
import com.info.ecommerce.modules.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarBatchService {

    private final CalendarEventRepository eventRepository;
    private final CalendarEventService eventService;

    /**
     * 批量更新事件時間
     */
    @Transactional
    public List<CalendarEventDTO> batchUpdate(BatchUpdateDTO dto) {
        // 驗證時間
        if (dto.getNewStartTime().isAfter(dto.getNewEndTime())) {
            throw new BusinessException("新的開始時間不能晚於結束時間");
        }

        // 獲取所有要更新的事件
        List<CalendarEvent> events = eventRepository.findAllById(dto.getEventIds());
        
        if (events.size() != dto.getEventIds().size()) {
            throw new BusinessException("部分事件不存在");
        }

        // 批量更新時間
        for (CalendarEvent event : events) {
            event.setStartTime(dto.getNewStartTime());
            event.setEndTime(dto.getNewEndTime());
        }

        events = eventRepository.saveAll(events);

        // 如果需要更新關聯商品或活動，可以在這裡添加邏輯
        // 例如：根據 entityType 和 entityId 更新對應的商品或活動時間

        return events.stream()
                .map(eventService::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 批量刪除事件
     */
    @Transactional
    public void batchDelete(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            throw new BusinessException("事件ID列表不可為空");
        }

        List<CalendarEvent> events = eventRepository.findAllById(eventIds);
        if (events.size() != eventIds.size()) {
            throw new BusinessException("部分事件不存在");
        }

        eventRepository.deleteAll(events);
    }
}

