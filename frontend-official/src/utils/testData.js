// ==================== 購物商城相關資料 ====================

// 輪播圖資料 (shop/IndexPage.vue)
export const shopHeroSlides = [
  {
    id: 1,
    title: '質感生活，由此開始',
    description: '2024 夏季新品系列，全館 85 折起',
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    buttonText: '前往選購',
  },
  {
    id: 2,
    title: '年中大促熱烈開跑',
    description: '指定商品兩件五折，錯過不再！',
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    buttonText: '立即選購',
  },
  {
    id: 3,
    title: '新會員專屬優惠',
    description: '註冊送 $100 購物金，首購再享免運',
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    buttonText: '立即註冊',
  },
];

// 彈窗廣告資料 (shop/IndexPage.vue)
export const shopPopupData = {
  title: '歡迎光臨 遇日小舖',
  description: '註冊會員立即獲得 $100 購物金，首購再享免運優惠！',
  image: 'https://cdn.quasar.dev/img/parallax2.jpg',
  buttonText: '立即領取',
};

// 優惠券資料 (shop/IndexPage.vue)
export const shopCoupons = [
  {
    id: 1,
    title: '滿千折百',
    description: '全館商品適用',
    code: 'SAVE100',
  },
  {
    id: 2,
    title: '免運券',
    description: '滿 $499 免運費',
    code: 'FREESHIP',
  },
  {
    id: 3,
    title: '會員禮',
    description: '新會員專屬',
    code: 'NEWMEMBER',
  },
  {
    id: 4,
    title: '限時優惠',
    description: '全館 85 折',
    code: 'SALE85',
  },
];

// 熱銷商品資料 (shop/IndexPage.vue)
export const shopHotProducts = [
  {
    id: 1,
    name: '極簡純棉 T-shirt',
    price: 590,
    originalPrice: 790,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    tag: 'hot',
  },
  {
    id: 2,
    name: '修身丹寧褲',
    price: 1280,
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    tag: null,
  },
  {
    id: 3,
    name: '限量聯名球鞋',
    price: 3600,
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    tag: 'pre-order',
  },
  {
    id: 4,
    name: '無線藍牙耳機',
    price: 1890,
    originalPrice: 2200,
    image: 'https://cdn.quasar.dev/img/material.png',
    tag: 'hot',
  },
  {
    id: 5,
    name: '運動健身手環',
    price: 1590,
    originalPrice: 2190,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    tag: 'hot',
  },
  {
    id: 6,
    name: '輕量化後背包',
    price: 890,
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    tag: null,
  },
];

// 最新商品資料 (shop/IndexPage.vue)
export const shopNewProducts = [
  {
    id: 11,
    name: '多功能後背包',
    price: 980,
    image: 'https://cdn.quasar.dev/img/cat.jpg',
    tag: 'new',
  },
  {
    id: 12,
    name: '不鏽鋼保溫瓶',
    price: 450,
    image: 'https://cdn.quasar.dev/img/donuts.png',
    tag: 'new',
  },
  {
    id: 13,
    name: '真皮零錢包',
    price: 650,
    image: 'https://cdn.quasar.dev/img/boy-avatar.png',
    tag: null,
  },
  {
    id: 14,
    name: '抗藍光眼鏡',
    price: 399,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    tag: null,
  },
  {
    id: 15,
    name: '智能手錶',
    price: 2990,
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    tag: 'new',
  },
  {
    id: 16,
    name: '無線充電盤',
    price: 590,
    image: 'https://cdn.quasar.dev/img/material.png',
    tag: 'new',
  },
];

// 商品列表資料 (shop/product/list.vue)
export const shopAllProducts = [
  {
    id: 'bath-ball',
    name: 'ENTRY GRADE 1/144 攻擊鋼彈(GRAND SLAM裝備型)附迷你古恩/佐諾 沐浴球 入浴劑',
    price: 499,
    image: 'https://cdn.quasar.dev/img/material.png',
    category: 'bath',
    tags: ['沐浴球', '入浴劑', '鋼彈'],
    hot: true,
    new: false,
  },
  {
    id: 'sewing-machine',
    name: '【日本直送】迷你縫紉機 - 史努比、玩具總動員',
    price: 799,
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    category: 'toy',
    tags: ['縫紉機', '玩具', '史努比'],
    hot: false,
    new: true,
  },
  {
    id: 'strawberry-dry',
    name: '【日本直送】草莓乾 - 100%純天然草莓',
    price: 350,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    category: 'food',
    tags: ['零食', '草莓', '天然'],
    hot: true,
    new: true,
  },
  {
    id: 'sample-1',
    name: '日本進口泡麵 - 豚骨拉麵',
    price: 120,
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    category: 'food',
    tags: ['泡麵', '拉麵'],
    hot: false,
    new: false,
  },
  {
    id: 'sample-2',
    name: '日系造型香氛蠟燭',
    price: 680,
    image: 'https://cdn.quasar.dev/img/material.png',
    category: 'home',
    tags: ['香氛', '蠟燭', '居家'],
    hot: true,
    new: false,
  },
  {
    id: 'sample-3',
    name: '角落生物保溫瓶 350ml',
    price: 580,
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    category: 'daily',
    tags: ['保溫瓶', '角落生物', '水壺'],
    hot: false,
    new: true,
  },
  {
    id: 'sample-4',
    name: '日式陶瓷碗組',
    price: 890,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    category: 'home',
    tags: ['陶瓷', '餐具', '碗'],
    hot: false,
    new: false,
  },
  {
    id: 'sample-5',
    name: 'KITTY 沐浴球',
    price: 399,
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    category: 'bath',
    tags: ['沐浴球', 'KITTY'],
    hot: true,
    new: true,
  },
  {
    id: 'sample-6',
    name: '日本限定巧克力禮盒',
    price: 750,
    image: 'https://cdn.quasar.dev/img/material.png',
    category: 'food',
    tags: ['巧克力', '禮盒', '限定'],
    hot: false,
    new: false,
  },
  {
    id: 'sample-7',
    name: '迪士尼公主玩偶',
    price: 1200,
    image: 'https://cdn.quasar.dev/img/parallax2.jpg',
    category: 'toy',
    tags: ['玩偶', '迪士尼', '公主'],
    hot: true,
    new: false,
  },
  {
    id: 'sample-8',
    name: '日式收納盒三件組',
    price: 560,
    image: 'https://cdn.quasar.dev/img/mountains.jpg',
    category: 'daily',
    tags: ['收納', '盒子', '日式'],
    hot: false,
    new: true,
  },
  {
    id: 'sample-9',
    name: '寶可夢沐浴組合',
    price: 899,
    image: 'https://cdn.quasar.dev/img/parallax1.jpg',
    category: 'bath',
    tags: ['沐浴', '寶可夢', '組合'],
    hot: true,
    new: true,
  },
];

// ==================== 首頁相關資料 ====================

// 特色功能 (IndexPage.vue)
export const features = [
  {
    icon: 'mdi:web',
    title: '專業團隊',
    description: '擁有豐富經驗的網站設計與開發團隊，打造頂尖數位解決方案',
  },
  {
    icon: 'mdi:responsive',
    title: '響應式設計',
    description: '確保網站在各種裝置上都能完美呈現，提供最佳使用體驗',
  },
  {
    icon: 'mdi:speedometer',
    title: '高效能',
    description: '採用最新技術架構，確保網站載入速度快速、運行流暢',
  },
  {
    icon: 'mdi:cog-outline',
    title: '客製化開發',
    description: '根據客戶需求量身打造專屬解決方案，滿足不同產業需求',
  },
];

// 精選項目 (IndexPage.vue)
export const featuredDishes = [
  {
    name: '企業形象網站',
    description: '專業企業官網設計，提升品牌形象與網路曝光度',
    price: 'NT$ 25,000 起',
    category: '熱門',
    image:
      'https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: '電商購物平台',
    description: '完整的線上購物系統，包含金流串接與訂單管理功能',
    price: 'NT$ 50,000 起',
    category: '客製',
    image:
      'https://images.unsplash.com/photo-1557821552-17105176677c?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: 'RWD響應式網頁',
    description: '跨平台完美呈現，手機、平板、電腦一次搞定',
    price: 'NT$ 18,000 起',
    category: '設計',
    image:
      'https://images.unsplash.com/photo-1498050108023-c5249f4df085?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
];

// 客戶評價 (IndexPage.vue)
export const reviews = [
  {
    name: '陳先生',
    rating: 5,
    comment: '網站設計非常專業，頁面精美且功能完善，客戶反應都很好！',
    date: '2024年8月',
  },
  {
    name: '李小姐',
    rating: 5,
    comment: '團隊很用心，從規劃到上線都很順利，是值得信賴的合作夥伴。',
    date: '2024年7月',
  },
  {
    name: '王經理',
    rating: 5,
    comment: '公司官網改版後，詢問度大增，網站效能也提升很多，推薦！',
    date: '2024年8月',
  },
];

// ==================== 案例頁面資料 ====================

// 分類選項 (CasesPage.vue)
export const caseCategoryOptions = [
  { label: '全部案例', value: 'all' },
  { label: '企業網站', value: 'corporate' },
  { label: '購物平台', value: 'ecommerce' },
  { label: '客製系統', value: 'custom' },
  { label: '形象網站', value: 'branding' },
];

// 統計數據 (CasesPage.vue)
export const caseStatistics = [
  { number: '500+', label: '成功案例', icon: 'mdi:check-circle' },
  { number: '98%', label: '客戶滿意度', icon: 'mdi:heart' },
  { number: '50+', label: '合作企業', icon: 'mdi:handshake' },
  { number: '5', label: '專業年資', icon: 'mdi:star' },
];

// 案例資料 (CasesPage.vue)
export const cases = [
  {
    id: 1,
    title: '科技公司企業官網',
    description:
      '為科技公司打造專業的企業形象網站，整合產品展示、最新消息與聯絡功能，提升品牌專業度。',
    category: 'corporate',
    image:
      'https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-01-15',
    location: '台北信義區',
    guests: 300,
    budget: '150,000',
    services: ['RWD響應式設計', 'CMS內容管理', 'SEO優化', '多語系支援'],
    gallery: [
      'https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1498050108023-c5249f4df085?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
  {
    id: 2,
    title: '精品電商購物平台',
    description:
      '為精品品牌打造完整的線上購物系統，包含會員系統、購物車、金流串接與訂單管理。',
    category: 'ecommerce',
    image:
      'https://images.unsplash.com/photo-1557821552-17105176677c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-02-20',
    location: '台中',
    guests: 80,
    budget: '250,000',
    services: ['購物車系統', '金流串接', '會員管理', '訂單追蹤'],
    gallery: [
      'https://images.unsplash.com/photo-1557821552-17105176677c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
  {
    id: 3,
    title: '預約系統客製開發',
    description:
      '為服務業客戶開發專屬的線上預約系統，整合日曆、提醒通知與客戶管理功能。',
    category: 'custom',
    image:
      'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-03-10',
    location: '新北板橋',
    guests: 50,
    budget: '180,000',
    services: ['預約管理', '自動提醒', '客戶資料庫', '報表分析'],
    gallery: [
      'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
];

// ==================== FAQ頁面資料 ====================

// FAQ分類選項 (FaqPage.vue)
export const faqCategoryOptions = [
  { label: '全部問題', value: 'all' },
  { label: '網站設計', value: 'design' },
  { label: '系統開發', value: 'development' },
  { label: '專案流程', value: 'process' },
  { label: '其他問題', value: 'others' },
];

// FAQ資料 (FaqPage.vue)
export const faqData = {
  design: {
    title: '網站設計',
    description: '關於網站設計的相關問題',
    icon: 'mdi:web',
    items: [
      {
        question: '網站設計的服務範圍包括哪些？',
        answer:
          '我們的網站設計服務包含企業形象網站、購物平台、客製化系統開發等。服務範圍涵蓋全台灣，也可以線上溝通配合遠端客戶的需求。',
      },
      {
        question: '需要提前多久開始規劃網站專案？',
        answer:
          '建議至少提前一個月開始規劃，讓我們有充足時間了解需求並進行設計。如果是大型專案或有特殊需求，建議提前2-3個月開始討論。',
      },
      {
        question: '網站設計是否包含主機與網域？',
        answer:
          '我們提供網站設計與開發服務。主機與網域可以另外購買，我們可以協助推薦合作的主機商，或協助您管理既有的主機。',
      },
      {
        question: '如何計算網站設計的費用？',
        answer:
          '費用主要依據網站規模、功能需求、設計複雜度和開發時間計算。我們會根據您的需求提供詳細報價，確保透明公開。',
      },
      {
        question: '可以客製化網站功能嗎？',
        answer:
          '當然可以！我們會根據您的預算、需求、目標受眾來設計專屬的網站功能與介面。',
      },
      {
        question: '網站開發的流程是什麼？',
        answer: [
          '需求訪談與規劃',
          '視覺設計與確認',
          '前後端程式開發',
          '測試與修正',
          '上線與教育訓練',
        ],
      },
    ],
  },
  development: {
    title: '系統開發',
    description: '關於系統開發的相關問題',
    icon: 'mdi:code-tags',
    items: [
      {
        question: '使用什麼技術開發網站？',
        answer:
          '我們使用現代化的前端框架（如 Vue.js、React）和後端技術（如 Spring Boot、Node.js），確保網站效能與安全性。',
      },
      {
        question: '網站完成後需要維護嗎？',
        answer:
          '建議定期維護，包含內容更新、安全性更新、效能優化等。我們提供年度維護合約，也可以按次計費。',
      },
      {
        question: '可以提供手機APP嗎？',
        answer:
          '可以的！我們提供手機APP開發服務，包含 iOS 和 Android 平台。',
      },
      {
        question: '網站可以整合既有系統嗎？',
        answer:
          '可以！我們有豐富的系統整合經驗，可以與ERP、CRM等既有系統進行串接。',
      },
      {
        question: '提供SEO優化服務嗎？',
        answer:
          '有的！我們在網站開發時會進行基本的SEO優化，也提供進階的SEO顧問服務。',
      },
    ],
  },
  process: {
    title: '專案流程',
    description: '關於專案流程的相關問題',
    icon: 'mdi:calendar-clock',
    items: [
      {
        question: '如何開始合作？',
        answer:
          '您可以透過電話 0988-178-713、LINE官方帳號或填寫線上表單聯繫我們。我們會安排時間進行需求訪談。',
      },
      {
        question: '可以修改設計稿嗎？',
        answer:
          '可以的！在設計階段我們會提供修改次數，確保設計符合您的期待。具體修改次數會在合約中註明。',
      },
      {
        question: '專案需要支付訂金嗎？',
        answer:
          '是的，專案開始前需要支付30%訂金，設計完成後支付40%，網站上線後支付尾款30%。',
      },
    ],
  },
  others: {
    title: '其他問題',
    description: '其他常見問題',
    icon: 'mdi:help-circle',
    items: [
      {
        question: '接受哪些付款方式？',
        answer: '我們接受銀行轉帳、信用卡、LINE Pay等付款方式。',
      },
      {
        question: '有提供發票嗎？',
        answer: '有的，我們提供統一發票，如需三聯式發票請在簽約時告知。',
      },
      {
        question: '完成的網站原始碼歸誰？',
        answer: '網站完成並結清款項後，原始碼所有權歸客戶所有，我們會提供完整的程式碼與文件。',
      },
    ],
  },
};

// ==================== 服務頁面資料 ====================

// 服務分類 (ServicePage.vue)
export const serviceCategories = [
  {
    id: 'yakitori',
    name: '燒烤串燒',
    description: '傳統炭火燒烤，保留食材原味',
    services: [
      {
        name: '招牌牛舌',
        description: '厚切牛舌搭配特調醬料，口感Q彈有嚼勁',
        price: 'NT$ 280',
        image:
          'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '澳洲牛舌、海鹽、胡椒、特製醬料',
        allergens: '無',
      },
      {
        name: '雞腿肉串',
        description: '嫩煎雞腿肉，外酥內嫩，香氣撲鼻',
        price: 'NT$ 180',
        image:
          'https://images.unsplash.com/photo-1606728035253-49e8a23146de?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '台灣土雞腿肉、蒜泥、醬油',
        allergens: '含大豆',
      },
      {
        name: '豚五花串',
        description: '油花分佈均勻的豬五花，炭火香味濃郁',
        price: 'NT$ 200',
        image:
          'https://images.unsplash.com/photo-1544025162-d76694265947?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '台灣豬五花肉、味噌醬',
        allergens: '含大豆',
      },
    ],
  },
  {
    id: 'sashimi',
    name: '新鮮刺身',
    description: '當日現撈海鮮，新鮮度第一',
    services: [
      {
        name: '綜合刺身拼盤',
        description: '鮭魚、鮪魚、甜蝦等多種海鮮組合',
        price: 'NT$ 880',
        image:
          'https://images.unsplash.com/photo-1563612220849-3c44be9cb414?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '鮭魚、鮪魚、甜蝦、海膽、山葵',
        allergens: '含甲殼類',
      },
      {
        name: '炙燒鮭魚',
        description: '表面炙燒的新鮮鮭魚，外焦內嫩',
        price: 'NT$ 380',
        image:
          'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '挪威鮭魚、檸檬、海鹽',
        allergens: '無',
      },
    ],
  },
  {
    id: 'hot_dishes',
    name: '熱食料理',
    description: '溫暖身心的熱騰騰料理',
    services: [
      {
        name: '特色茶碗蒸',
        description: '嫩滑茶碗蒸配蛤蜊蝦仁，口感層次豐富',
        price: 'NT$ 180',
        image:
          'https://images.unsplash.com/photo-1512058564366-18510be2db19?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '雞蛋、蛤蜊、蝦仁��高湯',
        allergens: '含蛋類、甲殼類',
      },
      {
        name: '日式炸雞唐揚',
        description: '酥脆外皮包裹鮮嫩雞肉，搭配特製沾醬',
        price: 'NT$ 220',
        image:
          'https://images.unsplash.com/photo-1562967916-eb82221dfb38?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '雞腿肉、太白粉、醬油、薑汁',
        allergens: '含麩質、大豆',
      },
    ],
  },
  {
    id: 'appetizers',
    name: '開胃小菜',
    description: '精緻小菜，開啟味蕾',
    services: [
      {
        name: '涼拌海帶絲',
        description: '清爽海帶絲配黃瓜，口感爽脆',
        price: 'NT$ 120',
        image:
          'https://images.unsplash.com/photo-1565299507177-b0ac66763828?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '海帶絲、黃瓜、芝麻油、醋',
        allergens: '含芝麻',
      },
      {
        name: '醃製蘿蔔',
        description: '傳統日式醃製蘿蔔，酸甜開胃',
        price: 'NT$ 100',
        image:
          'https://images.unsplash.com/photo-1606131731446-54db7c26c24b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
        ingredients: '白蘿蔔、米醋、糖、鹽',
        allergens: '無',
      },
    ],
  },
];

// 服務方案 (ServicePage.vue)
export const servicePackages = [
  {
    name: '精緻聚會套餐',
    description: '適合小型聚會，精選經典菜色搭配',
    people: '8-12',
    duration: '2-3小時',
    features: 8,
    price: 'NT$ 4,800',
    popular: true,
    image:
      'https://images.unsplash.com/photo-1574484284002-952d92456975?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: '豪華宴會套餐',
    description: '大型活動首選，豐富菜色滿足所有賓客',
    people: '20-30',
    duration: '3-4小時',
    features: 15,
    price: 'NT$ 12,800',
    popular: false,
    image:
      'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: '企業會議套餐',
    description: '商務會議專用，簡約精緻不失禮貌',
    people: '15-25',
    duration: '2小時',
    features: 10,
    price: 'NT$ 8,500',
    popular: false,
    image:
      'https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
];

// ==================== 關於頁面資料 ====================

// 時間線資料 (AboutPage.vue)
export const timeline = [
  {
    year: '創業初期',
    title: '工作室成立',
    description:
      '從小型設計工作室起步，專注於平面設計與品牌視覺規劃，累積了豐富的設計經驗',
    icon: 'mdi:palette',
  },
  {
    year: '發展期',
    title: '網頁設計服務',
    description:
      '因應數位化趨勢，開始提供網頁設計服務，學習最新的前端技術與使用者體驗設計',
    icon: 'mdi:web',
  },
  {
    year: '2022年',
    title: '技術提升',
    description:
      '西元2022疫情帶來轉機，團隊把握時間學習最新的網站開發技術，提供全方位數位解決方案',
    icon: 'mdi:code-tags',
  },
  {
    year: '現在',
    title: '伸遠國際成立',
    description:
      '轉型為專業網站架設公司，提供企業形象網站、購物平台、客製化系統開發等完整服務',
    icon: 'mdi:rocket-launch',
  },
];

// 核心價值 (AboutPage.vue)
export const coreValues = [
  {
    icon: 'mdi:heart-multiple',
    title: '用心服務',
    description: '以客戶需求為優先，用心打造每一個數位專案',
  },
  {
    icon: 'mdi:lightbulb-on',
    title: '創新思維',
    description: '持續學習最新技術，提供創新的網站解決方案',
  },
  {
    icon: 'mdi:certificate',
    title: '品質保證',
    description: '嚴格品質控管，確保每個專案都達到最高標準',
  },
  {
    icon: 'mdi:account-heart',
    title: '專業支援',
    description: '提供完善的售後服務與技術支援，確保網站長期穩定運行',
  },
];

// ==================== 創始人頁面資料 ====================

// 服務項目 (FounderPage.vue)
export const founderServices = [
  {
    icon: 'mdi:party-popper',
    title: '活動策劃',
    description: '大型企業、公司、婚喪喜慶、家庭小聚、生日派對、性別派對',
  },
  {
    icon: 'mdi:microphone',
    title: '舞台燈光音響',
    description: '專業舞台設備、燈光音響系統規劃與執行',
  },
  {
    icon: 'mdi:microphone',
    title: '節目策劃主持',
    description: '活動流程規劃、專業主持人服務',
  },
  {
    icon: 'mdi:flower',
    title: '會場佈置',
    description: '主題會場設計、氣氛營造與裝飾佈置',
  },
  {
    icon: 'mdi:glass-wine',
    title: '宴會酒飲',
    description: '精選酒類飲品、客製化調酒服務',
  },
  {
    icon: 'mdi:chef-hat',
    title: '外燴料理',
    description: '精緻燒烤、傳統合菜、創新日料',
  },
];

// 作品集 (FounderPage.vue)
export const portfolioWorks = [
  {
    category: '政治人物活動',
    items: [
      '現任雲林縣長張麗善謝票千人活動',
      '前任議長沈宗隆家族晚宴',
      '現任立法院長韓國瑜家族晚宴',
      '現任雲林縣副議長蔡詠鍀家族晚宴',
    ],
  },
  {
    category: '文化觀光活動',
    items: [
      '雲林縣西螺煙火文化觀光祭 烤大豬',
      '雲林縣西螺太平媽晚宴',
      '雲林縣西螺老大媽晚宴',
      '高雄市政府高雄展覽館',
    ],
  },
  {
    category: '藝人派對',
    items: [
      '張菲、孫鵬、庹宗康藝人派對',
      '納豆、陳漢典明星聚會',
      'LuLu、陳為民演藝圈活動',
      '謝麗金等百位藝人派對',
    ],
  },
];

// 統計數據 (FounderPage.vue)
export const founderStats = [
  {
    number: '20+',
    label: '年經驗',
  },
  {
    number: '1000+',
    label: '場活動',
  },
  {
    number: '100萬+',
    label: '單場金額',
  },
];

// ==================== 輪播組件資料 ====================

// Hero輪播 (HeroCarousel.vue)
export const heroSlides = [
  {
    title: '伸遠國際',
    subtitle: '專業網站架設，打造您的數位品牌',
    buttonText: '品牌介紹',
    buttonLink: '/about',
    buttonClass: 'btn-primary',
    image:
      'https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: '專業設計服務',
    subtitle: '從平面到網頁，全方位設計解決方案',
    buttonText: '服務項目',
    buttonLink: '/graphic-design',
    buttonClass: 'btn-secondary',
    image:
      'https://images.unsplash.com/photo-1498050108023-c5249f4df085?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: '客製化開發',
    subtitle: '打造專屬於您的網站系統',
    buttonText: '聯絡我們',
    buttonLink: '/contact',
    buttonClass: 'btn-primary',
    image:
      'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
];

// ==================== 布局組件資料 ====================

// 左側選單 (MainLayout.vue)
export const leftMenuItems = [
  { name: 'about', label: '品牌介紹', path: '/about', icon: 'mdi:information' },
  {
    name: 'graphic',
    label: '平面設計',
    path: '/graphic-design',
    icon: 'mdi:palette',
  },
  {
    name: 'print',
    label: '印刷設計',
    path: '/print-design',
    icon: 'mdi:printer',
  },
  {
    name: 'web',
    label: '網頁設計',
    icon: 'mdi:web',
    children: [
      {
        name: 'web-corporate',
        label: '形象網站',
        path: '/web-corporate',
        icon: 'mdi:briefcase',
      },
      {
        name: 'web-custom',
        label: '客製化網站',
        path: '/web-custom',
        icon: 'mdi:code-tags',
      },
      {
        name: 'web-stream',
        label: '監視器直播串接',
        path: '/web-stream',
        icon: 'mdi:video',
      },
    ],
  },
  { name: 'faq', label: '常見問題', path: '/faq', icon: 'mdi:help-circle' },
  { name: 'contact', label: '聯絡我們', path: '/contact', icon: 'mdi:phone' },
];

// 右側選單 (MainLayout.vue)
export const rightMenuItems = [
  { name: 'shop-introduce', label: '商店介紹', path: '/shop/introduce', icon: 'mdi:store' },
  { name: 'shop-news', label: '最新消息', path: '/shop/news', icon: 'mdi:newspaper' },
  {
    name: 'shop-products',
    label: '產品清單',
    icon: 'mdi:shopping',
    children: [
      {
        name: 'shop-products-all',
        label: '所有商品',
        path: '/shop/product/list?category=all',
        icon: 'mdi:view-grid',
      },
      {
        name: 'shop-products-dessert',
        label: '甜筒',
        path: '/shop/product/list?category=dessert',
        icon: 'mdi:ice-cream',
      },
      {
        name: 'shop-products-japanese',
        label: '日系產品',
        path: '/shop/product/list?category=japanese',
        icon: 'mdi:torii-gate',
      },
      {
        name: 'shop-products-other',
        label: '其他',
        path: '/shop/product/list?category=other',
        icon: 'mdi:dots-horizontal',
      },
    ],
  },
  { name: 'shop-member', label: '會員中心', path: '#', icon: 'mdi:account' },
];

// 社交連結 (MainLayout.vue)
export const socialLinks = [
  {
    name: 'facebook',
    icon: 'mdi:facebook',
    url: 'https://www.facebook.com/Double.baked.meat.stall?locale=zh_TW',
  },
  {
    name: 'instagram',
    icon: 'mdi:instagram',
    url: 'https://www.instagram.com/doublebarbecue522/',
  },
  {
    name: 'line',
    icon: 'simple-icons:line',
    url: 'https://line.me/ti/p/36RXP5pcj8',
  },
];

// 頁腳區塊 (MainLayout.vue)
export const footerSections = [
  {
    title: '聯絡我們',
    items: [
      { icon: 'mdi:phone', text: '0988-178-713' },
      { icon: 'mdi:map-marker', text: '648雲林縣西螺鎮光復西路333-1號' },
    ],
  },
  {
    title: '服務項目',
    items: [
      { text: '• 企業形象網站' },
      { text: '• 購物平台開發' },
      { text: '• 平面設計' },
      { text: '• 印刷設計' },
    ],
  },
  {
    title: '營業時間',
    items: [
      { text: '週一至週五：09:00 - 18:00' },
      { text: '週六：10:00 - 17:00' },
      { text: '週日及國定假日：休息' },
    ],
  },
];

// 版權資訊 (MainLayout.vue)
export const copyrightText = [
  'Copyright © 2024 伸遠國際 All rights reserved.',
  '網站設計：幸運草設計工作室',
];

// ==================== 聯繫頁面資料 ====================

// 活動類型 (ContactPage.vue)
export const eventTypes = [
  { label: '企業活動', value: 'corporate' },
  { label: '婚宴慶典', value: 'wedding' },
  { label: '生日派對', value: 'birthday' },
  { label: '私人聚會', value: 'private' },
  { label: '其他', value: 'other' },
];
