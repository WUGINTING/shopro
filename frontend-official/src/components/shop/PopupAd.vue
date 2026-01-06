<template>
  <q-dialog v-model="isVisible" persistent>
    <q-card class="popup-card">
      <!-- 關閉按鈕 -->
      <q-btn
        icon="close"
        flat
        round
        dense
        class="close-btn"
        @click="handleClose"
      />

      <!-- 彈窗內容 -->
      <q-card-section class="text-center">
        <q-img
          :src="
            popupData.image ||
            'https://via.placeholder.com/400x200?text=New+Member+Gift'
          "
          alt="Popup Ad"
          class="popup-image"
          ratio="2"
        />
      </q-card-section>

      <q-card-section class="text-center">
        <div class="text-h5 q-mb-md">{{ popupData.title }}</div>
        <div class="text-body2 text-grey-7 q-mb-md">
          {{ popupData.description }}
        </div>
        <q-btn
          unelevated
          color="primary"
          size="lg"
          :label="popupData.buttonText || '立即領取'"
          class="full-width"
          @click="handleAction"
        />
      </q-card-section>

      <!-- 不再顯示選項 -->
      <q-card-section class="q-pt-none">
        <q-checkbox
          v-model="dontShowAgain"
          label="不再顯示此廣告"
          color="primary"
          size="sm"
          class="text-grey-7"
        />
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import cookies from 'src/utils/cookies.js';
import { PopupAdHideKey } from 'src/config/constant.js';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  popupData: {
    type: Object,
    default: () => ({
      title: '歡迎光臨 遇日小舖',
      description: '註冊會員立即獲得 $100 購物金，首購再享免運優惠！',
      image: '',
      buttonText: '立即領取',
    }),
  },
  autoShow: {
    type: Boolean,
    default: true,
  },
  delay: {
    type: Number,
    default: 1000,
  },
});

const emit = defineEmits(['update:modelValue', 'close', 'action']);

const isVisible = ref(props.modelValue);
const dontShowAgain = ref(false);

watch(
  () => props.modelValue,
  val => {
    isVisible.value = val;
  }
);

watch(isVisible, val => {
  emit('update:modelValue', val);
});

onMounted(() => {
  // 檢查用戶是否設定過不再顯示
  const hidePopupAd = cookies.get(PopupAdHideKey);
  if (hidePopupAd === 'true') {
    console.log('用戶已設定不再顯示彈窗廣告');
    return; // 不顯示彈窗
  }

  if (props.autoShow) {
    setTimeout(() => {
      isVisible.value = true;
    }, props.delay);
  }
});

const handleClose = () => {
  // 如果勾選不再顯示，將設定存到 cookie
  if (dontShowAgain.value) {
    cookies.set(PopupAdHideKey, 'true', { expires: 365 }); // 保存一年
  }

  isVisible.value = false;
  emit('close');
};

const handleAction = () => {
  emit('action', props.popupData);
  handleClose();
};
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.popup-card {
  width: 90%;
  max-width: 450px;
  border-radius: 8px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  color: #999;
}

.popup-image {
  border-radius: 4px;
  overflow: hidden;
}
</style>
