<template>
  <q-page class="q-pa-md">
    <div class="blog-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">部落格管理</div>
          <div class="text-caption text-grey-7">管理部落格文章和内容</div>
        </div>
        <q-btn
          color="primary"
          icon="add_circle"
          label="新增文章"
          unelevated
          @click="showDialog = true; resetForm()"
        />
      </div>

      <!-- Filter Tabs -->
      <q-card class="q-mb-md">
        <q-tabs v-model="statusTab" dense class="text-grey" active-color="primary" indicator-color="primary" align="left">
          <q-tab name="all" label="全部" />
          <q-tab name="DRAFT" label="草稿" />
          <q-tab name="PUBLISHED" label="已发布" />
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
                    <q-item-section>编辑</q-item-section>
                  </q-item>
                  
                  <q-item v-if="props.row.status !== 'PUBLISHED'" clickable v-close-popup @click="handlePublish(props.row.id)">
                    <q-item-section avatar>
                      <q-icon name="publish" />
                    </q-item-section>
                    <q-item-section>发布</q-item-section>
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
                      <q-item-label class="text-negative">删除</q-item-label>
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
          <q-card-section class="row items-center q-pb-none bg-primary text-white">
            <div class="text-h6">{{ form.id ? '编辑文章' : '新增文章' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" />
          </q-card-section>

          <q-card-section>
            <q-form>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-8">
                  <q-input
                    v-model="form.title"
                    label="文章标题 *"
                    outlined
                    :rules="[val => !!val || '请输入文章标题']"
                  />
                </div>

                <div class="col-12 col-md-4">
                  <q-input
                    v-model="form.slug"
                    label="文章别名 (URL) *"
                    outlined
                    hint="只能包含小写字母、数字和连字号"
                    :rules="[val => !!val || '请输入文章别名', val => /^[a-z0-9-]+$/.test(val) || '只能包含小写字母、数字和连字号']"
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
                    label="文章内容"
                    outlined
                    type="textarea"
                    rows="10"
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.coverImageUrl"
                    label="封面图片URL"
                    outlined
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.tags"
                    label="标签 (逗号分隔)"
                    outlined
                    hint="例如: 春季,新品,促销"
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-input
                    v-model="form.authorName"
                    label="作者名称"
                    outlined
                  />
                </div>

                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.status"
                    label="状态"
                    outlined
                    :options="statusOptions"
                    emit-value
                    map-options
                  />
                </div>

                <div class="col-12">
                  <q-expansion-item label="SEO 设置" icon="settings">
                    <q-card>
                      <q-card-section>
                        <q-input
                          v-model="form.metaTitle"
                          label="SEO 标题"
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
                          label="SEO 关键字"
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
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useQuasar } from 'quasar'
import { blogApi, type BlogPost, type BlogStatus, type PageResponse } from '@/api'

const $q = useQuasar()

const posts = ref<BlogPost[]>([])
const loading = ref(false)
const showDialog = ref(false)
const statusTab = ref('all')

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
  { name: 'title', label: '标题', align: 'left' as const, field: 'title' },
  { name: 'status', label: '状态', align: 'center' as const, field: 'status' },
  { name: 'authorName', label: '作者', align: 'left' as const, field: 'authorName' },
  { name: 'viewCount', label: '浏览次数', align: 'center' as const, field: 'viewCount' },
  { name: 'publishedAt', label: '发布时间', align: 'left' as const, field: 'publishedAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
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
      message: '加载文章列表失败',
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
    PUBLISHED: '已发布',
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
      message: '请填写必填字段',
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
        message: '创建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadPosts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失败',
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
      message: '文章已发布',
      position: 'top'
    })
    loadPosts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '发布失败',
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
      message: '封存失败',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '确认删除',
    message: '确定要删除这篇文章吗？此操作不可恢复。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await blogApi.deleteBlogPost(id)
      $q.notify({
        type: 'positive',
        message: '删除成功',
        position: 'top'
      })
      loadPosts()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '删除失败',
        position: 'top'
      })
    }
  })
}

watch(statusTab, () => {
  pagination.value.page = 1
  loadPosts()
})

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.blog-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
