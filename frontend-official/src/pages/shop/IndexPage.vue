<template>
  <q-page class="shop-index-page">
    <!-- 彈跳廣告 -->
    <PopupAd
      v-model="showPopup"
      :popup-data="popupData"
      @action="handlePopupAction"
    />

    <!-- Hero 輪播區域 -->
    <section class="shop-hero">
      <q-carousel
        v-model="slide"
        transition-prev="slide-right"
        transition-next="slide-left"
        swipeable
        animated
        control-color="white"
        navigation
        arrows
        height="500px"
        class="shop-hero-carousel text-white"
      >
        <q-carousel-slide
          v-for="slideData in heroSlides"
          :key="slideData.id"
          :name="slideData.id"
          :img-src="slideData.image"
          class="column no-wrap flex-center"
        >
          <div class="q-carousel__slide-content text-center">
            <div class="fade-in-up">
              <div class="text-h2 text-weight-bold q-mb-md text-shadow">
                {{ slideData.title }}
              </div>
              <div class="text-h5 q-mb-lg text-shadow">
                {{ slideData.description }}
              </div>
              <q-btn
                unelevated
                size="lg"
                :label="slideData.buttonText"
                class="hero-btn hover-scale"
                @click="scrollToProducts"
              />
            </div>
          </div>
        </q-carousel-slide>
      </q-carousel>
    </section>

    <!-- 內容區域（淺灰背景） -->
    <div class="shop-content-area">
      <div class="container">
        <!-- 優惠券區塊 -->
        <CouponSection :coupons="coupons" @claim="handleClaimCoupon" />

        <!-- 熱銷商品 -->
        <section class="product-section">
          <div class="section-header">
            <h2 class="section-title">熱銷排行</h2>
            <a href="#" class="section-link">查看更多 →</a>
          </div>

          <div class="product-grid" ref="productsRef">
            <ProductCard
              v-for="product in hotProducts"
              :key="product.id"
              :product="product"
              @add-to-cart="handleAddToCart"
            />
          </div>
        </section>

        <!-- 中間橫幅廣告 -->
        <section class="mid-banner">
          <div class="banner-text">
            <h2>MID-SEASON SALE</h2>
            <p>年中大促 | 指定商品兩件五折</p>
          </div>
        </section>

        <!-- 最新上架 -->
        <section class="product-section">
          <div class="section-header">
            <h2 class="section-title">最新上架</h2>
          </div>

          <div class="product-grid">
            <ProductCard
              v-for="product in newProducts"
              :key="product.id"
              :product="product"
              @add-to-cart="handleAddToCart"
            />
          </div>
        </section>
      </div>
    </div>
    <!-- 內容區域結束 -->
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import PopupAd from 'components/shop/PopupAd.vue';
import CouponSection from 'components/shop/CouponSection.vue';
import ProductCard from 'components/shop/ProductCard.vue';
import { addToCart } from 'src/utils/cart.js';
import cookies from 'src/utils/cookies.js';
import { PopupAdHideKey } from 'src/config/constant.js';
import {
  shopHeroSlides,
  shopPopupData,
  shopCoupons,
  shopHotProducts,
  shopNewProducts,
} from 'src/utils/testData.js';

const $q = useQuasar();

// 輪播控制
const slide = ref(1);
const heroSlides = ref(shopHeroSlides);

// 彈窗控制
const showPopup = ref(true); // 預設顯示彈窗
const popupData = ref(shopPopupData);

// 優惠券資料
const coupons = ref(shopCoupons);

// 熱銷商品資料
const hotProducts = ref(shopHotProducts);

// 最新商品資料
const newProducts = ref(shopNewProducts);

// 商品區域引用
const productsRef = ref(null);

// 處理彈窗動作
const handlePopupAction = data => {
  $q.notify({
    message: '優惠券已發送到您的帳戶！',
    color: 'positive',
    position: 'top',
    icon: 'check_circle',
    timeout: 2000,
  });
};

// 處理領取優惠券
const handleClaimCoupon = coupon => {
  $q.notify({
    message: `成功領取「${coupon.title}」優惠券！`,
    color: 'positive',
    position: 'top',
    icon: 'check_circle',
    timeout: 2000,
  });
};

// 處理加入購物車
const handleAddToCart = product => {
  // 使用共用方法加入購物車
  addToCart(product, 1);

  $q.notify({
    message: `「${product.name}」已加入購物車`,
    color: 'positive',
    position: 'top',
    icon: 'check_circle',
    timeout: 2000,
  });
};

// 滾動到商品區域
const scrollToProducts = () => {
  if (productsRef.value) {
    productsRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

onMounted(() => {
  console.log('購物車首頁已載入');

  // 檢查是否需要顯示彈窗廣告
  const hidePopupAd = cookies.get(PopupAdHideKey);
  if (hidePopupAd === 'true') {
    showPopup.value = false;
    console.log('用戶已設定不再顯示彈窗廣告');
  }
});
</script>

<style lang="scss" scoped>
@import '../../css/common.scss';

.shop-index-page {
  background: $shop-bg-light;
  min-height: 100vh;
  padding: 0;
}

// =========================================
// Hero 輪播區域
// =========================================
.shop-hero {
  margin: 0;
  padding: 0;
  width: 100%;
  background: transparent;
  position: relative;
  z-index: 1;

  .shop-hero-carousel {
    width: 100%;
    height: 500px;
    position: relative;
    overflow: hidden;

    :deep(.q-carousel__slide) {
      background-size: cover;
      background-position: center;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(
          135deg,
          rgba(255, 107, 53, 0.3) 0%,
          rgba(0, 0, 0, 0.6) 100%
        );
        z-index: 1;
        pointer-events: none;
      }

      .q-carousel__slide-content {
        position: relative;
        z-index: 2;
      }
    }

    :deep(.q-carousel__control),
    :deep(.q-carousel__arrow) {
      color: rgba(255, 255, 255, 0.9);
      background: transparent;
      z-index: 10;
    }

    :deep(.q-carousel__navigation) {
      z-index: 10;
    }
  }
}

// =========================================
// 內容區域 (參考 demo1.html)
// =========================================
.shop-content-area {
  background: $shop-bg-light;
  padding: 40px 0 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

// =========================================
// 商品區塊
// =========================================
.product-section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 25px;
  border-bottom: 2px solid $shop-primary;
  padding-bottom: 12px;

  .section-title {
    font-size: 1.5rem;
    color: $shop-text;
    font-weight: 600;
    margin: 0;
  }

  .section-link {
    font-size: 0.9rem;
    color: #888;
    text-decoration: none;
    transition: color 0.3s;

    &:hover {
      color: $shop-primary;
    }
  }
}

// =========================================
// 商品網格 (響應式設計)
// =========================================
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

// =========================================
// 中間橫幅廣告
// =========================================
.mid-banner {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    $shop-primary 0%,
    $shop-primary-dark 100%
  );
  border-radius: 12px;
  margin-bottom: 50px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      135deg,
      rgba(197, 160, 89, 0.9) 0%,
      rgba(176, 141, 75, 0.8) 100%
    );
  }

  .banner-text {
    position: relative;
    z-index: 2;
    text-align: center;
    color: white;

    h2 {
      font-size: 2rem;
      margin-bottom: 10px;
      font-weight: 700;
      letter-spacing: 2px;
    }

    p {
      font-size: 1.2rem;
      margin-top: 10px;
    }
  }
}

// =========================================
// Hero 按鈕
// =========================================
.hero-btn {
  padding: 12px 40px;
  font-size: 1.1rem;
  background: $shop-primary !important;
  color: white !important;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s;

  &:hover {
    background: $shop-primary-dark !important;
    transform: scale(1.05);
  }
}

// =========================================
// 文字陰影效果
// =========================================
.text-shadow {
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
}

// =========================================
// 動畫效果
// =========================================
.fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hover-scale {
  transition: transform 0.3s;

  &:hover {
    transform: scale(1.05);
  }
}

// =========================================
// 響應式設計 - 平板
// =========================================
@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 18px;
  }
}

// =========================================
// 響應式設計 - 手機橫屏
// =========================================
@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }

  .shop-hero {
    .shop-hero-carousel {
      height: 400px !important;
    }

    .text-h2 {
      font-size: 1.8rem;
    }

    .text-h5 {
      font-size: 1.2rem;
    }
  }

  .section-header {
    margin-bottom: 20px;

    .section-title {
      font-size: 1.3rem;
    }

    .section-link {
      font-size: 0.85rem;
    }
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }

  .mid-banner {
    height: 180px;
    border-radius: 8px;

    .banner-text h2 {
      font-size: 1.5rem;
    }

    .banner-text p {
      font-size: 1rem;
    }
  }
}

// =========================================
// 響應式設計 - 小手機
// =========================================
@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }

  .shop-content-area {
    padding: 30px 0 40px;
  }

  .shop-hero {
    .shop-hero-carousel {
      height: 300px !important;
    }

    .text-h2 {
      font-size: 1.5rem;
    }

    .text-h5 {
      font-size: 1rem;
    }
  }

  .section-header {
    margin-bottom: 15px;
    padding-bottom: 10px;

    .section-title {
      font-size: 1.2rem;
    }

    .section-link {
      font-size: 0.8rem;
    }
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .product-section {
    margin-bottom: 35px;
  }

  .mid-banner {
    height: 150px;
    margin-bottom: 35px;

    .banner-text h2 {
      font-size: 1.3rem;
      letter-spacing: 1px;
    }

    .banner-text p {
      font-size: 0.9rem;
    }
  }

  .hero-btn {
    padding: 10px 30px;
    font-size: 1rem;
  }
}

// =========================================
// 超小螢幕優化
// =========================================
@media (max-width: 360px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .section-header .section-title {
    font-size: 1.1rem;
  }

  .mid-banner {
    height: 120px;

    .banner-text h2 {
      font-size: 1.1rem;
    }

    .banner-text p {
      font-size: 0.85rem;
    }
  }
}
</style>
