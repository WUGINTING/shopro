<template>
  <q-page class="sf-page q-pa-md q-pa-lg-lg">
    <section class="contact-hero q-pa-lg q-mb-md">
      <div class="sf-chip q-mb-sm">{{ content.contactPageBadge }}</div>
      <h1 class="sf-page-title">{{ content.contactPageTitle }}</h1>
      <p class="sf-page-lead text-preline">{{ content.contactPageLead }}</p>
    </section>

    <section class="sf-grid-auto q-mb-md">
      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">Email</div>
          <div class="text-body1">{{ content.contactEmail }}</div>
          <div class="text-caption text-grey-7 q-mt-sm text-preline">{{ content.contactEmailHint }}</div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">客服電話</div>
          <div class="text-body1">{{ content.contactPhone }}</div>
          <div class="text-caption text-grey-7 q-mt-sm text-preline">{{ content.contactPhoneHint }}</div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">營業時間</div>
          <div class="text-body1">{{ content.businessHours }}</div>
          <div class="text-caption text-grey-7 q-mt-sm text-preline">
            {{ content.contactBusinessHoursHint }}
          </div>
        </q-card-section>
      </q-card>

      <q-card bordered class="sf-card">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">聯絡地址</div>
          <div class="text-body1 text-preline">{{ content.address }}</div>
          <div class="text-caption text-grey-7 q-mt-sm text-preline">{{ content.contactAddressHint }}</div>
        </q-card-section>
      </q-card>
    </section>

    <q-banner rounded class="sf-success-note">
      <template #avatar>
        <q-icon name="support_agent" />
      </template>
      <span class="text-preline">{{ content.contactSupportNote }}</span>
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
  trackEvent('view_contact')
  void loadStoreContent()
})
</script>

<style scoped>
.contact-hero {
  border-radius: 18px;
  border: 1px solid #eadfcd;
  background:
    radial-gradient(circle at 90% 15%, rgba(228, 244, 238, 0.8), transparent 42%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.9) 0%, rgba(249, 255, 252, 0.95) 100%);
}

.text-preline {
  white-space: pre-line;
}
</style>
