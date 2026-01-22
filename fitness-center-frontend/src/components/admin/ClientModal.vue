<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">
          {{ client ? 'Редактировать клиента' : 'Добавить клиента' }}
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
            placeholder="ivan.petrov"
          />
        </div>
        <div class="form-row">
          <label class="label">ФИО</label>
          <input
            v-model="formData.name"
            type="text"
            class="form-input"
            placeholder="Иван Петров (необязательно)"
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
        <div v-if="!client" class="form-row">
          <label class="label">Пароль (первичный) <span style="color: red;">*</span></label>
          <input
            v-model="formData.password"
            type="password"
            required
            class="form-input"
            placeholder="Введите пароль для клиента"
          />
        </div>
        <div v-if="client" class="form-row">
          <label class="label">Телефон</label>
          <input
            v-model="formData.phone"
            type="tel"
            class="form-input"
            placeholder="+7 999 123-45-67"
          />
        </div>
        <div v-if="!client" class="form-row">
          <label class="label">Тип абонемента</label>
          <select
            v-model="formData.membershipTypeId"
            class="form-input"
          >
            <option :value="null">Не указывать</option>
            <option
              v-for="type in membershipTypes"
              :key="type.id"
              :value="type.id"
            >
              {{ type.name }} ({{ type.durationDays }} дней, {{ type.price }}₽)
            </option>
          </select>
        </div>
        <div v-if="!client && formData.membershipTypeId" class="form-row">
          <label class="label">Дата начала абонемента</label>
          <input
            v-model="formData.membershipStartDate"
            type="date"
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
import { clientsApi, usersApi, membershipTypesApi } from '@/api/client'

const props = defineProps({
  client: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const membershipTypes = ref([])

const formData = ref({
  username: '',
  name: '',
  email: '',
  phone: '',
  password: '',
  membershipTypeId: null,
  membershipStartDate: ''
})

const setDefaultDate = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  formData.value.membershipStartDate = `${year}-${month}-${day}`
}

watch(() => props.client, (client) => {
  if (client) {
    formData.value = {
      username: client.username || '',
      name: client.name,
      email: client.email || '',
      phone: client.phone,
      password: '',
      membershipTypeId: null,
      membershipStartDate: ''
    }
  } else {
    formData.value = {
      username: '',
      name: '',
      email: '',
      phone: '',
      password: '',
      membershipTypeId: null,
      membershipStartDate: ''
    }
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

onMounted(() => {
  loadMembershipTypes()
  if (!props.client) {
    setDefaultDate()
  }
})

const handleSubmit = async () => {
  try {
    if (props.client) {
      await clientsApi.update(props.client.id, formData.value)
    } else {
      const clientData = {
        username: formData.value.username,
        name: formData.value.name || formData.value.username,
        email: formData.value.email || null,
        phone: null,
        password: formData.value.password
      }
      
      if (formData.value.membershipTypeId) {
        clientData.membershipTypeId = formData.value.membershipTypeId
        clientData.membershipStartDate = formData.value.membershipStartDate || new Date().toISOString().split('T')[0]
      }
      
      await usersApi.createClient(clientData)
    }
    emit('save')
  } catch (error) {
    console.error('Save error:', error)
    alert(error.message || 'Ошибка сохранения клиента')
  }
}
</script>
