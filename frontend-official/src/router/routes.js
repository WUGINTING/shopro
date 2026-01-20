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
      { path: '/graphic-design', component: () => import('pages/official/GraphicDesign.vue') },
      { path: '/print-design', component: () => import('pages/official/PrintDesign.vue') },
      {
        path: '/web-design',
        component: () => import('pages/official/WebDesign.vue'),
      },
      {
        path: '/contact',
        component: () => import('pages/official/ContactPage.vue'),
      },
      { path: '/faq', component: () => import('pages/official/FaqPage.vue') },
      // 商店頁面整合到 MainLayout
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
      {
        path: '/shop/checkout',
        component: () => import('pages/shop/CheckoutPage.vue'),
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
