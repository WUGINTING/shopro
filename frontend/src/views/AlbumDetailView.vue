<template>
  <div class="q-pa-md">
    <q-card flat bordered>
      <q-card-section>
        <div class="row items-center justify-between q-col-gutter-sm">
          <div class="col">
            <q-btn flat icon="arrow_back" @click="goBack">返回</q-btn>
            <span class="text-h5 q-ml-md">{{ album?.name || '相簿詳情' }}</span>
            <div v-if="album?.description" class="text-caption text-grey q-mt-sm">
              {{ album.description }}
            </div>
            <div class="row items-center q-gutter-sm q-mt-sm" v-if="album">
              <q-chip dense color="grey-2" text-color="grey-8" icon="photo_library">
                {{ images.length }} 張圖片
              </q-chip>
              <q-chip v-if="album.coverImageUrl" dense color="primary" text-color="white" icon="image">
                已設定封面
              </q-chip>
            </div>
          </div>
          <div class="col-auto">
            <q-btn color="primary" icon="upload" label="上傳圖片" @click="showUploadDialog = true" />
          </div>
        </div>
      </q-card-section>
    </q-card>

    <div class="q-mt-md">
      <div v-if="loading" class="text-center q-pa-xl">
        <q-spinner color="primary" size="50px" />
      </div>

      <div v-else-if="images.length === 0" class="text-center q-pa-xl text-grey">
        <q-icon name="image" size="64px" />
        <div class="q-mt-md">此相簿尚無圖片</div>
        <q-btn color="primary" label="上傳圖片" class="q-mt-md" @click="showUploadDialog = true" />
      </div>

      <div v-else class="row q-col-gutter-md">
        <div
          v-for="image in images"
          :key="image.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3 col-xl-2"
        >
          <q-card
            flat
            bordered
            class="album-card"
            :class="{
              'album-card--cover': isCoverImage(image),
              'album-card--dragging': draggingImageId === image.id,
              'album-card--drop-before': dragOverImageId === image.id && dragOverPosition === 'before',
              'album-card--drop-after': dragOverImageId === image.id && dragOverPosition === 'after'
            }"
            :draggable="!isSorting"
            :aria-grabbed="draggingImageId === image.id ? 'true' : 'false'"
            @dragstart="onDragStart(image, $event)"
            @dragover.prevent="onDragOver(image, $event)"
            @drop.prevent="onDrop(image, $event)"
            @dragend="onDragEnd"
          >
            <q-img :src="image.imageUrl" :ratio="1" class="cursor-pointer" @click="viewImage(image)">
              <div v-if="isCoverImage(image)" class="absolute-top-left q-pa-xs">
                <q-chip dense color="primary" text-color="white" icon="star">封面</q-chip>
              </div>
              <template #error>
                <div class="absolute-full flex flex-center bg-grey-3 text-grey">無法載入圖片</div>
              </template>
            </q-img>

            <q-card-section v-if="image.title || image.description" class="q-pa-sm">
              <div v-if="image.title" class="text-caption text-weight-bold ellipsis">
                {{ image.title }}
              </div>
              <div v-if="image.description" class="text-caption text-grey ellipsis-2-lines">
                {{ image.description }}
              </div>
            </q-card-section>

            <q-card-actions align="between" class="q-pa-sm">
              <div class="row q-gutter-xs">
                <q-btn
                  flat
                  dense
                  round
                  icon="arrow_upward"
                  size="sm"
                  color="grey-7"
                  :disable="isSorting || isReorderingBoundary(image, 'up')"
                  @click="moveImage(image, 'up')"
                >
                  <q-tooltip>上移</q-tooltip>
                </q-btn>
                <q-btn
                  flat
                  dense
                  round
                  icon="arrow_downward"
                  size="sm"
                  color="grey-7"
                  :disable="isSorting || isReorderingBoundary(image, 'down')"
                  @click="moveImage(image, 'down')"
                >
                  <q-tooltip>下移</q-tooltip>
                </q-btn>
                <q-btn
                  flat
                  dense
                  round
                  icon="star"
                  size="sm"
                  :color="isCoverImage(image) ? 'primary' : 'amber-8'"
                  :disable="isSettingCover || isCoverImage(image)"
                  @click="setCoverImage(image)"
                >
                  <q-tooltip>{{ isCoverImage(image) ? '目前封面' : '設為封面' }}</q-tooltip>
                </q-btn>
              </div>
              <q-btn flat dense color="negative" icon="delete" size="sm" @click="deleteImageConfirm(image)">
                刪除
              </q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <q-dialog v-model="showUploadDialog">
      <q-card style="min-width: 400px; max-width: 92vw">
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
            <template #prepend>
              <q-icon name="image" />
            </template>
          </q-file>

          <q-input v-model="uploadForm.title" label="圖片標題" outlined dense class="q-mt-md" />
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
          <q-btn flat label="上傳" color="primary" @click="uploadImage" :loading="uploading" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="showImageDialog">
      <q-card style="max-width: 90vw; max-height: 90vh">
        <q-card-section class="q-pa-none">
          <q-img :src="selectedImage?.imageUrl" style="max-height: 80vh" fit="contain" />
        </q-card-section>
        <q-card-section v-if="selectedImage?.title || selectedImage?.description">
          <div v-if="selectedImage?.title" class="text-h6">{{ selectedImage.title }}</div>
          <div v-if="selectedImage?.description" class="text-body2 text-grey">{{ selectedImage.description }}</div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="關閉" color="grey" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
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
const isSorting = ref(false)
const isSettingCover = ref(false)
const draggingImageId = ref<number | null>(null)
const dragOverImageId = ref<number | null>(null)
const dragOverPosition = ref<'before' | 'after' | null>(null)

const showImageDialog = ref(false)
const selectedImage = ref<AlbumImage | null>(null)

const loadAlbum = async () => {
  loading.value = true
  try {
    const response = await albumApi.getAlbum(albumId.value)
    if (response.success && response.data) {
      album.value = response.data
    }
  } catch (error: any) {
    $q.notify({ type: 'negative', message: '載入相簿失敗：' + (error.message || '未知錯誤') })
  } finally {
    loading.value = false
  }
}

const loadImages = async () => {
  try {
    const response = await albumApi.getAlbumImages(albumId.value)
    if (response.success && response.data) {
      images.value = [...response.data].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
    }
  } catch (error: any) {
    $q.notify({ type: 'negative', message: '載入圖片失敗：' + (error.message || '未知錯誤') })
  }
}

const isCoverImage = (image: AlbumImage) => {
  if (!album.value?.coverImageUrl) return false
  return album.value.coverImageUrl === image.imageUrl
}

const isReorderingBoundary = (image: AlbumImage, direction: 'up' | 'down') => {
  const index = images.value.findIndex((item) => item.id === image.id)
  if (index < 0) return true
  return direction === 'up' ? index === 0 : index === images.value.length - 1
}

const normalizeSortOrder = (list: AlbumImage[]) =>
  list.map((item, index) => ({ ...item, sortOrder: index }))

const getPersistableImageIds = (list: AlbumImage[]) =>
  list.map((item) => item.id).filter((id): id is number => typeof id === 'number')

const persistImageOrder = async (nextImages: AlbumImage[], successMessage = '圖片排序已更新') => {
  if (isSorting.value) return

  const previousImages = [...images.value]
  images.value = normalizeSortOrder(nextImages)

  try {
    isSorting.value = true
    await albumApi.reorderImages(albumId.value, getPersistableImageIds(images.value))
    $q.notify({ type: 'positive', message: successMessage })
  } catch (error: any) {
    images.value = previousImages
    $q.notify({ type: 'negative', message: '更新排序失敗：' + (error.message || '未知錯誤') })
  } finally {
    isSorting.value = false
  }
}

const setCoverImage = async (image: AlbumImage) => {
  if (!image.id || isSettingCover.value) return

  isSettingCover.value = true
  try {
    const response = await albumApi.setCoverImage(albumId.value, image.id)
    if (response.success && response.data) {
      album.value = response.data
    } else if (album.value) {
      album.value = { ...album.value, coverImageUrl: image.imageUrl }
    }
    $q.notify({ type: 'positive', message: '封面圖片已更新' })
  } catch (error: any) {
    $q.notify({ type: 'negative', message: '設定封面失敗：' + (error.message || '未知錯誤') })
  } finally {
    isSettingCover.value = false
  }
}

const moveImage = async (image: AlbumImage, direction: 'up' | 'down') => {
  if (!image.id || isSorting.value) return

  const currentIndex = images.value.findIndex((item) => item.id === image.id)
  if (currentIndex < 0) return

  const targetIndex = direction === 'up' ? currentIndex - 1 : currentIndex + 1
  if (targetIndex < 0 || targetIndex >= images.value.length) return

  const nextImages = [...images.value]
  const [moved] = nextImages.splice(currentIndex, 1)
  nextImages.splice(targetIndex, 0, moved)

  await persistImageOrder(nextImages)
}

const onDragStart = (image: AlbumImage, event: DragEvent) => {
  if (!image.id || isSorting.value) return

  draggingImageId.value = image.id
  dragOverImageId.value = image.id
  dragOverPosition.value = null

  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', String(image.id))
  }
}

const onDragOver = (image: AlbumImage, event: DragEvent) => {
  if (!draggingImageId.value || draggingImageId.value === image.id || isSorting.value) return

  const target = event.currentTarget as HTMLElement | null
  if (!target) return

  const rect = target.getBoundingClientRect()
  const offsetY = event.clientY - rect.top
  dragOverImageId.value = image.id ?? null
  dragOverPosition.value = offsetY < rect.height / 2 ? 'before' : 'after'
}

const onDragEnd = () => {
  draggingImageId.value = null
  dragOverImageId.value = null
  dragOverPosition.value = null
}

const onDrop = async (targetImage: AlbumImage) => {
  if (
    !draggingImageId.value ||
    !dragOverPosition.value ||
    !targetImage.id ||
    draggingImageId.value === targetImage.id ||
    isSorting.value
  ) {
    onDragEnd()
    return
  }

  const sourceIndex = images.value.findIndex((item) => item.id === draggingImageId.value)
  const targetIndex = images.value.findIndex((item) => item.id === targetImage.id)
  if (sourceIndex < 0 || targetIndex < 0) {
    onDragEnd()
    return
  }

  const nextImages = [...images.value]
  const [moved] = nextImages.splice(sourceIndex, 1)
  const baseInsertIndex = dragOverPosition.value === 'before' ? targetIndex : targetIndex + 1
  const insertIndex = sourceIndex < baseInsertIndex ? baseInsertIndex - 1 : baseInsertIndex
  nextImages.splice(insertIndex, 0, moved)

  onDragEnd()
  await persistImageOrder(nextImages)
}

const uploadImage = async () => {
  if (!uploadFile.value) {
    $q.notify({ type: 'warning', message: '請先選擇要上傳的圖片' })
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

    $q.notify({ type: 'positive', message: '圖片已上傳' })
    showUploadDialog.value = false
    uploadFile.value = null
    uploadForm.value = { title: '', description: '' }

    await Promise.all([loadImages(), loadAlbum()])
  } catch (error: any) {
    $q.notify({ type: 'negative', message: '上傳圖片失敗：' + (error.message || '未知錯誤') })
  } finally {
    uploading.value = false
  }
}

const viewImage = (image: AlbumImage) => {
  selectedImage.value = image
  showImageDialog.value = true
}

const deleteImageConfirm = (image: AlbumImage) => {
  if (!image.id) {
    $q.notify({ type: 'warning', message: '無效的圖片 ID' })
    return
  }

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除此圖片嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await albumApi.deleteImage(albumId.value, image.id)
      $q.notify({ type: 'positive', message: '圖片已刪除' })
      await Promise.all([loadImages(), loadAlbum()])
    } catch (error: any) {
      $q.notify({ type: 'negative', message: '刪除圖片失敗：' + (error.message || '未知錯誤') })
    }
  })
}

const onFileRejected = () => {
  $q.notify({
    type: 'warning',
    message: '檔案大小超過限制（最大 10MB）或格式不正確'
  })
}

const goBack = () => {
  router.push('/albums')
}

onMounted(() => {
  loadAlbum()
  loadImages()
})
</script>

<style scoped>
.album-card {
  position: relative;
  cursor: grab;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.album-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.1);
}

.album-card--cover {
  border-color: rgba(25, 118, 210, 0.42);
  box-shadow: 0 0 0 1px rgba(25, 118, 210, 0.12), 0 10px 24px rgba(25, 118, 210, 0.12);
}

.album-card--dragging {
  opacity: 0.55;
  cursor: grabbing;
}

.album-card--drop-before::before,
.album-card--drop-after::after {
  content: '';
  position: absolute;
  left: 8px;
  right: 8px;
  height: 3px;
  border-radius: 999px;
  background: rgba(25, 118, 210, 0.92);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.9);
  z-index: 3;
}

.album-card--drop-before::before {
  top: -2px;
}

.album-card--drop-after::after {
  bottom: -2px;
}

.ellipsis-2-lines {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (prefers-reduced-motion: reduce) {
  .album-card {
    transition: none;
  }
}
</style>
