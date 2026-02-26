<template>
  <q-layout view="hHh lpR fFf" class="store-layout">
    <q-header bordered class="store-header text-dark">
      <div class="top-strip">
        <div class="store-shell top-strip__inner">
          <span class="top-strip__msg">新客首購享免運門檻優惠，結帳流程支援付款狀態追蹤</span>
          <q-btn flat dense no-caps class="top-strip__cta" label="立即選購" @click="goTo('/products')" />
        </div>
      </div>

      <q-toolbar class="store-toolbar store-shell">
        <q-toolbar-title class="brand-wrap">
          <button class="brand-btn" type="button" @click="goTo('/')" aria-label="Go to Shopro home">
            <span class="brand-title">Shopro</span>
            <span class="brand-subtitle">Everyday Essentials, Better Chosen</span>
          </button>
        </q-toolbar-title>

        <nav class="desktop-nav" aria-label="Main store navigation">
          <q-btn
            v-for="item in navItems"
            :key="item.path"
            flat
            no-caps
            class="nav-btn"
            :class="{ 'nav-btn--active': isActive(item.path) }"
            :label="item.label"
            @click="goTo(item.path)"
          />
        </nav>

        <div class="toolbar-actions">
          <q-btn
            flat
            no-caps
            class="desktop-only secondary-cta"
            icon="workspace_premium"
            label="會員專區"
            @click="goTo('/account')"
          />
          <q-btn
            flat
            round
            icon="shopping_bag"
            aria-label="Open cart"
            @click="goTo('/cart')"
          >
            <q-badge v-if="cartCount > 0" floating color="negative">{{ cartCount }}</q-badge>
          </q-btn>
          <q-btn
            unelevated
            color="primary"
            no-caps
            class="login-cta"
            :label="authStore.isAuthenticated ? '帳戶中心' : '登入 / 註冊'"
            @click="goTo(authStore.isAuthenticated ? '/account' : '/login')"
          />
        </div>
      </q-toolbar>

      <div class="mobile-nav-wrap">
        <div class="store-shell mobile-nav" role="navigation" aria-label="Mobile store navigation">
          <q-btn
            v-for="item in navItems"
            :key="`m-${item.path}`"
            flat
            dense
            no-caps
            class="mobile-nav__btn"
            :class="{ 'mobile-nav__btn--active': isActive(item.path) }"
            :label="item.label"
            @click="goTo(item.path)"
          />
        </div>
      </div>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer bordered class="store-footer text-dark">
      <div class="store-shell footer-inner">
        <div class="footer-brand">
          <div class="footer-brand__title">Shopro</div>
          <p class="footer-brand__copy">
            用更清楚的商品資訊、透明價格與可追蹤訂單流程，讓每一次購物更安心。
          </p>
        </div>

        <div class="footer-links" aria-label="Store footer links">
          <q-btn flat size="sm" no-caps label="退換貨政策" @click="goTo('/policy/returns')" />
          <q-btn flat size="sm" no-caps label="付款與配送" @click="goTo('/policy/payment-shipping')" />
          <q-btn flat size="sm" no-caps label="聯絡我們" @click="goTo('/contact')" />
          <q-btn flat size="sm" no-caps label="品牌故事" @click="goTo('/brand')" />
        </div>

        <div class="footer-trust">
          <div class="footer-pill"><q-icon name="payments" size="16px" /> ECPay / 貨到付款</div>
          <div class="footer-pill"><q-icon name="local_shipping" size="16px" /> 配送進度可追蹤</div>
        </div>
      </div>
    </q-footer>
  </q-layout>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCartItems } from '@/utils/storeCart'

interface NavItem {
  label: string
  path: string
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartCount = ref(0)

const navItems: NavItem[] = [
  { label: '商品列表', path: '/products' },
  { label: '品牌故事', path: '/brand' },
  { label: '聯絡我們', path: '/contact' }
]

const syncCartCount = () => {
  cartCount.value = getCartItems().reduce((sum, item) => sum + item.quantity, 0)
}

const isActive = computed(() => (path: string) => route.path.startsWith(path))

const goTo = (path: string) => {
  if (route.path === path) return
  router.push(path)
}

watch(
  () => route.fullPath,
  () => syncCartCount(),
  { immediate: true }
)

onMounted(() => {
  window.addEventListener('storage', syncCartCount)
  window.addEventListener('focus', syncCartCount)
})

onUnmounted(() => {
  window.removeEventListener('storage', syncCartCount)
  window.removeEventListener('focus', syncCartCount)
})
</script>

<style scoped>
.store-layout {
  background: transparent;
}

.store-header {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.88);
  border-bottom-color: #e9e4d7 !important;
}

.top-strip {
  border-bottom: 1px solid #f1ebdf;
  background: linear-gradient(90deg, #fff8f1 0%, #fff 50%, #f2fbf8 100%);
}

.top-strip__inner {
  min-height: 38px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 0.82rem;
}

.top-strip__msg {
  color: #6b4d36;
}

.top-strip__cta {
  color: #8f4f2d;
  font-weight: 700;
  border-radius: 999px;
}

.store-toolbar {
  min-height: 74px;
  gap: 12px;
}

.brand-wrap {
  min-width: 170px;
}

.brand-btn {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  border: 0;
  padding: 0;
  background: transparent;
  cursor: pointer;
}

.brand-title {
  font-size: 1.2rem;
  font-weight: 800;
  letter-spacing: 0.04em;
  color: #2f241f;
}

.brand-subtitle {
  color: #72655c;
  font-size: 0.75rem;
  letter-spacing: 0.04em;
}

.desktop-nav {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-btn {
  color: #4b5563;
  border-radius: 999px;
  padding-inline: 12px;
  min-height: 40px;
}

.nav-btn--active {
  background: #fff5ec;
  color: #8f4f2d;
  font-weight: 700;
}

.toolbar-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 6px;
}

.secondary-cta {
  border-radius: 999px;
  color: #4f5b67;
}

.login-cta {
  border-radius: 999px;
  padding-inline: 14px;
  background: linear-gradient(180deg, #9f5d39 0%, #8f4f2d 100%) !important;
}

.mobile-nav-wrap {
  display: none;
  border-top: 1px solid #f2ede3;
  border-bottom: 1px solid #efe7d8;
  background: rgba(255, 255, 255, 0.82);
}

.mobile-nav {
  display: flex;
  gap: 6px;
  padding: 8px 0;
  overflow-x: auto;
}

.mobile-nav__btn {
  flex: 0 0 auto;
  border-radius: 999px;
  color: #52606d;
}

.mobile-nav__btn--active {
  color: #8f4f2d;
  background: #fff5ec;
  font-weight: 700;
}

.store-footer {
  border-top-color: #e9e4d7 !important;
  background: rgba(255, 252, 247, 0.92);
}

.footer-inner {
  padding: 16px;
  display: grid;
  grid-template-columns: 1.5fr 2fr 1.5fr;
  gap: 16px;
  align-items: start;
}

.footer-brand__title {
  font-weight: 800;
  font-size: 1rem;
  color: #2f241f;
}

.footer-brand__copy {
  margin: 8px 0 0;
  color: #70645a;
  font-size: 0.85rem;
  line-height: 1.55;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.footer-trust {
  display: grid;
  gap: 8px;
}

.footer-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 36px;
  width: fit-content;
  border-radius: 999px;
  border: 1px solid #e8e0cf;
  background: #fff;
  padding: 0 12px;
  color: #5f564d;
  font-size: 0.82rem;
}

.desktop-only {
  display: inline-flex;
}

@media (max-width: 780px) {
  .desktop-nav {
    display: none;
  }

  .mobile-nav-wrap {
    display: block;
  }

  .top-strip__msg {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .top-strip__cta {
    display: none;
  }

  .desktop-only {
    display: none;
  }

  .footer-inner {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}
</style>
