import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('../views/CategoryView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/OrderView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'customers',
          name: 'customers',
          component: () => import('../views/CustomerView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('../views/AboutView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'order-discounts',
          name: 'orderDiscounts',
          component: () => import('../views/OrderDiscountView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'member-levels',
          name: 'memberLevels',
          component: () => import('../views/MemberLevelView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'blog',
          name: 'blog',
          component: () => import('../views/BlogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'order-qa',
          name: 'orderQA',
          component: () => import('../views/OrderQAView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'operation-logs',
          name: 'operationLogs',
          component: () => import('../views/OperationLogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('../views/UserView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/ProfileView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'albums',
          name: 'albums',
          component: () => import('../views/AlbumView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'albums/:id',
          name: 'albumDetail',
          component: () => import('../views/AlbumDetailView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-dashboard',
          name: 'paymentDashboard',
          component: () => import('../views/PaymentDashboardView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-transactions',
          name: 'paymentTransactions',
          component: () => import('../views/PaymentTransactionsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-settings',
          name: 'paymentSettings',
          component: () => import('../views/PaymentSettingsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'ecpay-config',
          name: 'ecpayConfig',
          component: () => import('../views/EcPayConfigView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'payment-callback-logs',
          name: 'paymentCallbackLogs',
          component: () => import('../views/PaymentCallbackLogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'marketing',
          name: 'marketing',
          component: () => import('../views/MarketingView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('../views/StatisticsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'system-settings',
          name: 'systemSettings',
          component: () => import('../views/SettingsView.vue'),
          meta: { roles: ['ADMIN'] }
        },
        {
          path: 'edm',
          name: 'edm',
          component: () => import('../views/EdmView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'points',
          name: 'points',
          component: () => import('../views/PointView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'promotions',
          name: 'promotions',
          component: () => import('../views/PromotionView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'member-groups',
          name: 'memberGroups',
          component: () => import('../views/MemberGroupView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'members',
          name: 'members',
          component: () => import('../views/MemberView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        }
      ]
    }
  ]
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Initialize auth store from localStorage
  if (!authStore.token) {
    authStore.initialize()
  }

  const requiresAuth = to.meta.requiresAuth !== false
  const isAuthenticated = authStore.isAuthenticated

  // 檢查是否需要認證
  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if not authenticated
    next({ name: 'login' })
    return
  }

  // 如果已登入但訪問登入頁，重定向到首頁
  if (to.name === 'login' && isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // 檢查角色權限
  const allowedRoles = to.meta.roles as string[] | undefined
  if (allowedRoles && allowedRoles.length > 0 && isAuthenticated) {
    const userRole = authStore.userRole
    if (!userRole || !allowedRoles.includes(userRole)) {
      // 沒有權限，重定向到首頁或顯示錯誤
      next({ name: 'home' })
      return
    }
  }

  next()
})

export default router
