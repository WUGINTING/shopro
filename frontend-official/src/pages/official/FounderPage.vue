<template>
  <q-page>
    <!-- 創辦人介紹英雄區 -->
    <section class="hero-section hero-tall hero-red relative-position">
      <div class="hero-background">
        <q-img
          :src="img_store3"
          class="full-height full-width"
          style="height: 500px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">
            創辦人介紹
          </h1>
          <p class="text-h5 q-mb-lg text-shadow">宇宙超人 - 曹宇超</p>
          <div class="text-h6 text-shadow">超過20年經驗，專業外燴領導者</div>
        </div>
      </div>
    </section>

    <!-- 創辦人介紹內容 -->
    <div class="container q-mx-auto">
      <!-- 主要介紹區塊 -->
      <section class="section-padding">
        <div class="row q-gutter-md items-center">
          <div class="col-12 col-sm-5 col-lg-4 fade-in-up">
            <q-img
              :src="img_work1"
              class="founder-image shadow-20"
              loading="lazy"
            />
          </div>

          <div class="col-12 col-sm-6 col-lg-7 fade-in-up">
            <q-card class="glass q-pa-xl" flat bordered>
              <div class="text-h3 text-weight-bold q-mb-lg">
                宇宙超人 - 曹宇超
              </div>
              <div
                class="founder-intro text-body1 text-grey-4 line-height-lg q-mb-lg"
              >
                <p class="text-h6 text-weight-bold text-orange-8 q-mb-md">
                  來自投嘉雲彰區域 - 雲鼎白金分會
                </p>

                <div class="achievements q-mb-lg">
                  <div class="achievement-item q-mb-sm">
                    <Icon icon="mdi:medal" class="text-amber q-mr-sm" />
                    雲鼎白金分會創始01號會員
                  </div>
                  <div class="achievement-item q-mb-sm">
                    <Icon icon="mdi:trophy" class="text-amber q-mr-sm" />
                    首位雲鼎白金分會1金質獎章🎖️
                  </div>
                  <div class="achievement-item q-mb-sm">
                    <Icon icon="mdi:account-star" class="text-amber q-mr-sm" />
                    雲鼎白金分會第一會期 - 副主席
                  </div>
                  <div class="achievement-item">
                    <Icon icon="mdi:chef-hat" class="text-amber q-mr-sm" />
                    專業別：中日式外燴服務
                  </div>
                </div>

                <div class="founder-philosophy">
                  <p class="text-weight-medium text-orange-7 q-mb-sm">
                    經營理念
                  </p>
                  <p class="italic">
                    我們結合「精緻燒烤、傳統合菜、創新日料、突破以往不同的宴席菜餚！」
                  </p>
                </div>

                <div class="founder-philosophy">
                  <p class="text-weight-medium text-orange-7 q-mb-sm">
                    品牌標語
                  </p>
                  <p class="italic">双台外燴找宇超，用心料理不取巧。</p>
                </div>
              </div>
            </q-card>
          </div>
        </div>
      </section>

      <!-- 服務項目 -->
      <section class="section-padding">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">服務項目</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">全方位活動規劃與外燴服務</p>
        </div>

        <div class="row q-gutter-lg justify-center">
          <div
            v-for="(service, index) in services"
            :key="index"
            class="col-12 col-sm-6 col-md-3 fade-in-up"
            :style="{ 'animation-delay': `${index * 0.15}s` }"
          >
            <q-card
              class="glass text-center q-pa-lg full-height hover-scale service-card"
              flat
              bordered
            >
              <div class="service-icon q-mb-md">
                <Icon
                  :icon="service.icon"
                  class="text-orange-8"
                  style="font-size: 3em"
                />
              </div>
              <div class="text-h6 text-weight-bold q-mb-sm">
                {{ service.title }}
              </div>
              <p class="text-body2 text-grey-4">{{ service.description }}</p>
            </q-card>
          </div>
        </div>
      </section>

      <!-- 代表作品 -->
      <section class="section-padding">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">代表作品</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">
            單場統包金額百萬以上經歷、超過20年經驗，辦過上千場的外燴活動
          </p>
        </div>

        <div class="portfolio-grid">
          <div
            v-for="(work, index) in portfolioWorks"
            :key="index"
            class="portfolio-category q-mb-xl fade-in-up"
            :style="{ 'animation-delay': `${index * 0.2}s` }"
          >
            <q-card class="glass q-pa-lg" flat bordered>
              <div class="category-header q-mb-md">
                <h4 class="text-h5 text-weight-bold text-orange-8 q-mb-sm">
                  {{ work.category }}
                </h4>
              </div>

              <div class="work-list">
                <div
                  v-for="(item, idx) in work.items"
                  :key="idx"
                  class="work-item q-mb-sm"
                >
                  <Icon icon="mdi:star" class="text-amber q-mr-sm" />
                  <span class="text-body1 text-grey-4">{{ item }}</span>
                </div>
              </div>
            </q-card>
          </div>
        </div>
      </section>

      <!-- 經驗統計 -->
      <section class="section-padding">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">經驗統計</h2>
        </div>

        <div class="row q-gutter-lg justify-center">
          <div
            v-for="(stat, index) in stats"
            :key="index"
            class="col-12 col-sm-6 col-md-3 fade-in-up"
            :style="{ 'animation-delay': `${index * 0.1}s` }"
          >
            <q-card class="glass text-center q-pa-xl stats-card" flat bordered>
              <div
                class="stat-number text-h2 text-weight-bold text-orange-8 q-mb-sm"
              >
                {{ stat.number }}
              </div>
              <div class="stat-label text-body1 text-grey-4">
                {{ stat.label }}
              </div>
            </q-card>
          </div>
        </div>
      </section>
    </div>
  </q-page>
</template>

<script setup>
import { Icon } from '@iconify/vue';
import {
  founderServices,
  portfolioWorks,
  founderStats,
} from 'src/utils/testData.js';

// 圖片
const img_store3 =
  'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80';
const img_work1 =
  'https://images.unsplash.com/photo-1556910103-1c02745aae4d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80';

const services = founderServices;
const stats = founderStats;
</script>

<style lang="scss" scoped>
.achievement-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.service-card {
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    border-color: rgba(255, 107, 53, 0.5);
  }
}

.service-icon {
  transform: scale(1);
  transition: transform 0.3s ease;
}

.hover-scale:hover .service-icon {
  transform: scale(1.1);
}

.portfolio-grid {
  max-width: 1000px;
  margin: 0 auto;
}

.work-item {
  display: flex;
  align-items: center;
}

.stats-card {
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-10px);
    border-color: rgba(220, 20, 60, 0.3);
  }
}

.motto-quote {
  position: relative;

  &::before,
  &::after {
    content: '"';
    font-size: 2em;
    color: $primary;
    position: absolute;
    top: -10px;
  }

  &::before {
    left: -30px;
  }

  &::after {
    right: -30px;
  }
}

@media (max-width: 768px) {
  .motto-quote {
    &::before,
    &::after {
      display: none;
    }
  }

  .achievement-item {
    font-size: 0.9em;
  }
}
</style>
