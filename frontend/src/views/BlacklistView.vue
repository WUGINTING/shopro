<template>
  <q-page padding>
    <div class="blacklist-page">
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">顧客黑名單</div>
          <div class="text-caption text-grey-7">黑名單中的顧客無法下單（前台結帳會被拒絕）。移除後即可恢復下單。</div>
        </div>
        <q-btn color="negative" unelevated no-caps icon="block" label="加入黑名單" @click="openAdd" />
      </div>

      <q-table :rows="entries" :columns="columns" row-key="id" flat bordered :loading="loading" :pagination="{ rowsPerPage: 20 }">
        <template #body-cell-actions="props">
          <q-td :props="props">
            <q-btn flat dense no-caps color="primary" label="移除" @click="remove(props.row)" />
          </q-td>
        </template>
        <template #no-data>
          <div class="full-width text-center text-grey-7 q-pa-md">目前沒有黑名單顧客</div>
        </template>
      </q-table>
    </div>

    <q-dialog v-model="showAdd">
      <q-card style="width: 480px; max-width: 95vw">
        <q-card-section class="text-h6">加入黑名單</q-card-section>
        <q-card-section class="q-gutter-md">
          <q-select
            v-model="selectedMember"
            outlined
            dense
            use-input
            input-debounce="300"
            label="搜尋顧客（姓名 / Email / 電話）*"
            :options="memberOptions"
            option-label="label"
            @filter="searchMembers"
          >
            <template #no-option>
              <q-item><q-item-section class="text-grey">輸入關鍵字搜尋</q-item-section></q-item>
            </template>
          </q-select>
          <q-input v-model="reason" outlined dense label="原因 *" maxlength="500" hint="例如：多次惡意棄單、詐騙" />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="取消" v-close-popup />
          <q-btn color="negative" unelevated label="加入" :loading="saving" :disable="!selectedMember || !reason.trim()" @click="add" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useQuasar, type QTableColumn } from 'quasar'
import { blacklistApi, type BlacklistEntry } from '@/api/blacklist'
import { memberApi } from '@/api/member'

type MemberOption = { label: string; id: number; name: string; email?: string; phone?: string }

const $q = useQuasar()
const entries = ref<BlacklistEntry[]>([])
const loading = ref(false)
const showAdd = ref(false)
const saving = ref(false)
const selectedMember = ref<MemberOption | null>(null)
const memberOptions = ref<MemberOption[]>([])
const reason = ref('')

const columns: QTableColumn<BlacklistEntry>[] = [
  { name: 'customerName', label: '顧客', field: 'customerName', align: 'left' },
  { name: 'customerEmail', label: 'Email', field: 'customerEmail', align: 'left' },
  { name: 'customerPhone', label: '電話', field: 'customerPhone', align: 'left' },
  { name: 'reason', label: '原因', field: 'reason', align: 'left' },
  { name: 'createdAt', label: '加入時間', field: 'createdAt', align: 'left', format: (value?: string) => (value ? new Date(value).toLocaleString('zh-TW') : '') },
  { name: 'actions', label: '操作', field: 'id', align: 'center' }
]

const load = async () => {
  loading.value = true
  try {
    const response = await blacklistApi.listActive()
    entries.value = response.data || []
  } catch {
    entries.value = []
  } finally {
    loading.value = false
  }
}

const searchMembers = (value: string, update: (callback: () => void) => void) => {
  if (!value || value.trim().length < 1) {
    update(() => {
      memberOptions.value = []
    })
    return
  }
  memberApi
    .searchMembers(value.trim())
    .then((page: any) => {
      const list = (page?.content ?? page ?? []) as Array<{ id?: number; name: string; email?: string; phone?: string }>
      update(() => {
        memberOptions.value = list
          .filter((member) => member.id != null)
          .map((member) => ({
            id: member.id as number,
            name: member.name,
            email: member.email,
            phone: member.phone,
            label: `${member.name}（${member.email || member.phone || '#' + member.id}）`
          }))
      })
    })
    .catch(() => update(() => (memberOptions.value = [])))
}

const openAdd = () => {
  selectedMember.value = null
  reason.value = ''
  showAdd.value = true
}

const add = async () => {
  if (!selectedMember.value) return
  saving.value = true
  try {
    await blacklistApi.add({
      customerId: selectedMember.value.id,
      customerName: selectedMember.value.name,
      customerEmail: selectedMember.value.email,
      customerPhone: selectedMember.value.phone,
      reason: reason.value.trim(),
      isActive: true
    })
    $q.notify({ type: 'positive', message: '已加入黑名單' })
    showAdd.value = false
    load()
  } catch {
    // 錯誤訊息由系統通知顯示
  } finally {
    saving.value = false
  }
}

const remove = (entry: BlacklistEntry) => {
  if (!entry.id) return
  $q.dialog({ title: '移除黑名單', message: `確定讓「${entry.customerName || entry.customerEmail}」恢復下單？`, cancel: true }).onOk(async () => {
    try {
      await blacklistApi.remove(entry.id!)
      load()
    } catch {
      // 錯誤訊息由系統通知顯示
    }
  })
}

onMounted(load)
</script>

<style scoped>
.blacklist-page {
  max-width: 1100px;
  margin: 0 auto;
}
</style>
