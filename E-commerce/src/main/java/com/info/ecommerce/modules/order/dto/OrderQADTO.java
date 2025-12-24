package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單問與答資料")
public class OrderQADTO {

    @Schema(description = "Q&A ID")
    private Long id;

    @NotNull(message = "訂單 ID 不能為空")
    @Schema(description = "訂單 ID", required = true)
    private Long orderId;

    @NotNull(message = "提問者類型不能為空")
    @Schema(description = "提問者類型（CUSTOMER, STORE）", required = true)
    private String askerType;

    @Schema(description = "提問者 ID")
    private Long askerId;

    @Schema(description = "提問者名稱")
    private String askerName;

    @NotNull(message = "問題內容不能為空")
    @Schema(description = "問題內容", required = true)
    private String question;

    @Schema(description = "回答內容")
    private String answer;

    @Schema(description = "回答者 ID")
    private Long answererId;

    @Schema(description = "回答者名稱")
    private String answererName;

    @Schema(description = "回答時間")
    private LocalDateTime answeredAt;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
