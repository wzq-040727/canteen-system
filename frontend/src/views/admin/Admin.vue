<template>
  <div>
    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-value">{{ dashboard.totalDishes || 0 }}</div>
        <div class="stat-label">菜品总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboard.totalReviews || 0 }}</div>
        <div class="stat-label">评价总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboard.totalUsers || 0 }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboard.avgRating?.toFixed(1) || 0 }}</div>
        <div class="stat-label">平均评分</div>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="card">
          <div class="card-title">热门菜品 TOP 5</div>
          <el-table :data="dashboard.topDishes || []" size="small">
            <el-table-column prop="name" label="菜品名称" />
            <el-table-column prop="avgRating" label="评分" width="80">
              <template #default="{ row }">
                {{ row.avgRating?.toFixed(1) || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="ratingCount" label="评价数" width="80" />
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-title">最新评价</div>
          <el-table :data="dashboard.recentReviews || []" size="small">
            <el-table-column prop="dishName" label="菜品" />
            <el-table-column prop="rating" label="评分" width="80">
              <template #default="{ row }">
                <span class="rating-stars">{{ '★'.repeat(row.rating) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
          </el-table>
        </div>
      </el-col>
    </el-row>

    <div class="card" style="margin-top: 20px;">
      <div class="card-title">快捷入口</div>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/admin/dishes')">菜品管理</el-button>
        <el-button type="primary" @click="$router.push('/admin/reviews')">评价管理</el-button>
        <el-button type="primary" @click="$router.push('/admin/users')">用户管理</el-button>
      </el-space>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/api'

const dashboard = ref({})

onMounted(async () => {
  const res = await api.get('/dashboard')
  dashboard.value = res.data || {}
})
</script>
