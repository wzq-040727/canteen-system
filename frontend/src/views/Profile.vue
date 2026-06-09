<template>
  <div>
    <div class="card">
      <div class="card-title">个人中心</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 500px;">
        <el-form-item label="头像">
          <el-avatar :size="80" :src="form.avatar">{{ form.realName?.charAt(0) }}</el-avatar>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="选填" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
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
const formRef = ref()

const form = reactive({
  username: '',
  realName: '',
  studentId: '',
  phone: '',
  email: '',
  avatar: ''
})

const validateStudentId = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^\d{10,20}$/.test(value)) {
    callback(new Error('学号格式不正确，正确格式为10-20位纯数字'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  studentId: [{ validator: validateStudentId, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }]
}

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
  await formRef.value.validate()
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
