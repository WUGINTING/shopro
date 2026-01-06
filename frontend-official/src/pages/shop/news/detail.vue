<template>
  <q-page class="shop-news-detail-page">
    <!-- Breadcrumb -->
    <div class="breadcrumb-container">
      <div class="breadcrumb-wrapper">
        <q-breadcrumbs separator="›" active-color="primary">
          <q-breadcrumbs-el label="首頁" to="/shop" />
          <q-breadcrumbs-el label="最新消息" to="/shop/news" />
          <q-breadcrumbs-el :label="newsData.title" />
        </q-breadcrumbs>
      </div>
    </div>

    <!-- Article Content -->
    <div class="article-container">
      <article class="article-main">
        <!-- Article Header -->
        <header class="article-header">
          <div
            class="article-category"
            :class="`category-${newsData.category}`"
          >
            {{ getCategoryName(newsData.category) }}
          </div>
          <h1 class="article-title">{{ newsData.title }}</h1>
          <div class="article-meta">
            <span class="meta-item">
              <q-icon name="event" size="18px" />
              {{ newsData.date }}
            </span>
            <span class="meta-item">
              <q-icon name="visibility" size="18px" />
              {{ newsData.views }} 次瀏覽
            </span>
            <span class="meta-item">
              <q-icon name="person" size="18px" />
              管理員
            </span>
          </div>
        </header>

        <!-- Featured Image -->
        <div class="article-featured-image">
          <img :src="newsData.image" :alt="newsData.title" />
        </div>

        <!-- Article Body -->
        <div class="article-body">
          <div v-html="newsData.content"></div>
        </div>

        <!-- Article Tags -->
        <div class="article-tags">
          <q-icon name="local_offer" size="20px" class="tags-icon" />
          <div class="tags-list">
            <q-chip
              v-for="tag in newsData.tags"
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
              @click="shareToFacebook"
            >
              <q-tooltip>分享到 Facebook</q-tooltip>
            </q-btn>
            <q-btn round color="info" icon="send" @click="shareToLine">
              <q-tooltip>分享到 LINE</q-tooltip>
            </q-btn>
            <q-btn round color="grey-7" icon="link" @click="copyLink">
              <q-tooltip>複製連結</q-tooltip>
            </q-btn>
          </div>
        </div>

        <!-- Navigation -->
        <div class="article-navigation">
          <div
            v-if="prevArticle"
            class="nav-item nav-prev"
            @click="goToArticle(prevArticle.id)"
          >
            <div class="nav-label">
              <q-icon name="chevron_left" size="24px" />
              上一篇
            </div>
            <div class="nav-title">{{ prevArticle.title }}</div>
          </div>
          <div v-else class="nav-item nav-disabled">
            <div class="nav-label">沒有更多文章了</div>
          </div>

          <div
            v-if="nextArticle"
            class="nav-item nav-next"
            @click="goToArticle(nextArticle.id)"
          >
            <div class="nav-label">
              下一篇
              <q-icon name="chevron_right" size="24px" />
            </div>
            <div class="nav-title">{{ nextArticle.title }}</div>
          </div>
          <div v-else class="nav-item nav-disabled">
            <div class="nav-label">沒有更多文章了</div>
          </div>
        </div>
      </article>

      <!-- Sidebar -->
      <aside class="article-sidebar">
        <!-- Related News -->
        <div class="sidebar-box">
          <h3 class="sidebar-title">相關文章</h3>
          <div class="related-list">
            <div
              v-for="related in relatedNews"
              :key="related.id"
              class="related-item"
              @click="goToArticle(related.id)"
            >
              <div class="related-image">
                <img :src="related.image" :alt="related.title" />
              </div>
              <div class="related-content">
                <div class="related-title">{{ related.title }}</div>
                <div class="related-date">{{ related.date }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Latest News -->
        <div class="sidebar-box">
          <h3 class="sidebar-title">最新消息</h3>
          <div class="latest-list">
            <div
              v-for="(latest, index) in latestNews"
              :key="latest.id"
              class="latest-item"
              @click="goToArticle(latest.id)"
            >
              <div class="latest-number">{{ index + 1 }}</div>
              <div class="latest-content">
                <div class="latest-title">{{ latest.title }}</div>
                <div class="latest-date">{{ latest.date }}</div>
              </div>
            </div>
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
            @click="goToList"
          />
        </div>
      </aside>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuasar } from 'quasar';

const router = useRouter();
const route = useRoute();
const $q = useQuasar();

// 假資料 - 所有文章
const allNews = [
  {
    id: 1,
    category: 'promotion',
    title: '雙12購物節限時優惠！全館商品最低5折起',
    excerpt:
      '年度最大優惠活動開跑！精選商品下殺5折，滿千免運，滿額再送精美禮品。',
    image: 'https://images.unsplash.com/photo-1607083206968-13611e3d76db?w=800',
    date: '2024-12-10',
    views: 1234,
    tags: ['限時優惠', '雙12'],
    content: `
      <p>親愛的顧客您好，</p>
      <p>一年一度的雙12購物節即將來臨！遇日小舖為了感謝各位長期以來的支持，特別推出年度最大優惠活動。</p>
      
      <h3>活動內容</h3>
      <ul>
        <li><strong>全館商品最低5折起</strong>：精選商品下殺5折，包含日本進口商品、手工甜點等</li>
        <li><strong>滿千免運</strong>：單筆消費滿1000元即享免運優惠</li>
        <li><strong>滿額贈禮</strong>：消費滿2000元贈送精美禮品一份</li>
        <li><strong>會員加碼</strong>：會員額外享有購物金回饋</li>
      </ul>
      
      <h3>活動時間</h3>
      <p>2024年12月10日 00:00 - 12月12日 23:59</p>
      
      <h3>注意事項</h3>
      <ul>
        <li>本活動限量商品售完為止</li>
        <li>不得與其他優惠併用</li>
        <li>詳細活動辦法請見官網公告</li>
      </ul>
      
      <p>機會難得，千萬不要錯過！立即前往選購您喜愛的商品吧！</p>
      <p>祝您購物愉快！</p>
    `,
  },
  {
    id: 2,
    category: 'product',
    title: '日本直送！冬季限定草莓甜點系列新登場',
    excerpt:
      '引進日本冬季限定草莓系列商品，包含草莓大福、草莓蛋糕捲等多款人氣甜點。',
    image: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=800',
    date: '2024-12-08',
    views: 892,
    tags: ['新品上市', '草莓季'],
    content: `
      <p>冬天就是草莓的季節！遇日小舖特別從日本引進冬季限定草莓甜點系列。</p>
      
      <h3>本次引進商品</h3>
      <ul>
        <li><strong>草莓大福</strong>：使用日本栃木縣產草莓，外皮Q彈，內餡綿密香甜</li>
        <li><strong>草莓蛋糕捲</strong>：新鮮草莓搭配輕盈鮮奶油，口感清爽不膩</li>
        <li><strong>草莓銅鑼燒</strong>：傳統日式點心結合草莓醬，別有一番風味</li>
        <li><strong>草莓巧克力</strong>：草莓乾裹上高級巧克力，酸甜完美平衡</li>
      </ul>
      
      <h3>產品特色</h3>
      <p>所有商品均使用日本產草莓製作，不添加人工色素與防腐劑，新鮮直送台灣。</p>
      
      <h3>購買資訊</h3>
      <p>限量供應中，欲購從速！可至門市或線上商店選購。</p>
      <p>線上購買滿500元即可享免運優惠。</p>
    `,
  },
  {
    id: 3,
    category: 'news',
    title: '12月份門市營業時間調整公告',
    excerpt: '因應年末活動，12月份營業時間調整為10:00-21:00。',
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800',
    date: '2024-12-05',
    views: 567,
    tags: ['營業公告'],
    content: `
      <p>親愛的顧客您好，</p>
      
      <h3>營業時間調整</h3>
      <p>因應年末購物旺季，遇日小舖將於12月份調整營業時間如下：</p>
      <ul>
        <li><strong>平日</strong>：10:00 - 21:00</li>
        <li><strong>假日</strong>：10:00 - 22:00</li>
      </ul>
      
      <h3>特殊日期公告</h3>
      <ul>
        <li><strong>12月25日（聖誕節）</strong>：正常營業</li>
        <li><strong>12月31日（跨年夜）</strong>：營業至 23:00</li>
        <li><strong>1月1日（元旦）</strong>：12:00 開始營業</li>
      </ul>
      
      <p>調整期間若造成不便，敬請見諒。歡迎舊雨新知蒞臨選購！</p>
    `,
  },
  {
    id: 4,
    category: 'promotion',
    title: '會員專屬！單筆消費滿額送購物金',
    excerpt: '會員單筆消費滿2000元即贈200元購物金，滿3000元贈500元。',
    image: 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800',
    date: '2024-12-03',
    views: 1056,
    tags: ['會員優惠', '購物金'],
    content: `
      <p>感謝會員長期支持，遇日小舖推出會員專屬回饋活動！</p>
      
      <h3>活動內容</h3>
      <p>即日起至12月底，會員單筆消費滿額即贈購物金：</p>
      <ul>
        <li>消費滿 <strong>2000元</strong> → 贈 <strong>200元</strong> 購物金</li>
        <li>消費滿 <strong>3000元</strong> → 贈 <strong>500元</strong> 購物金</li>
        <li>消費滿 <strong>5000元</strong> → 贈 <strong>1000元</strong> 購物金</li>
      </ul>
      
      <h3>購物金使用說明</h3>
      <ul>
        <li>購物金將於消費後7個工作天內發放至會員帳戶</li>
        <li>購物金使用期限為發放日起90天</li>
        <li>每筆訂單可使用購物金上限為訂單金額的30%</li>
      </ul>
      
      <p>快呼朋引伴一起來享受優惠吧！</p>
    `,
  },
  {
    id: 5,
    category: 'product',
    title: '冬季保暖系列商品到貨！日本進口羊毛圍巾',
    excerpt: '精選日本高品質羊毛圍巾，多種顏色可選，柔軟舒適保暖。',
    image: 'https://images.unsplash.com/photo-1520769945061-0a448c463865?w=800',
    date: '2024-12-01',
    views: 743,
    tags: ['冬季新品', '保暖商品'],
    content: `
      <p>寒冷的冬天來臨，遇日小舖為您準備了溫暖的冬季保暖系列商品。</p>
      
      <h3>日本進口羊毛圍巾</h3>
      <p>精選日本製高品質羊毛圍巾，觸感柔軟、保暖性極佳。</p>
      
      <h3>商品特色</h3>
      <ul>
        <li>100% 美麗諾羊毛</li>
        <li>多種經典配色可選</li>
        <li>尺寸：180cm x 30cm</li>
        <li>輕盈不厚重，好搭配</li>
        <li>日本製造，品質保證</li>
      </ul>
      
      <h3>配色選擇</h3>
      <ul>
        <li>經典灰</li>
        <li>優雅米</li>
        <li>時尚黑</li>
        <li>溫暖駝</li>
        <li>清新藍</li>
      </ul>
      
      <p>送禮自用兩相宜，歡迎來店試戴選購！</p>
    `,
  },
];

// 當前文章
const newsData = ref({});

// 分類對應
const categories = {
  promotion: '優惠活動',
  news: '最新公告',
  product: '新品上市',
};

// 相關文章（同分類）
const relatedNews = computed(() => {
  return allNews
    .filter(
      news =>
        news.category === newsData.value.category &&
        news.id !== newsData.value.id
    )
    .slice(0, 3);
});

// 最新文章
const latestNews = computed(() => {
  return allNews
    .filter(news => news.id !== newsData.value.id)
    .sort((a, b) => new Date(b.date) - new Date(a.date))
    .slice(0, 5);
});

// 上一篇
const prevArticle = computed(() => {
  const currentIndex = allNews.findIndex(news => news.id === newsData.value.id);
  return currentIndex > 0 ? allNews[currentIndex - 1] : null;
});

// 下一篇
const nextArticle = computed(() => {
  const currentIndex = allNews.findIndex(news => news.id === newsData.value.id);
  return currentIndex < allNews.length - 1 ? allNews[currentIndex + 1] : null;
});

// 獲取分類名稱
const getCategoryName = categoryId => {
  return categories[categoryId] || '';
};

// 前往文章
const goToArticle = id => {
  router.push(`/shop/news/${id}`);
  // 滾動到頂部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 返回列表
const goToList = () => {
  router.push('/shop/news');
};

// 分享到 Facebook
const shareToFacebook = () => {
  const url = encodeURIComponent(window.location.href);
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, '_blank');
};

// 分享到 LINE
const shareToLine = () => {
  const url = encodeURIComponent(window.location.href);
  const text = encodeURIComponent(newsData.value.title);
  window.open(`https://line.me/R/msg/text/?${text}%0A${url}`, '_blank');
};

// 複製連結
const copyLink = () => {
  navigator.clipboard.writeText(window.location.href).then(() => {
    $q.notify({
      message: '連結已複製到剪貼簿',
      color: 'positive',
      position: 'top',
      timeout: 2000,
    });
  });
};

// 載入文章
onMounted(() => {
  const newsId = parseInt(route.params.id);
  const article = allNews.find(news => news.id === newsId);

  if (article) {
    newsData.value = article;
    // 增加瀏覽次數（實際應該呼叫 API）
    newsData.value.views += 1;
  } else {
    // 文章不存在，導向列表
    router.push('/shop/news');
  }
});
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

// Article Container
.article-container {
  max-width: 1200px;
  margin: 40px auto 0;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 40px;
}

// Article Main
.article-main {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

// Article Header
.article-header {
  margin-bottom: 32px;
}

.article-category {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
  margin-bottom: 16px;

  &.category-promotion {
    background: $shop-danger;
  }

  &.category-news {
    background: $shop-secondary;
  }

  &.category-product {
    background: $shop-warning;
  }
}

.article-title {
  font-size: 2rem;
  font-weight: 700;
  color: $shop-text;
  line-height: 1.4;
  margin: 0 0 20px 0;
}

.article-meta {
  display: flex;
  gap: 24px;
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

  :deep(h3) {
    font-size: 1.5rem;
    font-weight: 600;
    margin: 32px 0 16px 0;
    color: $shop-text;
  }

  :deep(p) {
    margin: 16px 0;
  }

  :deep(ul) {
    margin: 16px 0;
    padding-left: 24px;

    li {
      margin: 12px 0;
      line-height: 1.8;

      strong {
        color: $shop-primary;
      }
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
}

.tags-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 40px;
}

.nav-item {
  padding: 20px;
  background: $shop-bg-light;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(.nav-disabled) {
    background: $shop-primary;
    color: white;
    transform: translateX(-4px);

    &.nav-next {
      transform: translateX(4px);
    }
  }

  &.nav-disabled {
    cursor: not-allowed;
    opacity: 0.5;
    text-align: center;
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

  .nav-item:hover & {
    color: white;
  }
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
}

.sidebar-box {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

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
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;

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
  transition: color 0.3s ease;
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
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;

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
  transition: color 0.3s ease;
}

.latest-date {
  font-size: 0.75rem;
  color: $shop-text-secondary;
}

// Responsive
@media (max-width: 1024px) {
  .article-container {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;

    .back-box {
      grid-column: 1 / -1;
    }
  }
}

@media (max-width: 768px) {
  .article-main {
    padding: 24px 20px;
  }

  .article-title {
    font-size: 1.5rem;
  }

  .article-navigation {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    grid-template-columns: 1fr;
  }

  .article-share {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
