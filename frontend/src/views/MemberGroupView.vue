<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <h4 class="q-my-none">會員群組管理</h4>
      </div>
      <div class="col-auto">
        <q-btn
          color="primary"
          label="新增群組"
          icon="add"
          @click="showCreateDialog"
        />
      </div>
    </div>

    <!-- 群組列表 -->
    <div class="row q-col-gutter-md">
      <div
        v-for="group in groups"
        :key="group.id"
        class="col-12 col-sm-6 col-md-4 col-lg-3"
      >
        <q-card
          class="cursor-pointer hover-highlight"
          :class="{ 'disabled-group': !group.enabled }"
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

          <q-card-actions>
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
      <q-card style="width: 500px; max-width: 90vw">
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
      <q-card>
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
          <p class="text-caption text-grey-7 q-mb-md">
            該群組共有 {{ groupMembers.length }} 位成員
          </p>

          <q-virtual-scroll
            :items="groupMembers"
            virtual-scroll-item-size="50"
          >
            <template #default="{ item, index }">
              <div class="q-pa-md border-bottom">
                <div class="row items-center">
                  <div class="col">
                    <p class="q-my-none">會員 ID: {{ item }}</p>
                  </div>
                  <div class="col-auto">
                    <q-btn
                      flat
                      round
                      dense
                      icon="close"
                      color="negative"
                      size="sm"
                      @click="removeMemberFromGroup(item)"
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

const $q = useQuasar()
const groupForm = ref()

// 資料
const groups = ref<MemberGroup[]>([])
const groupMembers = ref<number[]>([])
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)

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
    groups.value = result.content
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

// 顯示成員管理對話框
const showMembersDialog = async (groupId: number) => {
  currentGroupId.value = groupId
  try {
    groupMembers.value = await memberGroupApi.getGroupMembers(groupId)
    showMembersDialogFlag.value = true
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入成員失敗',
      position: 'top'
    })
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

onMounted(() => {
  loadGroups()
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
