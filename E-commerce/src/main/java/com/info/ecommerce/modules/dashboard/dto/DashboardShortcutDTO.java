package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Quick shortcut card for admin UI")
public class DashboardShortcutDTO {
    private String key;
    private String label;
    private String route;
    private String description;
    private Long badgeCount;
}
