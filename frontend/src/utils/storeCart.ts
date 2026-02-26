export interface CartItem {
  productId: number
  name: string
  price: number
  quantity: number
  specificationId?: number
  specName?: string
}

const CART_KEY = 'store_cart'

export const getCartItems = (): CartItem[] => {
  const raw = localStorage.getItem(CART_KEY)
  if (!raw) return []

  try {
    const parsed = JSON.parse(raw) as CartItem[]
    if (!Array.isArray(parsed)) return []
    return parsed
  } catch {
    return []
  }
}

export const saveCartItems = (items: CartItem[]): void => {
  localStorage.setItem(CART_KEY, JSON.stringify(items))
}

const findItemIndex = (items: CartItem[], target: CartItem) => {
  return items.findIndex((item) => {
    const sameProduct = item.productId === target.productId
    const sameSpec = (item.specificationId ?? null) === (target.specificationId ?? null)
    return sameProduct && sameSpec
  })
}

export const addToCart = (item: CartItem): void => {
  const items = getCartItems()
  const index = findItemIndex(items, item)

  if (index > -1) {
    items[index].quantity += item.quantity
  } else {
    items.push(item)
  }

  saveCartItems(items)
}

export const updateCartQuantity = (productId: number, quantity: number, specificationId?: number): void => {
  const items = getCartItems()
  const next = items
    .map((item) => {
      const sameProduct = item.productId === productId
      const sameSpec = (item.specificationId ?? null) === (specificationId ?? null)
      if (sameProduct && sameSpec) {
        return { ...item, quantity }
      }
      return item
    })
    .filter((item) => item.quantity > 0)

  saveCartItems(next)
}

export const removeFromCart = (productId: number, specificationId?: number): void => {
  const items = getCartItems()
  saveCartItems(items.filter((item) => {
    const sameProduct = item.productId === productId
    const sameSpec = (item.specificationId ?? null) === (specificationId ?? null)
    return !(sameProduct && sameSpec)
  }))
}

export const clearCart = (): void => {
  localStorage.removeItem(CART_KEY)
}

export const getCartTotal = (): number => {
  return getCartItems().reduce((sum, item) => sum + item.price * item.quantity, 0)
}
