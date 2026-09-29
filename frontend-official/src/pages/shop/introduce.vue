<template>
  <q-page class="shop-introduce-page">
    <!-- Hero Banner -->
    <section class="intro-hero">
      <div class="intro-hero-overlay">
        <div class="intro-hero-content">
          <h1 class="intro-hero-title">遇日小舖</h1>
          <p class="intro-hero-subtitle">每一天都是美好相遇</p>
        </div>
      </div>
    </section>

    <!-- 品牌故事 -->
    <section class="intro-section">
      <div class="intro-container">
        <div class="section-header">
          <h2 class="section-title">
            {{ content.brandStoryBadge || '品牌故事' }}
          </h2>
          <div class="section-divider"></div>
        </div>
        <div class="story-content">
          <div class="story-text">
            <!-- 後台有設定品牌故事時優先使用 -->
            <template v-if="hasCustomStory">
              <h3 v-if="content.brandStoryTitle" class="story-heading">
                {{ content.brandStoryTitle }}
              </h3>
              <p v-if="content.brandStoryLead" class="preline">
                {{ content.brandStoryLead }}
              </p>
            </template>
            <template v-else>
              <p>
                「遇日小舖」源於對美好生活的追求與分享。我們相信，每一個日常時刻都值得被珍惜，
                每一件精心挑選的商品都能為生活增添一份美好。
              </p>
              <p>
                從日本精選的居家用品、文具雜貨，到手工製作的甜點美食，
                我們用心為您挑選每一件商品，希望能為您的生活帶來驚喜與溫暖。
              </p>
              <p>
                在這裡，您不只是購物，更是在尋找一種生活態度，
                一種對美好事物的堅持，一份對生活的熱愛。
              </p>
            </template>
            <p v-if="content.brandStoryNote" class="story-note preline">
              {{ content.brandStoryNote }}
            </p>
          </div>
          <div class="story-image" aria-hidden="true">
            <q-icon name="local_florist" size="96px" />
            <span>遇日小舖</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 經營理念 -->
    <section class="intro-section philosophy-section">
      <div class="intro-container">
        <div class="section-header">
          <h2 class="section-title">經營理念</h2>
          <div class="section-divider"></div>
        </div>
        <div class="philosophy-grid">
          <div
            v-for="card in philosophyCards"
            :key="card.key"
            class="philosophy-card"
          >
            <div class="philosophy-icon">
              <q-icon :name="card.icon" size="48px" color="primary" />
            </div>
            <h3>{{ card.title }}</h3>
            <p class="preline">{{ card.text }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品特色 -->
    <section class="intro-section">
      <div class="intro-container">
        <div class="section-header">
          <h2 class="section-title">商品特色</h2>
          <div class="section-divider"></div>
        </div>
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-number">01</div>
            <h3>日本精選商品</h3>
            <p>直接從日本嚴選進口，帶來最道地的日式生活美學與品質保證。</p>
          </div>
          <div class="feature-item">
            <div class="feature-number">02</div>
            <h3>手工甜點</h3>
            <p>每日新鮮現做，使用優質食材，讓您品嚐到最美味的幸福滋味。</p>
          </div>
          <div class="feature-item">
            <div class="feature-number">03</div>
            <h3>獨特設計</h3>
            <p>網羅獨特設計商品，讓您的生活空間充滿個性與品味。</p>
          </div>
          <div class="feature-item">
            <div class="feature-number">04</div>
            <h3>實用生活用品</h3>
            <p>精選實用居家雜貨，兼具美觀與機能，提升生活品質。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 聯絡資訊（來自後台商店內容設定） -->
    <section class="intro-section contact-section">
      <div class="intro-container">
        <div class="section-header">
          <h2 class="section-title">
            {{ content.contactPageBadge || '聯絡我們' }}
          </h2>
          <div class="section-divider"></div>
          <p v-if="content.contactPageTitle" class="section-lead-title">
            {{ content.contactPageTitle }}
          </p>
          <p v-if="content.contactPageLead" class="section-lead preline">
            {{ content.contactPageLead }}
          </p>
        </div>
        <div class="contact-content">
          <!-- 載入中 -->
          <div v-if="loading" class="contact-info">
            <div v-for="n in 4" :key="n" class="contact-item">
              <q-skeleton type="QAvatar" size="32px" />
              <div class="contact-text full-width">
                <q-skeleton type="text" width="40%" />
                <q-skeleton type="text" width="80%" />
              </div>
            </div>
          </div>

          <!-- 載入失敗 -->
          <div v-else-if="loadError" class="contact-state">
            <q-icon name="cloud_off" size="48px" color="grey-5" />
            <p>聯絡資訊暫時無法載入</p>
            <q-btn
              outline
              color="primary"
              icon="refresh"
              label="重新載入"
              @click="loadContent(true)"
            />
          </div>

          <!-- 聯絡資訊 -->
          <div v-else-if="contactItems.length > 0" class="contact-info">
            <div
              v-for="item in contactItems"
              :key="item.key"
              class="contact-item"
            >
              <q-icon :name="item.icon" size="32px" color="primary" />
              <div class="contact-text">
                <h4>{{ item.label }}</h4>
                <p>
                  <a v-if="item.href" :href="item.href">{{ item.value }}</a>
                  <span v-else class="preline">{{ item.value }}</span>
                </p>
                <p v-if="item.hint" class="contact-hint">{{ item.hint }}</p>
              </div>
            </div>
          </div>

          <!-- 尚未設定聯絡資訊 -->
          <div v-else class="contact-state">
            <q-icon name="support_agent" size="48px" color="grey-5" />
            <p>目前尚未提供聯絡資訊，訂單相關問題可先使用訂單查詢。</p>
          </div>

          <!-- 顧客服務捷徑 -->
          <div class="contact-help">
            <h3>需要協助嗎？</h3>
            <p v-if="content.contactSupportNote" class="help-text preline">
              {{ content.contactSupportNote }}
            </p>
            <div class="help-links">
              <q-btn
                unelevated
                color="primary"
                icon="receipt_long"
                label="訂單查詢"
                to="/shop/order/lookup"
              />
              <q-btn
                outline
                color="primary"
                icon="help_outline"
                label="常見問題"
                to="/shop/page/faq"
              />
              <q-btn
                outline
                color="primary"
                icon="assignment_return"
                label="退換貨說明"
                to="/shop/page/returns"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getStoreContent } from 'src/api/store.js';

// 後台「商店內容設定」（未設定的欄位為空字串）
const content = ref({});
const loading = ref(false);
const loadError = ref(false);

const loadContent = async (force = false) => {
  loading.value = true;
  loadError.value = false;
  try {
    content.value = await getStoreContent({ force });
  } catch {
    loadError.value = true;
  } finally {
    loading.value = false;
  }
};

// 是否有後台設定的品牌故事
const hasCustomStory = computed(() =>
  Boolean(content.value.brandStoryTitle || content.value.brandStoryLead)
);

// 內建的經營理念（後台未設定使命 / 願景 / 價值時使用）
const DEFAULT_PHILOSOPHY = [
  {
    key: 'curation',
    icon: 'favorite',
    title: '用心選物',
    text: '嚴選每一件商品，確保品質與獨特性，讓每位顧客都能找到心儀的好物。',
  },
  {
    key: 'quality',
    icon: 'verified',
    title: '品質保證',
    text: '堅持品質第一，所有商品經過嚴格把關，給您最安心的購物體驗。',
  },
  {
    key: 'service',
    icon: 'support_agent',
    title: '貼心服務',
    text: '提供專業的客服支援，用心傾聽每一位顧客的需求，讓購物更加安心便利。',
  },
];

// 經營理念卡片：後台有設定使命 / 願景 / 價值內容時優先顯示
const philosophyCards = computed(() => {
  const c = content.value;
  const configured = [
    {
      key: 'mission',
      icon: 'flag',
      title: c.brandMissionTitle || '我們的使命',
      text: c.brandMissionContent,
    },
    {
      key: 'vision',
      icon: 'visibility',
      title: c.brandVisionTitle || '我們的願景',
      text: c.brandVisionContent,
    },
    {
      key: 'value',
      icon: 'favorite',
      title: c.brandValueTitle || '我們重視的價值',
      text: c.brandValueContent,
    },
  ].filter(card => card.text);
  return configured.length > 0 ? configured : DEFAULT_PHILOSOPHY;
});

// 聯絡資訊：只顯示有設定的欄位
const contactItems = computed(() => {
  const c = content.value;
  const phoneDigits = (c.contactPhone || '').replace(/[^\d+]/g, '');
  return [
    {
      key: 'address',
      icon: 'storefront',
      label: '門市地址',
      value: c.address,
      hint: c.contactAddressHint,
    },
    {
      key: 'hours',
      icon: 'schedule',
      label: '營業時間',
      value: c.businessHours,
      hint: c.contactBusinessHoursHint,
    },
    {
      key: 'phone',
      icon: 'phone',
      label: '聯絡電話',
      value: c.contactPhone,
      href: phoneDigits ? `tel:${phoneDigits}` : '',
      hint: c.contactPhoneHint,
    },
    {
      key: 'email',
      icon: 'email',
      label: '電子信箱',
      value: c.contactEmail,
      href: c.contactEmail ? `mailto:${c.contactEmail}` : '',
      hint: c.contactEmailHint,
    },
  ].filter(item => item.value);
});

onMounted(() => loadContent());
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.shop-introduce-page {
  background: #ffffff;
}

// Hero Section
.intro-hero {
  height: 400px;
  background: linear-gradient(135deg, $shop-primary 0%, $shop-primary-dark 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.intro-hero-overlay {
  text-align: center;
  color: white;
  z-index: 1;
}

.intro-hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin: 0 0 20px 0;
  letter-spacing: 4px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
}

.intro-hero-subtitle {
  font-size: 1.5rem;
  font-weight: 300;
  margin: 0;
  letter-spacing: 2px;
}

// Section Styles
.intro-section {
  padding: 80px 0;

  &.philosophy-section {
    background: $shop-bg-light;
  }

  &.contact-section {
    background: linear-gradient(135deg, #fff5f0 0%, #ffe8d8 100%);
  }
}

.intro-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: $shop-text;
  margin: 0 0 20px 0;
}

.section-divider {
  width: 80px;
  height: 4px;
  background: $shop-primary;
  margin: 0 auto;
  border-radius: 2px;
}

// Story Section
.story-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.story-text {
  p {
    font-size: 1.1rem;
    line-height: 2;
    color: $shop-text-secondary;
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.story-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 280px;
  border-radius: 16px;
  background: linear-gradient(135deg, lighten($shop-primary, 38%) 0%, lighten($shop-primary, 28%) 100%);
  color: $shop-primary-dark;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.2em;
}

// Philosophy Section
.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 40px;
}

.philosophy-card {
  text-align: center;
  padding: 40px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: $shop-text;
    margin: 20px 0 16px 0;
  }

  p {
    font-size: 1rem;
    line-height: 1.8;
    color: $shop-text-secondary;
  }
}

.philosophy-icon {
  margin-bottom: 20px;
}

// Features Section
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

.feature-item {
  padding: 30px;
  border-left: 4px solid $shop-primary;
  background: $shop-bg-light;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: $shop-text;
    margin: 16px 0;
  }

  p {
    font-size: 1rem;
    line-height: 1.8;
    color: $shop-text-secondary;
  }
}

.feature-number {
  font-size: 2.5rem;
  font-weight: 800;
  color: rgba($shop-primary, 0.2);
  line-height: 1;
}

// Contact Section
.contact-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 60px;
}

.contact-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.contact-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: $shop-text;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 1rem;
    color: $shop-text-secondary;
    margin: 0;
  }
}

.contact-hint {
  font-size: 0.85rem !important;
  margin-top: 6px !important;
  line-height: 1.6;
}

.contact-text {
  min-width: 0;
  overflow-wrap: anywhere;

  a {
    color: $shop-text;
    text-decoration: none;

    &:hover {
      color: $shop-primary;
    }
  }
}

.contact-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: $shop-shadow-sm;
  text-align: center;
  color: $shop-text-secondary;

  p {
    margin: 0;
  }
}

.contact-help {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: $shop-shadow-lg;
  align-self: start;

  h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: $shop-text;
    margin: 0 0 16px;
  }
}

.help-text {
  font-size: 0.95rem;
  line-height: 1.7;
  color: $shop-text-secondary;
  margin: 0 0 24px;
}

.help-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

// 後台輸入的多行文字保留換行
.preline {
  white-space: pre-line;
}

.story-heading {
  font-size: 1.6rem;
  font-weight: 700;
  line-height: 1.5;
  color: $shop-text;
  margin: 0 0 20px;
}

.story-note {
  font-size: 1rem !important;
  padding-left: 16px;
  border-left: 3px solid $shop-primary;
}

.section-lead-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: $shop-text;
  margin: 24px 0 8px;
}

.section-lead {
  max-width: 720px;
  margin: 8px auto 0;
  font-size: 1rem;
  line-height: 1.8;
  color: $shop-text-secondary;
}

// Responsive Design
@media (max-width: 1024px) {
  .story-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .philosophy-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .contact-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .contact-info {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .intro-hero {
    height: 300px;
  }

  .intro-hero-title {
    font-size: 2.5rem;
  }

  .intro-hero-subtitle {
    font-size: 1.2rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .intro-section {
    padding: 60px 0;
  }

  .intro-container {
    padding: 0 16px;
  }

  .section-header {
    margin-bottom: 40px;
  }

  .philosophy-card {
    padding: 32px 20px;
  }

  .feature-item {
    padding: 24px 20px;
  }

  .contact-help {
    padding: 32px 20px;
  }

  .story-heading {
    font-size: 1.35rem;
  }
}
</style>
