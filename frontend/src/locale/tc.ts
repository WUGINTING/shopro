/**
 * 繁體中文語系檔案
 * @module Locale-TC
 */

export default {
  // 通用
  common: {
    confirm: '確認',
    cancel: '取消',
    save: '儲存',
    delete: '刪除',
    edit: '編輯',
    add: '新增',
    search: '搜尋',
    reset: '重置',
    submit: '提交',
    back: '返回',
    loading: '載入中...',
    noData: '暫無資料',
    success: '操作成功',
    error: '操作失敗',
    warning: '警告',
    info: '提示',
    total: '共',
    items: '項',
    action: '操作',
    status: '狀態',
    createdAt: '創建時間',
    updatedAt: '更新時間',
    yes: '是',
    no: '否'
  },

  // 登入頁面
  login: {
    title: '登入',
    username: '使用者名稱',
    password: '密碼',
    rememberMe: '記住我',
    forgotPassword: '忘記密碼？',
    loginButton: '登入',
    loginSuccess: '登入成功',
    loginFailed: '登入失敗',
    usernameRequired: '請輸入使用者名稱',
    passwordRequired: '請輸入密碼'
  },

  // 導航選單
  menu: {
    dashboard: '儀表板',
    product: '商品管理',
    productList: '商品列表',
    order: '訂單管理',
    orderList: '訂單列表',
    orderDiscount: '訂單折扣',
    orderQA: '訂單問答',
    customer: '客戶管理',
    customerList: '客戶列表',
    member: '會員管理',
    memberGroup: '會員分組',
    memberLevel: '會員等級',
    marketing: '營銷管理',
    marketingCampaign: '營銷活動',
    promotion: '促銷管理',
    point: '積分管理',
    edm: 'EDM 管理',
    content: '內容管理',
    blog: '部落格',
    album: '相冊管理',
    payment: '支付管理',
    paymentDashboard: '支付儀表板',
    paymentTransaction: '交易記錄',
    paymentSettings: '支付設定',
    system: '系統管理',
    statistics: '數據統計',
    operationLog: '操作日誌',
    user: '用戶管理',
    settings: '系統設定'
  },

  // 商品管理
  product: {
    name: '商品名稱',
    description: '商品描述',
    price: '價格',
    stock: '庫存',
    category: '分類',
    status: '狀態',
    image: '圖片',
    createProduct: '新增商品',
    editProduct: '編輯商品',
    deleteProduct: '刪除商品',
    activateProduct: '上架商品',
    deactivateProduct: '下架商品',
    draft: '草稿',
    published: '已發布',
    unpublished: '已下架',
    nameRequired: '請輸入商品名稱',
    priceRequired: '請輸入商品價格',
    stockRequired: '請輸入庫存數量'
  },

  // 訂單管理
  order: {
    orderNumber: '訂單號',
    customerName: '客戶',
    totalAmount: '總金額',
    status: '狀態',
    orderDate: '下單時間',
    updateStatus: '更新狀態',
    viewDetail: '查看詳情',
    pending: '待處理',
    processing: '處理中',
    shipped: '已發貨',
    delivered: '已送達',
    cancelled: '已取消',
    cancelOrder: '取消訂單',
    cancelConfirm: '確定要取消此訂單嗎？此操作無法復原。',
    statusUpdateSuccess: '狀態更新成功',
    statusUpdateFailed: '狀態更新失敗'
  },

  // 客戶管理
  customer: {
    name: '客戶名稱',
    email: '電子郵件',
    phone: '電話',
    address: '地址',
    createCustomer: '新增客戶',
    editCustomer: '編輯客戶',
    deleteCustomer: '刪除客戶',
    emailRequired: '請輸入電子郵件',
    phoneRequired: '請輸入電話號碼'
  },

  // 會員管理
  member: {
    memberLevel: '會員等級',
    points: '積分',
    joinDate: '加入日期',
    lastLogin: '最後登入',
    createMember: '新增會員',
    editMember: '編輯會員',
    deleteMember: '刪除會員'
  },

  // 支付管理
  payment: {
    gateway: '支付閘道',
    transactionId: '交易編號',
    amount: '金額',
    status: '狀態',
    paymentMethod: '支付方式',
    paymentTime: '支付時間',
    success: '成功',
    failed: '失敗',
    pending: '待處理',
    refund: '退款',
    refundSuccess: '退款成功',
    refundFailed: '退款失敗'
  },

  // 系統設定
  settings: {
    profile: '個人資料',
    changePassword: '變更密碼',
    logout: '登出',
    language: '語言',
    theme: '主題',
    lightMode: '淺色模式',
    darkMode: '深色模式',
    notification: '通知設定',
    logoutConfirm: '確定要登出嗎？'
  },

  // 驗證訊息
  validation: {
    required: '此欄位為必填',
    email: '請輸入有效的電子郵件地址',
    phone: '請輸入有效的電話號碼',
    url: '請輸入有效的網址',
    number: '請輸入數字',
    integer: '請輸入整數',
    positive: '請輸入正數',
    min: '最小值為 {min}',
    max: '最大值為 {max}',
    minLength: '最少 {min} 個字元',
    maxLength: '最多 {max} 個字元',
    pattern: '格式不正確'
  },

  // 錯誤訊息
  error: {
    network: '網路錯誤，請檢查您的網路連接',
    timeout: '請求超時，請稍後再試',
    unauthorized: '未授權，請重新登入',
    forbidden: '沒有權限存取',
    notFound: '請求的資源不存在',
    serverError: '伺服器錯誤，請稍後再試',
    unknown: '未知錯誤'
  }
}
