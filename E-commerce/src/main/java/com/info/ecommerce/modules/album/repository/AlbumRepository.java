package com.info.ecommerce.modules.album.repository;

import com.info.ecommerce.modules.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 相冊 Repository
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
}
