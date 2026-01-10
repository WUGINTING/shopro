/**
 * English Language File
 * @module Locale-EN
 */

export default {
  // Common
  common: {
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    add: 'Add',
    search: 'Search',
    reset: 'Reset',
    submit: 'Submit',
    back: 'Back',
    loading: 'Loading...',
    noData: 'No Data',
    success: 'Success',
    error: 'Error',
    warning: 'Warning',
    info: 'Info',
    total: 'Total',
    items: 'Items',
    action: 'Action',
    status: 'Status',
    createdAt: 'Created At',
    updatedAt: 'Updated At',
    yes: 'Yes',
    no: 'No'
  },

  // Login Page
  login: {
    title: 'Login',
    username: 'Username',
    password: 'Password',
    rememberMe: 'Remember Me',
    forgotPassword: 'Forgot Password?',
    loginButton: 'Login',
    loginSuccess: 'Login Successful',
    loginFailed: 'Login Failed',
    usernameRequired: 'Please enter username',
    passwordRequired: 'Please enter password'
  },

  // Navigation Menu
  menu: {
    dashboard: 'Dashboard',
    product: 'Products',
    productList: 'Product List',
    order: 'Orders',
    orderList: 'Order List',
    orderDiscount: 'Order Discounts',
    orderQA: 'Order Q&A',
    customer: 'Customers',
    customerList: 'Customer List',
    member: 'Members',
    memberGroup: 'Member Groups',
    memberLevel: 'Member Levels',
    marketing: 'Marketing',
    marketingCampaign: 'Campaigns',
    promotion: 'Promotions',
    point: 'Points',
    edm: 'EDM',
    content: 'Content',
    blog: 'Blog',
    album: 'Albums',
    payment: 'Payment',
    paymentDashboard: 'Payment Dashboard',
    paymentTransaction: 'Transactions',
    paymentSettings: 'Payment Settings',
    system: 'System',
    statistics: 'Statistics',
    operationLog: 'Operation Logs',
    user: 'Users',
    settings: 'Settings'
  },

  // Product Management
  product: {
    name: 'Product Name',
    description: 'Description',
    price: 'Price',
    stock: 'Stock',
    category: 'Category',
    status: 'Status',
    image: 'Image',
    createProduct: 'Create Product',
    editProduct: 'Edit Product',
    deleteProduct: 'Delete Product',
    activateProduct: 'Activate Product',
    deactivateProduct: 'Deactivate Product',
    draft: 'Draft',
    published: 'Published',
    unpublished: 'Unpublished',
    nameRequired: 'Please enter product name',
    priceRequired: 'Please enter price',
    stockRequired: 'Please enter stock quantity'
  },

  // Order Management
  order: {
    orderNumber: 'Order Number',
    customerName: 'Customer',
    totalAmount: 'Total Amount',
    status: 'Status',
    orderDate: 'Order Date',
    updateStatus: 'Update Status',
    viewDetail: 'View Details',
    pending: 'Pending',
    processing: 'Processing',
    shipped: 'Shipped',
    delivered: 'Delivered',
    cancelled: 'Cancelled',
    cancelOrder: 'Cancel Order',
    cancelConfirm: 'Are you sure you want to cancel this order? This action cannot be undone.',
    statusUpdateSuccess: 'Status updated successfully',
    statusUpdateFailed: 'Failed to update status'
  },

  // Customer Management
  customer: {
    name: 'Customer Name',
    email: 'Email',
    phone: 'Phone',
    address: 'Address',
    createCustomer: 'Create Customer',
    editCustomer: 'Edit Customer',
    deleteCustomer: 'Delete Customer',
    emailRequired: 'Please enter email',
    phoneRequired: 'Please enter phone number'
  },

  // Member Management
  member: {
    memberLevel: 'Member Level',
    points: 'Points',
    joinDate: 'Join Date',
    lastLogin: 'Last Login',
    createMember: 'Create Member',
    editMember: 'Edit Member',
    deleteMember: 'Delete Member'
  },

  // Payment Management
  payment: {
    gateway: 'Payment Gateway',
    transactionId: 'Transaction ID',
    amount: 'Amount',
    status: 'Status',
    paymentMethod: 'Payment Method',
    paymentTime: 'Payment Time',
    success: 'Success',
    failed: 'Failed',
    pending: 'Pending',
    refund: 'Refund',
    refundSuccess: 'Refund successful',
    refundFailed: 'Refund failed'
  },

  // System Settings
  settings: {
    profile: 'Profile',
    changePassword: 'Change Password',
    logout: 'Logout',
    language: 'Language',
    theme: 'Theme',
    lightMode: 'Light Mode',
    darkMode: 'Dark Mode',
    notification: 'Notifications',
    logoutConfirm: 'Are you sure you want to logout?'
  },

  // Validation Messages
  validation: {
    required: 'This field is required',
    email: 'Please enter a valid email address',
    phone: 'Please enter a valid phone number',
    url: 'Please enter a valid URL',
    number: 'Please enter a number',
    integer: 'Please enter an integer',
    positive: 'Please enter a positive number',
    min: 'Minimum value is {min}',
    max: 'Maximum value is {max}',
    minLength: 'Minimum length is {min} characters',
    maxLength: 'Maximum length is {max} characters',
    pattern: 'Invalid format'
  },

  // Error Messages
  error: {
    network: 'Network error, please check your connection',
    timeout: 'Request timeout, please try again later',
    unauthorized: 'Unauthorized, please login again',
    forbidden: 'Access forbidden',
    notFound: 'Resource not found',
    serverError: 'Server error, please try again later',
    unknown: 'Unknown error'
  }
}
