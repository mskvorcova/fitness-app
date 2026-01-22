<template>
  <div class="min-h-screen bg-zinc-950">
    <header class="page-header">
      <div class="page-header-content">
        <div class="header-title">
          <h1 class="header-user-name">Профиль</h1>
        </div>
        <button class="logout-btn" @click="handleBack">
          <ArrowLeft :size="20" />
          <span class="hidden sm:inline">Назад</span>
        </button>
      </div>
    </header>

    <main class="page-main">
      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="alert alert-error mb-4">
        {{ error }}
      </div>
      <form v-else @submit.prevent="handleSave" class="card" style="max-width: 32rem; margin: 0 auto;">
        <h2 class="text-2xl font-bold text-white mb-6">Редактирование профиля</h2>

        <div class="form-row">
          <label class="label">Имя</label>
          <input
            v-model="formData.name"
            type="text"
            class="form-input"
            required
          />
        </div>

        <div class="form-row">
          <label class="label">Email</label>
          <input
            v-model="formData.email"
            type="email"
            class="form-input"
            required
          />
        </div>

        <div v-if="isClient" class="form-row">
          <label class="label">Телефон</label>
          <input
            v-model="formData.phone"
            type="tel"
            class="form-input"
            required
          />
        </div>

        <div v-if="isTrainer" class="form-row">
          <label class="label">Описание</label>
          <textarea
            v-model="formData.description"
            class="form-input"
            rows="3"
            placeholder="Описание тренера"
          />
        </div>

        <div v-if="isTrainer" class="form-row">
          <label class="label">Стаж (лет)</label>
          <input
            v-model.number="formData.experienceYears"
            type="number"
            class="form-input"
            min="0"
          />
        </div>

        <div class="form-row" style="margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid var(--border);">
          <label class="label">Новый пароль (оставьте пустым, если не хотите менять)</label>
          <input
            v-model="formData.newPassword"
            type="password"
            class="form-input"
            placeholder="Введите новый пароль"
          />
        </div>

        <div v-if="formData.newPassword" class="form-row">
          <label class="label">Подтвердите новый пароль</label>
          <input
            v-model="formData.confirmPassword"
            type="password"
            class="form-input"
            placeholder="Повторите новый пароль"
          />
        </div>

        <div style="display: flex; gap: 0.75rem; margin-top: 1.5rem;">
          <button
            type="button"
            @click="handleBack"
            class="btn btn-secondary"
            style="flex: 1;"
          >
            Отмена
          </button>
          <button
            type="submit"
            :disabled="saving"
            class="btn btn-primary"
            style="flex: 1;"
          >
            {{ saving ? 'Сохранение...' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { usersApi, clientsApi, trainersApi } from '@/api/client'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(true)
const saving = ref(false)
const error = ref('')

const formData = ref({
  name: '',
  email: '',
  phone: '',
  description: '',
  experienceYears: null,
  newPassword: '',
  confirmPassword: ''
})

const isClient = computed(() => authStore.userRole === 'client' || authStore.userRole === 'CLIENT')
const isTrainer = computed(() => authStore.userRole === 'trainer' || authStore.userRole === 'TRAINER')

const loadProfile = async () => {
  loading.value = true
  error.value = ''
  try {
    const user = await usersApi.getCurrentUser()
    formData.value.name = user.name
    formData.value.email = user.email

    if (isClient.value && user.id) {
      try {
        const client = await clientsApi.getByUserId(user.id)
        formData.value.phone = client.phone || ''
      } catch (err) {
        console.warn('Client data not found:', err)
      }
    }

    if (isTrainer.value && user.id) {
      try {
        const trainer = await trainersApi.getByUserId(user.id)
        formData.value.description = trainer.description || ''
        formData.value.experienceYears = trainer.experienceYears || null
      } catch (err) {
        console.warn('Trainer data not found:', err)
      }
    }
  } catch (err) {
    error.value = err.message || 'Ошибка загрузки профиля'
    console.error('Load profile error:', err)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (formData.value.newPassword && formData.value.newPassword !== formData.value.confirmPassword) {
    error.value = 'Пароли не совпадают'
    return
  }

  saving.value = true
  error.value = ''
  try {
    await usersApi.updateCurrentUser({
      name: formData.value.name,
      email: formData.value.email,
      newPassword: formData.value.newPassword || null
    })

    if (isClient.value && authStore.user?.id) {
      await clientsApi.updateByUserId(authStore.user.id, {
        phone: formData.value.phone
      })
    }

    if (isTrainer.value && authStore.user?.id) {
      await trainersApi.updateByUserId(authStore.user.id, {
        description: formData.value.description,
        experienceYears: formData.value.experienceYears
      })
    }

    await authStore.initAuth()
    handleBack()
  } catch (err) {
    error.value = err.message || 'Ошибка сохранения профиля'
    console.error('Save profile error:', err)
  } finally {
    saving.value = false
  }
}

const handleBack = () => {
  if (isClient.value) {
    router.push('/client')
  } else if (isTrainer.value) {
    router.push('/trainer')
  } else {
    router.push('/admin')
  }
}

onMounted(() => {
  loadProfile()
})
</script>
