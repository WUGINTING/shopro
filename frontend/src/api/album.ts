import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

// 相冊介面
export interface Album {
  id?: number
  name: string
  description?: string
  coverImageUrl?: string
  images?: AlbumImage[]
  imageCount?: number
  createdAt?: string
  updatedAt?: string
}

// 相冊圖片介面
export interface AlbumImage {
  id?: number
  albumId: number
  imageUrl: string
  fileName: string
  title?: string
  description?: string
  fileSize?: number
  fileType?: string
  sortOrder?: number
  uploadedAt?: string
}

// 相冊 API
export const albumApi = {
  // 獲取相冊列表
  getAlbums: (params?: { page?: number; size?: number }) => {
    return axios.get<any, ApiResponse<PageResponse<Album>>>('/albums', { params })
  },

  // 獲取相冊詳情
  getAlbum: (id: number) => {
    return axios.get<any, ApiResponse<Album>>(`/albums/${id}`)
  },

  // 創建相冊
  createAlbum: (data: Album) => {
    return axios.post<any, ApiResponse<Album>>('/albums', data)
  },

  // 更新相冊
  updateAlbum: (id: number, data: Album) => {
    return axios.put<any, ApiResponse<Album>>(`/albums/${id}`, data)
  },

  // 刪除相冊
  deleteAlbum: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/albums/${id}`)
  },

  // 獲取相冊中的所有圖片
  getAlbumImages: (albumId: number) => {
    return axios.get<any, ApiResponse<AlbumImage[]>>(`/albums/${albumId}/images`)
  },

  // 上傳圖片到相冊
  uploadImage: (albumId: number, file: File, title?: string, description?: string) => {
    const formData = new FormData()
    formData.append('file', file)
    if (title) formData.append('title', title)
    if (description) formData.append('description', description)

    return axios.post<any, ApiResponse<AlbumImage>>(`/albums/${albumId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 刪除相冊中的圖片
  deleteImage: (albumId: number, imageId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/albums/${albumId}/images/${imageId}`)
  },

  // 獲取圖片 URL
  getImageUrl: (filename: string) => {
    return `${axios.defaults.baseURL}/albums/images/${filename}`
  }
}
