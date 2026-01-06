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
        <div class="news-layout">
          <!-- Sidebar Categories -->
          <aside class="news-sidebar">
            <div class="category-box">
              <h3 class="category-title">消息分類</h3>
              <q-list class="category-list">
                <q-item
                  v-for="category in categories"
                  :key="category.id"
                  clickable
                  v-ripple
                  :active="selectedCategory === category.id"
                  @click="selectCategory(category.id)"
                  active-class="category-active"
                >
                  <q-item-section avatar>
                    <q-icon :name="category.icon" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ category.name }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-badge :color="category.color" :label="category.count" />
                  </q-item-section>
                </q-item>
              </q-list>
            </div>

            <!-- Popular News -->
            <div class="popular-box">
              <h3 class="popular-title">熱門文章</h3>
              <div class="popular-list">
                <div
                  v-for="popular in popularNews"
                  :key="popular.id"
                  class="popular-item"
                  @click="goToDetail(popular.id)"
                >
                  <div class="popular-number">{{ popular.rank }}</div>
                  <div class="popular-content">
                    <div class="popular-title-text">{{ popular.title }}</div>
                    <div class="popular-date">{{ popular.date }}</div>
                  </div>
                </div>
              </div>
            </div>
          </aside>

          <!-- News List -->
          <div class="news-main">
            <!-- Filter Bar -->
            <div class="news-filter">
              <div class="filter-info">
                共
                <span class="highlight">{{ filteredNews.length }}</span> 則消息
              </div>
              <div class="filter-controls">
                <q-select
                  v-model="sortBy"
                  :options="sortOptions"
                  outlined
                  dense
                  label="排序方式"
                  style="min-width: 150px"
                />
              </div>
            </div>

            <!-- News Cards -->
            <div class="news-grid">
              <div
                v-for="news in paginatedNews"
                :key="news.id"
                class="news-card"
                @click="goToDetail(news.id)"
              >
                <div class="news-image">
                  <img :src="news.image" :alt="news.title" />
                  <div class="news-badge" :class="`badge-${news.category}`">
                    {{ getCategoryName(news.category) }}
                  </div>
                </div>
                <div class="news-body">
                  <div class="news-meta">
                    <span class="news-date">
                      <q-icon name="event" size="16px" />
                      {{ news.date }}
                    </span>
                    <span class="news-views">
                      <q-icon name="visibility" size="16px" />
                      {{ news.views }}
                    </span>
                  </div>
                  <h3 class="news-title">{{ news.title }}</h3>
                  <p class="news-excerpt">{{ news.excerpt }}</p>
                  <div class="news-footer">
                    <div class="news-tags">
                      <q-chip
                        v-for="tag in news.tags"
                        :key="tag"
                        size="sm"
                        dense
                        color="grey-3"
                        text-color="grey-7"
                      >
                        {{ tag }}
                      </q-chip>
                    </div>
                    <q-btn
                      flat
                      dense
                      label="閱讀更多"
                      icon-right="arrow_forward"
                      color="primary"
                      class="read-more-btn"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- Empty State -->
            <div v-if="filteredNews.length === 0" class="empty-state">
              <q-icon name="article" size="80px" color="grey-4" />
              <p>目前沒有相關消息</p>
            </div>

            <!-- Pagination -->
            <div v-if="filteredNews.length > 0" class="news-pagination">
              <q-pagination
                v-model="currentPage"
                :max="totalPages"
                :max-pages="7"
                direction-links
                boundary-links
                color="primary"
                active-design="unelevated"
                active-color="primary"
                active-text-color="white"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 分類資料
const categories = ref([
  { id: 'all', name: '全部消息', icon: 'apps', color: 'grey-7', count: 12 },
  {
    id: 'promotion',
    name: '優惠活動',
    icon: 'local_offer',
    color: 'red',
    count: 5,
  },
  {
    id: 'news',
    name: '最新公告',
    icon: 'campaign',
    color: 'blue',
    count: 4,
  },
  {
    id: 'product',
    name: '新品上市',
    icon: 'new_releases',
    color: 'orange',
    count: 3,
  },
]);

// 選中的分類
const selectedCategory = ref('all');

// 排序選項
const sortBy = ref({ label: '最新發布', value: 'latest' });
const sortOptions = [
  { label: '最新發布', value: 'latest' },
  { label: '最多瀏覽', value: 'views' },
  { label: '最舊優先', value: 'oldest' },
];

// 分頁
const currentPage = ref(1);
const itemsPerPage = 6;

// 假資料 - 最新消息
const newsList = ref([
  {
    id: 1,
    category: 'promotion',
    title: '雙12購物節限時優惠！全館商品最低5折起',
    excerpt:
      '年度最大優惠活動開跑！精選商品下殺5折，滿千免運，滿額再送精美禮品。數量有限，售完為止！',
    image: 'https://images.unsplash.com/photo-1607083206968-13611e3d76db?w=800',
    date: '2024-12-10',
    views: 1234,
    tags: ['限時優惠', '雙12'],
  },
  {
    id: 2,
    category: 'product',
    title: '日本直送！冬季限定草莓甜點系列新登場',
    excerpt:
      '引進日本冬季限定草莓系列商品，包含草莓大福、草莓蛋糕捲等多款人氣甜點，限量供應中！',
    image: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=800',
    date: '2024-12-08',
    views: 892,
    tags: ['新品上市', '草莓季'],
  },
  {
    id: 3,
    category: 'news',
    title: '12月份門市營業時間調整公告',
    excerpt:
      '因應年末活動，12月份營業時間調整為10:00-21:00，歡迎舊雨新知蒞臨選購。',
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800',
    date: '2024-12-05',
    views: 567,
    tags: ['營業公告'],
  },
  {
    id: 4,
    category: 'promotion',
    title: '會員專屬！單筆消費滿額送購物金',
    excerpt:
      '即日起至月底，會員單筆消費滿2000元即贈200元購物金，滿3000元贈500元，快來選購！',
    image: 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800',
    date: '2024-12-03',
    views: 1056,
    tags: ['會員優惠', '購物金'],
  },
  {
    id: 5,
    category: 'product',
    title: '冬季保暖系列商品到貨！日本進口羊毛圍巾',
    excerpt:
      '精選日本高品質羊毛圍巾，多種顏色可選，柔軟舒適保暖，送禮自用兩相宜。',
    image: 'https://images.unsplash.com/photo-1520769945061-0a448c463865?w=800',
    date: '2024-12-01',
    views: 743,
    tags: ['冬季新品', '保暖商品'],
  },
  {
    id: 6,
    category: 'news',
    title: '感謝有您！遇日小舖週年慶特別活動預告',
    excerpt:
      '感謝各位顧客一年來的支持，週年慶將推出多項驚喜活動與超值優惠，敬請期待！',
    image: 'https://images.unsplash.com/photo-1513885535751-8b9238bd345a?w=800',
    date: '2024-11-28',
    views: 1345,
    tags: ['週年慶', '感謝祭'],
  },
  {
    id: 7,
    category: 'promotion',
    title: '黑色星期五！精選商品買一送一',
    excerpt: '黑色星期五特別企劃，精選人氣商品買一送一，數量有限，手刀搶購！',
    image: 'https://images.unsplash.com/photo-1607082349566-187342175e2f?w=800',
    date: '2024-11-24',
    views: 2103,
    tags: ['黑色星期五', '買一送一'],
  },
  {
    id: 8,
    category: 'product',
    title: '聖誕限定包裝禮盒開放預購',
    excerpt: '精美聖誕禮盒包裝服務開放預購，讓您的心意更添溫馨，送禮最佳選擇！',
    image: 'https://images.unsplash.com/photo-1512909006721-3d6018887383?w=800',
    date: '2024-11-20',
    views: 634,
    tags: ['聖誕限定', '禮盒'],
  },
  {
    id: 9,
    category: 'news',
    title: '新增LINE官方帳號，加入好友享專屬優惠',
    excerpt:
      '立即加入遇日小舖LINE官方帳號，隨時掌握最新優惠與活動資訊，首次加入再送50元購物金！',
    image: 'https://images.unsplash.com/photo-1611162616475-46b635cb6868?w=800',
    date: '2024-11-15',
    views: 821,
    tags: ['LINE好友', '社群'],
  },
  {
    id: 10,
    category: 'promotion',
    title: '週末限定！指定商品第二件5折',
    excerpt: '每週末限定優惠，指定商品第二件享5折優惠，多買多划算，快來選購！',
    image: 'https://images.unsplash.com/photo-1567401893414-76b7b1e5a7a5?w=800',
    date: '2024-11-10',
    views: 945,
    tags: ['週末優惠', '第二件5折'],
  },
  {
    id: 11,
    category: 'news',
    title: '物流配送說明與注意事項',
    excerpt: '為提供更好的服務品質，配送時間與注意事項請詳閱本公告內容。',
    image: 'https://images.unsplash.com/photo-1566576721346-d4a3b4eaeb55?w=800',
    date: '2024-11-05',
    views: 456,
    tags: ['物流公告'],
  },
  {
    id: 12,
    category: 'product',
    title: '秋冬新品！日本職人手作陶瓷餐具系列',
    excerpt:
      '由日本知名職人手工製作，每件都是獨一無二的藝術品，為您的餐桌增添質感。',
    image: 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=800',
    date: '2024-10-28',
    views: 712,
    tags: ['日本職人', '陶瓷餐具'],
  },
]);

// 熱門文章
const popularNews = ref([
  { id: 7, rank: 1, title: '黑色星期五！精選商品買一送一', date: '2024-11-24' },
  {
    id: 6,
    rank: 2,
    title: '感謝有您！遇日小舖週年慶特別活動預告',
    date: '2024-11-28',
  },
  {
    id: 1,
    rank: 3,
    title: '雙12購物節限時優惠！全館商品最低5折起',
    date: '2024-12-10',
  },
  {
    id: 4,
    rank: 4,
    title: '會員專屬！單筆消費滿額送購物金',
    date: '2024-12-03',
  },
  { id: 10, rank: 5, title: '週末限定！指定商品第二件5折', date: '2024-11-10' },
]);

// 過濾後的消息
const filteredNews = computed(() => {
  let filtered =
    selectedCategory.value === 'all'
      ? newsList.value
      : newsList.value.filter(news => news.category === selectedCategory.value);

  // 排序
  if (sortBy.value.value === 'latest') {
    filtered = filtered.sort((a, b) => new Date(b.date) - new Date(a.date));
  } else if (sortBy.value.value === 'oldest') {
    filtered = filtered.sort((a, b) => new Date(a.date) - new Date(b.date));
  } else if (sortBy.value.value === 'views') {
    filtered = filtered.sort((a, b) => b.views - a.views);
  }

  return filtered;
});

// 分頁後的消息
const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredNews.value.slice(start, end);
});

// 總頁數
const totalPages = computed(() => {
  return Math.ceil(filteredNews.value.length / itemsPerPage);
});

// 選擇分類
const selectCategory = categoryId => {
  selectedCategory.value = categoryId;
  currentPage.value = 1;
};

// 獲取分類名稱
const getCategoryName = categoryId => {
  const category = categories.value.find(cat => cat.id === categoryId);
  return category ? category.name : '';
};

// 前往詳情頁
const goToDetail = id => {
  router.push(`/shop/news/${id}`);
};
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 16px 0;
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.news-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 40px;
}

// Sidebar
.news-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.category-box,
.popular-box {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.category-title,
.popular-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: $shop-text;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid $shop-bg-light;
}

.category-list {
  .q-item {
    border-radius: 8px;
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.category-active {
  background: rgba($shop-primary, 0.1) !important;
  color: $shop-primary !important;

  .q-icon {
    color: $shop-primary !important;
  }
}

// Popular News
.popular-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.popular-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: $shop-bg-light;
  }
}

.popular-number {
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

.popular-content {
  flex: 1;
  min-width: 0;
}

.popular-title-text {
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
}

.popular-date {
  font-size: 0.75rem;
  color: $shop-text-secondary;
}

// News Main
.news-main {
  min-width: 0;
}

// Filter Bar
.news-filter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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

// News Grid
.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin-bottom: 40px;
}

.news-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

    .news-image img {
      transform: scale(1.1);
    }
  }
}

.news-image {
  position: relative;
  height: 200px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.news-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;

  &.badge-promotion {
    background: $shop-danger;
  }

  &.badge-news {
    background: $shop-secondary;
  }

  &.badge-product {
    background: $shop-warning;
  }
}

.news-body {
  padding: 24px;
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
}

.news-excerpt {
  font-size: 0.95rem;
  color: $shop-text-secondary;
  line-height: 1.6;
  margin: 0 0 16px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid $shop-bg-light;
}

.news-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.read-more-btn {
  flex-shrink: 0;
}

// Empty State
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: $shop-text-secondary;

  p {
    font-size: 1.1rem;
    margin-top: 20px;
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
  .news-layout {
    grid-template-columns: 1fr;
  }

  .news-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .news-sidebar {
    grid-template-columns: 1fr;
  }

  .news-filter {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}
</style>
