package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 縮圖處理服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailService {

    private final FileUploadProperties fileUploadProperties;

    /**
     * 異步生成縮圖
     * 
     * @param originalFilePath 原始檔案路徑
     * @param thumbnailFilePath 縮圖檔案路徑
     */
    @Async
    public void generateThumbnailAsync(String originalFilePath, String thumbnailFilePath) {
        try {
            log.info("開始生成縮圖：{}", thumbnailFilePath);
            
            File originalFile = new File(originalFilePath);
            File thumbnailFile = new File(thumbnailFilePath);
            
            // 確保縮圖目錄存在
            File thumbnailDir = thumbnailFile.getParentFile();
            if (thumbnailDir != null && !thumbnailDir.exists()) {
                thumbnailDir.mkdirs();
            }
            
            // 生成縮圖
            Thumbnails.of(originalFile)
                    .size(fileUploadProperties.getThumbnailWidth(), 
                          fileUploadProperties.getThumbnailHeight())
                    .keepAspectRatio(true)
                    .toFile(thumbnailFile);
            
            log.info("縮圖生成成功：{}", thumbnailFilePath);
        } catch (IOException e) {
            log.error("縮圖生成失敗：{}", thumbnailFilePath, e);
        }
    }

    /**
     * 同步生成縮圖
     * 
     * @param originalFilePath 原始檔案路徑
     * @param thumbnailFilePath 縮圖檔案路徑
     * @throws IOException IO異常
     */
    public void generateThumbnail(String originalFilePath, String thumbnailFilePath) throws IOException {
        File originalFile = new File(originalFilePath);
        File thumbnailFile = new File(thumbnailFilePath);
        
        // 確保縮圖目錄存在
        File thumbnailDir = thumbnailFile.getParentFile();
        if (thumbnailDir != null && !thumbnailDir.exists()) {
            thumbnailDir.mkdirs();
        }
        
        // 生成縮圖
        Thumbnails.of(originalFile)
                .size(fileUploadProperties.getThumbnailWidth(), 
                      fileUploadProperties.getThumbnailHeight())
                .keepAspectRatio(true)
                .toFile(thumbnailFile);
    }
}
