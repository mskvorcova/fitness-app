import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/client'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)

  const isAuthenticated = computed(() => !!user.value)
  const userRole = computed(() => {
    const role = user.value?.role
    return role ? role.toUpperCase() : null
  })

  const login = async (username, password) => {
    try {
      const response = await authApi.login(username, password)
      user.value = response.user
      localStorage.setItem('user', JSON.stringify(response.user))
      return response.user
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      user.value = null
      localStorage.removeItem('user')
    }
  }

  const initAuth = () => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      user.value = JSON.parse(storedUser)
    }
  }

  return {
    user,
    isAuthenticated,
    userRole,
    login,
    logout,
    initAuth
  }
})
