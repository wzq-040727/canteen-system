<template>
  <div>
    <div class="card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <div class="card-title" style="margin-bottom: 0;">{{ canteen?.name }}</div>
        <el-button @click="$router.back()">返回</el-button>
      </div>
      <div style="color: #666; margin-bottom: 20px;">
        <p>📍 位置：{{ canteen?.location }}</p>
        <p>🕐 营业时间：{{ canteen?.openingHours }}</p>
        <p>{{ canteen?.description }}</p>
      </div>
    </div>

    <div class="card">
      <div class="card-title">窗口列表</div>
      <el-tabs v-model="activeWindow" @tab-change="handleWindowChange">
        <el-tab-pane v-for="window in windows" :key="window.id" :label="window.name" :name="window.id.toString()" />
      </el-tabs>
    </div>

    <div class="card">
      <div class="card-title">菜品列表</div>
      <div class="dish-grid" v-if="dishes.length">
        <div class="dish-card" v-for="dish in dishes" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
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
      <el-empty v-else description="该窗口暂无菜品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import api, { IMAGE_BASE } from '../utils/api'

const route = useRoute()
const canteen = ref(null)
const windows = ref([])
const dishes = ref([])
const activeWindow = ref('')
const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%23f0f0f0%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 text-anchor=%22middle%22 dy=%22.3em%22 fill=%22%23999%22 font-size=%2212%22%3E暂无图片%3C/text%3E%3C/svg%3E'

const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const fetchCanteen = async () => {
  const res = await api.get(`/canteens/${route.params.id}`)
  canteen.value = res.data
}

const fetchWindows = async () => {
  const res = await api.get(`/windows/canteen/${route.params.id}`)
  windows.value = res.data || []
  if (windows.value.length) {
    activeWindow.value = windows.value[0].id.toString()
    fetchDishes()
  }
}

const fetchDishes = async () => {
  if (!activeWindow.value) return
  const res = await api.get('/dishes', {
    params: {
      windowId: activeWindow.value,
      pageNum: 1,
      pageSize: 50
    }
  })
  dishes.value = res.data?.records || []
}

const handleWindowChange = () => {
  fetchDishes()
}

onMounted(() => {
  fetchCanteen()
  fetchWindows()
})
</script>
