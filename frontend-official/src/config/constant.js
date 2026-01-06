// ========== Cookie Key 定義 ==========
// 認證相關
export const TokenKey = 'vite__token';

// 購物車相關
export const CartItemsKey = 'cart__items';
export const CartTotalKey = 'cart__total';

// 使用者偏好設定
export const LanguageKey = 'app__language';
export const ThemeKey = 'app__theme';

// 彈窗廣告相關
export const PopupAdHideKey = 'popup__ad__hide';

// 其他
export const LastVisitKey = 'last__visit';
export const RememberMeKey = 'remember__me';

// ========== Cookie 白名單 ==========
// (登出時不清空，需手動清空)
export const cookieWhiteList = [LanguageKey, ThemeKey, RememberMeKey];
