package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "購物車未結帳提醒資訊")
public class CartReminderDTO {

    @Schema(description = "提醒 ID", example = "1")
    private Long id;

    @NotNull(message = "會員 ID 不可為空")
    @Schema(description = "會員 ID", example = "1")
    private Long memberId;

    @Schema(description = "購物車內容摘要")
    private String cartSummary;

    @NotNull(message = "購物車商品數量不可為空")
    @Min(value = 1, message = "購物車商品數量必須大於 0")
    @Schema(description = "購物車商品數量", example = "3")
    private Integer itemCount;

    @Schema(description = "是否已發送提醒", example = "false")
    private Boolean isSent;

    @Schema(description = "提醒發送時間")
    private LocalDateTime sentAt;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;
}
