const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '/', component: () => import('pages/official/IndexPage.vue') },
      {
        path: '/about',
        component: () => import('pages/official/AboutPage.vue'),
      },
      { path: '/menu', component: () => import('pages/official/MenuPage.vue') },
      {
        path: '/cases',
        component: () => import('pages/official/CasesPage.vue'),
      },
      {
        path: '/contact',
        component: () => import('pages/official/ContactPage.vue'),
      },
      { path: '/faq', component: () => import('pages/official/FaqPage.vue') },
    ],
  },
  {
    path: '/shop',
    component: () => import('layouts/MainLayoutShop.vue'),
    children: [
      { path: '/shop', component: () => import('pages/shop/IndexPage.vue') },
      {
        path: '/shop/introduce',
        component: () => import('pages/shop/introduce.vue'),
      },
      {
        path: '/shop/news',
        component: () => import('pages/shop/news/list.vue'),
      },
      {
        path: '/shop/news/:id',
        component: () => import('pages/shop/news/detail.vue'),
      },
      {
        path: '/shop/product/list',
        component: () => import('pages/shop/product/list.vue'),
      },
      {
        path: '/shop/product/:id',
        component: () => import('pages/shop/product/detail.vue'),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/404.vue'),
  },
];

export default routes;
