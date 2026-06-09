<template>
  <div>
    <div class="card">
      <div class="card-title">菜品管理</div>
      <div style="margin-bottom: 15px; display: flex; gap: 10px;">
        <el-button type="primary" @click="showAddDialog">添加菜品</el-button>
        <el-button type="success" @click="showImportDialog">批量导入</el-button>
      </div>
      <el-table :data="dishes" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="canteenName" label="食堂" />
        <el-table-column prop="windowName" label="窗口" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="avgRating" label="评分" width="100">
          <template #default="{ row }">{{ row.avgRating?.toFixed(1) || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editDish(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteDish(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchDishes"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '添加菜品'" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            accept="image/*"
          >
            <img v-if="form.image" :src="getImageUrl(form.image)" class="upload-preview" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="所属食堂">
          <el-select v-model="form.canteenId" @change="handleCanteenChange" style="width: 100%;">
            <el-option v-for="c in canteens" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-select v-model="formFloor" @change="handleFloorChange" style="width: 100%;" :disabled="!form.canteenId">
            <el-option v-for="f in floors" :key="f" :label="f + '楼'" :value="f" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属窗口">
          <el-select v-model="form.windowId" style="width: 100%;" :disabled="!formFloor">
            <el-option v-for="w in windows" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%;">
            <el-option label="热菜" value="热菜" />
            <el-option label="面食" value="面食" />
            <el-option label="套餐" value="套餐" />
            <el-option label="小吃" value="小吃" />
            <el-option label="特色" value="特色" />
          </el-select>
        </el-form-item>
        <el-form-item label="口味">
          <el-input v-model="form.taste" placeholder="如：微辣、中辣、清淡" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDish">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入菜品" width="500px">
      <div style="margin-bottom: 16px;">
        <el-button type="primary" link @click="downloadTemplate">📥 下载导入模板</el-button>
      </div>
      <el-upload
        ref="importUploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleImportFileChange"
        :on-remove="handleImportFileChange"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx 格式文件，单次最多导入 200 条</div>
        </template>
      </el-upload>
      <div v-if="importResult" style="margin-top: 16px;">
        <el-alert :type="importResult.failCount > 0 ? 'warning' : 'success'" :closable="false">
          <div>成功：<strong>{{ importResult.successCount }}</strong> 条</div>
          <div>失败：<strong>{{ importResult.failCount }}</strong> 条</div>
        </el-alert>
        <div v-if="importResult.errors?.length" style="margin-top: 8px; max-height: 200px; overflow-y: auto;">
          <div v-for="(err, i) in importResult.errors" :key="i" style="color: #f56c6c; font-size: 13px; padding: 2px 0;">{{ err }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="submitImport" :loading="importLoading">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import api, { IMAGE_BASE } from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const dishes = ref([])
const canteens = ref([])
const floors = ref([])
const windows = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formFloor = ref(null)

const importDialogVisible = ref(false)
const importLoading = ref(false)
const importResult = ref(null)
const importUploadRef = ref(null)

const uploadUrl = '/api/upload'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const form = reactive({
  id: null,
  name: '',
  image: '',
  canteenId: null,
  windowId: null,
  price: 0,
  category: '',
  taste: '',
  description: '',
  status: 1
})

const fetchDishes = async () => {
  loading.value = true
  try {
    const res = await api.get('/dishes', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    dishes.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const fetchCanteens = async () => {
  const res = await api.get('/canteens')
  canteens.value = res.data || []
}

const fetchFloors = async (canteenId) => {
  if (!canteenId) { floors.value = []; return }
  const res = await api.get(`/windows/canteen/${canteenId}/floors`)
  floors.value = res.data || []
}

const fetchWindows = async (canteenId, floor) => {
  if (!canteenId || !floor) { windows.value = []; return }
  const res = await api.get(`/windows/canteen/${canteenId}`, { params: { floor } })
  windows.value = res.data || []
}

const handleCanteenChange = () => {
  formFloor.value = null
  form.windowId = null
  windows.value = []
  fetchFloors(form.canteenId)
}

const handleFloorChange = () => {
  form.windowId = null
  fetchWindows(form.canteenId, formFloor.value)
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    form.image = res.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', image: '', canteenId: null, windowId: null, price: 0, category: '', taste: '', description: '', status: 1 })
  formFloor.value = null
  floors.value = []
  windows.value = []
  dialogVisible.value = true
}

const editDish = async (dish) => {
  isEdit.value = true
  Object.assign(form, dish)
  formFloor.value = null
  if (dish.canteenId) {
    await fetchFloors(dish.canteenId)
    if (dish.windowId) {
      const winRes = await api.get(`/windows/canteen/${dish.canteenId}`)
      const allWindows = winRes.data || []
      const win = allWindows.find(w => w.id === dish.windowId)
      if (win) {
        formFloor.value = win.floor
        await fetchWindows(dish.canteenId, win.floor)
      }
    }
  }
  dialogVisible.value = true
}

const saveDish = async () => {
  if (isEdit.value) {
    await api.put('/dishes', form)
    ElMessage.success('修改成功')
  } else {
    await api.post('/dishes', form)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  fetchDishes()
}

const deleteDish = async (dish) => {
  await ElMessageBox.confirm('确定删除该菜品吗？', '提示', { type: 'warning' })
  await api.delete(`/dishes/${dish.id}`)
  ElMessage.success('删除成功')
  fetchDishes()
}

const showImportDialog = () => {
  importResult.value = null
  importFile.value = null
  importDialogVisible.value = true
}

const downloadTemplate = () => {
  const headers = ['菜品名称', '食堂名称', '楼层', '窗口名称', '价格', '分类', '口味', '描述']
  const example = ['示例菜品', '第一食堂', '1', '川菜窗口', '15.00', '热菜', '微辣', '示例描述']
  const ws = XLSX.utils.aoa_to_sheet([headers, example])
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  XLSX.writeFile(wb, '菜品导入模板.xlsx')
}

const importFile = ref(null)

const handleImportFileChange = (file) => {
  importResult.value = null
  importFile.value = file?.raw || null
}

const submitImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  const formData = new FormData()
  formData.append('file', importFile.value)

  importLoading.value = true
  try {
    const res = await api.post('/dishes/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    importResult.value = res.data
    if (res.data.failCount === 0) {
      ElMessage.success(`成功导入 ${res.data.successCount} 条菜品`)
      fetchDishes()
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '导入请求失败')
  } finally {
    importLoading.value = false
  }
}

onMounted(() => {
  fetchDishes()
  fetchCanteens()
})
</script>

<style scoped>
.upload-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.upload-icon {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #999;
  cursor: pointer;
}

.upload-icon:hover {
  border-color: #667eea;
  color: #667eea;
}
</style>
