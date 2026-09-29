<template>
  <q-dialog v-model="isVisible">
    <q-card v-if="ad" class="popup-card">
      <q-btn
        icon="close"
        flat
        round
        dense
        class="close-btn"
        aria-label="關閉"
        @click="close"
      />

      <component
        :is="ad.linkUrl ? 'a' : 'div'"
        :href="ad.linkUrl || undefined"
        class="popup-link"
        @click="ad.linkUrl && close()"
      >
        <q-img v-if="ad.imageUrl" :src="ad.imageUrl" :alt="ad.title" class="popup-image" />
        <q-card-section v-if="ad.title" class="text-center">
          <div class="text-h6">{{ ad.title }}</div>
        </q-card-section>
      </component>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import http from 'src/utils/request.js';

/**
 * 彈跳廣告：讀取後台設定的有效廣告（GET /api/popup-ads/active），
 * 依 displayFrequency 控制顯示次數：ONCE（只顯示一次）/ ONCE_PER_DAY（每天一次）/ EVERY_VISIT（每次造訪）
 * 沒有有效廣告時不顯示任何內容。
 */
const STORAGE_PREFIX = 'shop_popup_seen_';

const ad = ref(null);

// 只接受 http(s) 或站內路徑的連結（後端也會驗證）
const safeLink = url => (/^(https?:\/\/|\/(?!\/))/i.test(url || '') ? url : null);
const isVisible = ref(false);

// 以瀏覽器當地日期計算「每天一次」（toISOString 為 UTC 日期，台灣會在早上 8 點才換日）
const today = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
};

const storageFor = frequency => (frequency === 'EVERY_VISIT' ? sessionStorage : localStorage);

const alreadySeen = popup => {
  try {
    const seen = storageFor(popup.displayFrequency).getItem(STORAGE_PREFIX + popup.id);
    if (!seen) return false;
    return popup.displayFrequency === 'ONCE_PER_DAY' ? seen === today() : true;
  } catch (error) {
    return false;
  }
};

const markSeen = popup => {
  try {
    storageFor(popup.displayFrequency).setItem(STORAGE_PREFIX + popup.id, today());
  } catch (error) {
    // 無痕模式等情況忽略
  }
};

const close = () => {
  isVisible.value = false;
};

onMounted(async () => {
  try {
    const response = await http.get('/popup-ads/active', undefined, { silent: true });
    const list = Array.isArray(response?.data) ? response.data : [];
    const popup = list.find(item => (item.imageUrl || item.title) && !alreadySeen(item));
    if (popup) {
      ad.value = { ...popup, linkUrl: safeLink(popup.linkUrl) };
      isVisible.value = true;
      markSeen(popup);
    }
  } catch (error) {
    // 廣告載入失敗不影響頁面
  }
});
</script>

<style lang="scss" scoped>
.popup-card {
  width: 420px;
  max-width: 90vw;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
  background: rgba(255, 255, 255, 0.9);
}

.popup-link {
  display: block;
  color: inherit;
  text-decoration: none;
}

.popup-image {
  width: 100%;
  max-height: 70vh;
}
</style>
