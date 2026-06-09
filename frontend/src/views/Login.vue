<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>欢迎登录</h2>
        <p>校园食堂智能点评与推荐系统</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        <span class="link" @click="showResetDialog = true">忘记密码？</span>
        <span style="margin: 0 8px;">|</span>
        还没有账号？<router-link to="/register" class="link">立即注册</router-link>
      </div>
    </div>

    <el-dialog v-model="showResetDialog" title="重置密码" width="420px" @close="resetDialogState">
      <template v-if="!securityQuestion">
        <el-form :model="resetForm" ref="resetStep1Ref">
          <el-form-item prop="username" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
            <el-input v-model="resetForm.username" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
        </el-form>
        <el-button type="primary" style="width: 100%;" @click="querySecurityQuestion" :loading="resetLoading">查询安全问题</el-button>
      </template>
      <template v-else>
        <div style="margin-bottom: 16px;">
          <div style="color: #666; margin-bottom: 8px;">安全问题：</div>
          <div style="font-weight: 600; padding: 10px; background: #f5f7fa; border-radius: 4px;">{{ securityQuestion }}</div>
        </div>
        <el-form :model="resetForm" :rules="resetRules" ref="resetStep2Ref">
          <el-form-item prop="securityAnswer">
            <el-input v-model="resetForm.securityAnswer" placeholder="请输入安全答案" prefix-icon="Edit" />
          </el-form-item>
          <el-form-item prop="newPassword">
            <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="resetForm.confirmPassword" type="password" placeholder="请确认新密码" prefix-icon="Lock" show-password />
          </el-form-item>
        </el-form>
        <el-button type="primary" style="width: 100%;" @click="submitReset" :loading="resetLoading">重置密码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import api from '../utils/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const showResetDialog = ref(false)
const securityQuestion = ref('')
const resetLoading = ref(false)
const resetStep1Ref = ref()
const resetStep2Ref = ref()

const resetForm = reactive({
  username: '',
  securityAnswer: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPass = (rule, value, callback) => {
  if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const resetRules = {
  securityAnswer: [{ required: true, message: '请输入安全答案', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPass, trigger: 'blur' }
  ]
}

const querySecurityQuestion = async () => {
  await resetStep1Ref.value.validate()
  resetLoading.value = true
  try {
    const res = await api.get('/auth/security-question', { params: { username: resetForm.username } })
    securityQuestion.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    resetLoading.value = false
  }
}

const submitReset = async () => {
  await resetStep2Ref.value.validate()
  resetLoading.value = true
  try {
    await api.post('/auth/reset-password', {
      username: resetForm.username,
      securityAnswer: resetForm.securityAnswer,
      newPassword: resetForm.newPassword
    })
    ElMessage.success('密码重置成功，请重新登录')
    showResetDialog.value = false
  } catch (e) {
    console.error(e)
  } finally {
    resetLoading.value = false
  }
}

const resetDialogState = () => {
  securityQuestion.value = ''
  resetForm.username = ''
  resetForm.securityAnswer = ''
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
}
</script>

<style scoped>
.login-btn {
  width: 100%;
}

.register-link {
  text-align: center;
  color: #999;
}

.link {
  color: #667eea;
  cursor: pointer;
}

.link:hover {
  text-decoration: underline;
}
</style>
