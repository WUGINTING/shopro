<template>
  <q-page>
    <hero-carousel />

    <!-- 品牌特色區 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">伸遠國際特色</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4 max-width-md q-mx-auto">
            傳承家族料理精神，結合現代創新手法，為您打造最難忘的美食體驗
          </p>
        </div>

        <div class="row q-gutter-lg justify-center">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="col-12 col-md-5 col-lg-3 slide-in"
            :style="{ 'animation-delay': `${index * 0.2}s` }"
          >
            <q-card
              class="glass hover-scale text-center q-pa-lg full-height"
              flat
              bordered
            >
              <Icon
                :icon="feature.icon"
                class="text-orange-8 q-mb-md"
                style="font-size: 3em"
              />
              <div class="text-h6 text-weight-bold q-mb-sm">
                {{ feature.title }}
              </div>
              <p class="text-body2 text-grey-4">{{ feature.description }}</p>
            </q-card>
          </div>
        </div>
      </div>
    </section>

    <!-- 服務介紹區 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="row items-center q-gutter-xl">
          <div class="col-12 col-md-5 fade-in-up">
            <h2 class="text-h2 text-weight-bold text-orange-8 q-mb-lg">
              專業外燴服務
            </h2>
            <p class="text-body1 text-grey-4 q-mb-md">
              不論是企業活動、私人聚會，或是特殊節慶，我們都能為您量身打造最適合的餐飲服務。
            </p>
            <div class="q-mb-md">
              <Icon icon="mdi:check" class="text-primary q-mr-sm" />
              <span>專業廚師團隊到府服務</span>
            </div>
            <div class="q-mb-md">
              <Icon icon="mdi:check" class="text-primary q-mr-sm" />
              <span>新鮮食材當日採購</span>
            </div>
            <div class="q-mb-md">
              <Icon icon="mdi:check" class="text-primary q-mr-sm" />
              <span>客製化菜單設計</span>
            </div>
          </div>

          <div class="col-12 col-md-6 fade-in-up">
            <div class="relative-position image-container">
              <q-img
                src="https://images.unsplash.com/photo-1574484284002-952d92456975?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"
                class="rounded-borders-lg responsive-img"
                style="height: 400px"
                loading="lazy"
              />
              <div
                class="absolute-bottom q-pa-md bg-gradient-primary text-white"
              >
                <div class="text-h6 text-weight-bold">專業外燴服務</div>
                <div class="text-caption">讓每個聚會都成為美好回憶</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 餐點精選區 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">精選餐點</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">嚴選優質食材，用心烹調每一道菜</p>
        </div>

        <div class="row q-gutter-lg justify-center">
          <div
            v-for="(dish, index) in featuredDishes"
            :key="index"
            class="col-12 col-sm-6 col-md-4 slide-in"
            :style="{ 'animation-delay': `${index * 0.15}s` }"
          >
            <q-card
              class="glass hover-scale full-height cursor-pointer"
              flat
              bordered
              @click="router.push('/menu')"
            >
              <div class="image-container">
                <q-img :src="dish.image" class="responsive-img" loading="lazy">
                  <div class="absolute-bottom-right q-pa-sm">
                    <q-chip
                      :color="
                        dish.category === '招牌' ? 'primary' : 'secondary'
                      "
                      text-color="white"
                      :label="dish.category"
                    />
                  </div>
                </q-img>
              </div>

              <q-card-section class="q-pa-md">
                <div class="text-h6 text-weight-bold q-mb-sm">
                  {{ dish.name }}
                </div>
                <p class="text-body2 text-grey-4 q-mb-sm">
                  {{ dish.description }}
                </p>
                <div class="text-h6 text-primary text-weight-bold">
                  {{ dish.price }}
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>

        <div class="text-center q-mt-xl">
          <q-btn
            class="btn-secondary"
            size="lg"
            label="查看完整菜單"
            @click="router.push('/menu')"
          />
        </div>
      </div>
    </section>

    <!-- 客戶評價區 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">客戶見證</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">聽聽我們客戶的真實回饋</p>
        </div>

        <div class="row q-gutter-lg justify-center">
          <div
            v-for="(review, index) in reviews"
            :key="index"
            class="col-12 col-md-6 col-lg-4 fade-in-up"
            :style="{ 'animation-delay': `${index * 0.2}s` }"
          >
            <q-card class="glass q-pa-lg full-height" flat bordered>
              <div class="q-mb-md">
                <div class="row items-center q-gutter-sm q-mb-sm">
                  <q-rating
                    v-model="review.rating"
                    size="sm"
                    color="amber"
                    readonly
                  />
                  <span class="text-caption text-grey-4"
                    >({{ review.rating }}/5)</span
                  >
                </div>
                <p class="text-body2 text-grey-4 q-mb-md">
                  {{ review.comment }}
                </p>
              </div>

              <div class="row items-center q-gutter-sm">
                <q-avatar size="40px" color="primary">
                  <span class="text-white">{{ review.name.charAt(0) }}</span>
                </q-avatar>
                <div>
                  <div class="text-weight-bold">{{ review.name }}</div>
                  <div class="text-caption text-grey-4">{{ review.date }}</div>
                </div>
              </div>
            </q-card>
          </div>
        </div>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Icon } from '@iconify/vue';
import HeroCarousel from 'src/components/official/HeroCarousel.vue';
import { features, featuredDishes, reviews } from 'src/utils/testData.js';

const router = useRouter();
</script>
