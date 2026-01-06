<template>
  <div class="coupon-section">
    <div class="coupon-scroll-container">
      <q-scroll-area :thumb-style="thumbStyle" horizontal class="coupon-scroll">
        <div class="coupon-list">
          <q-card
            v-for="(coupon, index) in coupons"
            :key="index"
            class="coupon-card"
            flat
            bordered
          >
            <q-card-section class="coupon-content">
              <div class="coupon-info">
                <div class="coupon-title text-h6">{{ coupon.title }}</div>
                <div class="coupon-desc text-caption text-grey-7">
                  {{ coupon.description }}
                </div>
              </div>
              <q-btn
                unelevated
                size="sm"
                color="primary"
                label="領取"
                class="coupon-btn"
                @click="handleClaim(coupon)"
              />
            </q-card-section>
          </q-card>
        </div>
      </q-scroll-area>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  coupons: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['claim']);

const thumbStyle = {
  right: '2px',
  borderRadius: '5px',
  backgroundColor: '#c5a059',
  width: '5px',
  opacity: 0.5,
};

const handleClaim = coupon => {
  emit('claim', coupon);
};
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

// =========================================
// 優惠券區塊 (參考 demo1.html)
// =========================================
.coupon-section {
  margin-bottom: 40px;
}

.coupon-scroll-container {
  width: 100%;
  overflow: hidden;
}

.coupon-scroll {
  height: 120px;

  :deep(.q-scrollarea__content) {
    display: flex;
    gap: 15px;
  }
}

.coupon-list {
  display: flex;
  gap: 15px;
  padding-bottom: 10px;
  min-width: 100%;
}

// 優惠券卡片
.coupon-card {
  min-width: 280px;
  border: 2px dashed $shop-primary;
  background: #fffcf5;
  border-radius: 8px;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(197, 160, 89, 0.2);
  }
}

.coupon-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  padding: 15px;
}

.coupon-info {
  flex: 1;
}

.coupon-title {
  color: #e74c3c;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 4px;
  line-height: 1.3;
}

.coupon-desc {
  font-size: 0.85rem;
  color: #888;
  line-height: 1.4;
}

.coupon-btn {
  min-width: 60px;
  font-weight: 600;
  padding: 6px 16px;
  border-radius: 4px;

  &:hover {
    transform: scale(1.05);
  }
}

// =========================================
// 響應式設計 - 平板
// =========================================
@media (max-width: 768px) {
  .coupon-card {
    min-width: 260px;
  }

  .coupon-title {
    font-size: 1rem;
  }

  .coupon-desc {
    font-size: 0.8rem;
  }

  .coupon-btn {
    min-width: 55px;
    font-size: 0.85rem;
  }
}

// =========================================
// 響應式設計 - 手機
// =========================================
@media (max-width: 480px) {
  .coupon-section {
    margin-bottom: 30px;
  }

  .coupon-scroll {
    height: 110px;
  }

  .coupon-card {
    min-width: 240px;
  }

  .coupon-content {
    padding: 12px;
    gap: 12px;
  }

  .coupon-title {
    font-size: 0.95rem;
  }

  .coupon-desc {
    font-size: 0.75rem;
  }

  .coupon-btn {
    min-width: 50px;
    font-size: 0.8rem;
    padding: 5px 12px;
  }
}

// =========================================
// 響應式設計 - 超小螢幕
// =========================================
@media (max-width: 360px) {
  .coupon-card {
    min-width: 220px;
  }

  .coupon-content {
    padding: 10px;
  }

  .coupon-title {
    font-size: 0.9rem;
  }

  .coupon-btn {
    font-size: 0.75rem;
    padding: 4px 10px;
  }
}
</style>
