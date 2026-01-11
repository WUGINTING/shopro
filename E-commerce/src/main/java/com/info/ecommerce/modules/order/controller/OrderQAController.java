package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderQADTO;
import com.info.ecommerce.modules.order.service.OrderQAService;
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

/**
 * 訂單問與答控制器 - Q&A 功能
 */
@RestController
@RequestMapping("/api/orders/qa")
@RequiredArgsConstructor
@Tag(name = "訂單問與答", description = "允許顧客與店家針對訂單進行溝通")
public class OrderQAController {

    private final OrderQAService orderQAService;

    @PostMapping
    @Operation(summary = "新增問題", description = "顧客或店家對訂單提問")
    public ApiResponse<OrderQADTO> askQuestion(@Valid @RequestBody OrderQADTO dto) {
        return ApiResponse.success("問題已提交", orderQAService.askQuestion(dto));
    }

    @PatchMapping("/{qaId}/answer")
    @Operation(summary = "回答問題", description = "店家或客服回答訂單問題")
    public ApiResponse<OrderQADTO> answerQuestion(
            @Parameter(description = "Q&A ID") @PathVariable Long qaId,
            @Parameter(description = "回答內容") @RequestParam String answer,
            @Parameter(description = "回答者 ID") @RequestParam Long answererId,
            @Parameter(description = "回答者名稱") @RequestParam String answererName) {
        return ApiResponse.success("回答已提交", 
            orderQAService.answerQuestion(qaId, answer, answererId, answererName));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "取得訂單的所有問答")
    public ApiResponse<List<OrderQADTO>> getQAByOrderId(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId) {
        return ApiResponse.success(orderQAService.getQAByOrderId(orderId));
    }

    @GetMapping("/order/{orderId}/page")
    @Operation(summary = "分頁取得訂單問答")
    public ApiResponse<Page<OrderQADTO>> getQAByOrderIdPage(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderQAService.getQAByOrderIdPage(orderId, pageable));
    }

    @GetMapping
    @Operation(summary = "取得所有問答", description = "取得所有問答記錄，按創建時間倒序排列")
    public ApiResponse<List<OrderQADTO>> getAllQA() {
        return ApiResponse.success(orderQAService.getAllQA());
    }

    @GetMapping("/asker/{askerId}")
    @Operation(summary = "取得客戶的所有提問")
    public ApiResponse<List<OrderQADTO>> getQAByAskerId(
            @Parameter(description = "提問者 ID") @PathVariable Long askerId) {
        return ApiResponse.success(orderQAService.getQAByAskerId(askerId));
    }

    @DeleteMapping("/{qaId}")
    @Operation(summary = "刪除問答")
    public ApiResponse<Void> deleteQA(
            @Parameter(description = "Q&A ID") @PathVariable Long qaId) {
        orderQAService.deleteQA(qaId);
        return ApiResponse.success("問答已刪除", null);
    }
}
