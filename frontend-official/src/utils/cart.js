// 購物車共用方法

/**
 * 獲取購物車資料
 * @returns {Array} 購物車商品列表
 */
export function getCartItems() {
  try {
    const cartData = localStorage.getItem('shop_cart');
    const items = cartData ? JSON.parse(cartData) : [];
    return Array.isArray(items) ? items : [];
  } catch (error) {
    // 資料損毀或無法存取 localStorage 時視為空購物車
    return [];
  }
}

/**
 * 儲存購物車資料
 * @param {Array} items - 購物車商品列表
 */
export function saveCartItems(items) {
  localStorage.setItem('shop_cart', JSON.stringify(items));
  // 觸發自定義事件，通知購物車已更新
  window.dispatchEvent(new CustomEvent('cart-updated', { detail: items }));
}

/**
 * 加入商品到購物車
 * @param {Object} product - 商品資訊
 * @param {Number} quantity - 數量
 * @returns {Array} 更新後的購物車列表
 */
export function addToCart(product, quantity = 1) {
  const cartItems = getCartItems();
  
  // 如果有規格，需要比對商品ID + 規格ID來判斷是否為同一個項目
  const specId = product.specification?.id;
  const existingItemIndex = cartItems.findIndex(item => {
    if (specId) {
      return item.id === product.id && item.specification?.id === specId;
    }
    return item.id === product.id;
  });

  if (existingItemIndex > -1) {
    // 商品已存在，增加數量
    cartItems[existingItemIndex].quantity += quantity;
  } else {
    // 新商品，加入購物車
    cartItems.push({
      ...product,
      quantity: quantity,
      addedAt: new Date().toISOString(),
    });
  }

  saveCartItems(cartItems);
  return cartItems;
}

/**
 * 更新購物車商品數量
 * @param {Number} productId - 商品 ID
 * @param {Number} specId - 規格 ID (可選)
 * @param {Number} quantity - 新數量
 * @returns {Array} 更新後的購物車列表
 */
export function updateCartItemQuantity(productId, quantity, specId = null) {
  const cartItems = getCartItems();
  const itemIndex = cartItems.findIndex(item => {
    if (specId) {
      return item.id === productId && item.specification?.id === specId;
    }
    return item.id === productId && !item.specification;
  });

  if (itemIndex > -1) {
    if (quantity <= 0) {
      // 數量為 0，移除商品
      cartItems.splice(itemIndex, 1);
    } else {
      cartItems[itemIndex].quantity = quantity;
    }
  }

  saveCartItems(cartItems);
  return cartItems;
}

/**
 * 移除購物車商品
 * @param {Number} productId - 商品 ID
 * @param {Number} specId - 規格 ID (可選)
 * @returns {Array} 更新後的購物車列表
 */
export function removeFromCart(productId, specId = null) {
  const cartItems = getCartItems();
  const filteredItems = cartItems.filter(item => {
    if (specId) {
      return !(item.id === productId && item.specification?.id === specId);
    }
    return !(item.id === productId && !item.specification);
  });
  saveCartItems(filteredItems);
  return filteredItems;
}

/**
 * 更新購物車項目的規格
 * @param {Number} productId - 商品 ID
 * @param {Number} oldSpecId - 原規格 ID
 * @param {Object} newSpec - 新規格資訊
 * @returns {Array} 更新後的購物車列表
 */
export function updateCartItemSpec(productId, oldSpecId, newSpec) {
  const cartItems = getCartItems();
  const itemIndex = cartItems.findIndex(item => {
    if (oldSpecId) {
      return item.id === productId && item.specification?.id === oldSpecId;
    }
    return item.id === productId && !item.specification;
  });

  if (itemIndex > -1) {
    // 若購物車已有相同商品的新規格，合併為同一筆
    const duplicateIndex = cartItems.findIndex(
      (item, index) => index !== itemIndex && item.id === productId && item.specification?.id === newSpec.id
    );
    if (duplicateIndex > -1) {
      cartItems[duplicateIndex].quantity += cartItems[itemIndex].quantity;
      if (newSpec.stock !== null && newSpec.stock !== undefined && cartItems[duplicateIndex].quantity > newSpec.stock) {
        cartItems[duplicateIndex].quantity = Math.max(newSpec.stock, 1);
      }
      cartItems.splice(itemIndex, 1);
      saveCartItems(cartItems);
      return cartItems;
    }

    // 更新規格和價格
    cartItems[itemIndex].specification = newSpec;
    cartItems[itemIndex].selectedPrice = newSpec.price;
    cartItems[itemIndex].selectedSku = newSpec.sku;
    cartItems[itemIndex].price = newSpec.price;
    
    // 檢查數量是否超過新規格的庫存（未追蹤庫存時不限制）
    if (newSpec.stock !== null && newSpec.stock !== undefined && cartItems[itemIndex].quantity > newSpec.stock) {
      cartItems[itemIndex].quantity = Math.max(newSpec.stock, 1);
    }
  }

  saveCartItems(cartItems);
  return cartItems;
}

/**
 * 清空購物車
 */
export function clearCart() {
  localStorage.removeItem('shop_cart');
  window.dispatchEvent(new CustomEvent('cart-updated', { detail: [] }));
  return [];
}

/**
 * 計算購物車總金額
 * @returns {Number} 總金額
 */
export function getCartTotal() {
  const cartItems = getCartItems();
  return cartItems.reduce((total, item) => {
    return total + (Number(item.selectedPrice ?? item.price) || 0) * item.quantity;
  }, 0);
}

/**
 * 計算購物車商品總數量
 * @returns {Number} 總數量
 */
export function getCartCount() {
  const cartItems = getCartItems();
  return cartItems.reduce((total, item) => {
    return total + item.quantity;
  }, 0);
}
