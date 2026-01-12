package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.BlogPostDTO;
import com.info.ecommerce.modules.crm.entity.BlogPost;
import com.info.ecommerce.modules.crm.enums.BlogStatus;
import com.info.ecommerce.modules.crm.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    @Transactional
    public BlogPostDTO createBlogPost(BlogPostDTO dto) {
        if (blogPostRepository.existsBySlug(dto.getSlug())) {
            throw new BusinessException("文章別名已存在");
        }

        BlogPost blogPost = new BlogPost();
        BeanUtils.copyProperties(dto, blogPost, "id");
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    @Transactional
    public BlogPostDTO updateBlogPost(Long id, BlogPostDTO dto) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));

        if (!blogPost.getSlug().equals(dto.getSlug()) && blogPostRepository.existsBySlug(dto.getSlug())) {
            throw new BusinessException("文章別名已存在");
        }

        BeanUtils.copyProperties(dto, blogPost, "id", "createdAt", "updatedAt", "viewCount");
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    public BlogPostDTO getBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));
        return toDTO(blogPost);
    }

    @Transactional
    public BlogPostDTO getBlogPostBySlug(String slug) {
        BlogPost blogPost = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));
        
        // 增加瀏覽次數
        blogPost.setViewCount(blogPost.getViewCount() + 1);
        blogPost = blogPostRepository.save(blogPost);
        
        return toDTO(blogPost);
    }

    @Transactional
    public void deleteBlogPost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new BusinessException("部落格文章不存在");
        }
        blogPostRepository.deleteById(id);
    }

    public Page<BlogPostDTO> listBlogPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<BlogPostDTO> listBlogPostsByStatus(BlogStatus status, Pageable pageable) {
        return blogPostRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    public Page<BlogPostDTO> listBlogPostsByAuthor(Long authorId, Pageable pageable) {
        return blogPostRepository.findByAuthorId(authorId, pageable).map(this::toDTO);
    }

    public Page<BlogPostDTO> listBlogPostsByTag(String tag, Pageable pageable) {
        return blogPostRepository.findByTagsContaining(tag, pageable).map(this::toDTO);
    }

    @Transactional
    public BlogPostDTO publishBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));

        if (blogPost.getStatus() == BlogStatus.PUBLISHED) {
            throw new BusinessException("文章已發布");
        }

        blogPost.setStatus(BlogStatus.PUBLISHED);
        blogPost.setPublishedAt(LocalDateTime.now());
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    @Transactional
    public BlogPostDTO scheduleBlogPost(Long id, LocalDateTime scheduledAt) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));

        if (blogPost.getStatus() != BlogStatus.DRAFT) {
            throw new BusinessException("只有草稿狀態的文章可以排程");
        }

        blogPost.setScheduledAt(scheduledAt);
        blogPost.setStatus(BlogStatus.SCHEDULED);
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    @Transactional
    public BlogPostDTO archiveBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));

        blogPost.setStatus(BlogStatus.ARCHIVED);
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    @Transactional
    public BlogPostDTO scheduleUnpublishBlogPost(Long id, LocalDateTime scheduledUnpublishAt) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部落格文章不存在"));

        if (blogPost.getStatus() != BlogStatus.PUBLISHED && blogPost.getStatus() != BlogStatus.SCHEDULED) {
            throw new BusinessException("只有已發布或排程中的文章可以排程下架");
        }

        blogPost.setScheduledUnpublishAt(scheduledUnpublishAt);
        blogPost = blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    /**
     * 處理排程上架的文章
     * 將狀態為 SCHEDULED 且 scheduledAt 時間已到的文章改為 PUBLISHED
     */
    @Transactional
    public int processScheduledPublish() {
        LocalDateTime now = LocalDateTime.now();
        List<BlogPost> scheduledPosts = blogPostRepository.findByStatusAndScheduledAtLessThanEqual(
                BlogStatus.SCHEDULED, now);
        
        int count = 0;
        for (BlogPost post : scheduledPosts) {
            post.setStatus(BlogStatus.PUBLISHED);
            post.setPublishedAt(now);
            blogPostRepository.save(post);
            count++;
        }
        
        return count;
    }

    /**
     * 處理排程下架的文章
     * 將狀態為 PUBLISHED 或 SCHEDULED 且 scheduledUnpublishAt 時間已到的文章改為 ARCHIVED
     */
    @Transactional
    public int processScheduledUnpublish() {
        LocalDateTime now = LocalDateTime.now();
        List<BlogStatus> statuses = List.of(BlogStatus.PUBLISHED, BlogStatus.SCHEDULED);
        List<BlogPost> postsToUnpublish = blogPostRepository.findByStatusInAndScheduledUnpublishAtLessThanEqual(
                statuses, now);
        
        int count = 0;
        for (BlogPost post : postsToUnpublish) {
            if (post.getScheduledUnpublishAt() != null) {
                post.setStatus(BlogStatus.ARCHIVED);
                blogPostRepository.save(post);
                count++;
            }
        }
        
        return count;
    }

    private BlogPostDTO toDTO(BlogPost blogPost) {
        BlogPostDTO dto = new BlogPostDTO();
        BeanUtils.copyProperties(blogPost, dto);
        return dto;
    }
}
