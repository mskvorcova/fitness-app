<template>
  <div>
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem;">
      <h2 class="text-2xl font-bold text-white">Расписание тренировок</h2>
      <button
        v-if="canEdit"
        @click="showWorkoutModal = true"
        class="action-btn action-btn-primary"
      >
        <Plus :size="20" />
        Добавить тренировку
      </button>
    </div>

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
        :style="canEdit ? 'display: flex; align-items: center; justify-content: space-between;' : ''"
      >
        <div style="flex: 1;">
          <h3 class="workout-title">{{ workout.workoutName }}</h3>
          <div class="workout-details">
            <div class="workout-detail">Тренер: {{ workout.trainerName || workout.trainer }}</div>
            <div class="workout-detail">Время: {{ workout.time }}</div>
            <div class="workout-detail">Зал: {{ workout.hall }}</div>
            <div class="workout-detail">Записано: {{ workout.enrolled }}/{{ workout.capacity }}</div>
          </div>
        </div>
        <div v-if="canEdit" style="display: flex; gap: 0.5rem; margin-left: 1rem;">
          <button
            @click="editWorkout(workout)"
            class="icon-btn"
          >
            <Edit2 :size="20" />
          </button>
          <button
            @click="deleteWorkout(workout.id)"
            class="icon-btn icon-btn-danger"
          >
            <Trash2 :size="20" />
          </button>
        </div>
      </div>
      <div v-if="selectedDayWorkouts.length === 0" class="empty-state">
        Нет тренировок на этот день
      </div>
    </div>

    <WorkoutModal
      v-if="showWorkoutModal"
      :workout="selectedWorkout"
      :defaultDayOfWeek="getDayOfWeekFromDate(selectedDate)"
      @close="showWorkoutModal = false"
      @save="handleSave"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit2, Trash2 } from 'lucide-vue-next'
import { scheduleApi } from '@/api/client'
import { format, addDays, startOfDay } from 'date-fns'
import { ru } from 'date-fns/locale/ru'
import WorkoutModal from '@/components/admin/WorkoutModal.vue'

const props = defineProps({
  canEdit: {
    type: Boolean,
    default: false
  },
  trainerId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['workoutUpdated'])

const workouts = ref([])
const loading = ref(false)
const error = ref('')
const showWorkoutModal = ref(false)
const selectedWorkout = ref(null)
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
  } catch {
    return date
  }
}

const loadSchedule = async () => {
  loading.value = true
  error.value = ''
  try {
    if (props.trainerId) {
      workouts.value = await scheduleApi.getTrainerSchedule(props.trainerId, 7)
    } else {
      workouts.value = await scheduleApi.getSchedule(7)
    }
    workouts.value.sort((a, b) => {
      const dateCompare = new Date(a.date) - new Date(b.date)
      if (dateCompare !== 0) return dateCompare
      return (a.time || '').localeCompare(b.time || '')
    })
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка загрузки расписания'
    console.error('Load schedule error:', err)
  } finally {
    loading.value = false
  }
}

const editWorkout = (workout) => {
  selectedWorkout.value = workout
  showWorkoutModal.value = true
}

const deleteWorkout = async (id) => {
  if (!confirm('Удалить эту тренировку из расписания?')) return
  try {
    await scheduleApi.deleteWorkout(id)
    await loadSchedule()
    emit('workoutUpdated')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления тренировки'
    console.error('Delete workout error:', err)
  }
}

const handleSave = async () => {
  await loadSchedule()
  showWorkoutModal.value = false
  selectedWorkout.value = null
  emit('workoutUpdated')
}

const selectedDate = computed(() => {
  return days.value[selectedDayIndex.value]?.date
})

const getDayOfWeekFromDate = (date) => {
  if (!date) return ''
  const dateObj = typeof date === 'string' ? new Date(date) : date
  const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
  return days[dateObj.getDay()]
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
