<template>
  <div class="breadcrumb">
    <template v-for="(item, index) in items" :key="index">
      <router-link
        :to="item.to"
        :class="{
          'breadcrumb-link': true,
          'breadcrumb-current': index === items.length - 1,
        }"
      >
        {{ item.label }}
      </router-link>
      <q-icon
        v-if="index < items.length - 1"
        name="chevron_right"
        size="16px"
        class="breadcrumb-separator"
      />
    </template>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
  items: {
    type: Array,
    required: true,
    validator: items => {
      return items.every(
        item =>
          typeof item === 'object' &&
          'label' in item &&
          'to' in item &&
          typeof item.label === 'string' &&
          typeof item.to === 'string'
      );
    },
  },
});
</script>

<style lang="scss" scoped>
@import '../../css/variables.scss';

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  flex-wrap: wrap;

  .breadcrumb-link {
    color: $shop-text-secondary;
    text-decoration: none;
    transition: color 0.3s;

    &:hover {
      color: $shop-primary;
    }

    &.breadcrumb-current {
      color: $shop-text;
      font-weight: 500;
      pointer-events: none;
    }
  }

  .breadcrumb-separator {
    color: $shop-text-secondary;
  }
}
</style>
