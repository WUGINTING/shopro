package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.service.EdmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * EDM 退訂（公開，以信中的退訂連結 token 驗證）
 */
@RestController
@RequestMapping("/api/storefront/unsubscribe")
@RequiredArgsConstructor
@Tag(name = "EDM 退訂", description = "取消接收優惠與新品通知")
public class StorefrontUnsubscribeController {

    private final EdmService edmService;

    @PostMapping
    @Operation(summary = "取消訂閱優惠通知")
    public ApiResponse<Void> unsubscribe(@RequestBody Map<String, String> body) {
        edmService.unsubscribe(body.getOrDefault("token", ""));
        return ApiResponse.success("已取消訂閱，之後不會再收到優惠通知信", null);
    }
}
