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
    private final com.info.ecommerce.modules.product.service.InventoryManagementService inventoryManagementService;
    private final com.info.ecommerce.common.RateLimiter rateLimiter;

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

    @PostMapping("/{id}/restock-notification")
    @Operation(summary = "到貨通知登記", description = "缺貨商品補貨時寄信通知（每個 Email 同一商品 / 規格只登記一次）")
    public ApiResponse<Void> subscribeRestock(@Parameter(description = "商品 ID") @PathVariable Long id,
                                              @RequestBody java.util.Map<String, Object> body,
                                              jakarta.servlet.http.HttpServletRequest http) {
        rateLimiter.check("restock-notification", com.info.ecommerce.common.RateLimiter.clientKey(http), 10, java.time.Duration.ofMinutes(10),
                "登記次數過多，請稍後再試");
        productService.assertPubliclyVisible(id);
        Object spec = body.get("specificationId");
        Long specificationId;
        try {
            specificationId = spec == null || spec.toString().isBlank() ? null : Long.valueOf(spec.toString().trim());
        } catch (NumberFormatException e) {
            throw new com.info.ecommerce.common.exception.BusinessException("商品規格不存在");
        }
        inventoryManagementService.subscribeStockNotification(id, specificationId,
                body.get("email") == null ? null : body.get("email").toString(), null);
        return ApiResponse.success("已登記到貨通知，商品補貨時會寄信給您", null);
    }
}
