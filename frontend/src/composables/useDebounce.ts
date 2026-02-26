/**
 * 防抖和節流工具 Composable
 * 用於搜尋輸入、按鈕點擊等場景
 */
import { ref, watch, onUnmounted } from 'vue'
import type { Ref } from 'vue'

/**
 * 防抖 Composable
 * @param value - 要防抖的響應式值
 * @param delay - 延遲時間（毫秒）
 */
export function useDebouncedRef<T>(value: Ref<T>, delay = 300): Ref<T> {
  const debouncedValue = ref(value.value) as Ref<T>
  let timeout: ReturnType<typeof setTimeout> | null = null

  watch(value, (newValue) => {
    if (timeout) {
      clearTimeout(timeout)
    }
    timeout = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  onUnmounted(() => {
    if (timeout) {
      clearTimeout(timeout)
    }
  })

  return debouncedValue
}

/**
 * 防抖函數 Composable
 * @param fn - 要防抖的函數
 * @param delay - 延遲時間（毫秒）
 */
export function useDebounce<T extends (...args: unknown[]) => unknown>(
  fn: T,
  delay = 300
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout> | null = null

  const debouncedFn = (...args: Parameters<T>) => {
    if (timeout) {
      clearTimeout(timeout)
    }
    timeout = setTimeout(() => {
      fn(...args)
    }, delay)
  }

  onUnmounted(() => {
    if (timeout) {
      clearTimeout(timeout)
    }
  })

  return debouncedFn
}

/**
 * 節流函數 Composable
 * @param fn - 要節流的函數
 * @param delay - 間隔時間（毫秒）
 */
export function useThrottle<T extends (...args: unknown[]) => unknown>(
  fn: T,
  delay = 300
): (...args: Parameters<T>) => void {
  let lastTime = 0
  let timeout: ReturnType<typeof setTimeout> | null = null

  const throttledFn = (...args: Parameters<T>) => {
    const now = Date.now()

    if (now - lastTime >= delay) {
      fn(...args)
      lastTime = now
    } else {
      // 確保最後一次調用也會執行
      if (timeout) {
        clearTimeout(timeout)
      }
      timeout = setTimeout(() => {
        fn(...args)
        lastTime = Date.now()
      }, delay - (now - lastTime))
    }
  }

  onUnmounted(() => {
    if (timeout) {
      clearTimeout(timeout)
    }
  })

  return throttledFn
}

/**
 * 防止重複點擊 Composable
 * @param fn - 要執行的異步函數
 */
export function usePreventDoubleClick<T extends (...args: unknown[]) => Promise<unknown>>(
  fn: T
) {
  const loading = ref(false)

  const wrappedFn = async (...args: Parameters<T>): Promise<ReturnType<T> | undefined> => {
    if (loading.value) {
      return undefined
    }

    loading.value = true
    try {
      return await fn(...args) as ReturnType<T>
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    execute: wrappedFn
  }
}
