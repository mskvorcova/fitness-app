<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="login-icon">
          <Sparkle :size="32" />
        </div>
        <h1 class="text-3xl font-bold text-white mb-2">pulsar*</h1>
        <p class="text-zinc-400">Войдите в свой аккаунт</p>
      </div>

      <div class="login-form">
        <form @submit.prevent="handleSubmit" class="form-group">
          <div class="form-row">
            <label class="label">Имя пользователя</label>
            <input
              v-model="username"
              type="text"
              class="form-input"
              placeholder="username"
              required
            />
          </div>

          <div class="form-row">
            <label class="label">Пароль</label>
            <input
              v-model="password"
              type="password"
              class="form-input"
              placeholder="••••••••"
              required
            />
          </div>


          <div v-if="error" class="alert alert-error mb-4">
            {{ error }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="form-submit"
          >
            <LogIn :size="20" style="display: inline-block; margin-right: 0.5rem;" />
            {{ loading ? 'Вход...' : 'Войти' }}
          </button>
        </form>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Sparkle, LogIn } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/client'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const handleSubmit = async () => {
  error.value = ''
  loading.value = true

  try {
    await authStore.login(username.value, password.value)
    
    const user = authStore.user
    const role = user?.role?.toUpperCase()
    
    if (role === 'CLIENT') {
      router.push('/client')
    } else if (role === 'TRAINER') {
      router.push('/trainer')
    } else if (role === 'ADMIN') {
      router.push('/admin')
    } else {
      error.value = 'Неизвестная роль пользователя'
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка входа. Попробуйте снова.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.text-xs {
  font-size: 0.75rem;
}
</style>
