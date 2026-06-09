<template>
  <div class="card">
    <div class="card-title">食堂管理</div>
    <el-table :data="canteens" v-loading="loading">
      <el-table-column prop="name" label="食堂" min-width="140" />
      <el-table-column prop="location" label="位置" min-width="120" />
      <el-table-column label="营业状态" width="140">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="!canEditStatus"
            @change="updateStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="营业时间" min-width="220">
        <template #default="{ row }">
          <el-input
            v-model="row.openingHours"
            :disabled="!canEditHours"
            placeholder="例如 07:00-21:00"
            @blur="updateHours(row)"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../utils/api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const canteens = ref([])
const loading = ref(false)
const canEditStatus = computed(() => userStore.user?.role === 2)
const canEditHours = computed(() => userStore.user?.role === 1 || userStore.user?.role === 2)

const loadCanteens = async () => {
  loading.value = true
  try {
    const res = await api.get('/canteens/admin/list')
    canteens.value = res.data || []
  } finally {
    loading.value = false
  }
}

const updateStatus = async (row) => {
  if (!canEditStatus.value) return
  await api.put('/canteens/admin/update', { id: row.id, status: row.status, openingHours: row.openingHours })
  ElMessage.success('营业状态已更新')
  await loadCanteens()
}

const updateHours = async (row) => {
  if (!canEditHours.value) return
  await api.put('/canteens/admin/update', { id: row.id, openingHours: row.openingHours })
  ElMessage.success('营业时间已更新')
  await loadCanteens()
}

onMounted(loadCanteens)
</script>
