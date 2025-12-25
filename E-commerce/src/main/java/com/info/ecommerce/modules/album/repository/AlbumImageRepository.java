package com.info.ecommerce.modules.album.repository;

import com.info.ecommerce.modules.album.entity.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 相冊圖片 Repository
 */
@Repository
public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {
    
    List<AlbumImage> findByAlbumIdOrderBySortOrderAsc(Long albumId);
    
    void deleteByAlbumId(Long albumId);
}
