<template>
  <q-btn
    v-show="showScrollToTop"
    fab
    color="primary"
    class="scroll-to-top-btn flex-center"
    @click="scrollToTop"
    aria-label="回到頂部"
  >
    <q-icon name="keyboard_arrow_up" />
  </q-btn>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const showScrollToTop = ref(false);

const handleScroll = () => {
  showScrollToTop.value = window.scrollY > 100;
};

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

onMounted(() => window.addEventListener('scroll', handleScroll));
onUnmounted(() => window.removeEventListener('scroll', handleScroll));
</script>

<style lang="scss" scoped>
.scroll-to-top-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
  width: 56px;
  height: 56px;
  box-shadow: 0 4px 12px rgba(240, 131, 4, 0.4);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 8px 24px rgba(240, 131, 4, 0.6);
  }

  &.q-btn--fab {
    min-width: 56px;
    min-height: 56px;
  }

  .q-icon {
    font-size: 24px !important;
    width: 24px;
    height: 24px;
  }
}

@media (max-width: 1024px) {
  .scroll-to-top-btn {
    bottom: 20px;
    right: 20px;
    width: 48px;
    height: 48px;

    &.q-btn--fab {
      min-width: 48px;
      min-height: 48px;
    }

    .q-icon {
      font-size: 20px !important;
      width: 20px;
      height: 20px;
    }
  }
}

@media (max-width: 480px) {
  .scroll-to-top-btn {
    bottom: 15px;
    right: 15px;
    width: 44px;
    height: 44px;

    &.q-btn--fab {
      min-width: 44px;
      min-height: 44px;
    }

    .q-icon {
      font-size: 18px !important;
      width: 18px;
      height: 18px;
    }
  }
}
</style>
