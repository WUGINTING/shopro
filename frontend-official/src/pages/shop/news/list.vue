<template>
  <q-page class="shop-news-list-page">
    <!-- Page Header -->
    <section class="news-header">
      <div class="news-header-content">
        <h1 class="page-title">最新消息</h1>
        <p class="page-subtitle">掌握第一手優惠與活動資訊</p>
      </div>
    </section>

    <!-- Main Content -->
    <section class="news-content">
      <div class="news-container">
        <!-- Filter Bar -->
        <div v-if="!loading && !errorMessage && posts.length > 0" class="news-filter">
          <div class="filter-info">
            共 <span class="highlight">{{ totalElements }}</span> 則消息
            <span v-if="totalPages > 1" class="filter-page">
              （第 {{ currentPage }} / {{ totalPages }} 頁）
            </span>
          </div>

          <!-- 標籤篩選：依本頁文章的標籤產生 -->
          <div v-if="pageTags.length > 0" class="tag-filter">
            <span class="tag-filter-label">本頁標籤</span>
            <div class="tag-filter-chips">
              <q-chip
                clickable
                dense
                :outline="selectedTag !== ''"
                :color="selectedTag === '' ? 'primary' : 'grey-7'"
                :text-color="selectedTag === '' ? 'white' : 'grey-8'"
                @click="selectedTag = ''"
              >
                全部
              </q-chip>
              <q-chip
                v-for="tag in pageTags"
                :key="tag"
                clickable
                dense
                :outline="selectedTag !== tag"
                :color="selectedTag === tag ? 'primary' : 'grey-7'"
                :text-color="selectedTag === tag ? 'white' : 'grey-8'"
                @click="selectedTag = tag"
              >
                {{ tag }}
              </q-chip>
            </div>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="news-grid">
          <div v-for="n in 6" :key="n" class="news-card news-card-skeleton">
            <q-skeleton height="200px" square />
            <div class="news-body">
              <q-skeleton type="text" width="40%" />
              <q-skeleton type="text" class="text-h6" />
              <q-skeleton type="text" />
              <q-skeleton type="text" width="70%" />
            </div>
          </div>
        </div>

        <!-- Error -->
        <div v-else-if="errorMessage" class="state-box">
          <q-icon name="cloud_off" size="72px" color="grey-4" />
          <p>{{ errorMessage }}</p>
          <q-btn
            unelevated
            color="primary"
            icon="refresh"
            label="重新載入"
            @click="loadPosts"
          />
        </div>

        <!-- Empty -->
        <div v-else-if="posts.length === 0" class="state-box">
          <q-icon name="article" size="72px" color="grey-4" />
          <p>目前還沒有最新消息，敬請期待</p>
          <q-btn
            outline
            color="primary"
            label="逛逛商品"
            to="/shop/product/list?category=all"
          />
        </div>

        <template v-else>
          <!-- News Cards -->
          <div class="news-grid">
            <router-link
              v-for="post in visiblePosts"
              :key="post.id"
              :to="`/shop/news/${post.slug}`"
              class="news-card"
            >
              <div class="news-image">
                <img
                  v-if="post.coverImageUrl && !brokenImages[post.id]"
                  :src="post.coverImageUrl"
                  :alt="post.title"
                  loading="lazy"
                  @error="markBroken(post.id)"
                />
                <div v-else class="news-image-fallback">
                  <q-icon name="campaign" size="56px" />
                </div>
              </div>
              <div class="news-body">
                <div class="news-meta">
                  <span v-if="post.publishedAt" class="news-date">
                    <q-icon name="event" size="16px" />
                    {{ displayDate(post.publishedAt) }}
                  </span>
                  <span v-if="post.viewCount != null" class="news-views">
                    <q-icon name="visibility" size="16px" />
                    {{ post.viewCount }}
                  </span>
                </div>
                <h3 class="news-title">{{ post.title }}</h3>
                <p v-if="post.excerpt" class="news-excerpt">{{ post.excerpt }}</p>
                <div class="news-footer">
                  <div class="news-tags">
                    <q-chip
                      v-for="tag in post.tagList.slice(0, 3)"
                      :key="tag"
                      size="sm"
                      dense
                      color="grey-3"
                      text-color="grey-7"
                    >
                      {{ tag }}
                    </q-chip>
                  </div>
                  <span class="read-more">
                    閱讀更多
                    <q-icon name="arrow_forward" size="16px" />
                  </span>
                </div>
              </div>
            </router-link>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="news-pagination">
            <q-pagination
              :model-value="currentPage"
              :max="totalPages"
              :max-pages="5"
              direction-links
              boundary-links
              color="primary"
              active-design="unelevated"
              active-color="primary"
              active-text-color="white"
              @update:model-value="changePage"
            />
          </div>
        </template>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  getPublishedPosts,
  sortByPublishedDesc,
  parseTags,
  htmlToExcerpt,
} from 'src/api/blog.js';
import { formatDate } from 'src/utils/format.js';

const route = useRoute();
const router = useRouter();

// 每頁顯示數量
const PAGE_SIZE = 9;

const posts = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const loading = ref(false);
const errorMessage = ref('');
const selectedTag = ref('');
const brokenImages = ref({});

// 目前頁碼（網址 ?page= 為 1 起算）
const currentPage = computed(() => {
  const page = parseInt(route.query.page, 10);
  return Number.isFinite(page) && page > 0 ? page : 1;
});

// 本頁文章出現過的標籤
const pageTags = computed(() => {
  const tags = new Set();
  posts.value.forEach(post => post.tagList.forEach(tag => tags.add(tag)));
  return Array.from(tags);
});

// 套用標籤篩選後顯示的文章
const visiblePosts = computed(() =>
  selectedTag.value
    ? posts.value.filter(post => post.tagList.includes(selectedTag.value))
    : posts.value
);

const displayDate = value => (value ? formatDate(value) : '');

const markBroken = id => {
  brokenImages.value = { ...brokenImages.value, [id]: true };
};

// 載入目前頁的已發布文章
const loadPosts = async () => {
  loading.value = true;
  errorMessage.value = '';
  selectedTag.value = '';
  try {
    const res = await getPublishedPosts(
      { page: currentPage.value - 1, size: PAGE_SIZE },
      { silent: true }
    );
    const page = res?.data || {};
    // 後端未指定排序，於本頁內依發布時間由新到舊排列
    posts.value = sortByPublishedDesc(page.content || []).map(post => ({
      ...post,
      tagList: parseTags(post.tags),
      excerpt: post.summary || htmlToExcerpt(post.content, 90),
    }));
    totalElements.value = page.totalElements ?? posts.value.length;
    totalPages.value = page.totalPages ?? 1;

    // 頁碼超出範圍時回到第一頁
    if (posts.value.length === 0 && currentPage.value > 1) {
      router.replace({ query: { ...route.query, page: undefined } });
    }
  } catch (error) {
    posts.value = [];
    errorMessage.value = error.displayMessage || '最新消息載入失敗，請稍後再試';
  } finally {
    loading.value = false;
  }
};

// 切換頁碼
const changePage = page => {
  router.push({ query: { ...route.query, page: page > 1 ? page : undefined } });
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 頁碼改變時重新載入（離開本頁時路由也會變動，需略過）
const LIST_PATH = route.path;
watch(
  () => route.query.page,
  () => {
    if (route.path === LIST_PATH) loadPosts();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
@import '../../../css/variables.scss';

.shop-news-list-page {
  background: $shop-bg;
}

// Header
.news-header {
  background: linear-gradient(
    135deg,
    $shop-primary 0%,
    $shop-primary-dark 100%
  );
  padding: 60px 0;
  text-align: center;
  color: white;
}

.news-header-content {
  max-width: $shop-container-max-width;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.page-subtitle {
  font-size: 1.1rem;
  margin: 0;
  opacity: 0.95;
}

// Main Content
.news-content {
  padding: 60px 0;
}

.news-container {
  max-width: $shop-container-max-width;
  margin: 0 auto;
  padding: 0 20px;
}

// Filter Bar
.news-filter {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: $shop-shadow-sm;
}

.filter-info {
  font-size: 1rem;
  color: $shop-text-secondary;

  .highlight {
    color: $shop-primary;
    font-weight: 700;
    font-size: 1.2rem;
  }
}

.filter-page {
  font-size: 0.9rem;
}

.tag-filter {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.tag-filter-label {
  flex-shrink: 0;
  font-size: 0.9rem;
  color: $shop-text-secondary;
  line-height: 28px;
}

.tag-filter-chips {
  display: flex;
  flex-wrap: wrap;
  min-width: 0;

  .q-chip {
    max-width: 100%;
  }
}

// News Grid
.news-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.news-card {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: $shop-shadow-md;
  color: inherit;
  text-decoration: none;
  transition: transform $shop-transition, box-shadow $shop-transition;

  &:not(.news-card-skeleton):hover {
    transform: translateY(-6px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

    .news-image img {
      transform: scale(1.06);
    }

    .news-title {
      color: $shop-primary;
    }
  }
}

.news-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: $shop-bg-light;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.news-image-fallback {
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

.news-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.news-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  font-size: 0.85rem;
  color: $shop-text-secondary;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.news-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: $shop-text;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  overflow-wrap: anywhere;
  transition: color $shop-transition;
}

.news-excerpt {
  font-size: 0.95rem;
  color: $shop-text-secondary;
  line-height: 1.6;
  margin: 0 0 16px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow-wrap: anywhere;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-top: 16px;
  margin-top: auto;
  border-top: 1px solid $shop-bg-light;
}

.news-tags {
  display: flex;
  flex-wrap: wrap;
  min-width: 0;
}

.read-more {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  color: $shop-primary;
}

// 狀態區塊（錯誤 / 無資料）
.state-box {
  text-align: center;
  padding: 80px 20px;
  color: $shop-text-secondary;

  p {
    font-size: 1.1rem;
    margin: 20px 0 24px;
  }
}

// Pagination
.news-pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

// Responsive
@media (max-width: 1024px) {
  .news-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .news-header {
    padding: 40px 0;
  }

  .page-title {
    font-size: 2rem;
  }

  .news-content {
    padding: 32px 0;
  }

  .news-container {
    padding: 0 16px;
  }

  .news-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .tag-filter {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
