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
              alt="logo"
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

            <!-- 產品清單多層選單 -->
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
                :content-style="{ minWidth: '200px' }"
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

                    <q-separator />

                    <!-- 甜筒分類 -->
                    <q-item
                      clickable
                      v-close-popup
                      to="/shop/product/list?category=dessert"
                    >
                      <q-item-section>
                        <q-item-label>甜筒</q-item-label>
                      </q-item-section>
                    </q-item>

                    <!-- 日系產品分類 -->
                    <q-item
                      clickable
                      v-close-popup
                      to="/shop/product/list?category=japanese"
                    >
                      <q-item-section>
                        <q-item-label>日系產品</q-item-label>
                      </q-item-section>
                    </q-item>

                    <!-- 其他分類 -->
                    <q-item
                      clickable
                      v-close-popup
                      to="/shop/product/list?category=other"
                    >
                      <q-item-section>
                        <q-item-label>其他</q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </template>
              </q-btn-dropdown>
            </div>

            <a href="#" class="nav-link">會員中心</a>
          </nav>

          <!-- 右側圖標 -->
          <div class="shop-nav-icons">
            <!-- 搜尋 -->
            <q-btn flat dense round icon="search" @click="handleSearch">
              <q-tooltip>搜尋</q-tooltip>
            </q-btn>

            <!-- 通知 -->
            <q-btn
              flat
              dense
              round
              icon="notifications"
              @click="handleNotification"
            >
              <q-tooltip>通知</q-tooltip>
            </q-btn>

            <!-- 購物車 -->
            <q-btn
              flat
              dense
              round
              icon="shopping_cart"
              class="cart-btn"
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
              @click="drawer = !drawer"
            />
          </div>
        </div>
      </q-toolbar>
    </q-header>

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
          >
            <q-list padding>
              <q-item clickable v-ripple>
                <q-item-section>所有商品</q-item-section>
              </q-item>

              <q-expansion-item label="甜筒" header-class="q-pl-md">
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>巧克力甜筒</q-item-section>
                </q-item>
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>草莓甜筒</q-item-section>
                </q-item>
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>香草甜筒</q-item-section>
                </q-item>
              </q-expansion-item>

              <q-expansion-item label="日系產品" header-class="q-pl-md">
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>居家用品</q-item-section>
                </q-item>
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>文具用品</q-item-section>
                </q-item>
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>食品零食</q-item-section>
                </q-item>
              </q-expansion-item>

              <q-expansion-item label="其他" header-class="q-pl-md">
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>生活雜貨</q-item-section>
                </q-item>
                <q-item clickable v-ripple class="q-pl-lg">
                  <q-item-section>特賣商品</q-item-section>
                </q-item>
              </q-expansion-item>
            </q-list>
          </q-expansion-item>

          <q-item clickable v-ripple>
            <q-item-section avatar>
              <q-icon name="person" />
            </q-item-section>
            <q-item-section>會員中心</q-item-section>
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
                <li><a href="#">品牌故事</a></li>
                <li><a href="#">隱私權政策</a></li>
                <li><a href="#">服務條款</a></li>
              </ul>
            </div>

            <!-- 顧客服務 -->
            <div class="footer-col">
              <h4 class="footer-title">顧客服務</h4>
              <ul class="footer-links">
                <li><a href="#">訂單查詢</a></li>
                <li><a href="#">退換貨說明</a></li>
                <li><a href="#">常見問題</a></li>
              </ul>
            </div>

            <!-- 聯絡我們 -->
            <div class="footer-col">
              <h4 class="footer-title">聯絡我們</h4>
              <ul class="footer-links">
                <li>客服信箱: service@demo.com</li>
                <li>客服專線: 02-1234-5678</li>
                <li>服務時間: 09:00 - 18:00</li>
              </ul>
            </div>

            <!-- 社群媒體 -->
            <div class="footer-col">
              <h4 class="footer-title">關注我們</h4>
              <div class="social-icons">
                <q-btn
                  flat
                  dense
                  round
                  icon="facebook"
                  size="md"
                  color="white"
                />
                <q-btn
                  flat
                  dense
                  round
                  icon="telegram"
                  size="md"
                  color="white"
                />
                <q-btn flat dense round icon="mail" size="md" color="white" />
              </div>
            </div>
          </div>

          <q-separator class="q-my-md" dark />

          <div class="copyright text-center">
            &copy; 2024 com.info.ecommerce. All Rights Reserved.
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
import { ref, onMounted, onUnmounted } from 'vue';
import { useQuasar } from 'quasar';
import CartDrawer from 'components/shop/CartDrawer.vue';
import ScrollToTopBtn from 'src/components/ScrollToTopBtn.vue';
import BackToOfficialBtn from 'src/components/BackToOfficialBtn.vue';
import { getCartCount } from 'src/utils/cart.js';

const $q = useQuasar();

const img_logo_shop = '/icons/logo_shop.png';

// 側邊欄狀態
const drawer = ref(false);
const showCartDrawer = ref(false);

// 購物車數量 (從 localStorage 讀取)
const cartCount = ref(0);

// 控制子選單顯示狀態
const subMenuStates = ref({
  sweetCone: false,
  japaneseProduct: false,
  other: false,
});

// 控制產品選單顯示
const showProductMenu = ref(false);
let menuCloseTimer = null;

// 處理選單進入
const handleMenuEnter = () => {
  if (menuCloseTimer) {
    clearTimeout(menuCloseTimer);
    menuCloseTimer = null;
  }
  showProductMenu.value = true;
};

// 處理選單離開
const handleMenuLeave = () => {
  menuCloseTimer = setTimeout(() => {
    showProductMenu.value = false;
  }, 200);
};

// 滾動狀態
const isScrolled = ref(false);

// 更新購物車數量
const updateCartCount = () => {
  cartCount.value = getCartCount();
};

// 滾動監聽
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

// 在掛載時添加 body class 並讀取購物車數量
onMounted(() => {
  document.body.classList.add('shop-body');
  updateCartCount();
  window.addEventListener('scroll', handleScroll);
});

// 在卸載時移除 body class
onUnmounted(() => {
  document.body.classList.remove('shop-body');
  window.removeEventListener('scroll', handleScroll);
});

// 搜尋功能
const handleSearch = () => {
  $q.notify({
    message: '搜尋功能開發中...',
    color: 'info',
    position: 'top',
    timeout: 2000,
  });
};

// 通知功能
const handleNotification = () => {
  $q.notify({
    message: '目前沒有新通知',
    color: 'info',
    position: 'top',
    timeout: 2000,
  });
};

// 購物車功能
const handleCart = () => {
  showCartDrawer.value = true;
};
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

  .social-icons {
    display: flex;
    gap: 10px;
  }
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
