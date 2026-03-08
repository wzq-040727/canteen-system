<template>
  <div>
    <div class="card">
      <div class="card-title">我的评价</div>
      <div v-if="reviews.length">
        <div class="review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <div class="review-user">
              <div>
                <div style="font-weight: 500;">{{ review.dishName }}</div>
                <div style="color: #999; font-size: 12px;">{{ formatTime(review.createdTime) }}</div>
              </div>
            </div>
            <div class="rating-stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</div>
          </div>
          <div class="review-content">{{ review.content || '未填写评价内容' }}</div>
          <div class="review-images" v-if="review.images">
            <img v-for="(img, index) in JSON.parse(review.images)" :key="index" :src="img">
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/api'

const reviews = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(async () => {
  try {
    const res = await api.get('/reviews/my')
    reviews.value = res.data || []
  } catch (e) {
    reviews.value = []
  }
})
</script>
