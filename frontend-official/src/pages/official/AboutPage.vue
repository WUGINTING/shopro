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
            傳承家族料理精神，創新美食體驗
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
                  新世紀的來臨，大家對美食的品味非常講究，我們團隊意識到維持傳統也得改革創新，
                  便由夜市型態串燒炭烤，轉變為精緻燒烤，到現今居酒屋與中日式外燴。
                </p>
                <p class="q-mb-md">
                  除了保留原有的串燒優點，持續不斷的研發新菜色，向外學習各式料理，
                  讓每個客戶感受到團隊的堅持及創新的佳餚。
                </p>
                <p>
                  除此之外，為了讓客戶能在舒適自在的環境舉行聚餐活動，不用煩惱上哪找餐廳、飯館，
                  我們提供到府私廚料理的服務，讓您在府上亦能品嘗與餐廳同等級的新鮮餐餚。
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
            <p class="text-body1 text-grey-4">從街弄起家到專業外燴服務</p>
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
              傳承父親的料理精神，創造屬於我們的美食傳奇
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
                  開啟我料理生涯的正是我的父親，他是一個煎炒滷拌烤蒸的料理達人，
                  也是一個家喻戶曉的西點麵包師傅，亦是一位流水席宴指定的總鋪師，
                  在家中掌廚猶如辦桌一般，滿漢全席乃為家常便飯。
                </p>
                <p class="q-mb-md">
                  人生無常，父親的離開。點醒了沉浸於悲傷中的我，
                  我將帶著父親未完成的遺願，把他料理的味道，停留在最美的那一刻，並延續下去...
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
