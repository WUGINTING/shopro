package com.info.ecommerce.modules.album.service;

import com.info.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeFile_shouldSaveAllowedImageFile() throws Exception {
        FileStorageService service = new FileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.PNG",
                "image/png",
                "fake-image-content".getBytes()
        );

        String filename = service.storeFile(file);

        assertNotNull(filename);
        assertTrue(filename.toLowerCase().endsWith(".png"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void storeFile_shouldRejectUnsupportedExtension() {
        FileStorageService service = new FileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.svg",
                "image/svg+xml",
                "<svg></svg>".getBytes()
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> service.storeFile(file));
        assertTrue(ex.getMessage().contains("不支援的圖片格式"));
    }

    @Test
    void getFilePath_shouldRejectPathTraversal() {
        FileStorageService service = new FileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());

        assertThrows(BusinessException.class, () -> service.getFilePath("../evil.png"));
    }
}

