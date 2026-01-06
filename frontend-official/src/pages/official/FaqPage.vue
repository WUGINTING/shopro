<template>
  <q-page>
    <!-- 常見問題英雄區 -->
    <section class="hero-section hero-medium hero-orange relative-position">
      <div class="hero-background">
        <q-img
          src="https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80"
          class="full-height full-width"
          style="height: 400px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">常見問題</h1>
          <p class="text-h5 text-shadow">關於伸遠國際的常見疑問解答</p>
        </div>
      </div>
    </section>

    <!-- 常見問題內容 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <!-- 問題分類標籤 -->
        <div class="q-mb-xl">
          <div class="text-center">
            <q-btn-toggle
              v-model="activeCategory"
              toggle-color="orange-8"
              unelevated
              :options="categoryOptions"
              class="category-toggle"
            />
          </div>
        </div>

        <!-- 常見問題內容 -->
        <div class="faq-content max-width-lg q-mx-auto">
          <div
            v-for="(category, categoryKey) in filteredFaqs"
            :key="categoryKey"
            class="faq-category q-mb-xl"
          >
            <div class="category-header q-mb-lg fade-in-up">
              <h3 class="text-h4 text-weight-bold text-orange-8 q-mb-sm">
                <Icon :icon="category.icon" class="q-mr-sm" />
                {{ category.title }}
              </h3>
              <p class="text-body2 text-grey-4">{{ category.description }}</p>
            </div>

            <div class="faq-items">
              <q-expansion-item
                v-for="(faq, index) in category.items"
                :key="index"
                :label="faq.question"
                header-class="text-white text-weight-medium"
                class="faq-item q-mb-md fade-in-up"
                :style="{ 'animation-delay': `${index * 0.1}s` }"
              >
                <template v-slot:header>
                  <div class="row items-center full-width">
                    <div class="col">
                      <div class="text-h6 text-weight-medium">
                        {{ faq.question }}
                      </div>
                    </div>
                  </div>
                </template>

                <q-card class="glass q-pa-lg" flat>
                  <div class="text-body1 text-grey-4 line-height-lg">
                    <div v-if="typeof faq.answer === 'string'">
                      {{ faq.answer }}
                    </div>
                    <div v-else>
                      <div
                        v-for="(item, idx) in faq.answer"
                        :key="idx"
                        class="q-mb-sm"
                      >
                        • {{ item }}
                      </div>
                    </div>
                  </div>
                </q-card>
              </q-expansion-item>
            </div>
          </div>
        </div>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Icon } from '@iconify/vue';
import { faqCategoryOptions, faqData } from 'src/utils/testData.js';

const activeCategory = ref('all');
const categoryOptions = faqCategoryOptions;

const filteredFaqs = computed(() => {
  if (activeCategory.value === 'all') {
    return faqData;
  } else {
    return {
      [activeCategory.value]: faqData[activeCategory.value],
    };
  }
});
</script>

<style lang="scss" scoped>
@import '../../css/official/deep.scss';

.faq-category {
  .category-header {
    border-bottom: 2px solid rgba(255, 107, 53, 0.3);
    padding-bottom: 16px;
  }
}

.faq-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: $border-radius-md;
  border: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 16px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    border-color: rgba(255, 107, 53, 0.3);
    background: rgba(255, 255, 255, 0.08);
  }

  :deep(.q-expansion-item__container) {
    .q-expansion-item__header {
      padding: 20px;
      background: transparent;

      &:hover {
        background: rgba(255, 255, 255, 0.05);
      }
    }
  }
}
</style>
