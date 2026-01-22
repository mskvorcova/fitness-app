<template>
  <div>
    <h2 class="text-2xl font-bold text-white mb-6">Мой абонемент</h2>
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="alert alert-error mb-4">
      {{ error }}
    </div>
    <div v-else-if="membership" class="membership-card">
      <div class="membership-header">
        <div>
          <p class="membership-type">Тип абонемента</p>
          <h3 class="membership-name">{{ membership.membershipTypeName || membership.type || 'Не указан' }}</h3>
        </div>
        <CreditCard :size="48" style="opacity: 0.8;" />
      </div>
      <div class="membership-details">
        <div>
          <p class="membership-type">Действителен до</p>
          <p class="text-xl font-semibold">
            {{ formatDate(membership.endDate || membership.validUntil) }}
          </p>
        </div>
      </div>
    </div>
    <div v-else class="empty-state">
      У вас нет активного абонемента
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { CreditCard } from 'lucide-vue-next'
import { membershipsApi } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale/ru'

const authStore = useAuthStore()
const membership = ref(null)
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

const loadMembership = async () => {
  if (!authStore.user) return

  loading.value = true
  error.value = ''
  try {
    const { clientsApi } = await import('@/api/client')
    const client = await clientsApi.getByUserId(authStore.user.id)
    membership.value = await membershipsApi.getByClientId(client.id)
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка загрузки абонемента'
    console.error('Load membership error:', err)
    membership.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMembership()
})
</script>
