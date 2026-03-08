<template>
  <div>
    <div class="card">
      <div class="card-title">🔥 热门推荐</div>
      <div class="dish-grid" v-if="topDishes.length">
        <div class="dish-card" v-for="dish in topDishes" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
          <img :src="getImageUrl(dish.image)" class="dish-image" @error="$event.target.src=defaultImage">
          <div class="dish-info">
            <div class="dish-name">{{ dish.name }}</div>
            <div class="dish-meta">
              <span class="dish-price">¥{{ dish.price }}</span>
              <div class="dish-rating">
                <span class="rating-stars">{{ '★'.repeat(Math.round(dish.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish.avgRating || 0)) }}</span>
                <span style="color: #999; font-size: 12px;">{{ dish.avgRating?.toFixed(1) || '暂无评分' }}</span>
              </div>
            </div>
            <div class="dish-tags">
              <span class="tag" v-if="dish.category">{{ dish.category }}</span>
              <span class="tag" v-if="dish.taste">{{ dish.taste }}</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐菜品" />
    </div>

    <div class="card">
      <div class="card-title">📍 食堂列表</div>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="canteen in canteens" :key="canteen.id">
          <el-card shadow="hover" style="margin-bottom: 20px; cursor: pointer;" @click="$router.push(`/canteen/${canteen.id}`)">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-weight: 600;">{{ canteen.name }}</span>
                <el-tag :type="canteen.status === 1 ? 'success' : 'info'">
                  {{ canteen.status === 1 ? '营业中' : '休息中' }}
                </el-tag>
              </div>
            </template>
            <div style="color: #666; font-size: 14px;">
              <p style="margin-bottom: 8px;">📍 {{ canteen.location }}</p>
              <p style="margin-bottom: 8px;">🕐 {{ canteen.openingHours }}</p>
              <p>{{ canteen.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="card">
      <div class="card-title">💬 最新评价</div>
      <div v-if="recentReviews.length">
        <div class="review-item" v-for="review in recentReviews" :key="review.id">
          <div class="review-header">
            <div class="review-user">
              <div class="review-avatar">{{ (review.userName || '用户').charAt(0) }}</div>
              <div>
                <div style="font-weight: 500;">{{ review.userName }}</div>
                <div style="color: #999; font-size: 12px;">{{ formatTime(review.createdTime) }}</div>
              </div>
            </div>
            <div class="rating-stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</div>
          </div>
          <div class="review-content">{{ review.content || '用户未填写评价内容' }}</div>
          <div style="margin-top: 8px; color: #999; font-size: 12px;">
            评价菜品：<span style="color: #667eea; cursor: pointer;" @click="$router.push(`/dish/${review.dishId}`)">{{ review.dishName }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/api'
import { IMAGE_BASE } from '../utils/api'

const topDishes = ref([])
const canteens = ref([])
const recentReviews = ref([])
const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%23f0f0f0%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 text-anchor=%22middle%22 dy=%22.3em%22 fill=%22%23999%22 font-size=%2212%22%3E暂无图片%3C/text%3E%3C/svg%3E'

const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(async () => {
  try {
    const [topRes, canteenRes, reviewRes] = await Promise.all([
      api.get('/dishes/top?limit=6'),
      api.get('/canteens'),
      api.get('/reviews/recent?limit=5')
    ])
    topDishes.value = topRes.data || []
    canteens.value = canteenRes.data || []
    recentReviews.value = reviewRes.data || []
  } catch (e) {
    console.error(e)
  }
})
</script>
