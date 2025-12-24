package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.BlogPost;
import com.info.ecommerce.modules.crm.enums.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    Optional<BlogPost> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);
    
    Page<BlogPost> findByAuthorId(Long authorId, Pageable pageable);
    
    Page<BlogPost> findByTagsContaining(String tag, Pageable pageable);
}
