<template>
  <div>
    <h2 class="text-2xl font-bold text-white mb-6">История посещений</h2>
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="alert alert-error mb-4">
      {{ error }}
    </div>
    <div v-else>
      <div
        v-for="visit in visits"
        :key="visit.id"
        class="visit-card"
      >
        <div>
          <h3 class="text-lg font-semibold text-white mb-1">{{ visit.workoutName || 'Тренировка' }}</h3>
          <p class="text-muted text-sm">
            {{ formatDate(visit.workoutDate) }}
          </p>
          <p v-if="visit.trainerName" class="text-muted text-xs">
            Тренер: {{ visit.trainerName }}
          </p>
        </div>
        <div v-if="visit.status === 'ATTENDED'" class="visit-status attended">
          <CheckCircle :size="20" />
          <span>Посещено</span>
        </div>
        <div v-else-if="visit.status === 'NOT_ATTENDED'" class="visit-status missed">
          <XCircle :size="20" />
          <span>Пропущено</span>
        </div>
        <div v-else-if="visit.status === 'CANCELLED'" class="visit-status missed">
          <XCircle :size="20" />
          <span>Отменено</span>
        </div>
      </div>
      <div v-if="visits.length === 0" class="empty-state">
        Нет истории посещений
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { CheckCircle, XCircle } from 'lucide-vue-next'
import { visitsApi } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale/ru'

const authStore = useAuthStore()
const visits = ref([])
const loading = ref(false)
const error = ref('')

const formatDate = (date) => {
  if (!date) return ''
  try {
    const dateObj = new Date(date)
    if (isNaN(dateObj.getTime())) {
      return date
    }
    return format(dateObj, 'd MMMM yyyy', { locale: ru })
  } catch (error) {
    console.error('Date formatting error:', error, date)
    return date || ''
  }
}

const loadVisits = async () => {
  if (!authStore.user) return

  loading.value = true
  error.value = ''
  try {
    const { clientsApi } = await import('@/api/client')
    const client = await clientsApi.getByUserId(authStore.user.id)
    visits.value = await visitsApi.getByClientId(client.id)
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка загрузки посещений'
    console.error('Load visits error:', err)
    visits.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadVisits()
})
</script>
