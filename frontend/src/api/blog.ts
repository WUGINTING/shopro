/**
 * 部落格相關 API
 * @module BlogAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

/**
 * 部落格狀態枚舉
 * @typedef {'DRAFT' | 'PUBLISHED' | 'SCHEDULED' | 'ARCHIVED'} BlogStatus
 */
export type BlogStatus = 'DRAFT' | 'PUBLISHED' | 'SCHEDULED' | 'ARCHIVED'

/**
 * 部落格文章介面
 * @interface BlogPost
 */
export interface BlogPost {
  /** 文章 ID */
  id?: number
  /** 文章標題 */
  title: string
  /** 文章別名 (URL slug) */
  slug: string
  /** 文章內容 */
  content?: string
  /** 文章摘要 */
  summary?: string
  /** 封面圖片 URL */
  coverImageUrl?: string
  /** 文章狀態 */
  status?: BlogStatus
  /** 作者 ID */
  authorId?: number
  /** 作者名稱 */
  authorName?: string
  /** 標籤 */
  tags?: string
  /** SEO 標題 */
  metaTitle?: string
  /** SEO 描述 */
  metaDescription?: string
  /** SEO 關鍵字 */
  metaKeywords?: string
  /** 觀看次數 */
  viewCount?: number
  /** 排程發佈時間 */
  scheduledAt?: string
  /** 發佈時間 */
  publishedAt?: string
}

/**
 * 部落格 API 服務
 * @namespace blogApi
 */
export const blogApi = {
  /**
   * 創建部落格文章
   * @description 創建新的部落格文章，預設狀態為 DRAFT
   * @param {BlogPost} data - 文章資料
   * @param {string} data.title - 文章標題（必填）
   * @param {string} data.slug - 文章別名（必填）
   * @returns {Promise<ApiResponse<BlogPost>>} 創建成功的文章資料
   * @swagger POST /api/crm/blog
   * @example
   * const post = await blogApi.createBlogPost({
   *   title: '春節行銷策略',
   *   slug: 'spring-marketing-strategy',
   *   content: '<p>...</p>',
   *   status: 'DRAFT'
   * })
   */
  createBlogPost: (data: BlogPost) => {
    return axios.post<any, ApiResponse<BlogPost>>('/crm/blog', data)
  },
  
  /**
   * 更新部落格文章
   * @description 更新指定文章的資訊
   * @param {number} id - 文章 ID
   * @param {BlogPost} data - 更新的文章資料
   * @returns {Promise<ApiResponse<BlogPost>>} 更新後的文章資料
   * @swagger PUT /api/crm/blog/{id}
   * @example
   * const updated = await blogApi.updateBlogPost(123, { title: '新標題' })
   */
  updateBlogPost: (id: number, data: BlogPost) => {
    return axios.put<any, ApiResponse<BlogPost>>(`/crm/blog/${id}`, data)
  },
  
  /**
   * 取得部落格文章詳情
   * @description 查詢指定 ID 的文章詳細資訊
   * @param {number} id - 文章 ID
   * @returns {Promise<ApiResponse<BlogPost>>} 文章資料
   * @swagger GET /api/crm/blog/{id}
   * @example
   * const post = await blogApi.getBlogPost(123)
   */
  getBlogPost: (id: number) => {
    return axios.get<any, ApiResponse<BlogPost>>(`/crm/blog/${id}`)
  },
  
  /**
   * 依別名取得部落格文章
   * @description 使用文章別名（slug）查詢文章
   * @param {string} slug - 文章別名
   * @returns {Promise<ApiResponse<BlogPost>>} 文章資料
   * @swagger GET /api/crm/blog/slug/{slug}
   * @example
   * const post = await blogApi.getBlogPostBySlug('spring-marketing-strategy')
   */
  getBlogPostBySlug: (slug: string) => {
    return axios.get<any, ApiResponse<BlogPost>>(`/crm/blog/slug/${slug}`)
  },
  
  /**
   * 刪除部落格文章
   * @description 刪除指定的文章
   * @param {number} id - 文章 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @swagger DELETE /api/crm/blog/{id}
   * @example
   * await blogApi.deleteBlogPost(123)
   */
  deleteBlogPost: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/blog/${id}`)
  },
  
  /**
   * 分頁查詢部落格文章
   * @description 分頁查詢所有文章
   * @param {number} [page=0] - 頁碼（從 0 開始）
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<BlogPost>>>} 分頁文章資料
   * @swagger GET /api/crm/blog
   * @example
   * const page = await blogApi.listBlogPosts(0, 10)
   */
  listBlogPosts: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>('/crm/blog', {
      params: { page, size }
    })
  },
  
  /**
   * 依狀態查詢部落格文章
   * @description 根據文章狀態分頁查詢
   * @param {BlogStatus} status - 文章狀態
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<BlogPost>>>} 分頁文章資料
   * @swagger GET /api/crm/blog/status/{status}
   * @example
   * const published = await blogApi.listBlogPostsByStatus('PUBLISHED', 0, 20)
   */
  listBlogPostsByStatus: (status: BlogStatus, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/status/${status}`, {
      params: { page, size }
    })
  },
  
  /**
   * 依作者查詢部落格文章
   * @description 查詢指定作者的文章
   * @param {number} authorId - 作者 ID
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<BlogPost>>>} 分頁文章資料
   * @swagger GET /api/crm/blog/author/{authorId}
   * @example
   * const posts = await blogApi.listBlogPostsByAuthor(123, 0, 20)
   */
  listBlogPostsByAuthor: (authorId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/author/${authorId}`, {
      params: { page, size }
    })
  },
  
  /**
   * 依標籤查詢部落格文章
   * @description 查詢具有指定標籤的文章
   * @param {string} tag - 標籤名稱
   * @param {number} [page=0] - 頁碼
   * @param {number} [size=20] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<BlogPost>>>} 分頁文章資料
   * @swagger GET /api/crm/blog/tag/{tag}
   * @example
   * const posts = await blogApi.listBlogPostsByTag('marketing', 0, 20)
   */
  listBlogPostsByTag: (tag: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/tag/${tag}`, {
      params: { page, size }
    })
  },
  
  /**
   * 發布部落格文章
   * @description 將文章狀態設為 PUBLISHED
   * @param {number} id - 文章 ID
   * @returns {Promise<ApiResponse<BlogPost>>} 更新後的文章資料
   * @swagger POST /api/crm/blog/{id}/publish
   * @example
   * const published = await blogApi.publishBlogPost(123)
   */
  publishBlogPost: (id: number) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/publish`)
  },
  
  /**
   * 排程發布部落格文章
   * @description 設定文章的發布時間
   * @param {number} id - 文章 ID
   * @param {string} scheduledAt - 排程發布時間（ISO 8601 格式）
   * @returns {Promise<ApiResponse<BlogPost>>} 更新後的文章資料
   * @swagger POST /api/crm/blog/{id}/schedule
   * @example
   * const scheduled = await blogApi.scheduleBlogPost(123, '2026-02-01T10:00:00Z')
   */
  scheduleBlogPost: (id: number, scheduledAt: string) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/schedule`, null, {
      params: { scheduledAt }
    })
  },
  
  /**
   * 封存部落格文章
   * @description 將文章狀態設為 ARCHIVED
   * @param {number} id - 文章 ID
   * @returns {Promise<ApiResponse<BlogPost>>} 更新後的文章資料
   * @swagger POST /api/crm/blog/{id}/archive
   * @example
   * const archived = await blogApi.archiveBlogPost(123)
   */
  archiveBlogPost: (id: number) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/archive`)
  }
}

export default blogApi
