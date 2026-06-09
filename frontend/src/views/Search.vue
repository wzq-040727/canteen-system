<template>
  <div>
    <div class="card">
      <div class="card-title">搜索菜品</div>
      <div class="search-bar">
        <el-input v-model="searchParams.name" placeholder="菜品名称" clearable @keyup.enter="search" />
        <el-select v-model="searchParams.canteenId" placeholder="选择食堂" clearable>
          <el-option v-for="c in canteens" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="searchParams.category" placeholder="选择分类" clearable>
          <el-option label="热菜" value="热菜" />
          <el-option label="面食" value="面食" />
          <el-option label="套餐" value="套餐" />
          <el-option label="小吃" value="小吃" />
          <el-option label="特色" value="特色" />
        </el-select>
        <el-input v-model="searchParams.taste" placeholder="口味" clearable />
        <el-button type="primary" @click="search">搜索</el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-title">搜索结果 (共 {{ total }} 条)</div>
      <div class="dish-grid" v-if="loading">
        <DishCardSkeleton v-for="i in 6" :key="i" />
      </div>
      <div class="dish-grid" v-else-if="dishes.length">
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
      <el-empty v-else description="暂无搜索结果" />
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/api'
import { getImageUrl, defaultImage } from '../utils/helpers'
import DishCardSkeleton from '../components/DishCardSkeleton.vue'

const route = useRoute()
const router = useRouter()
const dishes = ref([])
const canteens = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const loading = ref(false)

const searchParams = reactive({
  name: '',
  canteenId: '',
  category: '',
  taste: ''
})

const fetchCanteens = async () => {
  const res = await api.get('/canteens')
  canteens.value = res.data || []
}

const search = async () => {
  loading.value = true
  try {
    const res = await api.get('/dishes', {
      params: {
        ...searchParams,
        status: 1,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    dishes.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  search()
}

onMounted(() => {
  if (route.query.name) {
    searchParams.name = route.query.name
  }
  if (route.query.category) {
    searchParams.category = route.query.category
  }
  fetchCanteens()
  search()
})
</script>
