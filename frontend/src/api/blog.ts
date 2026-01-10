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
  // 創建部落格文章
  createBlogPost: (data: BlogPost) => {
    return axios.post<any, ApiResponse<BlogPost>>('/crm/blog', data)
  },
  
  // 更新部落格文章
  updateBlogPost: (id: number, data: BlogPost) => {
    return axios.put<any, ApiResponse<BlogPost>>(`/crm/blog/${id}`, data)
  },
  
  // 取得部落格文章详情
  getBlogPost: (id: number) => {
    return axios.get<any, ApiResponse<BlogPost>>(`/crm/blog/${id}`)
  },
  
  // 依別名取得部落格文章
  getBlogPostBySlug: (slug: string) => {
    return axios.get<any, ApiResponse<BlogPost>>(`/crm/blog/slug/${slug}`)
  },
  
  // 刪除部落格文章
  deleteBlogPost: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/blog/${id}`)
  },
  
  // 分頁查詢部落格文章
  listBlogPosts: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>('/crm/blog', {
      params: { page, size }
    })
  },
  
  // 依狀態查詢部落格文章
  listBlogPostsByStatus: (status: BlogStatus, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/status/${status}`, {
      params: { page, size }
    })
  },
  
  // 依作者查詢部落格文章
  listBlogPostsByAuthor: (authorId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/author/${authorId}`, {
      params: { page, size }
    })
  },
  
  // 依標籤查詢部落格文章
  listBlogPostsByTag: (tag: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/tag/${tag}`, {
      params: { page, size }
    })
  },
  
  // 發布部落格文章
  publishBlogPost: (id: number) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/publish`)
  },
  
  // 排程發布部落格文章
  scheduleBlogPost: (id: number, scheduledAt: string) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/schedule`, null, {
      params: { scheduledAt }
    })
  },
  
  // 封存部落格文章
  archiveBlogPost: (id: number) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/archive`)
  }
}

export default blogApi
