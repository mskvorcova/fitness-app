<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content" style="max-width: 42rem;">
      <div class="modal-header">
        <h3 class="modal-title">Информация о клиенте</h3>
        <button @click="$emit('close')" class="modal-close">
          <X :size="24" />
        </button>
      </div>

      <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div>
          <h4 class="text-lg font-semibold text-white mb-4">Основная информация</h4>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-muted text-sm mb-1">ФИО</p>
              <p class="text-white">{{ client.name }}</p>
            </div>
            <div>
              <p class="text-muted text-sm mb-1">Email</p>
              <p class="text-white">{{ client.email }}</p>
            </div>
            <div>
              <p class="text-muted text-sm mb-1">Телефон</p>
              <p class="text-white">{{ client.phone }}</p>
            </div>
            <div>
              <p class="text-muted text-sm mb-1">Дата регистрации</p>
              <p class="text-white">{{ formatDate(client.registrationDate) }}</p>
            </div>
          </div>
        </div>

        <div>
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
            <h4 class="text-lg font-semibold text-white">Абонемент</h4>
            <button
              @click="showMembershipModal = true"
              class="btn"
              style="padding: 0.5rem 1rem; font-size: 0.875rem;"
            >
              {{ membership ? 'Изменить' : 'Добавить' }}
            </button>
          </div>
          <div v-if="membership" class="card">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-muted text-sm mb-1">Тип</p>
                <p class="text-white font-medium">{{ membership.membershipTypeName || membership.type || 'Не указан' }}</p>
              </div>
              <div>
                <p class="text-muted text-sm mb-1">Действителен до</p>
                <p class="text-white font-medium">{{ formatDate(membership.endDate) }}</p>
              </div>
            </div>
          </div>
          <div v-else class="text-muted">Нет активного абонемента</div>
        </div>

        <div>
          <h4 class="text-lg font-semibold text-white mb-4">Записи на тренировки</h4>
          <div v-if="bookings.length > 0" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div
              v-for="booking in bookings"
              :key="booking.id"
              class="card"
              style="display: flex; align-items: center; justify-content: space-between;"
            >
              <div>
                <p class="text-white font-medium">{{ booking.workoutName || 'Тренировка' }}</p>
                <p class="text-muted text-sm">{{ formatBookingDate(booking.workoutDate) }}</p>
              </div>
              <div style="display: flex; gap: 0.5rem;">
                <button
                  v-if="booking.status === 'BOOKED'"
                  @click="markAttendance(booking.id)"
                  class="btn"
                  style="padding: 0.5rem 1rem; font-size: 0.875rem;"
                >
                  Отметить посещенным
                </button>
                <button
                  v-if="booking.status === 'BOOKED'"
                  @click="cancelBooking(booking.id)"
                  class="btn btn-danger"
                  style="padding: 0.5rem 1rem; font-size: 0.875rem;"
                >
                  Отменить
                </button>
                <span
                  v-if="booking.status === 'ATTENDED'"
                  class="badge badge-success"
                >
                  Посещено
                </span>
              </div>
            </div>
          </div>
          <div v-else class="text-muted">Нет записей</div>
        </div>

        <div>
          <h4 class="text-lg font-semibold text-white mb-4">История посещений</h4>
          <div v-if="visits.length > 0" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div
              v-for="visit in visits"
              :key="visit.id"
              class="card"
              style="display: flex; align-items: center; justify-content: space-between;"
            >
              <div>
                <p class="text-white font-medium">{{ visit.workoutName }}</p>
                <p class="text-muted text-sm">{{ formatDate(visit.date) }}</p>
              </div>
              <span
                :class="[
                  'badge',
                  visit.attended ? 'badge-success' : 'badge-default'
                ]"
              >
                {{ visit.attended ? 'Посещено' : 'Пропущено' }}
              </span>
            </div>
          </div>
          <div v-else class="text-muted">Нет посещений</div>
        </div>
      </div>
    </div>

    <ClientMembershipModal
      v-if="showMembershipModal"
      :client-id="client.id"
      :membership="membership"
      @close="showMembershipModal = false"
      @save="handleMembershipSave"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { X } from 'lucide-vue-next'
import { membershipsApi, bookingsApi, visitsApi } from '@/api/client'
import ClientMembershipModal from './ClientMembershipModal.vue'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale/ru'

const props = defineProps({
  client: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close'])

const membership = ref(null)
const bookings = ref([])
const visits = ref([])
const showMembershipModal = ref(false)

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

const formatBookingDate = (date) => {
  if (!date) return ''
  try {
    const dateObj = new Date(date)
    if (isNaN(dateObj.getTime())) {
      return date
    }
    return format(dateObj, 'd MMMM yyyy, HH:mm', { locale: ru })
  } catch (error) {
    console.error('Date formatting error:', error, date)
    return date || ''
  }
}

const loadData = async () => {
  try {
    membership.value = await membershipsApi.getByClientId(props.client.id)
  } catch (error) {
    console.error('Load membership error:', error)
    membership.value = null
  }
  
  try {
    const clientBookings = await bookingsApi.getByClientId(props.client.id)
    bookings.value = clientBookings
      .filter(b => b.status !== 'CANCELLED')
      .sort((a, b) => {
        return new Date(a.workoutDate) - new Date(b.workoutDate)
      })
  } catch (error) {
    console.error('Load bookings error:', error)
    bookings.value = []
  }
  
  try {
    visits.value = await visitsApi.getByClientId(props.client.id)
  } catch (error) {
    console.error('Load visits error:', error)
    visits.value = []
  }
}

const cancelBooking = async (bookingId) => {
  if (!confirm('Отменить запись?')) return
  try {
    await bookingsApi.cancel(bookingId)
    await loadData()
  } catch (error) {
    console.error('Cancel booking error:', error)
    alert('Ошибка при отмене записи: ' + (error.message || 'Неизвестная ошибка'))
  }
}

const markAttendance = async (bookingId) => {
  if (!confirm('Отметить тренировку как посещенную?')) return
  try {
    await bookingsApi.markAttendance(bookingId)
    await loadData()
  } catch (error) {
    console.error('Mark attendance error:', error)
    alert('Ошибка при отметке посещения: ' + (error.message || 'Неизвестная ошибка'))
  }
}

const handleMembershipSave = async () => {
  await loadData()
  showMembershipModal.value = false
}

onMounted(() => {
  loadData()
})
</script>
