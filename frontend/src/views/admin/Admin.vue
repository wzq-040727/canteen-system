<template>
  <div>
    <div class="dashboard-stats">
      <div class="stat-card stat-dishes">
        <div class="stat-icon">🍽️</div>
        <div class="stat-value">{{ dashboard.totalDishes || 0 }}</div>
        <div class="stat-label">菜品总数</div>
      </div>
      <div class="stat-card stat-reviews">
        <div class="stat-icon">💬</div>
        <div class="stat-value">{{ dashboard.totalReviews || 0 }}</div>
        <div class="stat-label">评价总数</div>
      </div>
      <div class="stat-card stat-users">
        <div class="stat-icon">👥</div>
        <div class="stat-value">{{ dashboard.totalUsers || 0 }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card stat-rating">
        <div class="stat-icon">⭐</div>
        <div class="stat-value">{{ dashboard.avgRating?.toFixed(1) || 0 }}</div>
        <div class="stat-label">平均评分</div>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="card">
          <div class="card-title">🔥 热门菜品 TOP 5</div>
          <el-table :data="dashboard.topDishes || []" size="small" stripe>
            <el-table-column prop="name" label="菜品名称" />
            <el-table-column prop="avgRating" label="评分" width="100">
              <template #default="{ row }">
                <span class="rating-stars">{{ '★'.repeat(Math.round(row.avgRating || 0)) }}</span>
                <span class="rating-num">{{ row.avgRating?.toFixed(1) || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="ratingCount" label="评价数" width="80" />
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-title">📝 最新评价</div>
          <el-table :data="dashboard.recentReviews || []" size="small" stripe>
            <el-table-column prop="dishName" label="菜品" width="120" />
            <el-table-column prop="rating" label="评分" width="100">
              <template #default="{ row }">
                <span class="rating-stars">{{ '★'.repeat(row.rating) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
          </el-table>
        </div>
      </el-col>
    </el-row>

    <div class="card quick-entry">
      <div class="card-title">⚡ 快捷入口</div>
      <div class="quick-buttons">
        <el-button type="primary" class="quick-btn" @click="$router.push('/admin/canteens')">
          <span class="btn-icon">🏫</span>
          <span>食堂管理</span>
        </el-button>
        <el-button type="primary" class="quick-btn" @click="$router.push('/admin/dishes')">
          <span class="btn-icon">🍜</span>
          <span>菜品管理</span>
        </el-button>
        <el-button type="primary" class="quick-btn" @click="$router.push('/admin/reviews')">
          <span class="btn-icon">💬</span>
          <span>评价管理</span>
        </el-button>
        <el-button type="primary" class="quick-btn" @click="$router.push('/admin/users')">
          <span class="btn-icon">👤</span>
          <span>用户管理</span>
        </el-button>
        <el-button type="primary" class="quick-btn" @click="$router.push('/admin/announcements')">
          <span class="btn-icon">📢</span>
          <span>公告管理</span>
        </el-button>
      </div>
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

<style scoped>
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-dishes .stat-value {
  color: #667eea;
}

.stat-reviews .stat-value {
  color: #f56c6c;
}

.stat-users .stat-value {
  color: #67c23a;
}

.stat-rating .stat-value {
  color: #e6a23c;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.rating-num {
  margin-left: 5px;
  color: #999;
  font-size: 12px;
}

.quick-entry {
  margin-top: 20px;
}

.quick-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 30px;
  height: auto;
  min-width: 100px;
}

.btn-icon {
  font-size: 24px;
}
</style>
