package com.info.ecommerce.modules.album.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.album.dto.AlbumImageDTO;
import com.info.ecommerce.modules.album.service.AlbumService;
import com.info.ecommerce.modules.album.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private FileStorageService fileStorageService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AlbumController controller = new AlbumController(albumService, fileStorageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void uploadImage_shouldAcceptMultipartAndReturnSuccess() throws Exception {
        AlbumImageDTO dto = AlbumImageDTO.builder()
                .id(10L)
                .albumId(1L)
                .fileName("test.png")
                .imageUrl("/api/albums/images/test.png")
                .build();

        when(albumService.uploadImage(eq(1L), any(MultipartFile.class), eq("封面"), eq("描述")))
                .thenReturn(dto);

        mockMvc.perform(multipart("/api/albums/{albumId}/images", 1L)
                        .file("file", "fake-image".getBytes())
                        .param("title", "封面")
                        .param("description", "描述")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.fileName").value("test.png"));

        verify(albumService).uploadImage(eq(1L), any(MultipartFile.class), eq("封面"), eq("描述"));
    }

    @Test
    void reorderImages_shouldPassOrderedIdsToService() throws Exception {
        when(albumService.reorderImages(eq(3L), eq(List.of(5L, 2L, 9L))))
                .thenReturn(List.of());

        mockMvc.perform(put("/api/albums/{albumId}/images/sort", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(5L, 2L, 9L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(albumService).reorderImages(3L, List.of(5L, 2L, 9L));
    }

    @Test
    void getImage_shouldReturnNotFoundWhenFileMissing() throws Exception {
        when(fileStorageService.getFilePath("missing.png")).thenReturn(Paths.get("not-exists", "missing.png"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/albums/images/{filename}", "missing.png"))
                .andExpect(status().isNotFound());
    }
}

