<template>
  <q-page>
    <!-- 平面設計英雄區 -->
    <section class="hero-section hero-medium hero-orange relative-position">
      <div class="hero-background">
        <q-img
          src="https://images.unsplash.com/photo-1626785774573-4b799315345d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80"
          class="full-height full-width"
          style="height: 400px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">平面設計服務</h1>
          <p class="text-h5 text-shadow">專業設計，打造獨特品牌視覺</p>
        </div>
      </div>
    </section>

    <!-- 菜單分類 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <!-- 分類選單 -->
        <div class="text-center q-mb-xl">
          <q-tabs
            v-model="activeCategory"
            class="menu-tabs"
            indicator-color="orange-8"
            active-color="orange-8"
            align="center"
          >
            <q-tab
              v-for="category in categories"
              :key="category.id"
              :name="category.id"
              :label="category.name"
              class="text-weight-medium"
            />
          </q-tabs>
        </div>

        <!-- 餐點展示 -->
        <div class="menu-content">
          <div
            v-for="category in categories"
            :key="category.id"
            v-show="activeCategory === category.id"
            class="category-content fade-in-up"
          >
            <div class="text-center q-mb-xl">
              <h2 class="text-h3 text-weight-bold q-mb-sm">
                {{ category.name }}
              </h2>
              <div class="story-divider q-mx-auto q-mb-lg"></div>
              <p class="text-body1 text-grey-4">{{ category.description }}</p>
            </div>

            <div class="row q-gutter-lg">
              <div
                v-for="(dish, index) in category.dishes"
                :key="index"
                class="col-12 col-sm-6 col-lg-4 slide-in"
                :style="{ 'animation-delay': `${index * 0.1}s` }"
              >
                <menu-card :dish="dish" @click="openDishDialog(dish)" />
              </div>
            </div>
          </div>
        </div>

        <!-- 外燴套餐推薦 -->
        <section class="section-padding">
          <div class="text-center q-mb-xl fade-in-up">
            <h2 class="text-h2 text-weight-bold q-mb-md">外燴套餐推薦</h2>
            <div class="story-divider q-mx-auto q-mb-lg"></div>
            <p class="text-body1 text-grey-4">專為聚會活動設計的套餐組合</p>
          </div>

          <div class="row q-gutter-lg justify-center">
            <div
              v-for="(package_, index) in cateringPackages"
              :key="index"
              class="col-12 col-md-6 col-lg-4 fade-in-up"
              :style="{ 'animation-delay': `${index * 0.2}s` }"
            >
              <q-card
                class="glass full-height hover-scale cursor-pointer"
                flat
                bordered
                @click="contactUs"
              >
                <div class="image-container">
                  <q-img
                    :src="package_.image"
                    class="responsive-img"
                    loading="lazy"
                  >
                    <div class="absolute-top-right q-pa-sm">
                      <q-chip
                        :color="package_.popular ? 'secondary' : 'primary'"
                        text-color="white"
                        :label="package_.popular ? '熱門推薦' : '經典套餐'"
                      />
                    </div>
                  </q-img>
                </div>

                <q-card-section class="q-pa-lg">
                  <div class="text-h5 text-weight-bold q-mb-sm">
                    {{ package_.name }}
                  </div>
                  <div class="text-body2 text-grey-4 q-mb-md">
                    {{ package_.description }}
                  </div>

                  <div class="package-details q-mb-md">
                    <div class="detail-item q-mb-sm">
                      <Icon
                        icon="mdi:account-group"
                        class="text-secondary q-mr-sm"
                      />
                      <span class="text-body2"
                        >適合 {{ package_.people }} 人</span
                      >
                    </div>
                    <div class="detail-item q-mb-sm">
                      <Icon icon="mdi:clock" class="text-secondary q-mr-sm" />
                      <span class="text-body2">{{ package_.duration }}</span>
                    </div>
                    <div class="detail-item">
                      <Icon icon="mdi:food" class="text-secondary q-mr-sm" />
                      <span class="text-body2">{{ package_.dishes }}道菜</span>
                    </div>
                  </div>

                  <q-separator class="q-my-md" color="grey-7" />

                  <div class="row items-center justify-between">
                    <div class="text-h5 text-primary text-weight-bold">
                      {{ package_.price }}
                    </div>
                    <!-- <q-btn class="btn-secondary" size="sm" label="立即預約" /> -->
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </section>
      </div>
    </section>

    <!-- 餐點詳情對話框 -->
    <q-dialog v-model="dishDialog" class="dish-dialog">
      <q-card
        class="glass dish-card"
        style="min-width: 400px; max-width: 600px"
      >
        <div class="image-container">
          <q-img
            v-if="selectedDish"
            :src="selectedDish.image"
            class="responsive-img"
          >
            <div class="absolute-top-right q-pa-sm">
              <q-btn
                round
                flat
                size="sm"
                icon="close"
                color="white"
                @click="dishDialog = false"
              />
            </div>
          </q-img>
        </div>

        <q-card-section v-if="selectedDish" class="q-pa-lg">
          <div class="text-h5 text-weight-bold q-mb-sm">
            {{ selectedDish.name }}
          </div>
          <div class="text-body1 text-grey-4 q-mb-md">
            {{ selectedDish.description }}
          </div>

          <div v-if="selectedDish.ingredients" class="ingredients q-mb-md">
            <div class="text-subtitle2 text-weight-bold q-mb-sm">
              主要食材：
            </div>
            <div class="text-body2 text-grey-4">
              {{ selectedDish.ingredients }}
            </div>
          </div>

          <div v-if="selectedDish.allergens" class="allergens q-mb-md">
            <div class="text-subtitle2 text-weight-bold q-mb-sm">
              過敏原資訊：
            </div>
            <div class="text-body2 text-orange">
              {{ selectedDish.allergens }}
            </div>
          </div>

          <q-separator class="q-my-md" color="grey-7" />

          <div class="row items-center justify-between">
            <div class="text-h5 text-primary text-weight-bold">
              {{ selectedDish.price }}
            </div>
            <!-- <q-btn
              class="btn-primary"
              label="加入外燴清單"
              @click="addToCateringList"
            /> -->
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { Icon } from '@iconify/vue';
import MenuCard from 'src/components/official/MenuCard.vue';
import { menuCategories, cateringPackages } from 'src/utils/testData.js';

const router = useRouter();
const $q = useQuasar();
const activeCategory = ref('yakitori');
const dishDialog = ref(false);
const selectedDish = ref(null);

const categories = menuCategories;

/*
const categories_old = [
  {
    id: 'yakitori',
    name: '燒烤串燒',
    description: '傳統炭火燒烤，保留食材原味',
    dishes: [
      {
        name: '招牌牛舌',
        description: '厚切牛舌搭配特調醬料，口感Q彈有嚼勁',
        price: 'NT$ 280',
        image:
          'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '澳洲牛舌、海鹽、胡椒、特製醬料',
        allergens: '無',
      },
      {
        name: '雞腿肉串',
        description: '嫩煎雞腿肉，外酥內嫩，香氣撲鼻',
        price: 'NT$ 180',
        image:
          'https://images.unsplash.com/photo-1606728035253-49e8a23146de?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '台灣土雞腿肉、蒜泥、醬油',
        allergens: '含大豆',
      },
      {
        name: '豚五花串',
        description: '油花分佈均勻的豬五花，炭火香味濃郁',
        price: 'NT$ 200',
        image:
          'https://images.unsplash.com/photo-1544025162-d76694265947?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '台灣豬五花肉、味噌醬',
        allergens: '含大豆',
      },
    ],
  },
  {
    id: 'sashimi',
    name: '新鮮刺身',
    description: '當日現撈海鮮，新鮮度第一',
    dishes: [
      {
        name: '綜合刺身拼盤',
        description: '鮭魚、鮪魚、甜蝦等多種海鮮組合',
        price: 'NT$ 880',
        image:
          'https://images.unsplash.com/photo-1563612220849-3c44be9cb414?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '鮭魚、鮪魚、甜蝦、海膽、山葵',
        allergens: '含甲殼類',
      },
      {
        name: '炙燒鮭魚',
        description: '表面炙燒的新鮮鮭魚，外焦內嫩',
        price: 'NT$ 380',
        image:
          'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '挪威鮭魚、檸檬、海鹽',
        allergens: '無',
      },
    ],
  },
  {
    id: 'hot_dishes',
    name: '熱食料理',
    description: '溫暖身心的熱騰騰料理',
    dishes: [
      {
        name: '特色茶碗蒸',
        description: '嫩滑茶碗蒸配蛤蜊蝦仁，口感層次豐富',
        price: 'NT$ 180',
        image:
          'https://images.unsplash.com/photo-1512058564366-18510be2db19?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '雞蛋、蛤蜊、蝦仁、高湯',
        allergens: '含蛋類、甲殼類',
      },
      {
        name: '日式炸雞唐揚',
        description: '酥脆外皮包裹鮮嫩雞肉，搭配特製沾醬',
        price: 'NT$ 220',
        image:
          'https://images.unsplash.com/photo-1562967916-eb82221dfb38?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '雞腿肉、太白粉、醬油、薑汁',
        allergens: '含麩質、大豆',
      },
    ],
  },
  {
    id: 'appetizers',
    name: '開胃小菜',
    description: '精緻小菜，開啟味蕾',
    dishes: [
      {
        name: '涼拌海帶絲',
        description: '清爽海帶絲配黃瓜，口感爽脆',
        price: 'NT$ 120',
        image:
          'https://images.unsplash.com/photo-1565299507177-b0ac66763828?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '海帶絲、黃瓜、芝麻油、醋',
        allergens: '含芝麻',
      },
      {
        name: '醃製蘿蔔',
        description: '傳統日式醃製蘿蔔，酸甜開胃',
        price: 'NT$ 100',
        image:
          'https://images.unsplash.com/photo-1606131731446-54db7c26c24b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '白蘿蔔、米醋、糖、鹽',
        allergens: '無',
      },
    ],
  },
];
*/

const openDishDialog = dish => {
  selectedDish.value = dish;
  dishDialog.value = true;
};

const addToCateringList = () => {
  $q.notify({
    message: '已加入外燴清單！',
    color: 'primary',
    position: 'top',
    timeout: 2000,
  });
  dishDialog.value = false;
};

const contactUs = () => {
  router.push('/contact');
};
</script>

<style lang="scss" scoped>
.menu-tabs {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 40px;

  :deep(.q-tab) {
    font-size: 1rem;
    padding: 16px 24px;
  }
}

.category-content {
  animation: fadeInUp 0.6s ease-out;
}

.package-details {
  .detail-item {
    display: flex;
    align-items: center;
  }
}

.dish-dialog {
  :deep(.q-dialog__inner) {
    padding: 16px;
  }
}

.dish-card {
  background: rgba(30, 30, 30, 0.95);
  backdrop-filter: blur(20px);
}

@media (max-width: 768px) {
  .menu-tabs {
    :deep(.q-tab) {
      font-size: 0.875rem;
      padding: 12px 16px;
    }
  }
}
</style>
