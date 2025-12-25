import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '../views/HomeView.vue'

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
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('../views/ProductView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/OrderView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/customers',
      name: 'customers',
      component: () => import('../views/CustomerView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/order-discounts',
      name: 'orderDiscounts',
      component: () => import('../views/OrderDiscountView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/member-levels',
      name: 'memberLevels',
      component: () => import('../views/MemberLevelView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/blog',
      name: 'blog',
      component: () => import('../views/BlogView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/order-qa',
      name: 'orderQA',
      component: () => import('../views/OrderQAView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/operation-logs',
      name: 'operationLogs',
      component: () => import('../views/OperationLogView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UserView.vue'),
      meta: { requiresAuth: true }
    },
  ],
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
