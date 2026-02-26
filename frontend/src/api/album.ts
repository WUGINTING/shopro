/**
 * 相冊相關 API
 * @module AlbumAPI
 */

import axios from './axios'
import type { ApiResponse, PageResponse } from './types'

/**
 * 相冊介面
 * @interface Album
 */
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

/**
 * 相冊圖片介面
 * @interface AlbumImage
 */
export interface AlbumImage {
  /** 圖片 ID */
  id?: number
  /** 相冊 ID */
  albumId: number
  /** 圖片 URL */
  imageUrl: string
  /** 檔案名稱 */
  fileName: string
  /** 圖片標題 */
  title?: string
  /** 圖片描述 */
  description?: string
  /** 檔案大小 */
  fileSize?: number
  /** 檔案類型 */
  fileType?: string
  /** 排序順序 */
  sortOrder?: number
  /** 上傳時間 */
  uploadedAt?: string
}

/**
 * 相冊 API 服務
 * @namespace albumApi
 */
export const albumApi = {
  /**
   * 獲取相冊列表
   * @param {Object} [params] - 查詢參數
   * @param {number} [params.page] - 頁碼
   * @param {number} [params.size] - 每頁數量
   * @returns {Promise<ApiResponse<PageResponse<Album>>>} 相冊列表回應
   * @example
   * const albums = await albumApi.getAlbums({ page: 1, size: 10 })
   */
  getAlbums: (params?: { page?: number; size?: number }) => {
    return axios.get<any, ApiResponse<PageResponse<Album>>>('/albums', { params })
  },

  /**
   * 獲取單一相冊詳情
   * @param {number} id - 相冊 ID
   * @returns {Promise<ApiResponse<Album>>} 相冊詳情回應
   * @example
   * const album = await albumApi.getAlbum(123)
   */
  getAlbum: (id: number) => {
    return axios.get<any, ApiResponse<Album>>(`/albums/${id}`)
  },

  /**
   * 創建新相冊
   * @param {Album} data - 相冊資料
   * @returns {Promise<ApiResponse<Album>>} 創建成功的相冊資料
   * @example
   * const newAlbum = await albumApi.createAlbum({ name: '新相冊' })
   */
  createAlbum: (data: Album) => {
    return axios.post<any, ApiResponse<Album>>('/albums', data)
  },

  /**
   * 更新相冊資料
   * @param {number} id - 相冊 ID
   * @param {Album} data - 更新的相冊資料
   * @returns {Promise<ApiResponse<Album>>} 更新後的相冊資料
   * @example
   * const updated = await albumApi.updateAlbum(123, { name: '更新後的名稱' })
   */
  updateAlbum: (id: number, data: Album) => {
    return axios.put<any, ApiResponse<Album>>(`/albums/${id}`, data)
  },

  /**
   * 刪除相冊
   * @param {number} id - 相冊 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @example
   * await albumApi.deleteAlbum(123)
   */
  deleteAlbum: (id: number) => {
    return axios.delete<any, ApiResponse<void>>(`/albums/${id}`)
  },

  /**
   * 獲取相冊中的所有圖片
   * @param {number} albumId - 相冊 ID
   * @returns {Promise<ApiResponse<AlbumImage[]>>} 圖片列表回應
   * @example
   * const images = await albumApi.getAlbumImages(123)
   */
  getAlbumImages: (albumId: number) => {
    return axios.get<any, ApiResponse<AlbumImage[]>>(`/albums/${albumId}/images`)
  },

  /**
   * 上傳圖片到相冊
   * @param {number} albumId - 相冊 ID
   * @param {File} file - 圖片檔案
   * @param {string} [title] - 圖片標題
   * @param {string} [description] - 圖片描述
   * @returns {Promise<ApiResponse<AlbumImage>>} 上傳成功的圖片資料
   * @example
   * const image = await albumApi.uploadImage(123, file, '標題', '描述')
   */
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

  /**
   * 刪除相冊中的圖片
   * @param {number} albumId - 相冊 ID
   * @param {number} imageId - 圖片 ID
   * @returns {Promise<ApiResponse<void>>} 刪除結果
   * @example
   * await albumApi.deleteImage(123, 456)
   */
  deleteImage: (albumId: number, imageId: number) => {
    return axios.delete<any, ApiResponse<void>>(`/albums/${albumId}/images/${imageId}`)
  },

  /**
   * 設定相冊封面圖片
   */
  setCoverImage: (albumId: number, imageId: number) => {
    return axios.put<any, ApiResponse<Album>>(`/albums/${albumId}/images/${imageId}/cover`)
  },

  /**
   * 更新相冊圖片排序（傳入依序排列的圖片 ID 清單）
   */
  reorderImages: (albumId: number, imageIds: number[]) => {
    return axios.put<any, ApiResponse<AlbumImage[]>>(`/albums/${albumId}/images/sort`, imageIds)
  },

  /**
   * 獲取圖片 URL
   * @param {string} filename - 檔案名稱
   * @returns {string} 圖片 URL
   * @example
   * const url = albumApi.getImageUrl('image.jpg')
   */
  getImageUrl: (filename: string) => {
    return `${axios.defaults.baseURL}/albums/images/${filename}`
  }
}
