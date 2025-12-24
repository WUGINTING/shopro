package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.BatchUpdateProductDTO;
import com.info.ecommerce.modules.product.service.ProductBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/batch")
@RequiredArgsConstructor
@Tag(name = "商品批次操作", description = "批次更新、批次刪除等效率工具")
public class ProductBatchController {

    private final ProductBatchService batchService;

    @PutMapping
    @Operation(summary = "批次更新商品")
    public ApiResponse<Void> batchUpdateProducts(@Valid @RequestBody BatchUpdateProductDTO dto) {
        batchService.batchUpdateProducts(dto);
        return ApiResponse.success("批次更新成功", null);
    }

    @DeleteMapping
    @Operation(summary = "批次刪除商品")
    public ApiResponse<Void> batchDeleteProducts(@RequestBody List<Long> productIds) {
        batchService.batchDeleteProducts(productIds);
        return ApiResponse.success("批次刪除成功", null);
    }

    @PutMapping("/activate")
    @Operation(summary = "批次上架商品")
    public ApiResponse<Void> batchActivateProducts(@RequestBody List<Long> productIds) {
        batchService.batchActivateProducts(productIds);
        return ApiResponse.success("批次上架成功", null);
    }

    @PutMapping("/deactivate")
    @Operation(summary = "批次下架商品")
    public ApiResponse<Void> batchDeactivateProducts(@RequestBody List<Long> productIds) {
        batchService.batchDeactivateProducts(productIds);
        return ApiResponse.success("批次下架成功", null);
    }
}
