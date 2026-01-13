<template>
  <div
    v-show="!hide"
    class="back-to-official-btn"
    @click="goToOfficial"
    role="button"
    tabindex="0"
    aria-label="返回官網"
  >
    <q-img
      :src="img_logo"
      alt="返回官網"
      class="logo-img"
      fit="cover"
      spinner-color="primary"
    />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  hide: {
    type: Boolean,
    default: false
  }
});

const router = useRouter();
const img_logo = '/icons/logo.jpg';

const goToOfficial = () => {
  router.push('/');
};
</script>

<style lang="scss" scoped>
.back-to-official-btn {
  position: fixed;
  bottom: 100px;
  right: 30px;
  z-index: 9990;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(240, 131, 4, 0.4);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: white;

  &:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 8px 24px rgba(240, 131, 4, 0.6);
  }

  .logo-img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
}

@media (max-width: 1024px) {
  .back-to-official-btn {
    bottom: 80px;
    right: 20px;
    width: 52px;
    height: 52px;
  }
}

@media (max-width: 480px) {
  .back-to-official-btn {
    bottom: 70px;
    right: 15px;
    width: 48px;
    height: 48px;
  }
}
</style>
