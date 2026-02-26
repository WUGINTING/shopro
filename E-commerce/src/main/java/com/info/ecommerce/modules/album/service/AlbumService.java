package com.info.ecommerce.modules.album.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.album.dto.AlbumDTO;
import com.info.ecommerce.modules.album.dto.AlbumImageDTO;
import com.info.ecommerce.modules.album.entity.Album;
import com.info.ecommerce.modules.album.entity.AlbumImage;
import com.info.ecommerce.modules.album.repository.AlbumImageRepository;
import com.info.ecommerce.modules.album.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 相冊服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumImageRepository albumImageRepository;
    private final FileStorageService fileStorageService;

    /**
     * 創建相冊
     */
    @Transactional
    public AlbumDTO createAlbum(AlbumDTO dto) {
        Album album = new Album();
        BeanUtils.copyProperties(dto, album, "id", "images", "imageCount", "createdAt", "updatedAt");
        album = albumRepository.save(album);
        return toDTO(album);
    }

    /**
     * 更新相冊
     */
    @Transactional
    public AlbumDTO updateAlbum(Long id, AlbumDTO dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        BeanUtils.copyProperties(dto, album, "id", "images", "imageCount", "createdAt", "updatedAt");
        album = albumRepository.save(album);
        return toDTO(album);
    }

    /**
     * 取得相冊詳情
     */
    public AlbumDTO getAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException("相冊不存在"));
        return toDTO(album);
    }

    /**
     * 刪除相冊
     */
    @Transactional
    public void deleteAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        // 刪除所有圖片檔案
        for (AlbumImage image : album.getImages()) {
            try {
                String filename = extractFilename(image.getImageUrl());
                fileStorageService.deleteFile(filename);
            } catch (Exception e) {
                // 記錄錯誤但繼續處理，避免因單個檔案刪除失敗而阻止整個相冊刪除
                log.warn("刪除圖片檔案失敗: {}, 錯誤: {}", image.getFileName(), e.getMessage());
            }
        }

        albumRepository.delete(album);
    }

    /**
     * 分頁查詢相冊
     */
    public Page<AlbumDTO> listAlbums(Pageable pageable) {
        return albumRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 上傳圖片到相冊
     */
    @Transactional
    public AlbumImageDTO uploadImage(Long albumId, MultipartFile file, String title, String description) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        String filename = null;
        try {
            // 儲存檔案
            filename = fileStorageService.storeFile(file);
            String imageUrl = "/api/albums/images/" + filename;

            // 創建圖片記錄
            AlbumImage image = AlbumImage.builder()
                    .album(album)
                    .imageUrl(imageUrl)
                    .fileName(filename)
                    .title(title)
                    .description(description)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .sortOrder(album.getImages().size())
                    .build();

            album.addImage(image);
            albumRepository.saveAndFlush(album);

            // 如果相冊沒有封面，設定第一張圖片為封面
            if (album.getCoverImageUrl() == null || album.getCoverImageUrl().isEmpty()) {
                album.setCoverImageUrl(imageUrl);
                albumRepository.saveAndFlush(album);
            }

            return toImageDTO(image);
        } catch (Exception e) {
            // DB 寫入失敗時回滾已儲存的實體檔，避免殘留孤兒檔案
            if (filename != null) {
                try {
                    fileStorageService.deleteFile(filename);
                } catch (Exception cleanupError) {
                    log.warn("圖片上傳失敗後清理檔案失敗: {}, 錯誤: {}", filename, cleanupError.getMessage());
                }
            }
            throw e;
        }
    }

    /**
     * 刪除相冊中的圖片
     */
    @Transactional
    public void deleteImage(Long albumId, Long imageId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        AlbumImage image = albumImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("圖片不存在"));

        if (!image.getAlbum().getId().equals(albumId)) {
            throw new BusinessException("圖片不屬於此相冊");
        }

        // 刪除檔案
        try {
            String filename = extractFilename(image.getImageUrl());
            fileStorageService.deleteFile(filename);
        } catch (Exception e) {
            // 記錄錯誤但繼續處理，避免因檔案刪除失敗而阻止圖片記錄刪除
            log.warn("刪除圖片檔案失敗: {}, 錯誤: {}", image.getFileName(), e.getMessage());
        }

        album.removeImage(image);
        albumImageRepository.delete(image);

        // 如果刪除的是封面圖片，更新封面
        if (image.getImageUrl().equals(album.getCoverImageUrl())) {
            if (!album.getImages().isEmpty()) {
                album.setCoverImageUrl(album.getImages().get(0).getImageUrl());
            } else {
                album.setCoverImageUrl(null);
            }
            albumRepository.save(album);
        }
    }

    /**
     * 設定相冊封面圖片
     */
    @Transactional
    public AlbumDTO setCoverImage(Long albumId, Long imageId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        AlbumImage image = albumImageRepository.findByIdAndAlbumId(imageId, albumId)
                .orElseThrow(() -> new BusinessException("圖片不存在或不屬於此相冊"));

        album.setCoverImageUrl(image.getImageUrl());
        albumRepository.save(album);
        return toDTO(album);
    }

    /**
     * 更新相冊圖片排序（以 imageIds 的順序為準）
     */
    @Transactional
    public List<AlbumImageDTO> reorderImages(Long albumId, List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            throw new BusinessException("排序資料不能為空");
        }

        albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException("相冊不存在"));

        List<AlbumImage> images = albumImageRepository.findByAlbumIdOrderBySortOrderAsc(albumId);
        if (images.isEmpty()) {
            throw new BusinessException("相冊中沒有可排序的圖片");
        }

        if (images.size() != imageIds.size()) {
            throw new BusinessException("排序圖片數量不一致");
        }

        Set<Long> existingIds = images.stream().map(AlbumImage::getId).collect(Collectors.toSet());
        Set<Long> requestedIds = Set.copyOf(imageIds);
        if (!existingIds.equals(requestedIds)) {
            throw new BusinessException("排序資料包含無效圖片或缺少圖片");
        }

        Map<Long, AlbumImage> imageMap = images.stream()
                .collect(Collectors.toMap(AlbumImage::getId, image -> image));

        for (int i = 0; i < imageIds.size(); i++) {
            AlbumImage image = imageMap.get(imageIds.get(i));
            image.setSortOrder(i);
        }

        List<AlbumImage> saved = albumImageRepository.saveAll(images);
        return saved.stream()
                .sorted(java.util.Comparator.comparing(AlbumImage::getSortOrder, java.util.Comparator.nullsLast(Integer::compareTo)))
                .map(this::toImageDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得相冊中的所有圖片
     */
    public List<AlbumImageDTO> getAlbumImages(Long albumId) {
        List<AlbumImage> images = albumImageRepository.findByAlbumIdOrderBySortOrderAsc(albumId);
        return images.stream().map(this::toImageDTO).collect(Collectors.toList());
    }

    /**
     * 從 URL 中提取檔案名稱
     */
    private String extractFilename(String url) {
        if (url == null) {
            return null;
        }
        int lastSlash = url.lastIndexOf('/');
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }

    /**
     * 轉換為 DTO
     */
    private AlbumDTO toDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        BeanUtils.copyProperties(album, dto);
        dto.setImageCount(album.getImages().size());
        dto.setImages(album.getImages().stream().map(this::toImageDTO).collect(Collectors.toList()));
        return dto;
    }

    /**
     * 轉換圖片為 DTO
     */
    private AlbumImageDTO toImageDTO(AlbumImage image) {
        AlbumImageDTO dto = new AlbumImageDTO();
        BeanUtils.copyProperties(image, dto);
        dto.setAlbumId(image.getAlbum().getId());
        return dto;
    }
}
