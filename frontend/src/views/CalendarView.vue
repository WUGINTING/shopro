<template>
  <q-page padding>
    <div class="page-container">
      <!-- Header -->
      <div class="row items-center justify-between q-mb-md">
        <div class="text-h4">管理人員行事曆</div>
        <div class="row q-gutter-sm">
          <q-btn
            color="primary"
            label="新增事件"
            icon="add"
            @click="openDialog()"
          />
          <q-btn
            color="info"
            label="檢查衝突"
            icon="warning"
            @click="checkAllConflicts"
          />
        </div>
      </div>

      <!-- 視圖切換 -->
      <q-card class="q-mb-md">
        <q-tabs v-model="viewMode" dense>
          <q-tab name="calendar" label="行事曆視圖" icon="calendar_month" />
          <q-tab name="list" label="列表視圖" icon="list" />
        </q-tabs>
      </q-card>

      <!-- 搜索和篩選 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-3">
              <q-input
                v-model="searchKeyword"
                label="搜尋關鍵字"
                outlined
                dense
                clearable
                @update:model-value="loadEvents"
              />
            </div>
            <div class="col-12 col-md-2">
              <q-select
                v-model="filterType"
                label="事件類型"
                :options="eventTypeOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                dense
                clearable
                @update:model-value="loadEvents"
              />
            </div>
            <div class="col-12 col-md-2">
              <q-select
                v-model="filterEntityType"
                label="實體類型"
                :options="entityTypeOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                dense
                clearable
                @update:model-value="loadEvents"
              />
            </div>
            <div class="col-12 col-md-2">
              <q-input
                v-model="filterStartDate"
                label="開始日期"
                outlined
                dense
                type="date"
                clearable
                @update:model-value="loadEvents"
              />
            </div>
            <div class="col-12 col-md-2">
              <q-input
                v-model="filterEndDate"
                label="結束日期"
                outlined
                dense
                type="date"
                clearable
                @update:model-value="loadEvents"
              />
            </div>
            <div class="col-12 col-md-1">
              <q-btn
                label="重置"
                color="grey-7"
                outline
                @click="resetFilters"
                class="full-width"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 行事曆視圖 -->
      <q-card v-if="viewMode === 'calendar'" class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <!-- 月份選擇 -->
            <div class="col-12 col-md-3">
              <q-date
                ref="calendarRef"
                v-model="selectedDate"
                :events="calendarEventDates"
                event-color="primary"
                @update:model-value="onDateSelected"
                @navigation="onCalendarNavigation"
                navigation-min-year-month="2020/01"
                navigation-max-year-month="2030/12"
                mask="YYYY-MM-DD"
                class="calendar-with-events"
              />
            </div>
            <!-- 事件列表 -->
            <div class="col-12 col-md-9">
              <div class="text-h6 q-mb-md">
                {{ selectedDate ? formatDate(selectedDate) : '選擇日期' }} 的事件
              </div>
              <div v-if="selectedDateEvents.length === 0" class="text-center text-grey q-pa-xl">
                <q-icon name="event_busy" size="64px" />
                <div class="q-mt-md">當天沒有事件</div>
              </div>
              <q-list v-else separator>
                <q-item
                  v-for="event in selectedDateEvents"
                  :key="event.id"
                  clickable
                  @click="openDialog(event)"
                >
                  <q-item-section avatar>
                    <q-icon
                      :name="getEventIcon(event.type)"
                      :color="event.color || getDefaultColor(event.type)"
                      size="24px"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ event.title }}</q-item-label>
                    <q-item-label caption>
                      {{ formatDateTime(event.startTime) }} - {{ formatDateTime(event.endTime) }}
                    </q-item-label>
                    <q-item-label caption v-if="event.description">
                      {{ event.description }}
                    </q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-badge :color="getTypeColor(event.type)" :label="getTypeLabel(event.type)" />
                  </q-item-section>
                  <q-item-section side>
                    <q-btn
                      flat
                      dense
                      round
                      icon="edit"
                      color="primary"
                      size="sm"
                      @click.stop="openDialog(event)"
                    />
                    <q-btn
                      flat
                      dense
                      round
                      icon="delete"
                      color="negative"
                      size="sm"
                      @click.stop="deleteEvent(event.id!)"
                    />
                  </q-item-section>
                </q-item>
              </q-list>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 列表視圖 -->
      <q-card v-if="viewMode === 'list'">
        <q-table
          :rows="events"
          :columns="columns"
          :loading="loading"
          row-key="id"
          flat
          bordered
          :selected="selectedEvents"
          selection="multiple"
          @selection="onSelection"
        >
          <!-- Type Badge -->
          <template #body-cell-type="props">
            <q-td :props="props">
              <q-badge :color="getTypeColor(props.row.type)" :label="getTypeLabel(props.row.type)" />
            </q-td>
          </template>

          <!-- Color -->
          <template #body-cell-color="props">
            <q-td :props="props">
              <div
                class="color-preview"
                :style="{ backgroundColor: props.row.color || getDefaultColor(props.row.type) }"
              />
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
                  color="info"
                  icon="preview"
                  size="sm"
                  @click="previewEvent(props.row)"
                  title="預覽"
                />
                <q-btn
                  flat
                  dense
                  color="negative"
                  icon="delete"
                  size="sm"
                  @click="deleteEvent(props.row.id!)"
                  title="刪除"
                />
              </div>
            </q-td>
          </template>

          <template #no-data>
            <div class="text-center q-py-lg text-grey-7">
              沒有事件資料
            </div>
          </template>
        </q-table>

        <!-- 批量操作 -->
        <q-card-actions v-if="selectedEvents.length > 0" class="bg-grey-2">
          <q-btn
            color="primary"
            label="批量更新時間"
            icon="update"
            @click="openBatchUpdateDialog"
          />
          <q-btn
            color="negative"
            label="批量刪除"
            icon="delete"
            @click="batchDelete"
          />
          <q-space />
          <div class="text-body2 text-grey-7">
            已選擇 {{ selectedEvents.length }} 個事件
          </div>
        </q-card-actions>
      </q-card>

      <!-- 編輯對話框 -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="width: 700px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">
              {{ editingEvent?.id ? '編輯事件' : '新增事件' }}
            </div>
            <q-space />
            <q-btn icon="close" flat round dense @click="showDialog = false" />
          </q-card-section>

          <q-separator />

          <q-card-section class="q-pt-none">
            <q-form @submit="saveEvent" class="q-gutter-md">
              <q-select
                v-model="editingEvent.type"
                label="事件類型 *"
                :options="eventTypeOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                :rules="[val => !!val || '請選擇事件類型']"
              />

              <q-select
                v-model="editingEvent.entityType"
                label="關聯實體類型 *"
                :options="entityTypeOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                :rules="[val => !!val || '請選擇實體類型']"
              />

              <!-- 根據實體類型動態顯示選擇器 -->
              <q-select
                v-if="editingEvent.entityType === 'PRODUCT'"
                v-model="editingEvent.entityId"
                label="選擇商品 *"
                :options="productOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                use-input
                input-debounce="300"
                @filter="filterProducts"
                :loading="loadingProducts"
                :rules="[val => !!val || '請選擇商品']"
                hint="搜尋商品名稱或SKU"
              >
                <template #no-option>
                  <q-item>
                    <q-item-section class="text-grey">
                      沒有找到商品
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>

              <q-select
                v-else-if="editingEvent.entityType === 'MARKETING_CAMPAIGN'"
                v-model="editingEvent.entityId"
                label="選擇行銷活動 *"
                :options="campaignOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                use-input
                input-debounce="300"
                @filter="filterCampaigns"
                :loading="loadingCampaigns"
                :rules="[val => !!val || '請選擇行銷活動']"
                hint="搜尋活動名稱"
              >
                <template #no-option>
                  <q-item>
                    <q-item-section class="text-grey">
                      沒有找到行銷活動
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>

              <q-select
                v-else-if="editingEvent.entityType === 'PROMOTION'"
                v-model="editingEvent.entityId"
                label="選擇促銷活動 *"
                :options="promotionOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                use-input
                input-debounce="300"
                @filter="filterPromotions"
                :loading="loadingPromotions"
                :rules="[val => !!val || '請選擇促銷活動']"
                hint="搜尋促銷活動名稱"
              >
                <template #no-option>
                  <q-item>
                    <q-item-section class="text-grey">
                      沒有找到促銷活動
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>

              <q-input
                v-else
                v-model.number="editingEvent.entityId"
                label="關聯實體ID *"
                outlined
                type="number"
                :rules="[val => !!val || '請輸入實體ID']"
                hint="請輸入對應的實體ID"
              />

              <q-input
                v-model="editingEvent.title"
                label="事件標題 *"
                outlined
                :rules="[val => !!val || '請輸入事件標題']"
              />

              <q-input
                v-model="editingEvent.description"
                label="事件描述"
                outlined
                type="textarea"
                rows="3"
              />

              <div class="row q-col-gutter-md">
                <div class="col-6">
                  <q-input
                    v-model="editingEvent.startTime"
                    label="開始時間 *"
                    outlined
                    type="datetime-local"
                    :rules="[val => !!val || '請選擇開始時間']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model="editingEvent.endTime"
                    label="結束時間 *"
                    outlined
                    type="datetime-local"
                    :rules="[val => !!val || '請選擇結束時間']"
                  />
                </div>
              </div>

              <q-input
                v-model="editingEvent.color"
                label="顏色編碼"
                outlined
                hint="例如: #3498db"
              >
                <template #append>
                  <q-icon
                    name="colorize"
                    class="cursor-pointer"
                  >
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-color v-model="editingEvent.color" />
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <div class="row justify-end q-gutter-sm">
                <q-btn flat label="取消" @click="showDialog = false" />
                <q-btn type="submit" color="primary" label="保存" />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>

      <!-- 批量更新對話框 -->
      <q-dialog v-model="showBatchUpdateDialog" persistent>
        <q-card style="width: 500px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">批量更新時間</div>
            <q-space />
            <q-btn icon="close" flat round dense @click="showBatchUpdateDialog = false" />
          </q-card-section>

          <q-separator />

          <q-card-section class="q-pt-none">
            <q-form @submit="handleBatchUpdate" class="q-gutter-md">
              <div class="text-body2 q-mb-md">
                已選擇 {{ selectedEvents.length }} 個事件
              </div>

              <q-input
                v-model="batchUpdateForm.newStartTime"
                label="新的開始時間 *"
                outlined
                type="datetime-local"
                :rules="[val => !!val || '請選擇開始時間']"
              />

              <q-input
                v-model="batchUpdateForm.newEndTime"
                label="新的結束時間 *"
                outlined
                type="datetime-local"
                :rules="[val => !!val || '請選擇結束時間']"
              />

              <q-checkbox
                v-model="batchUpdateForm.updateRelatedProducts"
                label="同時更新關聯商品"
              />

              <q-checkbox
                v-model="batchUpdateForm.updateRelatedActivities"
                label="同時更新關聯活動"
              />

              <div class="row justify-end q-gutter-sm">
                <q-btn flat label="取消" @click="showBatchUpdateDialog = false" />
                <q-btn type="submit" color="primary" label="更新" />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>

      <!-- 衝突提示對話框 -->
      <q-dialog v-model="showConflictDialog">
        <q-card style="width: 600px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">時間衝突檢測</div>
            <q-space />
            <q-btn icon="close" flat round dense @click="showConflictDialog = false" />
          </q-card-section>

          <q-separator />

          <q-card-section>
            <div v-if="conflicts.length === 0" class="text-center text-positive q-pa-md">
              <q-icon name="check_circle" size="48px" />
              <div class="q-mt-md">沒有發現時間衝突</div>
            </div>
            <q-list v-else separator>
              <q-item v-for="(conflict, index) in conflicts" :key="index">
                <q-item-section avatar>
                  <q-icon name="warning" color="negative" size="24px" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ conflict.description }}</q-item-label>
                  <q-item-label caption>
                    <div>衝突類型: {{ conflict.conflictType }}</div>
                    <div v-if="conflict.suggestion">建議: {{ conflict.suggestion }}</div>
                  </q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="關閉" color="primary" @click="showConflictDialog = false" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- 預覽對話框 -->
      <q-dialog v-model="showPreviewDialog">
        <q-card style="width: 600px; max-width: 90vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">時間預覽</div>
            <q-space />
            <q-btn icon="close" flat round dense @click="showPreviewDialog = false" />
          </q-card-section>

          <q-separator />

          <q-card-section>
            <div class="text-body2 q-mb-md">
              預覽時間: {{ formatDateTime(previewData?.previewTime || '') }}
            </div>
            <div class="text-body2 q-mb-md">
              {{ previewData?.impactDescription }}
            </div>

            <q-separator class="q-my-md" />

            <div v-if="previewData?.listedProducts && previewData.listedProducts.length > 0">
              <div class="text-subtitle2 q-mb-sm">上架商品:</div>
              <q-list separator>
                <q-item
                  v-for="product in previewData.listedProducts"
                  :key="product.productId"
                >
                  <q-item-section>
                    <q-item-label>{{ product.productName }}</q-item-label>
                    <q-item-label caption>
                      SKU: {{ product.sku }} | 庫存: {{ product.stock }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </div>

            <div v-if="previewData?.activeActivities && previewData.activeActivities.length > 0" class="q-mt-md">
              <div class="text-subtitle2 q-mb-sm">進行中的活動:</div>
              <q-list separator>
                <q-item
                  v-for="activity in previewData.activeActivities"
                  :key="activity.activityId"
                >
                  <q-item-section>
                    <q-item-label>{{ activity.activityName }}</q-item-label>
                    <q-item-label caption>
                      類型: {{ activity.activityType }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </div>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="關閉" color="primary" @click="showPreviewDialog = false" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useQuasar, Quasar } from 'quasar'
import quasarLangZhTW from 'quasar/lang/zh-TW'
import calendarApi, {
  type CalendarEvent,
  type CalendarEventType,
  type EntityType,
  type CalendarConflict,
  type CalendarPreview
} from '@/api/calendar'
import { productApi, type Product } from '@/api/product'
import marketingApi, { type MarketingCampaign } from '@/api/marketing'
import { promotionApi } from '@/api/promotion'

const $q = useQuasar()

// 設置 Quasar 語言為繁體中文
$q.lang.set(quasarLangZhTW)

// 狀態
const events = ref<CalendarEvent[]>([])
const loading = ref(false)
const viewMode = ref<'calendar' | 'list'>('calendar')
const selectedDate = ref(new Date().toISOString().split('T')[0])
const selectedEvents = ref<CalendarEvent[]>([])
const showDialog = ref(false)
const showBatchUpdateDialog = ref(false)
const showConflictDialog = ref(false)
const showPreviewDialog = ref(false)
const conflicts = ref<CalendarConflict[]>([])
const previewData = ref<CalendarPreview | null>(null)

// Calendar ref for DOM manipulation
const calendarRef = ref<any>(null)

// 篩選條件
const searchKeyword = ref('')
const filterType = ref<CalendarEventType | null>(null)
const filterEntityType = ref<EntityType | null>(null)
const filterStartDate = ref('')
const filterEndDate = ref('')

// 實體選項相關
const productOptions = ref<Array<{ label: string; value: number }>>([])
const campaignOptions = ref<Array<{ label: string; value: number }>>([])
const promotionOptions = ref<Array<{ label: string; value: number }>>([])
const allProducts = ref<Product[]>([])
const allCampaigns = ref<MarketingCampaign[]>([])
const allPromotions = ref<any[]>([])
const loadingProducts = ref(false)
const loadingCampaigns = ref(false)
const loadingPromotions = ref(false)

// 編輯表單
const editingEvent = ref<Partial<CalendarEvent>>({
  type: 'PRODUCT_LISTING',
  entityType: 'PRODUCT',
  entityId: undefined,
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  color: ''
})

// 批量更新表單
const batchUpdateForm = ref({
  newStartTime: '',
  newEndTime: '',
  updateRelatedProducts: false,
  updateRelatedActivities: false
})

// 選項 - 使用對象格式以顯示中文標籤
const eventTypeOptions: Array<{ label: string; value: CalendarEventType }> = [
  { label: '商品上下架', value: 'PRODUCT_LISTING' },
  { label: '行銷活動', value: 'MARKETING_ACTIVITY' },
  { label: '特殊活動', value: 'SPECIAL_EVENT' },
  { label: '訂單截止', value: 'ORDER_DEADLINE' }
]

const entityTypeOptions: Array<{ label: string; value: EntityType }> = [
  { label: '商品', value: 'PRODUCT' },
  { label: '行銷活動', value: 'MARKETING_CAMPAIGN' },
  { label: '促銷活動', value: 'PROMOTION' },
  { label: '訂單', value: 'ORDER' }
]

// 表格列
const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'type', label: '類型', align: 'center' as const, field: 'type' },
  { name: 'title', label: '標題', align: 'left' as const, field: 'title' },
  { name: 'entityType', label: '實體類型', align: 'center' as const, field: 'entityType' },
  { name: 'entityId', label: '實體ID', align: 'center' as const, field: 'entityId' },
  { name: 'startTime', label: '開始時間', align: 'left' as const, field: 'startTime' },
  { name: 'endTime', label: '結束時間', align: 'left' as const, field: 'endTime' },
  { name: 'color', label: '顏色', align: 'center' as const, field: 'color' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

// 計算屬性 - 存儲日期到顏色的映射
const calendarEventsMap = computed(() => {
  const eventsMap: Record<string, string> = {}

  events.value.forEach(event => {
    if (!event.startTime || !event.endTime) return
    
    const startDateStr = event.startTime.split('T')[0]
    const endDateStr = event.endTime.split('T')[0]
    if (!startDateStr || !endDateStr) return
    
    const startDate = startDateStr.replace(/-/g, '/')
    const endDate = endDateStr.replace(/-/g, '/')

    const hexColor = event.color || getDefaultColor(event.type)

    if (!eventsMap[startDate]) {
      eventsMap[startDate] = hexColor
    }

    if (endDate !== startDate && !eventsMap[endDate]) {
      eventsMap[endDate] = hexColor
    }

    if (startDate !== endDate) {
      const start = new Date(startDateStr)
      const end = new Date(endDateStr)
      if (isNaN(start.getTime()) || isNaN(end.getTime())) return
      
      const current = new Date(start)
      current.setDate(current.getDate() + 1)

      while (current < end) {
        const dateKey = current.toISOString().split('T')[0]?.replace(/-/g, '/')
        if (dateKey && !eventsMap[dateKey]) {
          eventsMap[dateKey] = hexColor
        }
        current.setDate(current.getDate() + 1)
      }
    }
  })

  return eventsMap
})

// 計算屬性 - 為 q-date 組件準備事件日期數組
const calendarEventDates = computed(() => {
  return Object.keys(calendarEventsMap.value)
})

// 應用自定義顏色到日曆事件
const applyEventColors = () => {
  nextTick(() => {
    if (!calendarRef.value?.$el) return

    const eventsMap = calendarEventsMap.value
    const calendarEl = calendarRef.value.$el as HTMLElement

    // 找到所有有事件標記的日期元素
    const eventDots = calendarEl.querySelectorAll('.q-date__event')

    eventDots.forEach((dot: Element) => {
      // 找到父級日期按鈕來獲取日期值
      const dateBtn = dot.closest('.q-date__calendar-item')
      if (!dateBtn) return

      // 嘗試從按鈕獲取日期信息
      const btnElement = dateBtn.querySelector('button')
      if (!btnElement) return

      const dayNumber = btnElement.textContent?.trim()
      if (!dayNumber) return

      // 從 selectedDate 或當前顯示的月份構建完整日期
      // Quasar 日曆的結構中，我們需要從當前視圖推斷完整日期
      const currentView = calendarEl.querySelector('.q-date__navigation')
      const monthYearText = currentView?.textContent || ''

      // 解析當前顯示的年月
      let year: number, month: number
      if (!selectedDate.value) return
      const selectedParts = selectedDate.value.split('-')
      year = parseInt(selectedParts[0] || '2026')
      month = parseInt(selectedParts[1] || '1')

      // 構建日期鍵 (YYYY/MM/DD 格式)
      const day = dayNumber.padStart(2, '0')
      const monthStr = month.toString().padStart(2, '0')
      const dateKey = `${year}/${monthStr}/${day}`

      // 獲取該日期的顏色
      const color = eventsMap[dateKey]
      if (color) {
        (dot as HTMLElement).style.backgroundColor = color
      }
    })
  })
}

// 監聽事件變化，重新應用顏色
watch(calendarEventsMap, () => {
  applyEventColors()
}, { deep: true })

// 日曆導航時重新應用顏色
const onCalendarNavigation = () => {
  applyEventColors()
}

const selectedDateEvents = computed(() => {
  // selectedDate 是 'YYYY-MM-DD' 格式（因為 v-model 綁定的是這種格式）
  const selected = selectedDate.value
  if (!selected) {
    console.log('selectedDateEvents: No date selected')
    return []
  }
  
  console.log('selectedDateEvents: Filtering for date:', selected)
  console.log('selectedDateEvents: Total events available:', events.value.length)
  
  const filtered = events.value.filter(event => {
    if (!event.startTime || !event.endTime) {
      console.log('selectedDateEvents: Event missing time:', event.id, event.title)
      return false
    }
    const eventStartDate = event.startTime.split('T')[0]
    const eventEndDate = event.endTime.split('T')[0]
    if (!eventStartDate || !eventEndDate) {
      console.log('selectedDateEvents: Event date parsing failed:', event.id, event.title)
      return false
    }
    // 檢查選中的日期是否在事件的開始和結束日期之間
    const isInRange = selected >= eventStartDate && selected <= eventEndDate
    if (isInRange) {
      console.log('selectedDateEvents: Event matched:', event.id, event.title, 'from', eventStartDate, 'to', eventEndDate)
    }
    return isInRange
  })
  
  console.log('selectedDateEvents: Filtered count:', filtered.length)
  return filtered
})

// 方法
const loadEvents = async () => {
  loading.value = true
  try {
    const params: any = {
      page: 1,
      pageSize: 1000
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterType.value) {
      params.type = filterType.value
    }
    if (filterEntityType.value) {
      params.entityType = filterEntityType.value
    }
    if (filterStartDate.value) {
      params.startTime = filterStartDate.value + 'T00:00:00'
    }
    if (filterEndDate.value) {
      params.endTime = filterEndDate.value + 'T23:59:59'
    }

    const response = await calendarApi.getEvents(params)
    // response 已經是 CalendarEventResponse，直接使用 data 字段
    console.log('Calendar API Response:', response)
    if (response) {
      // response 可能是 CalendarEventResponse 或直接是數組
      if (Array.isArray(response)) {
        events.value = response
      } else if (response && typeof response === 'object' && 'data' in response) {
        const responseData = response as any
        if (Array.isArray(responseData.data)) {
          events.value = responseData.data
        } else if (responseData.data && Array.isArray(responseData.data.data)) {
          // 如果後端返回的是嵌套結構
          events.value = responseData.data.data
        }
      } else {
        events.value = []
      }
      console.log('Loaded events:', events.value.length)
      // 應用顏色到日曆
      applyEventColors()
    } else {
      events.value = []
    }
  } catch (error: any) {
    console.error('Load events error:', error)
    $q.notify({
      type: 'negative',
      message: '載入事件失敗：' + (error.message || '未知錯誤')
    })
  } finally {
    loading.value = false
  }
}

const openDialog = async (event?: CalendarEvent) => {
  if (event) {
    editingEvent.value = {
      ...event,
      startTime: formatDateTimeLocal(event.startTime),
      endTime: formatDateTimeLocal(event.endTime)
    }
  } else {
    editingEvent.value = {
      type: 'PRODUCT_LISTING',
      entityType: 'PRODUCT',
      entityId: undefined,
      title: '',
      description: '',
      startTime: '',
      endTime: '',
      color: ''
    }
  }
  
  // 根據實體類型載入對應的實體列表
  await loadEntityOptions(editingEvent.value.entityType)
  
  showDialog.value = true
}

// 載入實體選項
const loadEntityOptions = async (entityType?: EntityType) => {
  if (!entityType) return
  
  if (entityType === 'PRODUCT') {
    await loadProducts()
  } else if (entityType === 'MARKETING_CAMPAIGN') {
    await loadCampaigns()
  } else if (entityType === 'PROMOTION') {
    await loadPromotions()
  }
}

// 載入商品列表
const loadProducts = async () => {
  if (allProducts.value.length > 0) {
    updateProductOptions()
    return
  }
  
  loadingProducts.value = true
  try {
    const response = await productApi.getProducts({ page: 0, size: 1000 })
    if (response && response.data) {
      const responseData = response.data as any
      const products = Array.isArray(responseData) 
        ? responseData 
        : (responseData.content && Array.isArray(responseData.content) 
          ? responseData.content 
          : [])
      allProducts.value = products
      updateProductOptions()
    }
  } catch (error: any) {
    console.error('載入商品失敗:', error)
  } finally {
    loadingProducts.value = false
  }
}

// 更新商品選項
const updateProductOptions = () => {
  productOptions.value = allProducts.value.map(product => ({
    label: `${product.name} (ID: ${product.id})`,
    value: product.id!
  }))
}

// 過濾商品
const filterProducts = (val: string, update: (callback: () => void) => void) => {
  if (val === '') {
    update(() => {
      updateProductOptions()
    })
    return
  }
  
  update(() => {
    const needle = val.toLowerCase()
    productOptions.value = allProducts.value
      .filter(product => 
        product.name.toLowerCase().includes(needle) ||
        product.id?.toString().includes(needle)
      )
      .map(product => ({
        label: `${product.name} (ID: ${product.id})`,
        value: product.id!
      }))
  })
}

// 載入行銷活動列表
const loadCampaigns = async () => {
  if (allCampaigns.value.length > 0) {
    updateCampaignOptions()
    return
  }
  
  loadingCampaigns.value = true
  try {
    const response = await marketingApi.getAllCampaigns(1, 1000)
    if (response && typeof response === 'object' && 'data' in response) {
      const responseData = response as any
      const campaigns = responseData.data?.data || responseData.data || []
      allCampaigns.value = Array.isArray(campaigns) ? campaigns : []
      updateCampaignOptions()
    }
  } catch (error: any) {
    console.error('載入行銷活動失敗:', error)
  } finally {
    loadingCampaigns.value = false
  }
}

// 更新行銷活動選項
const updateCampaignOptions = () => {
  campaignOptions.value = allCampaigns.value.map(campaign => ({
    label: `${campaign.name} (${campaign.type})`,
    value: campaign.id!
  }))
}

// 過濾行銷活動
const filterCampaigns = (val: string, update: (callback: () => void) => void) => {
  if (val === '') {
    update(() => {
      updateCampaignOptions()
    })
    return
  }
  
  update(() => {
    const needle = val.toLowerCase()
    campaignOptions.value = allCampaigns.value
      .filter(campaign => campaign.name.toLowerCase().includes(needle))
      .map(campaign => ({
        label: `${campaign.name} (${campaign.type})`,
        value: campaign.id!
      }))
  })
}

// 載入促銷活動列表
const loadPromotions = async () => {
  if (allPromotions.value.length > 0) {
    updatePromotionOptions()
    return
  }
  
  loadingPromotions.value = true
  try {
    const response = await promotionApi.getPromotions(0, 1000)
    if (response) {
      const responseData = response as any
      if (responseData.content && Array.isArray(responseData.content)) {
        allPromotions.value = responseData.content
      } else if (Array.isArray(responseData)) {
        allPromotions.value = responseData
      } else if (responseData.data && Array.isArray(responseData.data)) {
        allPromotions.value = responseData.data
      }
      updatePromotionOptions()
    }
  } catch (error: any) {
    console.error('載入促銷活動失敗:', error)
  } finally {
    loadingPromotions.value = false
  }
}

// 更新促銷活動選項
const updatePromotionOptions = () => {
  promotionOptions.value = allPromotions.value.map(promotion => ({
    label: `${promotion.name} (${promotion.type})`,
    value: promotion.id!
  }))
}

// 過濾促銷活動
const filterPromotions = (val: string, update: (callback: () => void) => void) => {
  if (val === '') {
    update(() => {
      updatePromotionOptions()
    })
    return
  }
  
  update(() => {
    const needle = val.toLowerCase()
    promotionOptions.value = allPromotions.value
      .filter(promotion => promotion.name.toLowerCase().includes(needle))
      .map(promotion => ({
        label: `${promotion.name} (${promotion.type})`,
        value: promotion.id!
      }))
  })
}

// 監聽實體類型變化，自動載入對應的實體列表
watch(() => editingEvent.value.entityType, async (newType) => {
  if (newType && showDialog.value) {
    editingEvent.value.entityId = undefined // 清空之前選擇的ID
    await loadEntityOptions(newType)
  }
})

const saveEvent = async () => {
  try {
    const eventData: any = {
      ...editingEvent.value,
      startTime: formatDateTimeToISO(editingEvent.value.startTime!),
      endTime: formatDateTimeToISO(editingEvent.value.endTime!)
    }

    if (editingEvent.value.id) {
      await calendarApi.updateEvent(editingEvent.value.id, eventData)
      $q.notify({
        type: 'positive',
        message: '事件已更新'
      })
    } else {
      await calendarApi.createEvent(eventData)
      $q.notify({
        type: 'positive',
        message: '事件已創建'
      })
    }

    showDialog.value = false
    await loadEvents()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '保存失敗：' + (error.message || '未知錯誤')
    })
  }
}

const deleteEvent = async (id: number) => {
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除此事件嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await calendarApi.deleteEvent(id)
      $q.notify({
        type: 'positive',
        message: '事件已刪除'
      })
      await loadEvents()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗：' + (error.message || '未知錯誤')
      })
    }
  })
}

const checkAllConflicts = async () => {
  try {
    const response = await calendarApi.checkConflicts()
    // response 已經是 CalendarConflict[]，直接使用
    if (response && Array.isArray(response)) {
      conflicts.value = response
      showConflictDialog.value = true
    } else if (response && typeof response === 'object' && 'data' in response) {
      const responseData = response as any
      if (Array.isArray(responseData.data)) {
        conflicts.value = responseData.data
        showConflictDialog.value = true
      }
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '檢查衝突失敗：' + (error.message || '未知錯誤')
    })
  }
}

const previewEvent = async (event: CalendarEvent) => {
  try {
    const previewTime = event.startTime
    const response = await calendarApi.preview(previewTime)
    // response 已經是 CalendarPreview，直接使用
    if (response) {
      previewData.value = response
      showPreviewDialog.value = true
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '預覽失敗：' + (error.message || '未知錯誤')
    })
  }
}

const onSelection = (details: any) => {
  selectedEvents.value = details.rows
}

const openBatchUpdateDialog = () => {
  if (selectedEvents.value.length === 0) {
    $q.notify({
      type: 'warning',
      message: '請先選擇要更新的事件'
    })
    return
  }
  batchUpdateForm.value = {
    newStartTime: '',
    newEndTime: '',
    updateRelatedProducts: false,
    updateRelatedActivities: false
  }
  showBatchUpdateDialog.value = true
}

const handleBatchUpdate = async () => {
  try {
    const eventIds = selectedEvents.value.map(e => e.id!).filter(id => id !== undefined)
    const request = {
      eventIds,
      newStartTime: formatDateTimeToISO(batchUpdateForm.value.newStartTime),
      newEndTime: formatDateTimeToISO(batchUpdateForm.value.newEndTime),
      updateRelatedProducts: batchUpdateForm.value.updateRelatedProducts,
      updateRelatedActivities: batchUpdateForm.value.updateRelatedActivities
    }

    await calendarApi.batchUpdate(request)
    $q.notify({
      type: 'positive',
      message: '批量更新完成'
    })
    showBatchUpdateDialog.value = false
    selectedEvents.value = []
    await loadEvents()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '批量更新失敗：' + (error.message || '未知錯誤')
    })
  }
}

const batchDelete = async () => {
  if (selectedEvents.value.length === 0) {
    return
  }

  $q.dialog({
    title: '確認批量刪除',
    message: `確定要刪除選中的 ${selectedEvents.value.length} 個事件嗎？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      const eventIds = selectedEvents.value.map(e => e.id!).filter(id => id !== undefined)
      await calendarApi.batchDelete(eventIds)
      $q.notify({
        type: 'positive',
        message: '批量刪除完成'
      })
      selectedEvents.value = []
      await loadEvents()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: '批量刪除失敗：' + (error.message || '未知錯誤')
      })
    }
  })
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = null
  filterEntityType.value = null
  filterStartDate.value = ''
  filterEndDate.value = ''
  loadEvents()
}

const onDateSelected = (date: string) => {
  // q-date 的 v-model 會自動更新 selectedDate，但格式可能是 'YYYY/MM/DD'
  // 我們需要確保格式是 'YYYY-MM-DD' 以便與事件日期比較
  if (date) {
    // 如果日期格式是 'YYYY/MM/DD'，轉換為 'YYYY-MM-DD'
    const normalizedDate = date.includes('/') ? date.replace(/\//g, '-') : date
    selectedDate.value = normalizedDate
    console.log('Date selected:', date, 'normalized to:', selectedDate.value)
    console.log('Selected date events count:', selectedDateEvents.value.length)
    console.log('Selected date events:', selectedDateEvents.value)
  }
}

// 工具函數
const getTypeLabel = (type: CalendarEventType) => {
  const labels: Record<CalendarEventType, string> = {
    PRODUCT_LISTING: '商品上下架',
    MARKETING_ACTIVITY: '行銷活動',
    SPECIAL_EVENT: '特殊活動',
    ORDER_DEADLINE: '訂單截止'
  }
  return labels[type] || type
}

const getTypeColor = (type: CalendarEventType) => {
  const colors: Record<CalendarEventType, string> = {
    PRODUCT_LISTING: 'blue',
    MARKETING_ACTIVITY: 'green',
    SPECIAL_EVENT: 'orange',
    ORDER_DEADLINE: 'purple'
  }
  return colors[type] || 'grey'
}

const getDefaultColor = (type: CalendarEventType) => {
  const colors: Record<CalendarEventType, string> = {
    PRODUCT_LISTING: '#3498db',
    MARKETING_ACTIVITY: '#2ecc71',
    SPECIAL_EVENT: '#f39c12',
    ORDER_DEADLINE: '#9b59b6'
  }
  return colors[type] || '#95a5a6'
}

// 將十六進制顏色轉換為 Quasar 顏色名稱
const convertToQuasarColor = (hexColor: string): string => {
  if (!hexColor) return 'primary'

  const hex = hexColor.toLowerCase().trim()

  // Quasar 顏色映射（常用顏色）
  const colorMap: Record<string, string> = {
    '#3498db': 'blue',
    '#2ecc71': 'green',
    '#f39c12': 'orange',
    '#9b59b6': 'purple',
    '#95a5a6': 'grey',
    '#cc3131': 'red',
    '#914646': 'negative',
    '#e74c3c': 'red',
    '#c0392b': 'negative'
  }

  // 如果顏色在映射中，返回 Quasar 顏色名稱
  if (colorMap[hex]) {
    return colorMap[hex]
  }

  // 嘗試根據顏色值判斷最接近的 Quasar 顏色
  // 紅色系
  if (hex.startsWith('#cc') || hex.startsWith('#c') || hex.startsWith('#e7') || hex.startsWith('#91')) {
    return 'red'
  }
  // 藍色系
  if (hex.startsWith('#34') || hex.startsWith('#3')) {
    return 'blue'
  }
  // 綠色系
  if (hex.startsWith('#2e') || hex.startsWith('#2')) {
    return 'green'
  }
  // 橙色系
  if (hex.startsWith('#f3') || hex.startsWith('#f')) {
    return 'orange'
  }
  // 紫色系
  if (hex.startsWith('#9b') || hex.startsWith('#9')) {
    return 'purple'
  }

  // 默認返回 primary
  return 'primary'
}

const getEventIcon = (type: CalendarEventType) => {
  const icons: Record<CalendarEventType, string> = {
    PRODUCT_LISTING: 'inventory_2',
    MARKETING_ACTIVITY: 'campaign',
    SPECIAL_EVENT: 'star',
    ORDER_DEADLINE: 'schedule'
  }
  return icons[type] || 'event'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

const formatDateTime = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDateTimeLocal = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

const formatDateTimeToISO = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  // 如果沒有時區信息，添加本地時區
  if (!dateTimeStr.includes('Z') && !dateTimeStr.includes('+') && !dateTimeStr.includes('-', 10)) {
    return new Date(dateTimeStr).toISOString()
  }
  return dateTimeStr
}

onMounted(async () => {
  // 確保選中今天的日期
  const today = new Date().toISOString().split('T')[0]
  if (today) {
    selectedDate.value = today
    console.log('Mounted: Setting selected date to today:', today)
    
    // 載入事件
    await loadEvents()
    
    // 觸發日期選擇，確保事件列表顯示今天的事件
    onDateSelected(today)
  }
})
</script>

<style scoped>
.color-preview {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

/* 確保日曆事件顏色正確顯示 */
.calendar-with-events :deep(.q-date__event) {
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

/* 確保日曆容器有正確的定位 */
.calendar-with-events :deep(.q-date__calendar-item) {
  position: relative;
}

/* 為有事件的日期添加樣式 */
.calendar-with-events :deep(.q-date__event) {
  display: block;
}
</style>

