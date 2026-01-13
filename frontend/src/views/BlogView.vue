<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">部落格管理</div>
          <div class="text-caption text-grey-7">管理部落格文章和內容</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn
            round
            icon="help_outline"
            color="grey-7"
            @click="handleStartTour"
          >
            <q-tooltip>部落格管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="add_circle"
            label="新增文章"
            unelevated
            @click="showDialog = true; resetForm()"
          />
        </div>
      </div>

      <!-- Filter Tabs -->
      <q-card class="q-mb-md">
        <q-tabs v-model="statusTab" dense class="text-grey" active-color="primary" indicator-color="primary" align="left">
          <q-tab name="all" label="全部" />
          <q-tab name="DRAFT" label="草稿" />
          <q-tab name="PUBLISHED" label="已發布" />
          <q-tab name="SCHEDULED" label="排程中" />
          <q-tab name="ARCHIVED" label="已封存" />
        </q-tabs>
      </q-card>

      <!-- Blog Posts Table -->
      <q-card>
        <q-table
          :rows="posts"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          @request="onRequest"
          flat
        >
          <template v-slot:body-cell-title="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.row.title }}</div>
              <div class="text-caption text-grey-7">{{ props.row.slug }}</div>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)">
                {{ getStatusLabel(props.row.status) }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-viewCount="props">
            <q-td :props="props">
              <q-chip color="blue-grey" text-color="white" icon="visibility">
                {{ props.row.viewCount || 0 }}
              </q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn-dropdown flat dense color="primary" label="操作" size="sm">
                <q-list>
                  <q-item clickable v-close-popup @click="handleEdit(props.row)">
                    <q-item-section avatar>
                      <q-icon name="edit" />
                    </q-item-section>
                    <q-item-section>編輯</q-item-section>
                  </q-item>
                  
                  <q-item v-if="props.row.status !== 'PUBLISHED'" clickable v-close-popup @click="handlePublish(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="publish" />
                    </q-item-section>
                    <q-item-section>發布</q-item-section>
                  </q-item>

                  <q-item v-if="props.row.status === 'DRAFT'" clickable v-close-popup @click="showSchedulePublishDialog(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="schedule" />
                    </q-item-section>
                    <q-item-section>排程上架</q-item-section>
                  </q-item>

                  <q-item v-if="props.row.status === 'PUBLISHED' || props.row.status === 'SCHEDULED'" clickable v-close-popup @click="showScheduleUnpublishDialog(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="event_busy" />
                    </q-item-section>
                    <q-item-section>排程下架</q-item-section>
                  </q-item>
                  
                  <q-item v-if="props.row.status !== 'ARCHIVED'" clickable v-close-popup @click="handleArchive(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="archive" />
                    </q-item-section>
                    <q-item-section>封存</q-item-section>
                  </q-item>
                  
                  <q-separator />
                  
                  <q-item clickable v-close-popup @click="handleDelete(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="delete" color="negative" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-negative">刪除</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent maximized>
        <q-card>
          <q-card-section class="row items-center q-pb-none bg-primary">
            <div class="text-h6">{{ form.id ? '編輯文章' : '新增文章' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" />
          </q-card-section>

          <q-card-section>
            <q-form>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-8">
                  <q-input
                    v-model="form.title"
                    label="文章標題 *"
                    outlined
                    :rules="[val => !!val || '請輸入文章標題']"
                  />
                </div>

                <div class="col-12 col-md-4">
                  <q-input
                    v-model="form.slug"
                    label="文章別名 (URL) *"
                    outlined
                    hint="只能包含小寫字母、數字和連字號"
                    :rules="[val => !!val || '請輸入文章別名', val => /^[a-z0-9-]+$/.test(val) || '只能包含小寫字母、數字和連字號']"
                  />
                </div>

                <div class="col-12">
                  <q-input
                    v-model="form.summary"
                    label="摘要"
                    outlined
                    type="textarea"
                    rows="2"
                  />
                </div>

                <div class="col-12">
                  <q-input
                    v-model="form.content"
                    label="文章內容"
                    outlined
                    type="textarea"
                    rows="10"
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.coverImageUrl"
                    label="封面圖片URL"
                    outlined
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.tags"
                    label="標籤 (逗號分隔)"
                    outlined
                    hint="例如: 春季,新品,促銷"
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.authorName"
                    label="作者名稱"
                    outlined
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.status"
                    label="狀態"
                    outlined
                    :options="statusOptions"
                    emit-value
                    map-options
                  />
                </div>

                <div class="col-12">
                  <q-expansion-item label="SEO 設定" icon="settings">
                    <q-card>
                      <q-card-section>
                        <q-input
                          v-model="form.metaTitle"
                          label="SEO 標題"
                          outlined
                          class="q-mb-md"
                        />
                        <q-input
                          v-model="form.metaDescription"
                          label="SEO 描述"
                          outlined
                          type="textarea"
                          rows="3"
                          class="q-mb-md"
                        />
                        <q-input
                          v-model="form.metaKeywords"
                          label="SEO 關鍵字"
                          outlined
                        />
                      </q-card-section>
                    </q-card>
                  </q-expansion-item>
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- 排程上架對話框 -->
      <q-dialog v-model="showSchedulePublishDialogFlag">
        <q-card style="min-width: 400px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">排程上架</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-input
              v-model="schedulePublishDateTime"
              label="排程上架時間 *"
              outlined
              type="datetime-local"
              :rules="[val => !!val || '請選擇排程上架時間']"
            />
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="確認" color="primary" @click="handleSchedulePublish" :loading="schedulingPublish" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- 排程下架對話框 -->
      <q-dialog v-model="showScheduleUnpublishDialogFlag">
        <q-card style="min-width: 400px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">排程下架</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-input
              v-model="scheduleUnpublishDateTime"
              label="排程下架時間 *"
              outlined
              type="datetime-local"
              :rules="[val => !!val || '請選擇排程下架時間']"
            />
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="確認" color="primary" @click="handleScheduleUnpublish" :loading="schedulingUnpublish" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { blogApi, type BlogPost, type BlogStatus, type PageResponse } from '@/api'
import { startBlogTour, isBlogTourCompleted } from '@/utils/blogTour'

const $q = useQuasar()

const posts = ref<BlogPost[]>([])
const loading = ref(false)
const showDialog = ref(false)
const showSchedulePublishDialogFlag = ref(false)
const showScheduleUnpublishDialogFlag = ref(false)
const statusTab = ref('all')
const schedulingPublish = ref(false)
const schedulingUnpublish = ref(false)
const schedulePublishDateTime = ref('')
const scheduleUnpublishDateTime = ref('')
const currentSchedulePostId = ref<number | undefined>()

const form = ref<BlogPost>({
  title: '',
  slug: '',
  content: '',
  summary: '',
  coverImageUrl: '',
  status: 'DRAFT',
  authorName: '',
  tags: '',
  metaTitle: '',
  metaDescription: '',
  metaKeywords: ''
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'title', label: '標題', align: 'left' as const, field: 'title' },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'authorName', label: '作者', align: 'left' as const, field: 'authorName' },
  { name: 'viewCount', label: '瀏覽次數', align: 'center' as const, field: 'viewCount' },
  { name: 'publishedAt', label: '發布時間', align: 'left' as const, field: 'publishedAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已發布', value: 'PUBLISHED' },
  { label: '排程中', value: 'SCHEDULED' },
  { label: '已封存', value: 'ARCHIVED' }
]

const loadPosts = async () => {
  loading.value = true
  try {
    let response
    if (statusTab.value === 'all') {
      response = await blogApi.listBlogPosts(pagination.value.page - 1, pagination.value.rowsPerPage)
    } else {
      response = await blogApi.listBlogPostsByStatus(statusTab.value as BlogStatus, pagination.value.page - 1, pagination.value.rowsPerPage)
    }
    
    const data = response.data as PageResponse<BlogPost> | BlogPost[]
    if (Array.isArray(data)) {
      posts.value = data
      pagination.value.rowsNumber = data.length
    } else if (data && 'content' in data) {
      posts.value = data.content
      pagination.value.rowsNumber = data.totalElements
    } else {
      posts.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入文章清單失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onRequest = (props: any) => {
  pagination.value.page = props.pagination.page
  pagination.value.rowsPerPage = props.pagination.rowsPerPage
  loadPosts()
}

const getStatusColor = (status?: BlogStatus) => {
  const colorMap: Record<string, string> = {
    DRAFT: 'grey',
    PUBLISHED: 'positive',
    SCHEDULED: 'warning',
    ARCHIVED: 'blue-grey'
  }
  return colorMap[status || 'DRAFT'] || 'grey'
}

const getStatusLabel = (status?: BlogStatus) => {
  const labelMap: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已發布',
    SCHEDULED: '排程中',
    ARCHIVED: '已封存'
  }
  return labelMap[status || 'DRAFT'] || '未知'
}

const resetForm = () => {
  form.value = {
    title: '',
    slug: '',
    content: '',
    summary: '',
    coverImageUrl: '',
    status: 'DRAFT',
    authorName: '',
    tags: '',
    metaTitle: '',
    metaDescription: '',
    metaKeywords: ''
  }
}

const handleEdit = (post: BlogPost) => {
  form.value = { ...post }
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.value.title || !form.value.slug) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    if (form.value.id) {
      await blogApi.updateBlogPost(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await blogApi.createBlogPost(form.value)
      $q.notify({
        type: 'positive',
        message: '創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadPosts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handlePublish = async (id?: number) => {
  if (!id) return
  
  try {
    await blogApi.publishBlogPost(id)
    $q.notify({
      type: 'positive',
      message: '文章已發布',
      position: 'top'
    })
    loadPosts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '發布失敗',
      position: 'top'
    })
  }
}

const handleArchive = async (id?: number) => {
  if (!id) return
  
  try {
    await blogApi.archiveBlogPost(id)
    $q.notify({
      type: 'positive',
      message: '文章已封存',
      position: 'top'
    })
    loadPosts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '封存失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這篇文章嗎？此操作無法復原。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await blogApi.deleteBlogPost(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadPosts()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗',
        position: 'top'
      })
    }
  })
}

const showSchedulePublishDialog = (id?: number) => {
  if (!id) return
  currentSchedulePostId.value = id
  schedulePublishDateTime.value = ''
  showSchedulePublishDialogFlag.value = true
}

const handleSchedulePublish = async () => {
  if (!currentSchedulePostId.value || !schedulePublishDateTime.value) {
    $q.notify({
      type: 'warning',
      message: '請選擇排程上架時間',
      position: 'top'
    })
    return
  }

  schedulingPublish.value = true
  try {
    // 轉換為 ISO 8601 格式
    const isoDateTime = new Date(schedulePublishDateTime.value).toISOString()
    await blogApi.scheduleBlogPost(currentSchedulePostId.value, isoDateTime)
    $q.notify({
      type: 'positive',
      message: '排程上架設定成功',
      position: 'top'
    })
    showSchedulePublishDialogFlag.value = false
    loadPosts()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || '排程上架失敗',
      position: 'top'
    })
  } finally {
    schedulingPublish.value = false
  }
}

const showScheduleUnpublishDialog = (id?: number) => {
  if (!id) return
  currentSchedulePostId.value = id
  scheduleUnpublishDateTime.value = ''
  showScheduleUnpublishDialogFlag.value = true
}

const handleScheduleUnpublish = async () => {
  if (!currentSchedulePostId.value || !scheduleUnpublishDateTime.value) {
    $q.notify({
      type: 'warning',
      message: '請選擇排程下架時間',
      position: 'top'
    })
    return
  }

  schedulingUnpublish.value = true
  try {
    // 轉換為 ISO 8601 格式
    const isoDateTime = new Date(scheduleUnpublishDateTime.value).toISOString()
    await blogApi.scheduleUnpublishBlogPost(currentSchedulePostId.value, isoDateTime)
    $q.notify({
      type: 'positive',
      message: '排程下架設定成功',
      position: 'top'
    })
    showScheduleUnpublishDialogFlag.value = false
    loadPosts()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || '排程下架失敗',
      position: 'top'
    })
  } finally {
    schedulingUnpublish.value = false
  }
}

// 啟動部落格管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startBlogTour(true)
  })
}

watch(statusTab, () => {
  pagination.value.page = 1
  loadPosts()
})

onMounted(() => {
  loadPosts()
  
  // 如果用戶是第一次訪問部落格管理頁面，自動啟動導覽
  if (!isBlogTourCompleted()) {
    setTimeout(() => {
      startBlogTour()
    }, 1500)
  }
})
</script>

