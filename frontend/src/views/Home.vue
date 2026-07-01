<template>
  <div class="home-layout">
    <!-- 左侧锚点导航 -->
    <nav class="side-nav">
      <ul>
        <li v-if="userStore.isLoggedIn"
            :class="{ active: activeSection === 'personal-recommend' }"
            @click="scrollTo('personal-recommend')">🎯 个性化推荐</li>
        <li :class="{ active: activeSection === 'top-dishes' }"
            @click="scrollTo('top-dishes')">🔥 热门推荐</li>
        <li :class="{ active: activeSection === 'canteen-list' }"
            @click="scrollTo('canteen-list')">📍 食堂列表</li>
        <li :class="{ active: activeSection === 'dish-categories' }"
            @click="scrollTo('dish-categories')">🏷️ 菜品分类</li>
        <li :class="{ active: activeSection === 'announcements' }"
            @click="scrollTo('announcements')">📢 最新公告</li>
        <li :class="{ active: activeSection === 'latest-reviews' }"
            @click="scrollTo('latest-reviews')">💬 最新评价</li>
      </ul>
    </nav>

    <!-- 主内容区 -->
    <div class="home-content">

      <!-- 🎯 个性化推荐 -->
      <div id="personal-recommend" class="card" v-if="userStore.isLoggedIn">
        <div class="card-title">🎯 个性化推荐</div>
        <div class="dish-grid" v-if="personalLoading">
          <DishCardSkeleton v-for="i in 5" :key="i" />
        </div>
        <div class="dish-grid" v-else-if="personalDishes.length">
          <div class="dish-card" v-for="dish in personalDishes" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
            <div class="dish-card-img-wrapper">
              <img :src="getImageUrl(dish.image)" class="dish-image" loading="lazy" @error="$event.target.src=defaultImage">
              <div class="dish-card-fav" @click.stop="toggleFav(dish)">
                <el-icon :class="{ 'is-fav': dish.isFav }"><Star /></el-icon>
              </div>
            </div>
            <div class="dish-info">
              <div class="dish-name">{{ dish.name }}</div>
              <div class="dish-meta">
                <span class="dish-price">¥{{ dish.price }}</span>
                <div class="dish-rating">
                  <span class="rating-stars">{{ '★'.repeat(Math.round(dish.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish.avgRating || 0)) }}</span>
                  <span class="rating-text">{{ dish.avgRating?.toFixed(1) || '暂无评分' }}</span>
                </div>
              </div>
              <div class="dish-tags">
                <span class="tag canteen-tag" v-if="dish.canteenName">来自{{ dish.canteenName }}{{ dish.floor ? dish.floor + '楼' : '' }}{{ dish.windowName || '' }}</span>
                <span class="tag" v-if="dish.category">{{ dish.category }}</span>
                <span class="tag" v-if="dish.taste">{{ dish.taste }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无个性化推荐" />
      </div>

      <!-- 🔥 热门推荐 -->
      <div id="top-dishes" class="card">
        <div class="card-title">🔥 热门推荐</div>
        <div class="dish-grid" v-if="loading">
          <DishCardSkeleton v-for="i in 6" :key="i" />
        </div>
        <div class="dish-grid" v-else-if="topDishes.length">
          <div class="dish-card" v-for="dish in topDishes" :key="dish.id" @click="$router.push(`/dish/${dish.id}`)">
            <div class="dish-card-img-wrapper">
              <img :src="getImageUrl(dish.image)" class="dish-image" loading="lazy" @error="$event.target.src=defaultImage">
              <div class="dish-card-fav" @click.stop="toggleFav(dish)">
                <el-icon :class="{ 'is-fav': dish.isFav }"><Star /></el-icon>
              </div>
            </div>
            <div class="dish-info">
              <div class="dish-name">{{ dish.name }}</div>
              <div class="dish-meta">
                <span class="dish-price">¥{{ dish.price }}</span>
                <div class="dish-rating">
                  <span class="rating-stars">{{ '★'.repeat(Math.round(dish.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish.avgRating || 0)) }}</span>
                  <span class="rating-text">{{ dish.avgRating?.toFixed(1) || '暂无评分' }}</span>
                </div>
              </div>
              <div class="dish-tags">
                <span class="tag canteen-tag" v-if="dish.canteenName">来自{{ dish.canteenName }}{{ dish.floor ? dish.floor + '楼' : '' }}{{ dish.windowName || '' }}</span>
                <span class="tag" v-if="dish.category">{{ dish.category }}</span>
                <span class="tag" v-if="dish.taste">{{ dish.taste }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无推荐菜品" />
      </div>

      <!-- 📍 食堂列表 -->
      <div id="canteen-list" class="card">
        <div class="card-title">📍 食堂列表</div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="canteen in canteens" :key="canteen.id">
            <el-card shadow="hover" class="canteen-card" @click="$router.push(`/canteen/${canteen.id}`)">
              <template #header>
                <div class="canteen-header">
                  <span class="canteen-name">{{ canteen.name }}</span>
                  <el-tag :type="canteen.status === 1 ? 'success' : 'info'">
                    {{ canteen.status === 1 ? '营业中' : '休息中' }}
                  </el-tag>
                </div>
              </template>
              <div class="canteen-info">
                <p class="canteen-detail">📍 {{ canteen.location }}</p>
                <p class="canteen-detail">🕐 {{ canteen.openingHours }}</p>
                <p>{{ canteen.description }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 🏷️ 菜品分类 -->
      <div id="dish-categories" class="card" v-if="categories.length">
        <div class="card-title">🏷️ 菜品分类</div>
        <div class="category-cloud">
          <span class="category-tag" v-for="cat in categories" :key="cat.category"
                @click="$router.push(`/search?category=${cat.category}`)">
            {{ cat.category }}
            <span class="category-count">{{ cat.count }}</span>
          </span>
        </div>
      </div>

      <!-- 📢 最新公告 -->
      <div id="announcements" class="card" v-if="announcements.length">
        <div class="card-title">📢 最新公告</div>
        <div class="announcement-list">
          <div class="announcement-item" v-for="item in announcements" :key="item.id" :class="{ 'is-top': item.isTop }">
            <div class="announcement-header">
              <span class="announcement-title">
                <el-icon v-if="item.isTop" class="top-icon"><Top /></el-icon>
                {{ item.title }}
              </span>
              <span class="announcement-meta">
                <span v-if="item.canteenName" class="announcement-canteen">{{ item.canteenName }}</span>
                <span class="announcement-time">{{ formatTime(item.createdTime) }}</span>
              </span>
            </div>
            <div class="announcement-content">{{ item.content }}</div>
          </div>
        </div>
      </div>

      <!-- 💬 最新评价（多列） -->
      <div id="latest-reviews" class="card">
        <div class="card-title">💬 最新评价</div>
        <div v-if="recentReviews.length" class="reviews-grid">
          <div v-for="(floorGroups, canteenName) in groupedReviews" :key="canteenName" class="review-column">
            <div class="group-canteen">📍 {{ canteenName }}</div>
            <div v-for="(group, key) in floorGroups" :key="key" class="group-floor">
              <div class="group-floor-title">{{ group.floor }}楼 · {{ group.windowName }}</div>
              <div class="review-item" v-for="review in group.reviews" :key="review.id">
                <div class="review-header">
                  <div class="review-user">
                <div class="review-avatar" :style="{ background: getAvatarColor(review.userName) }">{{ (review.userName || '用户').charAt(0) }}</div>
                    <div>
                      <div class="review-username">{{ review.userName }}</div>
                      <div class="review-time">{{ formatTime(review.createdTime) }}</div>
                    </div>
                  </div>
                  <div class="rating-stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</div>
                </div>
                <div class="review-content">{{ review.content || '用户未填写评价内容' }}</div>
                <div class="review-dish">
                  评价菜品：<span class="dish-link" @click="$router.push(`/dish/${review.dishId}`)">{{ review.dishName }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无评价" />
      </div>

      <!-- 回到顶部 -->
      <el-backtop :right="40" :bottom="40" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../utils/api'
import { getImageUrl, formatTime, defaultImage } from '../utils/helpers'
import DishCardSkeleton from '../components/DishCardSkeleton.vue'
import { Top, Star } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const topDishes = ref([])
const personalDishes = ref([])
const canteens = ref([])
const recentReviews = ref([])
const categories = ref([])
const announcements = ref([])
const loading = ref(true)
const personalLoading = ref(true)
const activeSection = ref('')

// 头像随机颜色
const getAvatarColor = (name) => {
  const colors = ['#667eea', '#f56c6c', '#67c23a', '#e6a23c', '#909399', '#764ba2']
  let hash = 0
  for (let i = 0; i < (name || '').length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
}

// 收藏/取消收藏
const toggleFav = async (dish) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  try {
    await api.post('/favorites', { dishId: dish.id })
    dish.isFav = !dish.isFav
    ElMessage.success(dish.isFav ? '已收藏' : '已取消收藏')
  } catch (e) {
    console.error(e)
  }
}

const groupedReviews = computed(() => {
  const groups = {}
  for (const review of recentReviews.value) {
    const canteenName = review.canteenName || '未知食堂'
    if (!groups[canteenName]) {
      groups[canteenName] = {}
    }
    const floor = review.floor || 1
    const windowName = review.windowName || '未知窗口'
    const key = `${floor}-${windowName}`
    if (!groups[canteenName][key]) {
      groups[canteenName][key] = { floor, windowName, reviews: [] }
    }
    groups[canteenName][key].reviews.push(review)
  }
  return groups
})

// 锚点导航
const sectionIds = ['personal-recommend', 'top-dishes', 'canteen-list', 'dish-categories', 'announcements', 'latest-reviews']

const scrollTo = (id) => {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

let observer = null

const setupObserver = () => {
  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          activeSection.value = entry.target.id
        }
      }
    },
    { rootMargin: '-80px 0px -60% 0px', threshold: 0.1 }
  )
  for (const id of sectionIds) {
    const el = document.getElementById(id)
    if (el) observer.observe(el)
  }
}

onMounted(async () => {
  try {
    const [topRes, canteenRes, reviewRes, categoryRes, announceRes] = await Promise.all([
      api.get('/dishes/top?limit=12'),
      api.get('/canteens'),
      api.get('/reviews/recent-grouped?limit=20'),
      api.get('/dishes/categories').catch(() => ({ data: [] })),
      api.get('/announcements').catch(() => ({ data: [] }))
    ])
    topDishes.value = topRes.data || []
    canteens.value = canteenRes.data || []
    recentReviews.value = reviewRes.data || []
    categories.value = (categoryRes.data || []).filter(c => c.category)
    announcements.value = announceRes.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }

  // 个性化推荐（登录用户）
  if (userStore.isLoggedIn) {
    try {
      const res = await api.get('/dishes/recommend?limit=5')
      personalDishes.value = res.data || []
    } catch (e) {
      console.error(e)
    } finally {
      personalLoading.value = false
    }
  } else {
    personalLoading.value = false
  }

  // 等 DOM 更新后设置观察器
  await nextTick()
  setupObserver()
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.home-layout {
  display: flex;
  gap: 20px;
}

/* 左侧导航 */
.side-nav {
  width: 160px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  align-self: flex-start;
  max-height: calc(100vh - 100px);
}

.side-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
  border-left: 3px solid #e8e8e8;
}

.side-nav li {
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-left: 3px solid transparent;
  margin-left: -3px;
  transition: all 0.2s;
  white-space: nowrap;
}

.side-nav li:hover {
  color: #667eea;
  background: #f5f7ff;
}

.side-nav li.active {
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 600;
  background: #f0f2ff;
}

/* 主内容 */
.home-content {
  flex: 1;
  min-width: 0;
}

/* 菜品分类 */
.category-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f0f2f5;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.category-tag:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.category-count {
  background: rgba(0, 0, 0, 0.08);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.category-tag:hover .category-count {
  background: rgba(255, 255, 255, 0.2);
}

/* 公告 */
.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  padding: 12px 16px;
  border-radius: 8px;
  background: #fafafa;
  border-left: 3px solid #e0e0e0;
}

.announcement-item.is-top {
  border-left-color: #ff9800;
  background: #fff8e1;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.announcement-title {
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.top-icon {
  color: #ff9800;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.announcement-canteen {
  color: #667eea;
}

.announcement-content {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 评价多列 */
.reviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.review-column {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  max-height: 600px;
  overflow-y: auto;
}

.review-column .group-canteen {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.review-column .group-floor {
  margin-left: 0;
  margin-bottom: 12px;
}

/* 原有样式保留 */
.rating-text {
  color: #999;
  font-size: 12px;
}

.canteen-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.canteen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.canteen-name {
  font-weight: 600;
}

.canteen-info {
  color: #666;
  font-size: 14px;
}

.canteen-detail {
  margin-bottom: 8px;
}

.review-username {
  font-weight: 500;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-dish {
  margin-top: 8px;
  color: #999;
  font-size: 12px;
}

.dish-link {
  color: #667eea;
  cursor: pointer;
}

.dish-link:hover {
  text-decoration: underline;
}

.group-canteen {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.group-floor {
  margin-left: 16px;
  margin-bottom: 16px;
}

.group-floor-title {
  font-size: 14px;
  font-weight: 500;
  color: #667eea;
  margin-bottom: 8px;
}

/* 移动端隐藏导航 */
@media (max-width: 768px) {
  .side-nav {
    display: none;
  }

  .home-layout {
    display: block;
  }

  .dish-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .dish-image {
    height: 100px;
  }

  .reviews-grid {
    grid-template-columns: 1fr;
  }
}
</style>
