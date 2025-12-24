package com.info.ecommerce.config;

import com.info.ecommerce.modules.system.enums.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadProperties {

    /**
     * 儲存類型 (LOCAL 或 S3)
     */
    private StorageType storageType = StorageType.LOCAL;

    /**
     * 本地儲存路徑
     */
    private String localPath = "./uploads";

    /**
     * 最大檔案大小 (bytes)
     */
    private Long maxFileSize = 10485760L; // 10MB

    /**
     * 允許的圖片類型
     */
    private List<String> allowedImageTypes = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    /**
     * 縮圖寬度
     */
    private Integer thumbnailWidth = 200;

    /**
     * 縮圖高度
     */
    private Integer thumbnailHeight = 200;

    /**
     * AWS S3 配置
     */
    private S3Config s3 = new S3Config();

    @Data
    public static class S3Config {
        private String bucketName;
        private String region = "us-east-1";
        private String accessKey;
        private String secretKey;
        private String endpoint;
    }
}
