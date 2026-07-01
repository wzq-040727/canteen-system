<template>
  <div class="app-container">
    <header class="header">
      <h1 class="header-title" @click="$router.push('/')">🍽️ 校园食堂智能点评与推荐系统</h1>
      <div class="header-search" @click="$router.push('/search')">
        <el-input
          placeholder="搜索菜品、食堂..."
          prefix-icon="Search"
          size="small"
          style="width: 260px; border-radius: 20px;"
          readonly
        />
      </div>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="user-dropdown">
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
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { useUserStore } from './stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown, Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.header-search {
  color: white;
  cursor: pointer;
  font-size: 18px;
  transition: opacity 0.3s;
}

.header-search:hover {
  opacity: 0.8;
}

.header-title {
  cursor: pointer;
  transition: opacity 0.3s;
}

.header-title:hover {
  opacity: 0.8;
}

.user-dropdown {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: opacity 0.3s;
}

.user-dropdown:hover {
  opacity: 0.8;
}
</style>
