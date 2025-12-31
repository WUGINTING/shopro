package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.OperationLogDTO;
import com.info.ecommerce.modules.system.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 操作日誌控制器
 */
@RestController
@RequestMapping("/api/system/operation-logs")
@RequiredArgsConstructor
@Tag(name = "操作日誌", description = "操作日誌查詢")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping("/{id}")
    @Operation(summary = "取得日誌詳情")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<OperationLogDTO> getLog(
            @Parameter(description = "日誌 ID") @PathVariable Long id) {
        return ApiResponse.success(operationLogService.getLog(id));
    }

    @GetMapping
    @Operation(summary = "分頁查詢所有日誌")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listLogs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listLogs(pageable));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "依用戶查詢日誌")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listByUser(
            @Parameter(description = "用戶 ID") @PathVariable Long userId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listByUser(userId, pageable));
    }

    @GetMapping("/operation-type/{operationType}")
    @Operation(summary = "依操作類型查詢")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listByOperationType(
            @Parameter(description = "操作類型") @PathVariable String operationType,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listByOperationType(operationType, pageable));
    }

    @GetMapping("/module/{module}")
    @Operation(summary = "依模組查詢")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listByModule(
            @Parameter(description = "模組名稱") @PathVariable String module,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listByModule(module, pageable));
    }

    @GetMapping("/sensitive")
    @Operation(summary = "查詢敏感操作")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<OperationLogDTO>> listSensitiveOperations(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listSensitiveOperations(pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "依時間範圍查詢")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listByDateRange(
            @Parameter(description = "開始時間") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "結束時間") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listByDateRange(start, end, pageable));
    }

    @GetMapping("/user/{userId}/module/{module}")
    @Operation(summary = "依用戶和模組查詢")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Page<OperationLogDTO>> listByUserAndModule(
            @Parameter(description = "用戶 ID") @PathVariable Long userId,
            @Parameter(description = "模組名稱") @PathVariable String module,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operationLogService.listByUserAndModule(userId, module, pageable));
    }
}
