<template>
  <div>
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem;">
      <h2 class="text-2xl font-bold text-white">Управление клиентами</h2>
      <button
        @click="showClientModal = true"
        class="action-btn action-btn-primary"
      >
        <Plus :size="20" />
        Добавить клиента
      </button>
    </div>

    <div style="margin-bottom: 1.5rem;">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Поиск по имени, email или телефону..."
        class="form-input"
      />
    </div>

    <div class="table-wrapper">
      <div style="overflow-x: auto;">
        <table class="table">
          <thead>
            <tr>
              <th>Имя</th>
              <th>Email</th>
              <th>Телефон</th>
              <th>Абонемент</th>
              <th>Статус</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="client in filteredClients"
              :key="client.id"
              style="cursor: pointer;"
              @click="selectClient(client)"
            >
              <td class="text-white">{{ client.name }}</td>
              <td class="text-muted">{{ client.email }}</td>
              <td class="text-muted">{{ client.phone }}</td>
              <td>
                <div class="text-white">{{ client.membershipTypeName || client.membershipType || 'Нет' }}</div>
                <div v-if="client.membershipExpiry" class="text-xs text-muted">
                  До {{ formatDate(client.membershipExpiry) }}
                </div>
              </td>
              <td>
                <span
                  :class="[
                    'badge',
                    client.status === 'active' ? 'badge-success' : 'badge-default'
                  ]"
                >
                  {{ client.status === 'active' ? 'Активен' : 'Истек' }}
                </span>
              </td>
              <td>
                <div class="flex gap-2" @click.stop>
                  <button
                    @click="editClient(client)"
                    class="icon-btn"
                  >
                    <Edit2 :size="16" />
                  </button>
                  <button
                    @click="deleteClient(client.id)"
                    class="icon-btn icon-btn-danger"
                  >
                    <Trash2 :size="16" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <ClientModal
      v-if="showClientModal"
      :client="selectedClient"
      @close="showClientModal = false"
      @save="handleSaveClient"
    />

    <ClientDetailsModal
      v-if="showDetailsModal && selectedClient"
      :client="selectedClient"
      @close="showDetailsModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit2, Trash2 } from 'lucide-vue-next'
import { clientsApi } from '@/api/client'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale/ru'
import ClientModal from './ClientModal.vue'
import ClientDetailsModal from './ClientDetailsModal.vue'

const clients = ref([])
const searchQuery = ref('')
const showClientModal = ref(false)
const showDetailsModal = ref(false)
const selectedClient = ref(null)

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

const filteredClients = computed(() => {
  if (!searchQuery.value) return clients.value
  const query = searchQuery.value.toString().toLowerCase()
  return clients.value.filter(
    c =>
      (c.name && c.name.toLowerCase().includes(query)) ||
      (c.email && c.email.toLowerCase().includes(query)) ||
      (c.phone && c.phone.toLowerCase().includes(query))
  )
})

const loadClients = async () => {
  try {
    clients.value = await clientsApi.getAll()
  } catch (error) {
    console.error('Load clients error:', error)
    clients.value = []
  }
}

const selectClient = (client) => {
  selectedClient.value = client
  showDetailsModal.value = true
}

const editClient = (client) => {
  selectedClient.value = client
  showClientModal.value = true
}

const deleteClient = async (id) => {
  if (!confirm('Вы уверены, что хотите удалить этого клиента?')) return
  try {
    await clientsApi.delete(id)
    await loadClients()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSaveClient = async () => {
  await loadClients()
  showClientModal.value = false
  selectedClient.value = null
}

onMounted(() => {
  loadClients()
})
</script>

<style scoped>
.text-xs {
  font-size: 0.75rem;
}
</style>
