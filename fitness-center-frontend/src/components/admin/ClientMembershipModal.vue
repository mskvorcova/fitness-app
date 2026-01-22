<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">{{ membership ? 'Изменить абонемент' : 'Добавить абонемент' }}</h3>
        <button @click="$emit('close')" class="modal-close">
          <X :size="24" />
        </button>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <label class="label">Тип абонемента <span style="color: red;">*</span></label>
          <select
            v-model="formData.membershipTypeId"
            required
            class="form-input"
          >
            <option :value="null">Выберите тип абонемента</option>
            <option
              v-for="type in membershipTypes"
              :key="type.id"
              :value="type.id"
            >
              {{ type.name }} ({{ type.durationDays }} дней, {{ type.price }}₽)
            </option>
          </select>
        </div>
        <div class="form-row">
          <label class="label">Дата начала <span style="color: red;">*</span></label>
          <input
            v-model="formData.startDate"
            type="date"
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
import { ref, onMounted, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { membershipTypesApi, membershipsApi } from '@/api/client'

const props = defineProps({
  clientId: {
    type: Number,
    required: true
  },
  membership: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const membershipTypes = ref([])
const formData = ref({
  membershipTypeId: null,
  startDate: ''
})

const setDefaultDate = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  formData.value.startDate = `${year}-${month}-${day}`
}

watch(() => props.membership, (membership) => {
  if (membership) {
    formData.value.membershipTypeId = membership.membershipTypeId
    const startDate = new Date(membership.startDate)
    const year = startDate.getFullYear()
    const month = String(startDate.getMonth() + 1).padStart(2, '0')
    const day = String(startDate.getDate()).padStart(2, '0')
    formData.value.startDate = `${year}-${month}-${day}`
  } else {
    formData.value.membershipTypeId = null
    setDefaultDate()
  }
}, { immediate: true })

const loadMembershipTypes = async () => {
  try {
    membershipTypes.value = await membershipTypesApi.getAll()
  } catch (error) {
    console.error('Load membership types error:', error)
  }
}

const handleSubmit = async () => {
  try {
    const selectedType = membershipTypes.value.find(t => t.id === formData.value.membershipTypeId)
    if (!selectedType) {
      alert('Выберите тип абонемента')
      return
    }

    const startDate = new Date(formData.value.startDate)
    const endDate = new Date(startDate)
    endDate.setDate(endDate.getDate() + selectedType.durationDays)

    const membershipData = {
      membershipTypeId: formData.value.membershipTypeId,
      startDate: formData.value.startDate,
      endDate: endDate.toISOString().split('T')[0],
      price: selectedType.price,
      status: props.membership?.status || 'ACTIVE'
    }

    if (props.membership) {
      await membershipsApi.update(props.clientId, props.membership.id, membershipData)
    } else {
      await membershipsApi.create(props.clientId, membershipData)
    }
    emit('save')
  } catch (error) {
    console.error('Save error:', error)
    alert(error.message || 'Ошибка сохранения абонемента')
  }
}

onMounted(() => {
  loadMembershipTypes()
  if (!props.membership) {
    setDefaultDate()
  }
})
</script>
