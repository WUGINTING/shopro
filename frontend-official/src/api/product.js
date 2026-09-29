import http from '@/utils/request';

/**
 * 商品相關 API
 */

/**
 * 前台商品列表（只含上架 / 缺貨商品，後端分頁、篩選、排序）
 * @param {Object} params
 * @param {number} [params.categoryId] - 分類 ID（含子分類）
 * @param {string} [params.keyword] - 名稱或 SKU 關鍵字
 * @param {string} [params.sort] - newest / price_asc / price_desc / name
 * @param {number} [params.page] - 頁碼（0 起算）
 * @param {number} [params.size] - 每頁數量（最多 60）
 * @returns {Promise} Page<ProductDTO>
 */
export function getStorefrontProducts(params = {}) {
  return http.get('/storefront/products', params);
}

/**
 * 缺貨商品登記到貨通知
 * @param {number} id - 商品 ID
 * @param {string} email - 通知 Email
 * @param {number|null} specificationId - 規格 ID（選填）
 */
export function subscribeRestock(id, email, specificationId = null) {
  return http.post(`/storefront/products/${id}/restock-notification`, { email, specificationId });
}

/**
 * 前台商品詳情（未上架商品回傳錯誤）
 * @param {number} id - 商品 ID
 * @returns {Promise} ProductDTO
 */
export function getStorefrontProduct(id) {
  return http.get(`/storefront/products/${id}`, undefined, { silent: true });
}

/**
 * 分頁查詢商品列表
 * @param {Object} params - 查詢參數
 * @param {number} params.page - 頁碼，預設 0
 * @param {number} params.size - 每頁數量，預設 20
 * @returns {Promise} PageProductDTO
 */
export function getProductList(params = {}) {
  return http.get('/products', {
    page: params.page || 0,
    size: params.size || 20,
  });
}

/**
 * 取得單一商品詳情
 * @param {number} id - 商品 ID
 * @returns {Promise} ProductDTO
 */
export function getProductDetail(id) {
  return http.get(`/products/${id}`);
}

/**
 * 搜尋商品
 * @param {Object} params - 搜尋參數
 * @param {string} params.keyword - 搜尋關鍵字
 * @param {number} params.page - 頁碼
 * @param {number} params.size - 每頁數量
 * @returns {Promise} PageProductDTO
 */
export function searchProducts(params) {
  return http.get('/products/search', params);
}

/**
 * 依分類查詢商品
 * @param {number} categoryId - 分類 ID
 * @param {Object} params - 查詢參數
 * @param {number} params.page - 頁碼
 * @param {number} params.size - 每頁數量
 * @returns {Promise} PageProductDTO
 */
export function getProductsByCategory(categoryId, params = {}) {
  return http.get(`/products/category/${categoryId}`, {
    page: params.page || 0,
    size: params.size || 20,
  });
}

/**
 * 依狀態查詢商品
 * @param {string} status - 商品狀態 (DRAFT, ACTIVE, INACTIVE, OUT_OF_STOCK)
 * @param {Object} params - 查詢參數
 * @returns {Promise} PageProductDTO
 */
export function getProductsByStatus(status, params = {}) {
  return http.get(`/products/status/${status}`, {
    page: params.page || 0,
    size: params.size || 20,
  });
}

/**
 * 取得商品規格
 * @param {number} productId - 商品 ID
 * @returns {Promise} List<ProductSpecificationDTO>
 */
export function getProductSpecifications(productId, options) {
  return http.get(`/product-specifications/product/${productId}`, undefined, options);
}

/**
 * 取得商品圖片
 * @param {number} productId - 商品 ID
 * @returns {Promise} List<ProductImageDTO>
 */
export function getProductImages(productId) {
  return http.get(`/product-images/product/${productId}`);
}

/**
 * 取得所有商品分類
 * @returns {Promise} List<ProductCategoryDTO>
 */
export function getProductCategories() {
  return http.get('/product-categories');
}

/**
 * 取得頂層分類
 * @returns {Promise} List<ProductCategoryDTO>
 */
export function getTopCategories() {
  return http.get('/product-categories/top');
}

/**
 * 取得子分類
 * @param {number} parentId - 父分類 ID
 * @returns {Promise} List<ProductCategoryDTO>
 */
export function getChildCategories(parentId) {
  return http.get(`/product-categories/${parentId}/children`);
}

/**
 * 取得已啟用的分類
 * @returns {Promise} List<ProductCategoryDTO>
 */
export function getEnabledCategories() {
  return http.get('/product-categories/enabled');
}

/**
 * 取得單一分類詳情
 * @param {number} id - 分類 ID
 * @returns {Promise} ProductCategoryDTO
 */
export function getProductCategory(id) {
  // 分類名稱只是輔助資訊，取不到時不顯示錯誤
  return http.get(`/product-categories/${id}`, undefined, { silent: true });
}
