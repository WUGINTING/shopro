package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.BlogPost;
import com.info.ecommerce.modules.crm.enums.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    Optional<BlogPost> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);
    
    Page<BlogPost> findByAuthorId(Long authorId, Pageable pageable);
    
    Page<BlogPost> findByTagsContaining(String tag, Pageable pageable);
    
    // 查詢需要上架的文章（狀態為 SCHEDULED 且 scheduledAt 時間已到）
    List<BlogPost> findByStatusAndScheduledAtLessThanEqual(BlogStatus status, LocalDateTime dateTime);
    
    // 查詢需要下架的文章（狀態為 PUBLISHED 或 SCHEDULED 且 scheduledUnpublishAt 時間已到）
    List<BlogPost> findByStatusInAndScheduledUnpublishAtLessThanEqual(List<BlogStatus> statuses, LocalDateTime dateTime);
}
