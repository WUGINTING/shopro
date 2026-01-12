<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">會員等級管理</div>
          <div class="text-caption text-grey-7">管理會員等級、權益和折扣</div>
        </div>
        <q-btn
          color="primary"
          icon="add_circle"
          label="新增等級"
          unelevated
          @click="showDialog = true; resetForm()"
        />
      </div>

      <!-- Member Levels Table -->
      <q-card>
        <q-table
          :rows="levels"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-icon v-if="props.row.iconUrl" name="verified" size="24px" :color="getLevelColor(props.row.levelOrder)" class="q-mr-sm" />
                <span class="text-weight-bold">{{ props.row.name }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-levelOrder="props">
            <q-td :props="props">
              <q-badge :color="getLevelColor(props.row.levelOrder)" :label="`等級 ${props.row.levelOrder}`" />
            </q-td>
          </template>

          <template v-slot:body-cell-minSpendAmount="props">
            <q-td :props="props">
              <span v-if="props.row.minSpendAmount">${{ props.row.minSpendAmount.toFixed(2) }}</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-discountRate="props">
            <q-td :props="props">
              <span v-if="props.row.discountRate">{{ (props.row.discountRate * 100).toFixed(0) }}%折</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-pointsMultiplier="props">
            <q-td :props="props">
              <span v-if="props.row.pointsMultiplier">{{ props.row.pointsMultiplier }}倍</span>
              <span v-else class="text-grey-6">-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-enabled="props">
            <q-td :props="props">
              <q-toggle
                :model-value="props.row.enabled"
                @update:model-value="handleToggleEnabled(props.row.id)"
                color="positive"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 600px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯會員等級' : '新增會員等級' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <div class="row q-col-gutter-md">
                <div class="col-6">
                  <q-input
                    v-model="form.name"
                    label="等級名稱 *"
                    outlined
                    :rules="[val => !!val || '請輸入等級名稱']"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.levelOrder"
                    label="等級順序 *"
                    outlined
                    type="number"
                    :rules="[val => val > 0 || '等級順序必須大於0']"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.minSpendAmount"
                    label="最低消费金额"
                    outlined
                    type="number"
                    step="0.01"
                    prefix="$"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.discountRate"
                    label="折扣率 (0.0-1.0)"
                    outlined
                    type="number"
                    step="0.01"
                    hint="例如: 0.95 表示95折"
                    :rules="[
                      val => val === undefined || val === null || (val >= 0 && val <= 1) || '折扣率必須在0.0到1.0之間'
                    ]"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model.number="form.pointsMultiplier"
                    label="積分倍率"
                    outlined
                    type="number"
                    step="0.1"
                    hint="例如: 1.5 表示1.5倍積分"
                    :rules="[
                      val => val === undefined || val === null || (val > 0 && val <= 9.99) || '積分倍率必須在0到9.99之間'
                    ]"
                  />
                </div>

                <div class="col-6">
                  <q-input
                    v-model="form.iconUrl"
                    label="图标URL"
                    outlined
                  />
                </div>

                <div class="col-12">
                  <q-input
                    v-model="form.description"
                    label="等級描述"
                    outlined
                    type="textarea"
                    rows="3"
                  />
                </div>

                <div class="col-12">
                  <q-toggle
                    v-model="form.enabled"
                    label="啟用"
                    color="positive"
                  />
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="保存" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { memberLevelApi, type MemberLevel, type PageResponse } from '@/api'

const $q = useQuasar()

const levels = ref<MemberLevel[]>([])
const loading = ref(false)
const showDialog = ref(false)

const form = ref<MemberLevel>({
  name: '',
  levelOrder: 1,
  minSpendAmount: 0,
  discountRate: 1.0,
  pointsMultiplier: 1.0,
  description: '',
  iconUrl: '',
  enabled: true
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '等級名稱', align: 'left' as const, field: 'name' },
  { name: 'levelOrder', label: '等級順序', align: 'center' as const, field: 'levelOrder', sortable: true },
  { name: 'minSpendAmount', label: '最低消費', align: 'left' as const, field: 'minSpendAmount', sortable: true },
  { name: 'discountRate', label: '折扣率', align: 'center' as const, field: 'discountRate' },
  { name: 'pointsMultiplier', label: '積分倍率', align: 'center' as const, field: 'pointsMultiplier' },
  { name: 'enabled', label: '狀態', align: 'center' as const, field: 'enabled' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const loadLevels = async () => {
  loading.value = true
  try {
    const data = await memberLevelApi.listMemberLevels()
    if (Array.isArray(data)) {
      levels.value = data
    } else if (data && 'content' in data) {
      levels.value = data.content
    } else {
      levels.value = []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入會員等級列表失敗',
      position: 'top'
    })
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLevelColor = (order: number) => {
  const colors = ['brown', 'grey', 'amber', 'purple', 'blue', 'teal']
  return colors[Math.min(order - 1, colors.length - 1)]
}

const resetForm = () => {
  form.value = {
    name: '',
    levelOrder: 1,
    minSpendAmount: 0,
    discountRate: 1.0,
    pointsMultiplier: 1.0,
    description: '',
    iconUrl: '',
    enabled: true
  }
}

const handleEdit = (level: MemberLevel) => {
  form.value = { ...level }
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.levelOrder) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  try {
    if (form.value.id) {
      await memberLevelApi.updateMemberLevel(form.value.id, form.value)
      $q.notify({
        type: 'positive',
        message: '更新成功',
        position: 'top'
      })
    } else {
      await memberLevelApi.createMemberLevel(form.value)
      $q.notify({
        type: 'positive',
        message: '創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    loadLevels()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handleToggleEnabled = async (id?: number) => {
  if (!id) return
  
  try {
    await memberLevelApi.toggleEnabled(id)
    $q.notify({
      type: 'positive',
      message: '狀態更新成功',
      position: 'top'
    })
    loadLevels()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '狀態更新失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除這個會員等級嗎？此操作無法復原。`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await memberLevelApi.deleteMemberLevel(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadLevels()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '操作失敗',
        position: 'top'
      })
    }
  })
}

onMounted(() => {
  loadLevels()
})
</script>
