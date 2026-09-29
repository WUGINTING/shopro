<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">自訂頁面管理</div>
          <div class="text-caption text-grey-7">
            管理前台商城 /shop/page/&lt;頁面別名&gt; 顯示的說明頁面（例如退換貨說明、隱私權政策）
          </div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            icon="refresh"
            label="重新載入"
            :loading="loading"
            @click="reload"
          />
          <q-btn
            color="primary"
            icon="add_circle"
            label="新增頁面"
            unelevated
            @click="openCreateDialog()"
          />
        </div>
      </div>

      <!-- 前台內建頁面說明 -->
      <q-card flat bordered class="q-mb-md">
        <q-card-section>
          <div class="row items-center q-mb-sm">
            <q-icon name="info" color="primary" size="sm" class="q-mr-sm" />
            <div class="text-subtitle1 text-weight-medium">前台內建頁面</div>
          </div>
          <div class="text-body2 text-grey-8 q-mb-md">
            前台商城頁尾的以下四個頁面內建預設內容。只要建立並啟用相同別名的自訂頁面，前台就會改為顯示這裡的內容；
            停用或刪除該頁面後，前台會恢復顯示內建預設內容。建立時可使用「套用前台預設內容」從範本開始編輯。
          </div>
          <div class="row q-col-gutter-sm">
            <div
              v-for="item in builtInPages"
              :key="item.slug"
              class="col-12 col-sm-6 col-md-3"
            >
              <q-card flat bordered class="full-height">
                <q-card-section class="q-pa-sm">
                  <div class="row items-center no-wrap">
                    <div class="col">
                      <div class="text-weight-medium">{{ item.defaultTitle }}</div>
                      <div class="text-caption text-grey-7">/shop/page/{{ item.slug }}</div>
                    </div>
                    <q-badge
                      :color="item.page ? (item.page.enabled ? 'positive' : 'grey') : 'blue-grey-4'"
                      :label="item.page ? (item.page.enabled ? '已自訂' : '已停用') : '使用預設'"
                    />
                  </div>
                </q-card-section>
                <q-card-actions class="q-pt-none">
                  <q-btn
                    v-if="item.page"
                    flat
                    dense
                    size="sm"
                    color="primary"
                    icon="edit"
                    label="編輯"
                    @click="openEditDialog(item.page)"
                  />
                  <q-btn
                    v-else
                    flat
                    dense
                    size="sm"
                    color="primary"
                    icon="add"
                    label="以預設內容建立"
                    :disable="allPagesLoading || !!allPagesError"
                    @click="openCreateDialog(item.slug)"
                  />
                  <q-space />
                  <q-btn
                    flat
                    dense
                    round
                    size="sm"
                    color="grey-7"
                    icon="open_in_new"
                    :href="storefrontPageUrl(item.slug)"
                    target="_blank"
                    rel="noopener"
                  >
                    <q-tooltip>在前台查看</q-tooltip>
                  </q-btn>
                </q-card-actions>
              </q-card>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Custom Pages Table -->
      <q-card>
        <q-table
          :rows="pages"
          :columns="columns"
          row-key="id"
          :loading="loading"
          v-model:pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          flat
          @request="onRequest"
        >
          <template #body-cell-title="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.row.title }}</div>
              <div v-if="props.row.metaTitle" class="text-caption text-grey-7 ellipsis" style="max-width: 320px">
                SEO：{{ props.row.metaTitle }}
              </div>
            </q-td>
          </template>

          <template #body-cell-slug="props">
            <q-td :props="props">
              <code>{{ props.row.slug }}</code>
              <q-badge
                v-if="isDefaultShopPageSlug(props.row.slug)"
                color="primary"
                outline
                class="q-ml-sm"
                label="內建"
              >
                <q-tooltip>覆寫前台內建的「{{ DEFAULT_SHOP_PAGES[props.row.slug as DefaultShopPageSlug].title }}」預設內容</q-tooltip>
              </q-badge>
            </q-td>
          </template>

          <template #body-cell-enabled="props">
            <q-td :props="props">
              <q-toggle
                :model-value="!!props.row.enabled"
                :disable="togglingId === props.row.id"
                color="positive"
                @update:model-value="handleToggle(props.row)"
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
                icon="open_in_new"
                color="grey-7"
                :href="storefrontPageUrl(props.row.slug)"
                target="_blank"
                rel="noopener"
              >
                <q-tooltip>
                  {{ props.row.enabled ? '在前台預覽' : '在前台預覽（頁面停用中，前台不會顯示此內容）' }}
                </q-tooltip>
              </q-btn>
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
                <div class="q-mt-sm">載入自訂頁面失敗</div>
                <q-btn flat color="primary" icon="refresh" label="重新載入" class="q-mt-sm" @click="reload" />
              </template>
              <template v-else-if="!loading">
                <q-icon name="description" size="48px" color="grey-5" />
                <div class="q-mt-sm">尚未建立任何自訂頁面</div>
                <q-btn flat color="primary" icon="add" label="新增頁面" class="q-mt-sm" @click="openCreateDialog()" />
              </template>
            </div>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent maximized>
        <q-card class="column no-wrap">
          <q-card-section class="row items-center q-pb-sm bg-primary text-white">
            <div class="text-h6">{{ form.id ? '編輯自訂頁面' : '新增自訂頁面' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" :disable="saving" />
          </q-card-section>

          <q-card-section class="col scroll">
            <q-form ref="pageForm" @submit.prevent="handleSubmit">
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.title"
                    label="頁面標題 *"
                    outlined
                    maxlength="200"
                    counter
                    :rules="[
                      (val: string) => !!(val && val.trim()) || '請輸入頁面標題'
                    ]"
                  />
                </div>

                <div class="col-12 col-md-4">
                  <q-input
                    :model-value="form.slug"
                    label="頁面別名 (URL) *"
                    outlined
                    maxlength="100"
                    hint="只能包含小寫字母、數字和連字號，例如 about-us"
                    :rules="slugRules"
                    @update:model-value="onSlugInput"
                  >
                    <template #prepend>
                      <span class="text-caption text-grey-7">/shop/page/</span>
                    </template>
                  </q-input>
                </div>

                <div class="col-12 col-md-2">
                  <q-input
                    v-model.number="form.sortOrder"
                    label="排序"
                    outlined
                    type="number"
                    hint="數字越小越前面"
                  />
                </div>

                <div class="col-12">
                  <q-toggle
                    v-model="form.enabled"
                    label="啟用（前台可瀏覽此頁面）"
                    color="positive"
                  />
                </div>

                <!-- 內建頁面提示 -->
                <div v-if="formSlugIsBuiltIn" class="col-12">
                  <q-banner rounded class="bg-blue-1 text-grey-9">
                    <template #avatar>
                      <q-icon name="auto_awesome" color="primary" />
                    </template>
                    別名「{{ form.slug }}」是前台內建頁面（{{ formDefaultPage?.title }}），
                    {{ form.enabled ? '儲存並啟用後，前台會改為顯示此頁面內容。' : '此頁面停用時，前台會顯示內建預設內容。' }}
                    <template #action>
                      <q-btn
                        flat
                        color="primary"
                        icon="content_paste"
                        label="套用前台預設內容"
                        @click="handlePrefill"
                      />
                    </template>
                  </q-banner>
                </div>

                <div class="col-12">
                  <div class="row items-center q-mb-xs">
                    <div class="text-subtitle2">頁面內容</div>
                    <q-space />
                    <div class="text-caption text-grey-7">
                      可點選工具列最右側的 <q-icon name="code" /> 切換為 HTML 原始碼編輯
                    </div>
                  </div>
                  <q-editor
                    v-model="form.content"
                    :toolbar="editorToolbar"
                    min-height="360px"
                    max-height="60vh"
                    placeholder="輸入頁面內容…"
                    content-class="custom-page-editor"
                  />
                </div>

                <div class="col-12">
                  <q-expansion-item label="SEO 設定" icon="travel_explore" header-class="text-weight-medium">
                    <q-card flat bordered>
                      <q-card-section class="q-gutter-md">
                        <q-input
                          v-model="form.metaTitle"
                          label="SEO 標題"
                          outlined
                          maxlength="100"
                          counter
                        />
                        <q-input
                          v-model="form.metaDescription"
                          label="SEO 描述"
                          outlined
                          type="textarea"
                          rows="3"
                          maxlength="300"
                          counter
                        />
                        <q-input
                          v-model="form.metaKeywords"
                          label="SEO 關鍵字"
                          outlined
                          maxlength="200"
                          counter
                          hint="以逗號分隔"
                        />
                      </q-card-section>
                    </q-card>
                  </q-expansion-item>
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-separator />

          <q-card-actions align="right" class="q-px-md q-py-sm">
            <q-btn
              v-if="form.slug && isSlugFormatValid"
              flat
              color="grey-7"
              icon="open_in_new"
              label="在前台查看"
              :href="storefrontPageUrl(form.slug)"
              target="_blank"
              rel="noopener"
            >
              <q-tooltip>前台只會顯示已儲存且啟用的內容</q-tooltip>
            </q-btn>
            <q-space />
            <q-btn flat label="取消" color="grey-7" v-close-popup :disable="saving" />
            <q-btn unelevated label="儲存" color="primary" :loading="saving" @click="submitForm" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar, type QEditorProps, type QForm, type QTableColumn, type QTableProps } from 'quasar'
import customPageApi, { CUSTOM_PAGE_SLUG_PATTERN, type CustomPage } from '@/api/customPage'
import {
  DEFAULT_SHOP_PAGES,
  DEFAULT_SHOP_PAGE_SLUGS,
  isDefaultShopPageSlug,
  type DefaultShopPageSlug
} from '@/constants/shopPageDefaults'

const $q = useQuasar()

/** 前台商城網址（用於預覽連結） */
const STOREFRONT_URL = String(import.meta.env.VITE_STOREFRONT_URL || 'http://localhost:5174').replace(/\/+$/, '')

const storefrontPageUrl = (slug: string) => `${STOREFRONT_URL}/shop/page/${encodeURIComponent(slug)}`

// 表格資料
const pages = ref<CustomPage[]>([])
const loading = ref(false)
const loadError = ref(false)
const pagination = ref({
  page: 1,
  rowsPerPage: 20,
  rowsNumber: 0
})

// 全部頁面（用於內建頁面狀態與別名重複檢查）
const allPages = ref<CustomPage[]>([])
const allPagesLoading = ref(false)
const allPagesError = ref(false)

const togglingId = ref<number | null>(null)

// 對話框
const showDialog = ref(false)
const saving = ref(false)
const pageForm = ref<QForm | null>(null)

/** 表單資料：content / enabled 必定有值（q-editor 需要字串，後端更新時 enabled 不可為空） */
interface CustomPageForm extends CustomPage {
  content: string
  enabled: boolean
}

const emptyForm = (): CustomPageForm => ({
  title: '',
  slug: '',
  content: '',
  metaTitle: '',
  metaDescription: '',
  metaKeywords: '',
  enabled: true,
  sortOrder: 0
})

const form = ref<CustomPageForm>(emptyForm())

const formatDateTime = (value?: string) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString('zh-TW')
}

const columns: QTableColumn<CustomPage>[] = [
  { name: 'title', label: '頁面標題', field: 'title', align: 'left' },
  { name: 'slug', label: '頁面別名', field: 'slug', align: 'left' },
  { name: 'sortOrder', label: '排序', field: 'sortOrder', align: 'center' },
  { name: 'enabled', label: '啟用', field: 'enabled', align: 'center' },
  {
    name: 'updatedAt',
    label: '更新時間',
    field: (row) => row.updatedAt,
    format: (val?: string) => formatDateTime(val),
    align: 'left'
  },
  { name: 'actions', label: '操作', field: 'id', align: 'center' }
]

const editorToolbar: QEditorProps['toolbar'] = [
  [
    {
      label: '段落格式',
      icon: 'format_size',
      list: 'no-icons',
      options: ['p', 'h2', 'h3', 'h4']
    }
  ],
  ['bold', 'italic', 'underline', 'strike'],
  ['unordered', 'ordered', 'quote', 'hr'],
  ['link'],
  ['undo', 'redo'],
  ['removeFormat', 'viewsource']
]

// 內建頁面狀態
const builtInPages = computed(() =>
  DEFAULT_SHOP_PAGE_SLUGS.map((slug) => ({
    slug,
    defaultTitle: DEFAULT_SHOP_PAGES[slug].title,
    page: allPages.value.find((p) => p.slug === slug)
  }))
)

const isSlugFormatValid = computed(() => CUSTOM_PAGE_SLUG_PATTERN.test(form.value.slug || ''))

const formSlugIsBuiltIn = computed(() => isDefaultShopPageSlug(form.value.slug || ''))

const formDefaultPage = computed(() => {
  const slug = form.value.slug || ''
  return isDefaultShopPageSlug(slug) ? DEFAULT_SHOP_PAGES[slug] : null
})

const slugRules = [
  (val: string) => !!val || '請輸入頁面別名',
  (val: string) => CUSTOM_PAGE_SLUG_PATTERN.test(val) || '只能包含小寫字母、數字和連字號',
  (val: string) =>
    !allPages.value.some((p) => p.slug === val && p.id !== form.value.id) || '此頁面別名已被使用'
]

// 輸入別名時自動轉小寫、空白轉連字號
const onSlugInput = (val: string | number | null) => {
  form.value.slug = String(val ?? '').toLowerCase().replace(/\s+/g, '-')
}

const loadPages = async () => {
  loading.value = true
  loadError.value = false
  try {
    const response = await customPageApi.listCustomPages(
      pagination.value.page - 1,
      pagination.value.rowsPerPage
    )
    const data = response.data
    pages.value = data?.content || []
    pagination.value.rowsNumber = data?.totalElements || 0
  } catch (error) {
    // global axios interceptor already notifies
    loadError.value = true
    pages.value = []
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadAllPages = async () => {
  allPagesLoading.value = true
  allPagesError.value = false
  try {
    const response = await customPageApi.listAllCustomPages()
    allPages.value = response.data || []
  } catch (error) {
    allPagesError.value = true
    console.error(error)
  } finally {
    allPagesLoading.value = false
  }
}

const reload = () => {
  void loadPages()
  void loadAllPages()
}

const onRequest: QTableProps['onRequest'] = (props) => {
  pagination.value.page = props.pagination.page ?? 1
  pagination.value.rowsPerPage = props.pagination.rowsPerPage ?? 20
  void loadPages()
}

const openCreateDialog = (slug?: DefaultShopPageSlug) => {
  form.value = emptyForm()
  if (slug) {
    form.value.slug = slug
    form.value.title = DEFAULT_SHOP_PAGES[slug].title
    form.value.content = DEFAULT_SHOP_PAGES[slug].content.trim()
  }
  showDialog.value = true
}

const openEditDialog = (page: CustomPage) => {
  form.value = {
    ...emptyForm(),
    ...page,
    content: page.content || '',
    enabled: page.enabled !== false
  }
  showDialog.value = true
}

const applyDefaultContent = () => {
  const defaults = formDefaultPage.value
  if (!defaults) return
  form.value.content = defaults.content.trim()
  if (!form.value.title?.trim()) {
    form.value.title = defaults.title
  }
  $q.notify({ type: 'positive', message: '已套用前台預設內容', position: 'top' })
}

const handlePrefill = () => {
  if (!form.value.content?.trim()) {
    applyDefaultContent()
    return
  }
  $q.dialog({
    title: '套用前台預設內容',
    message: '目前的頁面內容將被前台預設內容取代，確定要繼續嗎？',
    cancel: { label: '取消', flat: true, color: 'grey-7' },
    ok: { label: '套用', unelevated: true, color: 'primary' },
    persistent: true
  }).onOk(applyDefaultContent)
}

const submitForm = async () => {
  const valid = await pageForm.value?.validate()
  if (!valid) {
    $q.notify({ type: 'warning', message: '請檢查必填欄位與格式', position: 'top' })
    return
  }
  await handleSubmit()
}

const handleSubmit = async () => {
  if (saving.value) return
  saving.value = true
  const payload: CustomPage = {
    id: form.value.id,
    title: form.value.title.trim(),
    slug: form.value.slug.trim(),
    content: form.value.content || '',
    metaTitle: form.value.metaTitle?.trim() || undefined,
    metaDescription: form.value.metaDescription?.trim() || undefined,
    metaKeywords: form.value.metaKeywords?.trim() || undefined,
    // 後端更新時會整筆覆蓋，enabled 不可為空
    enabled: form.value.enabled,
    sortOrder:
      typeof form.value.sortOrder === 'number' && !Number.isNaN(form.value.sortOrder)
        ? form.value.sortOrder
        : 0
  }
  try {
    if (form.value.id) {
      await customPageApi.updateCustomPage(form.value.id, payload)
      $q.notify({ type: 'positive', message: '自訂頁面已更新', position: 'top' })
    } else {
      await customPageApi.createCustomPage(payload)
      $q.notify({ type: 'positive', message: '自訂頁面已建立', position: 'top' })
    }
    showDialog.value = false
    reload()
  } catch (error) {
    // global axios interceptor already notifies（例如「頁面別名已存在」）
    console.error(error)
  } finally {
    saving.value = false
  }
}

const handleToggle = async (page: CustomPage) => {
  if (!page.id) return
  togglingId.value = page.id
  try {
    const response = await customPageApi.toggleEnabled(page.id)
    const enabled = response.data?.enabled ?? !page.enabled
    page.enabled = enabled
    const inAll = allPages.value.find((p) => p.id === page.id)
    if (inAll) inAll.enabled = enabled
    $q.notify({
      type: 'positive',
      message: enabled ? `「${page.title}」已啟用` : `「${page.title}」已停用`,
      position: 'top'
    })
  } catch (error) {
    console.error(error)
  } finally {
    togglingId.value = null
  }
}

const confirmDelete = (page: CustomPage) => {
  if (!page.id) return
  const id = page.id
  const builtInNote = isDefaultShopPageSlug(page.slug)
    ? '刪除後，前台會恢復顯示內建的預設內容。'
    : '刪除後，前台將無法瀏覽此頁面。'
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除「${page.title}」（${page.slug}）嗎？${builtInNote}此操作無法復原。`,
    cancel: { label: '取消', flat: true, color: 'grey-7' },
    ok: { label: '刪除', unelevated: true, color: 'negative' },
    persistent: true
  }).onOk(async () => {
    try {
      await customPageApi.deleteCustomPage(id)
      $q.notify({ type: 'positive', message: '自訂頁面已刪除', position: 'top' })
      // 若刪除後當頁已無資料，回到上一頁
      if (pages.value.length === 1 && pagination.value.page > 1) {
        pagination.value.page -= 1
      }
      reload()
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  reload()
})
</script>

<style scoped>
/* 讓編輯器中的標題大小接近前台頁面（Quasar 預設 h2~h4 字級過大） */
:deep(.custom-page-editor h2),
:deep(.custom-page-editor h3),
:deep(.custom-page-editor h4) {
  font-weight: 600;
  line-height: 1.4;
  letter-spacing: normal;
  margin: 20px 0 8px;
}

:deep(.custom-page-editor h2) {
  font-size: 1.5rem;
}

:deep(.custom-page-editor h3) {
  font-size: 1.3rem;
}

:deep(.custom-page-editor h4) {
  font-size: 1.1rem;
}

:deep(.custom-page-editor p) {
  margin: 8px 0;
}
</style>
