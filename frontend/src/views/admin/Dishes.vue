<template>
  <div>
    <div class="card">
      <div class="card-title">菜品管理</div>
      <div style="margin-bottom: 15px;">
        <el-button type="primary" @click="showAddDialog">添加菜品</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '添加菜品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属食堂">
          <el-select v-model="form.canteenId" @change="fetchWindows" style="width: 100%;">
            <el-option v-for="c in canteens" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属窗口">
          <el-select v-model="form.windowId" style="width: 100%;">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const dishes = ref([])
const canteens = ref([])
const windows = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
  id: null,
  name: '',
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
      params: { pageNum: pageNum.value, pageSize: pageSize.value, status: '' }
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

const fetchWindows = async () => {
  if (!form.canteenId) return
  const res = await api.get(`/windows/canteen/${form.canteenId}`)
  windows.value = res.data || []
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', canteenId: null, windowId: null, price: 0, category: '', taste: '', description: '', status: 1 })
  windows.value = []
  dialogVisible.value = true
}

const editDish = (dish) => {
  isEdit.value = true
  Object.assign(form, dish)
  fetchWindows()
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

onMounted(() => {
  fetchDishes()
  fetchCanteens()
})
</script>
