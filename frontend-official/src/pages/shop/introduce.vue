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
          <h2 class="section-title">品牌故事</h2>
          <div class="section-divider"></div>
        </div>
        <div class="story-content">
          <div class="story-text">
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
          </div>
          <div class="story-image">
            <img
              src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800"
              alt="品牌故事"
            />
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
          <div class="philosophy-card">
            <div class="philosophy-icon">
              <q-icon name="favorite" size="48px" color="primary" />
            </div>
            <h3>用心選物</h3>
            <p>
              嚴選每一件商品，確保品質與獨特性， 讓每位顧客都能找到心儀的好物。
            </p>
          </div>
          <div class="philosophy-card">
            <div class="philosophy-icon">
              <q-icon name="verified" size="48px" color="primary" />
            </div>
            <h3>品質保證</h3>
            <p>堅持品質第一，所有商品經過嚴格把關， 給您最安心的購物體驗。</p>
          </div>
          <div class="philosophy-card">
            <div class="philosophy-icon">
              <q-icon name="support_agent" size="48px" color="primary" />
            </div>
            <h3>貼心服務</h3>
            <p>
              提供專業的客服支援，用心傾聽每一位顧客的需求，
              讓購物更加安心便利。
            </p>
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

    <!-- 聯絡資訊 -->
    <section class="intro-section contact-section">
      <div class="intro-container">
        <div class="section-header">
          <h2 class="section-title">聯絡我們</h2>
          <div class="section-divider"></div>
        </div>
        <div class="contact-content">
          <div class="contact-info">
            <div class="contact-item">
              <q-icon name="storefront" size="32px" color="primary" />
              <div class="contact-text">
                <h4>門市地址</h4>
                <p>台灣 台北市</p>
              </div>
            </div>
            <div class="contact-item">
              <q-icon name="schedule" size="32px" color="primary" />
              <div class="contact-text">
                <h4>營業時間</h4>
                <p>週一至週日 10:00 - 20:00</p>
              </div>
            </div>
            <div class="contact-item">
              <q-icon name="phone" size="32px" color="primary" />
              <div class="contact-text">
                <h4>聯絡電話</h4>
                <p>02-1234-5678</p>
              </div>
            </div>
            <div class="contact-item">
              <q-icon name="email" size="32px" color="primary" />
              <div class="contact-text">
                <h4>電子信箱</h4>
                <p>service@yueri.com</p>
              </div>
            </div>
          </div>
          <div class="contact-social">
            <h3>追蹤我們</h3>
            <div class="social-buttons">
              <q-btn
                round
                size="lg"
                icon="facebook"
                color="primary"
                @click="openFacebook"
              />
              <q-btn
                round
                size="lg"
                icon="shopping_bag"
                color="orange"
                @click="openShopee"
              />
              <q-btn round size="lg" icon="send" color="info" />
            </div>
            <p class="social-text">關注我們的社群媒體，獲取最新優惠資訊</p>
          </div>
        </div>
      </div>
    </section>
  </q-page>
</template>

<script setup>
const openFacebook = () => {
  window.open('https://www.facebook.com/sylck168', '_blank');
};

const openShopee = () => {
  window.open('https://shopee.tw/lck050710', '_blank');
};
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.shop-introduce-page {
  background: #ffffff;
}

// Hero Section
.intro-hero {
  height: 400px;
  background: linear-gradient(rgba(255, 107, 53, 0.7), rgba(255, 107, 53, 0.85)),
    url('https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=1600')
      center/cover;
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
  img {
    width: 100%;
    border-radius: 16px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  }
}

// Philosophy Section
.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.contact-social {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: $shop-text;
    margin-bottom: 30px;
  }
}

.social-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 20px;
}

.social-text {
  font-size: 0.9rem;
  color: $shop-text-secondary;
  margin: 0;
}

// Responsive Design
@media (max-width: 1024px) {
  .story-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .philosophy-grid {
    grid-template-columns: 1fr;
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
}
</style>
