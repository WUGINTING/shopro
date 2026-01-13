<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none" data-tour="title">會員群組管理</h4>
      </div>
      <div class="col-auto">
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            round
            icon="help_outline"
            color="grey-7"
            @click="handleStartTour"
          >
            <q-tooltip>會員群組管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            label="新增群組"
            icon="add"
            data-tour="add-group-btn"
            @click="showCreateDialog"
          />
        </div>
      </div>
    </div>

    <!-- 群組列表 -->
    <div class="row q-col-gutter-md" data-tour="group-list">
      <div
        v-for="group in groups"
        :key="group.id"
        class="col-12 col-sm-6 col-md-4 col-lg-3"
      >
        <q-card
          class="cursor-pointer hover-highlight"
          :class="{ 'disabled-group': !group.enabled }"
          data-tour="group-card"
        >
          <q-card-section>
            <div class="row items-start">
              <div class="col">
                <h6 class="q-my-xs">{{ group.name }}</h6>
                <p class="text-caption q-my-xs text-grey-7">
                  {{ group.description || '無描述' }}
                </p>
              </div>
              <div class="col-auto">
                <q-badge
                  :label="group.enabled ? '啟用' : '停用'"
                  :color="group.enabled ? 'green' : 'red'"
                />
              </div>
            </div>
          </q-card-section>

          <q-separator />

          <q-card-section class="text-center">
            <div class="text-h6 text-primary">
              {{ group.memberCount || 0 }}
            </div>
            <p class="text-caption q-my-xs">位成員</p>
          </q-card-section>

          <q-separator />

          <q-card-actions data-tour="group-actions">
            <q-btn
              flat
              dense
              label="編輯"
              color="primary"
              @click="showEditDialog(group)"
            />
            <q-btn
              flat
              dense
              label="成員"
              color="info"
              @click="showMembersDialog(group.id!)"
            />
            <q-btn
              flat
              dense
              label="刪除"
              color="negative"
              @click="confirmDelete(group.id!)"
            />
          </q-card-actions>
        </q-card>
      </div>

      <!-- 空狀態 -->
      <div
        v-if="groups.length === 0"
        class="col-12 text-center q-py-lg"
      >
        <p class="text-grey-7">尚無會員群組</p>
      </div>
    </div>

    <!-- 新增/編輯對話框 -->
    <q-dialog v-model="showDialog">
      <q-card style="width: 500px; max-width: 90vw" data-tour="group-form-dialog">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">
            {{ editingGroup?.id ? '編輯群組' : '新增群組' }}
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
          <q-form ref="groupForm" @submit="saveGroup">
            <q-input
              v-model="editingGroup.name"
              label="群組名稱 *"
              outlined
              dense
              class="q-mb-md"
              :rules="[val => !!val || '請輸入群組名稱']"
            />

            <q-input
              v-model="editingGroup.description"
              label="描述"
              outlined
              dense
              type="textarea"
              class="q-mb-md"
            />

            <q-checkbox
              v-model="editingGroup.enabled"
              label="啟用此群組"
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
          <span class="q-ml-md">確定要刪除此群組嗎？</span>
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
            @click="deleteGroup"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 成員管理對話框 -->
    <q-dialog v-model="showMembersDialogFlag" style="width: 600px; max-width: 90vw">
      <q-card data-tour="member-management-dialog">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">群組成員管理</div>
          <q-space />
          <q-btn
            icon="close"
            flat
            round
            dense
            @click="showMembersDialogFlag = false"
          />
        </q-card-section>

        <q-separator />

        <q-card-section>
          <div class="row q-mb-md">
            <div class="col">
              <p class="text-caption text-grey-7">
                該群組共有 {{ groupMemberDetails.length }} 位成員
              </p>
            </div>
          </div>

          <!-- 添加成員 -->
          <div class="row q-col-gutter-md q-mb-md">
            <div class="col">
              <q-select
                v-model="selectedMemberId"
                :options="memberOptions"
                option-label="label"
                option-value="value"
                label="選擇會員"
                outlined
                dense
                use-input
                input-debounce="0"
                map-options
                emit-value
                @filter="filterMembers"
                clearable
              >
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey">
                      無會員資料
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
            </div>
            <div class="col-auto">
              <q-btn
                color="primary"
                label="添加"
                :loading="addingMember"
                :disable="!selectedMemberId"
                @click="addMemberToGroup"
              />
            </div>
          </div>

          <q-separator class="q-mb-md" />

          <!-- 成員列表 -->
          <q-virtual-scroll
            :items="groupMemberDetails"
            virtual-scroll-item-size="70"
            style="max-height: 400px"
          >
            <template #default="{ item, index }">
              <div class="q-pa-md border-bottom">
                <div class="row items-center">
                  <div class="col">
                    <p class="q-my-none text-weight-medium">{{ item.name }}</p>
                    <p class="q-my-none text-caption text-grey-7">{{ item.email }}</p>
                    <p class="q-my-none text-caption text-grey-6">ID: {{ item.id }}</p>
                  </div>
                  <div class="col-auto">
                    <q-btn
                      flat
                      round
                      dense
                      icon="close"
                      color="negative"
                      size="sm"
                      @click="removeMemberFromGroup(item.id)"
                    />
                  </div>
                </div>
              </div>
            </template>
          </q-virtual-scroll>
        </q-card-section>

        <q-separator />

        <q-card-actions align="right">
          <q-btn
            label="關閉"
            flat
            @click="showMembersDialogFlag = false"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { memberGroupApi, type MemberGroup } from '@/api/memberGroup'
import { memberApi } from '@/api/member'
import { startMemberGroupTour, isMemberGroupTourCompleted } from '@/utils/memberGroupTour'

const $q = useQuasar()
const groupForm = ref()

// 資料
const groups = ref<MemberGroup[]>([])
const groupMembers = ref<number[]>([])
const groupMemberDetails = ref<Array<{ id: number; name: string; email: string }>>([])
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const addingMember = ref(false)

// 會員選項
const allMembers = ref<Array<{ label: string; value: number }>>([])
const memberOptions = ref<Array<{ label: string; value: number }>>([])
const selectedMemberId = ref<number | undefined>(undefined)

// 對話框
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const showMembersDialogFlag = ref(false)
const deleteId = ref<number>()
const currentGroupId = ref<number>()

// 編輯群組
const editingGroup = ref<Partial<MemberGroup>>({
  name: '',
  description: '',
  enabled: true
})

// 載入群組列表
const loadGroups = async () => {
  loading.value = true
  try {
    const result = await memberGroupApi.getGroups()
    groups.value = result?.content || []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入群組列表失敗',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// 顯示新增對話框
const showCreateDialog = () => {
  editingGroup.value = {
    name: '',
    description: '',
    enabled: true
  }
  showDialog.value = true
}

// 顯示編輯對話框
const showEditDialog = (group: MemberGroup) => {
  editingGroup.value = { ...group }
  showDialog.value = true
}

// 保存群組
const saveGroup = async () => {
  saving.value = true
  try {
    if (editingGroup.value.id) {
      await memberGroupApi.updateGroup(
        editingGroup.value.id,
        editingGroup.value
      )
      $q.notify({
        type: 'positive',
        message: '群組已更新',
        position: 'top'
      })
    } else {
      await memberGroupApi.createGroup(editingGroup.value as MemberGroup)
      $q.notify({
        type: 'positive',
        message: '群組已建立',
        position: 'top'
      })
    }
    showDialog.value = false
    loadGroups()
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

// 刪除群組
const deleteGroup = async () => {
  if (!deleteId.value) return
  deleting.value = true
  try {
    await memberGroupApi.deleteGroup(deleteId.value)
    $q.notify({
      type: 'positive',
      message: '群組已刪除',
      position: 'top'
    })
    showDeleteDialog.value = false
    loadGroups()
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

// 載入會員列表
const loadMembers = async () => {
  try {
    const result = await memberApi.getMembers({ page: 0, size: 100 })
    allMembers.value = (result.content || []).map((member: { id?: number; name: string }) => ({
      label: `${member.name} (ID: ${member.id})`,
      value: member.id!
    }))
    memberOptions.value = allMembers.value
  } catch (error) {
    console.error('載入會員列表失敗', error)
  }
}

// 篩選會員
const filterMembers = (val: string, update: (callback: () => void) => void) => {
  if (val === '') {
    update(() => {
      memberOptions.value = allMembers.value.filter(
        m => !groupMembers.value.includes(m.value)
      )
    })
    return
  }
  update(() => {
    const needle = val.toLowerCase()
    memberOptions.value = allMembers.value.filter(
      m => m.label.toLowerCase().indexOf(needle) > -1 && !groupMembers.value.includes(m.value)
    )
  })
}

// 顯示成員管理對話框
const showMembersDialog = async (groupId: number) => {
  currentGroupId.value = groupId
  selectedMemberId.value = undefined
  try {
    groupMembers.value = await memberGroupApi.getGroupMembers(groupId)
    // 載入會員詳細資訊
    await loadMemberDetails()
    // 過濾掉已在群組中的會員
    memberOptions.value = allMembers.value.filter(
      m => !groupMembers.value.includes(m.value)
    )
    showMembersDialogFlag.value = true
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入成員失敗',
      position: 'top'
    })
  }
}

// 載入會員詳細資訊
const loadMemberDetails = async () => {
  groupMemberDetails.value = []
  for (const memberId of groupMembers.value) {
    try {
      const member = await memberApi.getMember(memberId)
      groupMemberDetails.value.push({
        id: member.id!,
        name: member.name,
        email: member.email
      })
    } catch (error) {
      // 如果獲取失敗，至少顯示ID
      groupMemberDetails.value.push({
        id: memberId,
        name: `會員 ID: ${memberId}`,
        email: ''
      })
    }
  }
}

// 添加成員到群組
const addMemberToGroup = async () => {
  if (!currentGroupId.value || !selectedMemberId.value) return
  
  // 檢查是否已在群組中
  if (groupMembers.value.includes(selectedMemberId.value)) {
    $q.notify({
      type: 'warning',
      message: '該會員已在群組中',
      position: 'top'
    })
    return
  }

  addingMember.value = true
  try {
    await memberGroupApi.addMemberToGroup(currentGroupId.value, selectedMemberId.value)
    groupMembers.value.push(selectedMemberId.value)
    
    // 獲取新添加會員的詳細資訊
    try {
      const member = await memberApi.getMember(selectedMemberId.value)
      groupMemberDetails.value.push({
        id: member.id!,
        name: member.name,
        email: member.email
      })
    } catch (error) {
      // 如果獲取失敗，至少顯示ID
      groupMemberDetails.value.push({
        id: selectedMemberId.value,
        name: `會員 ID: ${selectedMemberId.value}`,
        email: ''
      })
    }
    
    // 從選項中移除已添加的會員
    memberOptions.value = memberOptions.value.filter(
      m => m.value !== selectedMemberId.value
    )
    selectedMemberId.value = undefined
    $q.notify({
      type: 'positive',
      message: '成員已添加',
      position: 'top'
    })
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error?.response?.data?.message || '添加失敗',
      position: 'top'
    })
  } finally {
    addingMember.value = false
  }
}

// 從群組移除成員
const removeMemberFromGroup = async (memberId: number) => {
  if (!currentGroupId.value) return
  try {
    await memberGroupApi.removeMemberFromGroup(
      currentGroupId.value,
      memberId
    )
    groupMembers.value = groupMembers.value.filter(id => id !== memberId)
    groupMemberDetails.value = groupMemberDetails.value.filter(m => m.id !== memberId)
    
    // 將移除的會員重新加入選項
    const removedMember = allMembers.value.find(m => m.value === memberId)
    if (removedMember && !memberOptions.value.find(m => m.value === memberId)) {
      memberOptions.value.push(removedMember)
    }
    
    $q.notify({
      type: 'positive',
      message: '成員已移除',
      position: 'top'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '移除失敗',
      position: 'top'
    })
  }
}

// 啟動導覽
const handleStartTour = () => {
  startMemberGroupTour(true)
}

onMounted(() => {
  loadGroups()
  loadMembers()
  
  // 如果是第一次訪問，自動啟動導覽
  if (!isMemberGroupTourCompleted()) {
    setTimeout(() => {
      startMemberGroupTour()
    }, 500)
  }
})
</script>

<style scoped>
.disabled-group {
  opacity: 0.6;
}

.hover-highlight:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.border-bottom {
  border-bottom: 1px solid #e0e0e0;
}
</style>
