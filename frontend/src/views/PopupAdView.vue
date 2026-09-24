<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">彈跳廣告管理</div>
          <div class="text-caption text-grey-7">
            設定前台商城首頁的彈跳廣告。同時有多則進行中的廣告時，前台只會顯示第一則訪客尚未看過的廣告。
          </div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            icon="refresh"
            label="重新載入"
            :loading="loading"
            @click="loadAds"
          />
          <q-btn
            color="primary"
            icon="add_circle"
            label="新增廣告"
            unelevated
            @click="openCreateDialog"
          />
        </div>
      </div>

      <!-- Status Tabs -->
      <q-card class="q-mb-md">
        <q-tabs
          v-model="statusTab"
          dense
          class="text-grey"
          active-color="primary"
          indicator-color="primary"
          align="left"
        >
          <q-tab name="all" :label="`全部 (${ads.length})`" />
          <q-tab
            v-for="option in statusTabOptions"
            :key="option.value"
            :name="option.value"
            :label="`${option.label} (${statusCounts[option.value]})`"
          />
        </q-tabs>
      </q-card>

      <!-- Popup Ads Table -->
      <q-card>
        <q-table
          :rows="filteredAds"
          :columns="columns"
          row-key="id"
          :loading="loading"
          v-model:pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          flat
        >
          <template #body-cell-image="props">
            <q-td :props="props">
              <q-img
                v-if="props.row.imageUrl"
                :src="props.row.imageUrl"
                :alt="props.row.title"
                fit="cover"
                class="rounded-borders"
                style="width: 72px; height: 72px"
              >
                <template #error>
                  <div class="absolute-full flex flex-center bg-grey-3 text-grey-7">
                    <q-icon name="broken_image" size="24px" />
                  </div>
                </template>
              </q-img>
              <div
                v-else
                class="flex flex-center bg-grey-2 text-grey-6 rounded-borders"
                style="width: 72px; height: 72px"
              >
                <q-icon name="image_not_supported" size="24px" />
              </div>
            </q-td>
          </template>

          <template #body-cell-title="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.row.title }}</div>
              <div v-if="props.row.linkUrl" class="text-caption text-grey-7 ellipsis" style="max-width: 280px">
                <q-icon name="link" size="14px" /> {{ props.row.linkUrl }}
              </div>
            </q-td>
          </template>

          <template #body-cell-status="props">
            <q-td :props="props">
              <q-badge
                :color="STATUS_META[getAdStatus(props.row)].color"
                :label="STATUS_META[getAdStatus(props.row)].label"
              />
            </q-td>
          </template>

          <template #body-cell-period="props">
            <q-td :props="props">
              <div class="text-caption">
                <div>起：{{ props.row.startTime ? formatDateTime(props.row.startTime) : '立即開始' }}</div>
                <div>迄：{{ props.row.endTime ? formatDateTime(props.row.endTime) : '不限' }}</div>
              </div>
            </q-td>
          </template>

          <template #body-cell-enabled="props">
            <q-td :props="props">
              <q-toggle
                :model-value="!!props.row.enabled"
                :disable="togglingId === props.row.id"
                color="positive"
                @update:model-value="(val: boolean) => handleToggle(props.row, val)"
              />
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
                @click="openEditDialog(props.row)"
              >
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn
                flat
                round
                dense
                size="sm"
                icon="delete"
                color="negative"
                @click="confirmDelete(props.row)"
              >
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>

          <template #no-data>
            <div class="full-width column items-center q-py-lg text-grey-7">
              <template v-if="loadError">
                <q-icon name="cloud_off" size="48px" color="grey-5" />
                <div class="q-mt-sm">載入彈跳廣告失敗</div>
                <q-btn flat color="primary" icon="refresh" label="重新載入" class="q-mt-sm" @click="loadAds" />
              </template>
              <template v-else-if="!loading && ads.length === 0">
                <q-icon name="campaign" size="48px" color="grey-5" />
                <div class="q-mt-sm">尚未建立任何彈跳廣告</div>
                <q-btn flat color="primary" icon="add" label="新增廣告" class="q-mt-sm" @click="openCreateDialog" />
              </template>
              <template v-else-if="!loading">
                <q-icon name="filter_alt_off" size="48px" color="grey-5" />
                <div class="q-mt-sm">沒有符合此狀態的廣告</div>
              </template>
            </div>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="width: 720px; max-width: 95vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯彈跳廣告' : '新增彈跳廣告' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup :disable="saving" />
          </q-card-section>

          <q-form ref="adForm" @submit="handleSubmit">
            <q-card-section class="q-gutter-y-sm">
              <q-input
                v-model="form.title"
                label="廣告標題 *"
                outlined
                maxlength="100"
                counter
                hint="會顯示在前台廣告圖片下方"
                :rules="[(val: string) => !!(val && val.trim()) || '請輸入廣告標題']"
              />

              <div class="row q-col-gutter-md items-start">
                <div class="col-12 col-sm-8">
                  <q-input
                    v-model="form.imageUrl"
                    label="圖片網址"
                    outlined
                    clearable
                    maxlength="500"
                    hint="可輸入網址，或從相簿選擇已上傳的圖片"
                    :rules="[urlRule]"
                  />
                  <q-btn
                    flat
                    dense
                    color="primary"
                    icon="photo_library"
                    label="從相簿選擇"
                    class="q-mt-sm"
                    @click="openAlbumPicker"
                  />
                </div>
                <div class="col-12 col-sm-4">
                  <div class="text-caption text-grey-7 q-mb-xs">圖片預覽</div>
                  <q-img
                    v-if="form.imageUrl"
                    :src="form.imageUrl"
                    fit="contain"
                    class="rounded-borders bg-grey-2"
                    style="height: 140px"
                  >
                    <template #error>
                      <div class="absolute-full flex flex-center bg-grey-3 text-grey-7 text-caption column">
                        <q-icon name="broken_image" size="28px" />
                        <div>無法載入圖片</div>
                      </div>
                    </template>
                  </q-img>
                  <div
                    v-else
                    class="flex flex-center bg-grey-2 text-grey-6 rounded-borders text-caption"
                    style="height: 140px"
                  >
                    尚未設定圖片
                  </div>
                </div>
              </div>

              <q-input
                v-model="form.linkUrl"
                label="點擊連結"
                outlined
                clearable
                maxlength="500"
                hint="例如 /shop/product/123 或 https://…；留空則點擊無連結"
                :rules="[urlRule]"
              />

              <div class="row q-col-gutter-md">
                <div class="col-12 col-sm-6">
                  <q-input
                    v-model="form.startTime"
                    label="開始時間"
                    outlined
                    clearable
                    type="datetime-local"
                    stack-label
                    hint="留空表示立即開始"
                  />
                </div>
                <div class="col-12 col-sm-6">
                  <q-input
                    v-model="form.endTime"
                    label="結束時間"
                    outlined
                    clearable
                    type="datetime-local"
                    stack-label
                    hint="留空表示不限"
                    :rules="[endTimeRule]"
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md items-center">
                <div class="col-12 col-sm-6">
                  <q-select
                    v-model="form.displayFrequency"
                    label="顯示頻率"
                    outlined
                    :options="DISPLAY_FREQUENCY_OPTIONS"
                    emit-value
                    map-options
                    :hint="frequencyHint"
                  />
                </div>
                <div class="col-12 col-sm-6">
                  <q-toggle v-model="form.enabled" label="啟用此廣告" color="positive" />
                </div>
              </div>

              <q-banner dense rounded :class="[STATUS_META[formStatus].bannerClass, 'q-mt-sm']">
                <template #avatar>
                  <q-icon name="schedule" :color="STATUS_META[formStatus].color" />
                </template>
                儲存後狀態：<span class="text-weight-medium">{{ STATUS_META[formStatus].label }}</span>
                — {{ STATUS_META[formStatus].description }}
              </q-banner>
            </q-card-section>

            <q-card-actions align="right" class="q-px-md q-pb-md">
              <q-btn flat label="取消" color="grey-7" v-close-popup :disable="saving" />
              <q-btn unelevated label="儲存" color="primary" type="submit" :loading="saving" />
            </q-card-actions>
          </q-form>
        </q-card>
      </q-dialog>

      <!-- Album Image Picker -->
      <q-dialog v-model="showAlbumPicker">
        <q-card style="width: 800px; max-width: 95vw">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">從相簿選擇圖片</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-select
              v-model="selectedAlbumId"
              label="選擇相簿"
              outlined
              dense
              :options="albums"
              option-label="name"
              option-value="id"
              emit-value
              map-options
              :loading="albumsLoading"
              class="q-mb-md"
              @update:model-value="loadAlbumImages"
            >
              <template #prepend>
                <q-icon name="photo_library" />
              </template>
              <template #no-option>
                <q-item>
                  <q-item-section class="text-grey">尚無相簿，請先至「相簿管理」上傳圖片</q-item-section>
                </q-item>
              </template>
            </q-select>

            <div v-if="albumImagesLoading" class="flex flex-center q-pa-xl">
              <q-spinner color="primary" size="40px" />
            </div>
            <div v-else-if="albumImages.length > 0" class="row q-col-gutter-sm album-grid">
              <div
                v-for="image in albumImages"
                :key="image.id"
                class="col-6 col-sm-4 col-md-3"
              >
                <q-card
                  flat
                  bordered
                  class="cursor-pointer"
                  :class="{ 'bg-blue-1': image.imageUrl === form.imageUrl }"
                  @click="selectAlbumImage(image)"
                >
                  <q-img :src="image.imageUrl" :ratio="1">
                    <div v-if="image.imageUrl === form.imageUrl" class="absolute-top-right q-ma-xs bg-transparent">
                      <q-icon name="check_circle" color="primary" size="sm" />
                    </div>
                  </q-img>
                  <q-card-section v-if="image.title" class="q-pa-xs">
                    <div class="text-caption ellipsis">{{ image.title }}</div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
            <div v-else-if="selectedAlbumId" class="text-center text-grey q-pa-xl">
              <q-icon name="image" size="48px" />
              <div class="q-mt-sm">此相簿暫無圖片</div>
            </div>
            <div v-else class="text-center text-grey q-pa-xl">
              <q-icon name="photo_library" size="48px" />
              <div class="q-mt-sm">請選擇相簿</div>
            </div>
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar, type QTableColumn } from 'quasar'
import popupAdApi, {
  DISPLAY_FREQUENCY_OPTIONS,
  type DisplayFrequency,
  type PopupAd
} from '@/api/popupAd'
import { albumApi, type Album, type AlbumImage } from '@/api/album'

type AdStatus = 'active' | 'scheduled' | 'expired' | 'disabled'

/** 表單資料：時間欄位使用 datetime-local 格式（YYYY-MM-DDTHH:mm） */
interface PopupAdForm {
  id?: number
  title: string
  imageUrl: string | null
  linkUrl: string | null
  startTime: string | null
  endTime: string | null
  enabled: boolean
  displayFrequency: DisplayFrequency
}

const STATUS_META: Record<AdStatus, { label: string; color: string; bannerClass: string; description: string }> = {
  active: { label: '進行中', color: 'positive', bannerClass: 'bg-green-1', description: '目前會在前台首頁顯示' },
  scheduled: { label: '排程中', color: 'warning', bannerClass: 'bg-orange-1', description: '尚未到開始時間，屆時才會顯示' },
  expired: { label: '已結束', color: 'blue-grey', bannerClass: 'bg-blue-grey-1', description: '已超過結束時間，前台不再顯示' },
  disabled: { label: '已停用', color: 'grey', bannerClass: 'bg-grey-2', description: '前台不會顯示此廣告' }
}

const statusTabOptions: { label: string; value: AdStatus }[] = [
  { label: '進行中', value: 'active' },
  { label: '排程中', value: 'scheduled' },
  { label: '已結束', value: 'expired' },
  { label: '已停用', value: 'disabled' }
]

const $q = useQuasar()

const ads = ref<PopupAd[]>([])
const loading = ref(false)
const loadError = ref(false)
const statusTab = ref<'all' | AdStatus>('all')
const pagination = ref({ page: 1, rowsPerPage: 20 })
const togglingId = ref<number | null>(null)
// 用於計算狀態的「現在時間」，每次載入時更新
const now = ref(Date.now())

const showDialog = ref(false)
const saving = ref(false)

const emptyForm = (): PopupAdForm => ({
  title: '',
  imageUrl: '',
  linkUrl: '',
  startTime: null,
  endTime: null,
  enabled: true,
  displayFrequency: 'ONCE_PER_DAY'
})

const form = ref<PopupAdForm>(emptyForm())

// 相簿選擇
const showAlbumPicker = ref(false)
const albums = ref<Album[]>([])
const albumsLoading = ref(false)
const selectedAlbumId = ref<number | null>(null)
const albumImages = ref<AlbumImage[]>([])
const albumImagesLoading = ref(false)

// ---- 時間工具 ----

const parseDateTime = (value?: string | null) => {
  if (!value) return null
  const time = new Date(value).getTime()
  return Number.isNaN(time) ? null : time
}

const formatDateTime = (value?: string | null) => {
  const time = parseDateTime(value)
  if (time === null) return value || '—'
  return new Date(time).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/** 後端 LocalDateTime（2026-01-01T10:00:00）→ datetime-local（2026-01-01T10:00） */
const toInputDateTime = (value?: string | null) => (value ? value.slice(0, 16) : null)

/** datetime-local → 後端 LocalDateTime（不帶時區，以伺服器時區解讀） */
const toApiDateTime = (value?: string | null) => {
  if (!value) return null
  return value.length === 16 ? `${value}:00` : value
}

// ---- 狀態 ----

const computeStatus = (
  enabled: boolean | undefined | null,
  startTime: string | null | undefined,
  endTime: string | null | undefined,
  at: number
): AdStatus => {
  if (!enabled) return 'disabled'
  const start = parseDateTime(startTime)
  const end = parseDateTime(endTime)
  if (start !== null && at < start) return 'scheduled'
  if (end !== null && at > end) return 'expired'
  return 'active'
}

const getAdStatus = (ad: PopupAd) => computeStatus(ad.enabled, ad.startTime, ad.endTime, now.value)

const formStatus = computed(() =>
  computeStatus(form.value.enabled, form.value.startTime, form.value.endTime, Date.now())
)

const statusCounts = computed(() => {
  const counts: Record<AdStatus, number> = { active: 0, scheduled: 0, expired: 0, disabled: 0 }
  ads.value.forEach((ad) => {
    counts[getAdStatus(ad)] += 1
  })
  return counts
})

const filteredAds = computed(() =>
  statusTab.value === 'all' ? ads.value : ads.value.filter((ad) => getAdStatus(ad) === statusTab.value)
)

const frequencyLabel = (value?: DisplayFrequency | null) =>
  DISPLAY_FREQUENCY_OPTIONS.find((o) => o.value === value)?.label || '每天一次'

const frequencyHint = computed(() => {
  const hints: Record<DisplayFrequency, string> = {
    ONCE: '每位訪客（同一瀏覽器）只會看到一次',
    ONCE_PER_DAY: '每位訪客每天最多看到一次',
    EVERY_VISIT: '每次開啟網站（新的瀏覽器分頁工作階段）都會顯示'
  }
  return hints[form.value.displayFrequency]
})

// 排序：進行中 → 排程中 → 已結束 → 已停用
const statusOrder: Record<AdStatus, number> = { active: 0, scheduled: 1, expired: 2, disabled: 3 }

const columns: QTableColumn<PopupAd>[] = [
  { name: 'image', label: '圖片', field: 'imageUrl', align: 'left' },
  { name: 'title', label: '廣告標題', field: 'title', align: 'left', sortable: true },
  {
    name: 'status',
    label: '狀態',
    field: (row) => getAdStatus(row),
    align: 'center',
    sortable: true,
    sort: (a: AdStatus, b: AdStatus) => statusOrder[a] - statusOrder[b]
  },
  { name: 'period', label: '顯示期間', field: 'startTime', align: 'left' },
  {
    name: 'displayFrequency',
    label: '顯示頻率',
    field: 'displayFrequency',
    format: (val?: DisplayFrequency | null) => frequencyLabel(val),
    align: 'center'
  },
  { name: 'enabled', label: '啟用', field: 'enabled', align: 'center' },
  { name: 'actions', label: '操作', field: 'id', align: 'center' }
]

// ---- 驗證 ----

const urlRule = (val?: string | null) =>
  !val || /^(\/|https?:\/\/)/i.test(val.trim()) || '請輸入以 / 開頭的站內路徑，或 http(s):// 開頭的網址'

const endTimeRule = (val?: string | null) => {
  const start = parseDateTime(form.value.startTime)
  const end = parseDateTime(val)
  return start === null || end === null || end > start || '結束時間必須晚於開始時間'
}

// ---- 資料載入 ----

const loadAds = async () => {
  loading.value = true
  loadError.value = false
  try {
    const response = await popupAdApi.getAllAds()
    now.value = Date.now()
    const list = response.data || []
    ads.value = [...list].sort((a, b) => {
      const diff = statusOrder[getAdStatus(a)] - statusOrder[getAdStatus(b)]
      return diff !== 0 ? diff : (b.id || 0) - (a.id || 0)
    })
  } catch (error) {
    // global axios interceptor already notifies
    loadError.value = true
    ads.value = []
    console.error(error)
  } finally {
    loading.value = false
  }
}

// ---- 新增 / 編輯 ----

const openCreateDialog = () => {
  form.value = emptyForm()
  showDialog.value = true
}

const openEditDialog = (ad: PopupAd) => {
  form.value = {
    id: ad.id,
    title: ad.title || '',
    imageUrl: ad.imageUrl || '',
    linkUrl: ad.linkUrl || '',
    startTime: toInputDateTime(ad.startTime),
    endTime: toInputDateTime(ad.endTime),
    enabled: ad.enabled !== false,
    displayFrequency: ad.displayFrequency || 'ONCE_PER_DAY'
  }
  showDialog.value = true
}

const buildPayload = (source: PopupAdForm): PopupAd => ({
  id: source.id,
  title: source.title.trim(),
  imageUrl: source.imageUrl?.trim() || null,
  linkUrl: source.linkUrl?.trim() || null,
  startTime: toApiDateTime(source.startTime),
  endTime: toApiDateTime(source.endTime),
  // 後端更新時會整筆覆蓋，enabled 不可為空
  enabled: source.enabled,
  displayFrequency: source.displayFrequency
})

const handleSubmit = async () => {
  if (saving.value) return
  saving.value = true
  try {
    const payload = buildPayload(form.value)
    if (form.value.id) {
      await popupAdApi.updateAd(form.value.id, payload)
      $q.notify({ type: 'positive', message: '廣告已更新', position: 'top' })
    } else {
      await popupAdApi.createAd(payload)
      $q.notify({ type: 'positive', message: '廣告已新增', position: 'top' })
    }
    showDialog.value = false
    void loadAds()
  } catch (error) {
    // global axios interceptor already notifies
    console.error(error)
  } finally {
    saving.value = false
  }
}

// 後端沒有獨立的啟用切換 API，以完整資料 PUT 更新
const handleToggle = async (ad: PopupAd, enabled: boolean) => {
  if (!ad.id) return
  togglingId.value = ad.id
  try {
    const response = await popupAdApi.updateAd(ad.id, { ...ad, enabled })
    ad.enabled = response.data?.enabled ?? enabled
    $q.notify({
      type: 'positive',
      message: ad.enabled ? `「${ad.title}」已啟用` : `「${ad.title}」已停用`,
      position: 'top'
    })
  } catch (error) {
    console.error(error)
  } finally {
    togglingId.value = null
  }
}

const confirmDelete = (ad: PopupAd) => {
  if (!ad.id) return
  const id = ad.id
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除廣告「${ad.title}」嗎？此操作無法復原。`,
    cancel: { label: '取消', flat: true, color: 'grey-7' },
    ok: { label: '刪除', unelevated: true, color: 'negative' },
    persistent: true
  }).onOk(async () => {
    try {
      await popupAdApi.deleteAd(id)
      $q.notify({ type: 'positive', message: '廣告已刪除', position: 'top' })
      void loadAds()
    } catch (error) {
      console.error(error)
    }
  })
}

// ---- 相簿選擇 ----

const openAlbumPicker = async () => {
  showAlbumPicker.value = true
  if (albums.value.length > 0) return
  albumsLoading.value = true
  try {
    const response = await albumApi.getAlbums({ page: 0, size: 100 })
    albums.value = response.data?.content || []
  } catch (error) {
    console.error(error)
  } finally {
    albumsLoading.value = false
  }
}

const loadAlbumImages = async () => {
  albumImages.value = []
  if (!selectedAlbumId.value) return
  albumImagesLoading.value = true
  try {
    const response = await albumApi.getAlbumImages(selectedAlbumId.value)
    albumImages.value = response.data || []
  } catch (error) {
    console.error(error)
  } finally {
    albumImagesLoading.value = false
  }
}

const selectAlbumImage = (image: AlbumImage) => {
  form.value.imageUrl = image.imageUrl
  showAlbumPicker.value = false
}

onMounted(() => {
  void loadAds()
})
</script>

<style scoped>
.album-grid {
  max-height: 420px;
  overflow-y: auto;
}
</style>
