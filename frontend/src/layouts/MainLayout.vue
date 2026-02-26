<template>
  <q-layout view="hHh lpR fFf" class="admin-shell">
    <!-- Header -->
    <q-header elevated class="bg-primary text-white admin-header">
      <q-toolbar class="admin-toolbar">
        <q-btn dense flat round icon="menu" aria-label="切換側邊導覽" @click="toggleLeftDrawer" />
        
        <q-toolbar-title>
          <q-icon name="shopping_cart" size="sm" class="q-mr-sm" />
          Shopro 商城 - 管理後台
        </q-toolbar-title>

        <q-space />

        <!-- 新手導覽 -->
        <q-btn 
          flat 
          dense 
          round 
          icon="help_outline" 
          aria-label="開啟新手導覽"
          @click="handleStartTour"
          class="q-mr-sm"
        >
          <q-tooltip>新手導覽</q-tooltip>
        </q-btn>

        <!-- 通知選單 -->
        <q-btn flat dense round icon="notifications" aria-label="查看通知" data-tour="notifications">
          <q-badge color="red" floating>3</q-badge>
          <q-menu>
            <q-list style="min-width: 300px">
              <q-item>
                <q-item-section>
                  <q-item-label class="text-weight-bold">通知</q-item-label>
                </q-item-section>
              </q-item>
              <q-separator />
              <q-item clickable v-close-popup>
                <q-item-section avatar>
                  <q-avatar color="primary" text-color="white" icon="shopping_bag" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>新訂單 #12345</q-item-label>
                  <q-item-label caption>5 分鐘前</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-btn>

        <!-- 使用者選單 -->
        <q-btn flat dense round icon="account_circle" aria-label="開啟使用者選單" data-tour="user-menu">
          <q-menu>
            <q-list style="min-width: 200px">
              <q-item>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ userName }}</q-item-label>
                  <q-item-label caption>{{ userRoleLabel }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-separator />
              <q-item clickable v-close-popup @click="goToProfile">
                <q-item-section avatar>
                  <q-icon name="person" />
                </q-item-section>
                <q-item-section>個人資料</q-item-section>
              </q-item>
              <q-item clickable v-close-popup @click="restartTour">
                <q-item-section avatar>
                  <q-icon name="help_outline" />
                </q-item-section>
                <q-item-section>重新開始導覽</q-item-section>
              </q-item>
              <q-separator />
              <q-item clickable v-close-popup @click="logout">
                <q-item-section avatar>
                  <q-icon name="logout" />
                </q-item-section>
                <q-item-section>登出</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-btn>
      </q-toolbar>
    </q-header>

    <!-- 側邊導覽 -->
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      :width="250"
      :breakpoint="1024"
      bordered
      class="admin-drawer"
    >
      <q-scroll-area class="fit">
        <q-list padding class="admin-nav-list">
          <q-item
            v-if="authStore.canAccessStaff"
            clickable
            v-ripple
            :active="isActive('home')"
            active-class="bg-primary text-white"
            @click="navigateTo('home')"
            data-tour="dashboard"
          >
            <q-item-section avatar>
              <q-icon name="dashboard" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ adminGlossary.nav.dashboard }}</q-item-label>
            </q-item-section>
          </q-item>

          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="inventory"
            :label="adminGlossary.nav.productManagement"
            :default-opened="isMenuActive(['products', 'categories'])"
            data-tour="products"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('products')"
              active-class="bg-primary text-white"
              @click="navigateTo('products')"
            >
              <q-item-section avatar>
                <q-icon name="shopping_bag" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.productList }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('categories')"
              active-class="bg-primary text-white"
              @click="navigateTo('categories')"
            >
              <q-item-section avatar>
                <q-icon name="category" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.categoryManagement }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 訂單管理 -->
          <q-expansion-item
            icon="receipt"
            :label="adminGlossary.nav.orderManagement"
            :default-opened="isMenuActive(['orders', 'orderDiscounts', 'orderQA'])"
            data-tour="orders"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('orders')"
              active-class="bg-primary text-white"
              @click="navigateTo('orders')"
            >
              <q-item-section avatar>
                <q-icon name="list_alt" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.orderList }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              v-if="authStore.canAccessStaff"
              clickable
              v-ripple
              :active="isActive('orderDiscounts')"
              active-class="bg-primary text-white"
              @click="navigateTo('orderDiscounts')"
            >
              <q-item-section avatar>
                <q-icon name="local_offer" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.orderDiscounts }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              v-if="authStore.canAccessStaff"
              clickable
              v-ripple
              :active="isActive('orderQA')"
              active-class="bg-primary text-white"
              @click="navigateTo('orderQA')"
            >
              <q-item-section avatar>
                <q-icon name="help" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.orderQA }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="people"
            :label="adminGlossary.nav.memberManagement"
            :default-opened="isMenuActive(['customers', 'members', 'memberGroups', 'memberLevels'])"
            data-tour="customers"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('customers')"
              active-class="bg-primary text-white"
              @click="navigateTo('customers')"
            >
              <q-item-section avatar>
                <q-icon name="person" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.memberList }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('members')"
              active-class="bg-primary text-white"
              @click="navigateTo('members')"
            >
              <q-item-section avatar>
                <q-icon name="card_membership" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.memberAdmin }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('memberGroups')"
              active-class="bg-primary text-white"
              @click="navigateTo('memberGroups')"
            >
              <q-item-section avatar>
                <q-icon name="group" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.memberGroups }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('memberLevels')"
              active-class="bg-primary text-white"
              @click="navigateTo('memberLevels')"
            >
              <q-item-section avatar>
                <q-icon name="star" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.memberLevels }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="campaign"
            label="營銷管理"
            :default-opened="isMenuActive(['marketing', 'promotions', 'points', 'edm', 'calendar'])"
            data-tour="marketing"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('marketing')"
              active-class="bg-primary text-white"
              @click="navigateTo('marketing')"
            >
              <q-item-section avatar>
                <q-icon name="insights" />
              </q-item-section>
              <q-item-section>
                <q-item-label>營銷活動</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('promotions')"
              active-class="bg-primary text-white"
              @click="navigateTo('promotions')"
            >
              <q-item-section avatar>
                <q-icon name="local_offer" />
              </q-item-section>
              <q-item-section>
                <q-item-label>促銷管理</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('points')"
              active-class="bg-primary text-white"
              @click="navigateTo('points')"
            >
              <q-item-section avatar>
                <q-icon name="stars" />
              </q-item-section>
              <q-item-section>
                <q-item-label>積分管理</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('edm')"
              active-class="bg-primary text-white"
              @click="navigateTo('edm')"
            >
              <q-item-section avatar>
                <q-icon name="email" />
              </q-item-section>
              <q-item-section>
                <q-item-label>EDM 管理</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('calendar')"
              active-class="bg-primary text-white"
              @click="navigateTo('calendar')"
            >
              <q-item-section avatar>
                <q-icon name="calendar_month" />
              </q-item-section>
              <q-item-section>
                <q-item-label>行事曆排程</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="article"
            label="內容管理"
            :default-opened="isMenuActive(['blog', 'albums'])"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('blog')"
              active-class="bg-primary text-white"
              @click="navigateTo('blog')"
            >
              <q-item-section avatar>
                <q-icon name="edit_note" />
              </q-item-section>
              <q-item-section>
                <q-item-label>文章管理</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('albums')"
              active-class="bg-primary text-white"
              @click="navigateTo('albums')"
            >
              <q-item-section avatar>
                <q-icon name="photo_library" />
              </q-item-section>
              <q-item-section>
                <q-item-label>相簿管理</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="payment"
            :label="adminGlossary.nav.paymentManagement"
            :default-opened="isMenuActive(['paymentDashboard', 'paymentTransactions', 'paymentSettings', 'ecpayConfig', 'paymentCallbackLogs'])"
          >
            <q-item
              clickable
              v-ripple
              :active="isActive('paymentDashboard')"
              active-class="bg-primary text-white"
              @click="navigateTo('paymentDashboard')"
            >
              <q-item-section avatar>
                <q-icon name="dashboard" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.paymentDashboard }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('paymentTransactions')"
              active-class="bg-primary text-white"
              @click="navigateTo('paymentTransactions')"
            >
              <q-item-section avatar>
                <q-icon name="receipt_long" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.paymentTransactions }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              v-if="authStore.canAccessManager"
              clickable
              v-ripple
              :active="isActive('paymentSettings')"
              active-class="bg-primary text-white"
              @click="navigateTo('paymentSettings')"
            >
              <q-item-section avatar>
                <q-icon name="settings" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ adminGlossary.nav.paymentSettings }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              v-if="authStore.canAccessManager"
              clickable
              v-ripple
              :active="isActive('ecpayConfig')"
              active-class="bg-primary text-white"
              @click="navigateTo('ecpayConfig')"
            >
              <q-item-section avatar>
                <q-icon name="account_balance" />
              </q-item-section>
              <q-item-section>
                <q-item-label>ECPay 設定</q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              clickable
              v-ripple
              :active="isActive('paymentCallbackLogs')"
              active-class="bg-primary text-white"
              @click="navigateTo('paymentCallbackLogs')"
            >
              <q-item-section avatar>
                <q-icon name="history" />
              </q-item-section>
              <q-item-section>
                <q-item-label>付款回傳紀錄</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 分隔線 -->
          <q-separator v-if="authStore.canAccessStaff" class="q-my-md" />
          
          <q-item
            v-if="authStore.canAccessManager"
            clickable
            v-ripple
            :active="isActive('statistics')"
            active-class="bg-primary text-white"
            @click="navigateTo('statistics')"
          >
            <q-item-section avatar>
              <q-icon name="bar_chart" />
            </q-item-section>
            <q-item-section>
                <q-item-label>報表統計</q-item-label>
            </q-item-section>
          </q-item>

          <q-item
            v-if="authStore.canAccessStaff"
            clickable
            v-ripple
            :active="isActive('operationLogs')"
            active-class="bg-primary text-white"
            @click="navigateTo('operationLogs')"
          >
            <q-item-section avatar>
              <q-icon name="history" />
            </q-item-section>
            <q-item-section>
              <q-item-label>操作日誌</q-item-label>
            </q-item-section>
          </q-item>

          <q-item
            v-if="authStore.canAccessManager"
            clickable
            v-ripple
            :active="isActive('users')"
            active-class="bg-primary text-white"
            @click="navigateTo('users')"
          >
            <q-item-section avatar>
              <q-icon name="manage_accounts" />
            </q-item-section>
            <q-item-section>
              <q-item-label>帳號管理</q-item-label>
            </q-item-section>
          </q-item>

          <q-item
            v-if="authStore.canAccessAdmin"
            clickable
            v-ripple
            :active="isActive('systemSettings')"
            active-class="bg-primary text-white"
            @click="navigateTo('systemSettings')"
          >
            <q-item-section avatar>
              <q-icon name="settings" />
            </q-item-section>
            <q-item-section>
              <q-item-label>系統設定</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container class="admin-page-container">
      <div class="admin-content-shell">
        <router-view />
      </div>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useQuasar } from 'quasar'
import { startTour, isTourCompleted, resetTour } from '@/utils/tour'
import { adminGlossary } from '@/constants/adminGlossary'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const $q = useQuasar()

const leftDrawerOpen = ref(true)

const userName = computed(() => authStore.user?.username || '使用者')
const userRole = computed(() => authStore.user?.role || 'ADMIN')
const userRoleLabel = computed(() => {
  const map: Record<string, string> = {
    ADMIN: '系統管理員',
    MANAGER: '經理',
    STAFF: '員工',
    CUSTOMER: '顧客'
  }
  return map[userRole.value] || userRole.value
})

const toggleLeftDrawer = () => {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

const isActive = (routeName: string) => {
  return route.name === routeName
}

const isMenuActive = (routeNames: string[]) => {
  return routeNames.some(name => route.name === name)
}

const navigateTo = (name: string) => {
  router.push({ name })
}

const goToProfile = () => {
  router.push({ name: 'profile' })
}

const logout = () => {
  $q.dialog({
    title: '確認登出',
    message: '確定要登出目前帳號嗎？',
    cancel: true,
    persistent: true
  }).onOk(() => {
    authStore.logout()
    router.push({ name: 'login' })
  })
}

// 啟動新手導覽
const handleStartTour = () => {
  nextTick(() => {
    startTour(true)
  })
}

// 重新開始導覽
const restartTour = () => {
  resetTour()
  nextTick(() => {
    startTour(true)
  })
}

onMounted(() => {
  if (!isTourCompleted()) {
    setTimeout(() => {
      startTour()
    }, 1000)
  }
})
</script>

<style scoped>
.admin-shell {
  background:
    radial-gradient(circle at 4% 0%, rgba(22, 163, 74, 0.08), transparent 30%),
    radial-gradient(circle at 100% 8%, rgba(15, 118, 110, 0.1), transparent 28%),
    #f6f8fc;
}

.admin-header {
  backdrop-filter: blur(10px);
  background: linear-gradient(90deg, rgba(15, 23, 42, 0.96) 0%, rgba(30, 41, 59, 0.95) 55%, rgba(15, 118, 110, 0.92) 100%) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-toolbar {
  min-height: 64px;
  padding-inline: 8px;
}

.admin-drawer {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(10px);
}

.admin-drawer :deep(.q-drawer__content) {
  border-right: 1px solid #e5eaf4;
}

.admin-nav-list {
  padding-top: 10px;
  padding-bottom: 14px;
}

.admin-nav-list :deep(.q-item) {
  min-height: 42px;
  margin: 3px 8px;
  border-radius: 12px;
}

.admin-nav-list :deep(.q-item.q-router-link--active),
.admin-nav-list :deep(.q-item--active) {
  box-shadow: 0 8px 16px rgba(25, 118, 210, 0.16);
}

.admin-nav-list :deep(.q-expansion-item__container) {
  border-radius: 12px;
  margin: 4px 6px;
  overflow: hidden;
}

.admin-nav-list :deep(.q-expansion-item__content .q-item) {
  margin-left: 4px;
}

.admin-page-container {
  background: transparent;
}

.admin-content-shell {
  min-height: 100%;
  padding: 10px 10px 18px;
}

@media (max-width: 1023px) {
  .admin-toolbar {
    min-height: 58px;
  }

  .admin-content-shell {
    padding: 6px 6px 14px;
  }
}
</style>
