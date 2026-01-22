<template>
  <div>
    <h2 class="text-2xl font-bold text-white mb-6">Доступные тренировки</h2>
    
    <!-- Tabs for 7 days -->
    <div class="schedule-tabs">
      <button
        v-for="(day, index) in days"
        :key="index"
        @click="selectedDayIndex = index"
        :class="['schedule-tab', { active: selectedDayIndex === index }]"
      >
        <div class="tab-day-name">{{ getDayName(day.date) }}</div>
        <div class="tab-day-date">{{ formatDateShort(day.date) }}</div>
      </button>
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="alert alert-error mb-4">
      {{ error }}
    </div>
    <div v-else>
      <div
        v-for="workout in selectedDayWorkouts"
        :key="workout.id"
        class="workout-card"
      >
        <div class="workout-header">
          <div class="workout-info">
            <h3 class="workout-title">{{ workout.workoutName }}</h3>
            <div class="workout-details">
              <div class="workout-detail">
                <Users :size="16" />
                <span>{{ workout.trainerName || workout.trainer }}</span>
              </div>
              <div class="workout-detail">
                <Clock :size="16" />
                <span>{{ workout.time }}</span>
              </div>
              <div class="workout-detail">
                Записано: {{ workout.enrolled }}/{{ workout.capacity }}
              </div>
              <div class="workout-detail">
                Зал: {{ workout.hall }}
              </div>
            </div>
          </div>
          <button
            @click="handleBooking(workout)"
            :disabled="workout.enrolled >= workout.capacity && !workout.isBooked"
            :class="[
              'book-btn',
              workout.isBooked ? 'secondary' : workout.enrolled >= workout.capacity ? 'disabled' : 'primary'
            ]"
          >
            {{ workout.isBooked ? 'Отменить запись' : workout.enrolled >= workout.capacity ? 'Мест нет' : 'Записаться' }}
          </button>
        </div>
      </div>
      <div v-if="selectedDayWorkouts.length === 0" class="empty-state">
        Нет доступных тренировок на этот день
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, Clock, Users } from 'lucide-vue-next'
import { scheduleApi, bookingsApi, clientsApi } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { format, addDays, startOfDay } from 'date-fns'
import { ru } from 'date-fns/locale/ru'

const authStore = useAuthStore()

const emit = defineEmits(['book', 'cancel'])

const workouts = ref([])
const loading = ref(false)
const error = ref('')
const selectedDayIndex = ref(0)

const days = computed(() => {
  const today = startOfDay(new Date())
  return Array.from({ length: 7 }, (_, i) => ({
    date: addDays(today, i)
  }))
})

const selectedDayWorkouts = computed(() => {
  if (!workouts.value || workouts.value.length === 0) return []
  
  const selectedDate = days.value[selectedDayIndex.value].date
  const selectedDateStr = format(selectedDate, 'yyyy-MM-dd')
  
  return workouts.value
    .filter(workout => {
      if (!workout.date) return false
      try {
        const workoutDate = new Date(workout.date)
        const workoutDateStr = format(workoutDate, 'yyyy-MM-dd')
        return workoutDateStr === selectedDateStr
      } catch {
        return false
      }
    })
    .map(workout => ({
      ...workout,
      isBooked: workout.isBooked || false
    }))
    .sort((a, b) => {
      return (a.time || '').localeCompare(b.time || '')
    })
})

const formatDateShort = (date) => {
  if (!date) return ''
  try {
    const dateObj = new Date(date)
    if (isNaN(dateObj.getTime())) {
      return date
    }
    return format(dateObj, 'd MMM', { locale: ru })
  } catch (error) {
    console.error('Date formatting error:', error, date)
    return date || ''
  }
}

const getDayName = (date) => {
  if (!date) return ''
  try {
    const dateObj = new Date(date)
    if (isNaN(dateObj.getTime())) {
      return ''
    }
    const today = startOfDay(new Date())
    const tomorrow = addDays(today, 1)
    const dateStr = format(dateObj, 'yyyy-MM-dd')
    const todayStr = format(today, 'yyyy-MM-dd')
    const tomorrowStr = format(tomorrow, 'yyyy-MM-dd')
    
    if (dateStr === todayStr) {
      return 'Сегодня'
    } else if (dateStr === tomorrowStr) {
      return 'Завтра'
    } else {
      return format(dateObj, 'EEEE', { locale: ru })
    }
  } catch (error) {
    console.error('Day name formatting error:', error, date)
    return ''
  }
}

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

const loadSchedule = async () => {
  loading.value = true
  error.value = ''
  try {
    workouts.value = await scheduleApi.getSchedule(7)
    if (authStore.user) {
      const client = await clientsApi.getByUserId(authStore.user.id)
      const bookings = await bookingsApi.getByClientId(client.id)
      const bookedWorkouts = new Set(bookings
        .filter(b => b.status === 'BOOKED' || b.status === 'ATTENDED')
        .map(b => `${b.scheduleWorkoutId}-${new Date(b.workoutDate).toISOString().split('T')[0]}`))
      
      workouts.value = workouts.value.map(w => {
        const workoutKey = `${w.scheduleWorkoutId || w.id}-${w.date}`
        return {
          ...w,
          isBooked: bookedWorkouts.has(workoutKey)
        }
      })
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка загрузки расписания'
    console.error('Load schedule error:', err)
  } finally {
    loading.value = false
  }
}

const handleBooking = async (workout) => {
  try {
    if (workout.isBooked) {
      if (authStore.user) {
        const clientResponse = await clientsApi.getByUserId(authStore.user.id)
        const bookings = await bookingsApi.getByClientId(clientResponse.id)
        const selectedDate = days.value[selectedDayIndex.value].date
        const selectedDateStr = format(selectedDate, 'yyyy-MM-dd')
        const booking = bookings.find(b => {
          const bookingDateStr = new Date(b.workoutDate).toISOString().split('T')[0]
          return b.scheduleWorkoutId === workout.scheduleWorkoutId && 
                 bookingDateStr === selectedDateStr &&
                 (b.status === 'BOOKED' || b.status === 'ATTENDED')
        })
        if (booking) {
          await bookingsApi.cancel(booking.id)
          emit('cancel', booking.id)
          await loadSchedule()
        }
      }
    } else {
      const selectedDate = days.value[selectedDayIndex.value].date
      if (!selectedDate || !workout.time) {
        error.value = 'Не указаны дата или время тренировки'
        return
      }
      
      const scheduleWorkoutId = workout.scheduleWorkoutId
      if (!scheduleWorkoutId) {
        error.value = 'Не указан ID шаблона тренировки (scheduleWorkoutId)'
        console.error('Workout object:', workout)
        alert('Ошибка: не найден ID шаблона тренировки. Попробуйте обновить страницу.')
        return
      }
      
      const dateStr = format(selectedDate, 'yyyy-MM-dd')
      
      let hours, minutes
      if (typeof workout.time === 'string') {
        const timeParts = workout.time.split(':')
        if (timeParts.length < 2) {
          error.value = 'Неверный формат времени тренировки: ' + workout.time
          return
        }
        hours = parseInt(timeParts[0], 10)
        minutes = parseInt(timeParts[1], 10)
        if (isNaN(hours) || isNaN(minutes)) {
          error.value = 'Неверный формат времени тренировки: ' + workout.time
          return
        }
      } else {
        error.value = 'Время тренировки не указано'
        return
      }
      
      const workoutDateTimeStr = `${dateStr}T${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:00`
      
      console.log('Booking request:', {
        scheduleWorkoutId,
        workoutDate: workoutDateTimeStr,
        workout: workout
      })
      
      await bookingsApi.book(scheduleWorkoutId, workoutDateTimeStr)
      emit('book', workout.id)
      await loadSchedule()
    }
  } catch (err) {
    const errorMessage = err.message || err.response?.data?.message || 'Ошибка при записи'
    console.error('Booking error:', err)
    alert(errorMessage)
    return
  }
}

onMounted(() => {
  loadSchedule()
})
</script>

<style scoped>
.schedule-tabs {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.75rem;
  margin-bottom: 2rem;
}

.schedule-tab {
  padding: 1rem 0.75rem;
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.schedule-tab:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.schedule-tab.active {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgb(59, 130, 246);
  color: rgb(147, 197, 253);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.tab-day-name {
  font-weight: 700;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  text-transform: capitalize;
  color: white;
}

.tab-day-date {
  font-size: 0.875rem;
  opacity: 0.9;
  font-weight: 500;
  color: white;
}

@media (max-width: 1024px) {
  .schedule-tabs {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 640px) {
  .schedule-tabs {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
