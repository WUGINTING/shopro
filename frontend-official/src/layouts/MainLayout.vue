<template>
  <q-layout view="lHh LpR lFf">
    <!-- 頂部導航列 (僅手機版顯示) -->
    <q-header elevated class="bg-white text-primary lt-md">
      <q-toolbar class="q-px-md">
        <q-toolbar-title
          class="text-h5 text-weight-bold header-title"
          @click="$router.push('/')"
        >
          <q-img
            :src="img_logo"
            alt="logo"
            class="header-logo"
            spinner-color="white"
          />
          伸遠國際
        </q-toolbar-title>

        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          class="lt-md"
          @click="windowStore.toggleLeftDrawer()"
        />
        <q-btn
          flat
          dense
          round
          icon="design_services"
          aria-label="Design Menu"
          class="lt-md q-ml-sm"
          @click="windowStore.toggleRightDrawer()"
        />
      </q-toolbar>
    </q-header>

    <!-- 左側選單 -->
    <DrawerMenu
      v-model="leftDrawerOpen"
      side="left"
      :is-mobile="isMobile"
      :menu-items="leftMenuItems"
      :header-config="leftHeaderConfig"
      :footer-config="leftFooterConfig"
    />

    <!-- 右側選單 -->
    <DrawerMenu
      v-model="rightDrawerOpen"
      side="right"
      :is-mobile="isMobile"
      :menu-items="rightMenuItems"
      :header-config="rightHeaderConfig"
      :footer-config="rightFooterConfig"
    />

    <!-- 主要內容區域 -->
    <q-page-container class="main-content-area">
      <router-view />

      <!-- 頁尾 -->
      <footer class="bg-primary text-white">
        <div class="q-pa-md">
          <div class="row q-col-gutter-md">
            <div
              v-for="section in footerSections"
              :key="section.title"
              class="col-12 col-md-4"
            >
              <div class="text-h6 text-weight-bold q-mb-sm text-white">
                {{ section.title }}
              </div>
              <div
                v-for="(item, idx) in section.items"
                :key="idx"
                class="q-mb-xs text-body2"
              >
                <Icon
                  v-if="item.icon"
                  :icon="item.icon"
                  class="q-mr-sm text-white icon-sm"
                />
                <span>{{ item.text }}</span>
              </div>
            </div>
          </div>

          <q-separator class="q-my-md" color="grey-7" />

          <div class="row items-center justify-between q-gutter-sm">
            <div>
              <div
                v-for="(text, idx) in copyrightText"
                :key="idx"
                class="text-caption col-auto"
              >
                {{ text }}
              </div>
            </div>
            <div class="q-gutter-sm col-auto">
              <q-btn
                v-for="social in socialLinksWithActions"
                :key="social.name"
                round
                flat
                size="md"
                class="text-white social-btn"
                @click="social.action"
              >
                <Icon :icon="social.icon" class="icon-lg" />
              </q-btn>
            </div>
          </div>
        </div>
      </footer>
    </q-page-container>

    <!-- 返回頂部按鈕 -->
    <ScrollToTopBtn />
  </q-layout>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { Icon } from '@iconify/vue';
import ScrollToTopBtn from 'src/components/ScrollToTopBtn.vue';
import DrawerMenu from 'src/components/DrawerMenu.vue';
import { useWindowStore } from 'src/store/modules/window.js';
import {
  leftMenuItems,
  rightMenuItems,
  socialLinks,
  footerSections,
  copyrightText,
} from 'src/utils/testData.js';

const img_logo = '/icons/logo.jpg';
const img_logo_shop = '/icons/logo_shop.png';

const router = useRouter();

// 使用 window store
const windowStore = useWindowStore();
const { leftDrawerOpen, rightDrawerOpen, isMobile } = storeToRefs(windowStore);

// 初始化和清理
onMounted(() => {
  windowStore.init();
});

onUnmounted(() => {
  windowStore.cleanup();
});

// 为 socialLinks 添加 action
const socialLinksWithActions = socialLinks.map(link => ({
  ...link,
  action: () => window.open(link.url, '_blank'),
}));

// 左側選單配置
const leftHeaderConfig = computed(() => ({
  logo: img_logo,
  logoClass: 'logo-img-left',
  title: '伸遠國際',
  onClick: () => router.push('/'),
}));

const leftFooterConfig = computed(() => ({
  title: '關注我們',
  socialLinks: socialLinksWithActions,
}));

// 右側選單配置
const rightHeaderConfig = computed(() => ({
  logo: img_logo_shop,
  logoClass: 'logo-img-right',
  class: 'text-center',
  onClick: () => router.push('/shop'),
}));

const rightFooterConfig = computed(() => ({
  title: '聯絡我們',
  contact: {
    icon: 'mdi:phone',
    text: '0988-178-713',
  },
}));
</script>

<style lang="scss" scoped>
$border-color: rgba(0, 0, 0, 0.08);
$white-hover: rgba(255, 255, 255, 0.2);

// Header styles
.header-title {
  cursor: pointer;
}

.header-logo {
  max-width: 70px;
}

// Logo sizes for drawer
:deep(.logo-img-left) {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
}

:deep(.logo-img-right) {
  width: 120px;
  height: 60px;
}

// Icon sizes
.icon-lg {
  font-size: 1.5em;
}

.icon-sm {
  font-size: 1em;
}

.main-content-area {
  background: $primary;
  min-height: 100vh;
  :deep(.q-page) {
    background: transparent;
    color: white;
  }
}

.q-header {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95) !important;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.social-btn {
  min-width: 40px;
  min-height: 40px;
  transition: transform 0.2s ease, background 0.2s ease;
  
  &:hover {
    transform: scale(1.1);
    background: $white-hover;
  }
}

@media (max-width: 1024px) {
  .q-toolbar-title {
    font-size: 1.2rem;
  }

  footer {
    .row.items-center.justify-between {
      flex-direction: column;
      align-items: center;
      text-align: center;
      .text-caption {
        margin-bottom: 8px;
      }
    }
    .col-12.col-md-4 {
      margin-bottom: 24px;
      text-align: center;
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

@media (max-width: 480px) {
  footer {
    .q-pa-md {
      padding: 16px;
    }
    .text-h6 {
      font-size: 1rem;
    }
    .text-body2 {
      font-size: 0.875rem;
    }
  }
}
</style>
