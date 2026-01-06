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
          @click="leftDrawerOpen = !leftDrawerOpen"
        />
        <q-btn
          flat
          dense
          round
          icon="design_services"
          aria-label="Design Menu"
          class="lt-md q-ml-sm"
          @click="rightDrawerOpen = !rightDrawerOpen"
        />
      </q-toolbar>
    </q-header>

    <!-- 側邊選單 -->
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      :width="280"
      :breakpoint="1024"
      bordered
      class="bg-white"
    >
      <div class="drawer-content">
        <q-scroll-area class="drawer-scroll">
          <div
            class="q-pa-md border-bottom logo-container logo-container-left"
            @click="$router.push('/')"
          >
            <q-img
              :src="img_logo"
              alt="logo"
              class="logo-img-left"
              fit="contain"
              spinner-color="primary"
            />
            <div class="text-h6 text-weight-bold text-primary logo-text">
              伸遠國際
            </div>
          </div>

          <q-list>
            <q-item
              v-for="item in leftMenuItems"
              :key="item.name"
              :to="item.path"
              clickable
              v-ripple
              exact
              class="text-primary"
              active-class="bg-primary text-white"
            >
              <q-item-section avatar>
                <Icon :icon="item.icon" class="icon-lg" />
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-weight-medium">{{
                  item.label
                }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-scroll-area>

        <!-- 社群按鈕區域 -->
        <div class="social-footer q-pa-md text-center border-top">
          <div class="text-caption text-grey-6">關注我們</div>
          <div class="q-gutter-sm">
            <q-btn
              v-for="social in socialLinksWithActions"
              :key="social.name"
              round
              flat
              size="md"
              class="text-primary social-btn-drawer"
              @click="social.action"
            >
              <Icon :icon="social.icon" class="icon-lg" />
            </q-btn>
          </div>
        </div>
      </div>
    </q-drawer>

    <!-- 右側選單 -->
    <q-drawer
      v-model="rightDrawerOpen"
      show-if-above
      side="right"
      :width="280"
      :breakpoint="1024"
      bordered
      class="bg-white"
    >
      <div class="drawer-content">
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

        <q-scroll-area class="drawer-scroll">
          <q-list>
            <template v-for="item in rightMenuItems" :key="item.name">
              <q-expansion-item
                v-if="item.children"
                :label="item.label"
                class="text-primary"
                expand-separator
              >
                <template v-slot:header>
                  <q-item-section avatar>
                    <Icon :icon="item.icon" class="text-primary icon-lg" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium text-primary">{{
                      item.label
                    }}</q-item-label>
                  </q-item-section>
                </template>
                <q-list>
                  <q-item
                    v-for="child in item.children"
                    :key="child.name"
                    :to="child.path"
                    clickable
                    v-ripple
                    exact
                    class="text-primary q-pl-lg"
                    active-class="bg-primary text-white"
                  >
                    <q-item-section avatar>
                      <Icon :icon="child.icon" class="icon-md" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-regular">{{
                        child.label
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-expansion-item>

              <q-item
                v-else
                :to="item.path"
                clickable
                v-ripple
                exact
                class="text-primary"
                active-class="bg-primary text-white"
              >
                <q-item-section avatar>
                  <Icon :icon="item.icon" class="icon-lg" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-medium">{{
                    item.label
                  }}</q-item-label>
                </q-item-section>
              </q-item>
            </template>
          </q-list>
        </q-scroll-area>

        <!-- 聯絡資訊 -->
        <div class="social-footer q-pa-md text-center border-top">
          <div class="text-caption text-grey-6">聯絡我們</div>
          <div class="text-body2 text-primary q-mt-sm">
            <Icon icon="mdi:phone" class="q-mr-xs icon-sm" />0988-178-713
          </div>
        </div>
      </div>
    </q-drawer>

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
import { ref } from 'vue';
import { Icon } from '@iconify/vue';
import ScrollToTopBtn from 'src/components/ScrollToTopBtn.vue';
import {
  leftMenuItems,
  rightMenuItems,
  socialLinks,
  footerSections,
  copyrightText,
} from 'src/utils/testData.js';

const img_logo = '/icons/logo.jpg';
const img_logo_shop = '/icons/logo_shop.png';

const leftDrawerOpen = ref(true);
const rightDrawerOpen = ref(true);

// 为 socialLinks 添加 action
const socialLinksWithActions = socialLinks.map(link => ({
  ...link,
  action: () => window.open(link.url, '_blank'),
}));
</script>

<style lang="scss" scoped>
$border-color: rgba(0, 0, 0, 0.08);
$primary-hover: rgba(240, 131, 4, 0.1);
$white-hover: rgba(255, 255, 255, 0.2);

.border-bottom {
  border-bottom: 1px solid $border-color;
}
.border-top {
  border-top: 1px solid $border-color;
}

// Header styles
.header-title {
  cursor: pointer;
}

.header-logo {
  max-width: 70px;
}

// Logo container styles
.logo-container-left {
  cursor: pointer;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.logo-container-right {
  cursor: pointer;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-img-left {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
}

.logo-img-right {
  width: 120px;
  height: 60px;
}

.logo-text {
  line-height: 1.3;
}

// Icon sizes
.icon-lg {
  font-size: 1.5em;
}

.icon-md {
  font-size: 1.2em;
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

.q-drawer {
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  border-left: 1px solid rgba(0, 0, 0, 0.05);
}

.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.drawer-scroll {
  flex: 1;
  min-height: 0;
}

.social-footer {
  flex-shrink: 0;
}

.q-item {
  &:hover {
    background: $primary-hover;
  }
  &.q-router-link--active {
    background: $primary;
  }
}

.social-btn,
.social-btn-drawer {
  min-width: 40px;
  min-height: 40px;
  &:hover {
    transform: scale(1.1);
  }
}

.social-btn:hover {
  background: $white-hover;
}
.social-btn-drawer:hover {
  background: $primary-hover;
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
