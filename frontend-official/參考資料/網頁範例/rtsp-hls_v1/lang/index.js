// 多國語系管理
class LanguageManager {
    constructor() {
        this.currentLang = localStorage.getItem('language') || 'tc';
        this.languages = { tc, en };
        this.init();
    }

    init() {
        this.setLanguage(this.currentLang);
        this.bindLanguageSelector();
    }

    setLanguage(lang) {
        if (!this.languages[lang]) return;

        this.currentLang = lang;
        localStorage.setItem('language', lang);
        document.documentElement.lang = lang === 'tc' ? 'zh-TW' : 'en';

        const langData = this.languages[lang];
        
        // 更新 meta 標籤
        this.updateMetaTags(langData.meta);
        
        // 更新頁面內容
        this.updatePageContent(langData);

        // 更新選擇器
        const selector = document.getElementById('languageSelector');
        if (selector) selector.value = lang;
    }

    updateMetaTags(meta) {
        document.title = meta.title;
        const setContent = (selector, content) => {
            const el = document.querySelector(selector);
            if (el) el.content = content;
        };
        setContent('meta[name="description"]', meta.description);
        setContent('meta[property="og:title"]', meta.title);
        setContent('meta[property="og:description"]', meta.description);
        setContent('meta[name="twitter:title"]', meta.title);
        setContent('meta[name="twitter:description"]', meta.description);
    }

    updatePageContent(langData) {
        // 更新所有帶有 data-i18n 屬性的元素
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const value = this.getValue(langData, el.getAttribute('data-i18n'));
            if (value) el.textContent = value;
        });

        // 更新帶有 data-i18n-attr 的屬性（如 aria-label）
        document.querySelectorAll('[data-i18n-attr]').forEach(el => {
            const attrs = el.getAttribute('data-i18n-attr').split(',');
            attrs.forEach(attr => {
                const [attrName, key] = attr.split(':').map(s => s.trim());
                const value = this.getValue(langData, key);
                if (value) el.setAttribute(attrName, value);
            });
        });

        // 更新各個區塊
        this.updateHero(langData.hero);
        this.updateComparison(langData.comparison);
        this.updateSolution(langData.solution);
        this.updateCases(langData.cases);
        this.updateHow(langData.how);
        this.updatePlans(langData.plans);
        this.updateWhyUs(langData.whyUs);
        this.updateContact(langData.contact);
    }

    getValue(obj, path) {
        return path.split('.').reduce((curr, key) => curr?.[key], obj);
    }

    updateHero(hero) {
        const heroTitle = document.querySelector('.hero h1');
        const heroSubtitle = document.querySelector('.hero p');
        const heroCTA = document.querySelector('.hero .btn');

        if (heroTitle) {
            heroTitle.innerHTML = `<i class="bi bi-film"></i> ${hero.title}`;
        }
        if (heroSubtitle) heroSubtitle.textContent = hero.subtitle;
        if (heroCTA) heroCTA.textContent = hero.cta;
    }

    updateComparison(comparison) {
        const title = document.querySelector('.anchor-section .section-title');
        const subtitle = document.querySelector('.anchor-section .section-subtitle');

        if (title) title.textContent = comparison.title;
        if (subtitle) subtitle.textContent = comparison.subtitle;

        // 更新 Wowza 卡片
        const wowzaCard = document.querySelectorAll('.global-card')[0];
        if (wowzaCard) {
            const priceNote = wowzaCard.querySelector('.price-note');
            const features = wowzaCard.querySelectorAll('.features li');
            
            if (priceNote) priceNote.textContent = comparison.wowza.priceNote;
            features.forEach((li, idx) => {
                if (comparison.wowza.features[idx]) {
                    li.textContent = comparison.wowza.features[idx];
                }
            });
        }

        // 更新 Flussonic 卡片
        const flussonicCard = document.querySelectorAll('.global-card')[1];
        if (flussonicCard) {
            const priceNote = flussonicCard.querySelector('.price-note');
            const features = flussonicCard.querySelectorAll('.features li');
            
            if (priceNote) priceNote.textContent = comparison.flussonic.priceNote;
            features.forEach((li, idx) => {
                if (comparison.flussonic.features[idx]) {
                    li.textContent = comparison.flussonic.features[idx];
                }
            });
        }
    }

    updateSolution(solution) {
        const badge = document.querySelector('.our-solution .badge');
        const title = document.querySelector('.our-solution h2');
        const savings = document.querySelector('.savings');
        const description = document.querySelector('.our-solution p');

        if (badge) {
            badge.innerHTML = `<i class="bi bi-bullseye"></i> ${solution.badge}`;
        }
        if (title) title.textContent = solution.title;
        if (savings) {
            savings.innerHTML = `<i class="bi bi-cash-coin"></i> ${solution.savings}`;
        }
        if (description) description.innerHTML = solution.description;
    }

    updateCases(cases) {
        const title = document.querySelector('#cases .section-title');
        const subtitle = document.querySelector('#cases .section-subtitle');

        if (title) {
            title.innerHTML = `<i class="bi bi-trophy"></i> ${cases.title}`;
        }
        if (subtitle) subtitle.textContent = cases.subtitle;

        // 更新統計卡片
        const statCards = document.querySelectorAll('.stat-card');
        statCards.forEach((card, idx) => {
            if (cases.stats[idx]) {
                const label = card.querySelector('.stat-label');
                const desc = card.querySelector('.stat-desc');
                if (label) label.textContent = cases.stats[idx].label;
                if (desc) desc.textContent = cases.stats[idx].desc;
            }
        });

        // 更新應用場景
        const examplesTitle = document.querySelector('.case-examples h3');
        if (examplesTitle) examplesTitle.textContent = cases.examples.title;

        const exampleItems = document.querySelectorAll('.example-item');
        exampleItems.forEach((item, idx) => {
            if (cases.examples.items[idx]) {
                const title = item.querySelector('.example-title');
                const desc = item.querySelector('.example-desc');
                if (title) title.textContent = cases.examples.items[idx].title;
                if (desc) desc.textContent = cases.examples.items[idx].desc;
            }
        });

        // 更新客戶說明
        const customerNote = document.querySelector('.groups-note');
        if (customerNote) customerNote.innerHTML = cases.customerNote;

        // 更新實際串流展示
        const demoTitle = document.querySelector('.live-demo-section h3');
        if (demoTitle) {
            demoTitle.innerHTML = `<i class="bi bi-camera-video"></i> ${cases.demo.title}`;
        }

        const demoIntro = document.querySelector('.demo-intro');
        if (demoIntro) demoIntro.textContent = cases.demo.intro;

        // 更新 demo 卡片
        const demoCards = document.querySelectorAll('.demo-card');
        demoCards.forEach((card, idx) => {
            if (cases.demo.cards[idx]) {
                const tag = card.querySelector('.demo-tag');
                const title = card.querySelector('.demo-title');
                const button = card.querySelector('.demo-button');
                
                if (tag) tag.textContent = cases.demo.cards[idx].tag;
                if (title) title.textContent = cases.demo.cards[idx].title;
                if (button) {
                    button.innerHTML = `<i class="bi bi-camera-video-fill"></i> ${cases.demo.cards[idx].button}`;
                }
            }
        });

        // 更新 demo 說明
        const demoNotes = document.querySelectorAll('.demo-note p');
        if (demoNotes[0]) {
            demoNotes[0].innerHTML = `<i class="bi bi-lightbulb"></i> <strong>${cases.demo.note1}</strong>`;
        }
        if (demoNotes[1]) {
            demoNotes[1].textContent = cases.demo.note2;
        }
    }

    updateHow(how) {
        const title = document.querySelector('#how .section-title');
        const subtitle = document.querySelector('#how .section-subtitle');

        if (title) title.textContent = how.title;
        if (subtitle) subtitle.textContent = how.subtitle;

        // 更新步驟卡片
        const howCards = document.querySelectorAll('.how-card');
        howCards.forEach((card, idx) => {
            if (how.steps[idx]) {
                const step = card.querySelector('.how-step');
                const title = card.querySelector('h3');
                const desc = card.querySelector('p');
                
                if (step) step.textContent = how.steps[idx].step;
                if (title) title.textContent = how.steps[idx].title;
                if (desc) desc.textContent = how.steps[idx].desc;
            }
        });
    }

    updatePlans(plans) {
        const title = document.querySelector('#plans .section-title');
        const subtitle = document.querySelector('#plans .section-subtitle');

        if (title) title.textContent = plans.title;
        if (subtitle) subtitle.textContent = plans.subtitle;

        // 更新方案卡片
        const planCards = document.querySelectorAll('.plan-card');
        const planData = [plans.plan1, plans.plan2, plans.plan3];

        planCards.forEach((card, idx) => {
            const plan = planData[idx];
            if (!plan) return;

            const badge = card.querySelector('.badge');
            const name = card.querySelector('.plan-name');
            const type = card.querySelector('.plan-type');
            const unit = card.querySelector('.plan-unit');
            const features = card.querySelectorAll('.plan-features li');
            const cta = card.querySelector('.cta-button');

            if (badge) {
                const icon = badge.querySelector('i') ? badge.querySelector('i').outerHTML : '';
                badge.innerHTML = `${icon} ${plan.badge}`;
            }
            if (name) name.textContent = plan.name;
            if (type) type.textContent = plan.type;
            if (unit) unit.textContent = plan.unit;
            
            features.forEach((li, featureIdx) => {
                if (plan.features[featureIdx]) {
                    li.textContent = plan.features[featureIdx];
                }
            });
            
            if (cta) cta.textContent = plan.cta;
        });
    }

    updateWhyUs(whyUs) {
        const title = document.querySelector('.why-us .section-title');
        const subtitle = document.querySelector('.why-us .section-subtitle');

        if (title) title.textContent = whyUs.title;
        if (subtitle) subtitle.textContent = whyUs.subtitle;

        // 更新優勢卡片
        const benefits = document.querySelectorAll('.benefit');
        benefits.forEach((benefit, idx) => {
            if (whyUs.benefits[idx]) {
                const title = benefit.querySelector('h3');
                const desc = benefit.querySelector('p');
                
                if (title) title.textContent = whyUs.benefits[idx].title;
                if (desc) desc.textContent = whyUs.benefits[idx].desc;
            }
        });
    }

    updateContact(contact) {
        const title = document.querySelector('.final-cta h2');
        const subtitle = document.querySelector('.final-cta p');
        const button = document.querySelector('.final-cta-button');

        if (title) title.textContent = contact.title;
        if (subtitle) subtitle.textContent = contact.subtitle;
        if (button) {
            button.innerHTML = `<i class="bi bi-envelope-fill"></i> ${contact.button}`;
        }
    }

    bindLanguageSelector() {
        const selector = document.getElementById('languageSelector');
        if (selector) {
            selector.addEventListener('change', (e) => this.setLanguage(e.target.value));
        }
    }
}

// 初始化
window.addEventListener('DOMContentLoaded', () => {
    if (typeof tc !== 'undefined' && typeof en !== 'undefined') {
        window.languageManager = new LanguageManager();
    }
});
