<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">
          {{ trainer ? 'Редактировать тренера' : 'Добавить тренера' }}
        </h3>
        <button @click="$emit('close')" class="modal-close">
          <X :size="24" />
        </button>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <label class="label">Имя пользователя <span style="color: red;">*</span></label>
          <input
            v-model="formData.username"
            type="text"
            required
            class="form-input"
            placeholder="trainer.anna"
          />
        </div>
        <div class="form-row">
          <label class="label">ФИО</label>
          <input
            v-model="formData.name"
            type="text"
            required
            class="form-input"
            placeholder="Иван Петров"
          />
        </div>
        <div class="form-row">
          <label class="label">Email</label>
          <input
            v-model="formData.email"
            type="email"
            class="form-input"
            placeholder="ivan@email.ru (необязательно)"
          />
        </div>
        <div class="form-row">
          <label class="label">Описание</label>
          <textarea
            v-model="formData.description"
            class="form-input"
            rows="3"
            placeholder="Описание тренера"
          />
        </div>
        <div class="form-row">
          <label class="label">Стаж (лет)</label>
          <input
            v-model.number="formData.experienceYears"
            type="number"
            class="form-input"
            min="0"
            placeholder="0"
          />
        </div>
        <div v-if="!trainer" class="form-row">
          <label class="label">Пароль (первичный)</label>
          <input
            v-model="formData.password"
            type="password"
            required
            class="form-input"
            placeholder="Введите пароль для тренера"
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
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { trainersApi, usersApi } from '@/api/client'

const props = defineProps({
  trainer: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const formData = ref({
  username: '',
  name: '',
  email: '',
  description: '',
  experienceYears: null,
  password: ''
})

watch(() => props.trainer, (trainer) => {
  if (trainer) {
    formData.value = {
      username: trainer.username || '',
      name: trainer.name,
      email: trainer.email || '',
      description: trainer.description || '',
      experienceYears: trainer.experienceYears || null,
      password: ''
    }
  } else {
    formData.value = {
      username: '',
      name: '',
      email: '',
      description: '',
      experienceYears: null,
      password: ''
    }
  }
}, { immediate: true })

const handleSubmit = async () => {
  try {
    if (props.trainer) {
      await trainersApi.updateByUserId(props.trainer.userId, {
        description: formData.value.description,
        experienceYears: formData.value.experienceYears
      })
    } else {
      // Создаем тренера с паролем через usersApi
      await usersApi.createTrainer({
        username: formData.value.username,
        name: formData.value.name,
        email: formData.value.email || null,
        password: formData.value.password,
        description: formData.value.description,
        experienceYears: formData.value.experienceYears
      })
    }
    emit('save')
  } catch (error) {
    console.error('Save error:', error)
    alert(error.message || 'Ошибка сохранения тренера')
  }
}
</script>
