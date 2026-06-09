<template>
  <div class="card">
    <div class="card-title">公告管理</div>
    <div style="margin-bottom: 15px;">
      <el-button type="primary" @click="showAddDialog">发布公告</el-button>
    </div>
    <el-table :data="announcements" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column label="所属食堂" width="120">
        <template #default="{ row }">
          {{ getCanteenName(row.canteenId) }}
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="70" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="200">
        <template #default="{ row }">
          <span v-if="row.startTime || row.endTime">
            {{ row.startTime || '不限' }} ~ {{ row.endTime || '不限' }}
          </span>
          <span v-else style="color: #999;">永久有效</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteAnnouncement(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="所属食堂">
          <el-select v-model="form.canteenId" style="width: 100%;">
            <el-option v-for="c in canteens" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker
            v-model="form.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAnnouncement">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const announcements = ref([])
const canteens = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
  id: null,
  canteenId: null,
  title: '',
  content: '',
  isTop: 0,
  dateRange: null
})

const getCanteenName = (id) => {
  const c = canteens.value.find(c => c.id === id)
  return c?.name || '未知'
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const all = []
    for (const canteen of canteens.value) {
      try {
        const res = await api.get(`/announcements/canteen/${canteen.id}`)
        if (res.data) all.push(...res.data)
      } catch (e) { /* skip */ }
    }
    all.sort((a, b) => {
      if (a.isTop !== b.isTop) return b.isTop - a.isTop
      return new Date(b.createdTime) - new Date(a.createdTime)
    })
    announcements.value = all
  } finally {
    loading.value = false
  }
}

const loadCanteens = async () => {
  const res = await api.get('/canteens')
  canteens.value = res.data || []
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, canteenId: null, title: '', content: '', isTop: 0, dateRange: null })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    canteenId: row.canteenId,
    title: row.title,
    content: row.content,
    isTop: row.isTop || 0,
    dateRange: (row.startTime || row.endTime) ? [row.startTime, row.endTime] : null
  })
  dialogVisible.value = true
}

const saveAnnouncement = async () => {
  if (!form.canteenId || !form.title) {
    ElMessage.warning('请选择食堂并填写标题')
    return
  }
  const data = {
    canteenId: form.canteenId,
    title: form.title,
    content: form.content,
    isTop: form.isTop,
    startTime: form.dateRange?.[0] || null,
    endTime: form.dateRange?.[1] || null
  }
  if (isEdit.value) {
    await api.put(`/announcements/${form.id}`, data)
    ElMessage.success('公告已更新')
  } else {
    await api.post('/announcements', data)
    ElMessage.success('公告已发布')
  }
  dialogVisible.value = false
  loadAnnouncements()
}

const deleteAnnouncement = async (row) => {
  await ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
  await api.delete(`/announcements/${row.id}`)
  ElMessage.success('公告已删除')
  loadAnnouncements()
}

onMounted(async () => {
  await loadCanteens()
  loadAnnouncements()
})
</script>
