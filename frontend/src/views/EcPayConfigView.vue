<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">ECPay 設定管理</div>
          <div class="text-caption text-grey-7">管理 ECPay 支付閘道設定與測試/正式環境參數。</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn round icon="help_outline" color="grey-7" @click="handleStartTour">
            <q-tooltip>ECPay 設定導覽</q-tooltip>
          </q-btn>
          <q-btn v-if="!config" color="primary" icon="add" label="建立設定" unelevated @click="handleCreate" />
        </div>
      </div>

      <q-card v-if="config">
        <q-card-section>
          <div class="text-h6 q-mb-md">設定內容</div>
          <q-form @submit="handleSubmit">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.merchantId"
                  label="商店代號 (MerchantID) *"
                  outlined
                  dense
                  :rules="[(val) => !!val || '請輸入商店代號']"
                  hint="由 ECPay 提供的商店代號"
                />
              </div>

              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.apiUrl"
                  label="API URL *"
                  outlined
                  dense
                  :rules="[(val) => !!val || '請輸入 API URL']"
                  hint="例如：https://payment-stage.ecpay.com.tw"
                />
              </div>

              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.hashKey"
                  label="HashKey *"
                  outlined
                  dense
                  :type="showHashKey ? 'text' : 'password'"
                  :rules="[(val) => !!val || '請輸入 HashKey']"
                  hint="由 ECPay 提供的 HashKey"
                >
                  <template #append>
                    <q-icon
                      :name="showHashKey ? 'visibility' : 'visibility_off'"
                      class="cursor-pointer"
                      @click="showHashKey = !showHashKey"
                    />
                  </template>
                </q-input>
              </div>

              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.hashIV"
                  label="HashIV *"
                  outlined
                  dense
                  :type="showHashIV ? 'text' : 'password'"
                  :rules="[(val) => !!val || '請輸入 HashIV']"
                  hint="由 ECPay 提供的 HashIV"
                >
                  <template #append>
                    <q-icon
                      :name="showHashIV ? 'visibility' : 'visibility_off'"
                      class="cursor-pointer"
                      @click="showHashIV = !showHashIV"
                    />
                  </template>
                </q-input>
              </div>

              <div class="col-12">
                <q-input
                  v-model="form.returnUrl"
                  label="返回網址 (ReturnURL)"
                  outlined
                  dense
                  hint="付款完成後前台導向頁面（可選填）"
                  placeholder="例如：https://your-domain.com/payment/result"
                />
              </div>

              <div class="col-12">
                <q-input
                  v-model="form.notifyUrl"
                  label="通知網址 (NotifyURL) *"
                  outlined
                  dense
                  :rules="[(val) => !!val || '請輸入通知網址']"
                  hint="ECPay 付款結果回調通知網址"
                  placeholder="例如：https://your-domain.com/api/payment-gateway/callback/ecpay"
                />
              </div>

              <div class="col-12 col-md-6">
                <q-toggle v-model="form.sandbox" label="測試模式 (Sandbox)" color="primary" hint="啟用後使用測試環境參數" />
              </div>

              <div class="col-12 col-md-6">
                <q-toggle v-model="form.enabled" label="啟用設定" color="positive" hint="關閉後前台不可使用 ECPay 付款" />
              </div>

              <div class="col-12">
                <q-input
                  v-model="form.description"
                  label="備註說明"
                  outlined
                  dense
                  type="textarea"
                  rows="3"
                  hint="可填寫環境用途、維運資訊或交接備註"
                />
              </div>
            </div>

            <q-banner v-if="form.sandbox" class="bg-info text-white q-mt-md">
              <template #avatar>
                <q-icon name="info" />
              </template>
              <div class="text-subtitle2 q-mb-sm">測試環境預設參數</div>
              <div class="text-caption">
                <div>測試 MerchantID：2000132</div>
                <div>測試 HashKey：5294y06JbISpM5x9</div>
                <div>測試 HashIV：v77hoKGq4kWxNNIS</div>
                <div>測試 API URL：https://payment-stage.ecpay.com.tw</div>
              </div>
            </q-banner>

            <q-banner v-else class="bg-warning text-white q-mt-md">
              <template #avatar>
                <q-icon name="warning" />
              </template>
              <div class="text-caption">你目前使用正式環境設定，請確認商店代號、金鑰與回調網址皆為正式值。</div>
            </q-banner>

            <q-card-actions align="right" class="q-mt-md">
              <q-btn flat label="重新載入" color="grey-7" @click="loadConfig" />
              <q-btn unelevated label="儲存設定" color="primary" type="submit" :loading="loading" />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>

      <q-card v-else>
        <q-card-section class="text-center q-pa-xl">
          <q-icon name="settings" size="64px" color="grey-5" />
          <div class="text-h6 q-mt-md text-grey-7">尚未建立 ECPay 設定</div>
          <div class="text-caption text-grey-6 q-mt-sm">請先建立 ECPay 設定，前台才能啟用 ECPay 付款流程。</div>
          <q-btn color="primary" icon="add" label="建立設定" unelevated class="q-mt-md" @click="handleCreate" />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { ecpayConfigApi, type EcPayConfig } from '@/api/ecpayConfig'
import { isEcPayConfigTourCompleted, startEcPayConfigTour } from '@/utils/ecPayConfigTour'

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
    if (error?.response?.status === 404 || error?.response?.status === 500) {
      config.value = null
    } else {
      $q.notify({
        type: 'negative',
        message: `載入 ECPay 設定失敗：${error?.response?.data?.message || error?.message || '未知錯誤'}`,
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
    description: 'ECPay 測試環境設定'
  }
  config.value = { ...form.value } as EcPayConfig
}

const handleSubmit = async () => {
  if (!form.value.merchantId || !form.value.hashKey || !form.value.hashIV || !form.value.apiUrl || !form.value.notifyUrl) {
    $q.notify({ type: 'warning', message: '請填寫所有必填欄位', position: 'top' })
    return
  }

  loading.value = true
  try {
    const response = form.value.id
      ? await ecpayConfigApi.updateConfig(form.value.id, form.value)
      : await ecpayConfigApi.createConfig(form.value)

    if (response.success) {
      $q.notify({ type: 'positive', message: form.value.id ? 'ECPay 設定已更新' : 'ECPay 設定已建立', position: 'top' })
      await loadConfig()
    } else {
      $q.notify({ type: 'negative', message: response.message || '儲存設定失敗', position: 'top' })
    }
  } catch (error: any) {
    console.error('Failed to save config:', error)
    $q.notify({
      type: 'negative',
      message: `儲存設定失敗：${error?.response?.data?.message || error?.message || '未知錯誤'}`,
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const handleStartTour = () => {
  nextTick(() => {
    startEcPayConfigTour(true)
  })
}

onMounted(() => {
  loadConfig()
  if (!isEcPayConfigTourCompleted()) {
    setTimeout(() => {
      startEcPayConfigTour()
    }, 1500)
  }
})
</script>
