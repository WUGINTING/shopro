<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <div class="sr-only" aria-live="polite">{{ liveMessage }}</div>
    <section class="detail-hero q-mb-lg">
      <q-btn
        flat
        no-caps
        icon="arrow_back"
        color="primary"
        class="back-btn"
        label="回到日誌"
        @click="goBack"
      />

      <div v-if="loading" class="detail-skeleton">
        <div class="skeleton-line skeleton-line--xl"></div>
        <div class="skeleton-line skeleton-line--lg"></div>
        <div class="skeleton-line"></div>
      </div>

      <div v-else-if="!post" class="empty-state">
        <q-icon name="error_outline" size="32px" color="grey-6" aria-hidden="true" />
        <div class="text-subtitle2 q-mt-sm">找不到這篇日誌</div>
        <p class="text-body2 text-grey-7 q-mt-xs">可能已下架或連結有誤。</p>
        <q-btn unelevated color="primary" no-caps label="回到日誌列表" to="/blog" />
      </div>

      <div v-else class="detail-header">
        <q-chip dense square color="orange-2" text-color="deep-orange-9">
          {{ formatDate(post.publishedAt || post.scheduledAt) }}
        </q-chip>
        <h1 class="detail-title">{{ post.title }}</h1>
        <p class="detail-subtitle">{{ post.summary || '閱讀完整內容，掌握最新活動與商品消息。' }}</p>
        <div class="detail-meta">
          <span v-if="post.authorName">作者：{{ post.authorName }}</span>
          <span v-if="post.tags">標籤：{{ post.tags }}</span>
          <span v-if="post.viewCount !== undefined">瀏覽：{{ post.viewCount }}</span>
        </div>
      </div>
    </section>

    <section v-if="post" class="detail-body">
      <div v-if="post.coverImageUrl" class="detail-cover" :style="{ backgroundImage: `url(${post.coverImageUrl})` }"></div>
      <div class="detail-content" v-html="post.content"></div>
    </section>

    <section v-if="post" class="detail-cta q-mt-xl">
      <q-banner rounded class="sf-note q-py-md">
        <template #avatar>
          <q-icon name="campaign" size="28px" aria-hidden="true" />
        </template>
        最新促銷與折扣碼已更新，立即查看活動內容。
        <template #action>
          <q-btn flat no-caps color="primary" label="查看促銷" to="/promotions" />
        </template>
      </q-banner>
    </section>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Notify } from 'quasar'
import blogApi, { type BlogPost } from '@/api/blog'

const route = useRoute()
const router = useRouter()
const post = ref<BlogPost | null>(null)
const loading = ref(true)
const liveMessage = ref('')

const goTo = (path: string) => {
  router.push(path)
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    goTo('/blog')
  }
}

const formatDate = (value?: string) => {
  if (!value) return '最近更新'
  return new Date(value).toLocaleDateString('zh-TW')
}

const loadPost = async () => {
  loading.value = true
  try {
    const slug = route.params.slug as string
    const response = await blogApi.getBlogPostBySlug(slug)
    post.value = response.data || null
  } catch (error) {
    post.value = null
    liveMessage.value = '載入日誌失敗，請回到列表再試'
    Notify.create({ type: 'negative', message: '載入日誌失敗，請回到列表再試', position: 'top' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPost()
})
</script>

<style scoped>
.store-page {
  max-width: 980px;
  margin: 0 auto;
}

.back-btn {
  border-radius: 999px;
  margin-bottom: 16px;
}

.detail-header {
  display: grid;
  gap: 12px;
}

.detail-title {
  margin: 0;
  font-size: 2.2rem;
  line-height: 1.2;
  color: #2f241f;
  text-wrap: balance;
}

.detail-subtitle {
  margin: 0;
  color: #6b5c50;
  line-height: 1.6;
  text-wrap: pretty;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 0.85rem;
  color: #8a7a6d;
}

.detail-cover {
  height: 320px;
  border-radius: 18px;
  background-size: cover;
  background-position: center;
  margin-bottom: 20px;
  border: 1px solid #eadfcd;
}

.detail-content {
  line-height: 1.8;
  color: #2f241f;
  overflow-wrap: anywhere;
}

.detail-content :deep(p) {
  margin: 0 0 16px;
}

.detail-content :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  border: 1px solid #eadfcd;
}

.detail-content :deep(h2),
.detail-content :deep(h3) {
  margin: 24px 0 12px;
  color: #3b2f28;
}

.detail-skeleton {
  display: grid;
  gap: 10px;
}

.skeleton-line {
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(90deg, #f2e7d6 0%, #fff6eb 50%, #f2e7d6 100%);
}

.skeleton-line--lg {
  height: 16px;
}

.skeleton-line--xl {
  height: 26px;
}

.empty-state {
  border: 1px dashed #eadfcd;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  background: #fffdf9;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 640px) {
  .detail-title {
    font-size: 1.7rem;
  }

  .detail-cover {
    height: 220px;
  }
}
</style>
