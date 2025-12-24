import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    // CRM 模組路由
    {
      path: '/crm/members',
      name: 'member-management',
      component: () => import('../views/crm/MemberManagement.vue'),
    },
    // Product 模組路由
    {
      path: '/products',
      name: 'product-management',
      component: () => import('../views/product/ProductManagement.vue'),
    },
    // Order 模組路由
    {
      path: '/orders',
      name: 'order-management',
      component: () => import('../views/order/OrderManagement.vue'),
    },
  ],
})

export default router
