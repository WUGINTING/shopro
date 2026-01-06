<template>
  <q-card
    class="case-card glass hover-scale cursor-pointer full-height"
    flat
    bordered
    @click="$emit('click')"
  >
    <div class="card-image-container" v-if="caseData">
      <q-img
        :src="caseData.image"
        class="case-image responsive-img"
        loading="lazy"
      >
        <div class="image-overlay">
          <div class="overlay-content">
            <Icon icon="mdi:eye" class="view-icon" style="font-size: 2.5em" />
            <div class="text-body2 q-mt-sm">查看詳情</div>
          </div>
        </div>

        <div class="absolute-top-left q-pa-sm">
          <q-chip
            :color="getCategoryColor(caseData.category)"
            text-color="white"
            :label="getCategoryLabel(caseData.category)"
            class="case-category"
          />
        </div>
      </q-img>
    </div>

    <q-card-section class="case-content q-pa-lg" v-if="caseData">
      <div class="case-header q-mb-md">
        <div class="text-h6 text-weight-bold case-title q-mb-xs">
          {{ caseData.title }}
        </div>
        <div class="text-body2 text-grey-4 case-description">
          {{ caseData.description }}
        </div>
      </div>

      <div class="case-details q-mb-md">
        <div class="detail-row q-mb-xs">
          <Icon icon="mdi:calendar" class="text-secondary q-mr-sm" />
          <span class="text-body2 text-grey-4">{{ caseData.date }}</span>
        </div>
        <div class="detail-row q-mb-xs">
          <Icon icon="mdi:map-marker" class="text-secondary q-mr-sm" />
          <span class="text-body2 text-grey-4">{{ caseData.location }}</span>
        </div>
        <div class="detail-row">
          <Icon icon="mdi:account-group" class="text-secondary q-mr-sm" />
          <span class="text-body2 text-grey-4">{{ caseData.guests }} 人</span>
        </div>
      </div>

      <div class="case-footer">
        <q-btn
          flat
          color="primary"
          label="查看詳情"
          icon-right="arrow_forward"
          class="full-width case-btn"
        />
      </div>
    </q-card-section>

    <!-- 載入狀態 -->
    <q-card-section v-else class="q-pa-lg text-center">
      <q-spinner color="primary" size="2em" />
      <div class="text-body2 text-grey-4 q-mt-sm">載入中...</div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue';
import { Icon } from '@iconify/vue';

const props = defineProps({
  case: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['click']);

// 使用 computed 來確保資料安全
const caseData = computed(() => {
  return props.case && typeof props.case === 'object' ? props.case : null;
});

const getCategoryColor = category => {
  const colors = {
    corporate: 'primary',
    wedding: 'pink',
    private: 'secondary',
    birthday: 'amber',
  };
  return colors[category] || 'grey';
};

const getCategoryLabel = category => {
  const labels = {
    corporate: '企業活動',
    wedding: '婚宴慶典',
    private: '私人聚會',
    birthday: '生日派對',
  };
  return labels[category] || '其他';
};
</script>

<style lang="scss" scoped>
.case-card {
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-12px);
    box-shadow: 0 20px 40px rgba(220, 20, 60, 0.3);

    .image-overlay {
      opacity: 1;
    }

    .case-image {
      transform: scale(1.08);
    }

    .case-btn {
      background: rgba(220, 20, 60, 0.1);
    }
  }
}

.case-image {
  transition: transform 0.6s ease;
}

.image-overlay {
  .view-icon {
    animation: pulse 2s infinite;
  }
}

.case-category {
  backdrop-filter: blur(8px);
  font-weight: 600;
}

.case-content {
  display: flex;
  flex-direction: column;
  height: 200px;
}

.case-header {
  flex: 1;
}

.case-title {
  color: $text-primary;
  line-height: 1.4;
}

.case-description {
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}

.case-details {
  .detail-row {
    display: flex;
    align-items: center;
  }
}

.case-footer {
  margin-top: auto;
}

.case-btn {
  transition: all 0.3s ease;
  border-radius: $border-radius-md;
  font-weight: 600;

  &:hover {
    background: rgba(220, 20, 60, 0.15);
  }
}

@media (max-width: 480px) {
  .case-content {
    height: 180px;
  }

  .case-title {
    font-size: 1rem;
  }

  .case-description {
    font-size: 0.85rem;
  }
}
</style>
