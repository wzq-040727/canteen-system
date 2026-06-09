<template>
  <div>
    <div class="card">
      <div class="dish-detail">
        <img :src="getImageUrl(dish?.image)" class="dish-detail-image" @error="$event.target.src=defaultImage">
        <div class="dish-detail-info">
          <h2 class="dish-detail-name">{{ dish?.name }}</h2>
          <div class="dish-detail-price">
            <span class="dish-price price-large">¥{{ dish?.price }}</span>
          </div>
          <div class="dish-detail-rating">
            <span class="rating-stars rating-large">{{ '★'.repeat(Math.round(dish?.avgRating || 0)) }}{{ '☆'.repeat(5 - Math.round(dish?.avgRating || 0)) }}</span>
            <span class="rating-count">{{ dish?.avgRating?.toFixed(1) || '暂无评分' }} ({{ dish?.ratingCount || 0 }}人评价)</span>
          </div>
          <div class="dish-tags dish-detail-tags">
            <span class="tag" v-if="dish?.category">{{ dish.category }}</span>
            <span class="tag" v-if="dish?.taste">{{ dish.taste }}</span>
          </div>
          <p class="dish-description">{{ dish?.description || '暂无描述' }}</p>
          <div class="dish-actions">
            <el-button type="primary" :icon="isFavorite ? 'StarFilled' : 'Star'" @click="toggleFavorite">
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button @click="showReviewDialog = true" :disabled="!userStore.isLoggedIn">写评价</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-title">智能点评</div>
      <div v-if="smartReview?.summary" class="smart-review-content">
        {{ smartReview.summary }}
      </div>
      <el-empty v-else description="智能点评生成中..." />
    </div>

    <div class="card">
      <div class="card-title">用户评价</div>
      <div v-if="reviews.length">
        <div class="review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <div class="review-user">
              <div class="review-avatar">{{ (review.userName || '用户').charAt(0) }}</div>
              <div>
                <div class="review-username">{{ review.userName }}</div>
                <div class="review-time">{{ formatTime(review.createdTime) }}</div>
              </div>
            </div>
            <div class="rating-stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</div>
          </div>
          <div class="review-content">{{ review.content || '用户未填写评价内容' }}</div>
          <div class="review-images" v-if="review.images">
            <img v-for="(img, index) in JSON.parse(review.images)" :key="index" :src="img" @click="previewImage(img)">
          </div>
          <div class="review-like">
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
import api from '../utils/api'
import { getImageUrl, formatTime, defaultImage } from '../utils/helpers'
import { ElMessage } from 'element-plus'
import { Pointer, Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const dish = ref(null)
const reviews = ref([])
const smartReview = ref(null)
const isFavorite = ref(false)
const showReviewDialog = ref(false)
const submitting = ref(false)
const fileList = ref([])
const uploadedImages = ref([])

const reviewForm = reactive({
  rating: 5,
  content: '',
  images: ''
})

const fetchDish = async () => {
  const res = await api.get(`/dishes/${route.params.id}`)
  dish.value = res.data
}

const fetchReviews = async () => {
  const res = await api.get(`/reviews/dish/${route.params.id}`)
  reviews.value = res.data || []
}

const fetchSmartReview = async () => {
  const res = await api.get(`/dishes/${route.params.id}/smart-review`)
  smartReview.value = res.data
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
    fetchSmartReview()
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
  fetchSmartReview()
  checkFavorite()
})
</script>

<style scoped>
.dish-detail {
  display: flex;
  gap: 20px;
}

.dish-detail-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 12px;
}

.dish-detail-info {
  flex: 1;
}

.dish-detail-name {
  margin-bottom: 15px;
}

.dish-detail-price {
  margin-bottom: 10px;
}

.price-large {
  font-size: 24px;
}

.dish-detail-rating {
  margin-bottom: 10px;
}

.rating-large {
  font-size: 20px;
}

.rating-count {
  color: #999;
  margin-left: 10px;
}

.dish-detail-tags {
  margin-bottom: 15px;
}

.dish-description {
  color: #666;
}

.dish-actions {
  margin-top: 15px;
}

.smart-review-content {
  line-height: 1.8;
  color: #444;
}

.review-username {
  font-weight: 500;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-like {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .dish-detail {
    flex-direction: column;
  }

  .dish-detail-image {
    width: 100%;
    height: 200px;
  }
}
</style>
