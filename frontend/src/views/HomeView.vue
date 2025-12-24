<template>
  <q-page class="q-pa-md">
    <div class="dashboard">
      <!-- Welcome Banner -->
      <q-card class="welcome-banner q-mb-md">
        <q-card-section class="row items-center">
          <div class="col">
            <div class="text-h4 text-weight-bold">欢迎回来！</div>
            <div class="text-subtitle1 q-mt-sm">今天是 {{ currentDate }}，让我们开始新的一天吧</div>
          </div>
          <div class="col-auto">
            <q-icon name="shopping_cart" size="80px" style="opacity: 0.3" />
          </div>
        </q-card-section>
      </q-card>

      <!-- Stats Row -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="stat-card stat-card-1">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="inventory_2" size="40px" color="primary" />
                <div>
                  <div class="text-h4 text-weight-bold">1,234</div>
                  <div class="text-caption text-grey-7">总商品数</div>
                </div>
              </div>
              <q-badge color="positive" class="q-mt-sm">+12% ↑</q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="stat-card stat-card-2">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="shopping_bag" size="40px" color="orange" />
                <div>
                  <div class="text-h4 text-weight-bold">856</div>
                  <div class="text-caption text-grey-7">待处理订单</div>
                </div>
              </div>
              <q-badge color="warning" class="q-mt-sm">+8% ↑</q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="stat-card stat-card-3">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="people" size="40px" color="teal" />
                <div>
                  <div class="text-h4 text-weight-bold">5,678</div>
                  <div class="text-caption text-grey-7">总客户数</div>
                </div>
              </div>
              <q-badge color="positive" class="q-mt-sm">+15% ↑</q-badge>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="stat-card stat-card-4">
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="attach_money" size="40px" color="green" />
                <div>
                  <div class="text-h4 text-weight-bold">$89.2K</div>
                  <div class="text-caption text-grey-7">本月销售额</div>
                </div>
              </div>
              <q-badge color="positive" class="q-mt-sm">+23% ↑</q-badge>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Quick Actions -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="text-h6 text-weight-bold">快速操作</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <div class="row q-col-gutter-md">
            <div class="col-6 col-sm-4 col-md-2" v-for="action in quickActions" :key="action.label">
              <q-btn
                unelevated
                no-caps
                stack
                :color="action.color"
                :icon="action.icon"
                :label="action.label"
                class="full-width q-py-md"
                @click="action.onClick"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Recent Activity -->
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="row items-center justify-between">
                <div class="text-h6 text-weight-bold">最近订单</div>
                <q-btn flat dense color="primary" label="查看全部" @click="$router.push('/orders')" />
              </div>
            </q-card-section>
            <q-card-section class="q-pt-none">
              <q-timeline color="secondary">
                <q-timeline-entry
                  title="订单 #12345 已完成支付"
                  subtitle="2024-12-24 10:30"
                  icon="check_circle"
                  color="primary"
                />
                <q-timeline-entry
                  title="订单 #12344 等待发货"
                  subtitle="2024-12-24 09:15"
                  icon="local_shipping"
                  color="orange"
                />
                <q-timeline-entry
                  title="订单 #12343 已发货"
                  subtitle="2024-12-24 08:00"
                  icon="flight_takeoff"
                  color="teal"
                />
                <q-timeline-entry
                  title="订单 #12342 已送达"
                  subtitle="2024-12-23 20:45"
                  icon="done_all"
                  color="positive"
                />
              </q-timeline>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-6">
          <q-card>
            <q-card-section>
              <div class="row items-center justify-between">
                <div class="text-h6 text-weight-bold">热销商品</div>
                <q-btn flat dense color="primary" label="查看全部" @click="$router.push('/products')" />
              </div>
            </q-card-section>
            <q-card-section class="q-pt-none">
              <q-list separator>
                <q-item v-for="i in 4" :key="i">
                  <q-item-section avatar>
                    <q-avatar rounded size="50px" color="grey-3" text-color="primary">
                      <q-icon name="inventory_2" />
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>商品名称 {{ i }}</q-item-label>
                    <q-item-label caption>已售 {{ 100 + i * 50 }} 件</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-item-label class="text-primary text-weight-bold">
                      ¥{{ 99 + i * 10 }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
})

const quickActions = [
  {
    icon: 'add_circle',
    label: '新增商品',
    color: 'primary',
    onClick: () => router.push('/products')
  },
  {
    icon: 'receipt',
    label: '处理订单',
    color: 'orange',
    onClick: () => router.push('/orders')
  },
  {
    icon: 'person_add',
    label: '管理客户',
    color: 'teal',
    onClick: () => router.push('/customers')
  },
  {
    icon: 'bar_chart',
    label: '查看报表',
    color: 'green',
    onClick: () => {}
  },
  {
    icon: 'campaign',
    label: '创建活动',
    color: 'deep-purple',
    onClick: () => {}
  },
  {
    icon: 'settings',
    label: '系统设置',
    color: 'blue-grey',
    onClick: () => {}
  }
]
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
  color: white;
}

.stat-card {
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-card-1 {
  border-top: 4px solid #1976D2;
}

.stat-card-2 {
  border-top: 4px solid #ff9800;
}

.stat-card-3 {
  border-top: 4px solid #26A69A;
}

.stat-card-4 {
  border-top: 4px solid #21BA45;
}
</style>
