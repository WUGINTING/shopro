<template>
  <q-page class="q-pa-md">
    <div class="operation-log-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">操作日誌</div>
          <div class="text-caption text-grey-7">查看系統操作日誌和審計記錄</div>
        </div>
        <q-btn
          color="primary"
          icon="filter_list"
          label="高級篩選"
          outline
          @click="showFilterDialog = true"
        />
      </div>

      <!-- Quick Filters -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-3">
              <q-input
                v-model="searchUserId"
                label="使用者ID"
                outlined
                dense
                clearable
                type="number"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterModule"
                label="模組"
                outlined
                dense
                clearable
                :options="moduleOptions"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="filterOperationType"
                label="操作類型"
                outlined
                dense
                clearable
                :options="operationTypeOptions"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-btn label="搜尋" color="primary" @click="searchLogs" class="full-width" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Operation Logs Table -->
      <q-card>
        <q-table
          :rows="logs"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          @request="onRequest"
          flat
        >
          <template v-slot:body-cell-operationType="props">
            <q-td :props="props">
              <q-badge :color="getOperationTypeColor(props.row.operationType)">
                {{ props.row.operationType }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-module="props">
            <q-td :props="props">
              <q-chip :color="getModuleColor(props.row.module)" text-color="white" size="sm">
                {{ props.row.module }}
              </q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-success="props">
            <q-td :props="props">
              <q-icon 
                :name="props.row.success ? 'check_circle' : 'error'" 
                :color="props.row.success ? 'positive' : 'negative'"
                size="sm"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-sensitive="props">
            <q-td :props="props">
              <q-badge v-if="props.row.sensitive" color="warning">
                <q-icon name="warning" size="xs" class="q-mr-xs" />
                敏感
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-executionTime="props">
            <q-td :props="props">
              <span :class="{ 'text-warning': props.row.executionTime && props.row.executionTime > 1000 }">
                {{ props.row.executionTime }}ms
              </span>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="visibility" color="primary" size="sm" @click="handleViewDetails(props.row)">
                <q-tooltip>查看詳情</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Filter Dialog -->
      <q-dialog v-model="showFilterDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">高級篩選</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model="advancedFilter.startDate"
                label="開始時間"
                outlined
                type="datetime-local"
                class="q-mb-md"
              />

              <q-input
                v-model="advancedFilter.endDate"
                label="結束時間"
                outlined
                type="datetime-local"
                class="q-mb-md"
              />

              <q-toggle
                v-model="advancedFilter.sensitiveOnly"
                label="僅顯示敏感操作"
                color="warning"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="重置" color="grey-7" @click="resetAdvancedFilter" />
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="應用" color="primary" @click="applyAdvancedFilter" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Details Dialog -->
      <q-dialog v-model="showDetailsDialog" maximized>
        <q-card>
          <q-card-section class="row items-center q-pb-none bg-primary text-white">
            <div class="text-h6">操作日誌詳情</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" />
          </q-card-section>

          <q-card-section v-if="currentLog">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <q-list bordered separator>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>日誌ID</q-item-label>
                      <q-item-label>{{ currentLog.id }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>使用者</q-item-label>
                      <q-item-label>{{ currentLog.username }} (ID: {{ currentLog.userId }})</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>操作類型</q-item-label>
                      <q-item-label>{{ currentLog.operationType }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>模組</q-item-label>
                      <q-item-label>{{ currentLog.module }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>描述</q-item-label>
                      <q-item-label>{{ currentLog.description }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>請求方法</q-item-label>
                      <q-item-label>{{ currentLog.requestMethod }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>請求URL</q-item-label>
                      <q-item-label class="text-caption">{{ currentLog.requestUrl }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>

              <div class="col-12 col-md-6">
                <q-list bordered separator>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>IP地址</q-item-label>
                      <q-item-label>{{ currentLog.ipAddress }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>回應狀態</q-item-label>
                      <q-item-label>{{ currentLog.responseStatus }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>執行時間</q-item-label>
                      <q-item-label>{{ currentLog.executionTime }}ms</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>成功狀態</q-item-label>
                      <q-item-label>
                        <q-badge :color="currentLog.success ? 'positive' : 'negative'">
                            {{ currentLog.success ? '成功' : '失敗' }}
                        </q-badge>
                      </q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>敏感操作</q-item-label>
                      <q-item-label>
                        <q-badge v-if="currentLog.sensitive" color="warning">是</q-badge>
                        <span v-else>否</span>
                      </q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <q-item-label caption>創建時間</q-item-label>
                      <q-item-label>{{ currentLog.createdAt }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>

              <div class="col-12" v-if="currentLog.requestBody">
                <q-card flat bordered>
                  <q-card-section>
                    <div class="text-subtitle2 q-mb-sm">請求內容</div>
                    <pre class="log-content">{{ currentLog.requestBody }}</pre>
                  </q-card-section>
                </q-card>
              </div>

              <div class="col-12" v-if="currentLog.responseBody">
                <q-card flat bordered>
                  <q-card-section>
                    <div class="text-subtitle2 q-mb-sm">回應內容</div>
                    <pre class="log-content">{{ currentLog.responseBody }}</pre>
                  </q-card-section>
                </q-card>
              </div>

              <div class="col-12" v-if="currentLog.errorMessage">
                <q-card flat bordered class="bg-red-1">
                  <q-card-section>
                    <div class="text-subtitle2 q-mb-sm text-negative">错误信息</div>
                    <div class="text-negative">{{ currentLog.errorMessage }}</div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { operationLogApi, type OperationLog, type PageResponse } from '@/api'

const $q = useQuasar()

const logs = ref<OperationLog[]>([])
const loading = ref(false)
const showFilterDialog = ref(false)
const showDetailsDialog = ref(false)
const currentLog = ref<OperationLog | null>(null)

const searchUserId = ref<number>()
const filterModule = ref('')
const filterOperationType = ref('')

const advancedFilter = ref({
  startDate: '',
  endDate: '',
  sensitiveOnly: false
})

const pagination = ref({
  page: 1,
  rowsPerPage: 20,
  rowsNumber: 0
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'username', label: '使用者', align: 'left' as const, field: 'username' },
  { name: 'operationType', label: '操作類型', align: 'center' as const, field: 'operationType' },
  { name: 'module', label: '模組', align: 'center' as const, field: 'module' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'requestMethod', label: '方法', align: 'center' as const, field: 'requestMethod' },
  { name: 'success', label: '狀態', align: 'center' as const, field: 'success' },
  { name: 'sensitive', label: '敏感', align: 'center' as const, field: 'sensitive' },
  { name: 'executionTime', label: '執行時間', align: 'center' as const, field: 'executionTime' },
  { name: 'createdAt', label: '時間', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const moduleOptions = ['PRODUCT', 'ORDER', 'CRM', 'SYSTEM', 'AUTH']
const operationTypeOptions = ['CREATE', 'UPDATE', 'DELETE', 'READ', 'LOGIN', 'LOGOUT']

const loadLogs = async () => {
  loading.value = true
  try {
    const response = await operationLogApi.listLogs(pagination.value.page - 1, pagination.value.rowsPerPage)
    const data = response.data as PageResponse<OperationLog> | OperationLog[]
    
    if (Array.isArray(data)) {
      logs.value = data
      pagination.value.rowsNumber = data.length
    } else if (data && 'content' in data) {
      logs.value = data.content
      pagination.value.rowsNumber = data.totalElements
    } else {
      logs.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入操作日誌失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const searchLogs = async () => {
  loading.value = true
  try {
    let response
    
    if (searchUserId.value && filterModule.value) {
      response = await operationLogApi.listByUserAndModule(
        searchUserId.value,
        filterModule.value,
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else if (searchUserId.value) {
      response = await operationLogApi.listByUser(
        searchUserId.value,
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else if (filterModule.value) {
      response = await operationLogApi.listByModule(
        filterModule.value,
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else if (filterOperationType.value) {
      response = await operationLogApi.listByOperationType(
        filterOperationType.value,
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else {
      response = await operationLogApi.listLogs(pagination.value.page - 1, pagination.value.rowsPerPage)
    }
    
    const data = response.data as PageResponse<OperationLog> | OperationLog[]
    if (Array.isArray(data)) {
      logs.value = data
      pagination.value.rowsNumber = data.length
    } else if (data && 'content' in data) {
      logs.value = data.content
      pagination.value.rowsNumber = data.totalElements
    } else {
      logs.value = []
    }
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

const onRequest = (props: any) => {
  pagination.value.page = props.pagination.page
  pagination.value.rowsPerPage = props.pagination.rowsPerPage
  searchLogs()
}

const getOperationTypeColor = (type?: string) => {
  const colorMap: Record<string, string> = {
    CREATE: 'positive',
    UPDATE: 'primary',
    DELETE: 'negative',
    READ: 'blue',
    LOGIN: 'teal',
    LOGOUT: 'grey'
  }
  return colorMap[type || ''] || 'grey'
}

const getModuleColor = (module?: string) => {
  const colorMap: Record<string, string> = {
    PRODUCT: 'blue',
    ORDER: 'orange',
    CRM: 'teal',
    SYSTEM: 'purple',
    AUTH: 'pink'
  }
  return colorMap[module || ''] || 'grey'
}

const handleViewDetails = async (log: OperationLog) => {
  if (!log.id) return
  
  try {
    const response = await operationLogApi.getLog(log.id)
    currentLog.value = response.data
    showDetailsDialog.value = true
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入詳情失敗',
      position: 'top'
    })
  }
}

const resetAdvancedFilter = () => {
  advancedFilter.value = {
    startDate: '',
    endDate: '',
    sensitiveOnly: false
  }
}

const applyAdvancedFilter = async () => {
  loading.value = true
  try {
    let response
    
    if (advancedFilter.value.sensitiveOnly) {
      response = await operationLogApi.listSensitiveOperations(
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else if (advancedFilter.value.startDate && advancedFilter.value.endDate) {
      response = await operationLogApi.listByDateRange(
        advancedFilter.value.startDate,
        advancedFilter.value.endDate,
        pagination.value.page - 1,
        pagination.value.rowsPerPage
      )
    } else {
      response = await operationLogApi.listLogs(pagination.value.page - 1, pagination.value.rowsPerPage)
    }
    
    const data = response.data as PageResponse<OperationLog> | OperationLog[]
    if (Array.isArray(data)) {
      logs.value = data
      pagination.value.rowsNumber = data.length
    } else if (data && 'content' in data) {
      logs.value = data.content
      pagination.value.rowsNumber = data.totalElements
    } else {
      logs.value = []
    }
    
    showFilterDialog.value = false
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '篩選失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.operation-log-management {
  max-width: 1600px;
  margin: 0 auto;
}

.log-content {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
  max-height: 400px;
  overflow-y: auto;
}
</style>
