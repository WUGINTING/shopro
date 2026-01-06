// ==================== 购物商城相关数据 ====================

// 轮播图数据 (shop/IndexPage.vue)
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

// 弹窗广告数据 (shop/IndexPage.vue)
export const shopPopupData = {
  title: '歡迎光臨 遇日小舖',
  description: '註冊會員立即獲得 $100 購物金，首購再享免運優惠！',
  image: 'https://cdn.quasar.dev/img/parallax2.jpg',
  buttonText: '立即領取',
};

// 优惠券数据 (shop/IndexPage.vue)
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

// 热销商品数据 (shop/IndexPage.vue)
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

// 最新商品数据 (shop/IndexPage.vue)
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

// 商品列表数据 (shop/product/list.vue)
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

// ==================== 首页相关数据 ====================

// 特色功能 (IndexPage.vue)
export const features = [
  {
    icon: 'mdi:chef-hat',
    title: '專業廚師',
    description: '擁有豐富經驗的專業廚師團隊，傳承正宗日式料理技法',
  },
  {
    icon: 'mdi:leaf',
    title: '新鮮食材',
    description: '堅持使用當日新鮮食材，確保每道菜品的最佳品質',
  },
  {
    icon: 'mdi:home-heart',
    title: '到府服務',
    description: '提供專業到府外燴服務，讓您在家也能享受餐廳級料理',
  },
  {
    icon: 'mdi:star',
    title: '客製菜單',
    description: '根據客戶需求量身打造專屬菜單，滿足不同場合需求',
  },
];

// 精选菜品 (IndexPage.vue)
export const featuredDishes = [
  {
    name: '招牌燒烤拼盤',
    description: '精選牛舌、雞腿、豬五花等多種燒烤，搭配特製醬料',
    price: 'NT$ 680',
    category: '招牌',
    image:
      'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: '鮮美刺身拼盤',
    description: '當日現撈海鮮製作，包含鮭魚、鮪魚、甜蝦等',
    price: 'NT$ 880',
    category: '刺身',
    image:
      'https://images.unsplash.com/photo-1563612220849-3c44be9cb414?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
  {
    name: '特色茶碗蒸',
    description: '嫩滑茶碗蒸配上蛤蜊、蝦仁，口感豐富層次分明',
    price: 'NT$ 180',
    category: '熱食',
    image:
      'https://images.unsplash.com/photo-1512058564366-18510be2db19?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
];

// 客户评价 (IndexPage.vue)
export const reviews = [
  {
    name: '陳先生',
    rating: 5,
    comment: '外燴服務非常專業，食物新鮮美味，賓客都讚不絕口！',
    date: '2024年8月',
  },
  {
    name: '李小姐',
    rating: 5,
    comment: '居酒屋氛圍很棒，料理道道精緻，是聚餐的好選擇。',
    date: '2024年7月',
  },
  {
    name: '王經理',
    rating: 5,
    comment: '公司活動選擇双台的外燴服務，同事們都很滿意，推薦！',
    date: '2024年8月',
  },
];

// ==================== 案例页面数据 ====================

// 分类选项 (CasesPage.vue)
export const caseCategoryOptions = [
  { label: '全部案例', value: 'all' },
  { label: '企業活動', value: 'corporate' },
  { label: '婚宴慶典', value: 'wedding' },
  { label: '生日派對', value: 'birthday' },
  { label: '私人聚會', value: 'private' },
];

// 统计数据 (CasesPage.vue)
export const caseStatistics = [
  { number: '500+', label: '成功案例', icon: 'mdi:check-circle' },
  { number: '98%', label: '客戶滿意度', icon: 'mdi:heart' },
  { number: '50+', label: '合作企業', icon: 'mdi:handshake' },
  { number: '5', label: '專業年資', icon: 'mdi:star' },
];

// 案例数据 (CasesPage.vue)
export const cases = [
  {
    id: 1,
    title: '科技公司年末尾牙',
    description:
      '為科技公司提供300人的年末尾牙外燴服務，融合日式與台式料理，獲得員工一致好評。',
    category: 'corporate',
    image:
      'https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-01-15',
    location: '台北信義區',
    guests: 300,
    budget: '150,000',
    services: ['日式料理', '台式熱炒', '專業服務團隊', '現場料理表演'],
    gallery: [
      'https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1514933651103-005eec06c04b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
  {
    id: 2,
    title: '浪漫戶外婚禮',
    description:
      '在陽明山上為新人打造夢幻的戶外婚禮宴席，精緻的擺盤與溫馨的氛圍讓賓客印象深刻。',
    category: 'wedding',
    image:
      'https://images.unsplash.com/photo-1519225421980-715cb0215aed?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-02-20',
    location: '陽明山',
    guests: 80,
    budget: '120,000',
    services: ['婚禮佈置', '精緻套餐', '專業攝影', '音響設備'],
    gallery: [
      'https://images.unsplash.com/photo-1519225421980-715cb0215aed?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
  {
    id: 3,
    title: '60大壽慶生會',
    description:
      '為長輩舉辦的溫馨生日聚會，準備了長輩喜愛的傳統料理與現代創新菜色。',
    category: 'birthday',
    image:
      'https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
    date: '2024-03-10',
    location: '新北板橋',
    guests: 50,
    budget: '60,000',
    services: ['生日蛋糕', '長壽麵線', '傳統料理', '家庭聚會佈置'],
    gallery: [
      'https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    ],
  },
];

// ==================== FAQ页面数据 ====================

// FAQ分类选项 (FaqPage.vue)
export const faqCategoryOptions = [
  { label: '全部問題', value: 'all' },
  { label: '外燴服務', value: 'catering' },
  { label: '餐廳用餐', value: 'dining' },
  { label: '預約相關', value: 'booking' },
  { label: '其他問題', value: 'others' },
];

// FAQ数据 (FaqPage.vue)
export const faqData = {
  catering: {
    title: '外燴服務',
    description: '關於外燴服務的相關問題',
    icon: 'mdi:truck-delivery',
    items: [
      {
        question: '外燴服務的服務範圍包括哪些地區？',
        answer:
          '我們的外燴服務主要涵蓋雲林縣及鄰近地區，包括彰化、嘉義等縣市。其他地區請與我們聯繫討論，我們會盡力配合您的需求。',
      },
      {
        question: '需要提前多久預約外燴服務？',
        answer:
          '建議至少提前一週預約，讓我們有充足時間準備食材和安排人力。如果是大型活動或特殊節日，建議提前2-4週預約。',
      },
      {
        question: '外燴服務是否包含餐具和桌椅？',
        answer:
          '我們提供基本餐具、保溫設備等。桌椅需要另外租借，我們可以協助推薦合作的租賃商，或您也可以自行準備。',
      },
      {
        question: '如何計算外燴服務的費用？',
        answer:
          '費用主要依據人數、菜單內容、服務時間和地點距離計算。我們會根據您的需求提供詳細報價，確保透明公開。',
      },
      {
        question: '可以客製化菜單嗎？',
        answer:
          '當然可以！我們會根據您的預算、人數、口味偏好和特殊需求（如素食、過敏原）來設計專屬菜單。',
      },
      {
        question: '外燴服務當天的流程是什麼？',
        answer: [
          '提前2-3小時到場佈置',
          '準備食材並開始料理',
          '按時上菜，確保食物溫度',
          '活動結束後清理現場',
          '確認客戶滿意度後離場',
        ],
      },
    ],
  },
  dining: {
    title: '餐廳用餐',
    description: '關於店內用餐的相關問題',
    icon: 'mdi:silverware-fork-knife',
    items: [
      {
        question: '餐廳的營業時間是什麼？',
        answer:
          '中午：11:00 - 13:00，下午：17:00 - 01:00。每週一公休。特殊節日營業時間可能有調整，請事先致電確認。',
      },
      {
        question: '需要預約嗎？',
        answer:
          '建議事先預約，特別是週末和節假日。我們也接受現場候位，但可能需要等待。',
      },
      {
        question: '餐廳有停車場嗎？',
        answer:
          '餐廳附近有路邊停車格，我們也與鄰近停車場合作，可為客人提供停車優惠。',
      },
      {
        question: '餐廳可以容納多少人？',
        answer:
          '我們的餐廳可容納約40-50人，有包廂可供10-15人的聚會使用。大型聚會建議提前預約。',
      },
      {
        question: '有提供素食選項嗎？',
        answer:
          '有的！我們提供多種素食料理，包括素食燒烤、蔬菜料理等。請在預約時告知素食需求。',
      },
    ],
  },
  booking: {
    title: '預約相關',
    description: '關於預約流程的相關問題',
    icon: 'mdi:calendar-clock',
    items: [
      {
        question: '如何進行預約？',
        answer:
          '您可以透過電話 05-5880870、LINE官方帳號或臉書私訊進行預約。請提供用餐日期、時間、人數等資訊。',
      },
      {
        question: '可以臨時取消或更改預約嗎？',
        answer:
          '可以的，但請至少提前一天通知我們。如果是外燴服務，建議提前3天告知以便我們調整食材準備。',
      },
      {
        question: '預約需要支付訂金嗎？',
        answer:
          '餐廳用餐不需要訂金。外燴服務視活動規模可能需要支付30%訂金以確保雙方權益。',
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
        answer: '我們接受現金、信用卡、LINE Pay、街口支付等多種付款方式。',
      },
      {
        question: '有提供發票嗎？',
        answer: '有的，我們提供統一發票，如需三聯式發票請在結帳時告知。',
      },
      {
        question: '可以自帶酒水嗎？',
        answer: '可以的，但會收取基本開瓶費。詳細費用請諮詢現場服務人員。',
      },
    ],
  },
};

// ==================== 菜单页面数据 ====================

// 菜品分类 (MenuPage.vue)
export const menuCategories = [
  {
    id: 'yakitori',
    name: '燒烤串燒',
    description: '傳統炭火燒烤，保留食材原味',
    dishes: [
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
    dishes: [
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
    dishes: [
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
    dishes: [
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

// 外烩套餐 (MenuPage.vue)
export const cateringPackages = [
  {
    name: '精緻聚會套餐',
    description: '適合小型聚會，精選經典菜色搭配',
    people: '8-12',
    duration: '2-3小時',
    dishes: 8,
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
    dishes: 15,
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
    dishes: 10,
    price: 'NT$ 8,500',
    popular: false,
    image:
      'https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
  },
];

// ==================== 关于页面数据 ====================

// 时间线数据 (AboutPage.vue)
export const timeline = [
  {
    year: '創業初期',
    title: '街弄炭烤起家',
    description:
      '以前單炭烤串燒於街弄興起的自創品牌，老闆秉持爺爺奶奶白手起家，刻苦耐勞的精神',
    icon: 'mdi:grill',
  },
  {
    year: '發展期',
    title: '行動餐車服務',
    description:
      '因應顧客多元需求，而創立行動餐車，學習一碗白飯，一顆煎蛋，在樸實簡單中，也能嚐到最幸福的味道',
    icon: 'mdi:truck',
  },
  {
    year: '2022年',
    title: '疫情轉機',
    description:
      '西元2022疫情蔓延，我們一同經歷了艱辛時刻，老闆不氣餒的把握時間，化危機為轉機到各地學習各種料理',
    icon: 'mdi:school',
  },
  {
    year: '現在',
    title: '伸遠國際成立',
    description:
      '轉型為精緻居酒屋與中日式外燴服務，提供專業到府私廚料理，讓客戶在舒適環境中享受高品質餐飲',
    icon: 'mdi:store',
  },
];

// 核心价值 (AboutPage.vue)
export const coreValues = [
  {
    icon: 'mdi:heart-multiple',
    title: '傳承精神',
    description: '傳承家族料理技藝，保留傳統美食的精髓與溫度',
  },
  {
    icon: 'mdi:lightbulb-on',
    title: '創新研發',
    description: '持續研發新菜色，融合現代烹飪技法與傳統風味',
  },
  {
    icon: 'mdi:certificate',
    title: '品質堅持',
    description: '嚴選新鮮食材，堅持每道菜品的最高品質標準',
  },
  {
    icon: 'mdi:account-heart',
    title: '用心服務',
    description: '以客為尊，用心服務每一位客戶，創造美好用餐體驗',
  },
];

// ==================== 创始人页面数据 ====================

// 服务项目 (FounderPage.vue)
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

// 统计数据 (FounderPage.vue)
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

// ==================== 轮播组件数据 ====================

// Hero轮播 (HeroCarousel.vue)
export const heroSlides = [
  {
    title: '伸遠國際',
    subtitle: '傳承家族料理精神，創新美食體驗',
    buttonText: '品牌介紹',
    buttonLink: '/about',
    buttonClass: 'btn-primary',
    image:
      'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: '專業外燴服務',
    subtitle: '到府私廚，讓每個聚會都精彩難忘',
    buttonText: '查看菜單',
    buttonLink: '/menu',
    buttonClass: 'btn-secondary',
    image:
      'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: '精緻料理',
    subtitle: '新鮮食材，匠心烹調每一道菜',
    buttonText: '成功案例',
    buttonLink: '/cases',
    buttonClass: 'btn-primary',
    image:
      'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
  },
];

// ==================== 布局组件数据 ====================

// 左侧菜单 (MainLayout.vue)
export const leftMenuItems = [
  { name: 'about', label: '品牌介紹', path: '/about', icon: 'mdi:information' },
  { name: 'shop1', label: '遇日小舖', path: '/shop1', icon: 'mdi:store' },
  {
    name: 'shop2',
    label: '遇日角鋪',
    path: '/shop2',
    icon: 'mdi:store-outline',
  },
  { name: 'menu', label: '餐點介紹', path: '/menu', icon: 'mdi:food' },
  { name: 'cases', label: '成功案例', path: '/cases', icon: 'mdi:star' },
  { name: 'faq', label: '常見問題', path: '/faq', icon: 'mdi:help-circle' },
  { name: 'contact', label: '聯絡我們', path: '/contact', icon: 'mdi:phone' },
];

// 右侧菜单 (MainLayout.vue)
export const rightMenuItems = [
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
];

// 社交链接 (MainLayout.vue)
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

// 页脚区块 (MainLayout.vue)
export const footerSections = [
  {
    title: '聯絡我們',
    items: [
      { icon: 'mdi:phone', text: '05-5880870' },
      { icon: 'mdi:map-marker', text: '648雲林縣西螺鎮光復西路333-1號' },
    ],
  },
  {
    title: '服務項目',
    items: [
      { text: '• 居酒屋餐點' },
      { text: '• 中日式外燴' },
      { text: '• 到府私廚' },
      { text: '• 活動餐飲' },
    ],
  },
  {
    title: '營業時間',
    items: [
      { text: '中午:11:00 - 13:00' },
      { text: '下午:17:00 - 01:00' },
      { text: '外燴服務:全年無休' },
    ],
  },
];

// 版权信息 (MainLayout.vue)
export const copyrightText = [
  'Copyright © 2024 伸遠國際 All rights reserved.',
  '網站設計：幸運草設計工作室',
];

// ==================== 联系页面数据 ====================

// 活动类型 (ContactPage.vue)
export const eventTypes = [
  { label: '企業活動', value: 'corporate' },
  { label: '婚宴慶典', value: 'wedding' },
  { label: '生日派對', value: 'birthday' },
  { label: '私人聚會', value: 'private' },
  { label: '其他', value: 'other' },
];
