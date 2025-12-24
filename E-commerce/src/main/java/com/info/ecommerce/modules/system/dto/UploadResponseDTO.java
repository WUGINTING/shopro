package com.info.ecommerce.modules.system.dto;

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
@Schema(description = "批量上傳回應")
public class UploadResponseDTO {

    @Schema(description = "成功上傳的附件列表")
    private List<AttachmentDTO> successFiles;

    @Schema(description = "失敗的檔案列表")
    private List<FailedFileDTO> failedFiles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "失敗的檔案資訊")
    public static class FailedFileDTO {
        @Schema(description = "檔案名稱")
        private String fileName;
        
        @Schema(description = "失敗原因")
        private String reason;
    }
}
