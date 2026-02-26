# Shopro 電商平台 - 用戶操作體驗優化報告

**重點**: 聚焦於用戶面對的功能完整性、使用體驗、數據管理問題

---

## 📋 目錄

1. [商品管理優化](#1-商品管理優化)
2. [訂單管理優化](#2-訂單管理優化)
3. [會員管理優化](#3-會員管理優化)
4. [表單與數據驗證優化](#4-表單與數據驗證優化)
5. [搜尋和篩選優化](#5-搜尋和篩選優化)
6. [批量操作功能](#6-批量操作功能)
7. [數據導出和導入](#7-數據導出和導入)
8. [通知和反饋機制](#8-通知和反饋機制)
9. [移動端適配](#9-移動端適配)
10. [高級功能](#10-高級功能)

---

## 1. 商品管理優化

### 1.1 【功能缺失】商品圖片管理

**現狀**: 
- ❌ ProductView.vue 無圖片上傳和預覽功能
- ❌ 無圖片排序（決定展示順序）
- ❌ 無批量圖片上傳

**建議實施**:
```vue
<!-- ProductView.vue 中新增圖片管理區塊 -->
<div class="col-12">
  <q-card>
    <q-card-section>
      <div class="text-subtitle2 q-mb-md">商品圖片</div>
      
      <!-- 圖片預覽區 -->
      <div class="row q-gutter-md q-mb-md">
        <div v-for="(image, index) in form.images" :key="index" class="col-auto">
          <div class="relative-position">
            <q-img :src="image.url" style="width: 100px; height: 100px" />
            <q-btn
              round
              dense
              icon="close"
              color="negative"
              size="sm"
              class="absolute-top-right"
              @click="removeImage(index)"
            />
            <!-- 拖動排序指示 -->
            <q-icon 
              name="drag_indicator" 
              class="absolute-bottom-left q-pa-xs text-white"
            />
          </div>
        </div>
        
        <!-- 上傳按鈕 -->
        <q-file
          @update:model-value="onImageSelected"
          accept=".jpg,.jpeg,.png,.gif"
          multiple
          max-file-size="5242880"
          @rejected="onFileRejected"
        >
          <template v-slot="scope">
            <div class="column items-center justify-center" style="width: 100px; height: 100px">
              <q-icon name="add_a_photo" size="40px" color="primary" />
              <span class="text-caption">新增圖片</span>
            </div>
          </template>
        </q-file>
      </div>
      
      <!-- 拖動排序提示 -->
      <div class="text-caption text-grey-7">
        💡 提示：拖動圖片可改變展示順序
      </div>
    </q-card-section>
  </q-card>
</div>

<script setup>
// 圖片拖動排序
const onImageDragStart = (e, index) => {
  draggedImageIndex.value = index
}

const onImageDrop = (e, index) => {
  if (draggedImageIndex.value !== index) {
    const temp = form.value.images[draggedImageIndex.value]
    form.value.images[draggedImageIndex.value] = form.value.images[index]
    form.value.images[index] = temp
  }
  draggedImageIndex.value = null
}

const removeImage = (index) => {
  form.value.images.splice(index, 1)
}

const onImageSelected = (files) => {
  files.forEach(file => {
    const reader = new FileReader()
    reader.onload = (e) => {
      form.value.images.push({
        url: e.target.result,
        file: file,
        isNew: true
      })
    }
    reader.readAsDataURL(file)
  })
}
</script>
```

**預期收益**: ✅ 用戶可以完整管理商品圖片，改善商品視覺展示

---

### 1.2 【功能缺失】商品規格和 SKU 管理增強

**現狀**:
- ⚠️ ProductView.vue 有規格管理，但 UX 不夠友善
- ❌ 無法批量修改規格價格
- ❌ 無庫存預警機制

**建議實施**:
```vue
<!-- 規格管理增強 -->
<div class="col-12">
  <q-expansion-item 
    header-class="bg-primary text-white"
    icon="category"
    label="商品規格管理"
    default-opened
  >
    <!-- 規格列表表格 -->
    <q-table
      :rows="form.specifications"
      :columns="specColumns"
      row-key="id"
      flat
      bordered
      dense
    >
      <template v-slot:body-cell-stock="props">
        <q-td :props="props">
          <q-input
            v-model.number="props.row.stock"
            type="number"
            dense
            :input-class="getStockAlertClass(props.row.stock)"
            @update:model-value="onSpecStockChange(props.row)"
          />
        </q-td>
      </template>
      
      <template v-slot:body-cell-price="props">
        <q-td :props="props">
          <q-input
            v-model.number="props.row.price"
            type="number"
            prefix="$"
            dense
          />
        </q-td>
      </template>
      
      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn 
            size="sm" 
            icon="edit" 
            flat 
            @click="editSpec(props.row)"
          />
          <q-btn 
            size="sm" 
            icon="delete" 
            flat 
            color="negative"
            @click="deleteSpec(props.row.id)"
          />
        </q-td>
      </template>
    </q-table>
    
    <!-- 批量編輯工具 -->
    <q-card-section class="q-mt-md">
      <div class="text-subtitle2 q-mb-md">批量操作</div>
      <div class="row q-col-gutter-md">
        <div class="col-auto">
          <q-input
            v-model.number="batchPriceAdjust"
            type="number"
            dense
            outlined
            placeholder="價格調整 (%)"
            style="width: 120px"
            hint="正數漲價，負數降價"
          />
        </div>
        <div class="col-auto">
          <q-btn
            color="primary"
            label="應用調整"
            @click="applyBatchPriceAdjust"
          />
        </div>
      </div>
    </q-card-section>
  </q-expansion-item>
</div>

<script setup>
const getStockAlertClass = (stock) => {
  if (stock <= 5) return 'text-negative'  // 紅色警告
  if (stock <= 10) return 'text-warning'  // 黃色警告
  return ''
}

const applyBatchPriceAdjust = () => {
  const percentage = batchPriceAdjust.value
  form.value.specifications.forEach(spec => {
    spec.price = spec.price * (1 + percentage / 100)
  })
  $q.notify({
    type: 'positive',
    message: `已調整 ${form.value.specifications.length} 個規格的價格`
  })
}
</script>
```

**預期收益**: ✅ 提升規格管理效率，減少手動操作

---

### 1.3 【UX改進】商品搜尋結果優化

**現狀**:
- ⚠️ 搜尋功能基本，但無高級篩選
- ❌ 搜尋結果無排序選項（按名稱、價格、熱度）
- ❌ 無搜尋歷史

**建議實施**:
```vue
<div class="row q-col-gutter-md">
  <!-- 搜尋框 -->
  <div class="col-12 col-md-8">
    <q-input
      v-model="searchQuery"
      outlined
      dense
      placeholder="輸入商品名稱、SKU..."
      @keyup.enter="performSearch"
    >
      <template v-slot:prepend>
        <q-icon name="search" />
      </template>
      <!-- 搜尋建議下拉 -->
      <q-menu v-if="searchSuggestions.length" auto-close>
        <q-list>
          <q-item 
            v-for="suggestion in searchSuggestions" 
            :key="suggestion" 
            clickable
            @click="searchQuery = suggestion; performSearch()"
          >
            <q-item-section>{{ suggestion }}</q-item-section>
          </q-item>
        </q-list>
      </q-menu>
    </q-input>
  </div>
  
  <!-- 排序選項 -->
  <div class="col-12 col-md-4">
    <q-select
      v-model="sortBy"
      outlined
      dense
      :options="sortOptions"
      label="排序方式"
      @update:model-value="performSearch"
    />
  </div>
</div>

<!-- 搜尋結果統計 -->
<div v-if="searchPerformed" class="text-caption text-grey-7 q-mt-md">
  找到 {{ searchResults.length }} 個結果 (用時 {{ searchTime }}ms)
</div>

<script setup>
const sortOptions = [
  { label: '依名稱 (A-Z)', value: 'name_asc' },
  { label: '依名稱 (Z-A)', value: 'name_desc' },
  { label: '依價格 (低→高)', value: 'price_asc' },
  { label: '依價格 (高→低)', value: 'price_desc' },
  { label: '依熱度', value: 'views_desc' },
  { label: '依最新上架', value: 'created_desc' }
]

const performSearch = async () => {
  const startTime = performance.now()
  // API 調用...
  const endTime = performance.now()
  searchTime.value = Math.round(endTime - startTime)
  searchPerformed.value = true
}
</script>
```

**預期收益**: ✅ 提升搜尋效率，用戶更快找到商品

---

## 2. 訂單管理優化

### 2.1 【功能缺失】訂單狀態流程可視化

**現狀**:
- ⚠️ 無視覺化的訂單流程
- ❌ 用戶不清楚訂單現在的狀態和下一步是什麼
- ❌ 無操作建議

**建議實施**:
```vue
<!-- 訂單詳情頁面 - 訂單狀態時間線 -->
<q-card class="q-mb-lg">
  <q-card-section>
    <div class="text-h6 q-mb-md">訂單狀態時間線</div>
    
    <q-timeline
      color="primary"
      layout="dense"
    >
      <!-- 已完成的狀態 -->
      <q-timeline-entry
        v-for="event in completedStatusEvents"
        :key="event.id"
        :title="event.status"
        :subtitle="formatDateTime(event.timestamp)"
        icon="check_circle"
        color="positive"
      >
        <div class="text-caption">{{ event.note }}</div>
      </q-timeline-entry>
      
      <!-- 當前狀態 -->
      <q-timeline-entry
        v-if="currentStatus"
        :title="currentStatus.name"
        subtitle="目前狀態"
        icon="access_time"
        color="primary"
      >
        <div class="text-caption q-mb-md">{{ currentStatus.description }}</div>
        <!-- 下一步建議 -->
        <div class="bg-info q-pa-md rounded-borders">
          <div class="text-caption text-weight-bold q-mb-sm">建議操作:</div>
          <div class="text-caption">
            <div v-for="action in currentStatus.suggestedActions" :key="action">
              ✓ {{ action }}
            </div>
          </div>
        </div>
      </q-timeline-entry>
      
      <!-- 未來的狀態 -->
      <q-timeline-entry
        v-for="event in upcomingStatusEvents"
        :key="event.id"
        :title="event.status"
        icon="schedule"
        color="grey-5"
      >
        <div class="text-caption text-grey">預期流程</div>
      </q-timeline-entry>
    </q-timeline>
  </q-card-section>
</q-card>

<script setup>
const orderStatusFlow = {
  PENDING: {
    name: '待付款',
    description: '訂單已建立，等待顧客付款',
    suggestedActions: [
      '等待顧客付款',
      '如超過24小時未付款，系統將自動取消',
      '可手動取消訂單'
    ],
    nextStatus: ['PAID', 'CANCELLED']
  },
  PAID: {
    name: '已付款',
    description: '已收到付款，準備打包',
    suggestedActions: [
      '審核商品庫存',
      '準備打包',
      '安排物流'
    ],
    nextStatus: ['SHIPPED', 'CANCELLED']
  },
  SHIPPED: {
    name: '已出貨',
    description: '物品已交付物流商',
    suggestedActions: [
      '通知客戶物流信息',
      '監控包裹狀態'
    ],
    nextStatus: ['DELIVERED', 'RETURNED']
  },
  DELIVERED: {
    name: '已送達',
    description: '顧客已收到商品',
    suggestedActions: [
      '確認交付',
      '邀請評價'
    ],
    nextStatus: ['COMPLETED']
  },
  COMPLETED: {
    name: '已完成',
    description: '訂單已成功完成',
    suggestedActions: [],
    nextStatus: []
  }
}
</script>
```

**預期收益**: ✅ 用戶清楚訂單進度，提升信任度

---

### 2.2 【功能缺失】訂單操作快速工具欄

**現狀**:
- ❌ 無快速操作按鈕
- ❌ 批量操作不便
- ❌ 常見操作（打印、發貨、退款）分散在各個地方

**建議實施**:
```vue
<!-- 訂單列表 - 快速操作工具欄 -->
<q-card class="q-mb-md bg-primary text-white">
  <q-card-section class="row items-center justify-between">
    <!-- 操作統計 -->
    <div>
      已選擇 <strong>{{ selectedOrders.length }}</strong> 個訂單
    </div>
    
    <!-- 快速操作按鈕 -->
    <div class="row q-gutter-sm">
      <!-- 標記為已支付 -->
      <q-btn
        v-if="canMarkAsPaid"
        outline
        dense
        label="標記為已支付"
        @click="batchMarkAsPaid"
      />
      
      <!-- 生成發貨單 -->
      <q-btn
        v-if="canGenerateShipping"
        outline
        dense
        label="生成發貨單"
        @click="batchGenerateShipping"
      />
      
      <!-- 批量打印 -->
      <q-btn
        outline
        dense
        icon="print"
        @click="batchPrint"
      >
        <q-tooltip>批量打印訂單單據</q-tooltip>
      </q-btn>
      
      <!-- 導出選中 -->
      <q-btn
        outline
        dense
        icon="download"
        @click="batchExport"
      >
        <q-tooltip>導出為 Excel</q-tooltip>
      </q-btn>
      
      <!-- 批量操作菜單 -->
      <q-btn
        outline
        dense
        icon="more_vert"
      >
        <q-menu>
          <q-list>
            <q-item clickable @click="batchAddNote">
              <q-item-section avatar>
                <q-icon name="note_add" />
              </q-item-section>
              <q-item-section>新增備註</q-item-section>
            </q-item>
            <q-item clickable @click="batchChangeStatus">
              <q-item-section avatar>
                <q-icon name="edit" />
              </q-item-section>
              <q-item-section>更改狀態</q-item-section>
            </q-item>
            <q-separator />
            <q-item clickable @click="batchDelete">
              <q-item-section avatar>
                <q-icon name="delete" color="negative" />
              </q-item-section>
              <q-item-section>刪除訂單</q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-btn>
    </div>
  </q-card-section>
</q-card>

<script setup>
const selectedOrders = ref([])

const canMarkAsPaid = computed(() => 
  selectedOrders.value.every(o => o.status === 'PENDING')
)

const canGenerateShipping = computed(() =>
  selectedOrders.value.every(o => o.status === 'PAID')
)

const batchMarkAsPaid = async () => {
  const orderIds = selectedOrders.value.map(o => o.id)
  await orderApi.batchUpdateStatus(orderIds, 'PAID')
  $q.notify({ type: 'positive', message: '已標記為已支付' })
  selectedOrders.value = []
  loadOrders()
}

const batchGenerateShipping = async () => {
  const orderIds = selectedOrders.value.map(o => o.id)
  const shippingLabels = await orderApi.batchGenerateShippingLabels(orderIds)
  // 下載文件或打開預覽...
}

const batchPrint = () => {
  const orderIds = selectedOrders.value.map(o => o.id)
  window.open(`/api/orders/print?ids=${orderIds.join(',')}`)
}
</script>
```

**預期收益**: ✅ 批量操作效率提升 **3-5倍**，員工生產力大幅提高

---

### 2.3 【功能缺失】訂單退貨/退款流程

**現狀**:
- ❌ 完全沒有退貨管理功能
- ❌ 無退款工作流

**建議實施 - 新增 ReturnManagementView.vue**:
```vue
<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <div class="text-h5 q-mb-md">退貨管理</div>
      
      <!-- 退貨申請列表 -->
      <q-table
        :rows="returns"
        :columns="returnColumns"
        row-key="id"
      >
        <!-- 狀態顏色編碼 -->
        <template v-slot:body-cell-status="props">
          <q-td :props="props">
            <q-badge :color="getReturnStatusColor(props.row.status)">
              {{ props.row.status }}
            </q-badge>
          </q-td>
        </template>
        
        <!-- 操作 -->
        <template v-slot:body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              v-if="props.row.status === 'PENDING_APPROVAL'"
              size="sm"
              flat
              color="positive"
              icon="check"
              @click="approveReturn(props.row)"
            >
              <q-tooltip>批准退貨</q-tooltip>
            </q-btn>
            <q-btn
              v-if="props.row.status === 'PENDING_APPROVAL'"
              size="sm"
              flat
              color="negative"
              icon="close"
              @click="rejectReturn(props.row)"
            >
              <q-tooltip>拒絕退貨</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </div>
  </q-page>
</template>

<script setup>
const returnColumns = [
  { name: 'id', label: '申請編號', field: 'id' },
  { name: 'orderId', label: '訂單號', field: 'orderId' },
  { name: 'reason', label: '退貨原因', field: 'reason' },
  { name: 'status', label: '狀態', field: 'status' },
  { name: 'createdAt', label: '申請日期', field: 'createdAt' },
  { name: 'actions', label: '操作', field: 'actions' }
]

const getReturnStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'blue',
    'RETURNED': 'positive',
    'REFUNDED': 'positive',
    'REJECTED': 'negative'
  }
  return colors[status] || 'grey'
}
</script>
```

**預期收益**: ✅ 完整的售後管理流程，提升客戶滿意度

---

## 3. 會員管理優化

### 3.1 【功能缺失】會員標籤和分組自動化

**現狀**:
- ⚠️ 手動分組，無法自動化
- ❌ 無基於行為的自動標籤化
- ❌ 無會員標籤

**建議實施**:
```vue
<!-- 會員標籤管理 (新增頁面) -->
<q-card>
  <q-card-section>
    <div class="text-h6 q-mb-md">會員標籤</div>
    
    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-auto" v-for="tag in memberTags" :key="tag.id">
        <q-chip
          removable
          :color="tag.color"
          text-color="white"
          @remove="deleteTag(tag.id)"
        >
          {{ tag.name }}
          <q-badge 
            v-if="tag.memberCount"
            floating
            rounded
          >
            {{ tag.memberCount }}
          </q-badge>
        </q-chip>
      </div>
      
      <!-- 新增標籤按鈕 -->
      <q-btn
        flat
        round
        dense
        icon="add"
        @click="showNewTagDialog = true"
      />
    </div>
  </q-card-section>
</q-card>

<!-- 自動化規則 -->
<q-card>
  <q-card-section>
    <div class="text-h6 q-mb-md">自動標籤化規則</div>
    
    <q-list bordered separator>
      <q-item 
        v-for="rule in autoTagRules"
        :key="rule.id"
      >
        <q-item-section>
          <q-item-label>{{ rule.name }}</q-item-label>
          <q-item-label caption>
            條件: {{ rule.condition }}
          </q-item-label>
        </q-item-section>
        <q-item-section side top>
          <q-toggle
            v-model="rule.enabled"
            @update:model-value="updateRule(rule)"
          />
        </q-item-section>
      </q-item>
    </q-list>
    
    <!-- 新增規則 -->
    <q-btn
      class="full-width q-mt-md"
      color="primary"
      label="新增自動化規則"
      @click="showRuleDialog = true"
    />
  </q-card-section>
</q-card>

<script setup>
// 自動化規則示例
const autoTagRules = ref([
  {
    id: 1,
    name: '高價值客戶',
    condition: '消費金額 > 10000',
    enabled: true,
    tagId: 1
  },
  {
    id: 2,
    name: '活躍客戶',
    condition: '近30天購買 >= 3 次',
    enabled: true,
    tagId: 2
  },
  {
    id: 3,
    name: '休眠客戶',
    condition: '距上次購買 > 90 天',
    enabled: true,
    tagId: 3
  }
])
</script>
```

**預期收益**: ✅ 自動化會員分類，支持精準行銷

---

### 3.2 【功能缺失】會員成長值和等級系統

**現狀**:
- ⚠️ 有會員等級概念，但無可視化成長路徑
- ❌ 用戶不知道如何升級

**建議實施**:
```vue
<!-- 會員詳情 - 成長路徑 -->
<q-card>
  <q-card-section>
    <div class="text-h6 q-mb-lg">會員成長路徑</div>
    
    <!-- 當前進度 -->
    <div class="q-mb-md">
      <div class="row items-center q-mb-sm">
        <div>
          <div class="text-subtitle2">{{ currentLevel.name }}</div>
          <div class="text-caption text-grey-7">
            距下一等級還需 {{ pointsNeeded }} 積分
          </div>
        </div>
      </div>
      <q-linear-progress
        :value="progressRatio"
        color="primary"
        size="20px"
      >
        <div class="text-white text-center" style="font-size: 12px">
          {{ currentPoints }} / {{ nextLevelPoints }}
        </div>
      </q-linear-progress>
    </div>
    
    <!-- 等級對比 -->
    <div class="row q-col-gutter-md">
      <div v-for="level in allLevels" :key="level.id" class="col-4">
        <q-card
          :class="level.id === currentLevel.id ? 'bg-primary text-white' : ''"
        >
          <q-card-section class="text-center">
            <q-icon :name="level.icon" size="32px" />
            <div class="text-subtitle2 q-mt-sm">{{ level.name }}</div>
            <div class="text-caption">
              積分: {{ level.minPoints }}+
            </div>
            <div class="text-caption q-mt-sm">
              <div>折扣: {{ level.discountRate }}%</div>
              <div>倍數: {{ level.pointsMultiplier }}x</div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-card-section>
</q-card>

<script setup>
const currentPoints = ref(2500)
const allLevels = ref([
  { id: 1, name: '青銅會員', icon: 'star', minPoints: 0, discountRate: 0, pointsMultiplier: 1.0 },
  { id: 2, name: '白銀會員', icon: 'star', minPoints: 1000, discountRate: 5, pointsMultiplier: 1.5 },
  { id: 3, name: '黃金會員', icon: 'star', minPoints: 5000, discountRate: 10, pointsMultiplier: 2.0 },
  { id: 4, name: '鑽石會員', icon: 'star', minPoints: 10000, discountRate: 15, pointsMultiplier: 3.0 }
])

const currentLevel = computed(() => allLevels.value[1])
const nextLevel = computed(() => allLevels.value[2])
const nextLevelPoints = computed(() => nextLevel.value.minPoints)
const pointsNeeded = computed(() => nextLevelPoints.value - currentPoints.value)
const progressRatio = computed(() => currentPoints.value / nextLevelPoints.value)
</script>
```

**預期收益**: ✅ 激勵會員消費，提升留存率

---

## 4. 表單與數據驗證優化

### 4.1 【功能缺失】動態表單驗證反饋

**現狀**:
- ⚠️ 基本的驗證規則
- ❌ 無實時驗證反饋
- ❌ 無助手提示

**建議實施**:
```vue
<q-input
  v-model="form.username"
  label="使用者名稱"
  outlined
  class="q-mb-md"
  :rules="[
    val => !!val || '使用者名稱不能為空',
    val => val.length >= 3 || '至少3個字元',
    val => /^[a-zA-Z0-9_-]+$/.test(val) || '只能包含字母、數字、下劃線、連字號'
  ]"
  @update:model-value="checkUsernameAvailability"
  lazy-rules
>
  <!-- 即時驗證反饋 -->
  <template v-slot:hint v-if="usernameStatus === 'checking'">
    <q-icon name="hourglass_empty" class="q-mr-xs" />
    檢查可用性中...
  </template>
  <template v-slot:hint v-else-if="usernameStatus === 'available'">
    <q-icon name="check_circle" color="positive" class="q-mr-xs" />
    使用者名稱可用
  </template>
  <template v-slot:hint v-else-if="usernameStatus === 'unavailable'">
    <q-icon name="cancel" color="negative" class="q-mr-xs" />
    使用者名稱已被使用
  </template>
</q-input>

<script setup>
import { debounce } from 'quasar'

const usernameStatus = ref(null)

const checkUsernameAvailability = debounce(async (username) => {
  if (!username || username.length < 3) return
  
  usernameStatus.value = 'checking'
  try {
    const response = await userApi.checkUsernameAvailable(username)
    usernameStatus.value = response.data.available ? 'available' : 'unavailable'
  } catch (error) {
    usernameStatus.value = null
  }
}, 500)
</script>
```

**預期收益**: ✅ 減少表單提交錯誤，改善用戶體驗

---

## 5. 搜尋和篩選優化

### 5.1 【功能缺失】高級篩選和保存篩選條件

**現狀**:
- ⚠️ 有基本篩選
- ❌ 無高級篩選選項（日期範圍、金額範圍）
- ❌ 無保存篩選條件的功能

**建議實施**:
```vue
<q-card class="q-mb-md">
  <q-card-section>
    <!-- 已保存的篩選 -->
    <div class="q-mb-md">
      <div class="text-subtitle2 q-mb-sm">快速篩選</div>
      <div class="row q-gutter-sm">
        <q-chip
          v-for="savedFilter in savedFilters"
          :key="savedFilter.id"
          clickable
          removable
          @click="applySavedFilter(savedFilter)"
          @remove="deleteSavedFilter(savedFilter.id)"
        >
          {{ savedFilter.name }}
        </q-chip>
        <q-btn
          flat
          round
          dense
          icon="add"
          size="sm"
          @click="showSaveFilterDialog = true"
        />
      </div>
    </div>
    
    <!-- 篩選表單 -->
    <div class="row q-col-gutter-md">
      <!-- 基本篩選 -->
      <div class="col-12 col-md-3">
        <q-select
          v-model="filters.status"
          label="狀態"
          outlined
          dense
          :options="statusOptions"
        />
      </div>
      
      <!-- 日期範圍 -->
      <div class="col-12 col-md-3">
        <q-input
          v-model="filters.dateRange.from"
          label="開始日期"
          outlined
          dense
          type="date"
        />
      </div>
      <div class="col-12 col-md-3">
        <q-input
          v-model="filters.dateRange.to"
          label="結束日期"
          outlined
          dense
          type="date"
        />
      </div>
      
      <!-- 金額範圍 -->
      <div class="col-12 col-md-3">
        <div class="row q-gutter-sm">
          <q-input
            v-model.number="filters.amount.min"
            label="最低金額"
            outlined
            dense
            type="number"
            class="col"
          />
          <q-input
            v-model.number="filters.amount.max"
            label="最高金額"
            outlined
            dense
            type="number"
            class="col"
          />
        </div>
      </div>
    </div>
    
    <!-- 按鈕 -->
    <div class="row justify-end q-gutter-sm q-mt-md">
      <q-btn flat label="重置" @click="resetFilters" />
      <q-btn
        color="primary"
        label="保存此篩選"
        @click="showSaveFilterDialog = true"
      />
      <q-btn
        color="primary"
        unelevated
        label="應用篩選"
        @click="applyFilters"
      />
    </div>
  </q-card-section>
</q-card>

<script setup>
const savedFilters = ref([
  { id: 1, name: '本月訂單', filters: { dateRange: { from: '2026-02-01', to: '2026-02-28' } } },
  { id: 2, name: '待付款', filters: { status: 'PENDING' } },
  { id: 3, name: '高價值訂單', filters: { amount: { min: 10000 } } }
])

const applySavedFilter = (savedFilter) => {
  filters.value = { ...filters.value, ...savedFilter.filters }
  applyFilters()
}

const saveSavedFilter = (name) => {
  savedFilters.value.push({
    id: Date.now(),
    name,
    filters: { ...filters.value }
  })
}
</script>
```

**預期收益**: ✅ 快速找到數據，提升查詢效率 **50%+**

---

## 6. 批量操作功能

### 6.1 【功能缺失】批量導入功能

**現狀**:
- ❌ 無批量導入功能（商品、訂單、會員）
- ❌ 手動輸入效率低

**建議實施**:
```vue
<!-- 批量導入對話框 -->
<q-dialog v-model="showImportDialog" persistent>
  <q-card style="min-width: 500px">
    <q-card-section class="row items-center q-pb-none">
      <div class="text-h6">批量導入商品</div>
      <q-space />
      <q-btn icon="close" flat round dense v-close-popup />
    </q-card-section>

    <q-separator />

    <q-card-section>
      <!-- 步驟條 -->
      <q-stepper v-model="importStep" flat animated>
        <!-- 第1步：選擇文件 -->
        <q-step :name="1" title="選擇文件" prefix="1" done-icon="check">
          <div class="q-pa-md">
            <div class="text-subtitle2 q-mb-md">上傳 Excel 或 CSV 文件</div>
            
            <!-- 下載模板 -->
            <q-btn
              flat
              label="下載導入模板"
              icon="download"
              @click="downloadTemplate"
              class="q-mb-md full-width"
            />
            
            <!-- 文件上傳 -->
            <q-file
              v-model="importFile"
              accept=".xlsx,.csv"
              label="選擇文件"
              outlined
              @update:model-value="onFileSelected"
            />
          </div>
          <q-stepper-navigation>
            <q-btn
              color="primary"
              label="下一步"
              @click="importStep = 2"
              :disable="!importFile"
            />
          </q-stepper-navigation>
        </q-step>

        <!-- 第2步：預覽和驗證 -->
        <q-step :name="2" title="預覽" prefix="2" done-icon="check">
          <div class="q-pa-md">
            <div class="text-subtitle2 q-mb-md">
              即將導入 {{ importPreview.length }} 筆數據
            </div>
            
            <!-- 驗證結果 -->
            <q-banner
              v-if="importErrors.length"
              class="bg-negative text-white q-mb-md"
            >
              <template v-slot:avatar>
                <q-icon name="error" />
              </template>
              發現 {{ importErrors.length }} 個錯誤
              <q-expansion-item
                header-class="text-white"
                label="查看詳情"
              >
                <div v-for="(error, i) in importErrors" :key="i" class="text-caption">
                  第 {{ error.row }} 行: {{ error.message }}
                </div>
              </q-expansion-item>
            </q-banner>
            
            <!-- 數據預覽表格 -->
            <q-table
              :rows="importPreview.slice(0, 5)"
              :columns="importColumns"
              dense
              flat
            />
            <div v-if="importPreview.length > 5" class="text-caption text-grey-7 q-mt-md">
              ... 還有 {{ importPreview.length - 5 }} 筆數據
            </div>
          </div>
          <q-stepper-navigation>
            <q-btn
              color="primary"
              label="返回"
              flat
              @click="importStep = 1"
            />
            <q-btn
              color="primary"
              label="開始導入"
              @click="importStep = 3"
              :disable="importErrors.length > 0"
            />
          </q-stepper-navigation>
        </q-step>

        <!-- 第3步：導入進度 -->
        <q-step :name="3" title="導入中" prefix="3">
          <div class="q-pa-md">
            <q-linear-progress
              :value="importProgress / 100"
              color="primary"
              class="q-mb-md"
            />
            <div class="text-center text-caption">
              已導入 {{ importProgress }} %
              ({{ importSuccessCount }} / {{ importPreview.length }})
            </div>
          </div>
        </q-step>

        <!-- 第4步：完成 -->
        <q-step :name="4" title="完成" prefix="4">
          <div class="q-pa-md text-center">
            <q-icon name="check_circle" size="64px" color="positive" />
            <div class="text-h6 q-mt-md">導入完成！</div>
            <div class="text-subtitle2">
              成功導入 {{ importSuccessCount }} 筆
              <span v-if="importFailCount" class="text-negative">
                失敗 {{ importFailCount }} 筆
              </span>
            </div>
          </div>
          <q-stepper-navigation>
            <q-btn
              color="primary"
              label="完成"
              @click="closeImportDialog"
            />
          </q-stepper-navigation>
        </q-step>
      </q-stepper>
    </q-card-section>
  </q-card>
</q-dialog>

<script setup>
const downloadTemplate = () => {
  // 生成 Excel 模板
  const columns = ['商品名稱', '價格', '庫存', '分類', '描述']
  // 下載邏輯...
  window.location.href = '/api/products/template'
}

const onFileSelected = async () => {
  // 解析文件並預覽
  const data = await parseFile(importFile.value)
  importPreview.value = data
  validateImportData()
}

const validateImportData = () => {
  importErrors.value = []
  importPreview.value.forEach((row, index) => {
    if (!row['商品名稱']) {
      importErrors.value.push({
        row: index + 2,
        message: '商品名稱不能為空'
      })
    }
    if (!row['價格'] || row['價格'] <= 0) {
      importErrors.value.push({
        row: index + 2,
        message: '價格必須大於0'
      })
    }
  })
}
</script>
```

**預期收益**: ✅ 批量導入效率提升 **100倍** (手動 vs 導入)

---

## 7. 數據導出和導入

### 7.1 【功能缺失】多格式導出

**現狀**:
- ❌ 無導出功能
- ❌ 無打印功能

**建議實施**:
```vue
<!-- 導出菜單 -->
<q-btn
  flat
  dense
  icon="download"
  label="導出"
>
  <q-menu>
    <q-list>
      <q-item clickable @click="exportAs('excel')">
        <q-item-section avatar>
          <q-icon name="description" />
        </q-item-section>
        <q-item-section>導出為 Excel</q-item-section>
      </q-item>
      
      <q-item clickable @click="exportAs('csv')">
        <q-item-section avatar>
          <q-icon name="table_chart" />
        </q-item-section>
        <q-item-section>導出為 CSV</q-item-section>
      </q-item>
      
      <q-item clickable @click="exportAs('pdf')">
        <q-item-section avatar>
          <q-icon name="picture_as_pdf" />
        </q-item-section>
        <q-item-section>導出為 PDF</q-item-section>
      </q-item>
      
      <q-separator />
      
      <q-item clickable @click="printTable">
        <q-item-section avatar>
          <q-icon name="print" />
        </q-item-section>
        <q-item-section>打印</q-item-section>
      </q-item>
    </q-list>
  </q-menu>
</q-btn>

<script setup>
const exportAs = async (format) => {
  const data = selectedRows.value.length > 0 ? selectedRows.value : rows.value
  
  try {
    let url = `/api/export?format=${format}`
    if (data.length > 0) {
      url += `&ids=${data.map(r => r.id).join(',')}`
    }
    
    const response = await fetch(url)
    const blob = await response.blob()
    
    const filename = `export_${new Date().getTime()}.${format}`
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = filename
    link.click()
    
    $q.notify({ type: 'positive', message: '導出成功' })
  } catch (error) {
    $q.notify({ type: 'negative', message: '導出失敗' })
  }
}

const printTable = () => {
  const printWindow = window.open('', '', 'width=800,height=600')
  printWindow.document.write(generatePrintHTML())
  printWindow.print()
}
</script>
```

**預期收益**: ✅ 數據靈活使用，支持多種報表需求

---

## 8. 通知和反饋機制

### 8.1 【功能缺失】操作確認和撤銷

**現狀**:
- ⚠️ 刪除操作無撤銷
- ❌ 無操作確認對話框

**建議實施**:
```vue
<!-- 刪除確認對話框 -->
<q-dialog v-model="showDeleteDialog" persistent>
  <q-card>
    <q-card-section class="row items-center">
      <q-icon name="warning" size="50px" color="negative" />
      <span class="q-ml-md">
        確定要刪除此{{ entityName }}嗎？此操作無法撤銷。
      </span>
    </q-card-section>

    <q-card-section class="q-pt-none">
      <div class="text-caption text-grey-7">
        <strong>{{ deleteItem.name || deleteItem.title }}</strong>
      </div>
    </q-card-section>

    <q-card-actions align="right">
      <q-btn flat label="取消" color="grey" v-close-popup />
      <q-btn
        unelevated
        label="確認刪除"
        color="negative"
        @click="confirmDelete"
        :loading="deleteLoading"
      />
    </q-card-actions>
  </q-card>
</q-dialog>

<!-- 成功通知帶撤銷選項 -->
<script setup>
const confirmDelete = async () => {
  try {
    deleteLoading.value = true
    await api.delete(deleteItem.id)
    
    // 顯示可撤銷的通知
    const notification = $q.notify({
      type: 'positive',
      message: `已刪除 ${deleteItem.name || deleteItem.title}`,
      position: 'bottom-right',
      timeout: 0,
      actions: [
        {
          label: '撤銷',
          color: 'white',
          handler: async () => {
            await api.restore(deleteItem.id)
            notification()
            $q.notify({
              type: 'positive',
              message: '已恢復'
            })
          }
        },
        {
          label: '關閉',
          color: 'white'
        }
      ]
    })
    
    showDeleteDialog.value = false
  } finally {
    deleteLoading.value = false
  }
}
</script>
```

**預期收益**: ✅ 減少誤操作，提升用戶信心

---

## 9. 移動端適配

### 9.1 【UX改進】移動端操作優化

**現狀**:
- ⚠️ 響應式設計基本完成
- ❌ 移動端操作不流暢（過多點擊）
- ❌ 無移動端優先的工作流

**建議**:
```vue
<!-- 移動端自適應工具欄 -->
<template v-if="$q.screen.lt.md">
  <!-- 移動端簡化的操作欄 -->
  <q-card class="sticky" style="bottom: 0">
    <q-tabs
      v-model="mobileTab"
      dense
      class="text-grey-7"
      indicator-color="primary"
      active-color="primary"
    >
      <q-tab name="list" icon="list" label="列表" />
      <q-tab name="filter" icon="filter_list" label="篩選" />
      <q-tab name="actions" icon="more_horiz" label="操作" />
    </q-tabs>

    <q-tab-panels v-model="mobileTab" animated>
      <q-tab-panel name="list">
        <!-- 列表視圖 -->
      </q-tab-panel>
      <q-tab-panel name="filter">
        <!-- 篩選面板 -->
      </q-tab-panel>
      <q-tab-panel name="actions">
        <!-- 快速操作 -->
      </q-tab-panel>
    </q-tab-panels>
  </q-card>
</template>

<template v-else>
  <!-- 桌面版本原有布局 -->
</template>
```

**預期收益**: ✅ 移動端體驗改善，支持移動辦公

---

## 10. 高級功能

### 10.1 【功能缺失】數據分析儀表板

**現狀**:
- ⚠️ 有基本的統計模組
- ❌ 無實時儀表板
- ❌ 無圖表可視化

**建議實施 StatisticsView 增強**:
```vue
<!-- 實時儀表板 -->
<q-card>
  <q-card-section>
    <div class="text-h6 q-mb-md">今日數據摘要</div>
    
    <div class="row q-col-gutter-md">
      <!-- KPI卡片 -->
      <div class="col-12 col-md-3">
        <q-card flat bordered>
          <q-card-section class="text-center">
            <div class="text-grey-7">訂單數</div>
            <div class="text-h4 text-primary q-my-md">{{ todayStats.orders }}</div>
            <div class="text-caption">
              <q-icon name="trending_up" color="positive" size="xs" />
              比昨天 +{{ percentageChange.orders }}%
            </div>
          </q-card-section>
        </q-card>
      </div>
      
      <!-- 銷售額 -->
      <div class="col-12 col-md-3">
        <q-card flat bordered>
          <q-card-section class="text-center">
            <div class="text-grey-7">銷售額</div>
            <div class="text-h4 text-positive q-my-md">
              ${{ formatCurrency(todayStats.revenue) }}
            </div>
            <div class="text-caption">
              <q-icon name="trending_down" color="negative" size="xs" />
              比昨天 -{{ percentageChange.revenue }}%
            </div>
          </q-card-section>
        </q-card>
      </div>
      
      <!-- 轉化率 -->
      <div class="col-12 col-md-3">
        <q-card flat bordered>
          <q-card-section class="text-center">
            <div class="text-grey-7">轉化率</div>
            <div class="text-h4 q-my-md">{{ todayStats.conversionRate }}%</div>
            <q-linear-progress :value="todayStats.conversionRate / 100" class="q-mt-md" />
          </q-card-section>
        </q-card>
      </div>
      
      <!-- 活躍用戶 -->
      <div class="col-12 col-md-3">
        <q-card flat bordered>
          <q-card-section class="text-center">
            <div class="text-grey-7">活躍用戶</div>
            <div class="text-h4 q-my-md">{{ todayStats.activeUsers }}</div>
            <q-circular-progress
              :value="todayStats.activeUsers / 1000"
              size="80px"
              color="primary"
              show-value
              class="q-mt-md"
            />
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-card-section>
</q-card>

<!-- 圖表 -->
<q-card class="q-mt-lg">
  <q-card-section>
    <div class="text-h6 q-mb-md">銷售趨勢</div>
    <chart-component :chartData="salesChartData" :options="chartOptions" />
  </q-card-section>
</q-card>

<script setup>
import { ref, computed } from 'vue'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const todayStats = ref({
  orders: 24,
  revenue: 15600,
  conversionRate: 3.2,
  activeUsers: 856
})

const percentageChange = computed(() => ({
  orders: 12,
  revenue: -5
}))

const salesChartData = ref({
  labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  datasets: [{
    label: '銷售額',
    data: [12000, 15600, 13200, 18900, 16500, 21000, 19800],
    borderColor: '#1976d2',
    backgroundColor: 'rgba(25, 118, 210, 0.1)',
    tension: 0.4
  }]
})
</script>
```

**預期收益**: ✅ 實時掌握業務狀況，快速決策

---

## 📊 優化優先級和影響

| 功能 | 用戶影響 | 實施難度 | 優先級 | 預期收益 |
|------|--------|--------|------|--------|
| **商品圖片管理** | 🔴 高 | 🟡 中 | 🔴 P0 | 改善商品展示 |
| **訂單快速操作欄** | 🔴 高 | 🟡 中 | 🔴 P0 | 效率 +300% |
| **退貨管理** | 🟡 中 | 🔴 高 | 🔴 P0 | 完整售後流程 |
| **高級篩選&保存** | 🔴 高 | 🟢 低 | 🟡 P1 | 查詢效率 +50% |
| **批量導入** | 🟡 中 | 🔴 高 | 🟡 P1 | 數據錄入 +100x |
| **數據導出** | 🟡 中 | 🟢 低 | 🟡 P1 | 數據靈活使用 |
| **實時儀表板** | 🟡 中 | 🔴 高 | 📊 P2 | 決策支持 |
| **會員成長路徑** | 🟢 低 | 🟡 中 | 📊 P2 | 用戶留存 +20% |
| **操作撤銷** | 🟡 中 | 🟢 低 | 📊 P2 | 降低誤操作 |
| **移動端優化** | 🟢 低 | 🟡 中 | 📊 P3 | 移動辦公支持 |

---

## 🎯 實施計劃

### 第1週 (P0 優先)
- [ ] 商品圖片上傳和排序
- [ ] 訂單快速操作工具欄
- [ ] 訂單狀態時間線

### 第2週 (P0 優先)
- [ ] 退貨管理模組
- [ ] 退款流程

### 第3週 (P1 功能)
- [ ] 高級篩選和保存條件
- [ ] 批量導入 (商品/訂單)
- [ ] 數據導出 (Excel/PDF)

### 第4-5週 (P2 功能)
- [ ] 實時儀表板
- [ ] 會員成長路徑
- [ ] 操作撤銷功能

---

## ✅ 完成檢查清單

**商品管理**
- [ ] 圖片上傳和預覽
- [ ] 拖動排序
- [ ] 規格批量編輯

**訂單管理**
- [ ] 快速操作工具欄
- [ ] 狀態時間線
- [ ] 退貨管理
- [ ] 批量操作 (10+ 種操作)

**會員管理**
- [ ] 會員標籤
- [ ] 自動化規則
- [ ] 成長路徑視覺化

**數據管理**
- [ ] 高級篩選
- [ ] 保存篩選條件
- [ ] 批量導入
- [ ] 多格式導出

**用戶體驗**
- [ ] 操作撤銷
- [ ] 實時反饋
- [ ] 移動端優化
- [ ] 實時儀表板

---

**報告完成日期**: 2026-02-25  
**更新週期**: 每月審查一次

