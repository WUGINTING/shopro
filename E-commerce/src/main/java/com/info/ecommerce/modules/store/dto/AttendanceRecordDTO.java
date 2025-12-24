package com.info.ecommerce.modules.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "打卡紀錄")
public class AttendanceRecordDTO {

    @Schema(description = "紀錄 ID", example = "1")
    private Long id;

    @NotNull(message = "員工 ID 不可為空")
    @Schema(description = "員工 ID", example = "1")
    private Long staffId;

    @Schema(description = "員工姓名", accessMode = Schema.AccessMode.READ_ONLY)
    private String staffName;

    @NotNull(message = "工作日期不可為空")
    @Schema(description = "工作日期", example = "2024-01-15")
    private LocalDate workDate;

    @Schema(description = "上班打卡時間", example = "2024-01-15T09:00:00")
    private LocalDateTime clockIn;

    @Schema(description = "下班打卡時間", example = "2024-01-15T18:00:00")
    private LocalDateTime clockOut;

    @Schema(description = "備註", example = "遲到原因：交通阻塞")
    private String note;
}
