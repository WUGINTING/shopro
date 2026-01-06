<template>
  <q-card
    class="menu-card glass hover-scale cursor-pointer full-height"
    flat
    bordered
    @click="$emit('click')"
  >
    <div class="card-image-container">
      <q-img :src="dish.image" class="dish-image responsive-img" loading="lazy">
        <div class="image-overlay">
          <div class="overlay-content">
            <Icon icon="mdi:eye" class="view-icon" style="font-size: 2em" />
            <div class="text-body2 q-mt-sm">查看詳情</div>
          </div>
        </div>
      </q-img>
    </div>

    <q-card-section class="dish-content q-pa-lg">
      <div class="dish-header q-mb-md">
        <div class="text-h6 text-weight-bold dish-name q-mb-xs">
          {{ dish.name }}
        </div>
        <div class="text-body2 text-grey-4 dish-description">
          {{ dish.description }}
        </div>
      </div>

      <div class="dish-footer">
        <div class="price-section">
          <div class="text-h5 text-primary text-weight-bold">
            {{ dish.price }}
          </div>
        </div>

        <!-- <div class="action-buttons">
          <q-btn
            round
            flat
            size="sm"
            icon="favorite_border"
            color="grey-4"
            class="action-btn"
          >
            <q-tooltip class="bg-dark text-white">加入收藏</q-tooltip>
          </q-btn>
          <q-btn
            round
            flat
            size="sm"
            icon="add_shopping_cart"
            color="secondary"
            class="action-btn"
          >
            <q-tooltip class="bg-dark text-white">加入外燴清單</q-tooltip>
          </q-btn>
        </div> -->
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { Icon } from '@iconify/vue';

defineProps({
  dish: {
    type: Object,
    required: true,
  },
});

defineEmits(['click']);
</script>

<style lang="scss" scoped>
.menu-card {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-heavy;

    .image-overlay {
      opacity: 1;
    }

    .dish-image {
      transform: scale(1.05);
    }
  }
}

.dish-image {
  transition: transform 0.5s ease;
}

.image-overlay {
  .view-icon {
    animation: bounce 2s infinite;
  }
}

.dish-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 140px;
}

.dish-header {
  flex: 1;
}

.dish-name {
  color: $text-primary;
  line-height: 1.3;
}

.dish-description {
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}

.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.action-buttons {
  display: flex;
  gap: 8px;

  .action-btn {
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      transform: scale(1.1);
    }
  }
}

@media (max-width: 480px) {
  .dish-content {
    height: 120px;
  }

  .dish-name {
    font-size: 1rem;
  }

  .dish-description {
    font-size: 0.8rem;
  }
}
</style>
