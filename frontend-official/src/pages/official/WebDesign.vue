<template>
  <q-page>
    <!-- 成功案例英雄區 -->
    <section class="hero-section hero-medium hero-red relative-position">
      <div class="hero-background">
        <q-img
          src="https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80"
          class="full-height full-width"
          style="height: 400px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">成功案例</h1>
          <p class="text-h5 text-shadow">
            每一個成功案例都是我們用心服務的見證
          </p>
        </div>
      </div>
    </section>

    <!-- 成功案例展示 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <!-- 案例分類 -->
        <div class="case-categories q-mb-xl">
          <div class="text-center">
            <q-btn-toggle
              v-model="selectedCategory"
              toggle-color="orange-8"
              unelevated
              :options="categoryOptions"
              class="category-toggle"
            />
          </div>
        </div>

        <!-- 案例卡片展示 -->
        <div
          class="row q-gutter-lg justify-center"
          v-if="filteredCases.length > 0"
        >
          <div
            v-for="(caseItem, index) in filteredCases"
            :key="index"
            class="col-12 col-sm-6 col-md-4 fade-in-up"
            :style="{ 'animation-delay': `${index * 0.1}s` }"
          >
            <CaseCard
              :case="caseItem"
              @click="openCaseDialog(caseItem)"
              class="full-height"
            />
          </div>
        </div>

        <!-- 無案例時的提示 -->
        <div v-else class="text-center q-py-xl">
          <Icon
            icon="mdi:folder-open-outline"
            style="font-size: 4em"
            class="text-grey-5 q-mb-md"
          />
          <div class="text-h6 text-grey-4">此分類暫無案例</div>
          <div class="text-body2 text-grey-5 q-mt-sm">請選擇其他分類查看</div>
        </div>
      </div>
    </section>

    <!-- 其他內容保持不變... -->
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Icon } from '@iconify/vue';
import CaseCard from 'src/components/official/CaseCard.vue';
import {
  caseCategoryOptions,
  caseStatistics,
  cases,
} from 'src/utils/testData.js';

const selectedCategory = ref('all');
const categoryOptions = caseCategoryOptions;
const statistics = caseStatistics;

const caseDialog = ref(false);
const selectedCase = ref(null);
const gallerySlide = ref(0);

const filteredCases = computed(() => {
  if (selectedCategory.value === 'all') {
    return cases;
  }
  return cases.filter(c => c.category === selectedCategory.value);
});

const openCaseDialog = caseItem => {
  selectedCase.value = caseItem;
  gallerySlide.value = 0;
  caseDialog.value = true;
};
</script>

<style lang="scss" scoped>
@import '../../css/official/deep.scss';
</style>
