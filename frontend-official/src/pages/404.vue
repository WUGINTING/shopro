<template>
  <div class="flex flex-center text-white" style="min-height: 100vh">
    <div class="text-center fade-in-up">
      <div class="error-icon q-mb-lg">
        <Icon icon="mdi:web-off" style="font-size: 8em; color: #dc143c" />
      </div>

      <div class="text-h1 text-weight-bold q-mb-md">404</div>
      <div class="text-h4 q-mb-lg text-grey-4">糟糕！找不到這個頁面</div>
      <div class="text-body1 text-grey-5 q-mb-xl max-width-sm q-mx-auto">
        看起來您要找的頁面不存在或已被移除。
        讓我們帶您回到首頁或查看我們的服務！
      </div>

      <div class="q-gutter-md">
        <q-btn
          class="btn-primary"
          size="lg"
          label="回到首頁"
          @click="goHome"
        />
        <q-btn
          class="btn-secondary"
          size="lg"
          label="查看服務"
          @click="goToServices"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Icon } from '@iconify/vue';

const router = useRouter();

const goHome = () => {
  router.push('/');
};

const goToServices = () => {
  router.push('/web-design');
};
</script>

<style lang="scss" scoped>
.error-icon {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}
</style>
