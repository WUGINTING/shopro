<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="brand-hero q-pa-lg q-mb-md">
      <div class="sf-chip sf-chip--warm q-mb-sm">{{ content.brandStoryBadge }}</div>
      <h1 class="sf-page-title">{{ content.brandStoryTitle }}</h1>
      <p class="sf-page-lead text-preline">{{ content.brandStoryLead }}</p>
    </section>

    <section class="sf-grid-auto q-mb-md">
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">{{ content.brandMissionTitle }}</div>
          <p class="sf-section-subtitle text-preline">{{ content.brandMissionContent }}</p>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">{{ content.brandVisionTitle }}</div>
          <p class="sf-section-subtitle text-preline">{{ content.brandVisionContent }}</p>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">{{ content.brandValueTitle }}</div>
          <p class="sf-section-subtitle text-preline">{{ content.brandValueContent }}</p>
        </q-card-section>
      </q-card>
    </section>

    <q-banner rounded class="sf-note">
      <template #avatar>
        <q-icon name="lightbulb" />
      </template>
      <span class="text-preline">{{ content.brandStoryNote }}</span>
    </q-banner>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import storeContentConfigApi, {
  defaultStoreContentConfig,
  type StoreContentConfig
} from '@/api/storeContentConfig'
import { trackEvent } from '@/utils/tracking'

const content = ref<StoreContentConfig>(defaultStoreContentConfig())

const loadStoreContent = async () => {
  try {
    const response = await storeContentConfigApi.getStoreContentConfig()
    if (response?.data) {
      content.value = { ...content.value, ...response.data }
    }
  } catch {
    // Keep local defaults when API is unavailable.
  }
}

onMounted(() => {
  trackEvent('view_brand')
  void loadStoreContent()
})
</script>

<style scoped>
.brand-hero {
  border-radius: 18px;
  border: 1px solid #eadfcd;
  background:
    radial-gradient(circle at 85% 15%, rgba(202, 242, 231, 0.6), transparent 40%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.92) 0%, rgba(255, 250, 242, 0.95) 100%);
}

.text-preline {
  white-space: pre-line;
}
</style>
