<template>
  <div class="card">
    <div class="card-title">食堂管理</div>
    <el-table :data="canteens" v-loading="loading">
      <el-table-column prop="name" label="食堂" min-width="120" />
      <el-table-column prop="location" label="位置" min-width="100" />
      <el-table-column label="楼层数" width="80" align="center">
        <template #default="{ row }">{{ row.floorCount || 1 }}</template>
      </el-table-column>
      <el-table-column label="营业状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="!canEditStatus"
            @change="updateStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="营业时间" min-width="180">
        <template #default="{ row }">
          <el-input
            v-model="row.openingHours"
            :disabled="!canEditHours"
            placeholder="例如 07:00-21:00"
            @blur="updateHours(row)"
            size="small"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDrawer(row)">管理窗口</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawerVisible" :title="currentCanteen?.name + ' - 窗口管理'" size="65%">
      <div v-if="currentCanteen" style="padding: 0 10px;">
        <el-divider content-position="left">基础信息</el-divider>
        <el-form :model="editForm" label-width="80px" style="max-width: 500px;">
          <el-form-item label="楼层数">
            <el-input-number v-model="editForm.floorCount" :min="1" :max="20" @change="updateFloorCount" />
          </el-form-item>
        </el-form>

        <el-divider content-position="left">楼层与窗口</el-divider>
        <el-tabs v-model="activeFloorTab" @tab-change="loadFloorWindows">
          <el-tab-pane v-for="f in drawerFloors" :key="f" :label="f + '楼'" :name="String(f)" />
        </el-tabs>

        <div style="margin-bottom: 12px;">
          <el-button type="primary" size="small" @click="showWindowDialog()" :disabled="(floorWindows?.length || 0) >= 10">
            新增窗口
          </el-button>
          <span v-if="(floorWindows?.length || 0) >= 10" style="color: #f56c6c; font-size: 12px; margin-left: 8px;">
            该楼层窗口数已达上限（10个）
          </span>
        </div>

        <el-table :data="floorWindows" size="small">
          <el-table-column prop="name" label="窗口名称" />
          <el-table-column prop="cuisineType" label="菜系" width="100" />
          <el-table-column label="营业时间" width="160">
            <template #default="{ row }">
              {{ row.openTime && row.closeTime ? row.openTime + '-' + row.closeTime : '全天' }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '营业' : '关闭' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="130">
            <template #default="{ row }">
              <el-button size="small" @click="showWindowDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteWindow(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>

    <el-dialog v-model="windowDialogVisible" :title="isEditWindow ? '编辑窗口' : '新增窗口'" width="450px">
      <el-form :model="windowForm" label-width="80px">
        <el-form-item label="窗口名称">
          <el-input v-model="windowForm.name" />
        </el-form-item>
        <el-form-item label="菜系类型">
          <el-input v-model="windowForm.cuisineType" placeholder="如：川菜、粤菜、面食" />
        </el-form-item>
        <el-form-item label="营业时间">
          <div style="display: flex; gap: 10px; align-items: center;">
            <el-time-picker v-model="windowForm.openTime" format="HH:mm" value-format="HH:mm" placeholder="开始" style="width: 140px;" />
            <span>至</span>
            <el-time-picker v-model="windowForm.closeTime" format="HH:mm" value-format="HH:mm" placeholder="结束" style="width: 140px;" />
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="windowForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="windowForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="windowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveWindow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../utils/api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const canteens = ref([])
const loading = ref(false)
const canEditStatus = computed(() => userStore.user?.role === 2)
const canEditHours = computed(() => userStore.user?.role === 1 || userStore.user?.role === 2)

const drawerVisible = ref(false)
const currentCanteen = ref(null)
const drawerFloors = ref([])
const activeFloorTab = ref('1')
const floorWindows = ref([])
const editForm = reactive({ floorCount: 1 })

const windowDialogVisible = ref(false)
const isEditWindow = ref(false)
const windowForm = reactive({
  id: null,
  name: '',
  cuisineType: '',
  openTime: null,
  closeTime: null,
  description: '',
  status: 1
})

const loadCanteens = async () => {
  loading.value = true
  try {
    const res = await api.get('/canteens/admin/list')
    canteens.value = res.data || []
  } finally {
    loading.value = false
  }
}

const updateStatus = async (row) => {
  if (!canEditStatus.value) return
  await api.put('/canteens/admin/update', { id: row.id, status: row.status, openingHours: row.openingHours })
  ElMessage.success('营业状态已更新')
}

const updateHours = async (row) => {
  if (!canEditHours.value) return
  await api.put('/canteens/admin/update', { id: row.id, openingHours: row.openingHours })
  ElMessage.success('营业时间已更新')
}

const updateFloorCount = async () => {
  try {
    await api.put('/canteens/admin/update', { id: currentCanteen.value.id, floorCount: editForm.floorCount })
    ElMessage.success('楼层数已更新')
    currentCanteen.value.floorCount = editForm.floorCount
    loadDrawerFloors()
  } catch (e) {
    console.error(e)
  }
}

const openDrawer = (row) => {
  currentCanteen.value = row
  editForm.floorCount = row.floorCount || 1
  drawerVisible.value = true
  loadDrawerFloors()
}

const loadDrawerFloors = () => {
  const count = editForm.floorCount || 1
  drawerFloors.value = Array.from({ length: count }, (_, i) => i + 1)
  if (!drawerFloors.value.includes(Number(activeFloorTab.value))) {
    activeFloorTab.value = String(drawerFloors.value[0])
  }
  loadFloorWindows()
}

const loadFloorWindows = async () => {
  if (!currentCanteen.value) return
  const res = await api.get(`/windows/canteen/${currentCanteen.value.id}`, {
    params: { floor: Number(activeFloorTab.value) }
  })
  floorWindows.value = res.data || []
}

const showWindowDialog = (win) => {
  if (win) {
    isEditWindow.value = true
    Object.assign(windowForm, {
      id: win.id,
      name: win.name,
      cuisineType: win.cuisineType || '',
      openTime: win.openTime || null,
      closeTime: win.closeTime || null,
      description: win.description || '',
      status: win.status ?? 1
    })
  } else {
    isEditWindow.value = false
    Object.assign(windowForm, { id: null, name: '', cuisineType: '', openTime: null, closeTime: null, description: '', status: 1 })
  }
  windowDialogVisible.value = true
}

const saveWindow = async () => {
  if (!windowForm.name) {
    ElMessage.warning('请输入窗口名称')
    return
  }
  if (isEditWindow.value) {
    await api.put(`/windows/${windowForm.id}`, windowForm)
    ElMessage.success('窗口已更新')
  } else {
    await api.post('/windows', {
      ...windowForm,
      canteenId: currentCanteen.value.id,
      floor: Number(activeFloorTab.value)
    })
    ElMessage.success('窗口已添加')
  }
  windowDialogVisible.value = false
  loadFloorWindows()
}

const deleteWindow = async (win) => {
  await ElMessageBox.confirm('确定删除该窗口吗？如有菜品需先移除。', '提示', { type: 'warning' })
  await api.delete(`/windows/${win.id}`)
  ElMessage.success('窗口已删除')
  loadFloorWindows()
}

onMounted(loadCanteens)
</script>
