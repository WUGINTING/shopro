<script setup lang="ts">
import { RouterView } from 'vue-router'
import { ref } from 'vue'

const leftDrawerOpen = ref(false)

const toggleLeftDrawer = () => {
  leftDrawerOpen.value = !leftDrawerOpen.value
}
</script>

<template>
  <q-layout view="hHh lpR fFf">
    <!-- Header -->
    <q-header elevated class="bg-gradient">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          <div class="row items-center no-wrap">
            <q-icon name="shopping_cart" size="32px" class="q-mr-sm" />
            <span class="text-weight-bold">Shopro</span>
          </div>
        </q-toolbar-title>

        <q-btn flat round dense icon="notifications" />
        
        <q-btn flat round dense>
          <q-avatar size="32px" color="grey-4" text-color="primary">
            <q-icon name="person" />
          </q-avatar>
          
          <q-menu>
            <q-list style="min-width: 150px">
              <q-item clickable v-close-popup>
                <q-item-section avatar>
                  <q-icon name="person" />
                </q-item-section>
                <q-item-section>个人中心</q-item-section>
              </q-item>
              
              <q-item clickable v-close-popup>
                <q-item-section avatar>
                  <q-icon name="settings" />
                </q-item-section>
                <q-item-section>设置</q-item-section>
              </q-item>
              
              <q-separator />
              
              <q-item clickable v-close-popup>
                <q-item-section avatar>
                  <q-icon name="logout" />
                </q-item-section>
                <q-item-section>退出登录</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-btn>
      </q-toolbar>
    </q-header>

    <!-- Drawer / Sidebar -->
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      class="bg-grey-2"
    >
      <q-scroll-area class="fit">
        <q-list padding>
          <!-- Dashboard -->
          <q-item clickable v-ripple to="/" exact active-class="active-item">
            <q-item-section avatar>
              <q-icon name="dashboard" />
            </q-item-section>
            <q-item-section>
              <q-item-label>控制台</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced />

          <!-- Products -->
          <q-item clickable v-ripple to="/products" active-class="active-item">
            <q-item-section avatar>
              <q-icon name="inventory_2" />
            </q-item-section>
            <q-item-section>
              <q-item-label>商品管理</q-item-label>
            </q-item-section>
          </q-item>

          <!-- Orders -->
          <q-item clickable v-ripple to="/orders" active-class="active-item">
            <q-item-section avatar>
              <q-icon name="shopping_bag" />
            </q-item-section>
            <q-item-section>
              <q-item-label>订单管理</q-item-label>
            </q-item-section>
          </q-item>

          <!-- Customers -->
          <q-item clickable v-ripple to="/customers" active-class="active-item">
            <q-item-section avatar>
              <q-icon name="people" />
            </q-item-section>
            <q-item-section>
              <q-item-label>客户管理</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced />

          <!-- Marketing -->
          <q-item clickable v-ripple>
            <q-item-section avatar>
              <q-icon name="campaign" />
            </q-item-section>
            <q-item-section>
              <q-item-label>营销活动</q-item-label>
            </q-item-section>
          </q-item>

          <!-- Statistics -->
          <q-item clickable v-ripple>
            <q-item-section avatar>
              <q-icon name="bar_chart" />
            </q-item-section>
            <q-item-section>
              <q-item-label>数据统计</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced />

          <!-- Settings -->
          <q-item clickable v-ripple>
            <q-item-section avatar>
              <q-icon name="settings" />
            </q-item-section>
            <q-item-section>
              <q-item-label>系统设置</q-item-label>
            </q-item-section>
          </q-item>

          <!-- About -->
          <q-item clickable v-ripple to="/about" active-class="active-item">
            <q-item-section avatar>
              <q-icon name="info" />
            </q-item-section>
            <q-item-section>
              <q-item-label>关于</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <!-- Page Container -->
    <q-page-container>
      <RouterView />
    </q-page-container>
  </q-layout>
</template>

<style scoped>
.bg-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.active-item {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
}

.active-item .q-icon {
  color: white;
}

.q-item {
  border-radius: 8px;
  margin: 4px 8px;
}
</style>
