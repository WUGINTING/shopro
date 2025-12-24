package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.config.FileUploadProperties;
import com.info.ecommerce.modules.system.dto.AttachmentDTO;
import com.info.ecommerce.modules.system.dto.UploadResponseDTO;
import com.info.ecommerce.modules.system.entity.Attachment;
import com.info.ecommerce.modules.system.enums.StorageType;
import com.info.ecommerce.modules.system.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 附件管理服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileUploadProperties fileUploadProperties;
    private final ThumbnailService thumbnailService;
    private final Tika tika = new Tika();

    /**
     * 上傳多個檔案
     *
     * @param files 檔案列表
     * @param category 用途分類
     * @param createdBy 上傳者 ID
     * @return 上傳結果
     */
    @Transactional
    public UploadResponseDTO uploadFiles(MultipartFile[] files, String category, String createdBy) {
        List<AttachmentDTO> successFiles = new ArrayList<>();
        List<UploadResponseDTO.FailedFileDTO> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                AttachmentDTO dto = uploadSingleFile(file, category, createdBy);
                successFiles.add(dto);
            } catch (Exception e) {
                log.error("檔案上傳失敗：{}", file.getOriginalFilename(), e);
                failedFiles.add(UploadResponseDTO.FailedFileDTO.builder()
                        .fileName(file.getOriginalFilename())
                        .reason(e.getMessage())
                        .build());
            }
        }

        return UploadResponseDTO.builder()
                .successFiles(successFiles)
                .failedFiles(failedFiles)
                .build();
    }

    /**
     * 上傳單個檔案
     *
     * @param file 檔案
     * @param category 用途分類
     * @param createdBy 上傳者 ID
     * @return 附件資訊
     */
    @Transactional
    public AttachmentDTO uploadSingleFile(MultipartFile file, String category, String createdBy) throws IOException {
        // 1. 檔案驗證
        validateFile(file);

        // 2. 檢查 Magic Number（安全性檢查）
        String detectedMimeType = detectMimeType(file);
        if (!fileUploadProperties.getAllowedImageTypes().contains(detectedMimeType)) {
            throw new BusinessException("不支援的檔案類型：" + detectedMimeType);
        }

        // 3. 生成唯一檔名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 4. 儲存檔案
        String filePath = saveFile(file, uniqueFileName);

        // 5. 生成縮圖（異步處理）
        String thumbnailPath = null;
        if (isImage(detectedMimeType)) {
            thumbnailPath = generateThumbnailPath(uniqueFileName);
            String fullThumbnailPath = getFullPath(thumbnailPath);
            
            if (fileUploadProperties.getStorageType() == StorageType.LOCAL) {
                String fullFilePath = getFullPath(filePath);
                thumbnailService.generateThumbnailAsync(fullFilePath, fullThumbnailPath);
            }
        }

        // 6. 儲存到資料庫
        Attachment attachment = Attachment.builder()
                .fileName(originalFilename)
                .filePath(filePath)
                .fileSize(file.getSize())
                .fileType(detectedMimeType)
                .category(category)
                .thumbnailPath(thumbnailPath)
                .createdBy(createdBy)
                .build();

        attachment = attachmentRepository.save(attachment);

        // 7. 回傳 DTO
        return convertToDTO(attachment);
    }

    /**
     * 驗證檔案
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("檔案不能為空");
        }

        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            throw new BusinessException("檔案大小超過限制，最大允許 " + 
                    (fileUploadProperties.getMaxFileSize() / 1024 / 1024) + "MB");
        }
    }

    /**
     * 使用 Apache Tika 檢測 MIME 類型（Magic Number 檢查）
     */
    private String detectMimeType(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.detect(inputStream, file.getOriginalFilename());
        }
    }

    /**
     * 儲存檔案
     */
    private String saveFile(MultipartFile file, String uniqueFileName) throws IOException {
        if (fileUploadProperties.getStorageType() == StorageType.LOCAL) {
            return saveToLocal(file, uniqueFileName);
        } else {
            return saveToS3(file, uniqueFileName);
        }
    }

    /**
     * 儲存到本地
     */
    private String saveToLocal(MultipartFile file, String uniqueFileName) throws IOException {
        // 按日期組織目錄結構：/uploads/2024/01/01/
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + uniqueFileName;
        
        Path uploadPath = Paths.get(fileUploadProperties.getLocalPath(), datePath);
        Files.createDirectories(uploadPath);
        
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return relativePath;
    }

    /**
     * 儲存到 S3（預留實現）
     */
    private String saveToS3(MultipartFile file, String uniqueFileName) throws IOException {
        // TODO: 實現 S3 上傳邏輯
        throw new BusinessException("S3 上傳功能尚未實現");
    }

    /**
     * 生成縮圖路徑
     */
    private String generateThumbnailPath(String uniqueFileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String nameWithoutExt = uniqueFileName.substring(0, uniqueFileName.lastIndexOf('.'));
        String extension = uniqueFileName.substring(uniqueFileName.lastIndexOf('.'));
        return datePath + "/" + nameWithoutExt + "_thumb" + extension;
    }

    /**
     * 獲取完整路徑
     */
    private String getFullPath(String relativePath) {
        return Paths.get(fileUploadProperties.getLocalPath(), relativePath).toString();
    }

    /**
     * 獲取檔案副檔名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 判斷是否為圖片
     */
    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * 轉換為 DTO
     */
    private AttachmentDTO convertToDTO(Attachment attachment) {
        return AttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .fileSize(attachment.getFileSize())
                .fileType(attachment.getFileType())
                .category(attachment.getCategory())
                .thumbnailPath(attachment.getThumbnailPath())
                .createdBy(attachment.getCreatedBy())
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    /**
     * 根據 ID 查詢附件
     */
    public AttachmentDTO getAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("附件不存在"));
        return convertToDTO(attachment);
    }

    /**
     * 根據分類查詢附件列表
     */
    public List<AttachmentDTO> getAttachmentsByCategory(String category) {
        List<Attachment> attachments = attachmentRepository.findByCategory(category);
        return attachments.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 刪除附件
     */
    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("附件不存在"));

        // 刪除實體檔案
        if (fileUploadProperties.getStorageType() == StorageType.LOCAL) {
            deleteLocalFile(attachment.getFilePath());
            if (attachment.getThumbnailPath() != null) {
                deleteLocalFile(attachment.getThumbnailPath());
            }
        }

        // 刪除資料庫記錄
        attachmentRepository.delete(attachment);
    }

    /**
     * 刪除本地檔案
     */
    private void deleteLocalFile(String relativePath) {
        try {
            Path filePath = Paths.get(fileUploadProperties.getLocalPath(), relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("刪除檔案失敗：{}", relativePath, e);
        }
    }
}
