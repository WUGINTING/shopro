<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">ECPay 支付配置</div>
          <div class="text-caption text-grey-7">管理綠界 ECPay 支付閘道的配置資訊</div>
        </div>
        <q-btn
          v-if="!config"
          color="primary"
          icon="add"
          label="建立配置"
          unelevated
          @click="handleCreate"
        />
      </div>

      <!-- Config Form -->
      <q-card v-if="config">
        <q-card-section>
          <div class="text-h6 q-mb-md">配置資訊</div>
          <q-form @submit="handleSubmit">
            <div class="row q-col-gutter-md">
              <!-- 商店代號 -->
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.merchantId"
                  label="商店代號 (MerchantID) *"
                  outlined
                  dense
                  :rules="[val => !!val || '請輸入商店代號']"
                  hint="ECPay 提供的商店代號"
                />
              </div>

              <!-- API URL -->
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.apiUrl"
                  label="API URL *"
                  outlined
                  dense
                  :rules="[val => !!val || '請輸入 API URL']"
                  hint="測試環境: https://payment-stage.ecpay.com.tw"
                />
              </div>

              <!-- HashKey -->
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.hashKey"
                  label="HashKey *"
                  outlined
                  dense
                  :type="showHashKey ? 'text' : 'password'"
                  :rules="[val => !!val || '請輸入 HashKey']"
                  hint="ECPay 提供的 HashKey"
                >
                  <template v-slot:append>
                    <q-icon
                      :name="showHashKey ? 'visibility' : 'visibility_off'"
                      class="cursor-pointer"
                      @click="showHashKey = !showHashKey"
                    />
                  </template>
                </q-input>
              </div>

              <!-- HashIV -->
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.hashIV"
                  label="HashIV *"
                  outlined
                  dense
                  :type="showHashIV ? 'text' : 'password'"
                  :rules="[val => !!val || '請輸入 HashIV']"
                  hint="ECPay 提供的 HashIV"
                >
                  <template v-slot:append>
                    <q-icon
                      :name="showHashIV ? 'visibility' : 'visibility_off'"
                      class="cursor-pointer"
                      @click="showHashIV = !showHashIV"
                    />
                  </template>
                </q-input>
              </div>

              <!-- 返回 URL -->
              <div class="col-12">
                <q-input
                  v-model="form.returnUrl"
                  label="返回 URL (ReturnURL)"
                  outlined
                  dense
                  hint="付款完成後導向的頁面 URL（可選）"
                  placeholder="例如: https://your-domain.com/payment/result"
                />
              </div>

              <!-- 通知 URL -->
              <div class="col-12">
                <q-input
                  v-model="form.notifyUrl"
                  label="通知 URL (NotifyURL) *"
                  outlined
                  dense
                  :rules="[val => !!val || '請輸入通知 URL']"
                  hint="ECPay 回調通知的 URL"
                  placeholder="例如: https://your-domain.com/api/payment-gateway/callback/ecpay"
                />
              </div>

              <!-- 測試模式 -->
              <div class="col-12 col-md-6">
                <q-toggle
                  v-model="form.sandbox"
                  label="測試模式 (Sandbox)"
                  color="primary"
                  hint="啟用測試環境"
                />
              </div>

              <!-- 啟用狀態 -->
              <div class="col-12 col-md-6">
                <q-toggle
                  v-model="form.enabled"
                  label="啟用配置"
                  color="positive"
                  hint="啟用此配置"
                />
              </div>

              <!-- 備註 -->
              <div class="col-12">
                <q-input
                  v-model="form.description"
                  label="備註說明"
                  outlined
                  dense
                  type="textarea"
                  rows="3"
                  hint="可選：添加配置說明"
                />
              </div>
            </div>

            <!-- 提示資訊 -->
            <q-banner v-if="form.sandbox" class="bg-info text-white q-mt-md">
              <template v-slot:avatar>
                <q-icon name="info" />
              </template>
              <div class="text-subtitle2 q-mb-sm">測試環境資訊</div>
              <div class="text-caption">
                <div>測試商店代號：2000132</div>
                <div>測試 HashKey：5294y06JbISpM5x9</div>
                <div>測試 HashIV：v77hoKGq4kWxNNIS</div>
                <div>測試 API URL：https://payment-stage.ecpay.com.tw</div>
              </div>
            </q-banner>

            <q-banner v-if="!form.sandbox" class="bg-warning text-white q-mt-md">
              <template v-slot:avatar>
                <q-icon name="warning" />
              </template>
              <div class="text-caption">
                您正在使用正式環境配置，請確認所有資訊正確無誤。
              </div>
            </q-banner>

            <q-card-actions align="right" class="q-mt-md">
              <q-btn flat label="取消" color="grey-7" @click="loadConfig" />
              <q-btn unelevated label="儲存" color="primary" type="submit" :loading="loading" />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>

      <!-- Empty State -->
      <q-card v-else>
        <q-card-section class="text-center q-pa-xl">
          <q-icon name="settings" size="64px" color="grey-5" />
          <div class="text-h6 q-mt-md text-grey-7">尚未建立配置</div>
          <div class="text-caption text-grey-6 q-mt-sm">
            請點擊「建立配置」按鈕來建立 ECPay 配置
          </div>
          <q-btn
            color="primary"
            icon="add"
            label="建立配置"
            unelevated
            class="q-mt-md"
            @click="handleCreate"
          />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { ecpayConfigApi, type EcPayConfig } from '@/api/ecpayConfig'

const $q = useQuasar()

const config = ref<EcPayConfig | null>(null)
const loading = ref(false)
const showHashKey = ref(false)
const showHashIV = ref(false)

const form = ref<EcPayConfig>({
  merchantId: '',
  hashKey: '',
  hashIV: '',
  apiUrl: 'https://payment-stage.ecpay.com.tw',
  returnUrl: '',
  notifyUrl: '',
  sandbox: true,
  enabled: true,
  description: ''
})

const loadConfig = async () => {
  loading.value = true
  try {
    const response = await ecpayConfigApi.getConfig()
    if (response.success && response.data) {
      config.value = response.data
      form.value = {
        id: response.data.id,
        merchantId: response.data.merchantId || '',
        hashKey: response.data.hashKey || '',
        hashIV: response.data.hashIV || '',
        apiUrl: response.data.apiUrl || 'https://payment-stage.ecpay.com.tw',
        returnUrl: response.data.returnUrl || '',
        notifyUrl: response.data.notifyUrl || '',
        sandbox: response.data.sandbox ?? true,
        enabled: response.data.enabled ?? true,
        description: response.data.description || ''
      }
    } else {
      config.value = null
    }
  } catch (error: any) {
    console.error('Failed to load config:', error)
    if (error.response?.status === 404 || error.response?.status === 500) {
      // 配置不存在，顯示空狀態
      config.value = null
    } else {
      $q.notify({
        type: 'negative',
        message: '載入配置失敗：' + (error.response?.data?.message || error.message),
        position: 'top'
      })
    }
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  form.value = {
    merchantId: '2000132',
    hashKey: '5294y06JbISpM5x9',
    hashIV: 'v77hoKGq4kWxNNIS',
    apiUrl: 'https://payment-stage.ecpay.com.tw',
    returnUrl: '',
    notifyUrl: '',
    sandbox: true,
    enabled: true,
    description: 'ECPay 測試環境配置'
  }
  config.value = {
    id: undefined,
    ...form.value
  } as EcPayConfig
}

const handleSubmit = async () => {
  if (!form.value.merchantId || !form.value.hashKey || !form.value.hashIV || 
      !form.value.apiUrl || !form.value.notifyUrl) {
    $q.notify({
      type: 'warning',
      message: '請填寫所有必填欄位',
      position: 'top'
    })
    return
  }

  loading.value = true
  try {
    let response
    if (form.value.id) {
      // 更新
      response = await ecpayConfigApi.updateConfig(form.value.id, form.value)
    } else {
      // 創建
      response = await ecpayConfigApi.createConfig(form.value)
    }

    if (response.success) {
      $q.notify({
        type: 'positive',
        message: form.value.id ? '配置已更新' : '配置已建立',
        position: 'top'
      })
      await loadConfig()
    } else {
      $q.notify({
        type: 'negative',
        message: response.message || '儲存失敗',
        position: 'top'
      })
    }
  } catch (error: any) {
    console.error('Failed to save config:', error)
    $q.notify({
      type: 'negative',
      message: '儲存失敗：' + (error.response?.data?.message || error.message),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

