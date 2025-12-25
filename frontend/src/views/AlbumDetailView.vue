<template>
  <div class="q-pa-md">
    <q-card flat bordered>
      <q-card-section>
        <div class="row items-center justify-between">
          <div>
            <q-btn flat icon="arrow_back" @click="goBack">返回</q-btn>
            <span class="text-h5 q-ml-md">{{ album?.name || '相冊詳情' }}</span>
          </div>
          <q-btn color="primary" icon="upload" label="上傳圖片" @click="showUploadDialog = true" />
        </div>
        <div class="text-caption text-grey q-mt-sm" v-if="album?.description">
          {{ album.description }}
        </div>
      </q-card-section>
    </q-card>

    <!-- 圖片網格 -->
    <div class="q-mt-md">
      <div v-if="loading" class="text-center q-pa-xl">
        <q-spinner color="primary" size="50px" />
      </div>

      <div v-else-if="images.length === 0" class="text-center q-pa-xl text-grey">
        <q-icon name="image" size="64px" />
        <div class="q-mt-md">此相冊尚無圖片</div>
        <q-btn
          color="primary"
          label="上傳圖片"
          class="q-mt-md"
          @click="showUploadDialog = true"
        />
      </div>

      <div v-else class="row q-col-gutter-md">
        <div
          v-for="image in images"
          :key="image.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3 col-xl-2"
        >
          <q-card flat bordered class="image-card">
            <q-img
              :src="image.imageUrl"
              :ratio="1"
              class="cursor-pointer"
              @click="viewImage(image)"
            >
              <template v-slot:error>
                <div class="absolute-full flex flex-center bg-grey-3 text-grey">
                  無法載入圖片
                </div>
              </template>
            </q-img>

            <q-card-section v-if="image.title || image.description" class="q-pa-sm">
              <div class="text-caption text-weight-bold" v-if="image.title">
                {{ image.title }}
              </div>
              <div class="text-caption text-grey" v-if="image.description">
                {{ image.description }}
              </div>
            </q-card-section>

            <q-card-actions>
              <q-btn flat dense color="negative" icon="delete" size="sm" @click="deleteImageConfirm(image)">
                刪除
              </q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <!-- 上傳圖片對話框 -->
    <q-dialog v-model="showUploadDialog">
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">上傳圖片</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-file
            v-model="uploadFile"
            label="選擇圖片 *"
            outlined
            accept="image/*"
            max-file-size="10485760"
            @rejected="onFileRejected"
          >
            <template v-slot:prepend>
              <q-icon name="image" />
            </template>
          </q-file>

          <q-input
            v-model="uploadForm.title"
            label="圖片標題"
            outlined
            dense
            class="q-mt-md"
          />

          <q-input
            v-model="uploadForm.description"
            label="圖片描述"
            type="textarea"
            outlined
            dense
            class="q-mt-md"
            rows="3"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="取消" color="grey" v-close-popup />
          <q-btn
            flat
            label="上傳"
            color="primary"
            @click="uploadImage"
            :loading="uploading"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 圖片查看對話框 -->
    <q-dialog v-model="showImageDialog">
      <q-card style="max-width: 90vw; max-height: 90vh">
        <q-card-section class="q-pa-none">
          <q-img
            :src="selectedImage?.imageUrl"
            style="max-height: 80vh"
            fit="contain"
          />
        </q-card-section>

        <q-card-section v-if="selectedImage?.title || selectedImage?.description">
          <div class="text-h6" v-if="selectedImage?.title">
            {{ selectedImage.title }}
          </div>
          <div class="text-body2 text-grey" v-if="selectedImage?.description">
            {{ selectedImage.description }}
          </div>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="關閉" color="grey" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { albumApi, type Album, type AlbumImage } from '@/api/album'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const albumId = ref<number>(Number(route.params.id))
const album = ref<Album | null>(null)
const images = ref<AlbumImage[]>([])
const loading = ref(false)

const showUploadDialog = ref(false)
const uploadFile = ref<File | null>(null)
const uploadForm = ref({
  title: '',
  description: ''
})
const uploading = ref(false)

const showImageDialog = ref(false)
const selectedImage = ref<AlbumImage | null>(null)

// 載入相冊詳情
const loadAlbum = async () => {
  loading.value = true
  try {
    const response = await albumApi.getAlbum(albumId.value)
    if (response.success && response.data) {
      album.value = response.data
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

// 載入相冊圖片
const loadImages = async () => {
  try {
    const response = await albumApi.getAlbumImages(albumId.value)
    if (response.success && response.data) {
      images.value = response.data
    }
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '載入圖片失敗：' + (error.message || '未知錯誤')
    })
  }
}

// 上傳圖片
const uploadImage = async () => {
  if (!uploadFile.value) {
    $q.notify({
      type: 'warning',
      message: '請選擇要上傳的圖片'
    })
    return
  }

  uploading.value = true
  try {
    await albumApi.uploadImage(
      albumId.value,
      uploadFile.value,
      uploadForm.value.title,
      uploadForm.value.description
    )

    $q.notify({
      type: 'positive',
      message: '圖片已上傳'
    })

    showUploadDialog.value = false
    uploadFile.value = null
    uploadForm.value = { title: '', description: '' }
    
    await loadImages()
    await loadAlbum()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: '上傳圖片失敗：' + (error.message || '未知錯誤')
    })
  } finally {
    uploading.value = false
  }
}

// 查看圖片
const viewImage = (image: AlbumImage) => {
  selectedImage.value = image
  showImageDialog.value = true
}

// 刪除圖片確認
const deleteImageConfirm = (image: AlbumImage) => {
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除此圖片嗎？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await albumApi.deleteImage(albumId.value, image.id!)
      $q.notify({
        type: 'positive',
        message: '圖片已刪除'
      })
      await loadImages()
      await loadAlbum()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: '刪除圖片失敗：' + (error.message || '未知錯誤')
      })
    }
  })
}

// 檔案被拒絕
const onFileRejected = () => {
  $q.notify({
    type: 'warning',
    message: '檔案大小超過限制（最大 10MB）或格式不正確'
  })
}

// 返回
const goBack = () => {
  router.push('/albums')
}

onMounted(() => {
  loadAlbum()
  loadImages()
})
</script>

<style scoped>
.image-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.image-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
