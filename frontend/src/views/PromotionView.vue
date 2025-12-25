<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none">促銷活動管理</h4>
      </div>
      <div class="col-auto q-gutter-md">
        <q-btn
          color="primary"
          label="新增促銷"
          icon="add"
          @click="showCreateDialog"
        />
        <q-btn
          color="info"
          label="優惠券管理"
          icon="card_giftcard"
          @click="showCouponTab = true"
        />
      </div>
    </div>

    <!-- 促銷活動列表 -->
    <q-card>
      <q-linear-progress
        v-if="loading"
        indeterminate
        color="primary"
      />
      <q-table
        :rows="promotions"
        :columns="promotionColumns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
        flat
        bordered
      >
        <template #body-cell-type="props">
          <q-td :props="props">
            <q-badge
              :label="getTypeLabel(props.row.type)"
              :color="getTypeColor(props.row.type)"
            />
          </q-td>
        </template>

        <template #body-cell-discountValue="props">
          <q-td :props="props">
            <span v-if="props.row.discountType === 'PERCENTAGE'">
              {{ props.row.discountValue }}%
            </span>
            <span v-else>
              NT${{ props.row.discountValue }}
            </span>
          </q-td>
        </template>

        <template #body-cell-enabled="props">
          <q-td :props="props">
            <q-toggle
              :model-value="props.row.enabled"
              :disable="updating"
              @update:model-value="togglePromotion(props.row.id!, $event)"
            />
          </q-td>
        </template>

        <template #body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="edit"
              color="primary"
              @click="showEditDialog(props.row)"
              title="編輯"
            />
            <q-btn
              flat
              round
              dense
              size="sm"
              icon="delete"
              color="negative"
              @click="confirmDelete(props.row.id!)"
              title="刪除"
            />
          </q-td>
        </template>

        <template #no-data>
          <div class="text-center q-py-lg text-grey-7">
            沒有促銷活動
          </div>
        </template>
      </q-table>
    </q-card>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog" full-width max-width="600px">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">
            {{ editingPromotion?.id ? '編輯促銷活動' : '新增促銷活動' }}
          </div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showDialog = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pt-none">
          <q-form ref="promotionForm" @submit="savePromotion">
            <q-input
              v-model="editingPromotion.name"
              label="活動名稱 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入活動名稱']"
            />

            <q-input
              v-model="editingPromotion.description"
              label="描述"
              outlined
              dense
              type="textarea"
              class="q-mb-md"
            />

            <q-select
              v-model="editingPromotion.type"
              label="活動類型 *"
              outlined
              dense
              :options="['DISCOUNT', 'FULL_SHOP', 'FREE_SHIPPING', 'BUY_GIFT']"
              class="q-mb-md"
              :rules="[val => !!val || '請選擇活動類型']"
            />

            <div class="row q-col-gutter-md q-mb-md">
              <div class="col">
                <q-input
                  v-model="editingPromotion.startDate"
                  label="開始日期 *"
                  outlined
                  dense
                  type="date"
                  :rules="[val => !!val || '請選擇開始日期']"
                />
              </div>
              <div class="col">
                <q-input
                  v-model="editingPromotion.endDate"
                  label="結束日期 *"
                  outlined
                  dense
                  type="date"
                  :rules="[val => !!val || '請選擇結束日期']"
                />
              </div>
            </div>

            <div class="row q-col-gutter-md q-mb-md">
              <div class="col">
                <q-select
                  v-model="editingPromotion.discountType"
                  label="折扣類型"
                  outlined
                  dense
                  :options="['PERCENTAGE', 'FIXED']"
                />
              </div>
              <div class="col">
                <q-input
                  v-model.number="editingPromotion.discountValue"
                  :label="`折扣金額${editingPromotion.discountType === 'PERCENTAGE' ? ' (%)' : ' (NT$)'}`"
                  outlined
                  dense
                  type="number"
                />
              </div>
            </div>

            <q-input
              v-model.number="editingPromotion.minPurchaseAmount"
              label="最低購買金額"
              outlined
              dense
              type="number"
              class="q-mb-md"
            />

            <q-input
              v-model.number="editingPromotion.priority"
              label="優先級"
              outlined
              dense
              type="number"
              min="1"
              class="q-mb-md"
            />

            <q-checkbox
              v-model="editingPromotion.enabled"
              label="啟用此活動"
              class="q-mb-md"
            />

            <div class="row q-gutter-md">
              <q-btn
                label="取消"
                flat
                @click="showDialog = false"
              />
              <q-btn
                label="保存"
                color="primary"
                type="submit"
                :loading="saving"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 刪除確認 -->
    <q-dialog v-model="showDeleteDialog">
      <q-card>
        <q-card-section class="row items-center">
          <q-icon
            name="warning"
            size="md"
            color="negative"
          />
          <span class="q-ml-md">確定要刪除此活動嗎？</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            label="取消"
            flat
            @click="showDeleteDialog = false"
          />
          <q-btn
            label="刪除"
            color="negative"
            @click="deletePromotion"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 優惠券對話框 -->
    <q-dialog v-model="showCouponTab" full-width max-width="1000px">
      <coupon-management @close="showCouponTab = false" />
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { promotionApi, type Promotion } from '@/api/promotion'
import CouponManagement from '@/components/CouponManagement.vue'

const $q = useQuasar()
const promotionForm = ref()

// 資料
const promotions = ref<Promotion[]>([])
const pagination = ref({
  page: 0,
  rowsPerPage: 20,
  rowsNumber: 0
})
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const updating = ref(false)

// 對話框
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const showCouponTab = ref(false)
const deleteId = ref<number>()

// 編輯活動
const editingPromotion = ref<Partial<Promotion>>({
  name: '',
  description: '',
  type: 'DISCOUNT',
  discountType: 'PERCENTAGE',
  discountValue: 0,
  priority: 1,
  enabled: true
})

// 表格列定義
const promotionColumns = [
  { name: 'name', label: '活動名稱', field: 'name', align: 'left' },
  { name: 'type', label: '類型', field: 'type', align: 'center' },
  { name: 'discountValue', label: '折扣', field: 'discountValue', align: 'right' },
  { name: 'minPurchaseAmount', label: '最低金額', field: 'minPurchaseAmount', align: 'right' },
  { name: 'priority', label: '優先級', field: 'priority', align: 'center' },
  { name: 'enabled', label: '啟用', field: 'enabled', align: 'center' },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' }
]

// 獲取類型標籤
const getTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    DISCOUNT: '折扣',
    FULL_SHOP: '全館活動',
    FREE_SHIPPING: '免運',
    BUY_GIFT: '買贈'
  }
  return labels[type] || type
}

// 獲取類型顏色
const getTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    DISCOUNT: 'blue',
    FULL_SHOP: 'purple',
    FREE_SHIPPING: 'green',
    BUY_GIFT: 'orange'
  }
  return colors[type] || 'grey'
}

// 載入促銷活動
const loadPromotions = async () => {
  loading.value = true
  try {
    const result = await promotionApi.getPromotions(
      pagination.value.page,
      pagination.value.rowsPerPage
    )
    promotions.value = result.content
    pagination.value.rowsNumber = result.totalElements
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入活動失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingPromotion.value = {
    name: '',
    description: '',
    type: 'DISCOUNT',
    discountType: 'PERCENTAGE',
    discountValue: 0,
    priority: 1,
    enabled: true
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (promotion: Promotion) => {
  editingPromotion.value = { ...promotion }
  showDialog.value = true
}

// 保存活動
const savePromotion = async () => {
  saving.value = true
  try {
    if (editingPromotion.value.id) {
      await promotionApi.updatePromotion(
        editingPromotion.value.id,
        editingPromotion.value
      )
      $q.notify({
        type: 'positive',
        message: '活動已更新',
        position: 'top'
      })
    } else {
      await promotionApi.createPromotion(editingPromotion.value as Promotion)
      $q.notify({
        type: 'positive',
        message: '活動已建立',
        position: 'top'
      })
    }
    showDialog.value = false
    loadPromotions()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存失敗',
      position: 'top'
    })
  } finally {
    saving.value = false
  }
}

// 確認刪除
const confirmDelete = (id: number) => {
  deleteId.value = id
  showDeleteDialog.value = true
}

// 刪除活動
const deletePromotion = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await promotionApi.deletePromotion(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '活動已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadPromotions()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '刪除失敗',
      position: 'top'
    })
  } finally {
    deleting.value = false
  }
}

// 切換活動啟用狀態
const togglePromotion = async (id: number, enabled: boolean) => {
  updating.value = true
  try {
    if (enabled) {
      await promotionApi.enablePromotion(id)
    } else {
      await promotionApi.disablePromotion(id)
    }
    loadPromotions()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '更新失敗',
      position: 'top'
    })
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  loadPromotions()
})
</script>
