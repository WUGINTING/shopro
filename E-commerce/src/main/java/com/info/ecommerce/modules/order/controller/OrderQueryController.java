package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderQueryDTO;
import com.info.ecommerce.modules.order.service.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 訂單查詢控制器 - 進階查詢功能
 */
@RestController
@RequestMapping("/api/orders/search")
@RequiredArgsConstructor
@Tag(name = "訂單查詢", description = "訂單多條件查詢功能")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    @PostMapping
    @Operation(summary = "多條件查詢訂單", 
               description = "根據多條件篩選，如訂單ID、客戶ID、狀態、日期、金額範圍等")
    public ApiResponse<Page<OrderDTO>> searchOrders(@RequestBody OrderQueryDTO queryDTO) {
        return ApiResponse.success(orderQueryService.searchOrders(queryDTO));
    }

    @GetMapping("/order-number/{orderNumber}")
    @Operation(summary = "根據訂單編號查詢")
    public ApiResponse<OrderDTO> findByOrderNumber(
            @Parameter(description = "訂單編號") @PathVariable String orderNumber) {
        return ApiResponse.success(orderQueryService.findByOrderNumber(orderNumber));
    }

    @GetMapping("/customer-name")
    @Operation(summary = "根據客戶姓名模糊查詢")
    public ApiResponse<List<OrderDTO>> searchByCustomerName(
            @Parameter(description = "客戶姓名") @RequestParam String customerName) {
        return ApiResponse.success(orderQueryService.searchByCustomerName(customerName));
    }
}
