package com.info.ecommerce.modules.store.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.store.dto.StaffDTO;
import com.info.ecommerce.modules.store.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "員工管理", description = "員工帳號與權限管理（上限 50 組）")
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @Operation(summary = "取得所有員工")
    public ApiResponse<List<StaffDTO>> getAllStaff() {
        return ApiResponse.success(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得單一員工")
    public ApiResponse<StaffDTO> getStaff(@PathVariable Long id) {
        return ApiResponse.success(staffService.getStaffById(id));
    }

    @PostMapping
    @Operation(summary = "新增員工", description = "帳號上限 50 組")
    public ApiResponse<StaffDTO> createStaff(@Valid @RequestBody StaffDTO dto) {
        return ApiResponse.success("員工已新增", staffService.createStaff(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新員工")
    public ApiResponse<StaffDTO> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDTO dto) {
        return ApiResponse.success("員工已更新", staffService.updateStaff(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除員工")
    public ApiResponse<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ApiResponse.success("員工已刪除", null);
    }

    @PatchMapping("/{id}/toggle-enabled")
    @Operation(summary = "切換員工啟用狀態")
    public ApiResponse<StaffDTO> toggleEnabled(@PathVariable Long id) {
        return ApiResponse.success(staffService.toggleEnabled(id));
    }
}
