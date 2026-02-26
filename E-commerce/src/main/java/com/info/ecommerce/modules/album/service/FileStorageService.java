package com.info.ecommerce.modules.album.service;

import com.info.ecommerce.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * 檔案儲存服務
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    /**
     * 儲存上傳的檔案
     */
    public String storeFile(MultipartFile file) {
        // 驗證檔案
        if (file.isEmpty()) {
            throw new BusinessException("上傳的檔案為空");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new BusinessException("圖片大小不可超過 10MB");
        }

        // 驗證檔案類型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上傳圖片檔案");
        }

        try {
            // 創建上傳目錄
            Path uploadPath = resolveUploadPath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一檔案名稱
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            extension = extension.toLowerCase();
            if (extension.contains("..") || extension.contains("/") || extension.contains("\\")) {
                throw new BusinessException("不合法的檔案名稱");
            }
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BusinessException("不支援的圖片格式，僅支援 JPG / JPEG / PNG / GIF / WEBP");
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 儲存檔案
            Path filePath = uploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(uploadPath)) {
                throw new BusinessException("不合法的檔案路徑");
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("檔案已儲存: {}", filename);
            return filename;

        } catch (IOException e) {
            log.error("檔案儲存失敗", e);
            throw new BusinessException("檔案儲存失敗: " + e.getMessage());
        }
    }

    /**
     * 刪除檔案
     */
    public void deleteFile(String filename) {
        try {
            if (!StringUtils.hasText(filename)) {
                return;
            }
            Path uploadPath = resolveUploadPath();
            Path filePath = uploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(uploadPath)) {
                throw new BusinessException("不合法的檔案路徑");
            }
            Files.deleteIfExists(filePath);
            log.info("檔案已刪除: {}", filename);
        } catch (IOException e) {
            log.error("檔案刪除失敗", e);
            throw new BusinessException("檔案刪除失敗: " + e.getMessage());
        }
    }

    /**
     * 取得檔案路徑
     */
    public Path getFilePath(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new BusinessException("檔案名稱不能為空");
        }
        Path uploadPath = resolveUploadPath();
        Path filePath = uploadPath.resolve(filename).normalize();
        if (!filePath.startsWith(uploadPath)) {
            throw new BusinessException("不合法的檔案路徑");
        }
        log.debug("解析圖片路徑: filename={}, uploadPath={}, filePath={}, exists={}",
                filename, uploadPath, filePath, Files.exists(filePath));
        return filePath;
    }

    private Path resolveUploadPath() {
        Path configuredPath = Paths.get(uploadDir);
        if (configuredPath.isAbsolute()) {
            log.info("[resolveUploadPath] 使用絕對路徑: {}", configuredPath.normalize());
            return configuredPath.normalize();
        }

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path cwdCandidate = cwd.resolve(configuredPath).normalize();
        Path moduleDir = cwd.resolve("E-commerce").normalize();
        Path moduleCandidate = moduleDir.resolve(configuredPath).normalize();

        log.info("[resolveUploadPath] 工作目錄: {}", cwd);
        log.info("[resolveUploadPath] 候選路徑1 (cwd): {} (存在: {})", cwdCandidate, Files.exists(cwdCandidate));
        log.info("[resolveUploadPath] 候選路徑2 (module): {} (存在: {})", moduleCandidate, Files.exists(moduleCandidate));

        if (Files.exists(moduleDir) && (!Files.exists(cwdCandidate) || Files.exists(moduleCandidate))) {
            log.info("[resolveUploadPath] 選擇 module 路徑: {}", moduleCandidate);
            return moduleCandidate;
        }

        log.info("[resolveUploadPath] 選擇 cwd 路徑: {}", cwdCandidate);
        return cwdCandidate;
    }
}
