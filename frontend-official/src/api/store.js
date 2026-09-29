import http from '@/utils/request';

/**
 * 商店內容（品牌故事 / 聯絡資訊）相關 API
 *
 * 後端 GET /system/config/store-content 在管理員尚未設定某欄位時，
 * 會以內建的示範文字補上（例如 support@shopro.example）。
 * 前台不應把這些示範值當成真實聯絡資訊顯示，
 * 因此在此將「與後端預設值完全相同」的欄位視為未設定（空字串）。
 */

// 後端 SystemConfigService#createDefaultStoreContentConfig 的預設值
const BACKEND_PLACEHOLDERS = {
  brandStoryBadge: '品牌故事',
  brandStoryTitle: '從選品到出貨，打造讓人安心回購的購物體驗',
  brandStoryLead:
    'Shopro 以透明、效率與服務為核心，協助消費者快速找到合適商品，並提供穩定的售後支援。',
  brandMissionTitle: '我們的使命',
  brandMissionContent:
    '用更清楚的資訊、更穩定的出貨流程，降低消費者做決定的成本。',
  brandVisionTitle: '我們的願景',
  brandVisionContent: '成為值得信任的線上選品平台，讓每次購買都更輕鬆。',
  brandValueTitle: '我們重視的價值',
  brandValueContent: '誠實溝通、品質把關、服務效率，以及持續優化購物流程。',
  brandStoryNote:
    '若你對合作、選品或品牌提案有想法，歡迎透過聯絡我們與團隊討論。',
  contactPageBadge: '聯絡我們',
  contactPageTitle: '需要協助嗎？我們會盡快回覆',
  contactPageLead:
    '訂單、商品、售後與合作問題都可以透過以下方式聯繫，我們會在服務時間內處理。',
  contactEmail: 'support@shopro.example',
  contactEmailHint: '建議來信附上訂單編號與問題描述，可加速處理。',
  contactPhone: '(02) 1234-5678',
  contactPhoneHint: '客服時段來電可獲得較快回覆。',
  businessHours: '週一至週五 10:00 - 18:00',
  contactBusinessHoursHint: '國定假日與例假日暫停服務。',
  address: '台北市信義區市府路 1 號',
  contactAddressHint: '如需退換貨或合作寄件，請先與客服確認收件資訊。',
  contactSupportNote:
    '我們重視每一則訊息，若遇到尖峰時段回覆較慢，敬請見諒。',
};

// StoreContentConfigDTO 的所有欄位
const STORE_CONTENT_FIELDS = Object.keys(BACKEND_PLACEHOLDERS);

// 本次瀏覽期間的快取（Promise），讓 layout 與各頁面共用同一次請求
let storeContentPromise = null;

/**
 * 將後端回傳內容整理為固定欄位的物件：
 * 空白或等同後端示範預設值的欄位一律轉為空字串
 * @param {Object} raw - StoreContentConfigDTO
 * @returns {Object} 整理後的商店內容
 */
function normalizeStoreContent(raw = {}) {
  const result = {};
  STORE_CONTENT_FIELDS.forEach(key => {
    const value = typeof raw?.[key] === 'string' ? raw[key].trim() : '';
    result[key] = value && value !== BACKEND_PLACEHOLDERS[key] ? value : '';
  });
  return result;
}

/**
 * 取得商店內容設定（品牌故事、聯絡資訊）
 * 結果於本次瀏覽期間快取；請求失敗時清除快取，下次呼叫會重新請求
 * @param {Object} options
 * @param {boolean} options.force - 是否忽略快取強制重新取得
 * @returns {Promise<Object>} 整理後的 StoreContentConfigDTO
 */
export function getStoreContent({ force = false } = {}) {
  if (!storeContentPromise || force) {
    storeContentPromise = http
      .get('/system/config/store-content', undefined, { silent: true })
      .then(res => normalizeStoreContent(res?.data))
      .catch(error => {
        storeContentPromise = null;
        throw error;
      });
  }
  return storeContentPromise;
}

/**
 * 商店名稱（後端目前沒有公開的商店名稱設定，固定使用品牌名稱）
 */
export const STORE_NAME = '遇日小舖';
