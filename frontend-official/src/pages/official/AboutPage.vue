<template>
  <q-page>
    <!-- 品牌故事英雄區 -->
    <section class="hero-section hero-tall hero-red relative-position">
      <div class="hero-background">
        <q-img
          :src="img_catering9"
          class="full-height full-width"
          style="height: 500px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">伸遠國際</h1>
          <p class="text-h5 q-mb-lg text-shadow">
            專業網站架設，創新數位體驗
          </p>
          <div class="text-h6 text-shadow">西元2022年新創立</div>
        </div>
      </div>
    </section>

    <!-- 品牌介紹內容 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="text-center q-mb-xl fade-in-up">
          <h3 class="text-h2 text-weight-bold q-mb-md">品牌故事</h3>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
        </div>

        <!-- 品牌理念 -->
        <div class="row q-gutter-xl items-center">
          <div class="col-12 col-md-6 fade-in-up">
            <q-card class="glass q-pa-lg full-height" flat bordered>
              <div class="text-h4 text-weight-bold q-mb-lg">品牌理念</div>
              <div class="text-body1 text-grey-4 line-height-lg">
                <p class="q-mb-md">
                  數位時代的來臨，企業對品牌形象的要求非常講究，我們團隊意識到維持傳統也得改革創新，
                  便由小型工作室設計，轉變為全方位數位服務，到現今的企業網站與購物平台開發。
                </p>
                <p class="q-mb-md">
                  除了保留原有的設計優點，持續不斷的學習新技術，向外學習各式系統開發，
                  讓每個客戶感受到團隊的堅持及創新的解決方案。
                </p>
                <p>
                  除此之外，為了讓客戶能在線上展現完整的品牌形象，不用煩惱上哪找合適的網站公司，
                  我們提供客製化網站開發的服務，讓您擁有與大型企業同等級的專業網站。
                </p>
              </div>
            </q-card>
          </div>

          <div class="col-12 col-md-5 fade-in-up">
            <div class="relative-position image-container">
              <q-img
                :src="img_catering12"
                class="rounded-borders-lg responsive-img shadow-10"
                style="height: 400px"
                loading="lazy"
              />
              <div class="image-decoration"></div>
            </div>
          </div>
        </div>

        <!-- 核心價值 -->
        <section class="section-padding">
          <div class="text-center q-mb-xl fade-in-up">
            <h3 class="text-h3 text-weight-bold q-mb-md">核心價值</h3>
            <div class="story-divider q-mx-auto q-mb-lg"></div>
            <p class="text-body1 text-grey-4">我們堅持的經營理念</p>
          </div>

          <div class="row q-gutter-lg justify-center">
            <div
              v-for="(value, index) in coreValues"
              :key="index"
              class="col-12 col-sm-6 col-md-3 fade-in-up"
              :style="{ 'animation-delay': `${index * 0.15}s` }"
            >
              <q-card
                class="glass text-center q-pa-lg full-height hover-scale"
                flat
                bordered
              >
                <div class="value-icon q-mb-md">
                  <Icon
                    :icon="value.icon"
                    class="text-orange-8"
                    style="font-size: 3em"
                  />
                </div>
                <div class="text-h6 text-weight-bold q-mb-sm">
                  {{ value.title }}
                </div>
                <p class="text-body2 text-grey-4">{{ value.description }}</p>
              </q-card>
            </div>
          </div>
        </section>

        <!-- 品牌發展歷程 -->
        <section class="section-padding">
          <div class="text-center q-mb-xl fade-in-up">
            <h3 class="text-h3 text-weight-bold q-mb-md">發展歷程</h3>
            <div class="story-divider q-mx-auto q-mb-lg"></div>
            <p class="text-body1 text-grey-4">從工作室起家到專業網站公司</p>
          </div>

          <div class="timeline">
            <div
              v-for="(milestone, index) in timeline"
              :key="index"
              class="timeline-item fade-in-up"
              :style="{ 'animation-delay': `${index * 0.2}s` }"
            >
              <div class="timeline-marker">
                <Icon :icon="milestone.icon" style="font-size: 1.5em" />
              </div>
              <div class="timeline-content">
                <q-card class="glass q-pa-lg" flat bordered>
                  <div
                    class="timeline-year text-h6 text-orange-8 text-weight-bold q-mb-sm"
                  >
                    {{ milestone.year }}
                  </div>
                  <div class="timeline-title text-h6 text-weight-bold q-mb-sm">
                    {{ milestone.title }}
                  </div>
                  <div class="timeline-description text-body2 text-grey-4">
                    {{ milestone.description }}
                  </div>
                </q-card>
              </div>
            </div>
          </div>
        </section>

        <!-- 創辦人故事 -->
        <section class="section-padding">
          <div class="text-center q-mb-xl fade-in-up">
            <h3 class="text-h3 text-weight-bold q-mb-md">創辦人的話</h3>
            <div class="story-divider q-mx-auto q-mb-lg"></div>
            <p class="text-body1 text-grey-4">
              傳承前輩的專業精神，打造數位品牌的未來
            </p>
          </div>

          <q-card class="glass text-center q-pa-xl" flat bordered>
            <div class="row justify-center q-mb-lg">
              <q-img
                :src="img_person1"
                class="shadow-10"
                loading="lazy"
                style="
                  width: 120px;
                  height: 120px;
                  border-radius: 50%;
                  object-fit: cover;
                "
              />
            </div>

            <div class="founder-content max-width-lg q-mx-auto">
              <div class="text-body1 text-grey-4 line-height-lg q-mb-lg">
                <p class="q-mb-md">
                  開啟我設計生涯的是我的師長，他是一位專業的平面設計師，
                  也是一位在業界有名的品牌設計顧問，亦是一位標案指定的總總設計，
                  他的設計作品充滿創意且兼備實用性。
                </p>
                <p class="q-mb-md">
                  隨著時代變遷，我明白到數位轉型的重要性，
                  我將帶著前輩傳承的設計精神，結合最新的網站技術，並延續下去...
                </p>
              </div>
            </div>
          </q-card>
        </section>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { Icon } from '@iconify/vue';
import { timeline, coreValues } from 'src/utils/testData.js';

// 圖片
const img_catering9 =
  'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80';
const img_catering12 =
  'https://images.unsplash.com/photo-1574484284002-952d92456975?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80';
const img_person1 =
  'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&auto=format&fit=crop&w=150&q=80';
</script>

<style lang="scss" scoped>
.image-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: $gradient-secondary;
  border-radius: 50%;
  opacity: 0.1;
  z-index: -1;
}

.timeline {
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    bottom: 0;
    width: 2px;
    background: #f57c00;
    transform: translateX(-50%);
  }

  .timeline-item {
    display: flex;
    align-items: center;
    margin-bottom: 60px;
    position: relative;

    &:nth-child(odd) {
      flex-direction: row;

      .timeline-content {
        margin-left: 60px;
        text-align: left;
      }

      .timeline-marker {
        order: 1;
      }
    }

    &:nth-child(even) {
      flex-direction: row-reverse;

      .timeline-content {
        margin-right: 60px;
        text-align: right;
      }

      .timeline-marker {
        order: 1;
      }
    }

    .timeline-marker {
      width: 60px;
      height: 60px;
      background: #f57c00;
      border: 4px solid $surface-dark;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      z-index: 2;
      box-shadow: $shadow-medium;
    }

    .timeline-content {
      flex: 1;
      max-width: 400px;
    }
  }
}

.founder-content {
  .italic {
    font-style: italic;
  }
}

.value-icon {
  transform: scale(1);
  transition: transform 0.3s ease;
}

.hover-scale:hover .value-icon {
  transform: scale(1.1);
}

@media (max-width: 768px) {
  .timeline {
    &::before {
      left: 30px;
    }

    .timeline-item {
      flex-direction: row !important;

      .timeline-content {
        margin-left: 60px !important;
        margin-right: 0 !important;
        text-align: left !important;
      }

      .timeline-marker {
        position: absolute;
        left: 0;
      }
    }
  }
}
</style>
