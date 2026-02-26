export interface CheckoutDraft {
  customerName?: string
  customerPhone?: string
  shippingAddress?: string
}

const FAVORITES_KEY = 'store_favorite_ids'
const COMPARE_KEY = 'store_compare_ids'
const CHECKOUT_DRAFT_KEY = 'store_checkout_draft'

const readNumberList = (key: string): number[] => {
  try {
    const raw = localStorage.getItem(key)
    if (!raw) return []
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) return []
    return parsed.map((v) => Number(v)).filter((v) => Number.isFinite(v) && v > 0)
  } catch {
    return []
  }
}

const saveNumberList = (key: string, ids: number[]) => {
  const unique = Array.from(new Set(ids)).slice(0, 20)
  localStorage.setItem(key, JSON.stringify(unique))
}

export const getFavoriteIds = () => readNumberList(FAVORITES_KEY)
export const isFavorite = (id: number) => getFavoriteIds().includes(id)
export const toggleFavorite = (id: number): boolean => {
  const next = new Set(getFavoriteIds())
  if (next.has(id)) {
    next.delete(id)
  } else {
    next.add(id)
  }
  saveNumberList(FAVORITES_KEY, [...next])
  return next.has(id)
}

export const getCompareIds = () => readNumberList(COMPARE_KEY)
export const isCompared = (id: number) => getCompareIds().includes(id)
export const toggleCompare = (id: number, max = 4): { selected: boolean; limited: boolean } => {
  const list = getCompareIds()
  const next = new Set(list)

  if (next.has(id)) {
    next.delete(id)
    saveNumberList(COMPARE_KEY, [...next])
    return { selected: false, limited: false }
  }

  if (next.size >= max) {
    return { selected: false, limited: true }
  }

  next.add(id)
  saveNumberList(COMPARE_KEY, [...next])
  return { selected: true, limited: false }
}

export const clearCompare = () => saveNumberList(COMPARE_KEY, [])

export const getCheckoutDraft = (): CheckoutDraft => {
  try {
    const raw = localStorage.getItem(CHECKOUT_DRAFT_KEY)
    if (!raw) return {}
    const parsed = JSON.parse(raw)
    if (!parsed || typeof parsed !== 'object') return {}
    return parsed as CheckoutDraft
  } catch {
    return {}
  }
}

export const saveCheckoutDraft = (draft: CheckoutDraft) => {
  localStorage.setItem(CHECKOUT_DRAFT_KEY, JSON.stringify(draft))
}
