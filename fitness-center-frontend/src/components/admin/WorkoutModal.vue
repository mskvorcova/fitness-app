<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">
          {{ workout ? 'Редактировать тренировку' : 'Добавить тренировку' }}
        </h3>
        <button @click="$emit('close')" class="modal-close">
          <X :size="24" />
        </button>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <label class="label">Тип тренировки</label>
          <select
            v-model="formData.workoutTypeId"
            required
            class="form-input"
            @change="onWorkoutTypeChange"
            style="background-color: #27272a; color: #f4f4f5; border: 1px solid #3f3f46; border-radius: 0.5rem; padding: 0.75rem 1rem; font-size: 1rem; width: 100%; cursor: pointer;"
          >
            <option value="">Выберите тип тренировки</option>
            <option
              v-for="type in workoutTypes"
              :key="type.id"
              :value="type.id"
            >
              {{ type.name }}
            </option>
          </select>
        </div>
        <div class="form-row">
          <label class="label">Название тренировки</label>
          <input
            v-model="formData.workoutName"
            type="text"
            class="form-input"
            placeholder="Йога для начинающих (заполнится автоматически при выборе типа)"
            readonly
          />
        </div>
        <div class="form-row">
          <label class="label">Тренер</label>
          <select
            v-model="formData.trainerId"
            required
            class="form-input"
            style="background-color: #27272a; color: #f4f4f5; border: 1px solid #3f3f46; border-radius: 0.5rem; padding: 0.75rem 1rem; font-size: 1rem; width: 100%; cursor: pointer;"
          >
            <option value="">Выберите тренера</option>
            <option
              v-for="trainer in trainers"
              :key="trainer.id"
              :value="trainer.id"
            >
              {{ trainer.name }}
            </option>
          </select>
        </div>
        <div class="form-row">
          <label class="label">День недели <span style="color: red;">*</span></label>
          <select
            v-model="formData.dayOfWeek"
            required
            class="form-input"
            style="background-color: #27272a; color: #f4f4f5; border: 1px solid #3f3f46; border-radius: 0.5rem; padding: 0.75rem 1rem; font-size: 1rem; width: 100%; cursor: pointer;"
          >
            <option value="">Выберите день недели</option>
            <option value="MONDAY">Понедельник</option>
            <option value="TUESDAY">Вторник</option>
            <option value="WEDNESDAY">Среда</option>
            <option value="THURSDAY">Четверг</option>
            <option value="FRIDAY">Пятница</option>
            <option value="SATURDAY">Суббота</option>
            <option value="SUNDAY">Воскресенье</option>
          </select>
        </div>
        <div class="form-row">
          <label class="label">Время</label>
          <input
            v-model="formData.time"
            type="time"
            required
            class="form-input"
          />
        </div>
        <button type="submit" class="form-submit">
          Сохранить
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { X } from 'lucide-vue-next'
import { scheduleApi, workoutTypesApi, trainersApi } from '@/api/client'

const props = defineProps({
  workout: {
    type: Object,
    default: null
  },
  defaultDayOfWeek: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const workoutTypes = ref([])
const trainers = ref([])
const formData = ref({
  workoutTypeId: '',
  workoutName: '',
  trainerId: '',
  dayOfWeek: '',
  time: ''
})

const loadWorkoutTypes = async () => {
  try {
    workoutTypes.value = await workoutTypesApi.getAll()
  } catch (error) {
    console.error('Load workout types error:', error)
  }
}

const loadTrainers = async () => {
  try {
    trainers.value = await trainersApi.getAll()
  } catch (error) {
    console.error('Load trainers error:', error)
  }
}

const onWorkoutTypeChange = () => {
  const selectedType = workoutTypes.value.find(t => t.id === formData.value.workoutTypeId)
  if (selectedType) {
    formData.value.workoutName = selectedType.name
  }
}

watch(() => props.workout, (workout) => {
  if (workout) {
    let dayOfWeek = workout.dayOfWeek || ''
    if (!dayOfWeek && workout.date) {
      const date = typeof workout.date === 'string' ? new Date(workout.date) : workout.date
      const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
      dayOfWeek = days[date.getDay()]
    }
    const workoutTime = workout.time ? (typeof workout.time === 'string' ? workout.time.substring(0, 5) : workout.time) : ''
    
    formData.value = {
      workoutTypeId: workout.workoutTypeId || '',
      workoutName: workout.workoutName || '',
      trainerId: workout.trainerId || '',
      dayOfWeek: dayOfWeek,
      time: workoutTime
    }
  } else {
    formData.value = {
      workoutTypeId: '',
      workoutName: '',
      trainerId: '',
      dayOfWeek: props.defaultDayOfWeek || '',
      time: '10:00'
    }
  }
}, { immediate: true })

onMounted(() => {
  loadWorkoutTypes()
  loadTrainers()
})

const handleSubmit = async () => {
  try {
    if (!formData.value.workoutTypeId) {
      alert('Выберите тип тренировки')
      return
    }
    if (!formData.value.trainerId) {
      alert('Выберите тренера')
      return
    }
    if (!formData.value.dayOfWeek) {
      alert('Выберите день недели')
      return
    }
    if (!formData.value.time) {
      alert('Укажите время')
      return
    }

    const workoutData = {
      workoutTypeId: Number(formData.value.workoutTypeId),
      trainerId: Number(formData.value.trainerId),
      dayOfWeek: formData.value.dayOfWeek,
      time: formData.value.time
    }

    if (props.workout) {
      await scheduleApi.updateWorkout(props.workout.id, workoutData)
    } else {
      await scheduleApi.createWorkout(workoutData)
    }
    emit('save')
    emit('close')
  } catch (error) {
    console.error('Save error:', error)
    const errorMessage = error.response?.data?.message || error.message || 'Ошибка сохранения тренировки'
    alert(errorMessage)
  }
}
</script>
