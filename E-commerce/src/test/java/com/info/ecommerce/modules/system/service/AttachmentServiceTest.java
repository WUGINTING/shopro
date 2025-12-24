package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.config.FileUploadProperties;
import com.info.ecommerce.modules.system.dto.AttachmentDTO;
import com.info.ecommerce.modules.system.dto.UploadResponseDTO;
import com.info.ecommerce.modules.system.entity.Attachment;
import com.info.ecommerce.modules.system.enums.StorageType;
import com.info.ecommerce.modules.system.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AttachmentService
 */
@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private FileUploadProperties fileUploadProperties;

    @Mock
    private ThumbnailService thumbnailService;

    @InjectMocks
    private AttachmentService attachmentService;

    @TempDir
    Path tempDir;

    private FileUploadProperties.S3Config s3Config;

    @BeforeEach
    void setUp() {
        // Setup default file upload properties using lenient() to avoid unnecessary stubbing errors
        s3Config = new FileUploadProperties.S3Config();
        
        lenient().when(fileUploadProperties.getStorageType()).thenReturn(StorageType.LOCAL);
        lenient().when(fileUploadProperties.getLocalPath()).thenReturn(tempDir.toString());
        lenient().when(fileUploadProperties.getMaxFileSize()).thenReturn(10485760L); // 10MB
        lenient().when(fileUploadProperties.getAllowedImageTypes()).thenReturn(
                Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp"));
        lenient().when(fileUploadProperties.getThumbnailWidth()).thenReturn(200);
        lenient().when(fileUploadProperties.getThumbnailHeight()).thenReturn(200);
        lenient().when(fileUploadProperties.getS3()).thenReturn(s3Config);
    }

    @Test
    void testUploadSingleFile_Success() throws IOException {
        // Arrange
        byte[] imageContent = createTestJpegContent();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );

        Attachment savedAttachment = Attachment.builder()
                .id(1L)
                .fileName("test-image.jpg")
                .filePath("2024/01/01/test-uuid.jpg")
                .fileSize((long) imageContent.length)
                .fileType("image/jpeg")
                .category("TestCategory")
                .thumbnailPath("2024/01/01/test-uuid_thumb.jpg")
                .createdBy("testUser")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);

        // Act
        AttachmentDTO result = attachmentService.uploadSingleFile(file, "TestCategory", "testUser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("test-image.jpg");
        assertThat(result.getFileType()).isEqualTo("image/jpeg");
        assertThat(result.getCategory()).isEqualTo("TestCategory");
        assertThat(result.getCreatedBy()).isEqualTo("testUser");

        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(thumbnailService, times(1)).generateThumbnailAsync(anyString(), anyString());
    }

    @Test
    void testUploadSingleFile_EmptyFile_ThrowsException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        assertThatThrownBy(() -> attachmentService.uploadSingleFile(emptyFile, "TestCategory", "testUser"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("檔案不能為空");

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void testUploadSingleFile_FileTooLarge_ThrowsException() {
        // Arrange
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        // Act & Assert
        assertThatThrownBy(() -> attachmentService.uploadSingleFile(largeFile, "TestCategory", "testUser"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("檔案大小超過限制");

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void testUploadFiles_MultipleFiles() {
        // Arrange
        byte[] imageContent = createTestJpegContent();
        
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "image1.jpg",
                "image/jpeg",
                imageContent
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "image2.jpg",
                "image/jpeg",
                imageContent
        );

        MultipartFile[] files = {file1, file2};

        Attachment savedAttachment1 = Attachment.builder()
                .id(1L)
                .fileName("image1.jpg")
                .filePath("2024/01/01/uuid1.jpg")
                .fileSize((long) imageContent.length)
                .fileType("image/jpeg")
                .category("Gallery")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        Attachment savedAttachment2 = Attachment.builder()
                .id(2L)
                .fileName("image2.jpg")
                .filePath("2024/01/01/uuid2.jpg")
                .fileSize((long) imageContent.length)
                .fileType("image/jpeg")
                .category("Gallery")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentRepository.save(any(Attachment.class)))
                .thenReturn(savedAttachment1)
                .thenReturn(savedAttachment2);

        // Act
        UploadResponseDTO result = attachmentService.uploadFiles(files, "Gallery", "user123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSuccessFiles()).hasSize(2);
        assertThat(result.getFailedFiles()).isEmpty();
        
        verify(attachmentRepository, times(2)).save(any(Attachment.class));
    }

    @Test
    void testGetAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .fileName("test.jpg")
                .filePath("2024/01/01/test.jpg")
                .fileSize(1024L)
                .fileType("image/jpeg")
                .category("Test")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

        // Act
        AttachmentDTO result = attachmentService.getAttachment(attachmentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(attachmentId);
        assertThat(result.getFileName()).isEqualTo("test.jpg");

        verify(attachmentRepository, times(1)).findById(attachmentId);
    }

    @Test
    void testGetAttachment_NotFound_ThrowsException() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attachmentService.getAttachment(attachmentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("附件不存在");

        verify(attachmentRepository, times(1)).findById(attachmentId);
    }

    @Test
    void testGetAttachmentsByCategory() {
        // Arrange
        String category = "UserAvatar";
        List<Attachment> attachments = Arrays.asList(
                Attachment.builder()
                        .id(1L)
                        .fileName("avatar1.jpg")
                        .filePath("2024/01/01/avatar1.jpg")
                        .fileSize(1024L)
                        .fileType("image/jpeg")
                        .category(category)
                        .createdBy("user1")
                        .createdAt(LocalDateTime.now())
                        .build(),
                Attachment.builder()
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

        when(attachmentRepository.findByCategory(category)).thenReturn(attachments);

        // Act
        List<AttachmentDTO> result = attachmentService.getAttachmentsByCategory(category);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategory()).isEqualTo(category);
        assertThat(result.get(1).getCategory()).isEqualTo(category);

        verify(attachmentRepository, times(1)).findByCategory(category);
    }

    @Test
    void testDeleteAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .fileName("test.jpg")
                .filePath("2024/01/01/test.jpg")
                .fileSize(1024L)
                .fileType("image/jpeg")
                .category("Test")
                .thumbnailPath("2024/01/01/test_thumb.jpg")
                .createdBy("user123")
                .createdAt(LocalDateTime.now())
                .build();

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

        // Act
        attachmentService.deleteAttachment(attachmentId);

        // Assert
        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, times(1)).delete(attachment);
    }

    @Test
    void testDeleteAttachment_NotFound_ThrowsException() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attachmentService.deleteAttachment(attachmentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("附件不存在");

        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, never()).delete(any());
    }

    /**
     * Create a minimal valid JPEG content for testing
     */
    private byte[] createTestJpegContent() {
        // JPEG file signature (SOI marker) followed by minimal valid structure
        return new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, // SOI + APP0 marker
                0x00, 0x10, // APP0 segment length
                0x4A, 0x46, 0x49, 0x46, 0x00, // "JFIF" identifier
                0x01, 0x01, // JFIF version
                0x00, // density units
                0x00, 0x01, 0x00, 0x01, // X and Y density
                0x00, 0x00, // thumbnail size
                (byte) 0xFF, (byte) 0xD9 // EOI marker
        };
    }
}
