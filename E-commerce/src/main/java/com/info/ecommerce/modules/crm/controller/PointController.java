package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.PointBatchDTO;
import com.info.ecommerce.modules.crm.dto.PointRecordDTO;
import com.info.ecommerce.modules.crm.enums.PointType;
import com.info.ecommerce.modules.crm.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm/points")
@RequiredArgsConstructor
@Tag(name = "積點管理", description = "CRM 積點管理功能")
public class PointController {

    private final PointService pointService;

    @PostMapping("/add")
    @Operation(summary = "增加積點")
    public ApiResponse<PointRecordDTO> addPoints(
            @Parameter(description = "會員 ID") @RequestParam Long memberId,
            @Parameter(description = "積點數量") @RequestParam Integer points,
            @Parameter(description = "積點類型") @RequestParam PointType pointType,
            @Parameter(description = "原因描述") @RequestParam String reason,
            @Parameter(description = "關聯訂單 ID（可選）") @RequestParam(required = false) Long orderId) {
        return ApiResponse.success("積點已增加", 
            pointService.addPoints(memberId, points, pointType, reason, orderId));
    }

    @PostMapping("/deduct")
    @Operation(summary = "扣除積點")
    public ApiResponse<PointRecordDTO> deductPoints(
            @Parameter(description = "會員 ID") @RequestParam Long memberId,
            @Parameter(description = "積點數量") @RequestParam Integer points,
            @Parameter(description = "積點類型") @RequestParam PointType pointType,
            @Parameter(description = "原因描述") @RequestParam String reason) {
        return ApiResponse.success("積點已扣除", 
            pointService.deductPoints(memberId, points, pointType, reason));
    }

    @PostMapping("/batch-grant")
    @Operation(summary = "批次發放積點")
    public ApiResponse<List<PointRecordDTO>> batchGrantPoints(@Valid @RequestBody PointBatchDTO batchDTO) {
        return ApiResponse.success("積點批次發放完成", pointService.batchGrantPoints(batchDTO));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "取得會員積點紀錄")
    public ApiResponse<Page<PointRecordDTO>> listPointRecords(
            @Parameter(description = "會員 ID") @PathVariable Long memberId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(pointService.listPointRecords(memberId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得積點紀錄詳情")
    public ApiResponse<PointRecordDTO> getPointRecord(
            @Parameter(description = "積點紀錄 ID") @PathVariable Long id) {
        return ApiResponse.success(pointService.getPointRecord(id));
    }

    @GetMapping("/balance/{memberId}")
    @Operation(summary = "取得會員積點餘額")
    public ApiResponse<Integer> getMemberPointBalance(
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        return ApiResponse.success(pointService.getMemberPointBalance(memberId));
    }
}
