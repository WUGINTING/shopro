<template>
  <q-page class="store-page sf-page q-pa-md q-pa-lg-lg">
    <div class="sr-only" aria-live="polite">{{ liveMessage }}</div>
    <section class="blog-hero q-pa-lg q-pa-xl-md q-mb-lg">
      <div class="blog-hero__content">
        <div class="sf-chip sf-chip--warm q-mb-sm">
          <q-icon name="article" size="16px" aria-hidden="true" />
          品牌日誌
        </div>
        <h1 class="blog-title">最新故事與選品觀點</h1>
        <p class="blog-subtitle">
          你會在這裡看到新品靈感、保養指南與活動公告。每週更新，讓選購更有方向。
        </p>
      </div>
      <q-card flat bordered class="blog-hero__card">
        <q-card-section>
          <div class="blog-hero__card-title">本週推薦</div>
          <div class="blog-hero__card-text">
            先看精選內容，再逛商品，能更快找到適合自己的選擇。
          </div>
        <q-btn
          unelevated
          color="primary"
          no-caps
          class="blog-hero__cta"
          label="前往促銷活動"
          to="/promotions"
        />
      </q-card-section>
    </q-card>
    </section>

    <section>
      <div class="section-head q-mb-md">
        <div>
          <h2 class="sf-section-title">最新日誌</h2>
          <p class="sf-section-subtitle">從內容快速了解活動與新品亮點。</p>
        </div>
        <q-btn flat no-caps color="primary" icon="shopping_bag" label="去逛商品" to="/products" />
      </div>

      <div v-if="loading" class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="index in 6" :key="`blog-skeleton-${index}`">
          <q-card bordered class="blog-card blog-card--skeleton">
            <q-card-section>
              <div class="skeleton-line skeleton-line--lg"></div>
              <div class="skeleton-line"></div>
              <div class="skeleton-line skeleton-line--short"></div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <div v-else-if="posts.length === 0" class="empty-state">
        <q-icon name="article" size="32px" color="grey-6" aria-hidden="true" />
        <div class="text-subtitle2 q-mt-sm">目前沒有日誌內容</div>
        <p class="text-body2 text-grey-7 q-mt-xs">我們正在準備新內容，稍後再回來看看。</p>
      </div>

      <div v-else class="row q-col-gutter-md">
        <div class="col-12 col-md-4" v-for="post in posts" :key="post.id || post.slug">
          <q-card bordered class="blog-card sf-elevate-hover">
            <div class="blog-cover" :style="coverStyle(post.coverImageUrl)">
              <div v-if="!post.coverImageUrl" class="blog-cover__fallback">
                <q-icon name="auto_stories" size="32px" aria-hidden="true" />
              </div>
            </div>
            <q-card-section>
              <q-chip dense square color="grey-2" text-color="grey-8">
                {{ formatDate(post.publishedAt || post.scheduledAt) }}
              </q-chip>
              <div class="text-subtitle1 text-weight-bold q-mt-sm">{{ post.title }}</div>
              <div class="text-body2 text-grey-7 q-mt-xs">
                {{ post.summary || '閱讀完整內容，了解更多品牌觀點。' }}
              </div>
            </q-card-section>
            <q-card-actions align="right">
              <q-btn
                flat
                no-caps
                color="primary"
                label="閱讀日誌"
                :to="post.slug ? `/blog/${post.slug}` : '/blog'"
                :disable="!post.slug"
              />
            </q-card-actions>
          </q-card>
        </div>
      </div>

      <div v-if="totalPages > 1" class="q-mt-lg flex flex-center">
        <q-pagination
          v-model="page"
          :max="totalPages"
          color="primary"
          size="md"
          aria-label="Blog pagination"
          @update:model-value="handlePageChange"
        />
      </div>
    </section>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Notify } from 'quasar'
import blogApi, { type BlogPost } from '@/api/blog'

const route = useRoute()
const router = useRouter()
const posts = ref<BlogPost[]>([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)
const liveMessage = ref('')

const formatDate = (value?: string) => {
  if (!value) return '最近更新'
  return new Date(value).toLocaleDateString('zh-TW')
}

const coverStyle = (coverImageUrl?: string) => {
  if (!coverImageUrl) return {}
  return {
    backgroundImage: `url(${coverImageUrl})`
  }
}

const loadPosts = async () => {
  loading.value = true
  try {
    const response = await blogApi.listBlogPostsByStatus('PUBLISHED', page.value - 1, 9)
    const data = response.data
    posts.value = data?.content || []
    totalPages.value = data?.totalPages || 1
  } catch (error) {
    liveMessage.value = '載入日誌失敗，請重新整理'
    Notify.create({ type: 'negative', message: '載入日誌失敗，請重新整理', position: 'top' })
  } finally {
    loading.value = false
  }
}

const handlePageChange = (value: number) => {
  router.replace({ query: { ...route.query, page: String(value) } })
}

watch(
  () => route.query.page,
  (value) => {
    const nextPage = Number(value || 1)
    page.value = Number.isFinite(nextPage) && nextPage > 0 ? nextPage : 1
    loadPosts()
  },
  { immediate: true }
)
</script>

<style scoped>
.store-page {
  max-width: 1180px;
  margin: 0 auto;
}

.blog-hero {
  border-radius: 24px;
  background: linear-gradient(135deg, #1f2937 0%, #7c4023 45%, #f1c27d 100%);
  color: #fffaf2;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 60px rgba(30, 41, 59, 0.18);
}

.blog-title {
  margin: 8px 0;
  font-size: 2.2rem;
  line-height: 1.2;
  text-wrap: balance;
}

.blog-subtitle {
  margin: 0;
  color: rgba(255, 251, 245, 0.9);
  line-height: 1.65;
  max-width: 60ch;
  text-wrap: pretty;
}

.blog-hero__card {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
}

.blog-hero__card-title {
  font-weight: 700;
}

.blog-hero__card-text {
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 8px 0 16px;
}

.blog-hero__cta {
  border-radius: 999px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sf-section-title {
  text-wrap: balance;
}

.sf-section-subtitle {
  text-wrap: pretty;
}

.blog-card {
  border-radius: 18px;
  border-color: #eadfcd;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.blog-card .text-subtitle1 {
  overflow-wrap: anywhere;
}

.blog-card .text-body2 {
  overflow-wrap: anywhere;
}

.blog-cover {
  height: 180px;
  background-size: cover;
  background-position: center;
  background-color: #fff6eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.blog-cover__fallback {
  color: #9c6b4a;
}

.blog-card--skeleton {
  border-radius: 18px;
  background: #fffaf5;
}

.skeleton-line {
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(90deg, #f2e7d6 0%, #fff6eb 50%, #f2e7d6 100%);
  margin-bottom: 10px;
}

.skeleton-line--lg {
  height: 16px;
}

.skeleton-line--short {
  width: 60%;
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

@media (max-width: 900px) {
  .blog-hero {
    grid-template-columns: 1fr;
  }

  .blog-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 640px) {
  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
