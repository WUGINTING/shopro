package com.info.ecommerce.modules.calendar.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.calendar.dto.*;
import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import com.info.ecommerce.modules.calendar.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/calendar")
@RequiredArgsConstructor
@Tag(name = "管理人員行事曆", description = "管理人員行事曆管理功能")
public class CalendarController {

    private final CalendarEventService eventService;
    private final CalendarConflictService conflictService;
    private final CalendarPreviewService previewService;
    private final CalendarBatchService batchService;

    @GetMapping("/events")
    @Operation(summary = "獲取行事曆事件", description = "支援分頁和條件查詢")
    public ApiResponse<Map<String, Object>> getEvents(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(description = "事件類型") @RequestParam(required = false) CalendarEventType type,
            @Parameter(description = "關聯實體類型") @RequestParam(required = false) String entityType,
            @Parameter(description = "關聯實體ID") @RequestParam(required = false) Long entityId,
            @Parameter(description = "開始時間") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "結束時間") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "關鍵字") @RequestParam(required = false) String keyword) {
        
        if (startTime != null || endTime != null || type != null || entityType != null || keyword != null) {
            // 條件查詢
            CalendarEventQueryDTO queryDTO = new CalendarEventQueryDTO();
            queryDTO.setType(type);
            queryDTO.setEntityType(entityType);
            queryDTO.setEntityId(entityId);
            queryDTO.setStartTime(startTime);
            queryDTO.setEndTime(endTime);
            queryDTO.setKeyword(keyword);
            
            List<CalendarEventDTO> events = eventService.queryEvents(queryDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", events);
            response.put("total", events.size());
            response.put("page", page);
            response.put("pageSize", pageSize);
            
            return ApiResponse.success(response);
        } else {
            // 分頁查詢
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<CalendarEventDTO> pageResult = eventService.listEvents(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", pageResult.getContent());
            response.put("total", pageResult.getTotalElements());
            response.put("page", page);
            response.put("pageSize", pageSize);
            
            return ApiResponse.success(response);
        }
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "獲取事件詳情")
    public ApiResponse<CalendarEventDTO> getEvent(
            @Parameter(description = "事件 ID") @PathVariable Long id) {
        return ApiResponse.success(eventService.getEvent(id));
    }

    @PostMapping("/events")
    @Operation(summary = "建立事件")
    public ApiResponse<CalendarEventDTO> createEvent(
            @Valid @RequestBody CalendarEventDTO dto) {
        return ApiResponse.success("事件已建立", eventService.createEvent(dto));
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "更新事件")
    public ApiResponse<CalendarEventDTO> updateEvent(
            @Parameter(description = "事件 ID") @PathVariable Long id,
            @Valid @RequestBody CalendarEventDTO dto) {
        return ApiResponse.success("事件已更新", eventService.updateEvent(id, dto));
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "刪除事件")
    public ApiResponse<Void> deleteEvent(
            @Parameter(description = "事件 ID") @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ApiResponse.success("事件已刪除", null);
    }

    @GetMapping("/conflicts")
    @Operation(summary = "檢查時間衝突", description = "檢查所有事件的時間衝突")
    public ApiResponse<List<CalendarConflictDTO>> checkConflicts(
            @Parameter(description = "事件 ID（可選，檢查特定事件）") @RequestParam(required = false) Long eventId,
            @Parameter(description = "開始時間（可選）") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "結束時間（可選）") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "實體類型（可選）") @RequestParam(required = false) String entityType) {
        
        List<CalendarConflictDTO> conflicts;
        
        if (eventId != null && startTime != null && endTime != null && entityType != null) {
            // 檢查特定事件的衝突
            conflicts = conflictService.checkConflicts(eventId, startTime, endTime, entityType);
        } else if (startTime != null && endTime != null) {
            // 檢查時間範圍內的衝突
            conflicts = conflictService.checkConflictsInTimeRange(startTime, endTime);
        } else {
            // 檢查所有衝突
            conflicts = conflictService.checkAllConflicts();
        }
        
        return ApiResponse.success(conflicts);
    }

    @GetMapping("/preview")
    @Operation(summary = "預覽時間變更效果", description = "預覽特定時間點的實際效果")
    public ApiResponse<CalendarPreviewDTO> preview(
            @Parameter(description = "預覽時間點") @RequestParam 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime previewTime) {
        return ApiResponse.success(previewService.previewTimePoint(previewTime));
    }

    @PostMapping("/batch-update")
    @Operation(summary = "批量更新", description = "批量更新多個事件的時間")
    public ApiResponse<List<CalendarEventDTO>> batchUpdate(
            @Valid @RequestBody BatchUpdateDTO dto) {
        return ApiResponse.success("批量更新完成", batchService.batchUpdate(dto));
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量刪除", description = "批量刪除多個事件")
    public ApiResponse<Void> batchDelete(
            @Parameter(description = "事件ID列表") @RequestBody List<Long> eventIds) {
        batchService.batchDelete(eventIds);
        return ApiResponse.success("批量刪除完成", null);
    }
}

