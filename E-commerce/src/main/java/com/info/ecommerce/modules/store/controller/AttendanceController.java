package com.info.ecommerce.modules.store.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.store.dto.AttendanceRecordDTO;
import com.info.ecommerce.modules.store.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "出勤打卡", description = "員工打卡與出勤紀錄")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in/{staffId}")
    @Operation(summary = "上班打卡")
    public ApiResponse<AttendanceRecordDTO> clockIn(@PathVariable Long staffId) {
        return ApiResponse.success("上班打卡成功", attendanceService.clockIn(staffId));
    }

    @PostMapping("/clock-out/{staffId}")
    @Operation(summary = "下班打卡")
    public ApiResponse<AttendanceRecordDTO> clockOut(@PathVariable Long staffId) {
        return ApiResponse.success("下班打卡成功", attendanceService.clockOut(staffId));
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "查詢員工出勤紀錄")
    public ApiResponse<List<AttendanceRecordDTO>> getRecordsByStaff(
            @PathVariable Long staffId,
            @Parameter(description = "開始日期 (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "結束日期 (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(attendanceService.getRecordsByStaff(staffId, startDate, endDate));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "查詢某日全部出勤")
    public ApiResponse<List<AttendanceRecordDTO>> getRecordsByDate(
            @Parameter(description = "日期 (yyyy-MM-dd)")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(attendanceService.getRecordsByDate(date));
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "修改出勤紀錄", description = "主管用：補登或修正打卡時間")
    public ApiResponse<AttendanceRecordDTO> updateRecord(
            @PathVariable Long recordId,
            @RequestBody AttendanceRecordDTO dto) {
        return ApiResponse.success("出勤紀錄已更新", attendanceService.updateRecord(recordId, dto));
    }
}
