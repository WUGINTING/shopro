import { createRouter, createWebHistory } from 'vue-router'
import { Loading, Notify } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layouts/MainLayout.vue'
import PublicLayout from '@/layouts/PublicLayout.vue'
import { trackEvent } from '@/utils/tracking'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: PublicLayout,
      meta: { requiresAuth: false },
      children: [
        { path: '', name: 'storeHome', component: () => import('@/views/store/StoreHomeView.vue') },
        { path: 'products', name: 'storeProducts', component: () => import('@/views/store/StoreProductListView.vue') },
        { path: 'products/:id', name: 'storeProductDetail', component: () => import('@/views/store/StoreProductDetailView.vue') },
        { path: 'cart', name: 'cart', component: () => import('@/views/store/CartView.vue') },
        {
          path: 'checkout',
          name: 'checkout',
          component: () => import('@/views/store/CheckoutView.vue'),
          meta: { requiresAuth: true, roles: ['CUSTOMER'] }
        },
        { path: 'order/success', name: 'orderSuccess', component: () => import('@/views/store/OrderSuccessView.vue') },
        { path: 'brand', name: 'brand', component: () => import('@/views/store/BrandView.vue') },
        { path: 'contact', name: 'contact', component: () => import('@/views/store/ContactView.vue') },
        { path: 'policy/returns', name: 'returnPolicy', component: () => import('@/views/store/ReturnPolicyView.vue') },
        { path: 'policy/payment-shipping', name: 'paymentShippingPolicy', component: () => import('@/views/store/PaymentShippingPolicyView.vue') }
      ]
    },
    {
      path: '/account',
      component: PublicLayout,
      meta: { requiresAuth: true, roles: ['CUSTOMER'] },
      children: [
        { path: '', name: 'accountHome', component: () => import('@/views/store/AccountHomeView.vue') },
        { path: 'orders', name: 'accountOrders', component: () => import('@/views/store/AccountOrdersView.vue') },
        { path: 'orders/:id', name: 'accountOrderDetail', component: () => import('@/views/store/AccountOrderDetailView.vue') },
        { path: 'benefits', name: 'accountBenefits', component: () => import('@/views/store/AccountBenefitsView.vue') },
        { path: 'addresses', name: 'accountAddresses', component: () => import('@/views/store/AccountAddressesView.vue') }
      ]
    },
    {
      path: '/admin',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('@/views/ProductView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('@/views/CategoryView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('@/views/OrderView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'customers',
          name: 'customers',
          component: () => import('@/views/CustomerView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('@/views/AboutView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'order-discounts',
          name: 'orderDiscounts',
          component: () => import('@/views/OrderDiscountView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'member-levels',
          name: 'memberLevels',
          component: () => import('@/views/MemberLevelView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'blog',
          name: 'blog',
          component: () => import('@/views/BlogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'order-qa',
          name: 'orderQA',
          component: () => import('@/views/OrderQAView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'operation-logs',
          name: 'operationLogs',
          component: () => import('@/views/OperationLogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/views/UserView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER'] }
        },
        {
          path: 'albums',
          name: 'albums',
          component: () => import('@/views/AlbumView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'albums/:id',
          name: 'albumDetail',
          component: () => import('@/views/AlbumDetailView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-dashboard',
          name: 'paymentDashboard',
          component: () => import('@/views/PaymentDashboardView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-transactions',
          name: 'paymentTransactions',
          component: () => import('@/views/PaymentTransactionsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'payment-settings',
          name: 'paymentSettings',
          component: () => import('@/views/PaymentSettingsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'ecpay-config',
          name: 'ecpayConfig',
          component: () => import('@/views/EcPayConfigView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'payment-callback-logs',
          name: 'paymentCallbackLogs',
          component: () => import('@/views/PaymentCallbackLogView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'marketing',
          name: 'marketing',
          component: () => import('@/views/MarketingView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/views/StatisticsView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        {
          path: 'system-settings',
          name: 'systemSettings',
          component: () => import('@/views/SettingsView.vue'),
          meta: { roles: ['ADMIN'] }
        },
        {
          path: 'edm',
          name: 'edm',
          component: () => import('@/views/EdmView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'points',
          name: 'points',
          component: () => import('@/views/PointView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'promotions',
          name: 'promotions',
          component: () => import('@/views/PromotionView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'member-groups',
          name: 'memberGroups',
          component: () => import('@/views/MemberGroupView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'members',
          name: 'members',
          component: () => import('@/views/MemberView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        },
        {
          path: 'calendar',
          name: 'calendar',
          component: () => import('@/views/CalendarView.vue'),
          meta: { roles: ['ADMIN', 'MANAGER', 'STAFF'] }
        }
      ]
    },
    // Legacy admin paths redirect to /admin/*
    { path: '/products', redirect: '/admin/products' },
    { path: '/categories', redirect: '/admin/categories' },
    { path: '/orders', redirect: '/admin/orders' },
    { path: '/customers', redirect: '/admin/customers' },
    { path: '/about', redirect: '/admin/about' },
    { path: '/order-discounts', redirect: '/admin/order-discounts' },
    { path: '/member-levels', redirect: '/admin/member-levels' },
    { path: '/blog', redirect: '/admin/blog' },
    { path: '/order-qa', redirect: '/admin/order-qa' },
    { path: '/operation-logs', redirect: '/admin/operation-logs' },
    { path: '/users', redirect: '/admin/users' },
    { path: '/profile', redirect: '/admin/profile' },
    { path: '/albums', redirect: '/admin/albums' },
    { path: '/albums/:id', redirect: (to) => `/admin/albums/${to.params.id}` },
    { path: '/payment-dashboard', redirect: '/admin/payment-dashboard' },
    { path: '/payment-transactions', redirect: '/admin/payment-transactions' },
    { path: '/payment-settings', redirect: '/admin/payment-settings' },
    { path: '/ecpay-config', redirect: '/admin/ecpay-config' },
    { path: '/payment-callback-logs', redirect: '/admin/payment-callback-logs' },
    { path: '/marketing', redirect: '/admin/marketing' },
    { path: '/statistics', redirect: '/admin/statistics' },
    { path: '/system-settings', redirect: '/admin/system-settings' },
    { path: '/edm', redirect: '/admin/edm' },
    { path: '/points', redirect: '/admin/points' },
    { path: '/promotions', redirect: '/admin/promotions' },
    { path: '/member-groups', redirect: '/admin/member-groups' },
    { path: '/members', redirect: '/admin/members' },
    { path: '/calendar', redirect: '/admin/calendar' },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { requiresAuth: false }
    }
  ]
})

let loadingTimer: ReturnType<typeof setTimeout> | null = null

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }

  loadingTimer = setTimeout(() => {
    Loading.show({
      message: '?é¢è¼‰å…¥ä¸?..',
      spinnerSize: 40
    })
  }, 200)

  if (!authStore.token) {
    authStore.initialize()
  }

  const requiresAuth = to.meta.requiresAuth !== false
  const isAuthenticated = authStore.isAuthenticated

  if (requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
    return
  }

  if (to.name === 'login' && isAuthenticated) {
    if (authStore.userRole === 'CUSTOMER') {
      next({ name: 'storeHome' })
    } else {
      next({ name: 'home' })
    }
    return
  }

  const allowedRoles = to.meta.roles as string[] | undefined
  if (allowedRoles && allowedRoles.length > 0 && isAuthenticated) {
    const userRole = authStore.userRole
    if (!userRole || !allowedRoles.includes(userRole)) {
      const isStoreCustomerRoute = to.path.startsWith('/checkout') || to.path.startsWith('/account')

      Notify.create({
        type: 'warning',
        message: isStoreCustomerRoute
          ? 'This page is for customer accounts only. Please login with a customer account.'
          : 'You do not have permission to access this page.',
        position: 'top'
      })

      if (isStoreCustomerRoute) {
        next({ name: 'storeHome' })
        return
      }

      if (authStore.userRole === 'CUSTOMER') {
        next({ name: 'storeHome' })
      } else {
        next({ name: 'home' })
      }
      return
    }
  }

  next()
})

router.afterEach((to) => {
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  Loading.hide()
  trackEvent('page_view', { route_name: String(to.name || '') })
})

router.onError((error) => {
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  Loading.hide()

  console.error('Router Error:', error)
  Notify.create({
    type: 'negative',
    message: '?é¢è¼‰å…¥å¤±æ?ï¼Œè?ç¨å??è©¦',
    position: 'top'
  })
})

export default router
