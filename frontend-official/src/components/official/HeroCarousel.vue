<template>
  <div class="hero-carousel">
    <q-carousel
      v-model="slide"
      transition-prev="slide-right"
      transition-next="slide-left"
      swipeable
      animated
      control-color="white"
      navigation
      padding
      arrows
      height="600px"
      class="text-white shadow-1 rounded-borders"
      @mouseenter="stopAutoplay"
      @mouseleave="startAutoplay"
    >
      <q-carousel-slide
        v-for="(item, index) in slides"
        :key="index"
        :name="index + 1"
        :img-src="item.image"
        class="column no-wrap flex-center"
      >
        <div class="q-carousel__slide-content text-center">
          <div class="fade-in-up">
            <div class="text-h2 text-weight-bold q-mb-md text-shadow">
              {{ item.title }}
            </div>
            <div class="text-h5 q-mb-lg text-shadow">
              {{ item.subtitle }}
            </div>
            <!-- <q-btn
              :class="item.buttonClass"
              size="lg"
              :label="item.buttonText"
              @click="navigateToPage(item.buttonLink)"
              class="hover-scale"
            /> -->
          </div>
        </div>
      </q-carousel-slide>
    </q-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { heroSlides } from 'src/utils/testData.js';

const router = useRouter();
const slide = ref(1);
let autoplayInterval = null;

const slides = heroSlides;

const navigateToPage = link => {
  router.push(link);
};

const startAutoplay = () => {
  autoplayInterval = setInterval(() => {
    slide.value = slide.value === slides.length ? 1 : slide.value + 1;
  }, 5000);
};

const stopAutoplay = () => {
  if (autoplayInterval) {
    clearInterval(autoplayInterval);
    autoplayInterval = null;
  }
};

onMounted(() => {
  startAutoplay();
});

onUnmounted(() => {
  stopAutoplay();
});
</script>

<style lang="scss" scoped>
.hero-carousel {
  width: 100%;
  position: relative;
  z-index: 1;

  :deep(.q-carousel) {
    position: relative;
    overflow: hidden;
  }

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
        rgba(220, 20, 60, 0.3) 0%,
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

// 響應式設計 - 輪播圖特定
@media (max-width: 768px) {
  .hero-carousel {
    :deep(.q-carousel) {
      height: 400px !important;
    }

    .text-h2 {
      font-size: 1.8rem;
    }

    .text-h5 {
      font-size: 1.2rem;
    }
  }
}

@media (max-width: 480px) {
  .hero-carousel {
    :deep(.q-carousel) {
      height: 300px !important;
    }

    .text-h2 {
      font-size: 1.5rem;
    }

    .text-h5 {
      font-size: 1rem;
    }
  }
}
</style>
