package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.BlogPostDTO;
import com.info.ecommerce.modules.crm.enums.BlogStatus;
import com.info.ecommerce.modules.crm.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/crm/blog")
@RequiredArgsConstructor
@Tag(name = "部落格管理", description = "CRM 部落格管理功能")
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    @Operation(summary = "創建部落格文章")
    public ApiResponse<BlogPostDTO> createBlogPost(@Valid @RequestBody BlogPostDTO dto) {
        return ApiResponse.success("部落格文章已創建", blogService.createBlogPost(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部落格文章")
    public ApiResponse<BlogPostDTO> updateBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id,
            @Valid @RequestBody BlogPostDTO dto) {
        return ApiResponse.success("部落格文章已更新", blogService.updateBlogPost(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得部落格文章詳情")
    public ApiResponse<BlogPostDTO> getBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id) {
        return ApiResponse.success(blogService.getBlogPost(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "依別名取得部落格文章")
    public ApiResponse<BlogPostDTO> getBlogPostBySlug(
            @Parameter(description = "文章別名") @PathVariable String slug) {
        return ApiResponse.success(blogService.getBlogPostBySlug(slug));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除部落格文章")
    public ApiResponse<Void> deleteBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id) {
        blogService.deleteBlogPost(id);
        return ApiResponse.success("部落格文章已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢部落格文章")
    public ApiResponse<Page<BlogPostDTO>> listBlogPosts(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(blogService.listBlogPosts(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "依狀態查詢部落格文章")
    public ApiResponse<Page<BlogPostDTO>> listBlogPostsByStatus(
            @Parameter(description = "文章狀態") @PathVariable BlogStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(blogService.listBlogPostsByStatus(status, pageable));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "依作者查詢部落格文章")
    public ApiResponse<Page<BlogPostDTO>> listBlogPostsByAuthor(
            @Parameter(description = "作者 ID") @PathVariable Long authorId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(blogService.listBlogPostsByAuthor(authorId, pageable));
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "依標籤查詢部落格文章")
    public ApiResponse<Page<BlogPostDTO>> listBlogPostsByTag(
            @Parameter(description = "標籤") @PathVariable String tag,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(blogService.listBlogPostsByTag(tag, pageable));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "發布部落格文章")
    public ApiResponse<BlogPostDTO> publishBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id) {
        return ApiResponse.success("部落格文章已發布", blogService.publishBlogPost(id));
    }

    @PostMapping("/{id}/schedule")
    @Operation(summary = "排程發布部落格文章")
    public ApiResponse<BlogPostDTO> scheduleBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id,
            @Parameter(description = "排程時間") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        return ApiResponse.success("部落格文章已排程", blogService.scheduleBlogPost(id, scheduledAt));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "封存部落格文章")
    public ApiResponse<BlogPostDTO> archiveBlogPost(
            @Parameter(description = "文章 ID") @PathVariable Long id) {
        return ApiResponse.success("部落格文章已封存", blogService.archiveBlogPost(id));
    }
}
