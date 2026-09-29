import http from '@/utils/request';

/**
 * 商品分類相關 API
 */

/**
 * 取得所有已啟用的商品分類（扁平清單）
 * @param {Object} config - axios 設定（例如 { silent: true }）
 * @returns {Promise} ProductCategoryDTO[]
 */
export function getEnabledCategories(config = {}) {
  return http.get('/product-categories/enabled', undefined, config);
}

/**
 * 依排序欄位比較兩個分類
 */
function compareCategory(a, b) {
  const orderA = a.sortOrder ?? Number.MAX_SAFE_INTEGER;
  const orderB = b.sortOrder ?? Number.MAX_SAFE_INTEGER;
  if (orderA !== orderB) return orderA - orderB;
  return (a.id ?? 0) - (b.id ?? 0);
}

/**
 * 將扁平的分類清單轉為父子樹狀結構
 * parentId 為空（或 0、或父分類未啟用）者視為頂層分類
 * @param {Array} list - ProductCategoryDTO[]
 * @returns {Array} [{ id, name, icon, children: [...] }]
 */
export function buildCategoryTree(list = []) {
  const nodes = new Map();
  list.forEach(item => {
    if (item && item.id != null) {
      nodes.set(item.id, { ...item, children: [] });
    }
  });

  const roots = [];
  nodes.forEach(node => {
    const parent = node.parentId ? nodes.get(node.parentId) : null;
    if (parent && parent !== node) {
      parent.children.push(node);
    } else {
      roots.push(node);
    }
  });

  const sortTree = items => {
    items.sort(compareCategory);
    items.forEach(item => sortTree(item.children));
    return items;
  };
  return sortTree(roots);
}
