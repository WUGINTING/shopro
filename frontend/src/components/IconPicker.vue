<template>
  <div>
    <q-input
      :model-value="modelValue"
      :label="label"
      outlined
      readonly
      @click="showDialog = true"
      class="cursor-pointer"
    >
      <template v-slot:prepend>
        <q-icon v-if="modelValue" :name="modelValue" size="24px" />
        <q-icon v-else name="image" size="24px" color="grey-5" />
      </template>
      <template v-slot:append>
        <q-icon name="search" />
      </template>
    </q-input>

    <q-dialog v-model="showDialog" persistent @hide="resetSelection">
      <q-card style="min-width: 600px; max-width: 90vw; max-height: 80vh">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">選擇圖標</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <q-input
            v-model="searchQuery"
            outlined
            dense
            placeholder="搜尋圖標名稱"
            clearable
            class="q-mb-md"
          >
            <template v-slot:prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <div style="max-height: 500px; overflow-y: auto">
            <div v-if="filteredIcons.length === 0" class="text-center q-pa-xl text-grey">
              <q-icon name="search_off" size="48px" />
              <div class="q-mt-md">找不到匹配的圖標</div>
            </div>
            <div v-else class="row q-col-gutter-sm">
              <div
                v-for="icon in filteredIcons"
                :key="icon"
                class="col-2 col-sm-1"
                style="min-width: 60px"
              >
                <q-card
                  flat
                  bordered
                  class="cursor-pointer text-center q-pa-sm icon-card"
                  :class="{
                    'bg-primary text-white': selectedIcon === icon,
                    'bg-blue-1 border-primary': selectedIcon === icon && selectedIcon !== modelValue,
                    'bg-grey-2': selectedIcon !== icon
                  }"
                  @click="selectIcon(icon)"
                >
                  <q-icon 
                    :name="icon" 
                    size="32px" 
                    :color="selectedIcon === icon ? 'white' : 'primary'"
                  />
                  <div 
                    class="text-caption q-mt-xs ellipsis" 
                    :title="icon"
                    :class="{ 'text-white': selectedIcon === icon }"
                  >
                    {{ icon }}
                  </div>
                  <q-icon 
                    v-if="selectedIcon === icon"
                    name="check_circle" 
                    size="20px" 
                    color="white"
                    class="absolute-top-right q-ma-xs"
                  />
                </q-card>
              </div>
            </div>
          </div>
          
          <div v-if="selectedIcon" class="q-mt-md q-pa-sm bg-blue-1 rounded-borders">
            <div class="row items-center">
              <q-icon :name="selectedIcon" size="24px" color="primary" class="q-mr-sm" />
              <div class="text-body2">
                <span class="text-weight-medium">已選擇：</span>
                <span class="text-primary">{{ selectedIcon }}</span>
              </div>
            </div>
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-px-md q-pb-md">
          <q-btn flat label="清除" color="grey-7" @click="clearIcon" />
          <q-btn flat label="取消" color="grey-7" v-close-popup />
          <q-btn 
            unelevated 
            label="確定" 
            color="primary" 
            @click="confirmSelection"
            :disable="selectedIcon === modelValue"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const props = defineProps<{
  modelValue?: string
  label?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value?: string]
}>()

const showDialog = ref(false)
const searchQuery = ref('')
const selectedIcon = ref<string | undefined>(props.modelValue)

// 當對話框打開時，同步選中狀態
watch(showDialog, (isOpen) => {
  if (isOpen) {
    selectedIcon.value = props.modelValue
    searchQuery.value = ''
  }
})

// 當 modelValue 改變時，同步選中狀態
watch(() => props.modelValue, (newValue) => {
  if (showDialog.value) {
    selectedIcon.value = newValue
  }
})

// Material Icons 常用圖標列表（已去重）
const commonIcons = [
  // 購物相關
  'shopping_bag',
  'shopping_cart',
  'shopping_basket',
  'store',
  'storefront',
  'local_mall',
  'category',
  'inventory_2',
  'card_giftcard',
  'redeem',
  'local_offer',
  'sell',
  'percent',
  'monetization_on',
  'payment',
  'credit_card',
  'account_balance',
  
  // 常用操作
  'favorite',
  'favorite_border',
  'star',
  'star_border',
  'home',
  'menu',
  'apps',
  'dashboard',
  'person',
  'people',
  'group',
  'account_circle',
  'settings',
  'notifications',
  'notifications_none',
  'search',
  'filter_list',
  'sort',
  'add',
  'edit',
  'delete',
  'save',
  'cancel',
  'check',
  'close',
  'check_circle',
  'block',
  
  // 方向與導航
  'arrow_back',
  'arrow_forward',
  'arrow_upward',
  'arrow_downward',
  'chevron_left',
  'chevron_right',
  'expand_more',
  'expand_less',
  'more_vert',
  'more_horiz',
  
  // 系統操作
  'refresh',
  'sync',
  'download',
  'upload',
  'share',
  'print',
  'update',
  'autorenew',
  'cached',
  'restart_alt',
  'power',
  'power_off',
  
  // 通訊
  'email',
  'phone',
  'phone_android',
  'location_on',
  'map',
  
  // 時間日期
  'schedule',
  'event',
  'calendar_today',
  'access_time',
  'today',
  'date_range',
  'timer',
  'alarm',
  'watch_later',
  'history',
  
  // 媒體
  'image',
  'photo',
  'photo_library',
  'camera',
  'videocam',
  'music_note',
  'movie',
  'play_arrow',
  'pause',
  'stop',
  'volume_up',
  'volume_down',
  'volume_off',
  
  // 文檔
  'book',
  'library_books',
  'menu_book',
  'auto_stories',
  'article',
  'description',
  'file_copy',
  'folder',
  'folder_open',
  'attach_file',
  'link',
  
  // 科技設備
  'code',
  'computer',
  'laptop',
  'tablet',
  'watch',
  'headphones',
  'speaker',
  'tv',
  'gamepad',
  'devices',
  'cloud',
  'cloud_upload',
  'cloud_download',
  'wifi',
  'bluetooth',
  'router',
  'memory',
  'storage',
  
  // 運動健身
  'fitness_center',
  'directions_bike',
  'directions_run',
  'pool',
  'beach_access',
  'sports_soccer',
  'sports_basketball',
  'sports_tennis',
  'sports_golf',
  'sports_volleyball',
  'sports_baseball',
  'sports_football',
  'sports_hockey',
  'sports',
  'emoji_events',
  
  // 餐飲
  'local_dining',
  'restaurant',
  'coffee',
  'cake',
  'fastfood',
  'local_pizza',
  'icecream',
  
  // 交通
  'directions_car',
  'directions_bus',
  'train',
  'subway',
  'directions_walk',
  'pedal_bike',
  'flight',
  
  // 商業
  'work',
  'business',
  'apartment',
  'corporate_fare',
  'domain',
  'public',
  'language',
  'translate',
  
  // 教育
  'school',
  'science',
  'biotech',
  
  // 醫療
  'local_hospital',
  'medical_services',
  'healing',
  'psychology',
  
  // 生活
  'pets',
  'spa',
  'hotel',
  'sailing',
  
  // 特殊
  'celebration',
  'confetti',
  'party_mode',
  'local_fire_department',
  'whatshot',
  'new_releases',
  'fiber_new',
  'workspace_premium',
  'military_tech',
  
  // 圖表分析
  'trending_up',
  'trending_down',
  'show_chart',
  'bar_chart',
  'pie_chart',
  'assessment',
  'analytics',
  'insights',
  
  // 提示信息
  'lightbulb',
  'lightbulb_outline',
  'help',
  'help_outline',
  'info',
  'info_outline',
  'warning',
  'error',
  'error_outline',
  
  // 安全
  'security',
  'lock',
  'lock_open',
  'visibility',
  'visibility_off',
  'vpn_key',
  'fingerprint',
  'verified',
  'verified_user',
  'shield',
  'gavel',
  'balance',
  
  // 其他
  'restore',
  'undo',
  'redo',
  'replay',
  'loop',
  'shuffle',
  'hourglass_empty',
  'hourglass_full',
  'backup'
]

const filteredIcons = computed(() => {
  if (!searchQuery.value) {
    return commonIcons
  }
  const query = searchQuery.value.toLowerCase()
  return commonIcons.filter(icon => icon.toLowerCase().includes(query))
})

const selectIcon = (icon: string) => {
  // 如果點擊已選中的圖標，則取消選擇
  if (selectedIcon.value === icon) {
    selectedIcon.value = undefined
  } else {
    selectedIcon.value = icon
  }
}

const clearIcon = () => {
  selectedIcon.value = undefined
}

const confirmSelection = () => {
  emit('update:modelValue', selectedIcon.value)
  showDialog.value = false
}

const resetSelection = () => {
  selectedIcon.value = props.modelValue
  searchQuery.value = ''
}
</script>

<style scoped>
.icon-card {
  transition: all 0.2s ease;
  position: relative;
}

.icon-card:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.icon-card:active {
  transform: scale(0.95);
  transition: transform 0.1s ease;
}
</style>

