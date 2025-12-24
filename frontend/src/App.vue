<script setup lang="ts">
import { RouterView } from 'vue-router'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useQuasar } from 'quasar'

const router = useRouter()
const authStore = useAuthStore()
const $q = useQuasar()

const leftDrawerOpen = ref(true) // Open by default for desktop view

const toggleLeftDrawer = () => {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

const username = computed(() => authStore.user?.username || 'User')
const userRole = computed(() => {
  const roleMap: Record<string, string> = {
    'ADMIN': '管理员',
    'MANAGER': '经理',
    'STAFF': '员工',
    'CUSTOMER': '客户'
  }
  return authStore.user?.role ? roleMap[authStore.user.role] : ''
})

const handleLogout = () => {
  $q.dialog({
    title: '确认退出',
    message: '确定要退出登录吗？',
    cancel: true,
    persistent: true
  }).onOk(() => {
    authStore.logout()
    router.push('/login')
    $q.notify({
      type: 'positive',
      message: '已退出登录',
      position: 'top'
    })
  })
}
</script>

<template>
  <q-layout view="hHh lpR fFf">
    <!-- Header -->
    <q-header elevated class="bg-gradient">
      <q-toolbar class="text-white">
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Toggle menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          <div class="row items-center no-wrap">
            <q-icon name="shopping_cart" size="32px" class="q-mr-sm" />
            <span class="text-weight-bold">Shopro 电商管理系统</span>
          </div>
        </q-toolbar-title>

        <q-btn flat round dense icon="notifications" aria-label="Notifications" />
        
        <q-btn flat round dense>
          <q-avatar size="32px" color="white" text-color="primary">
            <q-icon name="person" />
          </q-avatar>
          
          <q-menu>
            <q-list style="min-width: 200px">
              <q-item>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ username }}</q-item-label>
                  <q-item-label caption>{{ userRole }}</q-item-label>
                </q-item-section>
              </q-item>
              
              <q-separator />
              
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
              
              <q-item clickable v-close-popup @click="handleLogout">
                <q-item-section avatar>
                  <q-icon name="logout" color="negative" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-negative">退出登录</q-item-label>
                </q-item-section>
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
      :width="260"
      :breakpoint="1024"
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
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
}

/* Toolbar text should be white for contrast */
.q-toolbar {
  color: white;
}

.q-toolbar .q-btn {
  color: white;
}

.q-toolbar-title {
  color: white;
  font-size: 1.1rem;
}

.active-item {
  background: linear-gradient(90deg, #1976D2 0%, #1565C0 100%);
  color: white;
  border-radius: 8px;
}

.active-item .q-icon {
  color: white;
}

.q-item {
  border-radius: 8px;
  margin: 4px 8px;
  font-size: 0.95rem;
}

.q-item-label {
  font-weight: 500;
  color: #424242;
}

/* Improve text contrast globally */
.q-field__label,
.q-field__native,
.text-caption,
.text-grey-7 {
  color: #616161 !important;
}

.text-grey-6 {
  color: #757575 !important;
}

.text-dark {
  color: #212121 !important;
}

/* Card text contrast */
.q-card__section {
  color: #212121;
}

/* Table text contrast */
.q-table tbody td {
  color: #212121;
  font-size: 0.9rem;
}

.q-table thead th {
  color: #424242;
  font-weight: 600;
  font-size: 0.95rem;
}
</style>
