<template>
  <div>
    <h2 class="text-2xl font-bold text-white mb-6">Мои записи</h2>
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="alert alert-error mb-4">
      {{ error }}
    </div>
    <div v-else>
      <div
        v-for="booking in activeBookings"
        :key="booking.id"
        class="booking-card"
      >
        <div class="booking-info">
          <h3 class="text-lg font-semibold text-white mb-1">
            {{ booking.workoutName || 'Тренировка' }}
          </h3>
          <div class="booking-details">
            <div class="booking-detail">
              <Calendar :size="16" />
              <span>{{ formatDate(booking.workoutDate) }}</span>
            </div>
            <div class="booking-detail">
              <Clock :size="16" />
              <span>{{ formatTime(booking.workoutDate) }}</span>
            </div>
            <div v-if="booking.trainerName" class="booking-detail">
              <Users :size="16" />
              <span>Тренер: {{ booking.trainerName }}</span>
            </div>
          </div>
        </div>
        <button
          @click="cancelBooking(booking.id)"
          class="cancel-btn"
        >
          Отменить запись
        </button>
      </div>
      <div v-if="activeBookings.length === 0" class="empty-state">
        У вас нет активных записей
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, Clock, Users } from 'lucide-vue-next'
import { bookingsApi, clientsApi } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale/ru'

const authStore = useAuthStore()
const bookings = ref([])
const loading = ref(false)
const error = ref('')

const activeBookings = computed(() => {
  return bookings.value
    .filter(b => b.status === 'BOOKED')
    .sort((a, b) => {
      const dateA = new Date(a.workoutDate)
      const dateB = new Date(b.workoutDate)
      return dateA - dateB
    })
})

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

const formatTime = (date) => {
  if (!date) return ''
  try {
    const dateObj = new Date(date)
    if (isNaN(dateObj.getTime())) {
      return date
    }
    return format(dateObj, 'HH:mm')
  } catch (error) {
    console.error('Time formatting error:', error, date)
    return date || ''
  }
}

const loadBookings = async () => {
  if (!authStore.user) return

  loading.value = true
  error.value = ''
  try {
    const client = await clientsApi.getByUserId(authStore.user.id)
    bookings.value = await bookingsApi.getByClientId(client.id)
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка загрузки записей'
    console.error('Load bookings error:', err)
    bookings.value = []
  } finally {
    loading.value = false
  }
}

const cancelBooking = async (bookingId) => {
  if (!confirm('Вы уверены, что хотите отменить эту запись?')) return
  
  try {
    await bookingsApi.cancel(bookingId)
    await loadBookings()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка отмены записи'
    console.error('Cancel booking error:', err)
    alert(error.value)
  }
}

onMounted(() => {
  loadBookings()
})
</script>

<style scoped>
.booking-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.75rem;
  padding: 1.5rem;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s;
}

.booking-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
}

.booking-info {
  flex: 1;
}

.booking-details {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 0.75rem;
}

.booking-detail {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.875rem;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.5);
  border-radius: 0.5rem;
  color: rgb(248, 113, 113);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: rgba(239, 68, 68, 0.3);
  border-color: rgba(239, 68, 68, 0.7);
}

.booking-status {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.booking-status.cancelled {
  background: rgba(107, 114, 128, 0.2);
  color: rgb(156, 163, 175);
  border: 1px solid rgba(107, 114, 128, 0.5);
}
</style>
