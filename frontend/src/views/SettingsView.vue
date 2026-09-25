<template>
  <q-page padding>
    <div class="settings-page">
      <div class="text-h5 text-weight-bold q-mb-md">系統設定</div>

      <q-tabs v-model="tab" dense align="left" active-color="primary" indicator-color="primary" class="text-grey-8">
        <q-tab name="shipping" label="運費設定" icon="local_shipping" no-caps />
        <q-tab name="status" label="上線檢查" icon="fact_check" no-caps />
      </q-tabs>
      <q-separator class="q-mb-md" />

      <q-tab-panels v-model="tab" animated class="bg-transparent">
        <q-tab-panel name="shipping" class="q-pa-none">
          <q-banner rounded class="bg-blue-1 text-blue-10 q-mb-md">
            結帳運費依此設定計算：每種配送方式使用「啟用」且排序最前面的一筆。
            沒有任何設定時，宅配運費 NT$100、滿 NT$1,000 免運，門市自取免運；某配送方式的設定全部停用時，前台不提供該配送方式。
          </q-banner>

          <div class="row justify-end q-mb-sm">
            <q-btn color="primary" unelevated no-caps icon="add" label="新增運費設定" @click="openEditor()" />
          </div>

          <q-table :rows="configs" :columns="columns" row-key="id" flat bordered :loading="loading" :pagination="{ rowsPerPage: 0 }" hide-bottom>
            <template #body-cell-enabled="props">
              <q-td :props="props">
                <q-toggle :model-value="props.row.enabled" @update:model-value="(value: boolean) => toggle(props.row, value)" />
              </q-td>
            </template>
            <template #body-cell-actions="props">
              <q-td :props="props">
                <q-btn flat dense round icon="edit" color="primary" @click="openEditor(props.row)"><q-tooltip>編輯</q-tooltip></q-btn>
                <q-btn flat dense round icon="delete" color="negative" @click="remove(props.row)"><q-tooltip>刪除</q-tooltip></q-btn>
              </q-td>
            </template>
            <template #no-data>
              <div class="full-width text-center text-grey-7 q-pa-md">尚未設定，目前使用預設運費（宅配 NT$100、滿 NT$1,000 免運；門市自取免運）。</div>
            </template>
          </q-table>
        </q-tab-panel>

        <q-tab-panel name="status" class="q-pa-none">
          <q-banner rounded class="bg-grey-2 q-mb-md">
            以下項目透過伺服器環境變數設定（見部署文件 docs/deployment-guide.md），修改後需重新啟動後端。
          </q-banner>
          <q-list bordered separator class="rounded-borders bg-white">
            <q-item v-for="check in checks" :key="check.key">
              <q-item-section avatar>
                <q-icon :name="check.ok ? 'check_circle' : 'warning'" :color="check.ok ? 'positive' : 'orange-8'" />
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-weight-medium">{{ check.label }}</q-item-label>
                <q-item-label caption>{{ check.detail }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item v-if="statusInfo">
              <q-item-section avatar><q-icon name="schedule" color="primary" /></q-item-section>
              <q-item-section>
                <q-item-label class="text-weight-medium">未付款訂單處理</q-item-label>
                <q-item-label caption>
                  綠界 ATM / 超商代碼繳費期限 {{ statusInfo.ecpayExpireDays }} 天；線上付款訂單
                  {{ statusInfo.unpaidTimeoutHours > 0 ? `${statusInfo.unpaidTimeoutHours} 小時未付款自動取消並歸還庫存` : '不會自動取消' }}
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-tab-panel>
      </q-tab-panels>
    </div>

    <q-dialog v-model="showEditor">
      <q-card style="width: 520px; max-width: 95vw">
        <q-card-section class="text-h6">{{ editing.id ? '編輯運費設定' : '新增運費設定' }}</q-card-section>
        <q-card-section class="q-gutter-md">
          <q-select
            v-model="editing.shippingMethod"
            outlined
            dense
            emit-value
            map-options
            label="配送方式 *"
            :options="[{ label: '宅配到府', value: 'HOME_DELIVERY' }, { label: '門市自取', value: 'STORE_PICKUP' }]"
          />
          <q-input v-model="editing.providerName" outlined dense label="物流商 / 名稱 *" hint="例如：黑貓宅急便、門市自取" />
          <div class="row q-col-gutter-md">
            <div class="col-6">
              <q-input v-model.number="editing.baseShippingFee" outlined dense type="number" prefix="NT$" label="運費 *" />
            </div>
            <div class="col-6">
              <q-input v-model.number="editing.freeShippingThreshold" outlined dense type="number" prefix="NT$" label="免運門檻" hint="空白或 0 表示沒有免運" clearable />
            </div>
          </div>
          <div class="row q-col-gutter-md">
            <div class="col-6">
              <q-input v-model.number="editing.estimatedDeliveryDays" outlined dense type="number" label="預計到貨天數" />
            </div>
            <div class="col-6">
              <q-input v-model.number="editing.sortOrder" outlined dense type="number" label="排序（小的優先）" />
            </div>
          </div>
          <q-toggle v-model="editing.enabled" label="啟用" />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="取消" v-close-popup />
          <q-btn color="primary" unelevated label="儲存" :loading="saving" @click="save" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useQuasar, type QTableColumn } from 'quasar'
import { shippingConfigApi, systemStatusApi, type ShippingConfig, type SystemCheck } from '@/api/shippingConfig'

const $q = useQuasar()
const tab = ref<'shipping' | 'status'>('shipping')
const configs = ref<ShippingConfig[]>([])
const loading = ref(false)
const showEditor = ref(false)
const saving = ref(false)
const checks = ref<SystemCheck[]>([])
const statusInfo = ref<{ unpaidTimeoutHours: number; ecpayExpireDays: number } | null>(null)

const emptyConfig = (): ShippingConfig => ({
  providerName: '',
  enabled: true,
  shippingMethod: 'HOME_DELIVERY',
  baseShippingFee: 100,
  freeShippingThreshold: 1000,
  estimatedDeliveryDays: 3,
  sortOrder: 1,
  testMode: false
})
const editing = ref<ShippingConfig>(emptyConfig())

const methodLabel = (method: string) => (method === 'STORE_PICKUP' ? '門市自取' : '宅配到府')
const money = (value?: number | null) => (value == null || Number(value) === 0 ? '—' : `NT$ ${Number(value).toLocaleString()}`)

const columns: QTableColumn<ShippingConfig>[] = [
  { name: 'shippingMethod', label: '配送方式', field: 'shippingMethod', align: 'left', format: methodLabel },
  { name: 'providerName', label: '名稱', field: 'providerName', align: 'left' },
  { name: 'baseShippingFee', label: '運費', field: 'baseShippingFee', align: 'right', format: (value: number) => `NT$ ${Number(value || 0).toLocaleString()}` },
  { name: 'freeShippingThreshold', label: '免運門檻', field: 'freeShippingThreshold', align: 'right', format: money },
  { name: 'sortOrder', label: '排序', field: 'sortOrder', align: 'center' },
  { name: 'enabled', label: '啟用', field: 'enabled', align: 'center' },
  { name: 'actions', label: '操作', field: 'id', align: 'center' }
]

const loadConfigs = async () => {
  loading.value = true
  try {
    const response = await shippingConfigApi.list()
    configs.value = response.data?.content ?? []
  } catch {
    configs.value = []
  } finally {
    loading.value = false
  }
}

const loadStatus = async () => {
  try {
    const response = await systemStatusApi.get()
    checks.value = response.data?.checks ?? []
    statusInfo.value = response.data ? { unpaidTimeoutHours: response.data.unpaidTimeoutHours, ecpayExpireDays: response.data.ecpayExpireDays } : null
  } catch {
    checks.value = []
  }
}

const openEditor = (config?: ShippingConfig) => {
  editing.value = config ? { ...config } : emptyConfig()
  showEditor.value = true
}

const payloadOf = (config: ShippingConfig): ShippingConfig => ({
  ...config,
  shippingMethodName: methodLabel(config.shippingMethod),
  freeShippingThreshold: config.freeShippingThreshold ? Number(config.freeShippingThreshold) : null,
  testMode: config.testMode ?? false
})

const save = async () => {
  if (!editing.value.providerName?.trim() || editing.value.baseShippingFee == null || Number(editing.value.baseShippingFee) < 0) {
    $q.notify({ type: 'warning', message: '請填寫名稱與運費' })
    return
  }
  saving.value = true
  try {
    const payload = payloadOf(editing.value)
    if (editing.value.id) {
      await shippingConfigApi.update(editing.value.id, payload)
    } else {
      await shippingConfigApi.create(payload)
    }
    $q.notify({ type: 'positive', message: '運費設定已儲存' })
    showEditor.value = false
    loadConfigs()
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    saving.value = false
  }
}

const toggle = async (config: ShippingConfig, enabled: boolean) => {
  if (!config.id) return
  try {
    await shippingConfigApi.update(config.id, payloadOf({ ...config, enabled }))
    loadConfigs()
  } catch {
    // 錯誤訊息由系統通知顯示
  }
}

const remove = (config: ShippingConfig) => {
  if (!config.id) return
  $q.dialog({ title: '刪除運費設定', message: `確定刪除「${config.providerName}」？`, cancel: true }).onOk(async () => {
    try {
      await shippingConfigApi.remove(config.id!)
      loadConfigs()
    } catch {
      // 錯誤訊息由系統通知顯示
    }
  })
}

watch(tab, (value) => {
  if (value === 'status') loadStatus()
})

onMounted(loadConfigs)
</script>

<style scoped>
.settings-page {
  max-width: 1000px;
  margin: 0 auto;
}
</style>
