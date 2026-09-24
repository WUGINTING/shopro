<template>
  <q-page class="shop-news-detail-page">
    <!-- Breadcrumb -->
    <div class="breadcrumb-container">
      <div class="breadcrumb-wrapper">
        <q-breadcrumbs separator="›" active-color="primary">
          <q-breadcrumbs-el label="首頁" to="/shop" />
          <q-breadcrumbs-el label="最新消息" to="/shop/news" />
          <q-breadcrumbs-el :label="breadcrumbLabel" class="breadcrumb-current" />
        </q-breadcrumbs>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="article-container">
      <div class="article-main">
        <q-skeleton type="text" width="30%" />
        <q-skeleton type="text" class="text-h4" />
        <q-skeleton type="text" width="50%" class="q-mb-lg" />
        <q-skeleton height="280px" class="q-mb-lg" />
        <q-skeleton v-for="n in 5" :key="n" type="text" />
      </div>
    </div>

    <!-- 文章不存在 / 載入失敗 -->
    <div v-else-if="notFound || errorMessage" class="state-container">
      <div class="state-box">
        <q-icon
          :name="notFound ? 'search_off' : 'cloud_off'"
          size="80px"
          color="grey-4"
        />
        <h2 class="state-title">{{ notFound ? '文章不存在' : '文章載入失敗' }}</h2>
        <p class="state-text">
          {{
            notFound
              ? '這篇文章可能已下架或網址有誤。'
              : errorMessage
          }}
        </p>
        <div class="state-actions">
          <q-btn
            v-if="!notFound"
            unelevated
            color="primary"
            icon="refresh"
            label="重新載入"
            @click="loadArticle"
          />
          <q-btn
            :unelevated="notFound"
            :outline="!notFound"
            color="primary"
            icon="list"
            label="返回消息列表"
            to="/shop/news"
          />
        </div>
      </div>
    </div>

    <!-- Article Content -->
    <div v-else-if="article" class="article-container">
      <article class="article-main">
        <!-- Article Header -->
        <header class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span v-if="article.publishedAt" class="meta-item">
              <q-icon name="event" size="18px" />
              {{ displayDate(article.publishedAt) }}
            </span>
            <span v-if="article.viewCount != null" class="meta-item">
              <q-icon name="visibility" size="18px" />
              {{ article.viewCount }} 次瀏覽
            </span>
            <span v-if="article.authorName" class="meta-item">
              <q-icon name="person" size="18px" />
              {{ article.authorName }}
            </span>
          </div>
        </header>

        <!-- Featured Image -->
        <div
          v-if="article.coverImageUrl && !coverBroken"
          class="article-featured-image"
        >
          <img
            :src="article.coverImageUrl"
            :alt="article.title"
            @error="coverBroken = true"
          />
        </div>

        <!-- Article Body（後台撰寫的 HTML，渲染前已清理） -->
        <div class="article-body">
          <div v-if="safeContent" v-html="safeContent"></div>
          <p v-else-if="article.summary">{{ article.summary }}</p>
        </div>

        <!-- Article Tags -->
        <div v-if="tagList.length > 0" class="article-tags">
          <q-icon name="local_offer" size="20px" class="tags-icon" />
          <div class="tags-list">
            <q-chip
              v-for="tag in tagList"
              :key="tag"
              color="grey-3"
              text-color="grey-8"
              size="md"
            >
              {{ tag }}
            </q-chip>
          </div>
        </div>

        <!-- Share Buttons -->
        <div class="article-share">
          <div class="share-title">分享文章</div>
          <div class="share-buttons">
            <q-btn
              round
              color="primary"
              icon="facebook"
              aria-label="分享到 Facebook"
              @click="shareToFacebook"
            >
              <q-tooltip>分享到 Facebook</q-tooltip>
            </q-btn>
            <q-btn
              round
              color="info"
              icon="send"
              aria-label="分享到 LINE"
              @click="shareToLine"
            >
              <q-tooltip>分享到 LINE</q-tooltip>
            </q-btn>
            <q-btn
              round
              color="grey-7"
              icon="link"
              aria-label="複製連結"
              @click="copyLink"
            >
              <q-tooltip>複製連結</q-tooltip>
            </q-btn>
          </div>
        </div>

        <!-- Navigation（依已發布文章的發布時間排序） -->
        <div v-if="currentIndex !== -1" class="article-navigation">
          <router-link
            v-if="prevArticle"
            :to="`/shop/news/${prevArticle.slug}`"
            class="nav-item nav-prev"
          >
            <div class="nav-label">
              <q-icon name="chevron_left" size="24px" />
              上一篇
            </div>
            <div class="nav-title">{{ prevArticle.title }}</div>
          </router-link>
          <div v-else class="nav-item nav-disabled">
            <div class="nav-label">已是最早的文章</div>
          </div>

          <router-link
            v-if="nextArticle"
            :to="`/shop/news/${nextArticle.slug}`"
            class="nav-item nav-next"
          >
            <div class="nav-label">
              下一篇
              <q-icon name="chevron_right" size="24px" />
            </div>
            <div class="nav-title">{{ nextArticle.title }}</div>
          </router-link>
          <div v-else class="nav-item nav-disabled">
            <div class="nav-label">已是最新的文章</div>
          </div>
        </div>
      </article>

      <!-- Sidebar -->
      <aside class="article-sidebar">
        <!-- Related News（有共同標籤的文章） -->
        <div v-if="relatedNews.length > 0" class="sidebar-box">
          <h3 class="sidebar-title">相關文章</h3>
          <div class="related-list">
            <router-link
              v-for="related in relatedNews"
              :key="related.id"
              :to="`/shop/news/${related.slug}`"
              class="related-item"
            >
              <div class="related-image">
                <img
                  v-if="related.coverImageUrl && !brokenImages[related.id]"
                  :src="related.coverImageUrl"
                  :alt="related.title"
                  loading="lazy"
                  @error="markBroken(related.id)"
                />
                <div v-else class="related-image-fallback">
                  <q-icon name="campaign" size="28px" />
                </div>
              </div>
              <div class="related-content">
                <div class="related-title">{{ related.title }}</div>
                <div v-if="related.publishedAt" class="related-date">
                  {{ displayDate(related.publishedAt) }}
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <!-- Latest News -->
        <div v-if="latestNews.length > 0" class="sidebar-box">
          <h3 class="sidebar-title">最新消息</h3>
          <div class="latest-list">
            <router-link
              v-for="(latest, index) in latestNews"
              :key="latest.id"
              :to="`/shop/news/${latest.slug}`"
              class="latest-item"
            >
              <div class="latest-number">{{ index + 1 }}</div>
              <div class="latest-content">
                <div class="latest-title">{{ latest.title }}</div>
                <div v-if="latest.publishedAt" class="latest-date">
                  {{ displayDate(latest.publishedAt) }}
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <!-- Back to List -->
        <div class="sidebar-box back-box">
          <q-btn
            unelevated
            color="primary"
            icon="list"
            label="返回消息列表"
            class="full-width"
            size="lg"
            to="/shop/news"
          />
        </div>
      </aside>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useQuasar, copyToClipboard } from 'quasar';
import { getPostBySlug, getPublishedIndex, parseTags } from 'src/api/blog.js';
import { sanitizeHtml } from 'src/utils/sanitize.js';
import { formatDate } from 'src/utils/format.js';

const route = useRoute();
const $q = useQuasar();

const article = ref(null);
const loading = ref(false);
const notFound = ref(false);
const errorMessage = ref('');
const coverBroken = ref(false);

// 已發布文章索引（新到舊），用於上一篇 / 下一篇 / 相關文章
const publishedIndex = ref([]);
const brokenImages = ref({});

const displayDate = value => (value ? formatDate(value) : '');

const markBroken = id => {
  brokenImages.value = { ...brokenImages.value, [id]: true };
};

const breadcrumbLabel = computed(() => {
  if (loading.value) return '載入中…';
  if (notFound.value) return '文章不存在';
  return article.value?.title || '文章';
});

const tagList = computed(() => parseTags(article.value?.tags));

const safeContent = computed(() => sanitizeHtml(article.value?.content));

const currentIndex = computed(() =>
  article.value
    ? publishedIndex.value.findIndex(post => post.id === article.value.id)
    : -1
);

// 上一篇：發布時間較早的文章
const prevArticle = computed(() =>
  currentIndex.value === -1
    ? null
    : publishedIndex.value[currentIndex.value + 1] || null
);

// 下一篇：發布時間較新的文章
const nextArticle = computed(() =>
  currentIndex.value > 0 ? publishedIndex.value[currentIndex.value - 1] : null
);

// 相關文章：有共同標籤者，依共同標籤數量排序
const relatedNews = computed(() => {
  if (!article.value || tagList.value.length === 0) return [];
  return publishedIndex.value
    .filter(post => post.id !== article.value.id)
    .map(post => ({
      post,
      score: parseTags(post.tags).filter(tag => tagList.value.includes(tag))
        .length,
    }))
    .filter(item => item.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, 3)
    .map(item => item.post);
});

// 最新文章（排除目前文章）
const latestNews = computed(() =>
  publishedIndex.value
    .filter(post => post.id !== article.value?.id)
    .slice(0, 5)
);

// 載入文章（以序號避免快速切換文章時舊請求覆蓋新結果）
let loadSeq = 0;
const loadArticle = async () => {
  const slug = route.params.slug;
  const seq = ++loadSeq;
  loading.value = true;
  notFound.value = false;
  errorMessage.value = '';
  coverBroken.value = false;
  article.value = null;

  try {
    const res = await getPostBySlug(slug, { silent: true });
    if (seq !== loadSeq) return;
    if (!res?.data) {
      notFound.value = true;
      return;
    }
    article.value = res.data;
    window.scrollTo({ top: 0 });
  } catch (error) {
    if (seq !== loadSeq) return;
    const status = error.response?.status;
    if (status === 400 || status === 404) {
      notFound.value = true;
    } else {
      errorMessage.value = error.displayMessage || '請稍後再試';
    }
  } finally {
    if (seq === loadSeq) loading.value = false;
  }

  // 側欄與上下篇資料載入失敗時不影響文章本身，僅隱藏相關區塊
  try {
    const index = await getPublishedIndex();
    if (seq === loadSeq) publishedIndex.value = index;
  } catch {
    if (seq === loadSeq) publishedIndex.value = [];
  }
};

// 分享到 Facebook
const shareToFacebook = () => {
  const url = encodeURIComponent(window.location.href);
  window.open(
    `https://www.facebook.com/sharer/sharer.php?u=${url}`,
    '_blank',
    'noopener'
  );
};

// 分享到 LINE
const shareToLine = () => {
  const url = encodeURIComponent(window.location.href);
  const text = encodeURIComponent(article.value?.title || '');
  window.open(
    `https://line.me/R/msg/text/?${text}%0A${url}`,
    '_blank',
    'noopener'
  );
};

// 複製連結
const copyLink = () => {
  copyToClipboard(window.location.href)
    .then(() => {
      $q.notify({
        message: '連結已複製到剪貼簿',
        color: 'positive',
        position: 'top',
        timeout: 2000,
      });
    })
    .catch(() => {
      $q.notify({
        message: '無法複製連結，請手動複製網址',
        color: 'warning',
        position: 'top',
        timeout: 2000,
      });
    });
};

// 路由參數改變（例如點選上一篇 / 下一篇）時重新載入
watch(
  () => route.params.slug,
  slug => {
    if (slug && route.path.startsWith('/shop/news/')) loadArticle();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.shop-news-detail-page {
  background: $shop-bg;
  padding-bottom: 60px;
}

// Breadcrumb
.breadcrumb-container {
  background: white;
  padding: 20px 0;
  border-bottom: 1px solid $shop-border;
}

.breadcrumb-wrapper {
  max-width: $shop-container-max-width;
  margin: 0 auto;
  padding: 0 20px;
  min-width: 0;

  :deep(.q-breadcrumbs) {
    flex-wrap: wrap;
  }
}

.breadcrumb-current {
  max-width: 60vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// Article Container
.article-container {
  max-width: $shop-container-max-width;
  margin: 40px auto 0;
  padding: 0 20px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 40px;
}

// Article Main
.article-main {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: $shop-shadow-md;
  min-width: 0;
}

// State（文章不存在 / 載入失敗）
.state-container {
  max-width: $shop-container-max-width;
  margin: 40px auto 0;
  padding: 0 20px;
}

.state-box {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: $shop-shadow-md;
}

.state-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: $shop-text;
  margin: 20px 0 8px;
  line-height: 1.4;
}

.state-text {
  color: $shop-text-secondary;
  margin: 0 0 24px;
}

.state-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

// Article Header
.article-header {
  margin-bottom: 32px;
}

.article-title {
  font-size: 2rem;
  font-weight: 700;
  color: $shop-text;
  line-height: 1.4;
  margin: 0 0 20px 0;
  overflow-wrap: anywhere;
}

.article-meta {
  display: flex;
  gap: 8px 24px;
  flex-wrap: wrap;
  font-size: 0.9rem;
  color: $shop-text-secondary;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

// Featured Image
.article-featured-image {
  margin: 32px 0;
  border-radius: 12px;
  overflow: hidden;

  img {
    width: 100%;
    height: auto;
    display: block;
  }
}

// Article Body
.article-body {
  font-size: 1.05rem;
  line-height: 1.8;
  color: $shop-text;
  margin-bottom: 40px;
  overflow-wrap: anywhere;

  :deep(h2),
  :deep(h3) {
    font-size: 1.5rem;
    font-weight: 600;
    line-height: 1.4;
    margin: 32px 0 16px 0;
    color: $shop-text;
  }

  :deep(p) {
    margin: 16px 0;
  }

  :deep(img),
  :deep(video),
  :deep(iframe) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
  }

  :deep(table) {
    display: block;
    max-width: 100%;
    overflow-x: auto;
    border-collapse: collapse;
  }

  :deep(a) {
    color: $shop-primary;
  }

  :deep(ul),
  :deep(ol) {
    margin: 16px 0;
    padding-left: 24px;

    li {
      margin: 12px 0;
      line-height: 1.8;
    }
  }
}

// Article Tags
.article-tags {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 0;
  border-top: 1px solid $shop-border;
  border-bottom: 1px solid $shop-border;
  margin-bottom: 32px;
}

.tags-icon {
  color: $shop-text-secondary;
  flex-shrink: 0;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  min-width: 0;
}

// Share Buttons
.article-share {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: $shop-bg-light;
  border-radius: 12px;
  margin-bottom: 32px;
}

.share-title {
  font-weight: 600;
  color: $shop-text;
}

.share-buttons {
  display: flex;
  gap: 12px;
}

// Article Navigation
.article-navigation {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 20px;
  margin-top: 40px;
}

.nav-item {
  display: block;
  padding: 20px;
  background: $shop-bg-light;
  border-radius: 12px;
  color: $shop-text;
  text-decoration: none;
  transition: all $shop-transition;
  min-width: 0;

  &:hover:not(.nav-disabled) {
    background: $shop-primary;
    color: white;

    .nav-label {
      color: white;
    }
  }

  &.nav-disabled {
    opacity: 0.5;
    text-align: center;

    .nav-label {
      justify-content: center;
      margin-bottom: 0;
    }
  }
}

.nav-prev {
  text-align: left;
}

.nav-next {
  text-align: right;
}

.nav-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: $shop-text-secondary;
}

.nav-next .nav-label {
  justify-content: flex-end;
}

.nav-title {
  font-size: 1rem;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// Sidebar
.article-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
  min-width: 0;
}

.sidebar-box {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: $shop-shadow-md;

  &.back-box {
    background: linear-gradient(
      135deg,
      $shop-primary 0%,
      $shop-primary-dark 100%
    );
    padding: 20px;

    .q-btn {
      color: white;
    }
  }
}

.sidebar-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: $shop-text;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid $shop-bg-light;
}

// Related News
.related-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.related-item {
  display: flex;
  gap: 12px;
  border-radius: 8px;
  overflow: hidden;
  color: inherit;
  text-decoration: none;
  transition: all $shop-transition;

  &:hover {
    background: $shop-bg-light;

    .related-title {
      color: $shop-primary;
    }
  }
}

.related-image {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.related-image-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: linear-gradient(
    135deg,
    rgba($shop-primary, 0.75) 0%,
    $shop-primary-dark 100%
  );
}

.related-content {
  flex: 1;
  min-width: 0;
  padding: 8px 0;
}

.related-title {
  font-size: 0.95rem;
  font-weight: 500;
  color: $shop-text;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  transition: color $shop-transition;
}

.related-date {
  font-size: 0.8rem;
  color: $shop-text-secondary;
}

// Latest News
.latest-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.latest-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  color: inherit;
  text-decoration: none;
  transition: all $shop-transition;

  &:hover {
    background: $shop-bg-light;

    .latest-title {
      color: $shop-primary;
    }
  }
}

.latest-number {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $shop-primary;
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 0.9rem;
}

.latest-content {
  flex: 1;
  min-width: 0;
}

.latest-title {
  font-size: 0.9rem;
  font-weight: 500;
  color: $shop-text;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  transition: color $shop-transition;
}

.latest-date {
  font-size: 0.75rem;
  color: $shop-text-secondary;
}

// Responsive
@media (max-width: 1024px) {
  .article-container {
    grid-template-columns: minmax(0, 1fr);
  }

  .article-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 20px;

    .sidebar-box {
      margin-bottom: 0;
    }

    .back-box {
      grid-column: 1 / -1;
    }
  }
}

@media (max-width: 768px) {
  .article-container,
  .state-container {
    margin-top: 24px;
    padding: 0 16px;
  }

  .breadcrumb-wrapper {
    padding: 0 16px;
  }

  .article-main {
    padding: 24px 20px;
  }

  .article-title {
    font-size: 1.5rem;
  }

  .article-navigation {
    grid-template-columns: minmax(0, 1fr);
  }

  .article-sidebar {
    grid-template-columns: minmax(0, 1fr);
  }

  .article-share {
    flex-direction: column;
    align-items: flex-start;
  }

  .state-box {
    padding: 56px 20px;
  }
}
</style>
