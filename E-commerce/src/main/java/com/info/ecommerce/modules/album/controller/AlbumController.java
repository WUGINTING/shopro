package com.info.ecommerce.modules.album.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.album.dto.AlbumDTO;
import com.info.ecommerce.modules.album.dto.AlbumImageDTO;
import com.info.ecommerce.modules.album.service.AlbumService;
import com.info.ecommerce.modules.album.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * 相冊管理控制器
 */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@Tag(name = "相冊管理", description = "相冊與圖片管理功能")
public class AlbumController {

    private final AlbumService albumService;
    private final FileStorageService fileStorageService;

    @PostMapping
    @Operation(summary = "創建相冊")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<AlbumDTO> createAlbum(@Valid @RequestBody AlbumDTO dto) {
        return ApiResponse.success("相冊已創建", albumService.createAlbum(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新相冊")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<AlbumDTO> updateAlbum(
            @Parameter(description = "相冊 ID") @PathVariable Long id,
            @Valid @RequestBody AlbumDTO dto) {
        return ApiResponse.success("相冊已更新", albumService.updateAlbum(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得相冊詳情")
    public ApiResponse<AlbumDTO> getAlbum(
            @Parameter(description = "相冊 ID") @PathVariable Long id) {
        return ApiResponse.success(albumService.getAlbum(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除相冊")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteAlbum(
            @Parameter(description = "相冊 ID") @PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ApiResponse.success("相冊已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢相冊")
    public ApiResponse<Page<AlbumDTO>> listAlbums(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(albumService.listAlbums(pageable));
    }

    @PostMapping(value = "/{albumId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上傳圖片到相冊")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<AlbumImageDTO> uploadImage(
            @Parameter(description = "相冊 ID") @PathVariable Long albumId,
            @Parameter(description = "圖片檔案") @RequestParam("file") MultipartFile file,
            @Parameter(description = "圖片標題") @RequestParam(required = false) String title,
            @Parameter(description = "圖片描述") @RequestParam(required = false) String description) {
        AlbumImageDTO imageDTO = albumService.uploadImage(albumId, file, title, description);
        return ApiResponse.success("圖片已上傳", imageDTO);
    }

    @DeleteMapping("/{albumId}/images/{imageId}")
    @Operation(summary = "刪除相冊中的圖片")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Void> deleteImage(
            @Parameter(description = "相冊 ID") @PathVariable Long albumId,
            @Parameter(description = "圖片 ID") @PathVariable Long imageId) {
        albumService.deleteImage(albumId, imageId);
        return ApiResponse.success("圖片已刪除", null);
    }

    @PutMapping("/{albumId}/images/{imageId}/cover")
    @Operation(summary = "設定相冊封面圖片")
    public ApiResponse<AlbumDTO> setCoverImage(
            @Parameter(description = "相冊 ID") @PathVariable Long albumId,
            @Parameter(description = "圖片 ID") @PathVariable Long imageId) {
        return ApiResponse.success("封面圖片已更新", albumService.setCoverImage(albumId, imageId));
    }

    @PutMapping("/{albumId}/images/sort")
    @Operation(summary = "更新相冊圖片排序")
    public ApiResponse<List<AlbumImageDTO>> reorderImages(
            @Parameter(description = "相冊 ID") @PathVariable Long albumId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "依序排列的圖片 ID 清單")
            @RequestBody List<Long> imageIds) {
        return ApiResponse.success("圖片排序已更新", albumService.reorderImages(albumId, imageIds));
    }

    @GetMapping("/{albumId}/images")
    @Operation(summary = "取得相冊中的所有圖片")
    public ApiResponse<List<AlbumImageDTO>> getAlbumImages(
            @Parameter(description = "相冊 ID") @PathVariable Long albumId) {
        return ApiResponse.success(albumService.getAlbumImages(albumId));
    }

    @GetMapping("/images/{filename:.+}")
    @Operation(summary = "下載或查看圖片")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "檔案名稱") @PathVariable String filename) {
        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg"; // 預設類型

                // 根據副檔名判斷內容類型
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFilename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (lowerFilename.endsWith(".webp")) {
                    contentType = "image/webp";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000, public")
                        .body(resource);
            } else {
                // 檔案不存在，記錄警告
                System.err.println("[AlbumController] 圖片檔案不存在: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("[AlbumController] 讀取圖片失敗: " + filename + ", 錯誤: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
