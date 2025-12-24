// 通用 API 回應格式
export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
}

// 分頁回應格式
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

// 會員相關類型
export interface Member {
  id?: number
  name: string
  email: string
  phone?: string
  birthday?: string
  gender?: 'M' | 'F' | 'OTHER'
  address?: string
  postalCode?: string
  status?: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  levelId?: number
  totalPoints?: number
  createdAt?: string
  updatedAt?: string
}

export interface MemberLevel {
  id?: number
  name: string
  description?: string
  minPoints: number
  maxPoints?: number
  discountRate?: number
  enabled?: boolean
}

export interface MemberGroup {
  id?: number
  name: string
  description?: string
  createdAt?: string
}

// 積點相關類型
export interface PointRecord {
  id?: number
  memberId: number
  points: number
  type: 'EARN' | 'REDEEM' | 'EXPIRE' | 'ADJUST'
  reason?: string
  orderId?: number
  createdAt?: string
}

export interface PointBatch {
  memberIds: number[]
  points: number
  reason: string
}

// EDM 相關類型
export interface EdmCampaign {
  id?: number
  title: string
  subject: string
  content: string
  scheduledTime?: string
  status?: 'DRAFT' | 'SCHEDULED' | 'SENT' | 'CANCELLED'
  targetGroupId?: number
  sentCount?: number
  openCount?: number
  clickCount?: number
  createdAt?: string
}

// 部落格相關類型
export interface BlogPost {
  id?: number
  title: string
  content: string
  summary?: string
  coverImage?: string
  tags?: string[]
  status?: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  viewCount?: number
  publishedAt?: string
  createdAt?: string
  updatedAt?: string
}

// 商品相關類型
export interface Product {
  id?: number
  name: string
  sku: string
  categoryId?: number
  status?: 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'
  salesMode?: 'NORMAL' | 'PRE_ORDER' | 'VOUCHER' | 'SUBSCRIPTION' | 'STORE_ONLY'
  description?: string
  basePrice: number
  salePrice?: number
  costPrice?: number
  stock?: number
  minPurchaseQuantity?: number
  maxPurchaseQuantity?: number
  tags?: number[]
  images?: string[]
  createdAt?: string
  updatedAt?: string
}

export interface ProductCategory {
  id?: number
  name: string
  parentId?: number
  sortOrder?: number
  enabled?: boolean
  description?: string
  children?: ProductCategory[]
}

export interface ProductTag {
  id?: number
  name: string
  color?: string
}

// 訂單相關類型
export interface Order {
  id?: number
  orderNumber?: string
  customerId: number
  customerName?: string
  status?: 'PENDING_PAYMENT' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED'
  paymentStatus?: 'PENDING' | 'PAID' | 'REFUNDING' | 'REFUNDED'
  shippingStatus?: 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'RETURNED'
  totalAmount: number
  discountAmount?: number
  shippingFee?: number
  finalAmount?: number
  shippingAddress?: string
  shippingMethod?: string
  pickupType?: 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP'
  storeId?: number
  items?: OrderItem[]
  notes?: string
  createdAt?: string
  updatedAt?: string
}

export interface OrderItem {
  id?: number
  productId: number
  productName: string
  sku?: string
  quantity: number
  price: number
  discount?: number
  subtotal?: number
}

// 訂單統計類型
export interface OrderStatistics {
  totalOrders: number
  totalAmount: number
  averageAmount: number
  statusDistribution: { [key: string]: number }
  dailyTrend?: { date: string; count: number; amount: number }[]
}
