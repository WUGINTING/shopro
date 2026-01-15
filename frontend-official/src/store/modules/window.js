import { defineStore } from 'pinia';
import { ref, computed, onMounted, onUnmounted } from 'vue';

export const useWindowStore = defineStore('window', () => {
  // 視窗尺寸
  const windowWidth = ref(window.innerWidth);
  const windowHeight = ref(window.innerHeight);
  
  // Drawer 狀態
  const leftDrawerOpen = ref(false);
  const rightDrawerOpen = ref(false);
  
  // Breakpoint (與 Quasar 的 breakpoint 保持一致)
  const BREAKPOINT = 1024;
  
  // 計算屬性：是否為桌面版
  const isDesktop = computed(() => windowWidth.value >= BREAKPOINT);
  
  // 計算屬性：是否為手機版
  const isMobile = computed(() => windowWidth.value < BREAKPOINT);
  
  // 更新視窗尺寸
  const updateWindowSize = () => {
    const newWidth = window.innerWidth;
    const oldWidth = windowWidth.value;
    
    windowWidth.value = newWidth;
    windowHeight.value = window.innerHeight;
    
    // 當從手機版切換到桌面版時，自動打開 drawer
    if (oldWidth < BREAKPOINT && newWidth >= BREAKPOINT) {
      leftDrawerOpen.value = true;
      rightDrawerOpen.value = true;
    }
    // 當從桌面版切換到手機版時，自動關閉 drawer
    else if (oldWidth >= BREAKPOINT && newWidth < BREAKPOINT) {
      leftDrawerOpen.value = false;
      rightDrawerOpen.value = false;
    }
  };
  
  // 初始化 drawer 狀態（根據當前視窗大小）
  const initDrawerState = () => {
    if (isDesktop.value) {
      leftDrawerOpen.value = true;
      rightDrawerOpen.value = true;
    } else {
      leftDrawerOpen.value = false;
      rightDrawerOpen.value = false;
    }
  };
  
  // 切換左側 drawer
  const toggleLeftDrawer = () => {
    leftDrawerOpen.value = !leftDrawerOpen.value;
  };
  
  // 切換右側 drawer
  const toggleRightDrawer = () => {
    rightDrawerOpen.value = !rightDrawerOpen.value;
  };
  
  // 關閉左側 drawer
  const closeLeftDrawer = () => {
    leftDrawerOpen.value = false;
  };
  
  // 關閉右側 drawer
  const closeRightDrawer = () => {
    rightDrawerOpen.value = false;
  };
  
  // 開啟左側 drawer
  const openLeftDrawer = () => {
    leftDrawerOpen.value = true;
  };
  
  // 開啟右側 drawer
  const openRightDrawer = () => {
    rightDrawerOpen.value = true;
  };
  
  // 監聽視窗大小變化
  let resizeTimer = null;
  const handleResize = () => {
    // 使用 debounce 避免過度觸發
    if (resizeTimer) {
      clearTimeout(resizeTimer);
    }
    resizeTimer = setTimeout(() => {
      updateWindowSize();
    }, 150);
  };
  
  // 初始化（在組件掛載時調用）
  const init = () => {
    initDrawerState();
    window.addEventListener('resize', handleResize);
  };
  
  // 清理（在組件卸載時調用）
  const cleanup = () => {
    window.removeEventListener('resize', handleResize);
    if (resizeTimer) {
      clearTimeout(resizeTimer);
    }
  };
  
  return {
    // State
    windowWidth,
    windowHeight,
    leftDrawerOpen,
    rightDrawerOpen,
    
    // Computed
    isDesktop,
    isMobile,
    
    // Actions
    toggleLeftDrawer,
    toggleRightDrawer,
    closeLeftDrawer,
    closeRightDrawer,
    openLeftDrawer,
    openRightDrawer,
    init,
    cleanup,
  };
});
