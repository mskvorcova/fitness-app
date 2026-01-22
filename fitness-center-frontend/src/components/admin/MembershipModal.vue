<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">
          {{ membership ? 'Редактировать абонемент' : 'Создать абонемент' }}
        </h3>
        <button @click="$emit('close')" class="modal-close">
          <X :size="24" />
        </button>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <label class="label">Название</label>
          <input
            v-model="formData.name"
            type="text"
            required
            class="form-input"
            placeholder="Премиум"
          />
        </div>
        <div class="form-row">
          <label class="label">Срок действия (дней)</label>
          <input
            v-model.number="formData.durationDays"
            type="number"
            required
            min="1"
            class="form-input"
          />
        </div>
        <div class="form-row">
          <label class="label">Количество занятий</label>
          <input
            v-model.number="formData.classes"
            type="number"
            required
            min="1"
            class="form-input"
          />
        </div>
        <div class="form-row">
          <label class="label">Стоимость (₽)</label>
          <input
            v-model.number="formData.price"
            type="number"
            required
            min="0"
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
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { membershipTypesApi } from '@/api/client'

const props = defineProps({
  membership: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const formData = ref({
  name: '',
  duration: 30,
  description: '',
  price: 3000
})

watch(() => props.membership, (membership) => {
  if (membership) {
    formData.value = {
      name: membership.name || '',
      duration: membership.duration || 30,
      description: membership.description || '',
      price: membership.price || 3000
    }
  } else {
    formData.value = {
      name: '',
      duration: 30,
      description: '',
      price: 3000
    }
  }
}, { immediate: true })

const handleSubmit = async () => {
  try {
    if (props.membership) {
      await membershipTypesApi.update(props.membership.id, formData.value)
    } else {
      await membershipTypesApi.create(formData.value)
    }
    emit('save')
  } catch (error) {
    console.error('Save error:', error)
    alert(error.message || 'Ошибка сохранения типа абонемента')
  }
}
</script>
