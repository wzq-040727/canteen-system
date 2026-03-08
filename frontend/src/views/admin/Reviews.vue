<template>
  <div>
    <div class="card">
      <div class="card-title">评价管理</div>
      <el-table :data="reviews" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="dishName" label="菜品" />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <span class="rating-stars">{{ '★'.repeat(row.rating) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="时间" width="150">
          <template #default="{ row }">{{ formatTime(row.createdTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '隐藏' : '显示' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteReview(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchReviews"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const reviews = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await api.get('/reviews/admin/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    reviews.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    reviews.value = []
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (review) => {
  const newStatus = review.status === 1 ? 0 : 1
  await api.put(`/reviews/${review.id}/audit`, null, { params: { status: newStatus } })
  review.status = newStatus
  ElMessage.success('操作成功')
}

const deleteReview = async (review) => {
  await ElMessageBox.confirm('确定删除该评价吗？', '提示', { type: 'warning' })
  await api.delete(`/reviews/${review.id}`)
  ElMessage.success('删除成功')
  fetchReviews()
}

onMounted(() => {
  fetchReviews()
})
</script>
