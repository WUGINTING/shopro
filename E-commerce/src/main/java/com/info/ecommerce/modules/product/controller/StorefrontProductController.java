package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.ProductDTO;
import com.info.ecommerce.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 前台商品控制器（不需登入）：只回傳上架 / 缺貨商品，分頁、篩選、排序皆在後端完成
 */
@RestController
@RequestMapping("/api/storefront/products")
@RequiredArgsConstructor
@Tag(name = "前台商品", description = "前台商品列表與詳情")
public class StorefrontProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "前台商品列表", description = "可依分類（含子分類）、關鍵字篩選；sort = newest / price_asc / price_desc / name")
    public ApiResponse<Page<ProductDTO>> listProducts(
            @Parameter(description = "分類 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "關鍵字（名稱或 SKU）") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序") @RequestParam(defaultValue = "newest") String sort,
            @Parameter(description = "頁碼（0 起算）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量（最多 60）") @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.success(productService.listPublicProducts(categoryId, keyword, sort, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "前台商品詳情")
    public ApiResponse<ProductDTO> getProduct(@Parameter(description = "商品 ID") @PathVariable Long id) {
        return ApiResponse.success(productService.getPublicProduct(id));
    }
}
