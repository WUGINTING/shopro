import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type User } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isManager = computed(() => user.value?.role === 'MANAGER')
  const isStaff = computed(() => user.value?.role === 'STAFF')
  const isCustomer = computed(() => user.value?.role === 'CUSTOMER')
  
  // 角色檢查方法
  const hasRole = (role: string) => user.value?.role === role
  const hasAnyRole = (roles: string[]) => roles.includes(user.value?.role || '')
  
  // 權限檢查方法
  const canAccessAdmin = computed(() => hasRole('ADMIN'))
  const canAccessManager = computed(() => hasAnyRole(['ADMIN', 'MANAGER']))
  const canAccessStaff = computed(() => hasAnyRole(['ADMIN', 'MANAGER', 'STAFF']))
  const canAccessCustomer = computed(() => hasRole('CUSTOMER'))

  // Actions
  function initialize() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }

  function setAuth(authToken: string, userData: User) {
    token.value = authToken
    user.value = userData
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  /** 重新讀取個人資料（例如 Email 驗證完成後更新狀態） */
  async function refreshUser() {
    if (!token.value) return
    const response = await authApi.getProfile()
    if (response?.data) {
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
    }
  }

  function clearAuth() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function logout() {
    clearAuth()
    authApi.logout()
  }

  return {
    user,
    token,
    isAuthenticated,
    userRole,
    isAdmin,
    isManager,
    isStaff,
    isCustomer,
    hasRole,
    hasAnyRole,
    canAccessAdmin,
    canAccessManager,
    canAccessStaff,
    canAccessCustomer,
    initialize,
    setAuth,
    refreshUser,
    clearAuth,
    logout
  }
})
