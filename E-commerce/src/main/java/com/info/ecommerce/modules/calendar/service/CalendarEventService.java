package com.info.ecommerce.modules.calendar.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.calendar.dto.CalendarEventDTO;
import com.info.ecommerce.modules.calendar.dto.CalendarEventQueryDTO;
import com.info.ecommerce.modules.calendar.entity.CalendarEvent;
import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import com.info.ecommerce.modules.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final CalendarEventRepository eventRepository;

    /**
     * 創建行事曆事件
     */
    @Transactional
    public CalendarEventDTO createEvent(CalendarEventDTO dto) {
        // 驗證時間
        validateTimeRange(dto.getStartTime(), dto.getEndTime());

        CalendarEvent event = new CalendarEvent();
        BeanUtils.copyProperties(dto, event, "id", "createdAt", "updatedAt");
        
        // 設置預設顏色
        if (event.getColor() == null) {
            event.setColor(getDefaultColor(dto.getType()));
        }
        
        event = eventRepository.save(event);
        return toDTO(event);
    }

    /**
     * 更新行事曆事件
     */
    @Transactional
    public CalendarEventDTO updateEvent(Long id, CalendarEventDTO dto) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException("行事曆事件不存在"));

        // 驗證時間
        validateTimeRange(dto.getStartTime(), dto.getEndTime());

        BeanUtils.copyProperties(dto, event, "id", "createdAt", "updatedAt");
        event = eventRepository.save(event);
        return toDTO(event);
    }

    /**
     * 獲取事件詳情
     */
    public CalendarEventDTO getEvent(Long id) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException("行事曆事件不存在"));
        return toDTO(event);
    }

    /**
     * 刪除事件
     */
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new BusinessException("行事曆事件不存在");
        }
        eventRepository.deleteById(id);
    }

    /**
     * 查詢事件列表（分頁）
     */
    public Page<CalendarEventDTO> listEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 根據條件查詢事件
     */
    public List<CalendarEventDTO> queryEvents(CalendarEventQueryDTO queryDTO) {
        List<CalendarEvent> events;

        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            // 根據時間範圍查詢
            if (queryDTO.getType() != null) {
                events = eventRepository.findEventsByTypeInTimeRange(
                        queryDTO.getType(),
                        queryDTO.getStartTime(),
                        queryDTO.getEndTime()
                );
            } else if (queryDTO.getEntityType() != null && queryDTO.getEntityId() != null) {
                events = eventRepository.findEventsByEntityInTimeRange(
                        queryDTO.getEntityType(),
                        queryDTO.getEntityId(),
                        queryDTO.getStartTime(),
                        queryDTO.getEndTime()
                );
            } else {
                events = eventRepository.findEventsInTimeRange(
                        queryDTO.getStartTime(),
                        queryDTO.getEndTime()
                );
            }
        } else if (queryDTO.getType() != null) {
            // 根據類型查詢（需要分頁，這裡簡化處理）
            events = eventRepository.findByType(queryDTO.getType(), Pageable.unpaged())
                    .getContent();
        } else if (queryDTO.getEntityType() != null && queryDTO.getEntityId() != null) {
            events = eventRepository.findByEntityTypeAndEntityId(
                    queryDTO.getEntityType(),
                    queryDTO.getEntityId()
            );
        } else {
            events = eventRepository.findAll();
        }

        // 關鍵字過濾
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim().toLowerCase();
            events = events.stream()
                    .filter(e -> (e.getTitle() != null && e.getTitle().toLowerCase().contains(keyword)) ||
                               (e.getDescription() != null && e.getDescription().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }

        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 根據時間範圍獲取事件
     */
    public List<CalendarEventDTO> getEventsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<CalendarEvent> events = eventRepository.findEventsInTimeRange(startTime, endTime);
        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 驗證時間範圍
     */
    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("開始時間不能晚於結束時間");
        }
    }

    /**
     * 獲取預設顏色
     */
    private String getDefaultColor(CalendarEventType type) {
        switch (type) {
            case PRODUCT_LISTING:
                return "#3498db"; // 藍色
            case MARKETING_ACTIVITY:
                return "#2ecc71"; // 綠色
            case SPECIAL_EVENT:
                return "#f39c12"; // 橙色
            case ORDER_DEADLINE:
                return "#9b59b6"; // 紫色
            default:
                return "#95a5a6"; // 灰色
        }
    }

    /**
     * 轉換為 DTO
     */
    public CalendarEventDTO toDTO(CalendarEvent event) {
        CalendarEventDTO dto = new CalendarEventDTO();
        BeanUtils.copyProperties(event, dto);
        return dto;
    }
}

