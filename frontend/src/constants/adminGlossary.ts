export const adminGlossary = {
  domain: {
    product: '商品',
    order: '訂單',
    member: '會員',
    customer: '會員',
    payment: '支付',
    shipping: '配送',
    checkout: '結帳'
  },
  common: {
    dashboard: '管理首頁',
    settings: '系統設定',
    reports: '報表分析',
    create: '新增',
    manage: '管理',
    viewMore: '查看更多'
  },
  actions: {
    createProduct: '新增商品',
    manageOrders: '管理訂單',
    addMember: '新增會員',
    mediaLibrary: '媒體素材',
    analytics: '報表分析',
    systemSettings: '系統設定'
  },
  nav: {
    dashboard: '管理首頁',
    productManagement: '商品管理',
    productList: '商品列表',
    categoryManagement: '分類管理',
    orderManagement: '訂單管理',
    orderList: '訂單列表',
    orderDiscounts: '訂單折扣',
    orderQA: '訂單問答',
    memberManagement: '會員管理',
    memberList: '會員列表',
    memberAdmin: '會員管理',
    memberGroups: '會員分群',
    memberLevels: '會員等級',
    paymentManagement: '支付管理',
    paymentDashboard: '支付儀表板',
    paymentTransactions: '支付交易',
    paymentSettings: '支付設定'
  },
  pages: {
    products: '商品管理',
    orders: '訂單管理',
    members: '會員管理',
    payments: '支付管理'
  },
  orderStatus: {
    PENDING: '待處理',
    PENDING_PAYMENT: '待付款',
    PAID: '已付款',
    PROCESSING: '處理中',
    SHIPPED: '已出貨',
    DELIVERED: '已送達',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  } as Record<string, string>,
  labels: {
    totalProducts: '商品總數',
    pendingOrders: '待處理訂單',
    totalMembers: '會員總數',
    monthlySales: '本月銷售額',
    quickActions: '快速操作',
    recentOrders: '近期訂單',
    topProducts: '熱門商品'
  }
} as const

export const getOrderStatusLabel = (status?: string): string => {
  if (!status) return ''
  return adminGlossary.orderStatus[status] ?? status
}
