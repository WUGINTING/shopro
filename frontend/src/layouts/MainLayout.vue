<template>
  <q-layout view="hHh lpR fFf">
    <!-- Header -->
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />
        
        <q-toolbar-title>
          <q-icon name="shopping_cart" size="sm" class="q-mr-sm" />
          遇日小舖 - 管理系統
        </q-toolbar-title>

        <q-space />

        <!-- 新手導覽按鈕 -->
        <q-btn 
          flat 
          dense 
          round 
          icon="help_outline" 
          @click="handleStartTour"
          class="q-mr-sm"
        >
          <q-tooltip>新手導覽</q-tooltip>
        </q-btn>

        <!-- 通知按鈕 -->
        <q-btn flat dense round icon="notifications" data-tour="notifications">
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

        <!-- 用戶菜單 -->
        <q-btn flat dense round icon="account_circle" data-tour="user-menu">
          <q-menu>
            <q-list style="min-width: 200px">
              <q-item>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ userName }}</q-item-label>
                  <q-item-label caption>{{ userRole }}</q-item-label>
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

    <!-- 側邊導航 -->
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      :width="250"
      :breakpoint="1024"
      bordered
    >
      <q-scroll-area class="fit">
        <q-list padding>
          <!-- 儀表板 (僅管理員、經理、員工可見) -->
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
              <q-item-label>儀表板</q-item-label>
            </q-item-section>
          </q-item>

          <!-- 商品管理 (僅管理員、經理、員工可見) -->
          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="inventory"
            label="商品管理"
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
                <q-item-label>商品列表</q-item-label>
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
                <q-item-label>分類管理</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 訂單管理 -->
          <q-expansion-item
            icon="receipt"
            label="訂單管理"
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
                <q-item-label>訂單列表</q-item-label>
              </q-item-section>
            </q-item>
            <!-- 訂單折扣 (僅管理員、經理、員工可見) -->
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
                <q-item-label>訂單折扣</q-item-label>
              </q-item-section>
            </q-item>
            <!-- 訂單問答 (僅管理員、經理、員工可見) -->
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
                <q-item-label>訂單問答</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 客戶管理 (僅管理員、經理、員工可見) -->
          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="people"
            label="客戶管理"
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
                <q-item-label>客戶列表</q-item-label>
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
                <q-item-label>會員管理</q-item-label>
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
                <q-item-label>會員分組</q-item-label>
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
                <q-item-label>會員等級</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 營銷管理 (僅管理員、經理、員工可見) -->
          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="campaign"
            label="營銷管理"
            :default-opened="isMenuActive(['marketing', 'promotions', 'points', 'edm'])"
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
          </q-expansion-item>

          <!-- 內容管理 (僅管理員、經理、員工可見) -->
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
                <q-item-label>部落格</q-item-label>
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
                <q-item-label>相冊管理</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 支付管理 (僅管理員、經理、員工可見) -->
          <q-expansion-item
            v-if="authStore.canAccessStaff"
            icon="payment"
            label="支付管理"
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
                <q-item-label>支付儀表板</q-item-label>
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
                <q-item-label>交易記錄</q-item-label>
              </q-item-section>
            </q-item>
            <!-- 支付設定 (僅管理員、經理可見) -->
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
                <q-item-label>支付設定</q-item-label>
              </q-item-section>
            </q-item>
            <!-- ECPay 配置 (僅管理員、經理可見) -->
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
                <q-item-label>ECPay 配置</q-item-label>
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
                <q-item-label>回調記錄</q-item-label>
              </q-item-section>
            </q-item>
          </q-expansion-item>

          <!-- 系統管理 -->
          <q-separator v-if="authStore.canAccessStaff" class="q-my-md" />
          
          <!-- 數據統計 (僅管理員、經理可見) -->
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
              <q-item-label>數據統計</q-item-label>
            </q-item-section>
          </q-item>

          <!-- 操作日誌 (僅管理員、經理、員工可見) -->
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

          <!-- 用戶管理 (僅管理員、經理可見) -->
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
              <q-item-label>用戶管理</q-item-label>
            </q-item-section>
          </q-item>

          <!-- 系統設定 (僅管理員可見) -->
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

    <!-- 主內容區域 -->
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useQuasar } from 'quasar'
import { startTour, isTourCompleted, resetTour } from '@/utils/tour'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const $q = useQuasar()

const leftDrawerOpen = ref(true)

const userName = computed(() => authStore.user?.username || '管理員')
const userRole = computed(() => authStore.user?.role || 'ADMIN')

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
    message: '確定要登出嗎？',
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

// 組件掛載時檢查是否需要自動啟動導覽
onMounted(() => {
  // 如果用戶是第一次訪問，自動啟動導覽
  if (!isTourCompleted()) {
    // 延遲一點時間，確保 DOM 已經渲染完成
    setTimeout(() => {
      startTour()
    }, 1000)
  }
})
</script>
