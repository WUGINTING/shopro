<template>
  <q-page padding>
    <div class="profile-container">
      <!-- Header -->
      <div class="text-h4 q-mb-md">系統設置</div>

      <!-- 頁籤導航 -->
      <q-tabs v-model="activeTab" class="q-mb-md">
        <q-tab name="system" label="系統設置" icon="settings" />
        <q-tab name="email" label="郵件設置" icon="email" />
        <q-tab name="notification" label="通知設置" icon="notifications" />
        <q-tab name="security" label="安全設置" icon="security" />
      </q-tabs>

      <!-- 系統設置 -->
      <q-tab-panels v-model="activeTab" animated>
        <!-- System Settings Panel -->
        <q-tab-panel name="system">
          <q-card>
            <q-card-section>
              <q-form @submit="saveSystemSettings" class="q-gutter-md">
                <div class="row q-col-gutter-md">
                  <div class="col-12">
                    <q-input
                      v-model="systemSettings.storeName"
                      label="商店名稱 *"
                      outlined
                      :rules="[val => !!val || '請輸入商店名稱']"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="systemSettings.storeDescription"
                      label="商店描述"
                      outlined
                      type="textarea"
                      rows="3"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model="systemSettings.storeEmail"
                      label="商店郵箱 *"
                      outlined
                      type="email"
                      :rules="[
                        val => !!val || '請輸入商店郵箱',
                        val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || '郵箱格式不正確'
                      ]"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model="systemSettings.storePhone"
                      label="商店電話 *"
                      outlined
                      :rules="[val => !!val || '請輸入商店電話']"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="systemSettings.storeAddress"
                      label="商店地址"
                      outlined
                    />
                  </div>

                  <div class="col-12 col-md-4">
                    <q-select
                      v-model="systemSettings.currency"
                      label="貨幣 *"
                      :options="['TWD', 'USD', 'CNY', 'JPY']"
                      outlined
                      :rules="[val => !!val || '請選擇貨幣']"
                    />
                  </div>

                  <div class="col-12 col-md-4">
                    <q-select
                      v-model="systemSettings.timezone"
                      label="時區 *"
                      :options="['Asia/Taipei', 'Asia/Shanghai', 'Asia/Tokyo', 'UTC']"
                      outlined
                      :rules="[val => !!val || '請選擇時區']"
                    />
                  </div>

                  <div class="col-12 col-md-4">
                    <q-select
                      v-model="systemSettings.language"
                      label="語言 *"
                      :options="['zh-TW', 'zh-CN', 'en-US', 'ja-JP']"
                      outlined
                      :rules="[val => !!val || '請選擇語言']"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model.number="systemSettings.taxRate"
                      label="稅率 (%)"
                      outlined
                      type="number"
                      step="0.01"
                      min="0"
                      max="100"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model.number="systemSettings.shippingFeeRate"
                      label="運費費率 (%)"
                      outlined
                      type="number"
                      step="0.01"
                      min="0"
                      max="100"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="systemSettings.enableNotification"
                      label="啟用通知"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="systemSettings.enableEmail"
                      label="啟用郵件"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="systemSettings.enableSMS"
                      label="啟用簡訊"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="systemSettings.maintenanceMode"
                      label="維護模式"
                      color="warning"
                    />
                  </div>
                </div>

                <div class="row justify-end q-gutter-sm q-mt-md">
                  <q-btn
                    color="negative"
                    label="重置為預設值"
                    @click="resetToDefaults"
                    flat
                  />
                  <q-btn
                    type="submit"
                    color="primary"
                    label="保存設置"
                    :loading="saving"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-tab-panel>

        <!-- Email Settings Panel -->
        <q-tab-panel name="email">
          <q-card>
            <q-card-section>
              <q-form @submit="saveEmailSettings" class="q-gutter-md">
                <div class="row q-col-gutter-md">
                  <div class="col-12 col-md-6">
                    <q-input
                      v-model="emailSettings.smtpServer"
                      label="SMTP 伺服器 *"
                      outlined
                      :rules="[val => !!val || '請輸入 SMTP 伺服器']"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model.number="emailSettings.smtpPort"
                      label="SMTP 端口 *"
                      outlined
                      type="number"
                      :rules="[val => !!val || '請輸入 SMTP 端口']"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="emailSettings.smtpUsername"
                      label="SMTP 使用者名稱"
                      outlined
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="emailSettings.smtpPassword"
                      label="SMTP 密碼"
                      outlined
                      type="password"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="emailSettings.emailFrom"
                      label="寄件人地址 *"
                      outlined
                      type="email"
                      :rules="[
                        val => !!val || '請輸入寄件人地址',
                        val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || '郵箱格式不正確'
                      ]"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="emailSettings.emailReplyTo"
                      label="回覆地址"
                      outlined
                      type="email"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-toggle
                      v-model="emailSettings.enableTLS"
                      label="啟用 TLS"
                      color="positive"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-toggle
                      v-model="emailSettings.enableSSL"
                      label="啟用 SSL"
                      color="positive"
                    />
                  </div>
                </div>

                <div class="row justify-end q-gutter-sm q-mt-md">
                  <q-btn
                    color="info"
                    label="測試郵件設置"
                    @click="testEmailSettings"
                    flat
                  />
                  <q-btn
                    type="submit"
                    color="primary"
                    label="保存郵件設置"
                    :loading="saving"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-tab-panel>

        <!-- Notification Settings Panel -->
        <q-tab-panel name="notification">
          <q-card>
            <q-card-section>
              <q-form @submit="saveNotificationSettings" class="q-gutter-md">
                <div class="row q-col-gutter-md">
                  <div class="col-12">
                    <q-toggle
                      v-model="notificationSettings.orderNotification"
                      label="訂單通知"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="notificationSettings.paymentNotification"
                      label="支付通知"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="notificationSettings.userRegistrationNotification"
                      label="使用者註冊通知"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="notificationSettings.lowStockNotification"
                      label="低庫存通知"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="notificationSettings.notificationEmail"
                      label="通知郵箱"
                      outlined
                      type="email"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model="notificationSettings.notificationPhone"
                      label="通知電話"
                      outlined
                    />
                  </div>
                </div>

                <div class="row justify-end q-gutter-sm q-mt-md">
                  <q-btn
                    type="submit"
                    color="primary"
                    label="保存通知設置"
                    :loading="saving"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-tab-panel>

        <!-- Security Settings Panel -->
        <q-tab-panel name="security">
          <q-card>
            <q-card-section>
              <q-form @submit="saveSecuritySettings" class="q-gutter-md">
                <div class="row q-col-gutter-md">
                  <div class="col-12 col-md-6">
                    <q-input
                      v-model.number="securitySettings.passwordMinLength"
                      label="密碼最小長度"
                      outlined
                      type="number"
                      min="1"
                    />
                  </div>

                  <div class="col-12 col-md-6">
                    <q-input
                      v-model.number="securitySettings.passwordMaxLength"
                      label="密碼最大長度"
                      outlined
                      type="number"
                      min="1"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="securitySettings.requireUppercase"
                      label="密碼需要大寫字母"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="securitySettings.requireLowercase"
                      label="密碼需要小寫字母"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="securitySettings.requireNumbers"
                      label="密碼需要數字"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="securitySettings.requireSpecialChars"
                      label="密碼需要特殊字符"
                      color="positive"
                    />
                  </div>

                  <div class="col-12">
                    <q-input
                      v-model.number="securitySettings.sessionTimeout"
                      label="會話超時 (分鐘)"
                      outlined
                      type="number"
                      min="1"
                    />
                  </div>

                  <div class="col-12">
                    <q-toggle
                      v-model="securitySettings.twoFactorAuthEnabled"
                      label="啟用雙因素驗證"
                      color="positive"
                    />
                  </div>
                </div>

                <div class="row justify-end q-gutter-sm q-mt-md">
                  <q-btn
                    type="submit"
                    color="primary"
                    label="保存安全設置"
                    :loading="saving"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-tab-panel>
      </q-tab-panels>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import systemSettingsApi, { 
  type SystemSettings, 
  type EmailSettings, 
  type NotificationSettings, 
  type SecuritySettings 
} from '@/api/settings'

const $q = useQuasar()

const activeTab = ref('system')
const saving = ref(false)

const systemSettings = ref<Partial<SystemSettings>>({
  storeName: '',
  storeDescription: '',
  storeEmail: '',
  storePhone: '',
  storeAddress: '',
  currency: 'TWD',
  timezone: 'Asia/Taipei',
  language: 'zh-TW',
  taxRate: 0,
  shippingFeeRate: 0,
  enableNotification: true,
  enableEmail: true,
  enableSMS: false,
  maintenanceMode: false
})

const emailSettings = ref<Partial<EmailSettings>>({
  smtpServer: '',
  smtpPort: 587,
  smtpUsername: '',
  smtpPassword: '',
  emailFrom: '',
  emailReplyTo: '',
  enableTLS: true,
  enableSSL: false
})

const notificationSettings = ref<Partial<NotificationSettings>>({
  orderNotification: true,
  paymentNotification: true,
  userRegistrationNotification: false,
  lowStockNotification: true,
  notificationEmail: '',
  notificationPhone: ''
})

const securitySettings = ref<Partial<SecuritySettings>>({
  passwordMinLength: 6,
  passwordMaxLength: 32,
  requireUppercase: true,
  requireLowercase: true,
  requireNumbers: true,
  requireSpecialChars: false,
  sessionTimeout: 30,
  twoFactorAuthEnabled: false
})

const saveSystemSettings = async () => {
  saving.value = true
  try {
    await systemSettingsApi.updateSystemSettings(systemSettings.value)
    $q.notify({
      type: 'positive',
      message: '系統設置已保存',
      position: 'top'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存系統設置失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

const saveEmailSettings = async () => {
  saving.value = true
  try {
    await systemSettingsApi.updateEmailSettings(emailSettings.value)
    $q.notify({
      type: 'positive',
      message: '郵件設置已保存',
      position: 'top'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存郵件設置失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

const saveNotificationSettings = async () => {
  saving.value = true
  try {
    await systemSettingsApi.updateNotificationSettings(notificationSettings.value)
    $q.notify({
      type: 'positive',
      message: '通知設置已保存',
      position: 'top'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存通知設置失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

const saveSecuritySettings = async () => {
  saving.value = true
  try {
    await systemSettingsApi.updateSecuritySettings(securitySettings.value)
    $q.notify({
      type: 'positive',
      message: '安全設置已保存',
      position: 'top'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存安全設置失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

const resetToDefaults = () => {
  $q.dialog({
    title: '確認重置',
    message: '確定要重置為預設值嗎？此操作無法撤銷。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await systemSettingsApi.resetToDefaults()
      $q.notify({
        type: 'positive',
        message: '已重置為預設值',
        position: 'top'
      })
      // Reload settings
      loadAllSettings()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '重置失敗',
        position: 'top'
      })
    }
  })
}

const testEmailSettings = async () => {
  const testEmail = $q.dialog({
    title: '測試郵件設置',
    message: '請輸入測試郵箱地址：',
    prompt: {
      model: '',
      type: 'email',
      outlined: true
    },
    cancel: true,
    persistent: true
  }).onOk(async (data) => {
    try {
      await systemSettingsApi.testEmailSettings(data)
      $q.notify({
        type: 'positive',
        message: '測試郵件已發送',
        position: 'top'
      })
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '測試郵件發送失敗',
        position: 'top'
      })
    }
  })
}

const loadAllSettings = async () => {
  try {
    const settings = await systemSettingsApi.getAllSettings()
    // Update settings from response
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '加載設置失敗',
      position: 'top'
    })
  }
}

onMounted(() => {
  loadAllSettings()
})
</script>

