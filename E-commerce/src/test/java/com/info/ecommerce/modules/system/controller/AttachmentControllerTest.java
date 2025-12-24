package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.modules.system.dto.AttachmentDTO;
import com.info.ecommerce.modules.system.dto.UploadResponseDTO;
import com.info.ecommerce.modules.system.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AttachmentController
 */
@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Test
    void testUploadSingleFile_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        AttachmentDTO responseDTO = AttachmentDTO.builder()
                .id(1L)
                .fileName("test-image.jpg")
                .filePath("2024/01/01/uuid.jpg")
                .fileSize(18L)
                .fileType("image/jpeg")
                .category("TestCategory")
                .thumbnailPath("2024/01/01/uuid_thumb.jpg")
                .createdBy("testUser")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentService.uploadSingleFile(any(), anyString(), anyString()))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/attachments/upload/single")
                        .file(file)
                        .param("category", "TestCategory")
                        .param("createdBy", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("檔案上傳成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.data.fileType").value("image/jpeg"))
                .andExpect(jsonPath("$.data.category").value("TestCategory"));
    }

    @Test
    void testUploadFiles_Success() throws Exception {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "image1.jpg",
                "image/jpeg",
                "test image 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "image2.jpg",
                "image/jpeg",
                "test image 2".getBytes()
        );

        AttachmentDTO dto1 = AttachmentDTO.builder()
                .id(1L)
                .fileName("image1.jpg")
                .filePath("2024/01/01/uuid1.jpg")
                .fileSize(12L)
                .fileType("image/jpeg")
                .category("Gallery")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        AttachmentDTO dto2 = AttachmentDTO.builder()
                .id(2L)
                .fileName("image2.jpg")
                .filePath("2024/01/01/uuid2.jpg")
                .fileSize(12L)
                .fileType("image/jpeg")
                .category("Gallery")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        UploadResponseDTO responseDTO = UploadResponseDTO.builder()
                .successFiles(Arrays.asList(dto1, dto2))
                .failedFiles(Collections.emptyList())
                .build();

        when(attachmentService.uploadFiles(any(), anyString(), anyString()))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/attachments/upload")
                        .file(file1)
                        .file(file2)
                        .param("category", "Gallery")
                        .param("createdBy", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("檔案上傳完成"))
                .andExpect(jsonPath("$.data.successFiles").isArray())
                .andExpect(jsonPath("$.data.successFiles.length()").value(2))
                .andExpect(jsonPath("$.data.failedFiles").isArray())
                .andExpect(jsonPath("$.data.failedFiles.length()").value(0));
    }

    @Test
    void testGetAttachment_Success() throws Exception {
        // Arrange
        Long attachmentId = 1L;
        AttachmentDTO dto = AttachmentDTO.builder()
                .id(attachmentId)
                .fileName("test.jpg")
                .filePath("2024/01/01/test.jpg")
                .fileSize(1024L)
                .fileType("image/jpeg")
                .category("Test")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentService.getAttachment(attachmentId)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/attachments/{id}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fileName").value("test.jpg"));
    }

    @Test
    void testGetAttachmentsByCategory_Success() throws Exception {
        // Arrange
        String category = "UserAvatar";
        List<AttachmentDTO> dtoList = Arrays.asList(
                AttachmentDTO.builder()
                        .id(1L)
                        .fileName("avatar1.jpg")
                        .filePath("2024/01/01/avatar1.jpg")
                        .fileSize(1024L)
                        .fileType("image/jpeg")
                        .category(category)
                        .createdBy("user1")
                        .createdAt(LocalDateTime.now())
                        .build(),
                AttachmentDTO.builder()
                        .id(2L)
                        .fileName("avatar2.jpg")
                        .filePath("2024/01/01/avatar2.jpg")
                        .fileSize(2048L)
                        .fileType("image/png")
                        .category(category)
                        .createdBy("user2")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(attachmentService.getAttachmentsByCategory(category)).thenReturn(dtoList);

        // Act & Assert
        mockMvc.perform(get("/api/attachments/category/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].category").value(category))
                .andExpect(jsonPath("$.data[1].category").value(category));
    }

    @Test
    void testDeleteAttachment_Success() throws Exception {
        // Arrange
        Long attachmentId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/attachments/{id}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("附件刪除成功"));
    }
}
