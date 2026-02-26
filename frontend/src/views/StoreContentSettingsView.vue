<template>
  <q-page padding>
    <div class="page-shell q-gutter-md">
      <div class="row items-center justify-between">
        <div>
          <div class="text-h5 text-weight-bold">品牌故事 / 聯絡我們內容設定</div>
          <div class="text-body2 text-grey-7 q-mt-xs">
            這裡修改的內容會同步顯示在前台「品牌故事」與「聯絡我們」頁面。
          </div>
        </div>
        <q-btn color="primary" icon="save" label="儲存" :loading="saving" @click="save" />
      </div>

      <q-card flat bordered>
        <q-card-section>
          <div class="text-h6 q-mb-md">品牌故事頁</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input v-model="form.brandStoryBadge" label="頁面標籤" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.brandStoryTitle" label="頁面標題" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.brandStoryLead" label="頁面介紹" type="textarea" rows="4" outlined />
            </div>
            <div class="col-12 col-md-4">
              <q-input v-model="form.brandMissionTitle" label="區塊一標題" outlined />
            </div>
            <div class="col-12 col-md-8">
              <q-input v-model="form.brandMissionContent" label="區塊一內容" type="textarea" rows="3" outlined />
            </div>
            <div class="col-12 col-md-4">
              <q-input v-model="form.brandVisionTitle" label="區塊二標題" outlined />
            </div>
            <div class="col-12 col-md-8">
              <q-input v-model="form.brandVisionContent" label="區塊二內容" type="textarea" rows="3" outlined />
            </div>
            <div class="col-12 col-md-4">
              <q-input v-model="form.brandValueTitle" label="區塊三標題" outlined />
            </div>
            <div class="col-12 col-md-8">
              <q-input v-model="form.brandValueContent" label="區塊三內容" type="textarea" rows="3" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.brandStoryNote" label="頁尾說明" type="textarea" rows="3" outlined />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered>
        <q-card-section>
          <div class="text-h6 q-mb-md">聯絡我們頁</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input v-model="form.contactPageBadge" label="頁面標籤" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.contactPageTitle" label="頁面標題" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.contactPageLead" label="頁面介紹" type="textarea" rows="4" outlined />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.contactEmail" label="客服 Email" type="email" outlined />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.contactPhone" label="客服電話" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.businessHours" label="營業時間" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.address" label="聯絡地址" type="textarea" rows="2" outlined />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.contactEmailHint" label="Email 提示" type="textarea" rows="2" outlined />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.contactPhoneHint" label="電話提示" type="textarea" rows="2" outlined />
            </div>
            <div class="col-12 col-md-6">
              <q-input
                v-model="form.contactBusinessHoursHint"
                label="營業時間提示"
                type="textarea"
                rows="2"
                outlined
              />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.contactAddressHint" label="地址提示" type="textarea" rows="2" outlined />
            </div>
            <div class="col-12">
              <q-input v-model="form.contactSupportNote" label="頁尾說明" type="textarea" rows="3" outlined />
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import storeContentConfigApi, {
  defaultStoreContentConfig,
  type StoreContentConfig
} from '@/api/storeContentConfig'

const $q = useQuasar()
const saving = ref(false)
const form = ref<StoreContentConfig>(defaultStoreContentConfig())

const load = async () => {
  try {
    const response = await storeContentConfigApi.getStoreContentConfig()
    if (response?.data) {
      form.value = { ...form.value, ...response.data }
    }
  } catch {
    // global axios interceptor already notifies
  }
}

const save = async () => {
  saving.value = true
  try {
    const response = await storeContentConfigApi.updateStoreContentConfig(form.value)
    if (response?.data) {
      form.value = { ...form.value, ...response.data }
    }
    $q.notify({ type: 'positive', message: '品牌故事與聯絡我們內容已更新', position: 'top' })
  } catch {
    $q.notify({ type: 'negative', message: '儲存失敗，請稍後再試', position: 'top' })
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  void load()
})
</script>

<style scoped>
.page-shell {
  max-width: 1200px;
}
</style>
