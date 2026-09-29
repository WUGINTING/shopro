package com.info.ecommerce.modules.statistics.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.statistics.dto.StatisticsOverviewDTO;
import com.info.ecommerce.modules.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "營運統計", description = "銷售、商品、分類與付款方式統計")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overall")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "期間營運統計", description = "預設最近 30 天；銷售數字只計已付款 / 處理中 / 已完成的訂單")
    public ApiResponse<StatisticsOverviewDTO> overall(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(statisticsService.overview(startDate, endDate));
    }
}
