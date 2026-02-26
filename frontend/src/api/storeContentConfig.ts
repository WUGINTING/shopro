import axios from './axios'
import type { ApiResponse } from './types'

export interface StoreContentConfig {
  brandStoryBadge: string
  brandStoryTitle: string
  brandStoryLead: string
  brandMissionTitle: string
  brandMissionContent: string
  brandVisionTitle: string
  brandVisionContent: string
  brandValueTitle: string
  brandValueContent: string
  brandStoryNote: string
  contactPageBadge: string
  contactPageTitle: string
  contactPageLead: string
  contactEmail: string
  contactEmailHint: string
  contactPhone: string
  contactPhoneHint: string
  businessHours: string
  contactBusinessHoursHint: string
  address: string
  contactAddressHint: string
  contactSupportNote: string
}

export const defaultStoreContentConfig = (): StoreContentConfig => ({
  brandStoryBadge: '品牌故事',
  brandStoryTitle: '從選品到出貨，打造讓人安心回購的購物體驗',
  brandStoryLead: 'Shopro 以透明、效率與服務為核心，協助消費者快速找到合適商品，並提供穩定的售後支援。',
  brandMissionTitle: '我們的使命',
  brandMissionContent: '用更清楚的資訊、更穩定的出貨流程，降低消費者做決定的成本。',
  brandVisionTitle: '我們的願景',
  brandVisionContent: '成為值得信任的線上選品平台，讓每次購買都更輕鬆。',
  brandValueTitle: '我們重視的價值',
  brandValueContent: '誠實溝通、品質把關、服務效率，以及持續優化購物流程。',
  brandStoryNote: '若你對合作、選品或品牌提案有想法，歡迎透過聯絡我們與團隊討論。',
  contactPageBadge: '聯絡我們',
  contactPageTitle: '需要協助嗎？我們會盡快回覆',
  contactPageLead: '訂單、商品、售後與合作問題都可以透過以下方式聯繫，我們會在服務時間內處理。',
  contactEmail: 'support@shopro.example',
  contactEmailHint: '建議來信附上訂單編號與問題描述，可加速處理。',
  contactPhone: '(02) 1234-5678',
  contactPhoneHint: '客服時段來電可獲得較快回覆。',
  businessHours: '週一至週五 10:00 - 18:00',
  contactBusinessHoursHint: '國定假日與例假日暫停服務。',
  address: '台北市信義區市府路 1 號',
  contactAddressHint: '如需退換貨或合作寄件，請先與客服確認收件資訊。',
  contactSupportNote: '我們重視每一則訊息，若遇到尖峰時段回覆較慢，敬請見諒。'
})

const storeContentConfigApi = {
  getStoreContentConfig: () =>
    axios.get<any, ApiResponse<StoreContentConfig>>('/system/config/store-content'),
  updateStoreContentConfig: (payload: StoreContentConfig) =>
    axios.put<any, ApiResponse<StoreContentConfig>>('/system/config/store-content', payload)
}

export default storeContentConfigApi
