// 商品資料共用方法

import { addToCart } from './cart.js';

/** 商品圖片缺少時使用的預設圖 */
export const PRODUCT_PLACEHOLDER = '/img/placeholder-product.svg';

/**
 * 實際售價：特價大於 0 時使用特價，否則使用原價（與後端結帳計價規則一致）
 * @param {Object} apiProduct - 後端 ProductDTO
 * @returns {number}
 */
export function effectivePrice(apiProduct) {
  const sale = Number(apiProduct?.salePrice);
  if (sale > 0) return sale;
  return Number(apiProduct?.basePrice) || 0;
}

/**
 * 將後端 ProductDTO 轉為前台商品卡片使用的資料
 * @param {Object} item - 後端 ProductDTO
 * @returns {Object}
 */
export function mapProduct(item) {
  const price = effectivePrice(item);
  const basePrice = Number(item.basePrice) || 0;
  const images = item.images || [];
  const primary = images.find(img => img.isPrimary) || images[0];
  const specifications = (item.specifications || []).filter(spec => spec.enabled !== false);
  const hasStockInfo = item.stock !== null && item.stock !== undefined;
  return {
    id: item.id,
    name: item.name,
    categoryId: item.categoryId,
    price,
    originalPrice: basePrice > price ? basePrice : null,
    image: primary?.imageUrl || PRODUCT_PLACEHOLDER,
    sku: item.sku,
    status: item.status,
    hasSpecs: specifications.length > 0,
    // stock 為 null 表示未追蹤庫存（不限量）
    soldOut: item.status === 'OUT_OF_STOCK' || (hasStockInfo && item.stock <= 0),
    createdAt: item.createdAt,
  };
}

/**
 * 商品卡片「加入購物車」：有規格的商品需到詳情頁選擇規格
 * @returns {boolean} 是否已直接加入購物車
 */
export function quickAddToCart(product, { router, $q }) {
  if (product.soldOut) {
    $q.notify({ type: 'warning', message: '此商品目前缺貨', position: 'top', timeout: 1500 });
    return false;
  }
  if (product.hasSpecs) {
    router.push(`/shop/product/${product.id}`);
    return false;
  }
  addToCart(
    {
      id: product.id,
      name: product.name,
      image: product.image,
      price: product.price,
      selectedPrice: product.price,
      originalPrice: product.originalPrice,
      sku: product.sku,
      selectedSku: product.sku,
      specification: null,
    },
    1
  );
  $q.notify({
    message: `已將「${product.name}」加入購物車`,
    color: 'positive',
    position: 'top',
    timeout: 1500,
  });
  return true;
}
