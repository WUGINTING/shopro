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
@Schema(description = "顧客黑名單資料")
public class CustomerBlacklistDTO {

    @Schema(description = "黑名單記錄 ID")
    private Long id;

    @NotNull(message = "客戶 ID 不能為空")
    @Schema(description = "客戶 ID", required = true)
    private Long customerId;

    @Schema(description = "客戶姓名")
    private String customerName;

    @Schema(description = "客戶電話")
    private String customerPhone;

    @Schema(description = "客戶電子郵件")
    private String customerEmail;

    @NotNull(message = "黑名單原因不能為空")
    @Schema(description = "黑名單原因", required = true)
    private String reason;

    @Schema(description = "是否啟用")
    private Boolean isActive;

    @Schema(description = "建立者 ID")
    private Long createdBy;

    @Schema(description = "建立者名稱")
    private String createdByName;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
