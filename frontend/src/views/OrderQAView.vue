<template>
  <q-page class="q-pa-md">
    <div class="order-qa-management">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">订单问答管理</div>
          <div class="text-caption text-grey-7">管理订单相关的客户问答</div>
        </div>
        <q-btn
          color="primary"
          icon="add_circle"
          label="新增问题"
          unelevated
          @click="showDialog = true; resetForm()"
        />
      </div>

      <!-- Search Filter -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md items-center">
            <div class="col-12 col-md-4">
              <q-input
                v-model="searchOrderId"
                label="订单ID"
                outlined
                dense
                clearable
                @keyup.enter="searchByOrderId"
              >
                <template v-slot:append>
                  <q-btn flat dense icon="search" @click="searchByOrderId" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-4">
              <q-select
                v-model="filterAnswered"
                label="回答状态"
                outlined
                dense
                :options="[
                  { label: '全部', value: 'all' },
                  { label: '已回答', value: 'answered' },
                  { label: '未回答', value: 'unanswered' }
                ]"
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-md-4">
              <q-btn label="清除筛选" outline color="grey-7" @click="clearFilters" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Q&A Table -->
      <q-card>
        <q-table
          :rows="filteredQAs"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
        >
          <template v-slot:body-cell-question="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.row.question }}</div>
              <div class="text-caption text-grey-7">
                提问者: {{ props.row.askerName }} ({{ props.row.askerType }})
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-answer="props">
            <q-td :props="props">
              <div v-if="props.row.answer">
                <div>{{ props.row.answer }}</div>
                <div class="text-caption text-grey-7">
                  回答者: {{ props.row.answererName }}
                </div>
              </div>
              <q-badge v-else color="warning">未回答</q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="props.row.answer ? 'positive' : 'warning'">
                {{ props.row.answer ? '已回答' : '待回答' }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn 
                v-if="!props.row.answer" 
                flat 
                dense 
                round 
                icon="reply" 
                color="primary" 
                size="sm" 
                @click="handleAnswer(props.row)"
              >
                <q-tooltip>回答</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>删除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add Question Dialog -->
      <q-dialog v-model="showDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">新增问题</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model.number="form.orderId"
                label="订单ID *"
                outlined
                type="number"
                class="q-mb-md"
                :rules="[val => !!val || '请输入订单ID']"
              />

              <q-select
                v-model="form.askerType"
                label="提问者类型 *"
                outlined
                :options="[
                  { label: '客户', value: 'CUSTOMER' },
                  { label: '商家', value: 'STORE' }
                ]"
                emit-value
                map-options
                class="q-mb-md"
                :rules="[val => !!val || '请选择提问者类型']"
              />

              <q-input
                v-model="form.askerName"
                label="提问者名称"
                outlined
                class="q-mb-md"
              />

              <q-input
                v-model="form.question"
                label="问题内容 *"
                outlined
                type="textarea"
                rows="4"
                :rules="[val => !!val || '请输入问题内容']"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="提交" color="primary" @click="handleSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Answer Dialog -->
      <q-dialog v-model="showAnswerDialog" persistent>
        <q-card style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">回答问题</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <div class="q-mb-md">
              <div class="text-subtitle2">问题:</div>
              <div class="text-body2">{{ currentQA?.question }}</div>
            </div>

            <q-form>
              <q-input
                v-model="answerForm.answer"
                label="回答内容 *"
                outlined
                type="textarea"
                rows="4"
                class="q-mb-md"
                :rules="[val => !!val || '请输入回答内容']"
              />

              <q-input
                v-model.number="answerForm.answererId"
                label="回答者ID"
                outlined
                type="number"
                class="q-mb-md"
              />

              <q-input
                v-model="answerForm.answererName"
                label="回答者名称 *"
                outlined
                :rules="[val => !!val || '请输入回答者名称']"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="提交回答" color="primary" @click="handleAnswerSubmit" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { orderQAApi, type OrderQA } from '@/api'

const $q = useQuasar()

const qas = ref<OrderQA[]>([])
const loading = ref(false)
const showDialog = ref(false)
const showAnswerDialog = ref(false)
const searchOrderId = ref('')
const filterAnswered = ref('all')
const currentQA = ref<OrderQA | null>(null)

const form = ref<OrderQA>({
  orderId: 0,
  askerType: 'CUSTOMER',
  askerName: '',
  question: ''
})

const answerForm = ref({
  answer: '',
  answererId: 0,
  answererName: ''
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderId', label: '订单ID', align: 'left' as const, field: 'orderId', sortable: true },
  { name: 'question', label: '问题', align: 'left' as const, field: 'question' },
  { name: 'answer', label: '回答', align: 'left' as const, field: 'answer' },
  { name: 'status', label: '状态', align: 'center' as const, field: 'status' },
  { name: 'createdAt', label: '提问时间', align: 'left' as const, field: 'createdAt' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const filteredQAs = computed(() => {
  if (filterAnswered.value === 'all') return qas.value
  if (filterAnswered.value === 'answered') return qas.value.filter(qa => qa.answer)
  if (filterAnswered.value === 'unanswered') return qas.value.filter(qa => !qa.answer)
  return qas.value
})

const resetForm = () => {
  form.value = {
    orderId: 0,
    askerType: 'CUSTOMER',
    askerName: '',
    question: ''
  }
}

const searchByOrderId = async () => {
  if (!searchOrderId.value) {
    $q.notify({
      type: 'warning',
      message: '请输入订单ID',
      position: 'top'
    })
    return
  }
  
  loading.value = true
  try {
    const response = await orderQAApi.getQAByOrderId(Number(searchOrderId.value))
    qas.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '查询失败',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const clearFilters = () => {
  searchOrderId.value = ''
  filterAnswered.value = 'all'
  qas.value = []
}

const handleSubmit = async () => {
  if (!form.value.orderId || !form.value.askerType || !form.value.question) {
    $q.notify({
      type: 'warning',
      message: '请填写必填字段',
      position: 'top'
    })
    return
  }

  try {
    await orderQAApi.askQuestion(form.value)
    $q.notify({
      type: 'positive',
      message: '问题提交成功',
      position: 'top'
    })
    showDialog.value = false
    resetForm()
    // Refresh list if we have a search active
    if (searchOrderId.value) {
      searchByOrderId()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '提交失败',
      position: 'top'
    })
  }
}

const handleAnswer = (qa: OrderQA) => {
  currentQA.value = qa
  answerForm.value = {
    answer: '',
    answererId: 0,
    answererName: ''
  }
  showAnswerDialog.value = true
}

const handleAnswerSubmit = async () => {
  if (!currentQA.value?.id || !answerForm.value.answer || !answerForm.value.answererName) {
    $q.notify({
      type: 'warning',
      message: '请填写必填字段',
      position: 'top'
    })
    return
  }

  try {
    await orderQAApi.answerQuestion(
      currentQA.value.id,
      answerForm.value.answer,
      answerForm.value.answererId,
      answerForm.value.answererName
    )
    $q.notify({
      type: 'positive',
      message: '回答提交成功',
      position: 'top'
    })
    showAnswerDialog.value = false
    // Refresh list if we have a search active
    if (searchOrderId.value) {
      searchByOrderId()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '回答提交失败',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return
  
  $q.dialog({
    title: '确认删除',
    message: '确定要删除这条问答记录吗？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderQAApi.deleteQA(id)
      $q.notify({
        type: 'positive',
        message: '删除成功',
        position: 'top'
      })
      // Refresh list if we have a search active
      if (searchOrderId.value) {
        searchByOrderId()
      }
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '删除失败',
        position: 'top'
      })
    }
  })
}
</script>

<style scoped>
.order-qa-management {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
