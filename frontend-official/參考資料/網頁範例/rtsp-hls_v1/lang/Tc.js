// 繁體中文語言包
const tc = {
    meta: {
        title: 'RTSPCloud - 台灣最划算的串流服務方案',
        description: 'RTSPCloud 專業直播串流服務,只需國際大廠 1/10 的價格。提供企業私有部署與雲端代管方案,24h/7d 穩定運作,已服務 18+ 企業客戶、74+ 串流路數。'
    },
    nav: {
        streaming: '串流服務',
        cases: '成功案例',
        how: '運作原理',
        plans: '方案價格',
        contact: '聯絡我們',
        menu: '選單'
    },
    hero: {
        title: 'RTSP to HLS 直播串流服務',
        subtitle: '將您既有的 RTSP 攝影機，轉為全球通用的 HLS 網頁直播',
        cta: '查看方案 NT$ 5,000 起'
    },
    comparison: {
        title: '全球同類服務,一年要花多少錢?',
        subtitle: '看看國際大廠的收費標準(皆不含主機與流量)',
        wowza: {
            priceNote: '每年授權費用',
            features: [
                '按支計算',
                '業界標準軟體',
                '需自備主機',
                '流量另計',
                'DRM 支援'
            ]
        },
        flussonic: {
            priceNote: '每年訂閱費用',
            features: [
                '按支計算',
                '歐洲大廠',
                '需自備主機',
                '流量另計',
                '企業級功能'
            ]
        }
    },
    solution: {
        badge: '超值方案',
        title: '在台灣，您只需要...',
        savings: '省下 90% 的成本！',
        description: '首年(含安裝)只需 <strong>NT$ 5,000</strong>，隔年維護費僅 <strong>NT$ 4,500</strong><br>同樣的串流功能，卻只要國際大廠的 <strong>1/10 價格</strong>'
    },
    cases: {
        title: '成功案例實績',
        subtitle: '我們已為全台上百路攝影機提供穩定的 24h/7d 串流服務',
        stats: [
            { label: '串流路數', desc: '穩定運作中' },
            { label: '企業客戶', desc: '長期合作' },
            { label: '不間斷服務', desc: '全年無休' },
            { label: '服務穩定度', desc: '持續監控' }
        ],
        examples: {
            title: '應用場景',
            items: [
                { title: '企業監控', desc: '多點位同步監看，提升管理效率' },
                { title: '環境監測', desc: '河川、水位即時監控系統' },
                { title: '工廠產線', desc: '生產流程遠端監看與管理' },
                { title: '工地安全', desc: '施工現場 24 小時監控' }
            ]
        },
        customerNote: '我們已為 <strong>交通建設-環狀線監控、道路監控-淡北快速道路、電力設施-興達監控點</strong> 等 18+ 家企業客戶<br>提供穩定可靠的串流轉換服務',
        demo: {
            title: '實際串流展示',
            intro: '點擊下方案例，即可觀看實際運作中的串流影像',
            cards: [
                { tag: '重大建設', title: '淡江大橋', button: '觀看即時影像' },
                { tag: '交通建設', title: '環狀線監控', button: '觀看即時影像' },
                { tag: '道路監控', title: '淡北快速道路', button: '觀看即時影像' },
                { tag: '電力設施', title: '興達監控點', button: '觀看即時影像' },
                { tag: '港口設施', title: '大潭監控系統', button: '觀看即時影像' },
                { tag: '工程監控', title: '三民玉里工區', button: '觀看即時影像' }
            ],
            note1: '這些都是實際運作中的監控系統',
            note2: '採用 RTSP to HLS 轉換服務，24 小時穩定串流'
        }
    },
    how: {
        title: '運作原理',
        subtitle: '三步驟，將您的影像上線',
        steps: [
            {
                step: 'STEP 1',
                title: '提供 RTSP 來源',
                desc: '您只需提供一個可連線的 RTSP 網址 (rtsp://...)，無需更改現有設備。'
            },
            {
                step: 'STEP 2',
                title: '我們轉換處理',
                desc: '我們的串流主機 (On-Premise 或雲端) 會即時拉取影像，並轉換為 HLS 格式。'
            },
            {
                step: 'STEP 3',
                title: '全球網頁播放',
                desc: '您會獲得一個 .m3u8 網址，可在任何瀏覽器、App 或播放器中嵌入播放。'
            }
        ]
    },
    plans: {
        title: '選擇最適合您的方案',
        subtitle: '無論是私有部署或雲端代管，我們都有對應方案',
        plan1: {
            badge: '最受歡迎',
            name: '企業私有部署',
            type: 'On-Premise｜自備主機',
            unit: '/ 每路每年',
            features: [
                '客戶提供主機(實體或雲端)',
                '我們負責安裝與設定',
                '轉封裝處理(Passthrough)',
                '隔年維護費 NT$ 4,500',
                '適合有 IT 人員的企業',
                '完全掌控主機與資料'
            ],
            cta: '立即請詢'
        },
        plan2: {
            badge: '雲端方案',
            name: '雲端代管 (基礎型)',
            type: 'SaaS｜我們搞定一切',
            unit: '/ 每路每月',
            features: [
                '一站式服務，無需準備主機',
                '含每月 100 GB 流量',
                '轉封裝處理(Passthrough)',
                '超額流量 NT$ 4/GB',
                '適合中小企業、開發者',
                '快速上線，即開即用'
            ],
            cta: '立即請詢'
        },
        plan3: {
            badge: '雲端方案',
            name: '雲端代管 (專業型)',
            type: 'SaaS｜含多畫質轉碼',
            unit: '/ 每路每月',
            features: [
                '一站式服務，無需準備主機',
                '含每月 500 GB 流量',
                'ABR 多畫質轉碼',
                '超額流量 NT$ 3/GB',
                '適合專業廣播、高流量活動',
                '自動調整畫質，觀看體驗佳'
            ],
            cta: '立即諮詢'
        }
    },
    whyUs: {
        title: '為什麼選擇我們？',
        subtitle: '專注於核心，為您省下不必要的開銷',
        benefits: [
            {
                title: '超高性價比',
                desc: '只要國際方案 1/10 的價格，就能享受同等級的串流服務。我們專注於核心功能，去除不必要的溢價。'
            },
            {
                title: '在地化服務',
                desc: '台灣團隊、中文溝通、本地支援。遇到問題不用等Email時差，隨時都能找到我們。'
            },
            {
                title: '快速部署',
                desc: '專業團隊幫您處理所有技術細節，從安裝到上線，最快當天完成設定。'
            },
            {
                title: '持續維護',
                desc: '不只是賣軟體，我們提供長期維護支援，確保系統 24h/7d 穩定運作。'
            },
            {
                title: '豐富經驗',
                desc: '團隊擁有 8 年以上開發經驗，系統已穩定運作 7 年，經過時間考驗的可靠方案。'
            }
        ]
    },
    contact: {
        title: '準備好開始了嗎？',
        subtitle: '立即聯繫我們，取得專屬報價與免費技術諮詢',
        button: '聯繫我們',
        info: {
            email: '電子郵件',
            phone: '聯絡電話',
            address: '公司地址',
            addressDetail: '臺北市士林區永平街80巷16號1樓'
        },
        form: {
            title: '填寫表單聯繫我們',
            name: '姓名 *',
            namePlaceholder: '請輸入您的姓名',
            email: '電子郵件 *',
            emailPlaceholder: 'example@email.com',
            phone: '聯絡電話',
            phonePlaceholder: '0912-345-678',
            company: '公司名稱',
            companyPlaceholder: '請輸入公司名稱',
            message: '訊息內容 *',
            messagePlaceholder: '請告訴我們您的需求或問題',
            submit: '送出訊息',
            sending: '發送中...',
            success: '✓ 訊息已成功發送！我們將盡快與您聯繫。',
            error: '✗ 發送失敗，請直接寄信至 service.ginting@gmail.com 或撥打 02-88113938'
        }
    },
    footer: {
        company: '伸遠國際有限公司',
        address: '臺北市士林區永平街80巷16號1樓',
        service: 'RTSPCloud 串流服務',
        tagline: '專業的串流技術，平實的在地價格',
        contactTitle: '聯絡方式',
        quickLinks: '快速連結',
        copyright: '© 2025 伸遠國際有限公司. All rights reserved.',
        backToTop: '回到頂部'
    }
};
