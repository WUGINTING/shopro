export interface CartItem {
  productId: number
  name: string
  price: number
  quantity: number
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

export const addToCart = (item: CartItem): void => {
  const items = getCartItems()
  const found = items.find((x) => x.productId === item.productId)

  if (found) {
    found.quantity += item.quantity
  } else {
    items.push(item)
  }

  saveCartItems(items)
}

export const updateCartQuantity = (productId: number, quantity: number): void => {
  const items = getCartItems()
  const next = items
    .map((item) => (item.productId === productId ? { ...item, quantity } : item))
    .filter((item) => item.quantity > 0)

  saveCartItems(next)
}

export const removeFromCart = (productId: number): void => {
  const items = getCartItems()
  saveCartItems(items.filter((x) => x.productId !== productId))
}

export const clearCart = (): void => {
  localStorage.removeItem(CART_KEY)
}

export const getCartTotal = (): number => {
  return getCartItems().reduce((sum, item) => sum + item.price * item.quantity, 0)
}
