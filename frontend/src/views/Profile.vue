<template>
  <div>
    <div class="card">
      <div class="card-title">个人中心</div>
      <el-form :model="form" label-width="100px" style="max-width: 500px;">
        <el-form-item label="头像">
          <el-avatar :size="80" :src="form.avatar">{{ form.realName?.charAt(0) }}</el-avatar>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="form.studentId" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateInfo">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card">
      <div class="card-title">修改密码</div>
      <el-form :model="passwordForm" label-width="100px" style="max-width: 500px;">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updatePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import api from '../utils/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const form = reactive({
  username: '',
  realName: '',
  studentId: '',
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchUserInfo = async () => {
  const res = await api.get('/auth/info')
  Object.assign(form, res.data)
}

const updateInfo = async () => {
  await api.put('/auth/info', form)
  ElMessage.success('保存成功')
  userStore.fetchUserInfo()
}

const updatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  await api.put('/auth/password', null, {
    params: {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    }
  })
  ElMessage.success('密码修改成功')
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

onMounted(() => {
  fetchUserInfo()
})
</script>
