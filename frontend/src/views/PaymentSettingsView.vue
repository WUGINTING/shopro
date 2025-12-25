<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4 q-mb-md">支付參數設定</div>

      <q-card>
        <q-card-section>
          <div class="text-subtitle1 q-mb-md">支付閘道設定</div>
          
          <q-list bordered separator>
            <q-item v-for="setting in settings" :key="setting.id">
              <q-item-section avatar>
                <q-avatar :color="getGatewayColor(setting.gateway)" text-color="white">
                  {{ getGatewayIcon(setting.gateway) }}
                </q-avatar>
              </q-item-section>

              <q-item-section>
                <q-item-label class="text-h6">{{ setting.displayName }}</q-item-label>
                <q-item-label caption>{{ setting.description }}</q-item-label>
              </q-item-section>

              <q-item-section side>
                <div class="row q-gutter-sm">
                  <q-toggle
                    v-model="setting.enabled"
                    color="positive"
                    label="啟用"
                    @update:model-value="updateSetting(setting)"
                  />
                  <q-toggle
                    v-model="setting.maintenanceMode"
                    color="warning"
                    label="維護"
                    @update:model-value="updateSetting(setting)"
                  />
                  <q-btn
                    flat
                    round
                    dense
                    color="primary"
                    icon="edit"
                    @click="editSetting(setting)"
                  >
                    <q-tooltip>編輯設定</q-tooltip>
                  </q-btn>
                </div>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- 編輯對話框 -->
      <q-dialog v-model="showEditDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section>
            <div class="text-h6">編輯支付設定</div>
          </q-card-section>

          <q-card-section v-if="editingSetting" class="q-pt-none">
            <q-input
              v-model="editingSetting.displayName"
              label="顯示名稱"
              outlined
              dense
              class="q-mb-md"
            />

            <q-input
              v-model="editingSetting.description"
              label="說明文字"
              outlined
              dense
              type="textarea"
              rows="3"
              class="q-mb-md"
            />

            <q-input
              v-model.number="editingSetting.commissionRate"
              label="抽成比率 (%)"
              outlined
              dense
              type="number"
              step="0.01"
              min="0"
              max="100"
              class="q-mb-md"
            />

            <q-input
              v-model.number="editingSetting.sortOrder"
              label="排序順序"
              outlined
              dense
              type="number"
              class="q-mb-md"
            />

            <q-input
              v-model="editingSetting.maintenanceMessage"
              label="維護說明"
              outlined
              dense
              type="textarea"
              rows="2"
              hint="在維護模式時顯示給用戶的訊息"
            />

            <div class="q-mt-md">
              <q-checkbox v-model="editingSetting.enabled" label="啟用此支付方式" />
              <q-checkbox v-model="editingSetting.maintenanceMode" label="維護模式" class="q-ml-md" />
            </div>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="取消" color="grey" v-close-popup />
            <q-btn flat label="儲存" color="primary" @click="saveSetting" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllPaymentSettings, updatePaymentSetting, type PaymentSetting } from '@/api/payment'
import { Notify } from 'quasar'

const settings = ref<PaymentSetting[]>([])
const showEditDialog = ref(false)
const editingSetting = ref<PaymentSetting | null>(null)

const getGatewayColor = (gateway: string): string => {
  const colors: Record<string, string> = {
    LINE_PAY: 'green',
    ECPAY: 'orange',
    MANUAL: 'grey'
  }
  return colors[gateway] || 'blue'
}

const getGatewayIcon = (gateway: string): string => {
  const icons: Record<string, string> = {
    LINE_PAY: 'L',
    ECPAY: 'E',
    MANUAL: 'M'
  }
  return icons[gateway] || '?'
}

const loadSettings = async () => {
  try {
    const response = await getAllPaymentSettings()
    if (response.success && response.data) {
      settings.value = response.data
    }
  } catch (error) {
    console.error('Failed to load settings:', error)
    Notify.create({
      type: 'negative',
      message: '載入設定失敗'
    })
  }
}

const editSetting = (setting: PaymentSetting) => {
  editingSetting.value = { ...setting }
  showEditDialog.value = true
}

const updateSetting = async (setting: PaymentSetting) => {
  try {
    const response = await updatePaymentSetting(setting)
    if (response.success) {
      Notify.create({
        type: 'positive',
        message: '設定已更新'
      })
      loadSettings()
    }
  } catch (error) {
    console.error('Failed to update setting:', error)
    Notify.create({
      type: 'negative',
      message: '更新設定失敗'
    })
  }
}

const saveSetting = async () => {
  if (!editingSetting.value) return

  try {
    const response = await updatePaymentSetting(editingSetting.value)
    if (response.success) {
      Notify.create({
        type: 'positive',
        message: '設定已儲存'
      })
      showEditDialog.value = false
      loadSettings()
    }
  } catch (error) {
    console.error('Failed to save setting:', error)
    Notify.create({
      type: 'negative',
      message: '儲存設定失敗'
    })
  }
}

onMounted(() => {
  loadSettings()
})
</script>
