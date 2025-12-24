import { defineStore } from 'pinia'
import { ref } from 'vue'
import { memberApi, memberLevelApi, pointApi, edmApi, blogApi } from '@/api'
import type { Member, MemberLevel, PointRecord, EdmCampaign, BlogPost, PageResponse } from '@/api'

export const useCrmStore = defineStore('crm', () => {
  // State
  const members = ref<Member[]>([])
  const currentMember = ref<Member | null>(null)
  const memberLevels = ref<MemberLevel[]>([])
  const pointRecords = ref<PointRecord[]>([])
  const edmCampaigns = ref<EdmCampaign[]>([])
  const blogPosts = ref<BlogPost[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0
  })

  // Actions - 會員管理
  async function fetchMembers(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await memberApi.list(page, size)
      if (response.success && response.data) {
        members.value = response.data.content
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages
        }
      }
    } catch (e: any) {
      error.value = e.message || '獲取會員列表失敗'
      console.error('Error fetching members:', e)
    } finally {
      loading.value = false
    }
  }

  async function createMember(memberData: Member) {
    loading.value = true
    error.value = null
    try {
      const response = await memberApi.create(memberData)
      if (response.success && response.data) {
        members.value.unshift(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建會員失敗'
      console.error('Error creating member:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateMember(id: number, memberData: Member) {
    loading.value = true
    error.value = null
    try {
      const response = await memberApi.update(id, memberData)
      if (response.success && response.data) {
        const index = members.value.findIndex((m) => m.id === id)
        if (index !== -1) {
          members.value[index] = response.data
        }
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '更新會員失敗'
      console.error('Error updating member:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteMember(id: number) {
    loading.value = true
    error.value = null
    try {
      await memberApi.delete(id)
      members.value = members.value.filter((m) => m.id !== id)
    } catch (e: any) {
      error.value = e.message || '刪除會員失敗'
      console.error('Error deleting member:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function getMemberByEmail(email: string) {
    loading.value = true
    error.value = null
    try {
      const response = await memberApi.getByEmail(email) as any
      if (response.success && response.data) {
        currentMember.value = response.data
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '查詢會員失敗'
      console.error('Error fetching member by email:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - 會員等級
  async function fetchMemberLevels() {
    loading.value = true
    error.value = null
    try {
      const response = await memberLevelApi.listAll() as any
      if (response.success && response.data) {
        memberLevels.value = response.data
      }
    } catch (e: any) {
      error.value = e.message || '獲取會員等級失敗'
      console.error('Error fetching member levels:', e)
    } finally {
      loading.value = false
    }
  }

  // Actions - 積點管理
  async function earnPoints(pointData: PointRecord) {
    loading.value = true
    error.value = null
    try {
      const response = await pointApi.earn(pointData) as any
      if (response.success && response.data) {
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '發放積點失敗'
      console.error('Error earning points:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function redeemPoints(pointData: PointRecord) {
    loading.value = true
    error.value = null
    try {
      const response = await pointApi.redeem(pointData) as any
      if (response.success && response.data) {
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '兌換積點失敗'
      console.error('Error redeeming points:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - EDM 管理
  async function fetchEdmCampaigns(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await edmApi.list(page, size) as any
      if (response.success && response.data) {
        edmCampaigns.value = response.data.content
      }
    } catch (e: any) {
      error.value = e.message || '獲取 EDM 列表失敗'
      console.error('Error fetching EDM campaigns:', e)
    } finally {
      loading.value = false
    }
  }

  async function createEdmCampaign(edmData: EdmCampaign) {
    loading.value = true
    error.value = null
    try {
      const response = await edmApi.create(edmData) as any
      if (response.success && response.data) {
        edmCampaigns.value.unshift(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建 EDM 失敗'
      console.error('Error creating EDM campaign:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // Actions - 部落格管理
  async function fetchBlogPosts(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await blogApi.list(page, size) as any
      if (response.success && response.data) {
        blogPosts.value = response.data.content
      }
    } catch (e: any) {
      error.value = e.message || '獲取部落格文章失敗'
      console.error('Error fetching blog posts:', e)
    } finally {
      loading.value = false
    }
  }

  async function createBlogPost(blogData: BlogPost) {
    loading.value = true
    error.value = null
    try {
      const response = await blogApi.create(blogData) as any
      if (response.success && response.data) {
        blogPosts.value.unshift(response.data)
        return response.data
      }
    } catch (e: any) {
      error.value = e.message || '創建部落格文章失敗'
      console.error('Error creating blog post:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    members,
    currentMember,
    memberLevels,
    pointRecords,
    edmCampaigns,
    blogPosts,
    loading,
    error,
    pagination,
    // Actions
    fetchMembers,
    createMember,
    updateMember,
    deleteMember,
    getMemberByEmail,
    fetchMemberLevels,
    earnPoints,
    redeemPoints,
    fetchEdmCampaigns,
    createEdmCampaign,
    fetchBlogPosts,
    createBlogPost
  }
})
