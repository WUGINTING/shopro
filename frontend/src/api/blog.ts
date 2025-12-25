import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 部落格状态枚举
export type BlogStatus = 'DRAFT' | 'PUBLISHED' | 'SCHEDULED' | 'ARCHIVED'

// 部落格文章相关接口
export interface BlogPost {
  id?: number
  title: string
  slug: string
  content?: string
  summary?: string
  coverImageUrl?: string
  status?: BlogStatus
  authorId?: number
  authorName?: string
  tags?: string
  metaTitle?: string
  metaDescription?: string
  metaKeywords?: string
  viewCount?: number
  scheduledAt?: string
  publishedAt?: string
}

// 部落格 API
export const blogApi = {
  // 创建部落格文章
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
  
  // 依别名取得部落格文章
  getBlogPostBySlug: (slug: string) => {
    return axios.get<any, ApiResponse<BlogPost>>(`/crm/blog/slug/${slug}`)
  },
  
  // 删除部落格文章
  deleteBlogPost: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/crm/blog/${id}`)
  },
  
  // 分页查询部落格文章
  listBlogPosts: (page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>('/crm/blog', {
      params: { page, size }
    })
  },
  
  // 依状态查询部落格文章
  listBlogPostsByStatus: (status: BlogStatus, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/status/${status}`, {
      params: { page, size }
    })
  },
  
  // 依作者查询部落格文章
  listBlogPostsByAuthor: (authorId: number, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/author/${authorId}`, {
      params: { page, size }
    })
  },
  
  // 依标签查询部落格文章
  listBlogPostsByTag: (tag: string, page: number = 0, size: number = 20) => {
    return axios.get<any, ApiResponse<PageResponse<BlogPost>>>(`/crm/blog/tag/${tag}`, {
      params: { page, size }
    })
  },
  
  // 发布部落格文章
  publishBlogPost: (id: number) => {
    return axios.post<any, ApiResponse<BlogPost>>(`/crm/blog/${id}/publish`)
  },
  
  // 排程发布部落格文章
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
