import type { Product, ProductSpecification } from '@/api/product'

/**
 * 顧客商城的價格與庫存顯示規則（與後端結帳計價一致）
 * - 售價：有效特價（大於 0 且低於定價）優先，否則為定價
 * - 規格有自己的價格時以規格價格為準
 * - 庫存：stock 為 null/undefined 表示未追蹤庫存（不限量）；商品狀態 OUT_OF_STOCK 視為缺貨
 */
export const effectivePrice = (product: Pick<Product, 'basePrice' | 'salePrice'> & { price?: number }): number => {
  const base = product.basePrice != null ? Number(product.basePrice) : null
  const sale = product.salePrice != null ? Number(product.salePrice) : null
  if (sale != null && sale > 0 && (base == null || base <= 0 || sale < base)) return sale
  if (base != null && base > 0) return base
  return Number(product.price ?? 0)
}

/** 原價（有特價時顯示刪除線用），沒有特價時為 null */
export const listPriceIfDiscounted = (product: Pick<Product, 'basePrice' | 'salePrice'>): number | null => {
  const price = effectivePrice(product)
  const base = product.basePrice != null ? Number(product.basePrice) : null
  return base != null && base > price ? base : null
}

export const specPrice = (product: Product, spec?: ProductSpecification | null): number =>
  spec && spec.price != null && Number(spec.price) > 0 ? Number(spec.price) : effectivePrice(product)

const enabledSpecs = (product: Product) => (product.specifications || []).filter((spec) => spec.enabled !== false)

/** 可購買數量上限；null 表示不限量（未追蹤庫存） */
export const availableStock = (product: Product, spec?: ProductSpecification | null): number | null => {
  if (product.status === 'OUT_OF_STOCK') return 0
  if (spec) return spec.stock == null ? null : Math.max(Number(spec.stock), 0)
  const specs = enabledSpecs(product)
  if (specs.length > 0) {
    if (specs.some((item) => item.stock == null)) return null
    return specs.reduce((sum, item) => sum + Math.max(Number(item.stock ?? 0), 0), 0)
  }
  const stock = (product as Product & { stock?: number | null }).stock
  return stock == null ? null : Math.max(Number(stock), 0)
}

export const isSoldOut = (product: Product, spec?: ProductSpecification | null): boolean => availableStock(product, spec) === 0
