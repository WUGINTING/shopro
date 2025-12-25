<template>
  <q-card>
    <q-card-section class="row items-center q-pb-none">
      <div class="text-h6">優惠券管理</div>
      <q-space />
      <q-btn
        icon="close"
        flat
        round
        dense
        @click="$emit('close')"
      />
    </q-card-section>

    <q-separator />

    <q-card-section>
      <div class="row q-mb-md items-center">
        <div class="col">
          <q-btn
            color="primary"
            label="新增優惠券"
            icon="add"
            @click="showCreateDialog"
          />
        </div>
      </div>

      <!-- 優惠券列表 -->
      <q-table
        :rows="coupons"
        :columns="columns"
        row-key="id"
        :pagination.sync="pagination"
        :loading="loading"
        flat
        bordered
      >
        <template #body-cell-type="props">
          <q-td :props="props">
            <q-badge
              :label="getCouponTypeLabel(props.row.type)"
              :color="getCouponTypeColor(props.row.type)"
            />
          </q-td>
        </template>

        <template #body-cell-discountValue="props">
          <q-td :props="props">
            <span v-if="props.row.type === 'PERCENTAGE'">
              {{ props.row.discountValue }}%
            </span>
            <span v-else>
              NT${{ props.row.discountValue }}
            </span>
          </q-td>
        </template>

        <template #body-cell-usageRate="props">
          <q-td :props="props">
            <div class="row items-center q-gutter-md">
              <div class="col-auto">
                {{ props.row.usedCount || 0 }} / {{ props.row.totalCount }}
              </div>
              <div class="col-auto">
                <q-linear-progress
                  :value="(props.row.usedCount || 0) / props.row.totalCount"
                  size="8px"
                  color="positive"
                  style="min-width: 80px"
                />
              </div>
            </div>
          </q-td>
        </template>

        <template #body-cell-enabled="props">
          <q-td :props="props">
            <q-toggle
              :model-value="props.row.enabled"
              :disable="updating"
              @update:model-value="toggleCoupon(props.row.id!, $event)"
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
            沒有優惠券
          </div>
        </template>
      </q-table>
    </q-card-section>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog" full-width max-width="600px">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">
            {{ editingCoupon?.id ? '編輯優惠券' : '新增優惠券' }}
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
          <q-form ref="couponForm" @submit="saveCoupon">
            <q-input
              v-model="editingCoupon.code"
              label="優惠券代碼 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入優惠券代碼']"
              counter
              maxlength="20"
            />

            <q-input
              v-model="editingCoupon.name"
              label="優惠券名稱 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入優惠券名稱']"
            />

            <q-select
              v-model="editingCoupon.type"
              label="折扣類型 *"
              outlined
              dense
              :options="['PERCENTAGE', 'FIXED', 'FREE_SHIPPING']"
              class="q-mb-md"
              :rules="[val => !!val || '請選擇折扣類型']"
            />

            <q-input
              v-model.number="editingCoupon.discountValue"
              :label="`折扣金額${editingCoupon.type === 'PERCENTAGE' ? ' (%)' : editingCoupon.type === 'FREE_SHIPPING' ? '' : ' (NT$)'} *`"
              outlined
              dense
              type="number"
              class="q-mb-md"
              :disable="editingCoupon.type === 'FREE_SHIPPING'"
              :rules="[val => editingCoupon.type === 'FREE_SHIPPING' || val > 0 || '請輸入折扣金額']"
            />

            <q-input
              v-model.number="editingCoupon.totalCount"
              label="總數量 *"
              outlined
              dense
              type="number"
              min="1"
              class="q-mb-md"
              :rules="[val => val && val > 0 || '請輸入正整數']"
            />

            <q-input
              v-model.number="editingCoupon.minPurchaseAmount"
              label="最低購買金額"
              outlined
              dense
              type="number"
              class="q-mb-md"
            />

            <div class="row q-col-gutter-md q-mb-md">
              <div class="col">
                <q-input
                  v-model="editingCoupon.validFrom"
                  label="生效日期 *"
                  outlined
                  dense
                  type="date"
                  :rules="[val => !!val || '請選擇生效日期']"
                />
              </div>
              <div class="col">
                <q-input
                  v-model="editingCoupon.validUntil"
                  label="過期日期 *"
                  outlined
                  dense
                  type="date"
                  :rules="[val => !!val || '請選擇過期日期']"
                />
              </div>
            </div>

            <q-checkbox
              v-model="editingCoupon.enabled"
              label="啟用此優惠券"
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
          <span class="q-ml-md">確定要刪除此優惠券嗎？</span>
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
            @click="deleteCoupon"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { couponApi, type Coupon } from '@/api/promotion'

const $q = useQuasar()
const emit = defineEmits(['close'])
const couponForm = ref()

// 資料
const coupons = ref<Coupon[]>([])
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
const deleteId = ref<number>()

// 編輯優惠券
const editingCoupon = ref<Partial<Coupon>>({
  code: '',
  name: '',
  type: 'PERCENTAGE',
  discountValue: 0,
  totalCount: 1,
  enabled: true
})

// 表格列定義
const columns = [
  { name: 'code', label: '代碼', field: 'code', align: 'left' },
  { name: 'name', label: '名稱', field: 'name', align: 'left' },
  { name: 'type', label: '類型', field: 'type', align: 'center' },
  { name: 'discountValue', label: '折扣', field: 'discountValue', align: 'right' },
  { name: 'usageRate', label: '使用率', field: 'usageRate', align: 'center' },
  { name: 'enabled', label: '啟用', field: 'enabled', align: 'center' },
  { name: 'actions', label: '操作', field: 'actions', align: 'center' }
]

// 獲取優惠券類型標籤
const getCouponTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    PERCENTAGE: '百分比折扣',
    FIXED: '固定金額',
    FREE_SHIPPING: '免運費'
  }
  return labels[type] || type
}

// 獲取優惠券類型顏色
const getCouponTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    PERCENTAGE: 'blue',
    FIXED: 'purple',
    FREE_SHIPPING: 'green'
  }
  return colors[type] || 'grey'
}

// 載入優惠券
const loadCoupons = async () => {
  loading.value = true
  try {
    const result = await couponApi.getCoupons(
      pagination.value.page,
      pagination.value.rowsPerPage
    )
    coupons.value = result.content
    pagination.value.rowsNumber = result.totalElements
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入優惠券失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingCoupon.value = {
    code: '',
    name: '',
    type: 'PERCENTAGE',
    discountValue: 0,
    totalCount: 1,
    enabled: true
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (coupon: Coupon) => {
  editingCoupon.value = { ...coupon }
  showDialog.value = true
}

// 保存優惠券
const saveCoupon = async () => {
  saving.value = true
  try {
    if (editingCoupon.value.id) {
      await couponApi.updateCoupon(
        editingCoupon.value.id,
        editingCoupon.value
      )
      $q.notify({
        type: 'positive',
        message: '優惠券已更新',
        position: 'top'
      })
    } else {
      await couponApi.createCoupon(editingCoupon.value as Coupon)
      $q.notify({
        type: 'positive',
        message: '優惠券已建立',
        position: 'top'
      })
    }
    showDialog.value = false
    loadCoupons()
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

// 刪除優惠券
const deleteCoupon = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await couponApi.deleteCoupon(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '優惠券已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadCoupons()
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

// 切換優惠券啟用狀態
const toggleCoupon = async (id: number, enabled: boolean) => {
  updating.value = true
  try {
    if (enabled) {
      await couponApi.enableCoupon(id)
    } else {
      await couponApi.disableCoupon(id)
    }
    loadCoupons()
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
  loadCoupons()
})
</script>
