<template>
  <q-page padding>
    <div class="marketing-management">
      <!-- Header -->
      <div class="row items-center justify-between q-mb-md">
        <div class="text-h4">營銷活動管理</div>
        <q-btn
          color="primary"
          label="新增活動"
          icon="add"
          @click="openDialog()"
        />
      </div>

      <!-- 搜索和篩選 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-3">
              <q-input
                v-model="searchName"
                label="搜尋活動名稱"
                outlined
                dense
                clearable
                @update:model-value="search"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterType"
                label="篩選類型"
                :options="campaignTypeOptions"
                outlined
                dense
                clearable
                @update:model-value="search"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterStatus"
                label="篩選狀態"
                :options="campaignStatusOptions"
                outlined
                dense
                clearable
                @update:model-value="search"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-btn
                label="搜尋"
                color="primary"
                @click="search"
                class="full-width"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 活動列表 -->
      <q-card>
        <q-table
          :rows="campaigns"
          :columns="columns"
          :loading="loading"
          row-key="id"
          flat
          bordered
        >
          <!-- Status Badge -->
          <template #body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="props.row.status" />
            </q-td>
          </template>

          <!-- Type Badge -->
          <template #body-cell-type="props">
            <q-td :props="props">
              <q-badge :color="getTypeColor(props.row.type)" :label="props.row.type" />
            </q-td>
          </template>

          <!-- Discount Badge -->
          <template #body-cell-discount="props">
            <q-td :props="props">
              <div v-if="props.row.discountAmount">
                ¥{{ props.row.discountAmount }}
              </div>
              <div v-else-if="props.row.discountRate">
                {{ props.row.discountRate }}%
              </div>
              <div v-else>-</div>
            </q-td>
          </template>

          <!-- Actions -->
          <template #body-cell-actions="props">
            <q-td :props="props">
              <div class="row q-gutter-xs">
                <q-btn
                  flat
                  dense
                  color="primary"
                  icon="edit"
                  size="sm"
                  @click="openDialog(props.row)"
                  title="編輯"
                />
                <q-btn
                  flat
                  dense
                  :color="props.row.status === 'ACTIVE' ? 'warning' : 'positive'"
                  :icon="props.row.status === 'ACTIVE' ? 'pause' : 'play_arrow'"
                  size="sm"
                  @click="toggleStatus(props.row)"
                  :title="props.row.status === 'ACTIVE' ? '暫停' : '啟用'"
                />
                <q-btn
                  flat
                  dense
                  color="negative"
                  icon="delete"
                  size="sm"
                  @click="deleteCampaign(props.row.id)"
                  title="刪除"
                />
              </div>
            </q-td>
          </template>

          <template #no-data>
            <div class="text-center q-py-lg text-grey-7">
              沒有營銷活動資料
            </div>
          </template>
        </q-table>
      </q-card>

      <!-- 編輯對話框 -->
      <q-dialog v-model="showDialog">
        <q-card style="width: 600px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">
              {{ editingCampaign?.id ? '編輯活動' : '新增活動' }}
            </div>
            <q-space />
            <q-btn
              icon="close"
              flat
              round
              dense
              @click="showDialog = false"
            />
          </q-card-section>

          <q-separator />

          <q-card-section class="q-pt-none">
            <q-form @submit="saveCampaign" class="q-gutter-md">
              <q-input
                v-model="editingCampaign.name"
                label="活動名稱 *"
                outlined
                :rules="[val => !!val || '請輸入活動名稱']"
              />

              <q-input
                v-model="editingCampaign.description"
                label="活動描述"
                outlined
                type="textarea"
                rows="3"
              />

              <q-select
                v-model="editingCampaign.type"
                label="活動類型 *"
                :options="campaignTypeOptions"
                outlined
                :rules="[val => !!val || '請選擇活動類型']"
              />

              <q-select
                v-model="editingCampaign.status"
                label="活動狀態 *"
                :options="campaignStatusOptions"
                outlined
                :rules="[val => !!val || '請選擇活動狀態']"
              />

              <div class="row q-col-gutter-md">
                <div class="col-6">
                  <q-input
                    v-model.number="editingCampaign.discountAmount"
                    label="折扣金額"
                    outlined
                    type="number"
                    step="0.01"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="editingCampaign.discountRate"
                    label="折扣百分比 (%)"
                    outlined
                    type="number"
                    min="0"
                    max="100"
                  />
                </div>
              </div>

              <q-input
                v-model.number="editingCampaign.minPurchaseAmount"
                label="最低購買金額"
                outlined
                type="number"
                step="0.01"
              />

              <div class="row q-col-gutter-md">
                <div class="col-6">
                  <q-input
                    v-model="editingCampaign.startDate"
                    label="開始日期 *"
                    outlined
                    type="date"
                    :rules="[val => !!val || '請選擇開始日期']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model="editingCampaign.endDate"
                    label="結束日期 *"
                    outlined
                    type="date"
                    :rules="[val => !!val || '請選擇結束日期']"
                  />
                </div>
              </div>

              <q-input
                v-model="editingCampaign.targetAudience"
                label="目標客群"
                outlined
              />

              <div class="row justify-end q-gutter-sm">
                <q-btn
                  flat
                  label="取消"
                  @click="showDialog = false"
                />
                <q-btn
                  type="submit"
                  color="primary"
                  label="保存"
                />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import marketingApi, { type MarketingCampaign } from '@/api/marketing'

const $q = useQuasar()

const campaigns = ref<MarketingCampaign[]>([])
const loading = ref(false)
const showDialog = ref(false)
const searchName = ref('')
const filterType = ref('')
const filterStatus = ref('')

const editingCampaign = ref<MarketingCampaign>({
  name: '',
  description: '',
  type: 'DISCOUNT',
  status: 'DRAFT',
  startDate: '',
  endDate: '',
  discountAmount: 0,
  discountRate: 0,
  minPurchaseAmount: 0,
  targetAudience: ''
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '活動名稱', align: 'left' as const, field: 'name' },
  { name: 'type', label: '類型', align: 'center' as const, field: 'type' },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'discount', label: '折扣', align: 'center' as const, field: 'discount' },
  { name: 'startDate', label: '開始日期', align: 'left' as const, field: 'startDate' },
  { name: 'endDate', label: '結束日期', align: 'left' as const, field: 'endDate' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const campaignTypeOptions = ['DISCOUNT', 'PROMOTION', 'FLASH_SALE', 'FREE_SHIPPING', 'COUPON', 'OTHER']
const campaignStatusOptions = ['DRAFT', 'ACTIVE', 'ENDED', 'SCHEDULED']

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    'ACTIVE': 'positive',
    'DRAFT': 'info',
    'SCHEDULED': 'warning',
    'ENDED': 'negative'
  }
  return colorMap[status] || 'grey'
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    'DISCOUNT': 'orange',
    'PROMOTION': 'blue',
    'FLASH_SALE': 'red',
    'FREE_SHIPPING': 'green',
    'COUPON': 'purple',
    'OTHER': 'grey'
  }
  return colorMap[type] || 'grey'
}

const loadCampaigns = async () => {
  loading.value = true
  try {
    const response = await marketingApi.getAllCampaigns()
    campaigns.value = response.data || []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '加載營銷活動失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const search = async () => {
  loading.value = true
  try {
    let response
    if (filterType.value) {
      response = await marketingApi.getCampaignsByType(filterType.value)
    } else if (filterStatus.value) {
      response = await marketingApi.getCampaignsByStatus(filterStatus.value)
    } else {
      await loadCampaigns()
      return
    }
    campaigns.value = response.data || []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '搜尋失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const openDialog = (campaign?: MarketingCampaign) => {
  if (campaign) {
    editingCampaign.value = { ...campaign }
  } else {
    editingCampaign.value = {
      name: '',
      description: '',
      type: 'DISCOUNT',
      status: 'DRAFT',
      startDate: '',
      endDate: '',
      discountAmount: 0,
      discountRate: 0,
      minPurchaseAmount: 0,
      targetAudience: ''
    }
  }
  showDialog.value = true
}

const saveCampaign = async () => {
  try {
    if (editingCampaign.value.id) {
      await marketingApi.updateCampaign(editingCampaign.value.id, editingCampaign.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await marketingApi.createCampaign(editingCampaign.value)
      $q.notify({
        type: 'positive',
        message: '新增成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadCampaigns()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存失敗',
      position: 'top'
    })
  }
}

const toggleStatus = async (campaign: MarketingCampaign) => {
  try {
    const newStatus = campaign.status === 'ACTIVE' ? 'ENDED' : 'ACTIVE'
    await marketingApi.updateCampaignStatus(campaign.id!, newStatus)
    $q.notify({
      type: 'positive',
      message: '狀態已更新',
      position: 'top'
    })
    loadCampaigns()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '更新狀態失敗',
      position: 'top'
    })
  }
}

const deleteCampaign = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這個營銷活動嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await marketingApi.deleteCampaign(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadCampaigns()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗',
        position: 'top'
      })
    }
  })
}

onMounted(() => {
  loadCampaigns()
})
</script>

<style scoped>
.marketing-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
