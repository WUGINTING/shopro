package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    
    /**
     * 根據分類查詢附件列表
     */
    List<Attachment> findByCategory(String category);
    
    /**
     * 根據上傳者查詢附件列表
     */
    List<Attachment> findByCreatedBy(String createdBy);
}
