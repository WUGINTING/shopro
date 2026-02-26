package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lookup options for admin screens")
public class DashboardLookupsDTO {
    private List<DashboardOptionDTO> roles;
    private List<DashboardOptionDTO> orderStatuses;
    private List<DashboardOptionDTO> paymentMethods;
    private List<DashboardOptionDTO> shippingMethods;
    private List<DashboardOptionDTO> pickupTypes;
}
