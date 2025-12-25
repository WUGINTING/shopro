<template>
  <div class="q-pa-md">
    <q-card flat bordered>
      <q-card-section>
        <div class="row items-center justify-between">
          <div class="text-h5">相冊管理</div>
          <q-btn color="primary" icon="add" label="新增相冊" @click="showCreateDialog = true" />
        </div>
      </q-card-section>
    </q-card>

    <!-- 相冊列表 -->
    <div class="q-mt-md">
      <div v-if="loading" class="text-center q-pa-xl">
        <q-spinner color="primary" size="50px" />
      </div>

      <div v-else class="row q-col-gutter-md">
        <div v-for="album in albums" :key="album.id" class="col-12 col-sm-6 col-md-4 col-lg-3">
          <q-card flat bordered class="album-card cursor-pointer" @click="viewAlbum(album.id)">
            <q-img
              :src="album.coverImageUrl || 'https://via.placeholder.com/300x200?text=No+Cover'"
              :ratio="4/3"
              class="album-cover"
            >
              <div class="absolute-bottom text-subtitle1 text-center">
                {{ album.name }}
              </div>
            </q-img>

            <q-card-section>
              <div class="text-caption text-grey">
                {{ album.description || '無描述' }}
              </div>
              <div class="text-caption text-grey q-mt-xs">
                {{ album.imageCount || 0 }} 張圖片
              </div>
            </q-card-section>

            <q-card-actions>
              <q-btn flat color="primary" icon="edit" @click.stop="editAlbum(album)">編輯</q-btn>
              <q-btn flat color="negative" icon="delete" @click.stop="deleteAlbumConfirm(album)">刪除</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>

      <!-- 分頁 -->
      <div class="q-mt-md flex flex-center" v-if="totalPages > 1">
        <q-pagination
          v-model="currentPage"
          :max="totalPages"
          direction-links
          @update:model-value="loadAlbums"
        />
      </div>
    </div>

    <!-- 創建/編輯相冊對話框 -->
    <q-dialog v-model="showCreateDialog">
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">{{ editingAlbum ? '編輯相冊' : '新增相冊' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            v-model="albumForm.name"
            label="相冊名稱 *"
            outlined
            dense
            :rules="[val => !!val || '請輸入相冊名稱']"
          />

          <q-input
            v-model="albumForm.description"
            label="相冊描述"
            type="textarea"
            outlined
            dense
            class="q-mt-md"
            rows="3"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="取消" color="grey" v-close-popup />
          <q-btn flat label="儲存" color="primary" @click="saveAlbum" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { albumApi, type Album } from '@/api/album'

const router = useRouter()
const $q = useQuasar()

const albums = ref<Album[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = ref(12)

const showCreateDialog = ref(false)
const editingAlbum = ref<Album | null>(null)
const albumForm = ref<Album>({
  name: '',
  description: ''
})

// 載入相冊列表
const loadAlbums = async () => {
  loading.value = true
  try {
    const response = await albumApi.getAlbums({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    if (response.success && response.data) {
      albums.value = response.data.content || []
      totalPages.value = response.data.totalPages || 1
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '載入相冊失敗：' + (error.message || '未知錯誤')
    })
  } finally {
    loading.value = false
  }
}

// 查看相冊
const viewAlbum = (id: number | undefined) => {
  if (id) {
    router.push(`/albums/${id}`)
  }
}

// 編輯相冊
const editAlbum = (album: Album) => {
  editingAlbum.value = album
  albumForm.value = {
    name: album.name,
    description: album.description
  }
  showCreateDialog.value = true
}

// 儲存相冊
const saveAlbum = async () => {
  if (!albumForm.value.name) {
    $q.notify({
      type: 'warning',
      message: '請輸入相冊名稱'
    })
    return
  }

  try {
    if (editingAlbum.value?.id) {
      // 更新相冊
      await albumApi.updateAlbum(editingAlbum.value.id, albumForm.value)
      $q.notify({
        type: 'positive',
        message: '相冊已更新'
      })
    } else {
      // 創建相冊
      await albumApi.createAlbum(albumForm.value)
      $q.notify({
        type: 'positive',
        message: '相冊已創建'
      })
    }

    showCreateDialog.value = false
    editingAlbum.value = null
    albumForm.value = { name: '', description: '' }
    loadAlbums()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '儲存相冊失敗：' + (error.message || '未知錯誤')
    })
  }
}

// 刪除相冊確認
const deleteAlbumConfirm = (album: Album) => {
  if (!album.id) {
    $q.notify({
      type: 'warning',
      message: '無效的相冊 ID'
    })
    return
  }

  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除相冊「${album.name}」嗎？此操作將同時刪除相冊中的所有圖片。`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await albumApi.deleteAlbum(album.id!)
      $q.notify({
        type: 'positive',
        message: '相冊已刪除'
      })
      loadAlbums()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: '刪除相冊失敗：' + (error.message || '未知錯誤')
      })
    }
  })
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped>
.album-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.album-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.album-cover {
  position: relative;
}
</style>
