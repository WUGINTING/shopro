package com.info.ecommerce.modules.system.dto;

import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "管理員通知")
public class AdminNotificationDTO {

    private Long id;
    private AdminNotificationType type;
    private Long orderId;
    private Long productId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean read;
}
