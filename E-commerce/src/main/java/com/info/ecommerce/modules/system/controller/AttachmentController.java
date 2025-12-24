package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.AttachmentDTO;
import com.info.ecommerce.modules.system.dto.UploadResponseDTO;
import com.info.ecommerce.modules.system.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 附件管理控制器
 */
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Tag(name = "附件管理", description = "圖片和附件上傳管理")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上傳檔案", description = "支援多檔案上傳，自動進行安全檢查和縮圖生成")
    public ApiResponse<UploadResponseDTO> uploadFiles(
            @Parameter(description = "上傳的檔案列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "用途分類", example = "UserAvatar") @RequestParam(value = "category", required = false) String category,
            @Parameter(description = "上傳者 ID", example = "user123") @RequestParam(value = "createdBy", required = false) String createdBy) {
        
        UploadResponseDTO response = attachmentService.uploadFiles(files, category, createdBy);
        return ApiResponse.success("檔案上傳完成", response);
    }

    @PostMapping(value = "/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上傳單個檔案", description = "上傳單個檔案，自動進行安全檢查和縮圖生成")
    public ApiResponse<AttachmentDTO> uploadSingleFile(
            @Parameter(description = "上傳的檔案") @RequestParam("file") MultipartFile file,
            @Parameter(description = "用途分類", example = "ProductImage") @RequestParam(value = "category", required = false) String category,
            @Parameter(description = "上傳者 ID", example = "user123") @RequestParam(value = "createdBy", required = false) String createdBy) throws IOException {
        
        AttachmentDTO response = attachmentService.uploadSingleFile(file, category, createdBy);
        return ApiResponse.success("檔案上傳成功", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢附件詳情", description = "根據 ID 查詢附件資訊")
    public ApiResponse<AttachmentDTO> getAttachment(
            @Parameter(description = "附件 ID") @PathVariable Long id) {
        
        AttachmentDTO attachment = attachmentService.getAttachment(id);
        return ApiResponse.success(attachment);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根據分類查詢附件", description = "查詢指定分類的所有附件")
    public ApiResponse<List<AttachmentDTO>> getAttachmentsByCategory(
            @Parameter(description = "用途分類") @PathVariable String category) {
        
        List<AttachmentDTO> attachments = attachmentService.getAttachmentsByCategory(category);
        return ApiResponse.success(attachments);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除附件", description = "刪除指定的附件及其實體檔案")
    public ApiResponse<Void> deleteAttachment(
            @Parameter(description = "附件 ID") @PathVariable Long id) {
        
        attachmentService.deleteAttachment(id);
        return ApiResponse.success("附件刪除成功", null);
    }
}
