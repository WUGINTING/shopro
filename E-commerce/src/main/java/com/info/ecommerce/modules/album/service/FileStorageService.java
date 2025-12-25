package com.info.ecommerce.modules.album.service;

import com.info.ecommerce.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 檔案儲存服務
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    /**
     * 儲存上傳的檔案
     */
    public String storeFile(MultipartFile file) {
        // 驗證檔案
        if (file.isEmpty()) {
            throw new BusinessException("上傳的檔案為空");
        }

        // 驗證檔案類型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上傳圖片檔案");
        }

        try {
            // 創建上傳目錄
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一檔案名稱
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 儲存檔案
            Path filePath = uploadPath.resolve(filename);
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
            Path filePath = Paths.get(uploadDir).resolve(filename);
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
        return Paths.get(uploadDir).resolve(filename);
    }
}
