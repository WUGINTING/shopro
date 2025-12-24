/**
 * 簡單的通知工具
 * 提供比 alert() 更好的使用者體驗
 */

type NotificationType = 'success' | 'error' | 'info' | 'warning'

interface NotificationOptions {
  message: string
  type?: NotificationType
  duration?: number
}

class NotificationService {
  private container: HTMLDivElement | null = null

  constructor() {
    this.createContainer()
  }

  private createContainer() {
    if (typeof document === 'undefined') return

    this.container = document.createElement('div')
    this.container.className = 'notification-container'
    this.container.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
    `
    document.body.appendChild(this.container)
  }

  show(options: NotificationOptions) {
    if (!this.container) return

    const { message, type = 'info', duration = 3000 } = options

    const notification = document.createElement('div')
    notification.className = `notification notification-${type}`
    notification.style.cssText = `
      background: ${this.getBackgroundColor(type)};
      color: white;
      padding: 12px 20px;
      border-radius: 4px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      min-width: 200px;
      max-width: 400px;
      word-wrap: break-word;
      animation: slideIn 0.3s ease-out;
    `
    notification.textContent = message

    this.container.appendChild(notification)

    // 自動移除
    setTimeout(() => {
      notification.style.animation = 'slideOut 0.3s ease-out'
      setTimeout(() => {
        if (this.container && this.container.contains(notification)) {
          this.container.removeChild(notification)
        }
      }, 300)
    }, duration)
  }

  private getBackgroundColor(type: NotificationType): string {
    const colors = {
      success: '#52c41a',
      error: '#ff4d4f',
      warning: '#faad14',
      info: '#1890ff'
    }
    return colors[type]
  }

  success(message: string, duration?: number) {
    this.show({ message, type: 'success', duration })
  }

  error(message: string, duration?: number) {
    this.show({ message, type: 'error', duration })
  }

  warning(message: string, duration?: number) {
    this.show({ message, type: 'warning', duration })
  }

  info(message: string, duration?: number) {
    this.show({ message, type: 'info', duration })
  }
}

// 添加動畫樣式
if (typeof document !== 'undefined') {
  const style = document.createElement('style')
  style.textContent = `
    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    @keyframes slideOut {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(100%);
        opacity: 0;
      }
    }
  `
  document.head.appendChild(style)
}

// 導出單例實例
export const notify = new NotificationService()

// 便捷函數
export function showSuccess(message: string) {
  notify.success(message)
}

export function showError(message: string) {
  notify.error(message)
}

export function showWarning(message: string) {
  notify.warning(message)
}

export function showInfo(message: string) {
  notify.info(message)
}

// 確認對話框的替代方案（仍使用原生 confirm，但可以未來替換）
export function showConfirm(message: string): boolean {
  return confirm(message)
}
