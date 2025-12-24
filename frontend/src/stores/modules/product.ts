import { defineStore } from 'pinia'
import { ref } from 'vue'
import { productApi, productCategoryApi, productTagApi } from '@/api'
import type { Product, ProductCategory, ProductTag, PageResponse } from '@/api'

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref<Product[]>([])
  const currentProduct = ref<Product | null>(null)
  const categories = ref<ProductCategory[]>([])
  const tags = ref<ProductTag[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0
  })

  // Actions - 商品管理
  async function fetchProducts(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.list(page, size) as any
      if (response.success && response.data) {
        products.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取商品列表失敗'
      console.error('Error fetching products:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchProductsByCategory(categoryId: number, page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.listByCategory(categoryId, page, size) as any
      if (response.success && response.data) {
        products.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取分類商品失敗'
      console.error('Error fetching products by category:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchProductsByStatus(status: string, page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.listByStatus(status, page, size) as any
      if (response.success && response.data) {
        products.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取商品失敗'
      console.error('Error fetching products by status:', e)
    } finally {
      loading.value = false
    }
  }

  async function createProduct(productData: Product) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.create(productData) as any
      if (response.success && response.data) {
        products.value.unshift(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建商品失敗'
      console.error('Error creating product:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateProduct(id: number, productData: Product) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.update(id, productData) as any
      if (response.success && response.data) {
        const index = products.value.findIndex((p) => p.id === id)
        if (index !== -1) {
          products.value[index] = response.data
        }
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '更新商品失敗'
      console.error('Error updating product:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteProduct(id: number) {
    loading.value = true
    error.value = null
    try {
      await productApi.delete(id)
      products.value = products.value.filter((p) => p.id !== id)
    } catch (e: any) {
      error.value = e.message || '刪除商品失敗'
      console.error('Error deleting product:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function getProduct(id: number) {
    loading.value = true
    error.value = null
    try {
      const response = await productApi.get(id) as any
      if (response.success && response.data) {
        currentProduct.value = response.data
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取商品詳情失敗'
      console.error('Error fetching product:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - 商品分類
  async function fetchCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await productCategoryApi.listAll() as any
      if (response.success && response.data) {
        categories.value = response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取分類列表失敗'
      console.error('Error fetching categories:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchEnabledCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await productCategoryApi.listEnabled() as any
      if (response.success && response.data) {
        categories.value = response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取啟用分類失敗'
      console.error('Error fetching enabled categories:', e)
    } finally {
      loading.value = false
    }
  }

  async function createCategory(categoryData: ProductCategory) {
    loading.value = true
    error.value = null
    try {
      const response = await productCategoryApi.create(categoryData) as any
      if (response.success && response.data) {
        categories.value.push(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建分類失敗'
      console.error('Error creating category:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateCategory(id: number, categoryData: ProductCategory) {
    loading.value = true
    error.value = null
    try {
      const response = await productCategoryApi.update(id, categoryData) as any
      if (response.success && response.data) {
        const index = categories.value.findIndex((c) => c.id === id)
        if (index !== -1) {
          categories.value[index] = response.data
        }
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '更新分類失敗'
      console.error('Error updating category:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteCategory(id: number) {
    loading.value = true
    error.value = null
    try {
      await productCategoryApi.delete(id)
      categories.value = categories.value.filter((c) => c.id !== id)
    } catch (e: any) {
      error.value = e.message || '刪除分類失敗'
      console.error('Error deleting category:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - 商品標籤
  async function fetchTags() {
    loading.value = true
    error.value = null
    try {
      const response = await productTagApi.listAll() as any
      if (response.success && response.data) {
        tags.value = response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取標籤列表失敗'
      console.error('Error fetching tags:', e)
    } finally {
      loading.value = false
    }
  }

  async function createTag(tagData: ProductTag) {
    loading.value = true
    error.value = null
    try {
      const response = await productTagApi.create(tagData) as any
      if (response.success && response.data) {
        tags.value.push(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建標籤失敗'
      console.error('Error creating tag:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    products,
    currentProduct,
    categories,
    tags,
    loading,
    error,
    pagination,
    // Actions
    fetchProducts,
    fetchProductsByCategory,
    fetchProductsByStatus,
    createProduct,
    updateProduct,
    deleteProduct,
    getProduct,
    fetchCategories,
    fetchEnabledCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    fetchTags,
    createTag
  }
})
