<template>
  <div class="app-container">
    <header class="header">
      <h1 @click="$router.push('/')" style="cursor: pointer;">🍽️ 校园食堂智能点评与推荐系统</h1>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span style="color: white; cursor: pointer; display: flex; align-items: center; gap: 5px;">
              <el-avatar :size="32" :src="userStore.user?.avatar">{{ userStore.user?.realName?.charAt(0) || userStore.user?.username?.charAt(0) }}</el-avatar>
              {{ userStore.user?.realName || userStore.user?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/favorites')">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/my-reviews')">我的评价</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" divided @click="$router.push('/admin')">管理后台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button text @click="$router.push('/login')">登录</el-button>
          <el-button text @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useUserStore } from './stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>
