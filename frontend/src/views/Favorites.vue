<template>
  <div>
    <div class="card">
      <div class="card-title">我的收藏</div>
      <div class="dish-grid" v-if="favorites.length">
        <div class="dish-card" v-for="dish in favorites" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
          <img :src="getImageUrl(dish.image)" class="dish-image" @error="$event.target.src=defaultImage">
          <div class="dish-info">
            <div class="dish-name">{{ dish.name }}</div>
            <div class="dish-meta">
              <span class="dish-price">¥{{ dish.price }}</span>
              <div class="dish-rating">
                <span class="rating-stars">{{ '★'.repeat(Math.round(dish.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish.avgRating || 0)) }}</span>
              </div>
            </div>
            <div class="dish-tags">
              <span class="tag" v-if="dish.category">{{ dish.category }}</span>
              <span class="tag" v-if="dish.taste">{{ dish.taste }}</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无收藏" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api, { IMAGE_BASE } from '../utils/api'

const favorites = ref([])
const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%23f0f0f0%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 text-anchor=%22middle%22 dy=%22.3em%22 fill=%22%23999%22 font-size=%2212%22%3E暂无图片%3C/text%3E%3C/svg%3E'

const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const fetchFavorites = async () => {
  const res = await api.get('/favorites')
  favorites.value = res.data || []
}

onMounted(() => {
  fetchFavorites()
})
</script>
