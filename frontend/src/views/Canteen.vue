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

    <div class="card" v-if="announcements.length">
      <div class="card-title">📢 食堂公告</div>
      <div v-for="ann in announcements" :key="ann.id" class="announcement-item">
        <div class="announcement-title">
          <el-tag v-if="ann.isTop" type="danger" size="small" style="margin-right: 8px;">置顶</el-tag>
          {{ ann.title }}
        </div>
        <div class="announcement-content" v-if="ann.content">{{ ann.content }}</div>
      </div>
    </div>

    <div class="card" v-if="floors.length">
      <div class="card-title">窗口列表</div>
      <div style="margin-bottom: 20px;">
        <el-radio-group v-model="activeFloor" @change="handleFloorChange">
          <el-radio-button v-for="f in floors" :key="f" :value="f">{{ f }}楼</el-radio-button>
        </el-radio-group>
      </div>

      <div v-for="win in floorWindows" :key="win.id" class="window-section">
        <div class="window-header">
          <span class="window-name">{{ win.name }}</span>
          <el-tag v-if="win.openTime && win.closeTime" :type="isWindowOpen(win) ? 'success' : 'info'" size="small">
            {{ isWindowOpen(win) ? '营业中' : '已暂停营业' }}
          </el-tag>
          <span v-if="win.openTime && win.closeTime && !isWindowOpen(win)" class="window-hours">
            营业时间为 {{ win.openTime }}-{{ win.closeTime }}
          </span>
          <span v-if="win.cuisineType" class="window-cuisine">{{ win.cuisineType }}</span>
        </div>
        <div class="dish-grid" v-if="windowDishes[win.id]?.length">
          <div class="dish-card" v-for="dish in windowDishes[win.id]" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
            <img :src="getImageUrl(dish.image)" class="dish-image" @error="$event.target.src=defaultImage">
            <div class="dish-info">
              <div class="dish-name">{{ dish.name }}</div>
              <div class="dish-meta">
                <span class="dish-price">¥{{ dish.price }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无菜品" :image-size="60" />
      </div>
    </div>

    <el-empty v-if="!loading && !floors.length" description="该食堂暂无窗口信息" />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import api, { IMAGE_BASE } from '../utils/api'

const route = useRoute()
const canteen = ref(null)
const announcements = ref([])
const floors = ref([])
const activeFloor = ref(1)
const floorWindows = ref([])
const windowDishes = reactive({})
const loading = ref(true)
const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%23f0f0f0%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 text-anchor=%22middle%22 dy=%22.3em%22 fill=%22%23999%22 font-size=%2212%22%3E暂无图片%3C/text%3E%3C/svg%3E'

const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const isWindowOpen = (win) => {
  if (!win.openTime || !win.closeTime) return true
  const now = new Date()
  const currentTime = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0')
  return currentTime >= win.openTime && currentTime <= win.closeTime
}

const fetchCanteen = async () => {
  const res = await api.get(`/canteens/${route.params.id}`)
  canteen.value = res.data
}

const fetchAnnouncements = async () => {
  try {
    const res = await api.get(`/announcements/canteen/${route.params.id}`)
    announcements.value = res.data || []
  } catch (e) {
    announcements.value = []
  }
}

const fetchFloors = async () => {
  const res = await api.get(`/windows/canteen/${route.params.id}/floors`)
  floors.value = res.data || []
  if (floors.value.length) {
    activeFloor.value = floors.value[0]
    fetchFloorWindows()
  }
}

const fetchFloorWindows = async () => {
  const res = await api.get(`/windows/canteen/${route.params.id}`, { params: { floor: activeFloor.value } })
  floorWindows.value = res.data || []
  for (const win of floorWindows.value) {
    fetchWindowDishes(win.id)
  }
}

const fetchWindowDishes = async (windowId) => {
  const res = await api.get('/dishes', {
    params: { windowId, status: 1, pageNum: 1, pageSize: 6 }
  })
  windowDishes[windowId] = res.data?.records || []
}

const handleFloorChange = () => {
  fetchFloorWindows()
}

onMounted(async () => {
  loading.value = true
  await Promise.all([fetchCanteen(), fetchAnnouncements(), fetchFloors()])
  loading.value = false
})
</script>

<style scoped>
.announcement-item {
  padding: 12px;
  margin-bottom: 8px;
  background: #fffbe6;
  border-radius: 6px;
  border-left: 3px solid #faad14;
}

.announcement-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.announcement-content {
  color: #666;
  font-size: 13px;
}

.window-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.window-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.window-name {
  font-size: 16px;
  font-weight: 600;
}

.window-hours {
  color: #999;
  font-size: 12px;
}

.window-cuisine {
  color: #667eea;
  font-size: 12px;
  background: #eef2ff;
  padding: 2px 8px;
  border-radius: 4px;
}
</style>
