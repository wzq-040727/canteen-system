import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../utils/api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1 || user.value?.role === 2)

  const login = async (loginData) => {
    const res = await api.post('/auth/login', loginData)
    token.value = res.data.token
    user.value = res.data
    localStorage.setItem('token', res.data.token)
    return res
  }

  const register = async (registerData) => {
    const res = await api.post('/auth/register', registerData)
    return res
  }

  const logout = () => {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
  }

  const fetchUserInfo = async () => {
    if (token.value) {
      try {
        const res = await api.get('/auth/info')
        user.value = res.data
      } catch (e) {
        logout()
      }
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchUserInfo
  }
})
