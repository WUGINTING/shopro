<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none">EDM 電子報管理</h4>
      </div>
      <div class="col-auto">
        <q-btn
          color="primary"
          label="新增活動"
          icon="add"
          @click="showCreateDialog"
        />
      </div>
    </div>

    <!-- 統計卡片 -->
    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-primary q-mb-xs">
              總活動數
            </div>
            <div class="text-h4">{{ statistics.totalCampaigns || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-info q-mb-xs">
              已發送
            </div>
            <div class="text-h4">{{ statistics.totalSent || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-success q-mb-xs">
              已開啟
            </div>
            <div class="text-h4">{{ statistics.totalOpened || 0 }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-warning q-mb-xs">
              開啟率
            </div>
            <div class="text-h4">
              {{ (statistics.averageOpenRate || 0).toFixed(1) }}%
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- 狀態篩選 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-col-gutter-md items-center">
          <div class="col">
            <q-btn-toggle
              v-model="selectedStatus"
              :options="statusOptions"
              color="primary"
              outline
              @update:model-value="loadCampaigns"
            />
          </div>
          <div class="col-auto">
            <q-btn
              color="primary"
              label="重新整理"
              icon="refresh"
              @click="loadCampaigns"
            />
          </div>
        </div>
      </q-card-section>
    </q-card>

    <!-- 活動列表 -->
    <q-card>
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        :rows="campaigns"
        :columns="columns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
        flat
        bordered
      >
        <template #body-cell-status="props">
          <q-td :props="props">
            <q-badge
              :label="props.row.status"
              :color="getStatusColor(props.row.status)"
            />
          </q-td>
        </template>

        <template #body-cell-openRate="props">
          <q-td :props="props">
            <div class="row items-center q-gutter-md">
              <div class="col-auto">
                <span v-if="props.row.sentCount && props.row.sentCount > 0">
                  {{ ((props.row.openCount || 0) / props.row.sentCount * 100).toFixed(1) }}%
                </span>
                <span v-else>-</span>
              </div>
              <div class="col-auto">
                <q-linear-progress
                  :value="props.row.sentCount ? (props.row.openCount || 0) / props.row.sentCount : 0"
                  size="10px"
                  color="positive"
                  style="min-width: 100px"
                />
              </div>
            </div>
          </q-td>
        </template>

        <template #body-cell-sendTime="props">
          <q-td :props="props">
            {{ props.row.sendTime ? formatDate(props.row.sendTime) : '-' }}
          </q-td>
        </template>

        <template #body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="edit"
              color="primary"
              @click="showEditDialog(props.row)"
              :disable="props.row.status !== 'DRAFT'"
              title="編輯"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="send"
              color="positive"
              @click="sendCampaign(props.row.id!)"
              :disable="props.row.status !== 'DRAFT'"
              title="發送"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="schedule"
              color="info"
              @click="showScheduleDialog(props.row)"
              :disable="props.row.status !== 'DRAFT'"
              title="排程發送"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="analytics"
              color="info"
              @click="showAnalytics(props.row.id!)"
              :disable="props.row.status !== 'SENT'"
              title="查看數據"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="delete"
              color="negative"
              @click="confirmDelete(props.row.id!)"
              :disable="props.row.status === 'SENT'"
              title="刪除"
            />
          </q-td>
        </template>

        <template #no-data>
          <div class="text-center q-py-lg text-grey-7">
            沒有 EDM 活動
          </div>
        </template>
      </q-table>
    </q-card>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog" full-width max-width="800px">
      <q-card>
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
          <q-form ref="campaignForm" @submit="saveCampaign">
            <q-input
              v-model="editingCampaign.name"
              label="活動名稱 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入活動名稱']"
            />

            <q-input
              v-model="editingCampaign.subject"
              label="郵件主旨 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入郵件主旨']"
            />

            <q-input
              v-model="editingCampaign.content"
              label="郵件內容 *"
              outlined
              dense
              type="textarea"
              class="q-mb-md"
              :rules="[val => !!val || '請輸入郵件內容']"
            />

            <q-select
              v-model.number="editingCampaign.targetGroupId"
              label="目標群組"
              outlined
              dense
              :options="groupOptions"
              option-value="id"
              option-label="name"
              class="q-mb-md"
            />

            <div class="row q-gutter-md">
              <q-btn
                label="取消"
                flat
                @click="showDialog = false"
              />
              <q-btn
                label="保存"
                color="primary"
                type="submit"
                :loading="saving"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 排程發送對話框 -->
    <q-dialog v-model="showScheduleDialogFlag">
      <q-card style="width: 400px; max-width: 90vw">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">排程發送</div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showScheduleDialogFlag = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section>
          <q-input
            v-model="scheduleTime"
            label="發送時間"
            outlined
            dense
            type="datetime-local"
            class="q-mb-md"
          />

          <div class="row q-gutter-md">
            <q-btn
              label="取消"
              flat
              @click="showScheduleDialogFlag = false"
            />
            <q-btn
              label="排程"
              color="primary"
              @click="scheduleForSend"
              :loading="scheduling"
            />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 刪除確認 -->
    <q-dialog v-model="showDeleteDialog">
      <q-card>
        <q-card-section class="row items-center">
          <q-icon
            name="warning"
            size="md"
            color="negative"
          />
          <span class="q-ml-md">確定要刪除此活動嗎？</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            label="取消"
            flat
            @click="showDeleteDialog = false"
          />
          <q-btn
            label="刪除"
            color="negative"
            @click="deleteCampaign"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { edmApi, type EdmCampaign } from '@/api/edm'

const $q = useQuasar()
const campaignForm = ref()

// 資料
const campaigns = ref<EdmCampaign[]>([])
const pagination = ref({
  page: 0,
  rowsPerPage: 20,
  rowsNumber: 0
})
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const scheduling = ref(false)

// 統計資料
const statistics = ref({
  totalCampaigns: 0,
  totalSent: 0,
  totalOpened: 0,
  totalClicked: 0,
  averageOpenRate: 0,
  averageClickRate: 0
})

// 狀態選項
const statusOptions = [
  { label: '全部', value: 'ALL' },
  { label: '草稿', value: 'DRAFT' },
  { label: '已排程', value: 'SCHEDULED' },
  { label: '已發送', value: 'SENT' }
]
const selectedStatus = ref('ALL')

// 群組選項（模擬資料）
const groupOptions = [
  { id: 1, name: '全部會員' },
  { id: 2, name: 'VIP 會員' },
  { id: 3, name: '新客戶' }
]

// 對話框
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const showScheduleDialogFlag = ref(false)
const deleteId = ref<number>()
const scheduleId = ref<number>()
const scheduleTime = ref('')

// 編輯活動
const editingCampaign = ref<Partial<EdmCampaign>>({
  name: '',
  subject: '',
  content: '',
  status: 'DRAFT'
})

// 表格列定義
const columns = [
  { name: 'name', label: '活動名稱', field: 'name', align: 'left' },
  { name: 'subject', label: '郵件主旨', field: 'subject', align: 'left' },
  { name: 'status', label: '狀態', field: 'status', align: 'center' },
  { name: 'sentCount', label: '已發送', field: 'sentCount', align: 'right' },
  { name: 'openRate', label: '開啟率', field: 'openRate', align: 'center' },
  { name: 'sendTime', label: '發送時間', field: 'sendTime', align: 'center' },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' }
]

// 獲取狀態顏色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    DRAFT: 'orange',
    SCHEDULED: 'blue',
    SENT: 'green',
    FAILED: 'red'
  }
  return colors[status] || 'grey'
}

// 格式化日期
const formatDate = (date?: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 載入活動列表
const loadCampaigns = async () => {
  loading.value = true
  try {
    let result
    if (selectedStatus.value === 'ALL') {
      result = await edmApi.getCampaigns(pagination.value.page, pagination.value.rowsPerPage)
    } else {
      result = await edmApi.getCampaignsByStatus(selectedStatus.value, pagination.value.page, pagination.value.rowsPerPage)
    }
    campaigns.value = result.content
    pagination.value.rowsNumber = result.totalElements
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入活動失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingCampaign.value = {
    name: '',
    subject: '',
    content: '',
    status: 'DRAFT'
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (campaign: EdmCampaign) => {
  editingCampaign.value = { ...campaign }
  showDialog.value = true
}

// 保存活動
const saveCampaign = async () => {
  saving.value = true
  try {
    if (editingCampaign.value.id) {
      await edmApi.updateCampaign(editingCampaign.value.id, editingCampaign.value)
      $q.notify({
        type: 'positive',
        message: '活動已更新',
        position: 'top'
      })
    } else {
      await edmApi.createCampaign(editingCampaign.value as EdmCampaign)
      $q.notify({
        type: 'positive',
        message: '活動已建立',
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
  } finally {
    saving.value = false
  }
}

// 發送活動
const sendCampaign = async (id: number) => {
  $q.loading.show()
  try {
    await edmApi.sendCampaign(id)
    $q.notify({
      type: 'positive',
      message: '活動已發送',
      position: 'top'
    })
    loadCampaigns()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '發送失敗',
      position: 'top'
    })
  } finally {
    $q.loading.hide()
  }
}

// 確認刪除
const confirmDelete = (id: number) => {
  deleteId.value = id
  showDeleteDialog.value = true
}

// 刪除活動
const deleteCampaign = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await edmApi.deleteCampaign(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '活動已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadCampaigns()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '刪除失敗',
      position: 'top'
    })
  } finally {
    deleting.value = false
  }
}

// 顯示排程對話框
const showScheduleDialog = (campaign: EdmCampaign) => {
  scheduleId.value = campaign.id
  scheduleTime.value = ''
  showScheduleDialogFlag.value = true
}

// 排程發送
const scheduleForSend = async () => {
  if (!scheduleId.value || !scheduleTime.value) {
    $q.notify({
      type: 'warning',
      message: '請選擇發送時間',
      position: 'top'
    })
    return
  }

  scheduling.value = true
  try {
    await edmApi.scheduleCampaign(scheduleId.value, scheduleTime.value)
    $q.notify({
      type: 'positive',
      message: '已排程發送',
      position: 'top'
    })
    showScheduleDialogFlag.value = false
    loadCampaigns()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '排程失敗',
      position: 'top'
    })
  } finally {
    scheduling.value = false
  }
}

// 查看數據
const showAnalytics = (id: number) => {
  $q.notify({
    type: 'info',
    message: `查看活動 #${id} 的詳細數據`,
    position: 'top'
  })
}

onMounted(() => {
  loadCampaigns()
})
</script>
