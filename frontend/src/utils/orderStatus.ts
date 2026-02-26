export const orderStatusLabels: Record<string, string> = {
  PENDING: '待處理',
  PENDING_PAYMENT: '待付款',
  PAID: '已付款',
  PROCESSING: '處理中',
  SHIPPED: '已出貨',
  DELIVERED: '已送達',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REFUNDED: '已退款'
}

export const getOrderStatusLabel = (status?: string | null) => {
  if (!status) return '-'
  return orderStatusLabels[status] ?? status
}

export const getOrderStatusColor = (status?: string | null) => {
  switch (status) {
    case 'PENDING':
    case 'PENDING_PAYMENT':
      return 'orange-1'
    case 'PAID':
    case 'PROCESSING':
      return 'blue-1'
    case 'SHIPPED':
      return 'teal-1'
    case 'DELIVERED':
    case 'COMPLETED':
      return 'green-1'
    case 'CANCELLED':
    case 'REFUNDED':
      return 'red-1'
    default:
      return 'grey-2'
  }
}

export const getOrderStatusTextColor = (status?: string | null) => {
  switch (status) {
    case 'PENDING':
    case 'PENDING_PAYMENT':
      return 'orange-9'
    case 'PAID':
    case 'PROCESSING':
      return 'blue-9'
    case 'SHIPPED':
      return 'teal-9'
    case 'DELIVERED':
    case 'COMPLETED':
      return 'green-9'
    case 'CANCELLED':
    case 'REFUNDED':
      return 'red-9'
    default:
      return 'grey-8'
  }
}
