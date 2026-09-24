<template>
  <q-page class="shop-cms-page">
    <!-- Breadcrumb -->
    <div class="breadcrumb-container">
      <div class="breadcrumb-wrapper">
        <q-breadcrumbs separator="›" active-color="primary">
          <q-breadcrumbs-el label="首頁" to="/shop" />
          <q-breadcrumbs-el :label="breadcrumbLabel" />
        </q-breadcrumbs>
      </div>
    </div>

    <div class="page-container">
      <!-- Loading -->
      <div v-if="loading" class="page-card">
        <q-skeleton type="text" class="text-h4" width="40%" />
        <q-skeleton type="text" width="70%" class="q-mb-lg" />
        <q-skeleton v-for="n in 6" :key="n" type="text" />
      </div>

      <!-- 頁面不存在 -->
      <div v-else-if="notFound" class="page-card state-box">
        <q-icon name="search_off" size="80px" color="grey-4" />
        <h1 class="state-title">頁面不存在</h1>
        <p class="state-text">您要找的頁面可能已移除或網址有誤。</p>
        <q-btn unelevated color="primary" icon="home" label="回到商城首頁" to="/shop" />
      </div>

      <!-- 載入失敗 -->
      <div v-else-if="errorMessage" class="page-card state-box">
        <q-icon name="cloud_off" size="80px" color="grey-4" />
        <h1 class="state-title">頁面載入失敗</h1>
        <p class="state-text">{{ errorMessage }}</p>
        <div class="state-actions">
          <q-btn
            unelevated
            color="primary"
            icon="refresh"
            label="重新載入"
            @click="loadPage"
          />
          <q-btn outline color="primary" label="回到商城首頁" to="/shop" />
        </div>
      </div>

      <!-- 頁面內容 -->
      <template v-else-if="page">
        <article class="page-card">
          <h1 class="page-title">{{ page.title }}</h1>
          <!-- 後台撰寫的 HTML（渲染前已清理）；站內連結改由 router 導頁 -->
          <div
            class="page-content"
            @click="handleContentClick"
            v-html="safeContent"
          ></div>
        </article>

        <!-- 說明頁附上客服聯絡方式（來自後台商店內容設定） -->
        <aside v-if="showContactCard" class="page-card contact-card">
          <h2 class="contact-title">
            <q-icon name="support_agent" size="24px" />
            聯絡客服
          </h2>
          <ul class="contact-list">
            <li v-if="storeContent.contactEmail">
              <q-icon name="email" size="18px" />
              <span class="contact-label">客服信箱</span>
              <a :href="`mailto:${storeContent.contactEmail}`">{{
                storeContent.contactEmail
              }}</a>
            </li>
            <li v-if="storeContent.contactPhone">
              <q-icon name="phone" size="18px" />
              <span class="contact-label">客服專線</span>
              <a :href="phoneHref">{{ storeContent.contactPhone }}</a>
            </li>
            <li v-if="storeContent.businessHours">
              <q-icon name="schedule" size="18px" />
              <span class="contact-label">服務時間</span>
              <span>{{ storeContent.businessHours }}</span>
            </li>
          </ul>
        </aside>
      </template>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getCustomPageBySlug } from 'src/api/customPage.js';
import { getStoreContent } from 'src/api/store.js';
import { sanitizeHtml } from 'src/utils/sanitize.js';
import { DEFAULT_SHOP_PAGES } from 'src/config/shopPageDefaults.js';

const route = useRoute();
const router = useRouter();

const page = ref(null);
const loading = ref(false);
const notFound = ref(false);
const errorMessage = ref('');
const storeContent = ref({});

const slug = computed(() => route.params.slug);

const breadcrumbLabel = computed(() => {
  if (loading.value) return '載入中…';
  if (notFound.value) return '頁面不存在';
  return page.value?.title || '頁面';
});

const safeContent = computed(() => sanitizeHtml(page.value?.content));

// 內建說明頁（隱私權、條款、退換貨、常見問題）才附上聯絡客服卡片
const showContactCard = computed(
  () =>
    Boolean(DEFAULT_SHOP_PAGES[slug.value]) &&
    Boolean(
      storeContent.value.contactEmail ||
        storeContent.value.contactPhone ||
        storeContent.value.businessHours
    )
);

const phoneHref = computed(
  () => `tel:${(storeContent.value.contactPhone || '').replace(/[^\d+]/g, '')}`
);

// 載入頁面：優先使用後台自訂頁面，不存在時改用內建預設內容
let loadSeq = 0;
const loadPage = async () => {
  const currentSlug = slug.value;
  const seq = ++loadSeq;
  loading.value = true;
  notFound.value = false;
  errorMessage.value = '';
  page.value = null;

  try {
    const res = await getCustomPageBySlug(currentSlug, { silent: true });
    if (seq !== loadSeq) return;
    if (res?.data) {
      page.value = res.data;
    } else {
      applyFallback(currentSlug);
    }
  } catch (error) {
    if (seq !== loadSeq) return;
    const status = error.response?.status;
    // 後端以 400「自訂頁面不存在」表示查無此頁
    if (status === 400 || status === 404) {
      applyFallback(currentSlug);
    } else {
      errorMessage.value = error.displayMessage || '請稍後再試';
    }
  } finally {
    if (seq === loadSeq) loading.value = false;
  }
};

const applyFallback = currentSlug => {
  const fallback = DEFAULT_SHOP_PAGES[currentSlug];
  if (fallback) {
    page.value = { ...fallback, slug: currentSlug };
  } else {
    notFound.value = true;
  }
};

// 內容中的站內連結（/shop/...）以 router 導頁，避免整頁重新載入
const handleContentClick = event => {
  const anchor = event.target.closest?.('a');
  if (!anchor) return;
  const href = anchor.getAttribute('href') || '';
  const isInternal = href.startsWith('/') && !href.startsWith('//');
  if (
    isInternal &&
    !anchor.target &&
    !event.ctrlKey &&
    !event.metaKey &&
    !event.shiftKey &&
    event.button === 0
  ) {
    event.preventDefault();
    router.push(href);
  }
};

const loadStoreContent = async () => {
  try {
    storeContent.value = await getStoreContent();
  } catch {
    // 聯絡資訊載入失敗時僅隱藏客服卡片
    storeContent.value = {};
  }
};

watch(
  slug,
  value => {
    if (value && route.path.startsWith('/shop/page/')) {
      loadPage();
      window.scrollTo({ top: 0 });
    }
  },
  { immediate: true }
);

onMounted(loadStoreContent);
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.shop-cms-page {
  background: $shop-bg-light;
  padding-bottom: 60px;
}

// Breadcrumb
.breadcrumb-container {
  background: white;
  padding: 20px 0;
  border-bottom: 1px solid $shop-border;
}

.breadcrumb-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-container {
  max-width: 900px;
  margin: 40px auto 0;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: $shop-shadow-md;
  min-width: 0;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: $shop-text;
  line-height: 1.4;
  margin: 0 0 24px;
  padding-bottom: 16px;
  border-bottom: 3px solid $shop-primary;
  overflow-wrap: anywhere;
}

.page-content {
  font-size: 1.02rem;
  line-height: 1.8;
  color: $shop-text;
  overflow-wrap: anywhere;

  :deep(h2),
  :deep(h3) {
    font-size: 1.3rem;
    font-weight: 600;
    line-height: 1.4;
    margin: 32px 0 12px;
    color: $shop-text;
  }

  :deep(p) {
    margin: 12px 0;
  }

  :deep(ul),
  :deep(ol) {
    margin: 12px 0;
    padding-left: 24px;

    li {
      margin: 8px 0;
    }
  }

  :deep(a) {
    color: $shop-primary;
    text-decoration: underline;
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

  > :deep(*:first-child) {
    margin-top: 0;
  }
}

// 聯絡客服卡片
.contact-card {
  padding: 28px 40px;
}

.contact-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: 600;
  color: $shop-text;
  margin: 0 0 16px;

  .q-icon {
    color: $shop-primary;
  }
}

.contact-list {
  list-style: none;
  margin: 0;
  padding: 0;

  li {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 4px 8px;
    padding: 6px 0;
    color: $shop-text;
    overflow-wrap: anywhere;

    .q-icon {
      color: $shop-text-secondary;
    }
  }

  a {
    color: $shop-primary;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.contact-label {
  color: $shop-text-secondary;

  &::after {
    content: '：';
  }
}

// 狀態區塊
.state-box {
  text-align: center;
  padding: 72px 20px;
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

@media (max-width: 768px) {
  .breadcrumb-wrapper,
  .page-container {
    padding: 0 16px;
  }

  .page-container {
    margin-top: 24px;
    gap: 16px;
  }

  .page-card,
  .contact-card {
    padding: 24px 20px;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .state-box {
    padding: 56px 20px;
  }
}
</style>
