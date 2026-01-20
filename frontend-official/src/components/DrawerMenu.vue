<template>
  <q-drawer
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :side="side"
    :width="280"
    :breakpoint="1024"
    :overlay="isMobile"
    :behavior="isMobile ? 'mobile' : 'desktop'"
    bordered
    class="bg-white"
  >
    <div class="drawer-content">
      <!-- 頂部區域 (Logo 或其他內容) -->
      <div
        v-if="headerConfig"
        class="q-pa-md border-bottom logo-container"
        :class="headerConfig.class"
        @click="headerConfig.onClick"
      >
        <q-img
          v-if="headerConfig.logo"
          :src="headerConfig.logo"
          alt="logo"
          :class="headerConfig.logoClass"
          fit="contain"
          spinner-color="primary"
        />
        <div v-if="headerConfig.title" class="text-h6 text-weight-bold text-primary logo-text">
          {{ headerConfig.title }}
        </div>
      </div>

      <!-- 選單列表 -->
      <q-scroll-area class="drawer-scroll">
        <q-list>
          <div v-for="item in menuItems" :key="item.name">
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
          </div>
        </q-list>
      </q-scroll-area>

      <!-- 底部區域 -->
      <div v-if="footerConfig" class="social-footer q-pa-md text-center border-top">
        <div class="text-caption text-grey-6">{{ footerConfig.title }}</div>
        
        <!-- 社群按鈕 -->
        <div v-if="footerConfig.socialLinks" class="q-gutter-sm">
          <q-btn
            v-for="social in footerConfig.socialLinks"
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

        <!-- 聯絡資訊 -->
        <div v-if="footerConfig.contact" class="text-body2 text-primary q-mt-sm">
          <Icon :icon="footerConfig.contact.icon" class="q-mr-xs icon-sm" />
          {{ footerConfig.contact.text }}
        </div>
      </div>
    </div>
  </q-drawer>
</template>

<script setup>
import { Icon } from '@iconify/vue';

defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  side: {
    type: String,
    default: 'left',
    validator: (value) => ['left', 'right'].includes(value),
  },
  isMobile: {
    type: Boolean,
    default: false,
  },
  menuItems: {
    type: Array,
    required: true,
  },
  headerConfig: {
    type: Object,
    default: null,
    // 格式: { logo, logoClass, title, class, onClick }
  },
  footerConfig: {
    type: Object,
    default: null,
    // 格式: { title, socialLinks, contact }
  },
});

defineEmits(['update:modelValue']);
</script>

<style lang="scss" scoped>
$border-color: rgba(0, 0, 0, 0.08);
$primary-hover: rgba(240, 131, 4, 0.1);

.border-bottom {
  border-bottom: 1px solid $border-color;
}
.border-top {
  border-top: 1px solid $border-color;
}

.logo-container {
  cursor: pointer;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  gap: 12px;
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

.q-drawer {
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  border-left: 1px solid rgba(0, 0, 0, 0.05);
  
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), 
              box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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
  transition: background 0.2s ease, color 0.2s ease;
  
  &:hover {
    background: $primary-hover;
  }
  &.q-router-link--active {
    background: $primary;
  }
}

.social-btn-drawer {
  min-width: 40px;
  min-height: 40px;
  transition: transform 0.2s ease, background 0.2s ease;
  
  &:hover {
    transform: scale(1.1);
    background: $primary-hover;
  }
}
</style>
