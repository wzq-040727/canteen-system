<template>
  <div>
    <div class="card">
      <div style="display: flex; gap: 20px;">
        <img :src="getImageUrl(dish?.image)" style="width: 300px; height: 200px; object-fit: cover; border-radius: 12px;" @error="$event.target.src=defaultImage">
        <div style="flex: 1;">
          <h2 style="margin-bottom: 15px;">{{ dish?.name }}</h2>
          <div style="margin-bottom: 10px;">
            <span class="dish-price" style="font-size: 24px;">¥{{ dish?.price }}</span>
          </div>
          <div style="margin-bottom: 10px;">
            <span class="rating-stars" style="font-size: 20px;">{{ '★'.repeat(Math.round(dish?.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish?.avgRating || 0)) }}</span>
            <span style="color: #999; margin-left: 10px;">{{ dish?.avgRating?.toFixed(1) || '暂无评分' }} ({{ dish?.ratingCount || 0 }}人评价)</span>
          </div>
          <div class="dish-tags" style="margin-bottom: 15px;">
            <span class="tag" v-if="dish?.category">{{ dish.category }}</span>
            <span class="tag" v-if="dish?.taste">{{ dish.taste }}</span>
          </div>
          <p style="color: #666;">{{ dish?.description || '暂无描述' }}</p>
          <div style="margin-top: 15px;">
            <el-button type="primary" :icon="isFavorite ? 'StarFilled' : 'Star'" @click="toggleFavorite">
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button @click="showReviewDialog = true" :disabled="!userStore.isLoggedIn">写评价</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-title">用户评价</div>
      <div v-if="reviews.length">
        <div class="review-item" v-for="review in reviews" :key="review.id">
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
          <div class="review-images" v-if="review.images">
            <img v-for="(img, index) in JSON.parse(review.images)" :key="index" :src="img" @click="previewImage(img)">
          </div>
          <div style="margin-top: 10px;">
            <el-button text size="small" :type="review.isLiked ? 'primary' : 'default'" @click="toggleLike(review)">
              <el-icon><Pointer /></el-icon>
              {{ review.likeCount || 0 }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价，快来抢沙发吧！" />
    </div>

    <el-dialog v-model="showReviewDialog" title="写评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入评价内容" />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            action="/api/upload"
            :headers="{ Authorization: `Bearer ${userStore.token}` }"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :file-list="fileList"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import api, { IMAGE_BASE } from '../utils/api'
import { ElMessage } from 'element-plus'
import { Pointer, Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const dish = ref(null)
const reviews = ref([])
const isFavorite = ref(false)
const showReviewDialog = ref(false)
const submitting = ref(false)
const fileList = ref([])
const uploadedImages = ref([])
const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%23f0f0f0%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 text-anchor=%22middle%22 dy=%22.3em%22 fill=%22%23999%22 font-size=%2212%22%3E暂无图片%3C/text%3E%3C/svg%3E'

const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return IMAGE_BASE + path
}

const reviewForm = reactive({
  rating: 5,
  content: '',
  images: ''
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchDish = async () => {
  const res = await api.get(`/dishes/${route.params.id}`)
  dish.value = res.data
}

const fetchReviews = async () => {
  const res = await api.get(`/reviews/dish/${route.params.id}`)
  reviews.value = res.data || []
}

const checkFavorite = async () => {
  if (!userStore.isLoggedIn) return
  const res = await api.get(`/favorites/check/${route.params.id}`)
  isFavorite.value = res.data?.isFavorite || false
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isFavorite.value) {
      await api.delete(`/favorites/${route.params.id}`)
      isFavorite.value = false
      ElMessage.success('已取消收藏')
    } else {
      await api.post(`/favorites/${route.params.id}`)
      isFavorite.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    console.error(e)
  }
}

const toggleLike = async (review) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (review.isLiked) {
      await api.delete(`/reviews/${review.id}/like`)
      review.isLiked = false
      review.likeCount--
    } else {
      await api.post(`/reviews/${review.id}/like`)
      review.isLiked = true
      review.likeCount++
    }
  } catch (e) {
    console.error(e)
  }
}

const handleUploadSuccess = (response) => {
  if (response.data?.url) {
    uploadedImages.value.push(response.data.url)
  }
}

const submitReview = async () => {
  if (!reviewForm.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  submitting.value = true
  try {
    await api.post('/reviews', {
      dishId: route.params.id,
      rating: reviewForm.rating,
      content: reviewForm.content,
      images: uploadedImages.value.length ? JSON.stringify(uploadedImages.value) : ''
    })
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    reviewForm.rating = 5
    reviewForm.content = ''
    uploadedImages.value = []
    fileList.value = []
    fetchReviews()
    fetchDish()
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const previewImage = (url) => {
  window.open(url, '_blank')
}

onMounted(() => {
  fetchDish()
  fetchReviews()
  checkFavorite()
})
</script>
