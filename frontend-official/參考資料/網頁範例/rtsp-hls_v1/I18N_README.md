# 多國語系 (i18n) 實作說明

## 📋 完成項目

### ✅ 1. 語言包完整實作
- **繁體中文** (`lang/Tc.js`) - 完整翻譯所有頁面內容
- **英文** (`lang/En.js`) - 完整英文翻譯

### ✅ 2. 語言管理器 (`lang/index.js`)
自動化處理所有翻譯功能：
- 自動載入語言檔案
- 切換語言時更新整個頁面
- 使用 localStorage 記住用戶語言偏好
- 自動更新 meta 標籤（SEO 友善）

### ✅ 3. 涵蓋的頁面區塊

#### 🎯 導航欄 (Navbar)
- 所有導航連結
- 語言選擇器下拉選單
- 漢堡選單 aria-label

#### 🎬 Hero 區塊
- 主標題
- 副標題
- CTA 按鈕

#### 💰 價格比較區塊
- 標題與副標題
- Wowza 和 Flussonic 卡片內容
- 所有功能列表

#### 💡 解決方案區塊
- Badge 標籤
- 標題與說明
- 節省成本訊息

#### 🏆 成功案例區塊
- 標題與副標題
- 4 個統計數據卡片
- 應用場景標題與 4 個場景說明
- 客戶案例說明
- 實際串流展示（6 個 demo 卡片）
- 說明文字

#### ⚙️ 運作原理區塊
- 標題與副標題
- 3 個步驟卡片（STEP 1, 2, 3）

#### 📦 方案價格區塊
- 標題與副標題
- 3 個方案卡片（企業私有部署、雲端基礎型、雲端專業型）
- 每個方案的所有功能列表
- CTA 按鈕

#### ⭐ 為什麼選擇我們區塊
- 標題與副標題
- 5 個優勢說明

#### 📧 聯絡我們區塊
- 標題、副標題、按鈕

#### 🦶 Footer
- 公司資訊
- 聯絡方式標題
- 快速連結標題
- 版權聲明
- 回到頂部按鈕 aria-label

## 🚀 如何測試

### 方法 1: 使用瀏覽器開啟
1. 在瀏覽器中開啟 `index.html`
2. 點擊右上角的語言選擇器
3. 切換「繁體中文」和「English」
4. 確認整個頁面內容都正確切換

### 方法 2: 使用開發者工具
1. 開啟 Chrome DevTools (F12)
2. 在 Console 中輸入：
```javascript
// 切換到英文
window.languageManager.setLanguage('en');

// 切換回中文
window.languageManager.setLanguage('tc');
```

### 方法 3: 檢查 localStorage
切換語言後，在 DevTools > Application > Local Storage 中可以看到：
```
key: language
value: tc (或 en)
```

## 📁 檔案結構

```
lang/
├── En.js       # 英文語言包
├── Tc.js       # 繁體中文語言包
└── index.js    # 語言管理器（核心邏輯）
```

## 🔧 技術特點

### 1. 自動記憶功能
使用 `localStorage` 儲存用戶選擇的語言，下次訪問時自動套用。

### 2. SEO 友善
自動更新以下 meta 標籤：
- `<title>`
- `<meta name="description">`
- Open Graph 標籤 (og:title, og:description)
- Twitter Card 標籤

### 3. 語言屬性更新
切換語言時自動更新：
```html
<html lang="zh-TW">  <!-- 中文時 -->
<html lang="en">      <!-- 英文時 -->
```

### 4. 支援 HTML 內容
可以正確處理包含 HTML 標籤的翻譯內容（如 `<strong>`, `<br>` 等）。

## 🎨 UI/UX 特性

### 語言選擇器樣式
- 美觀的圓角設計
- 懸停時顯示藍色邊框
- 焦點時顯示陰影效果
- 手機版自動居中，寬度 200px

## 📝 如何新增翻譯

### 1. 在語言包中新增內容

**Tc.js (中文):**
```javascript
const tc = {
    newSection: {
        title: '新區塊標題',
        description: '新區塊說明'
    }
};
```

**En.js (英文):**
```javascript
const en = {
    newSection: {
        title: 'New Section Title',
        description: 'New section description'
    }
};
```

### 2. 在 HTML 中使用

**簡單文字:**
```html
<h2 data-i18n="newSection.title">新區塊標題</h2>
```

**包含 HTML 的內容:**
需要在 `index.js` 的 `updatePageContent()` 中新增對應的更新函數。

## 🌍 支援的語言

目前支援：
- ✅ 繁體中文 (tc)
- ✅ English (en)

未來可擴展：
- 簡體中文 (sc)
- 日本語 (ja)
- 한국어 (ko)

## 💡 提示

1. 預設語言是繁體中文 (tc)
2. 用戶選擇的語言會自動儲存
3. 所有翻譯都已完成，可以直接使用
4. 圖示（icon）不會被翻譯，只翻譯文字內容

## 🐛 除錯

如果語言切換不正常，請檢查：
1. 語言檔案是否正確載入（檢查 Console 有無錯誤）
2. 語言包結構是否正確
3. HTML 元素的 selector 是否正確

在 Console 中可以檢查：
```javascript
// 檢查語言管理器是否存在
console.log(window.languageManager);

// 檢查語言包是否載入
console.log(tc);
console.log(en);
```
