<template>
  <q-layout view="hHh lpR fFf" class="shop-layout">
    <!-- Header -->
    <q-header
      :elevated="isScrolled"
      :class="[
        'shop-header',
        'bg-white',
        'text-dark',
        { 'header-fixed': isScrolled },
      ]"
    >
      <q-toolbar class="shop-toolbar">
        <div class="shop-toolbar-container">
          <!-- Logo -->
          <div
            class="q-pa-md text-center border-bottom logo-container logo-container-right"
            @click="$router.push('/shop')"
          >
            <q-img
              :src="img_logo_shop"
              :alt="STORE_NAME"
              class="logo-img-right"
              fit="contain"
              spinner-color="primary"
            />
          </div>

          <!-- 導航連結 (桌面版) -->
          <nav class="shop-nav-links gt-sm">
            <router-link to="/shop/introduce" class="nav-link"
              >商店介紹</router-link
            >
            <router-link to="/shop/news" class="nav-link">最新消息</router-link>

            <!-- 產品清單選單（滑鼠移入展開） -->
            <div
              class="nav-dropdown-wrapper"
              @mouseenter="handleMenuEnter"
              @mouseleave="handleMenuLeave"
            >
              <q-btn-dropdown
                flat
                no-caps
                label="產品清單"
                class="nav-dropdown"
                dropdown-icon="keyboard_arrow_down"
                :content-style="{ minWidth: '220px', maxHeight: '70vh' }"
                v-model="showProductMenu"
              >
                <template v-slot:default>
                  <q-list
                    @mouseenter="handleMenuEnter"
                    @mouseleave="handleMenuLeave"
                  >
                    <q-item
                      clickable
                      v-close-popup
                      to="/shop/product/list?category=all"
                    >
                      <q-item-section>
                        <q-item-label>所有商品</q-item-label>
                      </q-item-section>
                    </q-item>

                    <!-- 分類載入中 -->
                    <q-item v-if="categoriesLoading" dense>
                      <q-item-section avatar class="menu-state-avatar">
                        <q-spinner size="18px" color="primary" />
                      </q-item-section>
                      <q-item-section class="menu-state-text">
                        分類載入中…
                      </q-item-section>
                    </q-item>

                    <!-- 分類載入失敗 -->
                    <q-item
                      v-else-if="categoriesError"
                      clickable
                      dense
                      @click="loadCategories"
                    >
                      <q-item-section avatar class="menu-state-avatar">
                        <q-icon name="refresh" size="18px" />
                      </q-item-section>
                      <q-item-section class="menu-state-text">
                        分類載入失敗，點此重試
                      </q-item-section>
                    </q-item>

                    <template v-else-if="categoryMenu.length > 0">
                      <q-separator />
                      <q-item
                        v-for="category in categoryMenu"
                        :key="category.id"
                        clickable
                        v-close-popup
                        :to="categoryLink(category.id)"
                        :style="menuIndent(category.depth)"
                      >
                        <q-item-section>
                          <q-item-label>{{ category.name }}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-list>
                </template>
              </q-btn-dropdown>
            </div>

            <router-link to="/shop/order/lookup" class="nav-link"
              >訂單查詢</router-link
            >
          </nav>

          <!-- 右側圖標 -->
          <div class="shop-nav-icons">
            <!-- 搜尋 -->
            <q-btn
              flat
              dense
              round
              icon="search"
              aria-label="搜尋商品"
              @click="openSearch"
            >
              <q-tooltip>搜尋</q-tooltip>
            </q-btn>

            <!-- 購物車 -->
            <q-btn
              flat
              dense
              round
              icon="shopping_cart"
              class="cart-btn"
              aria-label="購物車"
              @click="handleCart"
            >
              <q-badge v-if="cartCount > 0" color="red" floating rounded>
                {{ cartCount }}
              </q-badge>
              <q-tooltip>購物車</q-tooltip>
            </q-btn>

            <!-- 手機版選單 -->
            <q-btn
              flat
              dense
              round
              icon="menu"
              class="lt-md"
              aria-label="開啟選單"
              @click="drawer = !drawer"
            />
          </div>
        </div>
      </q-toolbar>
    </q-header>

    <!-- 搜尋對話框 -->
    <q-dialog v-model="showSearch" position="top">
      <q-card class="search-dialog-card">
        <q-form class="search-form" @submit="submitSearch">
          <q-input
            v-model="searchKeyword"
            outlined
            dense
            autofocus
            clearable
            maxlength="100"
            placeholder="搜尋商品名稱…"
            class="search-input"
            @keydown.esc="showSearch = false"
          >
            <template v-slot:prepend>
              <q-icon name="search" />
            </template>
          </q-input>
          <q-btn
            type="submit"
            unelevated
            color="primary"
            label="搜尋"
            class="search-submit"
            :disable="!searchKeyword || !searchKeyword.trim()"
          />
        </q-form>
      </q-card>
    </q-dialog>

    <!-- 側邊欄 (手機版) -->
    <q-drawer
      v-model="drawer"
      side="right"
      overlay
      behavior="mobile"
      :width="280"
      :breakpoint="1024"
      class="shop-drawer"
    >
      <q-scroll-area class="fit">
        <q-list padding>
          <q-item clickable v-ripple to="/shop/introduce">
            <q-item-section avatar>
              <q-icon name="store" />
            </q-item-section>
            <q-item-section>商店介紹</q-item-section>
          </q-item>

          <q-item clickable v-ripple to="/shop/news">
            <q-item-section avatar>
              <q-icon name="article" />
            </q-item-section>
            <q-item-section>最新消息</q-item-section>
          </q-item>

          <!-- 產品清單 -->
          <q-expansion-item
            icon="shopping_bag"
            label="產品清單"
            header-class="text-primary"
            default-opened
          >
            <q-list class="drawer-category-list">
              <q-item
                clickable
                v-ripple
                to="/shop/product/list?category=all"
                class="drawer-category-item"
              >
                <q-item-section>所有商品</q-item-section>
              </q-item>

              <q-item v-if="categoriesLoading" class="drawer-category-item">
                <q-item-section avatar class="menu-state-avatar">
                  <q-spinner size="18px" color="primary" />
                </q-item-section>
                <q-item-section class="menu-state-text">
                  分類載入中…
                </q-item-section>
              </q-item>

              <q-item
                v-else-if="categoriesError"
                clickable
                v-ripple
                class="drawer-category-item"
                @click="loadCategories"
              >
                <q-item-section avatar class="menu-state-avatar">
                  <q-icon name="refresh" size="18px" />
                </q-item-section>
                <q-item-section class="menu-state-text">
                  分類載入失敗，點此重試
                </q-item-section>
              </q-item>

              <template v-else>
                <q-item
                  v-for="category in categoryMenu"
                  :key="category.id"
                  clickable
                  v-ripple
                  :to="categoryLink(category.id)"
                  class="drawer-category-item"
                  :style="drawerIndent(category.depth)"
                >
                  <q-item-section>{{ category.name }}</q-item-section>
                </q-item>
              </template>
            </q-list>
          </q-expansion-item>

          <q-item clickable v-ripple to="/shop/order/lookup">
            <q-item-section avatar>
              <q-icon name="receipt_long" />
            </q-item-section>
            <q-item-section>訂單查詢</q-item-section>
          </q-item>

          <q-separator class="q-my-md" />

          <q-item>
            <q-item-section>
              <div class="text-caption text-grey-7">購物車商品數量</div>
              <div class="text-h6 text-primary">{{ cartCount }} 件</div>
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <!-- 頁面內容 -->
    <q-page-container class="shop-page-container">
      <router-view />

      <!-- Footer -->
      <footer class="shop-footer bg-dark text-white">
        <div class="shop-footer-content">
          <div class="footer-cols">
            <!-- 關於商店 -->
            <div class="footer-col">
              <h4 class="footer-title">關於商店</h4>
              <ul class="footer-links">
                <li><router-link to="/shop/introduce">品牌故事</router-link></li>
                <li><router-link to="/shop/news">最新消息</router-link></li>
                <li>
                  <router-link to="/shop/page/privacy">隱私權政策</router-link>
                </li>
                <li><router-link to="/shop/page/terms">服務條款</router-link></li>
              </ul>
            </div>

            <!-- 顧客服務 -->
            <div class="footer-col">
              <h4 class="footer-title">顧客服務</h4>
              <ul class="footer-links">
                <li>
                  <router-link to="/shop/order/lookup">訂單查詢</router-link>
                </li>
                <li>
                  <router-link to="/shop/page/returns">退換貨說明</router-link>
                </li>
                <li><router-link to="/shop/page/faq">常見問題</router-link></li>
              </ul>
            </div>

            <!-- 聯絡我們（內容來自後台「商店內容設定」） -->
            <div v-if="showContactColumn" class="footer-col">
              <h4 class="footer-title">聯絡我們</h4>

              <ul v-if="storeLoading" class="footer-links">
                <li v-for="n in 3" :key="n">
                  <q-skeleton type="text" dark width="80%" />
                </li>
              </ul>

              <div v-else-if="storeError" class="footer-state">
                聯絡資訊暫時無法載入
                <button
                  type="button"
                  class="footer-retry"
                  @click="loadStoreContent(true)"
                >
                  重新載入
                </button>
              </div>

              <ul v-else class="footer-links footer-contact">
                <li v-if="storeContent.contactEmail">
                  <span class="contact-label">客服信箱</span>
                  <a :href="`mailto:${storeContent.contactEmail}`">{{
                    storeContent.contactEmail
                  }}</a>
                </li>
                <li v-if="storeContent.contactPhone">
                  <span class="contact-label">客服專線</span>
                  <a :href="phoneHref">{{ storeContent.contactPhone }}</a>
                </li>
                <li v-if="storeContent.businessHours">
                  <span class="contact-label">服務時間</span>
                  <span>{{ storeContent.businessHours }}</span>
                </li>
                <li v-if="storeContent.address">
                  <span class="contact-label">地址</span>
                  <span>{{ storeContent.address }}</span>
                </li>
              </ul>
            </div>
          </div>

          <q-separator class="q-my-md" dark />

          <div class="copyright text-center">
            &copy; {{ currentYear }} {{ STORE_NAME }}. All Rights Reserved.
          </div>
        </div>
      </footer>
      <!-- Footer 結束 -->
    </q-page-container>

    <!-- 返回官網按鈕 -->
    <BackToOfficialBtn :hide="showCartDrawer" />

    <!-- 返回頂部按鈕 -->
    <ScrollToTopBtn :hide="showCartDrawer" />

    <!-- 購物車側邊欄 -->
    <CartDrawer v-model="showCartDrawer" @cart-updated="updateCartCount" />
  </q-layout>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CartDrawer from 'components/shop/CartDrawer.vue';
import ScrollToTopBtn from 'src/components/ScrollToTopBtn.vue';
import BackToOfficialBtn from 'src/components/BackToOfficialBtn.vue';
import { getCartCount } from 'src/utils/cart.js';
import { getEnabledCategories, buildCategoryTree } from 'src/api/category.js';
import { getStoreContent, STORE_NAME } from 'src/api/store.js';

const route = useRoute();
const router = useRouter();

const img_logo_shop = '/icons/logo_shop.png';
const currentYear = new Date().getFullYear();

// 側邊欄狀態
const drawer = ref(false);
const showCartDrawer = ref(false);

// 購物車數量 (從 localStorage 讀取)
const cartCount = ref(0);

// ===== 商品分類選單 =====
const categoryTree = ref([]);
const categoriesLoading = ref(false);
const categoriesError = ref(false);

// 將分類樹攤平成選單項目（depth 用於縮排子分類）
const categoryMenu = computed(() => {
  const result = [];
  const walk = (items, depth) => {
    items.forEach(item => {
      result.push({ id: item.id, name: item.name, depth });
      walk(item.children || [], depth + 1);
    });
  };
  walk(categoryTree.value, 0);
  return result;
});

const categoryLink = id => `/shop/product/list?category=${id}`;
const menuIndent = depth =>
  depth > 0 ? { paddingLeft: `${16 + depth * 16}px` } : undefined;
const drawerIndent = depth => ({ paddingLeft: `${56 + depth * 16}px` });

const loadCategories = async () => {
  categoriesLoading.value = true;
  categoriesError.value = false;
  try {
    const res = await getEnabledCategories({ silent: true });
    categoryTree.value = buildCategoryTree(res?.data || []);
  } catch {
    categoriesError.value = true;
  } finally {
    categoriesLoading.value = false;
  }
};

// 控制產品選單顯示（滑鼠移入展開、移出延遲關閉）
const showProductMenu = ref(false);
let menuCloseTimer = null;

const handleMenuEnter = () => {
  if (menuCloseTimer) {
    clearTimeout(menuCloseTimer);
    menuCloseTimer = null;
  }
  showProductMenu.value = true;
};

const handleMenuLeave = () => {
  menuCloseTimer = setTimeout(() => {
    showProductMenu.value = false;
  }, 200);
};

// ===== 商店聯絡資訊 =====
const storeContent = ref({});
const storeLoading = ref(false);
const storeError = ref(false);

const hasContactInfo = computed(() =>
  Boolean(
    storeContent.value.contactEmail ||
      storeContent.value.contactPhone ||
      storeContent.value.businessHours ||
      storeContent.value.address
  )
);

// 載入中或失敗時保留欄位顯示狀態；成功但無任何聯絡資訊則隱藏整欄
const showContactColumn = computed(
  () => storeLoading.value || storeError.value || hasContactInfo.value
);

const phoneHref = computed(
  () => `tel:${(storeContent.value.contactPhone || '').replace(/[^\d+]/g, '')}`
);

const loadStoreContent = async (force = false) => {
  storeLoading.value = true;
  storeError.value = false;
  try {
    storeContent.value = await getStoreContent({ force });
  } catch {
    storeError.value = true;
  } finally {
    storeLoading.value = false;
  }
};

// ===== 搜尋 =====
const showSearch = ref(false);
const searchKeyword = ref('');

const openSearch = () => {
  searchKeyword.value =
    typeof route.query.keyword === 'string' ? route.query.keyword : '';
  showSearch.value = true;
};

const submitSearch = () => {
  const keyword = (searchKeyword.value || '').trim();
  if (!keyword) return;
  showSearch.value = false;
  router.push({ path: '/shop/product/list', query: { keyword } });
};

// ===== 滾動與購物車 =====
const isScrolled = ref(false);

const updateCartCount = () => {
  cartCount.value = getCartCount();
};

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

const handleCart = () => {
  showCartDrawer.value = true;
};

// 切換頁面時自動關閉手機版側邊欄
watch(
  () => route.fullPath,
  () => {
    drawer.value = false;
  }
);

// 在掛載時添加 body class 並讀取購物車數量
onMounted(() => {
  document.body.classList.add('shop-body');
  updateCartCount();
  window.addEventListener('scroll', handleScroll);
  // 購物車在任何頁面異動（加入、結帳清空）時同步徽章數量
  window.addEventListener('cart-updated', updateCartCount);
  loadCategories();
  loadStoreContent();
});

// 在卸載時移除 body class
onUnmounted(() => {
  document.body.classList.remove('shop-body');
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('cart-updated', updateCartCount);
  if (menuCloseTimer) clearTimeout(menuCloseTimer);
});
</script>

<style lang="scss" scoped>
@import '../css/variables.scss';

// Layout 整體背景 - 使用更高的優先級
.shop-layout {
  background-color: $shop-bg !important;

  :deep(.q-page-container) {
    background-color: $shop-bg !important;
  }

  :deep(.q-page) {
    background-color: $shop-bg !important;
  }
}

.shop-page-container {
  background-color: $shop-bg !important;
}

// Header 樣式 - 始終固定在頂部
.shop-header {
  position: fixed !important;
  top: 0;
  left: 0;
  right: 0;
  z-index: 2000;
  height: $shop-header-height;
  background: #ffffff !important;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;

  :deep(.q-toolbar) {
    background: #ffffff !important;
  }

  &.header-fixed {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.shop-toolbar {
  height: $shop-header-height;
  padding: 0;
}

.shop-toolbar-container {
  max-width: $shop-container-max-width;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-container-right {
  cursor: pointer;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: none !important;
  padding: 8px 0 !important;
}

.logo-img-right {
  width: 120px;
  height: 60px;
}

.shop-nav-links {
  display: flex;
  gap: 25px;
  align-items: center;

  .nav-link {
    color: $shop-text;
    text-decoration: none;
    font-weight: 500;
    transition: color $shop-transition;

    &:hover {
      color: $shop-primary;
    }

    &.router-link-active {
      color: $shop-primary;
    }
  }

  .nav-dropdown-wrapper {
    display: inline-block;
    position: relative;
  }

  .nav-dropdown {
    color: $shop-text;
    font-weight: 500;
    padding: 0 8px;

    :deep(.q-btn__content) {
      font-weight: 500;
      color: $shop-text;
    }

    &:hover {
      color: $shop-primary;

      :deep(.q-btn__content) {
        color: $shop-primary;
      }
    }
  }
}

// 產品清單下拉選單樣式 - 使用更高優先級
:deep(.nav-dropdown) {
  .q-menu {
    background: white !important;

    .q-list {
      background: white !important;
      color: $shop-text !important;
      padding: 8px 0;
    }

    .q-item {
      color: $shop-text !important;
      background: white !important;
      position: relative;
      min-height: 40px;
      padding: 8px 16px;

      &:hover {
        background: $shop-bg-light !important;
        color: $shop-primary !important;

        .q-item__label {
          color: $shop-primary !important;
        }

        // hover時自動展開子選單
        > .q-menu {
          display: block !important;
          opacity: 1 !important;
          visibility: visible !important;
        }
      }

      // 子選單樣式
      > .q-menu {
        transition: opacity 0.2s ease, visibility 0.2s ease;
      }
    }

    .q-item__label {
      color: $shop-text !important;
      font-size: 1rem !important;
      font-weight: 500 !important;
    }

    .q-item-section {
      color: $shop-text !important;
    }

    .q-icon {
      color: $shop-text-secondary !important;
    }

    .q-separator {
      background: #e0e0e0 !important;
      margin: 8px 0;
    }
  }

  // 確保子選單在hover時觸發
  .q-item:hover > .q-menu {
    pointer-events: auto !important;
  }
}

.shop-nav-icons {
  display: flex;
  gap: 8px;
  align-items: center;

  :deep(.q-btn) {
    color: #757575;

    &:hover {
      color: $shop-primary;
    }
  }

  :deep(.q-badge) {
    background: $shop-danger;
  }
}

.cart-btn {
  position: relative;
}

// Drawer 樣式
.shop-drawer {
  :deep(.q-drawer__content) {
    background: white;
  }

  // 修復產品清單文字顏色
  :deep(.q-item) {
    color: $shop-text !important;

    .q-item__label {
      color: $shop-text !important;
    }
  }

  :deep(.q-expansion-item__label) {
    color: $shop-text !important;
  }

  :deep(.q-icon) {
    color: $shop-text-secondary !important;
  }
}

// Footer 樣式
.shop-footer {
  padding: 50px 0 20px;
  margin-top: 50px;
  background: $shop-dark !important;
  color: #e0e0e0 !important;
}

.shop-footer-content {
  max-width: $shop-container-max-width;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-cols {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-col {
  .footer-title {
    color: white;
    margin-bottom: 20px;
    border-left: 3px solid $shop-primary;
    padding-left: 10px;
    font-size: 1.1rem;
    font-weight: 600;
  }

  .footer-links {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      margin-bottom: 10px;
      font-size: 0.9rem;

      a {
        color: #ccc;
        text-decoration: none;
        transition: color $shop-transition;

        &:hover {
          color: $shop-primary;
        }
      }
    }
  }

  .footer-contact li {
    display: flex;
    flex-wrap: wrap;
    gap: 2px 8px;
    color: #ccc;
    overflow-wrap: anywhere;
  }

  .contact-label {
    color: #999;
    flex-shrink: 0;

    &::after {
      content: '：';
    }
  }
}

.footer-state {
  font-size: 0.9rem;
  color: #999;
}

.footer-retry {
  background: none;
  border: none;
  padding: 0;
  margin-left: 6px;
  color: $shop-primary;
  font: inherit;
  cursor: pointer;
  text-decoration: underline;
}

// 產品選單狀態列（載入中 / 失敗）
.menu-state-avatar {
  min-width: 32px;
}

.menu-state-text {
  font-size: 0.9rem;
  color: $shop-text-secondary;
}

.drawer-category-item {
  padding-left: 56px;
  min-height: 40px;
}

// 搜尋對話框
.search-dialog-card {
  width: 600px;
  max-width: calc(100vw - 32px);
  margin-top: calc(#{$shop-header-height} + 16px);
  border-radius: 12px;
}

.search-form {
  display: flex;
  gap: 8px;
  padding: 16px;
  align-items: center;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.search-submit {
  flex-shrink: 0;
}

.copyright {
  padding-top: 20px;
  font-size: 0.8rem;
  color: #999;
}

// 響應式設計
@media (max-width: 1024px) {
  .shop-nav-links {
    display: none;
  }
}

@media (max-width: 768px) {
  .footer-cols {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>
