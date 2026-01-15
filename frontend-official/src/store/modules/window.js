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
  
  // 切換左側 drawer (手機版時關閉右側)
  const toggleLeftDrawer = () => {
    const willOpen = !leftDrawerOpen.value;
    leftDrawerOpen.value = willOpen;
    
    // 手機版且左側要開啟時，關閉右側
    if (isMobile.value && willOpen) {
      rightDrawerOpen.value = false;
    }
  };
  
  // 切換右側 drawer (手機版時關閉左側)
  const toggleRightDrawer = () => {
    const willOpen = !rightDrawerOpen.value;
    rightDrawerOpen.value = willOpen;
    
    // 手機版且右側要開啟時，關閉左側
    if (isMobile.value && willOpen) {
      leftDrawerOpen.value = false;
    }
  };
  
  // 關閉左側 drawer
  const closeLeftDrawer = () => {
    leftDrawerOpen.value = false;
  };
  
  // 關閉右側 drawer
  const closeRightDrawer = () => {
    rightDrawerOpen.value = false;
  };
  
  // 開啟左側 drawer (手機版時關閉右側)
  const openLeftDrawer = () => {
    leftDrawerOpen.value = true;
    if (isMobile.value) {
      rightDrawerOpen.value = false;
    }
  };
  
  // 開啟右側 drawer (手機版時關閉左側)
  const openRightDrawer = () => {
    rightDrawerOpen.value = true;
    if (isMobile.value) {
      leftDrawerOpen.value = false;
    }
  };
  
  // 使用 matchMedia 監聽 breakpoint 變化（立即響應）
  let mediaQuery = null;
  const handleBreakpointChange = (e) => {
    const isNowDesktop = e.matches; // matches = true 表示 >= 1024px
    const wasDesktop = isDesktop.value;
    
    if (!wasDesktop && isNowDesktop) {
      // 從手機版切換到桌面版：立即打開兩側 drawer
      leftDrawerOpen.value = true;
      rightDrawerOpen.value = true;
    } else if (wasDesktop && !isNowDesktop) {
      // 從桌面版切換到手機版：立即關閉兩側 drawer
      leftDrawerOpen.value = false;
      rightDrawerOpen.value = false;
    }
  };
  
  // 監聽視窗大小變化（用於更新寬高，使用較短的 debounce）
  let resizeTimer = null;
  const handleResize = () => {
    // 使用 debounce 避免過度觸發，但縮短延遲提升響應速度
    if (resizeTimer) {
      clearTimeout(resizeTimer);
    }
    resizeTimer = setTimeout(() => {
      windowWidth.value = window.innerWidth;
      windowHeight.value = window.innerHeight;
    }, 100); // 從 150ms 減少到 100ms
  };
  
  // 初始化（在組件掛載時調用）
  const init = () => {
    initDrawerState();
    
    // 使用 matchMedia 監聽 breakpoint 變化（無延遲）
    mediaQuery = window.matchMedia(`(min-width: ${BREAKPOINT}px)`);
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', handleBreakpointChange);
    } else {
      // 兼容舊版瀏覽器
      mediaQuery.addListener(handleBreakpointChange);
    }
    
    // 監聽視窗大小變化（更新寬高）
    window.addEventListener('resize', handleResize);
  };
  
  // 清理（在組件卸載時調用）
  const cleanup = () => {
    // 移除 matchMedia 監聽
    if (mediaQuery) {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener('change', handleBreakpointChange);
      } else {
        // 兼容舊版瀏覽器
        mediaQuery.removeListener(handleBreakpointChange);
      }
      mediaQuery = null;
    }
    
    // 移除 resize 監聽
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
