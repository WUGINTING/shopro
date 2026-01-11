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
          component: () => import('../views/HomeView.vue')
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue')
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('../views/CategoryView.vue')
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/OrderView.vue')
        },
        {
          path: 'customers',
          name: 'customers',
          component: () => import('../views/CustomerView.vue')
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('../views/AboutView.vue')
        },
        {
          path: 'order-discounts',
          name: 'orderDiscounts',
          component: () => import('../views/OrderDiscountView.vue')
        },
        {
          path: 'member-levels',
          name: 'memberLevels',
          component: () => import('../views/MemberLevelView.vue')
        },
        {
          path: 'blog',
          name: 'blog',
          component: () => import('../views/BlogView.vue')
        },
        {
          path: 'order-qa',
          name: 'orderQA',
          component: () => import('../views/OrderQAView.vue')
        },
        {
          path: 'operation-logs',
          name: 'operationLogs',
          component: () => import('../views/OperationLogView.vue')
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('../views/UserView.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/ProfileView.vue')
        },
        {
          path: 'albums',
          name: 'albums',
          component: () => import('../views/AlbumView.vue')
        },
        {
          path: 'albums/:id',
          name: 'albumDetail',
          component: () => import('../views/AlbumDetailView.vue')
        },
        {
          path: 'payment-dashboard',
          name: 'paymentDashboard',
          component: () => import('../views/PaymentDashboardView.vue')
        },
        {
          path: 'payment-transactions',
          name: 'paymentTransactions',
          component: () => import('../views/PaymentTransactionsView.vue')
        },
        {
          path: 'payment-settings',
          name: 'paymentSettings',
          component: () => import('../views/PaymentSettingsView.vue')
        },
        {
          path: 'marketing',
          name: 'marketing',
          component: () => import('../views/MarketingView.vue')
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('../views/StatisticsView.vue')
        },
        {
          path: 'system-settings',
          name: 'systemSettings',
          component: () => import('../views/SettingsView.vue')
        },
        {
          path: 'edm',
          name: 'edm',
          component: () => import('../views/EdmView.vue')
        },
        {
          path: 'points',
          name: 'points',
          component: () => import('../views/PointView.vue')
        },
        {
          path: 'promotions',
          name: 'promotions',
          component: () => import('../views/PromotionView.vue')
        },
        {
          path: 'member-groups',
          name: 'memberGroups',
          component: () => import('../views/MemberGroupView.vue')
        },
        {
          path: 'members',
          name: 'members',
          component: () => import('../views/MemberView.vue')
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

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if not authenticated
    next({ name: 'login' })
  } else if (to.name === 'login' && isAuthenticated) {
    // Redirect to home if already logged in
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
